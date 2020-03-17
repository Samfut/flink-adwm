import pandas as pd
import numpy as np

from didiUtil import DIDI201705, DIDI201706, DIDI201707, DIDI201708, DIDI201709, DIDI201710
from didiDraw import DrawLSTM, DrawXgboost

from sklearn.metrics import mean_squared_error
from sklearn.metrics import median_absolute_error
from sklearn.metrics import r2_score

months = [DIDI201705, DIDI201706, DIDI201707, DIDI201708, DIDI201709, DIDI201710]

def get_reg_metrics(months, update, start='2017-06'):
    '''
    params: 
    months: List 存储现有数据
    update:boolean, 为True的时候 会使用前一个月作为训练集 
    start: str 预测的起始月份 '2018-09'
    '''
    mse, mae, r2s = [], [], []
    
    date_index = pd.date_range(start=start, periods=len(months)-1, freq='MS')
    
    train_month, test_month = months[0], None
    draw = DrawLSTM()
    
    for i in range(1, len(months)):
        test_month = months[i]
        if update:
            train_month = months[i-1]
        draw.set_train_month(train_month)
        test_y, predict_y = draw.get_predict_result(train_month, test_month)
        mse.append(np.sqrt(mean_squared_error(test_y, predict_y)))
        mae.append(median_absolute_error(test_y, predict_y))
        r2s.append(r2_score(test_y, predict_y))
        
    data = dict(mse=mse, mae=mae, r2s=r2s)
    df = pd.DataFrame(data, index = date_index)
    return df


def get_xgb_metrics(months, update, start='2018-09'):
    '''
    params: 
    months: List 存储现有数据
    update:boolean, 为True的时候 会使用前一个月作为训练集 
    start: str 预测的起始月份 '2018-09'
    '''
    mse, mae, r2s = [], [], []
    
    date_index = pd.date_range(start=start, periods=len(months)-1, freq='MS')
    
    train_month, test_month = months[0], None
    draw = DrawXgboost()
    
    for i in range(1, len(months)):
        test_month = months[i]
        if update:
            train_month = months[i-1]
        test_y, predict_y = draw.get_predict_result([train_month], [test_month])
        mse.append(np.sqrt(mean_squared_error(test_y, predict_y)))
        mae.append(median_absolute_error(test_y, predict_y))
        r2s.append(r2_score(test_y, predict_y))
        
    data = dict(mse=mse, mae=mae, r2s=r2s)
    df = pd.DataFrame(data, index = date_index)
    return df