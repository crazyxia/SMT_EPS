<template>
  <div class="operation">
    <div v-if="isShow">
      <el-form :inline="true" :model="operationInfo" class="demo-form-inline">
        <el-form-item label="操作类型">
          <el-select v-model.trim="operationInfo.type"  value="">
            <el-option label="不限" value=""></el-option>
            <el-option label="上料" value="0"></el-option>
            <el-option label="换料" value="1"></el-option>
            <el-option label="核料" value="2"></el-option>
            <el-option label="全检" value="3"></el-option>
            <el-option label="发料" value="4"></el-option>
            <el-option label="首检" value="5"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="线号">
          <el-select v-model.trim="operationInfo.line"  value="">
            <el-option label="不限" value=""></el-option>
            <el-option v-for="item in lines" :label="item.line" :value="item.id" :key="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="工单号">
          <el-input v-model.trim="operationInfo.workOrderNo" placeholder="工单号"></el-input>
        </el-form-item>
        <el-form-item label="起止时间">
          <el-date-picker
            :clearable="isClear"
            v-model="time"
            type="datetimerange"
            align="right"
            value-format="yyyy-MM-dd HH:mm:ss"
            range-separator="-"
            :default-time="['00:00:00','23:59:59']"
            start-placeholder="开始日期"
            end-placeholder="结束日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="find">查询</el-button>
          <el-button type="primary" @click="download">报表下载</el-button>
          <el-button type="info" @click="reset">清除条件</el-button>
        </el-form-item>
      </el-form>
      <operation-table  @setIsShow="setIsShow"></operation-table>
    </div>
    <operation-detail v-else  @setIsShow="setIsShow"></operation-detail>
  </div>
</template>

<script>
  import Bus from '../../../utils/bus'
  import {mapGetters,mapActions} from 'vuex'
  import OperationTable from './components/OperationTable'
  import OperationDetail from './operationDetail/OperationDetail'
  import {setInitialTime} from "../../../utils/time"

  export default {
    name: 'operation',
    data() {
      return {
        operationInfo: {
          type: "",
          line: "",
          workOrderNo: "",
          startTime: "",
          endTime: "",
        },
        time:[],
        isShow:true,
        isClear:false
      }
    },
    beforeDestroy(){
      this.setPageInfo({});
    },
    watch:{
      time: {
        handler(value) {
          if(value !== null){
            this.operationInfo.startTime = value[0];
            this.operationInfo.endTime = value[1];
          }
        },
        deep: true
      }
    },
    created() {
      let time = setInitialTime();
      this.operationInfo.startTime = time[0] +  " 00:00:00";
      this.operationInfo.endTime = time[1] +  " 23:59:59";
      this.time = [this.operationInfo.startTime,this.operationInfo.endTime];
      this.setOperation(JSON.parse(JSON.stringify(this.operationInfo)))
    },
    computed: {
      ...mapGetters(['lines','token','operation'])
    },
    components: {
      OperationTable, OperationDetail
    },
    methods: {
      ...mapActions(['setOperation','setPageInfo']),
      find: function () {
        if(this.time === null){
          this.$alertWarning("开始日期和结束日期不能为空");
          return;
        }
        let startTime = new Date(this.time[0]).getTime();
        let endTime = new Date(this.time[1]).getTime();
        if(endTime - startTime > 3600 * 1000 * 24 * 90){
          this.$alertWarning("查询时所选时间范围不得超过90天，请重新选择时间");
          return;
        }
        this.operationInfo.startTime = this.time[0];
        this.operationInfo.endTime = this.time[1];
        this.setOperation(JSON.parse(JSON.stringify(this.operationInfo)));
        Bus.$emit('findOperation',true);
      },
      download: function () {
        if(this.time === null){
          this.$alertWarning("开始日期和结束日期不能为空");
          return;
        }
        if (JSON.stringify(this.operationInfo) !== JSON.stringify(this.operation) ){
          this.$alertWarning("查询条件已更改，请重新查询后再下载");
          return;
        }
        let startTime = new Date(this.time[0]).getTime();
        let endTime = new Date(this.time[1]).getTime();
        if(endTime - startTime > 3600 * 1000 * 24 * 30){
          this.$alertWarning("下载时所选时间范围不得超过30天，请重新选择时间");
          return;
        }
        this.operationInfo.startTime = this.time[0];
        this.operationInfo.endTime = this.time[1];
        Bus.$emit('downloadOperation',true);
      },
      reset:function () {
        this.operationInfo.line = '';
        this.operationInfo.type = '';
        this.operationInfo.workOrderNo = '';
        let time = setInitialTime();
        this.operationInfo.startTime = time[0] +  " 00:00:00";
        this.operationInfo.endTime = time[1] +  " 23:59:59";
        this.time = [this.operationInfo.startTime,this.operationInfo.endTime];
      },
      setIsShow:function(val){
        this.isShow = val;
      }
    }
  }
</script>

<style scoped lang="scss">
  .operation{
    padding: 20px;
  }
</style>
