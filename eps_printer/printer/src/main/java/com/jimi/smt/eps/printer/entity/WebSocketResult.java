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
	
	public static WebSocketResult succeed(String id, String data) {
		WebSocketResult webSocketResult  = new WebSocketResult();
		webSocketResult.setId(id);
		webSocketResult.setResult(200);
		webSocketResult.setData(data);
		return webSocketResult;
	}
	
	public static WebSocketResult fail(String id, String data) {
		WebSocketResult webSocketResult  = new WebSocketResult();
		webSocketResult.setId(id);
		webSocketResult.setResult(400);
		webSocketResult.setData(data);
		return webSocketResult;
	}
}
