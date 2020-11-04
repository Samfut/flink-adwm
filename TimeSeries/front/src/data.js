let src = {
    timeData : [],
    waitTime: [],
    windowTime: [],
    ywindow: [],
    ywincom: [],
    ywait: [],
    ycom: [],
    SelectData: [{
        value: 'CB',
        label: '纽约市共享单车数据集',
        children: [{
            value: '201809',
            label: '2018-09'
        },{
            value: '201810',
            label: '2018-10'
        },{
            value: '201811',
            label: '2018-11'
        },{
            value: '201812',
            label: '2018-12'
        },{
            value: '201901',
            label: '2019-01'
        },{
            value: '201902',
            label: '2019-02'
        }]
    },{
        value: 'DIDI',
        label: '海口市滴滴出行数据集',
        children: [{
            value: '201706',
            label: '2017-06'
        },{
            value: '201707',
            label: '2017-07'
        },{
            value: '201708',
            label: '2017-08'
        },{
            value: '201709',
            label: '2017-09'
        },{
            value: '201710',
            label: '2017-10'
        },]
    }],
    SelectModel: [{
        value: 'LSTM',
        label: '深度预测模型-LSTM'
    },{
        value: 'XGboost',
        label: '机器学习预测模型-XGboost'
    }],
    SelectFunc: [{
        value: 'Count',
        label: 'Count'
    },{
        value: 'Sum',
        label: 'Sum'
    },{
        value: 'Max',
        label: 'Max'
    },{
        value: 'Min',
        label: 'Min'
    },{
        value: 'MaxCount',
        label: 'MaxCount'
    },{
        value: 'MinCount',
        label: 'MinCount'
    },{
        value: 'ArithmeticMean',
        label: 'ArithmeticMean'
    },{
        value: 'GeometricMean',
        label: 'GeometricMean'
    },{
        value: 'SampleStdDev',
        label: 'SampleStdDev'
    },{
        value: 'PopulationStdDev',
        label: 'PopulationStdDev'
    }]
};

export default src;