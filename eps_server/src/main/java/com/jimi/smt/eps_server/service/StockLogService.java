package com.jimi.smt.eps_server.service;

/**打印日志服务接口
 * @package  com.jimi.smt.eps_server.service
 * @file     StockLogService.java
 * @author   HCJ
 * @date     2018年9月25日 下午4:32:22
 * @version  V 1.0
 */
public interface StockLogService {

	/**@author HCJ
	 * 打印时批量插入StockLog
	 * @method insert
	 * @param stockList
	 * @return void
	 * @date 2018年9月25日 下午4:32:48
	 */
	void insert(String stockList);
	
}
