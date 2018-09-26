package com.jimi.smt.eps_server.socket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jimi.smt.eps_server.entity.Display;
import com.jimi.smt.eps_server.entity.DisplayExample;
import com.jimi.smt.eps_server.entity.Line;
import com.jimi.smt.eps_server.entity.SocketLog;
import com.jimi.smt.eps_server.entity.CenterLogin;
import com.jimi.smt.eps_server.entity.CenterLoginExample;
import com.jimi.smt.eps_server.entity.Program;
import com.jimi.smt.eps_server.entity.ProgramExample;
import com.jimi.smt.eps_server.entity.ProgramExample.Criteria;
import com.jimi.smt.eps_server.mapper.ConfigMapper;
import com.jimi.smt.eps_server.mapper.DisplayMapper;
import com.jimi.smt.eps_server.mapper.LineMapper;
import com.jimi.smt.eps_server.mapper.SocketLogMapper;
import com.jimi.smt.eps_server.mapper.CenterLoginMapper;
import com.jimi.smt.eps_server.mapper.ProgramItemVisitMapper;
import com.jimi.smt.eps_server.mapper.ProgramMapper;
import com.jimi.smt.eps_server.mapper.CenterStateMapper;
import com.jimi.smt.eps_server.pack.BoardNumPackage;
import com.jimi.smt.eps_server.pack.LoginPackage;
import com.jimi.smt.eps_server.pack.LoginReplyPackage;
import com.jimi.smt.eps_server.timer.CheckErrorTimer;

import cc.darhao.dautils.api.BytesParser;
import cc.darhao.dautils.api.FieldUtil;
import cc.darhao.jiminal.callback.OnPackageArrivedListener;
import cc.darhao.jiminal.core.BasePackage;
import cc.darhao.jiminal.core.PackageParser;
import cc.darhao.jiminal.core.SyncCommunicator;

/**服务端SOCKET
 * @package  com.jimi.smt.eps_server.socket
 * @file     ServerSocket.java
 * @author   HCJ
 * @date     2018年9月26日 上午8:50:17
 * @version  V 1.0
 */
@Component
public class ServerSocket {

	private static Logger logger = LogManager.getRootLogger();
	
	/**
	 * communicator : 同步通讯器
	 */
	private SyncCommunicator communicator;

	/**
	 * LOCAL_PORT : 服务端监听端口
	 */
	private static final int LOCAL_PORT = 23333;

	/**
	 * PACKAGE_PATH : package所在位置
	 */
	private static final String PACKAGE_PATH = "com.jimi.smt.eps_server.pack";

	/**
	 * structure : 物料是几联板
	 */
	private int structure;

	/**
	 * alreadyProductOld : 当前工单已生产数量
	 */
	private int alreadyProductOld;

	@Autowired
	private ConfigMapper configMapper;
	@Autowired
	private ProgramMapper programMapper;
	@Autowired
	private ProgramItemVisitMapper programItemVisitMapper;
	@Autowired
	private CenterLoginMapper centerLoginMapper;
	@Autowired
	private SocketLogMapper socketLogMapper;
	@Autowired
	private CenterStateMapper centerStateMapper;
	@Autowired
	private DisplayMapper displayMapper;
	@Autowired
	private LineMapper lineMapper;

	/**
	 * 作为标志，用来进行判断
	 */
	private int flag = 0;	
	private int state = 0;

	/**
	 * clientSockets : 所有线别的报警模块列表
	 */
	private Map<Integer, ClientSocket> clientSockets;

	/**
	 * lineMap : 所有线别列表
	 */
	private Map<Integer, Line> lineMap = new HashMap<>();

	/**
	 * lineSize : 产线数量
	 */
	private long lineSize;
	
	/**
	 * checkErrorTimer : 错误检测定时器
	 */
	private CheckErrorTimer checkErrorTimer;

	
	/**@author HCJ
	 * 初始化
	 * @method init
	 * @return void
	 * @date 2018年9月26日 上午8:50:43
	 */
	@PostConstruct
	public void init() {
		clientSockets = new HashMap<Integer, ClientSocket>();
		List<Line> lines = lineMapper.selectByExample(null);
		lineSize = lines.size();
		for (int i = 0; i < lineSize; i++) {
			Line line = lines.get(i);
			lineMap.put(i, line);
		}
		communicator = new SyncCommunicator(LOCAL_PORT, PACKAGE_PATH);
		new Thread(() -> {
			open();
		}).start();
	}

