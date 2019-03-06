package com.jimi.smt.eps_server.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jimi.smt.eps_server.constant.ClientDevice;
import com.jimi.smt.eps_server.constant.ControlledDevice;
import com.jimi.smt.eps_server.constant.Operation;
import com.jimi.smt.eps_server.entity.SocketLog;
import com.jimi.smt.eps_server.entity.CenterControlInfo;
import com.jimi.smt.eps_server.entity.CenterState;
import com.jimi.smt.eps_server.mapper.SocketLogMapper;
import com.jimi.smt.eps_server.mapper.CenterStateMapper;
import com.jimi.smt.eps_server.socket.CenterServerSocket;
import com.jimi.smt.eps_server.util.OsHelper;


/**发送中控控制信息
 * @author HCJ
 * @date 2018年11月6日 下午5:00:28
 */
@Component
public class CenterControlInfoSender {

	private static Logger logger = LogManager.getRootLogger();

	/**
	 * converyStateMap : 存储各个树莓派中控接驳台状态
	 */
	private static Map<String, Boolean> converyStateMap = new HashMap<>();

	/**
	 * alarmStateMap : 存储各个树莓派中控报警器状态
	 */
	private static Map<String, Boolean> alarmStateMap = new HashMap<>();

	private static SocketLogMapper socketLogMapper;
	private static CenterStateMapper centerStateMapper;


	/**@author HCJ
	 * 发送中控控制信息到接驳台
	 * @param centerMac 树莓派Mac
	 * @param lineId 产线ID
	 * @param clientDevice 发送端设备
	 * @param isAlarm 是否处于报警状态
	 * @date 2019年3月4日 上午10:22:37
	 */
	public static synchronized void sendCmdToConvery(String centerMac, Integer lineId, ClientDevice clientDevice, boolean isAlarm) throws Exception {
		if (CenterServerSocket.getClients().containsKey(centerMac)) {
			if (converyStateMap.get(centerMac) ^ isAlarm) {
				// 开启or关闭接驳台
				CenterControlInfo centerControlInfo = new CenterControlInfo();
				centerControlInfo.setClientDevice(clientDevice);
				centerControlInfo.setControlledDevice(ControlledDevice.CONVEYOR);
				if (isAlarm) {
					centerControlInfo.setOperation(Operation.OFF);
				} else {
					centerControlInfo.setOperation(Operation.ON);
				}
				CenterServerSocket.sendCenterControlInfo(centerMac, centerControlInfo);
				// 创建日志：收到的信息
				insertSocketLog("产线ID为 " + lineId + " 的中控接收到的控制接驳台的信息为：发送端设备为 " + clientDevice + " isAlarm为 " + isAlarm, "Control", OsHelper.getLinuxLocalIp(), "");
				converyStateMap.put(centerMac, isAlarm);
				// 记录到state表
				CenterState state = centerStateMapper.selectByPrimaryKey(lineId);
				state.setConverypaused(isAlarm);
				centerStateMapper.updateByPrimaryKey(state);
			}
		}
	}

	
	/**@author HCJ
	 * 发送中控控制信息到报警器
	 * @param centerMac 树莓派Mac
	 * @param lineId 产线ID
	 * @param clientDevice 发送端设备
	 * @param isAlarm 是否处于报警状态
	 * @date 2019年3月4日 上午10:22:37
	 */
	public static synchronized void sendCmdToAlarm(String centerMac, Integer lineId, ClientDevice clientDevice, boolean isAlarm) throws Exception {
		if (CenterServerSocket.getClients().containsKey(centerMac)) {
			if (alarmStateMap.get(centerMac) ^ isAlarm) {
				// 打开or关闭报警器
				CenterControlInfo centerControlInfo = new CenterControlInfo();
				centerControlInfo.setClientDevice(clientDevice);
				centerControlInfo.setControlledDevice(ControlledDevice.ALARM);
				if (isAlarm) {
					centerControlInfo.setOperation(Operation.ON);
				} else {
					centerControlInfo.setOperation(Operation.OFF);
				}
				CenterServerSocket.sendCenterControlInfo(centerMac, centerControlInfo);
				// 创建日志：收到的信息
				insertSocketLog("产线ID为 " + lineId + " 的中控接收到的控制报警器的信息为：发送端设备为 " + clientDevice + " isAlarm为 " + isAlarm, "Control", OsHelper.getLinuxLocalIp(), "");
				alarmStateMap.put(centerMac, isAlarm);
				// 记录到state表
				CenterState state = centerStateMapper.selectByPrimaryKey(lineId);
				state.setAlarming(isAlarm);
				centerStateMapper.updateByPrimaryKey(state);
			}
		}
	}


	/**@author HCJ
	 * 重置中控报警器、接驳台状态
	 * @param centerMac 树莓派Mac
	 * @param lineId 产线ID
	 * @date 2019年3月4日 上午10:48:26
	 */
	public static void reset(String centerMac, Integer lineId) throws Exception {
		// 强制重置
		converyStateMap.put(centerMac, true);
		alarmStateMap.put(centerMac, true);
		sendCmdToAlarm(centerMac, lineId, ClientDevice.SERVER, false);
		sendCmdToConvery(centerMac, lineId, ClientDevice.SERVER, false);
		if (!OsHelper.isProductionEnvironment()) {
			logger.info("Mac为 " + centerMac + "的中控报警器、接驳台已重置");
		}
	}


	@Autowired
	public void setSocketLogMapper(SocketLogMapper socketLogMapper) {
		CenterControlInfoSender.socketLogMapper = socketLogMapper;
	}


	@Autowired
	public void setCenterStateMapper(CenterStateMapper centerStateMapper) {
		CenterControlInfoSender.centerStateMapper = centerStateMapper;
	}


	/**@author HCJ
	 * 插入socket日志
	 * @param data 参数信息
	 * @param protocol 类型
	 * @param senderIp 发送IP
	 * @param receiverIp 接收IP
	 * @date 2019年2月27日 下午6:00:28
	 */
	private static void insertSocketLog(String data, String protocol, String senderIp, String receiverIp) {
		SocketLog log = new SocketLog();
		log.setTime(new Date());
		log.setData(data);
		log.setProtocol(protocol);
		log.setSenderIp(senderIp);
		log.setReceiverIp(receiverIp);
		socketLogMapper.insert(log);
	}
}
