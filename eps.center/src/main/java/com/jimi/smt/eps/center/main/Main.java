package com.jimi.smt.eps.center.main;

import java.io.File;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import com.jimi.smt.eps.center.thread.SendBoardNumPackageThread;
import com.jimi.smt.eps.center.thread.UpdateBoardNumThread;
import com.jimi.smt.eps.center.util.IniReader;
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
	
	/**
	 * centerServicePort : RMI通信服务端口
	 */
	private static int centerServicePort;
	
	/**
	 * REGISTERED_PORT : RMI注册端口
	 */
	private static final int REGISTERED_PORT = 1099;
	
	/**
	 * CONFIG_FILE : 记录各项配置的文件
	 */
	private static final String CONFIG_FILE = "/config.ini";
	
	
	public static void main(String[] args) throws IOException {
		// 初始化Logger
		ConfigurationSource source;
		Map<String, String> portMap = IniReader.getItem(System.getProperty("user.dir") + CONFIG_FILE, "port");
		centerServicePort = Integer.parseInt(portMap.get("centerServicePort"));
		CenterRemoteImpl centerRemoteImpl = new CenterRemoteImpl();
		try {
			// 初始化文件内容
			writeToFile("/board_num", "0");
			writeToFile("/state", "00000011");
			source = new ConfigurationSource(ResourcesUtil.getResourceAsStream("log4j/log4j.xml"));
			Configurator.initialize(null, source);
			// 开启中控RMI服务端
			System.setProperty("java.rmi.server.hostname", IpHelper.getLinuxLocalIp());
			CenterRemote centerRemote = (CenterRemote) UnicastRemoteObject.exportObject(centerRemoteImpl, centerServicePort);
			LocateRegistry.createRegistry(REGISTERED_PORT);
			LocateRegistry.getRegistry().rebind("center", centerRemote);
			LogManager.getRootLogger().info("中控："+ IpHelper.getLinuxLocalIp() + " RMI服务端开启");
			// 开启上传板子数量包到服务端线程
			new SendBoardNumPackageThread().start();
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
	private static void writeToFile(String fileName, String fileContent) {
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
