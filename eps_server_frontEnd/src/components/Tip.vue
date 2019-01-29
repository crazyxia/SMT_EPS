<template>
  <div class="tip">
    <p class="col-sm-5">{{msg}}</p>
  </div>
</template>

<script>
export default {
  name: 'tip',
  data () {
    return {
      msg:'欢迎来到SMT防错料管理系统，请通过左侧导航栏进入相关界面进行操作。'
    }
  }
}
</script>

<style scoped lang="scss">
	.tip{
		width:100%;
		height:100%;
		p{
			text-indent:44px;
			padding:200px 20px 0 20px;
			margin:0 auto;
			text-align:center;
			font-size:22px;
		}
	}
	@media screen and (max-Width:600px){
		.tip p{
			font-size:18px;
		}
	}
</style>
