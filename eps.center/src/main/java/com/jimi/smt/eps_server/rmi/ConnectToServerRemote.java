package com.jimi.smt.eps_server.rmi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.rmi.registry.LocateRegistry;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jimi.smt.eps_server.pack.BoardNumPackage;
import com.jimi.smt.eps_server.pack.HeartPackage;
import com.jimi.smt.eps_server.pack.LoginPackage;
import com.jimi.smt.eps_server.pack.LoginReplyPackage;
import com.jimi.smt.eps.center.util.IniReader;
import com.jimi.smt.eps.center.util.IpHelper;

import cc.darhao.dautils.api.TextFileUtil;

/**与RMI服务端进行通讯
 * @author   HCJ
 * @date     2018年11月5日 下午4:11:25
 */
public class ConnectToServerRemote {

    private static Logger logger = LogManager.getRootLogger();
   
    /**
     * line : 产线
     */
    private int line;

    /**
     * BOARDNUM_FILE : 记录板子数量的文件
     */
    private static final String BOARDNUM_FILE = "/board_num.txt";

    /**
     * STATE_FILE : 记录报警灯、接驳台等硬件状态的文件
     */
    private static final String STATE_FILE = "/state.txt";

    /**
     * CONFIG_FILE : 记录各项配置的文件
     */
    private static final String CONFIG_FILE = "/config.ini";
    
    /**
     * connect_to_server_ip : 服务器的IP
     */
    private String connect_to_server_ip;
    
    /**
     * localIp : 本地IP
     */
    private String localIp;
    
    /**
     * server : RMI服务端对象
     */
    private ServerRemote server;
    
    
    /**
     * <p>Title: 查找RMI服务端对象</p>
     * <p>Description: 初始化操作</p>
     */
	public ConnectToServerRemote() {
		Map<String, String> map_ip = IniReader.getItem(System.getProperty("user.dir") + CONFIG_FILE, "ip");
		connect_to_server_ip = map_ip.get("connect_to_server_ip");
		localIp = IpHelper.getLinuxLocalIp();
		try {
			server = (ServerRemote) LocateRegistry.getRegistry(connect_to_server_ip).lookup("server");
			sendLoginPackageToServerRemote();
		} catch (Exception e) {
			new ConnectToServerRemote();
			e.printStackTrace();
		}
		logger.info("ID为：" + line + " 的产线已连接服务器");
	}

    
    /**@author HCJ
     * 发送心跳包到服务器
     * @date 2018年11月5日 下午4:09:28
     */
    public synchronized void sendHeartPackageToServerRemote() throws Exception {
		HeartPackage heartPackage = new HeartPackage();
		heartPackage.setLine(line);
		heartPackage.setTimestamp(new Date());
		String state = TextFileUtil.readFromFile(System.getProperty("user.dir") + STATE_FILE);
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
		heartPackage.protocol = "Heart";
		heartPackage.senderIp = localIp;
		heartPackage.receiverIp = connect_to_server_ip;
		heartPackage.serialNo = 0;
		server.saveHeartPackageLog(heartPackage);
		logger.info("在时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(heartPackage.getTimestamp()) + " ID为: " + line + " 的产线发送心跳包");
    }

    
	/**
	 * @author HCJ 发送登录包到服务器
	 * @throws Exception
	 * @date 2018年11月5日 下午4:09:54
	 */
	public synchronized void sendLoginPackageToServerRemote() throws Exception {
		LoginPackage loginPackage = new LoginPackage();
		loginPackage.setCenterControllerMAC(getMACAddress());
		loginPackage.protocol = "Login";
		loginPackage.senderIp = localIp;
		loginPackage.receiverIp = connect_to_server_ip;
		loginPackage.serialNo = 0;
		LoginReplyPackage loginReplyPackage = server.login(loginPackage);
		line = loginReplyPackage.getLine();
		logger.info("在时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(loginReplyPackage.getTimestamp()) + " ID为: " + line + " 的产线发送登录包");
	}

    
    /**@author HCJ
     * 发送板子数量包到服务器
     * @date 2018年11月5日 下午4:10:17
     */
    public synchronized void sendBoardNumPackageToServerRemote() throws Exception {
		BoardNumPackage boardNumPackage = new BoardNumPackage();
		String boardNum = TextFileUtil.readFromFile(System.getProperty("user.dir") + BOARDNUM_FILE);
		if (boardNum == null || boardNum.equals("")) {
			TextFileUtil.writeToFile(System.getProperty("user.dir") + BOARDNUM_FILE, "0");
			boardNumPackage.setBoardNum(Integer.parseInt("0"));
		} else {
			boardNumPackage.setBoardNum(Integer.parseInt(TextFileUtil.readFromFile(System.getProperty("user.dir") + BOARDNUM_FILE)));
		}
		boardNumPackage.setLine(line);
		boardNumPackage.setTimestamp(new Date());
		boardNumPackage.protocol = "BoardNum";
		boardNumPackage.senderIp = localIp;
		boardNumPackage.receiverIp = connect_to_server_ip;
		boardNumPackage.serialNo = 0;
		server.updateBoardNum(boardNumPackage);
		logger.info("在时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(boardNumPackage.getTimestamp()) + " ID为: " + line + " 的产线发送上传板子数量包");
    }

    
    /**@author HCJ
     * 获取mac地址
     * @method getMACAddress
     * @return
     * @throws Exception
     * @return String
     * @date 2018年9月25日 下午4:42:20
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
