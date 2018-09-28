import axios from 'axios';
import store from "../store";
import router from '../router';
import {programFileUploadUrl,loginUrl,downloadClientReportUrl} from './globalUrl';

// /*axios.defaults.timeout = 5000;
// axios.defaults.baseURL = window.g.API_URL + '/eps_server/';*/

axios.interceptors.request.use(
  config => {
    if(window.localStorage.getItem("token")!=""){
      if(store.state.token == ''){
        store.commit("setToken",window.localStorage.getItem("token"));
      }
    }
    if(config.url != programFileUploadUrl && config.url != loginUrl && config.url != downloadClientReportUrl){
      if (store.state.token !== '') {
        if (config.data === "") {
          config.data += ("#TOKEN#=" + store.state.token);
        } else {
          config.data += ("&#TOKEN#=" + store.state.token);
        }
      }
    }
    return config;
  },
  error => {
    return Promise.reject(error)
  }
);
axios.interceptors.response.use(
  res => {
    if (res.data.result === "failed_access_denied"){
      store.commit('setToken', '');
      localStorage.removeItem('token');
      router.replace({
        path: '/login',
      });
      alert('权限不足');
    }
    return res
  },
  error => {
    if (error.response) {
      console.log(JSON.stringify(error))
    }
    return Promise.reject(JSON.stringify(error))
  }
);
export default axios;
