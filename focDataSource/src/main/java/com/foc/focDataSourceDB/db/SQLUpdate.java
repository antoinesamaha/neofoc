/*
 * Created on 27 fevr. 2004
 */
package com.foc.focDataSourceDB.db;

import com.foc.db.DBManager;
import com.foc.db.SQLFilter;
import com.foc.desc.FocDesc;
import com.foc.desc.FocFieldEnum;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldPath;
import com.foc.focDataSourceDB.db.util.DBUtil;
import com.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
public class SQLUpdate extends SQLRequest {
  private FocObject focObj = null;

  public SQLUpdate(FocDesc focDesc, FocObject focObj) {
    super(focDesc);
    this.focObj = focObj;
    filter = new SQLFilter(focObj, SQLFilter.FILTER_ON_IDENTIFIER);
  }
  
  public SQLUpdate(FocDesc focDesc, FocObject focObj, SQLFilter filter){
  	super(focDesc, filter);
  	this.focObj = focObj;
  }
  
  @Override
  public boolean isFieldInQuery(int fieldID){
  	boolean inQuery = super.isFieldInQuery(fieldID);
  	if(inQuery && getQueryFieldList(false) == null){
  		
  	}
  	return inQuery;
  }

  @Override
  protected FocObject getFocObject(){
    return this.focObj; 
  }

  public boolean buildRequest() {
    boolean error = false;
    request = null;

    if (focDesc != null && focDesc.isPersistent()) {
      boolean firstField = true;
      boolean atLeastOneFieldOtherThanRef = false;
      
      FocFieldEnum enumer = focObj.newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_DB);
      while (enumer.hasNext()) {   
        FField focField = (FField) enumer.next();
        FFieldPath path = enumer.getFieldPath();
        if(isFieldInQuery(path)){
          FProperty prop      = enumer.getProperty();                
          FProperty firstProp = path.getPropertyFromObject(focObj, 0);  
             
          if(prop != null){
            //If the property is reference I need to make sure the ref is not Temp
          	//We are not relying on this assignReferenceIfNeeded because there is a code that calls the commitStatusToDatabaseWithPropagation
          	//in the commitStatusToDatabase of this same father FocObject.
            if(prop.getFocField() != null && prop.getFocField().getID() == FField.REF_FIELD_ID){
              FocObject propFocObj = prop.getFocObject();
              DBUtil.focObject_AssignReferenceIfNeeded(propFocObj, false);
            }
            
            if (focField != null) {
            	if(getQueryFieldList(false) != null || firstProp.isModifiedFlag()){
	              if(		 focField.getID() != FField.REF_FIELD_ID 
	              		|| path.size() > 1 
	              		|| focDesc.getProvider() != DBManager.PROVIDER_MSSQL){
		              if (!firstField) {
		                request.append(",");
		              } else {
		              	request = new StringBuffer("");
	                  request.append("UPDATE ");
	                  String namespacePrefix = SQLUpdate.getNamespacePrefix(focDesc);
	                	if(DBManager.provider_TableNamesBetweenSpeachmarks(focDesc.getProvider())){
	                		request.append(namespacePrefix+"\""+focDesc.getStorageName_ForSQL()+"\"");
	                	}else{
	                		request.append(namespacePrefix+focDesc.getStorageName_ForSQL());	              	
	                	}
		                request.append(" SET ");
		              }
	              	
	              	String fldName = enumer.getFieldCompleteName(focDesc);
	              	if(focDesc.getProvider() == DBManager.PROVIDER_MSSQL) fldName = "["+fldName+"]" ; 
	              	if(DBManager.provider_FieldNamesBetweenSpeachmarks(focDesc.getProvider())) fldName = "\""+fldName+"\"" ;
		              request.append(fldName + "=");
		              request.append(prop.getSqlString());
		              firstProp.setModifiedFlag(false);//2017-05-31
		              firstField = false;
	              }
	              
	              if(focField.getID() != FField.REF_FIELD_ID || path.size() > 1){
	              	atLeastOneFieldOtherThanRef = true;
	              }
            	}              
            }
          }
        }
      }
      
      if(atLeastOneFieldOtherThanRef){
      	error = addWhere();//firstField || addWhere()
      }else{
      	request = null;
      }

//      error = !atLeastOneFieldOtherThanRef || addWhere();//firstField || addWhere()
    }
    return error;
  }

  @Override
  protected int getSQLRequestType() {
    return TYPE_UPDATE;
  }
}
