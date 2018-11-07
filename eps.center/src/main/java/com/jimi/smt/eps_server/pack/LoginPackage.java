package com.jimi.smt.eps_server.pack;

import java.io.Serializable;

import cc.darhao.jiminal.annotation.Parse;
import cc.darhao.jiminal.annotation.Protocol;
import com.jimi.smt.eps_server.pack.BasePackage;

@Protocol(0x4C)
public class LoginPackage extends BasePackage implements Serializable{

	/**
	 * serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 9074694751433132651L;
	
	@Parse({0,6})
	private String centerControllerMAC;

	public String getCenterControllerMAC() {
		return centerControllerMAC;
	}

	public void setCenterControllerMAC(String centerControllerMAC) {
		this.centerControllerMAC = centerControllerMAC;
	}
	
}
