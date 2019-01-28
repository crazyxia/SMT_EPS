package com.jimi.smt.eps_appclient.Beans;

import java.util.List;

/**
 * 类名: Material
 * 创建人: Liang GuoChang
 * 创建时间: 2018/8/27 21:58
 * 描述:
 * 版本号:
 * 修改记录:
 */

public class Material extends BaseMsg {


    private List<MaterialBean> data;

    public List<MaterialBean> getData() {
        return data;
    }

    public void setData(List<MaterialBean> data) {
        this.data = data;
    }

    public static class MaterialBean extends BaseMaterial {
        /**
         * programId : 2454
         * line : 301
         * workOrder : 2344
         * boardType : 1
         * lineseat : 01-01
         * scanlineseat : 01-01
         * materialNo : K3108410300530
         * scanMaterial : K3108410300530
         * alternative : false
         * specitification : TVS管; & ;ESD9X5.0ST5G 5V; & ;（ON）; & ;
         * position : VR804 VR805 T607 T612
         * quantity : 0
         * serialNo : 1
         * result : PASS
         * remark : fgh
         */

        private String materialNo;
        private String scanMaterial;
        private boolean alternative;
        private String specitification;
        private String position;
        private int quantity;
        private int serialNo;

        public MaterialBean(){
            super();
        }

        public MaterialBean(String order, int boardType, String line, String programId, int serialNo, boolean alternative,String specitification,String position,
                            int quantity,String orgLineSeat, String orgMaterial, String scanLineSeat, String scanMaterial, String result, String remark) {
            super(programId, line, order, boardType, orgLineSeat, scanLineSeat, result, remark);
            this.alternative = alternative;
            this.specitification = specitification;
            this.position = position;
            this.quantity = quantity;
            this.materialNo = orgMaterial;
            this.scanMaterial = scanMaterial;
            this.serialNo = serialNo;

        }

        public String getMaterialNo() {
            return materialNo;
        }

        public void setMaterialNo(String materialNo) {
            this.materialNo = materialNo;
        }

        public String getScanMaterial() {
            return scanMaterial;
        }

        public void setScanMaterial(String scanMaterial) {
            this.scanMaterial = scanMaterial;
        }

        public boolean isAlternative() {
            return alternative;
        }

        public void setAlternative(boolean alternative) {
            this.alternative = alternative;
        }

        public int getSerialNo() {
            return serialNo;
        }

        public void setSerialNo(int serialNo) {
            this.serialNo = serialNo;
        }

        public String getSpecitification() {
            return specitification;
        }

        public void setSpecitification(String specitification) {
            this.specitification = specitification;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public MaterialBean copy(MaterialBean bean){
            MaterialBean materialBean = new MaterialBean();
            materialBean.setProgramId(bean.getProgramId());
            materialBean.setLine(bean.getLine());
            materialBean.setWorkOrder(bean.getWorkOrder());
            materialBean.setBoardType(bean.getBoardType());
            materialBean.setLineseat(bean.getLineseat());
            materialBean.setMaterialNo(bean.getMaterialNo());
            materialBean.setSerialNo(bean.getSerialNo());
            materialBean.setScanlineseat(bean.getLineseat());
            materialBean.setScanMaterial(bean.getScanMaterial());
            materialBean.setRemark(bean.getRemark());
            materialBean.setResult(bean.getResult());
            materialBean.setAlternative(bean.isAlternative());
            materialBean.setSpecitification(bean.getSpecitification());
            materialBean.setPosition(bean.getPosition());
            materialBean.setQuantity(bean.getQuantity());
            return materialBean;
        }

        @Override
        public boolean equals(Object obj) {
            MaterialBean bean = (MaterialBean) obj;
            int oldHasCode = bean.hashCode();
            int newHasCode = hashCode();
            return (oldHasCode == newHasCode);
        }

        @Override
        public int hashCode() {
            return (super.hashCode() + materialNo + serialNo + alternative + specitification + position + quantity).hashCode();
        }

        public String getMaterialStr() {
            return "program_id : " + getProgramId() + " \n "
                    + "line : " + getLine() + " \n "
                    + "order : " + getWorkOrder() + " \n "
                    + "boardType : " + getBoardType() + " \n "
                    + "serialNo : " + getSerialNo() + " \n "
                    + "lineSeat : " + getLineseat() + " \n "
                    + "material : " + getMaterialNo() + " \n "
                    + "specitification : " + getSpecitification() + " \n "
                    + "position : " + getPosition() + " \n "
                    + "quantity : " + getQuantity() + " \n "
                    + "scan lineSeat : " + getScanlineseat() + " \n "
                    + "scan material : " + getScanMaterial() + " \n "
                    + "result : " + getResult() + " \n "
                    + "remark : " + getRemark() + " \n "
                    + "alternative : " + isAlternative();
        }

        @Override
        public int compareTo(MaterialBean materialBean) {
            return 1;
        }
    }
}
