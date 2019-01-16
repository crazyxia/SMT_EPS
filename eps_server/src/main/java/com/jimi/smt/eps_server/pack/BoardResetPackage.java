package com.jimi.smt.eps_server.pack;

import java.io.Serializable;

import com.jimi.smt.eps_server.constant.BoardResetReson;
import com.jimi.smt.eps_server.constant.ClientDevice;

import cc.darhao.jiminal.annotation.Parse;
import cc.darhao.jiminal.annotation.Protocol;
import com.jimi.smt.eps_server.pack.BasePackage;

@Protocol(0x52)
public class BoardResetPackage extends BasePackage implements Serializable{
	
	/**
	 * serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 2567461203720615628L;
	
	@Parse({0,1})
	private ClientDevice ClientDevice;
	@Parse({1,1})
	private int line;
	@Parse({2,1})
	private BoardResetReson BoardResetReson;
	
	public ClientDevice getClientDevice() {
		return ClientDevice;
	}
	public void setClientDevice(ClientDevice clientDevice) {
		ClientDevice = clientDevice;
	}	
	public int getLine() {
		return line;
	}
	public void setLine(int line) {
		this.line = line;
	}
	public BoardResetReson getBoardResetReson() {
		return BoardResetReson;
	}
	public void setBoardResetReson(BoardResetReson boardResetReson) {
		BoardResetReson = boardResetReson;
	}
	
	
}
