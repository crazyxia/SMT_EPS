package com.jimi.smt.eps_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jimi.smt.eps_server.annotation.Open;
import com.jimi.smt.eps_server.entity.CenterLogin;
import com.jimi.smt.eps_server.service.CenterLoginService;

/**
 * 中控控制器
 * @author HCJ
 */
@Controller
@RequestMapping("/login")
public class CenterLoginController {

	@Autowired
	private CenterLoginService centerLoginService;

	
	/**@author HCJ
	 * 根据产线id查询中控对象
	 * @param id 产线id
	 * @date 2019年1月29日 上午10:03:09
	 */
	@Open
	@ResponseBody
	@RequestMapping("/selectById")
	public CenterLogin selectById(Integer id) {
		return centerLoginService.selectById(id);
	}
}
