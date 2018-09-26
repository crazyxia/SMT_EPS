package com.jimi.smt.eps_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jimi.smt.eps_server.annotation.Open;
import com.jimi.smt.eps_server.service.StockLogService;

@Controller
@RequestMapping("/stock")
public class StockLogController {

	@Autowired
	private StockLogService stockLogService;

	
	@Open
	@RequestMapping("/insert")
	public void insert(String stockList) {
		stockLogService.insert(stockList);
	}

}
