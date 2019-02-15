<template>
  <div class="ioTable" v-loading="loading">
    <el-table
      :data="tableData"
      max-height="700"
      border
      style="width: 100%">
      <el-table-column
        label="时间戳"
        width="150"
        prop="timestamp"
        align="center">
      </el-table-column>
      <el-table-column
        prop="materialNo"
        label="料号"
        align="center">
      </el-table-column>
      <el-table-column
        label="数量"
        prop="quantity"
        align="center">
      </el-table-column>
      <el-table-column
        prop="operator"
        label="操作者"
        align="center">
      </el-table-column>
      <el-table-column
        label="操作时间"
        width="200"
        prop="operationTimeString"
        align="center">
      </el-table-column>
      <el-table-column
        prop="position"
        label="仓位"
        align="center">
      </el-table-column>
      <el-table-column
        label="供应商"
        prop="custom"
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
  import {stockLogsListUrl} from "./../../../../config/globalUrl"

  export default {
    name: 'ioTable',
    data() {
      return {
        tableData: [],
        currentPage: 1,
        pageSize: 20,
        loading:false
      }
    },
    computed: {
      ...mapGetters(['ioPage','io'])
    },
    beforeDestroy() {
      //取消监听
      Bus.$off('findIo');
    },
    mounted() {
      this.find();
      //监听查找仓库
      Bus.$on('findIo', () => {
        this.currentPage = 1;
        this.pageSize = 20;
        this.setIOPage(-1);
        this.find();
      });
    },
    methods: {
      ...mapActions(['setIOPage']),
      fetchData: function (options) {
        axiosPost(options).then(response => {
          if (response.data.list) {
            let result = response.data.list;
            let page = response.data.page;
            if(result.length>0 || result.length<=0 && this.currentPage === 1){
              this.tableData = result;
            }else{
              if(this.ioPage === -1){
                this.setIOPage(page.currentPage - 1);
              }
              this.currentPage = this.ioPage;
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
          url: stockLogsListUrl,
          data: JSON.parse(JSON.stringify(this.io))
        };
        options.data["currentPage"] = this.currentPage;
        options.data["pageSize"] = this.pageSize;
        this.fetchData(options);
      },
      handleSizeChange: function (pageSize) {
        this.pageSize = pageSize;
        this.currentPage = 1;
        this.setIOPage(-1);
        this.find();
      },
      handlePageChange:function(currentPage){
        if(this.ioPage !== -1 && currentPage > this.ioPage){
          this.currentPage = this.ioPage;
          this.$alertInfo("当前是最后一页");
          return;
        }
        this.find();
      }
    }
  }

</script>
<style lang="scss" scoped>
  .ioTable {
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
