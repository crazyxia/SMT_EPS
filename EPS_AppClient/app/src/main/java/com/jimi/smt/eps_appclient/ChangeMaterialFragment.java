package com.jimi.smt.eps_appclient;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.jimi.smt.eps_appclient.Func.DBService;
import com.jimi.smt.eps_appclient.Func.GlobalFunc;
import com.jimi.smt.eps_appclient.Func.Log;
import com.jimi.smt.eps_appclient.Unit.Constants;
import com.jimi.smt.eps_appclient.Unit.MaterialItem;
import com.jimi.smt.eps_appclient.Views.InfoDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caobotao on 16/1/4.
 * @ 描述:换料
 */
public class ChangeMaterialFragment extends Fragment implements TextView.OnEditorActionListener {
    private final String TAG = this.getClass().getSimpleName();

    //全局变量
    private GlobalData globalData;

    //换料视图
    private View vChangeMaterialFragment;
    //操作员　站位　料号
    private TextView edt_Operation;
    private EditText edt_LineSeat;
    private EditText edt_OrgMaterial;
    private EditText edt_ChgMaterial;
    private TextView tv_Result,tv_Remark,tv_lastInfo;

    //当前的站位，线上料号，更换料号
    private String curLineSeat,curOrgMaterial,curChgMaterial;

    //当前上料时用到的排位料号表x
    private List<MaterialItem> lChangeMaterialItem = new ArrayList<MaterialItem>();

    //当前换料项
    private int curChangeMaterialId = -1;
    private String FileId;
    //成功换料的站位
    private String sucSeatNo;
    //成功换料时的时间
    private long sucTime;
    private GlobalFunc globalFunc;

    private boolean first_checkAll_result;
    private static final int FIRST_CHECKALL_TRUE = 101;
    private static final int FIRST_CHECKALL_FALSE = 102;
    private int checkType;
    private Handler changeHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case FIRST_CHECKALL_TRUE:
                    // TODO: 2017/11/14
//                    first_checkAll_result = true;
                    setFirst_checkAll_result(true);
                    break;
                case FIRST_CHECKALL_FALSE:
//                    first_checkAll_result = false;
                    setFirst_checkAll_result(false);
                    edt_LineSeat.setText("");
                    if (checkType == 0){
                        showInfo("提示","IPQC未做首次全检","请联系管理员！");
                    }
                    // TODO: 2017/11/14  
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        vChangeMaterialFragment = inflater.inflate(R.layout.changematerial_layout, container, false);
        //获取工单号和操作员
        Intent intent=getActivity().getIntent();
        savedInstanceState=intent.getExtras();
        Log.i(TAG,"curOderNum::"+savedInstanceState.getString("orderNum")+" -- curOperatorNUm::"+savedInstanceState.getString("operatorNum"));

        globalData = (GlobalData) getActivity().getApplication();
        globalData.setOperator(savedInstanceState.getString("operatorNum"));
//        globalData.setOperType(Constants.CHANGEMATERIAL);
        globalFunc = new GlobalFunc(getActivity());

        initData();
        initViews(savedInstanceState);
        initEvents();
//        initData();

