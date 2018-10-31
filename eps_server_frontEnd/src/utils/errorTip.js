export const errTip = function (code) {
  switch (code) {
    case "failed_id_exist":
      alert("该工号已存在，请重新填写工号");
      break;
    case "failed_not_found":
      alert("id不存在");
      break;
    case "failed_not_admin":
      alert("请先成为管理员");
      break;
    case "failed_wrong_password":
      alert("密码错误");
      break;
    case "failed_not_enabled":
      alert("该员工已离职，请重新输入工号");
      break;
    case "failed_access_denied":
      alert("权限不足");
      break;
    case "failed_item_key_duplicate":
      alert("Item表主键重复");
      break;
    case "failed_visit_not_found":
      alert("Visit表无记录");
      break;
    case "failed_program_not_found":
      alert("ProgramID不存在");
      break;
    case "failed_illegal_state":
      alert("只能编辑未开始和正在进行的工单");
      break;
    case "failed_materialNo_exist":
      alert("物料料号重复");
      break;
    case "fail_get_img":
      alert("获取图片失败");
      break;
    default:
      alert(code);
      break;
  }
};
