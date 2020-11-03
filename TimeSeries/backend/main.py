from flask_cors import CORS
from flask import Flask, request
import pandas as pd
import json

app = Flask(__name__)
CORS(app)

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
    t = dataset.split('2')[0]
    if t == 'CB':
        csvfile = pd.read_csv('./data/WaitTime/CB201810.csv', index_col=0)
        csvfile = csvfile.fillna(0)
        res = dict(xtime=csvfile.index.tolist(), ywait=csvfile['adwater'].tolist(), ycom=csvfile['period(1000ms)'].tolist())
    else:
        csvfile = pd.read_csv('./data/WaitTime/DIDI201710.csv', index_col=0)
        csvfile = csvfile.fillna(0)
        res = dict(xtime=csvfile.index.tolist(), ywait=csvfile['adwater'].tolist(), ycom=csvfile['period(1000ms)'].tolist())
    return json.dumps(res)
    

@app.route('/api/window/wait')
def handler_window():
    params = request.args.to_dict()
    dataset = params['dataset']
    t = dataset.split('2')[0]
    if t == 'CB':
        csvfile = pd.read_csv('./data/WindowWait/CB201810.csv', index_col=0)
        res = dict(xtime=csvfile.index.tolist(), ywait=csvfile['wait'].tolist(), ycom=csvfile['com'].tolist())
    else:
        csvfile = pd.read_csv('./data/WindowWait/DIDI201710.csv', index_col=0)
        res = dict(xtime=csvfile.index.tolist(), ywait=csvfile['wait'].tolist(), ycom=csvfile['com'].tolist())
    return json.dumps(res)

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)