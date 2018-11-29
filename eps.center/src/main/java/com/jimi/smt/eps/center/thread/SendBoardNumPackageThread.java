package com.jimi.smt.eps.center.thread;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jimi.smt.eps.center.util.IniReader;
import com.jimi.smt.eps_server.rmi.ServerRemoteWrapper;

/**上传板子数量包到服务器的线程
 * @author   HCJ
 * @date     2018年11月29日 下午2:33:56
 */
public class SendBoardNumPackageThread extends Thread {

    private static Logger logger = LogManager.getRootLogger();

    /**
     * boardCycle : 上传板子数量包周期
     */
    private long boardCycle;
       
    /**
     * serverRemoteWrapper : 中控连接服务器的包装器
     */
    private ServerRemoteWrapper serverRemoteWrapper = new ServerRemoteWrapper();
    
    /**
     * CONFIG_FILE : 记录各项配置的文件
     */
    private static final String CONFIG_FILE = "/config.ini";
    
    
    /**
     * <p>Title: 上传板子数量包到服务器的线程</p>
     * <p>Description: 初始化</p>
     */
    public SendBoardNumPackageThread() {
        Map<String, String> cycleMap = IniReader.getItem(System.getProperty("user.dir") + CONFIG_FILE,"cycle");      
        boardCycle = Integer.parseInt(cycleMap.get("boardCycle"));
    }

    
	@Override
	public void run() {
		// 提示已运行
		logger.info("SMT 中控   开始发送板子数量包!");
		try {
			while (true) {
				try {
					Thread.sleep(boardCycle);
					serverRemoteWrapper.sendBoardNumPackageToServerRemote();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			try {
				run();
			} catch (Exception e1) {
				e1.printStackTrace();
				logger.error(e1.getMessage());
			}
		}
	}
}
