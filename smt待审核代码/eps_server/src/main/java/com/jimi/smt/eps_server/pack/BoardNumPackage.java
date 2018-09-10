package com.jimi.smt.eps_server.pack;

import java.util.Date;


import cc.darhao.jiminal.annotation.Parse;
import cc.darhao.jiminal.annotation.Protocol;
import cc.darhao.jiminal.core.BasePackage;

@Protocol(0x42)
public class BoardNumPackage extends BasePackage {

	@Parse({ 0, 1 })
	private String line;
	@Parse({ 1, 4 })
	private Date timestamp;
	@Parse({ 5, 3 })
	private int boardNum;
	
	public String getLine() {
		return line;
	}
	public void setLine(String line) {
		this.line = line;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public int getBoardNum() {
		return boardNum;
	}
	public void setBoardNum(int boardNum) {
		this.boardNum = boardNum;
	}

	
}
