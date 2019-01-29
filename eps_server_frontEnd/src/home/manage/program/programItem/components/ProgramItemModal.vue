<template>
  <div>
    <!--添加-->
    <el-dialog
      title="添加信息"
      :close-on-click-modal="isCloseOnModal"
      :close-on-press-escape="isCloseOnModal"
      :visible.sync="addDialogVisible"
      width="400px">
      <el-form label-width="120px" label-position="right">
        <el-form-item label="序列号">
          <el-input v-model.trim="programItem.serialNo" size="large" ></el-input>
        </el-form-item>
        <el-form-item label="站位">
          <el-input v-model.trim="programItem.lineseat" size="large" ></el-input>
        </el-form-item>
        <el-form-item label="程序料号">
          <el-input v-model.trim="programItem.materialNo" size="large" ></el-input>
        </el-form-item>
        <el-form-item label="数量">
          <el-input v-model.trim="programItem.quantity" size="large" ></el-input>
        </el-form-item>
        <el-form-item label="料别">
          <el-select v-model.trim="programItem.materialType" placeholder="料别" value="" style="width:100%">
            <el-option label="主料" value='主料'></el-option>
            <el-option label="替料" value='替料'></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="BOM料号/规格">
          <el-input type="textarea" v-model.trim="programItem.specitification" size="large" ></el-input>
        </el-form-item>
        <el-form-item label="单板位置">
          <el-input  type="textarea" v-model.trim="programItem.position" size="large" ></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="add">确 定</el-button>
      </span>
    </el-dialog>
    <!--追加-->
    <el-dialog
      title="添加信息"
      :close-on-click-modal="isCloseOnModal"
      :close-on-press-escape="isCloseOnModal"
      :visible.sync="addLastDialogVisible"
      width="400px">
      <el-form label-width="120px" label-position="right">
        <el-form-item label="序列号">
          <el-input v-model.trim="programItem.serialNo" size="large" ></el-input>
        </el-form-item>
        <el-form-item label="站位">
          <el-input v-model.trim="programItem.lineseat" size="large" ></el-input>
        </el-form-item>
        <el-form-item label="程序料号">
          <el-input v-model.trim="programItem.materialNo" size="large" ></el-input>
        </el-form-item>
        <el-form-item label="数量">
          <el-input v-model.trim="programItem.quantity" size="large" ></el-input>
        </el-form-item>
        <el-form-item label="料别">
          <el-select v-model.trim="programItem.materialType" placeholder="料别" value="" style="width:100%">
            <el-option label="主料" value='主料'></el-option>
            <el-option label="替料" value='替料'></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="BOM料号/规格">
          <el-input type="textarea" v-model.trim="programItem.specitification" size="large" ></el-input>
        </el-form-item>
        <el-form-item label="单板位置">
          <el-input type="textarea" v-model.trim="programItem.position" size="large" ></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addLastDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="addLast">确 定</el-button>
      </span>
    </el-dialog>
    <!--修改-->
    <el-dialog
      title="修改信息"
      :close-on-click-modal="isCloseOnModal"
      :close-on-press-escape="isCloseOnModal"
      :visible.sync="editDialogVisible"
      width="400px">
      <el-form label-width="120px" label-position="right">
        <el-form-item label="序列号">
          <el-input v-model.trim="editData.serialNo" size="large" ></el-input>
        </el-form-item>
        <el-form-item label="站位">
          <el-input v-model.trim="editData.lineseat" size="large" ></el-input>
        </el-form-item>
        <el-form-item label="程序料号">
          <el-input v-model.trim="editData.materialNo" size="large" ></el-input>
        </el-form-item>
        <el-form-item label="数量">
          <el-input v-model.trim="editData.quantity" size="large" ></el-input>
        </el-form-item>
        <el-form-item label="料别">
          <el-select v-model.trim="editData.materialType" placeholder="料别" value="" style="width:100%">
            <el-option label="主料" value='主料'></el-option>
            <el-option label="替料" value='替料'></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="BOM料号/规格">
          <el-input type="textarea" v-model.trim="editData.specitification" size="large" ></el-input>
        </el-form-item>
        <el-form-item label="单板位置">
          <el-input type="textarea" v-model.trim="editData.position" size="large" ></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="edit">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
  import {mapGetters,mapActions} from 'vuex'
  import {programItemTip} from "./../../../../../utils/formValidate"
  import Bus from '../../../../../utils/bus'
  export default {
    name: 'programItemModal',
    data() {
      return {
        addDialogVisible:false,
        addLastDialogVisible:false,
        editDialogVisible:false,
        isCloseOnModal:false,
        //添加信息
        programItem: {
          serialNo:"",
          lineseat: "",
          materialType: "",
          materialNo: "",
          specitification: "",
          position: "",
          quantity:'',
          index:'',
          programId:'',
        },
        //修改信息
        editData: {
          serialNo:"",
          lineseat: "",
          materialType: "",
          materialNo: "",
          specitification: "",
          position: "",
          quantity:'',
          index:'',
          programId:''
        },
        row:{},
        programId:''
      }
    },
    beforeDestroy(){
      //取消监听
      Bus.$off('addProgramItem');
      Bus.$off('addLastProgramItem');
      Bus.$off('editProgramItem');
    },
    mounted(){
      //监听添加事件
      Bus.$on('addProgramItem',(row) => {
        this.row = row;
        this.resetModal();
        this.addDialogVisible = true;
      });
      //监听追加事件
      Bus.$on('addLastProgramItem',(id) => {
        this.programId = id;
        this.resetModal();
        this.addLastDialogVisible = true;
      });
      //监听修改事件
      Bus.$on('editProgramItem',(row) => {
        this.row = row;
        this.initEditData(row);
        this.editDialogVisible = true;
      })
    },
    computed:{
      ...mapGetters(['operations','programItemList'])
    },
    methods: {
      ...mapActions(['setOperations','setProgramItemList']),
      //初始化添加信息
      resetModal: function () {
        this.programItem.lineseat = '';
        this.programItem.materialNo = '';
        this.programItem.materialType = '';
        this.programItem.position = '';
        this.programItem.quantity = '';
        this.programItem.serialNo = '';
        this.programItem.specitification = '';
        this.programItem.index = this.row.index;
        this.programItem.programId = this.row.programId;
      },
      //初始化修改信息
      initEditData:function(row){
        this.editData.lineseat = row.lineseat;
        this.editData.materialNo = row.materialNo;
        this.editData.materialType = row.materialType;
        this.editData.position = row.position;
        this.editData.quantity = row.quantity + '';
        this.editData.serialNo = row.serialNo + '';
        this.editData.specitification = row.specitification;
        this.editData.index = row.index;
        this.editData.programId = row.programId;
      },
      //添加
      add:function(){
        let result = programItemTip(this.programItem);
        //判断
        if(result !== ''){
          this.$alertWarning(result);
          return;
        }
        for(let i = 0;i<this.programItemList.length;i++){
          let item = this.programItemList[i];
          if(this.programItem.lineseat === item.lineseat && this.programItem.materialNo === item.materialNo){
            this.$alertWarning('一个站位不允许多个相同料号');
            return;
          }
        }
        //刷新列表
        for(let i = 0;i<this.programItemList.length;i++){
          let item = this.programItemList[i];
          if(this.programItem.index === item.index){
            this.programItemList.splice(i+1,0,JSON.parse(JSON.stringify(this.programItem)));
            break;
          }
        }
        for(let i = 0;i<this.programItemList.length;i++){
          this.programItemList[i].index = i;
        }
        //添加到操作列表
        let obj = {
          operation:0,
          targetLineseat:this.row.lineseat,
          targetMaterialNo:this.row.materialNo,
          programId:this.programItem.programId,
          lineseat:this.programItem.lineseat,
          alternative:this.programItem.materialType === "替料",
          materialNo:this.programItem.materialNo,
          specitification:this.programItem.specitification,
          position:this.programItem.position,
          serialNo:this.programItem.serialNo,
          quantity:this.programItem.quantity,
        };
        this.operations.push(obj);
        this.addDialogVisible = false;
      },
      //修改
      edit:function(){
        let result = programItemTip(this.editData);
        //判断
        if(result !== ''){
          this.$alertWarning(result);
          return;
        }
        for(let i = 0;i<this.programItemList.length;i++){
          let item = this.programItemList[i];
          if(item.index !== this.editData.index && this.editData.lineseat === item.lineseat && this.editData.materialNo === item.materialNo){
            this.$alertWarning('一个站位不允许多个相同料号');
            return;
          }
        }
        //刷新列表
        for(let i = 0;i<this.programItemList.length;i++){
          let item = this.programItemList[i];
          if(this.editData.index === item.index){
            this.programItemList.splice(i,1,JSON.parse(JSON.stringify(this.editData)));
            break;
          }
        }
        //添加到操作列表
        let obj = {
          operation:1,
          targetLineseat:this.row.lineseat,
          targetMaterialNo:this.row.materialNo,
          programId:this.editData.programId,
          lineseat:this.editData.lineseat,
          alternative:this.editData.materialType === "替料",
          materialNo:this.editData.materialNo,
          specitification:this.editData.specitification,
          position:this.editData.position,
          serialNo:this.editData.serialNo,
          quantity:this.editData.quantity
        };
        this.operations.push(obj);
        this.editDialogVisible = false;
      },
      //追加
      addLast:function(){
        let result = programItemTip(this.programItem);
        //判断
        if(result !== ''){
          this.$alertWarning(result);
          return;
        }
        for(let i = 0;i<this.programItemList.length;i++){
          let item = this.programItemList[i];
          if(this.programItem.lineseat === item.lineseat && this.programItem.materialNo === item.materialNo){
            this.$alertWarning('一个站位不允许多个相同料号');
            return;
          }
        }
        //刷新列表
        this.programItemList.splice(this.programItemList.length,0,JSON.parse(JSON.stringify(this.programItem)));
        for(let i = 0;i<this.programItemList.length;i++){
          this.programItemList[i].index = i;
        }
        //添加到操作列表
        let obj = {
          operation:0,
          targetLineseat:"",
          targetMaterialNo:"",
          programId:this.programId,
          lineseat:this.programItem.lineseat,
          alternative:this.programItem.materialType === "替料",
          materialNo:this.programItem.materialNo,
          specitification:this.programItem.specitification,
          position:this.programItem.position,
          serialNo:this.programItem.serialNo,
          quantity:this.programItem.quantity,
        };
        this.operations.push(obj);
        this.programId = '';
        this.addLastDialogVisible = false;
      }
    }
  }

</script>

<style scoped lang="scss">
</style>
