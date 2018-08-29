package com.jimi.smt.eps_server.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jimi.smt.eps_server.entity.Line;
import com.jimi.smt.eps_server.entity.LineExample;
import com.jimi.smt.eps_server.mapper.LineMapper;
import com.jimi.smt.eps_server.service.LineService;

@Service
public class LineServiceImpl implements LineService {
	
	@Autowired
	private LineMapper lineMapper;
		
	@Override
	public List<Line> list() {
		return lineMapper.selectByExample(null);
	}
	
	@Override
	public String getLineById(int id) {
		return lineMapper.selectByPrimaryKey(id+1).getLine();		
	}

	@Override
	public long getLineNum() {
		return lineMapper.countByExample(null);
	}

	@Override
	public String selectByLine(String line) {
		LineExample lineExample = new LineExample();
		lineExample.createCriteria().andLineEqualTo(line);		
		if(lineMapper.selectByExample(lineExample).size()==0) {
			return "0";
		}else {
			return "1";
			}
		
	}

}
