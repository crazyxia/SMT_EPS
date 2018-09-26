<template>
  <div class="operationDetail">
    <form class="form-inline" role="form">
      <span title="返回" @click="returnToOperationReport"><icon name="returnB" scale="4"></icon></span>
      <div class="form-group">
        <label for="line">线号</label>
        <input type="text" class="form-control" id="line" disabled="disabled" v-model="item.line">
      </div>
      <div class="form-group">
        <label for="workOrderNo">工单号</label>
        <input type="text" class="form-control" id="workOrderNo" v-model="item.workOrderNo" disabled="disabled">
      </div>
      <div class="form-group">
        <label for="operator">操作员</label>
        <input type="text" class="form-control" id="operator" disabled="disabled" v-model="item.operator">
      </div>
      <div class="form-group">
        <label for="type">操作类型</label>
        <input type="text" class="form-control" id="type" disabled="disabled" v-model="item.typeName">
      </div>
    </form>
    <OperationDetailTable :operationInfos="operationInfos"></OperationDetailTable>
  </div>
</template>

<script>
import OperationDetailTable from './components/OperationDetailTable'
import store from './../../../../store'
export default {
  name:'operationDetail',
  props:['operationInfos'],
  data () {
    return {
    }
  },
  components:{
   OperationDetailTable
  },
  computed:{
    item:function(){
      return store.state.operationSummary;
    }
  },
  methods:{
    returnToOperationReport:function(){
      store.commit("setOperationDetailShow",false);
    }
  }
}

</script>

<style scoped lang="scss">
  .operationDetail{
    .form-inline{
      width:100%;
      margin-bottom:10px;
      span{
        cursor:pointer;
        margin-right:25px;
        margin-bottom:5px;
      }
      .form-group{
        margin-right:20px;
        label{
          padding-right:5px;
        }
        input.form-control,select.form-control{
          width:130px;
          border-radius:15px;
        }
        input[id="line"]{
          width:100px;
        }
        input[id="workOrderNo"]{
          width:300px;
        }
      }
    }
  }
</style>
