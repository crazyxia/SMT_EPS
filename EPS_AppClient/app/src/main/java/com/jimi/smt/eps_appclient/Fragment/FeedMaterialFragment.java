package com.jimi.smt.eps_appclient.Fragment;

import android.app.AlertDialog;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jimi.smt.eps_appclient.Activity.FactoryLineActivity;
import com.jimi.smt.eps_appclient.Adapter.MaterialAdapter;
import com.jimi.smt.eps_appclient.Beans.IsAllDoneInfo;
import com.jimi.smt.eps_appclient.Beans.Material;
import com.jimi.smt.eps_appclient.Beans.Operation;
import com.jimi.smt.eps_appclient.Dao.Feed;
import com.jimi.smt.eps_appclient.Dao.GreenDaoUtil;
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
 * 类名:FeedMaterialFragment
 * 创建人:Connie
 * 创建时间:2017-9-21
 * 描述:上料Fragment
 * 版本号:V1.0
 * 修改记录:
 */
public class FeedMaterialFragment extends Fragment implements OnEditorActionListener, OkHttpInterface {
    private transient final String TAG = this.getClass().getSimpleName();
    //全局变量
    private transient GlobalData globalData;
    //上料视图
    private transient View vFeedMaterialFragment;
    //操作员　站位　料号
    private transient MyEditTextDel edt_LineSeat;
    private transient MyEditTextDel edt_Material;
    private transient FactoryLineActivity mActivity;

    //上料列表
    private transient ListView lv_FeedMaterial;
    private transient MaterialAdapter materialAdapter;

    //上料本地数据表
    private List<Feed> feedList = new ArrayList<>();
    //是否恢复缓存
    private boolean isRestoreCache = false;
    //当前上料时用到的排位料号表
    private List<Material.MaterialBean> mFeedMaterialBeans = new ArrayList<>();
    private static List<Material.MaterialBean> tempMaterialBeans = new ArrayList<>();
    //扫描的站位在列表中的位置
    private ArrayList<Integer> scanLineIndex = new ArrayList<>();
    //成功上料数
    private int sucFeedCount = 0;
    //料号表的总数
    private int allCount = 0;
    //匹配的站位表项
    private int matchFeedMaterialId = -1;
    private transient GlobalFunc globalFunc;
    private transient LoadingDialog loadingDialog;
    private HttpUtils mHttpUtils;
    private int checkResetCondition = -1;
    private int checkAllDoneStrCondition = -1;
    private boolean mHidden = false;
    private MyEditTextDel edt_pwd;
    private AlertDialog feedLoginDialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        vFeedMaterialFragment = inflater.inflate(R.layout.feedmaterial_layout, container, false);
        //获取工单号和操作员
        Intent intent = getActivity().getIntent();
        savedInstanceState = intent.getExtras();

        globalData.setOperator(savedInstanceState != null ? savedInstanceState.getString("operatorNum") : null);

        checkResetCondition = 0;
        showLoading();
        mHttpUtils.isReset(globalData.getProgramId(), Constants.FEEDMATERIAL);

        initViews(savedInstanceState);
        initEvents();
        initData();//初始化数据

        return vFeedMaterialFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        //注册订阅
        EventBus.getDefault().register(this);
        mHttpUtils = new HttpUtils(this, getContext());
        mActivity = (FactoryLineActivity) getActivity();
        globalData = (GlobalData) getActivity().getApplication();
        globalFunc = new GlobalFunc(getActivity());

        //查询本地数据库是否存在缓存
        List<Feed> feeds = new GreenDaoUtil().queryFeedRecord(globalData.getOperator(), globalData.getWork_order()
                , globalData.getLine(), globalData.getBoard_type());

