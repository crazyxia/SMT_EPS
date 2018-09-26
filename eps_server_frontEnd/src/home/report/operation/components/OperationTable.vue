<template>
  <div class="operationTable">
    <datatable v-bind="$data" />
  </div>
</template>
<script>
import Vue from 'vue'
import store from './../../../../store'
import {axiosPost} from "./../../../../utils/fetchData"
import {getTypeName} from "./../../../../utils/formValidate"
import {operationReportSummaryListUrl} from "./../../../../config/globalUrl"
export default {
  name:'operationTable',
  props:['operationInfos'],
  data: () => ({
    columns: [
      { title:'线号', field:'line',colStyle: {'width': '80px'}},
      { title:'工单号', field:'workOrderNo',colStyle: {'width': '200px'} },
      { title:'操作者', field:'operator', colStyle: {'width': '100px'}},
      { title:'操作类型', field:'typeName',colStyle: {'width': '100px'}},
      { title:'成功数', field:'passCount',colStyle: {'width': '100px'}},
      { title:'失败数', field:'failCount',colStyle: {'width': '100px'}},
      { title:'总数', field:'total',colStyle: {'width': '100px'}},
      { title:'操作', field:'operation',tdComp: 'operationOperation', colStyle: {'width':'80px'}}
    ],
    HeaderSettings:false,
    fixHeaderAndSetBodyMaxHeight:700,
    data:[],
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
  computed:{
    oprationSummaryList:function(){
      return store.state.operationSummaryList;
    },
    isFind:function(){
      return store.state.isFind;
    },
    isDetail:function(){
      return store.state.isDetail;
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
        store.commit("setIsFind",false);
        this.getList();
      }
    },
    isDetail:function(val){
      if(val == true){
        store.commit("setIsDetail",false);
        store.commit("setOperationDetailShow",true);
      }
    }
  },
  mounted(){
    this.getList();
  },
  methods:{
    fetchData:function(options){
      let that = this;
      axiosPost(options).then(response => {
        store.commit("setLoading",false);
        if (response.data) {
          let result = response.data;
          console.log(result);
          that.total = result.length;
          let list = that.handleData(result,that);
          store.commit("setOperationSummaryList",list);
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
        url:operationReportSummaryListUrl,
        data:this.operationInfos
      }
      this.fetchData(options);
    },
    filterData:function(query){
      let list = store.state.operationSummaryList;
      let dataShow = list.slice(query.offset,query.offset+query.limit);
      this.data =dataShow;
    },
    handleData:function(arr,that){
      let typeName = getTypeName(that.operationInfos.type);
      for(let i=0;i<arr.length;i++){
        let obj = arr[i];
        obj["typeName"] = typeName;
        obj["total"] = obj.passCount + obj.failCount;
      }
      return arr;
    },
  },
  beforeDestroy(){
    store.commit("setOperationSummaryList","");
  }
}

export const operationOperation = Vue.component('operationOperation',{
  template:`<span>
      <span title="详情" @click.stop.prevent="detail(row)" style="cursor:pointer">
        <icon name="detail" scale="2.5"></icon>
      </span>
    </span>`,
  props:['row'],
  methods:{
    detail(row){
      store.commit("setOperationSummary",row);
      store.commit("setIsDetail",true);
    }
  }
});

</script>
<style lang="scss">
.-table-body .table tr td{
  vertical-align:middle;
}
</style>
