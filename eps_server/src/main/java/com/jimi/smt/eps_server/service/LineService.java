package com.jimi.smt.eps_server.service;

import java.util.List;

import com.jimi.smt.eps_server.entity.Line;

public interface LineService {
	
	/**
	 * 根据id返回线别
	 * @param id
	 * @return 
	 */
	String getLineNameById(int id);
	
	/**@author HCJ
	 * 根据id返回线别名称
	 * @method getLineIdByName
	 * @param line
	 * @return
	 * @return int
	 * @date 2018年9月21日 上午10:46:23
	 */
	int getLineIdByName(String line);
			
	
	/**
	 * 返回线别数量
	 * @return 
	 */
	long countLineNum();
	
	
	/**
	 * 列出所有产线
	 * @return
	 */
	List<Line> selectAll();
	
	
	/**
	 * 返回是否有这条产线的结果
	 * @param line
	 * @return
	 */
	Boolean isLineExist(String line);
	
}
