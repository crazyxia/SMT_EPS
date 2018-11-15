package com.jimi.smt.eps_server.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
		System.setProperty("java.rmi.server.hostname", "10.10.11.186");
		ServerRemote serverRemote = (ServerRemote) UnicastRemoteObject.exportObject(serverRemoteImpl, 23333);
		LocateRegistry.createRegistry(1099);
		LocateRegistry.getRegistry().bind("server", serverRemote);
		System.out.println("RMI服务端开启");
	}

}
