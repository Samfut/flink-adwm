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
                @change="handleChange"></el-cascader>
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
            <el-button type="primary">开始发放水位线</el-button>
            <el-button type="info">停止发放</el-button>
          </span>
        </el-card>
      </el-col>
    </el-row>
    <el-divider></el-divider>
    <el-row :gutter="10">
      <el-col :span="22" :offset="1">
        <el-card>
          <div id="disorder" style="width: 1000px;height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
      <br>
    <el-row :gutter="10">
      <el-col :span="22" :offset="1">
        <el-card>
          <div id="window" style="width: 1000px;height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
    <br>
    <el-row :gutter="10">
      <el-col :span="22" :offset="1">
        <el-card>
          <div id="wait" style="width: 1000px;height: 300px"></div>
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
export default {
  name: "WaterMark",
  data(){
    return{
      dataSetValue: [],
      modelValue: [],
      dataSetOption: src.SelectData,
      SelectModel: src.SelectModel
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
    handleChange(value) {
      // eslint-disable-next-line no-console
      console.log(value)
    },
    updateDisOrder() {
      let i = 0;
      let xtime = [];
      let ypredict = [];
      let yreal = [];
      function update() {
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
    initDelay() {
      setInterval(this.updateDisOrder(), 2000);
      let delayChart1 = this.$echarts.init(document.getElementById("window"));
      delayChart1.setOption(op.window);
      let delayChart2 = this.$echarts.init(document.getElementById("wait"));
      delayChart2.setOption(op.wait);
    },
  }
}
</script>

<style scoped>

</style>