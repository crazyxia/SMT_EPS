
package com.jimi.smt.eps.printer.callback;

import javax.websocket.CloseReason;

/**
 * 远程打印任务出错接口
 * 
 * @author Administrator
 *
 */
public interface PrinteTaskCloseCallBack {

	public void printTaskCloseCallBack(CloseReason closeReason);
}
