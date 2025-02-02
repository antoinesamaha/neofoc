/*
 * Created on 27 feb. 2004
 */
package com.foc.focDataSourceDB.db;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.admin.FocDBLog;
import com.foc.db.DBManager;
import com.foc.db.IDBReloader;
import com.foc.db.SQLFilter;
import com.foc.db.SQLJoin;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldPath;
import com.foc.focDataSourceDB.db.connectionPooling.StatementWrapper;
import com.foc.performance.PerfManager;

/**
 * @author 01Barmaja
 */
public class SQLRequest {

  public static final int TYPE_SELECT =  0;
  public static final int TYPE_INSERT =  1;
  public static final int TYPE_UPDATE =  2;
  public static final int TYPE_DELETE =  3;
  public static final int TYPE_OTHER  = 10;
  
  protected FocDesc      focDesc = null;

  protected SQLFilter    filter  = null;
  protected StringBuffer request = null;
  //protected ResultSet resultSet = null;
  private ArrayList<Integer> queryFieldList = null;
  public static final String SQL_DEBUG_STRING = "SELECT REF,FATHERNODE_REF,NODE_COLLAPSE,ORDER_FLD,CODE,EXTERNAL_CODE,NAME,WBS_REF,CLOSE_UND_REQUIREMENT,BKDN_SOURCE_REF,BKDN_RESERVATION_REF,BKDN_PATH_RESERVATION,CLT_ORD_LINE_REF,INSPECTION_AREA_REF,RELATED_SUB_SALES_QTY,RES_FOR_CLT_ORD_SUB_SALES_QTY,CONVERT(DESCRIP USING utf8),SUB_CONTRACT_REF,SUB_CONT_VAR_REF,OPTION_REF,DISABLED,PRINTING_LIMIT,SIGN,QUANTITY_PU,QUANTITY,NET_QUANTITY,QTY_UNIT_REF,CONV_FACTOR,WASTAGE,SALES_QTY,NET_SALES_QTY,SALES_UNIT_REF,PRICE_QTY,UNIT_PRICE,PRICE,PRICE_DISCOUNT,PRICE_DISCOUNT_PERCENTAGE,PRICE_AFTER_DISCOUNT,PRICE_TAX,PRICE_TAX_PERCENTAGE,PRICE_AFTER_DISCOUNT_AND_VAT,NET_COST,ADD_COST,ESTIMATE_BY_ITEM,ORG_COST_CURR_REF,UNIT_COST,INFLATION_FACTOR,FX_FACTOR,UNIT_COST_WITH_INFLATION,COST,TOP_DWN_PERCENTAGE,TOP_DWN_PRICE,PRICE_FIXED,CUM_FORECAST_QANTITY,CUM_FORECAST_COST,CUM_FORECAST_LOAD,FORECAST_QUANTITY,FORECAST_LOAD,FORECAST_TOTAL_QANTITY,FORECAST_TOTAL_COST,FORECAST_TOTAL_LOAD,FORECAST_MIN_START,FORECAST_MIN_END,MARGIN,MARGIN_CUSTOM,PRICE_FORCED,COMPUTED_MARGIN,COST_CENTER,ACTIVITY,OVERHEAD_BEHAVIOUR,DURATION,DUR_UNIT_REF,DURATION_UNIT_CUSTOM,MIN_START,MIN_END,ACTUAL_START,ACTUAL_END,MAX_START,MAX_END,MIN_START_ALLOWED,MAX_END_ALLOWED,DUE_DATE,PERCENT_COMPLETION,PROGRESS_REPORT_DATE,HR_EMPLOYEE_REF,MATERIAL_REF,LOG_VEHICLE_REF,SERVICE_REF,ASSET_REF,MACHINE_REF,UND_ALIAS_REF,CUST_PARAMSET_REF,BATCH,MODIF_COMMENT,UND_DESCRIPTION,ACTUAL_QUANTITY,ACTUAL_COST,TO_BE_INVOICED,TO_BE_DELIVERED,DELIVERY_CLOSED,AMOUNT_INVOICED,QUANTITY_INVOICED,UNFORECASTED_QUANTITY,UNFORECASTED_COST,FORECASTED_OUT_RANGE_QUANTITY,UNFORECASTED_OUT_RANGE_COST,ROLE_REF,TASK_REF,ROLE_DEDUCE_QTY,ROLE_QTY_REALY_DEDUCED,ROLE_DRIVING,ROLE_SPEED,ROLE_EFFICIENCY,ROLE_WORK_UNIT_REF,ROLE_TIME_UNIT_REF,EFFICIENCY_ACTUAL,EFFICIENCY_ON_BUDGET,VARIANCE_TOTAL,VARIANCE_COST,VARIANCE_QUANTITY,COMPANY_REF,CUMUL_EV_COST,CUMUL_BASELINE_QUANTITY,CUMUL_BASELINE_LOAD,CUMUL_BASELINE_COST,SCENARIO_QUANTITY,SCENARIO_LOAD,CPI,CUSTOM_COLOR,COLOR,DIVISION_REF,PLN_COST_1,PLN_COST_2,PLN_COST_3,PLN_COST_4,PLN_COST_5,PLN_COST_6,PLN_COST_7,PLN_COST_8,PLN_COST_9,PLN_COST_10,PLN_COST_11,PLN_COST_12,PLN_COST_13,PLN_COST_14,PLN_COST_15,PLN_COST_OTHER,ACT_COST_1,ACT_COST_2,ACT_COST_3,ACT_COST_4,ACT_COST_5,ACT_COST_6,ACT_COST_7,ACT_COST_8,ACT_COST_9,ACT_COST_10,ACT_COST_11,ACT_COST_12,ACT_COST_13,ACT_COST_14,ACT_COST_15,ACT_COST_OTHER FROM wbs_bkdn WHERE ( ( ( WBS_REF";

