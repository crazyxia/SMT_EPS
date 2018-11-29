package com.jimi.smt.eps_server.rmi;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.rmi.registry.LocateRegistry;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jimi.smt.eps_server.constant.ControlResult;
import com.jimi.smt.eps_server.constant.ControlledDevice;
import com.jimi.smt.eps_server.constant.ErrorCode;
import com.jimi.smt.eps_server.constant.Operation;
import com.jimi.smt.eps_server.pack.BoardNumPackage;
import com.jimi.smt.eps.center.util.IniReader;
import com.jimi.smt.eps.center.util.IpHelper;
import com.jimi.smt.eps_server.pack.BoardResetPackage;
import com.jimi.smt.eps_server.pack.BoardResetReplyPackage;
import com.jimi.smt.eps_server.pack.ControlPackage;
import com.jimi.smt.eps_server.pack.ControlReplyPackage;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import cc.darhao.dautils.api.TextFileUtil;

/**中控远程方法实现类：接收控制包、接收板子数量重置包
 * @author   HCJ
 * @date     2018年11月29日 上午11:49:03
 */
public class CenterRemoteImpl implements CenterRemote {

	private static Logger logger = LogManager.getRootLogger();

	/**
	 * 初始化GPIO口数据
	 */
	private final GpioController gpio = GpioFactory.getInstance();
	GpioPinDigitalOutput io13 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, PinState.LOW);
	GpioPinDigitalOutput io15 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, PinState.LOW);
	GpioPinDigitalOutput io16 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, PinState.LOW);
	GpioPinDigitalOutput io18 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, PinState.LOW);

	/**
	 * STATE_FILE : 记录报警灯、接驳台等硬件状态文件
	 */
	private static final String STATE_FILE = "/state.txt";

	/**
	 * BOARDNUM_FILE : 记录板子数量的文件
	 */
	private static final String BOARDNUM_FILE = "/board_num.txt";

	/**
	 * CONFIG_FILE : 记录各项配置的文件
	 */
	private static final String CONFIG_FILE = "/config.ini";

	/**
	 * server : RMI服务端对象
	 */
	private ServerRemote server;

	/**
	 * serverIp : 服务器的IP
	 */
	private String serverIp;

	
	public CenterRemoteImpl() {
		Map<String, String> ipMap = IniReader.getItem(System.getProperty("user.dir") + CONFIG_FILE, "ip");
		serverIp = ipMap.get("serverIp");
		try {
			server = (ServerRemote) LocateRegistry.getRegistry(serverIp).lookup("server");
		} catch (Exception e) {
			new CenterRemoteImpl();
			logger.error("查找RMI服务端失败:" + e.getMessage());
		}
	}

	
	/** <p>Title: receiveControl</p>
	 * <p>Description: 接收控制包,根据控制包信息进行相应操作</p>
	 */
	@Override
	public ControlReplyPackage receiveControl(ControlPackage controlPackage) {
		ControlReplyPackage controlReplyPackage = new ControlReplyPackage();
		try {
			ControlledDevice controlledDevice = controlPackage.getControlledDevice();
			controlReplyPackage.setClientDevice(controlPackage.getClientDevice());
			if (controlledDevice == ControlledDevice.CENTER_CONTROLLER) {
				if (controlPackage.getOperation() == Operation.OFF) {
					// 系统关机
					Runtime runtime = Runtime.getRuntime();
					Process process = runtime.exec("halt -p");
					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String line;
					while ((line = reader.readLine()) != null) {
						System.out.println(line);
					}
					reader.close();
					controlReplyPackage.setControlResult(ControlResult.SUCCEED);
					controlReplyPackage.setErrorCode(ErrorCode.SUCCEED);
				} else if (controlPackage.getOperation() == Operation.RESET) {
					// 系统重启
					Runtime runtime = Runtime.getRuntime();
					Process process = runtime.exec("init 6");
					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String line;
					while ((line = reader.readLine()) != null) {
						System.out.println(line);
					}
					reader.close();
					controlReplyPackage.setControlResult(ControlResult.SUCCEED);
					controlReplyPackage.setErrorCode(ErrorCode.SUCCEED);
				}
			} else if (controlledDevice == ControlledDevice.CONVEYOR) {
				if (controlPackage.getOperation() == Operation.OFF) {
					// 接驳台关闭
					logger.info("接驳台关闭");
					io13.high();
					Thread.sleep(200);
					io13.low();
					io18.high();
					if (io13.isLow() && io18.isHigh()) {
						controlReplyPackage.setControlResult(ControlResult.SUCCEED);
						controlReplyPackage.setErrorCode(ErrorCode.SUCCEED);
						TextFileUtil.writeToFile(System.getProperty("user.dir") + STATE_FILE, "00000001");
					} else {
						controlReplyPackage.setControlResult(ControlResult.FAILED);
						controlReplyPackage.setErrorCode(ErrorCode.RELAY_FAILURE);
					}
				} else if (controlPackage.getOperation() == Operation.ON) {
					// 接驳台打开
					logger.info("接驳台打开");
					io16.high();
					Thread.sleep(200);
					io16.low();
					io18.low();
					if (io16.isLow() && io18.isLow()) {
						controlReplyPackage.setControlResult(ControlResult.SUCCEED);
						controlReplyPackage.setErrorCode(ErrorCode.SUCCEED);
						TextFileUtil.writeToFile(System.getProperty("user.dir") + STATE_FILE, "00000011");
					} else {
						controlReplyPackage.setControlResult(ControlResult.FAILED);
						controlReplyPackage.setErrorCode(ErrorCode.RELAY_FAILURE);
					}
				}
			} else if (controlledDevice == ControlledDevice.ALARM) {
				if (controlPackage.getOperation() == Operation.OFF) {
					// 报警灯关闭
					logger.info("报警灯关闭");
					io15.low();
					if (io15.isLow()) {
						controlReplyPackage.setControlResult(ControlResult.SUCCEED);
						controlReplyPackage.setErrorCode(ErrorCode.SUCCEED);
						TextFileUtil.writeToFile(System.getProperty("user.dir") + STATE_FILE, "00000011");
					} else {
						controlReplyPackage.setControlResult(ControlResult.FAILED);
						controlReplyPackage.setErrorCode(ErrorCode.RELAY_FAILURE);
					}
				} else if (controlPackage.getOperation() == Operation.ON) {
					// 报警灯打开
					logger.info("报警灯打开");
					io15.high();
					if (io15.isHigh()) {
						controlReplyPackage.setControlResult(ControlResult.SUCCEED);
						controlReplyPackage.setErrorCode(ErrorCode.SUCCEED);
						TextFileUtil.writeToFile(System.getProperty("user.dir") + STATE_FILE, "00000111");
					} else {
						controlReplyPackage.setControlResult(ControlResult.FAILED);
						controlReplyPackage.setErrorCode(ErrorCode.RELAY_FAILURE);
					}
				}
			}
			// 记录包协议
			logger.info("控制包到达");
		} catch (Exception e) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			PrintStream printStream = new PrintStream(bos);
			e.printStackTrace(printStream);
			logger.error(new String(bos.toByteArray()));
		}
		controlReplyPackage.serialNo = 1;
		controlReplyPackage.protocol = controlPackage.protocol;
		controlReplyPackage.senderIp = controlPackage.receiverIp;
		controlReplyPackage.receiverIp = controlPackage.senderIp;
		return controlReplyPackage;
	}
	
	
	/** <p>Title: resetBoardNum</p>
	 * <p>Description: 接收板子数量重置包，重置板子数</p>
	 */
	@Override
	public BoardResetReplyPackage resetBoardNum(BoardResetPackage boardResetPackage) {
		BoardResetReplyPackage boardResetReplyPackage = new BoardResetReplyPackage();
		try {
			// 处理板子数量重置包逻辑
			BoardNumPackage boardNumPackage = new BoardNumPackage();
			String boardNum = TextFileUtil.readFromFile(System.getProperty("user.dir") + BOARDNUM_FILE);
			if (boardNum == null || boardNum.equals("")) {
				boardNumPackage.setBoardNum(Integer.parseInt("0"));
			} else {
				boardNumPackage.setBoardNum(Integer.parseInt(TextFileUtil.readFromFile(System.getProperty("user.dir") + BOARDNUM_FILE)));
			}
			boardNumPackage.setTimestamp(new Date());
			boardNumPackage.setLine(boardResetPackage.getLine());
			boardNumPackage.protocol = "BoardNum";
			boardNumPackage.serialNo = 0;
			boardNumPackage.senderIp = IpHelper.getLinuxLocalIp();
			boardNumPackage.receiverIp = serverIp;
			server.updateBoardNum(boardNumPackage);
			logger.info("在时间:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(boardNumPackage.getTimestamp()) + " ID为：" + boardResetPackage.getLine() + " 的产线display客户端发送上传板子数量包");
			TextFileUtil.writeToFile(System.getProperty("user.dir") + BOARDNUM_FILE, "0");
			boardResetReplyPackage.setClientDevice(boardResetPackage.getClientDevice());
			if ("0".equals(TextFileUtil.readFromFile(System.getProperty("user.dir") + BOARDNUM_FILE))) {
				boardResetReplyPackage.setControlResult(ControlResult.SUCCEED);
				boardResetReplyPackage.setErrorCode(ErrorCode.SUCCEED);
			} else {
				boardResetReplyPackage.setControlResult(ControlResult.FAILED);
				boardResetReplyPackage.setErrorCode(ErrorCode.RELAY_FAILURE);
			}
			// 记录包协议
			logger.info("板子数量重置包到达");
		} catch (Exception e) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			PrintStream printStream = new PrintStream(bos);
			e.printStackTrace(printStream);
			logger.error(new String(bos.toByteArray()));
		}
		boardResetReplyPackage.serialNo = 1;
		boardResetReplyPackage.protocol = boardResetPackage.protocol;
		boardResetReplyPackage.senderIp = boardResetPackage.receiverIp;
		boardResetReplyPackage.receiverIp = boardResetPackage.senderIp;
		return boardResetReplyPackage;
	}

}