	public ServerSocket() {
	}

	
	/**@author HCJ
	 * 打开端口，服务器开始监听包的到来
	 * @method open
	 * @return void
	 * @date 2018年9月26日 上午8:51:43
	 */
	public void open() {
		logger.info("SMT 服务器 正在运行!");
		communicator.startServer(new OnPackageArrivedListener() {
			@Override
			public void onPackageArrived(BasePackage p, BasePackage r) {
				try {
					// 处理登录包逻辑
					if (p instanceof LoginPackage && r instanceof LoginReplyPackage) {
						LoginPackage loginPackage = (LoginPackage) p;
						LoginReplyPackage loginReplyPackage = (LoginReplyPackage) r;
						flag = flag + 1;
						// 根据mac地址获取线别
						String mac = loginPackage.getCenterControllerMAC();
						CenterLoginExample example = new CenterLoginExample();
						example.createCriteria().andMacEqualTo(mac);
						List<CenterLogin> logins = centerLoginMapper.selectByExample(example);
						if (!logins.isEmpty()) {
							CenterLogin login = logins.get(0);
							loginReplyPackage.setLine(login.getLine());
							loginReplyPackage.setTimestamp(new Date());
						} else {
							loginReplyPackage.setLine(0);
							loginReplyPackage.setTimestamp(new Date());
						}
						// 处理板子数量上传包逻辑
					} else if (p instanceof BoardNumPackage) {
						BoardNumPackage boardNumPackage = (BoardNumPackage) p;
						DisplayExample displayExample = new DisplayExample();
						displayExample.createCriteria().andLineEqualTo(boardNumPackage.getLine());
						List<Display> displays = displayMapper.selectByExample(displayExample);
						if (!displays.isEmpty()) {
							Display display = displays.get(0);
							if (display.getWorkOrder() != null && display.getBoardType() != null) {
								ProgramExample programExample = new ProgramExample();
								Criteria criteria = programExample.createCriteria();
								criteria.andWorkOrderEqualTo(display.getWorkOrder());
								criteria.andBoardTypeEqualTo(display.getBoardType());
								criteria.andLineEqualTo(display.getLine());
								criteria.andStateEqualTo(1);
								List<Program> programs = programMapper.selectByExample(programExample);
								if (!programs.isEmpty()) {
									Program programFirst = programs.get(0);
									structure = programFirst.getStructure();
									alreadyProductOld = programFirst.getAlreadyProduct();
									Program program = new Program();
									program.setAlreadyProduct((boardNumPackage.getBoardNum()) * structure + alreadyProductOld);
									programMapper.updateByExampleSelective(program, programExample);
								}
							}
						}
					}
					// 创建日志：收到的包
					SocketLog pLog = createLogByPackage(p);
					socketLogMapper.insert(pLog);
					// 创建日志：回复的包
					SocketLog rLog = createLogByPackage(r);
					socketLogMapper.insert(rLog);
					// 记录包协议
					logger.info(p.protocol + "package arrived");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onCatchIOException(IOException e) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				PrintStream printStream = new PrintStream(bos);
				e.printStackTrace(printStream);
				logger.error(new String(bos.toByteArray()));
			}
		});
	}

	
	/**@author HCJ
	 * 初始化连接中控socket
	 * @method initClients
	 * @return void
	 * @date 2018年9月26日 上午8:52:21
	 */
	@Scheduled(cron = "0/3 * * * * ?")
	public void initClients() {
		if (flag >= lineSize) {
			for (int i = 0; i < lineSize; i++) {
				try {
					clientSockets.put(i, new ClientSocket(lineMap.get(i).getId(), centerLoginMapper, socketLogMapper, centerStateMapper));
				} catch (Exception e) {
					logger.error("搜索产线 " + lineMap.get(i).getLine() + " : " + e.getMessage());
				}
			}
			flag = 0;
			checkErrorTimer = new CheckErrorTimer(lineSize, lineMap, configMapper, programMapper, programItemVisitMapper, clientSockets);
			state = 1;
		}
	}

	
	/**@author HCJ
	 * 每隔3秒检查是否有错误项目
	 * @method checkError
	 * @return void
	 * @date 2018年9月26日 上午8:52:33
	 */
	@Scheduled(cron = "0/3 * * * * ?")
	public void checkError() {
		if (flag == 0 && state == 1) {
			logger.info("SMT 系统监听错误中。。。");
			checkErrorTimer.start();
		}
	}
	

	/**
	 * 根据包创建日志实体
	 * 
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
