package com.jimi.smt.eps.center.thread;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jimi.smt.eps.center.util.IniReader;
import com.jimi.smt.eps.center.websocket.CenterClientSocket;


/**上传板子数量信息到服务器的线程
 * @author HCJ
 * @date 2018年11月29日 下午2:33:56
 */
public class SendBoardNumInfoThread extends Thread {

	private static Logger logger = LogManager.getRootLogger();

	/**
	 * boardCycle : 上传板子数量信息周期
	 */
	private long boardCycle;

	/**
	 * CONFIG_FILE : 记录各项配置的文件
	 */
	private static final String CONFIG_FILE = "/config.ini";


	/**
	 * 上传板子数量信息到服务器的线程
	 */
	public SendBoardNumInfoThread() {
		Map<String, String> cycleMap = IniReader.getItem(System.getProperty("user.dir") + CONFIG_FILE, "cycle");
		boardCycle = Integer.parseInt(cycleMap.get("boardCycle"));
	}


	@Override
	public void run() {
		// 提示已运行
		logger.info("SMT 中控   开始上传板子数量信息!");
		while (true) {
			try {
				Thread.sleep(boardCycle);
				sendBoardnumInfo();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}


	/**
	 * @author HCJ 发送板子数量信息到服务器
	 * @date 2019年2月28日 上午11:50:58
	 */
	private void sendBoardnumInfo() {
		CenterClientSocket.sendBoardnumInfo("");
	}
}
