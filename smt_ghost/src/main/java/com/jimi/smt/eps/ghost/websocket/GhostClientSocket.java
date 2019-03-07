package com.jimi.smt.eps.ghost.websocket;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import com.jimi.smt.eps.ghost.parser.FujiPartUsageReportParser;
import com.jimi.smt.eps.ghost.robot.DataCollectRobot;

/**
 * 我是数据提取任务WebSocket</br>
 * 我与SMT-Server连接，接收来自它的请求，执行提取报表数据操作，返回解析好的报表数据
 * <br>
 * <b>2019年3月4日</b>
 * @author 几米物联自动化部-洪达浩
 */
@ClientEndpoint
public class GhostClientSocket {
	
	protected Session session;
	
	
	@OnOpen
	public void onOpen(Session session) throws IOException {
		this.session = session;
		System.out.println("成功连接SMT-Server");
	}

	
	/**
	 * @param message 接收参数：表示请求查询过去N分钟的抛料记录（类型：int）
	 */
	@OnMessage
	public void onMessage(String message) {
		try {
			System.out.println("接收信息：" + message);
			DataCollectRobot robot = new DataCollectRobot(new File("config.ini"));
			int pastMinutes = Integer.parseInt(message);
			String source = robot.getData(pastMinutes);
			FujiPartUsageReportParser parser = new FujiPartUsageReportParser(source);
			String response = parser.getJSON();
			sendData(response);
			System.out.println("发送信息：" + response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
//	@OnMessage
//	public void onMessageTest(String message) {
//		try {
//			System.out.println("接收信息：" + message);
//			String testSource = ClipBoardUtil.getClipboardString();//注意：此处将从系统粘贴板获取数据，内容来自文件：test-allLine-allMachie-allRecipe-report.html
//			FujiPartUsageReportParser parser = new FujiPartUsageReportParser(testSource);
//			String response = parser.getJSON();
//			sendData(response);
//			System.out.println("发送信息：" + response);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}


	private void sendData(String response) throws IOException {
		for (int i = 0; i < response.length(); i+=128) {
			try {
				session.getBasicRemote().sendText(response.substring(i, i + 128));
			} catch (IndexOutOfBoundsException e) {
				session.getBasicRemote().sendText(response.substring(i) + "#EOS#");
			}
		}
	}

	
	@OnError
	public void onError(Session session, Throwable e) {
		try {
			session.close();
			System.out.println("与SMT-Server连接出错，因为：" + e.getClass().getSimpleName() + " : " + e.getMessage());
			Thread.sleep(5000);
			connect();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	
	@OnClose
	public void onClose(Session session, CloseReason reason) {
		try {
			session.close();
			System.out.println("与SMT-Server连接断开，因为：" + reason.getReasonPhrase());
			Thread.sleep(5000);
			connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void connect() {
		while(true) {
			try {
				System.out.println("正在与SMT-Server建立连接...");
				Properties properties = new Properties();
				properties.load(new FileInputStream(new File("config.ini")));
				ContainerProvider.getWebSocketContainer().connectToServer(this, new URI(properties.getProperty("websocketURI")));
				break;
			} catch (Exception e) {
				System.out.println("连接失败，原因为："+e.getMessage());
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
