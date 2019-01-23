package com.jimi.smt.eps_appclient.Activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jimi.smt.eps_appclient.Dao.FLCheckAll;
import com.jimi.smt.eps_appclient.Dao.GreenDaoUtil;
import com.jimi.smt.eps_appclient.Fragment.ChangeMaterialFragment;
import com.jimi.smt.eps_appclient.Fragment.CheckAllMaterialFragment;
import com.jimi.smt.eps_appclient.Fragment.FeedMaterialFragment;
import com.jimi.smt.eps_appclient.Func.GlobalFunc;
import com.jimi.smt.eps_appclient.Func.Log;
import com.jimi.smt.eps_appclient.R;
import com.jimi.smt.eps_appclient.Service.RefreshCacheService;
import com.jimi.smt.eps_appclient.Unit.Constants;
import com.jimi.smt.eps_appclient.Unit.EvenBusTest;
import com.jimi.smt.eps_appclient.Unit.GlobalData;
import com.jimi.smt.eps_appclient.Views.InfoDialog;
import com.jimi.smt.eps_appclient.Views.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;


/**
 * 类名:FactoryLineActivity
 * 创建人:Liang GuoChang
 * 创建时间:2017/10/19 20:03
 * 描述: 厂线 Activity
 */
public class FactoryLineActivity extends FragmentActivity implements View.OnClickListener {

    private final String TAG = "FactoryLineActivity";
    private FeedMaterialFragment feedMaterialFragment;
    private ChangeMaterialFragment changeMaterialFragment;
    private CheckAllMaterialFragment checkAllMaterialFragment;
    private FragmentManager fragmentManager;
    private int oldFragmentIndex = 0;
    private TextView tv_factory_feed;
    private TextView tv_factory_change;
    private TextView tv_factory_checkAll;
    private GlobalData globalData;
    public LinearLayout factory_parent;
    private LoadingDialog loadingDialog;
    private static final int EXIT = 139;
    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;
//    public LoadingDialog updateDialog;
    private GlobalFunc globalFunc;

