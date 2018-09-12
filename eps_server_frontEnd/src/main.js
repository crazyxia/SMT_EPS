// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import locale from './locale/zh-cn'
import axios from './config/http'
/*store*/
import store from './store'
import Chart from 'chart.js'

/*bootstrap*/
import 'bootstrap/dist/css/bootstrap.min.css' 
import 'bootstrap/dist/js/bootstrap.min.js'

/*vue2-datatable-component*/
import Datatable from 'vue2-datatable-component'
Vue.use(Datatable,{locale})

/*svg*/
import Icon from 'vue-svg-icon/Icon.vue'
Vue.component('icon', Icon) 

/*font-awesome*/
import 'font-awesome/css/font-awesome.css'

/*jquery*/
import $ from 'jquery' 
window.jQuery = $
Vue.prototype.$axios = axios
Vue.prototype.Chart = Chart;
Vue.config.productionTip = false
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>'
})

