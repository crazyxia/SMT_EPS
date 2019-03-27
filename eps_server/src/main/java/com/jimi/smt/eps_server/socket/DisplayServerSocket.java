package com.jimi.smt.eps_server.socket;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson.JSONObject;
import com.jimi.smt.eps_server.entity.BoardResetInfo;
import com.jimi.smt.eps_server.entity.CenterLogin;
import com.jimi.smt.eps_server.entity.CenterLoginExample;
import com.jimi.smt.eps_server.entity.Line;
import com.jimi.smt.eps_server.entity.LineExample;
import com.jimi.smt.eps_server.entity.LineSelectedInfo;
import com.jimi.smt.eps_server.mapper.CenterLoginMapper;
import com.jimi.smt.eps_server.mapper.LineMapper;
import com.jimi.smt.eps_server.service.ProgramService;
import com.jimi.smt.eps_server.util.OsHelper;


/**Display实时监控webSocket服务端
 * @author   HCJ
 * @date     2019年3月4日 上午10:18:15
 */
@Controller
@ServerEndpoint("/display")
public class DisplayServerSocket {

	private Logger logger = LogManager.getRootLogger();

	/**
	 * clients : 存储当前存在的客户端
	 */
	private static Map<String, Session> clients = new HashMap<>();

	private static CenterLoginMapper centerLoginMapper;
	private static LineMapper lineMapper;
	private static ProgramService programService;


	/**@author HCJ
	 * Display客户端连接成功时调用
	 * @date 2019年2月28日 下午5:15:05
	 */
	@OnOpen
	public void onOpen(Session session) {
		session.setMaxIdleTimeout(0);
		clients.put(session.getId(), session);
		if(!OsHelper.isProductionEnvironment()) {
			logger.info("sessionID为 " + session.getId() + " 的Display客户端连接服务端成功");
		}
	}


	/**@author HCJ
	 * Display服务端接收来自客户端的信息
	 * @date 2019年2月28日 下午5:15:31
	 */
	@OnMessage
	public void onMessage(Session session, String message) {
		if (message != null && message.contains("boardResetReson")) {
			resetBoardNum(message);
		} else {
			sendLineSelectedInfo(session, message);
		}
		if (!OsHelper.isProductionEnvironment()) {
			logger.info("接收到Display客户端的信息： " + message);
		}
	}


	/**@author HCJ
	 * Display客户端与服务端连接关闭时调用
	 * @param closeReason 关闭原因
	 * @date 2019年2月28日 下午5:15:52
	 */
	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		clients.remove(session.getId());
		if(!OsHelper.isProductionEnvironment()) {
			logger.info("sessionID为  " + session.getId() + " 的Display客户端与服务端断开连接，原因为：" + closeReason);
		}
	}


	/**@author HCJ
	 * Display客户端与服务端连接出错时调用
	 * @date 2019年2月28日 下午5:16:31
	 */
	@OnError
	public void onError(Session session, Throwable error) {
		try {
			session.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		clients.remove(session.getId());
		logger.error("sessionID为  " + session.getId() + " 的Display客户端与服务端连接出现错误，原因为：" + error.getMessage());
	}


	/**@author HCJ
	 * 发送中控下线信息到Display
	 * @param centerMac 中控树莓派Mac地址
	 * @date 2019年3月13日 上午8:03:43
	 */
	public static void sendCenterOfflineInfo(String centerMac) {
		LineExample lineExample = new LineExample();
		Integer lineId = CenterServerSocket.getLineIdMap().get(centerMac);
		if (lineId != null) {
			lineExample.createCriteria().andIdEqualTo(lineId);
			List<Line> lines = lineMapper.selectByExample(lineExample);
			if (lines != null && lines.size() > 0) {
				String lineName = lines.get(0).getLine();
				for (Session session : clients.values()) {
					if (session != null && session.isOpen()) {
						try {
							session.getBasicRemote().sendText(lineName + " 产线的树莓派连接已断开，请检查网络连接");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}


	/**@author HCJ
	 * 发送中控上线信息到Display
	 * @param centerMac 中控树莓派Mac地址
	 * @date 2019年3月19日 上午8:20:48
	 */
	public static void sendCenterOnlineInfo(String centerMac) {
		LineExample lineExample = new LineExample();
		Integer lineId = CenterServerSocket.getLineIdMap().get(centerMac);
		if (lineId != null) {
			lineExample.createCriteria().andIdEqualTo(lineId);
			List<Line> lines = lineMapper.selectByExample(lineExample);
			if (lines != null && lines.size() > 0) {
				String lineName = lines.get(0).getLine();
				for (Session session : clients.values()) {
					if (session != null && session.isOpen()) {
						try {
							session.getBasicRemote().sendText(lineName + " 产线的树莓派已连接服务端");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}


	@Autowired
	public void setCenterLoginMapper(CenterLoginMapper centerLoginMapper) {
		DisplayServerSocket.centerLoginMapper = centerLoginMapper;
	}


	@Autowired
	public void setLineMapper(LineMapper lineMapper) {
		DisplayServerSocket.lineMapper = lineMapper;
	}


	@Autowired
	public void setProgramService(ProgramService programService) {
		DisplayServerSocket.programService = programService;
	}


	/**@author HCJ
	 * 根据板子数量重置信息重置板子数量
	 * @date 2019年2月28日 下午4:05:16
	 */
	private void resetBoardNum(String boardResetInfoString) {
		BoardResetInfo boardResetInfo = JSONObject.parseObject(boardResetInfoString, BoardResetInfo.class);
		CenterLoginExample centerLoginExample = new CenterLoginExample();
		centerLoginExample.createCriteria().andLineEqualTo(boardResetInfo.getLine());
		List<CenterLogin> centerLogins = centerLoginMapper.selectByExample(centerLoginExample);
		if (centerLogins != null && centerLogins.size() > 0) {
			try {
				CenterServerSocket.sendBoardResetInfo(centerLogins.get(0).getMac(), boardResetInfo);
			} catch (IOException e) {
				logger.error("发送板子数量重置信息到中控失败" + e.getMessage());
			}
		}
	}


	/**@author HCJ
	 * 发送产线选择信息到Display
	 * @param lineName 产线名称
	 * @date 2019年4月9日 下午3:49:34
	 */
	private void sendLineSelectedInfo(Session session, String lineName) {
		String result = programService.switchWorkOrder(lineName, null, null, true);
		LineSelectedInfo lineSelectedInfo = new LineSelectedInfo(lineName, "true");
		if ("succeed".equals(result)) {
			for (Entry<String, Session> clientSet : clients.entrySet()) {
				Session clientSession = clientSet.getValue();
				if (clientSession != null && clientSession.isOpen() && !clientSession.equals(session)) {
					clientSession.getAsyncRemote().sendText(JSONObject.toJSONString(lineSelectedInfo));
				}
			}
		}
	}
	
}
