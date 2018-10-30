<template>
  <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"
       data-backdrop="static">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="myModalLabel">
            {{message}}
          </h5>
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true" @click="resetModal">&times;
          </button>
        </div>
        <div class="modal-body">
          <form class="form-inline" role="form">
            <div class="form-group">
              <label for="lineseat">站位</label>
              <input type="text" class="form-control" id="lineseat" v-model="modalInfo.lineseat">
            </div>
            <div class="form-group">
              <label for="materialNo">程序料号</label>
              <input type="text" class="form-control" id="materialNo" v-model="modalInfo.materialNo">
            </div>
            <div class="form-group">
              <label for="quantity">数量</label>
              <input type="text" class="form-control" id="quantity" v-model="modalInfo.quantity">
            </div>
            <div class="form-group">
              <label for="materialType">料别</label>
              <select class="form-control" id="materialType" v-model="modalInfo.materialType">
                <option selected="selected" disabled="disabled" style='display: none' value=''></option>
                <option>主料</option>
                <option>替料</option>
              </select>
            </div>
            <div class="form-group">
              <label for="specitification">BOM料号/规格</label>
              <textarea v-model="modalInfo.specitification" id="specitification" class="form-control"></textarea>
            </div>
            <div class="form-group">
              <label for="position">单板位置</label>
              <textarea class="form-control" id="position" v-model="modalInfo.position"></textarea>
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn_save" @click="save">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import store from './../../../../../store'
  import {programItemTip} from "./../../../../../utils/formValidate"

  export default {
    name: 'programItemModal',
    data() {
      return {
        modalInfo: {
          lineseat: "",
          materialType: "",
          materialNo: "",
          specitification: "",
          position: "",
          quantity: 0
        },
        editData: {
          lineseat: "",
          materialNo: ""
        }
      }
    },
    computed: {
      message: function () {
        let operationType = store.state.programItemOperationType;
        if (operationType === "update") {
          return "修改信息";
        } else if (operationType === "add") {
          return "添加信息";
        }
      },
      isAdd: function () {
        return store.state.isAdd;
      },
      isUpdate: function () {
        return store.state.isUpdate;
      },
      programItem: function () {
        return store.state.programItem;
      },
      programItemList: function () {
        return store.state.programItemList;
      },
      operations: function () {
        return store.state.operations;
      }
    },
    watch: {
      isAdd: function (val) {
        if (val === true) {
          store.commit("setIsAdd", false);
          let programItem = JSON.parse(JSON.stringify(this.programItem));
          this.editData.materialNo = programItem.materialNo;
          this.editData.lineseat = programItem.lineseat;
          $('#myModal').modal({backdrop: 'static', keyboard: false});
        }
      },
      isUpdate: function (val) {
        if (val === true) {
          store.commit("setIsUpdate", false);
          let programItem = JSON.parse(JSON.stringify(this.programItem));
          this.modalInfo = programItem;
          this.editData.materialNo = programItem.materialNo;
          this.editData.lineseat = programItem.lineseat;
          $('#myModal').modal({backdrop: 'static', keyboard: false});
        }
      }
    },
    methods: {
      save: function () {
        if (programItemTip(this.modalInfo)) {
          let index = store.state.operationIndex;
          let operationType = store.state.programItemOperationType;
          if(operationType === "add"){
            for (let i = 0; i < this.programItemList.length; i++) {
              let item = this.programItemList[i];
              if (this.modalInfo.lineseat === item.lineseat && this.modalInfo.materialNo === item.materialNo) {
                alert("一个站位不允许多个相同料号");
                return;
              }
            }
          }else if(operationType === "update"){
            if (this.modalInfo.lineseat !== this.editData.lineseat || this.modalInfo.materialNo !== this.editData.materialNo){
              for (let i = 0; i < this.programItemList.length; i++) {
                let item = this.programItemList[i];
                if (this.modalInfo.lineseat === item.lineseat && this.modalInfo.materialNo === item.materialNo) {
                  alert("一个站位不允许多个相同料号");
                  return;
                }
              }
            }
          }
          if (operationType === "add") {
            this.programItemList.splice(index + 1, 0, this.modalInfo);
          } else if (operationType === "update") {
            this.programItemList.splice(index, 1, this.modalInfo);
          }
          store.commit("setProgramItemRefresh", true);
          $('#myModal').modal('hide');
          this.$emit("getModalInfo", this.modalInfo);
          this.resetModal();
        }
      },
      resetModal: function () {
        this.modalInfo = {
          lineseat: "",
          materialType: "",
          materialNo: "",
          specitification: "",
          position: "",
          quantity: 0
        }
      }
    }
  }

</script>

<style scoped lang="scss">
  .modal {
    .modal-dialog {
      max-width: 600px;
      min-width: 600px;
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      .form-inline {
        .form-group {
          margin: 0 20px 10px 0;
          label {
            margin-right: 10px;
          }
          .form-control {
            width: 180px;
            border-radius: 10px;
          }
          textarea.form-control {
            width: 400px;
          }
        }
      }
      .btn_save {
        width: 100px;
        color: #fff;
        background-color: #00acec;
      }
      .btn_save:hover {
        background-color: #808080;
      }
    }
  }
</style>
