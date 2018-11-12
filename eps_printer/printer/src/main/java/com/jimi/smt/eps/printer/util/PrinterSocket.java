package com.jimi.smt.eps.printer.util;

import java.io.IOException;
import java.net.Inet4Address;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jimi.smt.eps.printer.callback.PrinterCallBack;
import com.jimi.smt.eps.printer.entity.PrinterInfo;

@ClientEndpoint()
public class PrinterSocket {

	private Session session;
	private static Logger logger = LogManager.getRootLogger();
	private PrinterCallBack callBack;
	
	
	@OnOpen
	public void onOpen(Session session) throws IOException {
		this.session = session;
	}
	
	@OnMessage
	public void onMessage(String message) {
		System.out.println("接收自服务端消息:" + message);
		PrinterInfo info = PrinterInfo.jsonStringToPrnterInfo(message);
		if (callBack != null) {
			callBack.printCallBack(session, info);
		}
	}
	
	@OnError
	public void onError(Session session, Throwable e) {
		try {
			session.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		e.printStackTrace();
	}
	
	@OnClose
	public void onClose(Session session, CloseReason reason) {
		logger.error("Client was disconnected. | " + reason.toString());
	}
	
	public PrinterCallBack getCallBack() {
		return callBack;
	}
	
	public void setCallBack(PrinterCallBack callBack) {
		this.callBack = callBack;
	}
	
}
