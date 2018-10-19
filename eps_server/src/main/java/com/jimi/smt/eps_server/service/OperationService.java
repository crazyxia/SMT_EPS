package com.jimi.smt.eps_server.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.jimi.smt.eps_server.entity.Operation;
import com.jimi.smt.eps_server.entity.Page;
import com.jimi.smt.eps_server.entity.vo.ClientReport;
import com.jimi.smt.eps_server.entity.vo.DisplayReport;
import com.jimi.smt.eps_server.entity.vo.OperationReport;
import com.jimi.smt.eps_server.entity.vo.OperationReportSummary;
import com.jimi.smt.eps_server.entity.vo.StockLogVO;

/**
 * 操作日志服务接口
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public interface OperationService {
		
	
	/**
	 * 根据条件分页列出列表
	 * @param client
	 * @param programNo
	 * @param line
	 * @param orderNo
	 * @param workOrderNo
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws ParseException
	 */
	List<ClientReport> listClientReport(String client, String programNo, Integer line, String orderNo, String workOrderNo, String startTime, String endTime, Page page) throws ParseException;

	
	/**
	 * 根据条件生成并下载excel
	 * @return
	 * @throws ParseException 
	 * @throws IOException 
	 */
	ResponseEntity<byte[]> downloadClientReport(String client, String programNo, Integer line, String orderNo, String workOrderNo, String startTime, String endTime) throws ParseException, IOException;

	
	/**
	 * 根据线号显示实时展示报表
	 * @param line
	 * @return
	 */
	DisplayReport listDisplayReport(Integer line);

	
	/**
	 * 根据条件列出操作报表
	 * @return
	 * @throws ParseException 
	 */
	List<OperationReport> listOperationReport(String operator, String client, Integer line, String workOrderNo, String startTime, String endTime , Integer type, Page page) throws ParseException;
	
	
	/**
	 * 根据条件生成并下载excel
	 * @return
	 * @throws ParseException 
	 * @throws IOException 
	 */
	ResponseEntity<byte[]> downloadOperationReport(String operator, String client, Integer line, String workOrderNo, String startTime, String endTime , Integer type) throws ParseException, IOException;

	
	/**
	 * 根据条件列出操作报表概要（已按操作员+工单+线号折叠分组），按线号升序
	 * @param line
	 * @param workOrderNo
	 * @param startTime
	 * @param endTime
	 * @param type
	 * @return
	 * @throws ParseException 
	 */
	List<OperationReportSummary> listOperationReportSummary(Integer line, String workOrderNo, String startTime, String endTime, Integer type, Page page) throws ParseException;
	
	
	/**
	 * 根据条件查询仓库出入库记录
	 * @param operator
	 * @param materialNo
	 * @param custom
	 * @param position
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws ParseException 
	 */
	List<StockLogVO> listStockLogs(String operator, String materialNo, String custom, String position, String startTime, String endTime, Page page) throws ParseException;
	
	
	/**@author HCJ
	 * 添加操作日志
	 * @method add
	 * @param operation
	 * @return
	 * @return Integer
	 * @date 2018年9月25日 下午4:29:03
	 */
	Integer add(Operation operation);
			
}
