package com.jimi.smt.eps_server.timer;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jimi.smt.eps_server.entity.Display;
import com.jimi.smt.eps_server.entity.Line;
import com.jimi.smt.eps_server.entity.ProgramItemVisit;
import com.jimi.smt.eps_server.entity.bo.LineInfo;
import com.jimi.smt.eps_server.entity.vo.ConfigVO;
import com.jimi.smt.eps_server.service.ConfigService;
import com.jimi.smt.eps_server.service.LineService;
import com.jimi.smt.eps_server.service.ProgramService;

/**
 * 检料超时检查定时器类
 * <br>
 * <b>2018年4月26日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
@Component
public class TimeoutTimer {
	
	@Autowired
	private ProgramService programService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private LineService lineService;
	
	/**
	 * Config配置项
	 */
	private static final String CHECK_ALL_CYCLE_TIME = "check_all_cycle_time";
	private static final String CHECK_AFTER_CHANGE_TIME = "check_after_change_time";
	
	/**
	 * 产线数量
	 */
	private int lineSize;
	
	/**
	 * 线锁
	 */
	private Object[] lineLocks; 
	
	/**
	 * 产线当前信息
	 */
	private Map<Integer, LineInfo> lineInfos;
	
	/**
	 * 所有线别列表
	 */
	public Map<Integer, Line> lineMap = new HashMap<>();
			
	
	@PostConstruct
	public void init() {		
		List<Line> lines = lineService.selectAll();
		lineSize = lines.size();
		lineLocks = new Object[lineSize];
		lineInfos = new HashMap<Integer, LineInfo>();	
		for (int i = 0; i < lineSize; i++) {
			Line line = lines.get(i);
			lineMap.put(i, line);
			lineLocks[i] = new Object();
			lineInfos.put(i, new LineInfo());
		}
	}
	
	
	/**
	 * 每隔5秒检查是否有超时项目
	 */
	@Scheduled(cron = "0/5 * * * * ?")
	public void check(){
		//设置超时时间
		setTimeoutTime();
		//设置当前工单和板面类型
		setWorkOrderAndBoardType();
		//遍历所有线别进行检查
		for (int i = 0; i < lineSize; i++) {
			//锁住对应线别
			synchronized (lineLocks[i]) {
				//判断是否有工单在被监控
				if(lineInfos.get(i).getWorkOrder() == null) {
					continue;
				}
				//查询programID
				String line = lineMap.get(i).getLine();
				LineInfo bo = lineInfos.get(i);
				List<ProgramItemVisit> programItemVisits = programService.getVisits(line, bo.getWorkOrder(), bo.getBoardType());
				if(! programItemVisits.isEmpty()) {
					//超时检测
					checkTimeout(programItemVisits, i);
				}
			}
		}
	}


	/**
	 * 获取线锁
	 */
	public Object getLock(String line) {
		return lineLocks[getLineNoByName(line)];
	}
	
		
	/**@author HCJ
	 * 根据产线名称返回产线在lineMap的位置
	 * @method getLineNoByName
	 * @param line
	 * @return
	 * @return int
	 * @date 2018年9月22日 上午11:15:34
	 */
	private int getLineNoByName(String line) {
		int number = 0;
		for (Integer lineNo : lineMap.keySet()) {
			if (lineMap.get(lineNo).getLine().equals(line)) {
				number = lineNo;
				break;
			}
		}
		return number;
	}
			
	
	/**
	 * 超时检测
	 */
	private void checkTimeout(List<ProgramItemVisit> programItemVisits, int line) {
		for (ProgramItemVisit programItemVisit : programItemVisits) {
			//全检超时检测
			//当前全检时间加上设置的超时时间
			long temp = programItemVisit.getCheckAllTime().getTime() + lineInfos.get(line).getCheckAllTimeout() * 60 * 1000;
			//当前时间是否大于全检超时后的时间
			boolean isCheckAllTimeout = new Date().getTime() > temp;
			//是否已经正常首检
			boolean hasFirstCheckAll = programItemVisit.getFirstCheckAllResult() == 1;
			//全检结果是否不为超时
			boolean isNotInCheckAllTimeoutState = programItemVisit.getCheckAllResult() != 3;
			if(isCheckAllTimeout  && hasFirstCheckAll && isNotInCheckAllTimeoutState) {
				for (ProgramItemVisit programItemVisit2 : programItemVisits) {
					programItemVisit2.setCheckAllResult(3);
					programItemVisit2.setLastOperationType(3);
					programItemVisit2.setLastOperationTime(new Date());
					programService.updateVisit(programItemVisit2);
				}
			}
			//核料超时检测
			//当前换料时间加上设置的超时时间
			long temp2 = programItemVisit.getChangeTime().getTime() + lineInfos.get(line).getCheckTimeout() * 60 * 1000;
			//当前时间是否大于换料超时后的时间
			boolean isCheckTimeout = new Date().getTime() > temp2;
			//核料时间是否小于换料时间
			boolean notYetCheck = programItemVisit.getCheckTime().getTime() < programItemVisit.getChangeTime().getTime();
			//是否处于待核料状态
			boolean hasChangeButNeedCheck = programItemVisit.getChangeResult() == 4;
			//核料状态是否不为超时
			boolean isNotInCheckTimeoutState = programItemVisit.getCheckResult() != 3;
			if(isCheckTimeout && notYetCheck && hasChangeButNeedCheck && isNotInCheckTimeoutState) {
				 programItemVisit.setCheckResult(3);
				 programItemVisit.setLastOperationType(2);
				 programItemVisit.setLastOperationTime(new Date());
				 programService.updateVisit(programItemVisit);
			}
		}
	}


	/**
	 * 根据Config表设置超时时间
	 */
	private void setTimeoutTime() {
		List<ConfigVO> configVOs = configService.list();
		for (ConfigVO configVO : configVOs) {
			int lineNo = getIndexByLineId(configVO.getLine());
			switch (configVO.getName()) {
			case CHECK_ALL_CYCLE_TIME:
				lineInfos.get(lineNo).setCheckAllTimeout(Integer.parseInt(configVO.getValue()));
				break;
			case CHECK_AFTER_CHANGE_TIME:
				lineInfos.get(lineNo).setCheckTimeout(Integer.parseInt(configVO.getValue()));
				break;
			default:
				break;
			}
		}
	}


	/**
	 * 根据Display表设置工单号和板面类型
	 */
	private void setWorkOrderAndBoardType() {
		List<Display> displays = programService.listDisplays();
		for (Display display : displays) {
			int lineNo = getIndexByLineId(display.getLine());
			lineInfos.get(lineNo).setWorkOrder(display.getWorkOrder());
			lineInfos.get(lineNo).setBoardType(display.getBoardType());
		}
	}
	
	
	/**@author HCJ
	 * 根据产线ID返回产线在lineMap的位置
	 * @method getLineNoById
	 * @param id
	 * @return
	 * @return Integer
	 * @date 2018年9月22日 上午11:13:54
	 */
	private Integer getIndexByLineId(Integer id) {
		for (Integer index : lineMap.keySet()) {
			if(lineMap.get(index).getId().equals(id)) {
				return index;
			}
		}
		return null;
	}
	
}
