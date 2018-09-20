package com.jimi.smt.eps_server.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jimi.smt.eps_server.entity.Config;
import com.jimi.smt.eps_server.entity.Line;
import com.jimi.smt.eps_server.entity.filler.ConfigToConfigVOFiller;
import com.jimi.smt.eps_server.entity.vo.ConfigVO;
import com.jimi.smt.eps_server.mapper.ConfigMapper;
import com.jimi.smt.eps_server.mapper.LineMapper;
import com.jimi.smt.eps_server.service.ConfigService;

@Service
public class ConfigServiceImpl implements ConfigService {

	@Autowired
	private ConfigMapper configMappler;
	@Autowired
	private LineMapper lineMapper;
	@Autowired
	private ConfigToConfigVOFiller configToConfigVOFiller;

	
	@Override
	public List<ConfigVO> list() {
		List<Line> lines = lineMapper.selectByExample(null);
		List<Config> configs = configMappler.selectByExample(null);
		List<ConfigVO> configVOs = new ArrayList<>();
		for (Config config : configs) {
			configVOs.add(configToConfigVOFiller.fill(config));
		}
		for (ConfigVO configVO : configVOs) {
			for (Line line : lines) {
				if (line.getId() == configVO.getLine()) {
					configVO.setLineName(line.getLine());
				}
			}
		}
		return configVOs;
	}

	
	@Override
	public boolean set(List<Config> configs) {
		int i = 0;
		for (Config config : configs) {
			i += configMappler.updateByPrimaryKey(config);
		}
		if (i == configs.size()) {
			return true;
		} else {
			return false;
		}
	}

}
