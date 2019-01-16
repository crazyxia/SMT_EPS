import store from './../store'


export const lineStatusData = function(){
  let lineStatusData = {
    data:{}
  };
  let lineSize = store.state.lineSize;
  if(lineSize > 0){
    let statusDetails =[];
    for(let i = 0;i<24;i++){
      let obj = {
        "line": "0",
        "allsSuc": 0,
        "allsFail": 0,
        "allsSucRate": "",
        "allsFailRate": "",
        "feedSuc": 0,
        "feedFail": 0,
        "feedSucRate": "",
        "feedFailRate": "0",
        "changeSuc": 0,
        "changeFail": 0,
        "changeSucRate": "",
        "changeFailRate": "",
        "someSuc": 0,
        "someFail": 0,
        "someSucRate": "",
        "someFailRate": ""
      }
      statusDetails.push(obj);
    }

    lineStatusData.data = {
      "line": "",
      "allsSucOfDay": 0,
      "allsFailOfDay": 0,
      "allsSucRateOfDay": "",
      "allsFailRateOfDay": "",
      "feedSucOfDay": 0,
      "feedFailOfDay": 0,
      "feedSucRateOfDay": "",
      "feedFailRateOfDay": "",
      "changeSucOfDay": 0,
      "changeFailOfDay": 0,
      "changeSucRateOfDay": "",
      "changeFailRateOfDay": "",
      "someSucOfDay": 0,
      "someFailOfDay": 0,
      "someSucRateOfDay": "0",
      "someFailRateOfDay": "0",
      "statusDetails":statusDetails
    }
  }
  return lineStatusData;
}


export const lineAllDayData = function(){
  let lineAllDayData = {
    data:[]
  };
  let lineSize = store.state.lineSize;
  if(lineSize>0){
    for(let i=0;i<lineSize;i++){
      let obj = {
        "line": "",
        "allsSuc": 0,
        "allsFail": 0,
        "allsSucRate": "",
        "allsFailRate": "",
        "feedSuc": 0,
        "feedFail": 0,
        "feedSucRate": "",
        "feedFailRate": "",
        "changeSuc": 0,
        "changeFail": 0,
        "changeSucRate": "",
        "changeFailRate": "",
        "someSuc": 0,
        "someFail": 0,
        "someSucRate": "",
        "someFailRate": ""        
      }
      lineAllDayData.data.push(obj);
    }
  }
  return lineAllDayData;
}

