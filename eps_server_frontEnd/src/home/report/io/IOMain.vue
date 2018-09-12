<template>
  <div class="io">
    <form class="form-inline" role="form">
      <div class="form-group">
        <label for="position">仓位</label>
        <input type="text" class="form-control" id="position" v-model="io.position"> 
      </div>
      <div class="form-group">
        <label for="custom">供应商</label>
        <input type="text" class="form-control" id="custom" v-model="io.custom">
      </div>
      <div class="form-group">
        <label for="operator">操作员</label>
        <input type="text" class="form-control" id="operator" v-model="io.operator">
      </div>
      <div class="form-group">
        <label for="materialNo">料号</label>
        <input type="text" class="form-control" id="materialNo" v-model="io.materialNo">
      </div>
      <div class="form-group">
        <label for="time">起止时间</label>
        <input type="date" class="form-control" id="startTime" v-model="sTime">
        <input type="date" class="form-control" id="endTime" v-model="eTime">
      </div>
      <div class="btn-group">
        <button type="button" class="btn btn_find" @click="find">查询</button>
      </div>
    </form>
    <IOTable :ioInfos="io"></IOTable>
  </div>
</template>

<script>
import store from './../../../store' 
import IOTable from './components/IOTable'
import {setInitialTime,checkTimeByFind} from "./../../../utils/time"
export default {
  name:'io',
  data () {
    return {
      io:{
        position:"",
        custom:"",
        materialNo:"",
        operator:"",
        startTime:"",
        endTime:"",
      },
      sTime:"",
      eTime:""
    }
  },
  components:{
    IOTable
  },
  mounted(){
    let timeArr = setInitialTime(this.sTime,this.eTime);
    this.sTime = timeArr[0];
    this.eTime = timeArr[1];
  },
  watch:{
    sTime:function(val){
      this.io.startTime = val + " 00:00:00";
    },
    eTime:function(val){
      this.io.endTime = val + " 23:59:59";
    },
  },
  methods:{
    find:function(){
      let isOk = checkTimeByFind(this.io.startTime,this.io.endTime);
      if(isOk){
        store.commit("setIsFind",true);
      }
    }
  }
}
</script> 

<style scoped lang="scss">
@import './../../../../static/css/common.scss'
</style>
