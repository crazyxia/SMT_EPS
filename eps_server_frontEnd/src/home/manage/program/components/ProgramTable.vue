<template>
  <div class="programTable">
    <datatable v-bind="$data" />
  </div>
</template>
<script>
import Vue from 'vue'
import store from './../../../../store'
import {axiosPost} from "./../../../../utils/fetchData"
import {programListUrl} from "./../../../../config/globalUrl"
export default {
  name:'programTable',
  props:['programInfos'],
  data: () => ({
    columns: [
      { title:'站位表', field:'programName',colStyle: {'width':'180px'}},
      { title:'工单', field:'workOrder',colStyle: {'width': '150px'} },
      { title:'版面', field:'boardTypeName',colStyle: {'width': '80px'}},
      { title:'上传时间', field:'createTimeString', colStyle: {'width': '100px'}},
      { title:'状态', field:'stateName' , colStyle: {'width': '80px'}},
      { title:'线号', field:'lineName' , colStyle: {'width': '50px'}},
      { title:'操作', field:'operation',tdComp: 'ProgramOperation', colStyle: {'width':'150px'}}
    ],
    HeaderSettings:false,
    fixHeaderAndSetBodyMaxHeight:700,
    data: [],
    total:0,
    tblClass: 'table-bordered',
    query: {"limit": 20, "offset": 0},
    tblStyle: {
      'padding':'10px 0',
      'word-break': 'break-all',
      'table-layout': 'fixed',
      'color':'#666',
      'text-align':'center'
    },
  }),
  mounted(){
    this.find();
  },
  computed:{
    programList:function(){
      return store.state.programList;
    },
    isUploadFinish:function(){
      return store.state.isUploadFinish;
    },
    isFind:function(){
      return store.state.isFind;
    },
    isRefresh:function(){
      return store.state.isRefresh;
    }
  },
  watch: {
    query: {
      handler (query) {
        this.filterData(query);
      },
      deep: true
    },
    isUploadFinish:function(val){
      if(val == true){
        console.log("val",val);
        store.commit("setIsUploadFinish",false);
        this.getList();
      }
    },
    isFind:function(val){
      if(val == true){
        store.commit("setIsFind",false);
        this.find();
      }
    },
    isRefresh:function(val){
      if(val == true){
        console.log(val);
        store.commit("setIsRefresh",false);
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
          let [ ...result ] = response.data;
          that.total = result.length;
          store.commit("setProgramList",result);
          that.filterData(that.query);
        }
      }).catch(err => {
        store.commit("setLoading",false);
        alert("接口请求失败，请检查网络，再联系管理员");
      });
    },
    getList:function(){
      store.commit("setLoading",true);
      let options = {
        url:programListUrl,
        data:{
           orderBy:'create_time desc'
        }
      }
      this.fetchData(options);
    },
    find:function(){
      store.commit("setLoading",true);
      let options = {
        url:programListUrl,
        data:this.programInfos
      }
      options.data["orderBy"]='create_time desc';
      this.fetchData(options);
    },
    filterData:function(query){
      let list = store.state.programList;
      let dataShow = list.slice(query.offset,query.offset+query.limit);
      this.data =dataShow;
    }
  }
}

export const ProgramOperation = Vue.component('ProgramOperation',{
  template:`<div>
      <button type="button" class="btn" style="font-size:14px;background:#00acec;color:#fff;margin-right:5px;"
        @click.stop.prevent="updateStatus(row)">修改状态</button>
      <button type="button" class="btn" style="font-size:14px;background:#49bf67;color:#fff"
        @click.stop.prevent="updateTable(row)">修改表格</button>
  </div>`,
  props:['row'],
  methods:{
    updateStatus(row){
      store.commit("setOldState","");
      store.commit("setIsUpdate",true);
      store.commit("setProgram",row);
      store.commit("setOldState",row.state);
      store.commit("setProgramOperationType","update");
    },
    updateTable(row){
      store.commit("setProgram",row);
      store.commit("setProgramItemShow",true);
    }
  }
});
</script>
<style lang="scss">
.-table-body .table tr td{
  vertical-align:middle;
}
</style>
