<template>
  <div class="rightHeader">
    <div>
      <span @click="fold"><icon :name="iconName" scale="2"></icon></span>
      <span>{{navTitle}}</span>
    </div>
    <span @click="close" class="return"><icon name="return" scale="3" ></icon></span>
  </div>
</template>

<script>
export default {
  name:'rightHeader',
  props:{
    iconName:String,
    navTitle:String
  },
  data(){
    return {
      foldWidth:1100
    }
  },
  methods:{
    fold:function(){
      let width = window.innerWidth;
      if(width > this.foldWidth){
        let index = this.iconName === "rightArrow"?1:2;
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
    width:100%;
    height:100%;
    background: #00B7FF;
    display:flex;
    flex-wrap:nowrap;
    align-items:center;
    justify-content:space-between;
    div{
      display:flex;
      flex-wrap:nowrap;
      align-items:center;
      span:first-child{
        cursor:pointer;
      }
      span:last-child{
        font-size:18px;
        color:#fff;
        padding-left:20px;
      }
    }
    span.return{
      cursor:pointer;
    }
  }
</style>
