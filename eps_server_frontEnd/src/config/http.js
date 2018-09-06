import axios from 'axios';
import Qs from 'qs';
import store from "../store";
import router from '../router';
import {programFileUploadUrl,loginUrl,downloadClientReportUrl} from './globalUrl';

axios.defaults.timeout = 5000;
axios.defaults.baseURL = window.g.API_URL + '/eps_server/';

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
      //console.log(config)
      }
    }
    return config;
  },
  error => {
    return Promise.reject(error)
  }
);

export default axios;
