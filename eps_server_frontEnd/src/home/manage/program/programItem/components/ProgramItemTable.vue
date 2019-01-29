<template>
  <div class="programItemTable">
    <el-table
      :data="tableData"
      border
      style="width: 100%">
      <el-table-column
        label="序列号"
        prop="serialNo"
        width="70"
        align="center">
      </el-table-column>
      <el-table-column
        prop="lineseat"
        label="站位"
        width="80"
        align="center">
      </el-table-column>
      <el-table-column
        label="程序料号"
        prop="materialNo"
        width="150"
        align="center">
      </el-table-column>
      <el-table-column
        prop="quantity"
        width="50"
        label="数量"
        align="center">
      </el-table-column>
      <el-table-column
        label="BOM料号/规格"
        prop="specitification"
        align="center">
      </el-table-column>
      <el-table-column
        prop="position"
        label="单板位置"
        align="center">
      </el-table-column>
      <el-table-column
        width="60"
        prop="materialType"
        label="料别"
        align="center">
      </el-table-column>
      <el-table-column
        label="操作"
        align="center">
        <template slot-scope="scope">
          <el-button
            type="primary"
            size="mini"
            @click="add(scope.row)"
            icon="el-icon-plus">
          </el-button>
          <el-button
            type="success"
            size="mini"
            @click="edit(scope.row)"
            icon="el-icon-edit">
          </el-button>
          <el-button
            type="danger"
            size="mini"
            @click="deleteRow(scope.row)"
            icon="el-icon-delete">
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
  import Bus from '../../../../../utils/bus'
  import {mapActions,mapGetters} from 'vuex'
  import {axiosPost} from "./../../../../../utils/fetchData"
  import {programItemListUrl} from "./../../../../../config/globalUrl"

  export default {
    name: 'programItemTable',
    props:{
      id:String
    },
    mounted() {
      this.getList();
    },
    computed:{
      ...mapGetters(['programItemList','operations']),
      tableData:function () {
        return this.programItemList;
      }
    },
    methods: {
      ...mapActions(['setLoading','setProgramItemList','setOperations']),
      fetchData: function (options) {
        axiosPost(options).then(response => {
          this.setLoading(false);
          if (response.data) {
            if (response.data) {
              let result = response.data;
              this.setProgramItemList(this.handleArr(result));
              this.setOperations([]);
            }
          }
        }).catch(err => {
          this.setLoading(false);
          this.$alertError("接口请求失败，请检查网络，再联系管理员");
        });
      },
      //查询
      getList: function () {
        this.setLoading(true);
        let options = {
          url: programItemListUrl,
          data: {
            id: this.id,
            orderBy: 'create_time desc'
          }
        };
        this.fetchData(options);
      },
      //信息处理
      handleArr: function (arr) {
        let result = [];
        for (let i = 0; i < arr.length; i++) {
          if (arr[i].alternative === true) {
            arr[i]["materialType"] = "替料";
          } else {
            arr[i]["materialType"] = "主料";
          }
          arr[i]["index"] = i;
          result.push(arr[i]);
        }
        return result;
      },
      //添加
      add:function(row){
        Bus.$emit('addProgramItem',row);
      },
      //修改
      edit:function(row){
        Bus.$emit('editProgramItem',row);
      },
      //删除
      deleteRow:function(row){
        //刷新
        for(let i = 0;i<this.programItemList.length;i++){
          let item = this.programItemList[i];
          if(item.index === row.index){
            this.programItemList.splice(i,1);
            break;
          }
        }
        for(let i = 0;i<this.programItemList.length;i++){
          this.programItemList[i].index = i;
        }
        //添加到操作列表
        let obj = {
          operation:2,
          targetLineseat:row.lineseat,
          targetMaterialNo:row.materialNo,
          programId:row.programId,
          lineseat:row.lineseat,
          alternative:row.alternative,
          materialNo:row.materialNo,
          specitification:row.specitification,
          position:row.position,
          serialNo:row.serialNo,
          quantity:row.quantity
        };
        this.operations.push(obj);
      }
    }
  }
</script>

<style lang="scss" scoped>
  .programItemTable {
    padding: 24px;
    border: 1px solid #ebebeb;
    border-radius: 3px;
    background: #fff;
  }
</style>
