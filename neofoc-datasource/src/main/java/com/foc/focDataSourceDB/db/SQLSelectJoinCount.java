/*
 * Created on 27 fevr. 2004
 */
package com.foc.focDataSourceDB.db;

import java.sql.CallableStatement;
import java.sql.ResultSet;

import com.foc.Globals;
import com.foc.dataSource.IFocDataSource;
import com.foc.db.SQLFilter;
import com.foc.desc.FocDesc;
import com.foc.focDataSourceDB.db.connectionPooling.StatementWrapper;
import com.foc.join.FocRequestDesc;
import com.foc.list.FocList;
import com.foc.performance.PerfManager;

/**
 * @author 01Barmaja
 */
public class SQLSelectJoinCount extends SQLSelectJoinRequest {

	private int count = 0; 
	private String fieldName = null;
	
	public SQLSelectJoinCount(FocList initialList, FocRequestDesc requestDesc, SQLFilter filter, String fieldName) {
		super(initialList, requestDesc, filter);
		this.fieldName = fieldName;
	}
  
	protected void addTableFieldsToSelect(FocDesc focDesc, StringBuffer fieldsCommaSeparated, String tableAlias){
		StringBuffer fakeBuffer = new StringBuffer();
		super.addTableFieldsToSelect(focDesc, fakeBuffer, tableAlias);
		
    fieldsCommaSeparated.append("COUNT(DISTINCT ");
    fieldsCommaSeparated.append(fieldName);
    fieldsCommaSeparated.append(")");
	}
  
	@Override
  public boolean buildRequest(){
    request = new StringBuffer("SELECT ");
    StringBuffer fieldsCommaSeparated = new StringBuffer();
    boolean error = false;
    
    if (focDesc != null && focDesc.isPersistent()) {
      String tableAlias = filter.hasJoinMap() ? filter.getJoinMap().getMainTableAlias() : null;
      addTableFieldsToSelect(focDesc, fieldsCommaSeparated, tableAlias);
    
      request.append(fieldsCommaSeparated);
      
      addFrom();
      error = addWhere();
      if(sqlGroupBy != null){
      	request.append(" GROUP BY ("+sqlGroupBy.getGroupByExpression(focDesc)+")");
      }
    }
    return error;
  }
	
  public boolean execute() {
    boolean error = Globals.getDBManager() == null;  
    if(!error && focDesc != null && focDesc.isPersistent() == FocDesc.DB_RESIDENT){   	
      StatementWrapper stmtWrapper = DBManagerServer.getInstance().lockStatement(getDBSourceKey());
  
      ResultSet resultSet = null;
      if (stmtWrapper != null) {
        error = buildRequest();
        if(!error){
          try {
            String reqAdapted = getRequestAdaptedToProvider();
           
            PerfManager.startDBExec();
            long startTimeDBRequest = System.currentTimeMillis();
            if(focList != null && focList.isStoredProcedure()){
            	IFocDataSource iFocDataSource = Globals.getApp().getDataSource();
            	if(iFocDataSource != null){
            		CallableStatement stmt = iFocDataSource.sp_Call(focList.getStoredProcedureName(), focList.getStoredProcedureParams());
            		resultSet = stmt != null ? stmt.getResultSet() : null;
            	}
            }else{
            	stmtWrapper = DBManagerServer.getInstance().executeQuery_WithMultipleAttempts(stmtWrapper, reqAdapted);
            	resultSet = stmtWrapper != null ? stmtWrapper.getResultSet() : null;
            }
            //stmt = resultSet.getStatement(); 
            //resultSet = stmt.executeQuery(reqAdapted);
            long endTimeDBRequest = System.currentTimeMillis();
            Globals.logString(" - SQL DURATION = "+(endTimeDBRequest-startTimeDBRequest));
            PerfManager.endDBExecForRequest(reqAdapted);
          } catch (Exception e) {
            Globals.logException(e);
          }
        }
      }
      
      if(!error){
        if(resultSet != null){
          try{
            if(resultSet.next()) {
            	count = resultSet.getInt(1);
            }
            resultSet.close();
          }catch(Exception e){
            Globals.logException(e);
          }
        }
      }      
      DBManagerServer.getInstance().unlockStatement(stmtWrapper);
      
    }
    return error;
  }

	public int getCount() {
		return count;
	}
  
}
