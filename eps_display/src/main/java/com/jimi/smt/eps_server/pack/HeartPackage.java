package com.jimi.smt.eps_server.pack;

import java.io.Serializable;
import java.util.Date;


import cc.darhao.jiminal.annotation.Parse;
import cc.darhao.jiminal.annotation.Protocol;
import com.jimi.smt.eps_server.pack.BasePackage;

@Protocol(0x48)
public class HeartPackage extends BasePackage implements Serializable{

	/**
	 * serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 7789639345063527563L;
	
	@Parse({0,1})
	private int line;
	@Parse({1,4})
	private Date timestamp;
	@Parse({5,2})
	private boolean isAlarmEnabled;
	@Parse({5,1})
	private boolean isConveyorEnabled;
	@Parse({5,0})
	private boolean isInfraredEnabled;
	
	
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
	public boolean isAlarmEnabled() {
		return isAlarmEnabled;
	}
	public void setAlarmEnabled(boolean isAlarmEnabled) {
		this.isAlarmEnabled = isAlarmEnabled;
	}
	public boolean isConveyorEnabled() {
		return isConveyorEnabled;
	}
	public void setConveyorEnabled(boolean isConveyorEnabled) {
		this.isConveyorEnabled = isConveyorEnabled;
	}
	public boolean isInfraredEnabled() {
		return isInfraredEnabled;
	}
	public void setInfraredEnabled(boolean isInfraredEnabled) {
		this.isInfraredEnabled = isInfraredEnabled;
	}

	
}
