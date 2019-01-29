export const errTip = function (code) {
  let result = '';
  switch (code) {
    case "failed_id_exist":
      result = "该工号已存在，请重新填写工号" ;
      break;
    case "failed_not_found":
      result = "用户不存在" ;
      break;
    case "failed_not_admin":
      result = "当前用户不是管理员";
      break;
    case "failed_wrong_password":
      result = "密码错误" ;
      break;
    case "failed_not_enabled":
      result = "该员工已离职";
      break;
    case "failed_access_denied":
      result = "权限不足";
      break;
    case "failed_wrong_type":
      result = "你没有权限添加该类型的人员" ;
      break;
    case "failed_item_key_duplicate":
      result = "Item表主键重复";
      break;
    case "failed_visit_not_found":
      result = "Visit表无记录";
      break;
    case "failed_program_not_found":
      result = "ProgramID不存在";
      break;
    case "failed_illegal_state":
      result = "只能编辑未开始和正在进行的工单";
      break;
    case "failed_materialNo_exist":
      result = "物料料号重复";
      break;
    case "fail_get_img":
      result = "获取图片失败";
      break;
    case "failed_already_started":
      result = "已存在相同的正在进行的工单";
      break;
    default:
      result = code;
      break;
  }
  return result;
};
