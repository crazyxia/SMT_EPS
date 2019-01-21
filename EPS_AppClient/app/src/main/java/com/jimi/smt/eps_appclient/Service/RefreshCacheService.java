package com.jimi.smt.eps_appclient.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.google.gson.Gson;
import com.jimi.smt.eps_appclient.Beans.Material;
import com.jimi.smt.eps_appclient.Dao.FLCheckAll;
import com.jimi.smt.eps_appclient.Dao.Feed;
import com.jimi.smt.eps_appclient.Dao.GreenDaoUtil;
import com.jimi.smt.eps_appclient.Dao.QcCheckAll;
import com.jimi.smt.eps_appclient.Dao.Ware;
import com.jimi.smt.eps_appclient.Func.HttpUtils;
import com.jimi.smt.eps_appclient.Func.Log;
import com.jimi.smt.eps_appclient.Interfaces.OkHttpInterface;
import com.jimi.smt.eps_appclient.Unit.GlobalData;
import com.jimi.smt.eps_appclient.Unit.Constants;
import com.jimi.smt.eps_appclient.Unit.EvenBusTest;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RefreshCacheService extends Service implements OkHttpInterface {
    private static final String TAG = "RefreshCacheService";
    private GlobalData globalData;
    private HttpUtils mHttpUtils;
    private int checkAllTimeOut = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        globalData = (GlobalData) getApplication();
//        Log.d(TAG, "onCreate - " + "  line : " + globalData.getLine() + "  workOrder: " + globalData.getWork_order() + "  boardType: " + globalData.getBoard_type());
        mHttpUtils = new HttpUtils(this, getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return new Binder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        if (Constants.liveUpdate) {
            getRefreshProgram(globalData);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void getRefreshProgram(final GlobalData globalData) {
        mThread.start();
    }


    private Thread mThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                //根据线号、工单、版面类型去获取programId, 再根据programId去获取料号表
                String line = globalData.getLine();
                String workOrder = globalData.getWork_order();
                int boardType = globalData.getBoard_type();
//                Log.d(TAG, "getRefreshProgram - " + "  line : " + line + "  workOrder: " + workOrder + "  boardType: " + boardType);
//                mHttpUtils.getProgramId(line, workOrder, boardType);
                mHttpUtils.isCheckAllTimeOut(line, workOrder, boardType);
            }
        }
    });

    @Override
    public void showHttpResponse(int code, Object request, String response) {
        switch (code) {

            case HttpUtils.CodeIsCheckAllTimeOut:
                Log.d(TAG, "response - " + response);
                String programId = "";
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    programId = jsonObject.getString("programId");
                    checkAllTimeOut = jsonObject.getInt("isCheckAllTimeOutExist");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "showHttpResponse - " + e.toString());
                }
                if (null != programId && !("".equals(programId))) {
                    int result = doCheckProgramId(programId);
                    mHttpUtils.getMaterials(programId, result);
                }
                break;

            case HttpUtils.CodeMaterials:
                int resCode = -1;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    resCode = jsonObject.getInt("code");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "showHttpResponse - " + response);
                }
                int equal = (int) ((Object[]) request)[1];
                Gson gson = new Gson();
                if (resCode == 1) {
                    List<Material.MaterialBean> materialBeans = gson.fromJson(response, Material.class).getData();
                    for (Material.MaterialBean bean : materialBeans) {
                        bean.setLine(globalData.getLine());
                        bean.setWorkOrder(globalData.getWork_order());
                        bean.setBoardType(globalData.getBoard_type());
                    }
                    doRefresh(materialBeans, checkAllTimeOut, equal);
                } else {
                    // TODO: 2018/12/26 判断是否全检超时
                    EvenBusTest evenBusTest = doCheckTimeOut(new EvenBusTest(), checkAllTimeOut, equal);
                    //发送消息
                    EventBus.getDefault().post(evenBusTest);
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void showHttpError(int code, Object request, String s) {

    }


    /**
     * 改变programId
     *
     * @param newProgramId
     * @return 1, 表示相同; 0,表示不相同
     */
    private int doCheckProgramId(String newProgramId) {
        if (newProgramId.equals(globalData.getProgramId()))
            return 1;
        //programId不一样
        globalData.setProgramId(newProgramId);
        return 0;
    }


    /**
     * 获取站位表更新
     *
     * @param materialBeans 站位表
     * @param timeOut       1, 表示超时 ;  0,表示不超时
     * @param equal         1,表示相同; 0,表示不相同
     */
    private void doRefresh(List<Material.MaterialBean> materialBeans, int timeOut, int equal) {
        EvenBusTest evenBusTest = new EvenBusTest();
        //当前的料号表
        List<Material.MaterialBean> oldMaterialBeans = globalData.getMaterialBeans();
        //判断两个列表是否一致
        boolean isEqual = compare(oldMaterialBeans, materialBeans);
        Log.d(TAG, "doRefresh - isEqual " + isEqual);
        //更新
        if (!isEqual) {
            //更新
            evenBusTest.setUpdated(0);
            //超时与否
            // TODO: 2018/12/26
            evenBusTest.setCheckAllTimeOut(timeOut);
            //更新全局变量
            globalData.setMaterialBeans(materialBeans);
            //更新本地数据库
            if (Constants.isCache) {
                List<Material.MaterialBean> updateBeans = getUpdateMaterials(oldMaterialBeans, materialBeans);
                List<String> seats = getUpdateSeats(updateBeans);

                Log.d(TAG, "globalData.getUserType - " + globalData.getUserType());


                if ((globalData.getUserType() == Constants.WARE_HOUSE)
                        || (globalData.getUserType() == Constants.ADMIN && globalData.getAdminOperType() == Constants.ADMIN_WARE_HOUSE)) {
                    //发料纪录
                    List<Ware> wares = new GreenDaoUtil().queryWareRecord(globalData.getOperator(), globalData.getWork_order(),
                            globalData.getLine(), globalData.getBoard_type());
                    //发料
                    if (wares != null && wares.size() > 0) {
                        //更新programID
                        for (Ware ware : wares) {
                            ware.setProgramId(globalData.getProgramId());
                            GreenDaoUtil.getGreenDaoUtil().updateWare(ware);
                        }
                        //先删除更新的对应站位的数据
                        boolean delete = new GreenDaoUtil().deleteWareBySeat(seats);
                        Log.d(TAG, "发料 删除 - " + delete);
                        //再更新本地数据库
                        boolean update = new GreenDaoUtil().updateOrInsertWare(getUpdateWares(seats, materialBeans));
                        Log.d(TAG, "发料 更新本地数据库 - " + update);
                        //获取本地数据库最新的发料数据
                        List<Ware> newWares = new GreenDaoUtil().queryWareRecord(globalData.getOperator(), globalData.getWork_order()
                                , globalData.getLine(), globalData.getBoard_type());
                        //保存到订阅事件中
                        evenBusTest.setWareList(newWares);
                    }
                } else if ((globalData.getUserType() == Constants.FACTORY)
                        || (globalData.getUserType() == Constants.ADMIN && globalData.getAdminOperType() == Constants.ADMIN_FACTORY)) {
                    //上料纪录、操作员全检纪录
                    List<Feed> feeds = new GreenDaoUtil().queryFeedRecord(globalData.getOperator(), globalData.getWork_order(),
                            globalData.getLine(), globalData.getBoard_type());
                    List<FLCheckAll> flCheckAlls = new GreenDaoUtil().queryFLCheckRecord(globalData.getOperator(), globalData.getWork_order(),
                            globalData.getLine(), globalData.getBoard_type());
                    //上料
                    if (feeds != null && feeds.size() > 0) {
                        for (Feed feed : feeds) {
                            feed.setProgramId(globalData.getProgramId());
                            GreenDaoUtil.getGreenDaoUtil().updateFeed(feed);
                        }
                        //先删除更新的对应站位的数据
                        boolean delete = new GreenDaoUtil().deleteFeedBySeat(seats);
                        Log.d(TAG, "上料 删除 - " + delete);
                        //再更新本地数据库
                        boolean update = new GreenDaoUtil().updateOrInsertFeed(getUpdateFeeds(seats, materialBeans));
                        Log.d(TAG, "上料 更新本地数据库 - " + update);
                        //获取本地数据库最新的上料数据
                        List<Feed> newFeeds = new GreenDaoUtil().queryFeedRecord(globalData.getOperator(), globalData.getWork_order()
                                , globalData.getLine(), globalData.getBoard_type());
                        //保存到订阅事件中
                        evenBusTest.setFeedList(newFeeds);
                    }
                    //操作员全检
                    if (flCheckAlls != null && flCheckAlls.size() > 0) {
                        // TODO: 2018/12/26
                        if (timeOut == 0) {
                            //未超时
                            for (FLCheckAll flCheckAll : flCheckAlls) {
                                flCheckAll.setProgramId(globalData.getProgramId());
                                GreenDaoUtil.getGreenDaoUtil().updateFLCheck(flCheckAll);
                            }
                        } else if (timeOut == 1) {
                            //超时
                            for (FLCheckAll flCheckAll : flCheckAlls) {
                                flCheckAll.setProgramId(globalData.getProgramId());
                                flCheckAll.setScanLineSeat("");
                                flCheckAll.setScanMaterial("");
                                flCheckAll.setRemark("");
                                flCheckAll.setResult("");
                                GreenDaoUtil.getGreenDaoUtil().updateFLCheck(flCheckAll);
                            }
                        }
                        //先删除更新的对应站位的数据
                        boolean delete = new GreenDaoUtil().deleteFLCheckBySeat(seats);
                        Log.d(TAG, "操作员全检 删除 - " + delete);
                        //再更新本地数据库
                        boolean update = new GreenDaoUtil().updateOrInsertFLCheck(getUpdateFLChecks(seats, materialBeans));
                        Log.d(TAG, "操作员全检 更新本地数据库 - " + update);
                        //获取本地数据库最新的操作员全检数据
                        List<FLCheckAll> newFLCheckAlls = new GreenDaoUtil().queryFLCheckRecord(globalData.getOperator(), globalData.getWork_order()
                                , globalData.getLine(), globalData.getBoard_type());
                        //保存到订阅事件中
                        evenBusTest.setFlCheckAllList(newFLCheckAlls);
                    }
                } else if ((globalData.getUserType() == Constants.QC)
                        || (globalData.getUserType() == Constants.ADMIN && globalData.getAdminOperType() == Constants.ADMIN_QC)) {
                    //QC全检纪录
                    List<QcCheckAll> qcCheckAlls = new GreenDaoUtil().queryQcCheckRecord(globalData.getOperator(), globalData.getWork_order()
                            , globalData.getLine(), globalData.getBoard_type());
                    if (qcCheckAlls != null && qcCheckAlls.size() > 0) {
                        // TODO: 2018/12/26
                        if (timeOut == 0) {
                            for (QcCheckAll qcCheckAll : qcCheckAlls) {
                                qcCheckAll.setProgramId(globalData.getProgramId());
                                GreenDaoUtil.getGreenDaoUtil().updateQcCheck(qcCheckAll);
                            }
                        } else if (timeOut == 1) {
                            for (QcCheckAll qcCheckAll : qcCheckAlls) {
                                qcCheckAll.setProgramId(globalData.getProgramId());
                                qcCheckAll.setScanLineSeat("");
                                qcCheckAll.setScanMaterial("");
                                qcCheckAll.setRemark("");
                                qcCheckAll.setResult("");
                                GreenDaoUtil.getGreenDaoUtil().updateQcCheck(qcCheckAll);
                            }
                        }

                        //先删除更新的对应站位的数据
                        boolean delete = new GreenDaoUtil().deleteQcCheckBySeat(seats);
                        Log.d(TAG, "QC全检纪录 删除 - " + delete);
                        //再更新本地数据库
                        boolean update = new GreenDaoUtil().updateOrInsertQcCheck(getUpdateQcChecks(seats, materialBeans));
                        Log.d(TAG, "QC全检纪录 更新本地数据库 - " + update);
                        //获取本地数据库最新的QC全检纪录数据
                        List<QcCheckAll> newQcCheckAlls = new GreenDaoUtil().queryQcCheckRecord(globalData.getOperator(), globalData.getWork_order()
                                , globalData.getLine(), globalData.getBoard_type());
                        //保存到订阅事件中
                        evenBusTest.setQcCheckAllList(newQcCheckAlls);
                    }
                }
            }
            globalData.setUpdateProgram(true);
        }
        //未更新
        else {
            // TODO: 2018/12/26
            evenBusTest = doCheckTimeOut(evenBusTest, timeOut, equal);
        }

        //发送消息
        EventBus.getDefault().post(evenBusTest);
    }

    /**
     * 获取更新不成功，判断全检超时情况
     *
     * @param checkAllTimeOut 1, 表示超时 ;  0,表示不超时
     * @param equal           1,表示相同; 0,表示不相同
     */
    private EvenBusTest doCheckTimeOut(EvenBusTest evenBusTest, int checkAllTimeOut, int equal) {
        //不更新
        evenBusTest.setUpdated(1);
        evenBusTest.setCheckAllTimeOut(checkAllTimeOut);
        evenBusTest.setProgramIdEqual(equal);
        int userType = globalData.getUserType();
        if (checkAllTimeOut == 1) {
            //超时
            if ((globalData.getUserType() == Constants.WARE_HOUSE)
                    || (globalData.getUserType() == Constants.ADMIN && globalData.getAdminOperType() == Constants.ADMIN_WARE_HOUSE)) {
                //仓库
                if (0 == equal) {
                    //发料纪录
                    List<Ware> wares = new GreenDaoUtil().queryWareRecord(globalData.getOperator(), globalData.getWork_order(),
                            globalData.getLine(), globalData.getBoard_type());
                    //发料
                    if (wares != null && wares.size() > 0) {
                        //更新programID
                        for (Ware ware : wares) {
                            ware.setProgramId(globalData.getProgramId());
                            ware.setScanLineSeat("");
                            ware.setScanMaterial("");
                            ware.setRemark("");
                            ware.setResult("");
                            GreenDaoUtil.getGreenDaoUtil().updateWare(ware);
                        }
                        //保存到订阅事件中
                        evenBusTest.setWareList(wares);
                    }
                }

            } else if ((userType == Constants.FACTORY)
                    || (userType == Constants.ADMIN && globalData.getAdminOperType() == Constants.ADMIN_FACTORY)) {
                //厂线操作员
                List<FLCheckAll> flCheckAlls = new GreenDaoUtil().queryFLCheckRecord(globalData.getOperator(), globalData.getWork_order(),
                        globalData.getLine(), globalData.getBoard_type());
                //操作员全检
                if (flCheckAlls != null && flCheckAlls.size() > 0) {
                    for (FLCheckAll flCheckAll : flCheckAlls) {
                        flCheckAll.setProgramId(globalData.getProgramId());
                        flCheckAll.setScanLineSeat("");
                        flCheckAll.setScanMaterial("");
                        flCheckAll.setRemark("");
                        flCheckAll.setResult("");
                        GreenDaoUtil.getGreenDaoUtil().updateFLCheck(flCheckAll);
                    }
                    //保存到订阅事件中
                    evenBusTest.setFlCheckAllList(flCheckAlls);
                }

                //programId不同
                if (0 == equal) {
                    List<Feed> feeds = new GreenDaoUtil().queryFeedRecord(globalData.getOperator(), globalData.getWork_order(),
                            globalData.getLine(), globalData.getBoard_type());
                    //上料
                    if (feeds != null && feeds.size() > 0) {
                        for (Feed feed : feeds) {
                            feed.setProgramId(globalData.getProgramId());
                            feed.setScanLineSeat("");
                            feed.setScanMaterial("");
                            feed.setRemark("");
                            feed.setResult("");
                            GreenDaoUtil.getGreenDaoUtil().updateFeed(feed);
                        }
                        //保存到订阅事件中
                        evenBusTest.setFeedList(feeds);
                    }
                }

            } else if ((userType == Constants.QC)
                    || (userType == Constants.ADMIN && globalData.getAdminOperType() == Constants.ADMIN_QC)) {
                //QC全检纪录
                List<QcCheckAll> qcCheckAlls = new GreenDaoUtil().queryQcCheckRecord(globalData.getOperator(), globalData.getWork_order()
                        , globalData.getLine(), globalData.getBoard_type());
                if (qcCheckAlls != null && qcCheckAlls.size() > 0) {
                    for (QcCheckAll qcCheckAll : qcCheckAlls) {
                        qcCheckAll.setProgramId(globalData.getProgramId());
                        qcCheckAll.setScanLineSeat("");
                        qcCheckAll.setScanMaterial("");
                        qcCheckAll.setRemark("");
                        qcCheckAll.setResult("");
                        GreenDaoUtil.getGreenDaoUtil().updateQcCheck(qcCheckAll);
                    }
                    //保存到订阅事件中
                    evenBusTest.setQcCheckAllList(qcCheckAlls);
                }
            }
        } else {
            //不超时,判断 programId是否一样
            if (0 == equal) {
                //不一样
                if ((globalData.getUserType() == Constants.WARE_HOUSE)
                        || (globalData.getUserType() == Constants.ADMIN && globalData.getAdminOperType() == Constants.ADMIN_WARE_HOUSE)) {
                    //仓库发料纪录
                    List<Ware> wares = new GreenDaoUtil().queryWareRecord(globalData.getOperator(), globalData.getWork_order(),
                            globalData.getLine(), globalData.getBoard_type());
                    //发料
                    if (wares != null && wares.size() > 0) {
                        //更新programID
                        for (Ware ware : wares) {
                            ware.setProgramId(globalData.getProgramId());
                            ware.setScanLineSeat("");
                            ware.setScanMaterial("");
                            ware.setRemark("");
                            ware.setResult("");
                            GreenDaoUtil.getGreenDaoUtil().updateWare(ware);
                        }
                        //保存到订阅事件中
                        evenBusTest.setWareList(wares);
                    }

                } else if ((userType == Constants.FACTORY)
                        || (userType == Constants.ADMIN && globalData.getAdminOperType() == Constants.ADMIN_FACTORY)) {
                    //厂线操作员
                    List<FLCheckAll> flCheckAlls = new GreenDaoUtil().queryFLCheckRecord(globalData.getOperator(), globalData.getWork_order(),
                            globalData.getLine(), globalData.getBoard_type());
                    //厂线上料
                    List<Feed> feeds = new GreenDaoUtil().queryFeedRecord(globalData.getOperator(), globalData.getWork_order(),
                            globalData.getLine(), globalData.getBoard_type());
                    //操作员全检
                    if (flCheckAlls != null && flCheckAlls.size() > 0) {
                        for (FLCheckAll flCheckAll : flCheckAlls) {
                            flCheckAll.setProgramId(globalData.getProgramId());
                            flCheckAll.setScanLineSeat("");
                            flCheckAll.setScanMaterial("");
                            flCheckAll.setRemark("");
                            flCheckAll.setResult("");
                            GreenDaoUtil.getGreenDaoUtil().updateFLCheck(flCheckAll);
                        }
                        //保存到订阅事件中
                        evenBusTest.setFlCheckAllList(flCheckAlls);
                    }
                    //上料
                    if (feeds != null && feeds.size() > 0) {
                        for (Feed feed : feeds) {
                            feed.setProgramId(globalData.getProgramId());
                            feed.setScanLineSeat("");
                            feed.setScanMaterial("");
                            feed.setRemark("");
                            feed.setResult("");
                            GreenDaoUtil.getGreenDaoUtil().updateFeed(feed);
                        }
                        //保存到订阅事件中
                        evenBusTest.setFeedList(feeds);
                    }

                } else if ((userType == Constants.QC)
                        || (userType == Constants.ADMIN && globalData.getAdminOperType() == Constants.ADMIN_QC)) {
                    //QC全检纪录
                    List<QcCheckAll> qcCheckAlls = new GreenDaoUtil().queryQcCheckRecord(globalData.getOperator(), globalData.getWork_order()
                            , globalData.getLine(), globalData.getBoard_type());
                    if (qcCheckAlls != null && qcCheckAlls.size() > 0) {
                        for (QcCheckAll qcCheckAll : qcCheckAlls) {
                            qcCheckAll.setProgramId(globalData.getProgramId());
                            qcCheckAll.setScanLineSeat("");
                            qcCheckAll.setScanMaterial("");
                            qcCheckAll.setRemark("");
                            qcCheckAll.setResult("");
                            GreenDaoUtil.getGreenDaoUtil().updateQcCheck(qcCheckAll);
                        }
                        //保存到订阅事件中
                        evenBusTest.setQcCheckAllList(qcCheckAlls);
                    }
                }
            }
        }

        return evenBusTest;
    }


    private boolean compare(List<Material.MaterialBean> a, List<Material.MaterialBean> b) {
        if (a.size() != b.size())
            return false;
        for (int i = 0, len = a.size(); i < len; i++) {
            //序列号
            if (a.get(i).getSerialNo() != (b.get(i).getSerialNo())) {

//                Log.d(TAG, "itemList - " + a.get(i).getSerialNo());
//                Log.d(TAG, "NewItemList - " + b.get(i).getSerialNo());
                return false;
            }
            //原始料号
            if (!a.get(i).getMaterialNo().equalsIgnoreCase(b.get(i).getMaterialNo())) {
//                Log.d(TAG, "itemList - " + a.get(i).getMaterialNo());
//                Log.d(TAG, "NewItemList - " + b.get(i).getMaterialNo());
                return false;
            }
            //原始站位
            if (!a.get(i).getLineseat().equalsIgnoreCase(b.get(i).getLineseat())) {
//                Log.d(TAG, "itemList - " + a.get(i).getLineseat());
//                Log.d(TAG, "NewItemList - " + b.get(i).getLineseat());
                return false;
            }
            //主替料
            if (!(a.get(i).isAlternative() == b.get(i).isAlternative())) {
//                Log.d(TAG, "itemList - " + a.get(i).isAlternative());
//                Log.d(TAG, "NewItemList - " + b.get(i).isAlternative());
                return false;
            }
            //BOM料号/规格
            if (!(a.get(i).getSpecitification() .equalsIgnoreCase(b.get(i).getSpecitification()))) {
//                Log.d(TAG, "itemList - " + a.get(i).getSpecitification());
//                Log.d(TAG, "NewItemList - " + b.get(i).getSpecitification());
                return false;
            }
            //数量
            if (!(a.get(i).getQuantity() == b.get(i).getQuantity())) {
//                Log.d(TAG, "itemList - " + a.get(i).getQuantity());
//                Log.d(TAG, "NewItemList - " + b.get(i).getQuantity());
                return false;
            }
            //单板位置
            if (!(a.get(i).getPosition() .equalsIgnoreCase(b.get(i).getPosition()))) {
//                Log.d(TAG, "itemList - " + a.get(i).isAlternative());
//                Log.d(TAG, "NewItemList - " + b.get(i).isAlternative());
                return false;
            }
        }
        return true;
    }

    //获取更新或删除的项
    private List<Material.MaterialBean> getUpdateMaterials(List<Material.MaterialBean> oldList, List<Material.MaterialBean> newList) {
        HashSet<Material.MaterialBean> updateItemsHas = new HashSet<>();
        List<Material.MaterialBean> updateItems = new ArrayList<>();
        for (Material.MaterialBean materialItem : newList) {
            if (!oldList.contains(materialItem)) {
                updateItemsHas.add(materialItem);
//                Log.d(TAG, "不同的 - " + materialItem.getMaterialStr());
            }
        }
        for (Material.MaterialBean materialItem : oldList) {
            if (!newList.contains(materialItem)) {
                updateItemsHas.add(materialItem);
//                Log.d(TAG, "不同的 = " + materialItem.getMaterialStr());
            }
        }
        updateItems.addAll(updateItemsHas);
        Log.d(TAG, "getUpdateMaterials - " + updateItems.size());
        return updateItems;
    }

    //获取更新的站位
    private List<String> getUpdateSeats(List<Material.MaterialBean> updateItems) {
        List<String> seatStrings = new ArrayList<>();
        HashSet<String> seatHashSet = new HashSet<>();
        for (Material.MaterialBean materialItem : updateItems) {
            seatHashSet.add(materialItem.getLineseat());
        }
        seatStrings.addAll(seatHashSet);
        return seatStrings;
    }

    //获取更新数据库的发料实体
    private List<Ware> getUpdateWares(List<String> seats, List<Material.MaterialBean> newList) {
        List<Ware> wares = new ArrayList<>();
        for (String s : seats) {
            for (Material.MaterialBean materialItem : newList) {
                if (materialItem.getLineseat().equalsIgnoreCase(s)) {
                    Ware ware = new Ware(null, materialItem.getProgramId(), materialItem.getWorkOrder(),
                            globalData.getOperator(), materialItem.getBoardType(), materialItem.getLine(),
                            materialItem.getSerialNo(), materialItem.isAlternative(), materialItem.getSpecitification(),
                            materialItem.getPosition(),materialItem.getQuantity(),materialItem.getLineseat(),
                            materialItem.getMaterialNo(), materialItem.getScanlineseat(), materialItem.getScanMaterial(),
                            materialItem.getResult(), materialItem.getRemark());
                    wares.add(ware);
                }
            }
        }
        Log.d(TAG, "getUpdateWares - " + wares.size());
        return wares;
    }

    //获取更新数据库的上料实体
    private List<Feed> getUpdateFeeds(List<String> seats, List<Material.MaterialBean> newList) {
        List<Feed> feeds = new ArrayList<>();
        for (String s : seats) {
            for (Material.MaterialBean materialItem : newList) {
                if (materialItem.getLineseat().equalsIgnoreCase(s)) {
                    /*
                    Feed feed = new Feed(null, globalData.getWork_order(), globalData.getOperator(), globalData.getBoard_type(),
                            globalData.getLine(), materialItem.getOrgLineSeat(), materialItem.getOrgMaterial(),
                            "", "", "", "", materialItem.getSerialNo(), materialItem.getAlternative());
                    feeds.add(feed);
                    */

                    Feed feed = new Feed(null, materialItem.getProgramId(), materialItem.getWorkOrder(), globalData.getOperator(),
                            materialItem.getBoardType(), materialItem.getLine(), materialItem.getLineseat(), materialItem.getMaterialNo(),
                            materialItem.getSpecitification(),materialItem.getPosition(),materialItem.getQuantity(),
                            materialItem.getScanlineseat(), materialItem.getScanMaterial(), materialItem.getResult(), materialItem.getRemark(),
                            materialItem.getSerialNo(), materialItem.isAlternative());
                    feeds.add(feed);
                }
            }
        }
        Log.d(TAG, "getUpdateFeeds - " + feeds.size());
        return feeds;
    }

    //获取更新数据库的操作员全检实体
    private List<FLCheckAll> getUpdateFLChecks(List<String> seats, List<Material.MaterialBean> newList) {
        List<FLCheckAll> flCheckAlls = new ArrayList<>();
        for (String s : seats) {
            for (Material.MaterialBean materialItem : newList) {
                if (materialItem.getLineseat().equalsIgnoreCase(s)) {

                    /*
                    FLCheckAll flCheckAll = new FLCheckAll(null, globalData.getWork_order(), globalData.getOperator(), globalData.getBoard_type(),
                            globalData.getLine(), materialItem.getSerialNo(), materialItem.getAlternative(), materialItem.getOrgLineSeat(), materialItem.getOrgMaterial(),
                            "", "", "", "");
                    flCheckAlls.add(flCheckAll);
                    */

                    FLCheckAll flCheckAll = new FLCheckAll(null, materialItem.getProgramId(), materialItem.getWorkOrder(),
                            globalData.getOperator(), materialItem.getBoardType(), materialItem.getLine(), materialItem.getSerialNo(),
                            materialItem.isAlternative(), materialItem.getLineseat(), materialItem.getMaterialNo(),
                            materialItem.getSpecitification(),materialItem.getPosition(),materialItem.getQuantity(),materialItem.getScanlineseat(),
                            materialItem.getScanMaterial(), materialItem.getResult(), materialItem.getRemark());
                    flCheckAlls.add(flCheckAll);
                }
            }
        }
        Log.d(TAG, "getUpdateFLChecks - " + flCheckAlls.size());
        return flCheckAlls;
    }

    //获取更新数据库的操作员全检实体
    private List<QcCheckAll> getUpdateQcChecks(List<String> seats, List<Material.MaterialBean> newList) {
        List<QcCheckAll> qcCheckAlls = new ArrayList<>();
        for (String s : seats) {
            for (Material.MaterialBean materialItem : newList) {
                if (materialItem.getLineseat().equalsIgnoreCase(s)) {
                    /*
                    QcCheckAll qcCheckAll = new QcCheckAll(null, globalData.getWork_order(), globalData.getOperator(), globalData.getBoard_type(),
                            globalData.getLine(), materialItem.getSerialNo(), materialItem.getAlternative(), materialItem.getOrgLineSeat(), materialItem.getOrgMaterial(),
                            "", "", "", "");
                    */
                    QcCheckAll qcCheckAll = new QcCheckAll(null, materialItem.getProgramId(), materialItem.getWorkOrder(),
                            globalData.getOperator(), materialItem.getBoardType(), materialItem.getLine(), materialItem.getSerialNo(),
                            materialItem.isAlternative(), materialItem.getLineseat(), materialItem.getMaterialNo(),
                            materialItem.getSpecitification(),materialItem.getPosition(),materialItem.getQuantity(),materialItem.getScanlineseat(),
                            materialItem.getScanMaterial(), materialItem.getResult(), materialItem.getRemark());
                    qcCheckAlls.add(qcCheckAll);
                }
            }
        }
        Log.d(TAG, "getUpdateQcChecks - " + qcCheckAlls.size());
        return qcCheckAlls;
    }


    //发送广播
    private void sendContentBroadcast(int update) {
        Intent intent = new Intent();
        intent.setAction("com.jimi.smt.eps_appclient.update_program");
        intent.putExtra("update", update);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        stopSelf();
        super.onDestroy();
    }
}
