package com.jimi.smt.eps.printer.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.jimi.smt.eps.printer.entity.MaterialTb;
import com.jimi.smt.eps.printer.entity.MaterialTbResultData;

public class JsonFile {

	private static String FILENAME = "materialTb.json";
	private File file;
	private Logger logger = LogManager.getRootLogger();
	private static JsonFile jsonFile;
	private JsonFile() {
		file = new File(FILENAME);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				logger.error("创建物料表路径存储文件失败");
				e.printStackTrace();
			}
		}
		System.out.println(file.getAbsolutePath());
	}
	
	public static synchronized JsonFile getInstance() {
		if (jsonFile == null) {
			jsonFile = new JsonFile();
		}
		return jsonFile;
	}
	
	public List<MaterialTb> getJsonToObject(){
		List<MaterialTb> materialTbs = null;
		 String content = "";
		try {
			content = FileUtils.readFileToString(file,"UTF-8");
			System.out.println(content);
		} catch (IOException e) {
			logger.error("读取物料表路径存储文件失败");
			e.printStackTrace();
		}
		materialTbs = JSONArray.parseArray(content, MaterialTb.class);
		return materialTbs;
	}
	
	public boolean writeJsonObject(List<MaterialTbResultData> materialTbs) {
		
		String content = JSONArray.toJSONString(materialTbs);
		FileWriter fileWriter = null;
		BufferedWriter out = null;
		try {
			fileWriter = new FileWriter(file);
			out = new BufferedWriter(fileWriter);
			out.write(content);
			out.flush();
			return true;
		} catch (IOException e) {
			logger.error("写入物料表路径存储文件失败");
			e.printStackTrace();
			return false;
		}finally {
			try {
				if (out != null) {
					out.close();
				}
				if (fileWriter != null) {
					fileWriter.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
