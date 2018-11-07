package com.jimi.smt.eps_server.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.jimi.smt.eps_server.pack.BoardNumPackage;
import com.jimi.smt.eps_server.pack.HeartPackage;
import com.jimi.smt.eps_server.pack.LoginPackage;
import com.jimi.smt.eps_server.pack.LoginReplyPackage;

public interface ServerRemote extends Remote {

	/**@author HCJ
	 * 接收登录包
	 * @date 2018年11月5日 下午4:29:30
	 */
	public LoginReplyPackage login(LoginPackage loginPackage) throws RemoteException;

	/**@author HCJ
	 * 接收板子数量包
	 * @date 2018年11月5日 下午4:30:00
	 */
	public void updateBoardNum(BoardNumPackage boardNumPackage) throws RemoteException;

	/**@author HCJ
	 * 接收心跳包
	 * @date 2018年11月5日 下午4:30:03
	 */
	public void saveHeartPackageLog(HeartPackage heartPackage) throws RemoteException;
}
