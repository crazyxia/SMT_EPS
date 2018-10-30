<template>
  <div class="programItemTable">
    <datatable v-bind="$data"/>
  </div>
</template>
<script>
  import Vue from 'vue'
  import store from './../../../../../store'
  import {axiosPost} from "./../../../../../utils/fetchData"
  import {programItemListUrl} from "./../../../../../config/globalUrl"

  export default {
    name: 'programItemTable',
    data: () => ({
      columns: [
        {title: '站位', field: 'lineseat', colStyle: {'width': '60px'}},
        {title: '程序料号', field: 'materialNo', colStyle: {'width': '120px'}},
        {title: '数量', field: 'quantity', colStyle: {'width': '50px'}},
        {title: 'BOM料号/规格', field: 'specitification', colStyle: {'width': '250px'}},
        {title: '单板位置', field: 'position', colStyle: {'width': '120px'}},
        {title: '料别', field: 'materialType', colStyle: {'width': '50px'}},
        {title: '操作', field: 'operation', tdComp: 'ProgramItemOperation', colStyle: {'width': '100px'}}
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
    }),
    mounted() {
      this.getList();
    },
    computed: {
      programItemRefresh: function () {
        return store.state.programItemRefresh;
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
      programItemRefresh: function (val) {
        if (val === true) {
          store.commit("setProgramItemRefresh", false);
          this.total = store.state.programItemList.length;
          let index = store.state.operationIndex + 1;
          if (index === this.total) {
            let a = (index - this.query.limit) % this.query.limit;
            let b = parseInt((index - this.query.limit) / this.query.limit);
            if (a !== 0) {
              this.query.offset = b * this.query.limit + this.query.limit;
            } else {
              this.query.offset = b * this.query.limit;
            }
          }
          this.filterData(this.query);
        }
      },
      isRefresh: function (val) {
        if (val === true) {
          store.commit("setIsRefresh", false);
          this.getList();
        }
      }
    },
    methods: {
      fetchData: function (options) {
        axiosPost(options).then(response => {
          store.commit("setLoading", false);
          if (response.data) {
            if (response.data) {
              let result = response.data;
              let list = this.handleArr(result);
              this.total = list.length;
              store.commit("setProgramItemList", list);
              this.filterData(this.query);
            }
          }
        }).catch(err => {
          store.commit("setLoading", false);
          alert("接口请求失败，请检查网络，再联系管理员");
        });
      },
      getList: function () {
        store.commit("setLoading", true);
        let options = {
          url: programItemListUrl,
          data: {
            id: store.state.program.id,
            orderBy: 'create_time desc'
          }
        }
        this.fetchData(options);
      },
      filterData: function (query) {
        let list = store.state.programItemList;
        let dataShow = list.slice(query.offset, query.offset + query.limit);
        this.data = dataShow;
      },
      handleArr: function (arr) {
        let result = [];
        for (let i = 0; i < arr.length; i++) {
          if (arr[i].alternative === true) {
            arr[i]["materialType"] = "替料";
          } else {
            arr[i]["materialType"] = "主料";
          }
          result.push(arr[i]);
        }
        return result;
      },
    }
  }

  export const ProgramItemOperation = Vue.component('ProgramItemOperation', {
    template: `<span>
      <span title="添加" @click.stop.prevent="add(row)" style="padding-right:8px;cursor:pointer">
        <icon name="add" scale="2.5"></icon>
      </span>
      <span title="编辑" @click.stop.prevent="update(row)" style="padding-right:8px;cursor:pointer">
        <icon name="edit" scale="2.5"></icon>
      </span>
      <span title="删除" @click.stop.prevent="deleteRow(row)" style="cursor:pointer">
        <icon name="delete" scale="2.5"></icon>
      </span>
    </span>`,
    props: ['row'],
    computed: {
      programItemList: function () {
        return store.state.programItemList;
      }
    },
    methods: {
      add(row) {
        let index = this.findIndex(row);
        store.commit("setOperationIndex", index);
        store.commit("setProgramItem", row);
        store.commit("setProgramItemOperationType", "add");
        store.commit("setIsAdd", true);
      },
      update(row) {
        let index = this.findIndex(row);
        store.commit("setOperationIndex", index);
        store.commit("setProgramItem", row);
        store.commit("setProgramItemOperationType", "update");
        store.commit("setIsUpdate", true);
      },
      deleteRow(row) {
        let index = this.findIndex(row);
        store.commit("setOperationIndex", index);
        store.commit("setProgramItem", row);
        store.commit("setProgramItemOperationType", "delete");
        store.commit("setIsDelete", true);
      },
      findIndex(row) {
        let programItemList = store.state.programItemList;
        let len = programItemList.length;
        let index = programItemList.length - 1;
        for (let i = 0; i < len; i++) {
          if (programItemList[i] === row) {
            index = i;
          }
        }
        return index;
      }
    }
  });
</script>
<style lang="scss">
  .-table-body .table tr td {
    vertical-align: middle;
  }
</style>
