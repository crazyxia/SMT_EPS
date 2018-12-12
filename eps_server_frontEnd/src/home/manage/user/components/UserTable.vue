<template>
  <div class="userTable">
    <datatable v-bind="$data"/>
  </div>
</template>
<script>
  import Vue from 'vue'
  import store from './../../../../store'
  import {axiosPost} from "./../../../../utils/fetchData"
  import {userListUrl} from "./../../../../config/globalUrl"

  export default {
    name: 'userTable',
    props: ['userInfos'],
    data: () => ({
      columns: [
        {title: '工号', field: 'id', colStyle: {'width': '80px'}},
        {title: '姓名', field: 'name', colStyle: {'width': '80px'}},
        {title: '岗位', field: 'typeName', colStyle: {'width': '100px'}},
        {title: '班别', field: 'classTypeName', colStyle: {'width': '80px'}},
        {title: '是否在职',field:'enabledString',colStyle:{'width':'70px'}},
        {title: '入职时间', field: 'createTimeString', colStyle: {'width': '120px'}},
        {title: '操作', field: 'operation', tdComp: 'UserOperation', colStyle: {'width': '100px'}}
      ],
      HeaderSettings: false,
      fixHeaderAndSetBodyMaxHeight: 700,
      data: [],
      total: 0,
      tblClass: 'table-bordered',
      query: {"limit": 20, "offset": 0},
      tblStyle: {
        'padding': '10px 0',
        'word-break': 'break-all',
        'table-layout': 'fixed',
        'color': '#666',
        'text-align': 'center'
      },
      currentPage: 1,
      pageSize: 20
    }),
    computed: {
      userList: function () {
        return store.state.userList;
      },
      isRefresh: function () {
        return store.state.isRefresh;
      },
      isFind: function () {
        return store.state.isFind;
      }
    },
    watch: {
      query: {
        handler(query) {
          this.filterData(query);
        },
        deep: true
      },
      isRefresh: function (val) {
        if (val === true) {
          store.commit("setIsRefresh", false);
          this.find();
        }
      },
      isFind: function (val) {
        if (val === true) {
          this.currentPage = 1;
          this.pageSize = 20;
          this.query.limit = 20;
          this.query.offset = 0;
          store.commit("setIsFind", false);
          this.find();
        }
      },
    },
    methods: {
      fetchData: function (options) {
        axiosPost(options).then(response => {
          store.commit("setLoading", false);
          if (response.data) {
            if(response.data.list){
              let result = response.data.list;
              let page = response.data.page;
              let list = result;
              this.total = page.totallyData;
              this.currentPage = page.currentPage;
              this.pageSize = page.pageSize;
              store.commit("setUserList", list);
              this.data = list;
              this.data.map((item, index) => {
                item.enabledString = item.enabled === true?'是':'否';
              });
            }
          }
        }).catch(err => {
          store.commit("setLoading", false);
          alert("接口请求错误，请检查网络连接，再联系管理员");
        });
      },
      find: function () {
        store.commit("setLoading", true);
        let options = {
          url: userListUrl,
          data: this.userInfos
        };
        options.data["orderBy"] = 'create_time desc';
        options.data["currentPage"] = this.currentPage;
        options.data["pageSize"] = this.pageSize;
        this.fetchData(options);
      },
      filterData: function (query) {
        this.pageSize = query.limit;
        this.currentPage = query.offset / query.limit + 1;
        this.find();
      }
    }
  }

  export const UserOperation = Vue.component('UserOperation', {
    template: `<span title="编辑" @click.stop.prevent="update(row)" style="padding-right:8px;cursor:pointer">
        <icon name="edit" scale="2.5"></icon>
      </span>`,
    props: ['row'],
    methods: {
      update(row) {
        store.commit("setUser", row);
        store.commit("setUserOperationType", "update");
        store.commit("setIsUpdate", true);
      }
    }
  });
</script>
<style lang="scss">
  .-table-body .table tr td {
    vertical-align: middle;
  }
</style>
