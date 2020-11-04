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
                start: 40,
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
                data: []
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
                data: [],
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
                data: [],
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
            data: ['adwater window waiting time', 'period window waiting time'],
            left: '8%',
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
                start: 40,
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
                data: []
            }
        ],
        yAxis: [
            {
                name: 'time(ms)',
                type: 'value',
            },
        ],
        series: [
            {
                name: 'adwater window waiting time',
                type: 'bar',
                symbolSize: 8,
                hoverAnimation: false,
                data: [],
                itemStyle: {
                    color: "#E53A40"
                }
            },
            {
                name: 'period window waiting time',
                type: 'line',
                symbolSize: 8,
                hoverAnimation: false,
                data: [],
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
            data: ['adwater waiting time', 'period waiting time'],
            left: '2%',
            orient: "horizontal"
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
                start: 40,
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
                data: []
            }
        ],
        yAxis: [
            {
                name: 'time(ms)',
                type: 'value',
                // max: 2000
            },
        ],
        series: [
            {
                name: 'adwater waiting time',
                type: 'bar',
                symbolSize: 8,
                hoverAnimation: false,
                data: [],
                itemStyle: {
                    color: "#2b90d9"
                }
            },
            {
                name: 'period waiting time',
                type: 'line',
                xAxisIndex: 0,
                yAxisIndex: 0,
                symbolSize: 8,
                hoverAnimation: false,
                data: [],
                itemStyle: {
                    color: "#E53A40"
                }
            }
        ]
    },
    cpu: {
        title: {
            text: 'CPU Stat',
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
            data: ['native', 'slice', 'tree', 'stree'],
            left: '2%',
            orient: "horizontal"
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
                start: 40,
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
                data: []
            }
        ],
        yAxis: [
            {
                name: 'time',
                type: 'value',
                // max: 2000
            },
        ],
        series: [
            {
                name: 'native',
                type: 'line',
                symbolSize: 8,
                hoverAnimation: false,
                data: [],
                itemStyle: {
                    color: "#2b90d9"
                }
            },
            {
                name: 'slice',
                type: 'line',
                xAxisIndex: 0,
                yAxisIndex: 0,
                symbolSize: 8,
                hoverAnimation: false,
                data: [],
                itemStyle: {
                    color: "#E53A40"
                }
            },
            {
                name: 'tree',
                type: 'line',
                xAxisIndex: 0,
                yAxisIndex: 0,
                symbolSize: 8,
                hoverAnimation: false,
                data: [],
                itemStyle: {
                    color: "#000000"
                }
            },
            {
                name: 'stree',
                type: 'line',
                xAxisIndex: 0,
                yAxisIndex: 0,
                symbolSize: 8,
                hoverAnimation: false,
                data: [],
                itemStyle: {
                    color: "#499a56"
                }
            },
        ]
    },
    mem:{
        title: {
            text: 'Mem Stat',
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
            data: ['native', 'slice', 'tree', 'stree'],
            left: '2%',
            orient: "horizontal"
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
                start: 40,
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
                data: []
            }
        ],
        yAxis: [
            {
                name: 'time',
                type: 'value',
                // max: 2000
            },
        ],
        series: [
            {
                name: 'native',
                type: 'line',
                symbolSize: 8,
                hoverAnimation: false,
                data: [],
                itemStyle: {
                    color: "#2b90d9"
                }
            },
            {
                name: 'slice',
                type: 'line',
                xAxisIndex: 0,
                yAxisIndex: 0,
                symbolSize: 8,
                hoverAnimation: false,
                data: [],
                itemStyle: {
                    color: "#E53A40"
                }
            },
            {
                name: 'tree',
                type: 'line',
                xAxisIndex: 0,
                yAxisIndex: 0,
                symbolSize: 8,
                hoverAnimation: false,
                data: [],
                itemStyle: {
                    color: "#000000"
                }
            },
            {
                name: 'stree',
                type: 'line',
                xAxisIndex: 0,
                yAxisIndex: 0,
                symbolSize: 8,
                hoverAnimation: false,
                data: [],
                itemStyle: {
                    color: "#499a56"
                }
            },
        ]
    },
};
export default ops;