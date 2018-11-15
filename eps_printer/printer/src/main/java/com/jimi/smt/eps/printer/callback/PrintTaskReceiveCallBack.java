
package com.jimi.smt.eps.printer.callback;

import javax.websocket.Session;

import com.jimi.smt.eps.printer.entity.PrintTaskInfo;

/**
 * 打印请求回调接口
 * 
 * @author Administrator
 *
 */
public interface PrintTaskReceiveCallBack {

	/**
	 * 接收到打印请求时执行方法
	 * 
	 * @param session
	 * @param info
	 */
	public void printTaskReceiveCallBack(Session session, PrintTaskInfo info);
}
