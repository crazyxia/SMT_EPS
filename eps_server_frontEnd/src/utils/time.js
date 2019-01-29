export const setInitialTime = function () {
  let now = new Date();    //当前获得毫秒
  let nowYear = now.getFullYear();   ////得到年份
  let nowMonth = now.getMonth() + 1; // //得到月份
  nowMonth = nowMonth < 10 ? "0" + nowMonth : nowMonth;
  let nowDay = now.getDate();  //得到日期
  nowDay = nowDay < 10 ? "0" + nowDay : nowDay;
  let today = nowYear + "-" + nowMonth + "-" + nowDay;   //  今天的年月份
  let yesterdayMillion = now.getTime() - 1000 * 60 * 60 * 24;   //得到昨天的时间
  let yesterday = new Date(yesterdayMillion);
  let yesYear = yesterday.getFullYear();     //得到年份
  let yesMonth = yesterday.getMonth() + 1;  //得到月份
  yesMonth = yesMonth < 10 ? "0" + yesMonth : yesMonth;
  let yesDay = yesterday.getDate();      //得到日期
  yesDay = yesDay < 10 ? "0" + yesDay : yesDay;
  let preDay = yesYear + "-" + yesMonth + "-" + yesDay; //  昨天的年月份
  return [preDay, today];
};

export const checkOperationDownloadTime = function (startTime, endTime) {
  let result = '';
  //开始和结束时间都有输入时进行判断
  if (startTime !== "" && endTime !== "") {
    let aa = startTime.split(" ").join("-").split("-");
    let bb = endTime.split(" ").join("-").split("-");
    let fromDate = "";
    let toDate = "";
    for (let i = 0; i < aa.length - 1; i++) {
      fromDate += aa[i];
      toDate += bb[i];
    }
    fromDate = parseInt(fromDate);
    toDate = parseInt(toDate);
    /*同年*/
    if (aa[0] === bb[0]) {
      if ((toDate - fromDate) > 100) {
        result = "时间范围太大，请重新选择时间，时间区间规定在1个月内";
      }
    } else {
      if ((toDate - fromDate) > 8900) {
        result = "时间范围太大，请重新选择时间，时间区间规定在1个月内";
      }
    }
  }
  return result;
};

export const checkOperationFindTime = function (startTime, endTime) {
  let result = '';
  //开始和结束时间都有输入时进行判断
  if (startTime !== "" && endTime !== "") {
    let aa = startTime.split(" ").join("-").split("-");
    let bb = endTime.split(" ").join("-").split("-");
    let fromDate = "";
    let toDate = "";
    for (let i = 0; i < aa.length - 1; i++) {
      fromDate += aa[i];
      toDate += bb[i];
    }
    fromDate = parseInt(fromDate);
    toDate = parseInt(toDate);
    /*同年*/
    if (aa[0] === bb[0]) {
      if ((toDate - fromDate) > 300) {
        result = "时间范围太大，请重新选择时间，时间区间规定在3个月内";
      }
    } else {
      if ((toDate - fromDate) > 9100) {
        result = "时间范围太大，请重新选择时间，时间区间规定在3个月内";
      }
    }
  }
  return result;
};