  private boolean whereMandatory = false;
  
	public SQLRequest(FocDesc focDesc, SQLFilter filter) {
    this.focDesc = focDesc;
    this.filter = filter;
    queryFieldList = null;
  }

  public SQLRequest(FocDesc focDesc) {
    this.focDesc = focDesc;
    this.filter = null;
  }
  
  public void dispose(){
    focDesc = null;
    filter = null;
    request = null;
    if(queryFieldList != null){
      queryFieldList.clear();
      queryFieldList = null;
    }
  }

  public String getDBSourceKey(){
  	FocDesc focDesc = getFocDesc();
  	return focDesc != null ? focDesc.getDbSourceKey() : null;
  }
  
  protected FocObject getFocObject(){
  	return null;
  }
  
  public ArrayList<Integer> getQueryFieldList(boolean create){
    if(queryFieldList == null && create){
      queryFieldList = new ArrayList<Integer>();
    }
    return queryFieldList;
  }

  public int getQueryFieldCount(){
    ArrayList arrayList = getQueryFieldList(false);
    return (arrayList != null) ? arrayList.size() : 0;
  }

  public int getQueryFieldAt(int i){
    ArrayList arrayList = getQueryFieldList(false);
    return (arrayList != null) ? ((Integer)(arrayList.get(i))).intValue() : 0;
  }

  public void addQueryField(int i){
    ArrayList<Integer> arrayList = getQueryFieldList(true);
    arrayList.add(Integer.valueOf(i));
  }

  public boolean isFieldInQuery(int fieldID){
    FField field = focDesc.getFieldByID(fieldID);
    boolean ret = queryFieldList == null && field.isIncludeInDBRequests();
    
    for(int i=0; i<getQueryFieldCount() && !ret; i++){      
      ret = getQueryFieldAt(i) == fieldID;
    }
    return ret ;
  }

  public boolean isFieldInQuery(FFieldPath path){
    return path != null ? isFieldInQuery(path.get(0)) : false;
  }
  
