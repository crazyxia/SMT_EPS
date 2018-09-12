package com.jimi.smt.eps_appclient.Beans;

import java.util.List;

/**
 * 类名:Program
 * 创建人: Liang GuoChang
 * 创建时间: 2018/8/27 16:21
 * 描述:
 * 版本号:
 * 修改记录:
 */

public class Program extends BaseMsg{

    private List<ProgramBean> data;

    public List<ProgramBean> getData() {
        return data;
    }

    public void setData(List<ProgramBean> data) {
        this.data = data;
    }

    public static class ProgramBean {
        /**
         * id : 1daadd12ff284fe7bbf18457943c59d4
         * line : 301
         * workOrder : GCE201807021-2-BOT
         * boardType : 2
         * 添加一个字段
         *checked : false
         */

        private String id;
        private String line;
        private String workOrder;
        private int boardType;
        private boolean checked;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public String programToString() {

            return "programId:" + id + "\n"
                    + "workOrder:" + workOrder + "\n"
                    + "boardType:" + boardType + "\n"
                    + "line:" + line + "\n"
                    + "checked:" + checked;
        }
    }
}
