package com.jimi.smt.eps_appclient.Func;

import android.app.PendingIntent;
import android.content.Context;

import com.google.gson.Gson;
import com.jimi.smt.eps_appclient.Beans.Material;
import com.jimi.smt.eps_appclient.Beans.Operation;
import com.jimi.smt.eps_appclient.Beans.ProgramItemVisit;
import com.jimi.smt.eps_appclient.Interfaces.OkHttpInterface;
import com.jimi.smt.eps_appclient.Unit.GlobalData;
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
    private final GlobalData globalData;


    public HttpUtils(OkHttpInterface okHttpInterface, Context context) {
        this.mOkHttpInterface = okHttpInterface;
        globalData = (GlobalData) context.getApplicationContext();
    }

    public static HttpUtils getHttpUtils(OkHttpInterface okHttpInterface, GlobalData context) {
        if (httpUtils == null) {
            httpUtils = new HttpUtils(okHttpInterface, context);
        }
        return httpUtils;
    }

    private static final String operateUrl = "/program/operate";
    private static final String resetUrl = "/program/reset";
    private static final String lineExist = "/line/selectByLine";
    private static final String lines = "/line/selectAll";
    private static final String wokingOrders = "/program/selectWorkingProgram";
    private static final String userInfo = "/user/selectById";
    private static final String materials = "/program/selectProgramItem";
    private static final String addOperate = "/operation/add";
    private static final String addVisit = "/program/updateItemVisit";
    private static final String checkIsReset = "/program/checkIsReset";
    private static final String resetCheckAll = "/program/resetCheckAll";
    private static final String isAllDone = "/program/isAllDone";
    private static final String isChangeSucceed = "/program/isChangeSucceed";
    private static final String programID = "/program/getProgramId";
    private static final String isCheckAllTimeOut = "/program/isCheckAllTimeOut";


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
    public static final int CodeIsCheckAllTimeOut = 15;
    public static final int CodeIsAllDoneSTR = 16;

    private String errorResp_1 = "{\n" +
            "    \"code\": -1,\n" +
            "    \"msg\": \"此线号不存在进行中的工单\"\n" +
            "}";

    private String errorResp0 = "{\n" +
            "    \"code\": 0,\n" +
            "    \"msg\": \"查询失败，参数不存在\"\n" +
            "}";


    /**
     * 插入结果到visit表
     * 核料、全检
     *
     * @param materialItem
     * @param operatType   操作类型
     * @return 失败 failed_not_exist_item 、failed_not_exist ; 成功  succeed
     */
    public void operate(ArrayList<Integer> integers, Material.MaterialBean materialItem, int operatType, int condition) {
        Log.d(TAG, "operate - " + materialItem.getMaterialStr());
        String operationResult = materialItem.getResult();
        if (operationResult.equalsIgnoreCase("FAIL")) {
            operationResult = "0";
        } else if (operationResult.equalsIgnoreCase("PASS")) {
            operationResult = "1";
        }
        String failed_not_exist = "{\n" +
                "    \"result\": \"failed_not_exist\"\n" +
                "}";
        String notExist = "{\n" +
                "    \"result\": \"不存在此进行中的工单\"\n" +
                "}";

        Log.d(TAG, "operate - " + operateUrl);

        OkHttpUtils.post().url(globalData.getIp() + operateUrl)
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
                        Log.d(TAG, "operate - onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeOperate, new Object[]{integers, materialItem, condition}, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "operate - onResponse - " + response);
                        mOkHttpInterface.showHttpResponse(CodeOperate, new Object[]{integers, materialItem, condition}, response);
                    }
                });
    }

    /**
     * 重置visit表
     *
     * @param line
     * @param workOrder
     * @param boardType
     * @return 失败 failed_not_exist ; 成功 succeed
     */
    public void reset(String line, String workOrder, int boardType) {
        Log.d(TAG, "reset - " + "\n" + line + "\n" + workOrder + "\n" + boardType);
        OkHttpUtils.post().url(globalData.getIp() + resetUrl)
                .addParams("line", line)
                .addParams("workOrder", workOrder)
                .addParams("boardType", String.valueOf(boardType))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "reset - onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeReset, null, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "reset - onResponse - " + response);
                        mOkHttpInterface.showHttpResponse(CodeReset, null, response);
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
        Log.d(TAG, "getOrdersByLine - " + wokingOrders);
        Log.d(TAG, "getOrdersByLine - " + globalData.getIp());
        OkHttpUtils.post().url(globalData.getIp() + wokingOrders)
                .addParams("line", line)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "getOrdersByLine - onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeWokingOrders, line, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "getOrdersByLine - onResponse - " + response);
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
        Log.d(TAG, "getUserInfo - " + userInfo);
        OkHttpUtils.post().url(globalData.getIp() + userInfo)
                .addParams("id", uid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "getUserInfo - onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeUserInfo, uid, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "getUserInfo - onResponse - " + response);
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
//        Log.d(TAG, "getMaterials - " + globalData.getIp());
//        Log.d(TAG, "getMaterials - " + materials);
        OkHttpUtils.post().url(globalData.getIp() + materials)
                .addParams("programId", programId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "getMaterials - onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeMaterials, programId, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
//                        Log.d(TAG, "getMaterials - onResponse - " + response);
                        mOkHttpInterface.showHttpResponse(CodeMaterials, programId, response);
                    }
                });
    }

    /**
     * 获取某个工单的料号表,刷新判断更新时使用
     *
     * @param programId
     * @param equal     1,表示相同; 0,表示不相同
     */
    public void getMaterials(String programId, int equal) {
        Log.d(TAG, "getMaterials - " + programId + equal);
//        Log.d(TAG, "getMaterials - " + materials);
        OkHttpUtils.post().url(globalData.getIp() + materials)
                .addParams("programId", programId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "getMaterials - onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeMaterials, new Object[]{programId, equal}, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
//                        Log.d(TAG, "getMaterials - onResponse - " + response);
                        mOkHttpInterface.showHttpResponse(CodeMaterials, new Object[]{programId, equal}, response);
                    }
                });
    }

    /**
     * 添加操作日志
     *
     * @param operation
     */
    public void addOperation(Operation operation) {
        Log.d(TAG, "addOperation - " + addOperate);
        Gson gson = new Gson();
        String operationStr = gson.toJson(operation);
        Log.d(TAG, "addOperation - " + operationStr);
        OkHttpUtils.postString().url(globalData.getIp() + addOperate)
                .content(operationStr)
                .mediaType(MediaType.parse("application/json;charset=utf-8"))
                .addHeader("dataType", "json")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "addOperation - onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeAddOperate, operationStr, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "addOperation - onResponse - " + response);
                        mOkHttpInterface.showHttpResponse(CodeAddOperate, operationStr, response);
                    }
                });


    }


    /**
     * 更新visit表日志
     * 发料、上料、换料
     *
     * @param visit
     */
    public void updateVisit(Material.MaterialBean bean, ProgramItemVisit visit, int count) {
        Log.d(TAG, "updateVisit - " + addVisit);
        Gson gson = new Gson();
        String visitStr = gson.toJson(visit);
        Log.d(TAG, "updateVisit - " + visitStr);
        OkHttpUtils.postString().url(globalData.getIp() + addVisit)
                .content(visitStr)
                .mediaType(MediaType.parse("application/json;charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "updateVisit - onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeAddVisit, new Object[]{bean, visit, count}, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "updateVisit - onResponse - " + response);
                        mOkHttpInterface.showHttpResponse(CodeAddVisit, new Object[]{bean, visit, count}, response);
                    }
                });
    }

    /**
     * 更新visit表日志
     * 首检
     *
     * @param visit
     */
    public void updateVisit(Material.MaterialBean bean, ProgramItemVisit visit, int count, ArrayList<Integer> integers) {
        Log.d(TAG, "updateVisit - " + addVisit);
        Gson gson = new Gson();
        String visitStr = gson.toJson(visit);
        Log.d(TAG, "updateVisit - " + visitStr);
        OkHttpUtils.postString().url(globalData.getIp() + addVisit)
                .content(visitStr)
                .mediaType(MediaType.parse("application/json;charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "updateVisit - onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeAddVisit, new Object[]{bean, visit, count, integers}, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "updateVisit - onResponse - " + response);
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
        Log.d(TAG, "isReset - " + programId + "- type - " + type);
        Log.d(TAG, "isReset - " + checkIsReset);
        OkHttpUtils.post().url(globalData.getIp() + checkIsReset)
                .addParams("programId", programId)
                .addParams("type", String.valueOf(type))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "isReset - onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeCheckIsReset, new String[]{programId, String.valueOf(type)}, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "isReset - onResponse - " + response);
                        mOkHttpInterface.showHttpResponse(CodeCheckIsReset, new String[]{programId, String.valueOf(type)}, response);
                    }
                });
    }

    /**
     * 判断工单是否重置
     *
     * @param programId     工单id
     * @param type          操作类型
     * @param firstCheckRes 首检的结果
     * @onResponse 返回  1 表示重置;0 表示未重置
     */
    public void isReset(String programId, int type, int firstCheckRes) {
        Log.d(TAG, "isReset - " + programId + "- type - " + type);
        Log.d(TAG, "isReset - " + checkIsReset);
        OkHttpUtils.post().url(globalData.getIp() + checkIsReset)
                .addParams("programId", programId)
                .addParams("type", String.valueOf(type))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "isReset - onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeCheckIsReset, new String[]{programId, String.valueOf(type), String.valueOf(firstCheckRes)}, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "isReset - onResponse - " + response);
                        mOkHttpInterface.showHttpResponse(CodeCheckIsReset, new String[]{programId, String.valueOf(type), String.valueOf(firstCheckRes)}, response);
                    }
                });
    }


    /**
     * 全检完成后重置全检时间和全检结果
     *
     * @param programId
     * @onResponse 返回  -1 失败;其他结果 成功
     */
    public void resetCheckAllRR(String programId) {
        Log.d(TAG, "resetCheckAllRR - " + resetCheckAll);
        OkHttpUtils.post().url(globalData.getIp() + resetCheckAll)
                .addParams("programId", programId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "resetCheckAllRR - onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeResetCheckAll, programId, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "resetCheckAllRR - onResponse - " + response);
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
        Log.d(TAG, "checkAllDone - " + isAllDone);
        Log.d(TAG, "checkAllDone - " + programId);
        OkHttpUtils.post().url(globalData.getIp() + isAllDone)
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
     * @param type      0&1&3&4 表示同时查询4个结果
     *                  0:上料 1:换料 2:核料 3:全检 4:发料 5:首检
     *                  {
     *                  "code": 0,
     *                  "msg": "查询失败，参数不存在"
     *                  }
     *                  <p>
     *                  {
     *                  "code": -1,
     *                  "msg": "查询失败，此工单不在生产中"
     *                  }
     */
    // TODO: 2019/3/12
    public void checkAllDoneStr(String programId, String type) {
        Log.d(TAG, "checkAllDoneStr - " + isAllDone);
        Log.d(TAG, "checkAllDoneStr - programId - " + programId);
        Log.d(TAG, "checkAllDoneStr - type - " + type);
        OkHttpUtils.post().url(globalData.getIp() + isAllDone)
                .addParams("programId", programId)
                .addParams("type", String.valueOf(type))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "checkAllDoneStr - onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeIsAllDoneSTR, new Object[]{programId, type}, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "checkAllDoneStr - onResponse - " + response);
                        mOkHttpInterface.showHttpResponse(CodeIsAllDoneSTR, new Object[]{programId, type}, response);
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
        Log.d(TAG, "checkAllDone - " + isAllDone);
        Log.d(TAG, "checkAllDone - " + programId);
        OkHttpUtils.post().url(globalData.getIp() + isAllDone)
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
     * 返回某个工单是否全部完成某项操作的结果
     *
     * @param programId
     * @param type      0&1&3&4 表示同时查询4个结果
     *                  0:上料 1:换料 2:核料 3:全检 4:发料 5:首检
     */
    public void checkAllDoneStr(String programId, String type, ArrayList<Integer> integers, Material.MaterialBean bean, int condition) {
        Log.d(TAG, "checkAllDoneStr - " + isAllDone);
        Log.d(TAG, "checkAllDoneStr - programId - " + programId);
        Log.d(TAG, "checkAllDoneStr - type - " + type);
        OkHttpUtils.post().url(globalData.getIp() + isAllDone)
                .addParams("programId", programId)
                .addParams("type", String.valueOf(type))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "checkAllDoneStr - onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeIsAllDoneSTR, new Object[]{programId, type, integers, bean, condition}, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "checkAllDoneStr - onResponse - " + response);
                        mOkHttpInterface.showHttpResponse(CodeIsAllDoneSTR, new Object[]{programId, type, integers, bean, condition}, response);
                    }
                });
    }


    /**
     * 返回所有线号
     */
    public void getLines() {
        OkHttpUtils.post().url(globalData.getIp() + lines)
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
        Log.d(TAG, "getChangeSucceed - " + isChangeSucceed);
        OkHttpUtils.post().url(globalData.getIp() + isChangeSucceed)
                .addParams("programId", programId)
                .addParams("lineseat", lineseat)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "getChangeSucceed - onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeIsChangeSucceed, new String[]{programId, lineseat}, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "getChangeSucceed - onResponse - " + response);
                        mOkHttpInterface.showHttpResponse(CodeIsChangeSucceed, new String[]{programId, lineseat}, response);
                    }
                });
    }


    /**
     * 获取programId
     *
     * @param line      线号
     * @param workOrder 工单
     * @param boardType 版面类型
     */
    public void getProgramId(String line, String workOrder, int boardType) {
//        Log.d(TAG, "getProgramId - " + "  line : " + line + "  workOrder: " + workOrder + "  boardType: " + boardType);
//        Log.d(TAG, "getProgramId - " + programID);
        OkHttpUtils.post().url(globalData.getIp() + programID)
                .addParams("line", line)
                .addParams("workOrder", workOrder)
                .addParams("boardType", String.valueOf(boardType))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "getProgramId - onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeGetProgramId, new String[]{line, workOrder, String.valueOf(boardType)}, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
//                        Log.d(TAG, "getProgramId - onResponse - " + response);
                        mOkHttpInterface.showHttpResponse(CodeGetProgramId, new String[]{line, workOrder, String.valueOf(boardType)}, response);
                    }
                });
    }

    /**
     * 全检是否超时了
     *
     * @param line      线号
     * @param workOrder 工单
     * @param boardType 版面类型
     * @onResponse 1, 表示超时 ;  0,表示不超时
     */
    public void isCheckAllTimeOut(String line, String workOrder, int boardType) {
        Log.d(TAG, "isCheckAllTimeOut - " + "  line : " + line + "  workOrder: " + workOrder + "  boardType: " + boardType);
        OkHttpUtils.post().url(globalData.getIp() + isCheckAllTimeOut)
                .addParams("line", line)
                .addParams("workOrder", workOrder)
                .addParams("boardType", String.valueOf(boardType))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "isCheckAllTimeOut - onError - " + e.toString());
                        mOkHttpInterface.showHttpError(CodeIsCheckAllTimeOut, new String[]{line, workOrder, String.valueOf(boardType)}, e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "isCheckAllTimeOut - onResponse - " + response);
                        mOkHttpInterface.showHttpResponse(CodeIsCheckAllTimeOut, new String[]{line, workOrder, String.valueOf(boardType)}, response);
                    }
                });
    }


}
