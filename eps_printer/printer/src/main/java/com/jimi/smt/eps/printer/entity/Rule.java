package com.jimi.smt.eps.printer.entity;

import java.io.Serializable;

public class Rule implements Serializable{

	/**
	 * serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 4255390900355647226L;

	private String name;
	
	private String example;
	
	private String allRules;

	public String getAllRules() {
		return allRules;
	}

	public void setAllRules(String allRules) {
		this.allRules = allRules;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = example;
	}

}
