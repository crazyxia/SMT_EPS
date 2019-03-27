package com.jimi.smt.eps_server.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;


public class IpHelper {


	/**
	 * 获取linux系统下的本地IP
	 */
	public static String getLinuxLocalIp() {
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
	public static String getWindowsLocalIp() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			System.out.println("获取ip地址异常");
			e.printStackTrace();
		}
		return "127.0.0.1";
	}


	/**
	 * 根据请求获取客户端IP
	 * @date 2019年4月8日 下午5:11:28
	 */
	public static String getCliectIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Real-IP");
		if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
			return ip;
		}
		ip = request.getHeader("X-Forwarded-For");
		if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
			// 多次反向代理后会有多个IP值，第一个为真实IP。
			int index = ip.indexOf(',');
			if (index != -1) {
				return ip.substring(0, index);
			} else {
				return ip;
			}
		} else {
			return request.getRemoteAddr();
		}
	}

}