        return vChangeMaterialFragment;
    }

    /**
     * @author connie
     * @time 2017-9-22
     * @describe 初始化控件
     */
    private void initViews(Bundle bundle) {
        Log.i(TAG, "initViews");
        TextView tv_change_order= (TextView) vChangeMaterialFragment.findViewById(R.id.tv_change_order);
        edt_Operation = (TextView) vChangeMaterialFragment.findViewById(R.id.tv_change_Operation);
        edt_LineSeat = (EditText) vChangeMaterialFragment.findViewById(R.id.edt_lineseat);
        edt_OrgMaterial = (EditText) vChangeMaterialFragment.findViewById(R.id.edt_OrgMaterial);
        edt_ChgMaterial = (EditText) vChangeMaterialFragment.findViewById(R.id.edt_ChgMaterial);
        tv_Result = (TextView) vChangeMaterialFragment.findViewById(R.id.tv_Result);
        tv_lastInfo= (TextView) vChangeMaterialFragment.findViewById(R.id.tv_LastInfo);
        tv_Remark= (TextView) vChangeMaterialFragment.findViewById(R.id.tv_Remark);

        tv_change_order.setText(bundle.getString("orderNum"));
        edt_Operation.setText(bundle.getString("operatorNum"));

        edt_LineSeat.requestFocus();
    }

    /**
     * @author connie
     * @time 2017-9-22
     * @describe 初始化事件
     */
    private void initEvents() {
        Log.i(TAG, "initEvents");
        edt_LineSeat.setOnEditorActionListener(this);
        edt_OrgMaterial.setOnEditorActionListener(this);
        edt_ChgMaterial.setOnEditorActionListener(this);

        edt_LineSeat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    checkType = 0;
                    //判断是否首次全检
                    getFirstCheckAllResult(lChangeMaterialItem.get(0).getFileId());
                    Log.d(TAG,"globalData-OperType:"+globalData.getOperType());
                }
            }
        });

        //点击结果后换下一个料
        tv_Result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeNextMaterial();
                clearResultRemark();
            }
        });
    }

    /**
     * @author connie
     * @time 2017-9-22
     * @describe 初始化数据
     */
    private void initData() {
        Log.i(TAG, "initData");
        curChangeMaterialId = -1;
        //填充数据
        lChangeMaterialItem.clear();
        List<MaterialItem> materialItems = globalData.getMaterialItems();

        for (MaterialItem materialItem : materialItems) {
            FileId=materialItem.getFileId();
            MaterialItem feedMaterialItem = new MaterialItem(materialItem.getFileId(),materialItem.getOrgLineSeat(), materialItem.getOrgMaterial(), "", "", "", "");
            lChangeMaterialItem.add(feedMaterialItem);
        }
        
        //判断是否首次全检
//        getFirstCheckAllResult(materialItems.get(0).getFileId());
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        Log.i(TAG, "onEditorAction");
        //回车键
        if (i == EditorInfo.IME_ACTION_SEND ||
                (keyEvent != null && keyEvent.getKeyCode() == keyEvent.KEYCODE_ENTER)) {
            switch (keyEvent.getAction()) {
                //按下
                case KeyEvent.ACTION_UP:
                    //先判断是否联网
                    if (globalFunc.isNetWorkConnected()){
                        if (isFirst_checkAll_result()){
                            //扫描内容
                            String strValue = String.valueOf(((EditText) textView).getText());
                            strValue = strValue.replaceAll("\r", "");
                            Log.i(TAG, "strValue:" + strValue);
                            textView.setText(strValue);
                            //该站位的料号(包括替换料)、和位置
                            ArrayList<String> materialList=new ArrayList<String>();
                            ArrayList<Integer> materialIndex=new ArrayList<Integer>();

                            //将扫描的内容更新至列表中
                            switch (textView.getId()) {
                                case R.id.edt_lineseat:
                                    changeNextMaterial();
                                    String scanLineSeat=strValue;
                                    if (strValue.length()>=8){
                                        scanLineSeat=strValue.substring(4,6)+"-"+strValue.substring(6,8);
                                    }
                                    strValue=scanLineSeat;
                                    edt_LineSeat.setText(strValue);
                                    //站位
                                    //String scanLineSeat=strValue.substring(4,6)+"-"+strValue.substring(6,8);
                                    for (int j = 0; j < lChangeMaterialItem.size(); j++) {
                                        MaterialItem materialItem = lChangeMaterialItem.get(j);
                                        if (materialItem.getOrgLineSeat().equalsIgnoreCase(strValue)) {
                                            curChangeMaterialId = j;
                                            materialItem.setScanLineSeat(strValue);
                                        }
                                    }
                                    if (curChangeMaterialId < 0) {
                                        curLineSeat=strValue;
                                        displayResult(1,"","排位表不存在此站位！",0);
                                        return true;
                                    }
                                    curLineSeat=strValue;
                                    edt_OrgMaterial.requestFocus();
                                    break;

                                case R.id.edt_OrgMaterial:
                                    //站位正确后才进入这里
                                    if (strValue.indexOf("@") != -1) {
                                        strValue = strValue.substring(0, strValue.indexOf("@"));
                                        textView.setText(strValue);
                                    }
                                    curOrgMaterial = strValue;
                                    //初始化换料位置
                                    curChangeMaterialId = -1;
                                    //先获取扫描到的站位
                                    String scanSeatNo = curLineSeat;
                                    Log.d(TAG,"scanSeatNo-"+scanSeatNo);
                                    for (int j = 0;j < lChangeMaterialItem.size();j++){
                                        MaterialItem materialItem = lChangeMaterialItem.get(j);
                                        if (materialItem.getOrgLineSeat().equalsIgnoreCase(scanSeatNo)){
                                            //保存料号、和位置
                                            materialList.add(materialItem.getOrgMaterial());
                                            materialIndex.add(j);
                                        }
                                    }
                                    //判断扫到的料号是否等于站位的原始料号
                                    for (int jj = 0;jj < materialList.size();jj++){
                                        if (materialList.get(jj).equalsIgnoreCase(curOrgMaterial)){
                                            curChangeMaterialId = materialIndex.get(jj);
                                        }
                                    }
                                    //扫描到的料号不存在表中
                                    if (curChangeMaterialId < 0){
                                        displayResult(1,scanSeatNo,"料号与站位不对应！",1);
                                        materialIndex.clear();
                                        materialList.clear();
                                        return true;
                                    }
                                    edt_ChgMaterial.requestFocus();
                                    break;

                                case R.id.edt_ChgMaterial:
                                    //站位且线上料号正确后才进入这里
                                    if (strValue.indexOf("@") != -1) {
                                        strValue = strValue.substring(0, strValue.indexOf("@"));
                                        textView.setText(strValue);
                                    }
                                    curChgMaterial = strValue;
                                    //初始化换料位置
                                    curChangeMaterialId = -1;
                                    materialIndex.clear();
                                    materialList.clear();
                                    //先获取扫描到的站位
                                    String scanSeatNum = curLineSeat;
                                    Log.d(TAG,"scanSeatNum-"+scanSeatNum);
                                    //判断扫到的料号是否等于站位的原始料号
                                    for (int k = 0;k < lChangeMaterialItem.size();k++){
                                        MaterialItem materialItem = lChangeMaterialItem.get(k);
                                        if (materialItem.getOrgLineSeat().equalsIgnoreCase(scanSeatNum)){
                                            //保存料号、和位置
                                            materialList.add(materialItem.getOrgMaterial());
                                            materialIndex.add(k);
                                        }
                                    }
                                    for (int kk = 0;kk < materialList.size();kk++){
                                        if (materialList.get(kk).equalsIgnoreCase(curChgMaterial)){
                                            curChangeMaterialId = materialIndex.get(kk);
                                        }
                                    }
                                    //扫描到的料号不存在表中
                                    if (curChangeMaterialId < 0){
                                        displayResult(1,scanSeatNum,"料号与站位不对应！",1);
                                        materialIndex.clear();
                                        materialList.clear();
                                        return true;
                                    }
                                    //扫到的料号在站位表中
                                    if (edt_ChgMaterial.getText().toString().equals(edt_OrgMaterial.getText().toString())) {
                                        displayResult(0,lChangeMaterialItem.get(curChangeMaterialId).getOrgLineSeat(), "换料成功!",1);
                                    } else {
                                        displayResult(0,lChangeMaterialItem.get(curChangeMaterialId).getOrgLineSeat(), "主替料换料成功!",1);
                                    }
                                    //清空该站位的料号(包括替换料)、和位置
                                    materialIndex.clear();
                                    materialList.clear();
                                    edt_LineSeat.requestFocus();
                                    break;
                            }
                        }else {
                            showInfo("提示","IPQC未做首次全检","请联系管理员！");
                        }
                    }else {
                        globalFunc.showInfo("警告","请检查网络连接是否正常!","请连接网络!");
                        clearAndSetFocus();
                    }
                    return true;
                default:
                    return true;
            }
        }
        return false;
    }

    //判断是否全部进行了首次全检
    private void getFirstCheckAllResult(final String programId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean result = new DBService().isOrderFirstCheckAll(programId);
                Message message = Message.obtain();
                if (result){
                    message.what = FIRST_CHECKALL_TRUE;
                }else {
                    message.what = FIRST_CHECKALL_FALSE;
                }
                changeHandler.sendMessage(message);
            }
        }).start();
    }

    /**
     * @param i
     */
    private void displayResult(int i,String orgLineSeat,String remark,int logType) {
        Log.i(TAG, "displayResult");
        Log.d(TAG,"programId-"+FileId+"\norgLineSeat-"+orgLineSeat+"\nlogType-"+logType);
        String Result = "";
        switch (i) {
            case 0:
                tv_Result.setBackgroundColor(Color.GREEN);
                tv_Remark.setTextColor(Color.argb(255,102,153,0));
                Result = "PASS";
                break;
            case 1:
                tv_Result.setBackgroundColor(Color.RED);
                tv_Remark.setTextColor(Color.RED);
                Result = "FAIL";

                break;
        }
        tv_Result.setText(Result);
        tv_Remark.setText(remark);
        tv_lastInfo.setText("扫描结果: 站位:" + curLineSeat
                + "\r\n原始料号:" + curOrgMaterial
                + "\r\n替换料号:" + curChgMaterial);

        globalData.setOperType(Constants.CHANGEMATERIAL);
        globalData.setUpdateType(Constants.CHANGEMATERIAL);
        MaterialItem materialItem =
                new MaterialItem(
                    FileId,
                    orgLineSeat,
                    String.valueOf(edt_OrgMaterial.getText()),
                    String.valueOf(edt_LineSeat.getText()),
                    String.valueOf(edt_ChgMaterial.getText()),
                    Result,
                    remark);
        //添加操作日志
        globalFunc.AddDBLog(globalData, materialItem);
        if (logType == 1){
            //添加显示日志
            globalFunc.updateVisitLog(globalData,materialItem);
        }
        clearAndSetFocus();
    }

    /**
     * @author connie
     * @time 2017-9-22
     * @describe 测试下一项
     */
    private void changeNextMaterial() {
        Log.i(TAG, "changeNextMaterial");
        tv_Result.setBackgroundColor(Color.TRANSPARENT);
        clearAndSetFocus();
        clearResultRemark();
    }

    private void clearAndSetFocus(){
        edt_LineSeat.setText("");
        edt_OrgMaterial.setText("");
        edt_ChgMaterial.setText("");
        edt_LineSeat.requestFocus();

        curLineSeat="";
        curOrgMaterial="";
        curChgMaterial="";
        curChangeMaterialId=-1;
    }

    private void clearResultRemark(){
        tv_Result.setText("");
        tv_Remark.setText("");
    }

    //IPQC未做首次全检
    public void showInfo(String title, String message, final String netFailToastStr){
        //对话框所有控件id
        int itemResIds[]=new int[]{R.id.dialog_title_view,
                R.id.dialog_title,R.id.tv_alert_info,R.id.info_trust};
        //标题和内容
        String titleMsg[]=new String[]{title,message};
        //内容的样式
        int msgStype[]=new int[]{22, Color.RED};
        InfoDialog infoDialog=new InfoDialog(getActivity(),
                R.layout.info_dialog_layout,itemResIds,titleMsg,msgStype);

        infoDialog.setOnDialogItemClickListener(new InfoDialog.OnDialogItemClickListener() {
            @Override
            public void OnDialogItemClick(InfoDialog dialog, View view) {
                switch (view.getId()){
                    case R.id.info_trust:
                        dialog.dismiss();
//                        Toast.makeText(getActivity(),netFailToastStr,Toast.LENGTH_LONG).show();
                        clearAndSetFocus();
                        checkType = 1;
                        getFirstCheckAllResult(lChangeMaterialItem.get(0).getFileId());
                        break;
                }
            }
        });
        infoDialog.show();
    }

    private boolean isFirst_checkAll_result() {
        Log.d(TAG,"isFirst_checkAll_result-"+first_checkAll_result);
        return first_checkAll_result;
    }

    private void setFirst_checkAll_result(boolean first_checkAll_result) {
        this.first_checkAll_result = first_checkAll_result;
    }
}
