<template>
  <div class="material">
    <el-form :inline="true" :model="materialInfo" class="demo-form-inline">
      <el-form-item label="料号">
        <el-input v-model.trim="materialInfo.materialNo" placeholder="料号"></el-input>
      </el-form-item>
      <el-form-item label="保质期（天）">
        <el-input v-model.trim="materialInfo.perifdOfValidity" placeholder="保质期（天）"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="find">查询</el-button>
        <el-button type="primary" @click="add">添加</el-button>
        <el-button type="info" @click="reset">清除条件</el-button>
      </el-form-item>
    </el-form>
    <MaterialTable></MaterialTable>
    <MaterialModal></MaterialModal>
  </div>
</template>

<script>
  import {mapActions} from 'vuex'
  import Bus from '../../../utils/bus'
  import MaterialModal from './components/MaterialModal'
  import MaterialTable from './components/MaterialTable'
  import {judge} from "../../../utils/formValidate";

  export default {
    name: 'material',
    data() {
      return {
        materialInfo: {
          materialNo:'',
          perifdOfValidity:'',
          orderBy:"material_no"
        },
      }
    },
    created(){
      this.setMaterial(JSON.parse(JSON.stringify(this.materialInfo)));
    },
    components: {
      MaterialTable, MaterialModal
    },
    methods: {
      ...mapActions(['setMaterial']),
      add: function () {
        Bus.$emit('addMaterial',true);
      },
      find: function () {
        if (this.materialInfo.perifdOfValidity !== "") {
          if (!judge(this.materialInfo.perifdOfValidity)) {
            this.$alertWarning("物料保质期必须为不以0开头的正整数");
            return;
          }
        }
        this.setMaterial(JSON.parse(JSON.stringify(this.materialInfo)));
        Bus.$emit('findMaterial',true);
      },
      reset:function(){
        this.materialInfo.materialNo = '';
        this.materialInfo.perifdOfValidity = '';
      }
    }
  }
</script>

<style scoped lang="scss">
  .material{
    padding:20px;
  }
</style>
