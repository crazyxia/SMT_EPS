package com.jimi.smt.eps_appclient.Beans;

import com.google.gson.Gson;

/**
 * 类名: Operation
 * 创建人: Liang GuoChang
 * 创建时间: 2018/8/28 16:05
 * 描述:
 * 版本号:
 * 修改记录:
 */

public class Operation extends BaseMaterial {
    /**
     * operator : A9990
     * type : 1
     * materialNo : 342344
     * oldMaterialNo : 233455
     */

    private String operator;
    private int type;
    private String materialNo;
    private String oldMaterialNo;

    public Operation(String programId, String line, String workOrder, int boardType, String lineseat, String scanlineseat, String result, String remark) {
        super(programId, line, workOrder, boardType, lineseat, scanlineseat, result, remark);
    }

    public Operation() {
        super();
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMaterialNo() {
        return materialNo;
    }

    public void setMaterialNo(String materialNo) {
        this.materialNo = materialNo;
    }

    public String getOldMaterialNo() {
        return oldMaterialNo;
    }

    public void setOldMaterialNo(String oldMaterialNo) {
        this.oldMaterialNo = oldMaterialNo;
    }

    public String operationToString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    /**
     * 获取操作日志对象
     * @param operator 操作员
     * @param type     操作类型
     * @param materialBean 操作内容
     * @return Operation
     */
    public static Operation getOperation(String operator, int type, Material.MaterialBean materialBean) {
        Operation operation = new Operation();
        operation.setOperator(operator);
        operation.setType(type);
        operation.setResult(materialBean.getResult());
        operation.setLineseat(materialBean.getLineseat());
        operation.setMaterialNo(materialBean.getScanMaterial());
        operation.setOldMaterialNo(materialBean.getMaterialNo());
        operation.setScanlineseat(materialBean.getScanlineseat());
        operation.setRemark(materialBean.getRemark());
        operation.setProgramId(materialBean.getProgramId());
        operation.setLine(materialBean.getLine());
        operation.setWorkOrder(materialBean.getWorkOrder());
        operation.setBoardType(materialBean.getBoardType());
        return operation;
    }
}
