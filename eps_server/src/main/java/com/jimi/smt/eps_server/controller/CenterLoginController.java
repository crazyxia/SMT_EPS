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

	
	@Open
	@ResponseBody
	@RequestMapping("/selectById")
	public CenterLogin selectById(int id) {
		return centerLoginService.selectById(id);
	}
}
