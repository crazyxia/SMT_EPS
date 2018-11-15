package com.jimi.smt.eps_server.thread;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jimi.smt.eps_server.constant.ClientDevice;
import com.jimi.smt.eps_server.constant.ControlledDevice;
import com.jimi.smt.eps_server.rmi.ConnectToCenterRemote;

/**
 * 命令发送子线程
 * <br>
 * <b>2018年3月22日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class SendCmdThread extends Thread{

	private static Logger logger = LogManager.getRootLogger();
	
	private ConnectToCenterRemote connectToCenterRemote;
	
	private boolean isAlarm;
	
	private ClientDevice clientDevice;
	
	private ControlledDevice controlledDevice;
	
	
	public SendCmdThread(ClientDevice clientDevice, ConnectToCenterRemote connectToCenterRemote, boolean isAlarm, ControlledDevice controlledDevice) {
		this.connectToCenterRemote = connectToCenterRemote;
		this.isAlarm = isAlarm;
		this.clientDevice = clientDevice;
		this.controlledDevice = controlledDevice;
	}
	
	
	@Override
	public void run() {
		try {
			//发送命令
			if(controlledDevice == ControlledDevice.ALARM) {
				connectToCenterRemote.sendCmdToAlarm(clientDevice, isAlarm);
			}else if(controlledDevice == ControlledDevice.CONVEYOR) {
				connectToCenterRemote.sendCmdToConvery(clientDevice, isAlarm);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			try {
				run();
			} catch (Exception e1) {
				e1.printStackTrace();
				logger.error(e1.getMessage());
			}
		}
	}
	
}
