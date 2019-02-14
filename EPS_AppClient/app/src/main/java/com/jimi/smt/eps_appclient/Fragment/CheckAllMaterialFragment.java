package com.jimi.smt.eps_appclient.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jimi.smt.eps_appclient.Activity.FactoryLineActivity;
import com.jimi.smt.eps_appclient.Adapter.MaterialAdapter;
import com.jimi.smt.eps_appclient.Beans.IsAllDoneInfo;
import com.jimi.smt.eps_appclient.Beans.Material;
import com.jimi.smt.eps_appclient.Beans.Operation;
import com.jimi.smt.eps_appclient.Dao.FLCheckAll;
import com.jimi.smt.eps_appclient.Dao.GreenDaoUtil;
import com.jimi.smt.eps_appclient.Func.GlobalFunc;
import com.jimi.smt.eps_appclient.Func.HttpUtils;
import com.jimi.smt.eps_appclient.Func.Log;
import com.jimi.smt.eps_appclient.R;
import com.jimi.smt.eps_appclient.Unit.Constants;
import com.jimi.smt.eps_appclient.Unit.EvenBusTest;
import com.jimi.smt.eps_appclient.Unit.GlobalData;
import com.jimi.smt.eps_appclient.Views.InfoDialog;
import com.jimi.smt.eps_appclient.Views.InputDialog;
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


public class CheckAllMaterialFragment extends Fragment implements TextView.OnEditorActionListener, OkHttpInterface {
    private final String TAG = this.getClass().getSimpleName();

    private LoadingDialog loadingDialog;
    //全局变量
    private GlobalData globalData;
    //上料视图
    private View vCheckAllMaterialFragment;
    private MyEditTextDel edt_ScanMaterial;
    //上料列表
    private ListView lv_CheckAllMaterial;
    private MaterialAdapter materialAdapter;
    //当前检料时用到的排位料号表
    private List<Material.MaterialBean> mCheckAllMaterialBeans = new ArrayList<>();
    private static List<Material.MaterialBean> tempBeans = new ArrayList<>();
    //操作员全检纪录
    private List<FLCheckAll> flCheckAllList = new ArrayList<>();
    //是否恢复缓存
    private boolean isRestoreCache = false;
    //当前检料项
    private int curCheckId = 0;
    //长按时选择的行
    private int selectRow = -1;
    private GlobalFunc globalFunc;
    private InputDialog inputDialog;
    private InfoDialog resultInfoDialog;
    private boolean mHidden = false;
    private FactoryLineActivity factoryLineActivity;
    private HttpUtils mHttpUtils;
    //    private int checkFistCondition = -1;
    private int checkAllDoneStrCondition = -1;
    private int checkResetCondition = -1;
    private String longClickInput = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        vCheckAllMaterialFragment = inflater.inflate(R.layout.checkallmaterial_layout, container, false);
        //获取工单号和操作员
        Intent intent = getActivity().getIntent();
        savedInstanceState = intent.getExtras();
        if (savedInstanceState != null) {
            globalData.setOperator(savedInstanceState.getString("operatorNum"));
        }
        //判断是否上料
        /*
        if (factoryLineActivity.updateDialog != null && factoryLineActivity.updateDialog.isShowing()) {
            factoryLineActivity.updateDialog.cancel();
            factoryLineActivity.updateDialog.dismiss();
        }
        */
        mHttpUtils = new HttpUtils(this, getContext());
        showLoading();
//        checkFistCondition = 1;
        checkAllDoneStrCondition = 1;
//        mHttpUtils.checkAllDone(globalData.getProgramId(), Constants.FIRST_CHECK_ALL);
        mHttpUtils.checkAllDoneStr(globalData.getProgramId(), String.valueOf(Constants.FIRST_CHECK_ALL));

