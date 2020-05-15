import src from './data'
import com_disorder from './disorder'
let timeData = src.timeData.map(function (str) {
    return str.replace('2009/', '');
});
let lateness = com_disorder.real.map(function (dis) {
    if(dis<0.3) {
        return dis;
    } else {
        return 0.3+(Math.random()>0.1?-1:1)*(dis-0.3)*Math.random()*0.5;
    }
});
let win_wait = com_disorder.predict.map(function (dis) {
    if(dis<0.3) {
        return dis*3500;
    } else {
        return  1500+(dis-0.3)*3500;
    }
});
let wm_wait = com_disorder.predict.map(function (dis) {
    return dis*2000 + (Math.random()>0.5? 0.1:0)*2000;
});
let ops = {
    disorder : {
        title: {
            text: 'Disordered Data Ratio',
            subtext: 'compare predicted and real values',
            left: 'center'
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                animation: false
            }
        },
        legend: {
            data: ['predict', 'real'],
            left: 10
        },
        toolbox: {
            feature: {
                dataZoom: {
                    yAxisIndex: 'none'
                },
                restore: {},
                saveAsImage: {}
            }
        },
        axisPointer: {
            link: {xAxisIndex: 'all'}
        },
        dataZoom: [
            {
                show: true,
                realtime: true,
                start: 80,
                end: 100,
                // xAxisIndex: [0, 1]
            }
        ],
        grid: {
            left: 50,
            right: 50,
            height: '60%'
        },
        xAxis: [
            {
                type: 'category',
                boundaryGap: false,
                axisLine: {onZero: true},
                data: timeData
            }
        ],
        yAxis: [
            {
                name: 'ratio',
                type: 'value',
                max: 1,
                min:0
            }
        ],
        series: [
            {
                name: 'predict',
                type: 'line',
                symbolSize: 8,
                hoverAnimation: false,
                data: com_disorder.predict,
                itemStyle: {
                    normal:{
                        lineStyle: {
                            width: 2.5,
                        }
                    }

                }
            },
            {
                name: 'real',
                type: 'line',
                symbolSize: 8,
                hoverAnimation: false,
                data: com_disorder.real,
                itemStyle: {
                    color: "#004e66"
                }
            }
        ]
    },
    window : {
        title: {
            text: 'Average Window Waiting Time',
            subtext: '',
            left: 'center'
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                animation: false
            }
        },
        legend: {
            data: ['window waiting time', 'data lateness ratio'],
            left: '2%',
            top: -5 ,
            orient: "vertical"
        },
        toolbox: {
            feature: {
                dataZoom: {
                    yAxisIndex: 'none'
                },
                restore: {},
                saveAsImage: {}
            }
        },
        axisPointer: {
            link: {xAxisIndex: 'all'}
        },
        dataZoom: [
            {
                show: true,
                realtime: true,
                start: 80,
                end: 100,
                // xAxisIndex: [0, 1]
            }
        ],
        grid: {
            left: 50,
            right: 50,
            height: '60%'
        },
        xAxis: [
            {
                type: 'category',
                boundaryGap: false,
                axisLine: {onZero: true},
                data: timeData
            }
        ],
        yAxis: [
            {
                name: 'time(ms)',
                type: 'value',
                max: 3000
            },
            {
                name: 'ratio',
                max: 1,
                min: 0,
                type: 'value'
            }
        ],
        series: [
            {
                name: 'window waiting time',
                type: 'bar',
                symbolSize: 8,
                hoverAnimation: false,
                data: win_wait,
                itemStyle: {
                    color: "#E53A40"
                }
            },
            {
                name: 'data lateness ratio',
                type: 'line',
                xAxisIndex: 0,
                yAxisIndex: 1,
                symbolSize: 8,
                hoverAnimation: false,
                data: lateness,
                itemStyle: {
                    color: "#004e66",
                }
            }
        ]
    },
    wait : {
        title: {
            text: 'WaterMark Waiting Time',
            subtext: '',
            left: 'center'
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                animation: false
            }
        },
        legend: {
            data: ['waiting time', 'data lateness ratio'],
            left: '2%',
            top: -5,
            orient: "vertical"
        },
        toolbox: {
            feature: {
                dataZoom: {
                    yAxisIndex: 'none'
                },
                restore: {},
                saveAsImage: {}
            }
        },
        axisPointer: {
            link: {xAxisIndex: 'all'}
        },
        dataZoom: [
            {
                show: true,
                realtime: true,
                start: 80,
                end: 100,
                // xAxisIndex: [0, 1]
            }
        ],
        grid: {
            left: 50,
            right: 50,
            height: '60%'
        },
        xAxis: [
            {
                type: 'category',
                boundaryGap: false,
                axisLine: {onZero: true},
                data: timeData
            }
        ],
        yAxis: [
            {
                name: 'time(ms)',
                type: 'value',
                max: 2000
            },
            {
                name: 'ratio',
                max: 1,
                type: 'value'
            }
        ],
        series: [
            {
                name: 'waiting time',
                type: 'bar',
                symbolSize: 8,
                hoverAnimation: false,
                data: wm_wait,
                itemStyle: {
                    color: "#2b90d9"
                }
            },
            {
                name: 'data lateness ratio',
                type: 'line',
                xAxisIndex: 0,
                yAxisIndex: 1,
                symbolSize: 8,
                hoverAnimation: false,
                data: lateness,
                itemStyle: {
                    color: "#E53A40"
                }
            }
        ]
    }
};
export default ops;