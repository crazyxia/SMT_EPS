package com.jimi.smt.eps_server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jimi.smt.eps_server.entity.CenterLogin;
import com.jimi.smt.eps_server.mapper.CenterLoginMapper;
import com.jimi.smt.eps_server.service.CenterLoginService;

@Service
public class CenterLoginServiceImpl implements CenterLoginService {
	
	@Autowired
	private CenterLoginMapper centerLoginMapper;

	@Override
	public CenterLogin selectById(int id) {
		return centerLoginMapper.selectByPrimaryKey(id);
	}

}
