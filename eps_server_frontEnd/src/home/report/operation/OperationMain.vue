<template>
  <div class="operation">
    <div v-if="isShow">
      <form class="form-inline" role="form" :action="url" method="post" @submit.prevent="download" id="operationForm">
        <div class="form-group">
          <label for="type">操作类型</label>
          <select class="form-control" id="operationType" v-model.trim="operationInfos.type" name="type">
            <option value="">不限</option>
            <option value="0">上料</option>
            <option value="1">换料</option>
            <option value="2">核料</option>
            <option value="3">全检</option>
            <option value="4">发料</option>
          </select>
        </div>
        <div class="form-group">
          <label for="line">线号</label>
          <select class="form-control" id="line" v-model.trim="operationInfos.line" name="line">
            <option selected="selected" value=''>不限</option>
            <option v-for="item in lines" :value="item.id">{{item.line}}</option>
          </select>
        </div>
        <div class="form-group">
          <label for="workOrderNo">工单号</label>
          <input type="text" class="form-control" id="workOrderNo" v-model.trim="operationInfos.workOrderNo"
                 name="workOrderNo">
        </div>
        <div class="form-group">
          <label for="time">起止时间</label>
          <input type="date" class="form-control" v-model.trim="sTime">
          <input type="date" class="form-control" v-model.trim="eTime">
        </div>
        <input type="hidden" name="startTime" v-model.trim="operationInfos.startTime">
        <input type="hidden" name="endTime" v-model.trim="operationInfos.endTime">
        <input type="hidden" name="currentPage" v-model.trim="currentPage">
        <input type="hidden" name="pageSize" v-model.trim="pageSize">
        <input type="hidden" name="#TOKEN#" v-model.trim="token">
        <div class="btn-group">
          <button type="button" class="btn btn_find" @click="find">查询</button>
          <button type="submit" class="btn btn_download">报表下载</button>
        </div>
      </form>
      <operationTable :operationInfos="operationInfos" @getDownloadInfo="getDownloadInfo"></operationTable>
    </div>
    <OperationDetail v-else ></OperationDetail>
  </div>
</template>

<script>
  import store from './../../../store'
  import OperationTable from './components/OperationTable'
  import OperationDetail from './operationDetail/OperationDetail'
  import {setInitialTime, checkOperationTime, checkTimeByFind} from "./../../../utils/time"
  import {downloadOperationReportUrl} from './../../../config/globalUrl'

  export default {
    name: 'operation',
    data() {
      return {
        operationInfos: {
          type: "",
          line: "",
          workOrderNo: "",
          startTime: "",
          endTime: "",
        },
        sTime: "",
        eTime: "",
        currentPage: 1,
        pageSize: 20,
        isShow: true,
        lastOperationInfos: {
          type: "",
          line: "",
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
      this.operationInfos.startTime = this.sTime + " 00:00:00";
      this.operationInfos.endTime = this.eTime + " 23:59:59";
    },
    watch: {
      sTime: function (val) {
        this.operationInfos.startTime = val + " 00:00:00";
      },
      eTime: function (val) {
        this.operationInfos.endTime = val + " 23:59:59";
      },
      operationDetailShow: function (val) {
        if (val === true) {
          this.isShow = false;
        } else {
          this.isShow = true;
        }
      }
    },
    computed: {
      lines: function () {
        return store.state.lines;
      },
      url: function () {
        return downloadOperationReportUrl
      },
      token: function () {
        return store.state.token
      },
      operationDetailShow: function () {
        return store.state.operationDetailShow;
      }
    },
    components: {
      OperationTable, OperationDetail
    },
    methods: {
      find: function () {
        let isOk = checkTimeByFind(this.operationInfos.startTime, this.operationInfos.endTime);
        if (isOk) {
          store.commit("setIsFind", true);
        }
      },
      download: function () {
        let isOk = checkOperationTime(this.operationInfos.startTime, this.operationInfos.endTime);
        if (!isOk) {
          return;
        }
        if (JSON.stringify(this.operationInfos) !== JSON.stringify(this.lastOperationInfos) ){
          alert("查询条件已更改，请重新查询后再下载");
          return;
        }
        if (isOk) {
          $('#operationForm')[0].submit();
        }
      },
      getDownloadInfo: function (currentPage, pageSize,item) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.lastOperationInfos.line = item.line;
        this.lastOperationInfos.endTime = item.endTime;
        this.lastOperationInfos.startTime = item.startTime;
        this.lastOperationInfos.type = item.type;
        this.lastOperationInfos.workOrderNo = item.workOrderNo;
        this.lastOperationInfos.currentPage = item.currentPage;
        this.lastOperationInfos.pageSize = item.pageSize;
      }
    }
  }
</script>

<style scoped lang="scss">
  @import '@/assets/css/common.scss';
</style>
