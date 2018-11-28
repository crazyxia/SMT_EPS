<template>
  <div class="client">
    <form class="form-inline" role="form" :action="url" method="post" @submit.prevent="download" id="clientForm">
      <div class="form-group">
        <label for="client">客户名</label>
        <input type="text" class="form-control" id="client" v-model.trim="clientInfos.client" name="client">
      </div>
      <div class="form-group">
        <label for="programNo">程序表编号</label>
        <input type="text" class="form-control" id="programNo" v-model.trim="clientInfos.programNo" name="programNo">
      </div>
      <div class="form-group">
        <label for="line">线号</label>
        <select class="form-control" id="line" v-model.trim="clientInfos.line" name="line">
          <option selected="selected" value=''>不限</option>
          <option v-for="item in lines" :value="item.id">{{item.line}}</option>
        </select>
      </div>
      <div class="form-group">
        <label for="orderNo">订单号</label>
        <input type="text" class="form-control" id="orderNo" v-model.trim="clientInfos.orderNo" name="orderNo">
      </div>
      <div class="form-group">
        <label for="workOrderNo">工单号</label>
        <input type="text" class="form-control" id="workOrderNo" v-model.trim="clientInfos.workOrderNo"
               name="workOrderNo">
      </div>
      <div class="form-group">
        <label for="time">起止时间</label>
        <input type="date" class="form-control" v-model.trim="sTime">
        <input type="date" class="form-control" v-model.trim="eTime">
      </div>
      <input type="hidden" name="startTime" v-model.trim="clientInfos.startTime">
      <input type="hidden" name="endTime" v-model.trim="clientInfos.endTime">
      <input type="hidden" name="pageSize" v-model.trim="pageSize">
      <input type="hidden" name="currentPage" v-model.trim="currentPage">
      <input type="hidden" name="#TOKEN#" v-model.trim="token">
      <div class="btn-group">
        <button type="button" class="btn btn_find" @click="find">查询</button>
        <button type="submit" class="btn btn_download">报表下载</button>
      </div>
    </form>
    <ClientTable :clientInfos="clientInfos" @getDownloadInfo="getDownloadInfo"></ClientTable>
  </div>
</template>

<script>
  import store from './../../../store'
  import ClientTable from './components/ClientTable'
  import {setInitialTime, checkClientTime, checkTimeByFind} from "./../../../utils/time"
  import {downloadClientReportUrl} from './../../../config/globalUrl'

  export default {
    name: 'client',
    data() {
      return {
        clientInfos: {
          client: "",
          programNo: "",
          line: "",
          orderNo: "",
          workOrderNo: "",
          startTime: "",
          endTime: ""
        },
        sTime: "",
        eTime: "",
        currentPage: 1,
        pageSize: 20,
        lastClientInfos: {
          client: "",
          programNo: "",
          line: "",
          orderNo: "",
          workOrderNo: "",
          startTime: "",
          endTime: "",
          currentPage:1,
          pageSize:20
        },
      }
    },
    created() {
      let timeArr = setInitialTime(this.sTime, this.eTime);
      this.sTime = timeArr[0];
      this.eTime = timeArr[1];
      this.clientInfos.startTime = this.sTime + " 00:00:00";
      this.clientInfos.endTime = this.eTime + " 23:59:59";
    },
    watch: {
      sTime: function (val) {
        this.clientInfos.startTime = val + " 00:00:00";
      },
      eTime: function (val) {
        this.clientInfos.endTime = val + " 23:59:59";
      }
    },
    components: {
      ClientTable
    },
    computed: {
      lines: function () {
        return store.state.lines;
      },
      url: function () {
        return downloadClientReportUrl
      },
      token: function () {
        return store.state.token
      }
    },
    methods: {
      find: function () {
        let isOk = checkTimeByFind(this.clientInfos.startTime, this.clientInfos.endTime);
        if (isOk) {
          store.commit("setIsFind", true);
        }
      },
      download: function () {
        let isOk = checkClientTime(this.clientInfos.startTime, this.clientInfos.endTime);
        if (!isOk) {
          return;
        }
        if(JSON.stringify(this.clientInfos) !== JSON.stringify(this.lastClientInfos)){
          alert("查询条件已更改，请重新查询后再下载");
          return;
        }
        if (isOk) {
          $('#clientForm')[0].submit();
        }
      },
      getDownloadInfo: function (currentPage, pageSize, item) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.lastClientInfos.client = item.client;
        this.lastClientInfos.programNo = item.programNo;
        this.lastClientInfos.line = item.line;
        this.lastClientInfos.orderNo = item.orderNo;
        this.lastClientInfos.workOrderNo = item.workOrderNo;
        this.lastClientInfos.startTime = item.startTime;
        this.lastClientInfos.endTime = item.endTime;
        this.lastClientInfos.currentPage = item.currentPage;
        this.lastClientInfos.pageSize = item.pageSize;
      }
    }

  }
</script>

<style scoped lang="scss">
  @import '@/assets/css/common.scss';
</style>
