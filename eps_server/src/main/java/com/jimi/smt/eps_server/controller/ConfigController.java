package com.jimi.smt.eps_server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.jimi.smt.eps_server.annotation.Log;
import com.jimi.smt.eps_server.annotation.Open;
import com.jimi.smt.eps_server.entity.Config;
import com.jimi.smt.eps_server.entity.vo.ConfigVO;
import com.jimi.smt.eps_server.service.ConfigService;
import com.jimi.smt.eps_server.util.ResultUtil;

/**
 * 配置控制器
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
@Controller
@RequestMapping("/config")
public class ConfigController {

	@Autowired
	private ConfigService configService;

	
	@Log
	@ResponseBody
	@RequestMapping("/set")
	public ResultUtil set(String configs) {
		List<Config> configList = JSONObject.parseArray(configs, Config.class);
		if (configList != null && !configList.isEmpty()) {
			if (configService.set(configList)) {
				return ResultUtil.succeed();
			} else {
				return ResultUtil.failed();
			}
		}
		return ResultUtil.failed("参数JSON格式不对");
	}

	
	@Open
	@ResponseBody
	@RequestMapping("/list")
	public List<ConfigVO> list() {
		return configService.list();
	}

}
