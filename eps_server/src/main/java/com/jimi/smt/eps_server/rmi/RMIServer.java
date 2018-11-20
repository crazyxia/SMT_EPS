package com.jimi.smt.eps_server.rmi;

import java.io.InputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jimi.smt.eps_server.util.IpHelper;

/**
 * 开启RMI服务端
 * @author HCJ
 * @date 2018年11月4日 下午4:43:58
 */
@Component
public class RMIServer {
	
	@Autowired
	private ServerRemoteImpl serverRemoteImpl;

	
	@PostConstruct
	public void start() throws Exception {
		// TODO ip需要换成服务器ip
		System.setProperty("java.rmi.server.hostname",IpHelper.getLinuxLocalIp());
		InputStream inputStream = null; 
		Properties p = new Properties();   
        try {
        	inputStream = this.getClass().getClassLoader().getResourceAsStream("/properties/rmi.properties"); 
            p.load(inputStream);
        }catch (Exception e) {
        	e.printStackTrace();
		}finally {
			if(inputStream != null) {
				inputStream.close();
			}
		}
		ServerRemote serverRemote = (ServerRemote) UnicastRemoteObject.exportObject(serverRemoteImpl,Integer.parseInt(p.getProperty("port")));
		LocateRegistry.createRegistry(1099);
		LocateRegistry.getRegistry().bind("server", serverRemote);
		System.out.println("RMI服务端开启");
	}

}