        //数据库存在缓存数据
        if (feeds.size() != 0) {
            //保存缓存
            feedList.addAll(feeds);
            isRestoreCache = true;
        } else {
            //不存在缓存数据,删除之前的数据
            boolean result = new GreenDaoUtil().deleteAllFeedData();
            Log.d(TAG, "deleteAllFeedData - " + result);
            isRestoreCache = false;
        }

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.i(TAG, "onHiddenChanged - " + hidden);
        this.mHidden = hidden;
        if (!hidden) {
            checkResetCondition = 0;
            showLoading();
            mHttpUtils.isReset(globalData.getProgramId(), Constants.FEEDMATERIAL);
        }
    }


    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        //注销订阅
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    /**
     * @author connie
     * @time 2017-9-22
     * @describe 初始化控件
     */
    private void initViews(Bundle bundle) {
        Log.i(TAG, "initViews");
        TextView tv_feed_order = vFeedMaterialFragment.findViewById(R.id.tv_feed_order);
        TextView edt_Operation = vFeedMaterialFragment.findViewById(R.id.tv_feed_Operation);
        edt_LineSeat = vFeedMaterialFragment.findViewById(R.id.edt_feed_lineseat);
        edt_Material = vFeedMaterialFragment.findViewById(R.id.edt_feed_material);

        lv_FeedMaterial = vFeedMaterialFragment.findViewById(R.id.feed_list_view);
        tv_feed_order.setText(bundle.getString("orderNum"));
        edt_Operation.setText(bundle.getString("operatorNum"));

        edt_LineSeat.requestFocus();
    }

    //监听订阅的消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EvenBusTest event) {
        if (event.getUpdated() == 0) {
            Log.d(TAG, "onEventMainThread - " + event.getUpdated());
            if (event.getFeedList() != null && event.getFeedList().size() > 0) {
                //更新页面
                feedList.clear();
                feedList.addAll(event.getFeedList());
                sucFeedCount = 0;
                //填充数据
                mFeedMaterialBeans.clear();
                for (Feed feed : feedList) {
                    Material.MaterialBean bean = new Material.MaterialBean(feed.getOrder(), feed.getBoard_type(), feed.getLine(),
                            feed.getProgramId(), feed.getSerialNo(), feed.getAlternative(), feed.getSpecitification(), feed.getPosition(),
                            feed.getQuantity(), feed.getOrgLineSeat(), feed.getOrgMaterial(), feed.getScanLineSeat(), feed.getScanMaterial(),
                            feed.getResult(), feed.getRemark());
                    mFeedMaterialBeans.add(bean);
                    //获取成功上料
                    if ((null != feed.getResult()) && (feed.getResult().equalsIgnoreCase("PASS"))) {
                        sucFeedCount++;
                    }
                }
                allCount = mFeedMaterialBeans.size();
                //更新显示
                materialAdapter.notifyDataSetChanged();
            }
            Log.d(TAG, "mHidden - " + mHidden);
            if (!mHidden) {
                //重新开始扫描站位
                clearLineSeatMaterialScan();
                //可重新上料
                dismissFeedLogin();

                /*
                checkAllDoneStrCondition = 2;
                showLoading();
                mHttpUtils.checkAllDoneStr(globalData.getProgramId(),String.valueOf(Constants.STORE_ISSUE));
                */
            }
        }
        //未更新
        else {
            if (0 == event.getProgramIdEqual()) {
                Log.d(TAG, "getProgramIdEqual - " + event.getProgramIdEqual());
                if (event.getFeedList() != null && event.getFeedList().size() > 0) {
                    //更新页面
                    feedList.clear();
                    feedList.addAll(event.getFeedList());
                    sucFeedCount = 0;
                    allCount = mFeedMaterialBeans.size();
                    for (Material.MaterialBean bean : mFeedMaterialBeans) {
                        bean.setProgramId(globalData.getProgramId());
                        bean.setScanlineseat("");
                        bean.setScanMaterial("");
                        bean.setRemark("");
                        bean.setResult("");
                    }
                    //更新显示
                    materialAdapter.notifyDataSetChanged();
                    if (!mHidden) {
                        //重新开始扫描站位
                        clearLineSeatMaterialScan();
                        // TODO: 2018/9/17
                        dismissFeedLogin();
                    }
                }
            }
        }
    }

    //初始化事件
    private void initEvents() {
        Log.i(TAG, "initEvents");
        edt_LineSeat.setOnEditorActionListener(this);
        edt_Material.setOnEditorActionListener(this);
        edt_LineSeat.setOnFocusChangeListener((v, haFocus) -> {
            if (haFocus) {
                if (feedLoginDialog != null && feedLoginDialog.isShowing()) {
                    edt_LineSeat.setCursorVisible(false);
                    edt_pwd.requestFocus();
                }
            }
        });

        edt_Material.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (TextUtils.isEmpty(edt_LineSeat.getText().toString().trim())) {
                    edt_Material.setCursorVisible(false);
                    edt_LineSeat.setText("");
                    edt_LineSeat.requestFocus();
                } else {
                    edt_Material.setCursorVisible(true);
                }
            }
        });

        //解决站位失去焦点问题
        lv_FeedMaterial.setFocusable(false);
    }

    //初始化数据
    private void initData() {
        Log.i(TAG, "initData");
        sucFeedCount = 0;
        //填充数据
        mFeedMaterialBeans.clear();
        //没有缓存
        if (!isRestoreCache) {
            tempMaterialBeans.clear();
            tempMaterialBeans.addAll(globalData.getMaterialBeans());
            for (Material.MaterialBean org : tempMaterialBeans) {
                //保存缓存到数据库中
                Material.MaterialBean bean = new Material.MaterialBean();
                bean = bean.copy(org);
                bean.setScanlineseat("");
                bean.setScanMaterial("");
                bean.setRemark("");
                bean.setResult("");
                mFeedMaterialBeans.add(bean);
                Feed feed = new Feed(null, bean.getProgramId(), bean.getWorkOrder(), globalData.getOperator(),
                        bean.getBoardType(), bean.getLine(), bean.getLineseat(), bean.getMaterialNo(), bean.getSpecitification(),
                        bean.getPosition(), bean.getQuantity(), bean.getScanlineseat(), bean.getScanMaterial(), bean.getResult(),
                        bean.getRemark(), bean.getSerialNo(), bean.isAlternative());
                feedList.add(feed);
            }
            //保存到数据库中
            boolean cacheResult = new GreenDaoUtil().insertMultiFeedMaterial(feedList);
            Log.d(TAG, "cacheResult - " + cacheResult);
        }
        //存在缓存
        else {
            for (Feed feed : feedList) {
                Material.MaterialBean bean = new Material.MaterialBean(feed.getOrder(), feed.getBoard_type(), feed.getLine(),
                        feed.getProgramId(), feed.getSerialNo(), feed.getAlternative(), feed.getSpecitification(), feed.getPosition(),
                        feed.getQuantity(), feed.getOrgLineSeat(), feed.getOrgMaterial(), feed.getScanLineSeat(), feed.getScanMaterial(),
                        feed.getResult(), feed.getRemark());
                mFeedMaterialBeans.add(bean);
                //获取成功上料
                if ((null != feed.getResult()) && (feed.getResult().equalsIgnoreCase("PASS"))) {
                    sucFeedCount++;
                }
            }

            //需要更新全局变量为本地数据库的,以提供更新依据
            globalData.setFactoryProgramId(mFeedMaterialBeans.get(0).getProgramId());
            globalData.setMaterialBeans(mFeedMaterialBeans);
        }
        allCount = mFeedMaterialBeans.size();
        //设置Adapter
        materialAdapter = new MaterialAdapter(this.getActivity(), mFeedMaterialBeans);
        lv_FeedMaterial.setAdapter(materialAdapter);

        matchFeedMaterialId = -1;
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        Log.i(TAG, "onEditorAction");
        //回车键
        if (i == EditorInfo.IME_ACTION_SEND ||
                (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            switch (keyEvent.getAction()) {
                //抬上
                case KeyEvent.ACTION_UP:
                    //先判断是否联网
                    if (globalFunc.isNetWorkConnected()) {
                        Log.d(TAG, "matchFeedMaterialId-" + matchFeedMaterialId);
                        //扫描内容
                        String scanValue = String.valueOf(((EditText) textView).getText());
                        scanValue = scanValue.replaceAll("\r", "");
                        Log.i(TAG, "scan Value:" + scanValue);
                        textView.setText(scanValue);

                        switch (textView.getId()) {
                            case R.id.edt_feed_lineseat:
                                matchFeedMaterialId = -1;
                                //站位
                                scanValue = globalFunc.getLineSeat(scanValue);
                                edt_LineSeat.setText(scanValue);

                                checkAllDoneStrCondition = 3;
                                showLoading();
                                mHttpUtils.checkAllDoneStr(globalData.getProgramId(), String.valueOf(Constants.FEEDMATERIAL) + "&" + String.valueOf(Constants.STORE_ISSUE));

                                break;

                            case R.id.edt_feed_material:
                                //站位存在且已发料才会进入这里
                                scanValue = globalFunc.getMaterial(scanValue);
                                textView.setText(scanValue);
                                //站位不存在
                                if (matchFeedMaterialId < 0) {
                                    feedNextMaterial();
                                    return true;
                                }
                                //比对料号，包含替代料
                                matchFeedMaterialId = -1;

                                //只有一个料
                                if (scanLineIndex.size() == 1) {
                                    Material.MaterialBean singleMaterialItem = mFeedMaterialBeans.get(scanLineIndex.get(0));
                                    singleMaterialItem.setScanMaterial(scanValue);
                                    if (singleMaterialItem.getMaterialNo().equalsIgnoreCase(scanValue)) {
                                        singleMaterialItem.setResult("PASS");
                                        singleMaterialItem.setRemark("上料成功");
                                        //成功次数加1
                                        sucFeedCount++;
                                    } else {
                                        singleMaterialItem.setResult("FAIL");
                                        singleMaterialItem.setRemark("料号与站位不相符");
                                    }

                                    //添加显示日志
                                    globalData.setUpdateType(Constants.FEEDMATERIAL);
                                    com.jimi.smt.eps_appclient.Beans.ProgramItemVisit itemVisit = com.jimi.smt.eps_appclient.Beans.ProgramItemVisit.getProgramItemVisit(Constants.FEEDMATERIAL, singleMaterialItem);
                                    mHttpUtils.updateVisit(singleMaterialItem, itemVisit, 1);
                                    //当前上料索引
                                    matchFeedMaterialId = scanLineIndex.get(0);
                                }
                                //存在主替料
                                else {
                                    checkMultiItem(scanLineIndex, scanValue);
                                }

                                if (matchFeedMaterialId < 0) {
                                    feedNextMaterial();
                                    return true;
                                }

                                //更新数据显示
                                materialAdapter.notifyDataSetChanged();
                                //增加数据库日志
                                globalData.setOperType(Constants.FEEDMATERIAL);

                                Operation operation = Operation.getOperation(globalData.getOperator(), Constants.FEEDMATERIAL, mFeedMaterialBeans.get(matchFeedMaterialId));
                                mHttpUtils.addOperation(operation);
                                break;
                        }
                    } else {
                        globalFunc.showInfo("警告", "请检查网络连接是否正常!", "请连接网络!");
                        clearLineSeatMaterialScan();
                        matchFeedMaterialId = -1;
                    }

                    return true;
                default:
                    return true;
            }
        }
        return false;
    }

    private void beginOperate(String scanValue) {
        Log.i(TAG, "lineseat:" + scanValue);
        scanLineIndex.clear();
        //相同站位在列表中的位置
        for (int j = 0; j < mFeedMaterialBeans.size(); j++) {
            Material.MaterialBean materialItem = mFeedMaterialBeans.get(j);
            //扫到的站位在表中存在
            if (materialItem.getLineseat().equalsIgnoreCase(scanValue)) {
                scanLineIndex.add(j);
                matchFeedMaterialId = j;
                materialItem.setScanlineseat(scanValue);
            }
        }
        //无匹配的站位
        if (matchFeedMaterialId < 0) {
            //重新上料
            feedNextMaterial();
        } else {
            //更新显示
            materialAdapter.notifyDataSetChanged();
            edt_Material.requestFocus();
        }

    }

    private void checkMultiItem(ArrayList<Integer> integers, String mScanValue) {
        Material.MaterialBean bean = new Material.MaterialBean();
        //多个相同的站位,即有替换料
        for (int z = 0; z < integers.size(); z++) {
            Material.MaterialBean multiMaterialItem = mFeedMaterialBeans.get(integers.get(z));
            int serialNo;
            boolean alternative;
            multiMaterialItem.setScanMaterial(mScanValue);
            if (multiMaterialItem.getMaterialNo().equalsIgnoreCase(multiMaterialItem.getScanMaterial())) {
                serialNo = multiMaterialItem.getSerialNo();
                alternative = multiMaterialItem.isAlternative();
                Log.d(TAG, "alternative - " + alternative);
                for (int x = 0; x < integers.size(); x++) {
                    Log.d(TAG, "innerMaterialItem - " + x);
                    Material.MaterialBean innerMaterialItem = mFeedMaterialBeans.get(integers.get(x));
                    innerMaterialItem.setScanMaterial(mScanValue);
                    innerMaterialItem.setResult("PASS");
                    if (x == z) {
                        //扫描的是该料号
                        matchFeedMaterialId = integers.get(x);
                        innerMaterialItem.setRemark("上料成功");
                        //赋值
                        bean = innerMaterialItem;
                    } else {
                        //扫描的不是该料号
                        if (alternative) {
                            //上的料是替换料
                            innerMaterialItem.setRemark("替料" + serialNo + "上料成功");
                        } else {
                            //上的料是主料
                            innerMaterialItem.setRemark("主料" + serialNo + "上料成功");
                        }
                    }

                    //成功次数加1
                    sucFeedCount++;
                }
                //跳出外部循环
                break;
            } else {
                multiMaterialItem.setResult("FAIL");
                multiMaterialItem.setRemark("料号与站位不相符");
                //都不相符,默认操作的是第一个
                matchFeedMaterialId = integers.get(0);
                //赋值
                bean = multiMaterialItem;
            }
        }

        //添加显示
        globalData.setUpdateType(Constants.FEEDMATERIAL);
        com.jimi.smt.eps_appclient.Beans.ProgramItemVisit itemVisit = com.jimi.smt.eps_appclient.Beans.ProgramItemVisit.getProgramItemVisit(Constants.FEEDMATERIAL, bean);
        mHttpUtils.updateVisit(bean, itemVisit, 1);
    }

    //更新上料缓存
    private void cacheFeedResult(ArrayList<Integer> lineIndex, Material.MaterialBean materialItem) {
        Log.d(TAG, "cacheFeedResult - " + lineIndex.size());
        //保存缓存
        for (int i = 0, len = lineIndex.size(); i < len; i++) {
            Feed feed = feedList.get(lineIndex.get(i));
            feed.setScanLineSeat(materialItem.getScanlineseat());
            feed.setScanMaterial(materialItem.getScanMaterial());
            feed.setResult(materialItem.getResult());
            feed.setRemark(materialItem.getRemark());
            new GreenDaoUtil().updateFeed(feed);
        }
    }

    //更新visit表失败，清除显示
    private void clearDisplay(ArrayList<Integer> lineIndex) {
        Log.d(TAG, "clearDisplay - " + lineIndex.size());

        for (int i = 0, len = lineIndex.size(); i < len; i++) {
            Material.MaterialBean bean = mFeedMaterialBeans.get(lineIndex.get(i));
            bean.setResult("");
            bean.setRemark("");
            bean.setScanlineseat("");
            bean.setScanMaterial("");
        }
        materialAdapter.notifyDataSetChanged();
        clearLineSeatMaterialScan();
    }

    //清空上料页面结果
    private void clearFeedDisplay() {
        Log.d(TAG, " - clearFeedDisplay - ");
        sucFeedCount = 0;
        allCount = 0;
        for (int i = 0; i < mFeedMaterialBeans.size(); i++) {
            Material.MaterialBean materialItem = mFeedMaterialBeans.get(i);
            materialItem.setScanlineseat("");
            materialItem.setScanMaterial("");
            materialItem.setResult("");
            materialItem.setRemark("");
            Feed feed = feedList.get(i);
            feed.setScanLineSeat("");
            feed.setScanMaterial("");
            feed.setResult("");
            feed.setRemark("");
            allCount++;
        }
        materialAdapter.notifyDataSetChanged();
    }

    //上下一个料
    private void feedNextMaterial() {
        //清空站位位置表
        scanLineIndex.clear();
        Log.i(TAG, "feedNextMaterial:" + matchFeedMaterialId);
        //显示最新的上料结果
        if (matchFeedMaterialId >= 0) {
            lv_FeedMaterial.setSelection(matchFeedMaterialId);
            matchFeedMaterialId = -1;
        } else {
            //弹出提示框
            String titleMsg[] = new String[]{"提示", "排位表不存在此站位或料号!!!"};
            int msgStyle[] = new int[]{22, Color.RED};
            showInfo(titleMsg, msgStyle, false, 0);
        }

        Log.d(TAG, "sucFeedCount-" + sucFeedCount + " \n mFeedMaterialBeans-" + mFeedMaterialBeans.size() + " \n allCount-" + allCount);

        if (sucFeedCount < mFeedMaterialBeans.size() || sucFeedCount < allCount) {
            clearLineSeatMaterialScan();
        } else {
            //上料结束,显示结果
            showFeedMaterialResult();
        }
    }

    //显示上料结果
    private void showFeedMaterialResult() {
        //默认上料结果是PASS
        boolean feedResult = true;
        for (Material.MaterialBean feedMaterialItem : mFeedMaterialBeans) {
            if ((feedMaterialItem.getResult() == null) || (!feedMaterialItem.getResult().equalsIgnoreCase("PASS"))) {
                feedResult = false;
                break;
            }
        }
        //弹出上料结果
        String titleMsg[];
        int msgStyle[];
        if (feedResult) {
            titleMsg = new String[]{"上料完成", "PASS"};
            msgStyle = new int[]{66, Color.argb(255, 102, 153, 0)};
        } else {
            titleMsg = new String[]{"上料未完成，请检查!", "请继续上料"};
            msgStyle = new int[]{66, Color.argb(255, 212, 179, 17)};
        }
        showInfo(titleMsg, msgStyle, feedResult, 1);
    }

    //弹出消息窗口
    private void showInfo(String[] titleMsg, int[] msgStyle, final boolean result, final int resultType) {
        //对话框所有控件id
//        int itemResIds[] = new int[]{R.id.dialog_title_view,
//                R.id.dialog_title, R.id.tv_alert_info, R.id.info_trust};


        int itemResIds[] = new int[]{R.id.dialog_title_view,
                R.id.dialog_title, R.id.tv_alert_info, R.id.info_trust, R.id.tv_alert_msg};

        InfoDialog infoDialog = new InfoDialog(getActivity(),
                R.layout.info_dialog_layout, itemResIds, titleMsg, msgStyle);

        infoDialog.setOnDialogItemClickListener((dialog, view) -> {
            switch (view.getId()) {
                case R.id.info_trust:
                    dialog.dismiss();
                    clearLineSeatMaterialScan();
                    if (result) {

                        showFeedLoginWin();

                    } else {
                        if (resultType == 1) {
                            //将未成功数加到
                            for (Material.MaterialBean feedMaterialItem : mFeedMaterialBeans) {
                                if ((feedMaterialItem.getResult() == null) || (!feedMaterialItem.getResult().equalsIgnoreCase("PASS"))) {
                                    allCount++;
                                }
                            }
                        }
                    }
                    break;
            }
        });
        infoDialog.show();

    }

    //弹出解锁上料
    private void showFeedLoginWin() {
        Log.d(TAG, "showFeedLoginWin");
        dismissFeedLogin();
        feedLoginDialog = new AlertDialog.Builder(this.getContext()).create();
        feedLoginDialog.show();
        Window window = feedLoginDialog.getWindow();
        assert window != null;
        window.setContentView(R.layout.fragment_feed_login);
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        feedLoginDialog.setCancelable(false);
        edt_pwd = window.findViewById(R.id.edt_pwd);
        edt_pwd.setFocusable(true);
        edt_pwd.setFocusableInTouchMode(true);
        edt_pwd.requestFocus();
        window.findViewById(R.id.btn_unlock).setOnClickListener(view -> unlock());
        window.findViewById(R.id.btn_cancel).setOnClickListener(view -> {
            feedLoginDialog.dismiss();
            mActivity.setTabSelection(1);
        });
    }

    private void dismissFeedLogin() {
        if (feedLoginDialog != null && feedLoginDialog.isShowing()) {
            feedLoginDialog.dismiss();
        }
    }

    //检测密码是否正确,正确则登陆
    private void unlock() {
        if (!TextUtils.isEmpty(edt_pwd.getText().toString().trim())) {
            String pwd = edt_pwd.getText().toString().trim();
            if (pwd.equalsIgnoreCase(Constants.feedPwd)) {
                //重置visit表
                showLoading();
                mHttpUtils.reset(globalData.getLine(), globalData.getWork_order(), globalData.getBoard_type());
            } else {
                Toast.makeText(mActivity.getApplicationContext(), "密码错误", Toast.LENGTH_SHORT).show();
                edt_pwd.setText("");
                edt_pwd.requestFocus();
            }
        } else {
            Toast.makeText(mActivity.getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();
            edt_pwd.requestFocus();
        }
    }

    //清除扫描数据
    private void clearLineSeatMaterialScan() {
        edt_LineSeat.setText("");
        edt_Material.setText("");
        edt_LineSeat.requestFocus();
    }


    @Override
    public void showHttpResponse(int code, Object request, String response) {
        // TODO: 2019/2/18 加载进度 
        dismissLoading();
        Log.d(TAG, "showHttpResponse - " + response);
        Log.d(TAG, "code - " + code);
        switch (code) {
            case HttpUtils.CodeCheckIsReset:
                int checkReset = Integer.valueOf(response);
                Log.d(TAG, "checkReset - " + checkReset);
                //本地数据是否重置
                boolean reseted = true;
                for (Material.MaterialBean materialItem : mFeedMaterialBeans) {
                    if (null != materialItem.getResult()) {
                        if (materialItem.getResult().equals("PASS") || materialItem.getResult().equals("FAIL")) {
                            reseted = false;
                            break;
                        }
                    }
                }
                Log.d(TAG, "reseted - " + reseted);
                switch (checkResetCondition) {
                    case 0://切换到上料页面时
                        if (checkReset == 1 && !reseted) {
                            //重置，判断发料
                            checkAllDoneStrCondition = 0;
                            showLoading();
                            mHttpUtils.checkAllDoneStr(globalData.getProgramId(), String.valueOf(Constants.STORE_ISSUE));
                        } else {
                            //判断发料与上料
                            checkAllDoneStrCondition = 1;
                            showLoading();
                            mHttpUtils.checkAllDoneStr(globalData.getProgramId(), String.valueOf(Constants.FEEDMATERIAL) + "&" + String.valueOf(Constants.STORE_ISSUE));
                        }
                        break;

                    case 1://扫站位时
                        if (checkReset == 1) {
                            if (!reseted) {
                                //重置
                                clearFeedDisplay();
                                clearLineSeatMaterialScan();
                            } else {
                                //操作
                                beginOperate(edt_LineSeat.getText().toString().trim());
                            }
                        } else if (checkReset == 0) {
                            //操作
                            beginOperate(edt_LineSeat.getText().toString().trim());
                        }
                        break;
                }

                break;

            // TODO: 2019/2/12  
            case HttpUtils.CodeIsAllDoneSTR://查询某一项或某几项操作是否完成
                int allDoneCode;
                Gson allDoneGson = new Gson();
                IsAllDoneInfo isAllDoneInfo = allDoneGson.fromJson(response, IsAllDoneInfo.class);
                allDoneCode = isAllDoneInfo.getCode();
                if (allDoneCode == 1) {
                    IsAllDoneInfo.AllDoneInfoBean allDoneInfoBean = isAllDoneInfo.getData();
                    int isWare = Integer.valueOf(allDoneInfoBean.getStore());
                    int isFeed = -1;
                    switch (checkAllDoneStrCondition) {
                        case 0://只查发料
                            if (isWare == 0) {
                                String titleMsgs[] = new String[]{"提示", "仓库未完成发料!"};
                                int msgStyleStr[] = new int[]{22, Color.argb(255, 219, 201, 36)};
                                showInfo(titleMsgs, msgStyleStr, false, 2);
                                clearFeedDisplay();
                                clearLineSeatMaterialScan();
                            } else {
                                clearFeedDisplay();
                                clearLineSeatMaterialScan();
                            }
                            break;
                        case 1://查发料与上料
                            isFeed = Integer.valueOf(allDoneInfoBean.getFeed());
                            if (isWare == 0) {
                                String titleMsgStr[] = new String[]{"提示", "仓库未完成发料!"};
                                int msgStyles[] = new int[]{22, Color.argb(255, 219, 201, 36)};
                                showInfo(titleMsgStr, msgStyles, false, 2);
                            } else if (isFeed == 1) {
                                showFeedLoginWin();
                            }
                            break;
                        case 2:
                            if (isWare == 0) {
                                dismissFeedLogin();
                                /*
                                String titleMsg[] = new String[]{"站位表更新!", "仓库未完成发料!"};
                                int msgStyle[] = new int[]{22, Color.argb(255, 219, 201, 36)};
                                showInfo(titleMsg, msgStyle, false, 2);
                                */
                            }
                            break;
                        case 3://扫站位时查发料、上料
                            isFeed = Integer.valueOf(allDoneInfoBean.getFeed());
                            if (isWare == 0) {
                                String titleMsgStr[] = new String[]{"提示", "仓库未完成发料!"};
                                int msgStyles[] = new int[]{22, Color.argb(255, 219, 201, 36)};
                                showInfo(titleMsgStr, msgStyles, false, 2);
                            } else if (isWare == 1) {
                                if (isFeed == 0){
                                    checkResetCondition = 1;
                                    showLoading();
                                    mHttpUtils.isReset(globalData.getProgramId(), Constants.FEEDMATERIAL);
                                }else if (isFeed == 1){
                                    //已完成上料，清空本地数据，提示再次上料
                                    showFeedLoginWin();
                                }
                            }
                            break;
                    }
                    Log.d(TAG, "isFeed - " + isFeed);
                    Log.d(TAG, "isWare - " + isWare);
                }
                break;

            case HttpUtils.CodeAddOperate:
                break;

            case HttpUtils.CodeAddVisit:
                int resCode = -1;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    resCode = jsonObject.getInt("code");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "showHttpResponse - " + e.toString());
                }
                if (resCode == 1) {
                    cacheFeedResult(scanLineIndex, mFeedMaterialBeans.get(scanLineIndex.get(0)));
                    feedNextMaterial();
                } else if (resCode == 0) {
                    globalFunc.showInfo("提示", "上料失败!", "请重新上料");
                    clearDisplay(scanLineIndex);
                }
                break;

            case HttpUtils.CodeReset://失败 failed_not_exist ; 成功 succeed
                String result = "";
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    result = jsonObject.getString("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "showHttpResponse - " + response);
                }
                if (result.equalsIgnoreCase("failed_not_exist")) {
                    globalFunc.showInfo("警告", "请检查网络连接是否正常!", "请连接网络!");
                } else if (result.equalsIgnoreCase("succeed")) {
                    //清空上料结果
                    // TODO: 2018/12/17 不止清空上料记录，全检，首检 都要清空 
                    boolean updateAllFeed = new GreenDaoUtil().updateAllFeed(feedList);
                    boolean updateFlCheck = new GreenDaoUtil().updateFLCheck(globalData.getWork_order(),
                            globalData.getLine(), globalData.getBoard_type());
                    boolean updateQcCheck = new GreenDaoUtil().updateQcCheck(globalData.getWork_order(),
                            globalData.getLine(), globalData.getBoard_type());
                    Log.d(TAG, "updateAllFeed - " + updateAllFeed);
                    Log.d(TAG, "updateFlCheck - " + updateFlCheck);
                    Log.d(TAG, "updateQcCheck - " + updateQcCheck);
                    //清空上料页面结果
                    clearFeedDisplay();
                    edt_pwd.setText("");
                    dismissFeedLogin();
                }
                break;
        }
    }

    @Override
    public void showHttpError(int code, Object request, String s) {
        dismissLoading();
        Log.d(TAG, "showHttpError - " + s);
//        globalFunc.showInfo("警告", "请检查网络连接是否正常!", "请连接网络!");
        switch (code) {
            case HttpUtils.CodeAddOperate://添加操作日志
                break;
            case HttpUtils.CodeAddVisit://更新visit表
                //更新失败,网络访问失败
                globalFunc.showInfo("操作失败", "请重新操作!", "请重新操作!");
                clearDisplay(scanLineIndex);
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
