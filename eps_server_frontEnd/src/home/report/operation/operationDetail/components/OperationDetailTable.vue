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
  props:['operationInfos'],
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
    }
  }),
  mounted(){
    this.getList();
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
      let that = this;
      axiosPost(options).then(response => {
        store.commit("setLoading",false);
        if (response.data) {
          let result = response.data;
          that.total = result.length;
          store.commit("setOperationList",result);
          that.filterData(that.query);
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
      this.fetchData(options);
    },
    filterData:function(query){
      let list = store.state.operationList;
      let dataShow = list.slice(query.offset,query.offset+query.limit);
      this.data =dataShow;
    }
  }
}
</script>
<style lang="scss">
.-table-body .table tr td{
  vertical-align:middle;
}
</style>