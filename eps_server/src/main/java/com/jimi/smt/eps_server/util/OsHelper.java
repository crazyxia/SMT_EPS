package com.jimi.smt.eps_server.util;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class OsHelper {

	/**
	 * 获取linux系统下的本地IP
	 */
	public static String getLinuxLocalIp(){
		String ip = "";
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				String name = intf.getName();
				if (!name.contains("docker") && !name.contains("lo")) {
					for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (!inetAddress.isLoopbackAddress()) {
							String ipaddress = inetAddress.getHostAddress().toString();
							if (!ipaddress.contains("::") && !ipaddress.contains("0:0:") && !ipaddress.contains("fe80")) {
								ip = ipaddress;
								return ip;
							}
						}
					}
				}
			}
		} catch (SocketException ex) {
			System.out.println("获取ip地址异常");
			ip = "127.0.0.1";
			ex.printStackTrace();
		}
		return ip;
	}
	
	
	/**
	 * 获取windows系统下的本地IP
	 */
	public static String getWindowsLocalIp(){
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			System.out.println("获取ip地址异常");
			e.printStackTrace();
		}
		return "127.0.0.1";
	}
	
	
	/**@author HCJ
	 * 判断是否是windows系统下
	 * @date 2019年1月27日 上午9:04:14
	 */
	public static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0;
	}
	
	
	public static boolean isProductionEnvironment() {
		File[] roots = File.listRoots();
        for (int i=0; i < roots.length; i++) {
            if(new File(roots[i].toString() + "PRODUCTION_ENVIRONMENT_FLAG").exists()) {
            	return true;
            }
        }
        return false;
	}


	public static boolean isTestEnvironment() {
		File[] roots = File.listRoots();
        for (int i=0; i < roots.length; i++) {
            if(new File(roots[i].toString() + "TEST_ENVIRONMENT_FLAG").exists()) {
            	return true;
            }
        }
        return false;
	}
}
