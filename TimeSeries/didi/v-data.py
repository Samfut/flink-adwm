from didiDraw import DrawLSTM, DrawXgboost
from didiUtil import DIDI201705, DIDI201706, DIDI201707, DIDI201708, DIDI201709, DIDI201710

draw_lstm = DrawLSTM()
draw_xgb = DrawXgboost()

map_name = {
    DIDI201705: 'DIDI201705',
    DIDI201706: 'DIDI201706',
    DIDI201707: 'DIDI201707',
    DIDI201708: 'DIDI201708',
    DIDI201709: 'DIDI201709',
    DIDI201710: 'DIDI201710',
}

map_start_time = {
    DIDI201706: '2017-06-01 13:00:00',
    DIDI201707: '2017-07-01 13:00:00',
    DIDI201708: '2017-08-01 13:00:00',
    DIDI201709: '2017-09-01 13:00:00',
    DIDI201710: '2017-10-01 13:00:00',
}

data_set = [DIDI201705, DIDI201706, DIDI201707, DIDI201708, DIDI201709, DIDI201710]

if __name__ == "__main__":
    for i in range(1, len(data_set)):
        print(map_name[data_set[i]])
        draw_lstm.set_train_month(data_set[i-1])
        draw_lstm.get_predict_result(data_set[i-1], data_set[i])
        df = draw_lstm.draw_real_predict(week_num=1, start_time=map_start_time[data_set[i]], title='', figsize=(1,1))
        df.to_csv(f'./v-data/LSTM/{map_name[data_set[i]]}.csv')

        draw_xgb.get_predict_result([data_set[i-1]], [data_set[i]])
        df = draw_xgb.draw_real_predict(week_num=1, start_time=map_start_time[data_set[i]], title='', figsize=(1,1))
        df.to_csv(f'./v-data/XGboost/{map_name[data_set[i]]}.csv')
