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

import com.jimi.smt.eps_server.constant.ClientDevice;
import com.jimi.smt.eps_server.constant.ControlledDevice;
import com.jimi.smt.eps_server.entity.Config;
import com.jimi.smt.eps_server.entity.Display;
import com.jimi.smt.eps_server.entity.DisplayExample;
import com.jimi.smt.eps_server.entity.Line;
import com.jimi.smt.eps_server.entity.SocketLog;
import com.jimi.smt.eps_server.entity.CenterLogin;
import com.jimi.smt.eps_server.entity.CenterLoginExample;
import com.jimi.smt.eps_server.entity.Program;
import com.jimi.smt.eps_server.entity.ProgramExample;
import com.jimi.smt.eps_server.entity.ProgramExample.Criteria;
import com.jimi.smt.eps_server.entity.ProgramItemVisit;
import com.jimi.smt.eps_server.entity.bo.CenterControllerErrorCounter;
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
import com.jimi.smt.eps_server.thread.SendCmdThread;

import cc.darhao.dautils.api.BytesParser;
import cc.darhao.dautils.api.FieldUtil;
import cc.darhao.jiminal.callback.OnPackageArrivedListener;
import cc.darhao.jiminal.core.BasePackage;
import cc.darhao.jiminal.core.PackageParser;
import cc.darhao.jiminal.core.SyncCommunicator;

/**
 * 	服务端SOCKET
 */
@Component
public class ServerSocket {

	private static Logger logger = LogManager.getRootLogger();

	/**
	 * 同步通讯器
	 */
	private SyncCommunicator communicator;

	/**
	 * 服务端监听端口
	 */
	private static final int LOCAL_PORT = 23333;

	/**
	 * package所在位置
	 */
	private static final String PACKAGE_PATH = "com.jimi.smt.eps_server.pack";

	/**
	 * 物料是几联板
	 */
	private int structure;

	/**
	 * 当前工单已生产数量
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
	 * Config配置项
	 */
	private static final String OPERATOR_ERROR_ALARM = "operator_error_alarm";

	/**
	 * Config配置项
	 */
	private static final String IPQC_ERROR_ALARM = "ipqc_error_alarm";

	/**
	 * “线别-报警设备错误统计”实体
	 */
	private Map<Integer, CenterControllerErrorCounter> lineErrorsCounters;

	/**
	 * 所有线别的报警模块列表
	 */
	public Map<Integer, ClientSocket> clientSockets;

	/**
	 * 所有线别列表
	 */
	public Map<Integer, Line> listMap = new HashMap<>();

	/**
	 * 产线数量
	 */
	private long lineSize;

	@PostConstruct
	public void init() {
		clientSockets = new HashMap<Integer, ClientSocket>();
		lineSize = lineMapper.countByExample(null);
		List<Line> lists = lineMapper.selectByExample(null);
		for (int i = 0; i < lineSize; i++) {
			Line line = lists.get(i);
			listMap.put(line.getId() - 1, line);
		}
		communicator = new SyncCommunicator(LOCAL_PORT, PACKAGE_PATH);
		new Thread(() -> {
			open();
		}).start();
	}

	public ServerSocket() {
	}

	/**
	 * 打开端口，服务器开始监听包的到来
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
							loginReplyPackage.setLine(login.getId().toString());
							//loginReplyPackage.setLine(login.getLine());
							loginReplyPackage.setTimestamp(new Date());
						} else {
							loginReplyPackage.setLine("00");
							loginReplyPackage.setTimestamp(new Date());
						}
						// 处理板子数量上传包逻辑
					} else if (p instanceof BoardNumPackage) {
						BoardNumPackage boardNumPackage = (BoardNumPackage) p;
						DisplayExample displayExample = new DisplayExample();
						//displayExample.createCriteria().andLineEqualTo(boardNumPackage.getLine());
						displayExample.createCriteria().andIdEqualTo(Integer.parseInt(boardNumPackage.getLine()));
						List<Display> displays = displayMapper.selectByExample(displayExample);
						if (!displays.isEmpty()) {
							Display display = displays.get(0);
							if (display.getWorkOrder() != null && display.getBoardType() != null) {
								ProgramExample programExample = new ProgramExample();
								Criteria criteria = programExample.createCriteria();
								criteria.andWorkOrderEqualTo(display.getWorkOrder());
								criteria.andBoardTypeEqualTo(display.getBoardType());
								//criteria.andLineEqualTo(boardNumPackage.getLine());
								criteria.andLineEqualTo(display.getLine());
								criteria.andStateEqualTo(1);
								List<Program> programs = programMapper.selectByExample(programExample);
								if (!programs.isEmpty()) {
									Program programFirst = programs.get(0);
									structure = programFirst.getStructure();
									alreadyProductOld = programFirst.getAlreadyProduct();
									Program program = new Program();
									program.setAlreadyProduct(
											(boardNumPackage.getBoardNum()) * structure + alreadyProductOld);
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

	/**
	 * 初始化连接中控socket
	 */
	@Scheduled(cron = "0/3 * * * * ?")
	public void initClients() {
		if (flag >= lineSize) {
			for (int i = 0; i < lineSize; i++) {
				try {
					clientSockets.put(i,
							new ClientSocket(listMap.get(i).getLine(), centerLoginMapper, socketLogMapper, centerStateMapper));
				} catch (Exception e) {
					logger.error("搜索产线 " + listMap.get(i).getLine() + " : " + e.getMessage());
				}
			}
			flag = 0;
			state = 1;
		}
	}

