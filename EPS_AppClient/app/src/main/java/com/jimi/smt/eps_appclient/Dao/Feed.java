package com.jimi.smt.eps_appclient.Dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 类名:Feed
 * 创建人:Liang GuoChang
 * 创建时间:2018/3/27 10:06
 * 描述:
 * 版本号:
 * 修改记录:
 */
@Entity
public class Feed {

    @Id(autoincrement = true)
    private Long feed_id;
    private String programId;//工单Id
    private String order;//工单号
    private String operator;//操作员
    private int board_type;//版面类型
    private String line;//线号
    private String OrgLineSeat;    //当前站位表站位
    private String OrgMaterial;    //当前站位表料号
    private String specitification; //型号
    private String position;        //位置
    private int quantity;           //数量
    private String ScanLineSeat;   //扫描的站位
    private String ScanMaterial;   //扫描的料号
    private String Result;         //结果
    private String Remark;         //失败原因
    private int SerialNo;           //流水号
    private boolean Alternative;    //是否属于替料
    @Generated(hash = 111684577)
    public Feed(Long feed_id, String programId, String order, String operator,
            int board_type, String line, String OrgLineSeat, String OrgMaterial,
            String specitification, String position, int quantity,
            String ScanLineSeat, String ScanMaterial, String Result, String Remark,
            int SerialNo, boolean Alternative) {
        this.feed_id = feed_id;
        this.programId = programId;
        this.order = order;
        this.operator = operator;
        this.board_type = board_type;
        this.line = line;
        this.OrgLineSeat = OrgLineSeat;
        this.OrgMaterial = OrgMaterial;
        this.specitification = specitification;
        this.position = position;
        this.quantity = quantity;
        this.ScanLineSeat = ScanLineSeat;
        this.ScanMaterial = ScanMaterial;
        this.Result = Result;
        this.Remark = Remark;
        this.SerialNo = SerialNo;
        this.Alternative = Alternative;
    }
    @Generated(hash = 1810414124)
    public Feed() {
    }
    public Long getFeed_id() {
        return this.feed_id;
    }
    public void setFeed_id(Long feed_id) {
        this.feed_id = feed_id;
    }
    public String getProgramId() {
        return this.programId;
    }
    public void setProgramId(String programId) {
        this.programId = programId;
    }
    public String getOrder() {
        return this.order;
    }
    public void setOrder(String order) {
        this.order = order;
    }
    public String getOperator() {
        return this.operator;
    }
    public void setOperator(String operator) {
        this.operator = operator;
    }
    public int getBoard_type() {
        return this.board_type;
    }
    public void setBoard_type(int board_type) {
        this.board_type = board_type;
    }
    public String getLine() {
        return this.line;
    }
    public void setLine(String line) {
        this.line = line;
    }
    public String getOrgLineSeat() {
        return this.OrgLineSeat;
    }
    public void setOrgLineSeat(String OrgLineSeat) {
        this.OrgLineSeat = OrgLineSeat;
    }
    public String getOrgMaterial() {
        return this.OrgMaterial;
    }
    public void setOrgMaterial(String OrgMaterial) {
        this.OrgMaterial = OrgMaterial;
    }
    public String getSpecitification() {
        return this.specitification;
    }
    public void setSpecitification(String specitification) {
        this.specitification = specitification;
    }
    public String getPosition() {
        return this.position;
    }
    public void setPosition(String position) {
        this.position = position;
    }
    public int getQuantity() {
        return this.quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public String getScanLineSeat() {
        return this.ScanLineSeat;
    }
    public void setScanLineSeat(String ScanLineSeat) {
        this.ScanLineSeat = ScanLineSeat;
    }
    public String getScanMaterial() {
        return this.ScanMaterial;
    }
    public void setScanMaterial(String ScanMaterial) {
        this.ScanMaterial = ScanMaterial;
    }
    public String getResult() {
        return this.Result;
    }
    public void setResult(String Result) {
        this.Result = Result;
    }
    public String getRemark() {
        return this.Remark;
    }
    public void setRemark(String Remark) {
        this.Remark = Remark;
    }
    public int getSerialNo() {
        return this.SerialNo;
    }
    public void setSerialNo(int SerialNo) {
        this.SerialNo = SerialNo;
    }
    public boolean getAlternative() {
        return this.Alternative;
    }
    public void setAlternative(boolean Alternative) {
        this.Alternative = Alternative;
    }

}
