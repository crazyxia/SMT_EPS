package com.jimi.smt.eps.printer.printtool;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.jimi.smt.eps.printer.entity.MaterialFileInfo;
import com.jimi.smt.eps.printer.entity.MaterialFileInfoResultData;

import cc.darhao.dautils.api.TextFileUtil;

/**
 * 读写存储供应商料号表文件信息的Json文件
 * 
 * @author Administrator
 *
 */
public class PrintFileJsonReader {

	private static String FILENAME = "materialTb.json";
	private File file;
	private Logger logger = LogManager.getRootLogger();
	private static PrintFileJsonReader printFileJsonReader;

	
	private PrintFileJsonReader() {
		file = new File(FILENAME);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				logger.error("创建物料表路径存储文件失败|" + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	
	public static synchronized PrintFileJsonReader getInstance() {
		if (printFileJsonReader == null) {
			printFileJsonReader = new PrintFileJsonReader();
		}
		return printFileJsonReader;
	}

	
	public List<MaterialFileInfo> getJsonToObject() {
		List<MaterialFileInfo> materialTbs = null;
		String content = "";
		try {
			content = TextFileUtil.readFromFile(FILENAME);
			System.out.println(content);
		} catch (IOException e) {
			logger.error("读取物料表路径存储文件失败|" + e.getMessage());
			e.printStackTrace();
		}
		materialTbs = JSONArray.parseArray(content, MaterialFileInfo.class);
		return materialTbs;
	}

	
	public boolean writeJsonObject(List<MaterialFileInfoResultData> materialTbs) {
		String content = JSONArray.toJSONString(materialTbs);
		try {
			TextFileUtil.writeToFile(FILENAME, content);
			return true;
		} catch (IOException e) {
			logger.error("供应商料号表路径文件写入失败|" + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
}
