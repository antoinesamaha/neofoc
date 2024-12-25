/*
 * Created on 27 fevr. 2004
 */
package com.foc.focDataSourceDB.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.foc.Globals;
import com.foc.db.SQLFilter;
import com.foc.desc.*;
import com.foc.desc.field.FField;
import com.foc.focDataSourceDB.db.connectionPooling.StatementWrapper;

/**
 * @author 01Barmaja
 */
public class SQLSelectFindReferenceForUniqueKey extends SQLSelect {
   
  public SQLSelectFindReferenceForUniqueKey(FocObject obj) {
    super(obj, obj.getThisFocDesc(), null);
    filter = new SQLFilter(obj, SQLFilter.FILTER_ON_KEY);
    filter.setOwnerOfTemplate(false);
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
 		error = error || !getFocDesc().isKeyUnique();
  	if(!error){
    	request = new StringBuffer("SELECT ");
    	request.append(getFocDesc().getRefFieldName());
   		request.append(" FROM ");
    	request.append(getFocDesc().getStorageName_ForSQL());
    	addWhere();
  	}else{
  		Globals.logString("TABLE : "+getFocDesc().getStorageName_ForSQL());
  		if(getFocDesc().getFieldByID(FField.REF_FIELD_ID) == null){
  			Globals.logString("Ref field is null");
  		}else{
  			Globals.logString("Not unique key");
  		}
  	}
  	return error;
  }
  
  public boolean execute() {
    StatementWrapper stmt = DBManagerServer.getInstance().lockStatement();
    ResultSet resultSet = null;
    
    if (stmt != null) {
      try {
      	buildRequest();
      	String req = getRequestAdaptedToProvider();
      	//Globals.logString(req);
      	
      	stmt = DBManagerServer.getInstance().executeQuery_WithMultipleAttempts(stmt, req);
      	resultSet = stmt != null ? stmt.getResultSet() : null;  
      	
        //resultSet = stmt.executeQuery(req);
      } catch (Exception e) {
        Globals.logException(e);
      }
    }
      
    try {
			if (resultSet != null && resultSet.next()) {
				int reference = resultSet.getInt(1);
				getFocObjectToBeFilled().setReference(reference);
				getFocObjectToBeFilled().setCreated(false);
			}
			if(resultSet != null) resultSet.close();
		} catch (SQLException e) {
			Globals.logException(e);
		}
    
		DBManagerServer.getInstance().unlockStatement(stmt);
    return false;
  }
}
