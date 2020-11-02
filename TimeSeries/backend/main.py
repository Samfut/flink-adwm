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

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)