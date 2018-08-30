package com.jimi.smt.eps_server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jimi.smt.eps_server.annotation.Open;
import com.jimi.smt.eps_server.service.LineService;

/**
 * 产线控制器
 */
@Controller
@RequestMapping("/line")
public class LineController {
	
	@Autowired
	private LineService lineService;

	@Open
	@ResponseBody
	@RequestMapping("/selectAll")
	public List<String> selectAll() {
		return lineService.selectAll();
	}
		
	
}
