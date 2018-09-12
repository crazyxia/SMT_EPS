package com.jimi.smt.eps_appclient.Beans;

/**
 * 类名:
 * 创建人: Liang GuoChang
 * 创建时间: 2018/8/27 20:48
 * 描述:
 * 版本号:
 * 修改记录:
 */

public class Operator extends BaseMsg{

    /**
     * data : {"id":"A008","enabled":true,"name":"曹仪辉","type":1,"createTime":1508987711000,"classType":0}
     */

    private OperatorBean data;

    public OperatorBean getData() {
        return data;
    }

    public void setData(OperatorBean data) {
        this.data = data;
    }

    public static class OperatorBean {
        /**
         * id : A008
         * enabled : true
         * name : 曹仪辉
         * type : 1
         * createTime : 1508987711000
         * classType : 0
         */

        private String id;
        private boolean enabled;
        private String name;
        private int type;
        private long createTime;
        private int classType;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public int getClassType() {
            return classType;
        }

        public void setClassType(int classType) {
            this.classType = classType;
        }
    }
}
