import Vue from 'vue'
import App from './App.vue'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import './assets/global.css'
import axios from 'axios';

//注册login界面1
import VueRouter from 'vue-router';
import router from './router';
import store from './store';

Vue.prototype.$axios =axios;
Vue.prototype.$httpUrl ='http://localhost:8090';
Vue.config.productionTip = false

//注册login界面2
Vue.use(VueRouter);
Vue.use(ElementUI);

new Vue({
  router,
  store,
  render: h => h(App),
}).$mount('#app')
