/*
 * Created on 27 feb. 2004
 */
package com.foc.focDataSourceDB.db;

import com.foc.Globals;
import com.foc.db.DBManager;
import com.foc.desc.FocDesc;
import com.foc.desc.FocFieldEnum;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.focDataSourceDB.db.util.DBUtil;
import com.foc.property.FProperty;
import com.foc.property.FReference;

/**
 * @author 01Barmaja
 */
public class SQLInsert extends SQLRequest {
  private FocObject focObj = null;

  public SQLInsert(FocDesc focDesc, FocObject focObj) {
    super(focDesc);
    this.focObj = focObj;
  }

  @Override
  protected FocObject getFocObject(){
  	return this.focObj; 
  }

  //BAntoineS - AUTOINCREMENT
  private boolean excludeFieldFromInsert(String fieldName){
  	return fieldName.equals(focDesc.getRefFieldName()) && 
  			(  focDesc.getProvider() == DBManager.PROVIDER_MYSQL
  			|| focDesc.getProvider() == DBManager.PROVIDER_MSSQL
  			|| focDesc.getProvider() == DBManager.PROVIDER_POSTGRES
  			|| focDesc.getProvider() == DBManager.PROVIDER_H2
  			) ;
  }
  //EAntoineS - AUTOINCREMENT
  
  public boolean buildRequest() {
    request = new StringBuffer("");

    if (focDesc != null && focDesc.isPersistent()) {
    	DBUtil.focObject_AssignReferenceIfNeeded(focObj, true);//Usefull for Oracle only.
      boolean firstField = true;
      request.append("INSERT INTO ");
      request.append(SQLRequest.getNamespacePrefix(focDesc));
    	if(DBManager.provider_TableNamesBetweenSpeachmarks(focDesc.getProvider())){
    		request.append("\""+focDesc.getStorageName_ForSQL()+"\"");
    	}else{
    		request.append(focDesc.getStorageName_ForSQL());
    	}
      request.append(" (");
      
      FocFieldEnum enumer = focObj.newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_DB);
      while (enumer.hasNext()) {
        
        FField focField = (FField) enumer.next();
        //BAntoineS - AUTOINCREMENT
        if (focField != null && !excludeFieldFromInsert(enumer.getFieldCompleteName(focDesc))) {
        //EAntoineS - AUTOINCREMENT
          if (!firstField) {
            request.append(",");
          }
          String fieldCompleteName = enumer.getFieldCompleteName(focDesc);
          if(focDesc.getProvider() == DBManager.PROVIDER_MSSQL){
          	fieldCompleteName = "["+fieldCompleteName+"]" ;
          }else if(DBManager.provider_FieldNamesBetweenSpeachmarks(focDesc.getProvider())){
          	fieldCompleteName = "\""+fieldCompleteName+"\"" ;
          }
          request.append(fieldCompleteName);
          firstField = false;
        }
      }
      request.append(") VALUES (");
      firstField = true;

      int index = 0;
      enumer.reset();
      while (enumer.hasNext()) {
        FField focField = (FField) enumer.next();
        //BAntoineS - AUTOINCREMENT
        if (focField != null && !excludeFieldFromInsert(enumer.getFieldCompleteName(focDesc))) {
        //EAntoineS - AUTOINCREMENT
        	index++;
        	
          int       id   = focField.getID();
          FProperty prop = enumer.getProperty();          
          
          if(prop == null){
          	Globals.logString("Property null : "+enumer.getFieldCompleteName(focDesc)+" ID="+id);
            prop = enumer.getProperty();
          //}else if(prop.getFocField() == null){
          	//Globals.logString("Field null : "+id);
          }
                    
          //If the property is reference I need to make sure the ref is not Temp
        	//We are not relying on this assignReferenceIfNeeded because there is a code that calls the commitStatusToDatabaseWithPropagation
        	//in the commitStatusToDatabase of this same father FocObject.
          if(prop.getFocField() != null && prop.getFocField().getID() == FField.REF_FIELD_ID){
            FocObject propFocObj = prop.getFocObject();
            
            if(propFocObj != null){
              DBUtil.focObject_AssignReferenceIfNeeded(propFocObj, false);
            }
          }
          if (!firstField) {
            request.append(",");
          }

          try {
          	request.append(prop.getSqlString());
          } catch (Exception e) {
            if (prop == null) {
              Globals.logString("prop null for " + focDesc.getFocObjectClass() + " id = " + id);
            }
            Globals.logException(e);
          }

          firstField = false;
        }
      }
      request.append(")");
    }
    return false;
  }

  @Override
  protected int getSQLRequestType() {
    return TYPE_INSERT;
  }
  
  //BAntoineS - AUTOINCREMENT
  /*
  protected void executeUpdate(Statement stmt, String req) throws Exception{
  	if(Globals.getDBManager().getProvider() == DBManager.PROVIDER_MYSQL){
	  	stmt.executeUpdate(req, Statement.RETURN_GENERATED_KEYS);
	  	ResultSet rs = stmt.getGeneratedKeys();
	  	while(rs.next()){
	  		int ref = rs.getInt(1);
	  		//BAntoine - We need to remove from the FocList realReference MAP the temp ref and put the real one.
	  		FocList focList = null;
	  		Object fatherSubject = focObj.getFatherSubject();
	  		if(fatherSubject != null && fatherSubject instanceof FocList){
	  			focList = (FocList) fatherSubject;
	  			focList.elementHash_RemoveFromReferencesMap(focObj);
	  		}
	  		//EAntoine
	  		
	  		focObj.setReference(ref);
	  		
	  		//BAntoine - We need to remove from the FocList realReference MAP the temp ref and put the real one.
	  		if(focList != null){
	  			focList.elementHash_AddToReferencesMap(focObj);
	  		}
	  	  //EAntoine
	  	}
  	}else if(Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE){
  		stmt.executeUpdate(req);
  	}
  	
  	//Firing an event to the notification manager to be able to send the corresponding notifications.
  	FocNotificationManager.getInstance().fireEvent(FocEventNotifierConst.EVT_TABLE_ADD, focObj);
  }
  */
  //EAntoineS - AUTOINCREMENT
}