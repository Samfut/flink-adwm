from citybikeDraw import DrawLSTM, DrawXgboost
from citybikeUtil import CB201808, CB201809, CB201810, CB201811, CB201812, CB201901, CB201902

draw_lstm = DrawLSTM()
draw_xgb = DrawXgboost()

map_name = {
    CB201808: 'CB201808',
    CB201809: 'CB201809',
    CB201810: 'CB201810',
    CB201811: 'CB201811',
    CB201812: 'CB201812',
    CB201901: 'CB201901',
    CB201902: 'CB201902',
}

map_start_time = {
    CB201809: '2018-09-01 13:00:00',
    CB201810: '2018-10-01 13:00:00',
    CB201811: '2018-11-01 13:00:00',
    CB201812: '2018-12-01 13:00:00',
    CB201901: '2019-01-01 13:00:00',
    CB201902: '2019-02-01 13:00:00',    
}

data_set = [CB201808, CB201809, CB201810, CB201811, CB201812, CB201901, CB201902]

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