  protected boolean isSupportJoins(){
  	return false;
  }
  
  public String addTableNameSurroundings(String tableName){
  	if(DBManager.provider_TableNamesBetweenSpeachmarks(focDesc.getProvider()) && !focDesc.getStorageName_ForSQL().contains(".")){
  		return "\""+tableName+"\"";
  	}else{
  		return tableName;
  	}
  }
  
	public static String getNamespacePrefix(FocDesc focDesc) {
	  return focDesc != null ? focDesc.getNamespacePrefix() : "";
	}
  
  public void addFrom(boolean withJoin) {
    if (focDesc != null && request != null) {
      request.append(" FROM ");
      
      String namespacePart = getNamespacePrefix(focDesc);
      
      if(filter != null && filter.hasJoinMap()){
        Iterator iter = filter.getJoinMap().getJoinMapIterator();
        boolean firstTable = true;
        while (iter.hasNext()){
          SQLJoin join = (SQLJoin)iter.next();
          if(firstTable){
            request.append(namespacePart+addTableNameSurroundings(focDesc.getStorageName_ForSQL())+" "+filter.getJoinMap().getMainTableAlias());
            firstTable = false;
          }
          request.append(" ");
          request.append(join.getSQLString());
          //request.append(join.getNewTableName() +" "+join.getNewAlias());  
        }
      }else{
      	request.append(namespacePart+addTableNameSurroundings(focDesc.getStorageName_ForSQL()));
      }
    }
  }
  
  public void addFrom() {
  	addFrom(true);
  }

  public void append(String str) {
    if (request != null) request.append(str);
  }

  /*
   * private boolean addFieldToWhere(FocObject templateFocObj, String fldName,
   * int fieldID, boolean isFirst) { FProperty objProp =
   * templateFocObj.getFocProperty(fieldID); FField propField =
   * objProp.getFocField(); String value = objProp.getString(); String sqlValue =
   * objProp.getSqlString(); boolean errorAddingField = true; boolean
   * valueNotNull = (fieldID == FField.REF_FIELD_ID) ? value.compareTo("0") != 0 :
   * value.compareTo("") != 0;
   * 
   * if (valueNotNull && fldName.compareTo("") != 0) { if (isFirst) {
   * request.append(" WHERE ("); } else { request.append(" and ("); }
   * request.append(fldName); request.append("="); request.append(sqlValue); if
   * (!isFirst) { request.append(")"); } errorAddingField = false; } return
   * errorAddingField; }
   * 
   * protected void addWhere() { if (focDesc != null && filter != null &&
   * request != null) { boolean isFirst = true; boolean atLeastOneFieldAdded =
   * false;
   * 
   * //Building Where on Template fields //---------------------------------
   * FocObject focObj = filter.getObjectTemplate();
   * 
   * if (focObj != null) { //We start with the idetifier property field
   * FProperty idProp = focObj.getIdentifierProperty(); FField idField = (idProp !=
   * null) ? idProp.getFocField() : null; if (idField != null) { boolean
   * errorAdding = addFieldToWhere(focObj, idField.getName(), idField.getID(),
   * isFirst); isFirst = isFirst && errorAdding; atLeastOneFieldAdded =
   * !errorAdding; }
   * 
   * //If the identifier is not added we work on the key fields if(isFirst){
   * Enumeration enum = focDesc.newFocFieldEnum(FocFieldEnum.CAT_KEY,
   * FocFieldEnum.LEVEL_DB_FIELDS); while (enum.hasMoreElements()) { FField
   * focField = (FField) enum.nextElement(); boolean errorAdding =
   * addFieldToWhere(focObj, focField.getName(), focField.getID(), isFirst);
   * isFirst = isFirst && errorAdding; atLeastOneFieldAdded = !errorAdding; } } }
   * 
   * //Building Where on Master fields //-------------------------------
   * FocObject masterObj = filter.getMasterObject(); if (masterObj != null) {
   * FField focSlaveField = (FField)
   * focDesc.getFieldByID(FField.MASTER_REF_FIELD_ID); Enumeration enum =
   * masterObj.getThisFocDesc().newFocFieldEnum(FocFieldEnum.CAT_REF,
   * FocFieldEnum.LEVEL_DB_FIELDS); if (enum != null && enum.hasMoreElements() &&
   * focSlaveField != null) { FField focMasterField = (FField)
   * enum.nextElement(); boolean errorAdding = addFieldToWhere(masterObj,
   * focSlaveField.getName(), focMasterField.getID(), isFirst); isFirst =
   * isFirst && errorAdding; atLeastOneFieldAdded = !errorAdding; } }
   * 
   * if (!isFirst && atLeastOneFieldAdded) { request.append(")"); } } }
   */

