<template>
  <div class="operationDetailTable">
    <el-table
      :data="tableData"
      border
      style="width: 100%">
      <el-table-column
        prop="lineseat"
        label="站位"
        width="100"
        align="center">
      </el-table-column>
      <el-table-column
        prop="materialNo"
        label="物料编号"
        align="center">
      </el-table-column>
      <el-table-column
        label="物料描述"
        prop="materialDescription"
        align="center">
      </el-table-column>
      <el-table-column
        prop="materialSpecitification"
        label="物料规格"
        align="center">
      </el-table-column>
      <el-table-column
        label="操作结果"
        width="100"
        prop="result"
        align="center">
      </el-table-column>
      <el-table-column
        label="操作时间"
        prop="time"
        width="200"
        align="center">
      </el-table-column>
    </el-table>
    <div class="block">
      <span class="now">第 {{currentPage}} 页</span>
      <el-pagination
        :current-page.sync="currentPage"
        :page-size="pageSize"
        @size-change="handleSizeChange"
        @current-change="find"
        :page-sizes="[20, 40, 80, 100]"
        layout="sizes, prev, pager, next">
      </el-pagination>
    </div>
  </div>
</template>
<script>
  import {mapGetters, mapActions} from 'vuex'
  import {axiosPost} from "./../../../../../utils/fetchData"
  import {operationReportListUrl} from "./../../../../../config/globalUrl"

  export default {
    name: 'operationDetailTable',
    props: ['item'],
    data() {
      return {
        tableData: [],
        currentPage: 1,
        pageSize: 20
      }
    },
    mounted() {
      this.find();
    },
    computed: {
      ...mapGetters(['lines', 'operationDetailPage']),
      operationInfo: function () {
        let obj = {
          type: this.item.type,
          operator: this.item.operator,
          workOrderNo: this.item.workOrderNo,
          startTime: this.item.startTime,
          endTime: this.item.endTime,
          currentPage: this.currentPage,
          pageSize: this.pageSize
        };
        for (let i = 0; i < this.lines.length; i++) {
          if (this.item.line === this.lines[i].line) {
            obj["line"] = this.lines[i].id;
            break;
          }
        }
        return obj;
      },
    },
    methods: {
      ...mapActions(['setLoading', 'setOperationDetailPage']),
      fetchData: function (options) {
        axiosPost(options).then(response => {
          this.setLoading(false);
          if (response.data.list) {
            let result = response.data.list;
            let page = response.data.page;
            if(result.length>0 || result.length<=0 && this.currentPage === 1){
              this.tableData = result;
            }else{
              if(this.operationDetailPage === -1){
                this.setOperationDetailPage(page.currentPage - 1);
              }
              this.currentPage = this.operationDetailPage;
              this.$alertInfo('当前是最后一页');
            }
          }
        }).catch(err => {
          this.setLoading(false);
          this.$alertError("网络错误，请先检查网络，再连接联系管理员");
        });
      },
      find: function () {
        this.setLoading(true);
        let options = {
          url: operationReportListUrl,
          data: this.operationInfo
        };
        options.data["currentPage"] = this.currentPage;
        options.data["pageSize"] = this.pageSize;
        this.fetchData(options);
      },
      handleSizeChange: function (pageSize) {
        this.pageSize = pageSize;
        this.currentPage = 1;
        this.setOperationDetailPage(-1);
        this.find();
      }
    }
  }
</script>
<style lang="scss" scoped>
  .operationDetailTable {
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
