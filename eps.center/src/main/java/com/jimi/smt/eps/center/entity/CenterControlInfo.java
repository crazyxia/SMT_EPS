package com.jimi.smt.eps.center.entity;

import com.jimi.smt.eps.center.constant.ClientDevice;
import com.jimi.smt.eps.center.constant.ControlledDevice;
import com.jimi.smt.eps.center.constant.Operation;


/**中控控制信息类
 * @author   HCJ
 * @date     2019年2月28日 上午11:54:30
 */
public class CenterControlInfo {

	private Operation operation;

	private ClientDevice clientDevice;

	private ControlledDevice controlledDevice;

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public ControlledDevice getControlledDevice() {
		return controlledDevice;
	}

	public void setControlledDevice(ControlledDevice controlledDevice) {
		this.controlledDevice = controlledDevice;
	}

	public ClientDevice getClientDevice() {
		return clientDevice;
	}

	public void setClientDevice(ClientDevice clientDevice) {
		this.clientDevice = clientDevice;
	}

}
