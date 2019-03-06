package com.jimi.smt.eps_server.util;

public class SqlUtil {

	/**@author HCJ
	 * 对参数存在的mysql特殊字符进行转义
	 * @date 2018年11月27日 下午3:58:08
	 */
	public static String escapeParameter(String parameter) {
		StringBuffer buffer = new StringBuffer();
		int len = parameter.length();
		for (int i = 0; i < len; i++) {
			char c = parameter.charAt(i);
			switch (c) {
			case '_':
				buffer.append("\\_");
				break;
			case '%':
				buffer.append("\\%");
				break;
			case '\\':
				buffer.append("\\\\");
				break;
			default:
				buffer.append(c);
			}
		}
		return buffer.toString();
	}

}
