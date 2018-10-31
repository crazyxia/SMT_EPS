<template>
  <div class="modal fade" id="CodePicModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop=”static”>
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h6 class="modal-title" id="myModalLabel">
            工号：{{userId}}
          </h6>
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        </div>
        <div class="modal-body">
          <span ><img :src="imgSrc" alt="敬请期待" /></span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import store from './../../../../store'
import {axiosPost} from "./../../../../utils/fetchData"
import {getCodePicUrl} from "./../../../../config/globalUrl"
import {errTip} from "../../../../utils/errorTip";
export default {
  name:'codePic',
  data () {
    return {
      imgSrc:"",
      result:""
    }
  },
  computed:{
    isGetCodePic:function(){
      return store.state.isGetCodePic;
    },
    userId:function(){
      return store.state.userId;
    },
    token:function(){
      let token = store.state.token;
      if(token === ""){
        token = window.localStorage.getItem("token");
      }
      return token;
    }
  },
  watch:{
    isGetCodePic:function(val){
      if(val === true){
        store.commit("setIsGetCodePic",false);
        this.getCodePic();
      }
    },
    result:function(val){
      if(val !== ""){
        this.imgSrc = window.g.API_URL+val+"?timestamp="+Date.parse(new Date());
        $("#CodePicModal").modal('show');
        this.result = "";
      }
    }
  },
  methods:{
    getCodePic:function(){
      let options = {
        url:getCodePicUrl,
        data:{
          id:store.state.userId,
          '#TOKEN#':this.token
        }
      };
      axiosPost(options).then(response => {
        if (response.data) {
          let result = response.data.result;
          if(result !== "fail_get_img"){
            this.result = result;
          }else{
            errTip(result);
          }
        }
      }).catch(err => {
        alert("error:" + JSON.stringify(err));
      });
    }
  }
}

</script> 

<style scoped lang="scss">
.modal{
  .modal-dialog{
    width:300px;
    position:absolute;
    top:50%;
    left:50%;
    transform:translate(-50%, -50%);
    .modal-body{
      span{
        display:inline-block;
        width:100%;
        height:100%;
        img{
          width:100%;
          height:100%;
        }
      }
    }
  }
}
</style>
