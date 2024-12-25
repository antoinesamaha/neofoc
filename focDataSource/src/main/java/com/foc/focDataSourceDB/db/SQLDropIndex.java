/*
 * Created on 27 feb. 2004
 */
package com.foc.focDataSourceDB.db;

import com.foc.db.DBIndex;
import com.foc.db.DBManager;

/**
 * @author 01Barmaja
 */
public class SQLDropIndex extends SQLRequest {
  DBIndex index = null;
  
  public SQLDropIndex(DBIndex index) {
    super(index.getFocDesc());
    this.index = index;
  }

  public boolean buildRequest() {
    request = new StringBuffer("");

    if (index != null && index.getFieldCount() > 0 && focDesc != null && focDesc.isPersistent()) {
      request.append("DROP INDEX ");
     
      if(DBManager.provider_TableNamesBetweenSpeachmarks(focDesc.getProvider())){
      	request.append("\""+index.getName()+"\"");
      }else{
      	request.append(index.getName());
      }
      
      if(focDesc.getProvider() != DBManager.PROVIDER_ORACLE && focDesc.getProvider() != DBManager.PROVIDER_POSTGRES){
        request.append(" ON ");
        request.append(focDesc.getStorageName_ForSQL());
      }
    }
    return false;
  }
}
