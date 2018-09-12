package com.jimi.smt.eps_appclient.Beans;

import com.jimi.smt.eps_appclient.Unit.Constants;

public class ProgramItemVisit extends ProgramItemVisitKey {
    private String scanLineseat;

    private String scanMaterialNo;

    private Integer lastOperationType;

    private Integer storeIssueResult;

    private Integer feedResult;

    private Integer changeResult;

    private Integer checkResult;

    private Integer checkAllResult;

    private Integer firstCheckAllResult;

    public String getScanLineseat() {
        return scanLineseat;
    }

    public void setScanLineseat(String scanLineseat) {
        this.scanLineseat = scanLineseat == null ? null : scanLineseat.trim();
    }

    public String getScanMaterialNo() {
        return scanMaterialNo;
    }

    public void setScanMaterialNo(String scanMaterialNo) {
        this.scanMaterialNo = scanMaterialNo == null ? null : scanMaterialNo.trim();
    }

    public Integer getLastOperationType() {
        return lastOperationType;
    }

    public void setLastOperationType(Integer lastOperationType) {
        this.lastOperationType = lastOperationType;
    }

    public Integer getStoreIssueResult() {
        return storeIssueResult;
    }

    public void setStoreIssueResult(Integer storeIssueResult) {
        this.storeIssueResult = storeIssueResult;
    }


    public Integer getFeedResult() {
        return feedResult;
    }

    public void setFeedResult(Integer feedResult) {
        this.feedResult = feedResult;
    }


    public Integer getChangeResult() {
        return changeResult;
    }

    public void setChangeResult(Integer changeResult) {
        this.changeResult = changeResult;
    }


    public Integer getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(Integer checkResult) {
        this.checkResult = checkResult;
    }

    public Integer getCheckAllResult() {
        return checkAllResult;
    }

    public void setCheckAllResult(Integer checkAllResult) {
        this.checkAllResult = checkAllResult;
    }


    public Integer getFirstCheckAllResult() {
        return firstCheckAllResult;
    }

    public void setFirstCheckAllResult(Integer firstCheckAllResult) {
        this.firstCheckAllResult = firstCheckAllResult;
    }

    public static ProgramItemVisit getProgramItemVisit(int type, Material.MaterialBean bean) {
        ProgramItemVisit itemVisit = new ProgramItemVisit();
        itemVisit.setProgramId(bean.getProgramId());
        itemVisit.setLineseat(bean.getLineseat());
        itemVisit.setLastOperationType(type);
        itemVisit.setScanLineseat(bean.getScanlineseat());
        itemVisit.setScanMaterialNo(bean.getScanMaterial());
        int result = 0;
        if (bean.getResult().equalsIgnoreCase("PASS")) {
            result = 1;
        } else if (bean.getResult().equalsIgnoreCase("FAIL")) {
            result = 0;
        }
        switch (type) {
            case Constants.FEEDMATERIAL:
                itemVisit.setFeedResult(result);
                break;
            case Constants.CHANGEMATERIAL:
                itemVisit.setChangeResult(result);
                break;
            case Constants.CHECKMATERIAL:
                itemVisit.setCheckResult(result);
                break;
            case Constants.CHECKALLMATERIAL:
                itemVisit.setCheckAllResult(result);
                break;
            case Constants.STORE_ISSUE:
                itemVisit.setStoreIssueResult(result);
                break;
            case Constants.FIRST_CHECK_ALL:
                itemVisit.setFirstCheckAllResult(result);
                break;
        }

        return itemVisit;
    }

}