package com.jimi.smt.eps_appclient.Func;

import com.google.gson.Gson;
import com.jimi.smt.eps_appclient.Beans.Material;
import com.jimi.smt.eps_appclient.Beans.Operation;
import com.jimi.smt.eps_appclient.Beans.ProgramItemVisit;
import com.jimi.smt.eps_appclient.Unit.Constants;
import com.jimi.smt.eps_appclient.Interfaces.OkHttpInterface;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * 类名:HttpUtils
 * 创建人:Liang GuoChang
 * 创建时间:2018/4/27 20:06
 * 描述:发起http请求
 * 版本号:
 * 修改记录:
 */

public class HttpUtils {

    private static final String TAG = "HttpUtils";

    private static HttpUtils httpUtils = null;
    private OkHttpInterface mOkHttpInterface;

    public HttpUtils(OkHttpInterface okHttpInterface) {
        this.mOkHttpInterface = okHttpInterface;
    }

    public static HttpUtils getHttpUtils(OkHttpInterface okHttpInterface) {
        if (httpUtils == null) {
            httpUtils = new HttpUtils(okHttpInterface);
        }
        return httpUtils;
    }

    private static final String operateUrl = Constants.urlBase + "/program/operate";
    private static final String resetUrl = Constants.urlBase + "/program/reset";
    private static final String lineExist = Constants.urlBase + "/line/selectByLine";
    private static final String lines = Constants.urlBase + "/line/selectAll";
    private static final String wokingOrders = Constants.urlBase + "/program/selectWorkingProgram";
    private static final String userInfo = Constants.urlBase + "/user/selectById";
    private static final String materials = Constants.urlBase + "/program/selectProgramItem";
    private static final String addOperate = Constants.urlBase + "/operation/add";
    private static final String addVisit = Constants.urlBase + "/program/updateItemVisit";
    private static final String checkIsReset = Constants.urlBase + "/program/checkIsReset";
    private static final String resetCheckAll = Constants.urlBase + "/program/resetCheckAll";
    private static final String isAllDone = Constants.urlBase + "/program/isAllDone";
    private static final String isChangeSucceed = Constants.urlBase + "/program/isChangeSucceed";
    private static final String programID = Constants.urlBase + "/program/getProgramId";

    public static final int CodeOperate = 1;
    public static final int CodeReset = 2;
    public static final int CodeLineExist = 3;
    public static final int CodeLines = 4;
    public static final int CodeWokingOrders = 5;
    public static final int CodeUserInfo = 6;
    public static final int CodeMaterials = 7;
    public static final int CodeAddOperate = 8;
    public static final int CodeAddVisit = 9;
    public static final int CodeCheckIsReset = 10;
    public static final int CodeResetCheckAll = 11;
    public static final int CodeIsAllDone = 12;
    public static final int CodeIsChangeSucceed = 13;
    public static final int CodeGetProgramId = 14;


