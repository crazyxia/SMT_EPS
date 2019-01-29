import Vue from 'vue'
function alertMsg(title,message,type) {
  new Vue().$notify({
    title:title,
    message:message,
    duration:3000,
    position:'bottom-right',
    showClose: false,
    type:type
  });
}


export const alertError = function (string) {
  alertMsg("错误",string,'error');
};

export const alertSuccess = function (string) {
  alertMsg("成功",string,"success");
};

export const alertInfo = function (string) {
  alertMsg("提示",string,"info");
};

export const alertWarning = function (string) {
  alertMsg("警告",string,"warning");
};
