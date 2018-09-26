import store from './../store'

export const userTip = function (obj) {
  if(obj.id === ""){
    alert("工号不能为空");
  }else if(obj.name === ""){
    alert("姓名不能为空");
  }else if(obj.type === ""){
    alert("岗位不能为空");
  }else if(obj.classType === ""){
    alert("班别不能为空");
  }else{
    return true;
  }
  return false;
};

export const programTip = function(state){
  let oldState = store.state.oldState;
  if(oldState > state){
    alert("状态选择不正确");
    return false;
  }else if(oldState == 2&&state == 3){
    alert("状态选择不正确");
    return false;
  }
  return true;
}

export const programItemTip = function(obj){
  if(obj.lineseat === ""){
    alert("站位不能为空");
  }else if(obj.materialNo === ""){
    alert("程序料号不能为空");
  }else if(obj.quantity === ""){
    alert("数量不能为空");
  }else if(obj.materialType === ""){
    alert("料别不能为空");
  }else if(obj.specitification === ""){
    alert("BOM料号/规格不能为空");
  }else if(obj.position === ""){
    alert("单板位置不能为空");
  }else{
    return true;
  }
  return false;
}

export const configTip = function(arr){
  let index = 0;
  for(let i=0;i<arr.length;i++){
    let obj = arr[i];
    if(obj.value === ""){
      alert("值不能为空");
      return false;
    }
    if(judgeNumber(obj.value)){
      if(obj.name == "ipqc_error_alarm" || obj.name == "operator_error_alarm"){
        let number = obj.value;
        if(number == 0 || number == 1 || number == 2 || number == 3){
          index++;
        }else{
          alert("格式不对");
          return false;
        }
      }
    }else{
      alert("格式不对");
      return false;
    }
  }
  if(index == 2){
    return true;
  }else{
    return false;
  }
}

const judgeNumber = function(val){
  let regString = /[a-zA-Z]+/;
  if(regString.test(val)){
　　return false;
  }else{
    return true;
  }
}

export const materialTip = function (obj) {
  if(obj.materialNo === ""){
    alert("料号不能为空");
  }else if(obj.perifdOfValidity === ""){
    alert("物料保质期不能为空");
  }else if(!judge(obj.perifdOfValidity)){
    alert("物料保质期必须为正整数")
  }else{

    return true;
  }
  return false;
};

export const clientTip = function(val){
  if(val==""||val==null){
    alert("请输入要跳转的页面");
  }else if(!isNumber(val)){
    alert("跳转页面必须要正整数");
  }
}

  //判断是否为正整数
const judge = function isNumber(num){
      var val = num;
      var regu = /^[1-9]\d*$/;
      if(val!=""){
          if(regu.test(val)){
              return true;
          } else{
              return false;
          }
      }
    }

export const getTypeName = function(val){
      let typeName = '-'
      let value = parseInt(val)
      switch(value){
        case 0:
          typeName = "上料";
          break;
        case 1:
          typeName = "换料";
          break;
        case 2:
          typeName = "全检";
          break;
        case 3:
          typeName = "抽检";
          break;
        case 4:
          typeName = "仓库发料";
          break;
        default:
          break;
      }
      return typeName;
    } 
