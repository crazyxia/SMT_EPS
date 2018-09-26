<template>
  <div class="display">
    <div class="up-container">
      <dashboard/>
      <side-comp/>
    </div>
    <div class="bottom-container">
      <bottom-comp/>
    </div>
  </div>
</template>

<script>
  import store from './../../store'
  import Chart from 'chart.js'
  import {axiosPost} from "./../../utils/fetchData";
  import {getAllStatusDetails, getLineData} from "./../../config/globalUrl";
  import Dashboard from './../../components/Dashboard'
  import BottomComp from './../../components/BottomComp'
  import SideComp from './../../components/SideComp'
  let debounce = require('lodash.debounce');

  export default {
    name: 'display',
    data() {
      return {
        mark: 0,
      };
    },
    components: {
      Dashboard,
      BottomComp,
      SideComp
    },
    created(){
      this.initChart();
      if(this.lines.length !== 0){
        this.getData(this.mark,this.lines);
        this.mark++;
      }
      window.mainInterval = setInterval(() => {
          this.getData(this.mark,this.lines);
          this.mark++;
          if (this.mark === this.lineSize) {
            this.mark = 0;
          }
        }, 6000);
    },
    mounted() {
      let display = $('.display');
      if(display.length>0){
        this.pixelCalc();
        let that = this;
        window.onresize = debounce(function (){
          that.pixelCalc()
        }, 200)
      }else{
        document.getElementsByTagName('html')[0].style.fontSize = 18 + "px";
      }
    },
    computed:{
      lines:function(){
        return store.state.lines;
      },
      lineSize:function(){
        return store.state.lineSize;
      }
    },
    methods:{
      pixelCalc: function () {
        document.getElementsByTagName('html')[0].style.fontSize = (window.innerWidth / 1920) * 625 + '%'
      },
      initChart:function(){
        Chart.pluginService.register({
          beforeRender: function (chart) {
            if (chart.config.options.showAllTooltips) {
              chart.pluginTooltips = [];
              chart.config.data.datasets.forEach(function (dataset, i) {
                chart.getDatasetMeta(i).data.forEach(function (sector, j) {
                  chart.pluginTooltips.push(new Chart.Tooltip({
                    _chart: chart.chart,
                    _chartInstance: chart,
                    _data: chart.data,
                    _options: chart.options.tooltips,
                    _active: [sector]
                  }, chart));
                });
              });
              chart.options.tooltips.enabled = false;
            }
          },
          afterDraw: function (chart, easing) {
            if (chart.config.options.showAllTooltips) {
              if (!chart.allTooltipsOnce) {
                if (easing !== 1)return;
                chart.allTooltipsOnce = true;
              }
              
              // turn on tooltips
              chart.options.tooltips.enabled = true;
              Chart.helpers.each(chart.pluginTooltips, function (tooltip) {
                tooltip.initialize();
                tooltip.update();
                // we don't actually need this since we are not animating tooltips
                tooltip.pivot();
                tooltip.transition(easing).draw();
              });
              chart.options.tooltips.enabled = false;
            }
          }
        })
      },
      getData:function(thisMark,lines){
        let hourOptions = {
          url: getAllStatusDetails
        };
        axiosPost(hourOptions).then(response => {
          if (response.data) {
            store.commit('setAllDayData', {data: response.data})
          }
        }).catch(err => {
          
        });
            
        let lineOptions = {
          url: getLineData,
          data: {
            line:lines[thisMark].id
          }
        };
        axiosPost(lineOptions).then(response => {
          if (response.data) {
            store.commit('setLineData', {data: response.data})
          }
        }).catch(err => {
         
        });
      }
    }
  }
</script>

<style scoped>
  .display {
    width:19.2rem;
    background: #333;
  }
  .up-container {
    display: flex;
  }
</style>
