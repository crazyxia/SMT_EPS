package com.jimi.smt.eps.ghost.entity;

public class PartUsageItem {
    private String id;

    private String baseinfo;

    private String partnumber;

    private Integer pickupcount;

    private Integer totalpartsused;

    private Integer rejectparts;

    private Integer nopickup;

    private Integer errorparts;

    private Integer dislodgedparts;

    private Integer rescancount;

    private Float pickuprate;

    private Float errorrate;

    private Float dislodgedrate;

    private Float rejectrate;

    private Float successrate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getBaseinfo() {
        return baseinfo;
    }

    public void setBaseinfo(String baseinfo) {
        this.baseinfo = baseinfo == null ? null : baseinfo.trim();
    }

    public String getPartnumber() {
        return partnumber;
    }

    public void setPartnumber(String partnumber) {
        this.partnumber = partnumber == null ? null : partnumber.trim();
    }

    public Integer getPickupcount() {
        return pickupcount;
    }

    public void setPickupcount(Integer pickupcount) {
        this.pickupcount = pickupcount;
    }

    public Integer getTotalpartsused() {
        return totalpartsused;
    }

    public void setTotalpartsused(Integer totalpartsused) {
        this.totalpartsused = totalpartsused;
    }

    public Integer getRejectparts() {
        return rejectparts;
    }

    public void setRejectparts(Integer rejectparts) {
        this.rejectparts = rejectparts;
    }

    public Integer getNopickup() {
        return nopickup;
    }

    public void setNopickup(Integer nopickup) {
        this.nopickup = nopickup;
    }

    public Integer getErrorparts() {
        return errorparts;
    }

    public void setErrorparts(Integer errorparts) {
        this.errorparts = errorparts;
    }

    public Integer getDislodgedparts() {
        return dislodgedparts;
    }

    public void setDislodgedparts(Integer dislodgedparts) {
        this.dislodgedparts = dislodgedparts;
    }

    public Integer getRescancount() {
        return rescancount;
    }

    public void setRescancount(Integer rescancount) {
        this.rescancount = rescancount;
    }

    public Float getPickuprate() {
        return pickuprate;
    }

    public void setPickuprate(Float pickuprate) {
        this.pickuprate = pickuprate;
    }

    public Float getErrorrate() {
        return errorrate;
    }

    public void setErrorrate(Float errorrate) {
        this.errorrate = errorrate;
    }

    public Float getDislodgedrate() {
        return dislodgedrate;
    }

    public void setDislodgedrate(Float dislodgedrate) {
        this.dislodgedrate = dislodgedrate;
    }

    public Float getRejectrate() {
        return rejectrate;
    }

    public void setRejectrate(Float rejectrate) {
        this.rejectrate = rejectrate;
    }

    public Float getSuccessrate() {
        return successrate;
    }

    public void setSuccessrate(Float successrate) {
        this.successrate = successrate;
    }
}