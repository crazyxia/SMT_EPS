package com.jimi.smt.eps_server.rmi;

import java.io.InputStream;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.rmi.PortableRemoteObject;

import org.springframework.beans.factory.annotation.Autowired;

import com.jimi.smt.eps_server.util.OsHelper;

/**
 * 开启和关闭RMI服务端
 * 
 * @author HCJ
 * @date 2018年11月4日 下午4:43:58
 */
public class RMIServer {

	@Autowired
	private ServerRemoteImpl serverRemoteImpl;

	private Registry reg;
	
	/**
	 * REGISTERED_PORT : RMI注册端口
	 */
	private static final int REGISTERED_PORT = 1099;

	
	@PostConstruct
	public void start() throws Exception {
		System.setProperty("java.rmi.server.hostname", OsHelper.getLinuxLocalIp());
		InputStream inputStream = null;
		Properties p = new Properties();
		try {
			inputStream = this.getClass().getClassLoader().getResourceAsStream("/properties/rmi.properties");
			p.load(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
		ServerRemote serverRemote = (ServerRemote) UnicastRemoteObject.exportObject(serverRemoteImpl, Integer.parseInt(p.getProperty("port")));
		reg = LocateRegistry.createRegistry(REGISTERED_PORT);
		reg.rebind("server", serverRemote);
		if (OsHelper.isWindows()) {
			System.out.println("RMI服务端开启");
		}
	}

	
	/**
	 * @author HCJ 关闭RMI服务
	 * @date 2018年12月11日 上午11:03:24
	 */
	public void stop() {
		try {
			String[] objectNames = reg.list();
			for (String objectName : objectNames) {
				Remote remote = reg.lookup(objectName);
				reg.unbind(objectName);
				UnicastRemoteObject.unexportObject(remote, true);
			}
			UnicastRemoteObject.unexportObject(reg, true);
		} catch (RemoteException | NotBoundException e) {
			try {
				PortableRemoteObject.unexportObject(reg);
			} catch (NoSuchObjectException e1) {
				e1.printStackTrace();
			}
		}
		if (OsHelper.isWindows()) {
			System.out.println("RMI服务端关闭");
		}
	}

}
