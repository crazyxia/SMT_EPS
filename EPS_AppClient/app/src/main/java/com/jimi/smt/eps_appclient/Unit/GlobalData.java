package com.jimi.smt.eps_appclient.Unit;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.jimi.smt.eps_appclient.Beans.Material;
import com.jimi.smt.eps_appclient.Dao.GreenDaoManager;
import com.jimi.smt.eps_appclient.Func.Log;
import com.jimi.smt.eps_appclient.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名:GlobalData
 * 创建人:Connie
 * 创建时间:2017-9-25
 * 描述:全局变量
 * 版本号:V1.0
 * 修改记录:
 */
public class GlobalData extends Application {
    private static final String TAG = "GlobalData";
    @SuppressLint("StaticFieldLeak")
    protected static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        setIp(Constants.urlBase);
        //greenDao全局配置,只希望有一个数据库操作对象
        GreenDaoManager.getInstance();
    }

    public static Context getAppContext() {
        return context;
    }

    //连接IP
    private String ip = "";

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    //料号表
    private List<Material.MaterialBean> materialBeans = new ArrayList<>();

    public List<Material.MaterialBean> getMaterialBeans() {
        return materialBeans;
    }

    public void setMaterialBeans(List<Material.MaterialBean> materialBeans) {
        this.materialBeans = materialBeans;
    }

    //操作员
    private String Operator = "";

    public String getOperator() {
        return Operator;
    }

    public void setOperator(String operator) {
        Operator = operator;
    }

    //操作员类型(0:仓库操作员;1:厂线操作员;2:IPQC;3:管理员)
    private int UserType;

    public int getUserType() {
        return UserType;
    }

    public void setUserType(int userType) {
        UserType = userType;
    }

    //操作类型
    private int OperType;

    public int getOperType() {
        return OperType;
    }

    public void setOperType(int operType) {
        OperType = operType;
    }

    //管理员所做的操作类型
    private int adminOperType;

    public int getAdminOperType() {
        return adminOperType;
    }

    public void setAdminOperType(int adminOperType) {
        this.adminOperType = adminOperType;
    }

    //更新显示日志类型
    private int updateType;

    public int getUpdateType() {
        return updateType;
    }

    public void setUpdateType(int updateType) {
        this.updateType = updateType;
    }

    //工单的programId
    private String programId;

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    private String wareProgramId;

    public String getWareProgramId() {
        return wareProgramId;
    }

    public void setWareProgramId(String wareProgramId) {
        this.wareProgramId = wareProgramId;
    }

    private String factoryProgramId;

    public String getFactoryProgramId() {
        return factoryProgramId;
    }

    public void setFactoryProgramId(String factoryProgramId) {
        this.factoryProgramId = factoryProgramId;
    }

    private String qcProgramId;

    public String getQcProgramId() {
        return qcProgramId;
    }

    public void setQcProgramId(String qcProgramId) {
        this.qcProgramId = qcProgramId;
    }

    //操作线号 301 - 308
    private String line;

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    //线号的负值 -127 - -120
    private int minusLine;

    public int getMinusLine() {
        return minusLine;
    }

    public void setMinusLine(String minusLine) {
        this.minusLine = Integer.valueOf(minusLine.substring(minusLine.length() - 1)) - 128;
    }

    //工单号
    private String work_order;

    public String getWork_order() {
        return work_order;
    }

    public void setWork_order(String work_order) {
        this.work_order = work_order;
    }

    //板面类型
    private int board_type;

    public int getBoard_type() {
        return board_type;
    }

    public void setBoard_type(int board_type) {
        this.board_type = board_type;
    }

    //apk下载路径
    private String apkDownloadDir;

    public String getApkDownloadDir() {
        return apkDownloadDir;
    }

    public void setApkDownloadDir(String apkDownloadDir) {
        this.apkDownloadDir = apkDownloadDir;
    }
}
