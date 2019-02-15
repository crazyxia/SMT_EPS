<template>
  <div class="client">
    <el-form :inline="true" :model="clientInfo" class="demo-form-inline">
      <el-form-item label="客户名">
        <el-input v-model.trim="clientInfo.client" placeholder="客户名"></el-input>
      </el-form-item>
      <el-form-item label="程序表编号">
        <el-input v-model.trim="clientInfo.programNo" placeholder="程序员编号"></el-input>
      </el-form-item>
      <el-form-item label="线号">
        <el-select v-model.trim="clientInfo.line"  value="">
          <el-option label="不限" value=""></el-option>
          <el-option v-for="item in lines" :label="item.line" :value="item.id" :key="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="订单号">
        <el-input v-model.trim="clientInfo.orderNo" placeholder="订单号"></el-input>
      </el-form-item>
      <el-form-item label="工单号">
        <el-input v-model.trim="clientInfo.workOrderNo" placeholder="工单号"></el-input>
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
        <el-button type="info" @click="reset">清空条件</el-button>
      </el-form-item>
    </el-form>
    <ClientTable></ClientTable>
  </div>
</template>

<script>
  import Bus from '../../../utils/bus'
  import {mapGetters,mapActions} from 'vuex'
  import ClientTable from './components/ClientTable'
  import {setInitialTime} from "./../../../utils/time"

  export default {
    name: 'client',
    data() {
      return {
        clientInfo: {
          client: "",
          programNo: "",
          line: "",
          orderNo: "",
          workOrderNo: "",
          startTime: "",
          endTime: ""
        },
        time:[],
        isClear:false
      }
    },
    watch:{
      time: {
        handler(value) {
          if(value !== null){
            this.clientInfo.startTime = value[0];
            this.clientInfo.endTime = value[1];
          }
        },
        deep: true
      }
    },
    created() {
      let time = setInitialTime();
      this.clientInfo.startTime = time[0] +  " 00:00:00";
      this.clientInfo.endTime = time[1] +  " 23:59:59";
      this.time = [this.clientInfo.startTime,this.clientInfo.endTime];
      this.setClient(JSON.parse(JSON.stringify(this.clientInfo)));
    },
    components: {
      ClientTable
    },
    computed: {
      ...mapGetters(['lines','token','client'])
    },
    methods: {
      ...mapActions(['setClient']),
      find: function () {
        if(this.time === null){
          this.$alertWarning("开始日期和结束日期不能为空");
          return;
        }
        this.clientInfo.startTime = this.time[0];
        this.clientInfo.endTime = this.time[1];
        this.setClient(JSON.parse(JSON.stringify(this.clientInfo)));
        Bus.$emit('findClient',true);
      },
      download: function () {
        if(this.time === null){
          this.$alertWarning("开始日期和结束日期不能为空");
          return;
        }
        if(JSON.stringify(this.clientInfo) !== JSON.stringify(this.client)){
          this.$alertWarning("查询条件已更改，请查询后再下载");
          return;
        }
        Bus.$emit('downloadClient',true);
      },
      reset:function(){
        this.clientInfo.client = '';
        this.clientInfo.programNo = '';
        this.clientInfo.line = '';
        this.clientInfo.orderNo = '';
        this.clientInfo.workOrderNo = '';
        let time = setInitialTime();
        this.clientInfo.startTime = time[0] +  " 00:00:00";
        this.clientInfo.endTime = time[1] +  " 23:59:59";
        this.time = [this.clientInfo.startTime,this.clientInfo.endTime];
      }
    }

  }
</script>

<style scoped lang="scss">
  .client{
    padding: 20px;
  }
</style>
