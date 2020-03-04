import os
import pandas as pd
import numpy as np
from typing import List
from tqdm import tqdm

import dateutil
from pandas.tseries.offsets import Day, Hour, Minute, Second

CB201711 = "./citybick/201711-citibike-tripdata.csv"
CB201808 = "./citybick/201808-citibike-tripdata.csv"
CB201809 = "./citybick/201809-citibike-tripdata.csv"
CB201810 = "./citybick/201810-citibike-tripdata.csv"
CB201811 = "./citybick/201811-citibike-tripdata.csv"
CB201812 = "./citybick/201812-citibike-tripdata.csv"
CB201901 = "./citybick/201901-citibike-tripdata.csv"
CB201902 = "./citybick/201902-citibike-tripdata.csv"
CB201903 = "./citybick/201903-citibike-tripdata.csv"
CB201904 = "./citybick/201904-citibike-tripdata.csv"
CB201905 = "./citybick/201905-citibike-tripdata.csv"


def csv2ts(csv_file: str):
    '''
    该函数是专门用来处理csv文件的，先将csv数据转化为df，存储成pkl文件，等下次再次
    读取的时候，如果已经有了pkl直接返回pkl
    params: csv_file代表要处理的那个月的数据 例如：CB201810
    return 返回一个df 只保留了event-time表示开始时间戳, 如下格式：

    0         2018-10-01 00:00:00.701
    1         2018-10-01 00:00:13.578
    2         2018-10-01 00:00:16.604
    ...
    Name: starttime, Length: 1878657, dtype: datetime64[ns]

    '''
    pkl_path = "./pickles/" + csv_file[2:] + ".pkl"
    
    if os.path.isfile(pkl_path):
        return pd.read_pickle(pkl_path)
    
    trip_data = pd.read_csv(csv_file)
    
    trip_data_ts = trip_data['starttime'].apply(lambda x: dateutil.parser.parse(x))
    
    pd.to_pickle(trip_data_ts, pkl_path)
    return trip_data_ts


# 调用该函数将其他月份的数据切分成一个数组，存储到文件夹citibike文件夹中，这个函数是用来产生训练数据集的。
def divide2day(data):
    
    '''
    该函数用来将每月的数据切分成每天的数据。
    params：data表示一个月数据 CB201810
    returns 返回一个数组 数组大小为这个月的日期数 数组元素是一个df 代表每天的数据
    '''

    def produce_day_gaps(data):
        '''
        pandas对时间序列处理的时候可以用 pd.date_range 切分，具体的使用方法请查阅文档
        这个函数主要是用来产生用来切分成每日数据的切片gaps
        '''
        year, month, start_day, end_day = data[0].year, data[0].month, data[0].day, data[len(data)-1].day
        start = "/".join(map(lambda x:str(x),[year, month, start_day]))
        end = "/".join(map(lambda x:str(x), [year, month, end_day]))
        end = pd.Timestamp(end) + Day(1)
        gaps = pd.date_range(start, end, freq="1d")
        return gaps

    # 调用csv2ts
    ts = csv2ts(data)
    gaps = produce_day_gaps(ts)

    days = []
    for i in range(1, len(gaps)):
        temp = ts[(ts>=gaps[i-1])&(ts<gaps[i])]
        temp = temp.reset_index(drop=True)
        days.append(temp)
    
    return days


def analysis_disorder_count(data):
    """
    产生每个小时的数据乱序率
    params: data df是指每天的数据内容
    return: 返回向量 (<day, dayofweek, hour>, disorder)
    """
    day = data[0].day
    dayofweek = data[0].dayofweek
    
    t = data.to_frame()
    t['hour'] = t['starttime'].apply(lambda x: x.hour)
    
    def hour_disorder(hour):
        '''
        返回一个小时内的乱序个数和乱序率
        '''
        MAX = pd.Timestamp("1997/10/17")
        count = 0
        for h in hour:
            if h >= MAX:
                MAX = h
            else:
                count += 1
        return count, count/len(hour)
        
    vec = dict(hour=[], day=[], dayofweek=[])
    res = dict(res=[])
    for i in range(24):
        hour = t[t['hour']==i]
        c, num = hour_disorder(hour['starttime'])
        res['res'].append(num)
        vec['hour'].append(i)
        vec['day'].append(day)
#         vec['quarter'].append(quarter)
        vec['dayofweek'].append(dayofweek)
    return pd.DataFrame(vec), pd.DataFrame(res)


def get_train_data(dayss: List):
    '''
    该函数用于对分类算法的训练/测试数据的提取，和divide2day配合使用,
    params: dayss 该参数是一个数组 用来接收调用了divide2day产生的结果， 一般产生
    训练集的时候需要多个月的数据 所以这里传递给一个数组。
    使用例子如下：
    train_x, train_y = get_train_data([
        divide2day(CB201810),
        divide2day(CB201811), 
        divide2day(CB201812)]
        )
    return: 产生的结果是一个tuple (vec, label), vec和label都是df类型，长度相同。
    vec 表示对应的向量 <day, dayofweek, hour>
    label 表示 数据乱序率 计算的每个小时的数据乱序率
    '''
    vec = []
    res = []
    for i in tqdm(range(len(dayss))):
        days  = dayss[i]
        for day in days:
            x,y = analysis_disorder_count(day)
            vec.append(x)
            res.append(y)
    return pd.concat(vec), pd.concat(res)


# 表示使用前12个小时的数据作为数据乱序率的
TIMESTEPS = 12
# 隐藏层的大小
HIDDEN_SIZE = 40


def disorder_lstm(data):
    '''
    用来计算lstm的数据乱序率，其实和之前的函数analysis_disorder_count的函数功能类似
    只是因为lstm需要前固定周期的数据。所以这里就是简单的计算出来数据乱序率的数据序列，
    后面还需要进行更细致的切分成向量。
    '''
    t = data.to_frame()
    t['hour'] = t['starttime'].apply(lambda x: x.hour)

    def hour_disorder(hour):
        MAX = pd.Timestamp("1997/10/17")
        count = 0
        for h in hour:
            if h >= MAX:
                MAX = h
            else:
                count += 1
        return count, count/len(hour)
    
    res = []
    for i in range(24):
        hour = t[t['hour']==i]
        c, num = hour_disorder(hour['starttime'])
        res.append(num)
    return res


def gen_data(days):
    '''
    生成训练神经网络需要的向量,和标签值，
    params: days之前的参数是一个数组因为要得到多个月的值 对lstm的训练，我们这里只用一个月的数据作为训练集(因为我给忘了)
    return: 返回值是一个tuple (X,Y)
    X是特征向量(d_{i-m}, d_{i-m+1}, ... d_{i}, d_{i+1}, d_{i+2}...)
    Y是label 用于存储标签值。
    '''
    seq = []
    for day in days:
        seq.extend(disorder_lstm(day))
    X = []
    Y = []
    for i in range(len(seq)-TIMESTEPS-1):
        X.append([seq[i:i+TIMESTEPS]])
        Y.append([seq[i+TIMESTEPS]])
    return np.array(X, dtype=np.float32), np.array(Y, dtype=np.float32)