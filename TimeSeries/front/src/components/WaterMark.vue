<template>
  <div>
    <el-row :gutter="10">
      <el-col :span="12">
        <el-card>
          <div id="disorder" style="width: 650px;height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div id="window" style="width: 650px;height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
    <br>
    <el-row :gutter="10">
      <el-col :span="12">
        <el-card>
          <div id="wait" style="width: 650px;height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div slot="header" style="height: 10px;">
            <center><span><strong>Evaluation Metrics for Predict Model</strong></span></center>
          </div>
          <el-table
              :data="tableData"
              border
              shadow="always"
              height="264"
              style="width: 100%; margin-top: -10px">
            <el-table-column prop="date" label="time" width="180">
              <template slot-scope="scope">
                <i class="el-icon-time"></i>
                <span style="margin-left: 10px">{{ scope.row.date }}</span>
              </template>
            </el-table-column>
            <el-table-column
                prop="name"
                label="RMES"
                width="180">
            </el-table-column>
            <el-table-column
                prop="address"
                label="MAE">
            </el-table-column>
            <el-table-column
                prop="r"
                label="R-squared">
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
    <br>
    <el-row :gutter="10">
      <el-col :span="12">
        <el-card>
          <div id="th" style="width: 660px;height: 280px"></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div id="mem" style="width: 310px;height: 280px"></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div id="cpu" style="width: 300px;height: 280px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import op from '../option'
export default {
  name: "WaterMark",
  data(){
    return{
      tableData: [{
        date: '2018-07-30',
        name: '0.062314',
        address: '0.027652',
        r: '0.867158'
      }, {
        date: '2018-07-29',
        name: '0.048207',
        address: '0.021740',
        r: '0.879027'
      }, {
        date: '2018-07-28',
        name: '0.07449',
        address: '0.035753',
        r: '0.849027'
      }, {
        date: '2018-07-27',
        name: '0.074491',
        address: '0.034202',
        r: '0.905348'
      }, {
        date: '2018-07-26',
        name: '0.041274',
        address: '0.014607',
        r: '0.962902'
      }]
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
    initDelay() {
      let delayChart = this.$echarts.init(document.getElementById("disorder"));
      delayChart.setOption(op.disorder);
      let delayChart1 = this.$echarts.init(document.getElementById("window"));
      delayChart1.setOption(op.window);
      let delayChart2 = this.$echarts.init(document.getElementById("wait"));
      delayChart2.setOption(op.wait);
      let mem = this.$echarts.init(document.getElementById("mem"));
      mem.setOption(op.mem);
      let cpu = this.$echarts.init(document.getElementById("cpu"));
      cpu.setOption(op.cpu);
      let th = this.$echarts.init(document.getElementById("th"));
      th.setOption(op.th);
    },
  }
}
</script>

<style scoped>

</style>