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
import javax.sql.rowset.CachedRowSet;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.TransactionStatus;
import org.guanzon.appdriver.iface.GEntity;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Model_SalesInvoice_Master implements GEntity {
    final String XML = "Model_SalesInvoice_Master.xml";
    private final String psDefaultDate = "1900-01-01";
    private String psBranchCd;
    private String psExclude = "sTranStat»sBuyCltNm»cClientTp»sTaxIDNox»sAddressx";//»  
    
    GRider poGRider;                //application driver
    CachedRowSet poEntity;          //rowset
    JSONObject poJSON;              //json container
    int pnEditMode;                 //edit mode

    /**
     * Entity constructor
     *
     * @param foValue - GhostRider Application Driver
     */
    public Model_SalesInvoice_Master(GRider foValue) {
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
            poEntity.updateObject("dTransact", poGRider.getServerDate()); 
            poEntity.updateString("cTranStat", TransactionStatus.STATE_OPEN); //TransactionStatus.STATE_OPEN why is the value of STATE_OPEN is 0 while record status active is 1
             
            poEntity.updateBigDecimal("nTranTotl", new BigDecimal("0.00"));
            poEntity.updateBigDecimal("nDiscount", new BigDecimal("0.00"));
            poEntity.updateBigDecimal("nVatSales", new BigDecimal("0.00"));
            poEntity.updateBigDecimal("nVatAmtxx", new BigDecimal("0.00"));
            poEntity.updateBigDecimal("nNonVATSl", new BigDecimal("0.00"));
            poEntity.updateBigDecimal("nZroVATSl", new BigDecimal("0.00"));
            poEntity.updateBigDecimal("nCWTAmtxx", new BigDecimal("0.00"));
//            poEntity.updateBigDecimal("cWTRatexx", new BigDecimal("0.00"));
            poEntity.updateDouble("nWTRatexx", 0.00);
            poEntity.updateBigDecimal("nNetTotal", new BigDecimal("0.00"));
            poEntity.updateBigDecimal("nCashAmtx", new BigDecimal("0.00"));
            poEntity.updateBigDecimal("nChckAmtx", new BigDecimal("0.00"));
            poEntity.updateBigDecimal("nCardAmtx", new BigDecimal("0.00"));
            poEntity.updateBigDecimal("nOthrAmtx", new BigDecimal("0.00"));
            poEntity.updateBigDecimal("nGiftAmtx", new BigDecimal("0.00"));
            poEntity.updateBigDecimal("nAmtPaidx", new BigDecimal("0.00"));
            poEntity.updateBigDecimal("nAdvPaymx", new BigDecimal("0.00"));

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
        return "si_master";
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

        //replace with the primary key column info
        setTransNo(MiscUtil.getNextCode(getTable(), "sTransNox", true, poGRider.getConnection(), poGRider.getBranchCode()+"S"));
//        setReferNo(MiscUtil.getNextCode(getTable(), "sReferNox", true, poGRider.getConnection(), poGRider.getBranchCode()));
        setPrinted("0");
        
        poJSON = new JSONObject();
        poJSON.put("result", "success");
        return poJSON;
    }

    /**
     * Opens a record.
     *
     * @param fsValue - filter values
     * @return result as success/failed
     */
    @Override
    public JSONObject openRecord(String fsValue) {
        poJSON = new JSONObject();

        String lsSQL = getSQL(); //makeSelectSQL(); // MiscUtil.makeSelect(this, psExclude) getSQL();//exclude the columns called thru left join

        //replace the condition based on the primary key column of the record
        lsSQL = MiscUtil.addCondition(lsSQL, " a.sTransNox = " + SQLUtil.toSQL(fsValue)
                                                );
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
            String lsSQL; //nRsvAmtTl
            if (pnEditMode == EditMode.ADDNEW) {
                //replace with the primary key column info
                setTransNo(MiscUtil.getNextCode(getTable(), "sTransNox", true, poGRider.getConnection(), poGRider.getBranchCode()+"S"));
//                setReferNo(MiscUtil.getNextCode(getTable(), "sReferNox", true, poGRider.getConnection(), poGRider.getBranchCode()));
                setModifiedBy(poGRider.getUserID());
                setModifiedDte(poGRider.getServerDate());
                
                lsSQL = MiscUtil.makeSQL(this, psExclude);
                
               // lsSQL = "Select * FROM " + getTable() + " a left join (" + makeSQL() + ") b on a.column1 = b.column "
                if (!lsSQL.isEmpty()) {
                    if (poGRider.executeQuery(lsSQL, getTable(), poGRider.getBranchCode(), getTargetBranchCd()) > 0) {
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
                Model_SalesInvoice_Master loOldEntity = new Model_SalesInvoice_Master(poGRider);
                //replace with the primary key column info
                JSONObject loJSON = loOldEntity.openRecord(this.getTransNo());
                if ("success".equals((String) loJSON.get("result"))) {
                    setModifiedBy(poGRider.getUserID());
                    setModifiedDte(poGRider.getServerDate());
                    
                    //replace the condition based on the primary key column of the record
                    lsSQL = MiscUtil.makeSQL(this, loOldEntity, "sTransNox = " + SQLUtil.toSQL(this.getTransNo()), psExclude);

                    if (!lsSQL.isEmpty()) {
                        if (poGRider.executeQuery(lsSQL, getTable(), poGRider.getBranchCode(), getTargetBranchCd()) > 0) {
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

    private String getTargetBranchCd(){
        if(getBranchCd() != null) {
            if (!poGRider.getBranchCode().equals(getBranchCd())){
                return getBranchCd();
            } else {
                return "";
            }
        } else {
            return "";
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
    
    /**
     * Gets the SQL statement for this entity.
     * 
     * @return SQL Statement
     */
    public String makeSQL() {
        return MiscUtil.makeSQL(this, psExclude); //exclude columns called thru left join
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
                + "  , a.sBranchCd "                                                                                               
                + "  , a.dTransact "                                                                                               
                + "  , a.cDocTypex "                                                                                               
                + "  , a.sReferNox "                                                                                               
                + "  , a.sClientID "                                                                                               
                + "  , a.nTranTotl "                                                                                               
                + "  , a.nDiscount "                                                                                               
                + "  , a.nVatSales "                                                                                               
                + "  , a.nVatAmtxx "                                                                                               
                + "  , a.nNonVATSl "                                                                                               
                + "  , a.nZroVATSl "                                                                                               
                + "  , a.nWTRatexx "                                                                                               
                + "  , a.nCWTAmtxx "                                                                                               
                + "  , a.nAdvPaymx "                                                                                               
                + "  , a.nNetTotal "                                                                                               
                + "  , a.nCashAmtx "                                                                                               
                + "  , a.nChckAmtx "                                                                                               
                + "  , a.nCardAmtx "                                                                                               
                + "  , a.nOthrAmtx "                                                                                               
                + "  , a.nGiftAmtx "                                                                                               
                + "  , a.nAmtPaidx "                                                                                               
                + "  , a.cPrintedx "                                                                                               
                + "  , a.cTranStat "                                                                                               
                + "  , a.sModified "                                                                                               
                + "  , a.dModified "                                                                                               
                + "  , CASE        "                                                                                               
                + "     WHEN a.cTranStat = "+ SQLUtil.toSQL(TransactionStatus.STATE_CANCELLED)+" THEN 'CANCELLED'"                 
                + "     WHEN a.cTranStat = "+ SQLUtil.toSQL(TransactionStatus.STATE_CLOSED)+" THEN 'APPROVED'    "                 
                + "     WHEN a.cTranStat = "+ SQLUtil.toSQL(TransactionStatus.STATE_OPEN)+" THEN 'ACTIVE'        "                 
                + "     WHEN a.cTranStat = "+ SQLUtil.toSQL(TransactionStatus.STATE_POSTED)+" THEN 'POSTED'      "                 
                + "     ELSE 'ACTIVE'   "                                                                                          
                + "    END AS sTranStat "                                                                                          
                /*BUYING COSTUMER*/                                                                                                
//                + " , b.sCompnyNm AS sBuyCltNm "                                                                                   
                + " , b.cClientTp              "                                                                                  
                + " , b.sTaxIDNox              "                                                                                    
//                + " , TRIM(IFNULL(CONCAT( IFNULL(CONCAT(d.sHouseNox,' ') , ''), "                                                  
//                + "    IFNULL(CONCAT(d.sAddressx,' ') , ''),                    "                                                  
//                + "    IFNULL(CONCAT(e.sBrgyName,' '), ''),                     "                                                  
//                + "    IFNULL(CONCAT(f.sTownName, ', '),''),                    "                                                  
//                + "    IFNULL(CONCAT(g.sProvName),'') )	, '')) AS sAddressx     "    
                + " , CASE "                                                                                                                                 
                + " WHEN (h.sBrBankID != NULL || TRIM(h.sBrBankID) != '' )THEN CONCAT(k.sBankName, ' ', h.sBrBankNm) "                                       
                + " WHEN (l.sBrInsIDx != NULL || TRIM(l.sBrInsIDx) != '' )THEN CONCAT(o.sInsurNme, ' ', l.sBrInsNme) "                                       
                + " ELSE b.sCompnyNm   END AS sBuyCltNm "                                                                                                    
                + " , CASE "                                                                                                                                 
                + " WHEN (h.sBrBankID != NULL || TRIM(h.sBrBankID) != '' )THEN CONCAT(IFNULL(h.sAddressx, ''), i.sTownName, j.sProvName) "                   
                + " WHEN (l.sBrInsIDx != NULL || TRIM(l.sBrInsIDx) != '' )THEN CONCAT(IFNULL(l.sAddressx, ''), m.sTownName, n.sProvName) "                   
                + " ELSE TRIM(IFNULL(CONCAT( IFNULL(CONCAT(d.sHouseNox,' ') , ''), IFNULL(CONCAT(d.sAddressx,' ') , ''), "                                   
                + " IFNULL(CONCAT(e.sBrgyName,' '), ''),   IFNULL(CONCAT(f.sTownName, ', '),''), IFNULL(CONCAT(g.sProvName),'') )	, ''))   END AS sAddressx "
                
                + " FROM si_master a  "                                                                                            
                 /*CUSTOMER*/                                                                                                      
                + " LEFT JOIN client_master b ON b.sClientID = a.sClientID  "                                                      
                + " LEFT JOIN client_address c ON c.sClientID = a.sClientID AND c.cPrimaryx = 1 "                                  
                + " LEFT JOIN addresses d ON d.sAddrssID = c.sAddrssID "                                                           
                + " LEFT JOIN barangay e ON e.sBrgyIDxx = d.sBrgyIDxx  "                                                           
                + " LEFT JOIN towncity f ON f.sTownIDxx = d.sTownIDxx  "                                                           
                + " LEFT JOIN province g ON g.sProvIDxx = f.sProvIDxx  "                                                           
                + " LEFT JOIN client_mobile ba ON ba.sClientID = b.sClientID AND ba.cPrimaryx = 1 "                                
                + " LEFT JOIN client_email_address bb ON bb.sClientID = b.sClientID AND bb.cPrimaryx = 1 "
                /*BANK*/                                                                   
                + " LEFT JOIN banks_branches h ON h.sBrBankID = a.sClientID "              
                + " LEFT JOIN towncity i ON i.sTownIDxx = h.sTownIDxx "                    
                + " LEFT JOIN province j ON j.sProvIDxx = i.sProvIDxx "                    
                + " LEFT JOIN banks k ON k.sBankIDxx = h.sBankIDxx "                       
                /*INSURANCE*/                                                              
                + " LEFT JOIN insurance_company_branches l ON l.sBrInsIDx = a.sClientID "  
                + " LEFT JOIN towncity m ON m.sTownIDxx = l.sTownIDxx "                    
                + " LEFT JOIN province n ON n.sProvIDxx = m.sProvIDxx "                    
                + " LEFT JOIN insurance_company o ON o.sInsurIDx = l.sInsurIDx "           ;                           
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
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBranchCd(String fsValue) {
        return setValue("sBranchCd", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getBranchCd() {
        return (String) getValue("sBranchCd");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdValue
     * @return result as success/failed
     */
    public JSONObject setTransactDte(Date fdValue) {
        return setValue("dTransact", fdValue);
    }

    /**
     * @return The Value of this record.
     */
    public Date getTransactDte() {
        Date date = null;
        if(!getValue("dTransact").toString().isEmpty()){
            date = CommonUtils.toDate(getValue("dTransact").toString());
        }
        
        return date;
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setDocType(String fsValue) {
        return setValue("cDocTypex", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getDocType() {
        return (String) getValue("cDocTypex");
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
    public JSONObject setClientID(String fsValue) {
        return setValue("sClientID", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getClientID() {
        return (String) getValue("sClientID");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdbValue
     * @return result as success/failed
     */
    public JSONObject setTranTotl(BigDecimal fdbValue) {
        return setValue("nTranTotl", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public BigDecimal getTranTotl() {
        if(getValue("nTranTotl") == null || getValue("nTranTotl").equals("")){
            return new BigDecimal("0.00");
        } else {
            return new BigDecimal(String.valueOf(getValue("nTranTotl")));
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
    public JSONObject setVatSales(BigDecimal fdbValue) {
        return setValue("nVatSales", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public BigDecimal getVatSales() {
        if(getValue("nVatSales") == null || getValue("nVatSales").equals("")){
            return new BigDecimal("0.00");
        } else {
            return new BigDecimal(String.valueOf(getValue("nVatSales")));
        }
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdbValue
     * @return result as success/failed
     */
    public JSONObject setVatAmt(BigDecimal fdbValue) {
        return setValue("nVatAmtxx", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public BigDecimal getVatAmt() {
        if(getValue("nVatAmtxx") == null || getValue("nVatAmtxx").equals("")){
            return new BigDecimal("0.00");
        } else {
            return new BigDecimal(String.valueOf(getValue("nVatAmtxx")));
        }
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdbValue
     * @return result as success/failed
     */
    public JSONObject setNonVATSl(BigDecimal fdbValue) {
        return setValue("nNonVATSl", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public BigDecimal getNonVATSl() {
        if(getValue("nNonVATSl") == null || getValue("nNonVATSl").equals("")){
            return new BigDecimal("0.00");
        } else {
            return new BigDecimal(String.valueOf(getValue("nNonVATSl")));
        }
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdbValue
     * @return result as success/failed
     */
    public JSONObject setZroVATSl(BigDecimal fdbValue) {
        return setValue("nZroVATSl", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public BigDecimal getZroVATSl() {
        if(getValue("nZroVATSl") == null || getValue("nZroVATSl").equals("")){
            return new BigDecimal("0.00");
        } else {
            return new BigDecimal(String.valueOf(getValue("nZroVATSl")));
        }
    }
    
    
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdbValue
     * @return result as success/failed
     */
    public JSONObject setWTRate(Double fdbValue) {
        return setValue("nWTRatexx", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public Double getWTRate() {
        return Double.parseDouble(String.valueOf(getValue("nWTRatexx")));
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdbValue
     * @return result as success/failed
     */
    public JSONObject setCWTAmt(BigDecimal fdbValue) {
        return setValue("nCWTAmtxx", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public BigDecimal getCWTAmt() {
        if(getValue("nCWTAmtxx") == null || getValue("nCWTAmtxx").equals("")){
            return new BigDecimal("0.00");
        } else {
            return new BigDecimal(String.valueOf(getValue("nCWTAmtxx")));
        }
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdbValue
     * @return result as success/failed
     */
    public JSONObject setAdvPaym(BigDecimal fdbValue) {
        return setValue("nAdvPaymx", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public BigDecimal getAdvPaym() {
        if(getValue("nAdvPaymx") == null || getValue("nAdvPaymx").equals("")){
            return new BigDecimal("0.00");
        } else {
            return new BigDecimal(String.valueOf(getValue("nAdvPaymx")));
        }
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdbValue
     * @return result as success/failed
     */
    public JSONObject setNetTotal(BigDecimal fdbValue) {
        return setValue("nNetTotal", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public BigDecimal getNetTotal() {
        if(getValue("nNetTotal") == null || getValue("nNetTotal").equals("")){
            return new BigDecimal("0.00");
        } else {
            return new BigDecimal(String.valueOf(getValue("nNetTotal")));
        }
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdbValue
     * @return result as success/failed
     */
    public JSONObject setCashAmt(BigDecimal fdbValue) {
        return setValue("nCashAmtx", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public BigDecimal getCashAmt() {
        if(getValue("nCashAmtx") == null || getValue("nCashAmtx").equals("")){
            return new BigDecimal("0.00");
        } else {
            return new BigDecimal(String.valueOf(getValue("nCashAmtx")));
        }
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdbValue
     * @return result as success/failed
     */
    public JSONObject setChckAmt(BigDecimal fdbValue) {
        return setValue("nChckAmtx", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public BigDecimal getChckAmt() {
        if(getValue("nChckAmtx") == null || getValue("nChckAmtx").equals("")){
            return new BigDecimal("0.00");
        } else {
            return new BigDecimal(String.valueOf(getValue("nChckAmtx")));
        }
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdbValue
     * @return result as success/failed
     */
    public JSONObject setCardAmt(BigDecimal fdbValue) {
        return setValue("nCardAmtx", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public BigDecimal getCardAmt() {
        if(getValue("nCardAmtx") == null || getValue("nCardAmtx").equals("")){
            return new BigDecimal("0.00");
        } else {
            return new BigDecimal(String.valueOf(getValue("nCardAmtx")));
        }
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdbValue
     * @return result as success/failed
     */
    public JSONObject setOthrAmt(BigDecimal fdbValue) {
        return setValue("nOthrAmtx", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public BigDecimal getOthrAmt() {
        if(getValue("nOthrAmtx") == null || getValue("nOthrAmtx").equals("")){
            return new BigDecimal("0.00");
        } else {
            return new BigDecimal(String.valueOf(getValue("nOthrAmtx")));
        }
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdbValue
     * @return result as success/failed
     */
    public JSONObject setGiftAmt(BigDecimal fdbValue) {
        return setValue("nGiftAmtx", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public BigDecimal getGiftAmt() {
        if(getValue("nGiftAmtx") == null || getValue("nGiftAmtx").equals("")){
            return new BigDecimal("0.00");
        } else {
            return new BigDecimal(String.valueOf(getValue("nGiftAmtx")));
        }
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdbValue
     * @return result as success/failed
     */
    public JSONObject setAmtPaid(BigDecimal fdbValue) {
        return setValue("nAmtPaidx", fdbValue);
    }

    /**
     * @return The Value of this record.
     */
    public BigDecimal getAmtPaid() {
        if(getValue("nAmtPaidx") == null || getValue("nAmtPaidx").equals("")){
            return new BigDecimal("0.00");
        } else {
            return new BigDecimal(String.valueOf(getValue("nAmtPaidx")));
        }
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setPrinted(String fsValue) {
        return setValue("cPrintedx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getPrinted() {
        return (String) getValue("cPrintedx");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setTranStat(String fsValue) {
        return setValue("cTranStat", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getTranStat() {
        return (String) getValue("cTranStat");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setModifiedBy(String fsValue) {
        return setValue("sModified", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getModifiedBy() {
        return (String) getValue("sModified");
    }
    
    /**
     * Sets the date and time the record was modified.
     *
     * @param fdValue
     * @return result as success/failed
     */
    public JSONObject setModifiedDte(Date fdValue) {
        return setValue("dModified", fdValue);
    }

    /**
     * @return The date and time the record was modified.
     */
    public Date getModifiedDte() {
        return (Date) getValue("dModified");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBuyCltNm(String fsValue) {
        return setValue("sBuyCltNm", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getBuyCltNm() {
        return (String) getValue("sBuyCltNm");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setClientTp(String fsValue) {
        return setValue("cClientTp", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getClientTp() {
        return (String) getValue("cClientTp");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setTaxIDNo(String fsValue) {
        return setValue("sTaxIDNox", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getTaxIDNo() {
        return (String) getValue("sTaxIDNox");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setAddress(String fsValue) {
        return setValue("sAddressx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getAddress() {
        return (String) getValue("sAddressx");
    }
    
}

/*


*/