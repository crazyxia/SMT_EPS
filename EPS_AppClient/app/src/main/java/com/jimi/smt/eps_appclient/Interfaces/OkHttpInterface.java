package com.jimi.smt.eps_appclient.Interfaces;

/**
 * 类名: ${name}
 * 创建人: Liang GuoChang
 * 创建时间: ${date} ${time}
 * 描述:
 * 版本号:
 * 修改记录:
 */

public interface OkHttpInterface {
    void showHttpResponse(int code, Object request, String response);

    void showHttpError(int code, Object request, String s);
}
