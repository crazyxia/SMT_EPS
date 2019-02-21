package com.jimi.smt.eps_appclient.Dao;

import com.jimi.smt.eps_appclient.Beans.Material;
import com.jimi.smt.eps_appclient.Func.Log;
import com.jimi.smt.eps_appclient.gen.FLCheckAllDao;
import com.jimi.smt.eps_appclient.gen.FeedDao;
import com.jimi.smt.eps_appclient.gen.QcCheckAllDao;
import com.jimi.smt.eps_appclient.gen.WareDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * 类名:GreenDaoUtil
 * 创建人:Liang GuoChang
 * 创建时间:2018/3/26 17:15
 * 描述:数据库操作类
 * 版本号:
 * 修改记录:
 */

public class GreenDaoUtil {

    private static String TAG = "GreenDaoUtil";
    private static GreenDaoUtil greenDaoUtil = null;

    public GreenDaoUtil() {

    }

    public static GreenDaoUtil getGreenDaoUtil() {
        if (null == greenDaoUtil) {
            greenDaoUtil = new GreenDaoUtil();
        }
        return greenDaoUtil;
    }

    //更新数据
    /*public void updateData(){
        List<UserTest> userTests = getUserTestDao().loadAll();
        UserTest userTest = new UserTest(userTests.get(0).getId(),"更改后的数据1",22,true);
        getUserTestDao().update(userTest);
    }*/

    //查询数据详细 // TODO: 2018/3/26
    /*public void queryData(){
        List<UserTest> userTests = getUserTestDao().loadAll();
        for (UserTest userTest : userTests){
            Log.d(TAG,"userTest - id - "+userTest.getId()+" - name - "+userTest.getName());
        }
    }*/

    //根据条件查询数据库有无上料纪录
    public List<Feed> queryFeedRecord(String operator, String order, String line, int boardType) {
        Log.d(TAG, " - queryFeedRecord - ");
        QueryBuilder<Feed> feedQuery = getFeedDao().queryBuilder();
        feedQuery.where(FeedDao.Properties.Operator.eq(operator),
                FeedDao.Properties.Order.eq(order),
                FeedDao.Properties.Line.eq(line),
                FeedDao.Properties.Board_type.eq(boardType));
        List<Feed> feedList = feedQuery.orderAsc(FeedDao.Properties.OrgLineSeat)
                .orderAsc(FeedDao.Properties.SerialNo)
                .orderAsc(FeedDao.Properties.OrgMaterial)
                .list();
        Log.d(TAG, "queryFeedRecord - " + feedList.size());
        return feedList;
    }

    //根据条件查询数据库有无发料纪录
    public List<Ware> queryWareRecord(String operator, String order, String line, int boardType) {
        Log.d(TAG, " - queryWareRecord - ");
        QueryBuilder<Ware> wareQuery = getWareDao().queryBuilder();
        wareQuery.where(WareDao.Properties.Operator.eq(operator),
                WareDao.Properties.Order.eq(order),
                WareDao.Properties.Line.eq(line),
                WareDao.Properties.Board_type.eq(boardType));
        List<Ware> wareList = wareQuery.orderAsc(WareDao.Properties.OrgLineSeat)
                .orderAsc(WareDao.Properties.SerialNo)
                .orderAsc(WareDao.Properties.OrgMaterial)
                .list();
        Log.d(TAG, "queryWareRecord - " + wareList.size());
        return wareList;
    }

    //根据条件查询数据库有无操作员全检纪录
    public List<FLCheckAll> queryFLCheckRecord(String operator, String order, String line, int boardType) {
        Log.d(TAG, " - queryFLCheckRecord - ");
        QueryBuilder<FLCheckAll> flCheckQuery = getFlCheckAllDao().queryBuilder();
        flCheckQuery.where(FLCheckAllDao.Properties.Operator.eq(operator),
                FLCheckAllDao.Properties.Order.eq(order),
                FLCheckAllDao.Properties.Line.eq(line),
                FLCheckAllDao.Properties.Board_type.eq(boardType));
        List<FLCheckAll> flCheckList = flCheckQuery.orderAsc(FLCheckAllDao.Properties.OrgLineSeat)
                .orderAsc(FLCheckAllDao.Properties.SerialNo)
                .orderAsc(FLCheckAllDao.Properties.OrgMaterial)
                .list();
        Log.d(TAG, "queryFLCheckRecord - " + flCheckList.size());
        return flCheckList;
    }

