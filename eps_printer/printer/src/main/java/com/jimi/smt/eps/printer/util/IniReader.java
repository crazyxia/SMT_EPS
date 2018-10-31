package com.jimi.smt.eps.printer.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.ini4j.Config;
import org.ini4j.Ini;

/**读取配置文件
 * @author   HCJ
 * @date     2018年9月25日 下午5:14:42
 * @version  V 1.0
 */
public class IniReader {

	private static Ini ini = new Ini();

	
	public static void setIni(String filename) {
		Config config = new Config();
		config.setMultiSection(true);
		File file = new File(filename);
		ini.setConfig(config);
		try {
			ini.load(file);
		} catch (IOException e) {
			System.out.println("文件不存在");
			e.printStackTrace();
		}
	}

	
	public static Map<String, String> getItem(String item) {
		Map<String, String> map = ini.get(item);
		return map;
	}
}
