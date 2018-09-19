package com.jimi.smt.eps_server.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jimi.smt.eps_server.entity.Config;
import com.jimi.smt.eps_server.entity.Line;
import com.jimi.smt.eps_server.mapper.ConfigMapper;
import com.jimi.smt.eps_server.mapper.LineMapper;
import com.jimi.smt.eps_server.service.ConfigService;

@Service
public class ConfigServiceImpl implements ConfigService {

	@Autowired
	private ConfigMapper configMappler;
	@Autowired
	private LineMapper lineMapper;

	
	@Override
	public List<Config> list() {
		List<Line> lines = lineMapper.selectByExample(null);
		List<Config> configs = configMappler.selectByExample(null);
		for (Config config : configs) {
			for (int j = 0; j < lines.size(); j++) {
				if (lines.get(j).getId().equals(config.getLine())) {
					config.setLine(Integer.parseInt(lines.get(j).getLine()));
				}
			}
		}
		return configs;
	}

	
	@Override
	public boolean set(List<Config> configs) {
		List<Line> lines = lineMapper.selectByExample(null);
		int i = 0;
		for (Config config : configs) {
			for (int j = 0; j < lines.size(); j++) {
				if (lines.get(j).getLine().equals(config.getLine().toString())) {
					config.setLine(lines.get(j).getId());
				}
			}
		}
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
