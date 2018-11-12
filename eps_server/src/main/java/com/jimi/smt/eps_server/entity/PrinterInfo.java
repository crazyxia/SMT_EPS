package com.jimi.smt.eps_server.entity;


public class PrinterInfo {

	private String id;
	private String materialId;
	private String user;
	private String productDate;
	private String remainingQuantity;
	private String materialNo;
	private String supplier;
	
	
	public String getMaterialId() {
		return materialId;
	}
	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getProductDate() {
		return productDate;
	}
	public void setProductDate(String productDate) {
		this.productDate = productDate;
	}
	public String getMaterialNo() {
		return materialNo;
	}
	public void setMaterialNo(String materialNo) {
		this.materialNo = materialNo;
	}


	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRemainingQuantity() {
		return remainingQuantity;
	}
	public void setRemainingQuantity(String remainingQuantity) {
		this.remainingQuantity = remainingQuantity;
	}

	public PrinterInfo(String id,String materialId,String user,String productDate,String remainingQuantity,String materialNo,String supplier) {
		this.id = id;
		this.supplier = supplier;
		this.user = user;
		this.materialId = materialId;
		this.materialNo = materialNo;
		this.productDate = materialNo;
		this.remainingQuantity = remainingQuantity;
	}
}
