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
          <form class="form" role="form">
            <div class="form-group">
              <label for="materialNo">料号</label>
              <input type="text" class="form-control" id="materialNo" v-model.trim="modalInfo.materialNo">
            </div>
            <div class="form-group">
              <label for="perifdOfValidity">保质期(天)</label>
              <input type="text" class="form-control" id="perifdOfValidity" v-model.trim="modalInfo.perifdOfValidity">
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
import {materialTip} from "./../../../../utils/formValidate"
import {axiosPost} from "./../../../../utils/fetchData"
import {addMaterialUrl,updateMaterialUrl} from "./../../../../config/globalUrl"
import {errTip} from './../../../../utils/errorTip'
export default {
  name:'materialModal',
  data () {
    return {

    }
  },
  computed:{
    message:function(){
      let operationType = store.state.materialOperationType;
      if(operationType == "update"){
        return "修改信息";
      }else if(operationType == "add"){
        return "添加信息";
      }
    },
    modalInfo:function(){
      return store.state.material;
    },
    isAdd:function(){
      return store.state.isAdd;
    },
    isUpdate:function(){
      return store.state.isUpdate;
    }
  },
  watch:{
    isAdd:function(val){
      if(val == true){
        store.commit("setIsAdd",false);
        $('#myModal').modal({backdrop:'static', keyboard: false});
      }
    },
    isUpdate:function(val){
      if(val == true){
        store.commit("setIsUpdate",false);
        $('#myModal').modal({backdrop:'static', keyboard: false});
      }
    }
  },
  methods:{
    add:function(){
      let options ={
        url:addMaterialUrl,
        data:{
          materialNo:this.modalInfo.materialNo,
          perifdOfValidity:this.modalInfo.perifdOfValidity,
        }
      }
      axiosPost(options).then(response => {
        if (response.data) {
          let result = response.data.result;
          if(result == "succeed"){
            alert("添加成功");
            let infos = {
              materialNo:"",
              perifdOfValidity:"",
            }
            store.commit("setMaterial",infos);
          }else{
            errTip(result);
          }
        }
      }).catch(err => {
        alert("error:" + JSON.stringify(err));
      });
    },
    update:function(){
      let options ={
        url:updateMaterialUrl,
        data:{
          id:this.modalInfo.id,
          materialNo:this.modalInfo.materialNo,
          perifdOfValidity:this.modalInfo.perifdOfValidity,
        }
      }
      let that = this;
      axiosPost(options).then(response => {
        if (response.data) {
          let result = response.data.result;
          if(result == "succeed"){
            alert("修改成功");
            $('#myModal').modal('hide');
            store.commit("setIsRefresh",true);
          }else{
            errTip(result);
          }
        }
      }).catch(err => {
        alert("请求接口失败，请先检查网络，再联系管理员");
      });
    },
    save:function(){
      if(materialTip(this.modalInfo)){
        let operationType = store.state.materialOperationType;
        if(operationType == "add"){
          this.add();
        }else if(operationType == "update"){
          this.update();
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
    .form{
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
