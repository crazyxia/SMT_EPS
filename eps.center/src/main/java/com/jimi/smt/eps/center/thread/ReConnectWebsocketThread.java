package com.jimi.smt.eps.center.thread;

import com.jimi.smt.eps.center.main.Main;


/**中控重连websocket服务端线程
 * @author   HCJ
 * @date     2019年3月8日 下午2:35:42
 */
public class ReConnectWebsocketThread extends Thread {

	/**
	 * HEART_CYCLE : 发送心跳信息间隔时间
	 */
	private static final Integer HEART_CYCLE = 15000;


	public void run() {
		while (true) {
			try {
				Thread.sleep(HEART_CYCLE);
				Main.getSession().getBasicRemote().sendText("Heart");
			} catch (Exception e) {
				e.printStackTrace();
				Main.reConnect();
			}
		}
	}
}
