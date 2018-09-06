<template>
  <div class="userTable">
    <datatable v-bind="$data" />
  </div>
</template>
<script>
import Vue from 'vue'
import store from './../../../../store'
import {axiosPost} from "./../../../../utils/fetchData"
import {userListUrl} from "./../../../../config/globalUrl"
export default {
  name:'userTable',
  props:['userInfos'],
  data: () => ({
    columns: [
      { title:'工号', field:'id',colStyle: {'width': '80px'}},
      { title:'姓名', field:'name',colStyle: {'width': '80px'} },
      { title:'岗位', field:'typeName',colStyle: {'width': '100px'}},
      { title:'班别', field:'classTypeName', colStyle: {'width': '80px'}},
      { title:'入职时间', field:'createTimeString' , colStyle: {'width': '120px'}},
      { title:'操作', field:'operation',tdComp: 'UserOperation', colStyle: {'width':'100px'}}
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
    this.getList();
  },
  computed:{
    userList:function(){
      return store.state.userList;
    },
    isRefresh:function(){
      return store.state.isRefresh;
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
    isRefresh:function(val){
      if(val == true){
        store.commit("setIsRefresh",false);
        this.getList();
      }
    },
    isFind:function(val){
      if(val == true){
        store.commit("setIsFind",false);
        this.find();
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
          let list = that.copyArr(result);
          that.total = list.length;
          store.commit("setUserList",list);
          that.filterData(that.query);
        }
      }).catch(err => {
        store.commit("setLoading",false);
        alert("接口请求错误，请检查网络连接，再联系管理员");
      });
    },
    getList:function(){
      store.commit("setLoading",true);
      let options = {
        url:userListUrl,
        data:{
           orderBy:'create_time desc'
        }
      }
      this.fetchData(options);
    },
    find:function(){
      let options = {
        url:userListUrl,
        data:this.userInfos
      }
      options.data["orderBy"]='create_time desc';
      this.fetchData(options);
    },
    filterData:function(query){
      let list = store.state.userList;
      let dataShow = list.slice(query.offset,query.offset+query.limit);
      this.data =dataShow;
    },
    copyArr:function(arr) {
      let res = [];
      for (let i = 0; i < arr.length; i++) {
        if(arr[i].enabled == true){
          res.push(arr[i]);
        }
      }
      return res;
    }
  }
}

export const UserOperation = Vue.component('UserOperation',{
  template:`<span>
      <span title="编辑" @click.stop.prevent="update(row)" style="padding-right:8px;cursor:pointer">
        <icon name="edit" scale="2.5"></icon>
      </span>
      <span title="删除" @click.stop.prevent="deleteRow(row)" style="padding-right:8px;cursor:pointer">
        <icon name="delete" scale="2.5"></icon>
      </span>
      <span title="工号二维码" @click.stop.prevent="getCodePic(row)" style="cursor:pointer">
        <icon name="codePic" scale="2.5"></icon>
      </span>
    </span>`,
  props:['row'],
  methods:{
    update(row){
      store.commit("setUser",row);
      store.commit("setUserOperationType","update");
      store.commit("setIsUpdate",true);
    },
    deleteRow(row){
      store.commit("setUser",row);
      store.commit("setUserOperationType","delete");
      store.commit("setIsDelete",true);
    },
    getCodePic(row){
      store.commit("setUserId",row.id);
      store.commit("setIsGetCodePic",true);
    }
  }
});
</script>
<style lang="scss">
.-table-body .table tr td{
  vertical-align:middle;
}
</style>