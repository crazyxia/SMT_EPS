import Qs from 'qs'
import axios from 'axios'
export const axiosPost = (opt) =>{
  return axios({
    method: 'post',
    url: opt.url,
    timeout: 0,
    headers: {
      'Content-type': 'application/x-www-form-urlencoded;charset=UTF-8'
    },
    data: Qs.stringify(opt.data)
  })
};

//模拟form表单提交数据
export const downloadFile = (url, args) => {
  let body = document.body,
    form = document.createElement('form'),
    input;

  form.setAttribute('action', url);
  if (args) {
    for (let key in args) {
      input = document.createElement('input');
      input.setAttribute('type', 'hidden');
      input.setAttribute('name', key);
      input.setAttribute('value', args[key]);
      form.appendChild(input);
    }
  }

  let submitBtn = document.createElement('button');
  submitBtn.setAttribute('type', 'submit');
  form.appendChild(submitBtn);
  body.appendChild(form);
  submitBtn.click();
  body.removeChild(form);
  return form;
};
