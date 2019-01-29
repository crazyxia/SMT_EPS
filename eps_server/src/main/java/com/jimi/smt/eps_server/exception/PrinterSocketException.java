package com.jimi.smt.eps_server.exception;

/**打印错误异常类
 * @author   HCJ
 * @date     2019年1月29日 下午12:03:29
 */
public class PrinterSocketException extends RuntimeException{

	private static final long serialVersionUID = -1492328329282101887L;


	public PrinterSocketException(String message) {
		super(message);
	}
}
