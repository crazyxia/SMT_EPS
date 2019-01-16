package com.jimi.smt.eps_server.service;

import java.util.List;

import com.jimi.smt.eps_server.entity.Line;

/**产线服务接口
 * @package  com.jimi.smt.eps_server.service
 * @file     LineService.java
 * @author   HCJ
 * @date     2018年9月25日 下午4:26:46
 * @version  V 1.0
 */
public interface LineService {
	
	/**@author HCJ
	 * 根据产线id返回产线名称
	 * @method getLineNameById
	 * @param id
	 * @return
	 * @return String
	 * @date 2018年9月25日 下午4:27:02
	 */
	String getLineNameById(int id);
	
	
	/**@author HCJ
	 * 根据产线名称返回产线id
	 * @method getLineIdByName
	 * @param line
	 * @return
	 * @return Integer
	 * @date 2018年9月21日 上午10:46:23
	 */
	Integer getLineIdByName(String line);
			
	
	/**@author HCJ
	 * 返回线别数量
	 * @method countLineNum
	 * @return
	 * @return long
	 * @date 2018年9月25日 下午4:27:56
	 */
	long countLineNum();
	
		
	/**@author HCJ
	 * 返回所有产线
	 * @method selectAll
	 * @return
	 * @return List<Line>
	 * @date 2018年9月25日 下午4:28:31
	 */
	List<Line> selectAll();			
	
}
