<template>
  <div class="rightHeader">
    <div>
      <span @click="fold"><icon :name="img" scale="3"></icon></span>
      <span>{{message}}</span>
    </div>
    <span @click="close" class="return"><icon name="return" scale="4" ></icon></span>
  </div>
</template>

<script>
export default {
  name:'rightHeader',
  props:['imgSrc','navTitle'],
  data(){
    return {
      message:"首页",
      img:"rightArrow",
      foldWidth:1100
    }
  },
  watch:{
    imgSrc:function(newQuestion, oldQuestion) {
      if(newQuestion){
        this.img = "leftArrow";
      }else{
        this.img = "rightArrow";
      }
    },
    navTitle:function(newQuestion, oldQuestion){
      this.message = newQuestion;
    }
  },
  methods:{
    fold:function(){
      let width = window.innerWidth;
      if(width > this.foldWidth){
        let index = 0;
        if(this.img == "rightArrow"){
          index = 1;    // 折叠
        }else{
          index = 2;    // 弹出
        }
        this.$emit("isFold",index);
      }
    },
    close:function(){
      this.$router.replace("login");
    }
  }
}
</script>

<style scoped lang="scss">
  .rightHeader{
    background:#188AE2;
    display:flex;
    flex-wrap:nowrap;
    align-items:center;
    justify-content:space-between;
    padding:0 20px;
    height:60px;
    div{
      display:flex;
      flex-wrap:nowrap;
      align-items:center;
      span:first-child{
        cursor:pointer;
      }
      span:last-child{
        font-size:20px;
        color:#fff;
        padding-left:20px;
      }
    }
    span.return{
      cursor:pointer;
    }
  }
</style>
