<template>
  <div class="clientTable">
    <datatable v-bind="$data" />
  </div>
</template>
<script>
import store from './../../../../store'
import {clientTip} from "./../../../../utils/formValidate"
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
    pageSizeOptions:[30],
    total:0,
    tblClass: 'table-bordered',
    query: {"limit":30, "offset": 0},
    tblStyle: {
      'padding':'10px 0',
      'word-break': 'break-all',
      'table-layout': 'fixed',
      'color':'#666',
      'text-align':'center'
    },
    page:{},
    currentPage:0
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
        if(query.offset != 0){
          this.filterData(query);
        }
      },
      deep: true
    },
    isFind:function(val){
      if(val == true){
        store.commit("setIsFind",false);
        this.getList();
      }
    },
    page:function(val){
      if(val!={}){
        this.currentPage = this.page.currentPage;
        this.total = this.page.totallyData;
      }
    }
  },
  methods:{
    fetchData:function(options){
      let that = this;
      axiosPost(options).then(response => {
        store.commit("setLoading",false);
        if (response.data) {
          let result = response.data;
          that.page = result.page;
          that.data = result.list;
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
      }
      options.data["currentPage"]=this.currentPage;
      this.fetchData(options);
    },
    filterData:function(query){
      let index = query.offset / query.limit+1;
      this.currentPage = index;
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