package com.jimi.smt.eps_server.util;

public class ResultUtil2 {

	private int code;

	private String msg;

	private Object data;

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

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
		return "ResultUtil2 [code=" + code + ", msg=" + msg + ", data=" + data + "]";
	}
	
	public ResultUtil2() {
		
	}
	
	public ResultUtil2(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	public ResultUtil2 succeed(int code, String msg) {
		return new ResultUtil2(code, msg);
	}
	
	public ResultUtil2 faild(int code, String msg) {
		return new ResultUtil2(code, msg);
	}
	
}
