package com.jimi.smt.eps.printer.entity;

import javafx.beans.property.SimpleStringProperty;

public final class RuleResultData {

	SimpleStringProperty name = new SimpleStringProperty();
	
	SimpleStringProperty example = new SimpleStringProperty();
	
	SimpleStringProperty details = new SimpleStringProperty();
	
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
	
	public String getDetails() {
		return details.get();
	}

	public void setDetails(String details) {
		this.details.set(details);
	}
}
