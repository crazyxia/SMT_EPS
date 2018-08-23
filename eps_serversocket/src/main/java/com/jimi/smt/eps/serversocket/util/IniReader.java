package com.jimi.smt.eps.serversocket.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.ini4j.Config;
import org.ini4j.Ini;


public class IniReader {

    private static Ini ini = new Ini();    
    
    
    public static void setIni(String filename){
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
