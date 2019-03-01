<template>
  <div class="login">
    <header>
      <span>SMT防错料管理系统</span>
    </header>
    <main class="container">
      <el-form ref="form" :model="loginInfo" label-width="50px">
        <el-form-item label="工号">
          <el-input v-model.trim="loginInfo.id"  placeholder="工号"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model.trim="loginInfo.password" placeholder="密码" type="password"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="login" style="width:100%;background:#00B7FF;border:none">登录</el-button>
        </el-form-item>
      </el-form>
    </main>
  </div>
</template>

<script>
  import {mapActions} from 'vuex';
  import {errTip} from './../../../utils/errorTip'
  import {axiosPost} from "./../../../utils/fetchData"
  import {loginUrl} from "./../../../config/globalUrl"

  export default {
    name: 'login',
    data() {
      return {
        //登录信息
        loginInfo: {
          id: "",
          password: ""
        }
      }
    },
    methods: {
      ...mapActions(['setToken','setLoginUser']),
      //登录
      login: function () {
        //判断
        if (this.loginInfo.id === "") {
          this.$alertWarning("工号不能为空");
          return;
        }
        if(this.loginInfo.password === ""){
          this.$alertWarning("密码不能为空");
          return;
        }
        //登录请求
        let loginOptions = {
          url: loginUrl,
          data: {
            id: this.loginInfo.id,
            password: this.loginInfo.password
          }
        };
        axiosPost(loginOptions).then(response => {
          if (response.data) {
            let result = response.data.result;
            if (result === "200") {
              let obj = response.data.data;
              this.setLoginUser(obj);
              window.localStorage.setItem("user",JSON.stringify(obj));
              window.localStorage.setItem("token", obj["tokenId"]);
              this.setToken(obj["tokenId"]);
              this.$router.push('home');
            } else {
              this.$alertWarning(errTip(result));
            }
          }
        }).catch(err => {
          this.$alertError("网络问题，请检查网络连接或联系管理员");
        });
      }
    }
  }
</script>

<style scoped lang="scss">
  .login {
    width: 100%;
    height: 100%;
    header {
      position: fixed;
      z-index: 999;
      width: 100%;
      height: 60px;
      background: #00B7FF;
      span {
        display: inline-block;
        width: 300px;
        text-align: left;
        font-size: 22px;
        color: #fff;
        margin: 18px 0 0 70px;
      }
    }
    main {
      width: 350px;
      position: absolute;
      top: calc((100% + 60px) / 2);
      left: 50%;
      transform: translate(-50%, -50%);
      padding:40px 30px 10px 20px;
      border:1px solid #ccc;
      border-radius:4px;
      background:#fff;
      .el-form{
        width:100%;
        text-align:center;
        .el-form-item{
          label.el-form-item__label{
            font-size:16px;
          }
        }
      }
    }
  }
</style>
