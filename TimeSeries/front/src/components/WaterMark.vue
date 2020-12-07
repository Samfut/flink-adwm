<template>
  <div>
    <el-row>
      <el-col :span="22" :offset="1">
        <el-card>
          <div slot="header" class="clearfix">
            <h2>自适应水位线发放机制相关参数选择</h2>
          </div>
          <span style="margin-right: 2%;">
            数据集:
            <el-cascader
                style="width: 260px"
                v-model="dataSetValue"
                placeholder="请选择数据集"
                :options="dataSetOption"
            ></el-cascader>
          </span>
          <span>
            预测模型:
            <el-select
                style="width: 220px"
                v-model="modelValue"
                placeholder="请选择预测模型">
              <el-option
                  v-for="item in SelectModel"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
              </el-option>
            </el-select>
          </span>
          <span style="margin-left: 3%">
            <el-button type="primary" @click="getDataSet" plain>开始发放水位线</el-button>
            <el-button type="danger" @click="stopDataSet" plain>停止发放</el-button>
          </span>
        </el-card>
      </el-col>
    </el-row>
    <el-divider></el-divider>
    <el-row>
      <el-col :span="22" :offset="1">
        <el-card v-loading="isloading"
                 element-loading-text="等待数据加载中"
                 element-loading-spinner="el-icon-loading"
        >
          <div id="disorder" style="width: 1400px;height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
      <br>
      <el-row>
      <el-col :span="22" :offset="1">
        <el-card v-loading="isloading"
                 element-loading-text="等待数据加载中"
                 element-loading-spinner="el-icon-loading"
        >
          <div id="wait" style="width: 1400px;height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
    <br>
    <el-row>
      <el-col :span="22" :offset="1">
        <el-card v-loading="isloading"
                 element-loading-text="等待数据加载中"
                 element-loading-spinner="el-icon-loading"
        >
          <div id="window" style="width: 1400px;height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
    <br>
  </div>
</template>

<script>
import op from '../option'
import src from '../data'
import com_disorder from '../disorder'
import echarts from 'echarts'
import axios from "axios"
export default {
  name: "WaterMark",
  data(){
    return{
      dataSetValue: [],
      modelValue: [],
      dataSetOption: src.SelectData,
      SelectModel: src.SelectModel,
      baseUrl: "http://knl:5000",
      isloading: true
    }
  },
  created(){
  },
  mounted() {
    this.initDelay();
  },
  computed:{
  },
  methods:{
    getDataSet() {
      let params = {
        dataset: this.dataSetValue[0] + this.dataSetValue[1],
        model: this.modelValue
      }
      axios.get(this.baseUrl+"/api/window/wait", {params:params}).then(res=>{
        // eslint-disable-next-line no-console
        console.log(res.data.ywait)
        src.windowTime = res.data.xtime;
        src.ywindow = res.data.ywait;
        src.ywincom = res.data.ycom;
      })
      axios.get(this.baseUrl+"/api/watermark/predict", {params:params}).then(res=>{
        // eslint-disable-next-line no-console
        this.isloading = false;
        src.timeData = res.data.xtime;
        com_disorder.predict = res.data.ypredict;
        com_disorder.real = res.data.yreal;
      })
      axios.get(this.baseUrl+"/api/watermark/wait", {params:params}).then(res=>{
        // eslint-disable-next-line no-console
        src.waitTime = res.data.xtime;
        src.ywait = res.data.ywait;
        src.ycom = res.data.ycom;
      })

    },
    stopDataSet(){
      window.location.reload(true);
    },
    updateDisOrder() {
      let i = 0;
      let xtime = [];
      let ypredict = [];
      let yreal = [];
      function update() {
        if(i>src.timeData.length) {
          return
        }
        xtime.push(src.timeData[i]);
        ypredict.push(com_disorder.predict[i]);
        yreal.push(com_disorder.real[i]);
        let delayChart = echarts.init(document.getElementById("disorder"));
        op.disorder.xAxis[0].data = xtime;
        op.disorder.series[0].data = ypredict;
        op.disorder.series[1].data = yreal;
        delayChart.setOption(op.disorder);
        i++;
      }
      return update;
    },

    updateWait() {
      let i = 0;
      let gap = 400;
      let xtime = [];
      let ywait = [];
      let ycom = [];
      function update() {
          if(i > src.waitTime.length) {
            return;
          }
          xtime.push(src.waitTime[i+gap]);
          ywait.push(src.ywait[i+gap]);
          if(src.ycom[i+gap]===0) {
            ycom.push(1000);
          } else {
            ycom.push(src.ycom[i+gap]);
          }
          let WaitChart = echarts.init(document.getElementById("wait"));
          op.wait.xAxis[0].data = xtime;
          op.wait.series[0].data = ywait;
          op.wait.series[1].data = ycom;
          WaitChart.setOption(op.wait);
          i++;
      }
      return update;
    },

    updateWindow() {
      let i = 0;
      let gap = 200;
      let xtime = [];
      let ywait = [];
      let ycom = [];
      function update() {
        if(i > src.ywindow.length) {
          return;
        }
        xtime.push(src.windowTime[i+gap]);
        ywait.push(src.ywindow[i+gap]);
        ycom.push(src.ywincom[i+gap]);
        let Chart = echarts.init(document.getElementById("window"));
        op.window.xAxis[0].data = xtime;
        op.window.series[0].data = ywait;
        op.window.series[1].data = ycom;
        Chart.setOption(op.window);
        i++;
      }
      return update;
    },
    initDelay() {
      setInterval(this.updateDisOrder(), 1000);
      setInterval(this.updateWait(), 500);
      setInterval(this.updateWindow(), 1000);
    },
  }
}
</script>

<style scoped>

</style>