  public void addOrderBy() {
  	if(filter != null) {
  		filter.addOrderBy(this.request);
  	}
  }
  
  public void addOffset() {
  	if(			 filter != null 
  			&&   this.request != null
  			&&   filter.getOffset() >= 0
  			&&   filter.getOffsetCount() >= 0
  			&& 	(getSQLRequestType() == TYPE_SELECT || getSQLRequestType() == TYPE_OTHER)) {
  		if(focDesc.getProvider() == DBManager.PROVIDER_POSTGRES) {
				request.append(" offset ");
				request.append(filter.getOffset());
				request.append(" limit ");
				request.append(filter.getOffsetCount());
  		} else if(focDesc.getProvider() == DBManager.PROVIDER_ORACLE) {
 				if(focDesc.getServerVersion() < 12 || focDesc.getServerVersion() > 0) {
 					int endingIndex   = filter.getOffset() + filter.getOffsetCount();
 					int startingIndex = filter.getOffset();
 					
 					StringBuffer requestWrapper = new StringBuffer();
 					requestWrapper.append("SELECT * FROM ");
 					requestWrapper.append("( SELECT pagination.*, rownum r__ FROM (");
 					requestWrapper.append(request);
 					requestWrapper.append(") pagination "); 					
 					requestWrapper.append("WHERE rownum < ( " + endingIndex + " + 1) ");
 					requestWrapper.append(") WHERE r__ >= ( " + startingIndex + " + 1) ");
 					request = requestWrapper; 
  			} else {
					request.append(" OFFSET ");
					request.append(filter.getOffset());
					request.append(" ROWS FETCH NEXT ");
					request.append(filter.getOffsetCount());
					request.append(" ROWS ONLY ");
  			}
  		}
  	}
  }
  
  public boolean addWhere(boolean withJoin) {
    boolean requestNotValid = false;
    if (filter != null) {
    	boolean atLeastOneAdded = filter.addWhereToRequest(this.request, this.getFocDesc(), true, getSQLRequestType() != TYPE_UPDATE);

    	if(isWhereMandatory() && !atLeastOneAdded) {
    		requestNotValid = true;
    	}
    } else if(isWhereMandatory()){
    	requestNotValid = true;
    }
    return requestNotValid;
  }

  public boolean addWhere() {
    return addWhere(true);
  }

  public boolean buildRequest() {
    return false;
  }

  public static String adapteRequestToDBProvider(StringBuffer request){
    String req = request.toString();
//    if (Globals.getDBManager() != null && Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE){
//      req = req.replace('"', '\'');
//    }
    return req;
  }
  
  public String getRequestAdaptedToProvider(){
  	return adapteRequestToDBProvider(request);
  }
  
  //BAntoineS - AUTOINCREMENT
  protected void executeUpdate(Statement stmt, String req) throws Exception{
  	stmt.executeUpdate(req);
  }
  //BAntoineS - AUTOINCREMENT

  public boolean execute() throws Exception{
  	boolean error = false;
  	error = doExecute();
  	return error;
  }
  
  protected int getSQLRequestType(){
    return TYPE_OTHER;
  }
  
