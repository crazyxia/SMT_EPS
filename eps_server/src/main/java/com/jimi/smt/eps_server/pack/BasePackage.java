package com.jimi.smt.eps_server.pack;

import java.io.Serializable;

/**
 * 通讯协议包基类
 * 
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class BasePackage implements Serializable{

	/**
	 * serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 6187245719996415653L;
	
	/**
	 * 包长度
	 */
	public byte length;
	/**
	 * 协议类型
	 */
	public String protocol = "";
	/**
	 * 信息序列号
	 */
	public Short serialNo;
	/**
	 * 错误校验码
	 */
	public Short crc;
	
	/**
	 * 发送者ip
	 */
	public String senderIp = "";
	
	/**
	 * 接受者ip
	 */
	public String receiverIp = "";
	
}
