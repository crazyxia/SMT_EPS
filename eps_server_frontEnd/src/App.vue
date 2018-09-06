<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
import store from './store'
import {axiosPost} from "./utils/fetchData"
import {lineLitsUrl} from './config/globalUrl'
import {lineStatusData,lineAllDayData,label} from './utils/displayDataInit'
export default {
  name: 'App',
  created(){
    this.getLines();
  },
  methods:{
    getLines:function(){
      $.ajax({
        url:lineLitsUrl,
        type:'POST', //GET
        async:false,    //或false,是否异步
        timeout:12000,    //超时时间
        dataType:'json',    //返回的数据格式：
        beforeSend:function(xhr){
        },
        success:function(data,textStatus,jqXHR){
          store.commit("setLines",data);
          store.commit("setLineSize",data.length);
          store.commit("setLineData",lineStatusData());
          store.commit("setAllDayData",lineAllDayData());
        },
        error:function(xhr,textStatus){
        },
        complete:function(){
      }
    })
    }   
  }
}
</script>

<style lang="scss">
  html,body{
    width:100%;
    height:100%;
    border:none;
    background:url("../static/img/bg.png");
  }
  html,body,div,p,ul,li{
    margin:0;
    padding:0;
  }
  header,main{
    display: block;
  }
  input,select,button{
    border:none;
    outline:none;
    box-shadow:none;
  }
  .form,.form-inline{
    .form-group{
      .form-control{
        outline:none;
        box-shadow:none;
      }
    }
  }
  .btn-group,.form-inline,.modal-dialog{
    .btn{
      outline:none;
      box-shadow:none;
      border:none;
    }
  }
  table{
    .btn-group-sm{
      .btn{
        outline:none;
        box-shadow:none;
        border:none;
      }
    }
  }
  ul,li{
    list-style:none;
  }
  #app {
    font-family:'微软雅黑','Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    color: #2c3e50;
    width:100%;
    height:100%;
    font-size:18px;
  }
</style>
