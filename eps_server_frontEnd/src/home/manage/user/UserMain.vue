<template>
  <div class="user">
  	<form class="form-inline" role="form">
  		<div class="form-group">
    		<label for="id">工号</label>
    		<input type="text" class="form-control" id="id" v-model.trim="userInfos.id">
  		</div>
  		<div class="form-group">
    		<label for="name">姓名</label>
    		<input type="text" class="form-control" id="name" v-model.trim="userInfos.name">
  		</div>
  		<div class="form-group">
    		<label for="type">岗位</label>
        <select class="form-control" id="type" v-model.trim="userInfos.type">
          <option selected="selected"  value=''>不限</option>
          <option value="0">仓库操作员</option>
          <option value="1">厂线操作员</option>
          <option value="2">IPQC</option>
          <option value="3">超级管理员</option>
          <option value="4">生产管理员</option>
          <option value="5">品质管理员</option>
          <option value="6">工程管理员</option>
    		</select>
  		</div>
  		<div class="form-group">
    		<label for="classType">班别</label>
    		<select class="form-control" id="classType" v-model.trim="userInfos.classType">
          <option selected="selected" value=''>不限</option>
          <option value="0">白班</option>
          <option value="1">夜班</option>
    		</select>
  		</div>
  		<div class="btn-group">
    		<button type="button" class="btn btn_find" @click="find">查询</button>
    		<button type="button" class="btn btn_add" @click="addModal">添加</button>
 		  </div>
    </form>

    <UserTable :userInfos="userInfos"></UserTable>
    <UserModal></UserModal>
    <CodePic></CodePic>
  </div>
</template>

<script>
import store from './../../../store' 
import UserModal from './components/UserModal'
import UserTable from './components/UserTable'
import CodePic from './components/CodePic'
export default {
  name:'user',
  data () {
    return {
      userInfos:{
        id:"",
        name:"",
        type:"",
        classType:"",
        password:""
      },
    }
  },
  components:{
    UserTable,UserModal,CodePic
  },
  methods:{
    addModal:function(){
      store.commit("setUserOperationType","add");
      let modalInfos = {
        id:"",
        name:"",
        type:"",
        classType:"",
        password:""
      };
      store.commit("setUser",modalInfos);
      store.commit("setIsAdd",true);
    },
    find:function(){
      store.commit("setIsFind",true);
    }
  }
}
</script> 

<style scoped lang="scss">
@import '@/assets/css/common.scss';
</style>
