package com.jimi.smt.eps_server.pack;

import java.io.Serializable;
import java.util.Date;


import cc.darhao.jiminal.annotation.Parse;
import cc.darhao.jiminal.annotation.Protocol;
import com.jimi.smt.eps_server.pack.BasePackage;

@Protocol(0x42)
public class BoardNumPackage extends BasePackage implements Serializable{

	/**
	 * serialVersionUID : TODO
	 */
	private static final long serialVersionUID = -8908979165974399122L;
	
	@Parse({ 0, 1 })
	private int line;
	@Parse({ 1, 4 })
	private Date timestamp;
	@Parse({ 5, 3 })
	private int boardNum;
	
	
	public int getLine() {
		return line;
	}
	public void setLine(int line) {
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
