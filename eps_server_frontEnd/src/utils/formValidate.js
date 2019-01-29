export const userTip = function (obj) {
  let result = '';
  if (obj.id === "") {
    result = "工号不能为空";
  } else if (obj.name === "") {
    result = "姓名不能为空";
  }else if(obj.password === ""){
    result = "密码不能为空";
  } else if (obj.type === "") {
    result = "岗位不能为空";
  }else if (obj.classType === "") {
    result = "班别不能为空";
  }
  return result
};

export const programTip = function (oldState,newState) {
  let result = '';
  if (Number(oldState) >= Number(newState)) {
    result ="你选择的状态不符合，请重新选择";
  }
  return result;
};

export const programItemTip = function (obj) {
  let result = ''
  if (obj.serialNo === "") {
    result = "序列号不能为空";
  }else if (obj.lineseat === "") {
    result = "站位不能为空";
  } else if (obj.materialNo === "") {
    result = "程序料号不能为空";
  } else if (obj.quantity === "") {
    result = "数量不能为空";
  } else if (obj.materialType === "") {
    result = "料别不能为空";
  } else if (obj.specitification === "") {
    result = "BOM料号/规格不能为空";
  } else if (obj.position === "") {
    result = "单板位置不能为空";
  } else if (!judge(obj.serialNo )&& obj.serialNo !== "0"){
    result = "序列号必须为不以0开头的正整数或者0";
  } else if (!judge(obj.quantity )&& obj.quantity !== "0"){
    result = "数量必须为不以0开头的正整数或者0";
  }
  return result;
};

export const configTip = function (arr) {
  let result = '';
  for (let i = 0; i < arr.length; i++) {
    let obj = arr[i];
    if (obj.value === "") {
      result = "值不能为空";
      return result;
    }
    if(!judge(obj.value) && obj.value !== "0"){
      result = "格式不对，请输入不以0开头的正整数";
      return result;
    }
    if (obj.name === "check_after_change_time" || obj.name === "check_all_cycle_time") {
      let number = obj.value;
      if (number === "0") {
        result = "格式不对，核料延时时间和全检周期时间的值不能为0";
        return result;
      }
    }
    if (obj.name === "ipqc_error_alarm" || obj.name === "operator_error_alarm") {
      let number = obj.value;
      if (number !== "0" && number !== "1" && number !== "2" && number !== "3") {
        result = "格式不对，操作员出错时响应和IPQC出错时响应的值只能为0、1、2、3";
        return result;
      }
    }
  }
  return result;
};

export const materialTip = function (obj) {
  let result = '';
  if (obj.materialNo === "") {
    result = "料号不能为空";
  } else if (obj.perifdOfValidity === "") {
    result = "物料保质期不能为空";
  } else if (!judge(obj.perifdOfValidity)) {
    result = "物料保质期必须为不以0开头的正整数"
  } else if (obj.perifdOfValidity > 2147483647) {
    result = "保质期天数过长";
  }
  return result;
};

//判断是否为正整数
export const judge = function isNumber(num) {
  let val = num;
  let reg = /^[1-9]*[1-9][0-9]*$/;
  if (val !== "") {
    return reg.test(val);
  }
};

