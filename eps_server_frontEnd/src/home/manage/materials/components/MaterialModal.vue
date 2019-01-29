<template>
  <div>
    <el-dialog
      title="添加物料"
      :show-close="showClose"
      @close="close"
      :close-on-click-modal="isCloseOnModal"
      :close-on-press-escape="isCloseOnModal"
      :visible.sync="addDialogVisible"
      width="400px">
      <el-form label-width="100px" label-position="right">
        <el-form-item label="料号">
          <el-input v-model.trim="materialInfo.materialNo" size="large"></el-input>
        </el-form-item>
        <el-form-item label="保质期（天）">
          <el-input v-model.trim="materialInfo.perifdOfValidity" size="large"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="add">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog
      title="修改物料"
      :close-on-click-modal="isCloseOnModal"
      :close-on-press-escape="isCloseOnModal"
      :visible.sync="editDialogVisible"
      width="400px">
      <el-form label-width="100px" label-position="right">
        <el-form-item label="料号">
          <el-input v-model.trim="editData.materialNo" size="large" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="保质期（天）">
          <el-input v-model.trim="editData.perifdOfValidity" size="large"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="edit">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
  import Bus from '../../../../utils/bus'
  import {materialTip} from "./../../../../utils/formValidate"
  import {axiosPost} from "./../../../../utils/fetchData"
  import {addMaterialUrl, updateMaterialUrl} from "./../../../../config/globalUrl"
  import {errTip} from './../../../../utils/errorTip'

  export default {
    name: 'materialModal',
    data() {
      return {
        addDialogVisible: false,
        editDialogVisible: false,
        materialInfo: {
          materialNo: '',
          perifdOfValidity: ''
        },
        editData: {
          id: '',
          materialNo: '',
          perifdOfValidity: ''
        },
        isCloseOnModal: false,
        showClose: true
      }
    },
    beforeDestroy() {
      //取消监听
      Bus.$off('addMaterial');
      Bus.$off('editMaterial');
    },
    mounted() {
      //监听物料添加事件
      Bus.$on('addMaterial', () => {
        this.reset();
        this.addDialogVisible = true;
      });
      //监听物料修改事件
      Bus.$on('editMaterial', (editData) => {
        this.editData.id = editData.id;
        this.editData.materialNo = editData.materialNo;
        this.editData.perifdOfValidity = editData.perifdOfValidity;
        this.editDialogVisible = true;
      })
    },
    methods: {
      add: function () {
        let result = materialTip(this.materialInfo);
        if (result !== '') {
          this.$alertWarning(result);
          return;
        }
        let options = {
          url: addMaterialUrl,
          data: {
            materialNo: this.materialInfo.materialNo,
            perifdOfValidity: this.materialInfo.perifdOfValidity,
          }
        };
        axiosPost(options).then(response => {
          if (response.data) {
            let result = response.data.result;
            if (result === "succeed") {
              this.$alertSuccess("添加成功");
              this.reset();
            } else {
              this.$alertWarning(errTip(result));
            }
          }
        }).catch(err => {
          this.$alertError("error:" + JSON.stringify(err));
        });
      },
      edit: function () {
        let result = materialTip(this.editData);
        if (result !== '') {
          this.$alertWarning(result);
          return;
        }
        let options = {
          url: updateMaterialUrl,
          data: {
            id: this.editData.id,
            materialNo: this.editData.materialNo,
            perifdOfValidity: this.editData.perifdOfValidity,
          }
        };
        axiosPost(options).then(response => {
          if (response.data) {
            let result = response.data.result;
            if (result === "succeed") {
              this.$alertSuccess("修改成功");
              this.editDialogVisible = false;
              Bus.$emit('refreshMaterial', true);
            } else {
              this.$alertWarning(errTip(result));
            }
          }
        }).catch(err => {
          this.$alertError("请求接口失败，请先检查网络，再联系管理员");
        });
      },
      close: function () {
        this.addDialogVisible = false;
        Bus.$emit('refreshMaterial', true);
      },
      reset: function () {
        this.materialInfo.materialNo = '';
        this.materialInfo.perifdOfValidity = '';
      }
    }
  }

</script>

<style scoped lang="scss">
</style>
