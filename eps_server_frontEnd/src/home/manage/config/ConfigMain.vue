<template>
  <div class="config">
    <form class="form-inline" role="form">
      <div class="form-group">
        <label for="line">产线设置筛选</label>
        <select class="form-control" id="line" v-model.trim="line">
          <option selected="selected" disabled="disabled" style='display:none' value=''></option>
          <option>统一</option>
          <option v-for="item in lines">{{item.line}}</option>
        </select>
      </div>
      <button type="button" class="btn btn_save" @click="save">保存</button>
    </form>
    <div class="table-responsive">
      <table class="table table-bordered">
        <thead>
        <tr>
          <th>线号</th>
          <th>别名</th>
          <th>值</th>
          <th>描述</th>
        </tr>
        </thead>
        <tbody :style="{display:showType}">
        <tr>
          <td rowspan="5" class="line">{{line}}</td>
        </tr>
        <tr v-for="item in items">
          <td>{{item.alias}}</td>
          <td><input type="text" v-model.trim="item.value"></td>
          <td>{{item.description}}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
  import store from './../../../store'
  import {axiosPost} from "./../../../utils/fetchData"
  import {configTip} from "./../../../utils/formValidate"
  import {configListUrl, setConfigInfosUrl} from './../../../config/globalUrl'
  import {errTip} from "../../../utils/errorTip";

  export default {
    name: 'config',
    data() {
      return {
        line: "",
        result: [],
        items: [],
        totalItem: [
          {
            line: "统一",
            alias: "核料延时时间",
            name: "check_after_change_time",
            value: "",
            description: "换料后超过该时间未核料，会触发报警。单位：分钟"
          },
          {
            line: "统一",
            alias: "全检周期时间  ",
            name: "check_all_cycle_time",
            value: "",
            description: "该周期时间内未执行全检，会触发报警。单位：分钟"
          },
          {
            line: "统一",
            alias: "操作员出错时响应",
            name: "operator_error_alarm",
            value: "",
            description: "操作员操作（上料、换料）出错时，系统将执行的操作。0为报警+停接驳台；1为仅报警；2为仅停接驳台；3为无动作"
          },
          {
            line: "统一",
            alias: "IPQC出错时响应",
            name: "ipqc_error_alarm",
            value: "",
            description: "IPQC操作（核料、全检、首检）出错时，系统将执行的操作。0为报警+停接驳台；1为仅报警；2为仅停接驳台；3为无动作"
          }
        ],
        showType: "none"
      }
    },
    watch: {
      line: function (val) {
        if(val === "统一"){
          for(let i =0;i<this.totalItem.length;i++){
            this.totalItem[i].value = "";
          }
        }
        if (val !== "") {
          this.getList();
        }
      }
    },
    computed: {
      lines: function () {
        return store.state.lines;
      }
    },
    methods: {
      getList: function () {
        let options = {
          url: configListUrl,
          data: {}
        };
        axiosPost(options).then(response => {
          if (response.data) {
            let [...result] = response.data;
            this.result = result;
            this.$nextTick(function(){
              this.showType = "table-row-group";
              this.analyzeData();
            })
          }
        }).catch(err => {
          alert("请求接口失败，请先检查网络，再联系管理员");
        });
      },
      setConfigInfos: function () {
        let options = {
          url: setConfigInfosUrl,
          data: {
            configs: JSON.stringify(this.result),
          }
        };
        axiosPost(options).then(response => {
          if (response.data) {
            let result = response.data.result;
            if (result === "succeed") {
              alert("设置成功");
            }else{
              errTip(result);
            }
          }
        }).catch(err => {
          alert("请求接口失败，请先检查网络，再联系管理员");
        });
      },
      analyzeData: function () {
        this.items = [];
        let arr = this.result;
        for (let i = 0; i < arr.length; i++) {
          if (arr[i].lineName === this.line) {
            this.items.push(arr[i]);
          }
        }
        if (this.line === "统一") {
          this.items = this.totalItem;
        }
      },
      save: function () {
        if (this.line === "") {
          alert("请先选择产线");
        } else {
          if(configTip(this.items)){
            for(let i = 0;i<this.result.length;i++){
              let obj = this.result[i];
              for(let j=0;j<this.items.length;j++){
                let item = this.items[j];
                if(obj.line === item.lineName && obj.name === item.name){
                  this.result.splice(i,1,item);
                }
                if(item.line === "统一"&&obj.name === item.name){
                  obj.value = item.value;
                }
              }
            }
            this.setConfigInfos();
          }
        }
      }
    }
  }
</script>

<style scoped lang="scss">
  @import '@/assets/css/common.scss';
</style>
