<template>
  <div class="program">
    <!--站位表列表-->
    <div v-if="isShow">
      <p class="tip">提醒：上传站位表时，若“未开始”的项目列表中存在“版面类型”、“工单号”、“线别”三者一致时，将覆盖上一份“未开始”中的项目</p>
      <el-form :inline="true" :model="programInfo" class="demo-form-inline">
        <el-form-item label="站位表">
          <el-input v-model.trim="programInfo.programName" placeholder="站位表"></el-input>
        </el-form-item>
        <el-form-item label="工单">
          <el-input v-model.trim="programInfo.workOrder" placeholder="工单"></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model.trim="programInfo.state" placeholder="状态" value="">
            <el-option label="不限" selected="selected" value=''></el-option>
            <el-option label="未开始" value='0'></el-option>
            <el-option label="进行中" value='1'></el-option>
            <el-option label="已完成" value='2'></el-option>
            <el-option label="已作废" value='3'></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="线号">
          <el-select v-model.trim="programInfo.line" value="">
            <el-option label="不限" value=""></el-option>
            <el-option v-for="item in lines" :label="item.line" :value="item.id" :key="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="find">查询</el-button>
          <el-button type="primary" @click="download"  :disabled="loginUser.type === 4">下载站位表模板</el-button>
          <el-button type="info" @click="reset">清除条件</el-button>
        </el-form-item>
      </el-form>
      <el-form :inline="true" :model="fileInfo" class="demo-form-inline">
        <el-form-item label="站位表文件">
          <input type="file" style="display:none;" id="fileUpload" @change="handleFileChange"/>
          <el-input id="uploadFile" size="large" @click.native="handleUpload" v-model="fileName"
                    placeholder="请选择站位表上传"></el-input>
        </el-form-item>
        <el-form-item label="版面">
          <el-select v-model.trim="fileInfo.boardType" placeholder="版面" value="">
            <el-option label="默认" value='0'></el-option>
            <el-option label="AB面" value='1'></el-option>
            <el-option label="A面" value='2'></el-option>
            <el-option label="B面" value='3'></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="upload"  :disabled="loginUser.type === 4">上传</el-button>
        </el-form-item>
      </el-form>
      <!--表格-->
      <ProgramTable @setIsShow = 'setIsShow'></ProgramTable>
      <!--修改状态模态框-->
      <ProgramModal></ProgramModal>
    </div>
    <!--站位表表格详情-->
    <ProgramItem v-else  @setIsShow = 'setIsShow'></ProgramItem>
  </div>
</template>

<script>
  import {mapGetters,mapActions} from 'vuex'
  import Bus from '../../../utils/bus'
  import ProgramModal from './components/ProgramModal'
  import ProgramTable from './components/ProgramTable'
  import ProgramItem from './programItem/ProgramItemMain'
  import axios from 'axios'
  import {programFileUploadUrl, programDownloadUrl} from './../../../config/globalUrl'
  import {downloadFile} from "../../../utils/fetchData";

  export default {
    name: 'program',
    data() {
      return {
        //组件切换
        isShow: true,
        //表单查询信息
        programInfo: {
          programName: "",
          workOrder: "",
          state: "",
          line: ""
        },
        //上传文件信息
        fileInfo: {
          programFile: "",
          boardType: ""
        },
        //文件名
        fileName: ''
      }
    },
    components: {
      ProgramTable, ProgramModal, ProgramItem
    },
    created(){
      this.setProgram(JSON.parse(JSON.stringify(this.programInfo)));
    },
    computed: {
      ...mapGetters(['lines', 'token','loginUser'])
    },
    watch: {
      //监听文件名
      fileName: function (val) {
        if (val === '') {
          this.fileInfo.programFile = '';
        }
      }
    },
    methods: {
      ...mapActions(['setProgram']),
      //查询
      find: function () {
        this.setProgram(JSON.parse(JSON.stringify(this.programInfo)));
        Bus.$emit('findProgram', true);
      },
      //上传
      upload: function () {
        //判断
        if (this.fileInfo.programFile === '') {
          this.$alertWarning('请选择文件');
          return;
        }
        if (this.fileInfo.boardType === '') {
          this.$alertWarning('请选择版面类型');
          return;
        }
        let fileType = this.fileName.substr(this.fileName.lastIndexOf(".") + 1);
        if (fileType !== "xls" && fileType !== 'xlsx') {
          this.$alertWarning("文件格式错误");
          return;
        }
        //上传请求
        let param = new FormData();
        param.append('programFile', this.fileInfo.programFile);
        param.append('boardType', this.fileInfo.boardType);
        param.append('#TOKEN#', this.token);
        let config = {
          headers: {'Content-Type': 'multipart/form-data'}
        };
        axios.post(programFileUploadUrl, param, config).then(response => {
          if (response.data) {
            let result = response.data.result;
            let data = response.data.data;
            if (result === "200") {
              this.$alertSuccess(data);
              this.find();
            } else {
              this.$alertWarning(data);
            }
            this.fileInfo.programFile = '';
            this.fileInfo.boardType = '';
            this.fileName = '';
          }
        });
      },
      //重置表单
      reset: function () {
        this.programInfo.programName = '';
        this.programInfo.workOrder = '';
        this.programInfo.state = '';
        this.programInfo.line = '';
      },
      //文件选择后点击确定事件
      handleUpload: function () {
        let file = document.getElementById('fileUpload');
        file.value = null;
        file.click();
      },
      //文件名改变
      handleFileChange: function () {
        let files = document.getElementById('fileUpload');
        let file = files.files[0];
        this.fileInfo.programFile = file;
        this.fileName = file.name;
      },
      //下载
      download: function () {
        let data = {
          '#TOKEN#': this.token
        };
        downloadFile(programDownloadUrl, data);
      },
      //切换显示
      setIsShow:function(val){
        this.isShow = val;
      }
    }
  }
</script>

<style scoped lang="scss">
  .tip {
    font-size: 15px;
    color: red;
    border-bottom: 1px solid #eee;
    margin-bottom: 10px;
    padding-bottom: 10px;
  }
  .program {
    padding: 20px;
  }
</style>
