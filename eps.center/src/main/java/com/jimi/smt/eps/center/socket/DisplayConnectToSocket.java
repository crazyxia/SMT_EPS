package com.jimi.smt.eps.center.socket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jimi.smt.eps.center.constant.ControlResult;

import com.jimi.smt.eps.center.constant.ErrorCode;
import com.jimi.smt.eps.center.pack.BoardNumPackage;
import com.jimi.smt.eps.center.pack.BoardNumReplyPackage;
import com.jimi.smt.eps.center.pack.BoardResetPackage;
import com.jimi.smt.eps.center.pack.BoardResetReplyPackage;
import com.jimi.smt.eps.center.util.IniReader;

import cc.darhao.dautils.api.TextFileUtil;
import cc.darhao.jiminal.callback.OnPackageArrivedListener;
import cc.darhao.jiminal.core.BasePackage;
import cc.darhao.jiminal.core.SyncCommunicator;

public class DisplayConnectToSocket {

    private static Logger logger = LogManager.getRootLogger();

    private SyncCommunicator communicatorServer;

    private SyncCommunicator communicatorClient;

    private static final String PACKAGE_PATH = "com.jimi.smt.eps.center.pack";

    private static final int TIME_OUT = 100000;
      
    /**
     * 记录板子数量的文件
     */
     private static final String BOARDNUM_FILE = "/board_num.txt";
     
     /**
      * 记录各项配置的文件
      */
     private static final String CONFIG_FILE = "/config.ini";

     /**
      * 服务器的端口
      */
     private int listen_server_port = 23333;
     
     /**
      * 服务器的IP
      */
     private String connect_to_server_ip;
     
     /**
      * 监听display的端口
      */
     private int listen_display_port = 23334;
    
    public DisplayConnectToSocket() {
        // display连接到中控
        IniReader.setIni(System.getProperty("user.dir")+CONFIG_FILE);       
        communicatorClient = new SyncCommunicator(listen_display_port, PACKAGE_PATH);

        // 中控连接到服务器        
        Map<String, String> map_ip = IniReader.getItem("ip");
        connect_to_server_ip = map_ip.get("connect_to_server_ip");
        communicatorServer = new SyncCommunicator(connect_to_server_ip, listen_server_port, PACKAGE_PATH);
        communicatorServer.setTimeout(TIME_OUT);
        try {
            communicatorServer.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    /**
     * 打开端口，开始监听display
     */
    public void open() {
        logger.info("SMT 中控  开始监听display!");
        communicatorClient.startServer(new OnPackageArrivedListener() {

            @SuppressWarnings("unused")
            @Override
            public void onPackageArrived(BasePackage p, BasePackage r) {
                try {
                    // 处理板子数量重置包逻辑
                    if (p instanceof BoardResetPackage && r instanceof BoardResetReplyPackage) {
                        BoardResetPackage boardResetPackage = (BoardResetPackage) p;
                        BoardResetReplyPackage boardResetReplyPackage = (BoardResetReplyPackage) r;
                        BoardNumPackage boardNumPackage = new BoardNumPackage();
                        String boardNum = TextFileUtil.readFromFile(System.getProperty("user.dir")+BOARDNUM_FILE);
                        if (boardNum == null || boardNum.equals("")) {                            
                            boardNumPackage.setBoardNum(Integer.parseInt("0"));
                        } else {
                            boardNumPackage.setBoardNum(Integer.parseInt(TextFileUtil.readFromFile(System.getProperty("user.dir")+BOARDNUM_FILE)));
                        }
                        boardNumPackage.setTimestamp(new Date());
                        boardNumPackage.setLine(boardResetPackage.getLine());
                        BoardNumReplyPackage boardNumReplyPackage = (BoardNumReplyPackage) communicatorServer
                                .send(boardNumPackage);
                        logger.info("在时间:" + boardNumPackage.getTimestamp() + " ID为：" + boardResetPackage.getLine().toString() + " 的产线display客户端发送上传板子数量包");
                        TextFileUtil.writeToFile(System.getProperty("user.dir")+BOARDNUM_FILE, "0");
                        boardResetReplyPackage.setClientDevice(boardResetPackage.getClientDevice());
                        if ("0".equals(TextFileUtil.readFromFile(System.getProperty("user.dir")+BOARDNUM_FILE))) {
                            boardResetReplyPackage.setControlResult(ControlResult.SUCCEED);
                            boardResetReplyPackage.setErrorCode(ErrorCode.SUCCEED);
                        } else {
                            boardResetReplyPackage.setControlResult(ControlResult.FAILED);
                            boardResetReplyPackage.setErrorCode(ErrorCode.RELAY_FAILURE);
                        }
                    }
                    // 记录包协议
                    logger.info(p.protocol + "包到达");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCatchIOException(IOException e) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                PrintStream printStream = new PrintStream(bos);
                e.printStackTrace(printStream);
                logger.error(new String(bos.toByteArray()));
            }
        });
    }
}
