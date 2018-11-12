package com.jimi.smt.eps.printer.entity;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;

public class MaterialTbResultData {
	private SimpleStringProperty fileName;
	
	private SimpleStringProperty fileSize;
	
	private SimpleStringProperty filePath;

	public String getFileName() {
		return fileName.get();
	}

	public void setFileName(SimpleStringProperty fileName) {
		this.fileName = fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = new SimpleStringProperty(fileName);
	}

	public String getFileSize() {
		return fileSize.get();
	}

	public void setFileSize(SimpleStringProperty fileSize) {
		this.fileSize = fileSize;
	}
	
	public void setFileSize(String fileSize) {
		this.fileSize = new SimpleStringProperty(fileSize);
	}

	public String getFilePath() {
		return filePath.get();
	}

	public void setFilePath(SimpleStringProperty filePath) {
		this.filePath = filePath;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = new SimpleStringProperty(filePath);
	}
	public MaterialTbResultData() {
		
	}
	public MaterialTbResultData(MaterialTb materialTb) {
		
		fileName = new SimpleStringProperty(materialTb.getFileName());
		fileSize = new SimpleStringProperty(materialTb.getFileSize());
		filePath = new SimpleStringProperty(materialTb.getFilePath());
	}
	public static List<MaterialTbResultData> materialTbToReusltData(List<MaterialTb> materialTbs){
		List<MaterialTbResultData> materialTbResultDatas = new ArrayList<>();
		for (MaterialTb materialTb : materialTbs) {
			MaterialTbResultData materialTbResultData = new MaterialTbResultData(materialTb);
			materialTbResultDatas.add(materialTbResultData);
		}
		return materialTbResultDatas;
	}
}
