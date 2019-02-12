package com.jimi.smt.eps_appclient.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jimi.smt.eps_appclient.Activity.FactoryLineActivity;
import com.jimi.smt.eps_appclient.Beans.IsAllDoneInfo;
import com.jimi.smt.eps_appclient.Beans.Material;
import com.jimi.smt.eps_appclient.Beans.Operation;
import com.jimi.smt.eps_appclient.Beans.ProgramItemVisit;
import com.jimi.smt.eps_appclient.Func.GlobalFunc;
import com.jimi.smt.eps_appclient.Func.HttpUtils;
import com.jimi.smt.eps_appclient.Func.Log;
import com.jimi.smt.eps_appclient.Interfaces.OkHttpInterface;
import com.jimi.smt.eps_appclient.R;
import com.jimi.smt.eps_appclient.Unit.Constants;
import com.jimi.smt.eps_appclient.Unit.EvenBusTest;
import com.jimi.smt.eps_appclient.Unit.GlobalData;
import com.jimi.smt.eps_appclient.Views.InfoDialog;
import com.jimi.smt.eps_appclient.Views.LoadingDialog;
import com.jimi.smt.eps_appclient.Views.MyEditTextDel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @ 描述:换料
 */
public class ChangeMaterialFragment extends Fragment implements TextView.OnEditorActionListener, View.OnFocusChangeListener, OkHttpInterface {
    private final String TAG = this.getClass().getSimpleName();

    //全局变量
    private GlobalData globalData;
    private LoadingDialog loadingDialog;
    //换料视图
    private View vChangeMaterialFragment;
    //操作员　站位　料号
    private MyEditTextDel edt_LineSeat;
    private MyEditTextDel edt_OrgMaterial;
    private MyEditTextDel edt_ChgMaterial;
    private TextView tv_Result, tv_Remark, tv_lastInfo;

    //当前的站位，线上料号，更换料号
    private String curLineSeat, curOrgMaterial, curChgMaterial;
    //线上料号的时间戳、更换的料号的时间戳
    private String orgTimestamp, chgTimestamp;

    //当前换料时用到的排位料号表
    private List<Material.MaterialBean> mChangeMaterialBeans = new ArrayList<>();
    private static List<Material.MaterialBean> tempBeans = new ArrayList<>();
    //该站位的料号(包括替换料)、和位置
    private ArrayList<String> materialList = new ArrayList<>();
    private ArrayList<Integer> materialIndex = new ArrayList<>();

    //当前换料项
    private int curChangeMaterialId = -1;
    private GlobalFunc globalFunc;
    private FactoryLineActivity factoryLineActivity;
    private InfoDialog infoDialog;

    private boolean mHidden = false;
    private HttpUtils mHttpUtils;
    private int checkAllDoneStrCondition = -1;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        vChangeMaterialFragment = inflater.inflate(R.layout.changematerial_layout, container, false);
        //获取工单号和操作员
        Intent intent = getActivity().getIntent();
        savedInstanceState = intent.getExtras();
        Log.i(TAG, "curOderNum::" + savedInstanceState.getString("orderNum") + " -- curOperatorNUm::" + savedInstanceState.getString("operatorNum"));
        globalData.setOperator(savedInstanceState.getString("operatorNum"));
        /*
        if (factoryLineActivity.updateDialog != null && factoryLineActivity.updateDialog.isShowing()) {
            factoryLineActivity.updateDialog.cancel();
            factoryLineActivity.updateDialog.dismiss();
        }
        */
        //判断是否首次全检
        showLoading();
        checkAllDoneStrCondition = 0;
        mHttpUtils.checkAllDoneStr(globalData.getProgramId(),String.valueOf(Constants.FIRST_CHECK_ALL));

        initData();
        initViews(savedInstanceState);
        initEvents();

        return vChangeMaterialFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        //注册订阅
        EventBus.getDefault().register(this);
        factoryLineActivity = (FactoryLineActivity) getActivity();
        globalData = (GlobalData) getActivity().getApplication();
        globalFunc = new GlobalFunc(getActivity());
        mHttpUtils = new HttpUtils(this, getContext());
    }

    //监听订阅的消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EvenBusTest event) {
