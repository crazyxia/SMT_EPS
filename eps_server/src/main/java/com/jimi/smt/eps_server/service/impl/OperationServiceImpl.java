package com.jimi.smt.eps_server.service.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.jimi.smt.eps_server.entity.Operation;
import com.jimi.smt.eps_server.entity.OperationExample;
import com.jimi.smt.eps_server.entity.Page;
import com.jimi.smt.eps_server.entity.StockLogExample;
import com.jimi.smt.eps_server.entity.bo.OperationReportSummaryKey;
import com.jimi.smt.eps_server.entity.bo.OperationReportSummaryValue;
import com.jimi.smt.eps_server.entity.bo.ReportParameter;
import com.jimi.smt.eps_server.entity.filler.OperationToClientReportFiller;
import com.jimi.smt.eps_server.entity.filler.OperationToOperationReportFiller;
import com.jimi.smt.eps_server.entity.filler.StockLogToStockLogVOFiller;
import com.jimi.smt.eps_server.entity.vo.ClientReport;
import com.jimi.smt.eps_server.entity.vo.DisplayReport;
import com.jimi.smt.eps_server.entity.vo.DisplayReportItem;
import com.jimi.smt.eps_server.entity.vo.OperationReport;
import com.jimi.smt.eps_server.entity.vo.OperationReportSummary;
import com.jimi.smt.eps_server.entity.vo.StockLogVO;
import com.jimi.smt.eps_server.mapper.OperationMapper;
import com.jimi.smt.eps_server.mapper.StockLogMapper;
import com.jimi.smt.eps_server.service.LineService;
import com.jimi.smt.eps_server.service.OperationService;
import com.jimi.smt.eps_server.util.ExcelSpringHelper;

@Service
public class OperationServiceImpl implements OperationService {

