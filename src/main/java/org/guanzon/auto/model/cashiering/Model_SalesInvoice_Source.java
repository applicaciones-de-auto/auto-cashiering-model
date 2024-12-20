/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.model.cashiering;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.sql.rowset.CachedRowSet;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GEntity;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */

public class Model_SalesInvoice_Source implements GEntity{
final String XML = "Model_SalesInvoice_Source.xml";
    private final String psDefaultDate = "1900-01-01";
    private String psBranchCd;
    private String psExclude = "sFormNoxx»sDescript»cPayerCde"; //»
    
    GRider poGRider;                //application driver
    CachedRowSet poEntity;          //rowset
    JSONObject poJSON;              //json container
    int pnEditMode;                 //edit mode

    /**
     * Entity constructor
     *
     * @param foValue - GhostRider Application Driver
     */
    public Model_SalesInvoice_Source(GRider foValue) {
        if (foValue == null) {
            System.err.println("Application Driver is not set.");
            System.exit(1);
        }

        poGRider = foValue;

        initialize();
    }
    
    private void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);        
//            poEntity.updateObject("dTransact", poGRider.getServerDate()); 
//            poEntity.updateString("", TransactionStatus.STATE_OPEN); 
//            poEntity.updateObject("", SQLUtil.toDate(psDefaultDate, SQLUtil.FORMAT_SHORT_DATE));
//            poEntity.updateDouble("", 0.00);  
//            poEntity.updateInt("nEntryNox", 0);
            poEntity.updateBigDecimal("nTranAmtx", new BigDecimal("0.00"));
            poEntity.updateBigDecimal("nDiscount", new BigDecimal("0.00"));
            poEntity.updateBigDecimal("nAdvusedx", new BigDecimal("0.00"));
            poEntity.updateBigDecimal("nNetAmtxx", new BigDecimal("0.00")); 

            poEntity.insertRow();
            poEntity.moveToCurrentRow();
            poEntity.absolute(1);
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Gets the column index name.
     *
     * @param fnValue - column index number
     * @return column index name
     */
    @Override
    public String getColumn(int fnValue) {
        try {
            return poEntity.getMetaData().getColumnLabel(fnValue);
        } catch (SQLException e) {
        }
        return "";
    }

    /**
     * Gets the column index number.
     *
     * @param fsValue - column index name
     * @return column index number
     */
    @Override
    public int getColumn(String fsValue) {
        try {
            return MiscUtil.getColumnIndex(poEntity, fsValue);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Gets the total number of column.
     *
     * @return total number of column
     */
    @Override
    public int getColumnCount() {
        try {
            return poEntity.getMetaData().getColumnCount();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public int getEditMode() {
        return pnEditMode;
    }

    @Override
    public String getTable() {
        return "si_master_source";
    }
    
    /**
     * Gets the value of a column index number.
     *
     * @param fnColumn - column index number
     * @return object value
     */
    @Override
    public Object getValue(int fnColumn) {
        try {
            return poEntity.getObject(fnColumn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets the value of a column index name.
     *
     * @param fsColumn - column index name
     * @return object value
     */
    @Override
    public Object getValue(String fsColumn) {
        try {
            return poEntity.getObject(MiscUtil.getColumnIndex(poEntity, fsColumn));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets column value.
     *
     * @param fnColumn - column index number
     * @param foValue - value
     * @return result as success/failed
     */
    @Override
    public JSONObject setValue(int fnColumn, Object foValue) {
        try {
            poJSON = MiscUtil.validateColumnValue(System.getProperty("sys.default.path.metadata") + XML, MiscUtil.getColumnLabel(poEntity, fnColumn), foValue);
            if ("error".equals((String) poJSON.get("result"))) {
                System.out.println("ERROR : "+ MiscUtil.getColumnLabel(poEntity, fnColumn) + " : "+  poJSON.get("message"));
                return poJSON;
            }

            poEntity.updateObject(fnColumn, foValue);
            poEntity.updateRow();

            poJSON = new JSONObject();
            poJSON.put("result", "success");
            poJSON.put("value", getValue(fnColumn));
        } catch (SQLException e) {
            e.printStackTrace();
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }

        return poJSON;
    }
    
    /**
     * Sets column value.
     *
     * @param fsColumn - column index name
     * @param foValue - value
     * @return result as success/failed
     */
    @Override
    public JSONObject setValue(String fsColumn, Object foValue) {
        poJSON = new JSONObject();

        try {
            return setValue(MiscUtil.getColumnIndex(poEntity, fsColumn), foValue);
        } catch (SQLException e) {
            e.printStackTrace();
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }
        return poJSON;
    }

    /**
     * Set the edit mode of the entity to new.
     *
     * @return result as success/failed
     */
    @Override
    public JSONObject newRecord() {
        pnEditMode = EditMode.ADDNEW;
        
        //replace with the primary key column info
        setTransNo(MiscUtil.getNextCode(getTable(), "sTransNox", true, poGRider.getConnection(), poGRider.getBranchCode()));

        
        poJSON = new JSONObject();
        poJSON.put("result", "success");
        return poJSON;
    }
    
    @Override
    public JSONObject openRecord(String fsValue) {
        poJSON = new JSONObject();

        String lsSQL = getSQL(); //MiscUtil.makeSelect(this, psExclude); //exclude the columns called thru left join
        //replace the condition based on the primary key column of the record
        lsSQL = MiscUtil.addCondition(lsSQL, " a.sTransNox = " + SQLUtil.toSQL(fsValue)
                                                );

        System.out.println(lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);

        try {
            if (loRS.next()) {
                for (int lnCtr = 1; lnCtr <= loRS.getMetaData().getColumnCount(); lnCtr++) {
                    setValue(lnCtr, loRS.getObject(lnCtr));
                }

                pnEditMode = EditMode.UPDATE;

                poJSON.put("result", "success");
                poJSON.put("message", "Record loaded successfully.");
            } else {
                poJSON.put("result", "error");
                poJSON.put("message", "No record to load.");
            }
        } catch (SQLException e) {
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }

        return poJSON;
    }

    /**
     * Save the entity.
     *
     * @return result as success/failed
     */
    @Override
    public JSONObject saveRecord() {
        poJSON = new JSONObject();

        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
            String lsSQL; 
            if (pnEditMode == EditMode.ADDNEW) {
                //replace with the primary key column info
                setTransNo(MiscUtil.getNextCode(getTable(), "sTransNox", true, poGRider.getConnection(), poGRider.getBranchCode()));

//                setModifiedBy(poGRider.getUserID());
//                setModifiedDte(poGRider.getServerDate());
                
                //replace with the primary key column info
                lsSQL = MiscUtil.makeSQL(this, psExclude);
                
               // lsSQL = "Select * FROM " + getTable() + " a left join (" + makeSQL() + ") b on a.column1 = b.column "
                if (!lsSQL.isEmpty()) {
                    if (poGRider.executeQuery(lsSQL, getTable(), poGRider.getBranchCode(), getTargetBranchCd()) > 0) {
                        poJSON.put("result", "success");
                        poJSON.put("message", "Record saved successfully.");
                    } else {
                        poJSON.put("result", "error");
                        poJSON.put("message", "Error while saving SI detail.\n\n" + poGRider.getErrMsg());
                    }
                } else {
                    poJSON.put("result", "error");
                    poJSON.put("message", "Error while saving SI detail.\n\n" + "No record to save.");
                }
            } else {
                Model_SalesInvoice_Source loOldEntity = new Model_SalesInvoice_Source(poGRider);
                
                //replace with the primary key column info
                JSONObject loJSON = loOldEntity.openRecord(this.getTransNo());

                if ("success".equals((String) loJSON.get("result"))) {
//                    setModifiedBy(poGRider.getUserID());
//                    setModifiedDte(poGRider.getServerDate());
                    
                    //replace the condition based on the primary key column of the record
                    lsSQL = MiscUtil.makeSQL(this, loOldEntity, "sTransNox = " + SQLUtil.toSQL(this.getTransNo()), psExclude);

                    if (!lsSQL.isEmpty()) {
                        if (poGRider.executeQuery(lsSQL, getTable(), poGRider.getBranchCode(), getTargetBranchCd()) > 0) {
                            poJSON.put("result", "success");
                            poJSON.put("message", "Record saved successfully.");
                        } else {
                            poJSON.put("result", "error");
                            poJSON.put("message",  "Error while saving SI detail.\n\n" + poGRider.getErrMsg());
                        }
                    } else {
                        poJSON.put("result", "success");
                        poJSON.put("message", "No updates has been made.");
                    }
                } else {
                    poJSON.put("result", "error");
                    poJSON.put("message", "Record discrepancy. Unable to save record.");
                }
            }
        } else {
            poJSON.put("result", "error");
            poJSON.put("message", "Invalid update mode. Unable to save record.");
            return poJSON;
        }

        return poJSON;
    }
    
    private String getTargetBranchCd(){
//        if (!poGRider.getBranchCode().equals(getBranchCD())){
//            return getBranchCD();
//        } else {
            return "";
//        }
    }
    
    public JSONObject deleteRecord(String fsValue){
        poJSON = new JSONObject();
        
        String lsSQL = " DELETE FROM "+getTable()+" WHERE "
                    + " sTransNox = " + SQLUtil.toSQL(fsValue);
        if (!lsSQL.isEmpty()) {
            if (poGRider.executeQuery(lsSQL, getTable(), poGRider.getBranchCode(), "") > 0) {
                poJSON.put("result", "success");
                poJSON.put("message", "Record deleted successfully.");
            } else {
                poJSON.put("result", "error");
                poJSON.put("continue", true);
                poJSON.put("message", poGRider.getErrMsg());
            }
        }
        return poJSON;
    }

    /**
     * Prints all the public methods used<br>
     * and prints the column names of this entity.
     */
    @Override
    public void list() {
        Method[] methods = this.getClass().getMethods();

        System.out.println("--------------------------------------------------------------------");
        System.out.println("LIST OF PUBLIC METHODS FOR " + this.getClass().getName() + ":");
        System.out.println("--------------------------------------------------------------------");
        for (Method method : methods) {
            System.out.println(method.getName());
        }

        try {
            int lnRow = poEntity.getMetaData().getColumnCount();

            System.out.println("--------------------------------------------------------------------");
            System.out.println("ENTITY COLUMN INFO");
            System.out.println("--------------------------------------------------------------------");
            System.out.println("Total number of columns: " + lnRow);
            System.out.println("--------------------------------------------------------------------");

            for (int lnCtr = 1; lnCtr <= lnRow; lnCtr++) {
                System.out.println("Column index: " + (lnCtr) + " --> Label: " + poEntity.getMetaData().getColumnLabel(lnCtr));
                if (poEntity.getMetaData().getColumnType(lnCtr) == Types.CHAR
                        || poEntity.getMetaData().getColumnType(lnCtr) == Types.VARCHAR) {

                    System.out.println("Column index: " + (lnCtr) + " --> Size: " + poEntity.getMetaData().getColumnDisplaySize(lnCtr));
                }
            }
        } catch (SQLException e) {
        }

    }
    
    /**
     * Gets the SQL statement for this entity.
     *
     * @return SQL Statement
     */
    public String makeSQL() {
        return MiscUtil.makeSQL(this, psExclude);
    }
    
    /**
     * Gets the SQL Select statement for this entity.
     *
     * @return SQL Select Statement
     */
    public String makeSelectSQL() {
        return MiscUtil.makeSelect(this, psExclude);
    }
    
    public String getSQL(){
        return    " SELECT "                 
                + "    a.sTransNox "         
                + "  , a.sReferNox "         
                + "  , a.sSourceCD "         
                + "  , a.sSourceNo "         
                + "  , a.sTranType "         
                + "  , a.nTranAmtx "         
                + "  , a.nDiscount "         
                + "  , a.nAdvusedx "         
                + "  , a.nNetAmtxx "         
                + "  , a.nEntryNox "
                + "  , IFNULL( aa.sReferNox,IFNULL(c.sVSPNOxxx, IFNULL(d.sReferNox, IFNULL(e.sReferNox,'')))) AS sFormNoxx "
                + "  , b.sSourceCD AS sDescript "
                + "  , b.cPayerCde "
                + " FROM si_master_source a "                                              
                + " LEFT JOIN udr_master aa ON aa.sTransNox = a.sSourceNo "                
                + " LEFT JOIN cashier_receivables b ON b.sTransNox = a.sSourceNo "         
                + " LEFT JOIN vsp_master c ON c.sTransNox = b.sReferNox "                  
                + " LEFT JOIN customer_inquiry_reservation d ON d.sTransNox = b.sReferNox "
                + " LEFT JOIN insurance_policy_application e ON e.sTransNox = b.sReferNox ";             
    }
    
    private static String xsDateShort(Date fdValue) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(fdValue);
        return date;
    }

    private static String xsDateShort(String fsValue) throws org.json.simple.parser.ParseException, java.text.ParseException {
        SimpleDateFormat fromUser = new SimpleDateFormat("MMMM dd, yyyy");
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
        String lsResult = "";
        lsResult = myFormat.format(fromUser.parse(fsValue));
        return lsResult;
    }
    
    /*Convert Date to String*/
    private LocalDate strToDate(String val) {
        DateTimeFormatter date_formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(val, date_formatter);
        return localDate;
    }
    
    /**
     * Description: Sets the ID of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setTransNo(String fsValue) {
        return setValue("sTransNox", fsValue);
    }

    /**
     * @return The ID of this record.
     */
    public String getTransNo() {
        return (String) getValue("sTransNox");
    }
    
     /**
     * Description: Sets the Value of this record.
     *
     * @param fnValue
     * @return result as success/failed
     */
    public JSONObject setEntryNo(Integer fnValue) {
        return setValue("nEntryNox", fnValue);
    }

    /**
     * @return The Value of this record.
     */
    public Integer getEntryNo() {
        return Integer.parseInt(String.valueOf(getValue("nEntryNox")));
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setReferNo(String fsValue) {
        return setValue("sReferNox", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getReferNo() {
        return (String) getValue("sReferNox");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setSourceCD(String fsValue) {
        return setValue("sSourceCD", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getSourceCD() {
        return (String) getValue("sSourceCD");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setSourceNo(String fsValue) {
        return setValue("sSourceNo", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getSourceNo() {
        return (String) getValue("sSourceNo");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setTranType(String fsValue) {
        return setValue("sTranType", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getTranType() {
        return (String) getValue("sTranType");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdbValue
     * @return result as success/failed
     */
    public JSONObject setTranAmt(BigDecimal fdbValue) {
        return setValue("nTranAmtx", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public BigDecimal getTranAmt() {
        if(getValue("nTranAmtx") == null || getValue("nTranAmtx").equals("")){
            return new BigDecimal("0.00");
        } else {
            return new BigDecimal(String.valueOf(getValue("nTranAmtx")));
        }
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdbValue
     * @return result as success/failed
     */
    public JSONObject setDiscount(BigDecimal fdbValue) {
        return setValue("nDiscount", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public BigDecimal getDiscount() {
        if(getValue("nDiscount") == null || getValue("nDiscount").equals("")){
            return new BigDecimal("0.00");
        } else {
            return new BigDecimal(String.valueOf(getValue("nDiscount")));
        }
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdbValue
     * @return result as success/failed
     */
    public JSONObject setAdvused(BigDecimal fdbValue) {
        return setValue("nAdvusedx", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public BigDecimal getAdvused() {
        if(getValue("nAdvusedx") == null || getValue("nAdvusedx").equals("")){
            return new BigDecimal("0.00");
        } else {
            return new BigDecimal(String.valueOf(getValue("nAdvusedx")));
        }
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdbValue
     * @return result as success/failed
     */
    public JSONObject setNetAmt(BigDecimal fdbValue) {
        return setValue("nNetAmtxx", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public BigDecimal getNetAmt() {
        if(getValue("nNetAmtxx") == null || getValue("nNetAmtxx").equals("")){
            return new BigDecimal("0.00");
        } else {
            return new BigDecimal(String.valueOf(getValue("nNetAmtxx")));
        }
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setFormNo(String fsValue) {
        return setValue("sFormNoxx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getFormNo() {
        return (String) getValue("sFormNoxx");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setDescript(String fsValue) {
        return setValue("sDescript", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getDescript() {
        return (String) getValue("sDescript");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setPayerCde(String fsValue) {
        return setValue("cPayerCde", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getPayerCde() {
        return (String) getValue("cPayerCde");
    }
    
//    
//    /**
//     * Description: Sets the Value of this record.
//     *
//     * @param fsValue
//     * @return result as success/failed
//     */
//    public JSONObject setModifiedBy(String fsValue) {
//        return setValue("sModified", fsValue);
//    }
//
//    /**
//     * @return The Value of this record.
//     */
//    public String getModifiedBy() {
//        return (String) getValue("sModified");
//    }
//    
//    /**
//     * Sets the date and time the record was modified.
//     *
//     * @param fdValue
//     * @return result as success/failed
//     */
//    public JSONObject setModifiedDte(Date fdValue) {
//        return setValue("dModified", fdValue);
//    }
//
//    /**
//     * @return The date and time the record was modified.
//     */
//    public Date getModifiedDte() {
//        return (Date) getValue("dModified");
//    }
    
}
