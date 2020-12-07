<template>
  <div>
    <el-row>
      <el-col :span="22" :offset="1">
      <el-card>
        <div slot="header" class="clearfix">
          <h2>滑动窗口算子聚合运算相关参数选择</h2>
        </div>
        <span style="margin-right: 1%">
          窗口大小:
          <el-input style="width: 150px" v-model="windowSize" placeholder="请输入窗口大小"></el-input>
        </span>
        <span style="margin-right: 1%">
          滑动步长:
          <el-input style="width: 160px"  v-model="slideSize" placeholder="请输入滑动步长"></el-input>
        </span>
        <span style="margin-right: 2%">
            聚合函数:
            <el-select
                style="width: 160px"
                v-model="func"
                placeholder="请选择聚合函数">
              <el-option
                  v-for="item in selectFunc"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
              </el-option>
            </el-select>
          </span>
        <span>
            <el-button type="primary" @click="handlerRun" plain style="margin-right: 2%">开始监控</el-button>
        </span>
        <span>
            <el-button type="danger" @click="handlerStop" plain>停止任务</el-button>
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
          <div id="cpu" style="width: 1400px;height: 300px"></div>
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
          <div id="mem" style="width: 1400px;height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import src from "../data"
import echarts from "echarts";
import op from '../option'
import axios from "axios"
export default {
  name: "SlideWindow",
  data() {
    return {
      windowSize: 6,
      slideSize: 2,
      func: 'Count',
      selectFunc: src.SelectFunc,
      isloading: true,
      baseUrl: "http://slave2:5000",
    }
  },
  mounted() {
    this.init();
  },
  methods: {
    updateCpuAndMem() {
      let xtime = [];
      let cnative = [];
      let cslice = [];
      let ctree = [];
      let cstree = [];
      let mnative = [];
      let mslice = [];
      let mtree = [];
      let mstree = [];
      let i = 1;
      let base = this.baseUrl;
      function update() {
          axios.get(base+"/api/slide/sys").then(res=>{
            if(res.data.status === 1) {
              return
            }
            xtime.push(i);
            cnative.push(res.data.cpu.native);
            cslice.push(res.data.cpu.slice);
            ctree.push(res.data.cpu.tree);
            cstree.push(res.data.cpu.stree);
            mnative.push(res.data.mem.native);
            mslice.push(res.data.mem.slice);
            mtree.push(res.data.mem.tree);
            mstree.push(res.data.mem.stree);
            let cpuChart = echarts.init(document.getElementById("cpu"));
            let memChart = echarts.init(document.getElementById("mem"));
            op.cpu.xAxis[0].data = xtime;
            op.cpu.series[0].data = cnative;
            op.cpu.series[1].data = cslice;
            op.cpu.series[2].data = ctree;
            op.cpu.series[3].data = cstree;
            op.mem.xAxis[0].data = xtime;
            op.mem.series[0].data = mnative;
            op.mem.series[1].data = mslice;
            op.mem.series[2].data = mtree;
            op.mem.series[3].data = mstree;
            cpuChart.setOption(op.cpu);
            memChart.setOption(op.mem);
            i++;
          })
      }
      return update;
    },
    init() {
      let cpuChart = echarts.init(document.getElementById("cpu"));
      let memChart = echarts.init(document.getElementById("mem"));
      cpuChart.setOption(op.cpu);
      memChart.setOption(op.mem);
      setInterval(this.updateCpuAndMem(), 2000);
    },
    handlerRun() {
      let params = {
        'win': this.windowSize,
        'sli': this.slideSize,
        'agg': this.func
      }
      axios.get(this.baseUrl+"/api/slide/run", {params: params}).then(res=>{
        if(res.data.status===0) {
          this.isloading = false;
        }
      })
    },
    handlerStop() {
      axios.get(this.baseUrl+"/api/slide/killall",).then(res=>{
        if(res.data.status===0) {
          this.isloading = true;
          window.location.reload(true);
        }
      })
    }
  }
}
</script>

<style scoped>

</style>