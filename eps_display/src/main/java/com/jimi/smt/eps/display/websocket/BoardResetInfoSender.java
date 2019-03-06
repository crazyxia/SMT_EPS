package com.jimi.smt.eps.display.websocket;

import java.io.IOException;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.CloseReason.CloseCodes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.jimi.smt.eps.display.callback.SendBoardResetInfoCloseCallBack;
import com.jimi.smt.eps.display.entity.BoardResetInfo;


/**Display的websocket客户端，发送板子数量重置信息
 * @author   HCJ
 * @date     2019年3月1日 上午11:36:55
 */
@ClientEndpoint
public class BoardResetInfoSender {

	private static Logger logger = LogManager.getRootLogger();

	private static Session session;
	
	private SendBoardResetInfoCloseCallBack closeCallBack;
	
	/**
	 * ERROR_CLOSECODE : 自定义错误代码
	 */
	private static final Integer ERROR_CLOSECODE = 4888;


	/**@author HCJ
	 * 连接时调用
	 * @date 2019年3月1日 上午11:38:23
	 */
	@OnOpen
	public void onOpen(Session session) {
		BoardResetInfoSender.session = session;
		logger.info("sessionID为" + session.getId() + "的Display与服务器连接");
	}


	/**@author HCJ
	 * 接收服务端信息时调用
	 * @date 2019年3月1日 上午11:38:36
	 */
	@OnMessage
	public void onMessage(String message) {
		logger.info("接收到服务端信息" + message);
	}


	/**@author HCJ
	 * 关闭时调用
	 * @param closeReason 关闭原因
	 * @date 2019年3月1日 上午11:38:44
	 */
	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		logger.info("sessionID为  " + session.getId() + " 的Display客户端与服务端断开连接，原因为：" + closeReason);
		if(closeCallBack != null) {
			closeCallBack.sendBoardResetInfoCloseCallBack(closeReason);
		}
	}


	/**@author HCJ
	 * 连接出错时调用
	 * @date 2019年3月1日 上午11:41:09
	 */
	@OnError
	public void onError(Session session, Throwable error) {
		try {
			CloseReason closeReason = new CloseReason(CloseCodes.getCloseCode(ERROR_CLOSECODE), error.getMessage());
			session.close(closeReason);
		} catch (IOException e) {
			logger.error("关闭时sessionID为  " + session.getId() + " 的Display客户端出现错误：" + e.getMessage());
		}
		logger.error("sessionID为  " + session.getId() + " 的Display客户端与服务端连接出现错误，原因为：" + error.getMessage());
	}


	/**@author HCJ
	 * 发送板子数量重置信息到服务器
	 * @param boardResetInfo 板子数量重置信息
	 * @date 2019年3月1日 上午11:35:56
	 */
	public static synchronized void sendBoardResetInfo(BoardResetInfo boardResetInfo) {
		JSONObject jsonObject = (JSONObject) JSONObject.toJSON(boardResetInfo);
		try {
			session.getBasicRemote().sendText(jsonObject.toJSONString());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}


	public SendBoardResetInfoCloseCallBack getCloseCallBack() {
		return closeCallBack;
	}


	public void setCloseCallBack(SendBoardResetInfoCloseCallBack closeCallBack) {
		this.closeCallBack = closeCallBack;
	}
}
