<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
  import {mapActions} from 'vuex';
  import {lineListUrl} from './config/globalUrl'
  import {lineStatusData, lineAllDayData} from './utils/displayDataInit'

  export default {
    name: 'App',
    created() {
      this.getLines();
    },
    methods: {
      ...mapActions(['setLineData', 'setAllDayData', 'setLines', 'setLineSize']),
      getLines: function () {
        let that = this;
        $.ajax({
          url: lineListUrl,
          type: 'POST', //GET
          async: false,    //或false,是否异步
          timeout: 12000,    //超时时间
          dataType: 'json',    //返回的数据格式：
          beforeSend: function (xhr) {
          },
          success: function (data, textStatus, jqXHR) {
            that.setLines(data);
            that.setLineSize(data.length);
            that.setAllDayData(lineAllDayData());
            that.setLineData(lineStatusData());
          },
          error: function (xhr, textStatus) {
          },
          complete: function () {
          }
        })
      }
    }
  }
</script>

<style lang="scss">
  html, body {
    width: 100%;
    height: 100%;
    border: none;
    background: url("../static/img/bg.png");
  }

  html, body, div, p, ul, li {
    margin: 0;
    padding: 0;
  }

  header, main {
    display: block;
  }

  input, select, button {
    border: none;
    outline: none;
    box-shadow: none;
  }

  ul, li {
    list-style: none;
  }

  #app {
    font-family: '微软雅黑', 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    color: #2c3e50;
    width: 100%;
    height: 100%;
    font-size:16px;
  }

  .el-dialog{
    position:absolute;
    top:50%;
    left:50%;
    margin:0 !important;
    transform:translate(-50%, -50%);
  }
  .el-dialog__body{
    padding:20px 30px 15px 20px;
    font-size:20px;
  }
  .el-dialog__title{
    font-weight:bold;
  }
  .el-dialog__footer{
    padding-bottom:10px;
  }
</style>
