/*
 * Created on 27 feb. 2004
 */
package com.foc.focDataSourceDB.db;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;

import com.foc.Globals;
import com.foc.business.company.CompanyDesc;
import com.foc.db.DBManager;
import com.foc.db.SQLFilter;
import com.foc.db.SQLJoin;
import com.foc.desc.FocDesc;
import com.foc.desc.FocFieldEnum;
import com.foc.desc.FocObject;
import com.foc.desc.field.FBlobStringField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldPath;
import com.foc.desc.field.FObjectField121;
import com.foc.list.FocList;
import com.foc.list.FocListGroupBy;
import com.foc.property.FBlobProperty;
import com.foc.property.FBlobStringProperty;
import com.foc.property.FObject;
import com.foc.property.FProperty;
import com.foc.property.FSDOGeometryPoint;
import com.foc.util.Utils;

/**
 * @author 01Barmaja
 */
public class SQLSelect extends SQLSelectPlain {
	private ArrayList<FFieldPath> fieldsInSelect      = null;
	private ArrayList<FFieldPath> pathAccumulation    = null;
	private ArrayList<FFieldPath> fieldsToSetAsLoaded = null;//This array indicates fields that should be marked as loaded so that we do not reload upon the getFocProperty() in this fillPropertFromResults
	protected FocListGroupBy        sqlGroupBy          = null;
	
	public SQLSelect(FocList initialList, FocDesc focDesc, SQLFilter filter) {
		super(initialList, focDesc, filter);
		init();
	}

	public SQLSelect(FocObject focObject, FocDesc focDesc, SQLFilter filter) {
		super(focObject, focDesc, filter);
		init();
	}
	
	public void dispose(){
		super.dispose();
		
		sqlGroupBy = null;
		
		//We do not dispose the paths inside because they are disposed in pathAccumulation
		if(fieldsToSetAsLoaded != null){
			fieldsToSetAsLoaded.clear();
			fieldsToSetAsLoaded = null;
		}
		
		pathAccumulation = null;
		
		if(fieldsInSelect != null){
			for(int i=0; i<fieldsInSelect.size(); i++){
				if(fieldsInSelect.get(i) != null){
					fieldsInSelect.get(i).dispose();
				}
				fieldsInSelect.clear();
				fieldsInSelect = null;
			}
		}
	}
	
	private void init(){
		fieldsInSelect      = new ArrayList<FFieldPath>();
		pathAccumulation    = new ArrayList<FFieldPath>();
		fieldsToSetAsLoaded = new ArrayList<FFieldPath>();
	}
	
	private FFieldPath newFieldPathFromAccumulation(){
		int size = 0;
		for(int i=0; i<pathAccumulation.size(); i++){
			FFieldPath path = pathAccumulation.get(i);
			size += path.size();
		}
		FFieldPath newPath = new FFieldPath(size);

		int index = 0;
		for(int i=0; i<pathAccumulation.size(); i++){
			FFieldPath path = pathAccumulation.get(i);
			for(int j=0; j<path.size(); j++){
				int id = path.get(j);
				newPath.set(index++, id);
			}
		}
		return newPath;
	}
	
	protected void addTableFieldsToSelect(FocDesc focDesc, StringBuffer fieldsCommaSeparated, String tableAlias){
    FocFieldEnum enumer = focDesc.newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_PLAIN);
    while(enumer.hasNext()){
      FField focField = (FField) enumer.next();
      FFieldPath path = enumer.getFieldPath();
      if(focField != null){
        if(focField instanceof FObjectField121){
          pathAccumulation.add(path);
          FFieldPath newPath = newFieldPathFromAccumulation();
          if(isFieldInQuery(newPath)){
          	fieldsToSetAsLoaded.add(newPath);
	        	FObjectField121 obj121  = (FObjectField121) focField;
	        	if(tableAlias == null || tableAlias.isEmpty()) tableAlias = SQLJoin.MAIN_TABLE_ALIAS;
	        	SQLJoin newJoin = filter.getJoinMap().addJoin(new SQLJoin(focDesc.getProvider(), obj121.getFocDesc().getStorageName_ForSQL(), tableAlias, obj121.getDBName(), obj121.getFocDesc().getRefFieldName()));
	        	if(obj121.getFocDesc().isByCompany() && filter.isFilterByCompany()){
		        	//newJoin.setType(SQLJoin.JOIN_TYPE_RIGHT);
		        	String filterExpression = CompanyDesc.getCompanyFilter_IfNeeded(newJoin.getNewAlias()+"."+obj121.getFocDesc().getFieldByID(FField.FLD_COMPANY).getDBName());
		        	newJoin.setAdditionalWhere(filterExpression);
	        	}
	        	addTableFieldsToSelect(obj121.getFocDesc(), fieldsCommaSeparated, newJoin.getNewAlias());
          }
        	pathAccumulation.remove(path);
        }
      }
    }
		