	@Autowired
	private OperationMapper operationMapper;
	@Autowired
	private StockLogMapper stockLogMapper;
	@Autowired
	private LineService lineService;
	@Autowired
	private OperationToClientReportFiller operationToClientReportFiller;
	@Autowired
	private OperationToOperationReportFiller operationToOperationReportFiller;
	@Autowired
	private StockLogToStockLogVOFiller stockLogToStockLogVOFiller;
		
	
	/** 
	 * 分页查询客户报表
	 * @param client
	 * @param programNo
	 * @param line
	 * @param orderNo
	 * @param workOrderNo
	 * @param startTime
	 * @param endTime
	 * @param page
	 * @throws ParseException 
	 */
	@Override
	public synchronized List<ClientReport> listClientReport(String client, String programNo, Integer line, String orderNo, String workOrderNo, String startTime, String endTime, Page page) throws ParseException {
		operationToClientReportFiller.init();
		List<ClientReport> clientReports = new ArrayList<ClientReport>();
		ReportParameter parameter = new ReportParameter();
		// 筛选时间
		if (startTime != null && !startTime.equals("")) {
			parameter.setStartTime(Timestamp.valueOf(startTime));
		}
		if (endTime != null && !endTime.equals("")) {
			parameter.setEndTime(Timestamp.valueOf(endTime));
		}
		// 筛选工单号
		if (workOrderNo != null && !workOrderNo.equals("")) {
			parameter.setWorkOrderNo("%" + workOrderNo + "%");
		}
		// 筛选线别
		if (line != null) {
			parameter.setLine(line);
		}
		// 筛选客户
		if (client != null && !client.equals("")) {
			parameter.setClient("%" + client + "%");
		}
		// 筛选程序表编号
		if (programNo != null && !programNo.equals("")) {
			parameter.setProgramNo("%" + programNo + "%");
		}
		// 筛选订单号
		if (orderNo != null && !orderNo.equals("")) {
			parameter.setOrderNo("%" + orderNo + "%");
		}
		if (page != null) {
			// 获取总条数
			page.setTotallyData(operationMapper.countByClientParameter(parameter));
			// 设置取值位置和条数
			parameter.setFirstIndex(page.getFirstIndex());
			parameter.setPageSize(page.getPageSize());
		}
		List<Operation> operations = operationMapper.selectByClientParameter(parameter);
		// 匹配
		for (Operation operation : operations) {
			// 把操作日志转化为客户报告
			clientReports.add(operationToClientReportFiller.fill(operation));
		}
		return clientReports;
	}

	
	@Override
	public ResponseEntity<byte[]> downloadClientReport(String client, String programNo, Integer line, String orderNo, String workOrderNo, String startTime, String endTime) throws ParseException, IOException, OutOfMemoryError {
		ExcelSpringHelper helper = ExcelSpringHelper.create(true);
		// 获取数据
		List<ClientReport> clientReports = listClientReport(client, programNo, line, orderNo, workOrderNo, startTime, endTime, null);
		helper.fill(clientReports);
		return helper.getDownloadEntity("客户报表.xlsx", true);
	}

	
	@SuppressWarnings("deprecation")
	@Override
	public DisplayReport listDisplayReport(Integer line) {
		DisplayReport displayReport = new DisplayReport();
		// 日期筛选（过去24小时）
		OperationExample operationExample = new OperationExample();
		LocalDateTime today = LocalDateTime.now();
		LocalDateTime yesterday = today.plusDays(-1);
		Date t = Date.from(today.atZone(ZoneId.systemDefault()).toInstant());
		Date y = Date.from(yesterday.atZone(ZoneId.systemDefault()).toInstant());
		operationExample.createCriteria().andTimeGreaterThanOrEqualTo(y).andTimeLessThanOrEqualTo(t).andLineEqualTo(line);
		List<Operation> operations = operationMapper.selectByExample(operationExample);
		// 遍历
		for (Operation operation : operations) {
			DisplayReportItem item = null;
			int hour = new Date().getHours() - operation.getTime().getHours();
			if (hour < 0) {
				hour = 24 + hour;
			}
			switch (operation.getType()) {
			case 0:
				item = displayReport.getFeed().get(hour);
				break;
			case 1:
				item = displayReport.getChanges().get(hour);
				break;
			case 2:
				item = displayReport.getSomes().get(hour);
				break;
			case 3:
				item = displayReport.getAlls().get(hour);
				break;
			default:
				continue;
			}
			if (operation.getResult().equals("PASS")) {
				item.setSuc(item.getSuc() + 1);
			} else {
				item.setFail(item.getFail() + 1);
			}
			item.setTotal(item.getTotal() + 1);
			// 记录操作者
			item.getOperators().add(operation.getOperator());
		}

		return displayReport;
	}

	
	/** 
	 * 分页查询操作报表
	 * @param operator
	 * @param client
	 * @param line
	 * @param workOrderNo
	 * @param startTime
	 * @param endTime
	 * @param type
	 * @param page
	 * @throws ParseException 
	 */
	@Override
	public synchronized List<OperationReport> listOperationReport(String operator, String client, Integer line, String workOrderNo, String startTime, String endTime, Integer type, Page page) throws ParseException {
		operationToOperationReportFiller.init();
		List<OperationReport> operationReports = new ArrayList<OperationReport>();
		ReportParameter parameter = new ReportParameter();
		// 筛选时间
		if (startTime != null && !startTime.equals("")) {
			parameter.setStartTime(Timestamp.valueOf(startTime));
		}
		if (endTime != null && !endTime.equals("")) {
			parameter.setEndTime(Timestamp.valueOf(endTime));
		}
		// 筛选工单号
		if (workOrderNo != null && !workOrderNo.equals("")) {
			parameter.setWorkOrderNo("%" + workOrderNo + "%");
		}
		// 筛选线别
		if (line != null) {
			parameter.setLine(line);
		}
		// 筛选操作员
		if (operator != null && !operator.equals("")) {
			parameter.setOperator("%" + operator + "%");
		}
		// 过滤类型
		if (type != null) {
			parameter.setType(type);
		}
		// 筛选客户
		if (client != null && !client.equals("")) {
			parameter.setClient("%" + client + "%");
		}
		if (page != null) {
			page.setTotallyData(operationMapper.countByOperationParameter(parameter));
			parameter.setFirstIndex(page.getFirstIndex());
			parameter.setPageSize(page.getPageSize());
		}
		List<Operation> operations = operationMapper.selectByOperationParameter(parameter);
		// 包装
		for (Operation operation : operations) {
			// 把操作日志转化为操作报告
			operationReports.add(operationToOperationReportFiller.fill(operation));
		}		
		return operationReports;
	}

	
	@Override
	public ResponseEntity<byte[]> downloadOperationReport(String operator, String client, Integer line, String workOrderNo, String startTime, String endTime, Integer type) throws ParseException, IOException, OutOfMemoryError {
		List<OperationReport> operationReports = listOperationReport(operator, client, line, workOrderNo, startTime, endTime, type, null);
		ExcelSpringHelper helper = ExcelSpringHelper.create();
		// 解析操作类型
		String title = null;
		switch (type) {
		case 0:
			title = "SMT上料报表";
			break;
		case 1:
			title = "SMT换料报表";
			break;
		case 2:
			title = "SMT抽检报表";
			break;
		case 3:
			title = "SMT全检报表";
			break;
		case 4:
			title = "SMT仓库发料报表";
			break;
		default:
			break;
		}
		helper.fill(operationReports, title);
		return helper.getDownloadEntity(title + ".xls", true);
	}

	
	@Override
	public List<OperationReportSummary> listOperationReportSummary(Integer line, String workOrderNo, String startTime, String endTime, Integer type, Page page) throws ParseException {
		// 筛选数据
		List<OperationReport> operationReports = listOperationReport(null, null, line, workOrderNo, startTime, endTime,
				type, null);
		// 创建分组Map，按OperationReportSummaryKey分组，值为result的与运算
		Map<OperationReportSummaryKey, OperationReportSummaryValue> map = new HashMap<OperationReportSummaryKey, OperationReportSummaryValue>();
		for (OperationReport operationReport : operationReports) {
			// 创建Key
			OperationReportSummaryKey key = new OperationReportSummaryKey();
			key.setLine(operationReport.getLine());
			key.setWorkOrderNo(operationReport.getWorkOrderNo());
			key.setOperator(operationReport.getOperator());
			// 获取该key之前的value
			OperationReportSummaryValue value = map.get(key);
			if (value == null) {
				value = new OperationReportSummaryValue();
			}
			boolean result = operationReport.getResult().equals("PASS") ? true : false;
			if (result == true) {
				value.setPassCount(value.getPassCount() + 1);
			} else {
				value.setFailCount(value.getFailCount() + 1);
			}
			map.put(key, value);
		}
		List<OperationReportSummaryKey> operationReportSummaryKeys = new ArrayList<>();
		for (Entry<OperationReportSummaryKey, OperationReportSummaryValue> entry : map.entrySet()) {
			operationReportSummaryKeys.add(entry.getKey());
		}
		page.setTotallyData(map.size());
		List<OperationReportSummary> operationReportSummaries = new ArrayList<OperationReportSummary>();
		if (operationReportSummaryKeys.size() - page.getFirstIndex() > page.getPageSize()) {
			for (int i = page.getFirstIndex(); i < operationReportSummaryKeys.size(); i++) {
				OperationReportSummary summary = new OperationReportSummary();
				summary.setLine(operationReportSummaryKeys.get(i).getLine());
				summary.setWorkOrderNo(operationReportSummaryKeys.get(i).getWorkOrderNo());
				summary.setOperator(operationReportSummaryKeys.get(i).getOperator());
				summary.setPassCount(map.get(operationReportSummaryKeys.get(i)).getPassCount());
				summary.setFailCount(map.get(operationReportSummaryKeys.get(i)).getFailCount());
				operationReportSummaries.add(summary);
				if (operationReportSummaries.size() == page.getPageSize()) {
					break;
				}
			}
		} else {
			for (int i = page.getFirstIndex(); i < operationReportSummaryKeys.size(); i++) {
				OperationReportSummary summary = new OperationReportSummary();
				summary.setLine(operationReportSummaryKeys.get(i).getLine());
				summary.setWorkOrderNo(operationReportSummaryKeys.get(i).getWorkOrderNo());
				summary.setOperator(operationReportSummaryKeys.get(i).getOperator());
				summary.setPassCount(map.get(operationReportSummaryKeys.get(i)).getPassCount());
				summary.setFailCount(map.get(operationReportSummaryKeys.get(i)).getFailCount());
				operationReportSummaries.add(summary);
				if (operationReportSummaries.size() == operationReportSummaryKeys.size() - page.getFirstIndex() + 1) {
					break;
				}
			}
		}
		return operationReportSummaries;
	}

	
	@Override
	public List<StockLogVO> listStockLogs(String operator, String materialNo, String custom, String position, String startTime, String endTime, Page page) throws ParseException {

		StockLogExample stockLogExample = new StockLogExample();
		StockLogExample.Criteria stockLogCriteria = stockLogExample.createCriteria();

		// 筛选时间
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (startTime != null && !startTime.equals("")) {
			stockLogCriteria.andOperationTimeGreaterThanOrEqualTo(simpleDateFormat.parse(startTime));
		}
		if (endTime != null && !endTime.equals("")) {
			stockLogCriteria.andOperationTimeLessThanOrEqualTo(simpleDateFormat.parse(endTime));
		}

		// 筛选操作员
		if (operator != null && !operator.equals("")) {
			stockLogCriteria.andOperatorLike("%" + operator + "%");
		}

		// 筛选仓号
		if (position != null && !position.equals("")) {
			stockLogCriteria.andPositionLike("%" + position + "%");
		}

		// 筛选供应商
		if (custom != null && !custom.equals("")) {
			stockLogCriteria.andCustomLike("%" + custom + "%");
		}

		// 筛选料号
		if (materialNo != null && !materialNo.equals("")) {
			stockLogCriteria.andMaterialNoLike("%" + materialNo + "%");
		}

		// 时间降序
		stockLogExample.setOrderByClause("operation_time desc");
		if(page != null) {
			page.setTotallyData(stockLogMapper.countByExample(stockLogExample));
			stockLogExample.setLimitStart(page.getFirstIndex());
			stockLogExample.setLimitSize(page.getPageSize());
		}
		return stockLogToStockLogVOFiller.fill(stockLogMapper.selectByExample(stockLogExample));
	}

	
	@Override
	public Integer add(Operation operation) {
		operation.setTime(new Date());
		Integer lineId = lineService.getLineIdByName(operation.getLine().toString());
		if(lineId != null) {
			operation.setLine(lineId);
			return operationMapper.insert(operation);
		}		
		return 0;
	}
				
}
