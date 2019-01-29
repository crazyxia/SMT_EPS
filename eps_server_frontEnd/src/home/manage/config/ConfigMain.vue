<template>
  <div class="config">
    <!--表单筛选-->
    <el-form :inline="true" class="demo-form-inline">
      <el-form-item label="产线设置筛选">
        <el-select v-model="line"  value="">
          <el-option label="统一" value="统一"></el-option>
          <el-option v-for="item in lines" :label="item.line" :value="item.line" :key="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="save">保存</el-button>
      </el-form-item>
    </el-form>
    <!--表格-->
    <div class="configTable">
      <el-table
        :data="tableData"
        :span-method="objectSpanMethod"
        border
        style="width: 100%">
        <el-table-column
          label="线号"
          width="80"
          prop="lineName"
          aria-rowspan="4"
          align="center">
        </el-table-column>
        <el-table-column
          prop="alias"
          label="别名"
          width="200"
          align="center">
        </el-table-column>
        <el-table-column
          label="值"
          prop="value"
          width="100"
          align="center">
          <template slot-scope="scope">
            <el-input v-model="scope.row.value"></el-input>
          </template>
        </el-table-column>
        <el-table-column
          label="描述"
          prop="description"
          align="center">
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
  import {mapGetters,mapActions} from 'vuex'
  import {axiosPost} from "./../../../utils/fetchData"
  import {configTip} from "./../../../utils/formValidate"
  import {configListUrl, setConfigInfoUrl} from './../../../config/globalUrl'
  import {errTip} from "../../../utils/errorTip";

  export default {
    name: 'config',
    data() {
      return {
        //线号
        line: "",
        //获取的所有返回值
        result: [],
        //当前线号信息
        items: [],
        //统一
        totalItem: [
          {
            line:"统一",
            lineName: "统一",
            alias: "核料延时时间",
            name: "check_after_change_time",
            value: "",
            description: "换料后超过该时间未核料，会触发报警。单位：分钟"
          },
          {
            line:"统一",
            lineName: "统一",
            alias: "全检周期时间  ",
            name: "check_all_cycle_time",
            value: "",
            description: "该周期时间内未执行全检，会触发报警。单位：分钟"
          },
          {
            line:"统一",
            lineName: "统一",
            alias: "操作员出错时响应",
            name: "operator_error_alarm",
            value: "",
            description: "操作员操作（上料、换料）出错时，系统将执行的操作。0为报警+停接驳台；1为仅报警；2为仅停接驳台；3为无动作"
          },
          {
            line:"统一",
            lineName: "统一",
            alias: "IPQC出错时响应",
            name: "ipqc_error_alarm",
            value: "",
            description: "IPQC操作（核料、全检、首检）出错时，系统将执行的操作。0为报警+停接驳台；1为仅报警；2为仅停接驳台；3为无动作"
          }
        ],
        //表格信息
        tableData: []
      }
    },
    watch: {
      line: function (val) {
        if (val === "统一") {
          for (let i = 0; i < this.totalItem.length; i++) {
            this.totalItem[i].value = "";
          }
        }
        this.getList();
      }
    },
    computed: {
      ...mapGetters(['lines']),
    },
    methods: {
      ...mapActions(['setLoading']),
      //表格跨行
      objectSpanMethod: function ({row, column, rowIndex, columnIndex}) {
        if (columnIndex === 0) {
          if (rowIndex === 0) {
            return [4, 1]
          } else {
            return [0, 0]
          }
        }
      },
      //查询
      getList: function () {
        let options = {
          url: configListUrl,
          data: {}
        };
        axiosPost(options).then(response => {
          if (response.data) {
            this.result = response.data;
            this.$nextTick(function () {
              this.analyzeData();
            })
          }
        }).catch(err => {
          this.$alertError("请求接口失败，请先检查网络，再联系管理员");
        });
      },
      //设置
      setConfigInfo: function () {
        this.setLoading(true);
        let options = {
          url: setConfigInfoUrl,
          data: {
            configs: JSON.stringify(this.result),
          }
        };
        axiosPost(options).then(response => {
          this.setLoading(false);
          if (response.data) {
            let result = response.data.result;
            if (result === "succeed") {
              this.$alertSuccess("设置成功");
            } else {
              this.$alertWarning(errTip(result));
            }
          }
        }).catch(err => {
          this.setLoading(false);
          this.$alertError("请求接口失败，请先检查网络，再联系管理员");
        });
      },
      //解析（设置当前线号内容）
      analyzeData: function () {
        this.items = [];
        let arr = this.result;
        for (let i = 0; i < arr.length; i++) {
          if (arr[i].lineName === this.line) {
            this.items.push(arr[i]);
          }
        }
        if(this.line === "统一"){
          this.items = this.totalItem;
        }
        this.tableData = this.items;
      },
      //保存
      save: function () {
        if (this.line === "") {
          this.$alertWarning("请先选择产线");
          return;
        }
        let result = configTip(this.items);
        if (result !== '') {
          this.$alertWarning(result);
          return;
        }
        for (let i = 0; i < this.result.length; i++) {
          let obj = this.result[i];
          for (let j = 0; j < this.items.length; j++) {
            let item = this.items[j];
            if (obj.line === item.lineName && obj.name === item.name) {
              this.result.splice(i, 1, item);
            }
            if (item.line === "统一" && obj.name === item.name) {
              obj.value = item.value;
            }
          }
        }
        this.setConfigInfo();
      }
    }
  }
</script>

<style scoped lang="scss">
  .configTable {
    padding: 24px;
    border: 1px solid #ebebeb;
    border-radius: 3px;
    background: #fff;
  }
</style>
