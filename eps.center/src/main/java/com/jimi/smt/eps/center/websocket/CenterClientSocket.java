package com.jimi.smt.eps.center.websocket;

import java.io.IOException;
import java.util.Date;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.jimi.smt.eps.center.callback.ConnnectToServerSocketCloseCallBack;
import com.jimi.smt.eps.center.controltool.CenterController;
import com.jimi.smt.eps.center.entity.BoardNumInfo;
import com.jimi.smt.eps.center.entity.BoardResetInfo;
import com.jimi.smt.eps.center.entity.CenterControlInfo;
import com.jimi.smt.eps.center.util.IpHelper;

import cc.darhao.dautils.api.TextFileUtil;


/**中控websocket客户端
 * @author   HCJ
 * @date     2019年2月28日 上午11:47:12
 */
@ClientEndpoint
public class CenterClientSocket {

	private static Logger logger = LogManager.getRootLogger();

	private static Session session;
	
	/**
	 * BOARDNUM_FILE : 记录板子数量的文件
	 */
	private static final String BOARDNUM_FILE = "/board_num.txt";
	
	/**
	 * lineId : 产线ID
	 */
	private static Integer lineId;
	
	private ConnnectToServerSocketCloseCallBack closeCallBack;


	/**@author HCJ
	 * 连接时调用
	 * @date 2019年2月28日 上午11:47:46
	 */
	@OnOpen
	public void onOpen(Session session) {
		CenterClientSocket.session = session;
		logger.info("在" + new Date() + " 时，IP为 " + IpHelper.getLinuxLocalIp() + " 的中控连接服务器成功");
	}


	/**@author HCJ
	 * 接收服务端信息时调用
	 * @date 2019年2月28日 上午11:48:00
	 */
	@OnMessage
	public void onMessage(String message) {
		if (message != null && message.contains("controlledDevice")) {
			CenterControlInfo centerControlInfo = JSONObject.parseObject(message, CenterControlInfo.class);
			CenterController.setCenterState(centerControlInfo);
			// logger.info("在" + new Date() + " 时，IP为  " + IpHelper.getLinuxLocalIp() + " 的中控接收控制信息，内容为： " + centerControlInfo.getOperation() + " " + centerControlInfo.getControlledDevice());
		} else if (message != null && message.contains("boardResetReson")) {
			sendBoardnumInfo(message);
		}
	}


	/**@author HCJ
	 * 关闭时调用
	 * @param closeReason 关闭原因
	 * @date 2019年2月28日 上午11:48:19
	 */
	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		logger.info("在" + new Date() + " 时，IP为 " + IpHelper.getLinuxLocalIp() + " 的中控与服务器连接关闭，原因为 " + closeReason.toString());
		if(closeCallBack != null) {
			closeCallBack.connnectToServerSocketCloseCallBack(closeReason);
		}
	}


	/**@author HCJ
	 * 连接出错时调用
	 * @date 2019年2月28日 上午11:48:41
	 */
	@OnError
	public void onError(Session session, Throwable error) {
		logger.error("中控与服务器连接出现错误： " + error.getMessage());
	}


	/**@author HCJ
	 * 发送板子数量信息到服务器
	 * @date 2019年2月28日 上午11:49:01
	 */
	public static synchronized void sendBoardnumInfo(String boardResetInfoString) {
		if (!"".equals(boardResetInfoString)) {
			BoardResetInfo boardResetInfo = JSONObject.parseObject(boardResetInfoString, BoardResetInfo.class);
			lineId = boardResetInfo.getLine();
		}
		BoardNumInfo boardNumInfo = new BoardNumInfo();
		try {
			String boardNum = TextFileUtil.readFromFile(System.getProperty("user.dir") + BOARDNUM_FILE);
			if (boardNum == null || boardNum.equals("")) {
				TextFileUtil.writeToFile(System.getProperty("user.dir") + BOARDNUM_FILE, "0");
				boardNumInfo.setBoardNum(Integer.parseInt("0"));
			} else {
				boardNumInfo.setBoardNum(Integer.parseInt(TextFileUtil.readFromFile(System.getProperty("user.dir") + BOARDNUM_FILE)));
			}
			boardNumInfo.setLine(lineId);
			JSONObject jsonObject = (JSONObject) JSONObject.toJSON(boardNumInfo);
			session.getBasicRemote().sendText(jsonObject.toJSONString());
			TextFileUtil.writeToFile(System.getProperty("user.dir") + BOARDNUM_FILE, "0");
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}


	public ConnnectToServerSocketCloseCallBack getCloseCallBack() {
		return closeCallBack;
	}


	public void setCloseCallBack(ConnnectToServerSocketCloseCallBack closeCallBack) {
		this.closeCallBack = closeCallBack;
	}
}