    //根据条件查询数据库有无IPQC全检纪录
    public List<QcCheckAll> queryQcCheckRecord(String operator, String order, String line, int boardType) {
        Log.d(TAG, " - queryQcCheckRecord - ");
        QueryBuilder<QcCheckAll> qcCheckQuery = getQcCheckAllDao().queryBuilder();
        qcCheckQuery.where(QcCheckAllDao.Properties.Operator.eq(operator),
                QcCheckAllDao.Properties.Order.eq(order),
                QcCheckAllDao.Properties.Line.eq(line),
                QcCheckAllDao.Properties.Board_type.eq(boardType));
        List<QcCheckAll> qcCheckList = qcCheckQuery.orderAsc(QcCheckAllDao.Properties.OrgLineSeat)
                .orderAsc(QcCheckAllDao.Properties.SerialNo)
                .orderAsc(QcCheckAllDao.Properties.OrgMaterial)
                .list();
        Log.d(TAG, "queryQcCheckRecord - " + qcCheckList.size());
        return qcCheckList;
    }

    //批量插入上料初始数据
    public boolean insertMultiFeedMaterial(final List<Feed> feedList) {
        Log.d(TAG, " - insertMultiFeedMaterial - ");
        boolean result = false;
        try {
            GreenDaoManager.getInstance().getmDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (Feed feed : feedList) {
                        getFeedDao().insertOrReplace(feed);
                    }
                }
            });
            result = true;
        } catch (Exception e) {
            Log.d(TAG, "insertMultiFeedMaterial - " + e.toString());
        }
        return result;
    }

    //批量插入发料初始数据
    public boolean insertMultiWareMaterial(final List<Ware> wareList) {
        Log.d(TAG, " - insertMultiWareMaterial - ");
        boolean result = false;
        try {
            GreenDaoManager.getInstance().getmDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (Ware ware : wareList) {
                        getWareDao().insertOrReplace(ware);
                    }
                }
            });
            result = true;
        } catch (Exception e) {
            Log.d(TAG, "insertMultiWareMaterial - " + e.toString());
        }
        return result;
    }

    //批量插入操作员全检初始数据
    public boolean insertMultiFLCheckMaterial(final List<FLCheckAll> flCheckAllList) {
        Log.d(TAG, " - insertMultiFLCheckMaterial - ");
        boolean result = false;
        try {
            GreenDaoManager.getInstance().getmDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (FLCheckAll flCheckAll : flCheckAllList) {
                        getFlCheckAllDao().insertOrReplace(flCheckAll);
                    }
                }
            });
            result = true;
        } catch (Exception e) {
            Log.d(TAG, "insertMultiFLCheckMaterial - " + e.toString());
        }
        return result;
    }

    //批量插入IPQC全检初始数据
    public boolean insertMultiQcCheckMaterial(final List<QcCheckAll> qcCheckAllList) {
        Log.d(TAG, " - insertMultiQcCheckMaterial - ");
        boolean result = false;
        try {
            GreenDaoManager.getInstance().getmDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (QcCheckAll qcCheckAll : qcCheckAllList) {
                        getQcCheckAllDao().insertOrReplace(qcCheckAll);
                    }
                }
            });
            result = true;
        } catch (Exception e) {
            Log.d(TAG, "insertMultiQcCheckMaterial - " + e.toString());
        }
        return result;
    }

    //更新某一项上料结果
    public boolean updateFeed(Feed feed) {
        Log.d(TAG, " - updateFeed - ");
        boolean updateRes = false;
        try {
            getFeedDao().update(feed);
            updateRes = true;
        } catch (Exception e) {
            Log.d(TAG, "updateFeed - " + e.toString());
        }
        return updateRes;
    }

    //更新上料结果
    public boolean updateFeed(String operator, Material.MaterialBean bean) {
        boolean updateRes = false;
        QueryBuilder<Feed> queryBuilder = getFeedDao().queryBuilder();
        queryBuilder.where(FeedDao.Properties.Operator.eq(operator),
                FeedDao.Properties.Order.eq(bean.getWorkOrder()),
                FeedDao.Properties.Line.eq(bean.getLine()),
                FeedDao.Properties.Board_type.eq(bean.getBoardType()),
                FeedDao.Properties.OrgLineSeat.eq(bean.getLineseat()));
        List<Feed> feedList = queryBuilder.list();
        if (null != feedList && feedList.size() > 0) {
            for (Feed feed : feedList) {
                feed.setScanLineSeat(bean.getScanlineseat());
                feed.setScanMaterial(bean.getScanMaterial());
                feed.setResult(bean.getResult());
                feed.setRemark(bean.getRemark());
            }
            try {
                getFeedDao().updateInTx(feedList);
                updateRes = true;
            } catch (Exception e) {
                Log.d(TAG, "updateFeed - " + e.toString());
            }
        }
        return updateRes;
    }

    //更新某一项发料结果
    public boolean updateWare(Ware ware) {
        Log.d(TAG, " - updateWare - ");
        boolean updateRes = false;
        try {
            getWareDao().update(ware);
            updateRes = true;
        } catch (Exception e) {
            Log.d(TAG, "updateWare - " + e.toString());
        }
        return updateRes;
    }

    //更新某一项操作员全检结果
    public boolean updateFLCheck(FLCheckAll flCheckAll) {
        Log.d(TAG, " - updateFLCheck - ");
        boolean updateRes = false;
        try {
            getFlCheckAllDao().update(flCheckAll);
            updateRes = true;
        } catch (Exception e) {
            Log.d(TAG, "updateFLCheck - " + e.toString());
        }
        return updateRes;
    }

    //更新某一项IPQC全检结果
    public boolean updateQcCheck(QcCheckAll qcCheckAll) {
        Log.d(TAG, " - updateQcCheck - ");
        boolean updateRes = false;
        try {
            getQcCheckAllDao().update(qcCheckAll);
            updateRes = true;
        } catch (Exception e) {
            Log.d(TAG, "updateQcCheck - " + e.toString());
        }
        return updateRes;
    }

    //初始化所有上料结果
    public boolean updateAllFeed(List<Feed> feeds) {
        Log.d(TAG, " - updateAllFeed - ");
        boolean updateRes = false;
        try {
            GreenDaoManager.getInstance().getmDaoSession().runInTx(() -> {
                for (Feed feed : feeds) {
                    feed.setScanLineSeat("");
                    feed.setScanMaterial("");
                    feed.setResult("");
                    feed.setRemark("");
                }
                getFeedDao().updateInTx(feeds);
            });
            updateRes = true;
        } catch (Exception e) {
            Log.d(TAG, "updateAllFeed - " + e.toString());
        }
        return updateRes;
    }

    //初始化所有发料结果
    public boolean updateAllWare(List<Ware> wares) {
        Log.d(TAG, " - updateAllWare - ");
        boolean updateRes = false;
        try {
            GreenDaoManager.getInstance().getmDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (Ware ware : wares) {
                        ware.setScanLineSeat("");
                        ware.setScanMaterial("");
                        ware.setResult("");
                        ware.setRemark("");
                    }
                    getWareDao().updateInTx(wares);
                }
            });
            updateRes = true;
        } catch (Exception e) {
            Log.d(TAG, "updateAllWare - " + e.toString());
        }
        return updateRes;
    }

    //初始化所有操作员全检结果
    public boolean updateAllFLCheck(List<FLCheckAll> flCheckAlls) {
        Log.d(TAG, " - updateAllFLCheck - ");
        boolean updateRes = false;
        try {
            GreenDaoManager.getInstance().getmDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    /*
                    for (FLCheckAll flCheckAll : flCheckAlls) {
                        flCheckAll.setScanLineSeat("");
                        flCheckAll.setScanMaterial("");
                        flCheckAll.setResult("");
                        flCheckAll.setRemark("");
                    }
                    */
                    getFlCheckAllDao().updateInTx(flCheckAlls);
                }
            });
            updateRes = true;
        } catch (Exception e) {
            Log.d(TAG, "updateAllFLCheck - " + e.toString());
        }
        return updateRes;
    }

    //初始化所有IPQC全检结果
    public boolean updateAllQcCheck(List<QcCheckAll> qcCheckAlls) {
        Log.d(TAG, " - updateAllQcCheck - ");
        boolean updateRes = false;
        try {
            GreenDaoManager.getInstance().getmDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    /*
                    for (QcCheckAll qcCheckAll : qcCheckAlls) {
                        qcCheckAll.setScanLineSeat("");
                        qcCheckAll.setScanMaterial("");
                        qcCheckAll.setResult("");
                        qcCheckAll.setRemark("");
                    }
                    */
                    getQcCheckAllDao().updateInTx(qcCheckAlls);
                }
            });
            updateRes = true;
        } catch (Exception e) {
            Log.d(TAG, "updateAllQcCheck - " + e.toString());
        }
        return updateRes;
    }


    /**
     * 更新上料结果,重置的时候使用
     *
     * @param workOrder
     * @param line
     * @param boardType
     * @return
     */
    public boolean updateFeed(String workOrder, String line, int boardType) {
        boolean updateRes = false;
        QueryBuilder<Feed> queryBuilder = getFeedDao().queryBuilder();
        queryBuilder.where(FeedDao.Properties.Order.eq(workOrder),
                FeedDao.Properties.Line.eq(line),
                FeedDao.Properties.Board_type.eq(boardType));
        List<Feed> feedList = queryBuilder.list();
        if (null != feedList && feedList.size() > 0) {
            for (Feed feed : feedList) {
                feed.setScanLineSeat("");
                feed.setScanMaterial("");
                feed.setResult("");
                feed.setRemark("");
            }
            try {
                getFeedDao().updateInTx(feedList);
                updateRes = true;
            } catch (Exception e) {
                Log.d(TAG, "updateFeed - " + e.toString());
            }
        }
        return updateRes;
    }

    /**
     * 更新IPQC全检结果,重置的时候使用
     *
     * @param workOrder
     * @param line
     * @param boardType
     * @return
     */
    public boolean updateQcCheck(String workOrder, String line, int boardType) {
        Log.d(TAG, " - updateQcCheck - ");
        boolean updateRes = false;
        QueryBuilder<QcCheckAll> queryBuilder = getQcCheckAllDao().queryBuilder();
        queryBuilder.where(QcCheckAllDao.Properties.Order.eq(workOrder),
                QcCheckAllDao.Properties.Line.eq(line),
                QcCheckAllDao.Properties.Board_type.eq(boardType));
        List<QcCheckAll> qcList = queryBuilder.list();
        if (null != qcList && qcList.size() > 0) {
            for (QcCheckAll qc : qcList) {
                qc.setScanLineSeat("");
                qc.setScanMaterial("");
                qc.setResult("");
                qc.setRemark("");
            }
            try {
                getQcCheckAllDao().updateInTx(qcList);
                updateRes = true;
            } catch (Exception e) {
                Log.d(TAG, "updateQcCheck - " + e.toString());
            }
        }
        return updateRes;
    }

    /**
     * 更新厂线操作员全检结果,重置的时候使用
     *
     * @param workOrder
     * @param line
     * @param boardType
     * @return
     */
    public boolean updateFLCheck(String workOrder, String line, int boardType) {
        Log.d(TAG, " - updateFLCheck - ");
        boolean updateRes = false;
        QueryBuilder<FLCheckAll> queryBuilder = getFlCheckAllDao().queryBuilder();
        queryBuilder.where(FLCheckAllDao.Properties.Order.eq(workOrder),
                FLCheckAllDao.Properties.Line.eq(line),
                FLCheckAllDao.Properties.Board_type.eq(boardType));
        List<FLCheckAll> flList = queryBuilder.list();
        if (null != flList && flList.size() > 0) {
            for (FLCheckAll fl : flList) {
                fl.setScanLineSeat("");
                fl.setScanMaterial("");
                fl.setResult("");
                fl.setRemark("");
            }
            try {
                getFlCheckAllDao().updateInTx(flList);
                updateRes = true;
            } catch (Exception e) {
                Log.d(TAG, "updateFLCheck - " + e.toString());
            }
        }
        return updateRes;
    }


    //删除所有上料结果
    /*public boolean deleteAllFeed(List<Feed> feeds){
        boolean updateRes = false;
        try {
            getFeedDao().deleteInTx(feeds);
            updateRes = true;
        }catch (Exception e){
            Log.d(TAG,"deleteAllFeed - "+e.toString());
        }
        return updateRes;
    }*/

    //删除上料数据库所有数据
    public boolean deleteAllFeedData() {
        Log.d(TAG, " - deleteAllFeedData - ");
        boolean updateRes = false;
        try {
            getFeedDao().deleteAll();
            updateRes = true;
        } catch (Exception e) {
            Log.d(TAG, "deleteAllFeed - " + e.toString());
        }
        return updateRes;
    }

    //删除发料数据库所有数据
    public boolean deleteAllWareData() {
        Log.d(TAG, " - deleteAllWareData - ");
        boolean updateRes = false;
        try {
            getWareDao().deleteAll();
            updateRes = true;
        } catch (Exception e) {
            Log.d(TAG, "deleteAllWareData - " + e.toString());
        }
        return updateRes;
    }

    //删除操作员全检数据库所有数据
    public boolean deleteAllFLCheck() {
        Log.d(TAG, " - deleteAllFLCheck - ");
        boolean updateRes = false;
        try {
            getFlCheckAllDao().deleteAll();
            updateRes = true;
        } catch (Exception e) {
            Log.d(TAG, "deleteAllFLCheck - " + e.toString());
        }
        return updateRes;
    }

    //删除IPQC全检数据库所有数据
    public boolean deleteAllQcCheck() {
        Log.d(TAG, " - deleteAllQcCheck - ");
        boolean updateRes = false;
        try {
            getQcCheckAllDao().deleteAll();
            updateRes = true;
        } catch (Exception e) {
            Log.d(TAG, "deleteAllQcCheck - " + e.toString());
        }
        return updateRes;
    }

    //删除对应站位的上料纪录
    public boolean deleteFeedBySeat(List<String> seats) {
        Log.d(TAG, " - deleteFeedBySeat - ");
        boolean result = false;
        try {
            for (String s : seats) {
                List<Feed> feedList = getFeedDao().queryBuilder().where(FeedDao.Properties.OrgLineSeat.eq(s)).list();
                getFeedDao().deleteInTx(feedList);
            }
            result = true;
        } catch (Exception e) {
            Log.d(TAG, "deleteFeedBySeat - " + e.toString());
        }
        return result;
    }

    //删除对应站位的发料纪录
    public boolean deleteWareBySeat(List<String> seats) {
        Log.d(TAG, " - deleteWareBySeat - ");
        boolean result = false;
        try {
            for (String s : seats) {
                List<Ware> wareList = getWareDao().queryBuilder().where(WareDao.Properties.OrgLineSeat.eq(s)).list();
                getWareDao().deleteInTx(wareList);
            }
            result = true;
        } catch (Exception e) {
            Log.d(TAG, "deleteWareBySeat - " + e.toString());
        }
        return result;
    }

    //删除对应站位的操作员全检纪录
    public boolean deleteFLCheckBySeat(List<String> seats) {
        Log.d(TAG, " - deleteFLCheckBySeat - ");
        boolean result = false;
        try {
            for (String s : seats) {
                List<FLCheckAll> flCheckAlls = getFlCheckAllDao().queryBuilder().where(FLCheckAllDao.Properties.OrgLineSeat.eq(s)).list();
                getFlCheckAllDao().deleteInTx(flCheckAlls);
            }
            result = true;
        } catch (Exception e) {
            Log.d(TAG, "deleteFLCheckBySeat - " + e.toString());
        }
        return result;
    }

    //删除对应站位的IPQC全检纪录
    public boolean deleteQcCheckBySeat(List<String> seats) {
        Log.d(TAG, " - deleteQcCheckBySeat - ");
        boolean result = false;
        try {
            for (String s : seats) {
                List<QcCheckAll> qcCheckAlls = getQcCheckAllDao().queryBuilder().where(QcCheckAllDao.Properties.OrgLineSeat.eq(s)).list();
                getQcCheckAllDao().deleteInTx(qcCheckAlls);
            }
            result = true;
        } catch (Exception e) {
            Log.d(TAG, "deleteQcCheckBySeat - " + e.toString());
        }
        return result;
    }

    //更新或插入新上料数据
    public boolean updateOrInsertFeed(List<Feed> feeds) {
        Log.d(TAG, " - updateOrInsertFeed - ");
        boolean result = false;
        try {
            for (Feed feed : feeds) {
                getFeedDao().insertOrReplace(feed);
            }
            result = true;
        } catch (Exception e) {
            Log.d(TAG, "updateOrInsertFeed - " + e.toString());
        }
        return result;
    }

    //更新或插入新发料数据
    public boolean updateOrInsertWare(List<Ware> wares) {
        Log.d(TAG, " - updateOrInsertWare - ");
        boolean result = false;
        try {
            for (Ware ware : wares) {
                getWareDao().insertOrReplace(ware);
            }
            result = true;
        } catch (Exception e) {
            Log.d(TAG, "updateOrInsertWare - " + e.toString());
        }
        return result;
    }

    //更新或插入新操作员全检数据
    public boolean updateOrInsertFLCheck(List<FLCheckAll> flCheckAlls) {
        Log.d(TAG, " - updateOrInsertFLCheck - ");
        boolean result = false;
        try {
            for (FLCheckAll flCheckAll : flCheckAlls) {
                getFlCheckAllDao().insertOrReplace(flCheckAll);
            }
            result = true;
        } catch (Exception e) {
            Log.d(TAG, "updateOrInsertFLCheck - " + e.toString());
        }
        return result;
    }

    //更新或插入新IPQC全检数据
    public boolean updateOrInsertQcCheck(List<QcCheckAll> qcCheckAlls) {
        Log.d(TAG, " - updateOrInsertQcCheck - ");
        boolean result = false;
        try {
            for (QcCheckAll qcCheckAll : qcCheckAlls) {
                getQcCheckAllDao().insertOrReplace(qcCheckAll);
            }
            result = true;
        } catch (Exception e) {
            Log.d(TAG, "updateOrInsertQcCheck - " + e.toString());
        }
        return result;
    }


    private WareDao getWareDao() {
        return GreenDaoManager.getInstance().getmDaoSession().getWareDao();
    }

    private FeedDao getFeedDao() {
        return GreenDaoManager.getInstance().getmDaoSession().getFeedDao();
    }

    private FLCheckAllDao getFlCheckAllDao() {
        return GreenDaoManager.getInstance().getmDaoSession().getFLCheckAllDao();
    }

    private QcCheckAllDao getQcCheckAllDao() {
        return GreenDaoManager.getInstance().getmDaoSession().getQcCheckAllDao();
    }


}
