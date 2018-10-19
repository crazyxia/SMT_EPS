<template>
  <div class="ioTable">
    <datatable v-bind="$data" />
  </div>
</template>
<script>
import store from './../../../../store'
import {axiosPost} from "./../../../../utils/fetchData"
import {stockLogsListUrl} from "./../../../../config/globalUrl"
export default {
  name:'ioTable',
  props:['ioInfos'],
  data: () => ({
    columns: [
      { title:'时间戳', field:'timestamp',colStyle: {'width': '150px'}},
      { title:'料号', field:'materialNo',colStyle: {'width': '200px'} },
      { title:'数量', field:'quantity',colStyle: {'width': '60px'}},
      { title:'操作者', field:'operator', colStyle: {'width': '80px'}},
      { title:'操作时间', field:'operationTimeString' , colStyle: {'width': '180px'}},
      { title:'仓位', field:'position', colStyle: {'width': '80px'}},
      { title:'供应商', field:'custom' , colStyle: {'width': '100px'}}
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
    currentPage:1,
    pageSize:20
  }),
  beforeDestroy(){
    store.commit("setIoList","");
  },
  computed:{
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
      if(val == true){
        this.currentPage = 1;
        this.pageSize = 20;
        this.query.limit = 20;
        this.query.offset = 0;
        store.commit("setIsFind",false);
        this.getList();
      }
    }
  },
  methods:{
    fetchData:function(options){
      let that = this;
      axiosPost(options).then(response => {
        store.commit("setLoading",false);
        if (response.data) {
          let result = response.data.list;
          let page = response.data.page;
          that.total = page.totallyData;
          that.currentPage = page.currentPage;
          that.pageSize = page.pageSize;
          store.commit("setIoList",result);
          that.data = result;
        }
      }).catch(err => {
        store.commit("setLoading",false);
        alert("网络错误，请先检查网络，再连接联系管理员");
      });
    },
    getList:function(){
      store.commit("setLoading",true);
      let options = {
        url:stockLogsListUrl,
        data:this.ioInfos
      }
      options.data["currentPage"] = this.currentPage;
      options.data["pageSize"] = this.pageSize;
      this.fetchData(options);
    },
    filterData:function(query){
      this.pageSize = query.limit;
      this.currentPage = query.offset / query.limit + 1;
      this.getList();
    },
  }
}

</script>
<style lang="scss">
</style>
