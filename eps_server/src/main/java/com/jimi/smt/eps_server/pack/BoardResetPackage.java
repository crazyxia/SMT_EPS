package com.jimi.smt.eps_server.pack;

import com.jimi.smt.eps_server.constant.BoardResetReson;
import com.jimi.smt.eps_server.constant.ClientDevice;

import cc.darhao.jiminal.annotation.Parse;
import cc.darhao.jiminal.annotation.Protocol;
import cc.darhao.jiminal.core.BasePackage;

@Protocol(0x52)
public class BoardResetPackage extends BasePackage {
	
	@Parse({0,1})
	private ClientDevice ClientDevice;
	@Parse({1,1})
	private String line;
	@Parse({2,1})
	private BoardResetReson BoardResetReson;
	
	public ClientDevice getClientDevice() {
		return ClientDevice;
	}
	public void setClientDevice(ClientDevice clientDevice) {
		ClientDevice = clientDevice;
	}
	public String getLine() {
		return line;
	}
	public void setLine(String line) {
		this.line = line;
	}
	public BoardResetReson getBoardResetReson() {
		return BoardResetReson;
	}
	public void setBoardResetReson(BoardResetReson boardResetReson) {
		BoardResetReson = boardResetReson;
	}
	
	
}
