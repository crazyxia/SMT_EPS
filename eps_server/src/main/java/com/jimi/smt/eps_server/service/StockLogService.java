package com.jimi.smt.eps_server.service;

public interface StockLogService {

	/**
	 * 打印时批量插入StockLog
	 * @param list
	 */
	void insert(String list);
}
