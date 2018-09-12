package com.jimi.smt.eps_server.socket;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jimi.smt.eps_server.constant.ClientDevice;
import com.jimi.smt.eps_server.constant.ControlResult;
import com.jimi.smt.eps_server.constant.ControlledDevice;
import com.jimi.smt.eps_server.constant.Operation;
import com.jimi.smt.eps_server.entity.SocketLog;
import com.jimi.smt.eps_server.entity.CenterLogin;
import com.jimi.smt.eps_server.entity.CenterLoginExample;
import com.jimi.smt.eps_server.entity.CenterState;
import com.jimi.smt.eps_server.mapper.SocketLogMapper;
import com.jimi.smt.eps_server.mapper.CenterLoginMapper;
import com.jimi.smt.eps_server.mapper.CenterStateMapper;
import com.jimi.smt.eps_server.pack.ControlPackage;
import com.jimi.smt.eps_server.pack.ControlReplyPackage;

import cc.darhao.dautils.api.BytesParser;
import cc.darhao.dautils.api.FieldUtil;
import cc.darhao.jiminal.core.BasePackage;
import cc.darhao.jiminal.core.PackageParser;
import cc.darhao.jiminal.core.SyncCommunicator;

/**
 * 	连接产线中控的SOCKET
 */
public class ClientSocket {

	private static Logger logger = LogManager.getRootLogger();
	
	/**
	 * 同步通讯器
	 */
	private SyncCommunicator communicator;

	/**
	 * 连接超时时间
	 */
	private static final int TIME_OUT = 100000;

	/**
	 * package所在位置
	 */
	private static final String PACKAGE_PATH = "com.jimi.smt.eps_server.pack";

	/**
	 * 某一条产线
	 */
	private String line;
	
	/**
	 * 树莓派IP
	 */
	private String ip;
	
	/**
	 * 树莓派端口
	 */
	private int port;

	/**
	 * 报警中
	 */
	private boolean alarming;

	/**
	 * 接驳台已暂停
	 */
	private boolean converyPaused;

	private SocketLogMapper socketLogMapper;
	private CenterStateMapper centerStateMapper;

	/**
	 * 连接中控
	 * @throws Exception
	 */
	public ClientSocket(String line, CenterLoginMapper centerLoginMapper, SocketLogMapper socketLogMapper,
			CenterStateMapper centerStateMapper) throws Exception {
		this.line = line;
		this.socketLogMapper = socketLogMapper;
		this.centerStateMapper = centerStateMapper;
		CenterLoginExample example = new CenterLoginExample();
		example.createCriteria().andLineEqualTo(this.line);
		List<CenterLogin> logins = centerLoginMapper.selectByExample(example);
		if (!logins.isEmpty()) {
			// 尝试连接中控
			ip = logins.get(0).getIp();
			port = logins.get(0).getPort();
			System.out.println("ip=" + ip + ", port=" + port);
			communicator = new SyncCommunicator(ip, port, PACKAGE_PATH);
			communicator.setTimeout(TIME_OUT);
			communicator.connect();
			logger.info("搜索产线 " + line + " : " + "已找到中控设备：" + ip);
			reset();
		} else {
			throw new Exception("没有找到对应线号的中控");
		}
	}

	/**
	 * 发送包到中控接驳台
	 * @throws IOException
	 */
	public synchronized void sendCmdToConvery(ClientDevice clientDevice, boolean isAlarm) throws Exception {
		if(converyPaused ^ isAlarm) {
			//开启or关闭接驳台
			ControlPackage controlPackage = new ControlPackage();
			controlPackage.setLine(line);
			controlPackage.setClientDevice(clientDevice);
			controlPackage.setControlledDevice(ControlledDevice.CONVEYOR);
			if(isAlarm) {
				controlPackage.setOperation(Operation.OFF);
			}else {
				controlPackage.setOperation(Operation.ON);
			}
			ControlReplyPackage replyPackage = (ControlReplyPackage) communicator.send(controlPackage);			
			//创建日志：收到的包
			SocketLog pLog = createLogByPackage(controlPackage);
			socketLogMapper.insert(pLog);
			//创建日志：回复的包
			SocketLog rLog = createLogByPackage(replyPackage);
			socketLogMapper.insert(rLog);
			//判断是否操作成功
			if(! replyPackage.getControlResult().equals(ControlResult.SUCCEED)) {
				throw new Exception("控制接驳台失败");
			}
			converyPaused = isAlarm;
			//记录到state表
			CenterState state = centerStateMapper.selectByPrimaryKey(line);
			state.setConverypaused(converyPaused);
			centerStateMapper.updateByPrimaryKey(state);
		}
	}
	
	
	/**
	 * 发送包到中控报警器
	 * @throws IOException 
	 */
	public synchronized void sendCmdToAlarm(ClientDevice clientDevice, boolean isAlarm) throws Exception {
		if(alarming ^ isAlarm) {
			//打开or关闭报警器
			ControlPackage controlPackage = new ControlPackage();
			controlPackage.setLine(line);
			controlPackage.setClientDevice(clientDevice);
			controlPackage.setControlledDevice(ControlledDevice.ALARM);
			if(isAlarm) {
				controlPackage.setOperation(Operation.ON);
			}else {
				controlPackage.setOperation(Operation.OFF);
			}
			ControlReplyPackage replyPackage = (ControlReplyPackage) communicator.send(controlPackage);
			//创建日志：收到的包
			SocketLog pLog = createLogByPackage(controlPackage);
			socketLogMapper.insert(pLog);
			//创建日志：回复的包
			SocketLog rLog = createLogByPackage(replyPackage);
			socketLogMapper.insert(rLog);
			//判断是否操作成功
			if(! replyPackage.getControlResult().equals(ControlResult.SUCCEED)) {
				throw new Exception("控制报警器失败");
			}
			alarming = isAlarm;
			//记录到state表
			CenterState state = centerStateMapper.selectByPrimaryKey(line);
			state.setAlarming(alarming);
			centerStateMapper.updateByPrimaryKey(state);
		}
	}

	
	public void close() {
		communicator.close();
	}
	
	
	public void reconnect() throws Exception {
		close();
		communicator.connect();
		reset();
	}
	
	
	private void reset() throws Exception {
		//强制重置
		alarming = true;
		converyPaused = true;
		sendCmdToAlarm(ClientDevice.SERVER, false);
		sendCmdToConvery(ClientDevice.SERVER, false);
		logger.info("产线 "+ line +"报警器、接驳台已重置");
	}


	/**
	 * 根据包创建日志实体
	 * @param p
	 * @return
	 */
	private SocketLog createLogByPackage(BasePackage p) {
		SocketLog log = new SocketLog();
		FieldUtil.copy(p, log);
		log.setTime(new Date());
		String data = BytesParser.parseBytesToHexString(PackageParser.serialize(p));
		log.setData(data);
		return log;
	}
}