  public boolean doExecute() throws Exception{
    boolean error = false;
    
    if (focDesc.isPersistent()) {

  		//Writing the FocDBLog
  		//--------------------
    	String change = null; 
    	if(ConfigInfo.isDbLogActive() && getFocObject() != null && getSQLRequestType() == TYPE_UPDATE){
       	change = FocDBLog.prepareLogChangeString(getFocObject());
    	}
      //--------------------
      
    	StatementWrapper stmt = DBManagerServer.getInstance().lockStatement(getDBSourceKey());
    	if (stmt != null) {
	      error = buildRequest();
	      if(error){
	      	Globals.logString("DB Error Preparing Request : "+request);
	      }else if(request != null && request.length()>0){
		      try {
		        String req = getRequestAdaptedToProvider();
		        
		        PerfManager.startDBExec();
            long startTimeDBRequest = System.currentTimeMillis();
		        stmt      = DBManagerServer.getInstance().executeQuery_WithMultipleAttempts(stmt, req, getSQLRequestType(), getFocObject());
            long endTimeDBRequest = System.currentTimeMillis();
            Globals.logString(" - SQL DURATION = "+(endTimeDBRequest-startTimeDBRequest));
	          
		        if(getSQLRequestType() == TYPE_UPDATE || getSQLRequestType() == TYPE_INSERT){
	          	if(getFocObject() != null){
	          		getFocObject().backup();
	          		IDBReloader dbReloader=	Globals.getApp().getDbReloader();
	          		if(dbReloader!=null) {
	          			if (getFocObject().getThisFocDesc() != null) {
	          				Globals.logString("IDBReloader action=" + getSQLRequestType() + " Table =" + getFocObject().getThisFocDesc().getName() + " Ref =" + getFocObject().getReferenceInt());
	          			}
	          			dbReloader.reloadTable(getFocObject(), getSQLRequestType());	
//		    	      	Globals.logString("Do Execute: called DBReloader.reloadTable, table name: "+getFocObject().getThisFocDesc().getName());
	          		}
	          	}
	          }
	          if(getSQLRequestType() == TYPE_DELETE && getFocObject() != null){
          		IDBReloader dbReloader=	Globals.getApp().getDbReloader();
          		if (dbReloader!=null) {
          			if (getFocObject().getThisFocDesc() != null) {
          				Globals.logString("IDBReloader action=" + getSQLRequestType() + " Table =" + getFocObject().getThisFocDesc().getName() + " Ref =" + getFocObject().getReferenceInt());
          			}
          			dbReloader.reloadTable(getFocObject(), getSQLRequestType());
          		}
	          }
		        PerfManager.endDBExecForRequest(req);
		      } catch (Exception e) {
		        error = true;
		        Globals.logException(e);
		      }
	      }
	      DBManagerServer.getInstance().unlockStatement(stmt);
    	}
    	
    	if(!error) {
    		//Writing the FocDBLog
    		//--------------------
      	if(ConfigInfo.isDbLogActive() && getFocObject() != null){
  	      if(getSQLRequestType() == TYPE_UPDATE || getSQLRequestType() == TYPE_INSERT){
        		int action = getSQLRequestType() == TYPE_INSERT ? FocDBLog.ACTION_INSERT : FocDBLog.ACTION_UPDATE;
        		FocDBLog.addLog(getFocObject(), action, change);
  	      } else if(getSQLRequestType() == TYPE_DELETE){
        		int action = FocDBLog.ACTION_DELETE;
        		FocDBLog.addLog(getFocObject(), action, "");
  	      }
      	}
        //--------------------
    	}
    }

    return error;
  }

  /**
   * @return
   */
  public FocDesc getFocDesc() {
    return focDesc;
  }
  
  protected void setFocDesc(FocDesc focDesc) {
    this.focDesc = focDesc;
  }
  
	public boolean isWhereMandatory() {
		return whereMandatory;
	}

	public void setWhereMandatory(boolean whereMandatory) {
		this.whereMandatory = whereMandatory;
	}
}