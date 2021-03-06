package com.jimi.smt.eps_appclient.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.jimi.smt.eps_appclient.Dao.Ware;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "WARE".
*/
public class WareDao extends AbstractDao<Ware, Long> {

    public static final String TABLENAME = "WARE";

    /**
     * Properties of entity Ware.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Ware_id = new Property(0, Long.class, "ware_id", true, "_id");
        public final static Property ProgramId = new Property(1, String.class, "programId", false, "PROGRAM_ID");
        public final static Property Order = new Property(2, String.class, "order", false, "ORDER");
        public final static Property Operator = new Property(3, String.class, "operator", false, "OPERATOR");
        public final static Property Board_type = new Property(4, int.class, "board_type", false, "BOARD_TYPE");
        public final static Property Line = new Property(5, String.class, "line", false, "LINE");
        public final static Property SerialNo = new Property(6, int.class, "SerialNo", false, "SERIAL_NO");
        public final static Property Alternative = new Property(7, boolean.class, "Alternative", false, "ALTERNATIVE");
        public final static Property Specitification = new Property(8, String.class, "specitification", false, "SPECITIFICATION");
        public final static Property Position = new Property(9, String.class, "position", false, "POSITION");
        public final static Property Quantity = new Property(10, int.class, "quantity", false, "QUANTITY");
        public final static Property OrgLineSeat = new Property(11, String.class, "OrgLineSeat", false, "ORG_LINE_SEAT");
        public final static Property OrgMaterial = new Property(12, String.class, "OrgMaterial", false, "ORG_MATERIAL");
        public final static Property ScanLineSeat = new Property(13, String.class, "ScanLineSeat", false, "SCAN_LINE_SEAT");
        public final static Property ScanMaterial = new Property(14, String.class, "ScanMaterial", false, "SCAN_MATERIAL");
        public final static Property Result = new Property(15, String.class, "Result", false, "RESULT");
        public final static Property Remark = new Property(16, String.class, "Remark", false, "REMARK");
    }


    public WareDao(DaoConfig config) {
        super(config);
    }
    
    public WareDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"WARE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: ware_id
                "\"PROGRAM_ID\" TEXT," + // 1: programId
                "\"ORDER\" TEXT," + // 2: order
                "\"OPERATOR\" TEXT," + // 3: operator
                "\"BOARD_TYPE\" INTEGER NOT NULL ," + // 4: board_type
                "\"LINE\" TEXT," + // 5: line
                "\"SERIAL_NO\" INTEGER NOT NULL ," + // 6: SerialNo
                "\"ALTERNATIVE\" INTEGER NOT NULL ," + // 7: Alternative
                "\"SPECITIFICATION\" TEXT," + // 8: specitification
                "\"POSITION\" TEXT," + // 9: position
                "\"QUANTITY\" INTEGER NOT NULL ," + // 10: quantity
                "\"ORG_LINE_SEAT\" TEXT," + // 11: OrgLineSeat
                "\"ORG_MATERIAL\" TEXT," + // 12: OrgMaterial
                "\"SCAN_LINE_SEAT\" TEXT," + // 13: ScanLineSeat
                "\"SCAN_MATERIAL\" TEXT," + // 14: ScanMaterial
                "\"RESULT\" TEXT," + // 15: Result
                "\"REMARK\" TEXT);"); // 16: Remark
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"WARE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Ware entity) {
        stmt.clearBindings();
 
        Long ware_id = entity.getWare_id();
        if (ware_id != null) {
            stmt.bindLong(1, ware_id);
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
 
        String specitification = entity.getSpecitification();
        if (specitification != null) {
            stmt.bindString(9, specitification);
        }
 
        String position = entity.getPosition();
        if (position != null) {
            stmt.bindString(10, position);
        }
        stmt.bindLong(11, entity.getQuantity());
 
        String OrgLineSeat = entity.getOrgLineSeat();
        if (OrgLineSeat != null) {
            stmt.bindString(12, OrgLineSeat);
        }
 
        String OrgMaterial = entity.getOrgMaterial();
        if (OrgMaterial != null) {
            stmt.bindString(13, OrgMaterial);
        }
 
        String ScanLineSeat = entity.getScanLineSeat();
        if (ScanLineSeat != null) {
            stmt.bindString(14, ScanLineSeat);
        }
 
        String ScanMaterial = entity.getScanMaterial();
        if (ScanMaterial != null) {
            stmt.bindString(15, ScanMaterial);
        }
 
        String Result = entity.getResult();
        if (Result != null) {
            stmt.bindString(16, Result);
        }
 
        String Remark = entity.getRemark();
        if (Remark != null) {
            stmt.bindString(17, Remark);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Ware entity) {
        stmt.clearBindings();
 
        Long ware_id = entity.getWare_id();
        if (ware_id != null) {
            stmt.bindLong(1, ware_id);
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
 
        String specitification = entity.getSpecitification();
        if (specitification != null) {
            stmt.bindString(9, specitification);
        }
 
        String position = entity.getPosition();
        if (position != null) {
            stmt.bindString(10, position);
        }
        stmt.bindLong(11, entity.getQuantity());
 
        String OrgLineSeat = entity.getOrgLineSeat();
        if (OrgLineSeat != null) {
            stmt.bindString(12, OrgLineSeat);
        }
 
        String OrgMaterial = entity.getOrgMaterial();
        if (OrgMaterial != null) {
            stmt.bindString(13, OrgMaterial);
        }
 
        String ScanLineSeat = entity.getScanLineSeat();
        if (ScanLineSeat != null) {
            stmt.bindString(14, ScanLineSeat);
        }
 
        String ScanMaterial = entity.getScanMaterial();
        if (ScanMaterial != null) {
            stmt.bindString(15, ScanMaterial);
        }
 
        String Result = entity.getResult();
        if (Result != null) {
            stmt.bindString(16, Result);
        }
 
        String Remark = entity.getRemark();
        if (Remark != null) {
            stmt.bindString(17, Remark);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Ware readEntity(Cursor cursor, int offset) {
        Ware entity = new Ware( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // ware_id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // programId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // order
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // operator
            cursor.getInt(offset + 4), // board_type
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // line
            cursor.getInt(offset + 6), // SerialNo
            cursor.getShort(offset + 7) != 0, // Alternative
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // specitification
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // position
            cursor.getInt(offset + 10), // quantity
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // OrgLineSeat
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // OrgMaterial
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // ScanLineSeat
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // ScanMaterial
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // Result
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16) // Remark
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Ware entity, int offset) {
        entity.setWare_id(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setProgramId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setOrder(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setOperator(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setBoard_type(cursor.getInt(offset + 4));
        entity.setLine(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setSerialNo(cursor.getInt(offset + 6));
        entity.setAlternative(cursor.getShort(offset + 7) != 0);
        entity.setSpecitification(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setPosition(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setQuantity(cursor.getInt(offset + 10));
        entity.setOrgLineSeat(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setOrgMaterial(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setScanLineSeat(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setScanMaterial(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setResult(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setRemark(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Ware entity, long rowId) {
        entity.setWare_id(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Ware entity) {
        if(entity != null) {
            return entity.getWare_id();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Ware entity) {
        return entity.getWare_id() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