    @SuppressLint("HandlerLeak")
    private Handler mFactoryHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case EXIT:
                    isExit = false;
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_factory_line);
        //使屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        globalData = (GlobalData) getApplication();
        globalFunc = new GlobalFunc(this);
        //开启服务
        startService(new Intent(this, RefreshCacheService.class));
        //注册订阅
        EventBus.getDefault().register(this);

        initView();
        fragmentManager = getSupportFragmentManager();

        setTabSelection(0);
    }

    //初始化布局
    private void initView() {
        ImageView iv_factory_back = findViewById(R.id.iv_factory_back);
        factory_parent = findViewById(R.id.factory_parent);
        tv_factory_feed = findViewById(R.id.tv_factory_feed);
        tv_factory_change = findViewById(R.id.tv_factory_change);
        tv_factory_checkAll = findViewById(R.id.tv_factory_checkAll);
        iv_factory_back.setOnClickListener(this);
        tv_factory_feed.setOnClickListener(this);
        tv_factory_change.setOnClickListener(this);
        tv_factory_checkAll.setOnClickListener(this);
    }

    //监听订阅的消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EvenBusTest event) {
        // TODO: 2018/12/26
        if (event.getUpdated() == 0) {
            Log.d(TAG, "getUpdated - " + 0);
            if (event.getCheckAllTimeOut() == 1) {
                Log.d(TAG, "getCheckAllTimeOut - " + 1);
                //判断本地数据是否清空
                boolean clear = true;
                List<FLCheckAll> flCheckAlls = new GreenDaoUtil().queryFLCheckRecord(globalData.getOperator(), globalData.getWork_order(),
                        globalData.getLine(), globalData.getBoard_type());
                for (FLCheckAll flCheckAll : flCheckAlls) {
                    if ((null != flCheckAll.getResult()) && ((flCheckAll.getResult().equalsIgnoreCase("PASS")) || (flCheckAll.getResult().equalsIgnoreCase("FAIL")))) {
                        clear = false;
                        break;
                    }
                }
                if (!clear) {
                    showUpdateDialog("全检超时!", "全检超时!");
                }
            } else {
                Log.d(TAG, "getCheckAllTimeOut - " + 0);
                showUpdateDialog("站位表更新!", "站位表更新!");
            }
            globalData.setUpdateProgram(false);
        } else {
            if (event.getCheckAllTimeOut() == 1) {
                Log.d(TAG, "getUpdated - " + 1);
                Log.d(TAG, "getCheckAllTimeOut - " + 1);
                //是否作废
                if (0 == event.getProgramIdEqual()){
                    showUpdateDialog("站位表作废并重传！", "站位表作废并重传！");
                }else {
                    //判断本地数据是否清空
                    boolean clear = true;
                    List<FLCheckAll> flCheckAlls = new GreenDaoUtil().queryFLCheckRecord(globalData.getOperator(), globalData.getWork_order(),
                            globalData.getLine(), globalData.getBoard_type());
                    for (FLCheckAll flCheckAll : flCheckAlls) {
                        if ((null != flCheckAll.getResult()) && ((flCheckAll.getResult().equalsIgnoreCase("PASS")) || (flCheckAll.getResult().equalsIgnoreCase("FAIL")))) {
                            clear = false;
                            break;
                        }
                    }
                    if (!clear) {
                        showUpdateDialog("全检超时!", "全检超时!");
                    }
                }

            }else {
                //是否作废
                if (0 == event.getProgramIdEqual()){
                    Log.d(TAG, "getUpdated - " + 1);
                    Log.d(TAG, "getCheckAllTimeOut - " + 1);
                    Log.d(TAG, "getProgramIdEqual - " + 0);
                    showUpdateDialog("站位表作废并重传！", "站位表作废并重传！");
                }
            }
        }
    }

    private void showUpdateDialog(String msg, String toast) {
        globalFunc.showInfo("提示", msg, toast);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_factory_back:
                exit();
                break;

            case R.id.tv_factory_feed://上料
                setTabSelection(0);
                break;

            case R.id.tv_factory_change://换料
                setTabSelection(1);
                break;

            case R.id.tv_factory_checkAll://全检
                setTabSelection(2);
                break;
        }
    }

    public void setTabSelection(int index) {
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (index) {
            case 0:
                if (feedMaterialFragment == null) {
                    resetTitle();
                    hideFragments(transaction);
                    globalData.setOperType(Constants.FEEDMATERIAL);
                    tv_factory_feed.setBackgroundResource(R.drawable.factory_feed_click_shape);
                    feedMaterialFragment = new FeedMaterialFragment();
                    transaction.add(R.id.factory_layouts_content, feedMaterialFragment);
                } else if (feedMaterialFragment.isHidden()) {
                    resetTitle();
                    hideFragments(transaction);
                    globalData.setOperType(Constants.FEEDMATERIAL);
                    tv_factory_feed.setBackgroundResource(R.drawable.factory_feed_click_shape);
                    transaction.show(feedMaterialFragment);
                }
                break;

            case 1:
                if (changeMaterialFragment == null) {
                    resetTitle();
                    hideFragments(transaction);
                    globalData.setOperType(Constants.CHANGEMATERIAL);
                    tv_factory_change.setBackgroundResource(R.drawable.factory_change_click_shape);
                    changeMaterialFragment = new ChangeMaterialFragment();
                    transaction.add(R.id.factory_layouts_content, changeMaterialFragment);
                } else if (changeMaterialFragment.isHidden()) {
                    resetTitle();
                    hideFragments(transaction);
                    globalData.setOperType(Constants.CHANGEMATERIAL);
                    tv_factory_change.setBackgroundResource(R.drawable.factory_change_click_shape);
                    transaction.show(changeMaterialFragment);
                }
                break;

            case 2:
                if (checkAllMaterialFragment == null) {
                    resetTitle();
                    hideFragments(transaction);
                    globalData.setOperType(Constants.CHECKALLMATERIAL);
                    tv_factory_checkAll.setBackgroundResource(R.drawable.factory_checkall_click_shape);
                    checkAllMaterialFragment = new CheckAllMaterialFragment();
                    transaction.add(R.id.factory_layouts_content, checkAllMaterialFragment);
                } else if (checkAllMaterialFragment.isHidden()) {
                    resetTitle();
                    hideFragments(transaction);
                    globalData.setOperType(Constants.CHECKALLMATERIAL);
                    tv_factory_checkAll.setBackgroundResource(R.drawable.factory_checkall_click_shape);
                    transaction.show(checkAllMaterialFragment);
                }
                break;

        }
        transaction.commit();
    }

    //设置标题为原状态
    private void resetTitle() {
        tv_factory_feed.setBackgroundResource(R.drawable.factory_feed_unclick_shape);
        tv_factory_change.setBackgroundResource(R.drawable.factory_change_unclick_shape);
        tv_factory_checkAll.setBackgroundResource(R.drawable.factory_checkall_unclick_shape);
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction 用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (feedMaterialFragment != null && feedMaterialFragment.isVisible()) {
            transaction.hide(feedMaterialFragment);
        }
        if (changeMaterialFragment != null && changeMaterialFragment.isVisible()) {
            transaction.hide(changeMaterialFragment);
        }
        if (checkAllMaterialFragment != null && checkAllMaterialFragment.isVisible()) {
            transaction.hide(checkAllMaterialFragment);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    //物理返回键
    @Override
    public void onBackPressed() {
        exit();
    }

    //返回主页
    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            Message message = Message.obtain();
            message.what = EXIT;
            mFactoryHandler.sendMessageDelayed(message, 2000);
        } else {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            this.finish();
        }
    }


    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        //注销订阅
        EventBus.getDefault().unregister(this);
        //关闭服务
        stopService(new Intent(this, RefreshCacheService.class));
        super.onDestroy();
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
