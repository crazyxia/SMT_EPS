<template>
  <div class="home">
    <div class="container-fluid">
      <div class="row">
        <component v-bind:is="leftNavComponent" @getNavInfo="NavInfo"></component>
        <div class="right" :class="className">
          <RightHeader :imgSrc="imgChange" :navTitle="textChange" @isFold="foldInfo"></RightHeader>
          <main>
            <Loading v-if="loading"></Loading>
            <AccessDenied v-if="denied"></AccessDenied>
            <component v-bind:is="rightMainComponent"></component>
          </main>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import store from './../store'
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
        imgChange: false,
        textChange: '首页',
        className: 'col-sm-10',
        leftNavComponent: 'LeftNav',
        rightMainComponent: 'Tip'
      }
    },
    watch: {
      rightMainComponent: function (newQuestion, oldQuestion) {
        if (newQuestion != oldQuestion) {
          store.commit("setLoading", false);
        }
      }
    },
    created() {
      this.$options.methods.judgeWidth(this);
    },
    computed: {
      loading: function () {
        return store.state.loading;
      },
      denied:function () {
        return store.state.denied;
      }
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
      foldInfo: function (index) {
        if (index == 1) {
          this.className = 'fold';
          this.imgChange = true;
          this.leftNavComponent = 'LeftNavTwo';
        } else if (index == 2) {
          this.className = 'col-sm-10';
          this.imgChange = false;
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
          that.className = 'fold';
          that.imgChange = true;
          that.leftNavComponent = 'LeftNavTwo';
        } else {
          that.className = 'col-sm-10';
          that.imgChange = false;
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
    .container-fluid {
      padding: 0;
      width: 100%;
      height: 100%;
      .row {
        margin: 0;
        width: 100%;
        min-height: 100%;
        .right {
          padding: 0;
          main {
            width: 100%;
            min-height: calc(100% - 60px);
          }
        }
        div.fold {
          width: calc(100% - 70px);
        }
      }
    }
  }
</style>
