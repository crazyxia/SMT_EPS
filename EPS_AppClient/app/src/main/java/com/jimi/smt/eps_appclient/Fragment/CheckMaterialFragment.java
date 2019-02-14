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
import android.widget.TextView.OnEditorActionListener;

import com.google.gson.Gson;
import com.jimi.smt.eps_appclient.Activity.QCActivity;
import com.jimi.smt.eps_appclient.Beans.IsAllDoneInfo;
import com.jimi.smt.eps_appclient.Beans.Material;
import com.jimi.smt.eps_appclient.Beans.Operation;
import com.jimi.smt.eps_appclient.Func.GlobalFunc;
import com.jimi.smt.eps_appclient.Func.HttpUtils;
import com.jimi.smt.eps_appclient.Func.Log;
import com.jimi.smt.eps_appclient.R;
import com.jimi.smt.eps_appclient.Unit.Constants;
import com.jimi.smt.eps_appclient.Unit.EvenBusTest;
import com.jimi.smt.eps_appclient.Unit.GlobalData;
import com.jimi.smt.eps_appclient.Views.LoadingDialog;
import com.jimi.smt.eps_appclient.Views.MyEditTextDel;
import com.jimi.smt.eps_appclient.Interfaces.OkHttpInterface;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CheckMaterialFragment extends Fragment implements OnEditorActionListener, OkHttpInterface {
    private final String TAG = this.getClass().getSimpleName();

    //全局变量
    private GlobalData globalData;
    private LoadingDialog loadingDialog;
    //检料视图
    private View vCheckMaterialFragment;
    //操作员　站位　料号
    private MyEditTextDel edt_LineSeat;
    private MyEditTextDel edt_Material;
    private TextView tv_Result, tv_Remark, tv_seat,tv_check_material/*tv_LastInfo*/;
    //当前扫描的站位,料号
    private String curLineSeat, curMaterial;
    //当前检料时用到的排位料号表
    private List<Material.MaterialBean> mCheckBeanList = new ArrayList<>();
    private static List<Material.MaterialBean> tempBeans = new ArrayList<>();
    //核料时检测的料号
    private List<Material.MaterialBean> checkItems = new ArrayList<>();
    //当前检料项
    private int curCheckMaterialId = -1;
    private GlobalFunc globalFunc;
    private QCActivity qcActivity;
    private boolean mHidden = false;
    private HttpUtils mHttpUtils;
//    private int checkFirstCondition = -1;
    private int checkAllDoneStrCondition = -1;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreateView");
        //注册订阅
        EventBus.getDefault().register(this);
        qcActivity = (QCActivity) getActivity();
        globalData = (GlobalData) getActivity().getApplication();
        globalFunc = new GlobalFunc(getActivity());
        qcActivity.callBackInfoDialogClick(() -> {
            if (!mHidden) {
                Log.d(TAG, "接收回调");
                clearAndSetFocus();
            }
        });
    }

    //FragmentTransaction来控制fragment调用该方法
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.mHidden = hidden;
        Log.d(TAG, "onHiddenChanged - " + hidden);
        if (!hidden) {

            /*
            if (qcActivity.updateDialog != null && qcActivity.updateDialog.isShowing()) {
                qcActivity.updateDialog.cancel();
                qcActivity.updateDialog.dismiss();
            }
            */

            clearAndSetFocus();
            showLoading();
//            checkFirstCondition = 2;
            checkAllDoneStrCondition = 2;
            mHttpUtils.checkAllDoneStr(globalData.getProgramId(), String.valueOf(Constants.FIRST_CHECK_ALL));
//            mHttpUtils.checkAllDone(globalData.getProgramId(), Constants.FIRST_CHECK_ALL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        vCheckMaterialFragment = inflater.inflate(R.layout.checkmaterial_layout, container, false);
        //获取工单号和操作员
        Intent intent = getActivity().getIntent();
        savedInstanceState = intent.getExtras();

        globalData.setOperator(savedInstanceState.getString("operatorNum"));

        /*
        if (qcActivity.updateDialog != null && qcActivity.updateDialog.isShowing()) {
            qcActivity.updateDialog.cancel();
            qcActivity.updateDialog.dismiss();
        }
        */

        mHttpUtils = new HttpUtils(this, getContext());

        showLoading();
//        checkFirstCondition = 2;
//        mHttpUtils.checkAllDone(globalData.getProgramId(), Constants.FIRST_CHECK_ALL);

        checkAllDoneStrCondition = 2;
        mHttpUtils.checkAllDoneStr(globalData.getProgramId(), String.valueOf(Constants.FIRST_CHECK_ALL));

        initViews(savedInstanceState);
        initData();//初始化数据
        initEvents();

        return vCheckMaterialFragment;
    }

    //监听订阅的消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EvenBusTest event) {
        if (event.getUpdated() == 0) {
            Log.d(TAG, "mHidden - " + mHidden);
            Log.d(TAG, "isUpdateProgram - " + globalData.isUpdateProgram());
            if (!mHidden) {
                /*
                if (qcActivity.updateDialog != null && qcActivity.updateDialog.isShowing()) {
                    qcActivity.updateDialog.cancel();
                    qcActivity.updateDialog.dismiss();
                }
                */
//                showLoading();
//                checkFirstCondition = 1;
//                mHttpUtils.checkAllDone(globalData.getProgramId(), Constants.FIRST_CHECK_ALL);
            }

        }
    }

    //初始化控件
    private void initViews(Bundle bundle) {
        Log.i(TAG, "initViews");
        TextView tv_check_order = vCheckMaterialFragment.findViewById(R.id.tv_check_order);
        TextView edt_Operation = vCheckMaterialFragment.findViewById(R.id.tv_check_Operation);
        edt_LineSeat = vCheckMaterialFragment.findViewById(R.id.edt_check_lineseat);
        edt_Material = vCheckMaterialFragment.findViewById(R.id.edt_check_material);
        tv_Result = vCheckMaterialFragment.findViewById(R.id.tv_Result);
        tv_Remark = vCheckMaterialFragment.findViewById(R.id.tv_Remark);
//        tv_LastInfo = vCheckMaterialFragment.findViewById(R.id.tv_LastInfo);
        tv_seat = vCheckMaterialFragment.findViewById(R.id.tv_seat);
        tv_check_material = vCheckMaterialFragment.findViewById(R.id.tv_check_material);

        tv_check_order.setText(bundle.getString("orderNum"));
        edt_Operation.setText(bundle.getString("operatorNum"));

        edt_LineSeat.requestFocus();
    }

    //初始化事件
    private void initEvents() {
        Log.i(TAG, "initEvents");
        edt_LineSeat.setOnEditorActionListener(this);
        edt_Material.setOnEditorActionListener(this);
        edt_Material.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (TextUtils.isEmpty(edt_LineSeat.getText())) {
                    edt_Material.setCursorVisible(false);
                    edt_LineSeat.setText("");
                    edt_LineSeat.requestFocus();
                } else {
                    edt_Material.setCursorVisible(true);
                }
            }
        });

        tv_Result.setOnClickListener(view -> checkAgain());
    }

    //初始化数据
    private void initData() {
        Log.i(TAG, "initData");
        //填充数据
        mCheckBeanList.clear();
        tempBeans.clear();
        tempBeans.addAll(globalData.getMaterialBeans());
        for (Material.MaterialBean org : tempBeans) {
            //操作员
            Material.MaterialBean bean = new Material.MaterialBean();
            bean= bean.copy(org);
            bean.setScanlineseat("");
            bean.setScanMaterial("");
            bean.setRemark("");
            bean.setResult("");
            mCheckBeanList.add(bean);
        }
        curCheckMaterialId = -1;
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
                        String scanValue = String.valueOf(((EditText) textView).getText());
                        scanValue = scanValue.replaceAll("\r", "");
                        Log.i(TAG, "scan Value:" + scanValue);
                        textView.setText(scanValue);
                        //将扫描的内容更新至列表中
                        switch (textView.getId()) {

                            case R.id.edt_check_lineseat:
                                checkAgain(); // TODO: 2018/9/6  
                                //站位
                                scanValue = globalFunc.getLineSeat(scanValue);
                                edt_LineSeat.setText(scanValue);

                                //判断是否首检
                                showLoading();
//                                checkFirstCondition = 0;
                                checkAllDoneStrCondition = 0;
                                mHttpUtils.checkAllDoneStr(globalData.getProgramId(), String.valueOf(Constants.FIRST_CHECK_ALL));
//                                mHttpUtils.checkAllDone(globalData.getProgramId(), Constants.FIRST_CHECK_ALL);
                                break;

                            case R.id.edt_check_material:
                                //料号
                                scanValue = globalFunc.getMaterial(scanValue);
                                textView.setText(scanValue);
                                //扫到的料号
                                curMaterial = scanValue;
                                curCheckMaterialId = -1;
                                Material.MaterialBean bean = new Material.MaterialBean();
                                for (Material.MaterialBean materialItem : checkItems) {
                                    materialItem.setScanMaterial(scanValue);
                                    Log.i(TAG, "materialItem - " + materialItem.getMaterialNo());
                                    if (materialItem.getMaterialNo().equalsIgnoreCase(scanValue)) {
                                        curCheckMaterialId = 1;
                                        bean = materialItem;
                                    }
                                }

                                if (curCheckMaterialId == -1) {
                                    bean = checkItems.get(0);
                                    bean.setResult("FAIL");
                                    bean.setRemark("站位与料号不对应");
                                } else if (curCheckMaterialId == 1) {
                                    bean.setResult("PASS");
                                    bean.setRemark("站位和料号正确");
                                }
                                showCheckMaterialResult(bean.getResult(), bean.getRemark(), 1, bean);

                                break;
                        }
                    } else {
                        globalFunc.showInfo("警告", "请检查网络连接是否正常!", "请连接网络!");
                        //自动清空站位和料号以便下一项检测
                        clearAndSetFocus();
                    }

                    return true;
                default:
                    return true;
            }
        }
        return false;
    }

    private void doCheck(String scanLine) {
        for (int j = 0, len = mCheckBeanList.size(); j < len; j++) {
            Material.MaterialBean bean = mCheckBeanList.get(j).copy(mCheckBeanList.get(j));
            if (bean.getLineseat().equalsIgnoreCase(scanLine)) {
                curCheckMaterialId = j;
                bean.setScanlineseat(scanLine);
                checkItems.add(bean);
                Log.i(TAG, "materialItem - " + bean.getMaterialNo());
            }
        }
        if (curCheckMaterialId < 0) {
            curLineSeat = scanLine;
            showCheckMaterialResult("FAIL", "排位表不存在此站位！", 0, null);
        } else {
            mHttpUtils.getChangeSucceed(globalData.getProgramId(), mCheckBeanList.get(curCheckMaterialId).getLineseat());
        }
    }

    //显示抽检结果
    @SuppressLint("SetTextI18n")
    private void showCheckMaterialResult(String checkResult, String strRemark, int logType, Material.MaterialBean bean) {
        String result = "";
        switch (checkResult) {
            case "PASS":
                tv_Result.setBackgroundColor(Color.GREEN);
                result = "PASS";
                tv_Remark.setTextColor(Color.argb(255, 102, 153, 0));
                break;
            case "FAIL":
                tv_Result.setBackgroundColor(Color.RED);
                result = "FAIL";
                tv_Remark.setTextColor(Color.RED);
                break;
        }
        tv_Remark.setText(strRemark);
        tv_Result.setText(result);
//        tv_LastInfo.setText("扫描结果\r\n站位:" + curLineSeat + "\r\n" + "料号:" + curMaterial);
        tv_seat.setText("扫描结果:站位:" + curLineSeat);
        tv_check_material.setText("料号:" + curMaterial);

        if (logType == 1) {
            //添加显示日志
            globalData.setUpdateType(Constants.CHECKMATERIAL);
            mHttpUtils.operate(null, bean, Constants.CHECKMATERIAL, logType);
            //添加操作日志
            globalData.setOperType(Constants.CHECKMATERIAL);
            mHttpUtils.addOperation(Operation.getOperation(globalData.getOperator(), Constants.CHECKMATERIAL, bean));
        }
        //自动清空站位和料号以便下一项检测
        clearAndSetFocus();
    }

    //显示警告结果
    @SuppressLint("SetTextI18n")
    private void showWarnResult(String lineSeat) {
        tv_Remark.setText("该站位未换料成功");
        tv_Remark.setTextColor(Color.argb(255, 182, 171, 17));
        tv_Result.setText("WARN");
        tv_Result.setBackgroundColor(Color.YELLOW);
//        tv_LastInfo.setText("扫描结果\r\n站位:" + lineSeat + "\r\n" + "料号:");
        tv_seat.setText("扫描结果:站位:" + lineSeat);
        tv_check_material.setText("料号:");
    }

    //重新检下一个料
    private void checkAgain() {
        Log.i(TAG, "testAgain");
        curCheckMaterialId = -1;
        tv_Remark.setText("");
        tv_Result.setText("");
        tv_Result.setBackgroundColor(Color.TRANSPARENT);
        curLineSeat = "";
        curMaterial = "";
        edt_Material.setText("");

        checkItems.clear();
    }

    private void clearAndSetFocus() {
        edt_LineSeat.setText("");
        edt_Material.setText("");
        edt_LineSeat.requestFocus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销订阅
        EventBus.getDefault().unregister(this);
    }

    /**
     * http返问回调
     *
     * @param code 请求码
     * @param s 返回信息
     */
    @Override
    public void showHttpResponse(int code, Object request, String s) {
        dismissLoading();
        Log.d(TAG, "showHttpResponse - " + s);
        switch (code) {

            /*
            case HttpUtils.CodeIsAllDone:
                int checkFirst = Integer.valueOf(s);
                switch (checkFirstCondition) {
                    case 0://扫站位时
                        if (checkFirst == 1) {
                            if (!TextUtils.isEmpty(edt_LineSeat.getText())) {
                                doCheck(edt_LineSeat.getText().toString().trim());
                            } else {
                                edt_LineSeat.setText("");
                            }
                        } else {
                            edt_LineSeat.setText("");
                            qcActivity.showInfo("提示", "IPQC未做首次全检", "");
                        }
                        break;

                    case 1:
                        *//*
                        if (checkFirst == 0) {
                            qcActivity.showInfo("提示", "站位表更新!", "IPQC未做首次全检");
                        }
                        *//*
                        break;

                    case 2:
                        if (checkFirst == 0) {
                            edt_LineSeat.setText("");
                            qcActivity.showInfo("提示", "IPQC未做首次全检", "");
                        }
                        break;

                }
                break;
                */

            // TODO: 2019/2/12
            case HttpUtils.CodeIsAllDoneSTR://查询某一项或某几项操作是否完成
                int allDoneCode;
                Gson allDoneGson = new Gson();
                IsAllDoneInfo isAllDoneInfo = allDoneGson.fromJson(s, IsAllDoneInfo.class);
                allDoneCode = isAllDoneInfo.getCode();
                if (allDoneCode == 1) {
                    IsAllDoneInfo.AllDoneInfoBean allDoneInfoBean = isAllDoneInfo.getData();
                    int isFirstCheck = Integer.valueOf(allDoneInfoBean.getFirstCheckAll());
                    Log.d(TAG, "checkAllDoneStrCondition - " + checkAllDoneStrCondition);
                    Log.d(TAG, "isFirstCheck - " + isFirstCheck);
                    switch (checkAllDoneStrCondition) {
                        case 0:
                            if (isFirstCheck == 1) {
                                if (!TextUtils.isEmpty(edt_LineSeat.getText())) {
                                    doCheck(edt_LineSeat.getText().toString().trim());
                                } else {
                                    edt_LineSeat.setText("");
                                }
                            } else {
                                edt_LineSeat.setText("");
                                qcActivity.showInfo("提示", "IPQC未做首次全检", "");
                            }
                            break;

                        case 1:
                            break;

                        case 2:
                            if (isFirstCheck == 0) {
                                edt_LineSeat.setText("");
                                qcActivity.showInfo("提示", "IPQC未做首次全检", "");
                            }
                            break;
                    }
                }
                break;

            case HttpUtils.CodeIsChangeSucceed:
                int changed = Integer.valueOf(s);
                if (changed == 1) {
                    //换料成功
                    curLineSeat = ((String[]) request)[1];
                    edt_Material.requestFocus();
                } else {
                    //未换料成功
                    clearAndSetFocus();
                    showWarnResult(((String[]) request)[1]);
                }
                break;

            case HttpUtils.CodeOperate://更新visit表
                String updateVisit = "";
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    updateVisit = jsonObject.getString("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "showHttpResponse - " + e.toString());
                }
                if (!updateVisit.equals("succeed")) {
                    globalFunc.showInfo("提示", "核料失败!", "请重新核料");
                }
                break;
        }
    }

    /**
     * http返问出错回调
     *
     * @param code 请求码
     * @param s 返回信息
     */
    @Override
    public void showHttpError(int code, Object request, String s) {
        dismissLoading();
        switch (code){
            case HttpUtils.CodeAddVisit:
                globalFunc.showInfo("操作失败", "请重新操作!", "请重新操作!");
                clearAndSetFocus();
                break;
        }
    }


    private void showLoading() {
        dismissLoading();
        loadingDialog = new LoadingDialog(getActivity(), "正在加载...");
        loadingDialog.setCancelable(false);
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
