package com.jimi.smt.eps.printer.websocket;

import java.io.IOException;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jimi.smt.eps.printer.callback.PrintTaskReceiveCallBack;
import com.jimi.smt.eps.printer.callback.PrinteTaskCloseCallBack;
import com.jimi.smt.eps.printer.entity.PrintTaskInfo;

/**
 * 远程打印webSockt客户端
 * 
 * @author Administrator
 *
 */
@ClientEndpoint
public class RemotePrintTaskReceiver {

	private Session session;
	private static Logger logger = LogManager.getRootLogger();
	private PrintTaskReceiveCallBack receiveCallBack;
	private PrinteTaskCloseCallBack closeCallBack;

	private static final Integer ERROR_CLOSECODE = 8888;

	
	@OnOpen
	public void onOpen(Session session) throws IOException {
		this.session = session;
	}

	
	@OnMessage
	public void onMessage(String message) {
		System.out.println("接收自服务端消息:" + message);
		PrintTaskInfo info = PrintTaskInfo.jsonStringToPrnterInfo(message);
		if (receiveCallBack != null) {
			receiveCallBack.printTaskReceiveCallBack(session, info);
		}
	}

	
	@OnError
	public void onError(Session session, Throwable e) {
		try {
			CloseReason closeReason = new CloseReason(CloseCodes.getCloseCode(ERROR_CLOSECODE), e.getMessage());
			session.close(closeReason);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		logger.error(e.getMessage());
		e.printStackTrace();
	}

	
	@OnClose
	public void onClose(Session session, CloseReason reason) {
		logger.error("Client was disconnected. | " + reason.toString());
		if (closeCallBack != null) {
			closeCallBack.printTaskCloseCallBack(reason);
		}
	}

	
	public PrintTaskReceiveCallBack getReceiveCallBack() {
		return receiveCallBack;
	}

	
	public void setReceiveCallBack(PrintTaskReceiveCallBack receiveCallBack) {
		this.receiveCallBack = receiveCallBack;
	}

	
	public PrinteTaskCloseCallBack getCloseCallBack() {
		return closeCallBack;
	}

	
	public void setCloseCallBack(PrinteTaskCloseCallBack closeCallBack) {
		this.closeCallBack = closeCallBack;
	}
}
