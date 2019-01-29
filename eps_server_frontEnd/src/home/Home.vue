<template>
  <div class="home">
    <el-container>
      <el-aside :width="asideWidth"><component v-bind:is="leftNavComponent" @getNavInfo="NavInfo"></component></el-aside>
      <el-container>
        <el-header style="height:50px">
          <RightHeader :navTitle="textChange" :iconName="iconName" @isFold="fold"></RightHeader>
        </el-header>
        <el-main>
          <Loading v-if="loading"></Loading>
          <AccessDenied v-if="denied"></AccessDenied>
          <component v-bind:is="rightMainComponent" style="box-sizing:border-box;width:100%;height:100%;padding:20px;"></component>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
  import {mapGetters,mapActions} from 'vuex';
  import Loading from '../components/Loading.vue'
  import LeftNav from '../components/LeftNav.vue'
  import LeftNavTwo from '../components/LeftNavTwo.vue'
  import RightHeader from '../components/RightHeader.vue'
  import Tip from '../components/Tip.vue'
  import User from './manage/user/UserMain.vue'
  import Config from './manage/config/ConfigMain.vue'
  import Program from './manage/program/ProgramMain.vue'
  import Materials from './manage/materials/MaterialsMain.vue'
  import ClientReport from './report/client/ClientMain.vue'
  import OperationReport from './report/operation/OperationMain.vue'
  import IOReport from './report/io/IOMain.vue'
  import AccessDenied from "../components/AccessDenied";

  export default {
    name: 'home',
    data() {
      return {
        textChange: '首页',
        asideWidth:'250px',
        leftNavComponent: 'LeftNav',
        rightMainComponent: 'Tip',
        iconName:'rightArrow'
      }
    },
    watch: {
      rightMainComponent: function (newVal, oldVal) {
        if (newVal !== oldVal) {
          this.setLoading(false);
        }
      }
    },
    created() {
      this.$options.methods.judgeWidth(this);
    },
    computed: {
      ...mapGetters([
        'loading','denied'
      ]),
    },
    mounted() {
      let that = this;
      window.onresize = function temp() {
        that.$options.methods.judgeWidth(that);
      };
      document.getElementsByTagName('html')[0].style.fontSize = 16 + "px";
    },
    components: {
      AccessDenied,
      LeftNav,
      LeftNavTwo,
      Tip,
      RightHeader,
      User,
      Config,
      Program,
      Materials,
      OperationReport,
      ClientReport,
      IOReport,
      Loading
    },
    methods: {
      ...mapActions(['setLoading']),
      fold: function (index) {
        if (index === 1) {
          this.asideWidth = '70px';
          this.iconName = 'leftArrow';
          this.leftNavComponent = 'LeftNavTwo';
        } else if (index === 2) {
          this.asideWidth = '250px';
          this.iconName = 'rightArrow';
          this.leftNavComponent = 'LeftNav';
        }
      },
      NavInfo: function (item) {
        let name = item.name;
        let component = item.component;
        this.textChange = name;
        this.rightMainComponent = component;
      },
      judgeWidth: function (that) {
        let width = window.innerWidth;
        if (width < 1100) {
          that.asideWidth = '70px';
          that.iconName = 'leftArrow';
          that.leftNavComponent = 'LeftNavTwo';
        } else {
          that.asideWidth = '250px';
          that.iconName = 'rightArrow';
          that.leftNavComponent = 'LeftNav';
        }
      }
    }
  }
</script>

<style scoped lang="scss">
  .home {
    width: 100%;
    height: 100%;
    .el-container{
      width:100%;
      height:100%;
      .el-aside{
        background:#fff;
        height:100%;
        box-shadow: 5px 0 5px 0 #ddd;
      }
      .el-header{
        background: #00B7FF;
      }
      .el-main{
        padding:0;
        position:relative;
      }
    }
  }
</style>
