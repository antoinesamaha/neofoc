/*
 * Created on 27 fevr. 2004
 */
package com.foc.focDataSourceDB.db;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import com.foc.Globals;
import com.foc.dataSource.IFocDataSource;
import com.foc.db.SQLFilter;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocFieldEnum;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldPath;
import com.foc.focDataSourceDB.db.connectionPooling.StatementWrapper;
import com.foc.list.FocList;
import com.foc.performance.PerfManager;
import com.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
public class SQLSelectPlain extends SQLRequest {
  protected final static int LOAD_IN_OBJECT        = 0;
  protected final static int LOAD_IN_EMPTY_LIST    = 1;
  protected final static int LOAD_IN_EXISTING_LIST = 2;
  protected final static int LOAD_IN_EXISTING_LIST_INCREMENTAL = 3;

  protected int          loadMode            = LOAD_IN_OBJECT;// We have 2 modes: 1-Updating an
  																				                    // existing FocList 2-Creating a new
                                        	                    // FocList as a result
  protected FocList      focList             = null;
  private   FocObject    focObjectToBeFilled = null;
  
  private   FocFieldEnum enumer              = null; 
  
  public SQLSelectPlain(FocObject focObject, FocDesc focDesc, SQLFilter filter) {
    super(focDesc, filter);
    this.focObjectToBeFilled = focObject;
    loadMode = LOAD_IN_OBJECT;
  }
  
  public SQLSelectPlain(FocList initialList, FocDesc focDesc, SQLFilter filter) {
    super(focDesc, filter);
    this.focList = initialList;
    if(focList == null){
      loadMode = LOAD_IN_OBJECT;
    }else if(focList.size() > 0){
      loadMode = LOAD_IN_EXISTING_LIST;
    }else{
      //loadMode = LOAD_IN_EMPTY_LIST;
      loadMode = LOAD_IN_EXISTING_LIST;
    }
    
    if(loadMode == LOAD_IN_EXISTING_LIST) {
    	if(filter != null && filter.getAdditionalWhere(FocList.FILTER_KEY_FOR_INCREMENTAL_UPDATE) != null) {
    		loadMode = LOAD_IN_EXISTING_LIST_INCREMENTAL;
    	}
    }
  }
  
  public void dispose(){
    super.dispose();
    focList = null;
    focObjectToBeFilled = null;
    if(enumer != null){
    	enumer.dispose();
    }
  }

