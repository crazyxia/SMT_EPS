package com.jimi.smt.eps_server.socket;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson.JSONObject;
import com.jimi.smt.eps_server.entity.BoardNumInfo;
import com.jimi.smt.eps_server.entity.BoardResetInfo;
import com.jimi.smt.eps_server.entity.CenterControlInfo;
import com.jimi.smt.eps_server.entity.CenterLogin;
import com.jimi.smt.eps_server.entity.CenterLoginExample;
import com.jimi.smt.eps_server.entity.Display;
import com.jimi.smt.eps_server.entity.DisplayExample;
import com.jimi.smt.eps_server.entity.Program;
import com.jimi.smt.eps_server.entity.ProgramExample;
import com.jimi.smt.eps_server.entity.SocketLog;
import com.jimi.smt.eps_server.entity.ProgramExample.Criteria;
import com.jimi.smt.eps_server.mapper.CenterLoginMapper;
import com.jimi.smt.eps_server.mapper.DisplayMapper;
import com.jimi.smt.eps_server.mapper.ProgramMapper;
import com.jimi.smt.eps_server.mapper.SocketLogMapper;
import com.jimi.smt.eps_server.util.CenterControlInfoSender;
import com.jimi.smt.eps_server.util.OsHelper;


/**中控webSocket服务端
 * @author   HCJ
 * @date     2019年2月27日 下午5:55:13
 */
@Controller
@ServerEndpoint("/center/{centerMac}")
public class CenterServerSocket {

	private static Logger logger = LogManager.getRootLogger();

	/**
	 * clients : 存储当前存在的客户端
	 */
	private static Map<String, Session> clients = new HashMap<>();
	
	/**
	 * lineIdMap : 存储当前中控Mac和产线ID的信息
	 */
	private static Map<String, Integer> lineIdMap = new HashMap<>();
	
	/**
	 * timeStampMap : 存储当前中控Mac和时间戳的信息
	 */
	private static Map<String, Long> timeStampMap = new HashMap<>();

	private static CenterLoginMapper centerLoginMapper;
	private static SocketLogMapper socketLogMapper;
	private static DisplayMapper displayMapper;
	private static ProgramMapper programMapper;
	

	/**@author HCJ
	 * 中控连接时调用
	 * @param centerMac 中控树莓派Mac
	 * @date 2019年2月27日 下午5:57:57
	 */
	@OnOpen
	public void onOpen(@PathParam("centerMac") String centerMac, Session session) {
		CenterLoginExample example = new CenterLoginExample();
		String mac = centerMac.replaceAll(":", " ");
		example.createCriteria().andMacEqualTo(mac);
		List<CenterLogin> logins = centerLoginMapper.selectByExample(example);
		if (logins != null && !logins.isEmpty()) {
			if (!OsHelper.isProductionEnvironment()) {
				logger.info("IP为  " + logins.get(0).getIp() + " 的树莓派在 " + new Date() + " 时上线");
			}
			session.setMaxIdleTimeout(0);
			clients.put(logins.get(0).getMac(), session);
			lineIdMap.put(centerMac, logins.get(0).getLine());
			insertSocketLog("Mac地址为 " + centerMac + " IP为  " + logins.get(0).getIp() + " 的中控上线", "Login", logins.get(0).getIp(), OsHelper.getLinuxLocalIp());
			try {
				CenterControlInfoSender.reset(mac, logins.get(0).getLine());
			} catch (Exception e) {
				logger.error("重置中控状态出错 | " + e.getMessage());
			}
			timeStampMap.put(centerMac, System.currentTimeMillis());
			DisplayServerSocket.sendCenterOnlineInfo(centerMac);
		} else {
			logger.error("无效的树莓派Mac地址");
		}
	}


	/**@author HCJ
	 * 接收中控信息
	 * @date 2019年2月27日 下午5:58:41
	 */
	@OnMessage
	public void onMessage(@PathParam("centerMac") String centerMac, String message) {
		if (message != null && message.contains("boardNum")) {
			updateBoardNum(message);
		} else if (message != null && message.contains("Heart")) {
			timeStampMap.put(centerMac, System.currentTimeMillis());
		}
	}


	/**@author HCJ
	 * 中控关闭连接时调用
	 * @date 2019年2月27日 下午5:59:01
	 */
	@OnClose
	public void onClose(@PathParam("centerMac") String centerMac, Session session) {
		DisplayServerSocket.sendCenterOfflineInfo(centerMac);
		/*try {
			session.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}*/
		String mac = centerMac.replaceAll(":", " ");
		clients.remove(mac);
		insertSocketLog("Mac地址为 " + centerMac + " 的中控已关闭连接", "", "", "");
	}


