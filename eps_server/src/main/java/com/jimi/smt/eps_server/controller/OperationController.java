package com.jimi.smt.eps_server.controller;

import java.io.IOException;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jimi.smt.eps_server.annotation.Log;
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


	/**@author HCJ
	 * 查询客户报表
	 * @date 2019年1月29日 下午5:31:01
	 */
	@Log
	@Role(RoleType.IPQC)
	@ResponseBody
	@RequestMapping("/listClientReport")
	public PageVO<ClientReport> listClientReport(String client, String programNo, Integer line, String orderNo, String workOrderNo, String startTime, String endTime, Integer currentPage, Integer pageSize) {
		Page page = new Page();
		page.setCurrentPage(currentPage);
		page.setPageSize(pageSize);
		PageVO<ClientReport> pageVO = new PageVO<ClientReport>();
		pageVO.setPage(page);
		try {		
			pageVO.setList(operationService.listClientReport(client, programNo, line, orderNo, workOrderNo, startTime, endTime, page));		
		} catch (ParseException e) {
			ResultUtil.failed("日期格式不正确", e);
		}
		return pageVO;
	}

	
	/**@author HCJ
	 * 下载客户报表
	 * @date 2019年1月29日 下午5:31:19
	 */
	@Role(RoleType.IPQC)
	@RequestMapping("/downloadClientReport")
	public ResponseEntity<byte[]> downloadClientReport(String client, String programNo, Integer line, String orderNo, String workOrderNo, String startTime, String endTime, Integer currentPage, Integer pageSize) {
		Page page = new Page();
		page.setCurrentPage(currentPage);
		page.setPageSize(pageSize);
		try {
			return operationService.downloadClientReport(client, programNo, line, orderNo, workOrderNo, startTime, endTime, page);
		} catch (ParseException e) {
			ResultUtil.failed("日期格式不正确", e);
		} catch (IOException e) {
			ResultUtil.failed("IO异常", e);
		} catch (OutOfMemoryError e) {
			ResultUtil.failed("内存溢出", e);
		}
		return null;
	}

	
	/**@author HCJ
	 * 查询操作报表详情
	 * @date 2019年1月29日 下午5:31:39
	 */
	@Log
	@Role(RoleType.IPQC)
	@ResponseBody
	@RequestMapping("/listOperationReport")
	public PageVO<OperationReport> listOperationReport(String operator, Integer line, String workOrderNo, String startTime, String endTime, Integer type, Integer currentPage, Integer pageSize) {
		Page page = new Page();
		page.setCurrentPage(currentPage);
		page.setPageSize(pageSize);
		PageVO<OperationReport> pageVO = new PageVO<OperationReport>();
		pageVO.setPage(page);
		try {
			pageVO.setList(operationService.listOperationReport(operator, line, workOrderNo, startTime, endTime, type, page));
		} catch (ParseException e) {
			ResultUtil.failed("日期格式不正确", e);
		}
		return pageVO;
	}

	
	/**@author HCJ
	 * 查询操作报表
	 * @date 2019年1月29日 下午5:31:55
	 */
	@Log
	@Role(RoleType.IPQC)
	@ResponseBody
	@RequestMapping("/listOperationReportSummary")
	public PageVO<OperationReportSummary> listOperationReportSummary(Integer line, String workOrderNo, String startTime, String endTime, Integer type, Integer currentPage, Integer pageSize) {
		Page page = new Page();
		page.setCurrentPage(currentPage);
		page.setPageSize(pageSize);
		PageVO<OperationReportSummary> pageVO = new PageVO<OperationReportSummary>();
		pageVO.setPage(page);
		try {
			pageVO.setList(operationService.listOperationReportSummary(line, workOrderNo, startTime, endTime, type, page));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return pageVO;
	}

	
	/**@author HCJ
	 * 下载操作报表
	 * @date 2019年1月29日 下午5:32:09
	 */
	@Role(RoleType.IPQC)
	@RequestMapping("/downloadOperationReport")
	public ResponseEntity<byte[]> downloadOperationReport(Integer line, String workOrderNo, String startTime, String endTime, Integer type, Integer currentPage, Integer pageSize) {
		Page page = new Page();
		page.setCurrentPage(currentPage);
		page.setPageSize(pageSize);
		try {
			return operationService.downloadOperationReport(line, workOrderNo, startTime, endTime, type, page);
		} catch (ParseException e) {
			ResultUtil.failed("日期格式不正确", e);
		} catch (IOException e) {
			ResultUtil.failed("IO异常", e);
		} catch (OutOfMemoryError e) {
			ResultUtil.failed("内存溢出", e);
		}
		return null;
	}

	
	/**@author HCJ
	 * 查询仓库出入库记录
	 * @date 2019年1月29日 下午5:32:24
	 */
	@Log
	@Role(RoleType.IPQC)
	@ResponseBody
	@RequestMapping("/listStockLogs")
	public PageVO<StockLogVO> listStockLogs(String operator, String materialNo, String custom, String position, String startTime, String endTime, Integer currentPage, Integer pageSize) {
		Page page = new Page();
		page.setCurrentPage(currentPage);
		page.setPageSize(pageSize);
		PageVO<StockLogVO> pageVO = new PageVO<StockLogVO>();
		pageVO.setPage(page);
		try {
			pageVO.setList(operationService.listStockLogs(operator, materialNo, custom, position, startTime, endTime, page));
		} catch (ParseException e) {
			ResultUtil.failed("日期格式不正确", e);
		}
		return pageVO;
	}

	
	/**@author HCJ
	 * 查询实时显示报表
	 * @param line 产线id
	 * @date 2019年1月29日 下午5:32:59
	 */
	@Log
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

	
	/**@author HCJ
	 * 添加操作记录
	 * @date 2019年1月29日 下午5:33:53
	 */
	@Log
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
