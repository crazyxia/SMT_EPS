// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'

import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';

import App from './App'
import router from './router'
import axios from './config/http'
import Chart from 'chart.js'
import store from './store'

import Icon from 'vue-svg-icon/Icon.vue'
Vue.component('icon', Icon) ;

import $ from 'jquery' 
window.jQuery = $;

Vue.use(ElementUI);

import {alertError, alertInfo, alertSuccess,alertWarning} from "./utils/modal";
Vue.prototype.$alertError = alertError;
Vue.prototype.$alertInfo = alertInfo;
Vue.prototype.$alertSuccess = alertSuccess;
Vue.prototype.$alertWarning = alertWarning;

Vue.prototype.$axios = axios;
Vue.prototype.Chart = Chart;
Vue.config.productionTip = false;
new Vue({
  el: '#app',
  router,
  store,
  components: { App },
  template: '<App/>'
});

