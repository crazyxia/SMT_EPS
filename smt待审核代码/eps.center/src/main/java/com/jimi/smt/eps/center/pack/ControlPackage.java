package com.jimi.smt.eps.center.pack;

import com.jimi.smt.eps.center.constant.ClientDevice;
import com.jimi.smt.eps.center.constant.ControlledDevice;
import com.jimi.smt.eps.center.constant.Operation;

import cc.darhao.jiminal.annotation.Parse;
import cc.darhao.jiminal.annotation.Protocol;
import cc.darhao.jiminal.core.BasePackage;

@Protocol(0x43)
public class ControlPackage extends BasePackage {
	
	@Parse({1,1})
	private String line;
	@Parse({3,1})
	private Operation Operation;
	@Parse({0,1})
	private ClientDevice ClientDevice;
	@Parse({2,1})
	private ControlledDevice ControlledDevice;
	
	public String getLine() {
		return line;
	}
	public void setLine(String line) {
		this.line = line;
	}
	public Operation getOperation() {
		return Operation;
	}
	public void setOperation(Operation operation) {
		Operation = operation;
	}
	public ClientDevice getClientDevice() {
		return ClientDevice;
	}
	public void setClientDevice(ClientDevice clientDevice) {
		ClientDevice = clientDevice;
	}
	public ControlledDevice getControlledDevice() {
		return ControlledDevice;
	}
	public void setControlledDevice(ControlledDevice controlledDevice) {
		ControlledDevice = controlledDevice;
	}

	
}
