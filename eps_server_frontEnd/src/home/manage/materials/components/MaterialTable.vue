<template>
  <div class="MaterialTable">
    <datatable v-bind="$data"/>
  </div>
</template>
<script>
  import Vue from 'vue'
  import store from './../../../../store'
  import {axiosPost} from "./../../../../utils/fetchData"
  import {errTip} from "./../../../../utils/errorTip"
  import {materialListUrl, deleteMaterialUrl} from "./../../../../config/globalUrl"
  import {judge} from "../../../../utils/formValidate";

  export default {
    name: 'materialTable',
    props: ['materialInfos'],
    data: () => ({
      columns: [
        {title: '料号', field: 'materialNo', colStyle: {'width': '100px'}},
        {title: '保质期(天)', field: 'perifdOfValidity', colStyle: {'width': '80px'}},
        {title: '操作', field: 'operation', tdComp: 'materialOperation', colStyle: {'width': '100px'}}
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
      materialList: function () {
        return store.state.materialList;
      },
      material: function () {
        return store.state.material;
      },
      isRefresh: function () {
        return store.state.isRefresh;
      },
      isFind: function () {
        return store.state.isFind;
      },
      isDelete: function () {
        return store.state.isDelete;
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
          this.resetMaterialInfos();
          this.find();
          store.commit("setIsRefresh", false);
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
      isDelete: function (val) {
        if (val === true) {
          store.commit("setIsDelete", false);
          this.deleteRow();
        }
      }
    },
    methods: {
      fetchData: function (options) {
        axiosPost(options).then(response => {
          store.commit("setLoading", false);
          if (response.data) {
            if(response.data.list){
              let result = response.data.list;
              let page = response.data.page;
              this.total = page.totallyData;
              this.currentPage = page.currentPage;
              this.pageSize = page.pageSize;
              store.commit("setMaterialList", result);
              this.data = result;
            }
          }
        }).catch(err => {
          store.commit("setLoading", false);
          alert("请求接口失败，请先检查网络，再联系管理员");
        });
      },
      find: function () {
        store.commit("setLoading", true);
        let options = {
          url: materialListUrl,
          data: this.materialInfos
        };
        options.data["currentPage"] = this.currentPage;
        options.data["pageSize"] = this.pageSize;
        this.fetchData(options);
      },
      deleteRow: function () {
        let options = {
          url: deleteMaterialUrl,
          data: {
            id: this.material.id
          }
        };
        axiosPost(options).then(response => {
          if (response.data) {
            let result = response.data.result;
            if (result === "succeed") {
              alert("删除成功");
              this.find();
            } else {
              errTip(result);
            }
          }
        }).catch(err => {
          alert("请求接口失败，请先检查网络，再联系管理员");
        });
      },
      filterData: function (query) {
        this.pageSize = query.limit;
        this.currentPage = query.offset / query.limit + 1;
        this.find();
      },
      resetMaterialInfos: function () {
        this.materialInfos.materialNo = "";
        this.materialInfos.perifdOfValidity = "";
      }
    }
  }

  export const materialOperation = Vue.component('materialOperation', {
    template: `<span>
      <span title="编辑" @click.stop.prevent="update(row)" style="padding-right:8px;cursor:pointer">
        <icon name="edit" scale="2.5"></icon>
      </span>
      <span title="删除" @click.stop.prevent="deleteRow(row)" style="cursor:pointer">
        <icon name="delete" scale="2.5"></icon>
      </span>
    </span>`,
    props: ['row'],
    methods: {
      update(row) {
        store.commit("setMaterial", row);
        store.commit("setMaterialOperationType", "update");
        store.commit("setIsUpdate", true);
      },
      deleteRow(row) {
        store.commit("setMaterial", row);
        store.commit("setMaterialOperationType", "delete");
        store.commit("setIsDelete", true);
      }
    }
  });
</script>
<style lang="scss">
  .-table-body .table tr td {
    vertical-align: middle;
  }
</style>
