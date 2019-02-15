package com.jimi.smt.eps_appclient.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jimi.smt.eps_appclient.Adapter.WareHouseAdapter;
import com.jimi.smt.eps_appclient.Beans.Material;
import com.jimi.smt.eps_appclient.Beans.Operation;
import com.jimi.smt.eps_appclient.Dao.GreenDaoUtil;
import com.jimi.smt.eps_appclient.Dao.Ware;
import com.jimi.smt.eps_appclient.Func.GlobalFunc;
import com.jimi.smt.eps_appclient.Func.HttpUtils;
import com.jimi.smt.eps_appclient.Func.Log;
import com.jimi.smt.eps_appclient.Interfaces.OkHttpInterface;
import com.jimi.smt.eps_appclient.Unit.GlobalData;
import com.jimi.smt.eps_appclient.R;
import com.jimi.smt.eps_appclient.Service.RefreshCacheService;
import com.jimi.smt.eps_appclient.Unit.Constants;
import com.jimi.smt.eps_appclient.Unit.EvenBusTest;
import com.jimi.smt.eps_appclient.Views.InfoDialog;
import com.jimi.smt.eps_appclient.Views.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 类名:WareHouseActivity
 * 创建人:Liang GuoChang
 * 创建时间:2017/10/19 19:29
 * 描述: 仓库 Activity
 */
public class WareHouseActivity extends Activity implements View.OnClickListener, TextView.OnEditorActionListener, OkHttpInterface {

    private final String TAG = "WareHouseActivity";

    private String curOrderNum;//当前工单号
    private String curOperatorNUm;//当前操作员
    private GlobalData globalData;
    private List<Material.MaterialBean> mWareMaterialBeans = new ArrayList<>();//料号表
    private static List<Material.MaterialBean> tempBeans = new ArrayList<>();
    private ListView lv_ware_materials;//所有料号列表
    private EditText et_ware_scan_material;//扫描的料号
    private WareHouseAdapter wareHouseAdapter;
    private InfoDialog infoDialog;//弹出料号对应站位
    private InfoDialog wareResultDialog;//弹出发料结果
    private int sucIssueCount = 0;//成功发料个数
    private int allCount = 0;//总数
    private GlobalFunc globalFunc;
    private LoadingDialog loadingDialog;

    //上料本地数据表
    private List<Ware> wareList = new ArrayList<>();
    //是否恢复缓存
    private boolean isRestoreCache = false;

    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;
    private static final int doEXIT = 121;//退出
    private HttpUtils mHttpUtils;
    //未扫过料的站位
    private ArrayList<String> lineSeatAl = new ArrayList<>();
    //已经扫描过料号的站位
    private ArrayList<String> wareSeatList = new ArrayList<>();
    //当前扫的料号
    private String scanMaterial;
    //当前料号对应的不同站位的所有料号的位置
    private ArrayList<ArrayList<Integer>> arrayLists = new ArrayList<>();
    //当前操作的位置
    private int curSelectIndex = -1;
    private int wareCount = -1;

    @SuppressLint("HandlerLeak")
    private Handler mWareHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case doEXIT:
                    isExit = false;
                    break;
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("lifecycle-", TAG + "--onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ware_house);
        //使屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //开启服务
        startService(new Intent(this, RefreshCacheService.class));
        //注册订阅
        EventBus.getDefault().register(this);
        mHttpUtils = new HttpUtils(this, getApplicationContext());
        //全局变量
        globalData = (GlobalData) getApplication();
        Intent intent = getIntent();
        savedInstanceState = intent.getExtras();
        curOrderNum = savedInstanceState.getString("orderNum");
        curOperatorNUm = savedInstanceState.getString("operatorNum");
        globalData.setOperator(curOperatorNUm);
        globalData.setOperType(Constants.STORE_ISSUE);
        globalData.setUpdateType(Constants.STORE_ISSUE);
        globalFunc = new GlobalFunc(WareHouseActivity.this);

        //查询本地数据库是否存在缓存
        List<Ware> wares = new GreenDaoUtil().queryWareRecord(globalData.getOperator(), globalData.getWork_order()
                , globalData.getLine(), globalData.getBoard_type());

        //数据库存在缓存数据
        if (wares.size() != 0) {
            //保存缓存
            wareList.addAll(wares);
            isRestoreCache = true;
        } else {
            //不存在缓存数据,删除之前的数据
            boolean result = new GreenDaoUtil().deleteAllWareData();
            Log.d(TAG, "deleteAllWareData - " + result);
            isRestoreCache = false;
        }

        //初始化页面
        initViews();
        initData();
    }


