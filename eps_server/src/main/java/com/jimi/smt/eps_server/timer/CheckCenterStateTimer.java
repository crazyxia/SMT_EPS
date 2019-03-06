package com.jimi.smt.eps_server.timer;

import java.util.Map.Entry;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jimi.smt.eps_server.socket.CenterServerSocket;
import com.jimi.smt.eps_server.socket.DisplayServerSocket;


/**检测中控状态定时器
 * @author   HCJ
 * @date     2019年3月19日 上午11:26:51
 */
@Component
public class CheckCenterStateTimer {

	/**
	 * offlineTime : 断线时间
	 */
	private static final Integer offlineTime = 20000;
	

	@Scheduled(fixedDelay = 5000)
	public void start() {
		if (!CenterServerSocket.getClients().isEmpty()) {
			long currentTimeStamp = System.currentTimeMillis();
			for (Entry<String, Long> timeStampSet : CenterServerSocket.getTimeStampMap().entrySet()) {
				if ((currentTimeStamp - timeStampSet.getValue()) > offlineTime) {
					DisplayServerSocket.sendCenterOfflineInfo(timeStampSet.getKey());
				} else {
					DisplayServerSocket.sendCenterOnlineInfo(timeStampSet.getKey());
				}
			}
		}
	}
}
