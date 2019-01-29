<template>
  <div class="user">
    <!--查询表单-->
    <el-form :inline="true" :model="userInfo" class="demo-form-inline">
      <el-form-item label="工号">
        <el-input v-model.trim="userInfo.id" placeholder="工号"></el-input>
      </el-form-item>
      <el-form-item label="姓名">
        <el-input v-model.trim="userInfo.name" placeholder="姓名"></el-input>
      </el-form-item>
      <el-form-item label="岗位">
        <el-select v-model.trim="userInfo.type" placeholder="岗位" value="">
          <el-option label="不限" selected="selected"  value=''></el-option>
          <el-option label="仓库操作员" value='0'></el-option>
          <el-option label="厂线操作员" value='1'></el-option>
          <el-option label="IPQC" value='2'></el-option>
          <el-option label="超级管理员" value='3'></el-option>
          <el-option label="生产管理员" value='4'></el-option>
          <el-option label="品质管理员" value='5'></el-option>
          <el-option label="工程管理员" value='6'></el-option>
          <el-option label="仓库管理员" value='7'></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="班别">
        <el-select v-model.trim="userInfo.classType" placeholder="班别" value="">
          <el-option label="不限" selected="selected"  value=''></el-option>
          <el-option label="白班" value='0'></el-option>
          <el-option label="夜班" value='1'></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="是否在职">
        <el-select v-model.trim="userInfo.enabled" placeholder="是否在职" value="">
          <el-option label="不限" selected="selected"  value=''></el-option>
          <el-option label="是" value='1'></el-option>
          <el-option label="否" value='0'></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="find">查询</el-button>
        <el-button type="primary" @click="add">添加</el-button>
        <el-button type="info" @click="reset">清除条件</el-button>
      </el-form-item>
    </el-form>
    <!--表格-->
    <UserTable></UserTable>
    <!--操作模态框-->
    <UserModal></UserModal>
  </div>
</template>

<script>
  import {mapActions} from 'vuex'
  import Bus from '../../../utils/bus'
  import UserModal from './components/UserModal'
  import UserTable from './components/UserTable'
  export default {
    name:'user',
    data () {
      return {
        //表单查询信息
        userInfo:{
          id:'',
          name:'',
          type:'',
          classType:'',
          password:'',
          enabled:''
        },
      }
    },
    created(){
      //存取查询信息
      this.setUser(JSON.parse(JSON.stringify(this.userInfo)))
    },
    components:{
      UserTable,UserModal
    },
    methods:{
      ...mapActions(['setUser']),
      //添加
      add:function(){
        Bus.$emit('addUser',true);
      },
      //查询
      find:function(){
        this.setUser(JSON.parse(JSON.stringify(this.userInfo)));
        Bus.$emit('findUser',true);
      },
      //重置
      reset:function(){
        this.userInfo.id = '';
        this.userInfo.name = '';
        this.userInfo.type = '';
        this.userInfo.classType = '';
        this.userInfo.password = '';
        this.userInfo.enabled = '';
      }
    }
  }
</script>

<style scoped lang="scss">
  .user{
    padding:20px;
  }
</style>