    //初始化数据
    private void initData() {
        sucIssueCount = 0;
        mWareMaterialBeans.clear();
        //没有数据库缓存
        if (!isRestoreCache) {
            //填充数据
            tempBeans.addAll(globalData.getMaterialBeans());
            for (Material.MaterialBean org : tempBeans) {
                Material.MaterialBean bean = new Material.MaterialBean();
                bean = bean.copy(org);
                bean.setScanlineseat("");
                bean.setScanMaterial("");
                bean.setRemark("");
                bean.setResult("");
                mWareMaterialBeans.add(bean);
                Ware ware = new Ware(null, bean.getProgramId(), bean.getWorkOrder(), globalData.getOperator(),
                        bean.getBoardType(), bean.getLine(), bean.getSerialNo(), bean.isAlternative(), bean.getSpecitification(),
                        bean.getPosition(), bean.getQuantity(), bean.getLineseat(), bean.getMaterialNo(), bean.getScanlineseat(),
                        bean.getScanMaterial(), bean.getResult(), bean.getRemark());
                wareList.add(ware);
            }
            //保存到数据库中
            boolean cacheResult = new GreenDaoUtil().insertMultiWareMaterial(wareList);
            Log.d(TAG, "cacheResult - " + cacheResult);

        }
        //存在缓存
        else {
            for (Ware ware : wareList) {
                Material.MaterialBean bean = new Material.MaterialBean(ware.getOrder(), ware.getBoard_type(), ware.getLine(),
                        ware.getProgramId(), ware.getSerialNo(), ware.getAlternative(), ware.getSpecitification(), ware.getPosition(),
                        ware.getQuantity(), ware.getOrgLineSeat(), ware.getOrgMaterial(), ware.getScanLineSeat(), ware.getScanMaterial(),
                        ware.getResult(), ware.getRemark());
                mWareMaterialBeans.add(bean);
//                Log.d(TAG, "bean - " + bean.getLineseat());
//                Log.d(TAG, "bean - " + bean.getSerialNo());

                //获取成功发料次数
                if ((null != ware.getResult()) && (ware.getResult().equalsIgnoreCase("PASS"))) {
                    sucIssueCount++;
                }
            }

            //需要更新全局变量为本地数据库的,以提供更新依据
            globalData.setWareProgramId(mWareMaterialBeans.get(0).getProgramId());
            globalData.setMaterialBeans(mWareMaterialBeans);
        }

        allCount = mWareMaterialBeans.size();
        //设置Adapter
        wareHouseAdapter = new WareHouseAdapter(getApplicationContext(), mWareMaterialBeans);
        lv_ware_materials.setAdapter(wareHouseAdapter);
        Log.d(TAG, "sucIssueCount - " + sucIssueCount);
        Log.d(TAG, "allCount - " + allCount);
    }

    //初始化页面
    private void initViews() {
        Log.i(TAG, "curOderNum::" + curOrderNum + " -- curOperatorNUm::" + curOperatorNUm);
        ImageView iv_ware_back = findViewById(R.id.iv_ware_back);
        TextView tv_ware_order = findViewById(R.id.tv_ware_order);
        TextView tv_ware_operator = findViewById(R.id.tv_ware_operator);
        et_ware_scan_material = findViewById(R.id.et_ware_scan_material);
        lv_ware_materials = findViewById(R.id.lv_ware_materials);
        iv_ware_back.setOnClickListener(this);
        et_ware_scan_material.setOnEditorActionListener(this);
        tv_ware_order.setText(curOrderNum);
        tv_ware_operator.setText(curOperatorNUm);
    }

