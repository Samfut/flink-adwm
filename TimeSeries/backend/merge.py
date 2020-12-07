import pandas as  pd
from datetime import datetime
import argparse

def compare_adwm_perwm(path):
    waterlist = pd.read_csv(path)
    waterlist = waterlist[:-2]
    waterlist.columns = ['water', 'event']
    waterlist['skew'] = waterlist['event'] - waterlist['water']
    date_index = pd.date_range(start='2019-02-01 00:01:00',
                               periods=len(waterlist), 
                               freq='T')
    
    index = [date_index[i].hour for i in range(len(date_index))]
    if "CB" in path:
        data = {
            "adwater": waterlist['skew'].tolist(),
            "period(1000ms)": [1000]*len(index),
        }
        df = pd.DataFrame(data=data, index=date_index)
        df.index = waterlist['event'].apply(lambda x: datetime.fromtimestamp(x//1000)).tolist()
        df = df.resample('1min').mean()
        df.to_csv("CB201902.csv")

    if "DIDI" in path:
        data = {
            "adwater": waterlist['skew'].tolist(),
            "period(1000ms)": [120000]*len(index),
        }
        df = pd.DataFrame(data=data, index=date_index)
        df.index = waterlist['event'].apply(lambda x: datetime.fromtimestamp(x//1000)).tolist()
        df = df.resample('1min').mean()
        df['adwater'] = df['adwater'].fillna(0)
        df['period(1000ms)'] = df['period(1000ms)'].fillna(120000)
        df.to_csv("CB201902.csv")


def get_drop_latency(wm, latency):
    """
        params: wm 水印和事件的映射关系 哪一个事件时间产生了水印
                latency: 窗口被哪个水印触发
        return dict {
            "drop": 真实被丢弃的概率(一般不用),
            "late": 迟到率,
            "avg_wait": 每个窗口的平均等待时间
            "pre_win_wait": 用来具体记录每个窗口的等待时间
        }
    """
    threshold = 0
    if 'DIDI' in wm:
        threshold = 120000
    wm = pd.read_csv(wm)
    latency = pd.read_csv(latency)
    wm.columns = ["watermark", "ts"]
    # drop1 = wm[-1:].iloc[0]['watermark']/wm[-1:].iloc[0]['ts']
    # drop2 = wm[-2:-1].iloc[0]['watermark']/wm[-2:-1].iloc[0]['ts']
    latency.columns = ["watermark", "window_end"]
    latency = latency.set_index("watermark")
    wm = wm.set_index("watermark")
    res = latency.join(wm, how='inner')
    res = res.reset_index(drop = True)
    
    # all_wait = 0
    pre_win_wait = []
    for index, row in res.iterrows():
        if row['ts'] - row['window_end'] > threshold+6000:
            row['ts']  = row['window_end'] + threshold
        pre_win_wait.append(row['ts'] - row['window_end'])
    
    df = pd.DataFrame(data=dict(time=res['window_end'].tolist(),wait=pre_win_wait))
    df.index = df['time'].apply(lambda x: datetime.fromtimestamp(x//1000)).tolist()
    df.to_csv('a.csv')

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='result file')
    parser.add_argument(dest='filenames',metavar='filename', nargs='*')
    args = parser.parse_args()
    water_wait = args.filenames[0]
    window_wait = args.filenames[1]
    compare_adwm_perwm(water_wait)
    get_drop_latency(water_wait,window_wait)

