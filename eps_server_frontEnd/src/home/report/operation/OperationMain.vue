<template>
  <div class="operation">
    <div v-if="isShow">
  	<form class="form-inline" role="form" :action="url" method="post" @submit.prevent="download" id="operationForm">
      <div class="form-group">
        <label for="type">操作类型</label>
        <select class="form-control" id="operationType" v-model.trim="operationInfos.type" name="type">
          <option selected="selected" disabled="disabled"  style='display:none' value=''></option>
          <option value="0">上料</option>
          <option value="1">换料</option>
          <option value="2">全检</option>
          <option value="3">抽检</option>
          <option value="4">仓库</option>
        </select>
      </div>
  		<div class="form-group">
    		<label for="operation">客户名</label>
    		<input type="text" class="form-control" id="operation" v-model.trim="operationInfos.operation"name="operation">
  		</div>
      <div class="form-group">
          <label for="line">线号</label>
          <select class="form-control" id="line" v-model.trim="operationInfos.line" name="line">
            <option selected="selected" disabled="disabled"  style='display:none' value=''></option>
            <option v-for="item in lines" :value="item.id">{{item.line}}</option>
          </select>
      </div>
      <div class="form-group">
        <label for="workOrderNo">工单号</label>
        <input type="text" class="form-control" id="workOrderNo" v-model.trim="operationInfos.workOrderNo" name="workOrderNo">
      </div>
      <div class="form-group">
        <label for="time">起止时间</label>
        <input type="date" class="form-control" v-model.trim="sTime">
        <input type="date" class="form-control" v-model.trim="eTime">
      </div>
      <input type="hidden" name="startTime" v-model.trim="operationInfos.startTime">
      <input type="hidden" name="endTime" v-model.trim="operationInfos.endTime">
      <input type="hidden" name="#TOKEN#" v-model.trim="token">
  		<div class="btn-group">
    		<button type="button" class="btn btn_find" @click="find">查询</button>
    		<button type="submit" class="btn btn_download">报表下载</button>
 		  </div>
    </form>
    <operationTable :operationInfos="operationInfos"></operationTable>
  </div>
  <OperationDetail v-else :operationInfos="operationInfos"></OperationDetail>
  </div>
</template>

<script>
import store from './../../../store' 
import OperationTable from './components/OperationTable' 
import OperationDetail from './operationDetail/OperationDetail'
import {setInitialTime,checkTime,checkTimeByFind} from "./../../../utils/time"
import {downloadOperationReportUrl} from './../../../config/globalUrl'
export default {
  name:'operation',
  data () {
    return {
      operationInfos:{
        type:0,
        line:"",
        workOrderNo:"",
        startTime:"",
        endTime:"",
        client:""
      },
      sTime:"",
      eTime:"",
      isShow:true
    }
  },
  created(){
    let timeArr = setInitialTime(this.sTime,this.eTime);
    console.log(timeArr);
    this.sTime = timeArr[0];
    this.eTime = timeArr[1];
    this.operationInfos.startTime = this.sTime + " 00:00:00";
    this.operationInfos.endTime = this.eTime + " 23:59:59";
  },
  watch:{
    sTime:function(val){
      this.operationInfos.startTime = val + " 00:00:00";
    },
    eTime:function(val){
      this.operationInfos.endTime = val + " 23:59:59";
    },
    operationDetailShow:function(val){
      if(val == true){
        this.isShow = false;
      }else{
        this.isShow = true;
      }
    }
  },
  computed:{
    lines:function(){
      return store.state.lines;
    },
    url:function(){
      return downloadOperationReportUrl
    },
    token:function(){
      return store.state.token
    },
    operationDetailShow:function(){
      return store.state.operationDetailShow;
    }
  },
  components:{
    OperationTable,OperationDetail
  },
  methods:{
    find:function(){
      let isOk = checkTimeByFind(this.operationInfos.startTime,this.operationInfos.endTime);
      if(isOk){
        store.commit("setIsFind",true);
      }
    },
    download:function(){
      let isOk = checkTime(this.operationInfos.startTime,this.operationInfos.endTime);
      if(isOk){
        $('#operationForm')[0].submit();
      }
    }
  }
}
</script> 

<style scoped lang="scss">
@import '@/assets/css/common.scss';
</style>
