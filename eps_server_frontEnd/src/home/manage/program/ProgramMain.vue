<template>
  <div class="program">
    <div class="programInfo" v-if="isShow">
      <form class="form-inline" role="form">
        <div class="form-group">
          <label for="programName">站位表</label>
          <input type="text" class="form-control" id="programName" v-model.trim="programInfos.programName">
        </div>
        <div class="form-group">
          <label for="workOrder">工单</label>
          <input type="text" class="form-control" id="workOrder" v-model.trim="programInfos.workOrder">
        </div>
        <div class="form-group">
          <label for="state">状态</label>
          <select class="form-control" id="state" v-model.trim="programInfos.state">
            <option value="" selected="selected">不限</option>
            <option value="0">未开始</option>
            <option value="1">进行中</option>
            <option value="2">已完成</option>
            <option value="3">已作废</option>
          </select>
        </div>
        <div class="form-group">
          <label for="line">线号</label>
          <select class="form-control" id="line" v-model.trim="programInfos.line">
            <option value="" selected="selected">不限</option>
            <option v-for="item in lines" :value="item.id">{{item.line}}</option>
          </select>
        </div>
        <button type="button" class="btn btn_find" @click="find">查询</button>
      </form>
      <form class="form-inline" role="form">
        <div class="form-group">
          <input type="file" id="inputfile" @change="getfileName()"/>
        </div>
        <div class="form-group">
          <label for="boardType">版面</label>
          <select class="form-control" id="boardType" v-model="fileInfos.boardType">
            <option selected="selected" disabled="disabled" style='display: none' value=''></option>
            <option value="0">默认</option>
            <option value="1">AB面</option>
            <option value="2">A面</option>
            <option value="3">B面</option>
          </select>
        </div>
        <button type="button" class="btn btn_send" @click="upload">上传</button>
      </form>

      <ProgramTable :programInfos="programInfos"></ProgramTable>

      <ProgramModal></ProgramModal>
    </div>
    <ProgramItem v-else></ProgramItem>
  </div>
</template>

<script>
  import store from './../../../store'
  import ProgramModal from './components/ProgramModal'
  import ProgramTable from './components/ProgramTable'
  import ProgramItem from './programItem/ProgramItemMain'
  import Vue from 'vue'
  import axios from 'axios'
  import {axiosPost} from "./../../../utils/fetchData"
  import {programFileUploadUrl} from './../../../config/globalUrl'

  export default {
    name: 'program',
    data() {
      return {
        isShow: true,
        programInfos: {
          programName: "",
          workOrder: "",
          state:"",
          line: ""
        },
        fileInfos: {
          programFile: "",
          boardType: ""
        }
      }
    },
    mounted() {
      store.commit("setProgramItemShow", false);
    },
    components: {
      ProgramTable, ProgramModal, ProgramItem
    },
    computed: {
      programItemShow: function () {
        return store.state.programItemShow;
      },
      lines: function () {
        return store.state.lines;
      }
    },
    watch: {
      programItemShow: function (val) {
        if (val === true) {
          this.isShow = false;
        } else {
          this.isShow = true;
        }
      }
    },
    methods: {
      getfileName: function () {
        let fileName = $('#inputfile').val();
        let filetype = fileName.substr(fileName.lastIndexOf(".") + 1);
        if (filetype !== "xls" && filetype !== 'xlsx') {
          alert("文件格式错误");
          $('#inputfile').val();
        } else {
          let programFile = $('#inputfile')[0].files[0];
          this.fileInfos.programFile = programFile;
        }
      },
      find: function () {
        store.commit("setIsFind", true);
      },
      upload: function () {
        if (this.fileInfos.programFile !== "" && this.fileInfos.boardType !== "") {
          let param = new FormData();
          param.append('programFile', this.fileInfos.programFile);
          param.append('boardType', this.fileInfos.boardType);
          param.append('#TOKEN#', store.state.token);
          let config = {
            headers: {'Content-Type': 'multipart/form-data'}
          };
          axios.post(programFileUploadUrl, param, config).then(response => {
            if (response.data) {
              let result = response.data.result;
              alert(result);
              if (result === "上传完成，共解析到1张表") {
                store.commit("setIsUploadFinish", true);
              }
            }
          });
        } else {
          alert("文件格式错误或者版面未选择");
        }
      }
    }
  }
</script>

<style scoped lang="scss">
  @import '@/assets/css/common.scss';
</style>
