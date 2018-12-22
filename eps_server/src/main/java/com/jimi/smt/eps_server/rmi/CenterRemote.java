package com.jimi.smt.eps_server.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.jimi.smt.eps_server.pack.BoardResetPackage;
import com.jimi.smt.eps_server.pack.BoardResetReplyPackage;
import com.jimi.smt.eps_server.pack.ControlPackage;
import com.jimi.smt.eps_server.pack.ControlReplyPackage;

public interface CenterRemote extends Remote{

	/**@author HCJ
	 * 接收控制包
	 * @date 2018年11月5日 下午4:14:04
	 */
	public ControlReplyPackage receiveControl(ControlPackage controlPackage)throws RemoteException;
	
	/**@author HCJ
	 * 接收板子数量重置包，重置板子数量
	 * @date 2018年11月5日 下午4:14:40
	 */
	public BoardResetReplyPackage resetBoardNum(BoardResetPackage boardResetPackage)throws RemoteException;
}
