<template>
  <div class="programTable">
    <datatable v-bind="$data"/>
  </div>
</template>
<script>
  import Vue from 'vue'
  import store from './../../../../store'
  import {axiosPost} from "./../../../../utils/fetchData"
  import {programListUrl} from "./../../../../config/globalUrl"

  export default {
    name: 'programTable',
    props: ['programInfos'],
    data: () => ({
      columns: [
        {title: '站位表', field: 'programName', colStyle: {'width': '180px'}},
        {title: '工单', field: 'workOrder', colStyle: {'width': '150px'}},
        {title: '版面', field: 'boardTypeName', colStyle: {'width': '80px'}},
        {title: '上传时间', field: 'createTimeString', colStyle: {'width': '120px'}},
        {title: '状态', field: 'stateName', colStyle: {'width': '80px'}},
        {title: '线号', field: 'lineName', colStyle: {'width': '50px'}},
        {title: '操作', field: 'operation', tdComp: 'ProgramOperation', colStyle: {'width': '150px'}}
      ],
      HeaderSettings: false,
      fixHeaderAndSetBodyMaxHeight: 650,
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
      programList: function () {
        return store.state.programList;
      },
      isUploadFinish: function () {
        return store.state.isUploadFinish;
      },
      isFind: function () {
        return store.state.isFind;
      },
      isRefresh: function () {
        return store.state.isRefresh;
      }
    },
    watch: {
      query: {
        handler(query) {
          this.filterData(query);
        },
        deep: true
      },
      isUploadFinish: function (val) {
        if (val === true) {
          store.commit("setIsUploadFinish", false);
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
      isRefresh: function (val) {
        if (val === true) {
          store.commit("setIsRefresh", false);
          this.find();
        }
      }
    },
    methods: {
      fetchData: function (options) {
        axiosPost(options).then(response => {
          store.commit("setLoading", false);
          if (response.data) {
            if (response.data.list) {
              let result = response.data.list;
              let page = response.data.page;
              this.total = page.totallyData;
              this.currentPage = page.currentPage;
              this.pageSize = page.pageSize;
              store.commit("setProgramList", result);
              this.data = result;
            }
          }
        }).catch(err => {
          store.commit("setLoading", false);
          alert("接口请求失败，请检查网络，再联系管理员");
        });
      },
      find: function () {
        store.commit("setLoading", true);
        let options = {
          url: programListUrl,
          data: this.programInfos
        }
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

  export const ProgramOperation = Vue.component('ProgramOperation', {
      template: `<div>
      <button type="button" class="btn" :style="blueStyle"
        @click.stop.prevent="updateStatus(row)" :disabled="isDisabled" >修改状态</button>
      <button type="button" class="btn " :style="greenStyle"
        @click.stop.prevent="updateTable(row)" :disabled="isDisabled" >修改表格</button>
  </div>`,
      props: ['row'],
      computed: {
        blueStyle: function () {
          let state = this.row.state;
          let obj = {
            fontSize: "14px",
            color: "#fff",
            marginRight: "5px"
          };
          if (state === 2 || state === 3) {
            obj["background"] = "#ccc";
          } else {
            obj["background"] = "#00acec";
          }
          return obj;
        },
        greenStyle: function () {
          let state = this.row.state;
          let obj = {
            fontSize: "14px",
            color: "#fff",
            marginRight: "5px"
          };
          if (state === 2 || state === 3) {
            obj["background"] = "#ccc";
          } else {
            obj["background"] = "#49bf67";
          }
          return obj;
        },
        isDisabled: function () {
          let state = this.row.state;
          if (state === 2 || state === 3) {
            return true
          }
          return false;
        }
      },
      methods:
        {
          updateStatus(row) {
            store.commit("setOldState", "");
            store.commit("setIsUpdate", true);
            store.commit("setProgram", row);
            store.commit("setOldState", row.state);
            store.commit("setProgramOperationType", "update");
          }
          ,
          updateTable(row) {
            store.commit("setProgram", row);
            store.commit("setProgramItemShow", true);
          }
        }
    })
  ;
</script>
<style lang="scss">
  .-table-body .table tr td {
    vertical-align: middle;
  }
</style>
