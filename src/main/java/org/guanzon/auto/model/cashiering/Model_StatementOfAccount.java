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
import org.guanzon.appdriver.base.CommonUtils;
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
public class Model_StatementOfAccount implements GEntity{
final String XML = "Model_StatementOfAccount.xml";
    private final String psDefaultDate = "1900-01-01";
    private String psBranchCd;
    private String psExclude = "sPayerNme»sOwnrNmxx»cClientTp»sAddressx»sBankName»sBankAddr»sInsNamex»sInsAddrx"; //»
    
    GRider poGRider;                //application driver
    CachedRowSet poEntity;          //rowset
    JSONObject poJSON;              //json container
    int pnEditMode;                 //edit mode

    /**
     * Entity constructor
     *
     * @param foValue - GhostRider Application Driver
     */
    public Model_StatementOfAccount(GRider foValue) {
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
            poEntity.updateObject("dApproved", SQLUtil.toDate(psDefaultDate, SQLUtil.FORMAT_SHORT_DATE));      
            poEntity.updateObject("dPostedxx", SQLUtil.toDate(psDefaultDate, SQLUtil.FORMAT_SHORT_DATE));                    
            poEntity.updateBigDecimal("nTranTotl", new BigDecimal("0.00"));                     
            poEntity.updateBigDecimal("nAmtPaidx", new BigDecimal("0.00"));     

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
        return "soa_master";
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
        setTransNo(MiscUtil.getNextCode(getTable(), "sTransNox", true, poGRider.getConnection(), poGRider.getBranchCode()+"SOA"));
        setTransactDte(poGRider.getServerDate());
        
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

        String lsSQL = getSQL(); //MiscUtil.makeSelect(this, psExclude); //exclude the columns called thru left join
        //replace the condition based on the primary key column of the record
        lsSQL = MiscUtil.addCondition(lsSQL, " a.sTransNox = " + SQLUtil.toSQL(fsValue)
                                                //+ " GROUP BY a.sTransNox "
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
                setTransNo(MiscUtil.getNextCode(getTable(), "sTransNox", true, poGRider.getConnection(), poGRider.getBranchCode()+"SOA"));
//                setModifiedBy(poGRider.getUserID());
//                setModifiedDte(poGRider.getServerDate());
                
                lsSQL = MiscUtil.makeSQL(this, psExclude);
                
               // lsSQL = "Select * FROM " + getTable() + " a left join (" + makeSQL() + ") b on a.column1 = b.column "
                if (!lsSQL.isEmpty()) {
                    if (poGRider.executeQuery(lsSQL, getTable(), poGRider.getBranchCode(), getTargetBranchCd()) > 0) {
                        poJSON.put("result", "success");
                        poJSON.put("message", "Record saved successfully.");
                    } else {
                        poJSON.put("result", "error");
                        poJSON.put("message", "Error while saving cashier receivables.\n\n" + poGRider.getErrMsg());
                    }
                } else {
                    poJSON.put("result", "error");
                    poJSON.put("message", "Error while saving cashier receivables.\n\n" + "No record to save.");
                }
            } else {
                Model_StatementOfAccount loOldEntity = new Model_StatementOfAccount(poGRider);
                
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
                            poJSON.put("message",  "Error while saving cashier receivables.\n\n" + poGRider.getErrMsg());
                        }
                    } else {
                        poJSON.put("result", "success");
                        poJSON.put("message", "No updates has been made.");
                    }
                } else {
                    poJSON.put("result", "error");
                    poJSON.put("message", "Error while saving cashier receivables.\n\n" + "Record discrepancy. Unable to save record.");
                }
            }
        } else {
            poJSON.put("result", "error");
            poJSON.put("message", "Error while saving cashier receivables.\n\n" + "Invalid update mode. Unable to save record.");
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
                + "   a.sTransNox "                                                                                                                
                + " , a.dTransact "                                                                                                                
                + " , a.sClientID "                                                                                                                
                + " , a.sBankIDxx "                                                                                                                
                + " , a.sInsurIDx "                                                                                                                
                + " , a.sTermIDxx "                                                                                                                
                + " , a.nTranTotl "                                                                                                                
                + " , a.nAmtPaidx "                                                                                                                
                + " , a.sRemarksx "                                                                                                                
                + " , a.cTranStat "                                                                                                                
                + " , a.dApproved "                                                                                                                
                + " , a.dPostedxx "                                                                                                                
                + " , a.nEntryNox "                                                                                                                
                + " , a.sModified "                                                                                                                
                + " , a.dModified "            
                + " , CASE "
//                + "     WHEN a.sClientID != null THEN '' " //ASSOCIATE
                + "     WHEN a.sBankIDxx != null THEN CONCAT(k.sBankName, ' ', h.sBrBankNm) " //BANK
                + "     WHEN a.sClientID != null THEN b.sCompnyNm " //CUSTOMER
                + "     WHEN a.sInsurIDx != null THEN CONCAT(o.sInsurNme, ' ', l.sBrInsNme) " //INSURANCE
//                + "     WHEN a.cPayerCde != null THEN '' " //SUPPLIER                                  
                + " 	ELSE ''  "                                                          
                + "    END AS sPayerNme " 
                + " , b.sCompnyNm AS sOwnrNmxx "                                                                                                    
                + " , b.cClientTp "                                                                                                                
                + " , TRIM(IFNULL(CONCAT( IFNULL(CONCAT(d.sHouseNox,' ') , ''), "                                                                  
                + "   IFNULL(CONCAT(d.sAddressx,' ') , ''), "                                                                                      
                + "   IFNULL(CONCAT(e.sBrgyName,' '), ''),  "                                                                                      
                + "   IFNULL(CONCAT(f.sTownName, ', '),''), "                                                                                      
                + "   IFNULL(CONCAT(g.sProvName),'') ), '')) AS sAddressx "                                                                        
                + " , CONCAT(k.sBankName, ' ', h.sBrBankNm) AS sBankName  "                                                                        
                + " , CONCAT(IFNULL(h.sAddressx, ''), i.sTownName, j.sProvName) AS sBankAddr "                                                     
                + " , CONCAT(o.sInsurNme, ' ', l.sBrInsNme) AS sInsNamex "                                                                         
                + " , CONCAT(IFNULL(l.sAddressx, ''), m.sTownName, n.sProvName) AS sInsAddrx "                                                     
                + " FROM soa_master a "                                                                                                           
                + " LEFT JOIN client_master b ON b.sClientID = a.sClientID "                                                                       
                + " LEFT JOIN client_address c ON c.sClientID = a.sClientID AND c.cPrimaryx = 1 "                                                   
                + " LEFT JOIN addresses d ON d.sAddrssID = c.sAddrssID "                                                                           
                + " LEFT JOIN barangay e ON e.sBrgyIDxx = d.sBrgyIDxx  "                                                                           
                + " LEFT JOIN towncity f ON f.sTownIDxx = d.sTownIDxx  "                                                                           
                + " LEFT JOIN province g ON g.sProvIDxx = f.sProvIDxx  "                                                                           
                + " LEFT JOIN banks_branches h ON h.sBrBankID = a.sBankIDxx "                                                                      
                + " LEFT JOIN towncity i ON i.sTownIDxx = h.sTownIDxx  "                                                                           
                + " LEFT JOIN province j ON j.sProvIDxx = i.sProvIDxx  "                                                                           
                + " LEFT JOIN banks k ON k.sBankIDxx = h.sBankIDxx     "                                                                           
                + " LEFT JOIN insurance_company_branches l ON l.sBrInsIDx = a.sInsurIDx "                                                          
                + " LEFT JOIN towncity m ON m.sTownIDxx = l.sTownIDxx  "                                                                           
                + " LEFT JOIN province n ON n.sProvIDxx = m.sProvIDxx  "                                                                           
                + " LEFT JOIN insurance_company o ON o.sInsurIDx = l.sInsurIDx  "  ;                                                    
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
     * Description: Sets the ID of this record.
     *
     * @param fdValue
     * @return result as success/failed
     */
    public JSONObject setTransactDte(Date fdValue) {
        return setValue("dTransact", fdValue);
    }

    /**
     * @return The ID of this record.
     */
    public Date getTransactDte() {
        return (Date) getValue("dTransact");
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
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBankID(String fsValue) {
        return setValue("sBankIDxx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getBankID() {
        return (String) getValue("sBankIDxx");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setInsurID(String fsValue) {
        return setValue("sInsurIDx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getInsurID() {
        return (String) getValue("sInsurIDx");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setTermID(String fsValue) {
        return setValue("sTermIDxx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getTermID() {
        return (String) getValue("sTermIDxx");
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
    public JSONObject setRemarks(String fsValue) {
        return setValue("sRemarksx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getRemarks() {
        return (String) getValue("sRemarksx");
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
     * @param fdValue
     * @return result as success/failed
     */
    public JSONObject setApproveDte(Date fdValue) {
        return setValue("dApproved", fdValue);
    }

    /**
     * @return The Value of this record.
     */
    public Date getApproveDte() {
        Date date = null;
        if(getValue("dApproved") == null || getValue("dApproved").equals("")){
            date = SQLUtil.toDate(psDefaultDate, SQLUtil.FORMAT_SHORT_DATE);
        } else {
            date = SQLUtil.toDate(xsDateShort((Date) getValue("dApproved")), SQLUtil.FORMAT_SHORT_DATE);
        }
            
        return date;
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdValue
     * @return result as success/failed
     */
    public JSONObject setPostedDte(Date fdValue) {
        return setValue("dPostedxx", fdValue);
    }

    /**
     * @return The Value of this record.
     */
    public Date getPostedDte() {
        Date date = null;
        if(getValue("dPostedxx") == null || getValue("dPostedxx").equals("")){
            date = SQLUtil.toDate(psDefaultDate, SQLUtil.FORMAT_SHORT_DATE);
        } else {
            date = SQLUtil.toDate(xsDateShort((Date) getValue("dPostedxx")), SQLUtil.FORMAT_SHORT_DATE);
        }
            
        return date;
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
     * @return True if the record assignment is successful.
     */
    public JSONObject setEntryBy(String fsValue) {
        return setValue("sEntryByx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getEntryBy() {
        return (String) getValue("sEntryByx");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fdValue
     * @return result as success/failed
     */
    public JSONObject setEntryDte(Date fdValue) {
        return setValue("dEntryDte", fdValue);
    }

    /**
     * @return The Value of this record.
     */
    public Date getEntryDte() {
        Date date = null;
        if(!getValue("dEntryDte").toString().isEmpty()){
            date = CommonUtils.toDate(getValue("dEntryDte").toString());
        }
        
        return date;
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setPayerNme(String fsValue) {
        return setValue("sPayerNme", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getPayerNme() {
        return (String) getValue("sPayerNme");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setOwnrNm(String fsValue) {
        return setValue("sOwnrNmxx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getOwnrNm() {
        return (String) getValue("sOwnrNmxx");
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
    public JSONObject setAddress(String fsValue) {
        return setValue("sAddressx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getAddress() {
        return (String) getValue("sAddressx");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBankName(String fsValue) {
        return setValue("sBankName", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getBankName() {
        return (String) getValue("sBankName");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBankAddr(String fsValue) {
        return setValue("sBankAddr", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getBankAddr() {
        return (String) getValue("sBankAddr");
    }
   
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setInsName(String fsValue) {
        return setValue("sInsNamex", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getInsName() {
        return (String) getValue("sInsNamex");
    }
    
    /**
     * Description: Sets the Value of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setInsAddr(String fsValue) {
        return setValue("sInsAddrx", fsValue);
    }

    /**
     * @return The Value of this record.
     */
    public String getInsAddr() {
        return (String) getValue("sInsAddrx");
    }
    
}
