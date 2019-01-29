package com.jimi.smt.eps_server.entity.filler;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jimi.smt.eps_server.entity.Config;
import com.jimi.smt.eps_server.entity.Line;
import com.jimi.smt.eps_server.entity.vo.ConfigVO;
import com.jimi.smt.eps_server.mapper.LineMapper;
import com.jimi.smt.eps_server.util.EntityFieldFiller;

@Component
public class ConfigToConfigVOFiller extends EntityFieldFiller<Config, ConfigVO>{

	@Autowired
	private LineMapper lineMapper;

	
	@Override
	public ConfigVO fill(Config config) {
		List<Line> lines = lineMapper.selectByExample(null);
		ConfigVO configVO = new ConfigVO();
		BeanUtils.copyProperties(config, configVO);
		for (Line line : lines) {
			if (line.getId().equals(configVO.getLine())) {
				configVO.setLineName(line.getLine());
			}
		}
		return configVO;
	}
}