  public FocFieldEnum getFocFieldEnumerAndReset(){
  	if(enumer == null && focDesc != null){
  		enumer = focDesc.newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_DB);
  	}
  	if(enumer != null) enumer.reset();
  	return enumer;
  }
  
  protected boolean isSupportJoins(){
  	return true;
  }
  
  public FocObject getFocObjectToBeFilled(){
  	return focObjectToBeFilled;
  }
  
  public boolean buildRequest() {
    request = new StringBuffer("");
    boolean error = false;

    if (focDesc != null && focDesc.isPersistent()) {
      boolean firstField = true;
      request.append("SELECT ");
      
      String mainTableAlias     = filter.getJoinMap().getMainTableAlias();
      String PrefixForMainTable = filter.hasJoinMap()?mainTableAlias+".":"";
     
      //FocFieldEnum enumer = focDesc.newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_DB);
      FocFieldEnum enumer = getFocFieldEnumerAndReset();
      while(enumer.hasNext()){
        FField 			focField 	= (FField) enumer.next();
        FFieldPath 	path 			= enumer.getFieldPath();
        if(focField != null && isFieldInQuery(path)){
        	if(!filter.hasJoinMap() || focField.getID() != FField.FLD_SYNC_IS_NEW_OBJECT){//We want to exclude the sync fields if we are in a join otherwize we get ambigus...
	          if(!firstField){
	            request.append(",");
	          }
	          //request.append(enumer.getFieldCompleteName(focDesc)+" \""+enumer.getFieldCompleteName(focDesc)+"\"");
	          request.append(PrefixForMainTable+enumer.getFieldCompleteName(focDesc));
	          firstField = false;
        	}
        }
      }
      addFrom();
      error = addWhere();
      addOrderBy();
      addOffset();
    }
    return error;
  }
  
  protected int findFieldPositionInMetaData(ResultSetMetaData metaData, String fieldName){
    int foundPos = -1;
    
    try {
      for(int i=1; i<metaData.getColumnCount() && foundPos < 0; i++){
        String colName = metaData.getColumnName(i);
        if(colName.trim().toUpperCase().compareTo(fieldName.trim().toUpperCase()) == 0){
          foundPos = i;
        }
      }
    } catch (SQLException e) {
      Globals.logException(e);
    }
    
    return foundPos;
  }
  
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
      //obj.scanPropertiesAndNotifyListeners();
      FocFieldEnum enumer = getFocFieldEnumerAndReset();
      obj.scanPropertiesAndNotifyListeners(enumer);
      obj.backup();
      if(Globals.getApp().getRightsByLevel() != null){
        Globals.getApp().getRightsByLevel().lockValuesIfNecessary(obj);                  
      }
      obj.setDesactivateSubjectNotifications(false);
    }
  }

  protected void fillFieldsValuesFromResultSet(FocObject targetObject, ResultSet resultSet, ArrayList<FProperty> arrayOfPropertiesModified){
	  int i = 1;
	  //StringBuffer buffer = new StringBuffer();
	  //FocFieldEnum enumer = targetObject.newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_DB);
	  FocFieldEnum enumer = getFocFieldEnumerAndReset();
	  while(enumer.hasNext()){
	    FField focField = (FField) enumer.next();
	    FFieldPath path = enumer.getFieldPath();
	    if(focField != null && isFieldInQuery(path)){
	      FProperty prop = enumer.getProperty(targetObject);
	      if(prop != null ){
	      	String value = null; 
	      	try{
	      		value = resultSet.getString(i);
	      		//buffer.append(resultSet.getString(i));
	      		//buffer.append(',');
	      	}catch(Exception e){
	      		value = "";
	      		Globals.logException(e);
	      	}
	        prop.setSqlString(value);
	        arrayOfPropertiesModified.add(prop);
	      }
	    
	      i++;
	    }
	  }
	  
	  //Globals.logString(buffer);
  }	
  
  protected int findIdentifierFieldPositionInMetaData(ResultSetMetaData meta){
  	int posInSelectForIdentifierField = -1;
    FField identifierField = focDesc.getIdentifierField();
    if(identifierField != null && identifierField.isDBResident()){//BAntoine 2013-03-28 - We added the isDBResident because in the Join case when the REF is there for VAADIN reasons, we do not want the system to take any REF field and put its value in the Non DB resident one
    	//The Bug was seen in Material Submittals when loading the MatSubJoinList, we got 2 lines instead of 4 because the BkdnRef was the first column in the Select and FocList grouped by BkdnRef
      posInSelectForIdentifierField = findFieldPositionInMetaData(meta, identifierField.getName());
    }           
  	return posInSelectForIdentifierField;
  }
  
  protected void treatResultSet(ResultSet resultSet){
    try {
    	
    	boolean disableReSortAfterAdd_Backup = false;
    	if(focList != null){
    		disableReSortAfterAdd_Backup = focList.isDisableReSortAfterAdd();
    		focList.setDisableReSortAfterAdd(true);
    	}
    	
      //HashMap<FocObject, FocObject> visitedObjects = null;
    	VisitedObjectsContainer visitedObjects = null;
      if (   loadMode == LOAD_IN_EXISTING_LIST
      		|| loadMode == LOAD_IN_EXISTING_LIST_INCREMENTAL) {
        // If we are in update list mode, we should keep track of visited objects
        // to remove unvisited objects from initial list
        //visitedObjects = new HashMap<FocObject, FocObject>();
      	visitedObjects = new VisitedObjectsContainer(focList.getFocDesc());
        focList.setSleepListeners(true);
      }

      ResultSetMetaData meta = resultSet.getMetaData();
      // The identifier field position ensures the possibility to
      // directly read the identifier without having to scan all fields
      int posInSelectForIdentifierField = findIdentifierFieldPositionInMetaData(meta);
      /*
      FField identifierField = focDesc.getIdentifierField();
      if(identifierField != null){
        posInSelectForIdentifierField = findFieldPositionInMetaData(meta, identifierField.getName());
      } 
      */          
          
      //This temp object is only to read the identifier property
      FocObject tempObject = null;
      if (   loadMode == LOAD_IN_EXISTING_LIST
      		|| loadMode == LOAD_IN_EXISTING_LIST_INCREMENTAL) {
        FocConstructor constr = new FocConstructor(focDesc, null, focList.getMasterObject());
        tempObject = constr.newItem();
        if(tempObject == null){
        	tempObject = constr.newItem();
        }
        tempObject.setDbResident(false);
      }
      
      ArrayList<FProperty> arrayOfPropertiesModified = new ArrayList<FProperty>();
      
      boolean firstResult = true;
      while (resultSet.next()) {
        FocObject addedObject = null;
        //Reading identifier property into tempObject
        //-------------------------------------------
        String identifierValue = null;
        if(posInSelectForIdentifierField >= 0){
          identifierValue = resultSet.getString(posInSelectForIdentifierField);
        }
        //-------------------------------------------
        
        if (   loadMode == LOAD_IN_EXISTING_LIST
        		|| loadMode == LOAD_IN_EXISTING_LIST_INCREMENTAL
        		|| loadMode == LOAD_IN_EMPTY_LIST
        		) {
          FProperty identifierProp = null;
          
          if(    loadMode == LOAD_IN_EXISTING_LIST
          		|| loadMode == LOAD_IN_EXISTING_LIST_INCREMENTAL){            
            identifierProp = tempObject.getIdentifierProperty();
            if(identifierProp != null){
              identifierProp.setSqlString(identifierValue);
            }
            if(tempObject.getReference() != null){
              //addedObject = focList.searchByReference(tempObject.getReference().getInteger());
              addedObject = focList.searchByRealReferenceOnly(tempObject.getReference().getInteger());
            }
          }
          if (addedObject == null) {
            //boolean backup = focList.isDesactivateSubjectNotifications();
            //focList.setDesactivateSubjectNotifications(true);
            addedObject = focList.newItem(identifierProp);
            focList.add(addedObject);
            //Globals.logString("dans added object == null, debugCount : "+debugCount+ " foc list apres "+ focList.size() );
            //focList.setDesactivateSubjectNotifications(backup);
          }
        }else{
          FProperty identifierProp = focObjectToBeFilled.getIdentifierProperty();
          if(identifierProp != null){
            String initialString = identifierProp.getString();                
            identifierProp.setSqlString(identifierValue);
            String newString = identifierProp.getString();
            identifierProp.setString(initialString);
            
            if(firstResult){
              addedObject = focObjectToBeFilled;
              firstResult = false;
            }else{
	            if(initialString.compareTo(newString) == 0){
                Globals.logString("Warning! duplicate items detected in " + focDesc.getStorageName());
	            }
            }
          }
        }                                         

        if(addedObject != null){
          if(visitedObjects != null){
            //visitedObjects.put(addedObject, addedObject);
          	visitedObjects.push(addedObject);
          }
        	
          //addedObject.setDesactivateSubjectNotifications(true);
          //addedObject.setLoadedFromDB(true);
          fillFieldsValuesFromResultSet(addedObject, resultSet, arrayOfPropertiesModified);          
          //addedObject.resetStatus();
          //addedObject.setDesactivateSubjectNotifications(false);          
        }
      }

      //Re-Scan properties and notify listeners
      //---------------------------------------
      if (   loadMode == LOAD_IN_EXISTING_LIST
      		|| loadMode == LOAD_IN_EXISTING_LIST_INCREMENTAL){
        if (visitedObjects != null) {
          //Iterator iter = visitedObjects.keySet().iterator();
        	Iterator iter = visitedObjects.valuesIterator();
          while(iter != null && iter.hasNext()){
            FocObject addedObject = (FocObject) iter.next();
            notifyPropertiesListenersForObject(addedObject, arrayOfPropertiesModified);
          }
        }        
      }else if (loadMode == LOAD_IN_EMPTY_LIST){
        for(int i=0; i<focList.size(); i++){
          FocObject addedObject = (FocObject) focList.getFocObject(i);
          notifyPropertiesListenersForObject(addedObject, arrayOfPropertiesModified);
        }
      }else{
        notifyPropertiesListenersForObject(focObjectToBeFilled, arrayOfPropertiesModified);
      }
      //---------------------------------------      
      
      if(arrayOfPropertiesModified != null){
      	arrayOfPropertiesModified.clear();
      	arrayOfPropertiesModified = null;
      }
      
      if(tempObject != null){
        tempObject.setFatherSubject(null);
        tempObject.dispose();
        tempObject = null;
      }
            
      // Removing from the list objects that are not in DBF any more
      if (loadMode == LOAD_IN_EXISTING_LIST) {
        Iterator iter = focList.focObjectIterator();
        while (iter != null && iter.hasNext()) {
          FocObject obj = (FocObject) iter.next();
          //if (visitedObjects.containsKey(obj) == false) {
          if (visitedObjects.contains(obj) == false) {
          	//This means the obj is not found in the DB and is in the List
          	if(!Globals.getApp().isWebServer() || !obj.isCreated()){
          		focList.elementHash_removeCurrentObjectFromIterator(iter,obj);
          	}
          }
        }
        
        focList.setSleepListeners(false);
      }
     
      if(focList != null && !disableReSortAfterAdd_Backup){
      	focList.setDisableReSortAfterAdd(disableReSortAfterAdd_Backup);
      	focList.rebuildArrayList();
      }
      
    } catch (Exception e) {
      Globals.logException(e);
    }
  }
  
  protected void afterTreatResultSet(){
  	
  }
  
  public boolean execute() {
    boolean error = Globals.getDBManager() == null;  
    if(!error && focDesc != null && focDesc.isPersistent() == FocDesc.DB_RESIDENT){   	
      StatementWrapper stmtWrapper = DBManagerServer.getInstance().lockStatement(getDBSourceKey());
  
      ResultSet resultSet = null;
      if (stmtWrapper != null) {
        error = buildRequest();
        if(!error){
          try {
            String reqAdapted = getRequestAdaptedToProvider();
           
            PerfManager.startDBExec();
            long startTimeDBRequest = System.currentTimeMillis();
            if(focList != null && focList.isStoredProcedure()){
            	IFocDataSource iFocDataSource = Globals.getApp().getDataSource();
            	if(iFocDataSource != null){
            		CallableStatement stmt = iFocDataSource.sp_Call(focList.getStoredProcedureName(), focList.getStoredProcedureParams());
            		resultSet = stmt != null ? stmt.getResultSet() : null;
            	}
            }else{
            	stmtWrapper = DBManagerServer.getInstance().executeQuery_WithMultipleAttempts(stmtWrapper, reqAdapted);
            	resultSet = stmtWrapper != null ? stmtWrapper.getResultSet() : null;
            }
            //stmt = resultSet.getStatement(); 
            //resultSet = stmt.executeQuery(reqAdapted);
            long endTimeDBRequest = System.currentTimeMillis();
            Globals.logString(" - SQL DURATION = "+(endTimeDBRequest-startTimeDBRequest));
            PerfManager.endDBExecForRequest(reqAdapted);
          } catch (Exception e) {
            Globals.logException(e);
          }
        }
      }
      
      if(!error){
        if(resultSet != null){
        	PerfManager.startDBRead();
        	long startTimeDBRead = System.currentTimeMillis();
          treatResultSet(resultSet);
          afterTreatResultSet();
          long endTimeDBRead = System.currentTimeMillis();
          Globals.logString(" - SQL READ DURATION = "+(endTimeDBRead-startTimeDBRead));
          PerfManager.endDBRead();
          try{
            resultSet.close();
          }catch(Exception e){
            Globals.logException(e);
          }
        }
      }      
      DBManagerServer.getInstance().unlockStatement(stmtWrapper);
      
    }
    return error;
  }

  public FocList getFocList() {
    return focList;
  }
}