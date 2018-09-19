package com.jimi.smt.eps_server.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jimi.smt.eps_server.entity.CenterLogin;
import com.jimi.smt.eps_server.entity.CenterLoginExample;
import com.jimi.smt.eps_server.mapper.CenterLoginMapper;
import com.jimi.smt.eps_server.service.CenterLoginService;

@Service
public class CenterLoginServiceImpl implements CenterLoginService {

	@Autowired
	private CenterLoginMapper centerLoginMapper;

	
	@Override
	public CenterLogin selectById(int id) {
		CenterLoginExample centerLoginExample = new CenterLoginExample();
		centerLoginExample.createCriteria().andLineEqualTo(id);
		List<CenterLogin> list = centerLoginMapper.selectByExample(centerLoginExample);
		if(list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

}
