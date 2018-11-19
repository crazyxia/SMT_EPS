<template>
  <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop="static">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="myModalLabel">
            {{message}}
          </h5>
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true" @click="finishOperation">&times;</button>
        </div>
        <div class="modal-body">
          <form class ="form form-inline" role="form">
            <div class="form-group">
              <label for="modalId">工号</label>
              <input type="text" class="form-control" id="modalId" v-model.trim="modalInfo.id" :disabled="isDisabled">
            </div>
            <div class="form-group">
              <label for="modalName">姓名</label>
              <input type="text" class="form-control" id="modalName" v-model.trim="modalInfo.name">
            </div>
            <div class="form-group">
              <label for="modaltype">岗位</label>
              <select class="form-control" id="modaltype" v-model.trim="modalInfo.type">
                <option selected="selected" disabled="disabled"  style='display: none' value=''></option>
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
              <label for="modalClassType">班别</label>
              <select class="form-control" id="modalClassType" v-model.trim="modalInfo.classType">
                <option selected="selected" disabled="disabled"  style='display: none' value=''></option>
                <option value="0">白班</option>
                <option value="1">夜班</option>
              </select>
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn_save" @click="save">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import store from './../../../../store'
import {userTip} from "./../../../../utils/formValidate"
import {axiosPost} from "./../../../../utils/fetchData"
import {addUserUrl,updateUserUrl} from "./../../../../config/globalUrl"
import {errTip} from './../../../../utils/errorTip'
export default {
  name:'userModal',
  data () {
    return {
      isDisabled:false
    }
  },
  computed:{
    message:function(){
      let operationType = store.state.userOperationType;
      if(operationType === "update"){
        this.isDisabled = true;
        return "修改信息";
      }else if(operationType === "add"){
        this.isDisabled = false;
        return "添加信息";
      }
    },
    modalInfo:function(){
      let user = store.state.user;
      let obj = {};
      obj.id = user.id;
      obj.name = user.name;
      obj.type = user.type;
      obj.classType = user.classType;
      return obj;
    },
    isAdd:function(){
      return store.state.isAdd;
    },
    isDelete:function(){
      return store.state.isDelete;
    },
    isUpdate:function(){
      return store.state.isUpdate;
    }
  },
  watch:{
    isAdd:function(val){
      if(val === true){
        store.commit("setIsAdd",false);
        $('#myModal').modal({backdrop:'static', keyboard: false});
      }
    },
    isUpdate:function(val){
      if(val === true){
        store.commit("setIsUpdate",false);
        $('#myModal').modal({backdrop:'static', keyboard: false});
      }
    },
    isDelete:function(val){
      if(val == true){
        store.commit("setIsDelete",false);
        this.update(false,"删除成功");
      }
    }
  },
  methods:{
    add:function(){
      let options ={
        url:addUserUrl,
        data:{
          id:this.modalInfo.id,
          name:this.modalInfo.name,
          type:this.modalInfo.type,
          classType:this.modalInfo.classType
        }
      }
      axiosPost(options).then(response => {
        if (response.data) {
          let result = response.data.result;
          if(result === "succeed"){
            alert("添加成功");
            let infos = {
              id:"",
              name:"",
              type:"",
              classType:"",
              password:""
            };
            store.commit("setUser",infos);
          }else{
            errTip(result);
          }
        }
      }).catch(err => {
        alert("error:" + JSON.stringify(err));
      });
    },
    update:function(isEnabled,succeedTip){
      let options ={
        url:updateUserUrl,
        data:{
          id:this.modalInfo.id,
          name:this.modalInfo.name,
          type:this.modalInfo.type,
          classType:this.modalInfo.classType,
          enabled:isEnabled
        }
      };
      axiosPost(options).then(response => {
        if (response.data) {
          let result = response.data.result;
          if(result === "succeed"){
            alert(succeedTip);
            $('#myModal').modal('hide');
            store.commit("setIsRefresh",true);
          }else{
            errTip(result);
          }
        }
      }).catch(err => {
        alert("error:" + JSON.stringify(err));
      });
    },
    save:function(){
      if(userTip(this.modalInfo)){
        let operationType = store.state.userOperationType;
        if(operationType === "add"){
          this.add();
        }else if(operationType === "update"){
          this.update(true,"修改成功");
        }
      }
    },
    finishOperation:function(){
      store.commit("setIsRefresh",true);
    }
  }
}

</script> 

<style scoped lang="scss">
.modal{
  .modal-dialog{
    max-width:300px;
    min-width:200px;
    position:absolute;
    top:50%;
    left:50%;
    transform:translate(-50%, -50%);
    .form-inline{
      .form-group{
        width:100%;
        margin-bottom:10px;
        label{
          margin-right:10px;
        }
        .form-control{
          width:204px;
          border-radius:10px;
        }
      }
    }
    .btn_save{
      width:100px;
      color:#fff;
      background-color:#00acec;
    }
    .btn_save:hover{
      background-color:#808080;
    }
  }
}
</style>
