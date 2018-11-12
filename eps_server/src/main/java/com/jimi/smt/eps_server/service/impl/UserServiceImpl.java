package com.jimi.smt.eps_server.service.impl;

import java.util.Date;
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
import com.jimi.smt.eps_server.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserToUserVOFiller filler;
	
	
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
		user.setCreateTime(new Date());
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
		user.setId(id.equals("") ? null : id);
		user.setName(name.equals("") ? null : name);
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
	public List<UserVO> list(String id, Integer classType, String name, Integer type, String orderBy, Boolean enabled, Page page,String password) {
		UserExample userExample = new UserExample();
		Criteria criteria = userExample.createCriteria();
		if (id != null && !id.equals("")) {
			criteria.andIdLike("%" + id + "%");
		}
		if (name != null && !name.equals("")) {
			criteria.andNameLike("%" + name + "%");
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
		if(password != null) {
			criteria.andPasswordIsNotNull();
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
	
	@Override
	public String updatePassword(String id, String password) {
		// TODO Auto-generated method stub
		User user = new User();
		user.setId(id.equals("") ? null : id);
		user.setPassword("".equals(password) ? null : password);
		if(userMapper.updatePasswordByPrimaryKey(user) == 1){
			return "succeed";
		}
		return "failed_unknown";
	}
}
