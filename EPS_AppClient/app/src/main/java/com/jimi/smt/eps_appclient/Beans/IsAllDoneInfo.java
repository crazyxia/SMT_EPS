package com.jimi.smt.eps_appclient.Beans;

/**
 * 类名: IsAllDoneInfo
 * 创建人: Liang GuoChang
 * 创建时间: 2019/1/28 11:59
 * 描述:
 * 版本号:
 * 修改记录:
 */

public class IsAllDoneInfo extends BaseMsg{
    /**
     * data : {"store":"1","feed":"1","change":"0","check":"","checkAll":"0","firstCheckAll":""}
     */

    private AllDoneInfoBean data;

    public AllDoneInfoBean getData() {
        return data;
    }

    public void setData(AllDoneInfoBean data) {
        this.data = data;
    }

    public static class AllDoneInfoBean {
        /**
         * store : 1
         * feed : 1
         * change : 0
         * check :
         * checkAll : 0
         * firstCheckAll :
         */

        private String store;
        private String feed;
        private String change;
        private String check;
        private String checkAll;
        private String firstCheckAll;

        public String getStore() {
            return store;
        }

        public void setStore(String store) {
            this.store = store;
        }

        public String getFeed() {
            return feed;
        }

        public void setFeed(String feed) {
            this.feed = feed;
        }

        public String getChange() {
            return change;
        }

        public void setChange(String change) {
            this.change = change;
        }

        public String getCheck() {
            return check;
        }

        public void setCheck(String check) {
            this.check = check;
        }

        public String getCheckAll() {
            return checkAll;
        }

        public void setCheckAll(String checkAll) {
            this.checkAll = checkAll;
        }

        public String getFirstCheckAll() {
            return firstCheckAll;
        }

        public void setFirstCheckAll(String firstCheckAll) {
            this.firstCheckAll = firstCheckAll;
        }
    }
}
