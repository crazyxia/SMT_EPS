<template>
  <div class="clientTable">
    <datatable v-bind="$data" />
  </div>
</template>
<script>
import store from './../../../../store'
import {axiosPost} from "./../../../../utils/fetchData"
import {clientReportListUrl} from "./../../../../config/globalUrl"
export default {
  name:'clientTable',
  props:['clientInfos'],
  data: () => ({
    columns: [
      { title:'线号', field:'line',colStyle: {'width': '80px'}},
      { title:'工单号', field:'workOrderNo',colStyle: {'width': '100px'} },
      { title:'槽位', field:'lineseat',colStyle: {'width': '80px'}},
      { title:'物料编号', field:'materialNo', colStyle: {'width': '120px'}},
      { title:'物料描述', field:'materialDescription', colStyle: {'width': '100px'}},
      { title:'物料规格', field:'materialSpecitification' , colStyle: {'width': '250px'}},
      { title:'操作类型', field:'operationType',colStyle: {'width': '100px'}},
      { title:'操作者', field:'operator', colStyle: {'width': '100px'}},
      { title:'操作时间', field:'time' , colStyle: {'width': '150px'}}
    ],
    HeaderSettings:false,
    fixHeaderAndSetBodyMaxHeight:700,
    data: [],
    total:0,
    tblClass: 'table-bordered',
    query: {"limit":20, "offset": 0},
    tblStyle: {
      'padding':'10px 0',
      'word-break': 'break-all',
      'table-layout': 'fixed',
      'color':'#666',
      'text-align':'center'
    },
    page:{},
    currentPage:1,
    pageSize:20
  }),
  computed:{
    clientList:function(){
      return store.state.clientList;
    },
    isFind:function(){
      return store.state.isFind;
    }
  },
  watch: {
    query: {
      handler (query) {
          this.filterData(query);
      },
      deep: true
    },
    isFind:function(val){
      if(val === true){
        this.currentPage = 1;
        this.pageSize = 20;
        this.query.limit = 20;
        this.query.offset = 0;
        store.commit("setIsFind",false);
        this.getList();
      }
    },
    page:function(val){
      if(val!=={} && val !== undefined){
        this.currentPage = this.page.currentPage;
        this.pageSize = this.page.pageSize;
        this.total = this.page.totallyData;
      }
    }
  },
  methods:{
    fetchData:function(options){
      axiosPost(options).then(response => {
        store.commit("setLoading",false);
        if (response.data) {
          let result = response.data;
          if(result.page && result.list){
            this.page = result.page;
            this.data = result.list;
          }
          store.commit("setLoading",false);
        }
      }).catch(err => {
        store.commit("setLoading",false);
        alert("网络错误，请先检查网络，再连接联系管理员");
      });
    },
    getList:function(){
      store.commit("setLoading",true);
      let options = {
        url:clientReportListUrl,
        data:this.clientInfos
      };
      options.data["currentPage"]=this.currentPage;
      options.data["pageSize"]=this.pageSize;
      this.fetchData(options);
    },
    filterData:function(query){
      this.pageSize = query.limit;
      this.currentPage = query.offset / query.limit + 1;
      this.getList();
    }
  }
}

</script>
<style lang="scss">
.-table-body .table tr td{
  vertical-align:middle;
}
</style>
