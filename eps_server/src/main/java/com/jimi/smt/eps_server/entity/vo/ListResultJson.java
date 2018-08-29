package com.jimi.smt.eps_server.entity.vo;

import java.util.List;

import com.jimi.smt.eps_server.entity.ResultJson;

public class ListResultJson<T> extends ResultJson{

	private List<T> data;

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	
	
}
