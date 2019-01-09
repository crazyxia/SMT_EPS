package com.jimi.smt.eps_server.thread;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jimi.smt.eps_server.constant.ClientDevice;
import com.jimi.smt.eps_server.constant.ControlledDevice;
import com.jimi.smt.eps_server.rmi.CenterRemoteWrapper;

/**
 * 命令发送子线程
 * <br>
 * <b>2018年3月22日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class SendCmdThread extends Thread{

	private static Logger logger = LogManager.getRootLogger();
	
	private CenterRemoteWrapper connectToCenterRemote;
	
	private boolean isAlarm;
	
	private ClientDevice clientDevice;
	
	private ControlledDevice controlledDevice;
	
	
	public SendCmdThread(ClientDevice clientDevice, CenterRemoteWrapper connectToCenterRemote, boolean isAlarm, ControlledDevice controlledDevice) {
		this.connectToCenterRemote = connectToCenterRemote;
		this.isAlarm = isAlarm;
		this.clientDevice = clientDevice;
		this.controlledDevice = controlledDevice;
	}
	
	
	@Override
	public void run() {
		while (true) {
			try {
				// 发送命令
				if (controlledDevice == ControlledDevice.ALARM) {
					connectToCenterRemote.sendCmdToAlarm(clientDevice, isAlarm);
				} else if (controlledDevice == ControlledDevice.CONVEYOR) {
					connectToCenterRemote.sendCmdToConvery(clientDevice, isAlarm);
				}
				break;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
	}
	
}
