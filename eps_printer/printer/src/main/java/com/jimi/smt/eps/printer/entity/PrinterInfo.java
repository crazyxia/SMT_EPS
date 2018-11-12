package com.jimi.smt.eps.printer.entity;

import com.alibaba.fastjson.JSONObject;

public class PrinterInfo {

	private String id;
	private String supplier;
	private String materialId;
	private String user;
	private String productDate;
	private String remainingQuantity;
	private String materialNo;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
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
	public String getRemainingQuantity() {
		return remainingQuantity;
	}
	public void setRemainingQuantity(String remainingQuantity) {
		this.remainingQuantity = remainingQuantity;
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
	public static PrinterInfo jsonStringToPrnterInfo(String message) {
		
		PrinterInfo info = null;
		try {
			info = new PrinterInfo();
			JSONObject object = (JSONObject) JSONObject.parse(message);
			info.setId(object.getString("id"));
			info.setMaterialNo(object.getString("materialNo"));
			info.setMaterialId(object.getString("materialId"));
			info.setProductDate(object.getString("productDate"));
			info.setRemainingQuantity(object.getString("remainingQuantity"));
			info.setUser(object.getString("user"));
			info.setSupplier(object.getString("supplier"));
		}catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}
}
