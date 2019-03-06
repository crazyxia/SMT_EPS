package com.jimi.smt.eps.center.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Map;

import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import com.jimi.smt.eps.center.thread.ReConnectWebsocketThread;
import com.jimi.smt.eps.center.thread.SendBoardNumInfoThread;
import com.jimi.smt.eps.center.thread.UpdateBoardNumThread;
import com.jimi.smt.eps.center.util.IniReader;
import com.jimi.smt.eps.center.websocket.CenterClientSocket;

import cc.darhao.dautils.api.ResourcesUtil;
import cc.darhao.dautils.api.TextFileUtil;


public class Main {

	/**
	 * CONFIG_FILE : 记录各项配置的文件
	 */
	private static final String CONFIG_FILE = "/config.ini";

	/**
	 * BOARD_NUM_FILE : 记录板子数量文件
	 */
	private static final String BOARD_NUM_FILE = "/board_num.txt";

	/**
	 * STATE_FILE : 记录树莓派状态文件
	 */
	private static final String STATE_FILE = "/state.txt";

	// 连接websocket服务端线程信息
	private static Runnable connectToServerSocketRunnable = null;
	private static Thread connectToServerSocketThread = null;
	public static Session session;


	public static void main(String[] args) throws IOException {
		ConfigurationSource source;
		try {
			// 初始化文件内容
			TextFileUtil.writeToFile(System.getProperty("user.dir") + BOARD_NUM_FILE, "0");
			TextFileUtil.writeToFile(System.getProperty("user.dir") + STATE_FILE, "00000011");
			source = new ConfigurationSource(ResourcesUtil.getResourceAsStream("log4j/log4j.xml"));
			Configurator.initialize(null, source);
			// 初始化websocket客户端
			initWebsocketClient();
			// 开启上传板子数量信息到服务端线程
			new SendBoardNumInfoThread().start();
			// 开启更新板子数量线程
			new UpdateBoardNumThread().start();
			// 开启中控重连websocket服务端线程
			new ReConnectWebsocketThread().start();
		} catch (Exception e) {
			LogManager.getRootLogger().error(e.getMessage());
		}
	}


	/**@author HCJ
	 * 重新连接websocket服务端
	 * @date 2019年3月5日 下午4:33:50
	 */
	public static void reConnect() {
		try {
			Thread.sleep(5000);
			if (!(session != null && session.isOpen() && connectToServerSocketThread.isAlive())) {
				if (session != null && session.isOpen() && !connectToServerSocketThread.isAlive()) {
					session.close();
				}
				if (connectToServerSocketThread.isAlive()) {
					connectToServerSocketThread.interrupt();
				}
				connectToServerSocketThread = new Thread(connectToServerSocketRunnable);
				connectToServerSocketThread.start();
			}
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}


	public static Session getSession() {
		return session;
	}


	/**
	 * @author HCJ 获取树莓派Mac地址
	 * @date 2019年2月28日 下午2:42:45
	 */
	private static String getMACAddress() throws Exception {
		String mac = null;
		try {
			Process pro = Runtime.getRuntime().exec("ifconfig");
			InputStream is = pro.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String message = br.readLine();
			int index = -1;
			while (message != null) {
				if ((index = message.indexOf("HWaddr")) > 0) {
					mac = message.substring(index + 7).trim();
					break;
				}
				message = br.readLine();
			}
			br.close();
			pro.destroy();
		} catch (IOException e) {
			System.out.println("出错了，不能得到mac地址!");
			return null;
		}
		return mac;
	}


	/**@author HCJ
	 * 初始化websocket客户端
	 * @date 2019年3月5日 下午4:32:45
	 */
	private static void initWebsocketClient() {
		Map<String, String> websocketMap = IniReader.getItem(System.getProperty("user.dir") + CONFIG_FILE, "websocketUrl");
		String websocketUrl = websocketMap.get("websocketUrl");
		CenterClientSocket centerClientSocket = new CenterClientSocket();
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		connectToServerSocketRunnable = new Runnable() {
	
			@Override
			public void run() {
				try {
					session = container.connectToServer(centerClientSocket, URI.create(websocketUrl + getMACAddress()));
					while (!Thread.currentThread().isInterrupted()) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
							session.close();
							return;
						}
					}
					session.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		connectToServerSocketThread = new Thread(connectToServerSocketRunnable);
		connectToServerSocketThread.setDaemon(true);
		connectToServerSocketThread.setName("connectToServerSocketThread");
		// 设置关闭回调方法
		centerClientSocket.setCloseCallBack(() -> {
			reConnect();
		});
		connectToServerSocketThread.start();
	}

}
