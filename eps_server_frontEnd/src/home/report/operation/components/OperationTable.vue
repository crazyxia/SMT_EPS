<template>
  <div class="operationTable" v-loading="loading">
    <el-table
      :data="tableData"
      border
      max-height="700"
      style="width: 100%">
      <el-table-column
        label="线号"
        width="70"
        prop="line"
        align="center">
      </el-table-column>
      <el-table-column
        prop="workOrderNo"
        label="工单号"
        align="center">
      </el-table-column>
      <el-table-column
        label="操作者"
        prop="operator"
        align="center">
      </el-table-column>
      <el-table-column
        label="操作类型"
        width="80px"
        prop="operationType"
        align="center">
      </el-table-column>
      <el-table-column
        label="成功数"
        prop="passCount"
        width="100"
        align="center">
      </el-table-column>
      <el-table-column
        label="失败数"
        prop="failCount"
        width="100"
        align="center">
      </el-table-column>
      <el-table-column
        label="总数"
        prop="total"
        width="100"
        align="center">
      </el-table-column>
      <el-table-column
        label="操作"
        align="center">
        <template slot-scope="scope">
          <el-button
            type="primary"
            size="mini"
            @click="showDetail(scope.row)">详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="block">
      <span class="now">第 {{currentPage}} 页</span>
      <el-pagination
        background
        :current-page.sync="currentPage"
        :page-size="pageSize"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
        :page-sizes="[20, 40, 80, 100]"
        layout="sizes, prev, pager, next">
      </el-pagination>
    </div>
  </div>
</template>
<script>
  import Bus from '../../../../utils/bus'
  import {mapActions, mapGetters} from 'vuex'
  import {axiosPost} from "./../../../../utils/fetchData"
  import {operationReportSummaryListUrl} from "./../../../../config/globalUrl"
  import {downloadFile} from "../../../../utils/fetchData";
  import {downloadOperationReportUrl} from "../../../../config/globalUrl";

  export default {
    name: 'operationTable',
    data() {
      return {
        tableData: [],
        currentPage: 1,
        pageSize: 20,
        loading:false
      }
    },
    computed: {
      ...mapGetters(['operationPage','operation','token','pageInfo'])
    },
    beforeDestroy() {
      //取消监听
      Bus.$off('findOperation');
      Bus.$off('downloadOperation');
    },
    mounted() {
      if(this.pageInfo.hasOwnProperty('currentPage')){
        this.currentPage = this.pageInfo.currentPage;
        this.pageSize = this.pageInfo.pageSize;
      }
      this.find();
      //监听操作报表查找事件
      Bus.$on('findOperation', () => {
        this.currentPage = 1;
        this.pageSize = 20;
        this.setOperationPage(-1);
        this.find();
      });
      //监听下载报表事件
      Bus.$on('downloadOperation',() => {
        this.download();
      })
    },
    methods: {
      ...mapActions(['setOperation', 'setDetail', 'setOperationPage','setPageInfo']),
      fetchData: function (options) {
        axiosPost(options).then(response => {
          if (response.data.list) {
            let result = response.data.list;
            let page = response.data.page;
            if(result.length>0 || result.length<=0 && this.currentPage === 1){
              this.tableData = this.handleData(result);
            }else{
              if (this.operationPage === -1) {
                this.setOperationPage(page.currentPage - 1);
              }
              this.currentPage = this.operationPage;
              this.$alertInfo('当前是最后一页');
            }
          }
          this.loading = false;
        }).catch(err => {
          this.loading = false;
          this.$alertError("网络错误，请先检查网络，再连接联系管理员");
        });
      },
      find: function () {
        this.loading = true;
        let options = {
          url: operationReportSummaryListUrl,
          data: JSON.parse(JSON.stringify(this.operation))
        };
        options.data["currentPage"] = this.currentPage;
        options.data["pageSize"] = this.pageSize;
        this.fetchData(options);
      },
      handleData: function (arr) {
        for (let i = 0; i < arr.length; i++) {
          let obj = arr[i];
          let type;
          switch (obj.operationType) {
            case "上料":
              type = 0;
              break;
            case "换料":
              type = 1;
              break;
            case "核料":
              type = 2;
              break;
            case "全检":
              type = 3;
              break;
            case "发料":
              type = 4;
              break;
            case "首检":
              type = 5;
              break;
            default:
              type = '';
              break;
          }
          obj["total"] = obj.passCount + obj.failCount;
          obj["type"] = type;
          obj["startTime"] = this.operation.startTime;
          obj["endTime"] = this.operation.endTime;
          if (obj.operationType === "") {
            obj.operationType = "不限";
          }
        }
        return arr;
      },
      showDetail: function (row) {
        this.setPageInfo({
          currentPage:this.currentPage,
          pageSize:this.pageSize
        });
        this.setDetail(row);
        this.$emit('setIsShow', false);
      },
      handleSizeChange: function (pageSize) {
        this.pageSize = pageSize;
        this.currentPage = 1;
        this.setOperationPage(-1);
        this.find();
      },
      handlePageChange:function(currentPage){
        if(this.operationPage !== -1 && currentPage > this.operationPage){
          this.currentPage = this.operationPage;
          this.$alertInfo("当前是最后一页");
          return;
        }
        this.find();
      },
      download: function () {
         let data = JSON.parse(JSON.stringify(this.operation));
         data["pageSize"] = this.pageSize;
         data["currentPage"] = this.currentPage;
         data["#TOKEN#"] = this.token;
         downloadFile(downloadOperationReportUrl,data);
      },
    },
  }
</script>

<style lang="scss" scoped>
  .operationTable {
    padding: 24px;
    border: 1px solid #ebebeb;
    border-radius: 3px;
    background: #fff;
    .block {
      display: flex;
      margin-top: 10px;
      .now {
        display: inline-block;
        height: 32px;
        line-height: 32px;
        font-size: 14px;
        color: #606266;
        margin-right: 10px;
      }
    }
  }
</style>
