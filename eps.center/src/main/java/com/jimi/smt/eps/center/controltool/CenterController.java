package com.jimi.smt.eps.center.controltool;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jimi.smt.eps.center.constant.ControlledDevice;
import com.jimi.smt.eps.center.constant.Operation;
import com.jimi.smt.eps.center.entity.CenterControlInfo;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import cc.darhao.dautils.api.TextFileUtil;


/**中控控制类
 * @author   HCJ
 * @date     2019年2月28日 下午2:51:16
 */
public class CenterController {

	private static Logger logger = LogManager.getRootLogger();

	/**
	 * 初始化GPIO口数据
	 */
	final static GpioController gpio = GpioFactory.getInstance();
	static GpioPinDigitalOutput io13 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, PinState.LOW);
	static GpioPinDigitalOutput io15 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, PinState.LOW);
	static GpioPinDigitalOutput io16 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, PinState.LOW);
	static GpioPinDigitalOutput io18 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, PinState.LOW);

	/**
	 * STATE_FILE : 记录报警灯、接驳台等硬件状态文件
	 */
	private static final String STATE_FILE = "/state.txt";


	/**
	 * @author HCJ 根据中控控制信息类设置中控状态
	 * @date 2019年2月28日 上午11:52:57
	 */
	public static void setCenterState(CenterControlInfo centerControlInfo) {
		try {
			ControlledDevice controlledDevice = centerControlInfo.getControlledDevice();
			if (controlledDevice == ControlledDevice.CENTER_CONTROLLER) {
				if (centerControlInfo.getOperation() == Operation.OFF) {
					// 系统关机
					Runtime runtime = Runtime.getRuntime();
					Process process = runtime.exec("halt -p");
					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String line;
					while ((line = reader.readLine()) != null) {
						System.out.println(line);
					}
					reader.close();
				} else if (centerControlInfo.getOperation() == Operation.RESET) {
					// 系统重启
					Runtime runtime = Runtime.getRuntime();
					Process process = runtime.exec("init 6");
					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String line;
					while ((line = reader.readLine()) != null) {
						System.out.println(line);
					}
					reader.close();
				}
			} else if (controlledDevice == ControlledDevice.CONVEYOR) {
				if (centerControlInfo.getOperation() == Operation.OFF) {
					// 接驳台关闭
					io13.high();
					Thread.sleep(200);
					io13.low();
					io18.high();
					if (io13.isLow() && io18.isHigh()) {
						TextFileUtil.writeToFile(System.getProperty("user.dir") + STATE_FILE, "00000001");
					}
				} else if (centerControlInfo.getOperation() == Operation.ON) {
					// 接驳台打开
					io16.high();
					Thread.sleep(200);
					io16.low();
					io18.low();
					if (io16.isLow() && io18.isLow()) {
						TextFileUtil.writeToFile(System.getProperty("user.dir") + STATE_FILE, "00000011");
					}
				}
			} else if (controlledDevice == ControlledDevice.ALARM) {
				if (centerControlInfo.getOperation() == Operation.OFF) {
					// 报警灯关闭
					io15.low();
					if (io15.isLow()) {
						TextFileUtil.writeToFile(System.getProperty("user.dir") + STATE_FILE, "00000011");
					}
				} else if (centerControlInfo.getOperation() == Operation.ON) {
					// 报警灯打开
					io15.high();
					if (io15.isHigh()) {
						TextFileUtil.writeToFile(System.getProperty("user.dir") + STATE_FILE, "00000111");
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}
