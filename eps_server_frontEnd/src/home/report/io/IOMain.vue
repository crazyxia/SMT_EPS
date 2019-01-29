<template>
  <div class="io">
    <el-form :inline="true" :model="io" class="demo-form-inline">
      <el-form-item label="仓位">
        <el-input v-model.trim="io.position" placeholder="仓位"></el-input>
      </el-form-item>
      <el-form-item label="供应商">
        <el-input v-model.trim="io.custom" placeholder="供应商"></el-input>
      </el-form-item>
      <el-form-item label="操作员">
        <el-input v-model.trim="io.operator" placeholder="操作员"></el-input>
      </el-form-item>
      <el-form-item label="料号">
        <el-input v-model.trim="io.materialNo" placeholder="料号"></el-input>
      </el-form-item>
      <el-form-item label="起止时间">
        <el-date-picker
          v-model="time"
          type="daterange"
          align="right"
          value-format="yyyy-MM-dd"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="find">查询</el-button>
        <el-button type="info" @click="reset">清空条件</el-button>
      </el-form-item>
    </el-form>
    <IOTable></IOTable>
  </div>
</template>

<script>
  import {mapActions} from 'vuex'
  import Bus from '../../../utils/bus'
  import IOTable from './components/IOTable'
  import {setInitialTime} from "./../../../utils/time"

  export default {
    name: 'io',
    data() {
      return {
        io: {
          position: "",
          custom: "",
          materialNo: "",
          operator: "",
          startTime: "",
          endTime: "",
        },
        time:[],
      }
    },
    components: {
      IOTable
    },
    created() {
      this.time = setInitialTime();
      this.io.startTime = this.time[0] +  " 00:00:00";
      this.io.endTime = this.time[1] +  " 23:59:59";
      this.setIO(JSON.parse(JSON.stringify(this.io)));
    },
    methods: {
      ...mapActions(['setIO']),
      find: function () {
        this.io.startTime = this.time[0] +  " 00:00:00";
        this.io.endTime = this.time[1] +  " 23:59:59";
        this.setIO(JSON.parse(JSON.stringify(this.io)));
        Bus.$emit('findIo',true);
      },
      reset: function () {
        this.time = setInitialTime();
        this.io.startTime = this.time[0] +  " 00:00:00";
        this.io.endTime = this.time[1] +  " 23:59:59";
        this.io.materialNo = '';
        this.io.operator = '';
        this.io.custom = '';
        this.io.position = '';
      }
    }
  }
</script>

<style scoped lang="scss">
  .io {
    padding: 20px;
  }
</style>
