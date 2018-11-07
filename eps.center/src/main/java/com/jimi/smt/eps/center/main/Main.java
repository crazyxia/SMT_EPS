package com.jimi.smt.eps.center.main;

import java.io.File;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import com.jimi.smt.eps.center.thread.ConnectToServerRemoteThread;
import com.jimi.smt.eps.center.thread.UpdateBoardNumThread;
import com.jimi.smt.eps.center.util.IpHelper;
import com.jimi.smt.eps_server.rmi.CenterRemote;
import com.jimi.smt.eps_server.rmi.CenterRemoteImpl;

import cc.darhao.dautils.api.ResourcesUtil;
import cc.darhao.dautils.api.TextFileUtil;

public class Main {

	/**
	 * fileNameTemp : 文件名
	 */
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
			// 开启中控RMI服务端
			System.setProperty("java.rmi.server.hostname", IpHelper.getLinuxLocalIp());
			CenterRemote centerRemote = (CenterRemote) UnicastRemoteObject.exportObject(new CenterRemoteImpl(), 12345);
			LocateRegistry.createRegistry(1099);
			try {
				LocateRegistry.getRegistry().bind("center", centerRemote);
			} catch (AlreadyBoundException e) {
				System.out.println("请勿重复绑定");
				e.printStackTrace();
			}
			LogManager.getRootLogger().info("中控："+ IpHelper.getLinuxLocalIp() + " RMI服务端开启");
			// 开启连接服务端线程
			new ConnectToServerRemoteThread().start();
			// 开启更新板子数量线程
			new UpdateBoardNumThread().start();
		} catch (IOException e) {
			LogManager.getRootLogger().error(e.getMessage());
			e.printStackTrace();
		}
	}

	
	/**
	 * @author HCJ 文件不存在则创建文件并写入初始值
	 * @method createFile
	 * @param fileName
	 * @param fileContent
	 * @return void
	 * @date 2018年9月25日 下午4:40:50
	 */
	private static void createFile(String fileName, String fileContent) {
		fileNameTemp = System.getProperty("user.dir") + fileName + ".txt";
		File file = new File(fileNameTemp);
		try {
			if (!file.exists()) {
				file.createNewFile();
				System.out.println("成功创建文件" + fileNameTemp);
			}
			TextFileUtil.writeToFile(fileNameTemp, fileContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
