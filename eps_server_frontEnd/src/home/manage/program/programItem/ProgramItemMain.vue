<template>
  <div class="programDetail">
    <p class="tip">提示：编辑完成之后请记得点击保存</p>
    <el-form :inline="true" :model="programInfo" class="demo-form-inline">
      <el-form-item>
        <span title="返回" @click="returnToProgram"><icon name="returnB" scale="4"></icon></span>
      </el-form-item>
      <el-form-item label="工单">
        <el-input v-model.trim="programInfo.workOrder" placeholder="工单" :disabled="true"></el-input>
      </el-form-item>
      <el-form-item label="版面">
        <el-input v-model.trim="programInfo.boardTypeName" placeholder="版面" :disabled="true"></el-input>
      </el-form-item>
      <el-form-item label="线号">
        <el-input v-model.trim="programInfo.lineName" placeholder="线号" :disabled="true"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="lastAdd">追加</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </el-form-item>
    </el-form>
    <!--表格-->
    <program-item-table :id="programInfo.id"></program-item-table>
    <!--模态框-->
    <ProgramItemModal></ProgramItemModal>
  </div>
</template>

<script>
  import Bus from '../../../../utils/bus'
  import {mapGetters,mapActions} from 'vuex'
  import ProgramItemTable from './components/ProgramItemTable'
  import ProgramItemModal from './components/ProgramItemModal'
  import {axiosPost} from "./../../../../utils/fetchData"
  import {updateProgramItemUrl} from "./../../../../config/globalUrl"
  import {errTip} from './../../../../utils/errorTip'

  export default {
    name: 'programDetail',
    data() {
      return {}
    },
    components: {
      ProgramItemTable, ProgramItemModal
    },
    computed: {
      ...mapGetters(['programInfo','operations'])
    },
    methods: {
      ...mapActions(['setLoading','setOperations']),
      //返回
      returnToProgram: function () {
        this.$emit('setIsShow',true);
      },
      //编辑完成
      save:function(){
        //判断
        if(this.operations.length <= 0){
          this.$alertWarning('请编辑后再点击保存');
          return;
        }
        //请求
        this.setLoading(true);
        let options = {
          url:updateProgramItemUrl,
          data:{
            operations:JSON.stringify(this.operations)
          }
        };
        axiosPost(options).then(response => {
          this.setLoading(false);
          this.setOperations([]);
          if (response.data) {
            let result = response.data.result;
            if(result === "succeed"){
              this.$alertSuccess("编辑成功");
              this.returnToProgram();
            }else{
              this.$alertWarning(errTip(result));
            }
          }
        }).catch(err => {
          this.setLoading(false);
          this.setOperation([]);
          this.$alertError("接口请求失败，请检查网络，再联系管理员");
        });
      },
      //追加
      lastAdd:function () {
        Bus.$emit('addLastProgramItem',this.programInfo.id);
      }
    }
  }
</script>

<style scoped lang="scss">
  .tip {
    font-size: 15px;
    color: red;
    border-bottom: 1px solid #eee;
    margin-bottom: 10px;
    padding-bottom: 10px;
  }
</style>