        initViews(savedInstanceState);
        initData();//初始化数据
        initEvents();
        return vCheckAllMaterialFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        //注册订阅
        EventBus.getDefault().register(this);
        globalData = (GlobalData) getActivity().getApplication();
        globalFunc = new GlobalFunc(getActivity());
        Log.d(TAG, "用户类型UserType：" + globalData.getUserType());
        factoryLineActivity = (FactoryLineActivity) getActivity();
        //查询本地数据库是否存在缓存
        List<FLCheckAll> flCheckAlls = new GreenDaoUtil().queryFLCheckRecord(globalData.getOperator(),
                globalData.getWork_order(), globalData.getLine(), globalData.getBoard_type());
        if (flCheckAlls != null && flCheckAlls.size() > 0) {
            flCheckAllList.addAll(flCheckAlls);
            isRestoreCache = true;
        } else {
            boolean result = new GreenDaoUtil().deleteAllFLCheck();
            Log.d(TAG, "deleteAllFLCheck - " + result);
            isRestoreCache = false;
        }
    }

    //监听订阅的消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EvenBusTest event) {
        if (event.getUpdated() == 0) {
            Log.d(TAG, "onEventMainThread - getUpdated - ");
            if (inputDialog != null && inputDialog.isShowing()) {
                inputDialog.cancel();
                inputDialog.dismiss();
                selectRow = -1;
            }
            if (resultInfoDialog != null && resultInfoDialog.isShowing()) {
                resultInfoDialog.cancel();
                resultInfoDialog.dismiss();
            }
            if (event.getFlCheckAllList() != null && event.getFlCheckAllList().size() > 0) {
                flCheckAllList.clear();
                flCheckAllList.addAll(event.getFlCheckAllList());
                curCheckId = 0;
                mCheckAllMaterialBeans.clear();

                for (int i = 0, len = flCheckAllList.size(); i < len; i++) {
                    FLCheckAll flCheckAll = flCheckAllList.get(i);
                    Material.MaterialBean bean = new Material.MaterialBean(flCheckAll.getOrder(), flCheckAll.getBoard_type(), flCheckAll.getLine(),
                            flCheckAll.getProgramId(), flCheckAll.getSerialNo(), flCheckAll.getAlternative(), flCheckAll.getSpecitification(),
                            flCheckAll.getPosition(), flCheckAll.getQuantity(), flCheckAll.getOrgLineSeat(), flCheckAll.getOrgMaterial(),
                            flCheckAll.getScanLineSeat(), flCheckAll.getScanMaterial(), flCheckAll.getResult(), flCheckAll.getRemark());
                    mCheckAllMaterialBeans.add(bean);

                    if ((null != flCheckAll.getResult()) && ((flCheckAll.getResult().equalsIgnoreCase("PASS")) || (flCheckAll.getResult().equalsIgnoreCase("FAIL")))) {
                        if (i == flCheckAllList.size() - 1) {
                            curCheckId = i;
                        } else {
                            curCheckId = i + 1;
                        }
                    }
                }
            }
            //更新显示
            materialAdapter.notifyDataSetChanged();
//            edt_ScanMaterial.requestFocus();
            Log.d(TAG, "mHidden - " + mHidden);
            Log.d(TAG, "isUpdateProgram - " + globalData.isUpdateProgram());
            //提示首检或上料
            if (!mHidden) {
                /*
                if (factoryLineActivity.updateDialog != null && factoryLineActivity.updateDialog.isShowing()) {
                    factoryLineActivity.updateDialog.cancel();
                    factoryLineActivity.updateDialog.dismiss();
                }
                */
                edt_ScanMaterial.requestFocus();
            }
        } else {
            // TODO: 2018/12/26
            if (event.getCheckAllTimeOut() == 1) {
                Log.d(TAG, "onEventMainThread - getCheckAllTimeOut - 1");
                //超时,无论是否作废重传
                if (inputDialog != null && inputDialog.isShowing()) {
                    inputDialog.cancel();
                    inputDialog.dismiss();
                    selectRow = -1;
                }
                if (resultInfoDialog != null && resultInfoDialog.isShowing()) {
                    resultInfoDialog.cancel();
                    resultInfoDialog.dismiss();
                }
                showLoading();
                boolean mReset = true;
                for (Material.MaterialBean materialItem : mCheckAllMaterialBeans) {
                    if ((null != materialItem.getResult()) && (!materialItem.getResult().equalsIgnoreCase(""))) {
                        mReset = false;
                    }
                }
                Log.d(TAG, "mReset - " + mReset);
                if (!mReset) {
                    if (event.getFlCheckAllList() != null && event.getFlCheckAllList().size() > 0) {
                        flCheckAllList.clear();
                        flCheckAllList.addAll(event.getFlCheckAllList());
                        curCheckId = 0;
                        mCheckAllMaterialBeans.clear();

                        FLCheckAll flCheckAll;
                        for (int i = 0, len = flCheckAllList.size(); i < len; i++) {
                            flCheckAll = flCheckAllList.get(i);
                            Material.MaterialBean bean = new Material.MaterialBean(flCheckAll.getOrder(), flCheckAll.getBoard_type(), flCheckAll.getLine(),
                                    flCheckAll.getProgramId(), flCheckAll.getSerialNo(), flCheckAll.getAlternative(), flCheckAll.getSpecitification(),
                                    flCheckAll.getPosition(), flCheckAll.getQuantity(), flCheckAll.getOrgLineSeat(), flCheckAll.getOrgMaterial(),
                                    flCheckAll.getScanLineSeat(), flCheckAll.getScanMaterial(), flCheckAll.getResult(), flCheckAll.getRemark());
                            mCheckAllMaterialBeans.add(bean);
                        }

                    }
                    //更新显示
                    materialAdapter.notifyDataSetChanged();
                }
                dismissLoading();
//                edt_ScanMaterial.requestFocus();
                Log.d(TAG, "mHidden - " + mHidden);
                Log.d(TAG, "isUpdateProgram - " + globalData.isUpdateProgram());
                //提示首检或上料
                if (!mHidden) {
                    /*
                    if (factoryLineActivity.updateDialog != null && factoryLineActivity.updateDialog.isShowing()) {
                        factoryLineActivity.updateDialog.cancel();
                        factoryLineActivity.updateDialog.dismiss();
                    }
                    */
                    edt_ScanMaterial.requestFocus();
                }

            }
            //未超时
            else {
                //作废重传
                if (0 == event.getProgramIdEqual()) {
                    Log.d(TAG, "getProgramIdEqual - " + event.getProgramIdEqual());
                    if (inputDialog != null && inputDialog.isShowing()) {
                        inputDialog.cancel();
                        inputDialog.dismiss();
                        selectRow = -1;
                    }
                    if (resultInfoDialog != null && resultInfoDialog.isShowing()) {
                        resultInfoDialog.cancel();
                        resultInfoDialog.dismiss();
                    }
                    if (event.getFlCheckAllList() != null && event.getFlCheckAllList().size() > 0) {
                        flCheckAllList.clear();
                        flCheckAllList.addAll(event.getFlCheckAllList());
                        curCheckId = 0;
                        for (Material.MaterialBean bean : mCheckAllMaterialBeans) {
                            bean.setProgramId(globalData.getProgramId());
                            bean.setScanlineseat("");
                            bean.setScanMaterial("");
                            bean.setRemark("");
                            bean.setResult("");
                        }
                        //更新显示
                        materialAdapter.notifyDataSetChanged();
                        if (!mHidden) {
                            edt_ScanMaterial.requestFocus();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.i(TAG, "onHiddenChanged-" + hidden);
        this.mHidden = hidden;
        if (!mHidden) {
            //判断是否首检或上料
            /*
            if (factoryLineActivity.updateDialog != null && factoryLineActivity.updateDialog.isShowing()) {
                factoryLineActivity.updateDialog.cancel();
                factoryLineActivity.updateDialog.dismiss();
            }
            */
            clearLineSeatMaterialScan();
            showLoading();
//            checkFistCondition = 1;
            checkAllDoneStrCondition = 1;
            mHttpUtils.checkAllDoneStr(globalData.getProgramId(), String.valueOf(Constants.FIRST_CHECK_ALL));
//            mHttpUtils.checkAllDone(globalData.getProgramId(), Constants.FIRST_CHECK_ALL);
        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        //注销订阅
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    //初始化控件
    private void initViews(Bundle bundle) {
        Log.i(TAG, "initViews");
        TextView tv_checkAll_order = vCheckAllMaterialFragment.findViewById(R.id.tv_checkAll_order);
        TextView edt_Operation = vCheckAllMaterialFragment.findViewById(R.id.tv_checkAll_Operation);
        edt_ScanMaterial = vCheckAllMaterialFragment.findViewById(R.id.edt_material);
        lv_CheckAllMaterial = vCheckAllMaterialFragment.findViewById(R.id.checkall_list_view);
        tv_checkAll_order.setText(bundle.getString("orderNum"));
        edt_Operation.setText(bundle.getString("operatorNum"));
        edt_ScanMaterial.requestFocus();
    }

    //初始化事件
    private void initEvents() {
        Log.i(TAG, "initEvents");
        edt_ScanMaterial.setOnEditorActionListener(this);
        //长按弹出框
        lv_CheckAllMaterial.setOnItemLongClickListener((adapterView, view, row, l) -> {
            //若未扫过不弹出框
            if (isFirstScaned()) {
                //弹出对话框
                selectRow = row;
                showLongClickDialog();
            }
            return true;
        });
    }

    //是否扫过了
    private boolean isFirstScaned() {
        boolean firstScaned = false;
        for (Material.MaterialBean materialItem : mCheckAllMaterialBeans) {
            if ((null != materialItem.getResult()) && (!(materialItem.getResult().equalsIgnoreCase("")))) {
                firstScaned = true;
                Log.d(TAG, "isFirstScaned::" + true);
                break;
            }
        }
        Log.d(TAG, "isFirstScaned::" + firstScaned);
        return firstScaned;
    }

    //弹出长按对话框
    private void showLongClickDialog() {
        Log.d(TAG, "showLongClickDialog - selectRow - " + selectRow);
        inputDialog = new InputDialog(getActivity(),
                R.layout.input_dialog_layout, new int[]{R.id.input_dialog_title, R.id.et_input}, "请重新扫描新的料号");
        inputDialog.show();
        inputDialog.setOnDialogEditorActionListener((inputDialog, v, actionId, event) -> {

            if (actionId == EditorInfo.IME_ACTION_SEND ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                switch (event.getAction()) {
                    //按下
                    case KeyEvent.ACTION_UP:
                        //先判断是否联网
                        if (globalFunc.isNetWorkConnected()) {
                            switch (v.getId()) {
                                case R.id.et_input:
                                    //扫描内容
                                    String scanValue = String.valueOf(((EditText) v).getText());
                                    scanValue = globalFunc.getMaterial(scanValue);
                                    v.setText(scanValue);
                                    longClickInput = scanValue;
                                    //检测是否首次全检
                                    showLoading();
//                                    checkFistCondition = 3;
                                    checkAllDoneStrCondition = 3;
                                    mHttpUtils.checkAllDoneStr(globalData.getProgramId(), String.valueOf(Constants.FIRST_CHECK_ALL));
//                                    mHttpUtils.checkAllDone(globalData.getProgramId(), Constants.FIRST_CHECK_ALL);
                                    break;
                            }
                        } else {
                            globalFunc.showInfo("警告", "请检查网络连接是否正常!", "请连接网络!");
                            v.setText("");
                        }
                        return true;
                    default:
                        return true;
                }
            }
            return false;
        });
    }


    //初始化数据
    private void initData() {
        Log.i(TAG, "initData");
        //填充数据
        curCheckId = 0;
        mCheckAllMaterialBeans.clear();
        tempBeans.clear();
        if (!isRestoreCache) {
            //不存在缓存
            tempBeans.addAll(globalData.getMaterialBeans());
            for (Material.MaterialBean org : tempBeans) {
                //操作员
                Material.MaterialBean bean = new Material.MaterialBean();
                bean = bean.copy(org);
                bean.setScanlineseat("");
                bean.setScanMaterial("");
                bean.setRemark("");
                bean.setResult("");
                mCheckAllMaterialBeans.add(bean);
                FLCheckAll flCheckAll = new FLCheckAll(null, bean.getProgramId(), bean.getWorkOrder(), globalData.getOperator(),
                        bean.getBoardType(), bean.getLine(), bean.getSerialNo(), bean.isAlternative(), bean.getLineseat(), bean.getMaterialNo(),
                        bean.getSpecitification(), bean.getPosition(), bean.getQuantity(), "", "", "", "");
                flCheckAllList.add(flCheckAll);
            }
            //保存到数据库中
            boolean cacheResult = new GreenDaoUtil().insertMultiFLCheckMaterial(flCheckAllList);
            Log.d(TAG, "insertMultiFLCheckMaterial - " + cacheResult);
        } else {
            //存在缓存
            FLCheckAll flCheckAll;
            for (int i = 0, len = flCheckAllList.size(); i < len; i++) {
                flCheckAll = flCheckAllList.get(i);
                Material.MaterialBean bean = new Material.MaterialBean(flCheckAll.getOrder(), flCheckAll.getBoard_type(), flCheckAll.getLine(),
                        flCheckAll.getProgramId(), flCheckAll.getSerialNo(), flCheckAll.getAlternative(), flCheckAll.getSpecitification(),
                        flCheckAll.getPosition(), flCheckAll.getQuantity(), flCheckAll.getOrgLineSeat(), flCheckAll.getOrgMaterial(),
                        flCheckAll.getScanLineSeat(), flCheckAll.getScanMaterial(), flCheckAll.getResult(), flCheckAll.getRemark());
                mCheckAllMaterialBeans.add(bean);

                if ((null != flCheckAll.getResult()) && ((flCheckAll.getResult().equalsIgnoreCase("PASS")) || (flCheckAll.getResult().equalsIgnoreCase("FAIL")))) {
                    if (i == flCheckAllList.size() - 1) {
                        curCheckId = i;
                    } else {
                        curCheckId = i + 1;
                    }
                }
            }
            //需要更新全局变量为本地数据库的,以提供更新依据
            globalData.setFactoryProgramId(mCheckAllMaterialBeans.get(0).getProgramId());
            globalData.setMaterialBeans(mCheckAllMaterialBeans);
        }

        //设置Adapter
        materialAdapter = new MaterialAdapter(getActivity(), mCheckAllMaterialBeans);
        lv_CheckAllMaterial.setAdapter(materialAdapter);
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        //回车键
        if (i == EditorInfo.IME_ACTION_SEND ||
                (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            switch (keyEvent.getAction()) {
                //按下
                case KeyEvent.ACTION_UP:
                    //先判断是否联网
                    if (globalFunc.isNetWorkConnected()) {
                        switch (textView.getId()) {
                            case R.id.edt_material:
                                //扫描内容
                                String scanValue = String.valueOf(((EditText) textView).getText());
                                scanValue = scanValue.replaceAll("\r", "");
                                Log.i(TAG, "scan Value:" + scanValue);
                                //料号,若为二维码则提取@@前的料号
                                scanValue = globalFunc.getMaterial(scanValue);
                                textView.setText(scanValue);

                                //检测是否首次全检
                                showLoading();
//                                checkFistCondition = 2;
                                checkAllDoneStrCondition = 2;
                                mHttpUtils.checkAllDoneStr(globalData.getProgramId(), String.valueOf(Constants.FIRST_CHECK_ALL));
//                                mHttpUtils.checkAllDone(globalData.getProgramId(), Constants.FIRST_CHECK_ALL);

                                break;
                        }
                    } else {
                        globalFunc.showInfo("警告", "请检查网络连接是否正常!", "请连接网络!");
                        clearLineSeatMaterialScan();
                    }
                    return true;
                default:
                    return true;
            }
        }
        return false;
    }

    private void beginOperate(int checkIndex, String scanValue, int condition) {
        Log.d(TAG, "curCheckId - " + curCheckId);
        Log.d(TAG, "checkIndex - " + checkIndex);
        //将扫描的内容更新至列表中
        Material.MaterialBean checkAllMaterialItem = mCheckAllMaterialBeans.get(checkIndex);
        //当前操作的站位
        String curCheckLineSeat = checkAllMaterialItem.getLineseat();
        //相同站位的索引数组
        ArrayList<Integer> sameLineSeatIndexs = new ArrayList<>();

        if (condition == 2) {//正常的全检操作
            //当前操作的位置
            curCheckId = curCheckId - 1;

            //遍历所有相同站位的位置
            for (int m = 0, len = mCheckAllMaterialBeans.size(); m < len; m++) {
                if (mCheckAllMaterialBeans.get(m).getLineseat().equalsIgnoreCase(curCheckLineSeat)) {
                    sameLineSeatIndexs.add(m);
                    if (m > curCheckId) {
                        //将检查的位置往后移
                        curCheckId++;
                    }
                }
            }
        } else if (condition == 3) {//长按弹窗的全检操作
            //相同站位的索引数组
            for (int i = 0, len = mCheckAllMaterialBeans.size(); i < len; i++) {
                if (mCheckAllMaterialBeans.get(i).getLineseat().equalsIgnoreCase(curCheckLineSeat)) {
                    sameLineSeatIndexs.add(i);
                }
            }
        }

        //根据站位索引数组检查料号与扫到的料号比对
        if (sameLineSeatIndexs.size() == 1) {
            //只有一个站位
            Material.MaterialBean singleMaterialItem = mCheckAllMaterialBeans.get(sameLineSeatIndexs.get(0));
            singleMaterialItem.setScanMaterial(scanValue);
            if (singleMaterialItem.getMaterialNo().equalsIgnoreCase(singleMaterialItem.getScanMaterial())) {
                singleMaterialItem.setScanlineseat(singleMaterialItem.getLineseat());
                singleMaterialItem.setResult("PASS");
                singleMaterialItem.setRemark("站位和料号正确");
            } else {
                singleMaterialItem.setScanlineseat(singleMaterialItem.getLineseat());
                singleMaterialItem.setResult("FAIL");
                singleMaterialItem.setRemark("料号与站位不对应");
            }
            //更新显示日志
            updateVisitLog(sameLineSeatIndexs, singleMaterialItem, condition);

        } else if (sameLineSeatIndexs.size() > 1) {
            //多个相同的站位,即有替换料
            checkMultiItem(sameLineSeatIndexs, scanValue, condition);
        }

        materialAdapter.notifyDataSetChanged();

        //增加数据库日志
        globalData.setOperType(Constants.CHECKALLMATERIAL);
        Operation operation = Operation.getOperation(globalData.getOperator(), Constants.CHECKALLMATERIAL, mCheckAllMaterialBeans.get(checkIndex));
        mHttpUtils.addOperation(operation);
    }

    private void checkMultiItem(ArrayList<Integer> integers, String mScanValue, int condition) {
        //多个相同的站位,即有替换料
        Material.MaterialBean bean = new Material.MaterialBean();
        boolean result = false;
        for (int i = 0; i < integers.size(); i++) {
            Material.MaterialBean multiMaterialItem = mCheckAllMaterialBeans.get(integers.get(i));
            multiMaterialItem.setScanMaterial(mScanValue);
            multiMaterialItem.setScanlineseat(multiMaterialItem.getLineseat());
            if (multiMaterialItem.getMaterialNo().equalsIgnoreCase(multiMaterialItem.getScanMaterial())) {
                result = true;
            }
        }

        if (result) {
            for (int j = 0; j < integers.size(); j++) {
                Material.MaterialBean innerMaterialItem = mCheckAllMaterialBeans.get(integers.get(j));
                innerMaterialItem.setResult("PASS");
                innerMaterialItem.setRemark("主替有一项成功");

                bean = innerMaterialItem;
            }
        } else {
            for (int j = 0; j < integers.size(); j++) {
                Material.MaterialBean innerMaterialItem = mCheckAllMaterialBeans.get(integers.get(j));
                innerMaterialItem.setResult("FAIL");
                innerMaterialItem.setRemark("料号与站位不对应");

                bean = innerMaterialItem;
            }
        }
        //更新显示日志
        updateVisitLog(integers, bean, condition);

    }

    //更新全检缓存
    private void cacheCheckResult(ArrayList<Integer> integers, Material.MaterialBean materialItem) {
        //保存缓存
        if (null != integers && integers.size() > 0) {
            for (int i = 0, len = integers.size(); i < len; i++) {
                FLCheckAll flCheckAll = flCheckAllList.get(integers.get(i));
                flCheckAll.setScanLineSeat(materialItem.getScanlineseat());
                flCheckAll.setScanMaterial(materialItem.getScanMaterial());
                flCheckAll.setResult(materialItem.getResult());
                flCheckAll.setRemark(materialItem.getRemark());
                new GreenDaoUtil().updateFLCheck(flCheckAll);
            }
        }

    }

    //清除刚刚的操作
    private void clearDisplay(ArrayList<Integer> integers) {
        if (null != integers && integers.size() > 0) {
            for (int i = 0, len = integers.size(); i < len; i++) {
                Material.MaterialBean bean = mCheckAllMaterialBeans.get(integers.get(i));
                bean.setScanlineseat("");
                bean.setScanMaterial("");
                bean.setResult("");
                bean.setRemark("");
            }
            materialAdapter.notifyDataSetChanged();
        }
    }


    // TODO: 2018/4/27
    //更新显示日志
    private void updateVisitLog(ArrayList<Integer> integers, Material.MaterialBean materialItem, int condition) {
        globalData.setUpdateType(Constants.CHECKALLMATERIAL);
        mHttpUtils.operate(integers, materialItem, Constants.CHECKALLMATERIAL, condition);
    }

    //IPQC未做首次全检
    private void showInfo() {
        //对话框所有控件id
        int itemResIds[] = new int[]{R.id.dialog_title_view, R.id.dialog_title, R.id.tv_alert_info,
                R.id.info_trust, R.id.tv_alert_msg};
        //标题和内容
        String titleMsg[] = new String[]{"提示", "IPQC未做首次全检"};
        //内容的样式
        int msgStype[] = new int[]{22, Color.RED, Color.argb(255, 219, 201, 36)};
        InfoDialog infoDialog = new InfoDialog(getActivity(), R.layout.info_dialog_layout, itemResIds, titleMsg, msgStype);
        infoDialog.setOnDialogItemClickListener((dialog, view) -> {
            switch (view.getId()) {
                case R.id.info_trust:
                    dialog.dismiss();
                    clearLineSeatMaterialScan();
                    break;
            }
        });
        infoDialog.show();
    }

    //上下一个料
    private void checkNextMaterial() {
        lv_CheckAllMaterial.setSelection(curCheckId);
        if (curCheckId < mCheckAllMaterialBeans.size() - 1) {
            curCheckId++;
            clearLineSeatMaterialScan();
        } else {
            showCheckAllMaterialResult(0);
        }
        Log.i(TAG, "checkNextMaterial:" + curCheckId);
    }

    private void showCheckAllMaterialResult(int showType) {
        boolean checkResult = true;
        String[] titleMsg;
        int[] msgStyle;
        for (Material.MaterialBean checkMaterialItem : mCheckAllMaterialBeans) {
            if ((null == checkMaterialItem.getResult()) || (!(checkMaterialItem.getResult().equals("PASS")))) {
                checkResult = false;
                break;
            }
        }
        if ((showType == 0) || (checkResult && (showType == 1))) {

            if (checkResult) {
                titleMsg = new String[]{"全检完成", "PASS"};
                msgStyle = new int[]{66, Color.argb(255, 102, 153, 0)};
            } else {
                titleMsg = new String[]{"全检未完成，请检查!", "请继续全检"};
                msgStyle = new int[]{66, Color.argb(255, 212, 179, 17)};
            }
            showResultInfo(titleMsg, msgStyle, checkResult);
        }
    }


    //弹出消息窗口
    private void showResultInfo(String[] titleMsg, int[] msgStyle, final boolean result) {

        //对话框所有控件id
//        int itemResIds[] = new int[]{R.id.dialog_title_view,
//                R.id.dialog_title, R.id.tv_alert_info, R.id.info_trust};


        int itemResIds[] = new int[]{R.id.dialog_title_view,
                R.id.dialog_title, R.id.tv_alert_info, R.id.info_trust, R.id.tv_alert_msg};

        resultInfoDialog = new InfoDialog(getActivity(),
                R.layout.info_dialog_layout, itemResIds, titleMsg, msgStyle);

        resultInfoDialog.setOnDialogItemClickListener((dialog, view) -> {
            switch (view.getId()) {
                case R.id.info_trust:
                    dialog.dismiss();
                    if (result) {
                        clearMaterialInfo();
                        //将全检的时间设为当前时间
                        mHttpUtils.resetCheckAllRR(globalData.getProgramId());
                    } else {
                        edt_ScanMaterial.setText("");
                        edt_ScanMaterial.requestFocus();
                    }
                    break;
            }
        });
        resultInfoDialog.show();
    }

    //清空之前的全检信息进入下一轮全检
    private void clearMaterialInfo() {
        Log.d(TAG, "clearMaterialInfo");
        clearLineSeatMaterialScan();
        curCheckId = 0;
        clearCheckAllDisplay();
        boolean result = new GreenDaoUtil().updateAllFLCheck(flCheckAllList);
        Log.d(TAG, "updateAllFLCheck - " + result);
    }

    //清除全检结果
    private void clearCheckAllDisplay() {
        for (int i = 0, len = mCheckAllMaterialBeans.size(); i < len; i++) {
            Material.MaterialBean materialItem = mCheckAllMaterialBeans.get(i);
            materialItem.setScanlineseat("");
            materialItem.setScanMaterial("");
            materialItem.setResult("");
            materialItem.setRemark("");
            FLCheckAll flCheckAll = flCheckAllList.get(i);
            flCheckAll.setScanLineSeat("");
            flCheckAll.setScanMaterial("");
            flCheckAll.setResult("");
            flCheckAll.setRemark("");

        }
        materialAdapter.notifyDataSetChanged();
    }

    private void clearLineSeatMaterialScan() {
        edt_ScanMaterial.setText("");
        edt_ScanMaterial.requestFocus();
    }


    /**
     * http返问回调
     *
     * @param code 请求码
     * @param s    返回信息
     */
    @Override
    public void showHttpResponse(int code, Object request, String s) {
        Log.d(TAG, "showHttpResponse - " + s);
        dismissLoading();
        switch (code) {
            /*
            case HttpUtils.CodeIsAllDone:
                int checkFirst = Integer.valueOf(s);
                switch (checkFistCondition) {
                    case 1:
                        if (checkFirst == 0) {
                            //未首检
                            showInfo();
                            checkResetCondition = 4;
                            mHttpUtils.isReset(globalData.getProgramId(), Constants.CHECKALLMATERIAL);
                        }
                        break;

                    case 2://正常扫料号
                        if (checkFirst == 1) {
                            //已首检，检测是否重置
                            checkResetCondition = 2;
                            mHttpUtils.isReset(globalData.getProgramId(), Constants.CHECKALLMATERIAL);
                        } else {
                            //未首检
                            showInfo();
                            checkResetCondition = 4;
                            mHttpUtils.isReset(globalData.getProgramId(), Constants.CHECKALLMATERIAL);
                        }
                        break;

                    case 3://长按扫料号
                        if (checkFirst == 1) {
                            //已首检，检测是否重置
                            checkResetCondition = 3;
                            mHttpUtils.isReset(globalData.getProgramId(), Constants.CHECKALLMATERIAL);
                        } else {
                            //未首检
                            showInfo();
                            checkResetCondition = 4;
                            mHttpUtils.isReset(globalData.getProgramId(), Constants.CHECKALLMATERIAL);
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
                        case 1:
                            if (isFirstCheck == 0) {
                                //未首检
                                showInfo();
                                checkResetCondition = 4;
                                mHttpUtils.isReset(globalData.getProgramId(), Constants.CHECKALLMATERIAL);
                            }
                            break;

                        case 2://正常扫料号
                            if (isFirstCheck == 1) {
                                //已首检，检测是否重置
                                checkResetCondition = 2;
                                mHttpUtils.isReset(globalData.getProgramId(), Constants.CHECKALLMATERIAL);
                            } else {
                                //未首检
                                showInfo();
                                checkResetCondition = 4;
                                mHttpUtils.isReset(globalData.getProgramId(), Constants.CHECKALLMATERIAL);
                            }
                            break;

                        case 3://长按扫料号
                            if (isFirstCheck == 1) {
                                //已首检，检测是否重置
                                checkResetCondition = 3;
                                mHttpUtils.isReset(globalData.getProgramId(), Constants.CHECKALLMATERIAL);
                            } else {
                                //未首检
                                showInfo();
                                checkResetCondition = 4;
                                mHttpUtils.isReset(globalData.getProgramId(), Constants.CHECKALLMATERIAL);
                            }
                            break;
                    }
                }
                break;

            case HttpUtils.CodeCheckIsReset:
                int reset = Integer.valueOf(s);
                //本地数据结果是否清空
                boolean mReset = true;
                for (Material.MaterialBean materialItem : mCheckAllMaterialBeans) {
                    if ((null != materialItem.getResult()) && (!materialItem.getResult().equalsIgnoreCase(""))) {
                        mReset = false;
                    }
                }
                Log.d(TAG, "reset - " + reset);
                Log.d(TAG, "mReset - " + mReset);
                switch (checkResetCondition) {
                    case 2://正常扫料号
                        if (reset == 0) {
                            //未重置,操作
                            beginOperate(curCheckId, edt_ScanMaterial.getText().toString().trim(), 2);
                        } else if (reset == 1) {
                            if (mReset) {
                                //操作
                                beginOperate(curCheckId, edt_ScanMaterial.getText().toString().trim(), 2);
                            } else {
                                //重置
                                clearMaterialInfo();
                            }
                        }
                        break;

                    case 3://长按扫料号
                        if (reset == 0) {
                            //未重置,操作
                            beginOperate(selectRow, longClickInput, 3);
                        } else if (reset == 1) {
                            if (mReset) {
                                //操作
                                beginOperate(selectRow, longClickInput, 3);
                            } else {
                                //重置
                                clearMaterialInfo();
                            }
                        }
                        break;

                    case 4:
                        if (reset == 1 && !mReset) {
                            clearMaterialInfo();
                        }
                        edt_ScanMaterial.setText("");
                        break;
                }
                break;

            case HttpUtils.CodeResetCheckAll:
                //设置全检的时间为当前时间
                break;

            case HttpUtils.CodeOperate:
                //更新visit表
                String result = "";
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    result = jsonObject.getString("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "showHttpResponse - " + e.toString());
                }
                ArrayList<Integer> integers = (ArrayList<Integer>) ((Object[]) request)[0];
                Material.MaterialBean bean = (Material.MaterialBean) ((Object[]) request)[1];
                int condition = (int) ((Object[]) request)[2];
                if ((result.equalsIgnoreCase("succeed"))) {
                    //更新本地缓存
                    cacheCheckResult(integers, bean);
                    //清空站位
                    if (condition == 2) {//正常的全检操作
                        //检查下一个料
                        checkNextMaterial();
                    } else if (condition == 3) {//长按弹窗的全检操作
                        //判断全是否全部正确
                        showCheckAllMaterialResult(1);
                        if (inputDialog != null && inputDialog.isShowing()) {
                            inputDialog.dismiss();
                        }
                        edt_ScanMaterial.requestFocus();
                    }

                } else {
                    //清除刚刚的操作
                    clearDisplay(integers);
                    if (condition == 2) {
                        //正常的全检操作,回退
                        if (null != integers && integers.size() > 0) {
                            curCheckId -= integers.size();
                        }
                    }
                    clearLineSeatMaterialScan();
                }

                break;
        }
    }


    /**
     * http返问出错回调
     *
     * @param code 请求码
     * @param s    返回信息
     */
    @Override
    public void showHttpError(int code, Object request, String s) {
        Log.d(TAG, "showHttpResponse - " + s);
        Log.d(TAG, "showHttpResponse - code - " + code);
        dismissLoading();
//        globalFunc.showInfo("警告", "请检查网络连接是否正常!", "请连接网络!");
        switch (code) {
            case HttpUtils.CodeOperate:
                //更新visit表
                globalFunc.showInfo("操作失败", "请重新操作!", "请重新操作!");
                ArrayList<Integer> integers = (ArrayList<Integer>) ((Object[]) request)[0];
                Material.MaterialBean bean = (Material.MaterialBean) ((Object[]) request)[1];
                int condition = (int) ((Object[]) request)[2];
                //清除刚刚的操作
                clearDisplay(integers);
                if (condition == 2) {
                    //正常的全检操作,回退
                    /*
                    if ((null != integers && integers.size() > 0) && (curCheckId > 0)) {
                        curCheckId -= integers.size();
                    }
                    */
                    if (curCheckId <= 0) {
                        lv_CheckAllMaterial.setSelection(0);
                    } else {
                        lv_CheckAllMaterial.setSelection(curCheckId - 1);
                    }
                }
                clearLineSeatMaterialScan();
                break;
        }
    }


    private void showLoading() {
        dismissLoading();
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
