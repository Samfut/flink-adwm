import seaborn as sns
import matplotlib.pyplot as plt

import numpy as np
import pandas as pd

from didiUtil import DIDI201705, DIDI201706, DIDI201707, DIDI201708, DIDI201709, DIDI201710
from predictor import LSTMPredictor, DecisionTreePredictor, XGBoostPredictor

class DrawLSTM:
    '''
    draw =  DrawLSTM()
    # 一定要设置训练集 因为会根据训练集筛选模型
    draw.set_train_month(CB201808)
    test_y, train_y = draw.get_predict_result(CB201808, CB201809)
    draw.draw_real_predict(
        week_num=4,
        start_time="2018-09-01 13:00:00",
        title="DataStream DisOrder Predict",
        figsize=(15,5)
    )
    '''
    def __init__(self):
        self.lstm_predictor = LSTMPredictor()
        self.test_y = None
        self.predict_y = None

    def set_train_month(self, train_month):
        self.lstm_predictor.train_month = train_month

    def get_predict_result(self, train_month, test_month):
        '''
        params:
        train_month: str 训练数据的月份 ex: CB201808
        test_month: str 测试数据的月份 ex: CB201809
        return: (test_y, predict_y) 返回真实值和预测值
        '''
        train_x, train_y = self.lstm_predictor.get_train_vec(train_month)
        test_x, test_y = self.lstm_predictor.get_train_vec(test_month)
        model = self.lstm_predictor.train_model(train_x, train_y)
        self.test_y = test_y
        self.predict_y = model.predict(test_x)
        return self.test_y, self.predict_y
    
    def draw_real_predict(self, week_num: int, start_time: str, title: str, figsize=(15,5)):
        '''
        画这个月第几周的结果 
        params: week_num 为int值 取值为1-4
                start_time 例如'2018-09-01 13:00:00'
                title 图的标题
                figsize 图的大小 (15,5)
        '''
        date_index = pd.date_range(start=start_time, periods=len(self.test_y), freq='H')
        merge_data = {
            'predict': self.predict_y.flatten(),
            'real': self.test_y.flatten()
        }
        lstm_df = pd.DataFrame(merge_data, index=date_index)

        sns.set(style="whitegrid")
        sns.set_context(rc={"lines.linewidth": 2})

        f, ax = plt.subplots(figsize = figsize)
        # 设置图名称
        ax.set_title(title, fontsize=15)
        # 设置X轴 Y轴 的label
        ax.set_xlabel('Time',fontsize=15)
        ax.set_ylabel('DisOrder Rate',fontsize=15)
        sns.lineplot(size='event',
                    markers=True,dashes=False,
                    data=lstm_df[
                        168*(week_num-1):
                        min(168*(week_num), len(lstm_df))
                    ])
        plt.show()

class DrawXgboost:
    
    def __init__(self):
        self.xgb_predictor = XGBoostPredictor()
        self.predict_y = None
        self.test_y = None
    
    def get_predict_result(self, train_month, test_month):
        """
        train_month: list
        test_month: list
        """
        train_x, train_y = self.xgb_predictor.get_train_vec(train_month)
        test_x, test_y = self.xgb_predictor.get_train_vec(test_month)
        model = self.xgb_predictor.train_model(train_x, train_y, [(test_x, test_y)])
        self.test_y = test_y['res'].tolist()
        self.predict_y = model.predict(test_x)
        return self.test_y, self.predict_y

    def draw_real_predict(self, week_num: int, start_time: str, title: str, figsize=(15,5)):
        '''
        画这个月第几周的结果 
        params: week_num 为int值 取值为1-4
                start_time 例如'2018-09-01 13:00:00'
                title 图的标题
                figsize 图的大小 (15,5)
        '''
        date_index = pd.date_range(start=start_time, periods=len(self.test_y), freq='H')
        merge_data = {
            'predict': self.predict_y,
            'real': self.test_y
        }
        xgb_df = pd.DataFrame(merge_data, index=date_index)

        sns.set(style="whitegrid")
        sns.set_context(rc={"lines.linewidth": 2})

        f, ax = plt.subplots(figsize = figsize)
        # 设置图名称
        ax.set_title(title, fontsize=15)
        # 设置X轴 Y轴 的label
        ax.set_xlabel('Time',fontsize=15)
        ax.set_ylabel('DisOrder Rate',fontsize=15)
        sns.lineplot(size='event',
                    markers=True,dashes=False,
                    data=xgb_df[
                        168*(week_num-1):
                        min(168*(week_num), len(xgb_df))
                    ])
        plt.show()