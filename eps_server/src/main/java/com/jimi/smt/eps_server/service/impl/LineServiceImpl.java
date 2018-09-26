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
	public String getLineNameById(int id) {
		Line line = lineMapper.selectByPrimaryKey(id);
		if(line == null) {
			return null;
		}
		return line.getLine();
	}
	
	
	@Override
	public Integer getLineIdByName(String line) {
		LineExample lineExample = new LineExample();
		lineExample.createCriteria().andLineEqualTo(line);
		List<Line> lines = lineMapper.selectByExample(lineExample);
		if(lines.isEmpty()) {
			return null;
		}
		return lines.get(0).getId();
	}

	
	@Override
	public long countLineNum() {
		return lineMapper.countByExample(null);
	}

	
	@Override
	public List<Line> selectAll() {
		LineExample lineExample = new LineExample();
		lineExample.setOrderByClause("id asc");
		return lineMapper.selectByExample(lineExample);
	}		
		
}
