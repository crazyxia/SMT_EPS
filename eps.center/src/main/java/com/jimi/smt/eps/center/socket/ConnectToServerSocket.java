package com.jimi.smt.eps.center.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jimi.smt.eps.center.pack.BoardNumPackage;
import com.jimi.smt.eps.center.pack.BoardNumReplyPackage;
import com.jimi.smt.eps.center.pack.HeartPackage;
import com.jimi.smt.eps.center.pack.HeartReplyPackage;
import com.jimi.smt.eps.center.pack.LoginPackage;
import com.jimi.smt.eps.center.pack.LoginReplyPackage;
import com.jimi.smt.eps.center.util.IniReader;

import cc.darhao.dautils.api.TextFileUtil;
import cc.darhao.jiminal.core.SyncCommunicator;

public class ConnectToServerSocket {

    private static Logger logger = LogManager.getRootLogger();

    private SyncCommunicator communicator;

    private static final String PACKAGE_PATH = "com.jimi.smt.eps.center.pack";

    /**
     * 超时时间
     */
    private static final int TIME_OUT = 100000;
   
    /**
     * 产线
     */
    private String line;

    /**
     * 记录板子数量的文件
     */
    private static final String BOARDNUM_FILE = "/board_num.txt";

    /**
     * 记录报警灯、接驳台等硬件状态的文件
     */
    private static final String STATE_FILE = "/state.txt";

    /**
     * 记录各项配置的文件
     */
    private static final String CONFIG_FILE = "/config.ini";
    
    /**
     * 服务器的端口
     */
    private int listen_server_port = 23333;
    
    /**
     * 服务器的IP
     */
    private String connect_to_server_ip;
    
    /**
     * 初始化
     */
    public ConnectToServerSocket() {
        IniReader.setIni(System.getProperty("user.dir")+CONFIG_FILE);       
        Map<String, String> map_ip = IniReader.getItem("ip");
        connect_to_server_ip = map_ip.get("connect_to_server_ip");
        communicator = new SyncCommunicator(connect_to_server_ip, listen_server_port, PACKAGE_PATH);
        communicator.setTimeout(TIME_OUT);
        try {
            communicator.connect();
            sendCmdToServerForLogin();
            logger.info("ID为：" + line + " 的产线已连接服务器");            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    /**
     * 发送心跳包到服务器
     */
    @SuppressWarnings("unused")
    public synchronized void sendCmdToServerForHeart() throws Exception {
        HeartPackage heartPackage = new HeartPackage();
        heartPackage.setLine(line);
        heartPackage.setTimestamp(new Date());
        String state = TextFileUtil.readFromFile(System.getProperty("user.dir")+STATE_FILE);
        if (state.substring(state.length() - 3, state.length() - 2).equals("1")) {
            heartPackage.setAlarmEnabled(true);
        } else {
            heartPackage.setAlarmEnabled(false);
        }
        if (state.substring(state.length() - 2, state.length() - 1).equals("1")) {
            heartPackage.setConveyorEnabled(true);
        } else {
            heartPackage.setConveyorEnabled(false);
        }
        if (state.substring(state.length() - 1, state.length()).equals("1")) {
            heartPackage.setInfraredEnabled(true);
        } else {
            heartPackage.setInfraredEnabled(false);
        }
        HeartReplyPackage heartReplyPackage = (HeartReplyPackage) communicator.send(heartPackage);
        logger.info("在时间: " + heartPackage.getTimestamp() + " ID为: " + line + " 的产线发送心跳包");
    }

    
    /**
     * 发送登录包到服务器
     */
    public synchronized void sendCmdToServerForLogin() throws Exception {
        LoginPackage loginPackage = new LoginPackage();
        loginPackage.setCenterControllerMAC(getMACAddress());
        LoginReplyPackage loginReplyPackage = (LoginReplyPackage) communicator.send(loginPackage);
        line = loginReplyPackage.getLine();
        logger.info("在时间: " + loginReplyPackage.getTimestamp() + " ID为: " + line + " 的产线发送登录包");
    }

    
    /**
     * 发送上传板子数量包到服务器
     */
    @SuppressWarnings("unused")
    public synchronized void sendCmdToServerForBoardNum() throws Exception {
        BoardNumPackage boardNumPackage = new BoardNumPackage();
        String boardNum = TextFileUtil.readFromFile(System.getProperty("user.dir")+BOARDNUM_FILE);
        if (boardNum == null || boardNum.equals("")) {
            TextFileUtil.writeToFile(System.getProperty("user.dir")+BOARDNUM_FILE, "0");
            boardNumPackage.setBoardNum(Integer.parseInt("0"));
        } else {
            boardNumPackage.setBoardNum(Integer.parseInt(TextFileUtil.readFromFile(System.getProperty("user.dir")+BOARDNUM_FILE)));
        }
        boardNumPackage.setLine(line);
        boardNumPackage.setTimestamp(new Date());
        BoardNumReplyPackage boardNumReplyPackage = (BoardNumReplyPackage) communicator.send(boardNumPackage);
        logger.info("在时间: " + boardNumPackage.getTimestamp() + " ID为: " + line + " 的产线发送上传板子数量包");
    }

    
    /**
     * 重新连接
     */
    public void reconnect() throws Exception {
        communicator.close();
        communicator.connect();
        logger.info("重新连接成功");
    }

    
    /**
     * 获取树莓派的mac地址
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
        return mac.replaceAll(":", " ");
    }
}