	/**
	 * 每隔3秒检查是否有错误项目
	 */
	@Scheduled(cron = "0/3 * * * * ?")
	public void checkError() {
		if (flag == 0 && state == 1) {
			logger.info("SMT 系统监听错误中。。。");
			try {
				// 初始化“线别-报警设备错误统计”实体
				initCounters();
				// 遍历visits进行错误扫描
				scanErrors();
				// 根据统计结果发送指令
				sendCmdByCounters();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 初始化“线别-报警设备错误统计”实体
	 */
	private void initCounters() throws IOException {
		lineErrorsCounters = new HashMap<Integer, CenterControllerErrorCounter>();
		for (int i = 0; i < lineSize; i++) {
			lineErrorsCounters.put(i, new CenterControllerErrorCounter());
		}
		// 设置报警模式
		setAlarmMode();
	}

	/**
	 * 根据Config表设置报警方式
	 * 
	 * @throws IOException
	 */
	private void setAlarmMode() throws IOException {
		List<Config> configs = configMapper.selectByExample(null);
		for (Config config : configs) {
			int lineNo = getLineNO(config.getLine());
			switch (config.getName()) {
			case IPQC_ERROR_ALARM:
				lineErrorsCounters.get(lineNo).setIpqcErrorAlarm(Integer.parseInt(config.getValue()));
				break;
			case OPERATOR_ERROR_ALARM:
				lineErrorsCounters.get(lineNo).setOperatorErrorAlarm(Integer.parseInt(config.getValue()));
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 根据线号得到id
	 */
	private int getLineNO(String line) {
		int number = 0;
		for (int i = 0; i < lineSize; i++) {
			if (listMap.get(i).getLine().equals(line)) {
				number = i;
				break;
			}
		}
		return number;
	}

	/**
	 * 扫描错误
	 */
	private void scanErrors() {
		try {
			long s = System.currentTimeMillis();
			// 查询所有State为1且被客户端选中的Program条目
			List<Program> programs = programMapper.selectByWorkOrderAndBoardType();
			// 查询所有visit
			List<ProgramItemVisit> programItemVisits = programItemVisitMapper.selectByExample(null);
			System.out.println("查询耗时：" + (System.currentTimeMillis() - s) + "ms");
			// 遍历Visit
			for (ProgramItemVisit programItemVisit : programItemVisits) {
				// 匹配线别
				for (Program program : programs) {
					if (program.getId().equals(programItemVisit.getProgramId())) {
						try {
							// 遍历字段
							int line = getLineNO(program.getLine());
							updateLineErrorCounter(line, 0, programItemVisit.getFeedResult());
							updateLineErrorCounter(line, 1, programItemVisit.getChangeResult());
							updateLineErrorCounter(line, 2, programItemVisit.getCheckResult());
							updateLineErrorCounter(line, 3, programItemVisit.getCheckAllResult());
							updateLineErrorCounter(line, 5, programItemVisit.getFirstCheckAllResult());
							break;
						} catch (Exception e) {
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 更新错误计数
	 * 
	 * @param line
	 * @param operaton 0：上料 1：换料 2：核料 3：全检 5：首检
	 * @param result
	 */
	private void updateLineErrorCounter(int line, int opertion, int result) {
		// 根据配置文件计数
		if (result == 0 || result == 3) {
			CenterControllerErrorCounter counter = lineErrorsCounters.get(line);
			if (opertion > 1) {
				int ipqcMode = counter.getIpqcErrorAlarm();
				count(counter, ipqcMode);
			} else {
				int operatorMode = counter.getOperatorErrorAlarm();
				count(counter, operatorMode);
			}
		}
	}

	/**
	 * 计数
	 */
	private void count(CenterControllerErrorCounter counter, int mode) {
		switch (mode) {
		case 0:
			counter.setConveryErrorCount(counter.getConveryErrorCount() + 1);
			counter.setAlarmErrorCount(counter.getAlarmErrorCount() + 1);
			break;
		case 1:
			counter.setAlarmErrorCount(counter.getAlarmErrorCount() + 1);
			break;
		case 2:
			counter.setConveryErrorCount(counter.getConveryErrorCount() + 1);
			break;
		default:
			break;
		}
	}

	/**
	 * 根据统计结果发送指令
	 */
	private void sendCmdByCounters() {
		for (int i = 0; i < lineSize; i++) {
			if (clientSockets.get(i) == null) {
				continue;
			}
			CenterControllerErrorCounter counter = lineErrorsCounters.get(i);
			if (counter.getAlarmErrorCount() > 0) {
				sendCmd(ClientDevice.SERVER, clientSockets.get(i), true, ControlledDevice.ALARM);
			} else {
				sendCmd(ClientDevice.SERVER, clientSockets.get(i), false, ControlledDevice.ALARM);
			}
			if (counter.getConveryErrorCount() > 0) {
				sendCmd(ClientDevice.SERVER, clientSockets.get(i), true, ControlledDevice.CONVEYOR);
			} else {
				sendCmd(ClientDevice.SERVER, clientSockets.get(i), false, ControlledDevice.CONVEYOR);
			}
		}
	}

	/**
	 * 根据配置，发送报警/解除包
	 * 
	 * @param clientDevice     发起者
	 * @param socket           发送指令的socket
	 * @param isAlarm          报警/解除报警
	 * @param controlledDevice 被控制设备：报警灯或接驳台
	 */
	private void sendCmd(ClientDevice clientDevice, ClientSocket socket, boolean isAlarm,
			ControlledDevice controlledDevice) {
		new SendCmdThread(clientDevice, socket, isAlarm, controlledDevice).start();
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
