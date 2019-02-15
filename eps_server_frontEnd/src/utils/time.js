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
