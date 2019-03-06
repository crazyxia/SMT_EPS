package com.jimi.smt.eps.center.thread;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import cc.darhao.dautils.api.TextFileUtil;


/**监听红外线和更新板子数量线程
 * @package  com.jimi.smt.eps.center.thread
 * @file     UpdateBoardNumThread.java
 * @author   HCJ
 * @date     2018年9月25日 下午5:12:27
 * @version  V 1.0
 */
public class UpdateBoardNumThread extends Thread {

    private static Logger logger = LogManager.getRootLogger();

    private int num = 0;

    /**
     * BOARDNUM_FILE : 板子数量文件
     */
    private static final String BOARDNUM_FILE = "/board_num.txt";

    /**
     * gpio : 初始化GPIO口数据
     */
    final GpioController gpio = GpioFactory.getInstance();

    
    @Override
	public void run() {
		// 提示已运行
		logger.info("SMT 中控  更新板子数量线程已开启!");
		GpioPinDigitalInput io29 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_21, PinPullResistance.PULL_UP);
		io29.setShutdownOptions(true);
		logger.info("开始对红外线进行监听并更新板子数量");
		io29.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				if (event.getState().isLow()) {
					try {
						String num_str = TextFileUtil.readFromFile(System.getProperty("user.dir") + BOARDNUM_FILE);
						if (num_str != null && !num_str.isEmpty()) {
							num = Integer.parseInt(num_str);
							num = num + 1;
							TextFileUtil.writeToFile(System.getProperty("user.dir") + BOARDNUM_FILE, num + "");
							//logger.info("板子数量+1,现在数量为：" + TextFileUtil.readFromFile(System.getProperty("user.dir") + BOARDNUM_FILE));
						}
					} catch (IOException e) {
						logger.error(e.getMessage());
					}
				}
			}
		});
		while (true) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
			}
		}
	}
}
