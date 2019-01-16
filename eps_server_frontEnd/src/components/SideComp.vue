<template>
  <div class="side-comp">
    <div class="side-comp-wrap">
      <div class="side-comp-content">
        <canvas id="side-comp-canvas" width="90" height="140"></canvas>
      </div>
    </div>
  </div>
</template>

<script>
  import store from './../store'
  import {getChartsConf} from "../config/ChartsConfig";
  export default {
    name: "SideComp",
    data() {
      return {}
    },
    computed:{
      lineSize:function(){
        return store.state.lineSize
      },
      lines:function(){
        return store.state.lines;
      }
    },
    mounted() {
      let sideCompCanvas = document.getElementById('side-comp-canvas');
      let config = getChartsConf('horizontalBar',this.lines);
      let updatedConfig = {
        name: '产线任务达成率',
        data: []
      };
      for (let i = 0; i < this.lineSize; i++) {
        let tempNumber = Math.floor(Math.random() * 40);
        updatedConfig.data.push(tempNumber + 100)
      }
      this.initChart(sideCompCanvas, config, updatedConfig);
    },
    methods: {
      initChart: function (ctx, srcConfig, updatedConfig) {
        return new Promise(resolve => {
          let tempConfig = JSON.parse(JSON.stringify(srcConfig));
          tempConfig.options.title.text = updatedConfig.name;
          tempConfig.data.datasets[0].data = updatedConfig.data;
          resolve(new this.Chart(ctx, tempConfig))
        })
      },
    }
  }
</script>

<style scoped>
  .side-comp {
    position: relative;
    width: 4.8rem;
    height: 7.2rem;
    background: #333;
  }

  .side-comp-wrap {
    position: absolute;
    background-color: #333;
    width: 4.5rem;
    height: 6.8rem;
    margin: 0.2rem 0.15rem;
    box-shadow: #222 0.05rem 0.05rem 0.5rem;

  }

  .side-comp-content {
    position: relative;
    width: 4.2rem;
    height: 6.4rem;
    margin: 0.2rem 0.15rem;
  }
</style>
