import store from './../store'

export const userTip = function (obj) {
  if (obj.id === "") {
    alert("工号不能为空");
  } else if (obj.name === "") {
    alert("姓名不能为空");
  }else if(obj.password === ""){
    alert("密码不能为空");
  } else if (obj.type === "") {
    alert("岗位不能为空");
  }else if (obj.classType === "") {
    alert("班别不能为空");
  }else {
    return true;
  }
  return false;
};

export const programTip = function (state) {
  let oldState = store.state.oldState;
  if (oldState >= state) {
    alert("你选择的状态不符合，请重新选择");
    return false;
  }
  return true;
}

export const programItemTip = function (obj) {
  if (obj.lineseat === "") {
    alert("站位不能为空");
  } else if (obj.materialNo === "") {
    alert("程序料号不能为空");
  } else if (obj.quantity === "") {
    alert("数量不能为空");
  } else if (obj.materialType === "") {
    alert("料别不能为空");
  } else if (obj.specitification === "") {
    alert("BOM料号/规格不能为空");
  } else if (obj.position === "") {
    alert("单板位置不能为空");
  } else if (!judge(obj.quantity )&& obj.quantity != "0"){
    alert("数量必须为不以0开头的正整数或者0")
  } else {
    return true;
  }
  return false;
}

export const configTip = function (arr) {
  for (let i = 0; i < arr.length; i++) {
    let obj = arr[i];
    if (obj.value === "") {
      alert("值不能为空");
      return false;
    }
    if(!judge(obj.value) && obj.value !== "0"){
      alert("格式不对，请输入不以0开头的正整数");
      return false;
    }
    if (obj.name === "check_after_change_time" || obj.name === "check_all_cycle_time") {
      let number = obj.value;
      if (number === "0") {
        alert("格式不对，核料延时时间和全检周期时间的值不能为0");
        return false;
      }
    }
    if (obj.name === "ipqc_error_alarm" || obj.name === "operator_error_alarm") {
      let number = obj.value;
      if (number !== "0" && number !== "1" && number !== "2" && number !== "3") {
        alert("格式不对，操作员出错时响应和IPQC出错时响应的值只能为0、1、2、3");
        return false;
      }
    }
  }
  return true;
}
export const materialTip = function (obj) {
  if (obj.materialNo === "") {
    alert("料号不能为空");
  } else if (obj.perifdOfValidity === "") {
    alert("物料保质期不能为空");
  } else if (!judge(obj.perifdOfValidity)) {
    alert("物料保质期必须为不以0开头的正整数")
  } else if (obj.perifdOfValidity > 2147483647) {
    alert("保质期天数过长");
  } else {
    return true;
  }
  return false;
};

//判断是否为正整数
export const judge = function isNumber(num) {
  let val = num;
  let regu = /^[1-9]*[1-9][0-9]*$/;
  if (val !== "") {
    if (regu.test(val)) {
      return true;
    } else {
      return false;
    }
  }
};

