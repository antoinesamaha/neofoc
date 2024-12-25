/*
 * Created on 27 feb. 2004
 */
package com.foc.focDataSourceDB.db;

import com.foc.db.DBIndex;
import com.foc.db.DBManager;
import com.foc.desc.field.FField;

/**
 * @author 01Barmaja
 */
public class SQLCreateIndex extends SQLRequest {
  DBIndex index = null;
  
  public SQLCreateIndex(DBIndex index) {
    super(index.getFocDesc());
    this.index = index;
  }

  public boolean buildRequest() {
    request = new StringBuffer("");

    if (index != null && index.getFieldCount() > 0 && focDesc != null && focDesc.isPersistent()) {
      request.append("CREATE ");
      if(index.isUnique()){
        request.append("UNIQUE ");
      }
      request.append("INDEX ");
      if(DBManager.provider_TableNamesBetweenSpeachmarks(focDesc.getProvider())){
      	request.append("\""+index.getName()+"\"");
      }else{
      	request.append(index.getName());
      }
      
      request.append(" ON ");
      request.append(SQLRequest.getNamespacePrefix(focDesc));
      if(DBManager.provider_TableNamesBetweenSpeachmarks(focDesc.getProvider())){
    		request.append("\""+focDesc.getStorageName_ForSQL()+"\"");
    	}else{
    		request.append(focDesc.getStorageName_ForSQL());
    	}
            
      boolean firstField = true;
      request.append("(");      
      for(int i=0; i<index.getFieldCount(); i++){
        int fieldID = index.getFieldAt(i);
        FField field = focDesc.getFieldByID(fieldID);
        if(field != null){
          if (!firstField) request.append(",");
          if(focDesc.getProvider() == DBManager.PROVIDER_MSSQL) request.append("[");
          if(DBManager.provider_FieldNamesBetweenSpeachmarks(focDesc.getProvider())) request.append("\"");
          request.append(field.getDBName());
          if(focDesc.getProvider() == DBManager.PROVIDER_MSSQL) request.append("]");
          if(DBManager.provider_FieldNamesBetweenSpeachmarks(focDesc.getProvider())) request.append("\"");
          firstField = false;
        }
        
      }
      request.append(")");
    }
    return false;
  }
}
