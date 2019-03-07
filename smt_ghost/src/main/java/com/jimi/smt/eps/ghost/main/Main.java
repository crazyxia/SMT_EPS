package com.jimi.smt.eps.ghost.main;

import com.jimi.smt.eps.ghost.websocket.GhostClientSocket;

/**
 * 我是程序入口
 */
public class Main {
	
    public static void main(String[] args) throws Exception {
    	GhostClientSocket socket = new GhostClientSocket();
    	socket.connect();
    	while(true) {Thread.sleep(99999);}
    }
    
}
