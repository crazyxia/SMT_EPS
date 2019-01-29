<template>
  <div class="clientTable">
    <el-table
      :data="tableData"
      border
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
        label="槽位"
        prop="lineseat"
        width="80"
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
        label="操作类型"
        width="80px"
        prop="operationType"
        align="center">
      </el-table-column>
      <el-table-column
        label="操作者"
        prop="operator"
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
        background
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
  import Bus from '../../../../utils/bus'
  import {mapActions,mapGetters} from 'vuex'
  import {axiosPost} from "./../../../../utils/fetchData"
  import {clientReportListUrl} from "./../../../../config/globalUrl"

  export default {
    name: 'clientTable',
    data() {
      return {
        tableData: [],
        currentPage: 1,
        pageSize: 20,
      }
    },
    computed:{
      ...mapGetters(['clientPage','client'])
    },
    beforeDestroy(){
      //取消监听
      Bus.$off('findClient');
    },
    mounted(){
      this.find();
      //监听查找客户报表事件
      Bus.$on('findClient',() => {
        this.currentPage = 1;
        this.pageSize = 20;
        this.setClientPage(-1);
        this.find();
      })
    },
    methods: {
      ...mapActions(['setLoading','setClient','setClientPage']),
      fetchData: function (options) {
        axiosPost(options).then(response => {
          this.setLoading(false);
          if (response.data.list) {
            let result = response.data.list;
            let page = response.data.page;
            if(result.length>0 || result.length<=0 && this.currentPage === 1){
              this.tableData = result;
            }else{
              if(this.clientPage === -1){
                this.setClientPage(page.currentPage - 1)
              }
              this.currentPage = this.clientPage;
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
          url: clientReportListUrl,
          data: this.client
        };
        options.data["currentPage"] = this.currentPage;
        options.data["pageSize"] = this.pageSize;
        this.fetchData(options);
      },
      handleSizeChange: function (pageSize) {
        this.pageSize = pageSize;
        this.currentPage = 1;
        this.setClientPage(-1);
        this.find();
      }
    }
  }

</script>
<style lang="scss" scoped>
  .clientTable {
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
