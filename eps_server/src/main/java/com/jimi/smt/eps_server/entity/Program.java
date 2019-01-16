package com.jimi.smt.eps_server.entity;

import java.util.Date;

public class Program {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column program.id
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    private String id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column program.file_name
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    private String fileName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column program.client
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    private String client;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column program.machine_name
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    private String machineName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column program.version
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    private String version;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column program.machine_config
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    private String machineConfig;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column program.program_no
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    private String programNo;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column program.line
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    private Integer line;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column program.effective_date
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    private String effectiveDate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column program.PCB_no
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    private String pcbNo;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column program.BOM
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    private String bom;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column program.program_name
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    private String programName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column program.auditor
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    private String auditor;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column program.create_time
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    private Date createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column program.work_order
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    private String workOrder;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column program.board_type
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    private Integer boardType;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column program.state
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    private Integer state;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column program.structure
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    private Integer structure;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column program.plan_product
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    private Integer planProduct;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column program.already_product
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    private Integer alreadyProduct;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column program.id
     *
     * @return the value of program.id
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public String getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column program.id
     *
     * @param id the value for program.id
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column program.file_name
     *
     * @return the value of program.file_name
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column program.file_name
     *
     * @param fileName the value for program.file_name
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public void setFileName(String fileName) {
        this.fileName = fileName == null ? null : fileName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column program.client
     *
     * @return the value of program.client
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public String getClient() {
        return client;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column program.client
     *
     * @param client the value for program.client
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public void setClient(String client) {
        this.client = client == null ? null : client.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column program.machine_name
     *
     * @return the value of program.machine_name
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public String getMachineName() {
        return machineName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column program.machine_name
     *
     * @param machineName the value for program.machine_name
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public void setMachineName(String machineName) {
        this.machineName = machineName == null ? null : machineName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column program.version
     *
     * @return the value of program.version
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public String getVersion() {
        return version;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column program.version
     *
     * @param version the value for program.version
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column program.machine_config
     *
     * @return the value of program.machine_config
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public String getMachineConfig() {
        return machineConfig;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column program.machine_config
     *
     * @param machineConfig the value for program.machine_config
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public void setMachineConfig(String machineConfig) {
        this.machineConfig = machineConfig == null ? null : machineConfig.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column program.program_no
     *
     * @return the value of program.program_no
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public String getProgramNo() {
        return programNo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column program.program_no
     *
     * @param programNo the value for program.program_no
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public void setProgramNo(String programNo) {
        this.programNo = programNo == null ? null : programNo.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column program.line
     *
     * @return the value of program.line
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public Integer getLine() {
        return line;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column program.line
     *
     * @param line the value for program.line
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public void setLine(Integer line) {
        this.line = line;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column program.effective_date
     *
     * @return the value of program.effective_date
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public String getEffectiveDate() {
        return effectiveDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column program.effective_date
     *
     * @param effectiveDate the value for program.effective_date
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate == null ? null : effectiveDate.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column program.PCB_no
     *
     * @return the value of program.PCB_no
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public String getPcbNo() {
        return pcbNo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column program.PCB_no
     *
     * @param pcbNo the value for program.PCB_no
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public void setPcbNo(String pcbNo) {
        this.pcbNo = pcbNo == null ? null : pcbNo.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column program.BOM
     *
     * @return the value of program.BOM
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public String getBom() {
        return bom;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column program.BOM
     *
     * @param bom the value for program.BOM
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public void setBom(String bom) {
        this.bom = bom == null ? null : bom.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column program.program_name
     *
     * @return the value of program.program_name
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public String getProgramName() {
        return programName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column program.program_name
     *
     * @param programName the value for program.program_name
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public void setProgramName(String programName) {
        this.programName = programName == null ? null : programName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column program.auditor
     *
     * @return the value of program.auditor
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public String getAuditor() {
        return auditor;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column program.auditor
     *
     * @param auditor the value for program.auditor
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public void setAuditor(String auditor) {
        this.auditor = auditor == null ? null : auditor.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column program.create_time
     *
     * @return the value of program.create_time
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column program.create_time
     *
     * @param createTime the value for program.create_time
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column program.work_order
     *
     * @return the value of program.work_order
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public String getWorkOrder() {
        return workOrder;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column program.work_order
     *
     * @param workOrder the value for program.work_order
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public void setWorkOrder(String workOrder) {
        this.workOrder = workOrder == null ? null : workOrder.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column program.board_type
     *
     * @return the value of program.board_type
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public Integer getBoardType() {
        return boardType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column program.board_type
     *
     * @param boardType the value for program.board_type
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public void setBoardType(Integer boardType) {
        this.boardType = boardType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column program.state
     *
     * @return the value of program.state
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public Integer getState() {
        return state;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column program.state
     *
     * @param state the value for program.state
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public void setState(Integer state) {
        this.state = state;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column program.structure
     *
     * @return the value of program.structure
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public Integer getStructure() {
        return structure;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column program.structure
     *
     * @param structure the value for program.structure
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public void setStructure(Integer structure) {
        this.structure = structure;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column program.plan_product
     *
     * @return the value of program.plan_product
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public Integer getPlanProduct() {
        return planProduct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column program.plan_product
     *
     * @param planProduct the value for program.plan_product
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public void setPlanProduct(Integer planProduct) {
        this.planProduct = planProduct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column program.already_product
     *
     * @return the value of program.already_product
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public Integer getAlreadyProduct() {
        return alreadyProduct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column program.already_product
     *
     * @param alreadyProduct the value for program.already_product
     *
     * @mbg.generated Tue Sep 18 17:12:42 CST 2018
     */
    public void setAlreadyProduct(Integer alreadyProduct) {
        this.alreadyProduct = alreadyProduct;
    }
}