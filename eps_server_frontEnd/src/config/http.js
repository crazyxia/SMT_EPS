import axios from 'axios';
import store from "../store";
import {programFileUploadUrl,loginUrl,downloadClientReportUrl} from './globalUrl';
import Vue from 'vue'

axios.interceptors.request.use(
  config => {
    if(window.localStorage.getItem("token")!==""){
      if(store.state.token === ''){
        store.commit("setToken",window.localStorage.getItem("token"));
      }
    }
    if(config.url !== programFileUploadUrl && config.url !== loginUrl && config.url !== downloadClientReportUrl){
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
      new Vue().$alertWarning('权限不足');
      store.commit('setDenied',true);
    }else{
      store.commit('setDenied',false);
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
