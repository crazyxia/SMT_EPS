package com.jimi.smt.eps_server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jimi.smt.eps_server.annotation.Open;
import com.jimi.smt.eps_server.entity.StatusDetail;
import com.jimi.smt.eps_server.entity.vo.StatusDetailsVO;
import com.jimi.smt.eps_server.service.StatusService;


@Controller
@RequestMapping("/")
public class StatusDetailsController {
	
	@Autowired
	StatusService statusService;
	
	
	/**@author HCJ
	 * 根据产线查询每个小时的操作情况
	 * @date 2019年1月29日 下午5:37:21
	 */
	@Open
	@ResponseBody
	@RequestMapping("/getStatusDetails")
	public StatusDetailsVO ListStatusDetailsByHour(Integer line) {
		return statusService.ListStatusDetailsByHour(line);
	}
	
	
	/**@author HCJ
	 * 实时监控每条产线一天内的操作情况
	 * @date 2019年1月29日 下午5:37:48
	 */
	@Open
	@ResponseBody
	@RequestMapping("/getStatusDetailsByDay")
	public List<StatusDetail> ListStatusDetailsByDay() {
		return statusService.ListStatusDetailsByDay();
	}
}
