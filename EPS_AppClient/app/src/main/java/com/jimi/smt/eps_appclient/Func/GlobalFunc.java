package com.jimi.smt.eps_appclient.Func;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.jimi.smt.eps_appclient.R;
import com.jimi.smt.eps_appclient.Views.InfoDialog;

/**
 * 类名:GlobalFunc
 * 修改人:Liang GuoChang
 * 修改时间:2017/11/07 9:29
 * 描述: 公共方法类
 */
public class GlobalFunc {
    private static final String TAG = "GlobalFunc";
    private Context context;

    public GlobalFunc() {
    }

    public GlobalFunc(Context context) {
        //获得全局变量
        this.context = context;
    }

    //判断是否有网络链接
    public boolean isNetWorkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            Log.d(TAG, "networkInfo::" + networkInfo.isAvailable());
            return networkInfo.isAvailable();
        }
        return false;
    }

    /**
     * 提示网络连接失败
     *
     * @param title           提示
     * @param message         提示内容
     * @param toast 提示
     */
    public void showInfo(String title, String message, final String toast) {

        //对话框所有控件id
//        int itemResIds[] = new int[]{R.id.dialog_title_view,
//                R.id.dialog_title, R.id.tv_alert_info, R.id.info_trust};

        int itemResIds[] = new int[]{R.id.dialog_title_view,
                R.id.dialog_title, R.id.tv_alert_info, R.id.info_trust, R.id.tv_alert_msg};


        //标题和内容
        String titleMsg[] = new String[]{title, message};
        //内容的样式
        int msgStype[] = new int[]{22, Color.RED};

        if (message.equalsIgnoreCase("站位表更新!")){
            msgStype = new int[]{22,Color.GREEN};
        }

        InfoDialog infoDialog = new InfoDialog(context,
                R.layout.info_dialog_layout, itemResIds, titleMsg, msgStype);

        infoDialog.setOnDialogItemClickListener((dialog, view) -> {
            switch (view.getId()) {
                case R.id.info_trust:
                    dialog.dismiss();
                    Toast.makeText(context, toast, Toast.LENGTH_LONG).show();
                    break;
            }
        });
        infoDialog.show();
    }



    //获取扫描的线号
    public String getLineNum(String scanValue) {
        String lineNum = scanValue.replaceAll(" ", "").trim();
        if (scanValue.length() >= 8) {
            lineNum = "30" + scanValue.substring(3, 4);
        }
        return lineNum;
    }

    //根据扫的站位条码获取站位值 (两者兼容100805118,3040101)
    public String getLineSeat(String scanValue) {
        scanValue = scanValue.replaceAll(" ", "").trim();
        String linSeat = scanValue;
        if (scanValue.length() >= 8) {
            linSeat = scanValue.substring(4, 6) + "-" + scanValue.substring(6, 8);
        } else if (scanValue.length() == 7) {
            linSeat = scanValue.substring(3, 5) + "-" + scanValue.substring(5, 7);
        } else if (scanValue.length() == 5) {
            linSeat = scanValue.substring(1, 3) + "-" + scanValue.substring(3, 5);
        }
        return linSeat;
    }

    //获取料号(新料号格式 K310160008E203@300@1520814123730@A1119@@00-00@1@; )
    public String getMaterial(String scanValue) {
        scanValue = scanValue.replaceAll(" ", "").trim();
        String material = scanValue;
        if (scanValue.indexOf("@") != -1) {
            material = scanValue.substring(0, scanValue.indexOf("@"));
        }
        return material;
    }

    //获取料号的流水号(即是时间戳,如料号:K310160008E203@300@1520814123730@A1119@@00-00@1@)
    public String getSerialNum(String scanValue) {
        String[] materialStr;
        scanValue = scanValue.replaceAll(" ", "").trim();
        String serialNum = scanValue;
        if (scanValue.contains("@")) {
//            serialNum = scanValue.substring(scanValue.lastIndexOf("@"),scanValue.length());
            materialStr = serialNum.split("@");
            serialNum = materialStr[2];
        }
        return serialNum;
    }


}
