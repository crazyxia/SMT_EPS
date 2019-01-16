package com.jimi.smt.eps_server.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jimi.smt.eps_server.entity.Page;
import com.jimi.smt.eps_server.entity.User;
import com.jimi.smt.eps_server.entity.UserExample;
import com.jimi.smt.eps_server.entity.UserExample.Criteria;
import com.jimi.smt.eps_server.entity.filler.UserToUserVOFiller;
import com.jimi.smt.eps_server.entity.vo.UserVO;
import com.jimi.smt.eps_server.mapper.UserMapper;
import com.jimi.smt.eps_server.service.ProgramService;
import com.jimi.smt.eps_server.service.UserService;
import com.jimi.smt.eps_server.util.SqlUtil;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserToUserVOFiller filler;
	@Autowired
	private ProgramService programService;
	
	
	@Override
	public String add(String id, Integer classType, String name, Integer type, String password) {
		if(userMapper.selectByPrimaryKey(id) != null) {
			return "failed_id_exist";
		}
		User user = new User();
		user.setId(id);
		user.setName(name);
		user.setType(type);
		user.setPassword("".equals(password) ? null : password);
		user.setCreateTime(programService.getCurrentTime());
		user.setClassType(classType);
		if(userMapper.insertSelective(user) == 1) {
			return "succeed";
		}
		return "failed_unknown";
	}

	
	@Override
	public String update(String id, Integer classType, String name, Integer type, String password, Boolean enabled) {
		if(userMapper.selectByPrimaryKey(id) == null) {
			return "failed_not_found";
		}
		User user = new User();
		user.setId("".equals(id) ? null : id);
		user.setName("".equals(name) ? null : name);
		user.setType(type);
		user.setPassword("".equals(password) ? null : password);
		user.setEnabled(enabled);
		user.setClassType(classType);
		if(userMapper.updateByPrimaryKeySelective(user) == 1){
			return "succeed";
		}
		return "failed_unknown";
	}

	
	@Override
	public List<UserVO> list(String id, Integer classType, String name, Integer type, String orderBy, Boolean enabled, Page page) {
		UserExample userExample = new UserExample();
		Criteria criteria = userExample.createCriteria();
		if (id != null && !id.equals("")) {
			criteria.andIdLike("%" + SqlUtil.escapeParameter(id) + "%");
		}
		if (name != null && !name.equals("")) {
			criteria.andNameLike("%" + SqlUtil.escapeParameter(name) + "%");
		}
		if(type != null) {
			criteria.andTypeEqualTo(type);
		}
		if(enabled != null) {
			criteria.andEnabledEqualTo(enabled);
		}
		if(classType != null) {
			criteria.andClassTypeEqualTo(classType);
		}
		userExample.setOrderByClause(orderBy);
		if(page != null) {
			page.setTotallyData(userMapper.countByExample(userExample));
			userExample.setLimitStart(page.getFirstIndex());
			userExample.setLimitSize(page.getPageSize());
		}
		return filler.fill(userMapper.selectByExample(userExample));
	}

			
	@Override
	public User selectUserById(String id) {	
		return userMapper.selectByPrimaryKey(id);		
	}
}
