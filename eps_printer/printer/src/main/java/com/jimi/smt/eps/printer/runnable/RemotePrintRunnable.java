package com.jimi.smt.eps.printer.runnable;

import java.util.concurrent.CountDownLatch;

import com.jimi.smt.eps.printer.callback.RemotePrintCallBack;
import com.jimi.smt.eps.printer.entity.WebSocketResult;

/**
 * 远程打印Runnable，将打印结果赋值给参数isPrintSucceed，在打印完成后自动执行countDown。
 * @author Administrator
 *
 */
public class RemotePrintRunnable implements Runnable{

	private WebSocketResult result;
	private String id;
	private CountDownLatch countDownLatch;
	private RemotePrintCallBack remotePrintCallBack;
	public RemotePrintRunnable(WebSocketResult result, String id, CountDownLatch countDownLatch, RemotePrintCallBack remotePrintCallBack) {
		this.result = result;
		this.countDownLatch = countDownLatch;
		this.remotePrintCallBack = remotePrintCallBack;
		this.id = id;
	}
	@Override
	public void run() {
		if (remotePrintCallBack != null) {
			boolean flag = remotePrintCallBack.remotePrintCallBack();
			if (flag) {
				result.succeed(id, "成功下达打印指令");
			} else {
				result.fail(id, "打印失败");
			}
		}
		countDownLatch.countDown();
	}
	
	
}
