export const setInitialTime = function(startTime,endTime) {
    let now = new Date();    //当前获得毫秒
    let nowYear = now.getFullYear();   ////得到年份
    let nowMonth = now.getMonth() + 1; // //得到月份
    nowMonth = nowMonth < 10 ? "0" + nowMonth : nowMonth;
    let nowDay = now.getDate();  //得到日期
    nowDay = nowDay < 10 ? "0" + nowDay : nowDay;
    let today = nowYear + "-" + nowMonth + "-" + nowDay;   //  今天的年月份
    endTime = today;

    let yesterdayMillion = now.getTime() - 1000 * 60 * 60 * 24;   //得到昨天的时间
    let yesterday = new Date(yesterdayMillion);
    let yesYear = yesterday.getFullYear();     //得到年份
    let yesMonth = yesterday.getMonth() + 1;  //得到月份
    yesMonth = yesMonth < 10 ? "0" + yesMonth : yesMonth;
    let yesDay = yesterday.getDate();      //得到日期
    yesDay = yesDay < 10 ? "0" + yesDay : yesDay;
    let preDay = yesYear + "-" + yesMonth + "-" + yesDay; //  昨天的年月份
    startTime = preDay;
    return [startTime,endTime];
}


export const checkClientTime = function(startTime,endTime){
    //开始和结束时间都有输入时进行判断
    if (startTime != "" && endTime != "") {
        let aa = startTime.split("-");
        let bb = endTime.split("-");
        let fromDate = "";
        let toDate = "";
        for(let i = 0; i < aa.length; i++) {
            fromDate += aa[i];
            toDate += bb[i];
        }
        fromDate = parseInt(fromDate);
        toDate = parseInt(toDate);
        if (fromDate > toDate || fromDate === 0 || toDate === 0) {
            alert("时间输入错误！请重新输入");
            startTime = "";
            endTime = "";
            return false;
        }
    }
    return true;
}

export const checkOperationTime = function(startTime,endTime){
  //开始和结束时间都有输入时进行判断
  if (startTime != "" && endTime != "") {
    let aa = startTime.split("-");
    let bb = endTime.split("-");
    let fromDate = "";
    let toDate = "";
    for(let i = 0; i < aa.length; i++) {
      fromDate += aa[i];
      toDate += bb[i];
    }
    fromDate = parseInt(fromDate);
    toDate = parseInt(toDate);
    if (fromDate > toDate || fromDate === 0 || toDate === 0) {
      alert("时间输入错误！请重新输入");
      startTime = "";
      endTime = "";
      return false;
    }else if((toDate - fromDate)>100 || (toDate - fromDate)>8900){
      alert("时间范围太大，请重新选择时间，时间区间规定在一个月内");
      return false;
    }
  }
  return true;
}

export const checkTimeByFind = function(startTime,endTime){
    //开始和结束时间都有输入时进行判断
    if (startTime != "" && endTime != "") {
        let aa = startTime.split("-");
        let bb = endTime.split("-");
        let fromDate = "";
        let toDate = "";
        for(let i = 0; i < aa.length; i++) {
            fromDate += aa[i];
            toDate += bb[i];
        }
        fromDate = parseInt(fromDate);
        toDate = parseInt(toDate);
        if (fromDate > toDate || fromDate === 0 || toDate === 0) {
            alert("时间输入错误！请重新输入");
            startTime = "";
            endTime = "";
            return false;
        }
    }
    return true;
}
