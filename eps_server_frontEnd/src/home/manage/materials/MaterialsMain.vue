<template>
  <div class="material">
    <form class="form-inline" role="form">
      <div class="form-group">
        <label for="materialNo">料号</label>
        <input type="text" class="form-control" id="materialNo" v-model.trim="materialInfos.materialNo">
      </div>
      <div class="form-group">
        <label for="perifdOfValidity">保质期(天)</label>
        <input type="text" class="form-control" id="perifdOfValidity" v-model.trim="materialInfos.perifdOfValidity">
      </div>
      <div class="btn-group">
        <button type="button" class="btn btn_find" @click="find">查询</button>
        <button type="button" class="btn btn_add" @click="addModal">添加</button>
      </div>
    </form>  

    <MaterialTable :materialInfos="materialInfos"></MaterialTable>
    <MaterialModal></MaterialModal>
  </div>
</template>

<script>
import store from './../../../store' 
import MaterialModal from './components/MaterialModal'
import MaterialTable from './components/MaterialTable'
export default {
  name:'material',
  data () {
    return {
      materialInfos:{
        materialNo:"",
        perifdOfValidity:""
      },
    }
  },
  components:{
    MaterialTable,MaterialModal
  },
  methods:{
    addModal:function(){
      store.commit("setMaterialOperationType","add");
      let modalInfos = {
        materialNo:"",
        perifdOfValidity:""
      };
      store.commit("setMaterial",modalInfos);
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
