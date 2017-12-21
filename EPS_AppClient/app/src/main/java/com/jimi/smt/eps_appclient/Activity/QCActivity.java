package com.jimi.smt.eps_appclient.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jimi.smt.eps_appclient.CheckAllMaterialFragment;
import com.jimi.smt.eps_appclient.CheckMaterialFragment;
import com.jimi.smt.eps_appclient.GlobalData;
import com.jimi.smt.eps_appclient.R;
import com.jimi.smt.eps_appclient.Unit.Constants;
import com.jimi.smt.eps_appclient.Views.CustomViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名:QCActivity
 * 创建人:Liang GuoChang
 * 创建时间:2017/10/19 19:29
 * 描述: 质检 Activity
 */
public class QCActivity extends FragmentActivity implements View.OnClickListener {

    private final String TAG="QCActivity";
    private TextView tv_check_some;
    private TextView tv_check_all;
    private GlobalData globalData;
    private CustomViewPager viewpager_qc;
    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;
    private Handler mQCHandler = new Handler(){
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
        globalData = (GlobalData) getApplication();
        initView();
        //设置选中标题
        setSelectTabTitle(0);
    }

    //初始化布局
    private void initView(){
        ImageView iv_QC_back= (ImageView) findViewById(R.id.iv_QC_back);
        tv_check_some = (TextView) findViewById(R.id.tv_check_some);
        tv_check_all = (TextView) findViewById(R.id.tv_check_all);
        viewpager_qc = (CustomViewPager) findViewById(R.id.viewpager_QC);
        iv_QC_back.setOnClickListener(this);
        tv_check_some.setOnClickListener(this);
        tv_check_all.setOnClickListener(this);
        //fragment集合
        final List<Fragment> fragmentList=new ArrayList<Fragment>();
        fragmentList.add(new CheckMaterialFragment());
        fragmentList.add(new CheckAllMaterialFragment());
        //fragment适配器
        FragmentPagerAdapter fragmentPagerAdapter=new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        };
        //设置适配器
        viewpager_qc.setAdapter(fragmentPagerAdapter);
        viewpager_qc.setScanScroll(false);
        /*
        //设置viewpager切换事件监听
        viewpager_qc.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //页面滑动事件
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.d(TAG,"onPageScrolled:"+position);
            }

            //页面选中事件
            @Override
            public void onPageSelected(int position) {
                //设置选中标题
                setSelectTabTitle(position);
            }

            //页面滚动状态改变事件
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_QC_back:
                exit();
                break;

            case R.id.tv_check_some://抽检
                //设置选中标题
                setSelectTabTitle(0);
                viewpager_qc.setCurrentItem(0);
                break;

            case R.id.tv_check_all://全捡
                //设置选中标题
                setSelectTabTitle(1);
                viewpager_qc.setCurrentItem(1);
                break;
        }
    }

    //物理返回键
    @Override
    public void onBackPressed() {
        exit();
    }


    //设置选中页面时标题
    private void setSelectTabTitle(int tab){
        resetTitle();
        switch (tab){
            case 0:
                globalData.setOperType(Constants.CHECKMATERIAL);
                tv_check_some.setBackgroundResource(R.drawable.factory_feed_click_shape);
                break;
            case 1:
                globalData.setOperType(Constants.CHECKALLMATERIAL);
                tv_check_all.setBackgroundResource(R.drawable.factory_checkall_click_shape);
                break;
        }
    }

    //设置标题为原状态
    private void resetTitle(){
        tv_check_some.setBackgroundResource(R.drawable.factory_feed_unclick_shape);
        tv_check_all.setBackgroundResource(R.drawable.factory_checkall_unclick_shape);
    }

    //返回主页
    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mQCHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            Intent intent=getIntent();
            Bundle bundle=intent.getExtras();
            intent.putExtras(bundle);
            setResult(RESULT_OK,intent);
            this.finish();
        }
    }
    
}
