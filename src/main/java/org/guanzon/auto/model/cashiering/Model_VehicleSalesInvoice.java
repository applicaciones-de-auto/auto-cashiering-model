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
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.RecordStatus;
import org.guanzon.appdriver.iface.GEntity;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Model_VehicleSalesInvoice implements GEntity {
    final String XML = "Model_VehicleSalesInvoice.xml";

    GRider poGRider;                //application driver
    CachedRowSet poEntity;          //rowset
    JSONObject poJSON;              //json container
    int pnEditMode;                 //edit mode
    String psExclude = "sUDRNoxxx»cCustType»sCSNoxxxx»sPlateNox»sFrameNox»sEngineNo»sKeyNoxxx»sVhclFDsc»sVhclDesc»sColorDsc»sCoCltNmx»sSENamexx"
                     + "»nUnitPrce»nPromoDsc»nFleetDsc»nSPFltDsc»nBndleDsc»nAddlDscx»sBankname»cPayModex";
    private String psTargetBranchCd = "";

    /**
     * Entity constructor
     *
     * @param foValue - GhostRider Application Driver
     */
    public Model_VehicleSalesInvoice(GRider foValue) {
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
            
            poEntity.updateBigDecimal("nTranAmtx", new BigDecimal("0.00"));
            poEntity.updateBigDecimal("nDiscount", new BigDecimal("0.00"));
            poEntity.updateBigDecimal("nAdvusedx", new BigDecimal("0.00"));
            poEntity.updateBigDecimal("nNetAmtxx", new BigDecimal("0.00"));
            
            
            poEntity.updateBigDecimal("nUnitPrce", new BigDecimal("0.00"));
            poEntity.updateBigDecimal("nPromoDsc", new BigDecimal("0.00"));
            poEntity.updateBigDecimal("nFleetDsc", new BigDecimal("0.00"));
            poEntity.updateBigDecimal("nSPFltDsc", new BigDecimal("0.00"));
            poEntity.updateBigDecimal("nBndleDsc", new BigDecimal("0.00"));
            poEntity.updateBigDecimal("nAddlDscx", new BigDecimal("0.00"));

            poEntity.insertRow();
            poEntity.moveToCurrentRow();

            poEntity.absolute(1);

            pnEditMode = EditMode.UNKNOWN;
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
        
        setTransNo(MiscUtil.getNextCode(getTable(), "sTransNox", true, poGRider.getConnection(), poGRider.getBranchCode()));

        poJSON = new JSONObject();
        poJSON.put("result", "success");
        return poJSON;
    }
    
    @Override
    public JSONObject openRecord(String fsValue) {
        poJSON = new JSONObject();

        String lsSQL = getSQL();

        //replace the condition based on the primary key column of the record
        lsSQL = MiscUtil.addCondition(lsSQL, " a.sTransNox = " + SQLUtil.toSQL(fsValue));
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
//    /**
//     * Opens a record.
//     *
//     * @param fsValue - filter values
//     * @param fnValue - filter values
//     * @return result as success/failed
//     */
//    public JSONObject openRecord(String fsValue, int fnValue) {
//        poJSON = new JSONObject();
//
//        String lsSQL = getSQL();
//
//        //replace the condition based on the primary key column of the record
//        lsSQL = MiscUtil.addCondition(lsSQL, " a.sTransNox = " + SQLUtil.toSQL(fsValue) 
//                                                + " AND a.nEntryNox = " + SQLUtil.toSQL(fnValue));
//        System.out.println(lsSQL);
//        ResultSet loRS = poGRider.executeQuery(lsSQL);
//
//        try {
//            if (loRS.next()) {
//                for (int lnCtr = 1; lnCtr <= loRS.getMetaData().getColumnCount(); lnCtr++) {
//                    setValue(lnCtr, loRS.getObject(lnCtr));
//                }
//
//                pnEditMode = EditMode.UPDATE;
//
//                poJSON.put("result", "success");
//                poJSON.put("message", "Record loaded successfully.");
//            } else {
//                poJSON.put("result", "error");
//                poJSON.put("message", "No record to load.");
//            }
//        } catch (SQLException e) {
//            poJSON.put("result", "error");
//            poJSON.put("message", e.getMessage());
//        }
//
//        return poJSON;
//    }
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

                lsSQL = makeSQL();

                if (!lsSQL.isEmpty()) {
                    if (poGRider.executeQuery(lsSQL, getTable(), poGRider.getBranchCode(), psTargetBranchCd) > 0) {
                        poJSON.put("result", "success");
                        poJSON.put("message", "Record saved successfully.");
                    } else {
                        poJSON.put("result", "error");
                        poJSON.put("message", poGRider.getErrMsg());
                    }
                } else {
                    poJSON.put("result", "error");
                    poJSON.put("message", "No record to save.");
                }
            } else {
                Model_VehicleSalesInvoice loOldEntity = new Model_VehicleSalesInvoice (poGRider);

                //replace with the primary key column info
//                JSONObject loJSON = loOldEntity.openRecord(this.getTransNo(),this.getEntryNo());
                JSONObject loJSON = loOldEntity.openRecord(this.getTransNo());

                if ("success".equals((String) loJSON.get("result"))) {
                    //replace the condition based on the primary key column of the record
//                    lsSQL = MiscUtil.makeSQL(this, loOldEntity, " sTransNox = " + SQLUtil.toSQL(this.getTransNo())
//                                                                                + " AND nEntryNox = " + SQLUtil.toSQL(this.getEntryNo()), psExclude);
                    lsSQL = MiscUtil.makeSQL(this, loOldEntity, " sTransNox = " + SQLUtil.toSQL(this.getTransNo()), psExclude);

                    if (!lsSQL.isEmpty()) {
                        if (poGRider.executeQuery(lsSQL, getTable(), poGRider.getBranchCode(), psTargetBranchCd) > 0) {
                            poJSON.put("result", "success");
                            poJSON.put("message", "Record saved successfully.");
                        } else {
                            
                            poJSON.put("result", "error");
                            poJSON.put("message", poGRider.getErrMsg());
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
    
    public void setTargetBranchCd(String fsBranchCd){
        if (!poGRider.getBranchCode().equals(fsBranchCd)){
            psTargetBranchCd = fsBranchCd;
        } else {
            psTargetBranchCd = "";
        }
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
    
    //Get vat rate thru standardsets
    public Double getVatRate(){
        JSONObject loJSON = new JSONObject();
        
        String ldblValue = "0.00";
        try {
            //Vat Rate
            String lsStandardSets = "SELECT sValuexxx FROM xxxstandard_sets WHERE sDescript = 'vat_percent'";
            System.out.println("CHECK STANDARD SETS FROM vat_percent : " + lsStandardSets);
            ResultSet loRS = poGRider.executeQuery(lsStandardSets);
            if (MiscUtil.RecordCount(loRS) > 0){
                    while(loRS.next()){
                        ldblValue = loRS.getString("sValuexxx");
                    }

                    MiscUtil.close(loRS);
                    return Double.valueOf(ldblValue);
            }else {
//                loJSON.put("result", "error");
//                loJSON.put("message", "Notify System Administrator to config Standard set for `vat_percent`.");
                return 0.00;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Model_VehicleSalesInvoice.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Double.valueOf(ldblValue);
        
    }
    
    
    public Double getBasePriceWVatPercent(){
        JSONObject loJSON = new JSONObject();
        
        String ldblValue = "0.00";
        try {
            //Vat Rate
            String lsStandardSets = "SELECT sValuexxx FROM xxxstandard_sets WHERE sDescript = 'baseprice_with_vat_percent'";
            System.out.println("CHECK STANDARD SETS FROM vat_percent : " + lsStandardSets);
            ResultSet loRS = poGRider.executeQuery(lsStandardSets);
            if (MiscUtil.RecordCount(loRS) > 0){
                while(loRS.next()){
                    ldblValue = loRS.getString("sValuexxx");
                }

                MiscUtil.close(loRS);
            }else {
//                loJSON.put("result", "error");
//                loJSON.put("message", "Notify System Administrator to config Standard set for `baseprice_with_vat_percent`.");
//                return loJSON;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Model_VehicleSalesInvoice.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Double.valueOf(ldblValue);
        
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
    
    private String getSQL(){
        return   " SELECT "               
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
                  /*VDR INFORMATION*/
                + "  , b.sReferNox AS sUDRNoxxx " 
                + "  , b.cCustType AS cCustType "     
                  /*VEHICLE INFORMATION*/                                                         
                + " , c.sCSNoxxxx "                                                               
                + " , d.sPlateNox "                                                               
                + " , c.sFrameNox "                                                               
                + " , c.sEngineNo "                                                               
                + " , c.sKeyNoxxx "                                                               
                + " , e.sDescript AS sVhclFDsc " 
                + " , TRIM(CONCAT_WS(' ',f.sMakeDesc, g.sModelDsc, h.sTypeDesc, e.sTransMsn, e.nYearModl )) AS sVhclDesc "
                + " , i.sColorDsc "
                + " , k.sCompnyNm AS sCoCltNmx "                                               
                + " , m.sCompnyNm AS sSENamexx "                                               
                + " , j.nUnitPrce "                                                                                
                + " , j.nPromoDsc "                                                                       
                + " , j.nFleetDsc "                                                                       
                + " , j.nSPFltDsc "                                                                       
                + " , j.nBndleDsc "                                                                       
                + " , j.nAddlDscx "                                                                       
                + " , j.cPayModex "                                                                    
                + " , n.sBankname " 
                + " FROM si_master_source a "   
                  /*VDR INFORMATION*/
                + " LEFT JOIN udr_master b ON b.sTransNox = a.sSourceNo"      //sReferNox
                 /*VEHICLE INFORMATION*/                                                          
                + " LEFT JOIN vehicle_serial c ON c.sSerialID = b.sSerialID "                     
                + " LEFT JOIN vehicle_serial_registration d ON d.sSerialID = b.sSerialID "        
                + " LEFT JOIN vehicle_master e ON e.sVhclIDxx = c.sVhclIDxx " 
                + " LEFT JOIN vehicle_make f ON f.sMakeIDxx = e.sMakeIDxx  "
                + " LEFT JOIN vehicle_model g ON g.sModelIDx = e.sModelIDx "
                + " LEFT JOIN vehicle_type h ON h.sTypeIDxx = e.sTypeIDxx  "
                + " LEFT JOIN vehicle_color i ON i.sColorIDx = e.sColorIDx " 
                 /*CO CLIENT*/                                                  
                + " LEFT JOIN vsp_master j ON j.sTransNox = b.sSourceNo "                                         
                + " LEFT JOIN client_master k ON k.sClientID = j.sCoCltIDx "  
                + " LEFT JOIN customer_inquiry l ON l.sTransNox = j.sInqryIDx " 
                + " LEFT JOIN ggc_isysdbf.client_master m ON m.sClientID = l.sEmployID    "   
                + " LEFT JOIN vsp_finance n ON n.sTransNox = j.sTransNox " ;   
//                + " LEFT JOIN banks_branches o ON o.sBrBankID = n.sBankIDxx "  
//                + " LEFT JOIN banks p ON p.sBankIDxx = o.sBankIDxx ";  
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
     * Description: Sets the ID of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setReferNo(String fsValue) {
        return setValue("sReferNox", fsValue);
    }

    /**
     * @return The ID of this record.
     */
    public String getReferNo() {
        return (String) getValue("sReferNox");
    }
    
    /**
     * Description: Sets the ID of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setSourceCD(String fsValue) {
        return setValue("sSourceCD", fsValue);
    }

    /**
     * @return The ID of this record.
     */
    public String getSourceCD() {
        return (String) getValue("sSourceCD");
    }
    
    /**
     * Description: Sets the ID of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setSourceNo(String fsValue) {
        return setValue("sSourceNo", fsValue);
    }

    /**
     * @return The ID of this record.
     */
    public String getSourceNo() {
        return (String) getValue("sSourceNo");
    }
    
    /**
     * Description: Sets the ID of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setTranType(String fsValue) {
        return setValue("sTranType", fsValue);
    }

    /**
     * @return The ID of this record.
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
    public JSONObject setUDRNo(String fsValue) {
        return setValue("sUDRNoxxx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getUDRNo() {
        return (String) getValue("sUDRNoxxx");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setCustType(String fsValue) {
        return setValue("cCustType", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getCustType() {
        return (String) getValue("cCustType");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setCSNo(String fsValue) {
        return setValue("sCSNoxxxx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getCSNo() {
        return (String) getValue("sCSNoxxxx");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setPlateNo(String fsValue) {
        return setValue("sPlateNox", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getPlateNo() {
        return (String) getValue("sPlateNox");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setFrameNo(String fsValue) {
        return setValue("sFrameNox", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getFrameNo() {
        return (String) getValue("sFrameNox");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setEngineNo(String fsValue) {
        return setValue("sEngineNo", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getEngineNo() {
        return (String) getValue("sEngineNo");
    }
                                                        
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setVhclFDsc(String fsValue) {
        return setValue("sVhclFDsc", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getVhclFDsc() {
        return (String) getValue("sVhclFDsc");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setColorDsc(String fsValue) {
        return setValue("sColorDsc", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getColorDsc() {
        return (String) getValue("sColorDsc");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setVhclDesc(String fsValue) {
        return setValue("sVhclDesc", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getVhclDesc() {
        return (String) getValue("sVhclDesc");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setCoCltNm(String fsValue) {
        return setValue("sCoCltNmx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getCoCltNm() {
        return (String) getValue("sCoCltNmx");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setSEName(String fsValue) {
        return setValue("sSENamexx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getSEName() {
        return (String) getValue("sSENamexx");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdbValue
     * @return result as success/failed
     */
    public JSONObject setUnitPrce(BigDecimal fdbValue) {
        return setValue("nUnitPrce", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public BigDecimal getUnitPrce() {
        if(getValue("nUnitPrce") == null || getValue("nUnitPrce").equals("")){
            return new BigDecimal("0.00");
        } else {
            return new BigDecimal(String.valueOf(getValue("nUnitPrce")));
        }
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdbValue
     * @return result as success/failed
     */
    public JSONObject setPromoDsc(BigDecimal fdbValue) {
        return setValue("nPromoDsc", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public BigDecimal getPromoDsc() {
        if(getValue("nPromoDsc") == null || getValue("nPromoDsc").equals("")){
            return new BigDecimal("0.00");
        } else {
            return new BigDecimal(String.valueOf(getValue("nPromoDsc")));
        }
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdbValue
     * @return result as success/failed
     */
    public JSONObject setFleetDsc(BigDecimal fdbValue) {
        return setValue("nFleetDsc", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public BigDecimal getFleetDsc() {
        if(getValue("nFleetDsc") == null || getValue("nFleetDsc").equals("")){
            return new BigDecimal("0.00");
        } else {
            return new BigDecimal(String.valueOf(getValue("nFleetDsc")));
        }
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdbValue
     * @return result as success/failed
     */
    public JSONObject setSPFltDsc(BigDecimal fdbValue) {
        return setValue("nSPFltDsc", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public BigDecimal getSPFltDsc() {
        if(getValue("nSPFltDsc") == null || getValue("nSPFltDsc").equals("")){
            return new BigDecimal("0.00");
        } else {
            return new BigDecimal(String.valueOf(getValue("nSPFltDsc")));
        }
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdbValue
     * @return result as success/failed
     */
    public JSONObject setBndleDsc(BigDecimal fdbValue) {
        return setValue("nBndleDsc", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public BigDecimal getBndleDsc() {
        if(getValue("nBndleDsc") == null || getValue("nBndleDsc").equals("")){
            return new BigDecimal("0.00");
        } else {
            return new BigDecimal(String.valueOf(getValue("nBndleDsc")));
        }
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdbValue
     * @return result as success/failed
     */
    public JSONObject setAddlDsc(BigDecimal fdbValue) {
        return setValue("nAddlDscx", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public BigDecimal getAddlDsc() {
        if(getValue("nAddlDscx") == null || getValue("nAddlDscx").equals("")){
            return new BigDecimal("0.00");
        } else {
            return new BigDecimal(String.valueOf(getValue("nAddlDscx")));
        }
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setPayMode(String fsValue) {
        return setValue("cPayModex", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getPayMode() {
        return (String) getValue("cPayModex");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBankname(String fsValue) {
        return setValue("sBankname", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getBankname() {
        return (String) getValue("sBankname");
    }
}
