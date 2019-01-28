package com.jimi.smt.eps_server.service.impl;

import java.util.ArrayList;
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
	
	/**
	 * WAREHOUSE_OPERATOR : 仓库操作员
	 */
	private static final int WAREHOUSE_OPERATOR = 0;
	/**
	 * LINE_OPERATOR : 产线操作员
	 */
	private static final int LINE_OPERATOR = 1;
	/**
	 * IPQC : IPQC
	 */
	private static final int IPQC = 2;
	/**
	 * PRODUCER : 生产管理员
	 */
	private static final int PRODUCER = 4;
	/**
	 * QC : 品质管理员
	 */
	private static final int QC = 5;
	/**
	 * WAREHOUSE_ADMINISTRATOR : 仓库管理员
	 */
	private static final int WAREHOUSE = 7;
	
	
	@Override
	public String add(String id, Integer classType, String name, Integer type, String password, Integer userType) {
		if(userMapper.selectByPrimaryKey(id) != null) {
			return "failed_id_exist";
		}
		User user = new User();
		user.setId(id);
		user.setName(name);
		switch (userType) {
		case PRODUCER:
			if (type != LINE_OPERATOR) {
				return "failed_wrong_type";
			}
			break;
		case QC:
			if (type != IPQC) {
				return "failed_wrong_type";
			}
			break;
		case WAREHOUSE:
			if (type != WAREHOUSE_OPERATOR) {
				return "failed_wrong_type";
			}
			break;
		default:
			break;
		}
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
	public List<UserVO> list(String id, Integer classType, String name, Integer type, String orderBy, Boolean enabled, Page page, Integer userType) {
		UserExample userExample = new UserExample();
		List<UserVO> userVOs = new ArrayList<>();
		Criteria criteria = userExample.createCriteria();
		if (id != null && !id.equals("")) {
			criteria.andIdLike("%" + SqlUtil.escapeParameter(id) + "%");
		}
		if (name != null && !name.equals("")) {
			criteria.andNameLike("%" + SqlUtil.escapeParameter(name) + "%");
		}
		if (type != null) {
			switch (userType) {
			case PRODUCER:
				if (type != LINE_OPERATOR) {
					return userVOs;
				}
				break;
			case QC:
				if (type != IPQC) {
					return userVOs;
				}
				break;
			case WAREHOUSE:
				if (type != WAREHOUSE_OPERATOR) {
					return userVOs;
				}
				break;
			default:
				break;
			}
			criteria.andTypeEqualTo(type);
		} else {
			switch (userType) {
			case PRODUCER:
				type = LINE_OPERATOR;
				criteria.andTypeEqualTo(type);
				break;
			case QC:
				type = IPQC;
				criteria.andTypeEqualTo(type);
				break;
			case WAREHOUSE:
				type = WAREHOUSE_OPERATOR;
				criteria.andTypeEqualTo(type);
				break;
			default:
				break;
			}
		}
		if (enabled != null) {
			criteria.andEnabledEqualTo(enabled);
		}
		if (classType != null) {
			criteria.andClassTypeEqualTo(classType);
		}
		userExample.setOrderByClause(orderBy);
		if (page != null) {
			userExample.setLimitStart(page.getFirstIndex());
			userExample.setLimitSize(page.getPageSize());
		}
		List<User> users = userMapper.selectByExample(userExample);
		Integer requireType = null;
		switch (userType) {
		case PRODUCER:
			requireType = LINE_OPERATOR;
			break;
		case QC:
			requireType = IPQC;
			break;
		case WAREHOUSE:
			requireType = WAREHOUSE_OPERATOR;
			break;
		default:
			break;
		}
		if (requireType != null) {
			for (User user : users) {
				if (user.getType() != type) {
					return userVOs;
				}
			}
		}
		return filler.fill(users);
	}

			
	@Override
	public User selectUserById(String id) {	
		return userMapper.selectByPrimaryKey(id);		
	}
}
