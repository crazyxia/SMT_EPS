package com.jimi.smt.eps.center.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.ini4j.Config;
import org.ini4j.Ini;

/**读取配置文件
 * @package  com.jimi.smt.eps.center.util
 * @file     IniReader.java
 * @author   HCJ
 * @date     2018年9月25日 下午5:14:42
 * @version  V 1.0
 */
public class IniReader {

	private static Ini ini = new Ini();

	
	public static Map<String, String> getItem(String filename, String item) {
		Config config = new Config();
		config.setMultiSection(true);
		File file = new File(filename);
		ini.setConfig(config);
		try {
			ini.load(file);
			Map<String, String> map = ini.get(item);
			return map;
		} catch (IOException e) {
			System.out.println("文件不存在");
			e.printStackTrace();
		}
		return null;
	}
}
