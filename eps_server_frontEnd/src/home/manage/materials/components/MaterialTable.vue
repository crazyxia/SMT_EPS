<template>
  <div class="materialTable" v-loading="loading">
    <el-table
      :data="tableData"
      max-height="700"
      border
      style="width: 100%">
      <el-table-column
        label="料号"
        prop="materialNo"
        align="center">
      </el-table-column>
      <el-table-column
        prop="perifdOfValidity"
        label="保质期（天）"
        align="center">
      </el-table-column>
      <el-table-column
        label="操作"
        align="center">
        <template slot-scope="scope">
          <el-button
            type="primary"
            size="mini"
            @click="edit(scope.row)"
            icon="el-icon-edit">编辑
          </el-button>
          <el-button
            type="danger"
            size="mini"
            @click="deleteRow(scope.row)"
            icon="el-icon-delete">删除
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
  import {mapActions,mapGetters} from 'vuex'
  import {axiosPost} from "./../../../../utils/fetchData"
  import {errTip} from "./../../../../utils/errorTip"
  import {materialListUrl, deleteMaterialUrl} from "./../../../../config/globalUrl"

  export default {
    name: 'materialTable',
    data() {
      return {
        tableData: [],
        pageSize: 20,
        currentPage: 1,
        loading:false
      }
    },
    computed:{
      ...mapGetters(['materialPage','material'])
    },
    beforeDestroy() {
      //取消监听
      Bus.$off('refreshMaterial');
      Bus.$off('findMaterial');
    },
    mounted() {
      this.find();
      //监听刷新事件
      Bus.$on('refreshMaterial', () => {
        this.setMaterialPage(-1);
        this.find();
      });
      //监听查询事件
      Bus.$on('findMaterial', () => {
        this.currentPage = 1;
        this.pageSize = 20;
        this.setMaterialPage(-1);
        this.find();
      });
    },
    methods: {
      ...mapActions(['setMaterialPage']),
      fetchData: function (options) {
        axiosPost(options).then(response => {
          if (response.data.list) {
            let result = response.data.list;
            let page = response.data.page;
            if(result.length>0 || result.length<=0 && this.currentPage === 1){
              this.tableData = result;
            }else{
              if(this.materialPage === -1){
                this.setMaterialPage(page.currentPage -1);
              }
              this.currentPage = this.materialPage;
              this.$alertInfo('当前是最后一页');
            }
          }
          this.loading = false;
        }).catch(err => {
          this.loading = false;
          this.$alertError("请求接口失败，请先检查网络，再联系管理员");
        });
      },
      find: function () {
        this.loading = true;
        let options = {
          url: materialListUrl,
          data:JSON.parse(JSON.stringify(this.material))
        };
        options.data["currentPage"] = this.currentPage;
        options.data["pageSize"] = this.pageSize;
        this.fetchData(options);
      },
      handleSizeChange: function (pageSize) {
        this.pageSize = pageSize;
        this.currentPage = 1;
        this.setMaterialPage(-1);
        this.find();
      },
      handlePageChange:function(currentPage){
        if(this.materialPage !== -1 && currentPage > this.materialPage){
          this.currentPage = this.materialPage;
          this.$alertInfo("当前是最后一页");
          return;
        }
        this.find();
      },
      deleteRow: function (row) {
        let options = {
          url: deleteMaterialUrl,
          data: {
            id: row.id
          }
        };
        axiosPost(options).then(response => {
          if (response.data) {
            let result = response.data.result;
            if (result === "succeed") {
              this.$alertSuccess("删除成功");
              this.find();
            } else {
              this.$alertWarning(errTip(result));
            }
          }
        }).catch(err => {
          this.$alertError("请求接口失败，请先检查网络，再联系管理员");
        });
      },
      edit: function (row) {
        Bus.$emit('editMaterial', row);
      }
    }
  }
</script>
<style lang="scss" scoped>
  .materialTable {
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
