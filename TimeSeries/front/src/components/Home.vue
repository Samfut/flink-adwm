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
    <el-container>
      <el-aside width="200px" style="height: 100%; margin-right: 1%">
        <el-menu>
          <el-menu-item index="watermark">
            <template slot="title">
              <span style="margin-right: 20px">
                <svg class="ali-icon" aria-hidden="true">
                    <use xlink:href="#ali-icon-shijian"></use>
                </svg>
              </span>
              自适应水位线
            </template>
          </el-menu-item>
          <el-menu-item index="slidewindow">
            <template slot="title">
              <span style="margin-right: 20px">
                <svg class="ali-icon" aria-hidden="true">
                    <use xlink:href="#ali-icon-shijian"></use>
                </svg>
              </span>
              自适应水位线
            </template>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <router-view></router-view>
    </el-container>

  </el-main>
  <el-footer>
  </el-footer>
</el-container>
</template>

<script>
    export default {
        name: "Home",
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