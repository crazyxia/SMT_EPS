package com.jimi.smt.eps.printer.entity;

public class WebSocketResult {

	private String id;
	private Integer result;
	private String data;
	
	public Integer getResult() {
		return result;
	}
	public void setResult(Integer result) {
		this.result = result;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public void succeed(String id, String data) {
		this.setId(id);
		this.setResult(200);
		this.setData(data);
	}
	
	public void fail(String id, String data) {
		this.setId(id);
		this.setResult(400);
		this.setData(data);
	}
}
