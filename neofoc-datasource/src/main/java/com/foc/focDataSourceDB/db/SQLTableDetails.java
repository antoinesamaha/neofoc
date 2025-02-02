/*
 * Created on 27 fevr. 2004
 */
package com.foc.focDataSourceDB.db;

import java.sql.*;
import java.util.*;

import com.foc.*;
import com.foc.desc.*;
import com.foc.desc.field.*;
import com.foc.focDataSourceDB.db.connectionPooling.StatementWrapper;

/**
 * @author 01Barmaja
 */
public class SQLTableDetails extends SQLRequest {

  private ResultSet resultSet = null;
  private Hashtable<String, FField> hash        = null;
  private Hashtable<String, String> foreignKeys = null;
  private boolean withForeignKeys = false;

  public SQLTableDetails(FocDesc focDesc) {
    this(focDesc, false);
  }

  public SQLTableDetails(FocDesc focDesc, boolean withForeignKeys) {
  	super(focDesc);
  	this.withForeignKeys = withForeignKeys;
  }
  
  public boolean buildRequest() {
    request = new StringBuffer("");

    if (focDesc != null && focDesc.isPersistent()) {
      request.append("SELECT *");
      addFrom();
      request.append(" WHERE 1=2");
    }
    return false;
  }

  public boolean execute() {
    boolean error = false;
    StatementWrapper stmt = DBManagerServer.getInstance().lockStatement(getDBSourceKey());
    hash = new Hashtable<String, FField>();

    if (hash != null) {
      error = buildRequest();
      
      /*if(request.toString().compareTo("SELECT * FROM L3TEST WHERE 1=2") == 0){
        int debug = 4;
      }*/
      
      if(!error){
        try {
        	stmt = DBManagerServer.getInstance().executeQuery_WithMultipleAttempts(stmt, request.toString());
        	resultSet = stmt != null ? stmt.getResultSet() : null;
        	//stmt = resultSet != null ? resultSet.getStatement() : null;
//          Globals.logString(request.toString());
//          resultSet = stmt.executeQuery(request.toString());
          
        } catch (Exception e) {
          Globals.logException(e);
        }
      }
    }
    
    if(!error){
      if (resultSet != null) {
        try {
          ResultSetMetaData meta = resultSet.getMetaData();
          
          if (meta != null) {
            for (int i = 1; i <= meta.getColumnCount(); i++) {
              String colLabel = meta.getColumnLabel(i);
              int colType = meta.getColumnType(i);
              int size = meta.getColumnDisplaySize(i);
              int scale = meta.getScale(i);
              int precision = meta.getPrecision(i);
              //BAntoineS - AUTOINCREMENT
              boolean autoIncrement = meta.isAutoIncrement(i);
              FField focField = FField.newField(colType, colLabel, 0, precision ==0 ? size : precision, scale, autoIncrement);
              //EAntoineS - AUTOINCREMENT
              if(autoIncrement && focField instanceof FIntField){
              	focField = new FReferenceField(colLabel, colLabel);
              	focField.setAutoIncrement(true);
              }
              if(focField == null){
              	focField = FField.newField(colType, colLabel, 0, precision ==0 ? size : precision, scale, autoIncrement);
              }
              hash.put(focField.getName(), focField);
            }
          }
        } catch (Exception e) {
          Globals.logException(e);
        }
        DBManagerServer.getInstance().unlockStatement(stmt);
        
        if(withForeignKeys){
        	foreignKeys = new Hashtable<String, String>();
	        try{
	        	Connection connection = DBManagerServer.getInstance().getConnection();
		        DatabaseMetaData metaData = connection.getMetaData();
		        ResultSet resSet = metaData.getImportedKeys(connection.getCatalog(), null, focDesc.getStorageName());
		        while (resSet.next()) {
//		            String fkTableName = resSet.getString("FKTABLE_NAME");
		            String fkColumnName = resSet.getString("FKCOLUMN_NAME");//This is field name
		            String pkTableName = resSet.getString("PKTABLE_NAME");//Other Table
//		            String pkColumnName = resSet.getString("PKCOLUMN_NAME");
		            foreignKeys.put(fkColumnName, pkTableName);
//		            Globals.logString(fkTableName + "." + fkColumnName + " -> " + pkTableName + "." + pkColumnName);
		        }
		        DBManagerServer.getInstance().releaseConnection(connection);
	        }catch(Exception e){
	        	Globals.logException(e);
	        }
        }
        
        try{
					if(resultSet != null) resultSet.close();
				}catch (SQLException e){
					Globals.logExceptionWithoutPopup(e);
				}
      }
    }
    return error;
  }

  public Hashtable<String, FField> getFieldsHashtable() {
    return hash;
  }

	public Hashtable<String, String> getForeignKeys() {
		return foreignKeys;
	}
}