    //按钮点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_ware_back:
                exit();
                break;
        }
    }


    //监听订阅的消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EvenBusTest event) {
        //更新
        if (event.getUpdated() == 0) {
            Log.d(TAG, "onEventMainThread - " + event.getUpdated());
            showUpdateDialog("提示", "站位表更新!", "站位表更新!");
            if (event.getWareList() != null && event.getWareList().size() > 0) {
                //更新页面
                wareList.clear();
                wareList.addAll(event.getWareList());
                sucIssueCount = 0;
                //填充数据
                mWareMaterialBeans.clear();
                for (Ware ware : wareList) {
                    Material.MaterialBean bean = new Material.MaterialBean(ware.getOrder(), ware.getBoard_type(), ware.getLine(),
                            ware.getProgramId(), ware.getSerialNo(), ware.getAlternative(), ware.getSpecitification(), ware.getPosition(), ware.getQuantity(),
                            ware.getOrgLineSeat(), ware.getOrgMaterial(), ware.getScanLineSeat(), ware.getScanMaterial(), ware.getResult(), ware.getRemark());
                    mWareMaterialBeans.add(bean);
                    //获取成功发料次数
                    if ((null != ware.getResult()) && (ware.getResult().equalsIgnoreCase("PASS"))) {
                        sucIssueCount++;
                    }
                }
                allCount = mWareMaterialBeans.size();
                //更新显示
                wareHouseAdapter.notifyDataSetChanged();
                //重新开始扫描料号
                et_ware_scan_material.requestFocus();
                et_ware_scan_material.setText("");
            }
        }
        //未更新
        else {
            if (0 == event.getProgramIdEqual()) {
                showUpdateDialog("提示", "站位表作废并重传！", "站位表作废并重传！");
                if (event.getWareList() != null && event.getWareList().size() > 0) {
                    //更新页面
                    wareList.clear();
                    wareList.addAll(event.getWareList());
                    sucIssueCount = 0;
                    //填充数据
                    for (Material.MaterialBean bean : mWareMaterialBeans) {
                        bean.setProgramId(globalData.getProgramId());
                        bean.setScanlineseat("");
                        bean.setScanMaterial("");
                        bean.setRemark("");
                        bean.setResult("");
                    }
                    allCount = mWareMaterialBeans.size();
                    //更新显示
                    wareHouseAdapter.notifyDataSetChanged();
                    //重新开始扫描料号
                    et_ware_scan_material.requestFocus();
                    et_ware_scan_material.setText("");
                }
            }
        }
    }

    // TODO: 2018/9/12
    private void showUpdateDialog(String title, String msg, String toast) {
        if (wareResultDialog != null && wareResultDialog.isShowing()) {
            wareResultDialog.cancel();
            wareResultDialog.dismiss();
        }
        Log.d(TAG, "showUpdateDialog");

        globalFunc.showInfo(title, msg, toast);
//        globalData.setUpdateProgram(false);


        /*
        final LoadingDialog loadingDialog = new LoadingDialog(this, "站位表更新...");
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                loadingDialog.cancel();
                loadingDialog.dismiss();
            }
        }).start();

        */


    }

    @Override
    protected void onDestroy() {
        //注销订阅
        EventBus.getDefault().unregister(this);
        //关闭服务
        stopService(new Intent(this, RefreshCacheService.class));
        super.onDestroy();
        Log.i("lifecycle-", TAG + "--onDestroy");
    }

    //物理返回键
    @Override
    public void onBackPressed() {
        exit();
    }

    //扫料号的输入事件
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        if (actionId == EditorInfo.IME_ACTION_SEND ||
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            clearLastOperate();
            //先判断是否联网
            if (globalFunc.isNetWorkConnected()) {
                Log.d(TAG, "onEditorAction::" + v.getText());
//                Log.d(TAG, "event.getAction()::" + event.getAction());

                if (!TextUtils.isEmpty(v.getText().toString().trim())) {
                    //扫描的内容
                    scanMaterial = String.valueOf(((EditText) v).getText());
                    scanMaterial = scanMaterial.replaceAll("\r", "");
                    Log.i(TAG, "sacnMaterial = " + scanMaterial);
                    //料号,若为二维码则提取@@前的料号
                    //提取有效料号
                    scanMaterial = globalFunc.getMaterial(scanMaterial);
                    v.setText(scanMaterial);
                    //未扫描过料号的站位及结果
                    HashSet<String> lineSeatHashSet = new HashSet<>();
                    //未扫描过料号的站位
                    ArrayList<String> lineSeatList = new ArrayList<>();
                    //已经扫描过料号的站位及结果
                    HashSet<String> scanedSeatSet = new HashSet<>();

                    for (int i = 0, len = mWareMaterialBeans.size(); i < len; i++) {
                        Material.MaterialBean bean = mWareMaterialBeans.get(i);
                        if (bean.getMaterialNo().equalsIgnoreCase(scanMaterial)) {
                            int serialNo = bean.getSerialNo();
                            if ((null == bean.getResult()) || (!bean.getResult().equalsIgnoreCase("PASS"))) {
                                lineSeatHashSet.add("(" + String.valueOf(serialNo) + ")" + " " + bean.getLineseat());
                                lineSeatList.add(bean.getLineseat());
                            } else if (bean.getResult().equalsIgnoreCase("PASS")) {
                                scanedSeatSet.add("(" + String.valueOf(serialNo) + ")" + " " + bean.getLineseat() + "(已经发料)");
                            }
                        }
                    }

                    //未扫过料的站位
                    lineSeatAl.addAll(lineSeatHashSet);
                    //已经扫描过料号的站位
                    wareSeatList.addAll(scanedSeatSet);

                    //arrayLists的外部长度等于lineSeatList的长度
                    for (int k = 0, len = lineSeatList.size(); k < len; k++) {
                        ArrayList<Integer> lineSeatIndex = new ArrayList<>();
                        for (int j = 0, length = mWareMaterialBeans.size(); j < length; j++) {
                            Material.MaterialBean bean = mWareMaterialBeans.get(j);
                            if (bean.getLineseat().equalsIgnoreCase(lineSeatList.get(k))) {
                                bean.setScanlineseat(bean.getLineseat());
                                bean.setScanMaterial(scanMaterial);
                                bean.setResult("PASS");
                                lineSeatIndex.add(j);
                                //成功次数加1
                                sucIssueCount++;
                            }
                        }
                        arrayLists.add(lineSeatIndex);
                    }

                    //写日志
                    showLoading();
                    setOperateLog(arrayLists, scanMaterial);
                }
            } else {
                showInfo("警告", null, null, 2);
            }
        }
        return false;
    }

    /**
     * 弹出提示站位窗口
     *
     * @param title        标题
     * @param lineSeatList 未发料的站位
     * @param wareSeatList 已发料的站位
     * @param type         类型
     */
    private void showInfo(String title, ArrayList<String> lineSeatList, ArrayList<String> wareSeatList, int type) {
        //未发料的站位
        StringBuilder message = new StringBuilder();
        //已经扫描过料号的站位
        StringBuilder wareSeatStr = new StringBuilder();
        //内容的样式
        int msgStype[];
        //标题和内容
        String titleMsg[];
        if ((lineSeatList != null) && (lineSeatList.size() > 0)) {
            //添加所有站位
            message = new StringBuilder("站位:");
            for (String lineSeat : lineSeatList) {
                message.append("\n").append(lineSeat);
            }
            if (wareSeatList == null || wareSeatList.size() <= 0) {
                //只存在未扫描过站位,
                msgStype = new int[]{23, Color.argb(255, 102, 153, 0)};
                titleMsg = new String[]{title, message.toString()};
            } else {
                //同时存在已经扫描过料号的站位
                for (String wareSeat : wareSeatList) {
                    wareSeatStr.append("\n").append(wareSeat);
                }
                msgStype = new int[]{23, Color.argb(255, 102, 153, 0), Color.argb(255, 219, 201, 36)};
                titleMsg = new String[]{title, message.toString(), wareSeatStr.toString()};
            }

        } else {
            if (wareSeatList != null && wareSeatList.size() > 0) {
                //只存在已经扫描过料号的站位
                for (String wareSeat : wareSeatList) {
                    wareSeatStr.append("\n").append(wareSeat);
                }
                msgStype = new int[]{23, Color.argb(255, 219, 201, 36)};
                titleMsg = new String[]{title, wareSeatStr.toString()};
            } else {
                if (type == 1) {
                    //写日志
                    setOperateLog(null, title);
                    //站位不存在
                    message = new StringBuilder("不存在该料号的站位!");
                } else if (type == 2) {
                    //网络未连接
                    message = new StringBuilder("请检查网络是否连接!");
                }
                titleMsg = new String[]{title, message.toString()};
                //内容的样式
                msgStype = new int[]{22, Color.RED};
            }
        }

        //对话框所有控件id
        int itemResIds[] = new int[]{R.id.dialog_title_view,
                R.id.dialog_title, R.id.tv_alert_info, R.id.info_trust, R.id.tv_alert_msg};

        infoDialog = new InfoDialog(this,
                R.layout.info_dialog_layout, itemResIds, titleMsg, msgStype);
        //确定按钮点击事件
        infoDialog.setOnDialogItemClickListener((dialog, view) -> {
            switch (view.getId()) {
                case R.id.info_trust:
                    dialog.dismiss();
                    break;
            }
        });
        //弹出窗取消事件监听
        infoDialog.setOnDismissListener(dialog -> {
            et_ware_scan_material.requestFocus();
            et_ware_scan_material.setText("");
            showIssueResult();
        });
        infoDialog.show();

    }

    //显示最终发料结果
    private void showIssueResult() {
        Log.d(TAG, "sucIssueCount-" + sucIssueCount
                + "\nwareHouseMaterialItems-" + mWareMaterialBeans.size() + "\nallCount-" + allCount);
        if (sucIssueCount >= mWareMaterialBeans.size() && sucIssueCount >= allCount) {
            //显示最终结果
            boolean result = true;
            for (Material.MaterialBean materialItem : mWareMaterialBeans) {
                if ((null == materialItem.getResult()) || (!materialItem.getResult().equalsIgnoreCase("PASS"))) {
                    result = false;
                    break;
                }
            }
            //弹出发料结果
            String titleMsg[];
            int msgStyle[];
            if (result) {
                titleMsg = new String[]{"发料完成", "PASS"};
                msgStyle = new int[]{66, Color.argb(255, 102, 153, 0)};
            } else {
                titleMsg = new String[]{"发料未完成，请检查!", "请继续发料"};
                msgStyle = new int[]{66, Color.argb(255, 212, 179, 17)};
            }
            showIssueInfo(titleMsg, msgStyle, result);
        }
    }

    //发料结果
    private void showIssueInfo(String[] titleMsg, int[] msgStyle, final boolean result) {
        //对话框所有控件id
//        int itemResIds[] = new int[]{R.id.dialog_title_view,
//                R.id.dialog_title, R.id.tv_alert_info, R.id.info_trust};

        int itemResIds[] = new int[]{R.id.dialog_title_view,
                R.id.dialog_title, R.id.tv_alert_info, R.id.info_trust, R.id.tv_alert_msg};

        wareResultDialog = new InfoDialog(this,
                R.layout.info_dialog_layout, itemResIds, titleMsg, msgStyle);

        wareResultDialog.setOnDialogItemClickListener((dialog, view) -> {
            switch (view.getId()) {
                case R.id.info_trust:
                    if (result) {
                        dialog.dismiss();
                        et_ware_scan_material.requestFocus();
                        et_ware_scan_material.setText("");
                        boolean result1 = new GreenDaoUtil().updateAllWare(wareList);
                        Log.d(TAG, "updateAllWare - " + result1);
                        clearWareDisplay();
                    } else {
                        dialog.dismiss();
                        et_ware_scan_material.requestFocus();
                        et_ware_scan_material.setText("");
                        //将未成功数加到
                        for (Material.MaterialBean bean : mWareMaterialBeans) {
                            if ((null == bean.getResult()) || (!bean.getResult().equalsIgnoreCase("PASS"))) {
                                allCount++;
                            }
                        }
                    }
                    break;
            }
        });
        wareResultDialog.show();
    }

    //清除发料页面结果
    private void clearWareDisplay() {
        sucIssueCount = 0;
        allCount = 0;
        for (int i = 0, len = mWareMaterialBeans.size(); i < len; i++) {
            Material.MaterialBean bean = mWareMaterialBeans.get(i);
            bean.setScanlineseat("");
            bean.setScanMaterial("");
            bean.setResult("");
            bean.setRemark("");
            Ware ware = wareList.get(i);
            ware.setScanLineSeat("");
            ware.setScanMaterial("");
            ware.setResult("");
            ware.setRemark("");
            allCount++;
        }
        wareHouseAdapter.notifyDataSetChanged();
    }

    //写日志并显示 
    private void setOperateLog(ArrayList<ArrayList<Integer>> integerLists, String scanMaterial) {
        wareCount = -1;
        if (integerLists != null) {
            if (integerLists.size() > 0) {
                wareCount = integerLists.size();
                Log.d(TAG, "setOperateLog::" + integerLists.size());
                ArrayList<Integer> lineSeatIndex = new ArrayList<>();
                for (int m = 0; m < wareCount; m++) {
                    lineSeatIndex.clear();
                    lineSeatIndex.addAll(integerLists.get(m));

                    Material.MaterialBean operate = new Material.MaterialBean();
                    for (int n = 0, nLen = lineSeatIndex.size(); n < nLen; n++) {
                        Material.MaterialBean bean = mWareMaterialBeans.get(lineSeatIndex.get(n));
                        boolean curAlternative = bean.isAlternative();
                        if (lineSeatIndex.size() > 1) {
                            //当前扫的料
                            if (bean.getMaterialNo().equalsIgnoreCase(scanMaterial)) {
                                bean.setRemark("该料发料成功");
                                //赋值
                                curSelectIndex = lineSeatIndex.get(n);
                                operate = bean;
                            } else {
                                //该站位的其他料
                                if (curAlternative) {
                                    //当前扫的料是主料
                                    bean.setRemark("主料" + "发料成功");
                                } else {
                                    //当前扫的料是替料
                                    bean.setRemark("替料" + "发料成功");
                                }
                            }

                        } else {
                            curSelectIndex = lineSeatIndex.get(0);
                            bean.setRemark("发料成功");
                            //赋值
                            operate = bean;
                        }

                    }

                    Log.d(TAG, "curSelectIndex - " + curSelectIndex);

                    //更新visit表
                    com.jimi.smt.eps_appclient.Beans.ProgramItemVisit itemVisit = com.jimi.smt.eps_appclient.Beans.ProgramItemVisit.getProgramItemVisit(Constants.STORE_ISSUE, operate);
                    mHttpUtils.updateVisit(operate, itemVisit, m);
                    //添加日志
//                    Log.d(TAG, "operate " + operate.getMaterialStr());
                    Operation operation = Operation.getOperation(curOperatorNUm, Constants.STORE_ISSUE, operate);
                    mHttpUtils.addOperation(operation);
                }
            } else {
                //弹出站位
                dismissLoading();
                showInfo("料号:" + scanMaterial, lineSeatAl, wareSeatList, 1);
            }
        } else {
            //添加日志
            Material.MaterialBean bean = new Material.MaterialBean(globalData.getWork_order(), globalData.getBoard_type(), globalData.getLine(), globalData.getProgramId(),
                    -1, false, "", "", -1, "", "", "", "", "FAIL", "不存在该料号的站位!");
            Operation operation = Operation.getOperation(curOperatorNUm, Constants.STORE_ISSUE, bean);
            mHttpUtils.addOperation(operation);
        }
    }

    //更新visit表失败时，清除操作显示
    private void clearDisplay(ArrayList<ArrayList<Integer>> integerLists) {
        ArrayList<Integer> lineSeatIndex = new ArrayList<>();
        for (int m = 0, len = integerLists.size(); m < len; m++) {
            lineSeatIndex.clear();
            lineSeatIndex.addAll(integerLists.get(m));
        }
        for (int k = 0, len = lineSeatIndex.size(); k < len; k++) {
            Material.MaterialBean bean = mWareMaterialBeans.get(lineSeatIndex.get(k));
            bean.setResult("");
            bean.setRemark("");
            bean.setScanlineseat("");
            bean.setScanMaterial("");
        }
        //刷新数据
        wareHouseAdapter.notifyDataSetChanged();
        et_ware_scan_material.requestFocus();
        et_ware_scan_material.setText("");
    }

    //更新本地数据库发料缓存
    private void cacheWareResult(ArrayList<ArrayList<Integer>> integerLists) {
        ArrayList<Integer> lineSeatIndex = new ArrayList<>();
        for (int m = 0, len = integerLists.size(); m < len; m++) {
            lineSeatIndex.clear();
            lineSeatIndex.addAll(integerLists.get(m));

            for (int n = 0, length = lineSeatIndex.size(); n < length; n++) {
                Material.MaterialBean bean = mWareMaterialBeans.get(lineSeatIndex.get(n));
                //保存缓存
                Ware ware = wareList.get(lineSeatIndex.get(n));
                ware.setScanLineSeat(bean.getScanlineseat());
                ware.setScanMaterial(bean.getScanMaterial());
                ware.setResult(bean.getResult());
                ware.setRemark(bean.getRemark());
                GreenDaoUtil.getGreenDaoUtil().updateWare(ware);
            }
        }
    }

    //清除上一次操作
    private void clearLastOperate() {
        curSelectIndex = -1;
        lineSeatAl.clear();
        wareSeatList.clear();
        arrayLists.clear();
    }

    //返回主页
    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            Message message = Message.obtain();
            message.what = doEXIT;
            mWareHandler.sendMessageDelayed(message, 2000);
        } else {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            this.finish();
        }
    }

    @Override
    public void showHttpResponse(int code, Object request, String response) {
        Log.d(TAG, "showHttpResponse - code:" + code + "\nresponse:" + response);
        int resCode = -1;
        try {
            JSONObject jsonObject = new JSONObject(response);
            resCode = jsonObject.getInt("code");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "showHttpResponse - " + response);
        }
        switch (code) {
            case HttpUtils.CodeAddOperate://添加操作日志
                dismissLoading();
                break;
            case HttpUtils.CodeAddVisit://更新visit表
                dismissLoading();
                if ((Integer) (((Object[]) request)[2]) == (wareCount - 1)) {
                    if (resCode == 1) {
                        //更新成功,显示并写本地数据
                        showInfo("料号:" + scanMaterial, lineSeatAl, wareSeatList, 1);
                        //保存本地数据库缓存
                        cacheWareResult(arrayLists);
                        //刷新数据
                        lv_ware_materials.setSelection(curSelectIndex);
                        wareHouseAdapter.notifyDataSetChanged();

                        Log.d(TAG, "curSelectIndex - " + curSelectIndex);
                    } else if (resCode == 0) {
                        //更新失败,提示重扫
                        globalFunc.showInfo("提示", "发料失败!", "请重新发料");
                        clearDisplay(arrayLists);
                    }
                }
                break;
        }
    }

    @Override
    public void showHttpError(int code, Object request, String s) {
        dismissLoading();
        globalFunc.showInfo("警告", "请检查网络连接是否正常!", "请连接网络!");
        switch (code) {
            case HttpUtils.CodeAddOperate://添加操作日志
                break;
            case HttpUtils.CodeAddVisit://更新visit表
                //更新失败,网络访问失败
                clearDisplay(arrayLists);
                break;
        }
    }

    private void showLoading() {
        dismissLoading();
        loadingDialog = new LoadingDialog(this, "正在加载...");
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
