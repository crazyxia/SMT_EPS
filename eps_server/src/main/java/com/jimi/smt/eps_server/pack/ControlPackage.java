package com.jimi.smt.eps_server.pack;

import java.io.Serializable;

import com.jimi.smt.eps_server.constant.ClientDevice;
import com.jimi.smt.eps_server.constant.ControlledDevice;
import com.jimi.smt.eps_server.constant.Operation;

import cc.darhao.jiminal.annotation.Parse;
import cc.darhao.jiminal.annotation.Protocol;
import com.jimi.smt.eps_server.pack.BasePackage;

@Protocol(0x43)
public class ControlPackage extends BasePackage implements Serializable{
	
	/**
	 * serialVersionUID : TODO
	 */
	private static final long serialVersionUID = -3538048061152495021L;
	
	@Parse({1,1})
	private int line;
	@Parse({3,1})
	private Operation Operation;
	@Parse({0,1})
	private ClientDevice ClientDevice;
	@Parse({2,1})
	private ControlledDevice ControlledDevice;
		
	public int getLine() {
		return line;
	}
	public void setLine(int line) {
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
