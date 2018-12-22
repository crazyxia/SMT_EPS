package com.jimi.smt.eps_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jimi.smt.eps_server.annotation.Log;
import com.jimi.smt.eps_server.annotation.Open;
import com.jimi.smt.eps_server.service.PrinterService;
import com.jimi.smt.eps_server.util.ResultUtil2;

/**
 * 打印控制器
 * @author coke
 */
@Controller
@RequestMapping("/task")
public class PrinterController {

	@Autowired
	private PrinterService printerService;

	
	@Log
	@Open
	@ResponseBody
	@RequestMapping("/printBarcode")
	public ResultUtil2 printBarcode(String printerIP, String materialId, String materialNo, Integer remainingQuantity, String productDate, String user, String supplier) throws InterruptedException {
		if (printerIP == null || materialId == null || materialNo == null || remainingQuantity == null || productDate == null || user == null || supplier == null) {
			return new ResultUtil2(400, "参数不足");
		} else if ("".equals(printerIP)) {
			return new ResultUtil2(400, "打印机IP不能为空");
		} else {
			ResultUtil2 resultUtil2 = printerService.printBarcode(printerIP, materialId, materialNo, remainingQuantity, productDate, user, supplier);
			return resultUtil2;
		}
	}

}
