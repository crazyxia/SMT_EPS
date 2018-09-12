<template>
  <div class="client">
  	<form class="form-inline" role="form" :action="url" method="post" @submit.prevent="download" id="clientForm">
  		<div class="form-group">
    		<label for="client">客户名</label>
    		<input type="text" class="form-control" id="client" v-model="clientInfos.client" name="client"> 
  		</div>
  		<div class="form-group">
    		<label for="programNo">程序表编号</label>
    		<input type="text" class="form-control" id="programNo" v-model="clientInfos.programNo" name="programNo">
  		</div>
      <div class="form-group">
          <label for="line">线号</label>
          <select class="form-control" id="line" v-model="clientInfos.line" name="line">
            <option selected="selected" disabled="disabled"  style='display:none' value=''></option>
            <option v-for="item in lines">{{item}}</option>
          </select>
      </div>
      <div class="form-group">
        <label for="orderNo">订单号</label>
        <input type="text" class="form-control" id="orderNo" v-model="clientInfos.orderNo" name="orderNo">
      </div>
      <div class="form-group">
        <label for="workOrderNo">工单号</label>
        <input type="text" class="form-control" id="workOrderNo" v-model="clientInfos.workOrderNo" name="workOrderNo">
      </div>
      <div class="form-group">
        <label for="time">起止时间</label>
        <input type="date" class="form-control"  v-model="sTime" >
        <input type="date" class="form-control"  v-model="eTime">
      </div>
      <input type="hidden" name="startTime" v-model="clientInfos.startTime">
      <input type="hidden" name="endTime" v-model="clientInfos.endTime">
      <input type="hidden" name="#TOKEN#" v-model="token">
  		<div class="btn-group">
    		<button type="button" class="btn btn_find" @click="find">查询</button>
    		<button type="submit" class="btn btn_download">报表下载</button>
 		  </div>
    </form>
    <ClientTable :clientInfos="clientInfos"></ClientTable>
  </div>
</template>

<script>
import store from './../../../store' 
import ClientTable from './components/ClientTable'
import {setInitialTime,checkTime,checkTimeByFind} from "./../../../utils/time"
import {downloadClientReportUrl} from './../../../config/globalUrl'
import Vue from 'vue'

export default {
  name:'client',
  data () {
    return {
      clientInfos:{
        client:"",
        programNo:"",
        line:"",
        orderNo:"",
        workOrderNo:"",
        startTime:"",
        endTime:""
      },
      sTime:"",
      eTime:""
    }
  },
  mounted(){
    let timeArr = setInitialTime(this.sTime,this.eTime);
    this.sTime = timeArr[0];
    this.eTime = timeArr[1];
  },
  watch:{
    sTime:function(val){
      this.clientInfos.startTime = val + " 00:00:00";
    },
    eTime:function(val){
      this.clientInfos.endTime = val + " 23:59:59";
    }
  },
  components:{
    ClientTable
  },
  computed:{
    lines:function(){
      return store.state.lines;
    },
    url:function(){
      return downloadClientReportUrl
    },
    token:function(){
      return store.state.token
    }
  },
  methods:{
    find:function(){
      let isOk = checkTimeByFind(this.clientInfos.startTime,this.clientInfos.endTime);
      if(isOk){
        store.commit("setIsFind",true);
      }
    },
    download:function(){
      let isOk = checkTime(this.clientInfos.startTime,this.clientInfos.endTime);
      if(isOk){
        $('#clientForm')[0].submit();
      }
    }
  }
}
</script> 

<style scoped lang="scss">
@import './../../../../static/css/common.scss'
</style>
