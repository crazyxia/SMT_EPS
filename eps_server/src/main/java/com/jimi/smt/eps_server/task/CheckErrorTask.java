package com.jimi.smt.eps_server.task;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jimi.smt.eps_server.constant.ClientDevice;
import com.jimi.smt.eps_server.constant.ControlledDevice;
import com.jimi.smt.eps_server.entity.Config;
import com.jimi.smt.eps_server.entity.ConfigExample;
import com.jimi.smt.eps_server.entity.Line;
import com.jimi.smt.eps_server.entity.LineOperationResult;
import com.jimi.smt.eps_server.entity.bo.CenterControllerErrorCounter;
import com.jimi.smt.eps_server.mapper.ConfigMapper;
import com.jimi.smt.eps_server.mapper.ProgramItemVisitMapper;
import com.jimi.smt.eps_server.rmi.CenterRemoteWrapper;
import com.jimi.smt.eps_server.thread.SendCmdThread;
import com.jimi.smt.eps_server.util.OsHelper;

/**产线错误操作检测任务类
 * @author   HCJ
 * @date     2019年1月29日 下午5:20:13
 */
public class CheckErrorTask {

	private static Logger logger = LogManager.getRootLogger();

	private ConfigMapper configMapper;

	private ProgramItemVisitMapper programItemVisitMapper;

	/**
	 * Config配置项
	 */
	private static final String OPERATOR_ERROR_ALARM = "operator_error_alarm";

	/**
	 * Config配置项
	 */
	private static final String IPQC_ERROR_ALARM = "ipqc_error_alarm";

	/**
	 * “线别-报警设备错误统计”实体
	 */
	private Map<Integer, CenterControllerErrorCounter> lineErrorsCounters;

	/**
	 * 所有线别的报警模块列表
	 */
	private Map<Integer, CenterRemoteWrapper> clientSockets;

	/**
	 * 产线数量
	 */
	private long lineSize;

