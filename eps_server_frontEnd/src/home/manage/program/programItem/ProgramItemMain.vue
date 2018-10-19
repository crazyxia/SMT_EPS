<template>
  <div class="programDetail">
    <form class="form-inline" role="form">
      <span title="返回" @click="returnToProgram"><icon name="returnB" scale="4"></icon></span>
      <div class="form-group">
        <label for="workOrder">工单</label>
        <input type="text" class="form-control" id="workOrder" v-model="programInfos.workOrder" disabled="disabled">
      </div>
      <div class="form-group">
        <label for="boardType">版面</label>
        <input type="text" class="form-control" id="boardType" v-model="programInfos.boardTypeName" disabled="disabled" >
      </div>
      <div class="form-group">
        <label for="line">线号</label>
        <input type="text" class="form-control" id="line" v-model="programInfos.lineName" disabled="disabled" >
      </div>
      <div class="btn-group">
        <button type="button" class="btn btn_add" @click="addModal">添加</button>
        <button type="button" class="btn btn_conserve" @click="save">保存</button>
      </div>
    </form>
    <p>温馨提示：编辑完成之后请记得点击保存</p>
    <ProgramItemTable></ProgramItemTable>
    <ProgramItemModal @getModalInfo="getModalMessage"></ProgramItemModal>
  </div>
</template>

<script>
import store from './../../../../store'
import ProgramItemTable from './components/ProgramItemTable'
import ProgramItemModal from './components/ProgramItemModal'
import {axiosPost} from "./../../../../utils/fetchData"
import {updateProgramItemUrl} from "./../../../../config/globalUrl"
import {errTip} from './../../../../utils/errorTip'
export default {
  name:'programDetail',
  top:"",
  data () {
    return {
      operations:[],
      operationItem:{
        serialNo:0,
        operation:"",
        targetLineseat:"",
        targetMaterialNo:"",
        programId:store.state.program.id,
        lineseat:"",
        alternative:"",
        materialNo:"",
        specitification:"",
        position:"",
        quantity:0
      },
      modalInfo:{}
    }
  },
  components:{
    ProgramItemTable,ProgramItemModal
  },
  computed:{
    programInfos:function(){
      return store.state.program;
    },
    programItemInfos:function(){
      return store.state.programItem;
    },
    programItemList:function(){
      return store.state.programItemList;
    },
    isDelete:function(){
      return store.state.isDelete;
    }
  },
  watch:{
    isDelete:function(val){
      if(val == true){
        store.commit("setIsDelete",false);
        let index = store.state.operationIndex;
        this.programItemList.splice(index,1);
        store.commit("setProgramItemRefresh",true);
        let programItemInfos = this.programItemInfos;
        this.modalInfo ={
          lineseat: programItemInfos.lineseat,
          materialType: programItemInfos.materialType,
          materialNo: programItemInfos.materialNo,
          specitification: programItemInfos.specitification,
          position: programItemInfos.position,
          quantity: programItemInfos.quantity
        };
        this.setOptionItem();
      }
    }
  },
  methods:{
    returnToProgram:function(){
      store.commit("setProgramItemShow",false);
    },
    setOptionItem:function(){
      let opera = this.operationItem;
      let item = this.programItemInfos;
      let operationType = store.state.programItemOperationType;
      if(operationType == "add"){
        opera.operation = 0;
      }else if(operationType == "update"){
        opera.operation = 1;
      }else if(operationType == "delete"){
        opera.operation = 2;
      }
      opera.targetLineseat = item.lineseat;
      opera.targetMaterialNo = item.materialNo;
      opera.lineseat = this.modalInfo.lineseat;
      if(this.modalInfo.materialType == "主料"){
        opera.alternative = false;
      }else{
        opera.alternative = true;
      }
      opera.materialNo = this.modalInfo.materialNo;
      opera.specitification = this.modalInfo.specitification;
      opera.position = this.modalInfo.position;
      opera.quantity = this.modalInfo.quantity;
      this.operations.push(opera);
      this.modalInfo = {};
      this.resetOperation();
      store.commit("setProgramItem",{});
    },
    getModalMessage:function(data){
      this.modalInfo = data;
      this.setOptionItem();
    },
    save:function(){
      if(this.operations.length <= 0){
        alert("请编辑后再点击保存");
      }else{
        this.updateProgramItem();
      }
    },
    addModal:function(){
      let programItem = {
        lineseat:"",
        alternative:"",
        materialNo:"",
        specitification:"",
        position:"",
        quantity:""
      }
      store.commit("setOperationIndex",this.programItemList.length);
      store.commit("setProgramItemOperationType","add");
      store.commit("setProgramItem",programItem);
      store.commit("setIsAdd",true);
    },
    resetOperation:function(){
      this.operationItem = {
        serialNo:0,
        operation:"",
        targetLineseat:"",
        targetMaterialNo:"",
        programId:store.state.program.id,
        lineseat:"",
        alternative:"",
        materialNo:"",
        specitification:"",
        position:"",
        quantity:0
      }
    },
    updateProgramItem:function(){
      let options = {
        url:updateProgramItemUrl,
        data:{
          operations:JSON.stringify(this.operations)
        }
      }
      axiosPost(options).then(response => {
        if (response.data) {
          let result = response.data.result;
          if(result == "succeed"){
            alert("编辑成功");
            this.operations = [];
            this.resetOperation();
            this.returnToProgram();
          }else{
            errTip(result);
            this.operations = [];
          }
          store.commit("setIsRefresh",true);
        }
      }).catch(err => {
        alert("接口请求失败，请检查网络，再联系管理员");
        this.operations = [];
      });
    },
  }
}
</script>

<style scoped lang="scss">
@import '@/assets/css/common.scss';
</style>
