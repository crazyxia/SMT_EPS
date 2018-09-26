package com.jimi.smt.eps_server.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jimi.smt.eps_server.annotation.Open;
import com.jimi.smt.eps_server.annotation.Role;
import com.jimi.smt.eps_server.annotation.Role.RoleType;
import com.jimi.smt.eps_server.entity.Operation;
import com.jimi.smt.eps_server.entity.Page;
import com.jimi.smt.eps_server.entity.vo.ClientReport;
import com.jimi.smt.eps_server.entity.vo.DisplayReport;
import com.jimi.smt.eps_server.entity.vo.OperationReport;
import com.jimi.smt.eps_server.entity.vo.OperationReportSummary;
import com.jimi.smt.eps_server.entity.vo.PageVO;
import com.jimi.smt.eps_server.entity.vo.StockLogVO;
import com.jimi.smt.eps_server.service.OperationService;
import com.jimi.smt.eps_server.util.ResultUtil;
import com.jimi.smt.eps_server.util.ResultUtil2;

/**
 * 操作日志控制器
 * 
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
@Controller
@RequestMapping("/operation")
public class OperationController {

	@Autowired
	private OperationService operationService;

	
	// 分页查询客户报表
	@Role(RoleType.IPQC)
	@ResponseBody
	@RequestMapping("/listClientReport")
	public PageVO<ClientReport> listClientReportByPage(String client, String programNo, Integer line, String orderNo,
			String workOrderNo, String startTime, String endTime, Integer currentPage) {
		try {
			Page page = new Page();
			page.setCurrentPage(currentPage);
			PageVO<ClientReport> pageVO = new PageVO<ClientReport>();
			pageVO.setList(operationService.listClientReportByPage(client, programNo, line, orderNo, workOrderNo,
					startTime, endTime, page));
			pageVO.setPage(page);
			return pageVO;
		} catch (ParseException e) {
			ResultUtil.failed("日期格式不正确", e);
		}
		return null;
	}

	
	@Role(RoleType.IPQC)
	@RequestMapping("/downloadClientReport")
	public ResponseEntity<byte[]> downloadClientReport(String client, String programNo, Integer line, String orderNo,
			String workOrderNo, String startTime, String endTime) {
		try {
			return operationService.downloadClientReport(client, programNo, line, orderNo, workOrderNo, startTime,
					endTime);
		} catch (ParseException e) {
			ResultUtil.failed("日期格式不正确", e);
		} catch (IOException e) {
			ResultUtil.failed("IO异常", e);
		} catch (OutOfMemoryError e) {
			ResultUtil.failed("内存溢出", e);
		}
		return null;
	}

	
	@Role(RoleType.IPQC)
	@ResponseBody
	@RequestMapping("/listOperationReport")
	public List<OperationReport> listOperationReport(String operator, String client, Integer line, String workOrderNo,
			String startTime, String endTime, Integer type) {
		if (type == null) {
			ResultUtil.failed("参数不足");
			return null;
		}
		try {
			return operationService.listOperationReport(operator, client, line, workOrderNo, startTime, endTime, type);
		} catch (ParseException e) {
			ResultUtil.failed("日期格式不正确", e);
		}
		return null;
	}

	
	@Role(RoleType.IPQC)
	@ResponseBody
	@RequestMapping("/listOperationReportSummary")
	public List<OperationReportSummary> listOperationReportSummary(Integer line, String workOrderNo, String startTime,
			String endTime, Integer type) {
		if (type == null) {
			ResultUtil.failed("参数不足");
			return null;
		}
		try {
			return operationService.listOperationReportSummary(line, workOrderNo, startTime, endTime, type);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	@Role(RoleType.IPQC)
	@RequestMapping("/downloadOperationReport")
	public ResponseEntity<byte[]> downloadOperationReport(String operator, String client, Integer line,
			String workOrderNo, String startTime, String endTime, Integer type) {
		if (type == null) {
			ResultUtil.failed("参数不足");
			return null;
		}
		try {
			return operationService.downloadOperationReport(operator, client, line, workOrderNo, startTime, endTime,
					type);
		} catch (ParseException e) {
			ResultUtil.failed("日期格式不正确", e);
		} catch (IOException e) {
			ResultUtil.failed("IO异常", e);
		} catch (OutOfMemoryError e) {
			ResultUtil.failed("内存溢出", e);
		}
		return null;
	}

	
	@Role(RoleType.IPQC)
	@ResponseBody
	@RequestMapping("/listStockLogs")
	public List<StockLogVO> listStockLogs(String operator, String materialNo, String custom, String position,
			String startTime, String endTime) {
		try {
			return operationService.listStockLogs(operator, materialNo, custom, position, startTime, endTime);
		} catch (ParseException e) {
			ResultUtil.failed("日期格式不正确", e);
		}
		return null;
	}

	
	@Open
	@ResponseBody
	@RequestMapping("/listDisplayReport")
	public DisplayReport listDisplayReport(Integer line) {
		if (line == null) {
			ResultUtil.failed("产线不能为空");
			return null;
		}
		DisplayReport displayReport = operationService.listDisplayReport(line);
		return displayReport;
	}

	
	@Open
	@ResponseBody
	@RequestMapping("/add")
	public ResultUtil2 add(@RequestBody Operation operation) {
		ResultUtil2 resultUtil2 = new ResultUtil2();
		if (operationService.add(operation) == 1) {
			resultUtil2.setCode(1);
			resultUtil2.setMsg("操作成功");
		} else {
			resultUtil2.setCode(0);
			resultUtil2.setMsg("操作失败");
		}
		return resultUtil2;
	}
}