	/**@author HCJ
	 * 中控连接出错时调用
	 * @date 2019年2月27日 下午5:59:20
	 */
	@OnError
	public void onError(@PathParam("centerMac") String centerMac, Session session, Throwable error) {
		DisplayServerSocket.sendCenterOfflineInfo(centerMac);
		try {
			session.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		String mac = centerMac.replaceAll(":", " ");
		clients.remove(mac);
		logger.error(error.getMessage());
	}


	/**@author HCJ
	 * 发送控制信息到中控
	 * @param centerMac 中控Mac
	 * @param centerControlInfo 中控控制信息类
	 * @date 2019年2月27日 下午5:59:39
	 */
	public synchronized static void sendCenterControlInfo(String centerMac, CenterControlInfo centerControlInfo) throws IOException {
		JSONObject jsonObject = (JSONObject) JSONObject.toJSON(centerControlInfo);
		Session session = clients.get(centerMac);
		if (session != null && session.isOpen()) {
			session.getBasicRemote().sendText(jsonObject.toJSONString());
		}
	}
	
	
	/**@author HCJ
	 * 发送板子数量重置信息到达中控
	 * @param centerMac 中控Mac
	 * @param boardResetInfo 中控板子数量重置信息类
	 * @date 2019年2月28日 下午4:13:29
	 */
	public synchronized static void sendBoardResetInfo(String centerMac, BoardResetInfo boardResetInfo) throws IOException {
		JSONObject jsonObject = (JSONObject) JSONObject.toJSON(boardResetInfo);
		Session session = clients.get(centerMac);
		if (session != null && session.isOpen()) {
			session.getBasicRemote().sendText(jsonObject.toJSONString());
		}
	}


	@Autowired
	public void setCenterLoginMapper(CenterLoginMapper centerLoginMapper) {
		CenterServerSocket.centerLoginMapper = centerLoginMapper;
	}


	@Autowired
	public void setSocketLogMapper(SocketLogMapper socketLogMapper) {
		CenterServerSocket.socketLogMapper = socketLogMapper;
	}


	@Autowired
	public void setDisplayMapper(DisplayMapper displayMapper) {
		CenterServerSocket.displayMapper = displayMapper;
	}


	@Autowired
	public void setProgramMapper(ProgramMapper programMapper) {
		CenterServerSocket.programMapper = programMapper;
	}


	public synchronized static Map<String, Session> getClients() {
		return clients;
	}


	public synchronized static Map<String, Integer> getLineIdMap() {
		return lineIdMap;
	}


	public static Map<String, Long> getTimeStampMap() {
		return timeStampMap;
	}


	/**@author HCJ
	 * 更新板子数量
	 * @param boardNumInfoString 板子数量信息文本
	 * @date 2019年2月28日 下午4:02:40
	 */
	private void updateBoardNum(String boardNumInfoString) {
		BoardNumInfo boardNumInfo = JSONObject.parseObject(boardNumInfoString, BoardNumInfo.class);
		// 物料是几联板
		int structure;
		// 当前工单已生产数量
		int alreadyProduct;
		DisplayExample displayExample = new DisplayExample();
		displayExample.createCriteria().andLineEqualTo(boardNumInfo.getLine());
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
				if (programs != null && !programs.isEmpty()) {
					Program firstProgram = programs.get(0);
					structure = firstProgram.getStructure();
					alreadyProduct = firstProgram.getAlreadyProduct();
					Program program = new Program();
					program.setAlreadyProduct((boardNumInfo.getBoardNum()) * structure + alreadyProduct);
					programMapper.updateByExampleSelective(program, programExample);
					insertSocketLog("ID为  " + boardNumInfo.getLine() + " 的产线更新板子数量为 " + boardNumInfo.getBoardNum(), "BoardNum", "", "");
				}
			}
		}
		if (!OsHelper.isProductionEnvironment()) {
			logger.info("ID为  " + boardNumInfo.getLine() + " 的产线在  " + new Date() + " 时更新板子数量为" + boardNumInfo.getBoardNum());
		}
	}


	/**@author HCJ
	 * 插入socket日志
	 * @param data 参数信息
	 * @param protocol 类型
	 * @param senderIp 发送IP
	 * @param receiverIp 接收IP
	 * @date 2019年2月27日 下午6:00:28
	 */
	private void insertSocketLog(String data, String protocol, String senderIp, String receiverIp) {
		SocketLog log = new SocketLog();
		log.setTime(new Date());
		log.setData(data);
		log.setProtocol(protocol);
		log.setSenderIp(senderIp);
		log.setReceiverIp(receiverIp);
		socketLogMapper.insert(log);
	}

}
