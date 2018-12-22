<template>
  <div class="login">
    <header>
      <span>SMT防错料管理系统</span>
    </header>
    <main class="container">
      <form class="form" v-on:submit.prevent="login">
        <div class="form-group">
          <label for="id">工号</label>
          <input type="text" name="id" id="id" v-model.trim="loginInfos.id">
        </div>
        <div class="form-group">
          <label for="password">密码</label>
          <input type="password" name="password" id="password" v-model.trim="loginInfos.password">
        </div>
        <div class="form-group">
          <button>登录</button>
        </div>
      </form> 
    </main>
  </div>
</template>

<script>
import store from './../../../store' 
import {errTip} from './../../../utils/errorTip' 
import {axiosPost} from "./../../../utils/fetchData"
import {loginUrl} from "./../../../config/globalUrl"
export default {
  name: 'login',
  data () {
    return {
      loginInfos:{
        id:"",
        password:""
      }
    }
  },
  methods:{
    login:function(){
      if(this.loginInfos.id !== ""){
        let loginOptions = {
          url:loginUrl,
          data:{
            id:this.loginInfos.id,
            password:this.loginInfos.password
          }
        };
        axiosPost(loginOptions).then(response => {
          if (response.data) {
            let result = response.data.result;
            if(result === "200"){
              let obj = response.data.data;
              window.localStorage.setItem("token", obj.tokenId);
              store.commit("setToken", obj.tokenId);
              this.$router.push('home');
            }else{
              errTip(result);
            }
          }
        }).catch(err => {
          alert("网络问题，请检查网络连接或联系管理员");
        });
      }else{
        alert("工号不能为空");
      }
    }
  }
}
</script>

<style scoped lang="scss">
  .login{
    width:100%;
    height:100%;
    header{
      position:fixed;
      z-index:999;
      width:100%;
      height:60px;
      background:#188AE2;
      span{
        display:inline-block;
        width:300px;
        text-align:left;
        font-size:25px;
        color:#fff;
        margin:18px 0 0 70px;
      }
    }
    main{
      form{
        width:350px;
        text-align:center;
        position:absolute;
        top:calc((100% + 60px)/2);
        left:50%;
        transform:translate(-50%, -50%);
        div.form-group{
          width:100%;
          text-align:right;
          margin-bottom:20px;
          label{
            font-weight:normal;
            font-size:20px;
            padding-right:10px;
          }
          input{
            border:1px solid #ccc;
            width:280px;
            font-size:18px;
            line-height:35px;
            padding:5px 0 5px 15px;
            border-radius:22px;
            background:#fff;
          }
          button{
            cursor:pointer;
            width:280px;
            padding:10px 0;
            color: #188AE2;
            background:#fff;
            border:1px solid #188AE2;
            font-size:18px;
            border-radius:22px;
          }
          button:hover{
            background:#188AE2;
            color: #fff;
          }
        }
      }
    }
  }
</style>
