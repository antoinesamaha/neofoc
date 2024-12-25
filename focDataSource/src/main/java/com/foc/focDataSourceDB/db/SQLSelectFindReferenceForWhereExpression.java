/*
 * Created on 20 May 2009
 */
package com.foc.focDataSourceDB.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.foc.Globals;
import com.foc.db.SQLFilter;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.focDataSourceDB.db.connectionPooling.StatementWrapper;

/**
 * @author 01Barmaja
 */
public class SQLSelectFindReferenceForWhereExpression extends SQLSelect {
  
	private int ref = 0;
	
  public SQLSelectFindReferenceForWhereExpression(FocDesc focDesc, String whereExpression) {
    super((FocObject)null, focDesc, null);
    filter = new SQLFilter(null, SQLFilter.FILTER_ON_NOTHING);
    filter.putAdditionalWhere("SPECIAL", whereExpression);
  }
 
  public void dispose(){
  	if(filter != null){
	  	filter.dispose();
	  	filter = null;
  	}
  	super.dispose();
  }
  
  public boolean buildRequest() {
  	boolean error = getFocDesc().getFieldByID(FField.REF_FIELD_ID) == null;
  	if(!error){
    	request = new StringBuffer("SELECT ");
    	request.append(getFocDesc().getRefFieldName());
   		request.append(" FROM ");
    	request.append(getFocDesc().getStorageName_ForSQL());
    	addWhere();
  	}else{
  		Globals.logString("TABLE : "+getFocDesc().getStorageName_ForSQL()+" REF field does not exist");
  	}
  	return error;
  }
  
  public boolean execute() {
    StatementWrapper stmt = DBManagerServer.getInstance().lockStatement(getDBSourceKey());
    ResultSet resultSet = null;
    
    if (stmt != null) {
      try {
      	buildRequest();
      	String req = getRequestAdaptedToProvider();

      	stmt = DBManagerServer.getInstance().executeQuery_WithMultipleAttempts(stmt, req);
      	resultSet =  stmt != null ? stmt.getResultSet() : null;
      	
//      	Globals.logString(req);
//        resultSet = stmt.executeQuery(req);
      } catch (Exception e) {
        Globals.logException(e);
      }
    }
      
    try {
			if (resultSet != null && resultSet.next()) {
				ref = resultSet.getInt(1);
			}
			if(resultSet != null) resultSet.close();
		} catch (SQLException e) {
			Globals.logException(e);
		}
		DBManagerServer.getInstance().unlockStatement(stmt);
    return false;
  }
  
  public int getReference(){
  	return ref;
  }
}
