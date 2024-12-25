/*
 * Created on 27 fevr. 2004
 */
package com.foc.focDataSourceDB.db;

import java.sql.*;

import com.foc.*;
import com.foc.desc.*;
import com.foc.desc.field.*;
import com.foc.focDataSourceDB.db.connectionPooling.StatementWrapper;
import com.foc.list.*;

/**
 * @author 01Barmaja
 */
public class SQLSelectString extends SQLSelect{
  
  protected StringBuffer request = null;
  
  public SQLSelectString(StringBuffer request) {
    super((FocObject)null, null, null);
    this.request = request;
  }
 
  public boolean buildRequest() {
    return false;
  }
  
  public boolean execute() {
    StatementWrapper stmt = DBManagerServer.getInstance().lockStatement(getDBSourceKey());
    ResultSet resultSet = null;
    
    if (stmt != null) {
      try {
      	stmt = DBManagerServer.getInstance().executeQuery_WithMultipleAttempts(stmt, request.toString());
      	resultSet = stmt != null ? stmt.getResultSet() : null; 
      } catch (Exception e) {
        Globals.logException(e);
      }
    }
      
    //Constructing a FocDesc
    if (resultSet != null) {
      try {
        ResultSetMetaData meta = resultSet.getMetaData();
        FocDesc focDesc = new FocDesc(FocObjectGeneral.class, FocDesc.NOT_DB_RESIDENT, meta.getCatalogName(1), false);
        FocObjectGeneral.setFocDesc(focDesc);
        
        for (int i = 1; i <= meta.getColumnCount(); i++) {
          String colLabel = meta.getColumnLabel(i);
          int colType = meta.getColumnType(i);
          int size = meta.getColumnDisplaySize(i);
          int scale = meta.getScale(i);
          //BAntoineS - AUTOINCREMENT
          boolean autoIncrement = meta.isAutoIncrement(i);
          FField focField = FField.newField(colType, colLabel, i, size, scale, autoIncrement);
          //EAntoineS - AUTOINCREMENT
          focDesc.addField(focField);
        }
        setFocDesc(focDesc);
      }catch(Exception e){
        Globals.logException(e);
      }
    }
        
    
    loadMode = SQLSelect.LOAD_IN_EMPTY_LIST;
    focList = new FocList(new FocLinkSimple(focDesc));

    treatResultSet(resultSet);
    
		try{
			if(resultSet != null) resultSet.close();
		}catch (SQLException e){
			Globals.logExceptionWithoutPopup(e);
		}
    DBManagerServer.getInstance().unlockStatement(stmt);
    return false;
  }

}
