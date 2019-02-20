package com.jimi.smt.eps_appclient.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jimi.smt.eps_appclient.Dao.FLCheckAll;
import com.jimi.smt.eps_appclient.Dao.GreenDaoUtil;
import com.jimi.smt.eps_appclient.Dao.QcCheckAll;
import com.jimi.smt.eps_appclient.Fragment.CheckMaterialFragment;
import com.jimi.smt.eps_appclient.Fragment.QCcheckAllFragment;
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
 * 类名:QCActivity
 * 创建人:Liang GuoChang
 * 创建时间:2017/10/19 19:29
 * 描述: 质检 Activity
 */
public class QCActivity extends FragmentActivity implements View.OnClickListener {

    private final String TAG = "QCActivity";
    private TextView tv_check_some;
    private TextView tv_check_all;
    private GlobalData globalData;
    private GlobalFunc globalFunc;
    private QCActivityInterface qcActivityInterface;
    //    public LoadingDialog updateDialog;
    private InfoDialog infoDialog;
    private CheckMaterialFragment checkMaterialFragment;
    private QCcheckAllFragment qCcheckAllFragment;
    private FragmentManager fragmentManager;
    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;

    @SuppressLint("HandlerLeak")
    private Handler mQCHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_qc);
        //使屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        globalData = (GlobalData) getApplication();
        globalFunc = new GlobalFunc(this);
        fragmentManager = getSupportFragmentManager();
        //开启服务
        startService(new Intent(this, RefreshCacheService.class));
        //注册订阅
        EventBus.getDefault().register(this);
        initView();
        //设置选中标题
        setTabSelection(0);
    }

    //监听订阅的消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EvenBusTest event) {
        if (event.getUpdated() == 0) {
            showUpdateDialog("站位表更新!", "站位表更新!");
        } else {
            //是否作废重传
            if (0 == event.getProgramIdEqual()) {
                showUpdateDialog("站位表作废并重传！", "站位表作废并重传！");
            }
        }
    }

    private void showUpdateDialog(String msg, String toast) {
        Log.d(TAG, "showUpdateDialog");
        if (infoDialog != null && infoDialog.isShowing()) {
            infoDialog.cancel();
            infoDialog.dismiss();
        }

        globalFunc.showInfo("提示", msg, toast);
    }

    //初始化布局
    private void initView() {
        ImageView iv_QC_back = findViewById(R.id.iv_QC_back);
        tv_check_some = findViewById(R.id.tv_check_some);
        tv_check_all = findViewById(R.id.tv_check_all);
        iv_QC_back.setOnClickListener(this);
        tv_check_some.setOnClickListener(this);
        tv_check_all.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_QC_back:
                exit();
                break;

            case R.id.tv_check_some://抽检
                //设置选中标题
                setTabSelection(0);
                break;

            case R.id.tv_check_all://全捡
                //设置选中标题
                setTabSelection(1);
                break;
        }
    }

    //物理返回键
    @Override
    public void onBackPressed() {
        exit();
    }

    private void setTabSelection(int index) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (index) {
            case 0:
                if (checkMaterialFragment == null) {
                    resetTitle();
                    hideFragments(transaction);
                    globalData.setOperType(Constants.CHECKMATERIAL);
                    tv_check_some.setBackgroundResource(R.drawable.factory_feed_click_shape);
                    checkMaterialFragment = new CheckMaterialFragment();
                    transaction.add(R.id.qc_layouts_content, checkMaterialFragment);
                } else if (checkMaterialFragment.isHidden()) {
                    resetTitle();
                    hideFragments(transaction);
                    globalData.setOperType(Constants.CHECKMATERIAL);
                    tv_check_some.setBackgroundResource(R.drawable.factory_feed_click_shape);
                    transaction.show(checkMaterialFragment);
                }
                break;
            case 1:
                if (qCcheckAllFragment == null) {
                    resetTitle();
                    hideFragments(transaction);
                    globalData.setOperType(Constants.CHECKALLMATERIAL);
                    tv_check_all.setBackgroundResource(R.drawable.factory_checkall_click_shape);
                    qCcheckAllFragment = new QCcheckAllFragment();
                    transaction.add(R.id.qc_layouts_content, qCcheckAllFragment);
                } else if (qCcheckAllFragment.isHidden()) {
                    resetTitle();
                    hideFragments(transaction);
                    globalData.setOperType(Constants.CHECKALLMATERIAL);
                    tv_check_all.setBackgroundResource(R.drawable.factory_checkall_click_shape);
                    transaction.show(qCcheckAllFragment);
                }
                break;
        }
        transaction.commit();
    }

    //设置标题为原状态
    private void resetTitle() {
        tv_check_some.setBackgroundResource(R.drawable.factory_feed_unclick_shape);
        tv_check_all.setBackgroundResource(R.drawable.factory_checkall_unclick_shape);
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction 用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (checkMaterialFragment != null) {
            transaction.hide(checkMaterialFragment);
        }
        if (qCcheckAllFragment != null) {
            transaction.hide(qCcheckAllFragment);
        }
    }

    //返回主页
    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mQCHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        //注销订阅
        EventBus.getDefault().unregister(this);
        //关闭服务
        stopService(new Intent(this, RefreshCacheService.class));
        super.onDestroy();
    }

    //回调接口
    public interface QCActivityInterface {
        void infoDialogClick_callBack();
    }

    public void callBackInfoDialogClick(QCActivityInterface qcActivityInterface) {
        this.qcActivityInterface = qcActivityInterface;
    }

    //IPQC未做首次全检
    public void showInfo(String title, String message, String tip) {
        /*if (updateDialog != null && updateDialog.isShowing()) {
            updateDialog.cancel();
            updateDialog.dismiss();
        }*/
        //对话框所有控件id
        int itemResIds[] = new int[]{R.id.dialog_title_view,
                R.id.dialog_title, R.id.tv_alert_info, R.id.info_trust, R.id.tv_alert_msg};
        //标题和内容
        String titleMsg[] = new String[]{title, message, tip};
        //内容的样式
        int msgStype[] = new int[]{22, Color.RED, Color.argb(255, 219, 201, 36)};
        infoDialog = new InfoDialog(this,
                R.layout.info_dialog_layout, itemResIds, titleMsg, msgStype);

        infoDialog.setOnDialogItemClickListener((dialog, view) -> {
            switch (view.getId()) {
                case R.id.info_trust:
                    dialog.dismiss();
                    break;
            }
        });
        infoDialog.setOnDismissListener(dialogInterface -> qcActivityInterface.infoDialogClick_callBack());
        infoDialog.show();
    }

}
