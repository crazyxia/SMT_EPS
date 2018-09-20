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
		return lineMapper.selectByPrimaryKey(id).getLine();		
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

	
	@Override
	public Boolean isLineExist(String line) {		
		Boolean result = false;
		LineExample lineExample = new LineExample();
		lineExample.createCriteria().andLineEqualTo(line);
		List<Line> lines = lineMapper.selectByExample(lineExample);
		if(lines != null && lines.size() > 0) {
			result = true;
		}
		return result;
	}	
}
