package com.jimi.smt.eps_server.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jimi.smt.eps_server.entity.Line;
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
	public List<String> selectAll() {
		return lineMapper.selectAll();
	}

	@Override
	public int selectLine(String line) {
		List<String> lines = lineMapper.selectLine();
		int result = 0;
		for (int i = 0; i < lines.size(); i++) {
			if (!lines.get(i).equals("") && lines.get(i).equals(line)) {
				result = 1;
				break;
			}
		}
		return result;
	}
	

}
