<template>
  <div class="userTable">
    <!--表格-->
    <el-table
      :data="tableData"
      border
      style="width: 100%">
      <el-table-column
        label="工号"
        prop="id"
        align="center">
      </el-table-column>
      <el-table-column
        prop="name"
        label="姓名"
        align="center">
      </el-table-column>
      <el-table-column
        label="岗位"
        prop="typeName"
        align="center">
      </el-table-column>
      <el-table-column
        label="班别"
        prop="classTypeName"
        align="center">
      </el-table-column>
      <el-table-column
        label="是否在职"
        prop="enabledString"
        align="center">
      </el-table-column>
      <el-table-column
        width="200"
        label="入职时间"
        prop="createTimeString"
        align="center">
      </el-table-column>
      <el-table-column
        label="操作"
        align="center">
        <template slot-scope="scope">
          <el-button
            type="primary"
            size="mini"
            @click="edit(scope.row)" icon="el-icon-edit">编辑
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!--分页-->
    <div class="block">
      <span class="now">第 {{page.currentPage}} 页</span>
      <el-pagination
        background
        :current-page.sync="page.currentPage"
        :page-size="page.pageSize"
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
  import {mapActions, mapGetters} from 'vuex';
  import {axiosPost} from "./../../../../utils/fetchData"
  import {userListUrl} from "./../../../../config/globalUrl"

  export default {
    name: 'userTable',
    data() {
      return {
        //表格信息
        tableData: [],
        //分页信息
        page: {
          currentPage: 1,
          pageSize: 20
        }
      }
    },
    beforeDestroy() {
      //取消监听
      Bus.$off('refreshUser');
      Bus.$off('findUser');
    },
    computed: {
      ...mapGetters(['userPage', 'user'])
    },
    mounted() {
      this.find();
      //监听刷新事件
      Bus.$on('refreshUser', () => {
        //设置最后一页
        this.setUserPage(-1);
        this.find();
      });
      //监听查询事件
      Bus.$on('findUser', () => {
        this.page.currentPage = 1;
        this.page.pageSize = 20;
        this.setUserPage(-1);
        this.find();
      });
    },
    methods: {
      ...mapActions(['setLoading', 'setUserPage']),
      fetchData: function (options) {
        axiosPost(options).then(response => {
          this.setLoading(false);
          if (response.data.data) {
            let result = response.data.result;
            let list = response.data.data.list;
            let page = response.data.data.page;
            //有权限
            if (result === "401") {
              //list有值 || 当前为第1页，list没有值
              if (list.length > 0 || list.length <= 0 && this.page.currentPage === 1) {
                this.tableData = list;
                this.tableData.map((item) => {
                  item.enabledString = item.enabled === true ? '是' : '否';
                });
              }
              //最后一页
              else {
                if (this.userPage === -1) {
                  this.setUserPage(page.currentPage - 1);
                }
                this.page.currentPage = this.userPage;
                this.$alertInfo('当前是最后一页');
              }
            }
            //权限不足
            if (result === "400") {
              this.tableData = list;
              this.tableData.map((item) => {
                item.enabledString = item.enabled === true ? '是' : '否';
              });
              this.$alertWarning('你没有权限查询该类型的人员，请重新填写查询条件');
              this.page.currentPage = 1;
            }
          }
        }).catch(err => {
          this.setLoading(false);
          this.$alertError("接口请求错误，请检查网络连接，再联系管理员");
        });
      },
      //查询
      find: function () {
        this.setLoading(true);
        let options = {
          url: userListUrl,
          data: this.user
        };
        options.data["orderBy"] = 'create_time desc';
        options.data["currentPage"] = this.page.currentPage;
        options.data["pageSize"] = this.page.pageSize;
        this.fetchData(options);
      },
      //改变pageSize
      handleSizeChange: function (pageSize) {
        this.page.currentPage = 1;
        this.page.pageSize = pageSize;
        this.setUserPage(-1);
        this.find();
      },
      //编辑
      edit: function (row) {
        Bus.$emit('editUser', row);
      },
    }
  }

</script>
<style lang="scss" scoped>
  .userTable {
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
