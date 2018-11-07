package com.jimi.smt.eps_server.pack;

import java.io.Serializable;
import java.util.Date;

import cc.darhao.jiminal.annotation.Parse;
import cc.darhao.jiminal.annotation.Protocol;
import com.jimi.smt.eps_server.pack.BasePackage;

@Protocol(0x4C)
public class LoginReplyPackage extends BasePackage implements Serializable{
	
	/**
	 * serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 6830238646377104506L;
	
	@Parse({0,1})
	private int line;
	@Parse({1,4})
	private Date timestamp;
	
	
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

	
}