    enumer = focDesc.newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_DB);
    while (enumer.hasNext()) {
      FField focField = (FField) enumer.next();
      FFieldPath path = enumer.getFieldPath();
      if(focField != null){
        pathAccumulation.add(path);
        FFieldPath newPath = newFieldPathFromAccumulation();
        if(isFieldInQuery(newPath)){
	        if(fieldsCommaSeparated.length() > 0){
	        	fieldsCommaSeparated.append(",");
	        }
	
	        String fieldNameString = enumer.getFieldCompleteName(focDesc);
	        if(tableAlias != null && !tableAlias.isEmpty()){
	        	fieldNameString = tableAlias + "." + fieldNameString;
	        }else{
	        	fieldNameString = FField.adaptFieldNameToProvider(focDesc.getProvider(), fieldNameString);
	        }
        	if(sqlGroupBy != null){
        		fieldNameString = sqlGroupBy.addFormulaToFieldName(path.get(0), fieldNameString);
        	}
        	
        	if(focField instanceof FBlobStringField && focDesc.getProvider() == DBManager.PROVIDER_MYSQL){
        		fieldNameString = "CONVERT("+fieldNameString+" USING utf8)";
        	}
	        fieldsCommaSeparated.append(fieldNameString);
	        fieldsInSelect.add(newFieldPathFromAccumulation());
        }	        
        pathAccumulation.remove(path);
      }
    }
	}
	
	@Override
  public boolean buildRequest(){
    request = new StringBuffer("SELECT ");
    StringBuffer fieldsCommaSeparated = new StringBuffer();
    boolean error = false;
    
    if (focDesc != null && focDesc.isPersistent()) {
      String tableAlias = filter.hasJoinMap() ? filter.getJoinMap().getMainTableAlias() : null;
      addTableFieldsToSelect(focDesc, fieldsCommaSeparated, tableAlias);
    
      request.append(fieldsCommaSeparated);
      
      addFrom();
      error = addWhere();
      if(sqlGroupBy != null){
      	request.append(" GROUP BY ("+sqlGroupBy.getGroupByExpression(focDesc)+")");
      }
      addOrderBy();
      addOffset();      
    }
    return error;
  }
  
	@Override
  protected void notifyPropertiesListenersForObject(FocObject obj, ArrayList<FProperty> arrayOfPropertiesModified){
  	
  	//This is useful because it does not call all listeners it only calls the listeners of the selected properties
  	/*
  	for(int i=0; i<arrayOfPropertiesModified.size(); i++){
  		FProperty p = arrayOfPropertiesModified.get(i);
  		if(p.getFocObject() != null){
	  		p.getFocObject().setDesactivateSubjectNotifications(true);
	  		if(p.getFocField() == null || p.getFocField().getID() != FField.MASTER_REF_FIELD_ID){
	  	    p.notifyListeners();
	  		}
	  		p.backup();
	      if(Globals.getApp().getRightsByLevel() != null){
	        Globals.getApp().getRightsByLevel().lockValuesIfNecessary(obj);                  
	      }
	  		p.getFocObject().setDesactivateSubjectNotifications(false);
  		}
  	}
  	*/

    if(obj != null){
      obj.setDesactivateSubjectNotifications(true);
      if(Globals.getApp().isWebServer()){
	  		for(int i=0; i<fieldsInSelect.size(); i++){
	    		FFieldPath path = fieldsInSelect.get(i);
	    		if(isFieldInQuery(path)){
	  	      FProperty prop = path.getPropertyFromObject(obj);
	  	      if (prop != null ) {
	  	      	prop.notifyListeners();
	  	      }
	    		}
	  		}
      }else{
        obj.scanPropertiesAndNotifyListeners();
      }
      
      obj.backup();
      if(Globals.getApp().getRightsByLevel() != null){
        Globals.getApp().getRightsByLevel().lockValuesIfNecessary(obj);                  
      }
      obj.setDesactivateSubjectNotifications(false);
    }
  }
	
	@Override
  protected void fillFieldsValuesFromResultSet(FocObject targetObject, ResultSet resultSet, ArrayList<FProperty> arrayOfPropertiesModified){
		if(!Globals.getApp().isWebServer()){
			//We should mark all 121 object fields as loaded otherwise they will trigger a select upon getFocProperty
			scanObjects121ToSetLoadedFromDB(targetObject);//We need to recall this for all list items after the select because the call to listeners after the select will trigger a setLoaded(false)
			//StringBuffer buffer = new StringBuffer();
		}
		
		//Begin 2010-05-18
		if(targetObject != null){
			targetObject.setLoadedFromDB(true);
		}
		//End 2010-05-18
		
		for(int i=0; i<fieldsInSelect.size(); i++){
  		FFieldPath path = fieldsInSelect.get(i);
  		if(isFieldInQuery(path)){
	      FProperty prop = path.getPropertyFromObject(targetObject);
	      if (prop != null ) {
	      	String value = null;
	      	try{
	      		if(			focDesc.getProvider() == DBManager.PROVIDER_ORACLE 
	      				&& 	(prop instanceof FBlobProperty || prop instanceof FBlobStringProperty)
	      				){
	      			Blob b = resultSet.getBlob(i+1);
	      			if(b != null){
	      				value = Utils.inputStreamToString(b.getBinaryStream());
	      			}else{
	      				value = "";
	      			}
	      		}else if(focDesc.getProvider() == DBManager.PROVIDER_ORACLE && prop instanceof FSDOGeometryPoint) {
	      			
	      		}else{
	      			value = resultSet.getString(i+1);
	      		}
	      	}catch(Exception e){
	      		value = "";
	      		Globals.logExceptionWithoutPopup(e);
	      	}
	      	
	        FocObject loadedFocObjForProperty = prop.getFocObject();
	        if(loadedFocObjForProperty != null){
	        	loadedFocObjForProperty.setDesactivateSubjectNotifications(true);
	      		//Begin 2010-05-18 
	        	//loadedFocObjForProperty.setLoadedFromDB(true);
	      		//End 2010-05-18
	        }
	        
      		//buffer.append(value);
      		//buffer.append(',');

//	        if(prop instanceof FSDOGeometryPoint) {
//	        	oracle.sql.STRUCT struct = resultSet.getObject(i+1);	        	
//	        } else {
	        	prop.setSqlString(value);
//	        }
	        
	        if(loadedFocObjForProperty != null){
	        	//Attention this is added after the FObject modif to make it require less memory
	        	/*if(path.get(path.size() - 1) == FField.REF_FIELD_ID){
	        		loadedFocObjForProperty.setReference(prop.getInteger());
	        	}*/
	        	//-----------------------
	        	//Begin 2010-05-18
	        	//loadedFocObjForProperty.resetStatus();
	        	//End 2010-05-18
	        	loadedFocObjForProperty.setDesactivateSubjectNotifications(false);          
	        }	        
	      }
  		}
  		//The section A was here before I moved it out of the loop
		}
  	//Begin 2010-05-18
		//SECTION A
		if(targetObject != null){
			if(fieldsInSelect == null){
				targetObject.resetStatus();
			}
			scanObjects121ToResetStatus(targetObject);
		}
		
  	//End 2010-05-18

		//Globals.logString(buffer);
  }

	@Override
  protected void afterTreatResultSet(){
		if(focList != null && fieldsToSetAsLoaded != null && fieldsToSetAsLoaded.size() > 0){
			//Here also we need to setLoaded(true) because the listeners call at the end of the treatResultsSet has reset the flag to false when setReference() of the object
	  	for(int i=0; i<focList.size(); i++){
	  		scanObjects121ToSetLoadedFromDB(focList.getFocObject(i));
	  	}
		}
  }

	private void scanObjects121ToSetLoadedFromDB(FocObject targetObject){
		//We should mark all 121 object fields as loaded otherwise they will trigger a select upon getFocProperty
  	for(int i=0; i<fieldsToSetAsLoaded.size(); i++){
  		FFieldPath path = fieldsToSetAsLoaded.get(i);
  		FObject prop = (FObject) path.getPropertyFromObject(targetObject);
  		FocObject obj = prop.getObject_CreateIfNeeded();
  		obj.setLoadedFromDB(true);
  		//obj.setReference(prop.getLocalReferenceInt());
  	}
	}

	private void scanObjects121ToResetStatus(FocObject targetObject){
		//We should mark all 121 object fields as loaded otherwise they will trigger a select upon getFocProperty
  	for(int i=0; i<fieldsToSetAsLoaded.size(); i++){
  		FFieldPath path = fieldsToSetAsLoaded.get(i);
  		FObject prop = (FObject) path.getPropertyFromObject(targetObject);
  		FocObject obj = prop.getObject_CreateIfNeeded();
  		obj.resetStatus();
  		//obj.setReference(prop.getLocalReferenceInt());
  	}
	}

	@Override
  protected int findIdentifierFieldPositionInMetaData(ResultSetMetaData meta){
  	int posInSelectForIdentifierField = -1;
		
		if(fieldsInSelect != null){
			for(int i=0; i<fieldsInSelect.size() && posInSelectForIdentifierField < 0; i++){
				FFieldPath path = fieldsInSelect.get(i);
				if(path != null && path.size() == 1 && path.get(0) == FField.REF_FIELD_ID){
					posInSelectForIdentifierField = i+1;
				}
			}
		}
		if(posInSelectForIdentifierField < 0){
			posInSelectForIdentifierField = super.findIdentifierFieldPositionInMetaData(meta);
		}
		return posInSelectForIdentifierField;
  }

	public FocListGroupBy getSqlGroupBy() {
		return sqlGroupBy;
	}

	public void setSqlGroupBy(FocListGroupBy sqlGroupBy) {
		this.sqlGroupBy = sqlGroupBy;
	}
}