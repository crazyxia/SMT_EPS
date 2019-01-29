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
          v-model="time"
          type="daterange"
          align="right"
          value-format="yyyy-MM-dd"
          range-separator="-"
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
  import {downloadFile} from "../../../utils/fetchData";
  import {setInitialTime} from "./../../../utils/time"
  import {downloadClientReportUrl} from './../../../config/globalUrl'

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
        currentPage: 1,
        pageSize: 20
      }
    },
    created() {
      this.time = setInitialTime();
      this.clientInfo.startTime = this.time[0] +  " 00:00:00";
      this.clientInfo.endTime = this.time[1] +  " 23:59:59";
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
        this.clientInfo.startTime = this.time[0] +  " 00:00:00";
        this.clientInfo.endTime = this.time[1] +  " 23:59:59";
        this.setClient(JSON.parse(JSON.stringify(this.clientInfo)));
        Bus.$emit('findClient',true);
      },
      download: function () {
        this.clientInfo["currentPage"] = this.client.currentPage;
        this.clientInfo["pageSize"] = this.client.pageSize;
        if(JSON.stringify(this.clientInfo) !== JSON.stringify(this.client)){
          this.$alertWarning("查询条件已更改，请查询后再下载");
          return;
        }
        this.clientInfo.startTime = this.time[0] +  " 00:00:00";
        this.clientInfo.endTime = this.time[1] +  " 23:59:59";
        let data = this.clientInfo;
        data["pageSize"] = this.pageSize;
        data["currentPage"] = this.currentPage;
        data["#TOKEN#"] = this.token;
        downloadFile(downloadClientReportUrl,data);
      },
      reset:function(){
        this.clientInfo.client = '';
        this.clientInfo.programNo = '';
        this.clientInfo.line = '';
        this.clientInfo.orderNo = '';
        this.clientInfo.workOrderNo = '';
        this.time = setInitialTime();
        this.clientInfo.startTime = this.time[0] +  " 00:00:00";
        this.clientInfo.endTime = this.time[1] +  " 23:59:59";
      }
    }

  }
</script>

<style scoped lang="scss">
  .client{
    padding: 20px;
  }
</style>
