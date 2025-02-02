/*
 * Created on 27 feb. 2004
 */
package com.foc.focDataSourceDB.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.foc.Globals;
import com.foc.db.DBManager;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.focDataSourceDB.db.connectionPooling.StatementWrapper;

/**
 * @author 01Barmaja
 */
public class SQLSelectExistance extends SQLSelect {
  
	private int exist = EXIST_UNDETERMINED;
	private int count = 0;
  private StringBuffer sqlWhere = null;
  
  public static int EXIST_YES          = 1;
  public static int EXIST_NO           = 2;
  public static int EXIST_UNDETERMINED = 0;
  
  public SQLSelectExistance(FocDesc desc, StringBuffer sqlWhere) {
    super((FocObject)null, desc, null);
    this.sqlWhere = sqlWhere;
    exist = EXIST_UNDETERMINED;
  }
 
  public boolean buildRequest() {
  	request = new StringBuffer("SELECT ");
//    if (getFocDesc().getFieldByID(FField.REF_FIELD_ID) != null) {
//    	request.append(FField.REF_FIELD_NAME);
//    }else{
    	request.append("count(*)");
//    }
 		request.append(" FROM ");
 		
 		request.append(SQLRequest.getNamespacePrefix(getFocDesc())); 		
 		if(DBManager.provider_TableNamesBetweenSpeachmarks(getFocDesc().getProvider())){
 			request.append("\""+getFocDesc().getStorageName_ForSQL()+"\"");
 		}else{
 			request.append(getFocDesc().getStorageName_ForSQL());
 		}
  	request.append(" WHERE "+sqlWhere);
    return false;
  }
  
  public boolean execute() {
    StatementWrapper stmt = DBManagerServer.getInstance().lockStatement(getDBSourceKey());
    ResultSet resultSet = null;
    
    if (stmt != null) {
      try {
      	buildRequest();
      	String req = request.toString();
      	
      	stmt = DBManagerServer.getInstance().executeQuery_WithMultipleAttempts(stmt, req);
      	resultSet = stmt != null ? stmt.getResultSet() : null; 
//      	Globals.logString(req);
//        resultSet = stmt.executeQuery(req);
        
      } catch (Exception e) {
        Globals.logException(e);
      }
    }
      
    try {
			if (resultSet != null && resultSet.next()) {
				count = resultSet.getInt(1);
				if(count == 0){
					exist = EXIST_NO;
				}else{
					exist = EXIST_YES;
				}
			}else{
				exist = EXIST_UNDETERMINED;
				count = -1;
			}
		} catch (SQLException e) {
			exist = EXIST_UNDETERMINED;
			Globals.logException(e);
		}
    
		DBManagerServer.getInstance().unlockStatement(stmt);
    return false;
  }
  
  public int getExist(){
  	return exist;
  }
  
  public int getCount(){
  	return count;
  }
}
