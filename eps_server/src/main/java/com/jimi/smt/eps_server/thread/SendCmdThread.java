package com.jimi.smt.eps_server.thread;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jimi.smt.eps_server.constant.ClientDevice;
import com.jimi.smt.eps_server.constant.ControlledDevice;
import com.jimi.smt.eps_server.util.CenterControlInfoSender;


/**命令发送子线程
 * @date     2019年3月4日 上午11:01:12
 */
public class SendCmdThread extends Thread{

	private static Logger logger = LogManager.getRootLogger();
	
	private String centerMac;
	
	private boolean isAlarm;
	
	private ClientDevice clientDevice;
	
	private ControlledDevice controlledDevice;
	
	private Integer lineId;
	
	
	/**命令发送子线程
	 * @param clientDevice 发起者
	 * @param centerMac 树莓派Mac
	 * @param lineId 产线ID
	 * @param isAlarm 报警/解除报警
	 * @param controlledDevice 被控制设备：报警灯或接驳台
	 */
	public SendCmdThread(ClientDevice clientDevice, String centerMac, Integer lineId,boolean isAlarm, ControlledDevice controlledDevice) {
		this.centerMac = centerMac;
		this.isAlarm = isAlarm;
		this.clientDevice = clientDevice;
		this.controlledDevice = controlledDevice;
		this.lineId = lineId;
	}
	
	
	@Override
	public void run() {
		try {
			// 发送命令
			if (controlledDevice == ControlledDevice.ALARM) {
				CenterControlInfoSender.sendCmdToAlarm(centerMac, lineId, clientDevice, isAlarm);
			} else if (controlledDevice == ControlledDevice.CONVEYOR) {
				CenterControlInfoSender.sendCmdToConvery(centerMac, lineId, clientDevice, isAlarm);
			}
		} catch (Exception e) {
			logger.error("命令发送子线程出错 |" + e.getMessage());
		}
	}
	
}
