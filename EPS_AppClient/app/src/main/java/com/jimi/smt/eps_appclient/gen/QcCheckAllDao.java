package com.jimi.smt.eps_appclient.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.jimi.smt.eps_appclient.Dao.QcCheckAll;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "QC_CHECK_ALL".
*/
public class QcCheckAllDao extends AbstractDao<QcCheckAll, Long> {

    public static final String TABLENAME = "QC_CHECK_ALL";

    /**
     * Properties of entity QcCheckAll.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Qccheck_id = new Property(0, Long.class, "qccheck_id", true, "_id");
        public final static Property ProgramId = new Property(1, String.class, "programId", false, "PROGRAM_ID");
        public final static Property Order = new Property(2, String.class, "order", false, "ORDER");
        public final static Property Operator = new Property(3, String.class, "operator", false, "OPERATOR");
        public final static Property Board_type = new Property(4, int.class, "board_type", false, "BOARD_TYPE");
        public final static Property Line = new Property(5, String.class, "line", false, "LINE");
        public final static Property SerialNo = new Property(6, int.class, "SerialNo", false, "SERIAL_NO");
        public final static Property Alternative = new Property(7, boolean.class, "Alternative", false, "ALTERNATIVE");
        public final static Property OrgLineSeat = new Property(8, String.class, "OrgLineSeat", false, "ORG_LINE_SEAT");
        public final static Property OrgMaterial = new Property(9, String.class, "OrgMaterial", false, "ORG_MATERIAL");
        public final static Property ScanLineSeat = new Property(10, String.class, "ScanLineSeat", false, "SCAN_LINE_SEAT");
        public final static Property ScanMaterial = new Property(11, String.class, "ScanMaterial", false, "SCAN_MATERIAL");
        public final static Property Result = new Property(12, String.class, "Result", false, "RESULT");
        public final static Property Remark = new Property(13, String.class, "Remark", false, "REMARK");
    }


    public QcCheckAllDao(DaoConfig config) {
        super(config);
    }
    
    public QcCheckAllDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"QC_CHECK_ALL\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: qccheck_id
                "\"PROGRAM_ID\" TEXT," + // 1: programId
                "\"ORDER\" TEXT," + // 2: order
                "\"OPERATOR\" TEXT," + // 3: operator
                "\"BOARD_TYPE\" INTEGER NOT NULL ," + // 4: board_type
                "\"LINE\" TEXT," + // 5: line
                "\"SERIAL_NO\" INTEGER NOT NULL ," + // 6: SerialNo
                "\"ALTERNATIVE\" INTEGER NOT NULL ," + // 7: Alternative
                "\"ORG_LINE_SEAT\" TEXT," + // 8: OrgLineSeat
                "\"ORG_MATERIAL\" TEXT," + // 9: OrgMaterial
                "\"SCAN_LINE_SEAT\" TEXT," + // 10: ScanLineSeat
                "\"SCAN_MATERIAL\" TEXT," + // 11: ScanMaterial
                "\"RESULT\" TEXT," + // 12: Result
                "\"REMARK\" TEXT);"); // 13: Remark
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"QC_CHECK_ALL\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, QcCheckAll entity) {
        stmt.clearBindings();
 
        Long qccheck_id = entity.getQccheck_id();
        if (qccheck_id != null) {
            stmt.bindLong(1, qccheck_id);
        }
 
        String programId = entity.getProgramId();
        if (programId != null) {
            stmt.bindString(2, programId);
        }
 
        String order = entity.getOrder();
        if (order != null) {
            stmt.bindString(3, order);
        }
 
        String operator = entity.getOperator();
        if (operator != null) {
            stmt.bindString(4, operator);
        }
        stmt.bindLong(5, entity.getBoard_type());
 
        String line = entity.getLine();
        if (line != null) {
            stmt.bindString(6, line);
        }
        stmt.bindLong(7, entity.getSerialNo());
        stmt.bindLong(8, entity.getAlternative() ? 1L: 0L);
 
        String OrgLineSeat = entity.getOrgLineSeat();
        if (OrgLineSeat != null) {
            stmt.bindString(9, OrgLineSeat);
        }
 
        String OrgMaterial = entity.getOrgMaterial();
        if (OrgMaterial != null) {
            stmt.bindString(10, OrgMaterial);
        }
 
        String ScanLineSeat = entity.getScanLineSeat();
        if (ScanLineSeat != null) {
            stmt.bindString(11, ScanLineSeat);
        }
 
        String ScanMaterial = entity.getScanMaterial();
        if (ScanMaterial != null) {
            stmt.bindString(12, ScanMaterial);
        }
 
        String Result = entity.getResult();
        if (Result != null) {
            stmt.bindString(13, Result);
        }
 
        String Remark = entity.getRemark();
        if (Remark != null) {
            stmt.bindString(14, Remark);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, QcCheckAll entity) {
        stmt.clearBindings();
 
        Long qccheck_id = entity.getQccheck_id();
        if (qccheck_id != null) {
            stmt.bindLong(1, qccheck_id);
        }
 
        String programId = entity.getProgramId();
        if (programId != null) {
            stmt.bindString(2, programId);
        }
 
        String order = entity.getOrder();
        if (order != null) {
            stmt.bindString(3, order);
        }
 
        String operator = entity.getOperator();
        if (operator != null) {
            stmt.bindString(4, operator);
        }
        stmt.bindLong(5, entity.getBoard_type());
 
        String line = entity.getLine();
        if (line != null) {
            stmt.bindString(6, line);
        }
        stmt.bindLong(7, entity.getSerialNo());
        stmt.bindLong(8, entity.getAlternative() ? 1L: 0L);
 
        String OrgLineSeat = entity.getOrgLineSeat();
        if (OrgLineSeat != null) {
            stmt.bindString(9, OrgLineSeat);
        }
 
        String OrgMaterial = entity.getOrgMaterial();
        if (OrgMaterial != null) {
            stmt.bindString(10, OrgMaterial);
        }
 
        String ScanLineSeat = entity.getScanLineSeat();
        if (ScanLineSeat != null) {
            stmt.bindString(11, ScanLineSeat);
        }
 
        String ScanMaterial = entity.getScanMaterial();
        if (ScanMaterial != null) {
            stmt.bindString(12, ScanMaterial);
        }
 
        String Result = entity.getResult();
        if (Result != null) {
            stmt.bindString(13, Result);
        }
 
        String Remark = entity.getRemark();
        if (Remark != null) {
            stmt.bindString(14, Remark);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public QcCheckAll readEntity(Cursor cursor, int offset) {
        QcCheckAll entity = new QcCheckAll( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // qccheck_id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // programId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // order
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // operator
            cursor.getInt(offset + 4), // board_type
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // line
            cursor.getInt(offset + 6), // SerialNo
            cursor.getShort(offset + 7) != 0, // Alternative
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // OrgLineSeat
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // OrgMaterial
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // ScanLineSeat
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // ScanMaterial
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // Result
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13) // Remark
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, QcCheckAll entity, int offset) {
        entity.setQccheck_id(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setProgramId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setOrder(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setOperator(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setBoard_type(cursor.getInt(offset + 4));
        entity.setLine(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setSerialNo(cursor.getInt(offset + 6));
        entity.setAlternative(cursor.getShort(offset + 7) != 0);
        entity.setOrgLineSeat(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setOrgMaterial(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setScanLineSeat(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setScanMaterial(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setResult(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setRemark(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(QcCheckAll entity, long rowId) {
        entity.setQccheck_id(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(QcCheckAll entity) {
        if(entity != null) {
            return entity.getQccheck_id();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(QcCheckAll entity) {
        return entity.getQccheck_id() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
