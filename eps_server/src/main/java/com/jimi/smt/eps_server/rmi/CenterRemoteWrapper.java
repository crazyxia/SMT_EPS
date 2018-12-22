package com.jimi.smt.eps_server.rmi;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
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
import com.jimi.smt.eps_server.util.IpHelper;

import cc.darhao.dautils.api.BytesParser;
import cc.darhao.dautils.api.FieldUtil;
import com.jimi.smt.eps_server.pack.BasePackage;
import com.jimi.smt.eps_server.util.PackageParser;

/**与中控RMI服务端进行通讯
 * @author   HCJ
 * @date     2018年11月6日 下午5:00:28
 */
public class CenterRemoteWrapper {

	private static Logger logger = LogManager.getRootLogger();

	/**
	 * line : 某一条产线的ID
	 */
	private int line;

	/**
	 * remoteIp : 树莓派IP
	 */
	private String remoteIp;

	/**
	 * localIp : 本地IP
	 */
	private String localIp;

	/**
	 * alarming : 报警中
	 */
	private boolean alarming;

	/**
	 * converyPaused : 接驳台已暂停
	 */
	private boolean converyPaused;

	/**
	 * centerRemote : 中控RMI服务端对象
	 */
	private CenterRemote centerRemote;

	private SocketLogMapper socketLogMapper;
	private CenterStateMapper centerStateMapper;

	
	/**
	 * Title: 查找中控RMI服务端对象
	 * Description: 进行初始化操作
	 */
	public CenterRemoteWrapper(int line, CenterLoginMapper centerLoginMapper, SocketLogMapper socketLogMapper, CenterStateMapper centerStateMapper) throws Exception {
		this.line = line;
		this.socketLogMapper = socketLogMapper;
		this.centerStateMapper = centerStateMapper;
		localIp = IpHelper.getLinuxLocalIp();
		CenterLoginExample example = new CenterLoginExample();
		example.createCriteria().andLineEqualTo(this.line);
		List<CenterLogin> logins = centerLoginMapper.selectByExample(example);
		if (!logins.isEmpty()) {
			// 查找中控RMI服务端对象
			remoteIp = logins.get(0).getIp();
			System.out.println("ip=" + remoteIp);
			centerRemote = (CenterRemote) LocateRegistry.getRegistry(remoteIp).lookup("center");
			logger.info("搜索产线,ID为: " + line + " : " + "已找到中控设备：" + remoteIp);
			reset();
		} else {
			throw new Exception("没有找到对应线号的中控");
		}
	}

	
	/**
	 * 发送包到中控接驳台
	 * 
	 * @throws IOException
	 */
	public synchronized void sendCmdToConvery(ClientDevice clientDevice, boolean isAlarm) throws Exception {
		if (converyPaused ^ isAlarm) {
			// 开启or关闭接驳台
			ControlPackage controlPackage = new ControlPackage();
			controlPackage.setLine(line);
			controlPackage.setClientDevice(clientDevice);
			controlPackage.setControlledDevice(ControlledDevice.CONVEYOR);
			if (isAlarm) {
				controlPackage.setOperation(Operation.OFF);
			} else {
				controlPackage.setOperation(Operation.ON);
			}
			controlPackage.protocol = "Control";
			controlPackage.senderIp = localIp;
			controlPackage.receiverIp = remoteIp;
			controlPackage.serialNo = 0;
			ControlReplyPackage controlReplyPackage = centerRemote.receiveControl(controlPackage);
			// 创建日志：收到的包
			insertLogByPackage(controlPackage);
			insertLogByPackage(controlReplyPackage);
			// 判断是否操作成功
			if (!controlReplyPackage.getControlResult().equals(ControlResult.SUCCEED)) {
				throw new Exception("控制接驳台失败");
			}
			converyPaused = isAlarm;
			// 记录到state表
			CenterState state = centerStateMapper.selectByPrimaryKey(line);
			state.setConverypaused(converyPaused);
			centerStateMapper.updateByPrimaryKey(state);
		}
	}

	
	/**
	 * 发送包到中控报警器
	 * 
	 * @throws IOException
	 */
	public synchronized void sendCmdToAlarm(ClientDevice clientDevice, boolean isAlarm) throws Exception {
		if (alarming ^ isAlarm) {
			// 打开or关闭报警器
			ControlPackage controlPackage = new ControlPackage();
			controlPackage.setLine(line);
			controlPackage.setClientDevice(clientDevice);
			controlPackage.setControlledDevice(ControlledDevice.ALARM);
			if (isAlarm) {
				controlPackage.setOperation(Operation.ON);
			} else {
				controlPackage.setOperation(Operation.OFF);
			}
			controlPackage.protocol = "Control";
			controlPackage.senderIp = localIp;
			controlPackage.receiverIp = remoteIp;
			controlPackage.serialNo = 0;
			ControlReplyPackage controlReplyPackage = centerRemote.receiveControl(controlPackage);
			// 创建日志：收到的包
			insertLogByPackage(controlPackage);
			insertLogByPackage(controlReplyPackage);
			// 判断是否操作成功
			if (!controlReplyPackage.getControlResult().equals(ControlResult.SUCCEED)) {
				throw new Exception("控制报警器失败");
			}
			alarming = isAlarm;
			// 记录到state表
			CenterState state = centerStateMapper.selectByPrimaryKey(line);
			state.setAlarming(alarming);
			centerStateMapper.updateByPrimaryKey(state);
		}
	}

	
	private void reset() throws Exception {
		// 强制重置
		alarming = true;
		converyPaused = true;
		sendCmdToAlarm(ClientDevice.SERVER, false);
		sendCmdToConvery(ClientDevice.SERVER, false);
		logger.info("ID为 " + line + "的产线报警器、接驳台已重置");
	}

	
	/**
	 * 根据包插入日志实体
	 * 
	 * @param p
	 * @return
	 */
	private void insertLogByPackage(BasePackage p) {
		SocketLog log = new SocketLog();
		FieldUtil.copy(p, log);
		log.setTime(new Date());
		String data = BytesParser.parseBytesToHexString(PackageParser.serialize(p));
		log.setData(data);
		socketLogMapper.insert(log);
	}
}
