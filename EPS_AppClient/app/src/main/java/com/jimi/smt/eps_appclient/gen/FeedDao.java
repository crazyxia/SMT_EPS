package com.jimi.smt.eps_appclient.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.jimi.smt.eps_appclient.Dao.Feed;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/**
 * DAO for table "FEED".
 */
public class FeedDao extends AbstractDao<Feed, Long> {

    public static final String TABLENAME = "FEED";

    /**
     * Properties of entity Feed.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Feed_id = new Property(0, Long.class, "feed_id", true, "_id");
        public final static Property Order = new Property(1, String.class, "order", false, "ORDER");
        public final static Property Operator = new Property(2, String.class, "operator", false, "OPERATOR");
        public final static Property Board_type = new Property(3, int.class, "board_type", false, "BOARD_TYPE");
        public final static Property Line = new Property(4, String.class, "line", false, "LINE");
        public final static Property OrgLineSeat = new Property(5, String.class, "OrgLineSeat", false, "ORG_LINE_SEAT");
        public final static Property OrgMaterial = new Property(6, String.class, "OrgMaterial", false, "ORG_MATERIAL");
        public final static Property ScanLineSeat = new Property(7, String.class, "ScanLineSeat", false, "SCAN_LINE_SEAT");
        public final static Property ScanMaterial = new Property(8, String.class, "ScanMaterial", false, "SCAN_MATERIAL");
        public final static Property Result = new Property(9, String.class, "Result", false, "RESULT");
        public final static Property Remark = new Property(10, String.class, "Remark", false, "REMARK");
        public final static Property SerialNo = new Property(11, int.class, "SerialNo", false, "SERIAL_NO");
        public final static Property Alternative = new Property(12, Byte.class, "Alternative", false, "ALTERNATIVE");
    }


    public FeedDao(DaoConfig config) {
        super(config);
    }

    public FeedDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"FEED\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: feed_id
                "\"ORDER\" TEXT," + // 1: order
                "\"OPERATOR\" TEXT," + // 2: operator
                "\"BOARD_TYPE\" INTEGER NOT NULL ," + // 3: board_type
                "\"LINE\" TEXT," + // 4: line
                "\"ORG_LINE_SEAT\" TEXT," + // 5: OrgLineSeat
                "\"ORG_MATERIAL\" TEXT," + // 6: OrgMaterial
                "\"SCAN_LINE_SEAT\" TEXT," + // 7: ScanLineSeat
                "\"SCAN_MATERIAL\" TEXT," + // 8: ScanMaterial
                "\"RESULT\" TEXT," + // 9: Result
                "\"REMARK\" TEXT," + // 10: Remark
                "\"SERIAL_NO\" INTEGER NOT NULL ," + // 11: SerialNo
                "\"ALTERNATIVE\" INTEGER);"); // 12: Alternative
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"FEED\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Feed entity) {
        stmt.clearBindings();

        Long feed_id = entity.getFeed_id();
        if (feed_id != null) {
            stmt.bindLong(1, feed_id);
        }

        String order = entity.getOrder();
        if (order != null) {
            stmt.bindString(2, order);
        }

        String operator = entity.getOperator();
        if (operator != null) {
            stmt.bindString(3, operator);
        }
        stmt.bindLong(4, entity.getBoard_type());

        String line = entity.getLine();
        if (line != null) {
            stmt.bindString(5, line);
        }

        String OrgLineSeat = entity.getOrgLineSeat();
        if (OrgLineSeat != null) {
            stmt.bindString(6, OrgLineSeat);
        }

        String OrgMaterial = entity.getOrgMaterial();
        if (OrgMaterial != null) {
            stmt.bindString(7, OrgMaterial);
        }

        String ScanLineSeat = entity.getScanLineSeat();
        if (ScanLineSeat != null) {
            stmt.bindString(8, ScanLineSeat);
        }

        String ScanMaterial = entity.getScanMaterial();
        if (ScanMaterial != null) {
            stmt.bindString(9, ScanMaterial);
        }

        String Result = entity.getResult();
        if (Result != null) {
            stmt.bindString(10, Result);
        }

        String Remark = entity.getRemark();
        if (Remark != null) {
            stmt.bindString(11, Remark);
        }
        stmt.bindLong(12, entity.getSerialNo());

        Byte Alternative = entity.getAlternative();
        if (Alternative != null) {
            stmt.bindLong(13, Alternative);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Feed entity) {
        stmt.clearBindings();

        Long feed_id = entity.getFeed_id();
        if (feed_id != null) {
            stmt.bindLong(1, feed_id);
        }

        String order = entity.getOrder();
        if (order != null) {
            stmt.bindString(2, order);
        }

        String operator = entity.getOperator();
        if (operator != null) {
            stmt.bindString(3, operator);
        }
        stmt.bindLong(4, entity.getBoard_type());

        String line = entity.getLine();
        if (line != null) {
            stmt.bindString(5, line);
        }

        String OrgLineSeat = entity.getOrgLineSeat();
        if (OrgLineSeat != null) {
            stmt.bindString(6, OrgLineSeat);
        }

        String OrgMaterial = entity.getOrgMaterial();
        if (OrgMaterial != null) {
            stmt.bindString(7, OrgMaterial);
        }

        String ScanLineSeat = entity.getScanLineSeat();
        if (ScanLineSeat != null) {
            stmt.bindString(8, ScanLineSeat);
        }

        String ScanMaterial = entity.getScanMaterial();
        if (ScanMaterial != null) {
            stmt.bindString(9, ScanMaterial);
        }

        String Result = entity.getResult();
        if (Result != null) {
            stmt.bindString(10, Result);
        }

        String Remark = entity.getRemark();
        if (Remark != null) {
            stmt.bindString(11, Remark);
        }
        stmt.bindLong(12, entity.getSerialNo());

        Byte Alternative = entity.getAlternative();
        if (Alternative != null) {
            stmt.bindLong(13, Alternative);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    @Override
    public Feed readEntity(Cursor cursor, int offset) {
        Feed entity = new Feed( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // feed_id
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // order
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // operator
                cursor.getInt(offset + 3), // board_type
                cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // line
                cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // OrgLineSeat
                cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // OrgMaterial
                cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // ScanLineSeat
                cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // ScanMaterial
                cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // Result
                cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // Remark
                cursor.getInt(offset + 11), // SerialNo
                cursor.isNull(offset + 12) ? null : (byte) cursor.getShort(offset + 12) // Alternative
        );
        return entity;
    }

    @Override
    public void readEntity(Cursor cursor, Feed entity, int offset) {
        entity.setFeed_id(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setOrder(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setOperator(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setBoard_type(cursor.getInt(offset + 3));
        entity.setLine(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setOrgLineSeat(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setOrgMaterial(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setScanLineSeat(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setScanMaterial(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setResult(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setRemark(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setSerialNo(cursor.getInt(offset + 11));
        entity.setAlternative(cursor.isNull(offset + 12) ? null : (byte) cursor.getShort(offset + 12));
    }

    @Override
    protected final Long updateKeyAfterInsert(Feed entity, long rowId) {
        entity.setFeed_id(rowId);
        return rowId;
    }

    @Override
    public Long getKey(Feed entity) {
        if(entity != null) {
            return entity.getFeed_id();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Feed entity) {
        return entity.getFeed_id() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }

}