	/**
	 * lineMap : 所有产线列表
	 */
	private Map<Integer, Line> lineMap;

	
	public CheckErrorTask(Long lineSize, Map<Integer, Line> lineMap, ConfigMapper configMapper, ProgramItemVisitMapper programItemVisitMapper, Map<Integer, CenterRemoteWrapper> clientSockets) {
		this.configMapper = configMapper;
		this.programItemVisitMapper = programItemVisitMapper;
		this.lineSize = lineSize;
		this.clientSockets = clientSockets;
		this.lineMap = lineMap;
	}

	
	/**@author HCJ
	 * 开始错误检测
	 * @date 2019年1月29日 下午5:23:23
	 */
	public void start() {
		try {
			// 初始化“线别-报警设备错误统计”实体
			initCounters();
			// 遍历visits进行错误扫描
			scanErrors();
			// 根据统计结果发送指令
			sendCmdByCounters();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * 初始化“线别-报警设备错误统计”实体
	 */
	private void initCounters() throws IOException {
		lineErrorsCounters = new HashMap<Integer, CenterControllerErrorCounter>();
		for (int i = 0; i < lineSize; i++) {
			lineErrorsCounters.put(i, new CenterControllerErrorCounter());
		}
		// 设置报警模式
		setAlarmMode();
	}

	
	/**
	 * 根据Config表设置报警方式
	 * 
	 * @throws IOException
	 */
	private void setAlarmMode() throws IOException {
		ConfigExample example = new ConfigExample();
		List<Config> configs = configMapper.selectByExample(example);
		for (Config config : configs) {
			int lineNo = getIndexByLineId(config.getLine());
			switch (config.getName()) {
			case IPQC_ERROR_ALARM:
				lineErrorsCounters.get(lineNo).setIpqcErrorAlarm(Integer.parseInt(config.getValue()));
				break;
			case OPERATOR_ERROR_ALARM:
				lineErrorsCounters.get(lineNo).setOperatorErrorAlarm(Integer.parseInt(config.getValue()));
				break;
			default:
				break;
			}
		}
	}

	
	/**
	 * 扫描错误
	 */
	private void scanErrors() {
		try {
			long s = System.currentTimeMillis();
			List<LineOperationResult> lineOperationResults = programItemVisitMapper.selectLineOperationResult();
			if (!OsHelper.isProductionEnvironment()) {
				System.out.println("查询耗时：" + (System.currentTimeMillis() - s) + "ms");
			}
			for (LineOperationResult lineOperationResult : lineOperationResults) {
				try {
					// 遍历字段
					int line = getIndexByLineId(lineOperationResult.getLine());
					updateLineErrorCounter(line, 0, lineOperationResult.getFeedResult());
					updateLineErrorCounter(line, 1, lineOperationResult.getChangeResult());
					updateLineErrorCounter(line, 2, lineOperationResult.getCheckResult());
					updateLineErrorCounter(line, 3, lineOperationResult.getCheckAllResult());
					updateLineErrorCounter(line, 5, lineOperationResult.getFirstCheckAllResult());
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	
	/**
	 * 更新错误计数
	 * 
	 * @param line
	 * @param operaton 0：上料 1：换料 2：核料 3：全检 5：首检
	 * @param result
	 */
	private void updateLineErrorCounter(int line, int opertion, int result) {
		// 根据配置文件计数
		if (result == 0 || result == 3) {
			CenterControllerErrorCounter counter = lineErrorsCounters.get(line);
			if (opertion > 1) {
				int ipqcMode = counter.getIpqcErrorAlarm();
				count(counter, ipqcMode);
			} else {
				int operatorMode = counter.getOperatorErrorAlarm();
				count(counter, operatorMode);
			}
		}
	}

	
	/**
	 * 计数
	 */
	private void count(CenterControllerErrorCounter counter, int mode) {
		switch (mode) {
		case 0:
			counter.setConveryErrorCount(counter.getConveryErrorCount() + 1);
			counter.setAlarmErrorCount(counter.getAlarmErrorCount() + 1);
			break;
		case 1:
			counter.setAlarmErrorCount(counter.getAlarmErrorCount() + 1);
			break;
		case 2:
			counter.setConveryErrorCount(counter.getConveryErrorCount() + 1);
			break;
		default:
			break;
		}
	}

	
	/**
	 * 根据统计结果发送指令
	 */
	private void sendCmdByCounters() {
		for (int i = 0; i < lineSize; i++) {
			if (clientSockets.get(i) == null) {
				continue;
			}
			CenterControllerErrorCounter counter = lineErrorsCounters.get(i);
			if (counter.getAlarmErrorCount() > 0) {
				sendCmd(ClientDevice.SERVER, clientSockets.get(i), true, ControlledDevice.ALARM);
			} else {
				sendCmd(ClientDevice.SERVER, clientSockets.get(i), false, ControlledDevice.ALARM);
			}
			if (counter.getConveryErrorCount() > 0) {
				sendCmd(ClientDevice.SERVER, clientSockets.get(i), true, ControlledDevice.CONVEYOR);
			} else {
				sendCmd(ClientDevice.SERVER, clientSockets.get(i), false, ControlledDevice.CONVEYOR);
			}
		}
	}

	
	/**
	 * 根据配置，发送报警/解除包
	 * 
	 * @param clientDevice     发起者
	 * @param socket           发送指令的socket
	 * @param isAlarm          报警/解除报警
	 * @param controlledDevice 被控制设备：报警灯或接驳台
	 */
	private void sendCmd(ClientDevice clientDevice, CenterRemoteWrapper socket, boolean isAlarm, ControlledDevice controlledDevice) {
		new SendCmdThread(clientDevice, socket, isAlarm, controlledDevice).start();
	}

	
	/**
	 * @author HCJ 根据产线ID返回产线在lineMap的位置
	 * @method getLineNoById
	 * @param id
	 * @return
	 * @return Integer
	 * @date 2018年9月22日 上午11:13:54
	 */
	private Integer getIndexByLineId(Integer id) {
		for (Integer index : lineMap.keySet()) {
			if (lineMap.get(index).getId().equals(id)) {
				return index;
			}
		}
		return null;
	}
}
