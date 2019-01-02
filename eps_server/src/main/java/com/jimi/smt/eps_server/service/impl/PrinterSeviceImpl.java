package com.jimi.smt.eps_server.service.impl;


import java.io.IOException;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jimi.smt.eps_server.service.PrinterService;
import com.jimi.smt.eps_server.socket.PrintServerSocket;
import com.jimi.smt.eps_server.util.ResultUtil2;

@Service
public class PrinterSeviceImpl implements PrinterService{
	
	// 等待反馈的时间
	private static final int TIME_OUT = 60000;
	
	
	@Override
	public ResultUtil2 printBarcode(String printerIP, String materialId, String materialNo, Integer remainingQuantity, String productDate, String user, String supplier) throws InterruptedException, IOException{
		// 判断打印机IP是否存在
		if(PrintServerSocket.getClients().containsKey(printerIP)) {
			long startTime = System.currentTimeMillis();
			Long id = PrintServerSocket.getResults().size() + 1 + startTime;
			// 发送打印信息
			try {
				PrintServerSocket.send(printerIP, id.toString(), materialId, materialNo, remainingQuantity.toString(), productDate, user, supplier);
			} catch (Exception e) {
				PrintServerSocket.getResults().remove(id.toString());
				PrintServerSocket.getClients().get(printerIP).close();
				PrintServerSocket.getClients().remove(printerIP);
				return new ResultUtil2(500,"发送打印信息失败,请检查打印机连接");
			}
			// 监听客户端返回的信息
			while(System.currentTimeMillis() - startTime < TIME_OUT){
				Thread.sleep(500);
				if(PrintServerSocket.getResults().get(id.toString()) != null) {
					String returnInfo = PrintServerSocket.getResults().get(id.toString());
					JSONObject jsonObject = JSON.parseObject(returnInfo);
					PrintServerSocket.getResults().remove(id.toString());
					return new ResultUtil2(jsonObject.getIntValue("result"),jsonObject.getString("data"));
				}else if(!PrintServerSocket.getClients().containsKey(printerIP)) {
					PrintServerSocket.getResults().remove(id.toString());
					return new ResultUtil2(500,"连接失败,请检查打印机连接");
				}
			}
			PrintServerSocket.getResults().remove(id.toString());
			PrintServerSocket.getClients().get(printerIP).close();
			PrintServerSocket.getClients().remove(printerIP);
			return new ResultUtil2(500,"打印信息发送超时,请检查打印机连接");
		}else {
			return new ResultUtil2(500,"打印失败,请检查打印机连接");
		}
	}
}