//        Log.d(TAG, "onEventMainThread - " + event.getUpdated());
        if (event.getUpdated() == 0) {
            if (infoDialog != null && infoDialog.isShowing()) {
                infoDialog.cancel();
                infoDialog.dismiss();
            }
            Log.d(TAG, "mHidden - " + mHidden);
            Log.d(TAG, "isUpdateProgram - " + globalData.isUpdateProgram());
            if (!mHidden) {
                /*
                if (factoryLineActivity.updateDialog != null && factoryLineActivity.updateDialog.isShowing()) {
                    factoryLineActivity.updateDialog.cancel();
                    factoryLineActivity.updateDialog.dismiss();
                }
                */
//                clearAndSetFocus();
//                showLoading();
//                checkFirstCondition = 2;
//                mHttpUtils.checkAllDone(globalData.getProgramId(), Constants.FIRST_CHECK_ALL);
            }


        }
        // TODO: 2019/1/23 需解决作废重传问题
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.i(TAG, "onHiddenChanged-" + hidden);
        this.mHidden = hidden;
        if (!hidden) {
            /*
            if (factoryLineActivity.updateDialog != null && factoryLineActivity.updateDialog.isShowing()) {
                factoryLineActivity.updateDialog.cancel();
                factoryLineActivity.updateDialog.dismiss();
            }
            */
            clearAndSetFocus();
            //判断是否首次全检
            showLoading();
            checkAllDoneStrCondition = 0;
            mHttpUtils.checkAllDoneStr(globalData.getProgramId(),String.valueOf(Constants.FIRST_CHECK_ALL));

        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        //注销订阅
        EventBus.getDefault().unregister(this);
    }

    /**
     * @author connie
     * @time 2017-9-22
     * @describe 初始化控件
     */
    private void initViews(Bundle bundle) {
        Log.i(TAG, "initViews");
        TextView tv_change_order = vChangeMaterialFragment.findViewById(R.id.tv_change_order);
        TextView edt_Operation = vChangeMaterialFragment.findViewById(R.id.tv_change_Operation);
        edt_LineSeat = vChangeMaterialFragment.findViewById(R.id.edt_change_lineseat);
        edt_OrgMaterial = vChangeMaterialFragment.findViewById(R.id.edt_change_OrgMaterial);
        edt_ChgMaterial = vChangeMaterialFragment.findViewById(R.id.edt_change_ChgMaterial);
        tv_Result = vChangeMaterialFragment.findViewById(R.id.tv_Result);
        tv_lastInfo = vChangeMaterialFragment.findViewById(R.id.tv_LastInfo);
        tv_Remark = vChangeMaterialFragment.findViewById(R.id.tv_Remark);

        tv_change_order.setText(bundle.getString("orderNum"));
        edt_Operation.setText(bundle.getString("operatorNum"));

        edt_LineSeat.requestFocus();
    }

    //初始化事件
    private void initEvents() {
        Log.i(TAG, "initEvents");
        edt_LineSeat.setOnEditorActionListener(this);
        edt_OrgMaterial.setOnEditorActionListener(this);
        edt_ChgMaterial.setOnEditorActionListener(this);
        edt_LineSeat.setOnFocusChangeListener(this);
        edt_OrgMaterial.setOnFocusChangeListener(this);
        edt_ChgMaterial.setOnFocusChangeListener(this);

        //点击结果后换下一个料
        tv_Result.setOnClickListener(view -> changeNextMaterial());
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {

            case R.id.edt_change_OrgMaterial:
                if (hasFocus) {
                    if (TextUtils.isEmpty(edt_LineSeat.getText())) {
                        edt_OrgMaterial.setCursorVisible(false);
                        edt_LineSeat.setText("");
                        edt_LineSeat.requestFocus();
                    } else {
                        edt_OrgMaterial.setCursorVisible(true);
                    }
                }
                break;

            case R.id.edt_change_ChgMaterial:
                if (hasFocus) {
                    if (TextUtils.isEmpty(edt_LineSeat.getText())) {
                        edt_ChgMaterial.setCursorVisible(false);
                        edt_LineSeat.setText("");
                        edt_LineSeat.requestFocus();
                    } else if (TextUtils.isEmpty(edt_OrgMaterial.getText())) {
                        edt_ChgMaterial.setCursorVisible(false);
                        edt_OrgMaterial.setText("");
                        edt_OrgMaterial.requestFocus();
                    } else {
                        edt_ChgMaterial.setCursorVisible(true);
                    }
                }
                break;
        }
    }

    //初始化数据
    private void initData() {
        Log.i(TAG, "initData");
        curChangeMaterialId = -1;
        //填充数据
        mChangeMaterialBeans.clear();
        tempBeans.clear();
        tempBeans.addAll(globalData.getMaterialBeans());
        for (Material.MaterialBean org : tempBeans) {
            //保存缓存到数据库中
            Material.MaterialBean bean = new Material.MaterialBean();
            bean = bean.copy(org);
            bean.setScanlineseat("");
            bean.setScanMaterial("");
            bean.setRemark("");
            bean.setResult("");
            mChangeMaterialBeans.add(bean);
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        Log.i(TAG, "onEditorAction");
        //回车键
        if (i == EditorInfo.IME_ACTION_SEND ||
                (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            switch (keyEvent.getAction()) {
                //按下
                case KeyEvent.ACTION_UP:
                    //先判断是否联网
                    if (globalFunc.isNetWorkConnected()) {

                        //扫描内容
                        String strValue = String.valueOf(((EditText) textView).getText());
                        strValue = strValue.replaceAll("\r", "");
                        Log.i(TAG, "strValue:" + strValue);
                        textView.setText(strValue);

                        //将扫描的内容更新至列表中
                        switch (textView.getId()) {
                            case R.id.edt_change_lineseat:
                                changeNextMaterial();
                                strValue = globalFunc.getLineSeat(strValue);
                                edt_LineSeat.setText(strValue);

                                //判断是否做了首次全检
                                showLoading();
                                checkAllDoneStrCondition = 1;
                                mHttpUtils.checkAllDoneStr(globalData.getProgramId(),String.valueOf(Constants.FIRST_CHECK_ALL));
                                break;

                            case R.id.edt_change_OrgMaterial://站位正确后才进入这里
                                //线上料号的时间戳
                                orgTimestamp = globalFunc.getSerialNum(strValue);
                                //料号
                                strValue = globalFunc.getMaterial(strValue);
                                textView.setText(strValue);
                                curOrgMaterial = strValue;
                                //初始化换料位置
                                curChangeMaterialId = -1;
                                //先获取扫描到的站位
                                String scanSeatNo = curLineSeat;
                                Log.d(TAG, "scanSeatNo-" + scanSeatNo);
                                //判断扫到的料号是否等于站位的原始料号
                                for (int jj = 0; jj < materialList.size(); jj++) {
                                    if (materialList.get(jj).equalsIgnoreCase(curOrgMaterial)) {
                                        curChangeMaterialId = materialIndex.get(jj);
                                    }
                                }
                                //扫描到的料号不存在表中
                                if (curChangeMaterialId < 0) {
                                    displayResult(1, scanSeatNo, "料号与站位不对应！", 1);
                                    materialIndex.clear();
                                    materialList.clear();
                                    return true;
                                }
                                edt_ChgMaterial.requestFocus();
                                break;

                            case R.id.edt_change_ChgMaterial://站位且线上料号正确后才进入这里
                                //更换料号的流水号
                                chgTimestamp = globalFunc.getSerialNum(strValue);
                                //更换料号
                                strValue = globalFunc.getMaterial(strValue);
                                textView.setText(strValue);
                                curChgMaterial = strValue;
                                //初始化换料位置
                                curChangeMaterialId = -1;
                                //先获取扫描到的站位
                                String scanSeatNum = curLineSeat;
                                Log.d(TAG, "scanSeatNum-" + scanSeatNum);

                                //时间相同、并且料号也相同(防呆)
                                if ((chgTimestamp.equalsIgnoreCase(orgTimestamp)) && (curChgMaterial.equals(curOrgMaterial))) {
                                    curChangeMaterialId = -2;
                                } else {
                                    //不相等,判断扫到的料号是否等于站位的原始料号
                                    for (int kk = 0; kk < materialList.size(); kk++) {
                                        if (materialList.get(kk).equalsIgnoreCase(curChgMaterial)) {
                                            curChangeMaterialId = materialIndex.get(kk);
                                        }
                                    }
                                }
                                //扫描到的料号不存在表中
                                if (curChangeMaterialId < 0) {
                                    if (curChangeMaterialId == -1) {
                                        displayResult(1, scanSeatNum, "料号与站位不对应！", 1);
                                    } else if (curChangeMaterialId == -2) {
                                        displayResult(1, scanSeatNum, "不能扫同一个料盘", 1);
                                    }
                                    materialIndex.clear();
                                    materialList.clear();
                                    return true;
                                }
                                //扫到的料号在站位表中
                                if (edt_ChgMaterial.getText().toString().equals(edt_OrgMaterial.getText().toString())) {
                                    displayResult(0, mChangeMaterialBeans.get(curChangeMaterialId).getLineseat(), "换料成功!", 1);
                                } else {
                                    displayResult(0, mChangeMaterialBeans.get(curChangeMaterialId).getLineseat(), "主替料换料成功!", 1);
                                }
                                //清空该站位的料号(包括替换料)、和位置
                                materialIndex.clear();
                                materialList.clear();
                                edt_LineSeat.requestFocus();
                                break;
                        }

                    } else {
                        globalFunc.showInfo("警告", "请检查网络连接是否正常!", "请连接网络!");
                        clearAndSetFocus();
                    }
                    return true;
                default:
                    return true;
            }
        }
        return false;
    }

    //执行换料操作
    private void beginChange(String scanLine) {
        //站位
        for (int j = 0; j < mChangeMaterialBeans.size(); j++) {
            Material.MaterialBean materialItem = mChangeMaterialBeans.get(j);
            if (materialItem.getLineseat().equalsIgnoreCase(scanLine)) {
                curChangeMaterialId = j;
                materialItem.setScanlineseat(scanLine);
                //保存料号、和位置
                materialList.add(materialItem.getMaterialNo());
                materialIndex.add(j);
            }
        }
        if (curChangeMaterialId < 0) {
            curLineSeat = scanLine;
            displayResult(1, "", "排位表不存在此站位！", 0);
            //清空列表
            materialIndex.clear();
            materialList.clear();
        }
        curLineSeat = scanLine;
        edt_OrgMaterial.requestFocus();
    }

    @SuppressLint("SetTextI18n")
    private void displayResult(int i, String orgLineSeat, String remark, int logType) {
        Log.i(TAG, "displayResult");
        String result = "";
        switch (i) {
            case 0:
                tv_Result.setBackgroundColor(Color.GREEN);
                tv_Remark.setTextColor(Color.argb(255, 102, 153, 0));
                result = "PASS";
                break;
            case 1:
                tv_Result.setBackgroundColor(Color.RED);
                tv_Remark.setTextColor(Color.RED);
                result = "FAIL";

                break;
        }
        tv_Result.setText(result);
        tv_Remark.setText(remark);
        tv_lastInfo.setText("扫描结果: 站位:" + curLineSeat
                + "\r\n原始料号:" + curOrgMaterial
                + "\r\n替换料号:" + curChgMaterial);

        globalData.setOperType(Constants.CHANGEMATERIAL);
        globalData.setUpdateType(Constants.CHANGEMATERIAL);

        Material.MaterialBean bean = new Material.MaterialBean(globalData.getWork_order(),
                globalData.getBoard_type(), globalData.getLine(), globalData.getProgramId(), -1, false, "", "", -1,
                orgLineSeat, String.valueOf(edt_OrgMaterial.getText()).trim(), String.valueOf(edt_LineSeat.getText()).trim(), String.valueOf(edt_ChgMaterial.getText()).trim(), result, remark);

        Operation operation = Operation.getOperation(globalData.getOperator(), Constants.CHANGEMATERIAL, bean);

        mHttpUtils.addOperation(operation);

        //扫描的站位不在站位表上,不显示
        if (logType == 1) {
            //添加显示换料日志
            ProgramItemVisit itemVisit = ProgramItemVisit.getProgramItemVisit(Constants.CHANGEMATERIAL, bean);
            mHttpUtils.updateVisit(bean, itemVisit, 1);
        }
        clearAndSetFocus();
    }

    //测试下一项
    private void changeNextMaterial() {
        Log.i(TAG, "changeNextMaterial");
        tv_Result.setBackgroundColor(Color.TRANSPARENT);
        clearAndSetFocus();
        clearResultRemark();
    }

    private void clearAndSetFocus() {
        Log.i(TAG, " - clearAndSetFocus - ");
        edt_LineSeat.setText("");
        edt_OrgMaterial.setText("");
        edt_ChgMaterial.setText("");
        edt_LineSeat.requestFocus();

        curLineSeat = "";
        curOrgMaterial = "";
        curChgMaterial = "";
        curChangeMaterialId = -1;
    }

    private void clearResultRemark() {
        tv_Result.setText("");
        tv_Remark.setText("");
    }

    //IPQC未做首次全检
    private void showInfo(String message, String tip) {
        //对话框所有控件id
        int itemResIds[] = new int[]{R.id.dialog_title_view,
                R.id.dialog_title, R.id.tv_alert_info, R.id.info_trust, R.id.tv_alert_msg};
        //标题和内容
        String titleMsg[] = new String[]{"提示", message, tip};
        //内容的样式
        int msgStype[] = new int[]{22, Color.RED, Color.argb(255, 219, 201, 36)};

        infoDialog = new InfoDialog(getActivity(),
                R.layout.info_dialog_layout, itemResIds, titleMsg, msgStype);

        infoDialog.setOnDialogItemClickListener((dialog, view) -> {
            switch (view.getId()) {
                case R.id.info_trust:
                    dialog.dismiss();
                    clearAndSetFocus();
                    break;
            }
        });
        infoDialog.show();
    }

    @Override
    public void showHttpResponse(int code, Object request, String response) {
        Log.d(TAG, "showHttpResponse - " + response);
        dismissLoading();
        switch (code) {
            /*
            case HttpUtils.CodeIsAllDone:
                int checkFirst = Integer.valueOf(response);
                switch (checkFirstCondition) {
                    case 0://进入页面
                        if (checkFirst == 0) {
                            showInfo("IPQC未做首次全检", "");
                        }
                        break;
                    case 1://扫描时
                        if (checkFirst == 1) {//已首次全检
                            beginChange(edt_LineSeat.getText().toString().trim());
                        } else {//未首次全检
                            edt_LineSeat.setText("");
                            showInfo("IPQC未做首次全检", "");
                        }
                        break;
                    case 2://站位表更新
                        *//*
                        if (checkFirst == 0) {
                            showInfo("站位表更新!", "IPQC未做首次全检");
                        }
                        *//*
                        break;
                }
                break;
                */

            // TODO: 2019/2/12
            case HttpUtils.CodeIsAllDoneSTR://查询某一项或某几项操作是否完成
                int allDoneCode;
                Gson allDoneGson = new Gson();
                IsAllDoneInfo isAllDoneInfo = allDoneGson.fromJson(response, IsAllDoneInfo.class);
                allDoneCode = isAllDoneInfo.getCode();
                if (allDoneCode == 1) {
                    IsAllDoneInfo.AllDoneInfoBean allDoneInfoBean = isAllDoneInfo.getData();
                    int isFirstCheck = Integer.valueOf(allDoneInfoBean.getFirstCheckAll());
                    switch (checkAllDoneStrCondition){
                        case 0://进入页面
                            if (isFirstCheck == 0) {
                                showInfo("IPQC未做首次全检", "");
                            }
                            break;
                        case 1://扫描时
                            if (isFirstCheck == 1) {//已首次全检
                                beginChange(edt_LineSeat.getText().toString().trim());
                            } else {//未首次全检
                                edt_LineSeat.setText("");
                                showInfo("IPQC未做首次全检", "");
                            }
                            break;
                    }
                }
                break;

            case HttpUtils.CodeAddOperate://操作日志
                break;

            case HttpUtils.CodeAddVisit://visit表
                //更新visit表成功,则可继续换料,否则提醒重新换料
                int resCode = -1;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    resCode = jsonObject.getInt("code");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "showHttpResponse - " + response);
                }
                if (resCode == 0) {
                    showInfo("换料失败!", "请重新换料");
                }
                break;
        }
    }

    @Override
    public void showHttpError(int code, Object request, String s) {
        dismissLoading();
        showInfo("网络连接异常!", "请检查网络");
    }

    private void showLoading() {
        loadingDialog = new LoadingDialog(getActivity(), "正在加载...");
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();
    }

    private void dismissLoading() {
        //取消弹出窗
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.cancel();
            loadingDialog.dismiss();
        }
    }
}
