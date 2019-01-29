<template>
  <div class="programTable">
    <!--表格-->
    <el-table
      :data="tableData"
      border
      style="width: 100%">
      <el-table-column
        label="站位表"
        prop="programName"
        align="center">
      </el-table-column>
      <el-table-column
        prop="workOrder"
        label="工单"
        align="center">
      </el-table-column>
      <el-table-column
        label="版面"
        prop="boardTypeName"
        width="80"
        align="center">
      </el-table-column>
      <el-table-column
        prop="createTimeString"
        width="200"
        label="上传时间"
        align="center">
      </el-table-column>
      <el-table-column
        label="状态"
        width="80"
        prop="stateName"
        align="center">
      </el-table-column>
      <el-table-column
        prop="lineName"
        label="线号"
        width="50"
        align="center">
      </el-table-column>
      <el-table-column
        label="操作"
        align="center">
        <template slot-scope="scope">
          <el-button
            type="primary"
            size="mini"
            @click="editState(scope.row)"
            style="margin-bottom:5px;"
            :disabled="scope.row.state === 2 || scope.row.state === 3">修改状态
          </el-button>
          <el-button
            type="success"
            size="mini"
            @click="editTable(scope.row)"
            :disabled="scope.row.state === 2 || scope.row.state === 3">修改表格
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!--分页-->
    <div class="block">
      <span class="now">第 {{page.currentPage}} 页</span>
      <el-pagination
        :current-page.sync="page.currentPage"
        :page-size="page.pageSize"
        @size-change="handleSizeChange"
        @current-change="find"
        :page-sizes="[20, 40, 80, 100]"
        background
        layout="sizes, prev, pager, next">
      </el-pagination>
    </div>
  </div>
</template>
<script>
  import Bus from '../../../../utils/bus'
  import {mapActions, mapGetters} from 'vuex'
  import {axiosPost} from "./../../../../utils/fetchData"
  import {programListUrl} from "./../../../../config/globalUrl"
  import {setLoading} from "../../../../store/actions";

  export default {
    name: 'programTable',
    data() {
      return {
        //表格信息
        tableData: [],
        //分页信息
        page:{
          currentPage: 1,
          pageSize: 20
        }
      }
    },
    computed: {
      ...mapGetters(['programPage','program']),
    },
    beforeDestroy() {
      //取消监听
      Bus.$off('findProgram');
      Bus.$off('refreshProgram');
    },
    mounted() {
      this.find();
      //监听站位表列表查找事件
      Bus.$on('findProgram', () => {
        this.page.currentPage = 1;
        this.page.pageSize = 20;
        this.setProgramPage(-1);
        this.find();
      });
      //监听站位表列表刷新事件
      Bus.$on('refreshProgram', () => {
        this.setProgramPage(-1);
        this.find();
      })
    },
    methods: {
      ...mapActions(['setLoading', 'setProgramInfo', 'setProgramPage']),
      fetchData: function (options) {
        axiosPost(options).then(response => {
          this.setLoading(false);
          if (response.data.list) {
            let list = response.data.list;
            let page = response.data.page;
            //list有值 || 当前为第1页，list没有值
            if(list.length > 0 || list.length <= 0 && this.page.currentPage === 1){
              this.tableData = list;
            }
            //最后一页
            else{
              if(this.programPage === -1){
                this.setProgramPage( page.currentPage - 1);
              }
              this.$alertInfo('当前是最后一页');
              this.page.currentPage = this.programPage;
            }
          }
        }).catch(err => {
          this.setLoading(false);
          this.$alertError("接口请求失败，请检查网络，再联系管理员");
        });
      },
      //查询
      find: function () {
        this.setLoading(true);
        let options = {
          url: programListUrl,
          data: this.program
        };
        options.data["orderBy"] = 'create_time desc';
        options.data["currentPage"] = this.page.currentPage;
        options.data["pageSize"] = this.page.pageSize;
        this.fetchData(options);
      },
      //pageSize改变
      handleSizeChange: function (pageSize) {
        this.page.pageSize = pageSize;
        this.page.currentPage = 1;
        this.setProgramPage(-1);
        this.find();
      },
      //修改状态
      editState: function (row) {
        Bus.$emit('editState', row);
      },
      //修改表格
      editTable: function (row) {
        //存放行信息
        this.setProgramInfo(row);
        //设置显示站位表详情
        this.$emit('setIsShow', false);
      }
    }
  }
</script>
<style lang="scss" scoped>
  .programTable {
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
