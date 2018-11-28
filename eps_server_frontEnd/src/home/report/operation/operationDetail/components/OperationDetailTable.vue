<template>
  <div class="operationDetailTable">
    <datatable v-bind="$data" />
  </div>
</template>
<script>
import store from './../../../../../store'
import {axiosPost} from "./../../../../../utils/fetchData"
import {operationReportListUrl} from "./../../../../../config/globalUrl"
export default {
  name:'operationDetailTable',
  props:['item'],
  data: () => ({
    columns: [
      { title:'站位', field:'lineseat',colStyle: {'width': '80px'}},
      { title:'物料编号', field:'materialNo', colStyle: {'width': '200px'}},
      { title:'物料描述', field:'materialDescription', colStyle: {'width': '100px'}},
      { title:'物料规格', field:'materialSpecitification' , colStyle: {'width': '250px'}},
      { title:'操作结果', field:'result' , colStyle: {'width': '80px'}},
      { title:'操作时间', field:'time',colStyle: {'width': '150px'}},
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
  computed:{
    operationInfos:function(){
      let obj = {
        type:this.item.type,
        operator:this.item.operator,
        workOrderNo:this.item.workOrderNo,
        startTime:this.item.startTime,
        endTime:this.item.endTime,
        currentPage:this.currentPage,
        pageSize:this.pageSize
      };
      for(let i = 0;i<this.lines.length;i++){
        if(this.item.line === this.lines[i].line){
          obj["line"] = this.lines[i].id;
          break;
        }
      }
      return obj;
    },
    lines: function () {
      return store.state.lines;
    },
  },
  watch: {
    query: {
      handler (query) {
        this.filterData(query);
      },
      deep: true
    }
  },
  methods:{
    fetchData:function(options){

      axiosPost(options).then(response => {
        store.commit("setLoading",false);
        if (response.data) {
          if( response.data.list){
            let result = response.data.list;
            let page = response.data.page;
            this.total = page.totallyData;
            this.currentPage = page.currentPage;
            this.pageSize = page.pageSize;
            store.commit("setOperationList",result);
            this.data = result;
          }
        }
      }).catch(err => {
        store.commit("setLoading",false);
        alert("网络错误，请先检查网络，再连接联系管理员");
      });
    },
    getList:function(){
      store.commit("setLoading",true);
      let options = {
        url:operationReportListUrl,
        data:this.operationInfos
      }
      options.data["currentPage"] = this.currentPage;
      options.data["pageSize"] = this.pageSize;
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
