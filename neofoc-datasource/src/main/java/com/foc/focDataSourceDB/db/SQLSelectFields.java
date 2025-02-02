/*
 * Created on 27 feb. 2004
 */
package com.foc.focDataSourceDB.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;

import com.foc.Globals;
import com.foc.db.DBManager;
import com.foc.db.SQLFilter;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FField;
import com.foc.focDataSourceDB.db.connectionPooling.StatementWrapper;
import com.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
public class SQLSelectFields extends SQLRequest {
  private ArrayList<Integer> fields = null;
  private ArrayList lines = null;
  
  public SQLSelectFields(FocDesc focDesc, int fieldID, SQLFilter filter) {
    super(focDesc, filter);
    fields = new ArrayList<Integer>();
    addField(fieldID);
  }
  
  public void addField(int fieldID){
    fields.add(Integer.valueOf(fieldID));
  }

  protected boolean isSupportJoins(){
  	return true;
  }
  
  public boolean buildRequest(String selectCommand) {
    request = new StringBuffer("");
    boolean error = false;

    if (focDesc != null && focDesc.isPersistent()) {
      request.append(selectCommand+" ");

      for(int i=0; i<fields.size(); i++){
        Integer fieldIDInt = (Integer) fields.get(i);
        int fieldID = fieldIDInt.intValue();
        FField field = focDesc.getFieldByID(fieldID);
        
        if (i > 0) {
          request.append(",");
        }
        String fieldName = field.getDBName(); 
        if(DBManager.provider_FieldNamesBetweenSpeachmarks(focDesc.getProvider())){
        	fieldName = "\""+fieldName+"\"";
        }
        request.append(fieldName);
      }
      
      addFrom();
      error = addWhere();
      addOrderBy();
      addOffset();
    }
    return error;
  }

  public boolean buildRequest() {
    return buildRequest("SELECT");
  }

  public boolean execute() {
    boolean error = Globals.getDBManager() == null;     
    if(!error && focDesc != null && focDesc.isPersistent() == FocDesc.DB_RESIDENT){
      StatementWrapper stmt = DBManagerServer.getInstance().lockStatement(focDesc.getDbSourceKey());
  
      ResultSet resultSet = null;
  
      if (stmt != null) {
        error = buildRequest();
        if(!error){
          try {
          	stmt = DBManagerServer.getInstance().executeQuery_WithMultipleAttempts(stmt, request.toString());
          	resultSet = stmt != null ? stmt.getResultSet() : null;
          	error = stmt == null;
//            Globals.logString(request.toString());
//            resultSet = stmt.executeQuery(request.toString());
          } catch (Exception e) {
            Globals.logException(e);
          }
        }
      }
      
      if(!error){
        if (resultSet != null) {
          try {
            lines = new ArrayList();
            
            ResultSetMetaData meta = resultSet.getMetaData();
            meta.getColumnCount();                       

            while (resultSet.next()) {
              //Initialize the property array
              FProperty[] props = new FProperty[fields.size()];
              for(int i=0; i<fields.size(); i++){
                Integer fieldID = (Integer) fields.get(i);
                FField field = focDesc.getFieldByID(fieldID.intValue()); 
                props[i] = field.newProperty(null, null);
              }            
                  
              for(int col=1; col <= meta.getColumnCount(); col++){
                String value = resultSet.getString(col);
                props[col-1].setSqlString(value);
              }
              
              lines.add(props);
            }
            
          } catch (Exception e) {
            Globals.logException(e);
          }
        }
      }
      DBManagerServer.getInstance().unlockStatement(stmt);
    }
    return error;
  }

  public int getLineNumber() {
    return lines != null ? lines.size() : 0;
  }
  
  public int getColumnNumber() {
    return fields != null ? fields.size() : 0;
  }
  
  public FProperty getPropertyAt(int line, int col){
    FProperty prop = null;
    if(lines != null){
      FProperty[] lineAt = (FProperty[]) lines.get(line);
      if(lineAt != null){
        prop = lineAt[col];
      }
    }
    return prop;
  }
}
