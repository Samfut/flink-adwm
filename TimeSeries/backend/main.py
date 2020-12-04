from json import dump
import os
import json
from sys import stdout
import psutil
import pandas as pd
import subprocess

from flask_cors import CORS
from flask import Flask, request

app = Flask(__name__)
CORS(app)

ops = ["NATIVE", "SLICE", "TREE", "STREE"]
ps = [None, None, None, None]
begin = False
dup = False
window = 2
slide = 1


map_idx_window = {
    0: 'native',
    1: 'slice',
    2: 'tree',
    3: 'stree'
}

@app.route('/api/watermark/predict')
def handler_predict():
    params = request.args.to_dict()
    dataset = params['dataset']
    model = params['model']
    csvfile = pd.read_csv(f'./data/{model}/{dataset}.csv', index_col=0)
    res = dict(xtime=csvfile.index.tolist(), ypredict=csvfile['predict'].tolist(), yreal=csvfile['real'].tolist())
    return json.dumps(res)

@app.route('/api/watermark/wait')
def handler_watermark():
    params = request.args.to_dict()
    dataset = params['dataset']
    csvfile = pd.read_csv(f'./data/WaitTime/{dataset}.csv', index_col=0)
    csvfile = csvfile.fillna(0)
    res = dict(xtime=csvfile.index.tolist(), ywait=csvfile['adwater'].tolist(), ycom=csvfile['period(1000ms)'].tolist())
    return json.dumps(res)
    
@app.route('/api/window/wait')
def handler_window():
    params = request.args.to_dict()
    dataset = params['dataset']
    csvfile = pd.read_csv(f'./data/WindowWait/{dataset}.csv', index_col=0)
    res = dict(xtime=csvfile.index.tolist(), ywait=csvfile['wait'].tolist(), ycom=csvfile['com'].tolist())    
    return json.dumps(res)

@app.route('/api/slide/run')
def handler_run():
    global begin, ps, dup, window, slide
    if dup == True:
        return json.dumps({'status': 1})
    params = request.args.to_dict()
    win = params['win']
    sli = params['sli']
    agg = params['agg']
    window = win
    slide = sli
    cmds = [["./slidewindow", "-cpu=1", 
            f"-type={op}", f"-win={win}", 
            f"-sli={sli}", f"-num={20000000}", 
            f"-op={agg}", "&"] for op in ops]
    for i in range(4):
        ps[i] = psutil.Popen(cmds[i], stdout=subprocess.PIPE)
    begin = True
    dup = True
    return json.dumps({'status': 0})

@app.route('/api/slide/sys')
def handler_sys():
    global begin, ps, dup, window, slide
    cpu = dict(native=0, slice=0, tree=0, stree=0)
    mem = dict(native=0, slice=0, tree=0, stree=0)
    flag = True
    if begin == False:
        return json.dumps({'status': 1})
    else: 
        for i in range(4):
            print(ps[i].status())
            if ps[i] and ps[i].status() != 'zombie':
                flag = False
                cpu[map_idx_window[i]] = ps[i].cpu_times().user
                mem[map_idx_window[i]] = ps[i].memory_full_info().uss/ 1024. / 1024. / 1024.
                if map_idx_window[i] == 'native':
                    n = window/slide
                    mem[map_idx_window[i]] *= n
                # print(dict(ps[i].memory_full_info()))
    if flag == True:
        dup = False
    return json.dumps(dict(status=0,cpu=cpu, mem=mem))

@app.route('/api/slide/killall')
def handler_kill():
    global begin, ps, dup
    for p in ps:
        if p: p.terminate()
    ps = [None, None, None, None]
    begin = False  
    dup = False
    return json.dumps({'status':0})
    
if __name__ == "__main__":

    app.run(host="0.0.0.0", port=5000, debug=True)