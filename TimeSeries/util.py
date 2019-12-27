import os
import pandas as pd
import numpy as np
import xgboost as xgb
import matplotlib.pyplot as plt
import seaborn as sns
from tqdm import tqdm
import plotly.graph_objects as go

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
    pkl_path = "./pickles/" + csv_file[2:] + ".pkl"
    
    if os.path.isfile(pkl_path):
        return pd.read_pickle(pkl_path)
    
    trip_data = pd.read_csv(csv_file)
    
    trip_data_ts = trip_data['starttime'].apply(lambda x: dateutil.parser.parse(x))
    
    pd.to_pickle(trip_data_ts, pkl_path)
    return trip_data_ts

def divide2day(data):
    
    # 生成每天gap
    def produce_day_gaps(data):
        year, month, start_day, end_day = data[0].year, data[0].month, data[0].day, data[len(data)-1].day
        start = "/".join(map(lambda x:str(x),[year, month, start_day]))
        end = "/".join(map(lambda x:str(x), [year, month, end_day]))
        end = pd.Timestamp(end) + Day(1)
        gaps = pd.date_range(start, end, freq="1d")
        return gaps
    
    ts = csv2ts(data)
    gaps = produce_day_gaps(ts)
    
    days = []
    for i in range(1, len(gaps)):
        temp = ts[(ts>=gaps[i-1])&(ts<gaps[i])]
        temp = temp.reset_index(drop=True)
        days.append(temp)
    
    return days


def cal_delay(day, interval, delta):
    """
    delta假设为每条消息的事件间隔， 默认是1s
    day 是一整天的数据
    interval 时间窗口大小，假设在每个时间窗口内计算 现在默认是计算每分钟
    水印生成是每到来一个事件就根据事件的event_time创建水印
    """
#     day = day.reset_index(drop=True)
    
    global windows_end
    global windows_count
    
    start = day[0].year, day[0].month, day[0].day, day[0].hour
    start = pd.Timestamp(*start)
    windows_end = start + interval
    
    # 窗口个数
    # TODO 需要改 根据interval产生窗口个数
    windows = pd.Series([[0] for i in range(24*60)])
    # 窗口下标
    windows_idx = [0 for i in range(24*60*60)]
    windows_count = 0
   
    for idx, event in enumerate(day):
        if event > windows_end:
            minute = (event - windows_end).seconds // 60
            windows_end += (minute + 1)*interval
            while minute >= 0:
                windows_idx[windows_count] = idx - 1
                windows_count += 1
                minute -= 1
            continue
            
        if event < windows_end - interval:
            # 去掉迟到10分钟以上的
            if (windows_end - interval - event).seconds > 60 * 5:
                continue
            win_idx = event.hour*60 + event.minute
            windows[win_idx].append((idx-windows_idx[win_idx])*delta)
            
    return windows
