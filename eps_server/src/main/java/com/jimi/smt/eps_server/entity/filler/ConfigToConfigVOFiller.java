package com.jimi.smt.eps_server.entity.filler;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.jimi.smt.eps_server.entity.Config;
import com.jimi.smt.eps_server.entity.vo.ConfigVO;
import com.jimi.smt.eps_server.util.EntityFieldFiller;

@Component
public class ConfigToConfigVOFiller extends EntityFieldFiller<Config, ConfigVO>{

	@Override
	public ConfigVO fill(Config config) {
		ConfigVO configVO = new ConfigVO();
		BeanUtils.copyProperties(config, configVO);		
		return configVO;
	}
}
