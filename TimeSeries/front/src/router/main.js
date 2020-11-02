import Vue from 'vue'
import VueRouter from 'vue-router'

// import Home from '../components/Home'
import WaterMark from '../components/WaterMark'
import SlideWindow from "../components/SlideWindow";

// import store from '../store/main'

Vue.use(VueRouter);

let router = new VueRouter({
  routes:[
    {
      path: '/',
      redirect: 'watermark'
    },
    {
      path:'/watermark',
      name: 'watermark',
      component: WaterMark
    },
    {
      path: '/slidewindow',
      name: 'slidewindow',
      component: SlideWindow
    }
  ]
});

// router.beforeEach((to, from, next) => {
//   store.commit("setActiveMenu", to.name);
//   next()
// });


export default router