package com.jimi.smt.eps_appclient.Beans;

/**
 * 类名: BaseMsg
 * 创建人: Liang GuoChang
 * 创建时间: 2018/8/27 9:49
 * 描述:
 * 版本号:
 * 修改记录:
 */

public class BaseMsg {

    /**
     * code : 0
     * msg : 工单不存在
     */

    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
