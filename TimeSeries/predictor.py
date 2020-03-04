import os
import pandas as pd
import numpy as np
import xgboost as xgb
import matplotlib.pyplot as plt
import seaborn as sns
from tqdm import tqdm

from sklearn.tree import DecisionTreeRegressor

from keras.models import Sequential
from keras.layers import Dense
from keras.layers import LSTM
from sklearn.preprocessing import MinMaxScaler
from sklearn.metrics import mean_squared_error
from keras.utils.vis_utils import plot_model
from keras.models import load_model


from citybikeUtil import divide2day, gen_data, get_train_data
from citybikeUtil import TIMESTEPS, CB201808, CB201809, CB201810, CB201811


class LSTMPredictor:
    '''
    LSTM预测器,使用方法如下：
        l = LSTMPredictor()
        # 获取训练数据
        x,y = l.get_train_vec(CB201810)
        # 训练模型
        l.train_model(x,y)
        m = l.train_model(x,y)
        pred = m.predict(x)
    '''
    def __init__(self):
        """
            self.model 训练出的模型
            self.train_x 训练向量集合
            self.train_y 训练label集合
        """
        self.model = None
        self.train_x = None
        self.train_y = None

    def train_model(self, train_x, y):
        """
        params: (train_x, y) 训练集
        return model返回训练模型 调用 
            model.predict(test_x)
        返回预测结果
        """
        if os.path.isfile('./lstm.h5'):
            model = load_model("./lstm.h5")
            self.model = model
            return model
        model = Sequential()
        model.add(LSTM(120, input_shape=(train_x.shape[1], train_x.shape[2])))
        model.add(Dense(1))
        model.compile(loss='mean_squared_error', optimizer='adam')
        model.fit(train_x, y, epochs=100, batch_size=1, verbose=2)
        model.save("lstm.h5")
        self.model = model
        return model
    
    def get_train_vec(self, month_data: str):
        '''
        该函数用来产生训练数据和测试数据
        params: month_data: str 需要产生训练数据或者测试数据的月份
        return: (x,y) 产生训练集和测试集 x 代表向量 y 代表 label
        '''
        seqs = divide2day(month_data)
        train_x, train_y = gen_data(seqs)
        # 这里注意需要对train_x进行reshape
        train_x = np.reshape(train_x, (train_x.shape[0], TIMESTEPS, train_x.shape[1]))
        self.train_x = train_x
        self.train_model = train_y
        return train_x, train_y


class DecisionTreePredictor:
    '''
    d = DecisionTreePredict()
    x, y = d.get_train_vec([CB201810, CB201809])
    model = d.train_model(x,y)
    pre = model.predict(x)
    '''

    def __init__(self):
        """
            self.model 训练出的模型
            self.train_x 训练向量集合
            self.train_y 训练label集合
        """
        self.model = None
        self.train_x = None
        self.train_y = None

    def train_model(self, train_x, train_y):
        """
        params: (train_x, train_y) 训练集
        return model返回训练模型 调用 
            model.predict(test_x)
        返回预测结果
        """
        clf = DecisionTreeRegressor()
        clf.fit(train_x, train_y)
        self.model = clf
        return clf
    
    def get_train_vec(self, months):
        train_month = [divide2day(m) for m in months]
        train_x, train_y = get_train_data(train_month)
        self.train_x = train_x
        self.train_y = train_y
        return train_x, train_y


class XGBoostPredictor:
    '''
        model = XGBoostPredictor()
        tx, ty = model.get_train_vec([CB201810, CB201809])
        x, y = model.get_train_vec([CB201811])
        eval_set = [(x,y)]
        xgb = model.train_model(tx, ty, eval_set)
        pred = xgb.predict(x)
    '''
    def __init__(self):
        """
            self.model 训练出的模型
            self.train_x 训练向量集合
            self.train_y 训练label集合
        """
        self.model = None
        self.train_x = None
        self.train_y = None

    def train_model(self, train_x, train_y, eval_set):
        '''
        xgboost 需要获取测试集的数据 eval_set = [(X_test, y_test)]
        '''
        reg = xgb.XGBRegressor(n_estimators=1000)
        reg.fit(train_x, train_y, eval_set=eval_set, early_stopping_rounds=50, verbose=True)
        self.model = reg
        return reg

    def get_train_vec(self, months):
        train_month = [divide2day(m) for m in months]
        train_x, train_y = get_train_data(train_month)
        self.train_x = train_x
        self.train_y = train_y
        return train_x, train_y