package com.jimi.smt.eps_server.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.jimi.smt.eps_server.entity.StockLog;
import com.jimi.smt.eps_server.mapper.StockLogMapper;
import com.jimi.smt.eps_server.service.StockLogService;

@Service
public class StockLogServiceImpl implements StockLogService {

	@Autowired
	private StockLogMapper stockLogMapper;
	
	
	@Override
	public void insert(String list) {
		//将字符串类型的JSON转换为集合对象
		List<StockLog> lists = JSON.parseArray(list, StockLog.class);
		stockLogMapper.insertList(lists);
	}

}
