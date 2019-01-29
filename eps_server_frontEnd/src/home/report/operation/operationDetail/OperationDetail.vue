<template>
  <div class="operationDetail">
    <el-form :inline="true" :model="detail" class="demo-form-inline">
      <el-form-item>
        <span title="返回" style="cursor:pointer" @click="returnTo"><icon name="returnB" scale="4"></icon></span>
      </el-form-item>
      <el-form-item label="线号">
        <el-input v-model.trim="detail.line" :disabled="true"></el-input>
      </el-form-item>
      <el-form-item label="工单号">
        <el-input v-model.trim="detail.workOrderNo" :disabled="true"></el-input>
      </el-form-item>
      <el-form-item label="操作员">
        <el-input v-model.trim="detail.operator" :disabled="true"></el-input>
      </el-form-item>
      <el-form-item label="操作类型">
        <el-input v-model.trim="detail.operationType" :disabled="true"></el-input>
      </el-form-item>
    </el-form>
    <OperationDetailTable :item="detail" :disabled="true"></OperationDetailTable>
  </div>
</template>

<script>
  import {mapGetters} from 'vuex'
  import OperationDetailTable from './components/OperationDetailTable'

  export default {
    name: 'operationDetail',
    data() {
      return {
      }
    },
    components: {
      OperationDetailTable
    },
    computed:{
      ...mapGetters(['detail'])
    },
    methods: {
      returnTo: function () {
        this.$emit('setIsShow',true);
      }
    }
  }

</script>

<style scoped lang="scss">
</style>
