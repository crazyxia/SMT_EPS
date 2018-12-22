package com.jimi.smt.eps_server.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jimi.smt.eps_server.entity.MaterialInfo;
import com.jimi.smt.eps_server.entity.MaterialInfoExample;
import com.jimi.smt.eps_server.entity.MaterialInfoExample.Criteria;
import com.jimi.smt.eps_server.entity.Page;
import com.jimi.smt.eps_server.mapper.MaterialInfoMapper;
import com.jimi.smt.eps_server.service.MaterialService;
import com.jimi.smt.eps_server.util.SqlUtil;

@Service
public class MaterialServiceImpl implements MaterialService {

	@Autowired
	private MaterialInfoMapper materialInfoMapper;

	
	@Override
	public String add(String materialNo, Integer perifdOfValidity) {

		MaterialInfoExample materialInfoExample = new MaterialInfoExample();
		Criteria criteria = materialInfoExample.createCriteria();
		criteria.andMaterialNoEqualTo(materialNo);
		criteria.andEnableEqualTo(1);
		List<MaterialInfo> list = materialInfoMapper.selectByExample(materialInfoExample);
		if (!list.isEmpty()) {
			return "failed_materialNo_exist";
		}
		MaterialInfo materialInfo = new MaterialInfo();
		materialInfo.setMaterialNo(materialNo);
		materialInfo.setPerifdOfValidity(perifdOfValidity);
		materialInfo.setEnable(1);
		if (materialInfoMapper.insertSelective(materialInfo) == 1) {
			return "succeed";
		}
		return "failed_unknown";

	}

	
	@Override
	public String update(Integer id, Integer perifdOfValidity) {

		MaterialInfo materialInfo = materialInfoMapper.selectByPrimaryKey(id);
		if (materialInfo == null || materialInfo.getEnable() != 1) {
			return "failed_not_found";
		}
		materialInfo.setPerifdOfValidity(perifdOfValidity);
		if (materialInfoMapper.updateByPrimaryKeySelective(materialInfo) == 1) {
			return "succeed";
		}
		return "failed_unknown";

	}

	
	@Override
	public String delete(Integer id) {

		MaterialInfo materialInfo = materialInfoMapper.selectByPrimaryKey(id);
		if (materialInfo == null || materialInfo.getEnable() != 1) {
			return "failed_not_found";
		}
		materialInfo.setEnable(0);
		if (materialInfoMapper.updateByPrimaryKeySelective(materialInfo) == 1) {
			return "succeed";
		}
		return "failed_unknown";
	}

	
	@Override
	public List<MaterialInfo> list(Integer id, String materialNo, Integer perifdOfValidity, String orderBy, Page page) {

		MaterialInfoExample materialInfoExample = new MaterialInfoExample();
		Criteria criteria = materialInfoExample.createCriteria();
		if (id != null) {
			criteria.andIdEqualTo(id);
		}
		if (materialNo != null && !"".equals(materialNo)) {
			criteria.andMaterialNoLike("%" + SqlUtil.escapeParameter(materialNo) + "%");
		}
		if (perifdOfValidity != null) {
			criteria.andPerifdOfValidityEqualTo(perifdOfValidity);
		}
		criteria.andEnableEqualTo(1);
		if (orderBy != null && !orderBy.equals("")) {
			materialInfoExample.setOrderByClause(orderBy);
		}
		if (page != null) {
			page.setTotallyData(materialInfoMapper.countByExample(materialInfoExample));
			materialInfoExample.setLimitStart(page.getFirstIndex());
			materialInfoExample.setLimitSize(page.getPageSize());
		}
		return materialInfoMapper.selectByExample(materialInfoExample);
	}

}
