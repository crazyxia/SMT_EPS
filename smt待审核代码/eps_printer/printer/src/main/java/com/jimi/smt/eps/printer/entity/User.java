package com.jimi.smt.eps.printer.entity;

import java.util.Date;

public class User {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user.id
     *
     * @mbggenerated Tue Feb 06 14:43:30 CST 2018
     */
    private String id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user.enabled
     *
     * @mbggenerated Tue Feb 06 14:43:30 CST 2018
     */
    private Boolean enabled;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user.name
     *
     * @mbggenerated Tue Feb 06 14:43:30 CST 2018
     */
    private String name;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user.type
     *
     * @mbggenerated Tue Feb 06 14:43:30 CST 2018
     */
    private Integer type;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user.password
     *
     * @mbggenerated Tue Feb 06 14:43:30 CST 2018
     */
    private String password;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user.create_time
     *
     * @mbggenerated Tue Feb 06 14:43:30 CST 2018
     */
    private Date createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user.class_type
     *
     * @mbggenerated Tue Feb 06 14:43:30 CST 2018
     */
    private Integer classType;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user.id
     *
     * @return the value of user.id
     *
     * @mbggenerated Tue Feb 06 14:43:30 CST 2018
     */
    public String getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user.id
     *
     * @param id the value for user.id
     *
     * @mbggenerated Tue Feb 06 14:43:30 CST 2018
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user.enabled
     *
     * @return the value of user.enabled
     *
     * @mbggenerated Tue Feb 06 14:43:30 CST 2018
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user.enabled
     *
     * @param enabled the value for user.enabled
     *
     * @mbggenerated Tue Feb 06 14:43:30 CST 2018
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user.name
     *
     * @return the value of user.name
     *
     * @mbggenerated Tue Feb 06 14:43:30 CST 2018
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user.name
     *
     * @param name the value for user.name
     *
     * @mbggenerated Tue Feb 06 14:43:30 CST 2018
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user.type
     *
     * @return the value of user.type
     *
     * @mbggenerated Tue Feb 06 14:43:30 CST 2018
     */
    public Integer getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user.type
     *
     * @param type the value for user.type
     *
     * @mbggenerated Tue Feb 06 14:43:30 CST 2018
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user.password
     *
     * @return the value of user.password
     *
     * @mbggenerated Tue Feb 06 14:43:30 CST 2018
     */
    public String getPassword() {
        return password;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user.password
     *
     * @param password the value for user.password
     *
     * @mbggenerated Tue Feb 06 14:43:30 CST 2018
     */
    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user.create_time
     *
     * @return the value of user.create_time
     *
     * @mbggenerated Tue Feb 06 14:43:30 CST 2018
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user.create_time
     *
     * @param createTime the value for user.create_time
     *
     * @mbggenerated Tue Feb 06 14:43:30 CST 2018
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user.class_type
     *
     * @return the value of user.class_type
     *
     * @mbggenerated Tue Feb 06 14:43:30 CST 2018
     */
    public Integer getClassType() {
        return classType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user.class_type
     *
     * @param classType the value for user.class_type
     *
     * @mbggenerated Tue Feb 06 14:43:30 CST 2018
     */
    public void setClassType(Integer classType) {
        this.classType = classType;
    }
}