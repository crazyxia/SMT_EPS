package com.jimi.smt.eps_server.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jimi.smt.eps_server.entity.LineExample;
import com.jimi.smt.eps_server.entity.Operation;
import com.jimi.smt.eps_server.entity.OperationExample;
import com.jimi.smt.eps_server.entity.StatusDetail;
import com.jimi.smt.eps_server.entity.vo.StatusDetailsVO;
import com.jimi.smt.eps_server.entity.OperationExample.Criteria;
import com.jimi.smt.eps_server.mapper.LineMapper;
import com.jimi.smt.eps_server.mapper.OperationMapper;
import com.jimi.smt.eps_server.service.StatusService;

@Service
public class StatusServiceImpl implements StatusService {

	@Autowired
	private OperationMapper OperationMapper;
	@Autowired
	private LineMapper lineMapper;

	
	@Override
	public StatusDetailsVO ListStatusDetailsByHour(int line) {

		OperationExample operationExample = new OperationExample();
		Criteria criteria = operationExample.createCriteria();
		// 得到当前整点时间
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE),
				calendar.get(Calendar.HOUR_OF_DAY), 0, 0);
		Date endTime = calendar.getTime();
		// 得到24小时前整点时间
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE),
				calendar.get(Calendar.HOUR_OF_DAY) - 24, 0, 0);
		Date startTime = calendar.getTime();
		criteria.andTimeGreaterThan(startTime);
		criteria.andTimeLessThanOrEqualTo(endTime);
		criteria.andTypeLessThanOrEqualTo(3);
		criteria.andLineEqualTo(line);
		// 查询符合24小时内某线号的操作记录
		List<Operation> operations = OperationMapper.selectByExample(operationExample);
		// 初始化列表
		StatusDetailsVO statusDetailsVO = new StatusDetailsVO();
		List<StatusDetail> statusDetails = new ArrayList<>();
		for (int i = 0; i < 24; i++) {
			statusDetails.add(new StatusDetail());
			statusDetails.get(i).setLine(getLineName(line));
		}
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		// 根据操作记录时间进行每一小时分类
		for (Operation operation : operations) {
			for (int i = 1; i <= 24; i++) {
				calendar.setTime(endTime);
				calendar.set(Calendar.HOUR_OF_DAY, hour - i);
				if (operation.getTime().after(calendar.getTime())) {
					count(operation, statusDetails.get(i - 1));
					break;
				}
			}

		}

		for (StatusDetail statusDetail : statusDetails) {
			statusDetail.fill();
		}
		statusDetailsVO.setLine(getLineName(line));
		statusDetailsVO.setStatusDetails(statusDetails);
		statusDetailsVO.fill();
		return statusDetailsVO;
	}

	
	@Override
	public List<StatusDetail> ListStatusDetailsByDay() {
		OperationExample operationExample = new OperationExample();
		Criteria criteria = operationExample.createCriteria();
		// 得到当前整点时间
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE),
				calendar.get(Calendar.HOUR_OF_DAY), 0, 0);
		Date endTime = calendar.getTime();
		// 得到24小时前整点时间
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE),
				calendar.get(Calendar.HOUR_OF_DAY) - 24, 0, 0);
		Date startTime = calendar.getTime();
		criteria.andTimeGreaterThan(startTime);
		criteria.andTimeLessThanOrEqualTo(endTime);
		criteria.andTypeLessThanOrEqualTo(3);
		// 查询符合24小时内所有产线的操作记录
		List<Operation> operations = OperationMapper.selectByExample(operationExample);
		// 初始化列表
		List<StatusDetail> statusDetails = new ArrayList<>();
		List<String> line = new ArrayList<>();
		int linzeSize = (int) lineMapper.countByExample(null);
		for (int i = 0; i < linzeSize; i++) {
			line.add(getLineName(i) + "");
			statusDetails.add(new StatusDetail());
			statusDetails.get(i).setLine(line.get(i));
		}
		// 根据每条产线进行分类
		for (Operation operation : operations) {
			for (int i = 0; i < linzeSize; i++) {
				if (operation.getLine() == getLineId(line.get(i))) {
					count(operation, statusDetails.get(i));
					break;
				}
			}

		}

		for (StatusDetail statusDetail : statusDetails) {
			statusDetail.fill();
		}
		return statusDetails;
	}

	
	/**
	 * 清点数目
	 * 
	 * @param operation
	 * @param bo
	 */
	private void count(Operation operation, StatusDetail statusDetail) {
		switch (operation.getType()) {
		case 0:
			if (operation.getResult().equals("PASS")) {
				statusDetail.setFeedSuc(statusDetail.getFeedSuc() + 1);
			} else {
				statusDetail.setFeedFail(statusDetail.getFeedFail() + 1);
			}
			break;
		case 1:
			if (operation.getResult().equals("PASS")) {
				statusDetail.setChangeSuc(statusDetail.getChangeSuc() + 1);
			} else {
				statusDetail.setChangeFail(statusDetail.getChangeFail() + 1);
			}
			break;
		case 2:
			if (operation.getResult().equals("PASS")) {
				statusDetail.setSomeSuc(statusDetail.getSomeSuc() + 1);
			} else {
				statusDetail.setSomeFail(statusDetail.getSomeFail() + 1);
			}
			break;
		case 3:
			if (operation.getResult().equals("PASS")) {
				statusDetail.setAllsSuc(statusDetail.getAllsSuc() + 1);
			} else {
				statusDetail.setAllsFail(statusDetail.getAllsFail() + 1);
			}
			break;
		default:
			break;
		}
	}

	
	/**@author HCJ
	 * 根据产线名称得到ID
	 * @method getLineId
	 * @param line
	 * @return int
	 * @date 2018年9月19日 下午8:49:33
	 */
	private int getLineId(String line) {
		LineExample lineExample = new LineExample();
		lineExample.createCriteria().andLineEqualTo(line);
		return lineMapper.selectByExample(lineExample).get(0).getId();
	}

	
	/**@author HCJ
	 * 根据产线ID得到名称
	 * @method getLineName
	 * @param id
	 * @return int
	 * @date 2018年9月19日 下午8:50:00
	 */
	private String getLineName(int id) {
		return lineMapper.selectByPrimaryKey(id).getLine();
	}
}
