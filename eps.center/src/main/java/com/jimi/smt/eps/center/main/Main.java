package com.jimi.smt.eps.center.main;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import com.jimi.smt.eps.center.socket.AlarmConnectToSocket;
import com.jimi.smt.eps.center.socket.DisplayConnectToSocket;
import com.jimi.smt.eps.center.thread.ConnectToServerThread;
import com.jimi.smt.eps.center.thread.UpdateBoardNumThread;

import cc.darhao.dautils.api.ResourcesUtil;
import cc.darhao.dautils.api.TextFileUtil;

public class Main {
	
	private static String fileNameTemp;

	public static void main(String[] args) throws IOException {
		// 初始化Logger
		ConfigurationSource source;
		try {
			// 初始化文件内容
			createFile("/board_num", "0");
			createFile("/state", "00000011");
			source = new ConfigurationSource(ResourcesUtil.getResourceAsStream("log4j/log4j.xml"));
			Configurator.initialize(null, source);
			// 开始监听服务器
			new Thread(() -> {
				AlarmConnectToSocket alarmConnectToSocket = new AlarmConnectToSocket();
				alarmConnectToSocket.open();
			}).start();
			// 开启连接服务器线程
			new ConnectToServerThread().start();
			// 开启更新板子数量线程
			new UpdateBoardNumThread().start();
			// 开启监听display线程
			new Thread(() -> {
				DisplayConnectToSocket displayConnectToSocket = new DisplayConnectToSocket();
				displayConnectToSocket.open();
			}).start();
		} catch (IOException e) {
			LogManager.getRootLogger().error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static void createFile(String fileName, String fileContent) {
		fileNameTemp = System.getProperty("user.dir") + fileName + ".txt";
		File file = new File(fileNameTemp);
		try {
			if(!file.exists()) {
				file .createNewFile();
				System.out.println("成功创建文件" + fileNameTemp);				
			}
			TextFileUtil.writeToFile(fileNameTemp, fileContent);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
