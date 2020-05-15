<template>
<el-container>
  <el-header style="padding: 0px">
    <el-menu
      mode="horizontal"
      background-color="#545c64"
      text-color="#fff"
      active-text-color="#ffd04b"
      @select="handlerRouter"
      :default-active="activeMenu"
    >
      <el-menu-item index="home">
        <svg class="ali-icon" aria-hidden="true" style="margin-right: 10px">
          <use xlink:href="#ali-icon-ziyuan"></use>
        </svg>
        <span slot="title">Monitor</span>
      </el-menu-item>
    </el-menu>
  </el-header>
  <el-main style="height: 100%; border: 1px solid #eee">
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
            <el-table-column prop="date" label="time window" width="180">
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
  </el-main>
  <el-footer>
<!--    <div class="copyright"><p>© 2019 <a href="http://github.com/yangsoon">yangsoon</a></p></div>-->
  </el-footer>
</el-container>
</template>

<script>
    import op from '../option'
    export default {
        name: "AchieveMent",
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
          baseUrl(){
              return this.$store.state.app.baseUrl
          },
          activeMenu: {
            get(){
              return this.$store.state.app.activeMenu;
            },
            set(value){
              this.$store.commit("setActiveMenu", value);
            }
          }
        },
        methods:{
          initDelay() {
            let delayChart = this.$echarts.init(document.getElementById("disorder"));
            delayChart.setOption(op.disorder);
            let delayChart1 = this.$echarts.init(document.getElementById("window"));
            delayChart1.setOption(op.window);
            let delayChart2 = this.$echarts.init(document.getElementById("wait"));
            delayChart2.setOption(op.wait);
          },
        }
    }
</script>

<style scoped>
.copyright {
    margin: 4em 0;
    border-top: 1px solid #ddd;
    text-align: center;
}
a {
  text-decoration:none;
  color:inherit;
}
.ali-icon {
  width: 1.2em;
  height: 1.2em;
  vertical-align: -0.15em;
  fill: currentColor;
  overflow: hidden;
}

  .el-col {
    border-radius: 4px;
  }
  .bg-purple-dark {
    background: #99a9bf;
  }
  .bg-purple {
    background: #d3dce6;
  }
  .bg-purple-light {
    background: #e5e9f2;
  }
  .grid-content {
    border-radius: 4px;
    min-height: 36px;
  }
  .row-bg {
    padding: 10px 0;
    background-color: #f9fafc;
  }

</style>