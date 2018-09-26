package com.jimi.smt.eps_server.service;

import com.jimi.smt.eps_server.entity.CenterLogin;

/**中控登录服务接口
 * @package  com.jimi.smt.eps_server.service
 * @file     CenterLoginService.java
 * @author   HCJ
 * @date     2018年9月25日 下午4:26:10
 * @version  V 1.0
 */
public interface CenterLoginService {
	
	/**@author HCJ
	 * 返回中控登录对象
	 * @method selectById
	 * @param id
	 * @return
	 * @return CenterLogin
	 * @date 2018年9月25日 下午4:25:49
	 */
	CenterLogin selectById(Integer id);
	
}