    /**
     * 插入结果到visit表
     *
     * @param materialItem
     * @param operatType   操作类型
     * @return 失败 failed_not_exist_item 、failed_not_exist ; 成功  succeed
     */
    public int operate(ArrayList<Integer> integers, Material.MaterialBean materialItem, int operatType, int condition) {
        Log.d(TAG, "operate - " + materialItem.getMaterialStr());
        String operationResult = materialItem.getResult();
        if (operationResult.equalsIgnoreCase("FAIL")) {
            operationResult = "0";
        } else if (operationResult.equalsIgnoreCase("PASS")) {
            operationResult = "1";
        }

        int operateResult = 0;
        OkHttpUtils.post().url(operateUrl)
                .addParams("line", materialItem.getLine())
                .addParams("workOrder", materialItem.getWorkOrder())
                .addParams("boardType", String.valueOf(materialItem.getBoardType()))
                .addParams("type", String.valueOf(operatType))
                .addParams("lineseat", materialItem.getLineseat())
                .addParams("scanLineseat", materialItem.getScanlineseat())
                .addParams("scanMaterialNo", materialItem.getScanMaterial())
                .addParams("operationResult", operationResult)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeOperate, new Object[]{integers, materialItem, condition}, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse - " + response);
                        mOkHttpInterface.showHttpResponse(CodeOperate, new Object[]{integers, materialItem, condition}, response);
                    }
                });
        return operateResult;
    }

    /**
     * 重置visit表
     *
     * @param line
     * @param workOrder
     * @param boardType
     * @return 失败 failed_not_exist ; 成功 succeed
     */
    public int reset(String line, String workOrder, int boardType) {
        Log.d(TAG, "reset - " + "\n" + line + "\n" + workOrder + "\n" + boardType);
        int resetResult = 0;
        OkHttpUtils.post().url(resetUrl)
                .addParams("line", line)
                .addParams("workOrder", workOrder)
                .addParams("boardType", String.valueOf(boardType))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeReset, null, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse - " + response);
                        mOkHttpInterface.showHttpResponse(CodeReset, null, response);
                    }
                });
        return resetResult;
    }


    /**
     * 线号是否存在
     *
     * @param line
     */
    public void lineIsExist(String line) {
        OkHttpUtils.post().url("http://10.10.11.180:8080/orders.json")//lineExist
                .addParams("line", line)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeLineExist, line, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse - " + response);
                        mOkHttpInterface.showHttpResponse(CodeLineExist, line, response);
                    }
                });
    }


    /**
     * 获取该线号下正在进行的工单
     *
     * @param line
     */
    public void getOrdersByLine(String line) {
        Log.d(TAG, "getOrdersByLine - " + line);
        OkHttpUtils.post().url(wokingOrders)//wokingOrders http://10.10.11.180:8080/orders.json
                .addParams("line", line)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeWokingOrders, line, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse - " + response);
                        mOkHttpInterface.showHttpResponse(CodeWokingOrders, line, response);
                    }
                });
    }


    /**
     * 判断用户是否存在同时返回用户信息
     *
     * @param uid
     */
    public void getUserInfo(String uid) {
        Log.d(TAG, "getUserInfo - " + uid);
        OkHttpUtils.post().url(userInfo)//userInfo http://10.10.11.180:8080/A2098.json
                .addParams("id", uid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeUserInfo, uid, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse - " + response);
                        mOkHttpInterface.showHttpResponse(CodeUserInfo, uid, response);
                    }
                });
    }


    /**
     * 获取某个工单的料号表
     *
     * @param programId
     */
    public void getMaterials(String programId) {
        Log.d(TAG, "getMaterials - " + programId);
        OkHttpUtils.post().url(materials)//materials http://10.10.11.180:8080/material.json
                .addParams("programId", programId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeMaterials, programId, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse - " + response);
                        mOkHttpInterface.showHttpResponse(CodeMaterials, programId, response);
                    }
                });
    }

    /**
     * 添加操作日志
     *
     * @param operation
     */
    public void addOperation(Operation operation) {
        Gson gson = new Gson();
        String operationStr = gson.toJson(operation);
        Log.d(TAG, "addOperation - " + operationStr);
        OkHttpUtils.postString().url(addOperate)
                .content(operationStr)
                .mediaType(MediaType.parse("application/json;charset=utf-8"))
                .addHeader("dataType", "json")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeAddOperate, operationStr, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse - " + response);
                        mOkHttpInterface.showHttpResponse(CodeAddOperate, operationStr, response);
                    }
                });


    }


    /**
     * 更新visit表日志
     *
     * @param visit
     */
    public void updateVisit(Material.MaterialBean bean, ProgramItemVisit visit, int count) {
        Log.d(TAG, "updateVisit");
        Gson gson = new Gson();
        String visitStr = gson.toJson(visit);
        Log.d(TAG, "updateVisit - " + visitStr);
        OkHttpUtils.postString().url(addVisit)//http://10.10.11.180:8080/material.json addVisit
                .content(visitStr)
                .mediaType(MediaType.parse("application/json;charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeAddVisit, new Object[]{bean, visit, count}, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse - " + response);
//                        mOkHttpInterface.showHttpResponse(CodeAddVisit, new Object[]{bean, visit}, "{\"code\":0,\"msg\":\"更新失败\"}");
                        mOkHttpInterface.showHttpResponse(CodeAddVisit, new Object[]{bean, visit, count}, response);
                    }
                });
    }

    /**
     * 更新visit表日志
     *
     * @param visit
     */
    public void updateVisit(Material.MaterialBean bean, ProgramItemVisit visit, int count, ArrayList<Integer> integers) {
        Log.d(TAG, "updateVisit");
        Gson gson = new Gson();
        String visitStr = gson.toJson(visit);
        Log.d(TAG, "updateVisit - " + visitStr);
        OkHttpUtils.postString().url(addVisit)//http://10.10.11.180:8080/material.json addVisit
                .content(visitStr)
                .mediaType(MediaType.parse("application/json;charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeAddVisit, new Object[]{bean, visit, count, integers}, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse - " + response);
//                        mOkHttpInterface.showHttpResponse(CodeAddVisit, new Object[]{bean, visit}, "{\"code\":0,\"msg\":\"更新失败\"}");
                        mOkHttpInterface.showHttpResponse(CodeAddVisit, new Object[]{bean, visit, count, integers}, response);
                    }
                });
    }


    /**
     * 判断工单是否重置
     *
     * @param programId 工单id
     * @param type      操作类型
     * @onResponse 返回  1 表示重置;0 表示未重置
     */
    public void isReset(String programId, int type) {
        Log.d(TAG, "isReset - " + programId);
        OkHttpUtils.post().url(checkIsReset)
                .addParams("programId", programId)
                .addParams("type", String.valueOf(type))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeCheckIsReset, new String[]{programId, String.valueOf(type)}, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse - " + response);
                        mOkHttpInterface.showHttpResponse(CodeCheckIsReset, new String[]{programId, String.valueOf(type)}, response);
                    }
                });
    }


    /**
     * 首检完成后重置全检时间和全检结果
     *
     * @param programId
     * @onResponse 返回  -1 失败;其他结果 成功
     */
    public void resetCheckAllRR(String programId) {
        OkHttpUtils.post().url(resetCheckAll)
                .addParams("programId", programId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeResetCheckAll, programId, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse - " + response);
                        mOkHttpInterface.showHttpResponse(CodeResetCheckAll, programId, response);
                    }
                });
    }


    /**
     * 返回某个工单是否全部完成某项操作的结果
     *
     * @param programId
     * @param type
     */
    public void checkAllDone(String programId, int type) {
        Log.d(TAG, "checkAllDone - " + programId);
        OkHttpUtils.post().url(isAllDone)
                .addParams("programId", programId)
                .addParams("type", String.valueOf(type))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeIsAllDone, new Object[]{programId, type}, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse - " + response);
                        mOkHttpInterface.showHttpResponse(CodeIsAllDone, new Object[]{programId, type}, response);
                    }
                });
    }

    /**
     * 返回某个工单是否全部完成某项操作的结果
     *
     * @param programId
     * @param type
     */
    public void checkAllDone(String programId, int type, ArrayList<Integer> integers, Material.MaterialBean bean, int condition) {
        Log.d(TAG, "checkAllDone - " + programId);
        OkHttpUtils.post().url(isAllDone)
                .addParams("programId", programId)
                .addParams("type", String.valueOf(type))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeIsAllDone, new Object[]{programId, type, integers, bean, condition}, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse - " + response);
                        mOkHttpInterface.showHttpResponse(CodeIsAllDone, new Object[]{programId, type, integers, bean, condition}, response);
                    }
                });
    }

    /**
     * 返回所有线号
     */
    public void getLines() {
        OkHttpUtils.post().url(lines)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeLines, null, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse - " + response);
                        mOkHttpInterface.showHttpResponse(CodeLines, null, response);
                    }
                });
    }


    /**
     * 核料时判断某个站位是否换料成功
     *
     * @param programId
     * @param lineseat
     * @onResponse 返回  失败; 成功
     */
    public void getChangeSucceed(String programId, String lineseat) {
        Log.d(TAG, "getChangeSucceed - " + "programId : " + programId + "lineseat: " + lineseat);
        OkHttpUtils.post().url(isChangeSucceed)
                .addParams("programId", programId)
                .addParams("lineseat", lineseat)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeIsChangeSucceed, new String[]{programId, lineseat}, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse - " + response);
                        mOkHttpInterface.showHttpResponse(CodeIsChangeSucceed, new String[]{programId, lineseat}, response);
                    }
                });
    }


    /**
     * 获取programId
     * @param line 线号
     * @param workOrder 工单
     * @param boardType 版面类型
     */
    public void getProgramId(String line, String workOrder, int boardType) {
        Log.d(TAG, "getProgramId - " + "  line : " + line + "  workOrder: " + workOrder + "  boardType: " + boardType);
        OkHttpUtils.post().url(programID)
                .addParams("line", line)
                .addParams("workOrder", workOrder)
                .addParams("boardType", String.valueOf(boardType))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeGetProgramId, new String[]{line, workOrder, String.valueOf(boardType)}, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse - " + response);
                        mOkHttpInterface.showHttpResponse(CodeGetProgramId, new String[]{line, workOrder, String.valueOf(boardType)}, response);
                    }
                });
    }


}
