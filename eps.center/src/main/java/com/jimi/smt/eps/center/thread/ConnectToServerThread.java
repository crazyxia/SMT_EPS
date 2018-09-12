package com.jimi.smt.eps.center.thread;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jimi.smt.eps.center.socket.ConnectToServerSocket;
import com.jimi.smt.eps.center.util.IniReader;

public class ConnectToServerThread extends Thread {

    private static Logger logger = LogManager.getRootLogger();

    /**
     * 心跳包周期
     */
    private long heart_cycle;

    /**
     * 上传板子数量包周期
     */
    private long board_cycle;
       
    /**
     * 中控连接服务器的socket
     */
    private ConnectToServerSocket connectToServerSocket = new ConnectToServerSocket();
    
    /**
     * 记录各项配置的文件
     */
    private static final String CONFIG_FILE = "/config.ini";
    
    /**
     * 初始化
     */
    public ConnectToServerThread() {
        IniReader.setIni(System.getProperty("user.dir") + CONFIG_FILE);
        Map<String, String> map_cycle = IniReader.getItem("cycle");
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
                            connectToServerSocket.sendCmdToServerForHeart();
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
                            connectToServerSocket.sendCmdToServerForBoardNum();
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
                connectToServerSocket.reconnect();
                run();
            } catch (Exception e1) {
                e1.printStackTrace();
                logger.error(e1.getMessage());
            }
        }
    }
}
