package com.jimi.smt.eps_server.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jimi.smt.eps_server.annotation.Open;
import com.jimi.smt.eps_server.entity.Line;
import com.jimi.smt.eps_server.service.LineService;

/**
 * 产线控制器
 * @author HCJ
 */
@Controller
@RequestMapping("/line")
public class LineController {

	@Autowired
	private LineService lineService;

	
	/**@author HCJ
	 * 查询所有Line对象
	 * @date 2019年1月29日 下午5:27:32
	 */
	@Open
	@ResponseBody
	@RequestMapping("/selectAll")
	public List<Line> selectAll() {
		return lineService.selectAll();
	}
	
	
	/**@author HCJ
	 * 查询所有产线名称的集合
	 * @date 2019年1月29日 下午5:28:11
	 */
	@Open
	@ResponseBody
	@RequestMapping("/selectLine")
	public List<String> selectLine() {
		List<Line> lines = lineService.selectAll();
		List<String> lineNames = new ArrayList<>();
		for (Line line : lines) {
			lineNames.add(line.getLine());
		}
		return lineNames;		
	}

}
