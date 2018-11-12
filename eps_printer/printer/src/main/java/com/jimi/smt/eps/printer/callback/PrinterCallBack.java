package com.jimi.smt.eps.printer.callback;

import javax.websocket.Session;

import com.jimi.smt.eps.printer.entity.PrinterInfo;

public interface PrinterCallBack {
	public void printCallBack(Session session, PrinterInfo info);
}
