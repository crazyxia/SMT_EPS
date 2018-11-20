package com.jimi.smt.eps_server.service;

import com.jimi.smt.eps_server.util.ResultUtil2;

public interface PrinterService {
	
	/**
	 * 利用webSoket转发打印请求
	 * @param printerIP
	 * @param materialId
	 * @param materialNo
	 * @param remainingQuantity
	 * @param productDate
	 * @param user
	 * @return
	 * @throws InterruptedException 
	 */
	ResultUtil2 printBarcode(String printerIP, String materialId, String materialNo, Integer remainingQuantity, String productDate, String user, String supplier) throws InterruptedException;

}
