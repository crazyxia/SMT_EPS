package com.jimi.smt.eps.printer.entity;

import javafx.beans.property.SimpleStringProperty;

public final class ResultData {

	SimpleStringProperty name = new SimpleStringProperty();
	
	SimpleStringProperty example = new SimpleStringProperty();
	
	SimpleStringProperty allRules = new SimpleStringProperty();
	
	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public String getExample() {
		return example.get();
	}

	public void setExample(String example) {
		this.example.set(example);
	}
	
	public String getAllRules() {
		return allRules.get();
	}

	public void setAllRules(String allRules) {
		this.allRules.set(allRules);
	}
}
