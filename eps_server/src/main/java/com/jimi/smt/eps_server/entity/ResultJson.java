package com.jimi.smt.eps_server.entity;


public class ResultJson {

	private int code;
	
	private String msg;
		
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "ResultJson [code=" + code + ", msg=" + msg + "]";
	}
	
	
}
