package com.jimi.smt.eps_server.pack;

import java.io.Serializable;

import com.jimi.smt.eps_server.constant.ClientDevice;
import com.jimi.smt.eps_server.constant.ControlResult;
import com.jimi.smt.eps_server.constant.ErrorCode;

import cc.darhao.jiminal.annotation.Parse;
import cc.darhao.jiminal.annotation.Protocol;
import com.jimi.smt.eps_server.pack.BasePackage;

@Protocol(0x43)
public class ControlReplyPackage extends BasePackage implements Serializable{

	/**
	 * serialVersionUID : TODO
	 */
	private static final long serialVersionUID = -8466121178927094521L;
	
	@Parse({ 0, 1 })
	private ClientDevice ClientDevice;
	@Parse({ 1, 1 })
	private ControlResult ControlResult;
	@Parse({ 2, 1 })
	private ErrorCode ErrorCode;

	public ClientDevice getClientDevice() {
		return ClientDevice;
	}

	public void setClientDevice(ClientDevice ClientDevice) {
		this.ClientDevice = ClientDevice;
	}

	public ErrorCode getErrorCode() {
		return ErrorCode;
	}

	public void setErrorCode(ErrorCode ErrorCode) {
		this.ErrorCode = ErrorCode;
	}

	public ControlResult getControlResult() {
		return ControlResult;
	}

	public void setControlResult(ControlResult ControlResult) {
		this.ControlResult = ControlResult;
	}

}
