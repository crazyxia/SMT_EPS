package com.jimi.smt.eps_server.entity;

/**
 * 产线选择信息类
 * @author HCJ
 * @date 2019年4月9日 上午10:06:42
 */
public class LineSelectedInfo {

	/**
	 * lineName : 产线名称
	 */
	private String lineName;

	/**
	 * selected : 是否被选中，false表示未被选中，true表示被选中
	 */
	private String selected;

	public String getLineName() {
		return lineName;
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public LineSelectedInfo(String lineName, String selected) {
		super();
		this.lineName = lineName;
		this.selected = selected;
	}
}
