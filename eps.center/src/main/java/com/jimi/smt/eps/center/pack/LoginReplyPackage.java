package com.jimi.smt.eps.center.pack;

import java.util.Date;

import cc.darhao.jiminal.annotation.Parse;
import cc.darhao.jiminal.annotation.Protocol;
import cc.darhao.jiminal.core.BasePackage;

@Protocol(0x4C)
public class LoginReplyPackage extends BasePackage {
	
	@Parse({0,1})
	private String line;
	@Parse({1,4})
	private Date timestamp;
	
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

	
}
