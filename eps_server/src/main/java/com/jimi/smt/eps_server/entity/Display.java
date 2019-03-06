package com.jimi.smt.eps_server.entity;

public class Display {

	private Integer line;
	private String workOrder;
	private Integer boardType;
	private Boolean selected;

	public Integer getLine() {
		return line;
	}

	public void setLine(Integer line) {
		this.line = line;
	}

	public String getWorkOrder() {
		return workOrder;
	}

	public void setWorkOrder(String workOrder) {
		this.workOrder = workOrder == null ? null : workOrder.trim();
	}

	public Integer getBoardType() {
		return boardType;
	}

	public void setBoardType(Integer boardType) {
		this.boardType = boardType;
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
}