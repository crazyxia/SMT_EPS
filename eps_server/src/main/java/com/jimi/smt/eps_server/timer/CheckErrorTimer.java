package com.jimi.smt.eps_server.timer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jimi.smt.eps_server.constant.ClientDevice;
import com.jimi.smt.eps_server.constant.ControlledDevice;
import com.jimi.smt.eps_server.entity.CenterLogin;
import com.jimi.smt.eps_server.entity.Config;
import com.jimi.smt.eps_server.entity.ConfigExample;
import com.jimi.smt.eps_server.entity.LineOperationResult;
import com.jimi.smt.eps_server.entity.bo.CenterControllerErrorCounter;
import com.jimi.smt.eps_server.mapper.CenterLoginMapper;
import com.jimi.smt.eps_server.mapper.ConfigMapper;
import com.jimi.smt.eps_server.mapper.ProgramItemVisitMapper;
import com.jimi.smt.eps_server.socket.CenterServerSocket;
import com.jimi.smt.eps_server.thread.SendCmdThread;
import com.jimi.smt.eps_server.util.OsHelper;


/**产线错误操作检测定时器
 * @date 2019年1月29日 下午5:20:13
 */
@Component
public class CheckErrorTimer {

	private static Logger logger = LogManager.getRootLogger();

	/**
	 * Config配置项：操作员错误报警
	 */
	private static final String OPERATOR_ERROR_ALARM = "operator_error_alarm";

	/**
	 * Config配置项：IPQC错误报警
	 */
	private static final String IPQC_ERROR_ALARM = "ipqc_error_alarm";

	/**
	 * “线别-报警设备错误统计”实体
	 */
	private Map<Integer, CenterControllerErrorCounter> lineErrorsCounters;

	/**
	 * 中控数量
	 */
	private long centerLoginSize;

	/**
	 * centerLoginMap : 所有中控登录类列表
	 */
	private static Map<Integer, CenterLogin> centerLoginMap= new HashMap<Integer, CenterLogin>();

	@Autowired
	private ConfigMapper configMapper;
	@Autowired
	private ProgramItemVisitMapper programItemVisitMapper;
	@Autowired
	private CenterLoginMapper centerLoginMapper;

	/**
	 * isCenterExist : 中控是否存在
	 */
	private boolean isCenterExist = true;


	/**开始错误检测
	 * @date 2019年1月29日 下午5:23:23
	 */
	@Scheduled(fixedDelay = 5000)
	public void start() {
		initCenterLoginConfig();
		if (CenterServerSocket.getClients().size() > 0 && isCenterExist) {
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
	}


	/**
	 * @author HCJ 初始化产线数量和集合
	 * @date 2018年9月26日 上午8:50:43
	 */
	private void initCenterLoginConfig() {
		centerLoginMap.clear();
		List<CenterLogin> centerLogins = centerLoginMapper.selectByExample(null);
		if (centerLogins != null && centerLogins.size() > 0) {
			centerLoginSize = centerLogins.size();
			for (int i = 0; i < centerLoginSize; i++) {
				CenterLogin centerLogin = centerLogins.get(i);
				centerLoginMap.put(i, centerLogin);
			}
		} else {
			isCenterExist = false;
		}
	}


	/**
	 * 初始化“线别-报警设备错误统计”实体
	 */
	private void initCounters() throws IOException {
		lineErrorsCounters = new HashMap<Integer, CenterControllerErrorCounter>();
		for (int i = 0; i < centerLoginSize; i++) {
			lineErrorsCounters.put(i, new CenterControllerErrorCounter());
		}
		// 设置报警模式
		setAlarmMode();
	}


	/**
	 * 根据Config表设置报警方式
	 */
	private void setAlarmMode() throws IOException {
		ConfigExample example = new ConfigExample();
		List<Config> configs = configMapper.selectByExample(example);
		for (Config config : configs) {
			Integer lineIndex = getIndexByLineId(config.getLine());
			if (lineIndex != null) {
				switch (config.getName()) {
				case IPQC_ERROR_ALARM:
					lineErrorsCounters.get(lineIndex).setIpqcErrorAlarm(Integer.parseInt(config.getValue()));
					break;
				case OPERATOR_ERROR_ALARM:
					lineErrorsCounters.get(lineIndex).setOperatorErrorAlarm(Integer.parseInt(config.getValue()));
					break;
				default:
					break;
				}
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
					int lineIndex = getIndexByLineId(lineOperationResult.getLine());
					updateLineErrorCounter(lineIndex, 0, lineOperationResult.getFeedResult());
					updateLineErrorCounter(lineIndex, 1, lineOperationResult.getChangeResult());
					updateLineErrorCounter(lineIndex, 2, lineOperationResult.getCheckResult());
					updateLineErrorCounter(lineIndex, 3, lineOperationResult.getCheckAllResult());
					updateLineErrorCounter(lineIndex, 5, lineOperationResult.getFirstCheckAllResult());
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}


	/**
	 * 更新错误计数
	 * @param line 产线在lineMap中的对应序号
	 * @param operaton 0：上料 1：换料 2：核料 3：全检 5：首检
	 * @param result 操作结果
	 */
	private void updateLineErrorCounter(int lineIndex, int opertion, int result) {
		// 根据配置文件计数
		if (result == 0 || result == 3) {
			CenterControllerErrorCounter counter = lineErrorsCounters.get(lineIndex);
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
		for (int i = 0; i < centerLoginSize; i++) {
			CenterControllerErrorCounter counter = lineErrorsCounters.get(i);
			if (counter.getAlarmErrorCount() > 0) {
				sendCmd(ClientDevice.SERVER, centerLoginMap.get(i).getMac(), centerLoginMap.get(i).getLine(), true, ControlledDevice.ALARM);
			} else {
				sendCmd(ClientDevice.SERVER, centerLoginMap.get(i).getMac(), centerLoginMap.get(i).getLine(), false, ControlledDevice.ALARM);
			}
			if (counter.getConveryErrorCount() > 0) {
				sendCmd(ClientDevice.SERVER, centerLoginMap.get(i).getMac(), centerLoginMap.get(i).getLine(), true, ControlledDevice.CONVEYOR);
			} else {
				sendCmd(ClientDevice.SERVER, centerLoginMap.get(i).getMac(), centerLoginMap.get(i).getLine(), false, ControlledDevice.CONVEYOR);
			}
		}
	}


	/**@author HCJ
	 * 根据配置，发送报警/解除信息
	 * @param clientDevice 发起者
	 * @param centerMac 树莓派Mac
	 * @param lineId 产线ID
	 * @param isAlarm 报警/解除报警
	 * @param controlledDevice 被控制设备：报警灯或接驳台
	 * @date 2019年3月4日 上午10:59:03
	 */
	private void sendCmd(ClientDevice clientDevice, String centerMac, Integer lineId, boolean isAlarm, ControlledDevice controlledDevice) {
		new SendCmdThread(clientDevice, centerMac, lineId, isAlarm, controlledDevice).start();
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
		for (Integer index : centerLoginMap.keySet()) {
			if (centerLoginMap.get(index).getLine().equals(id)) {
				return index;
			}
		}
		return null;
	}
}
