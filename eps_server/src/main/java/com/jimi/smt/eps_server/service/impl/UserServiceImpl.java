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
import com.jimi.smt.eps_server.entity.vo.PageVO;
import com.jimi.smt.eps_server.entity.vo.UserVO;
import com.jimi.smt.eps_server.mapper.UserMapper;
import com.jimi.smt.eps_server.service.ProgramService;
import com.jimi.smt.eps_server.service.UserService;
import com.jimi.smt.eps_server.util.ResultUtil;
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
	 * IPQC_OPERATOR : IPQC操作员
	 */
	private static final int IPQC_OPERATOR = 2;
	/**
	 * PRODUCER_ADMINISTRATOR : 生产管理员
	 */
	private static final int PRODUCER_ADMINISTRATOR = 4;
	/**
	 * QC_ADMINISTRATOR : 品质管理员
	 */
	private static final int QC_ADMINISTRATOR = 5;
	/**
	 * WAREHOUSE_ADMINISTRATOR : 仓库管理员
	 */
	private static final int WAREHOUSE_ADMINISTRATOR = 7;
	
	
	@Override
	public String add(String id, Integer classType, String name, Integer type, String password, Integer userType) {
		if (userMapper.selectByPrimaryKey(id) != null) {
			return "failed_id_exist";
		}
		User user = new User();
		user.setId(id);
		user.setName(name);
		switch (userType) {
		case PRODUCER_ADMINISTRATOR:
			if (type != LINE_OPERATOR) {
				return "failed_wrong_type";
			}
			break;
		case QC_ADMINISTRATOR:
			if (type != IPQC_OPERATOR) {
				return "failed_wrong_type";
			}
			break;
		case WAREHOUSE_ADMINISTRATOR:
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
		if (userMapper.insertSelective(user) == 1) {
			return "succeed";
		}
		return "failed_unknown";
	}

	
	@Override
	public String update(String id, Integer classType, String name, Integer type, String password, Boolean enabled, Integer userType) {
		if (userMapper.selectByPrimaryKey(id) == null) {
			return "failed_not_found";
		}
		User user = new User();
		user.setId("".equals(id) ? null : id);
		user.setName("".equals(name) ? null : name);
		switch (userType) {
		case PRODUCER_ADMINISTRATOR:
			if (type != LINE_OPERATOR) {
				return "failed_wrong_type";
			}
			break;
		case QC_ADMINISTRATOR:
			if (type != IPQC_OPERATOR) {
				return "failed_wrong_type";
			}
			break;
		case WAREHOUSE_ADMINISTRATOR:
			if (type != WAREHOUSE_OPERATOR) {
				return "failed_wrong_type";
			}
			break;
		default:
			break;
		}
		user.setType(type);
		user.setPassword("".equals(password) ? null : password);
		user.setEnabled(enabled);
		user.setClassType(classType);
		if (userMapper.updateByPrimaryKeySelective(user) == 1) {
			return "succeed";
		}
		return "failed_unknown";
	}


	@Override
	public ResultUtil list(String id, Integer classType, String name, Integer type, String orderBy, Boolean enabled, Integer currentPage, Integer pageSize, Integer userType) {
		UserExample userExample = new UserExample();
		Page page = new Page();
		page.setCurrentPage(currentPage);
		page.setPageSize(pageSize);
		PageVO<UserVO> pageVO = new PageVO<UserVO>();
		pageVO.setPage(page);
		ResultUtil resultUtil = new ResultUtil(null, null);
		List<UserVO> userVOs = new ArrayList<>();
		Criteria criteria = userExample.createCriteria();
		if (id != null && !id.equals("")) {
			criteria.andIdLike("%" + SqlUtil.escapeParameter(id) + "%");
		}
		if (name != null && !name.equals("")) {
			criteria.andNameLike("%" + SqlUtil.escapeParameter(name) + "%");
		}
		if (type != null) {
			if ((userType.equals(PRODUCER_ADMINISTRATOR) && type != LINE_OPERATOR) || (userType.equals(QC_ADMINISTRATOR) && type != IPQC_OPERATOR) || (userType.equals(WAREHOUSE_ADMINISTRATOR) && type != WAREHOUSE_OPERATOR)) {
				pageVO.setList(userVOs);
				resultUtil.setData(pageVO);
				resultUtil.setResult("400");
				return resultUtil;
			}
			criteria.andTypeEqualTo(type);
		} else {
			switch (userType) {
			case PRODUCER_ADMINISTRATOR:
				type = LINE_OPERATOR;
				criteria.andTypeEqualTo(type);
				break;
			case QC_ADMINISTRATOR:
				type = IPQC_OPERATOR;
				criteria.andTypeEqualTo(type);
				break;
			case WAREHOUSE_ADMINISTRATOR:
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
		case PRODUCER_ADMINISTRATOR:
			requireType = LINE_OPERATOR;
			break;
		case QC_ADMINISTRATOR:
			requireType = IPQC_OPERATOR;
			break;
		case WAREHOUSE_ADMINISTRATOR:
			requireType = WAREHOUSE_OPERATOR;
			break;
		default:
			break;
		}
		if (requireType != null) {
			for (User user : users) {
				if (user.getType() != type) {
					pageVO.setList(userVOs);
					resultUtil.setData(pageVO);
					resultUtil.setResult("400");
					return resultUtil;
				}
			}
		}
		pageVO.setList(filler.fill(users));
		resultUtil.setData(pageVO);
		resultUtil.setResult("401");
		return resultUtil;
	}

			
	@Override
	public User selectUserById(String id) {
		return userMapper.selectByPrimaryKey(id);
	}
}
