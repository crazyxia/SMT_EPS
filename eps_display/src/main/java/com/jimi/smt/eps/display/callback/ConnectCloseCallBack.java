package com.jimi.smt.eps.display.callback;

import javax.websocket.CloseReason;


/**websocket连接关闭时的回调方法
 * @author   HCJ
 * @date     2019年3月5日 下午4:46:27
 */
public interface ConnectCloseCallBack {

	public void connectCloseCallBack(CloseReason closeReason);
}
