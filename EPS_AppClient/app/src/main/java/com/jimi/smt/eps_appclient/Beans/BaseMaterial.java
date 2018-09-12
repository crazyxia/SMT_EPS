package com.jimi.smt.eps_appclient.Beans;

import android.support.annotation.NonNull;

/**
 * 类名: BaseMaterial
 * 创建人: Liang GuoChang
 * 创建时间: 2018/8/29 11:58
 * 描述:
 * 版本号:
 * 修改记录:
 */

public abstract class BaseMaterial implements Comparable<Material.MaterialBean>{

    /**
     * programId : 2454
     * line : 301
     * workOrder : 2344
     * boardType : 1
     * lineseat : 01-01
     * scanlineseat : 01-01
     * result : PASS
     * remark : fgh
     */

    private String programId;
    private String line;
    private String workOrder;
    private int boardType;
    private String lineseat;
    private String scanlineseat;
    private String result;
    private String remark;

    public BaseMaterial(String programId, String line, String workOrder, int boardType, String lineseat,
                        String scanlineseat, String result, String remark) {
        this.programId = programId;
        this.line = line;
        this.workOrder = workOrder;
        this.boardType = boardType;
        this.lineseat = lineseat;
        this.scanlineseat = scanlineseat;
        this.result = result;
        this.remark = remark;
    }

    public BaseMaterial() {

    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getWorkOrder() {
        return workOrder;
    }

    public void setWorkOrder(String workOrder) {
        this.workOrder = workOrder;
    }

    public int getBoardType() {
        return boardType;
    }

    public void setBoardType(int boardType) {
        this.boardType = boardType;
    }

    public String getLineseat() {
        return lineseat;
    }

    public void setLineseat(String lineseat) {
        this.lineseat = lineseat;
    }

    public String getScanlineseat() {
        return scanlineseat;
    }

    public void setScanlineseat(String scanlineseat) {
        this.scanlineseat = scanlineseat;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public int hashCode() {
        return (workOrder + line + boardType + lineseat).hashCode();
    }

    @Override
    public int compareTo(@NonNull Material.MaterialBean materialBean) {
        return 1;
    }

}
