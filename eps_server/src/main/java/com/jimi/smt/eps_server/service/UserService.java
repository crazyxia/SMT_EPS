package com.jimi.smt.eps_server.service;

import com.jimi.smt.eps_server.entity.User;
import com.jimi.smt.eps_server.util.ResultUtil;

/**
 * 用户业务层
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public interface UserService {

	/**@author HCJ
	 * 新增一个用户，根据登录的用户类型去判断有没有权限
	 * @method add
	 * @param id
	 * @param classType
	 * @param name
	 * @param type 要增加的用户类型
	 * @param password
	 * @param userType 登录的用户类型
	 * @return
	 * @return String
	 * @date 2019年2月22日 上午11:28:03
	 */
	String add(String id, Integer classType, String name, Integer type, String password, Integer userType);
	

	/**@author HCJ
	 * 更新用户信息，根据登录的用户类型去判断有没有权限
	 * @method update
	 * @param id
	 * @param classType
	 * @param name
	 * @param type 要修改的用户类型
	 * @param password
	 * @param enabled
	 * @param userType 登录的用户类型
	 * @return
	 * @return String
	 * @date 2019年2月22日 上午11:29:41
	 */
	String update(String id, Integer classType, String name, Integer type, String password, Boolean enabled, Integer userType);


	/**@author HCJ
	 * 根据条件列出用户，根据登录的用户类型去判断拥有查询到哪些职员信息的权限
	 * 前三个为查询条件，多选为求交集；
		orderBy：
		根据字段名排序
		可能的值为：
		id(默认)
		name
		type
		create_time；
	 * @method list
	 * @param id
	 * @param classType
	 * @param name
	 * @param type 要查询的用户类型
	 * @param orderBy
	 * @param enabled
	 * @param currentPage
	 * @param pageSize
	 * @param userType 登录的用户类型
	 * @return
	 * @return ResultUtil 查询失败为400，查询成功为401
	 * @date 2019年2月22日 上午11:31:25
	 */
	ResultUtil list(String id, Integer classType, String name, Integer type, String orderBy, Boolean enabled, Integer currentPage, Integer pageSize, Integer userType);
				
	
	/**
	 * 使用管理员id以及对应的密码进行登录（如果有），
	 * 只有登录成功后才能使用其他接口，
	 * 否则调用其他接口时会返回failed_access_denied
	 * @param id
	 * @return
	 */
	User selectUserById(String id);
	
}
