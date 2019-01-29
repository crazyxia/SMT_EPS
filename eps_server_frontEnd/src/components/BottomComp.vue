<template>
  <div class="bottom-comp">
    <div class="comp-wrap" v-for="item in operationGroup">
      <div class="bottom-left-comp">
        <div class="pointer">
          <span>{{lines[mark].line}}线: {{item.name}}</span>
        </div>
        <div class="bottom-comp-style" :class="'bottom-comp-' + item.value">
          <canvas :id="'bottom-comp-' + item.value" width="100" height="100"></canvas>
        </div>
      </div>
      <div class="bottom-comp-tips">
        <p>● 成功数: {{nowData[item.value].success}}</p>
        <p>◆ 失败数: {{nowData[item.value].failure}}</p>
        <p>● 成功率: {{nowData[item.value].rate}}%</p>
      </div>
    </div>
  </div>

</template>

<script>
  import {mapGetters} from 'vuex';
  import {getChartsConf} from "../config/ChartsConfig";

  export default {
    name: "BottomComp",
    data() {
      return {
        mark: 0,
        operationGroup: [
          {
            name: '上料',
            value: 'feed'
          },
          {
            name: '换料',
            value: 'change'
          },
          {
            name: '核料',
            value: 'some'
          },
          {
            name: '全检',
            value: 'alls'
          }
        ],
        nowData: {
          feed: {},
          change: {},
          alls: {},
          some: {}
        }
      }
    },
    computed:{
      ...mapGetters(['lineSize','lines','lineAllDayData'])
    },
    mounted() {
      /*获取对应canvas*/
      let ctxFeed = document.getElementById("bottom-comp-feed");
      let ctxChange = document.getElementById("bottom-comp-change");
      let ctxSome = document.getElementById("bottom-comp-some");
      let ctxAlls = document.getElementById("bottom-comp-alls");

      let feedChart, changeChart, someChart, allsChart;

      /*获取配置(copy)*/
      let pieConf = JSON.parse(JSON.stringify(getChartsConf("pie")));

      let updatedData = this.getUpdatedConf(this.mark);

      /*上料初始化*/
      let updateFeedConf = {
        name: "Rate in the Past 24 Hours",
        data: [updatedData.feedSuc, updatedData.feedFail],
      };
      this.nowData.feed = {
        success: updatedData.feedSuc,
        failure: updatedData.feedFail,
        rate: updatedData.feedSucRate
      };
      this.initChart(ctxFeed, pieConf, updateFeedConf).then(value => {
        feedChart = value;
      });

      /*换料初始化*/
      let updateChangeConf = {
        name: "Rate in the Past 24 Hours",
        data: [updatedData.changeSuc, updatedData.changeFail],
      };
      this.nowData.change = {
        success: updatedData.changeSuc,
        failure: updatedData.changeFail,
        rate: updatedData.changeSucRate
      };
      this.initChart(ctxChange, pieConf, updateChangeConf).then(value => {
        changeChart = value;
      });

      /*核料初始化*/
      let updateSomeConf = {
        name: "Rate in the Past 24 Hours",
        data: [updatedData.someSuc, updatedData.someFail],
      };
      this.nowData.some = {
        success: updatedData.someSuc,
        failure: updatedData.someFail,
        rate: updatedData.someSucRate
      };
      this.initChart(ctxSome, pieConf, updateSomeConf).then(value => {
        someChart = value;
      });

      /*全检初始化*/
      let updateAllsConf = {
        name: "Rate in the Past 24 Hours",
        data: [updatedData.allsSuc, updatedData.allsFail],
      };
      this.nowData.alls = {
        success: updatedData.allsSuc,
        failure: updatedData.allsFail,
        rate: updatedData.allsSucRate
      };
      this.initChart(ctxAlls, pieConf, updateAllsConf).then(value => {
        allsChart = value;
      });

      setInterval(() => {
        let updatedData = this.getUpdatedConf(this.mark);
        let updateFeedConf = {
          data: [updatedData.feedSuc, updatedData.feedFail],
        };
        this.nowData.feed = {
          success: updatedData.feedSuc,
          failure: updatedData.feedFail,
          rate: updatedData.feedSucRate
        };

        let updateChangeConf = {
          data: [updatedData.changeSuc, updatedData.changeFail],
        };
        this.nowData.change = {
          success: updatedData.changeSuc,
          failure: updatedData.changeFail,
          rate: updatedData.changeSucRate
        };

        let updateSomeConf = {
          data: [updatedData.someSuc, updatedData.someFail],
        };
        this.nowData.some = {
          success: updatedData.someSuc,
          failure: updatedData.someFail,
          rate: updatedData.someSucRate
        };

        let updateAllsConf = {
          data: [updatedData.allsSuc, updatedData.allsFail],
        };
        this.nowData.alls = {
          success: updatedData.allsSuc,
          failure: updatedData.allsFail,
          rate: updatedData.allsSucRate
        };
        this.updateChart(feedChart, updateFeedConf);
        this.updateChart(changeChart, updateChangeConf);
        this.updateChart(someChart, updateSomeConf);
        this.updateChart(allsChart, updateAllsConf);


        this.mark ++;
        if (this.mark === this.lineSize) {
          this.mark = 0;
        }
      }, 10000)

    },
    methods: {
      getUpdatedConf: function (index) {
          return this.lineAllDayData[index];
      },
      initChart: function (ctx, srcConfig, updatedConfig) {
        return new Promise(resolve => {
          let tempConfig = JSON.parse(JSON.stringify(srcConfig));
          tempConfig.options.title.text = updatedConfig.name;
          tempConfig.data.datasets[0].data = updatedConfig.data;
          resolve(new this.Chart(ctx, tempConfig))
        })
      },
      updateChart: function (chart, config) {
        chart.data.datasets[0].data = config.data;
        chart.update()
      }
    }
  }
</script>

<style scoped>
  .bottom-comp {
    width:100%;
    height: 3.6rem;
    background: #333333;
    display: flex;

  }


  .comp-wrap {
    position: relative;
    background-color: #333333;
    width: 4.5rem;
    height: 3.2rem;
    margin: 0.2rem 0.15rem;
    box-shadow: #222 0.05rem 0.05rem 0.5rem;
  }

  .pointer {
    width: 2.6rem;
    height: 0.4rem;
    padding-top: 0.15rem;
    position: absolute;
    background: #0FD2FE;
    color: white;
    font-size: 0.11rem;
    letter-spacing: 0.2em;
    text-align: center;
    text-transform: uppercase;
  }

  .pointer:after {
    content: "";
    position: absolute;
    left: 0;
    bottom: 0;
    width: 0;
    height: 0;
    border-bottom: 0.08rem solid #333;
    border-left: 1.3rem solid transparent;
    border-right: 1.3rem solid transparent;
  }

  .pointer span {
    color: #fff;
    font-size: 0.2rem;
    line-height: 0.16rem;
    font-weight: normal;
  }

  .bottom-comp-style {
    margin-top: 0.5rem;
    height: 2.6rem;
    width: 2.6rem
  }

  .bottom-left-comp {
    position: absolute;
    height: 3.2rem;
  }

  .bottom-comp-tips {
    position: absolute;
    margin-top: 0.8rem;
    margin-left: 0.2rem;
    height: 2.4rem;
    width: 1.6rem;
    right: 0;
  }
  .bottom-comp-tips p {
    text-align: left;
    display: block;
    font-size: 0.15rem;
    line-height: 0.36rem;
    color: #fff;
  }
</style>
