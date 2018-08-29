package com.jimi.smt.eps_server.service;

import java.util.List;

import com.jimi.smt.eps_server.entity.Line;

public interface LineService {
	
	/**
	 * 根据id返回线别
	 * @param id
	 * @return 
	 */
	String getLineById(int id);
	
	/**
	 * 列出所有线别
	 * @return 
	 */
	List<Line> list();
	
	/**
	 * 返回线别数量
	 * @return 
	 */
	long getLineNum();
	
	String selectByLine(String line);

}
