package com.jimi.smt.eps_server.entity.vo;

import com.jimi.smt.eps_server.entity.ResultJson;
import com.jimi.smt.eps_server.entity.User;

public class UserResultJson extends ResultJson {

	private User data;

	public User getData() {
		return data;
	}

	public void setData(User data) {
		this.data = data;
	}

	

}
