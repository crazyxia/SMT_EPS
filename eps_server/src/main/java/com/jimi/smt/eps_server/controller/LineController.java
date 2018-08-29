package com.jimi.smt.eps_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jimi.smt.eps_server.annotation.Open;
import com.jimi.smt.eps_server.entity.ResultJson;
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
	@RequestMapping("/selectByLine")
	public ResultJson selectByLine(String line) {
		String code = lineService.selectByLine(line);
		ResultJson resultJson = new ResultJson();
		if("0".equals(code)) {
			resultJson.setCode(0);
			resultJson.setMsg("线号不存在");
		}else{
			resultJson.setCode(1);
			resultJson.setMsg("线号存在");
		}
		return resultJson;
	}
	
}
