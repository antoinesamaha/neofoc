/*
 * Created on 27 feb. 2004
 */
package com.foc.focDataSourceDB.db;

import com.foc.Globals;
import com.foc.db.DBManager;
import com.foc.desc.FocDesc;
import com.foc.desc.FocFieldEnum;
import com.foc.desc.field.FField;
import com.foc.desc.field.FStringField;
import com.foc.util.Utils;

/**
 * @author 01Barmaja
 */
public class SQLCreateTable extends SQLRequest {

  public SQLCreateTable(FocDesc focDesc) {
    super(focDesc);
  }

  public boolean buildRequest() {
    request = new StringBuffer("");

    if (focDesc != null && focDesc.isPersistent()) {
      request.append("CREATE TABLE ");
      request.append(SQLRequest.getNamespacePrefix(focDesc));
      if(DBManager.provider_TableNamesBetweenSpeachmarks(focDesc.getProvider())) request.append("\"");
      request.append(focDesc.getStorageName_ForSQL());
      if(DBManager.provider_TableNamesBetweenSpeachmarks(focDesc.getProvider())) request.append("\" ");
      request.append("(");

      boolean firstField = true;
      boolean addPrimaryKeyConstraints = false;
      String  fieldNames_LobNeedEndDeclaration = null;

      FocFieldEnum enumer = focDesc.newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_DB);
      while (enumer.hasNext()) {
        FField focField = (FField) enumer.next();
        if (focField != null) {
          if (!firstField) request.append(",");
          
          boolean isTheREFField = enumer.getFieldCompleteName(focDesc).equals(focDesc.getRefFieldName());
          
          if(isTheREFField && focDesc.getProvider() == DBManager.PROVIDER_POSTGRES) {
        		request.append(" \"" + focDesc.getRefFieldName() + "\" SERIAL PRIMARY KEY ");
          } else {
	          request.append(focField.getCreationString(enumer.getFieldCompleteName(focDesc)));
	          
	          //Needed for Oracle CLOB fields only
	          //----------------------------------
	          if(			focDesc.getProvider() == DBManager.PROVIDER_ORACLE 
	          		&& 	focField instanceof FStringField
	          		&&  ((FStringField) focField).isClob()) {
	          	if(fieldNames_LobNeedEndDeclaration != null) fieldNames_LobNeedEndDeclaration += ",";
	          	else fieldNames_LobNeedEndDeclaration = "";
	         		fieldNames_LobNeedEndDeclaration += "\""+focField.getDBName()+"\"";  
	          }
	          //----------------------------------
	          
	          if (		focDesc.getProvider() != DBManager.PROVIDER_ORACLE 
	          		&& 	focDesc.getProvider() != DBManager.PROVIDER_MSSQL
	          		&& 	focDesc.getProvider() != DBManager.PROVIDER_POSTGRES
	          		){
	            request.append(" NOT NULL ");
	          }
	          if(enumer.getFieldCompleteName(focDesc).equals(focDesc.getRefFieldName())){
	          	if(focDesc.getProvider() == DBManager.PROVIDER_ORACLE){
	          		request.append(" PRIMARY KEY ");
	          	}else if(focDesc.getProvider() == DBManager.PROVIDER_MSSQL){
	          		request.append(" IDENTITY(1,1) PRIMARY KEY ");
	          	}else if(focDesc.getProvider() == DBManager.PROVIDER_H2){
	          		request.append(" AUTO_INCREMENT PRIMARY KEY ");
	          	}else if(focDesc.getProvider()== DBManager.PROVIDER_MYSQL){
	          		request.append("AUTO_INCREMENT ");
	          		addPrimaryKeyConstraints = true;
	          	}
	          }
          }
          firstField = false;
        }
      }
      if(addPrimaryKeyConstraints){
      	if(focDesc.getProvider()== DBManager.PROVIDER_MYSQL){
      		request.append(", UNIQUE INDEX "+Globals.getDBManager().getINDEX_NAME_IDENTIFIER()+" ("+FField.REF_FIELD_NAME+")");
      	}
      }
      request.append(")");
      
      if(!Utils.isStringEmpty(fieldNames_LobNeedEndDeclaration)) {
      	request.append(" LOB(");
      	request.append(fieldNames_LobNeedEndDeclaration);
      	request.append(") STORE AS SECUREFILE");
      }
    }
    return false;
  }
}
