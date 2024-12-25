/*******************************************************************************
 * Copyright 2016 Antoine Nicolas SAMAHA
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.foc.focDataSourceDB;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import com.foc.Application;
import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.admin.AdminModule;
import com.foc.admin.FocUser;
import com.foc.admin.FocUserDesc;
import com.foc.admin.FocVersion;
import com.foc.business.notifier.FocNotificationManager;
import com.foc.business.workflow.implementation.ILoggable;
import com.foc.business.workflow.implementation.Loggable;
import com.foc.business.workflow.implementation.LoggableChangeCumulator;
import com.foc.business.workflow.implementation.WFLogDesc;
import com.foc.dataSource.IExecuteResultSet;
import com.foc.dataSource.IFocDataSource;
import com.foc.dataSource.IFocDataUtil;
import com.foc.db.DBManager;
import com.foc.db.SQLFilter;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocFieldEnum;
import com.foc.desc.FocObject;
import com.foc.desc.ReferenceChecker;
import com.foc.desc.ReferenceCheckerDelete;
import com.foc.desc.ReferenceCheckerToPutToZero;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldPath;
import com.foc.desc.field.FListField;
import com.foc.desc.field.FObjectField;
import com.foc.desc.field.FObjectField121;
import com.foc.desc.field.FStringField;
import com.foc.event.FocEvent;
import com.foc.focDataSourceDB.db.DBManagerServer;
import com.foc.focDataSourceDB.db.SQLDelete;
import com.foc.focDataSourceDB.db.SQLInsert;
import com.foc.focDataSourceDB.db.SQLRequest;
import com.foc.focDataSourceDB.db.SQLSelect;
import com.foc.focDataSourceDB.db.SQLSelectDistinct;
import com.foc.focDataSourceDB.db.SQLSelectExistance;
import com.foc.focDataSourceDB.db.SQLSelectFields;
import com.foc.focDataSourceDB.db.SQLSelectFindReferenceForUniqueKey;
import com.foc.focDataSourceDB.db.SQLSelectFindReferenceForWhereExpression;
import com.foc.focDataSourceDB.db.SQLSelectJoinCount;
import com.foc.focDataSourceDB.db.SQLSelectJoinRequest;
import com.foc.focDataSourceDB.db.SQLUpdate;
import com.foc.focDataSourceDB.db.ScriptRunner;
import com.foc.focDataSourceDB.db.adaptor.DBAdaptor;
import com.foc.focDataSourceDB.db.connectionPooling.ConnectionPool;
import com.foc.focDataSourceDB.db.connectionPooling.StatementWrapper;
import com.foc.focDataSourceDB.db.util.DBUtil;
import com.foc.list.FocLinkJoinRequest;
import com.foc.list.FocList;
import com.foc.property.FDate;
import com.foc.property.FList;
import com.foc.property.FObject;
import com.foc.property.FObject121;
import com.foc.property.FProperty;
import com.foc.property.FTypedObject;
import com.foc.shared.json.B01JsonBuilder;
import com.foc.util.Utils;
import com.vaadin.server.ClassResource;

public class FocDataSource_DB implements IFocDataSource {

	private Application     app       = null;
	private DBAdaptor       dbAdaptor = null; 
	private DBManagerServer dbManagerServer = null;
	private DBUtil          dbUtil    = null;
	private int             emptyDatabaseJustCreated = DB_STATUS_NOT_CHECKED_YET;

	public static final String SP_DELIMITER  = "DELIMITER";
	public static final int DB_STATUS_EXIST           =  1;
	public static final int DB_STATUS_NOT_CHECKED_YET =  0;
	public static final int DB_STATUS_DOES_NOT_EXIST  = -1;
	//See MigrationFileReader
	//
	
	public FocDataSource_DB(Application app){
		this.app = app;
		dbManagerServer = new DBManagerServer();
	}
	
	public void dispose(){
		if(dbManagerServer != null){
			dbManagerServer.dispose();
			dbManagerServer = null;
		}
		app = null;
	}
	
	private DBAdaptor getDBAdaptor(){
		if(dbAdaptor == null){
			dbAdaptor = new DBAdaptor(this);
		}
		return dbAdaptor;
	}
	
	@Override
	public boolean isServer() {
		return true;
	}

	//-----------------------------------------------------
	//-----------------------------------------------------
	// focObject_Load
	//-----------------------------------------------------
	//-----------------------------------------------------
	
	@Override
	public boolean focObject_Load(FocObject focObject) {
		boolean error = focObject == null;
		
		if(!error){
			focObject.setLoadedFromDB(true);
	    DBManager dbManager = Globals.getDBManager();
	    if (dbManager != null) {
	      SQLFilter filter = new SQLFilter(focObject, SQLFilter.FILTER_ON_IDENTIFIER);
	      filter.setFilterByCompany(!focObject.isShared());
	
	      FocDesc focDesc = focObject.getThisFocDesc();
	      if (focDesc != null) {
	        SQLSelect sqlSelect = new SQLSelect(focObject, focDesc, filter);
	        if(sqlSelect.execute()){
	        	focObject.setLoadedFromDB(false);
	          error = false;
	        }
	      }
	    }
	    
	    if(Globals.getApp().getRightsByLevel() != null){
	      Globals.getApp().getRightsByLevel().lockValuesIfNecessary(focObject);
	    }
	    focObject.setModified(false);
		}
		
		return error;
	}
	
	//-----------------------------------------------------
	//-----------------------------------------------------
	// focObject_Delete
	//-----------------------------------------------------
	//-----------------------------------------------------

	@Override
	public boolean focObject_Delete(FocObject focObject, ReferenceChecker referenceCjeckerToIgnore) {
		ArrayList<ReferenceCheckerToPutToZero> arrayPutToZero = new ArrayList<ReferenceCheckerToPutToZero>();
		ArrayList<ReferenceCheckerDelete>      arrayToDelete  = new ArrayList<ReferenceCheckerDelete>();
		
    boolean error = !focObject_CanDelete(focObject, referenceCjeckerToIgnore, arrayPutToZero, arrayToDelete);

    FocDesc focDesc = focObject.getThisFocDesc();
    if(!error && focDesc != null && !focDesc.isLogicalDeleteEnabled()){
      FocFieldEnum fieldsIterator = focDesc.newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_PLAIN);
      while (!error && fieldsIterator.hasNext()) {
        FField focField = (FField) fieldsIterator.next();
  
        if(focField.isDBResident() && focField instanceof FObjectField121){
        	FObjectField121 field121  = (FObjectField121) focField;
          FFieldPath      fieldPath = fieldsIterator.getFieldPath();
          FObject121 obj = (FObject121) fieldPath.getPropertyFromObject(focObject);
          if(obj.getLocalReferenceInt() > 0){
          	error = !(obj.getObject_CreateIfNeeded().delete(field121.getReferenceCheckerAdater()));
          }
        }
      }
    }

    if(focObject.hasRealReference() && focDesc != null && !focDesc.isLogicalDeleteEnabled()){ // this will delete the child elements from object fields -- not to be done if logical
	    if(!error){
	    	FocFieldEnum fieldsIterator = focDesc.newFocFieldEnum(FocFieldEnum.CAT_LIST, FocFieldEnum.LEVEL_PLAIN);
	      fieldsIterator.reverseOrder();
	      while (fieldsIterator.hasNext()) {
	        FListField focField = (FListField) fieldsIterator.next();
	  
	        if (focField.isDBResident() && focField.isDeleteListWhenMasterDeleted()) {
	          FFieldPath fieldPath = fieldsIterator.getFieldPath();
	          FList list = (FList) fieldPath.getPropertyFromObject(focObject);
	          FocList focListToDelete = list.getList();
	          focListToDelete.deleteFromDB();
	        }
	      }
	    }
    }
    
    if(!error){
    	//BAntoineS - 20151206 - Added this because we used to do nothing with putToZero!
    	for(int i=0; i<arrayPutToZero.size(); i++){
  			ReferenceCheckerToPutToZero toPutToZero = arrayPutToZero.get(i);
  			toPutToZero.getReferenceChecker().redirectReferencesToNewFocObject(toPutToZero.getObjectToRedirecctFrom(), null);
  		}
    	//EAntoineS - 20151206 - Added this because we used to do nothing with putToZero!
      //BAntoineS - 20151206
    	for(int i=0; i<arrayToDelete.size(); i++){
  			ReferenceCheckerDelete toDelete = arrayToDelete.get(i);
  			if(toDelete != null){
  				StringBuffer sql = toDelete.buildDeleteRequest();
  				command_ExecuteRequest(sql);
  			}
  		}
    	//EAntoineS
    	
    	focObject_DB_Delete(focObject, referenceCjeckerToIgnore);
    	focObject.fireEvent(FocEvent.ID_DELETE);      
    }

    if(arrayPutToZero != null){
	    for(int i=0; i<arrayPutToZero.size(); i++){
	    	arrayPutToZero.get(i).dispose();
	    }
	    arrayPutToZero.clear();
	    arrayPutToZero = null;
    }

    if(arrayToDelete != null){
	    for(int i=0; i<arrayToDelete.size(); i++){
	    	arrayToDelete.get(i).dispose();
	    }
	    arrayToDelete.clear();
	    arrayToDelete = null;
    }

    return error;
	}

  private boolean focObject_CanDelete(FocObject focObject, ReferenceChecker referenceCjeckerToIgnore, ArrayList<ReferenceCheckerToPutToZero> arrayPutToZero, ArrayList<ReferenceCheckerDelete> arrayDelete){
  	boolean can = false;
  	if(Globals.getDBManager() != null && focObject.isDeletable() && focObject.sync_AllowObjectDBModification()){
	    FocDesc focDesc = focObject.getThisFocDesc();
	    if(focDesc != null){
		    if(!focDesc.getWithReference() || focObject.hasRealReference()){
		      StringBuffer message = new StringBuffer();
		      can = focObject.referenceCheck_GetNumber(message, true, referenceCjeckerToIgnore, arrayPutToZero, arrayDelete) == 0;
		    }else{
		    	can = true;
		    }
		    if(can){
		    	FocFieldEnum fieldsIterator = focDesc.newFocFieldEnum(FocFieldEnum.CAT_LIST, FocFieldEnum.LEVEL_PLAIN);
		      while(fieldsIterator.hasNext() && can) {
		        FListField focField = (FListField) fieldsIterator.next();
		  
		        if(focField.isDBResident() && focField.isDeleteListWhenMasterDeleted()){
		          FFieldPath fieldPath       = fieldsIterator.getFieldPath();
		          FList      list            = (FList) fieldPath.getPropertyFromObject(focObject);
		          FocList    focListToDelete = list.getList();
		          can = canDeleteDB(focListToDelete, arrayPutToZero, arrayDelete);
		        }
		      }
		    }
	    }
  	}
    return can;
  }

	private boolean focObject_DB_Delete(FocObject focObject, ReferenceChecker refCheck) {
    boolean successfull = false;
    
    ArrayList<ReferenceCheckerToPutToZero> arrayPutToZero = new ArrayList<ReferenceCheckerToPutToZero>();
    ArrayList<ReferenceCheckerDelete> arrayToDelete  = new ArrayList<ReferenceCheckerDelete>();
    if(focObject_CanDelete(focObject, refCheck, arrayPutToZero, arrayToDelete)){
    	if(!focObject.getThisFocDesc().getWithReference() || !focObject.hasRealReference()){
    		successfull = true;
    	}else{
    		for(int i=0; i<arrayPutToZero.size(); i++){
    			ReferenceCheckerToPutToZero toPutToZero = arrayPutToZero.get(i);
    			toPutToZero.getReferenceChecker().redirectReferencesToNewFocObject(toPutToZero.getObjectToRedirecctFrom(), null);
    		}
    		for(int i=0; i<arrayToDelete.size(); i++){
    			ReferenceCheckerDelete toDelete = arrayToDelete.get(i);
    			if(toDelete != null){
    				StringBuffer sql = toDelete.buildDeleteRequest();
    				command_ExecuteRequest(sql);
    			}
    		}
    		
    		successfull = !focObject_DB_Delete_AtomicNoCheck(focObject);
    	}
    }

    if(successfull){
    	focObject.setDeletionExecuted(true);
    }

    if(arrayPutToZero != null){
    	for(int i=0; i<arrayPutToZero.size(); i++){
    		arrayPutToZero.get(i).dispose();
    	}
    	arrayPutToZero.clear();
    	arrayPutToZero = null;
    }
    if(arrayToDelete != null){
    	for(int i=0; i<arrayToDelete.size(); i++){
    		arrayToDelete.get(i).dispose();
    	}
    	arrayToDelete.clear();
    	arrayToDelete = null;
    }
    
    
		return !successfull;
	}

	private boolean focObject_DB_Delete_AtomicNoCheck(FocObject focObject) {
		boolean successfull = false;
		boolean logicalDelete = focObject != null && focObject.getThisFocDesc() != null ? focObject.getThisFocDesc().isLogicalDeleteEnabled() : false;
		if(logicalDelete) {
			SQLUpdate sqlUpdate = new SQLUpdate(focObject.getThisFocDesc(), focObject);
      sqlUpdate.addQueryField(FField.FLD_LOGICAL_DELETE);
      sqlUpdate.addQueryField(FField.FLD_LOGICAL_DELETE_DATE);
      sqlUpdate.addQueryField(FField.FLD_LOGICAL_DELETE_USER);
      try{
      	focObject.setLogicalDeleted(true);
				sqlUpdate.execute();
				successfull = true;
			}catch (Exception e){
				Globals.logException(e);
			}
		} else {
			SQLDelete sqlDelete = new SQLDelete(focObject);
		  try{
		  	sqlDelete.execute();
		  	successfull = true;
		  }catch(Exception e){
		  	Globals.logException(e);
		  	successfull = false;
		  }
		}
	  return !successfull;
	}
	
	//-----------------------------------------------------
	//-----------------------------------------------------
	// focObject_Save
	//-----------------------------------------------------
	//-----------------------------------------------------

	@Override
	public boolean focObject_Save(FocObject focObject, int fieldsArray[]) {
		boolean error = false;

		FocNotificationManager.getInstance().threadCumulatingEvents_AddThread();
		
		if(isEmptyDatabaseJustCreated() && Globals.getApp().isWebServer()){
			Globals.getIFocNotification().setNotificationsEnabled(false);
		}
		/*
    for(int i = 0; i < focObject.propertiesArray_Size(); i++){
      FProperty prop = focObject.propertiesArray_Get(i);
      if(prop != null && prop instanceof FObject){
        FocObject obj = (FocObject)prop.getObject();
        if(obj != null && obj.isCreated() && prop.getFocField() != null && prop.getFocField().isDBResident()){
        	//should not get here because the object must have been validated 
        	//upon commitStatusToDataBase() when we have iterate the properties 
        	//and invoked commitStatusToDataBaseWithPropagation() on the focObjects
          obj.save();
        }
      }
    }
    */
    if (focObject.isCreated()) {
      
    	dbInsert(focObject);
    	focObject.setCreated(false);// Is useless if the command is comming from the Access
                        // Subject Interface
    	if(focObject.getThisFocDesc() != null) focObject.getThisFocDesc().resetStatusForFocObject(focObject);
    } else if (focObject.isModified()) {
    	dbUpdate(focObject);
    	focObject.setModified(false);// Is useless if the command is comming from the
                          // Access Subject Interface
    	if(focObject.getThisFocDesc() != null) focObject.getThisFocDesc().resetStatusForFocObject(focObject);
    }
    
    FocNotificationManager.getInstance().threadCumulatingEvents_FireEventsCumulated();
    
		return error;
	}
	
  private boolean dbInsert(FocObject focObject) {
  	boolean error = true;
    DBManager dbManager = Globals.getDBManager();
    if(dbManager != null){
      FocDesc focDesc = focObject.getThisFocDesc();
      Globals.setMouseComputing(true);
      if(focDesc != null){
//      	if(focObject.workflow_IsWorkflowSubject() && ((IWorkflow)focObject).iWorkflow_getWorkflow() != null){
//      		((IWorkflow)focObject).iWorkflow_getWorkflow().addLogLine();
//      	}
      	if(Globals.getDBManager().sync_isRemote()){
      		focObject.sync_SetNew(true);
      	}
        SQLInsert sqlInsert = new SQLInsert(focDesc, focObject);
        try{
        	error = sqlInsert.execute();
					if(!error && focObject.workflow_IsLoggable()){
						Loggable workflow = ((ILoggable)focObject).iWorkflow_getWorkflow();
						if(workflow != null) {
							String json = null;
							B01JsonBuilder builder = new B01JsonBuilder();
							try {
								builder.setModifiedOnly(false);
								builder.setPrintObjectNamesNotRefs(true);
								builder.setScanSubList(true);
								builder.setPrintRootRef(true);
								focObject.toJson(builder);
								json = builder.toString();
								if(json.length() >= WFLogDesc.LEN_FLD_CHANGES) {
									json = json.substring(0, WFLogDesc.LEN_FLD_CHANGES-1);
								}
							} catch(Exception e) {
								Globals.logException(e);
							}
							builder.dispose();
							
							workflow.insertLogLine(WFLogDesc.EVENT_CREATION, null, json);
						}
					}        	
				}catch (Exception e){
					error = true;
					Globals.logException(e);
				}
      }
      Globals.setMouseComputing(false);
    }
    if(Globals.getApp().isWebServer() && Globals.getIFocNotification() != null) Globals.getIFocNotification().setNotificationsEnabled(true);
    return error;
  }

  private boolean dbUpdate(FocObject focObject, int queryFields[]) {
  	boolean error = true;
    DBManager dbManager = Globals.getDBManager();
    if(dbManager != null){
    	Globals.setMouseComputing(true);
      FocDesc focDesc = focObject.getThisFocDesc();

      if(focDesc != null && focObject.sync_AllowObjectDBModification()){
        SQLUpdate sqlUpdate = new SQLUpdate(focDesc, focObject);
        if(queryFields != null){
        	for(int i=0; i<queryFields.length; i++){
        		sqlUpdate.addQueryField(queryFields[i]);	
        	}
        }
        try{
        	Loggable loggable = null;
        	String json = null;
        	
					if(focObject.workflow_IsLoggable()){
						loggable = ((ILoggable)focObject).iWorkflow_getWorkflow();
						if(loggable != null) {
				  		LoggableChangeCumulator logger = LoggableChangeCumulator.getInstanceForThread();
				  		json = logger.getJson(focObject);
						}
					}
        	
					error = sqlUpdate.execute();
					
					if(!error && loggable != null) {
						LoggableChangeCumulator logger = LoggableChangeCumulator.getInstanceForThread();
						if(logger != null) {
							logger.insertLogLine_IfNotInsertedYet((ILoggable)focObject);
						}
					}
				}catch (Exception e){
					error = true;
					Globals.logException(e);
				}
      }
    	Globals.setMouseComputing(false);
    }
    return error;
  }
  
  /*
  private boolean dbUpdate(FocObject focObject, boolean forLockFieldsOnly) {
  	int fieldsArray[] = null;
  	if(forLockFieldsOnly){
  		fieldsArray = new int[1];
	  	fieldsArray[0] = FField.LOCK_USER_FIELD_ID;
  	}
  	return dbUpdate(focObject, fieldsArray);
  }
  */

  private void postUpdate(FocObject focObject){
    FocFieldEnum enumer = focObject.newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_DB);
    while (enumer.hasNext()) {   
      FField focField = (FField) enumer.next();
      FProperty prop = focObject.getFocProperty(focField.getID());
      if(!focField.isIncludeInDBRequests() && prop.isModifiedFlag()){
        SQLUpdate sqlUpdate = new SQLUpdate(focObject.getThisFocDesc(), focObject);
        sqlUpdate.addQueryField(focField.getID());
        try{
					sqlUpdate.execute();
	        prop.setModifiedFlag(false);					
				}catch (Exception e){
					Globals.logException(e);
				}
      }
    }
  }
  
  private void dbUpdate(FocObject focObject) {
    dbUpdate(focObject, null);
    postUpdate(focObject);
  }

	//-----------------------------------------------------
	//-----------------------------------------------------
	// focList_Load
	//-----------------------------------------------------
	//-----------------------------------------------------
  
	@Override
	public int focList_Join_Count(FocList focList, String fieldInsideCount) {
		int count = 0;
		
  	FocLinkJoinRequest link   = (FocLinkJoinRequest) focList.getFocLink();
  	SQLSelectJoinCount select = new SQLSelectJoinCount(focList, link.getRequestDesc(), focList.getFilter(), fieldInsideCount); 

    boolean error = !select.execute();
    count = select.getCount();
		
		return count;
	}
  
	@Override
	public int focList_Count(FocList focList, String fieldInsideCount) {
		int count = 0;
		
		return count;
	}
	
  @Override
  public boolean focList_Load(FocList focList, long refToBeReloaded){
  	boolean error = true;
  	if(focList != null && !isEmptyDatabaseJustCreated()){
  		FocDesc slaveDesc = focList.getFocDesc();
		  focList.setLoaded(true);
		  
		  //We remove first
		  //this way if the incremental where condition was stuck from before
		  //it is removed
		  if(focList.getFilter() != null) {
			  focList.getFilter().removeAdditionalWhere(FocList.FILTER_KEY_FOR_INCREMENTAL_UPDATE);
			  if(refToBeReloaded > 0) {//ForIncremental only
	      	String fldName = slaveDesc.getRefFieldName();
	      	if(!Utils.isStringEmpty(fldName)) {
		      	if(slaveDesc.getProvider() == DBManager.PROVIDER_MSSQL) fldName = "["+fldName+"]" ; 
		      	if(DBManager.provider_FieldNamesBetweenSpeachmarks(slaveDesc.getProvider())) fldName = "\""+fldName+"\"" ;
				  	focList.getFilter().putAdditionalWhere(FocList.FILTER_KEY_FOR_INCREMENTAL_UPDATE, fldName + "=" + String.valueOf(refToBeReloaded));
	      	}
			  }
		  }
		  //-------------
		  
		  SQLSelect select = new SQLSelect(focList, slaveDesc, focList.getFilter());
		  select.setSqlGroupBy(focList.getSqlGroupBy());
		  focList.putSiteReadRightConditionIfRequired();
		  focList.setLoading(true);
		  
		  error = select.execute();
		  
		  if(focList.getFilter() != null) {
			  focList.getFilter().removeAdditionalWhere(FocList.FILTER_KEY_FOR_INCREMENTAL_UPDATE);
		  }
			  
		  focList.setLoading(false);
		  FocList loadedFocList = select.getFocList();
		  focList.synchronize(loadedFocList);
		  focList.resetStatusWithPropagation();
  	}
  	
  	return error;
  }

	//-----------------------------------------------------
	//-----------------------------------------------------
	// focList_Join_Load
	//-----------------------------------------------------
	//-----------------------------------------------------

  @Override
  public boolean focList_Join_Load(FocList focList, long refToBeReloaded){
  	boolean error = true;
  	FocLinkJoinRequest link = (FocLinkJoinRequest) focList.getFocLink();
  	
  	if(link != null){
	    focList.setLoaded(true);
	    
		  //We remove first
		  //this way if the incremental where condition was stuck from before
		  //it is removed
		  if(focList.getFilter() != null && focList.getFocDesc() != null) {
		  	FocDesc focDesc = focList.getFocDesc();
			  focList.getFilter().removeAdditionalWhere(FocList.FILTER_KEY_FOR_INCREMENTAL_UPDATE);
			  if(refToBeReloaded > 0) {//ForIncremental only
	      	String fldName = focDesc.getRefFieldName();
	      	if(!Utils.isStringEmpty(fldName)) {
						if (focDesc.getProvider() == DBManager.PROVIDER_MSSQL) {
							if (fldName.contains(".")) {
								int dotIndex = fldName.indexOf(".");
								fldName = fldName.substring(0, dotIndex + 1) + "[" + fldName.substring(dotIndex + 1) + "]";
							} else {
								fldName = "[" + fldName + "]";
							}
						}
		      	fldName = DBManager.provider_ConvertFieldName(focDesc.getProvider(), fldName);
//		      	if(DBManager.provider_FieldNamesBetweenSpeachmarks(focDesc.getProvider())) fldName = "\""+fldName+"\"" ;
				  	focList.getFilter().putAdditionalWhere(FocList.FILTER_KEY_FOR_INCREMENTAL_UPDATE, fldName + "=" + String.valueOf(refToBeReloaded));
	      	}
			  }
		  }
		  //-------------
	    
	    SQLSelect select = new SQLSelectJoinRequest(focList, link.getRequestDesc(), focList.getFilter());
	    select.setSqlGroupBy(focList.getSqlGroupBy());
		  focList.setLoading(true);
	    error = !select.execute();
	    
		  if(focList.getFilter() != null) {
			  focList.getFilter().removeAdditionalWhere(FocList.FILTER_KEY_FOR_INCREMENTAL_UPDATE);
		  }	    
	    
		  focList.setLoading(false);
	    FocList loadedFocList = select.getFocList();
	    focList.synchronize(loadedFocList);
	    focList.resetStatusWithPropagation();
  	}
  	return error;
  }
  
	//-----------------------------------------------------
	//-----------------------------------------------------
	// focList_Delete
	//-----------------------------------------------------
	//-----------------------------------------------------

  @Override
  public boolean focList_Delete(FocList focList){
  	FocDesc focDesc = focList.getFocDesc();
  	boolean deleted = false;
  	if(focDesc != null){
  		FocFieldEnum enumeration = focDesc.newFocFieldEnum(FocFieldEnum.CAT_LIST, FocFieldEnum.LEVEL_PLAIN);
  		if(enumeration.hasNext()){
  			deleted = deleteDB_ElementByElement(focList);
  		}else{
  			deleted = deleteDB_AllTheListInOneTime(focList);
  		}
  	}
    return deleted;
  }  	
  	
  private boolean canDeleteDB(FocList focList, ArrayList<ReferenceCheckerToPutToZero> arrayPutToZero, ArrayList<ReferenceCheckerDelete> arrayDelete) {
  	boolean can = true;
  	FocDesc focDesc = focList.getFocDesc();
  	if(focDesc != null){
  		FocFieldEnum enumeration = focDesc.newFocFieldEnum(FocFieldEnum.CAT_LIST, FocFieldEnum.LEVEL_PLAIN);
  		if(enumeration.hasNext()){
  			can = canDeleteDB_ElementByElement(focList, arrayPutToZero, arrayDelete);
  		}
  	}
    return can;
  }

  private boolean canDeleteDB_ElementByElement(FocList focList, ArrayList<ReferenceCheckerToPutToZero> arrayPutToZero, ArrayList<ReferenceCheckerDelete> arrayToDelete){
  	boolean can = true;
  	Iterator iter = focList.focObjectIterator();
    while(iter != null && iter.hasNext() && can){
      FocObject obj = (FocObject) iter.next();
      if(obj != null) can = focObject_CanDelete(obj, null, arrayPutToZero, arrayToDelete);
    }
    return can;
  }

  private boolean deleteDB_ElementByElement(FocList focList){
  	boolean deleted = false;
  	Iterator iter = focList.focObjectIterator();
    while(iter != null && iter.hasNext()){
      FocObject obj = (FocObject) iter.next();
      if(obj != null) obj.delete();
    }
    deleted = true;
    return deleted;
  }
  
  private boolean deleteDB_AllTheListInOneTime(FocList focList){
  	boolean error = true;
  	
    FocDesc slaveDesc = focList.getFocDesc();
    if (slaveDesc != null) {
      Iterator iter = focList.focObjectIterator();
      while(iter != null && iter.hasNext()){
        FocObject obj = (FocObject) iter.next();
        if(obj != null) obj.delete();
      }
      SQLDelete delete = new SQLDelete(slaveDesc, focList.getFilter());
      try{
				delete.execute();
			}catch (Exception e){
				Globals.logException(e);
			}
			error = false;
    }
    return error;
  }
  
	//-----------------------------------------------------
	//-----------------------------------------------------
	// focObject_Redirect
	//-----------------------------------------------------
	//-----------------------------------------------------

  @Override
  public boolean focObject_Redirect(FocObject initialFocObject, FocObject newFocObject){
  	if(initialFocObject != null){
	  	Iterator iter = initialFocObject.getThisFocDesc().referenceLocationIterator();
	    while(iter != null && iter.hasNext()){
	      ReferenceChecker refCheck = (ReferenceChecker)iter.next();
	      if(refCheck != null){
	      	focObject_Redirect_ForReferenceChecker(refCheck, initialFocObject, newFocObject);
	      }
	    }
  	}
  	return false;
  }
  
  private SQLFilter getFilterAdapted(ReferenceChecker refCheck, FocObject obj){
  	SQLFilter filter = ReferenceChecker_VoidObject.getReferenceChecker_getSQLFilter(refCheck);
  	/*
    FocConstructor constr = new FocConstructor(refCheck.getFocDesc(), null, null);
    FocObject templateFocObj = constr.newItem();
    if(templateFocObj != null){
    	templateFocObj.setDbResident(false);
      filter = new SQLFilter(templateFocObj, SQLFilter.FILTER_ON_SELECTED);
      filter.addSelectedField(refCheck.getObjectFieldID());
    }
    */
    if(refCheck.getFocDesc() != null && filter != null){
      FocObject templateObj = (FocObject)filter.getObjectTemplate();
      if(templateObj != null){
        FProperty prop = (FProperty)templateObj.getFocProperty(refCheck.getObjectFieldID());
        
        if(FTypedObject.class.isInstance(prop)){
        	//ATTENTION
        	//ATTENTION
        	//ATTENTION
        	//ATTENTION
        	//ATTENTION
        	//ATTENTION
        	/*
          FTypedObject objProp = (FTypedObject)templateObj.getFocProperty(refCheck.getObjectFieldID());
          if(objProp != null){
            FocTypedObject typedObj = (FocTypedObject) objProp.getObject();
            if(typedObj != null){
              ObjectTypeMap typeMap = objProp.getObjectTypeMap();
              ObjectType objType = typeMap.findObjectType(obj);
              if(objType != null){
                typedObj.setType(objType.getId());
                typedObj.setFocObject(obj);
              }
            }
          }
          */
        }else if(FObject.class.isInstance(prop)){
          FObject objProp = (FObject)templateObj.getFocProperty(refCheck.getObjectFieldID());          
          if(objProp != null){
            objProp.setObject(obj);
          }
        }
      }
    }    
    return filter;
  }

  private void focObject_Redirect_ForReferenceChecker(ReferenceChecker refCheck, FocObject initialFocObject, FocObject focObjectToRedirectTo){
  	SQLFilter filter = getFilterAdapted(refCheck, initialFocObject);
  	
  	StringBuffer sqlWhere = new StringBuffer(); 
  	filter.addWhereToRequest_WithoutWhere(sqlWhere, refCheck.getFocDesc());
  	SQLSelectExistance selectExistance = new SQLSelectExistance(refCheck.getFocDesc(), sqlWhere);
  	selectExistance.execute();
  	  	
  	if(selectExistance.getExist() == SQLSelectExistance.EXIST_YES){
	  	FocConstructor constr = new FocConstructor(refCheck.getFocDesc(), null);
	  	FocObject newFocObject = constr.newItem();
	  	FObject fObjProp = (FObject) newFocObject.getFocProperty(refCheck.getObjectFieldID());
	  	boolean isDesactivateListener = fObjProp.isDesactivateListeners();
	  	fObjProp.setDesactivateListeners(true);
	  	fObjProp.setObject(focObjectToRedirectTo);
	  	fObjProp.setDesactivateListeners(isDesactivateListener);
	  	SQLUpdate update = new SQLUpdate(refCheck.getFocDesc(), newFocObject, filter);
	  	update.addQueryField(refCheck.getObjectFieldID());
	  	update.setWhereMandatory(true);
	  	try{
	  		update.execute();
	  	}catch(Exception e){
	  		Globals.logException(e);
	  	}
	  	newFocObject.dispose();
	  	update.dispose();
  	}
  	
  	refCheck.dispose_Content();
  }

	//-----------------------------------------------------
	//-----------------------------------------------------
	// focObject_GetNumberOfReferences
	//-----------------------------------------------------
	//-----------------------------------------------------

  @Override
  public int focObject_GetNumberOfReferences(FocObject focObj, StringBuffer message, ReferenceChecker referenceCjeckerToIgnore, ArrayList<ReferenceCheckerToPutToZero> arrayPutToZero, ArrayList<ReferenceCheckerDelete> arrayDelete){
    int     nbOfReferences = 0;
    FocDesc focDesc        = focObj.getThisFocDesc();
    
    if(focObj.hasRealReference()){
	    Iterator iter = focDesc.referenceLocationIterator();
	    while(iter != null && iter.hasNext()){
	      ReferenceChecker refCheck = (ReferenceChecker)iter.next();
	      if(refCheck != null && (referenceCjeckerToIgnore == null || refCheck != referenceCjeckerToIgnore)){
	      	if(refCheck.applyForThisObject(focObj)){
	      		
		      	if(refCheck.isDeleteWhenReferenceDeleted()){
		      		if(arrayDelete != null){
			      		ReferenceCheckerDelete toDelete = new ReferenceCheckerDelete(refCheck, focObj);
			      		arrayDelete.add(toDelete);
		      		}
		      	}else if(refCheck.isPutToZeroWhenReferenceDeleted()){
		      		if(arrayPutToZero != null){
			      		ReferenceCheckerToPutToZero toPutToZero = new ReferenceCheckerToPutToZero(refCheck, focObj);
			      		arrayPutToZero.add(toPutToZero);
		      		}
		      	}else{
		      		nbOfReferences += focObject_GetNumberOfReferences_ForChecker(refCheck, focObj, message);
		      	}
	      	}	      	
	      }
	    }
    }
    
    return nbOfReferences;
  }

  private int focObject_GetNumberOfReferences_ForChecker(ReferenceChecker refCheck, FocObject obj, StringBuffer message){
    int nbRef = 0;
    
    if(refCheck.getFocDesc() != null && !obj.isCreated()){
      FObjectField objField = (FObjectField) refCheck.getFocDesc().getFieldByID(refCheck.getObjectFieldID());
      if(!objField.isReferenceChecker_PutToZeroWhenReferenceDeleted() && !objField.isReferenceChecker_DeleteWhenReferenceDeleted()){      
	      SQLFilter     filter        = getFilterAdapted(refCheck, obj);
	      
	    	StringBuffer sqlWhere = new StringBuffer(); 
	    	filter.addWhereToRequest_WithoutWhere(sqlWhere, refCheck.getFocDesc());
	    	SQLSelectExistance selectExistance = new SQLSelectExistance(refCheck.getFocDesc(), sqlWhere);
	    	selectExistance.execute();
	    	nbRef = selectExistance.getCount();
	    	selectExistance.dispose();
	    	
	    	refCheck.dispose_Content();
	      
	      if(nbRef > 0 && message != null){
	        message.append("\n"+refCheck.getFocDesc().getStorageName()+", "+objField.getTitle()+", "+nbRef+" times");
	      }
      }
    }    
    return nbRef;
  }

	//-----------------------------------------------------
	//-----------------------------------------------------
	// command_DataModel2Code_
	//-----------------------------------------------------
	//-----------------------------------------------------

  @Override
  public String command_DataModel2Code(){
  	return null;
//  	ConnectionCredentials credentials = new ConnectionCredentials();
//  	credentials.setDrivers(ConfigInfo.getJdbcDrivers());
//  	credentials.setUrl(ConfigInfo.getJdbcURL());
//  	credentials.setUsername(ConfigInfo.getJdbcUserName());
//  	credentials.setPassword(ConfigInfo.getJdbcPassword());
////  	credentials.setXpassword(ConfigInfo.getJdbcXPassword());
//  	
//  	ConnectionPool pool = new ConnectionPool(credentials);
////  	DBReverseEngineering reverse = new DBReverseEngineering(pool); 
//  	
//  	return getDBAdaptor().writeCode_FromDataModel();
  }
  
	//-----------------------------------------------------
	//-----------------------------------------------------
	// command_AdaptDataModel
	//-----------------------------------------------------
	//-----------------------------------------------------

  @Override
  public boolean command_AdaptDataModel(boolean forceAlterTables, boolean schemaEmpty){
  	return getDBAdaptor().adaptDataModel(forceAlterTables, schemaEmpty);
  }

	//-----------------------------------------------------
	//-----------------------------------------------------
	// command_AdaptDataModel_SingleTable
	//-----------------------------------------------------
	//-----------------------------------------------------

	@Override
	public boolean command_AdaptDataModel_SingleTable(FocDesc focDesc) {
		return getDBAdaptor().adaptTable(focDesc);
	}

	//-----------------------------------------------------
	//-----------------------------------------------------
	// focObject_GetReference_ForUniqueKey
	//-----------------------------------------------------
	//-----------------------------------------------------

	@Override
	public int focObject_GetReference_ForUniqueKey(FocObject focObj) {
  	SQLSelectFindReferenceForUniqueKey selectReference = new SQLSelectFindReferenceForUniqueKey(focObj);
  	selectReference.execute();
  	selectReference.dispose();
  	return focObj.getReference().getInteger();
	}

	//-----------------------------------------------------
	//-----------------------------------------------------
	// focObject_GetReference_ForFilter
	//-----------------------------------------------------
	//-----------------------------------------------------

	@Override
	public int focObject_GetReference_ForFilter(FocDesc focDesc, String filterExpression) {
		SQLSelectFindReferenceForWhereExpression selectForWhere = new SQLSelectFindReferenceForWhereExpression(focDesc, filterExpression);
		selectForWhere.execute();
		int ref = selectForWhere.getReference();
		selectForWhere.dispose();
		return ref;
	}

	//-----------------------------------------------------
	//-----------------------------------------------------
	// command_Select
	//-----------------------------------------------------
	//-----------------------------------------------------

	@Override
	public ArrayList command_Select(FocDesc desc, int fieldID, boolean distinct, StringBuffer filterExpression) {
		ArrayList array = new ArrayList();
		
    SQLFilter filter = null;
    if(filterExpression != null){
	    filter = new SQLFilter(null, SQLFilter.FILTER_ON_SELECTED);
	    StringBuffer buff   = new StringBuffer(filterExpression);
	    filter.setAdditionalWhere(buff);
    }
		
		SQLSelectFields select = null;
		if(distinct){
			select = new SQLSelectDistinct(desc, fieldID, filter);
		}else{
			select = new SQLSelectFields(desc, fieldID, filter);
		}
		
    select.execute();
    for(int l=0; l<select.getLineNumber(); l++){
      //FDate dateProp = (FDate) ;
    	FProperty prop = select.getPropertyAt(l, 0);
    	if(prop instanceof FDate){
    		array.add(((FDate)prop).getDate());
    	}else{
    		array.add(prop.getString());
    	}
    }

    if(select != null){
    	select.dispose();
    	select = null;
    }
    if(filter != null){
    	filter.dispose();
    	filter = null;
    }
    
		return array;
	}

	@Override
	public ArrayList<String> command_SelectRequest(StringBuffer sqlRequest){
		ArrayList<String> array = new ArrayList();
		
		boolean error = true;
    StatementWrapper stmt = getDBManagerServer().lockStatement();
    if (stmt != null) {
      try {
      	String req = SQLRequest.adapteRequestToDBProvider(sqlRequest);
        if(ConfigInfo.isLogDBRequestActive()){
        	Globals.logString(req);
        }
        
        ResultSet resSet = stmt.executeQuery(req);
        while(resSet.next()){
        	String value = resSet.getString(1);
        	array.add(value);
        }
        if(resSet != null) resSet.close();
        
        error = false;
      } catch (Exception e) {
      	error = true;
        Globals.logString(e.getMessage());
        Globals.logExceptionWithoutPopup(e);
        
        getDBManagerServer().unlockStatement(stmt);
        Globals.logString("Exception is thrown again ->...");        
      }
      getDBManagerServer().unlockStatement(stmt);
    }

		return array;
	}
	
	@Override
	public ArrayList<String[]> command_SelectRequest(StringBuffer sqlRequest, int nbrColumns){
		ArrayList<String[]> array = new ArrayList();
		
		boolean error = true;
	  StatementWrapper stmt = getDBManagerServer().lockStatement();
	  String threadID = Thread.currentThread() != null ? String.valueOf(Thread.currentThread().getId()) : "--";
	  if (stmt != null) {
	    try {
	    	String req = SQLRequest.adapteRequestToDBProvider(sqlRequest);
	      if(ConfigInfo.isLogDBRequestActive()){
	      	Globals.logString(req);
	      }
	      
	      ResultSet resSet = stmt.executeQuery(req);
				Globals.logString(threadID+" - OUT of execute(req)");
	      while(resSet.next()){
	      	String[] row = new String[nbrColumns];
	      	for(int c=1; c<=nbrColumns; c++) {
	      		String value = resSet.getString(c);
	      		row[c-1] = value;
	      	}
	      	array.add(row);
	      }
	      if(resSet != null) resSet.close();
	      Globals.logString(threadID+" - read result set ended");
	      error = false;
	    } catch (Exception e) {
	    	error = true;
	      Globals.logString(e.getMessage());
	      Globals.logExceptionWithoutPopup(e);
	      
	      getDBManagerServer().unlockStatement(stmt);
	      Globals.logString("Exception is thrown again ->...");        
	    }
      Globals.logString(threadID+" - unlocking statement");
	    getDBManagerServer().unlockStatement(stmt);
	    Globals.logString(threadID+" - unlocking statement DONE");
	  }
	
		return array;
	}	
	//-----------------------------------------------------
	//-----------------------------------------------------
	// command_CheckTables
	//-----------------------------------------------------
	//-----------------------------------------------------

  private boolean userExists() {
  	boolean exist = false;
  	FocUserDesc userDesc = (FocUserDesc) FocUser.getFocDesc(); 
  	FStringField nameFld = (FStringField) userDesc.getFieldByID(FocUserDesc.FLD_NAME);
  	StringBuffer sqlWhere = new StringBuffer(nameFld.getDBName()+"='"+AdminModule.ADMIN_USER+"'");
  	SQLSelectExistance selectExistance = new SQLSelectExistance(FocUser.getFocDesc(), sqlWhere);
  	selectExistance.execute();
  	exist = selectExistance.getExist() == SQLSelectExistance.EXIST_YES;
    return exist;
  }

	@Override
	public boolean command_CheckTables(){
    Application app = Globals.getApp();
    if(app != null){
      DBManager dbMan = app.getDBManager();
      if(dbMan != null){
        Hashtable allRealTables = getDBManagerServer().newAllRealTables();
        if(allRealTables != null){
          FocDesc userDesc    = FocUser.getFocDesc();              
          FocDesc versionDesc = FocVersion.getFocDesc();
          
          boolean userExist    = allRealTables.get(userDesc.getStorageName_ForSQL()) != null;
          boolean versionExist = allRealTables.get(versionDesc.getStorageName_ForSQL()) != null;
          
          boolean newUserTables = !userExist && Globals.getApp().isWithLogin();
          setEmptyDatabaseJustCreated(newUserTables);
          
          int versionDifference = (!versionExist) ? 1 : 0;
          if(versionDifference == 0){
            versionDifference = FocVersion.compareWithDatabaseVersion();
          }
          
          if(versionDifference < 0){
            String text = "Your executable version is less than the database version.\nPlease update your application directory."; 
            text = text + "     * Module : DB version -> EXE version\n";
            FocList verList = FocVersion.getVersionList();
            Iterator iter = verList.focObjectIterator();
            while(iter != null && iter.hasNext()){
              FocVersion ver = (FocVersion)iter.next();
              FocVersion dbVer = ver.getDbVersion();
              
              text = text + "     * " + ver.getJar()+" : " ;
              if(dbVer != null){
                text = text + dbVer.getName() + " ("+dbVer.getId()+") -> ";                    
              }else{
                text = text + " not available -> ";                    
              }
              text = text + ver.getName() + " ("+ver.getId()+")\n";
            }
            if(Globals.getDisplayManager() != null){
            	Globals.getDisplayManager().popupMessage(text);	
            }else{
            	Globals.logString(text);
            }
            Globals.getApp().exit();
          }else if(newUserTables || versionDifference > 0){
            String text = "You need to adapt data model for the following reasons:\n";
            if(newUserTables){
              text = text + "  - User tables don't exist\n";
            }
            if(versionDifference > 0){
              text = text + "  - New version installation:\n";
              text = text + "     * Module : DB version -> EXE version\n";
              FocList verList = FocVersion.getVersionList();
              Iterator iter = verList.focObjectIterator();
              while(iter != null && iter.hasNext()){
                FocVersion ver = (FocVersion)iter.next();
                FocVersion dbVer = ver.getDbVersion();
                
                text = text + "     * " + ver.getJar()+" : " ;
                if(dbVer != null){
                  text = text + dbVer.getName() + " ("+dbVer.getId()+") -> ";                    
                }else{
                  text = text + " not available -> ";                    
                }
                text = text + ver.getName() + " ("+ver.getId()+")\n";
              }
            }
            
            text = text + "Adapt data model now ?";
            
            int dialogRet = JOptionPane.YES_OPTION;
            if(Globals.getDisplayManager() != null){
            	dialogRet = JOptionPane.showConfirmDialog(Globals.getDisplayManager().getMainFrame(),
                text,
                "01Barmaja",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null);
            }
            
            switch(dialogRet){
            case JOptionPane.YES_OPTION:
              app.declareFocObjects();
              app.adaptDataModel(false, !userExist);
              break;
            case JOptionPane.NO_OPTION:
              //app.exit();
              break;
            }
          }         
        }
      }
    }
    return false;
	}
	
	//-----------------------------------------------------
	//-----------------------------------------------------
	// focObject_Load
	//-----------------------------------------------------
	//-----------------------------------------------------

	@Override
	public boolean focObject_Load(FocObject focObject, int[] fieldsArray) {
    FocDesc focDesc = focObject.getThisFocDesc();
    if( focDesc != null ){
      SQLFilter filter = new SQLFilter(focObject, SQLFilter.FILTER_ON_IDENTIFIER);
      SQLSelect sqlSelect = new SQLSelect(focObject, focObject.getThisFocDesc(), filter);
      for(int i=0; i<fieldsArray.length; i++){
      	sqlSelect.addQueryField(fieldsArray[i]);
      }
      if(sqlSelect.execute()){
        focObject.setLoadedFromDB(false);
      }
    }

		return false;
	}

	@Override
	public boolean command_Replace(FocDesc desc, int fieldID, String originalValue, String newValue) {
  	FField cFld = (FField) desc.getFieldByID(fieldID);
  	
    StatementWrapper stmt = getDBManagerServer().lockStatement();
    if (stmt != null && desc.isPersistent()) {
    	StringBuffer sqlRequest = new StringBuffer();
    	sqlRequest.append("UPDATE "+desc.getStorageName()+" SET ");
    	sqlRequest.append(cFld.getDBName()+"='"+newValue+"' WHERE "+cFld.getDBName()+"='"+originalValue+"'");
      try {
      	String req = SQLRequest.adapteRequestToDBProvider(sqlRequest);
        if(ConfigInfo.isLogDBRequestActive()){
        	Globals.logString(req);
        }
        
        stmt.executeUpdate(req);
      } catch (Exception e) {
        SQLException sqlE = (SQLException) e;  
        Globals.logString(sqlE.getMessage());
        Globals.logException(e);
        
        getDBManagerServer().unlockStatement(stmt);
        Globals.logString("Exception is thrown again ->...");        
      }
      getDBManagerServer().unlockStatement(stmt);
    }

		return false;
	}
	
	public static FocDataSource_DB getInstance(){
		return (FocDataSource_DB) Globals.getApp().getDataSource();
	}

	@Override
	public Date command_GetCurrentTimeStamp() {
		return dbManagerServer != null ? dbManagerServer.getCurrentTimeStamp_AsTime() : null;
	}

	@Override
	public InputStream focObject_LoadInputStream(FocObject focObject, int fieldID) {
		InputStream is = null;

  	FocDesc       focDesc        = focObject != null ? focObject.getThisFocDesc() : null;
  	FField        focField       = focDesc.getFieldByID(fieldID);
    String        imageFieldName = focField != null ? focField.getName() : null;
		
	  String        selectRequest = "select "+imageFieldName+" from "+focDesc.getStorageName_ForSQL()+" where ref = " + focObject.getReference();
	
	  Globals.logString(selectRequest);
	
	  PreparedStatement preparedStmt = null;
	  Connection connection = null;
		try{
			connection = getDBManagerServer().getConnection();
			preparedStmt = connection.prepareStatement(selectRequest);			
		}catch (SQLException e){
			Globals.logException(e);
		}
	  ResultSet rs = null;
		try{
			rs = preparedStmt.executeQuery();
		}catch (SQLException e){
			Globals.logException(e);
		}
	  try{
			while (rs.next()) {
			  is = rs.getBinaryStream(imageFieldName);
			}
			if(rs != null) rs.close();
			getDBManagerServer().releaseConnection(connection);
		}catch (SQLException e){
			Globals.logException(e);
		}
	  return is;
	}
	
	@Override
	public BufferedImage focObject_LoadImage(FocObject focObject, int fieldID) {
		BufferedImage imageValue     = null;
		  	
  	FocDesc       focDesc        = focObject != null ? focObject.getThisFocDesc() : null;
  	FField        focField       = focDesc.getFieldByID(fieldID);
    String        imageFieldName = focField != null ? focField.getName() : null;
		
    if(focDesc != null && focObject != null && focObject.hasRealReference()){
      String selectRequest = "SELECT " + imageFieldName + "  FROM " + focDesc.getStorageName_ForSQL() + " WHERE REF = " + focObject.getReference().getInteger();
      if(focDesc.getProvider() == DBManager.PROVIDER_POSTGRES) {
      	selectRequest = "SELECT \"" + imageFieldName + "\"  FROM \"" + focDesc.getStorageName_ForSQL() + "\" WHERE \"REF\" = " + focObject.getReference().getInteger();
      }
      
      Globals.logString(selectRequest);

      ResultSet rs = null;
      Connection connection = null;
      try{
      	connection = getDBManagerServer().getConnection();
	      PreparedStatement preparedStmt = connection.prepareStatement(selectRequest);
	      rs = preparedStmt.executeQuery();
	      while (rs.next()) {
	        InputStream is = rs.getBinaryStream(imageFieldName);
	        if(is != null){
		        imageValue = ImageIO.read(is);
		        is.close();
	        }
	      }
	      if(rs != null) {
	      	rs.close();
	      	rs = null;
	      }	      
      }catch(Exception e){
      	Globals.logException(e);
      }finally {
      	if(getDBManagerServer() != null && connection != null) getDBManagerServer().releaseConnection(connection);
      	if(rs != null) {
					try{
						rs.close();
						rs = null;
					}catch (SQLException e){
						Globals.logException(e);
					}
      	}
			}
    }
    
		return imageValue;
	}

	@Override
	public boolean focObject_UpdateImage(FocObject focObject, int fieldID) {
		boolean error = focObject == null;
		if(!error){
			FocDesc focDesc        = focObject.getThisFocDesc();
			FField  focField       = focDesc.getFieldByID(fieldID);
	    String  imageFieldName = focField != null ? focField.getName() : null;

	    String sqlRequest = "UPDATE "+ focDesc.getStorageName_ForSQL()+" SET "+ imageFieldName +" = (?) WHERE REF = "+ focObject.getReference().getInteger();
	    try{
	      Connection        connection      = getDBManagerServer().getConnection();
	      PreparedStatement stmt            = connection.prepareStatement(sqlRequest);
	      
	      BufferedImage bufferedImage = focObject.getPropertyImage(fieldID);
	      ByteArrayOutputStream imagebuffer = new ByteArrayOutputStream();	      
      	ImageIO.write(bufferedImage, "png", imagebuffer);

	      ByteArrayInputStream inputStream = new ByteArrayInputStream(imagebuffer.toByteArray());
	      stmt.setBinaryStream(1, inputStream);
	      
	      stmt.executeUpdate();
	      stmt.close();
	      getDBManagerServer().releaseConnection(connection);
	      inputStream.close();
	      imagebuffer.close();
	    }catch (SQLException e){
	      Globals.logException(e);
	    }catch (IOException e){
	      Globals.logException(e);
	    }
		}
		return error;
	}
	
	@Override
	public boolean focObject_UpdateImage(FocObject focObject, int fieldID, File file) {
		boolean error = focObject == null;
		if(!error){
			FocDesc focDesc        = focObject.getThisFocDesc();
			FField  focField       = focDesc.getFieldByID(fieldID);
	    String  imageFieldName = focField != null ? focField.getName() : null;

	    String sqlRequest = "UPDATE "+ focDesc.getStorageName()+" SET "+ imageFieldName +" = (?) WHERE REF = "+ focObject.getReference().getInteger();
	    try{
	    	FileInputStream   fileInputStream = new FileInputStream(file);
	      Connection        connection      = getDBManagerServer().getConnection();
	      PreparedStatement stmt            = connection.prepareStatement(sqlRequest);
	      stmt.setBinaryStream(1, fileInputStream, (int) file.length());
	      stmt.executeUpdate();
	      stmt.close();
	      getDBManagerServer().releaseConnection(connection);
	      fileInputStream.close();
	      fileInputStream = null;
	    }catch (SQLException e){
	      Globals.logException(e);
	    }catch (IOException e){
	      Globals.logException(e);
	    }
		}
		return error;
	}
	
	@Override
	public boolean focObject_addBlobFromFilePath(FocObject obj, int fieldID, String filePath){
		boolean error = obj == null;
		if(!error){
			FocDesc focDesc = obj.getThisFocDesc();
		  String attachmentColumn = focDesc.getFieldByID(fieldID).getName();
		  int refField = obj.getPropertyInteger(FField.REF_FIELD_ID);
		  String sqlRequest = "UPDATE "+ focDesc.getStorageName_ForSQL()+" SET "+ attachmentColumn +" = (?) WHERE REF = "+ refField;
		  try {
		    if (filePath != null){
		    	File file = new File(filePath);
		    	FileInputStream fileInputStream = new FileInputStream(file);
		      Connection connection = getDBManagerServer().getConnection();
		      PreparedStatement stmt = connection.prepareStatement(sqlRequest);
		      stmt.setBinaryStream(1, fileInputStream, (int) file.length());
		      stmt.executeUpdate();
		      getDBManagerServer().releaseConnection(connection);
		      fileInputStream.close();
		    }
		  } catch (SQLException e) {
		    Globals.logException(e);
		  } catch (IOException e) {
		    Globals.logException(e);
		  }
		}
		return error;
	}
	
  @Override
  public boolean focObject_addBlobFromInputStream(FocObject obj, int fieldID, InputStream inputStream) {
    boolean error = obj == null;
    if(!error){
      FocDesc focDesc = obj.getThisFocDesc();
      String attachmentColumn = focDesc.getFieldByID(fieldID).getName();
      int refField = obj.getPropertyInteger(FField.REF_FIELD_ID);
      String sqlRequest = "UPDATE "+ focDesc.getStorageName_ForSQL()+" SET "+ attachmentColumn +" = (?) WHERE REF = "+ refField;
      try {
        if (inputStream != null){
          Connection connection = getDBManagerServer().getConnection();
          PreparedStatement stmt = connection.prepareStatement(sqlRequest);
          stmt.setBinaryStream(1, inputStream);
          stmt.executeUpdate();
          getDBManagerServer().releaseConnection(connection);
          inputStream.close();
        }
      } catch (SQLException e) {
        Globals.logException(e);
      } catch (IOException e) {
        Globals.logException(e);
      }
    }
    return error;
  }
	
	@Override
	public void transaction_SeeIfShouldCommit(){
		getDBManagerServer().transaction_SeeIfShouldCommit();
	}
	
	@Override
	public void transaction_setShouldSurroundWithTransactionIfRequest(){
		getDBManagerServer().transaction_setShouldSurroundWithTransactionIfRequest();
	}

	@Override
	public boolean command_ExecuteRequest(String dbSourceKey, StringBuffer sqlRequest) {
		boolean error = true;
    StatementWrapper stmt = getDBManagerServer().lockStatement(dbSourceKey);
    if (stmt != null) {
      try {
      	String req = SQLRequest.adapteRequestToDBProvider(sqlRequest);
        if(ConfigInfo.isLogDBRequestActive()){
        	Globals.logString(req);
        }
        
        stmt.executeUpdate(req);
        error = false;
      } catch (Exception e) {
      	error = true;
        SQLException sqlE = (SQLException) e;  
        Globals.logString(sqlE.getMessage());
        Globals.logException(e);
        
        getDBManagerServer().unlockStatement(stmt);
        Globals.logString("Exception is thrown again ->...");        
      }
      getDBManagerServer().unlockStatement(stmt);
    }
		return error;
	}
	
	@Override
	public boolean command_ExecuteRequest(StringBuffer sqlRequest) {
		return command_ExecuteRequest(null, sqlRequest);
	}

	public Application getApp() {
		return app;
	}

	@Override
	public IFocDataUtil getUtility() {
		if(dbUtil == null){
			dbUtil = new DBUtil();
		}
		return dbUtil;
	}

	@Override
	public DBManagerServer getDBManagerServer() {
		return dbManagerServer;
	}

	@Override
	public boolean isEmptyDatabaseJustCreated() {
		if(emptyDatabaseJustCreated == DB_STATUS_NOT_CHECKED_YET){
			emptyDatabaseJustCreated = DB_STATUS_EXIST;
	    Application app = Globals.getApp();
	    if(app != null){
	      DBManager dbMan = app.getDBManager();
	      if(dbMan != null){
	        Hashtable allRealTables = getDBManagerServer().newAllRealTables();
	        if(allRealTables != null){
	        	if(allRealTables.values().size() <= 1){//Initially we do have the _initial_saas_config so normally we have 1
	        		setEmptyDatabaseJustCreated(true);
	        	}
	        }
	      }
	    }
		}
		
		return emptyDatabaseJustCreated == DB_STATUS_DOES_NOT_EXIST;
	}

	@Override
	public void setEmptyDatabaseJustCreated(boolean emptyDatabaseJustCreated) {
		if(emptyDatabaseJustCreated){
			this.emptyDatabaseJustCreated = DB_STATUS_DOES_NOT_EXIST;	
		}else{
			this.emptyDatabaseJustCreated = DB_STATUS_EXIST;
		}
	}

	@Override
	public CallableStatement sp_Call(String name, Object[] params) {
		Globals.logString("SP Call : "+name);
		CallableStatement callableStatement = null;
		try{
			Connection connection = getDBManagerServer() != null ? getDBManagerServer().getConnection() : null;
			if(connection != null){
				StringBuffer callString = new StringBuffer("call " + name);
				if(params != null && params.length > 0){
					callString.append("(");
					for(int i=0; i<params.length; i++){
						if(i > 0) callString.append(",");
						Object paramObj = params[i];
						if(paramObj instanceof Date){
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
							callString.append("'");
							callString.append(sdf.format(paramObj));
							callString.append("'");
						}else if(paramObj instanceof Integer){
							callString.append(paramObj);
						}else if(paramObj instanceof Boolean){
							callString.append(paramObj);							
						}else{
							callString.append("'");
							callString.append(paramObj);
							callString.append("'");
						}
					}
					callString.append(")");
				}
				
				String statementString = callString.toString();
				Globals.logString("SP Call Statement : "+statementString);
				callableStatement = connection.prepareCall(statementString);
				if(callableStatement != null){
					callableStatement.execute();
					callableStatement.close();
				}
				getDBManagerServer().releaseConnection(connection);
			}
			Globals.logString("SP Call : Successful");
		}catch(Exception ex){
			Globals.logString("SP Call : Failed");
			Globals.logException(ex);
		}
		return callableStatement;
	}
	
	@Override
	public boolean command_executeRequestForModulesSP(String spFileName) {
		try{
			Globals.logString("SP Re-Generation : "+spFileName);
			
  		ClassResource resource = new ClassResource(spFileName);
			InputStream inputStream = resource.getStream().getStream();
			InputStreamReader isReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(isReader);
			
			Connection connection = getDBManagerServer() != null ? getDBManagerServer().getConnection() : null;
			if(connection != null){
				Statement sqlStatement = connection.createStatement();
				if(sqlStatement != null){
					String line = null;
					StringBuilder stringBuilder = null;
					while((line = bufferedReader.readLine()) != null){
						line = line.trim();
						if(!Utils.isStringEmpty(line)){
							if(stringBuilder != null && (line.contains(SP_DELIMITER) || line.contains(SP_DELIMITER.toLowerCase()))){
								String query = stringBuilder.toString().replaceAll("//", "");
								sqlStatement.addBatch(query);
								stringBuilder = null;
							}else{
								if(stringBuilder == null){
									stringBuilder = new StringBuilder();
								}
								if(stringBuilder.length() != 0) stringBuilder.append("\n");
								stringBuilder.append(line);
							}
						}
					}
					sqlStatement.executeBatch();
				}
				
				if(getDBManagerServer() != null) getDBManagerServer().releaseConnection(connection);
			}
			inputStream.close();
			inputStream = null;
			
			isReader.close();
			isReader = null;
			
			bufferedReader.close();
			bufferedReader = null;
			Globals.logString("SP Re-Generation : Successful");
		}catch(Exception ex){
			Globals.logString("SP Re-Generation : Failed"); 
			Globals.logException(ex);
		}
		return false;
	}

	@Override
	public void executeCustomQuery(StringBuffer sqlRequest, IExecuteResultSet iExecuteResultSet) {
    StatementWrapper stmt = getDBManagerServer().lockStatement();
    if (stmt != null) {
      try {
      	String req = SQLRequest.adapteRequestToDBProvider(sqlRequest);
        if(ConfigInfo.isLogDBRequestActive()){
        	Globals.logString(req);
        }
        
        stmt = getDBManagerServer().executeQuery_WithMultipleAttempts(stmt, req);
        ResultSet resSet = stmt != null ? stmt.getResultSet() : null;
        
        if(resSet != null){
        	int columnCount = resSet.getMetaData().getColumnCount();
        	int rowIndex = 0;
        	while(resSet.next()){
        		for(int columnIndex=1; columnIndex<=columnCount; columnIndex++){
        			String value = resSet.getString(columnIndex);
        			iExecuteResultSet.executeResultSet(rowIndex, columnIndex, value);
        		}
        		rowIndex++;
        	}
        	iExecuteResultSet.afterResultSetFinished();
        	
        	resSet.close();
        }
        
      } catch (Exception e) {
        Exception sqlE = (Exception) e;  
        Globals.logString(sqlE.getMessage());
        Globals.logExceptionWithoutPopup(e);
        
        getDBManagerServer().unlockStatement(stmt);
        Globals.logString("Exception is thrown again ->...");        
      }
      getDBManagerServer().unlockStatement(stmt);
    }
	}

	@Override
	public int getProvider(String dbSourceKey) {
		int provider = DBManager.PROVIDER_MYSQL;
		ConnectionPool pool = DBManagerServer.getInstance().getConnectionPool(dbSourceKey);
		if(pool != null){
			provider = pool.getProvider();
		}
		return provider;
	}
	
	@Override
	public int getServerVersion(String dbSourceKey) {
		int version = 0;
		ConnectionPool pool = DBManagerServer.getInstance().getConnectionPool(dbSourceKey);
		if(pool != null){
			version = pool.getServerVersion();
		}
		return version;
	}

	@Override
	public boolean command_AdaptDataModel_Reindex() {
		getDBAdaptor().adaptDataModel_ReorganzeIndexes();
		return false;
	}

	@Override
	public StringBuffer getMonitoringText() {
		DBManagerServer dbServer = getDBManagerServer();
		return dbServer != null ? dbServer.getMonitoringText() : null;
	}

	@Override
	public boolean command_executeSQLScript(String key, String spFileName) {
		try{
			Globals.logString("SQL Script Runner : "+spFileName);
			
			ConnectionPool pool       = getDBManagerServer() != null ? getDBManagerServer().getConnectionPool(key) : null;
			Connection     connection = pool != null ? pool.getConnection() : null;
			
			if(connection != null){
				ScriptRunner runner = new ScriptRunner(connection, true, false);

	  		ClassResource resource = new ClassResource(spFileName);
				InputStream inputStream = resource.getStream().getStream();
				InputStreamReader isReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(isReader);
				
				runner.runScript(bufferedReader);
				runner.dispose();
				runner = null;
				
				if(pool != null) pool.releaseConnection(connection);
			}
			
			Globals.logString("    Script : Successful");
		}catch(Exception ex){
			Globals.logString("    Scritp : Failed"); 
			Globals.logException(ex);
		}
		return false;
	}

	@Override
	public String getNamespaceName(String dbSouceKey) {
		ConnectionPool defaultConnection = DBManagerServer.getInstance() != null ? DBManagerServer.getInstance().getConnectionPool(dbSouceKey) : null;
	  return defaultConnection != null ? defaultConnection.getCredentials().getNamespace() : "";
	}

}
