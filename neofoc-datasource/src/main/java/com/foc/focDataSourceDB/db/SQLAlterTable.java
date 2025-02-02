/*
 * Created on 27 feb. 2004
 */
package com.foc.focDataSourceDB.db;

import java.util.Iterator;

import com.foc.Globals;
import com.foc.db.DBIndex;
import com.foc.db.DBManager;
import com.foc.desc.*;
import com.foc.desc.field.*;
import com.foc.util.Utils;

/**
 * @author 01Barmaja
 */
public class SQLAlterTable extends SQLRequest {

  final public static int ADD = 1;
  final public static int MODIFY = 2;
  final public static int DROP = 3;

  int id;
  int action;
  String fieldNameToDrop = "";
  String fieldToAlterName = "";
  FField fieldToAlter = null;

  public SQLAlterTable(FocDesc focDesc, FField fieldToAlter, String fieldToAlterName, int action) {
    super(focDesc);
    this.id = fieldToAlter != null ? fieldToAlter.getID() : 0;
    this.action = action;
    this.fieldToAlter = fieldToAlter;
    this.fieldToAlterName = fieldToAlterName; 
  }

  public SQLAlterTable(FocDesc focDesc, String fieldNameToDrop) {
    super(focDesc);
    this.fieldNameToDrop = fieldNameToDrop;
    this.action = DROP;
  }
  
  private void appendCreationString_AfterSpecificAdapt() {
		boolean appended = false;
		String fieldCreationString = fieldToAlter.getCreationString(fieldToAlterName);
		if(action == MODIFY && focDesc.getProvider() == DBManager.PROVIDER_POSTGRES) {
			if(!Utils.isStringEmpty(fieldCreationString)) {//Normally this is not empty
				int indexOfSecondSpeachMark = fieldCreationString.indexOf("\"", 2);
				if(indexOfSecondSpeachMark > 0 && indexOfSecondSpeachMark + 1 < fieldCreationString.length()) {
    			String columnType = fieldCreationString.substring(indexOfSecondSpeachMark + 1);
    			String columnName = fieldCreationString.substring(0, indexOfSecondSpeachMark + 1);
    			if(!Utils.isStringEmpty(columnType) && !Utils.isStringEmpty(columnName)) {
      			String result = " " + columnName + " TYPE " + columnType;
      			request.append(result);
      			appended = true;
    			}
				}
			}
		} 
		
		if(!appended){
			request.append(fieldCreationString);
		}
  }

  public boolean buildRequest() {
    request = new StringBuffer("");

    if (focDesc != null && focDesc.isPersistent()) {           
      request.append("ALTER TABLE ");
      request.append(SQLRequest.getNamespacePrefix(focDesc));
      if(DBManager.provider_TableNamesBetweenSpeachmarks(focDesc.getProvider())){
      	request.append("\""+focDesc.getStorageName_ForSQL()+"\"");
      }else{
      	request.append(focDesc.getStorageName_ForSQL());
      }
      request.append(" ");

      switch (action) {
      case ADD:
        request.append(" ADD ");
        break;
      case MODIFY:
      	if(   focDesc.getProvider() == DBManager.PROVIDER_MSSQL
      		 || focDesc.getProvider() == DBManager.PROVIDER_POSTGRES){
      		request.append(" ALTER COLUMN ");
      	}else{
      		request.append(" MODIFY ");
      	}
        break;
      case DROP:
        request.append(" DROP COLUMN ");
        if(DBManager.provider_FieldNamesBetweenSpeachmarks(focDesc.getProvider())){
        	request.append("\""+fieldNameToDrop+"\"");
        }else{
        	request.append(fieldNameToDrop);
        }
        break;
      }

      if (action != DROP) {
        if (fieldToAlter != null) {
        	//particular Case when 
        	if(fieldToAlter instanceof FReferenceField){
        		//request.append(((FReferenceField)fieldToAlter).getCreationString(focDesc.getProvider(), fieldToAlterName));
        		appendCreationString_AfterSpecificAdapt();
        	}else{
        		//Oracle CLOB needs
        		boolean oracleCLOB = 		focDesc.getProvider() == DBManager.PROVIDER_ORACLE 
								            		&& 	fieldToAlter instanceof FStringField
								            		&&  ((FStringField) fieldToAlter).isClob();
        		if(oracleCLOB) request.append(" (");
        		//-----------------
  
        		appendCreationString_AfterSpecificAdapt();
        		
        		//Oracle CLOB needs
            if(oracleCLOB) request.append(") LOB(\""+fieldToAlterName+"\") STORE AS SECUREFILE ");
            //-----------------
        	}
          if (		focDesc.getProvider() != DBManager.PROVIDER_ORACLE 
          		&& 	focDesc.getProvider() != DBManager.PROVIDER_MSSQL
          		&& 	focDesc.getProvider() != DBManager.PROVIDER_POSTGRES){
            request.append(" NOT NULL ");
          }
          
          //This part should be Object Oriented should not contain 'instanceof' 
          if(focDesc.getProvider() == DBManager.PROVIDER_MSSQL && action == ADD){
          	if(fieldToAlter instanceof FBoolField) request.append(" DEFAULT 0 ");
          	else if(fieldToAlter instanceof FBlobStringField) request.append(" DEFAULT (0x) ");
          	else if(fieldToAlter instanceof FObjectField) request.append(" DEFAULT 0 ");
          	else if(fieldToAlter instanceof FIntField) request.append(" DEFAULT 0 ");
          	else if(fieldToAlter instanceof FReferenceField) request.append(" DEFAULT 0 ");
          	else if(fieldToAlter instanceof FStringField) request.append(" DEFAULT '' ");
          	else if(fieldToAlter instanceof FDateTimeField) request.append(" DEFAULT CONVERT(DATETIME2, '1970-01-01 00:00:00.00000000');");
          }
          //--------------------------------------------------------------------
          
        	//BAntoineS - AUTOINCREMENT
          if(fieldToAlterName.equals(focDesc.getRefFieldName()) && focDesc.getProvider()== DBManager.PROVIDER_MYSQL){
            request.append("AUTO_INCREMENT ");
            //Check if unique index exists otherwise create it in this same request
            boolean identifierIndexExists = false;
            SQLTableIndexesDetails indexesDetails = new SQLTableIndexesDetails(focDesc);
            Iterator iter = indexesDetails.iterator();
            while(iter != null && iter.hasNext() && !identifierIndexExists){
              DBIndex index = (DBIndex) iter.next();
             	identifierIndexExists = index != null && index.getName().equals(Globals.getDBManager().getINDEX_NAME_IDENTIFIER());   	
            }
            if(!identifierIndexExists){
            	request.append(", ADD UNIQUE INDEX "+Globals.getDBManager().getINDEX_NAME_IDENTIFIER()+" ("+FField.REF_FIELD_NAME+")");
            }
          }
          //EAntoineS - AUTOINCREMENT
        }
      }
    }
    return false;
  }
}
