<template>
  <div class="operationTable">
    <datatable v-bind="$data"/>
  </div>
</template>
<script>
  import Vue from 'vue'
  import store from './../../../../store'
  import {axiosPost} from "./../../../../utils/fetchData"
  import {operationReportSummaryListUrl} from "./../../../../config/globalUrl"

  export default {
    name: 'operationTable',
    props: ['operationInfos'],
    data: () => ({
      columns: [
        {title: '线号', field: 'line', colStyle: {'width': '80px'}},
        {title: '工单号', field: 'workOrderNo', colStyle: {'width': '200px'}},
        {title: '操作者', field: 'operator', colStyle: {'width': '100px'}},
        {title: '操作类型', field: 'operationType', colStyle: {'width': '100px'}},
        {title: '成功数', field: 'passCount', colStyle: {'width': '100px'}},
        {title: '失败数', field: 'failCount', colStyle: {'width': '100px'}},
        {title: '总数', field: 'total', colStyle: {'width': '100px'}},
        {title: '操作', field: 'operation', tdComp: 'operationOperation', colStyle: {'width': '80px'}}
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
      oprationSummaryList: function () {
        return store.state.operationSummaryList;
      },
      isFind: function () {
        return store.state.isFind;
      },
      isDetail: function () {
        return store.state.isDetail;
      }
    },
    watch: {
      query: {
        handler(query) {
          this.filterData(query);
        },
        deep: true
      },
      isFind: function (val) {
        if (val === true) {
          this.currentPage = 1;
          this.pageSize = 20;
          this.query.limit = 20;
          this.query.offset = 0;
          store.commit("setIsFind", false);
          this.getList();
        }
      },
      isDetail: function (val) {
        if (val === true) {
          store.commit("setIsDetail", false);
          store.commit("setOperationDetailShow", true);
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
              if (this.total > 0) {
                let list = this.handleData(result, this);
                store.commit("setOperationSummaryList", list);
                this.data = list;
                this.$emit('getDownloadInfo', this.currentPage, this.pageSize,this.operationInfos);
              } else {
                this.data = result;
                this.$emit('getDownloadInfo', this.currentPage, this.pageSize, this.operationInfos);
                return false;
              }
            }
          }
        }).catch(err => {
          store.commit("setLoading", false);
          alert("网络错误，请先检查网络，再连接联系管理员");
        });
      },
      getList: function () {
        store.commit("setLoading", true);
        let options = {
          url: operationReportSummaryListUrl,
          data: this.operationInfos
        }
        options.data["currentPage"] = this.currentPage;
        options.data["pageSize"] = this.pageSize;
        this.fetchData(options);
      },
      filterData: function (query) {
        this.pageSize = query.limit;
        this.currentPage = query.offset / query.limit + 1;
        this.getList();
      },
      handleData: function (arr, that) {
        for (let i = 0; i < arr.length; i++) {
          let obj = arr[i];
          obj["total"] = obj.passCount + obj.failCount;
          let type;
          switch (obj.operationType) {
            case "上料":
              type = 0;
              break;
            case "换料":
              type = 1;
              break;
            case "核料":
              type = 2;
              break;
            case "全检":
              type = 3;
              break;
            case "发料":
              type = 4;
              break;
            default:
              break;
          }
          obj["type"] = type;
          obj["startTime"] = this.operationInfos.startTime;
          obj["endTime"] = this.operationInfos.endTime;
        }
        return arr;
      },
    },
    beforeDestroy() {
      store.commit("setOperationSummaryList", []);
    }
  }

  export const operationOperation = Vue.component('operationOperation', {
    template: `<span>
      <span title="详情" @click.stop.prevent="detail(row)" style="cursor:pointer">
        <icon name="detail" scale="2.5"></icon>
      </span>
    </span>`,
    props: ['row'],
    methods: {
      detail(row) {
        store.commit("setOperationSummary", row);
        store.commit("setIsDetail", true);
      }
    }
  });

</script>
<style lang="scss">
  .-table-body .table tr td {
    vertical-align: middle;
  }
</style>
