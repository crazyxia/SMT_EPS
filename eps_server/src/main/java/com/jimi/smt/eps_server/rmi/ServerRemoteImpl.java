package com.jimi.smt.eps_server.rmi;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jimi.smt.eps_server.entity.CenterLogin;
import com.jimi.smt.eps_server.entity.CenterLoginExample;
import com.jimi.smt.eps_server.entity.Display;
import com.jimi.smt.eps_server.entity.DisplayExample;
import com.jimi.smt.eps_server.entity.Line;
import com.jimi.smt.eps_server.entity.Program;
import com.jimi.smt.eps_server.entity.ProgramExample;
import com.jimi.smt.eps_server.entity.SocketLog;
import com.jimi.smt.eps_server.entity.ProgramExample.Criteria;
import com.jimi.smt.eps_server.mapper.CenterLoginMapper;
import com.jimi.smt.eps_server.mapper.CenterStateMapper;
import com.jimi.smt.eps_server.mapper.ConfigMapper;
import com.jimi.smt.eps_server.mapper.DisplayMapper;
import com.jimi.smt.eps_server.mapper.LineMapper;
import com.jimi.smt.eps_server.mapper.ProgramItemVisitMapper;
import com.jimi.smt.eps_server.mapper.ProgramMapper;
import com.jimi.smt.eps_server.mapper.SocketLogMapper;
import com.jimi.smt.eps_server.pack.BoardNumPackage;
import com.jimi.smt.eps_server.pack.BoardNumReplyPackage;
import com.jimi.smt.eps_server.pack.LoginPackage;
import com.jimi.smt.eps_server.pack.LoginReplyPackage;
import com.jimi.smt.eps_server.timer.CheckErrorTimer;

import cc.darhao.dautils.api.BytesParser;
import cc.darhao.dautils.api.FieldUtil;
import com.jimi.smt.eps_server.pack.BasePackage;
import com.jimi.smt.eps_server.util.PackageParser;

@Component
public class ServerRemoteImpl implements ServerRemote {

	private static Logger logger = LogManager.getRootLogger();

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
	 * clientSockets : 所有线别的报警模块列表
	 */
	private Map<Integer, CenterRemoteWrapper> connectToCenterRemotes = new HashMap<Integer, CenterRemoteWrapper>();

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
	
	// spring注入bean需要使用的构造器
	public ServerRemoteImpl() throws RemoteException{}

	
	/** <p>Title: login</p>
	 * <p>Description: 接收登录包，开启错误检测线程</p> 
	 */
	@Override
	public LoginReplyPackage login(LoginPackage loginPackage){
		// 根据mac地址获取线别
		String mac = loginPackage.getCenterControllerMAC();
		CenterLoginExample example = new CenterLoginExample();
		example.createCriteria().andMacEqualTo(mac);
		List<CenterLogin> logins = centerLoginMapper.selectByExample(example);
		LoginReplyPackage loginReplyPackage = new LoginReplyPackage();
		if (!logins.isEmpty()) {
			CenterLogin login = logins.get(0);
			loginReplyPackage.setLine(login.getLine());
			loginReplyPackage.setTimestamp(new Date());
			loginReplyPackage.protocol = loginPackage.protocol;
			loginReplyPackage.serialNo = 1;
			loginReplyPackage.senderIp = loginPackage.receiverIp;
			loginReplyPackage.receiverIp = loginPackage.senderIp;
			insertLogByPackage(loginPackage);
			insertLogByPackage(loginReplyPackage);
			initLineConfig();
			try {
				connectToCenterRemotes.put(getIndexByLineId(login.getLine()), new CenterRemoteWrapper(login.getLine(), centerLoginMapper, socketLogMapper, centerStateMapper));
			} catch (Exception e) {
				logger.error("搜索产线 " + lineMap.get(login.getLine()).getLine() + " : " + e.getMessage());
			}
			checkErrorTimer = new CheckErrorTimer(lineSize, lineMap, configMapper, programMapper, programItemVisitMapper, connectToCenterRemotes);
		} else {
			logger.error("无效的树莓派MAC地址");
		}
		logger.info(loginPackage.protocol + "package arrived");
		return loginReplyPackage;
	}

	
	/** <p>Title: updateBoardNum</p>
	 * <p>Description: 接收板子数量包，更新板子数量</p>
	 */
	@Override
	public BoardNumReplyPackage updateBoardNum(BoardNumPackage boardNumPackage)throws RemoteException{
		// 物料是几联板
		int structure;
		// 当前工单已生产数量
		int alreadyProduct;
		
		BoardNumReplyPackage boardNumReplyPackage = new BoardNumReplyPackage();
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
					Program firstProgram = programs.get(0);
					structure = firstProgram.getStructure();
					alreadyProduct = firstProgram.getAlreadyProduct();
					Program program = new Program();
					program.setAlreadyProduct((boardNumPackage.getBoardNum()) * structure + alreadyProduct);
					programMapper.updateByExampleSelective(program, programExample);
				}
			}
		}
		boardNumReplyPackage.protocol = boardNumPackage.protocol;
		boardNumReplyPackage.serialNo = 1;
		boardNumReplyPackage.senderIp = boardNumPackage.receiverIp;
		boardNumReplyPackage.receiverIp = boardNumPackage.senderIp;
		insertLogByPackage(boardNumPackage); 
		insertLogByPackage(boardNumReplyPackage);
		logger.info(boardNumPackage.protocol + "package arrived");
		return boardNumReplyPackage;
	}

	
	/**
	 * @author HCJ 每隔3秒检查是否有错误项目
	 * @method checkError
	 * @return void
	 * @date 2018年9月26日 上午8:52:33
	 */
	@Scheduled(cron = "0/3 * * * * ?")
	public void checkError() {
		if (checkErrorTimer != null) {
			logger.info("SMT 系统监听错误中。。。");
			checkErrorTimer.start();
		}
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

	
	/**@author HCJ
	 * 根据产线ID返回产线在lineMap的位置
	 * @date 2018年11月5日 下午4:33:21
	 */
	private Integer getIndexByLineId(Integer id) {
		for (Integer index : lineMap.keySet()) {
			if (lineMap.get(index).getId().equals(id)) {
				return index;
			}
		}
		return null;
	}


	/**@author HCJ
	 * 初始化产线数量和集合
	 * @date 2018年9月26日 上午8:50:43
	 */
	private void initLineConfig() {
		List<Line> lines = lineMapper.selectByExample(null);
		lineSize = lines.size();
		for (int i = 0; i < lineSize; i++) {
			Line line = lines.get(i);
			lineMap.put(i, line);
		}
	}
}