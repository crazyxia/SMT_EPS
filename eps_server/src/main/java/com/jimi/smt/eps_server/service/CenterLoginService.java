package com.jimi.smt.eps_server.service;

import com.jimi.smt.eps_server.entity.CenterLogin;

/**
 * 中控登录服务接口
 * @author HCJ
 *
 */
public interface CenterLoginService {
	
	/**
	 * 返回中控登录对象
	 * @param id
	 * @return
	 */
	CenterLogin selectById(int id);
	
}
