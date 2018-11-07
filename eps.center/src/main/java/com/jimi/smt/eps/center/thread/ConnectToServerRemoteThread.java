package com.jimi.smt.eps.center.thread;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jimi.smt.eps.center.util.IniReader;
import com.jimi.smt.eps_server.rmi.ConnectToServerRemote;

/**与服务端进行通讯线程
 * @package  com.jimi.smt.eps.center.thread
 * @file     ConnectToServerThread.java
 * @author   HCJ
 * @date     2018年9月25日 下午5:08:25
 * @version  V 1.0
 */
public class ConnectToServerRemoteThread extends Thread {

    private static Logger logger = LogManager.getRootLogger();

    /**
     * heart_cycle : 心跳包周期
     */
    private long heart_cycle;

    /**
     * board_cycle : 上传板子数量包周期
     */
    private long board_cycle;
       
    /**
     * connectToServerSocket : 中控连接服务器的socket
     */
    private ConnectToServerRemote connectToServerRemote = new ConnectToServerRemote();
    
    /**
     * CONFIG_FILE : 记录各项配置的文件
     */
    private static final String CONFIG_FILE = "/config.ini";
    
    
    /**
     * <p>Title: 连接到服务端线程</p>
     * <p>Description: 初始化</p>
     */
    public ConnectToServerRemoteThread() {
        Map<String, String> map_cycle = IniReader.getItem(System.getProperty("user.dir") + CONFIG_FILE,"cycle");
        heart_cycle = Integer.parseInt(map_cycle.get("heart_cycle"));       
        board_cycle = Integer.parseInt(map_cycle.get("board_cycle"));
    }

    
    @Override
    public void run() {
		// 提示已运行
		logger.info("SMT 中控   开始发送心跳和板子数量包!");
		try {
			Thread heart = new Thread() {
				@Override
				public void run() {
					while (true) {
						try {
							Thread.sleep(heart_cycle);
							connectToServerRemote.sendHeartPackageToServerRemote();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			};
			heart.start();
			Thread boardNum = new Thread() {
				@Override
				public void run() {
					while (true) {
						try {
							Thread.sleep(board_cycle);
							connectToServerRemote.sendBoardNumPackageToServerRemote();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			};
			boardNum.start();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			try {
				run();
			} catch (Exception e1) {
				e1.printStackTrace();
				logger.error(e1.getMessage());
			}
		}
    }
}
