package com.jimi.smt.eps_server.entity;

import java.util.Date;

public class OperationDetials {
	
    private long id;
    private String operator;
    private Date time;
    private Integer type;
    private String result;
    private String lineseat;
    private String materialNo;
    private String oldMaterialNo;
    private String scanlineseat;
    private String remark;
    private String programId;
    private String workOrder;
    private Integer boardType;
    private String line;
    private String specitification;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getOperator() {
		return operator;
	}
	
	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	public Date getTime() {
		return time;
	}
	
	public void setTime(Date time) {
		this.time = time;
	}
	
	public Integer getType() {
		return type;
	}
	
	public void setType(Integer type) {
		this.type = type;
	}
	
	public String getResult() {
		return result;
	}
	
	public void setResult(String result) {
		this.result = result;
	}
	
	public String getLineseat() {
		return lineseat;
	}
	
	public void setLineseat(String lineseat) {
		this.lineseat = lineseat;
	}
	
	public String getMaterialNo() {
		return materialNo;
	}
	
	public void setMaterialNo(String materialNo) {
		this.materialNo = materialNo;
	}
	
	public String getOldMaterialNo() {
		return oldMaterialNo;
	}
	
	public void setOldMaterialNo(String oldMaterialNo) {
		this.oldMaterialNo = oldMaterialNo;
	}
	
	public String getScanlineseat() {
		return scanlineseat;
	}
	
	public void setScanlineseat(String scanlineseat) {
		this.scanlineseat = scanlineseat;
	}
	
	public String getRemark() {
		return remark;
	}
	
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public String getProgramId() {
		return programId;
	}
	
	public void setProgramId(String programId) {
		this.programId = programId;
	}
	
	public String getWorkOrder() {
		return workOrder;
	}
	
	public void setWorkOrder(String workOrder) {
		this.workOrder = workOrder;
	}
	
	public Integer getBoardType() {
		return boardType;
	}
	
	public void setBoardType(Integer boardType) {
		this.boardType = boardType;
	}
	
	public String getLine() {
		return line;
	}
	
	public void setLine(String line) {
		this.line = line;
	}
	
	public String getSpecitification() {
		return specitification;
	}
	
	public void setSpecitification(String specitification) {
		this.specitification = specitification;
	}
    
    
}
