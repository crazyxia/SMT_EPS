<template>
  <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop="static">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="myModalLabel">
            {{message}}
          </h5>
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        </div>
        <div class="modal-body">
          <form class="form form-inline" role="form">
            <div class="form-group">
              <label for="line">线号</label>
              <input type="text" class="form-control" id="line" disabled="disabled" v-model="modalInfo.lineName">
            </div>
            <div class="form-group">
              <label for="workOrder">工单</label>
              <input type="text" class="form-control" id="WorkOrder" disabled="disabled" v-model="modalInfo.workOrder">
            </div>
            <div class="form-group">
              <label for="state">状态</label>
              <select class="form-control" id="state" v-model="modalInfo.state">
                <option selected="selected" disabled="disabled"  style='display: none' value=''></option>
                <option value="0">未开始</option>
                <option value="1">进行中</option>
                <option value="2">已完成</option>
                <option value="3">已作废</option>
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
import {programTip} from "./../../../../utils/formValidate"
import {axiosPost} from "./../../../../utils/fetchData"
import {programStartUrl,programfinishUrl,programCancelUrl} from "./../../../../config/globalUrl"
import {errTip} from './../../../../utils/errorTip'
export default {
  name:'programModal',
  data () {
    return {

    }
  },
  computed:{
    message:function(){
      let operationType = store.state.programOperationType;
      if(operationType === "update"){
        return "修改信息";
      }
    },
    modalInfo:function(){
      return store.state.program;
    },
    isUpdate:function(){
      return store.state.isUpdate;
    }
  },
  watch:{
    isUpdate:function(val){
      if(val === true){
        store.commit("setIsUpdate",false);
        $('#myModal').modal({backdrop:'static', keyboard: false});
      }
    },
  },
  methods:{
    fetchData:function(url){
      let options = {
        url:url,
        data:{
          id:store.state.program.id
        }
      };
      axiosPost(options).then(response => {
        if (response.data) {
          let result = response.data.result;
          if(result === "succeed"){
            store.commit("setIsRefresh",true);
          }else{
            errTip(result);
            store.commit("setIsRefresh",true);
          }
        }
      }).catch(err => {
        alert("接口请求失败，请检查网络，再联系管理员");
      });
    },
    save:function(){
      let state = this.modalInfo.state;
      if(programTip(state)){
        if(state == 1){
          this.setStart();
        }else if(state == 2){
          this.setFinish();
        }else if(state == 3){
          this.setCancel();
        }
        $('#myModal').modal('hide');
      }else{
        let oldState = store.state.oldState;
        store.commit("setProgramState",oldState);
      }
    },
    setStart:function(){
      this.fetchData(programStartUrl);
    },
    setFinish:function(){
      this.fetchData(programfinishUrl);
    },
    setCancel:function(){
      this.fetchData(programCancelUrl);
    }
  }
}

</script> 

<style scoped lang="scss">
.modal{
  .modal-dialog{
    max-width:350px;
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
          width:254px;
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
