<template>
  <div>
    <!--添加-->
    <el-dialog
      title="添加人员"
      @close="close"
      :close-on-click-modal="isCloseOnModal"
      :close-on-press-escape="isCloseOnModal"
      :visible.sync="addDialogVisible"
      width="400px">
      <el-form label-width="50px" label-position="right">
        <el-form-item label="工号">
          <el-input v-model.trim="userInfo.id" size="large"></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model.trim="userInfo.name" size="large"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model.trim="userInfo.password" size="large"></el-input>
        </el-form-item>
        <el-form-item label="岗位">
          <el-select v-model.trim="userInfo.type" value="" style="width:100%">
            <el-option label="仓库操作员" value='0'></el-option>
            <el-option label="厂线操作员" value='1'></el-option>
            <el-option label="IPQC" value='2'></el-option>
            <el-option label="超级管理员" value='3'></el-option>
            <el-option label="生产管理员" value='4'></el-option>
            <el-option label="品质管理员" value='5'></el-option>
            <el-option label="工程管理员" value='6'></el-option>
            <el-option label="仓库管理员" value='7'></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="班别">
          <el-select v-model.trim="userInfo.classType" style="width:100%" value="">
            <el-option label="白班" value='0'></el-option>
            <el-option label="夜班" value='1'></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="add">确 定</el-button>
      </span>
    </el-dialog>
    <!--修改-->
    <el-dialog
      title="修改人员"
      :close-on-click-modal="isCloseOnModal"
      :close-on-press-escape="isCloseOnModal"
      :visible.sync="editDialogVisible"
      width="400px">
      <el-form label-width="50px" label-position="right">
        <el-form-item label="工号">
          <el-input v-model.trim="editData.id" size="large" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model.trim="editData.name" size="large"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model.trim="editData.password" size="large"></el-input>
        </el-form-item>
        <el-form-item label="岗位">
          <el-select v-model.trim="editData.type" value="" style="width:100%">
            <el-option label="仓库操作员" value='0'></el-option>
            <el-option label="厂线操作员" value='1'></el-option>
            <el-option label="IPQC" value='2'></el-option>
            <el-option label="超级管理员" value='3'></el-option>
            <el-option label="生产管理员" value='4'></el-option>
            <el-option label="品质管理员" value='5'></el-option>
            <el-option label="工程管理员" value='6'></el-option>
            <el-option label="仓库管理员" value='7'></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="班别">
          <el-select v-model.trim="editData.classType" value="" style="width:100%">
            <el-option label="白班" value='0'></el-option>
            <el-option label="夜班" value='1'></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="在职">
          <el-select v-model.trim="editData.enabled" placeholder="是否在职" style="width:100%" value="">
            <el-option label="是" value='1'></el-option>
            <el-option label="否" value='0'></el-option>
          </el-select>
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
  import {mapGetters} from 'vuex'
  import Bus from '../../../../utils/bus'
  import {userTip} from "./../../../../utils/formValidate"
  import {axiosPost} from "./../../../../utils/fetchData"
  import {addUserUrl, updateUserUrl} from "./../../../../config/globalUrl"
  import {errTip} from './../../../../utils/errorTip'

  export default {
    name: 'userModal',
    data() {
      return {
        //点击 (modal || esc) 不退出
        isCloseOnModal: false,
        //添加模态框
        addDialogVisible: false,
        //修改模态框
        editDialogVisible: false,
        //添加信息
        userInfo: {
          id: '',
          name: '',
          type: '',
          classType: '',
          password: '',
          enabled: '1'
        },
        //修改信息
        editData: {
          id: '',
          name: '',
          type: '',
          password: '',
          classType: '',
          enabled: ''
        }
      }
    },
    beforeDestroy() {
      //取消监听
      Bus.$off('addUser');
      Bus.$off('editUser');
    },
    mounted() {
      //监听添加人员事件
      Bus.$on('addUser', () => {
        this.reset();
        this.addDialogVisible = true;
      });
      //监听修改人员事件
      Bus.$on('editUser', (editData) => {
        this.editData.id = editData.id;
        this.editData.name = editData.name;
        this.editData.type = editData.type + '';
        this.editData.password = editData.password;
        this.editData.classType = editData.classType + '';
        this.editData.enabled = editData.enabled === true ? '1' : '0';
        this.editDialogVisible = true;
      })
    },
    computed:{
      ...mapGetters(['loginUser'])
    },
    methods: {
      //添加
      add: function () {
        //判断
        let result = userTip(this.userInfo);
        if (result !== '') {
          this.$alertWarning(result);
          return;
        }
        //添加请求
        let options = {
          url: addUserUrl,
          data: {
            id: this.userInfo.id,
            name: this.userInfo.name,
            type: this.userInfo.type,
            classType: this.userInfo.classType,
            password: this.userInfo.password
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
      //修改
      edit: function () {
        //判断
        let result = userTip(this.editData);
        if (result !== '') {
          this.$alertWarning(result);
          return;
        }
        //修改请求
        let options = {
          url: updateUserUrl,
          data: {
            id: this.editData.id,
            name: this.editData.name,
            type: this.editData.type,
            classType: this.editData.classType,
            password: this.editData.password,
            enabled: this.editData.enabled
          }
        };
        axiosPost(options).then(response => {
          if (response.data) {
            let result = response.data.result;
            if (result === "succeed") {
              this.editDialogVisible = false;
              //判断是否自我离职
              if(this.loginUser.id === this.editData.id && this.editData.enabled === "0"){
                this.$alertInfo('你已离职');
                this.$router.replace('/login');
              }else{
                this.$alertSuccess("修改成功");
                Bus.$emit('refreshUser', true);
              }
            } else {
              this.$alertWarning(errTip(result));
            }
          }
        }).catch(err => {
          this.$alertError("error:" + JSON.stringify(err));
        });
      },
      //关闭添加弹窗
      close: function () {
        this.addDialogVisible = false;
        Bus.$emit('refreshUser', true);
      },
      //重置
      reset: function () {
        this.userInfo.id = '';
        this.userInfo.name = '';
        this.userInfo.type = '';
        this.userInfo.classType = '';
        this.userInfo.password = '';
        this.userInfo.enabled = '1';
      }
    }
  }

</script>

<style scoped lang="scss">
</style>
