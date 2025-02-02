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
// PROPERTIES
// MAIN
// ADAPT DATABASE
// INVOKE
// INDEX
// FIELD LIST
//    FIELDS
//    KEY FLD
//    ALL FLD
//    ARRAY FIELDS
//    MULTIPLAGUAGE FIELDS
// REFERENCE LOCATION LIST
// LIST
// REVISION
// COMPANY

/*
 * Created on Oct 14, 2004
 */
package com.foc.desc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.fab.gui.details.GuiDetails;
import com.fab.model.table.FieldDefinition;
import com.fab.model.table.FieldDefinitionDesc;
import com.fab.model.table.TableDefinition;
import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.IFocEnvironment;
import com.foc.admin.FocUser;
import com.foc.admin.FocUserDesc;
import com.foc.api.IFocDesc;
import com.foc.business.multilanguage.LanguageValuesFocDesc;
import com.foc.business.printing.gui.PrintingAction;
import com.foc.business.status.IStatusHolder;
import com.foc.business.status.IStatusHolderDesc;
import com.foc.business.status.StatusHolderDesc;
import com.foc.business.workflow.WFSite;
import com.foc.business.workflow.WFSiteDesc;
import com.foc.business.workflow.implementation.ILoggableDesc;
import com.foc.business.workflow.implementation.IWorkflow;
import com.foc.business.workflow.implementation.IWorkflowDesc;
import com.foc.business.workflow.implementation.LoggableDesc;
import com.foc.business.workflow.implementation.WFLog;
import com.foc.business.workflow.implementation.Workflow;
import com.foc.business.workflow.implementation.WorkflowDesc;
import com.foc.business.workflow.map.WFSignature;
import com.foc.business.workflow.map.WFSignatureDesc;
import com.foc.business.workflow.map.WFStage;
import com.foc.business.workflow.map.WFTransactionConfig;
import com.foc.business.workflow.map.WFTransactionConfigDesc;
import com.foc.business.workflow.rights.RightLevel;
import com.foc.business.workflow.rights.RightLevelDesc;
import com.foc.dataSource.store.DataStore;
import com.foc.db.DBIndex;
import com.foc.db.DBManager;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FCloudStorageField;
import com.foc.desc.field.FCompanyField;
import com.foc.desc.field.FDateField;
import com.foc.desc.field.FDateTimeField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldArray;
import com.foc.desc.field.FFieldContainer;
import com.foc.desc.field.FFieldPath;
import com.foc.desc.field.FInLineObjectField;
import com.foc.desc.field.FIntField;
import com.foc.desc.field.FListField;
import com.foc.desc.field.FMasterField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FObjectField;
import com.foc.desc.field.FReferenceField;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.InheritedPropertyGetter;
import com.foc.gui.FPanel;
import com.foc.gui.table.FAbstractTableModel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocLink;
import com.foc.list.FocLinkConditionalForeignKey;
import com.foc.list.FocLinkForeignKey;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;
import com.foc.list.FocListGroupBy;
import com.foc.list.FocListOrder;
import com.foc.list.filter.FocDescForFilter;
import com.foc.plugin.IFocDescPlugIn;
import com.foc.property.FList;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;
import com.foc.shared.dataStore.AbstractDataStore;
import com.foc.shared.dataStore.IFocData;
import com.foc.shared.xmlView.IXMLViewConst;
import com.foc.util.FocMath;

/**
 * @author 01Barmaja
 */
public class FocDesc implements Cloneable, IFocDesc, IFocData {
	
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // PROPERTIES
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

	private String dbSourceKey = null;//Auxiliary Pools
	private int    providerCached      = DBManager.PROVIDER_NONE;
	private int    serverVersionCached = 0;//set to -1 if already read and got 0
	
  private Class<FocObject> focObjectClass = null;
  private Class guiBrowsePanelClass  = null;
  private Class guiDetailsPanelClass = null;
  
  private   boolean      dbResident          = false;
  private   boolean      deprecated          = false;
  protected WorkflowDesc workflowDesc        = null ;
  protected LoggableDesc loggableDesc        = null ;
  private   int          dummyPropertyCount  = 0    ;
	private   boolean      logActive           = false;
	private   boolean      listInCache         = true ;
	private   boolean      allowAdaptDataModel = true ;
	private 	boolean 		 logicalDelete		   = false;
	private   boolean      siteRestrictionAccess = true;//User from Site A cannot see items of Site B
	private   boolean      useOracleListAggCLOB = true;
	
	private FocModule                   module                = null;
	
	private String                      name                  = null;
  private String                      storageName           = "";
  private String                      storageName_ForSQL    = null;
  private String                      reportContext         = null;
  private String                      title                 = "";
  private FFieldContainer             keyFields             = new FFieldContainer();
  private boolean                     isKeyUnique           = true;
  private FFieldContainer             fields                = new FFieldContainer();
  private FFieldContainer             arrayFields           = null;    
  private FField                      identifierField 			= null;
  private HashMap<String, DBIndex>    indexes               = null;
  private ArrayList<ReferenceChecker> referenceLocationList = null;
  private ArrayList<FField>           mandatoryFields       = null;
  private ArrayList<FStringField>       multiLanguageFields   = null;
  private LanguageValuesFocDesc       languageValueFocDesc  = null;
  private IFocDescPlugIn              iFocDescPlugIn        = null;
  private ArrayList<FocObject>        descFocObjects        = null;//global array
  
  private ArrayList<Integer>          cloudStorageFields    = null;          
  
  private int                rightsByLevelMode      = RIGHTS_BY_LEVEL_MODE_NONE;
  private boolean            concurrenceLockEnabled = false;
  private ArrayList<Integer> concurrenceLockView    = null;
  
  private int                masterRefFieldID       = FField.NO_FIELD_ID;
  private static int         slaveFieldListID       = FField.FLD_SLAVE_LIST_FIRST;
  
  private TableDefinition    fabTableDefinition     = null;
  
	// For link Tables descriptions
  private FocLink n2nLink = null;

  private int     propertyArrayLength = 0;
  private boolean addMasterMirror     = false;
  
  final public static boolean DB_RESIDENT = true;
  final public static boolean NOT_DB_RESIDENT = false;

  final public static int LEN_CODE_FOC          = 15;
  final public static int LEN_NAME_FOC          = 20;
  final public static int LEN_EXTERNAL_CODE_FOC = 30;
  final public static int LEN_DESCRIPTION       = 60;
  
  final public static int RIGHTS_BY_LEVEL_MODE_NONE = 0;
  final public static int RIGHTS_BY_LEVEL_MODE_TRACE_ONLY = 1;
  final public static int RIGHTS_BY_LEVEL_MODE_TRACE_AND_ACCESS = 2;
  
	public static final int TABLE_OWNER_CUSTOMER          = 0;
	public static final int TABLE_OWNER_SOFTWARE_PROVIDER = 1;
	public static final int TABLE_OWNER_VAR               = 2;
  
//  final public static String INDEX_IDENTIFIER = "IDENTIFIER";
  
  private FFieldPath revisionPath          = null;
  private boolean    parentRevisionSupport = false;

  private int fObjectTreeFatherNodeID = FField.NO_FIELD_ID;
//  private PrintingAction printingAction = null;
  
	private FocFieldEnum enumer_KeyPlain               = null; 
	private FocFieldEnum enumer_ResetObject            = null;
  private boolean      enumer_ResetObject_DuringScan = false;
  
  //Used for filtering FocLists by transaction type.
  private static int    FOC_DESC_TRANSACTION_TYPE = AbstractDataStore.TRANSACTION_TYPE_NONE;
  private static String FOC_DESC_GUI_CONTEXT      = IXMLViewConst.CONTEXT_DEFAULT;
  
  private FocListGroupBy focListGroupBy = null;
  
  public void dispose(){
    focObjectClass       = null;
    guiBrowsePanelClass  = null;
    guiDetailsPanelClass = null;
    n2nLink = null;
    identifierField = null;
    
    storageName = null;
    title = null;
    
    if(indexes != null){
      indexes.clear();
      indexes = null;
    }
    
    if(fields != null){
      fields.dispose();
      fields = null;
    }
    
    if(keyFields != null){
      keyFields.dispose();
      keyFields = null;  
    }
    
    dispose_MandatoryFieldsArray();
    
    if(arrayFields != null){
      arrayFields.dispose();
      arrayFields = null;  
    }
    
    if(referenceLocationList != null){
      referenceLocationList.clear();
      referenceLocationList = null;
    }
    
    if(concurrenceLockView != null){
      concurrenceLockView.clear();
      concurrenceLockView = null;
    }
        
    if(multiLanguageFields != null){
    	multiLanguageFields.clear();
    	multiLanguageFields = null;
    }
    
//    if(printingAction != null){
//    	printingAction.dispose();
//    	printingAction = null;
//    }
    
    revisionPath = null;
    if(enumer_KeyPlain != null){
    	enumer_KeyPlain.dispose();
    	enumer_KeyPlain = null;
    }
    
    if(descFocObjects != null) {
    	descFocObjects.clear();
    	descFocObjects = null;
    }
  }
  
  public void dispose_MandatoryFieldsArray(){
	  if(mandatoryFields != null){
	    mandatoryFields.clear();
	    mandatoryFields = null;
	  }
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // MAIN
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  private void init(Class focObjectClass, boolean dbResident, String storageName, boolean isKeyUnique, FocLink link, boolean addSyncFields) {
    this.focObjectClass = focObjectClass;
    this.dbResident = dbResident;
    setStorageName(storageName);
    this.isKeyUnique = isKeyUnique;
    this.n2nLink = link;
    this.propertyArrayLength = 0;

    if(Globals.getApp() != null) {
    	Globals.getApp().getFocDescMap().put(storageName, focDesc_getTransactionType(), this);
    }
    if(focObjectClass != null){
    	String className = focObjectClass.getName();
    	if(className == null || className.isEmpty()){
    		Globals.logString("FocDesc declared with Empty FocObject class name");
    	}else {
    		if(Globals.getApp().getFocDescMap_ByFocObjectClassName().get(className) == null){
    			Globals.getApp().getFocDescMap_ByFocObjectClassName().put(className, this);
    		}else{
    			Globals.logDebug("FocDesc already in MAP : "+className);
    		}
    	}
    }else{
//    	Globals.logString("FocDesc declared with Null FocObject class");
    }
    
    if(Globals.getDBManager() != null && !Globals.getDBManager().sync_isNone() && addSyncFields){
    	sync_AddField();
    }
  }

  public FocDesc(Class focObjectClass) {
    init(focObjectClass, false, "", false, null, true);
  }
  
  public FocDesc(Class focObjectClass, boolean dbResident, String storageName, boolean isKeyUnique) {
    init(focObjectClass, dbResident, storageName, isKeyUnique, null, true);
  }

  public FocDesc(Class focObjectClass, boolean dbResident, String storageName, boolean isKeyUnique, boolean deprecated, boolean addSyncFields) {
  	init(focObjectClass, dbResident, storageName, isKeyUnique, null, addSyncFields);
  	this.deprecated = deprecated;
  }

  public FocDesc(Class focObjectClass, boolean dbResident, String storageName, boolean isKeyUnique, boolean deprecated) {
    init(focObjectClass, dbResident, storageName, isKeyUnique, null, true);
    this.deprecated = deprecated;
  }
  
  public FocDesc(Class focObjectClass, boolean dbResident, String storageName, FocLink link, boolean deprecated) {
    init(focObjectClass, dbResident, storageName, false, link, true);
    this.deprecated = deprecated;
  }
  
  public FocDesc(Class focObjectClass, boolean dbResident, String storageName, FocLink link) {
    init(focObjectClass, dbResident, storageName, false, link, true);
  }
  
  public void afterCreation(){
  }
 
  public String getSequenceName() {
  	return getStorageName_ForSQL()+"_SQ";
  }
  
	public boolean isKeyUnique() {
    return isKeyUnique;
  }
	
	public boolean loadCachedListFromServletOnAction_CRUD() {
		return true;
	}
	
	public void setKeyUnique(boolean keyUnique){
		this.isKeyUnique = keyUnique;
	}

  public String getStorageName() {
    return storageName;
  }

  public void setStorageName(String storageName) {
//    this.storageName = storageName.toUpperCase();
    this.storageName = storageName;
    if(name == null) setName(storageName);
  	//if(Globals.getDBManager().getProvider() == DBManager.PROVIDER_MYSQL){
  	//this.storageName = storageName.toLowerCase();
  	//}else{
  	//	this.storageName = storageName.toUpperCase();
  	//}
  }
  
  public void setStorageName_ForSQL(String storageForSQL) {
  	storageName_ForSQL = storageForSQL;
  }
  
  public String getStorageName_ForSQL() {
  	if(storageName_ForSQL == null){
  		if(getProvider() == DBManager.PROVIDER_MSSQL){
  			storageName_ForSQL = storageName;
  		}else if(getProvider() == DBManager.PROVIDER_H2){
  			storageName_ForSQL = storageName;
  		}else if(getProvider() == DBManager.PROVIDER_ORACLE
  				  || getProvider() == DBManager.PROVIDER_POSTGRES){
//  			storageName_ForSQL = storageName.toUpperCase();
  			storageName_ForSQL = storageName;
//  			if(storageName_ForSQL.length() > 30){
//  				storageName_ForSQL = storageName_ForSQL.substring(0, 30);
//  			}
  		}else{
  			storageName_ForSQL = storageName.toLowerCase();
  		}
  	}
  	return storageName_ForSQL;
  }
  
  public boolean isPersistent() {
    return dbResident;
  }

  public boolean getWithReference() {
    return getFieldByID(FField.REF_FIELD_ID) != null;
  }

  public void setFocObjectClass(Class focObjectClass){
  	this.focObjectClass = focObjectClass;
  }
  
  public Class getFocObjectClass() {
    return focObjectClass;
  }
  
  public int getRightsByLevelMode(){
    return rightsByLevelMode;
  }
  
  public void setRightsByLevelEnabled(int rightsByLevelMode) {
    this.rightsByLevelMode = RIGHTS_BY_LEVEL_MODE_NONE;
    if(Globals.getApp().isWithLogin()){
      this.rightsByLevelMode = rightsByLevelMode;
    }
    
    if(this.rightsByLevelMode != RIGHTS_BY_LEVEL_MODE_NONE){
      FObjectField userField = (FObjectField) getFieldByID(FField.RIGHT_LEVEL_USER_FIELD_ID);
      if (userField == null) {
        userField = new FObjectField(FField.RIGHT_LEVEL_USER_FIELD_NAME, FField.RIGHT_LEVEL_USER_FIELD_NAME, FField.RIGHT_LEVEL_USER_FIELD_ID, false, FocUser.getFocDesc(), "USER_");       
        userField.setSelectionList(FocUserDesc.getList(FocList.NONE));
        userField.setDisplayField(FocUserDesc.FLD_NAME);
        userField.setComboBoxCellEditor(FocUserDesc.FLD_NAME);
        addField(userField);
      }
      
      FField levelField = getFieldByID(FField.RIGHT_LEVEL_FIELD_ID);
      if (levelField == null) {
        levelField = new FIntField(FField.RIGHT_LEVEL_FIELD_NAME, FField.RIGHT_LEVEL_FIELD_NAME, FField.RIGHT_LEVEL_FIELD_ID, false, 2);
        addField(levelField);        
      }
      
      FField dateTimeField = getFieldByID(FField.RIGHT_LEVEL_DATETIME_FIELD_ID);
      if (dateTimeField == null) {
        dateTimeField = new FStringField(FField.RIGHT_LEVEL_DATETIME_FIELD_NAME, FField.RIGHT_LEVEL_DATETIME_FIELD_NAME, FField.RIGHT_LEVEL_DATETIME_FIELD_ID, false, 20);
        addField(dateTimeField);
      }
    }
  }
  
  public boolean isConcurrenceLockEnabled() {
    return concurrenceLockEnabled;
  } 
  
  public void setConcurrenceLockEnabled(boolean concurrenceLockEnabled) {
    this.concurrenceLockEnabled = concurrenceLockEnabled && Globals.getApp().isWithLogin();
    if(this.concurrenceLockEnabled){
      FObjectField userField = (FObjectField) getFieldByID(FField.LOCK_USER_FIELD_ID);
      if (userField == null) {
        userField = new FObjectField(FField.LOCK_USER_FIELD_NAME, FField.LOCK_USER_FIELD_NAME, FField.LOCK_USER_FIELD_ID, false, FocUser.getFocDesc(), FField.CONCURRENCY_LOCK_USER_FIELD_PREFIX);
        userField.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
        /*userField.setSelectionList(FocUser.getList(FocList.NONE));
        userField.setDisplayField(FocUserDesc.FLD_NAME);
        userField.setComboBoxCellEditor(FocUserDesc.FLD_NAME);*/
        userField.setWithList(false);
        addField(userField);
      }
    }
    
    this.concurrenceLockEnabled = concurrenceLockEnabled;
  }
  
  public void concurrenceLockView_AddField(int fieldID){
    if(concurrenceLockView == null){
      concurrenceLockView = new ArrayList<Integer>();
    }
    concurrenceLockView.add(Integer.valueOf(fieldID));
  }

  public int concurrenceLockView_FieldNumber(){
    return concurrenceLockView != null ? concurrenceLockView.size() : 0;
  }

  public int concurrenceLockView_FieldAt(int at){
    return concurrenceLockView != null ? ((Integer)concurrenceLockView.get(at)).intValue() : 0;
  }

  public void setFieldSelectionListNotLoaded(){
    FocFieldEnum enumer = newFocFieldEnum(FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
    while (enumer.hasNext()) {
      FField focField = (FField) enumer.next();
      FocList list = focField.getSelectionList();
      if(list != null){
        list.setLoaded(false);
      }
    }
  }
  
  public void fillTableModelWithKeyFields(FAbstractTableModel fTableModel, boolean withSelectionCheckBox) {
    if (fTableModel != null) {
      FTableView view = fTableModel.getTableView();

      if (withSelectionCheckBox) {
        view.addSelectionColumn();
      }

      for (int i = 0; i < getKeyFieldsSize(); i++) {
        FField field = getKeyFieldAt(i);
        if (field != null) {
          view.addColumn(this, field);
        }
      }
    }
  }  
  
  public FocDesc clone() {
    FocDesc zFocDesc = null;
    try {
			zFocDesc = (FocDesc)super.clone();
		} catch (CloneNotSupportedException e) {
			Globals.logException(e);
		}
  	return zFocDesc;
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // ADAPT DATABASE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public void adaptTableAlone(){
  	DBManager dbManager = Globals.getApp().getDBManager();
  	dbManager.adaptTable(this);
  }
  
  public void beforeAdaptTableModel(boolean alreadyExists){
  }

  public void afterAdaptTableModel(){
  }
  
  public void beforeLogin() {
  }

  public void afterLogin() {
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // INVOKE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  @SuppressWarnings("unchecked")
	public static FocDesc getFocDescription(Class focObjectClass) {
    FocDesc focDesc = null;

    try {
      if (focObjectClass != null) {
        //FocObject focObj = (FocObject) focObjectClass.newInstance();
        //FocObject focObj = newClassInstance(null);        
        //focObj.getThisFocDesc();
        Class[] argsDeclare = null;
        Object[] args = null;
        Method methodGetFocDesc = null;
        try{
        	methodGetFocDesc = focObjectClass.getMethod("getFocDesc", argsDeclare);
        }catch(NoSuchMethodException e){
        	Class cls = Class.forName(focObjectClass.getName()+"Desc");
        	methodGetFocDesc = cls.getMethod("getInstance", argsDeclare);
        }
        
        if(methodGetFocDesc != null){
        	focDesc = (FocDesc) methodGetFocDesc.invoke(null, args);
        }
      }
    } catch (Exception e) {
      Globals.getDisplayManager().popupMessage("getFocDescription");      
    	Globals.logString("Exception while getting FocDesc for class : "+focObjectClass.getName());
      Globals.logException(e);
    }
    
    return focDesc;
  }

  @SuppressWarnings("unchecked")
	public static FocDesc getFocDescriptionForDescClassX(Class descClass) {
    FocDesc focDesc = null;

    try {
      if (descClass != null) {
        //FocObject focObj = (FocObject) focObjectClass.newInstance();
        //FocObject focObj = newClassInstance(null);        
        //focObj.getThisFocDesc();
        Class[] argsDeclare = null;
        Object[] args = null;
        Method methodGetFocDesc = null;
        try{
        	methodGetFocDesc = descClass.getMethod("getInstance", argsDeclare);
        }catch(NoSuchMethodException e){
        	methodGetFocDesc = descClass.getMethod("getFocDesc", argsDeclare);
        }
        if(methodGetFocDesc != null){
        	focDesc = (FocDesc) methodGetFocDesc.invoke(null, args);
        }
      }
    } catch (Exception e) {
      Globals.getDisplayManager().popupMessage("getFocDescriptionForDescClassX");
    	Globals.logString("Exception while getting FocDesc for class : "+descClass.getName());
      Globals.logException(e);
    }
    
    return focDesc;
  }
  
  @SuppressWarnings("unchecked")
	public FPanel callNewBrowsePanel(Class guiClass, FocList list, int viewID) {
    FPanel panel = null;
    try {
      if (guiClass != null) {
        Class[] param = new Class[2];
        param[0] = FocList.class;
        param[1] = Integer.TYPE;
        
        Object[] args = new Object[2];
        args[0] = list;
        args[1] = Integer.valueOf(viewID);
        
      	Constructor constr = guiClass.getConstructor(param);
      	panel = (FPanel) constr.newInstance(args);
      }
    } catch (Exception e) {
      Globals.logException(e);
    }
    return panel;
  }
  
  @SuppressWarnings("unchecked")
	public FPanel callNewBrowsePanel(FocList list, int viewID) {
    FPanel panel = null;
    try {
      if (focObjectClass != null) {
        Class[] param = new Class[2];
        param[0] = FocList.class;
        param[1] = Integer.TYPE;
        
        Object[] args = new Object[2];
        args[0] = list;
        args[1] = Integer.valueOf(viewID);
        
        try{
          Method method = null;        	
        	method = focObjectClass.getMethod("newBrowsePanel", param);
          panel = (FPanel) method.invoke(null, args);        	
        }catch(NoSuchMethodException e){
        	Class cls = getGuiBrowsePanelClass();
        	Constructor constr = cls.getConstructor(param);
        	panel = (FPanel) constr.newInstance(args);
        }
      }
    } catch (Exception e) {
      Globals.logException(e);
    }
    return panel;
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // INDEX
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private void indexFill(){
  	if(			getProvider() != DBManager.PROVIDER_ORACLE 
  			&& 	getProvider() != DBManager.PROVIDER_H2
  			&& 	getProvider() != DBManager.PROVIDER_POSTGRES){
	  	String indexRef = Globals.getDBManager().getINDEX_NAME_IDENTIFIER(); 
	    if(indexes.get(indexRef) == null){
	      //IDENTIFIER field
	      FField identifierField = getIdentifierField();
	      if(identifierField != null){
	        DBIndex idIndex = new DBIndex(indexRef, this, true);
	        idIndex.addField(identifierField.getID());
	        indexPut(indexRef, idIndex);
	      }
	    }
  	}

  	String indexName = "MASTER";
//  	if(Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE){
//  		indexName = "FMTR";
//  	}
  	
    if(indexes.get(indexName) == null){
      //MASTER REF field
      FField masterRefField = this.getFieldByID(FField.MASTER_REF_FIELD_ID);
      if(masterRefField == null){
      	masterRefField = this.getFieldByID(getMasterRefFieldID());
      }
      if(masterRefField != null){
        DBIndex masterIndex = new DBIndex(indexName, this, false);
        masterIndex.addField(masterRefField.getID());
        indexPut(indexName, masterIndex);        
      }
    }

    indexName = "MAIN_KEY";
//  	if(Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE){
//  		indexName = "FKEY";
//  	}
  	
    if(indexes.get(indexName) == null){
      //UNIQUE KEY fields
      if(isKeyUnique()){
        DBIndex mainKeyIndex = null;
                
        Iterator iter = newFocFieldEnum(FocFieldEnum.CAT_KEY, FocFieldEnum.LEVEL_PLAIN);
        while(iter != null && iter.hasNext()){
        	if(mainKeyIndex == null){
        		mainKeyIndex = new DBIndex(indexName, this, isKeyUnique());
        	}
        	FField field = (FField)iter.next();
          mainKeyIndex.addField(field.getID());
        }
        if(mainKeyIndex != null){
        	indexPut(indexName, mainKeyIndex);
        }else{
        	Globals.showNotification("Warining", "Missing key fields for "+getStorageName(), IFocEnvironment.TYPE_WARNING_MESSAGE);
        }
      }
    }
  }
  
  private void indexCreate(){
    if(indexes == null){
      indexes = new HashMap<String, DBIndex>();
    }
  }
  
  private void indexPut(String name, DBIndex index) {
  	if(indexes != null && index != null) indexes.put(name, index);
  }
  
  public void indexAdd(DBIndex index){
    indexCreate();
    indexPut(index.getName(), index);
  }
  
  public Iterator indexIterator(){
    Iterator iter = null;
    indexCreate();
    indexFill();
    if(indexes != null && indexes.values() != null){
      iter = indexes.values().iterator();
    }
    return iter;
  }
  
  /*
  public int indexCount(){
    indexInitialize();    
    return indexes != null ? indexes.size() : 0;
  }
  
  public DBIndex indexAt(int i){
    indexInitialize();    
    return indexes != null ? (DBIndex)indexes.get(i) : null;
  }
  */ 
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // FIELD LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  // ---------------------------------
  //    FIELDS
  // ---------------------------------
  public FocFieldEnum newFocFieldEnum(int category, int level) {
    return new FocFieldEnum(this, category, level);
  }

  public FField getIdentifierField() {
    FField refField = getFieldByID(FField.REF_FIELD_ID);
    if (refField == null) {
      refField = identifierField;
    }
    return refField;
  }
  
  public void setIdentifierField(FField identifierField) {
    if(getFieldByID(FField.REF_FIELD_ID) == null){
      this.identifierField = identifierField;      
    }
  }
  
  public FField getFieldByID(int id) {
    FField focField = null;
    focField = fields.getByID(id);
    
    //If Not found we should look for ArrayFields and take the current index
    if(focField == null){
      FFieldArray fldArray = arrayFields != null ? (FFieldArray) arrayFields.getByID(id) : null;
      if(fldArray != null){
        focField = fldArray.getCurrentField();
      }
    }

    return focField;
  }

  public String getFieldNameByID(int id) {
  	FField fld = getFieldByID(id);
  	return fld != null ? fld.getName() : "";
  }

  public String getRefFieldName() {
  	return getFieldNameByID(FField.REF_FIELD_ID);
  }

  public int getFieldIDByName(String name){
  	int fieldID = FField.NO_FIELD_ID;
		FField field = getFieldByName(name);
		if(field != null){
			fieldID = field.getID();
		}
		return fieldID;
  }
  
  public FField getFieldByName(String name) {
    return fields.getByName(name);
  }

  public FField getFieldByDBCompleteName(String name) {
    FField found = null;
    FocFieldEnum enumer = newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_DB);
    while(enumer != null && enumer.hasNext()){
      enumer.next();
      String fldName = enumer.getFieldCompleteName(this);
      if(fldName.compareTo(name) == 0){
        found = getFieldByID(enumer.getFieldPath().get(0));
        break;
      }
    }
    return found;
  }

  public FField getFieldByDBCompleteName_GetDBLevelField(String name) {
    FField found = null;
    FocFieldEnum enumer = newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_DB);
    while(enumer != null && enumer.hasNext()){
      enumer.next();
      String fldName = enumer.getFieldCompleteName(this);
      if(fldName.compareTo(name) == 0){
        found = getFieldByID(enumer.getFieldPath().get(enumer.getFieldPath().size()-1));
        break;
      }
    }
    return found;
  }
  
  public FField getFieldByPath(FFieldPath path) {
    FField retField = null;
    
    FField curField = null;
    FocDesc curDesc = this;
    if (path != null) {
      int i = 0;
      for (i = 0; i < path.size(); i++) {
        int fieldID = path.get(i);
        curField = curDesc != null ? curDesc.getFieldByID(fieldID) : null;
        curDesc = curField != null ? curField.getFocDesc() : null;
      }
      if (i == path.size()) {
        retField = curField;
      }
    }

    return retField;
  }  
  
  public String getFieldGuiName(int fieldID){
    FField fld = getFieldByID(fieldID);
    return getFieldGuiName(fld);
  }
  
  public String getFieldGuiName(FField fld){
    return fld != null ? getStorageName()+"."+fld.getName() : "";    
  }
  
  public FField addReferenceField() {
  	FReferenceField refField = (FReferenceField)getFieldByID(FField.REF_FIELD_ID);
    if (refField == null) {
      refField = new FReferenceField(FField.REF_FIELD_NAME, FField.REF_FIELD_NAME);
      refField.setAutoIncrement(true);
      addField(refField);
    }
    
    return refField;
  }

	public FMultipleChoiceField addFabOwnerField(){
		FMultipleChoiceField mFld = new FMultipleChoiceField("OWNER", "Owner", FField.FLD_FAB_OWNER, false, 2);
		mFld.addChoice(TABLE_OWNER_CUSTOMER, "Customer");
		mFld.addChoice(TABLE_OWNER_SOFTWARE_PROVIDER, "01Barmaja");
		mFld.addChoice(TABLE_OWNER_VAR, "Value Added Reseller");
		mFld.setAllwaysLocked(true);
		addField(mFld);
		return mFld;
	}
  
  public FBoolField addDeprecatedField(String name, String title){
    FBoolField field = (FBoolField)getFieldByID(FField.FLD_DEPRECATED_FIELD);
    if(field == null) {
      field = new FBoolField(name, title, FField.FLD_DEPRECATED_FIELD, false);
      addField(field);
    }
    return field;
  }
  
  public FBoolField addDeprecatedField(){
  	return addDeprecatedField("DEPRECATED", "Deprecate");
  }

  public FBoolField addNotCompletedField(){
    FBoolField field = (FBoolField)getFieldByID(FField.FLD_NOT_COMPLETED_YET);
    if(field == null) {
      field = new FBoolField(FField.FNAME_NOT_COMPLETED_YET, "Not Completed", FField.FLD_NOT_COMPLETED_YET, false);
      addField(field);
      /*field.addListener(new FPropertyListener() {
				@Override
				public void propertyModified(FProperty property) {
					int debug = 3;
					debug++;
				}
				@Override
				public void dispose() {
				}
			});*/
    }
    return field;
  }
  
  /*public FBoolField addNotCompletedField(){
  	return addDeprecatedField("NOT_COMPLETED", "Not Completed");
  }*/
  
  public boolean hasOrderField(){
    return getFieldByID(FField.FLD_ORDER) != null;
  }
  
  public FField addOrderField() {
    FIntField orderField = null;
    if (!hasOrderField()) {
      orderField = new FIntField(FField.ORDER_FIELD_NAME, FField.ORDER_FIELD_NAME, FField.FLD_ORDER, false, 10);
      addField(orderField);
    }
    return orderField;
  }

  public FStringField addNameField(){
  	return addNameField(true);
  }
  
  public FStringField addNameField(boolean key){
  	FStringField field = (FStringField)getFieldByID(FField.FLD_NAME);
    if(field == null) {
    	field = new FStringField(FField.FNAME_NAME, "Name", FField.FLD_NAME, key, LEN_NAME_FOC);
    	field.setLockValueAfterCreation(true);
      addField(field);
    }
  	return field;
  }

  public FDateField addDateField(){
  	return addDateField("DATE");
  }
  
  public FDateField addDateField(String name){
  	FDateField field = (FDateField)getFieldByID(FField.FLD_DATE);
    if(field == null) {
    	field = new FDateField(name, "Date", FField.FLD_DATE, false);
    	field.setLockValueAfterCreation(true);
    	field.setMandatory(true);
      addField(field);
      if(getFieldByID(FField.FLD_CODE) != null){
	      field.addListener(new FPropertyListener() {
					@Override
					public void propertyModified(FProperty property) {
						if(property != null && property.getFocObject() != null && !property.isLastModifiedBySetSQLString()){
							if(property.isManualyEdited()){
								property.getFocObject().code_resetCodeIfPrefixHasChanged();
							}
							//For JV Lock Date
							FocObject focObject = property.getFocObject();
							if(focObject.focObject_IsLocked()){
								if(property.isManualyEdited()){
									Globals.showNotification("Date Modification Not Allowed", "You are moving the transaction prior to a system lock date", IFocEnvironment.TYPE_WARNING_MESSAGE);
								}
								if(focObject.isCreated()){
									property.setValue(Globals.getApp().getSystemDate());
								}else{
									property.restore();
								}
							}
							//----------------
						}
						
					}
					
					@Override
					public void dispose() {
					}
				});
      }
    }
  	return field;
  }  
  
  public FStringField addExternalCodeField(){
  	FStringField field = (FStringField)getFieldByID(FField.FLD_EXTERNAL_CODE);
    if(field == null) {
    	field = new FStringField(FField.FNAME_EXTERNAL_CODE, "Ext Code", FField.FLD_EXTERNAL_CODE, false, LEN_EXTERNAL_CODE_FOC);
      addField(field);
    }
  	return field;
  }
  
  //Review Fields
  public FMultipleChoiceField addReviewStatusField(){
  	FMultipleChoiceField field = (FMultipleChoiceField)getFieldByID(FField.FLD_REVIEWSTATUS);
    if(field == null) {
    	field = new FMultipleChoiceField("REVIEW_STATUS", "Review Status", FField.FLD_REVIEWSTATUS, false, 2);
    	field.addChoice(0, "");
    	if(ConfigInfo.isArabic()){
    		field.addChoice(1, "للمراجعة");
    	}else{
    		field.addChoice(1, "Review");
    	}    	
      addField(field);
    }
  	return field;
  }
  
  public FStringField addReviewCommentField(){
  	FStringField field = (FStringField)getFieldByID(FField.FLD_REVIEWCOMMENT);
    if(field == null) {
    	field = new FStringField("REVIEW_COMMENT", "Review Comment", FField.FLD_REVIEWCOMMENT, false, 2000);
      addField(field);
    }
  	return field;
  }
  //End Review Fields

  public FStringField addCodeField(){
  	return addCodeField(true);
  }
  
  public FStringField addCodeField(boolean key){
  	return addCodeField(key, LEN_CODE_FOC);
  }
  
  public FBoolField addIsSystemObjectField(){
  	FBoolField boolField = (FBoolField) getFieldByID(FField.FLD_IS_SYSTEM_OBJECT);
  	if(boolField == null){
  		boolField = new FBoolField(FField.FNAME_IS_SYSTEM_OBJECT, "Is System Object", FField.FLD_IS_SYSTEM_OBJECT, false);
  		addField(boolField);
  	}
  	return boolField;
  }

  public boolean hasCodeField(){
  	return getFieldByID(FField.FLD_CODE) != null;
  }
  
  public FStringField addCodeField(boolean key, int length){
  	FStringField field = (FStringField)getFieldByID(FField.FLD_CODE);
    if(field == null) {
    	field = new FStringField(FField.FNAME_CODE, "Code", FField.FLD_CODE, key, length);
    	field.setLockValueAfterCreation(true);
    	field.setMandatory(true);
    	field.addListener(new FPropertyListener() {
				
				@Override
				public void propertyModified(FProperty property) {
					if(property != null && !property.isLastModifiedBySetSQLString()){
						FocObject focObject = property.getFocObject();
						if(focObject instanceof IStatusHolder && ((IStatusHolder) focObject).getStatusHolder() != null){
							if(((IStatusHolder) focObject).getStatusHolder().getStatus() == StatusHolderDesc.STATUS_PROPOSAL){
								focObject.code_copyProposalCode(false);
							}
						}
					}
				}
				
				@Override
				public void dispose() {
					
				}
			});
      addField(field);
    }
  	return field;
  }
  
  public FStringField addProposalCodeField(boolean key){
  	FStringField field = (FStringField)getFieldByID(FField.FLD_PROPOSAL_CODE);
    if(field == null) {
    	field = new FStringField("PROPOSAL_CODE", "Proposal Code", FField.FLD_PROPOSAL_CODE, key, LEN_CODE_FOC);
    	field.setLockValueAfterCreation(true);
      addField(field);
    }
  	return field;
  }

  public FStringField addDescriptionField(int fld, boolean key){
  	FStringField field = (FStringField)getFieldByID(fld);
    if(field == null){
    	field = new FStringField(FField.FNAME_DESCRIPTION, "Description", fld, key, LEN_DESCRIPTION);
      addField(field);
    }
  	return field;
  }

  public FStringField addDescriptionField(){
  	return addDescriptionField(FField.FLD_DESCRIPTION, false);
  }

  public FField addMasterReferenceField(FocDesc masterDesc) {
    FField masterField = getFieldByID(FField.MASTER_REF_FIELD_ID);    
    try{
      if (masterField == null && masterDesc != null) {
        FField masterRefFieldInMaster = null;
  
        // Getting the master reference field in the master description
        FocFieldEnum focFieldEnum = masterDesc.newFocFieldEnum(FocFieldEnum.CAT_REF, FocFieldEnum.LEVEL_DB);
        if (focFieldEnum != null && focFieldEnum.hasNext()) {
          masterRefFieldInMaster = (FField) focFieldEnum.next();
        }
  
        // Duplicate - change the name - add
        if (masterRefFieldInMaster != null) {
          masterField = (FField)masterRefFieldInMaster.clone();
          masterField.setId(FField.MASTER_REF_FIELD_ID);
          masterField.setName(FField.MASTER_REF_FIELD_NAME);
          this.addField(masterField);
          
          if(isAddMasterMirror()){
	          FMasterField masterMirrorField = new FMasterField(masterDesc);
	          this.addField(masterMirrorField);
          }
        }
      }
    }catch(Exception e){
      Globals.logException(e);
    }
    return masterField;
  }
  
  public FocLink getListFieldLink(int fieldId){
    FocLink link = null;
    FListField listField = (FListField) getFieldByID(fieldId);
    if(listField != null){
      link = listField.getLink();
    }
    return link;
  }
  
  public final void afterConstructionInternal_1(){
  	afterConstruction_1();
  }
  
  protected void afterConstruction_1(){
  	
  }
  
  public final void afterConstructionInternal_2(){
  	afterConstruction();
  	scanFieldsAndCreateFormulas();//This should be after the afterConstruction otherwize the formulas of fields comming from undParam will not be hooked
    if(getIFocDescPlugIn() != null){
    	getIFocDescPlugIn().afterConstruction(this);
    }
  }
  
  protected void afterConstruction(){
  	
  }
  
  protected void scanFieldsAndCreateFormulas(){
  	FocFieldEnum enumeration = newFocFieldEnum(FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
  	while(enumeration != null && enumeration.hasNext()){
  		FField field = enumeration.nextField();
  		if(field != null){
  			field.createFormula(this);
  		}
  	}
  }

  // ---------------------------------
  //    KEY FLD
  // ---------------------------------

  private ArrayList getMandatoryFields(){
    if(mandatoryFields == null){
      mandatoryFields = new ArrayList<FField>();
      for(int i=0; i<getFieldsSize(); i++){
        FField field = getFieldAt(i);
        if(field.isMandatory()){
          mandatoryFields.add(field);
        }
      }
    }
    return mandatoryFields;
  }
  
  public int mandatoryFieldCount(){
    ArrayList arrList = getMandatoryFields();
    return arrList != null ? arrList.size() : 0;
  }

  public FField mandatoryFieldAt(int i){
    ArrayList arrList = getMandatoryFields();
    return arrList != null ? (FField) arrList.get(i) : null;
  }
  
  // ---------------------------------
  //    KEY FLD
  // ---------------------------------

  private void addFieldToKey(FField keyFld) {
    keyFields.add(keyFld);
    keyFld.setKey(true);
    //if (keyFields.size() > 1 && this.isKeyUnique()) {
    //  this.addReferenceField();
    //}
  }

  public void removeFieldFromKey(FField keyFld) {
    keyFields.remove(keyFld);
    keyFld.setKey(false);
  }  
  
  public int getKeyFieldsSize() {
    return keyFields.size();
  }

  public FField getKeyFieldAt(int i) {
    return (keyFields != null) ? (FField) keyFields.get(i) : null;
  }
  
  public void setFieldAsNonKey(FField nonKeyField){
  	nonKeyField.setKey(false);
  	removeFieldFromKey(nonKeyField);
  }
  
  // ---------------------------------
  //    ALL FLD
  // ---------------------------------

  public int getFieldsSize() {
    return fields.size();
  }

  public FField getFieldAt(int i) {
    return (fields != null) ? (FField) fields.get(i) : null;
  }

  public void addField(FField fld) {
  	if(getFieldByID(fld.getID()) != null && fld.getID() != FField.FLD_SYNC_IS_NEW_OBJECT){
  		try{
  			throw new Exception("ADDING 2 FIELDS WITH SAME ID IN TABLE : "+getStorageName()+" PREV = "+getFieldByID(fld.getID()).getDBName()+" NEW = "+fld.getDBName());
  		}catch(Exception e){
  			Globals.logException(e);
  		}
  	}
//  	if(getFieldByName(fld.getName()) != null /*&& fld.getID() != FField.FLD_SYNC_IS_NEW_OBJECT*/){
//  		try{
//  			throw new Exception("ADDING 2 FIELDS "+fld.getName()+" WITH SAME NAME IN TABLE : "+getStorageName()+" PREV ID= "+getFieldByName(fld.getName()).getID()+" NEW ID = "+fld.getID());
//  		}catch(Exception e){
//  			Globals.logException(e);
//  		}
//  	}
  	
    if(fld.getClass() == FFieldArray.class){
      
    }else{
      fld.setIndexOfPropertyInArray(propertyArrayLength++);
      fields.add(fld);
      if (fld.getKey()) {
        addFieldToKey(fld);
      }
      
      if(fld.getClass() == FInLineObjectField.class){
        FInLineObjectField inLineField = (FInLineObjectField) fld;
        inLineField.getFocDesc().setPropertyArrayLength(inLineField.getFocDesc().getPropertyArrayLength()+1);//+1:pour reserver un field pour le maser object
        //NON NON NON parce que ca cree une column dans la table inLineField.getFocDesc().addMasterReferenceField(this);
      }
      
      if(fld instanceof FCloudStorageField){
      	getCloudStorageFields(true).add(fld.getID());
      }
    }
    
    if(fld != null) fld.setFocDescParent(this);
  }
  
  public void removeField(FField fld){
  	fields.remove(fld);
  	removeFieldFromKey(fld);
  }

  // ---------------------------------
  //    ARRAY FIELDS
  // ---------------------------------
  /*
  public void addFieldArray(FFieldArray fieldArray){
    if(arrayFields == null){
      arrayFields = new FFieldContainer();
    }
    fieldArray.setIndexOfPropertyInArray(propertyArrayLength++);
    arrayFields.add(fieldArray);
    for(int i=0; i<fieldArray.getSize(); i++){
      FField fld = fieldArray.getFieldAt(i);
      addField(fld);
    }
  }

  public FField getFieldArrayByID(int id){
    return arrayFields != null ? arrayFields.getByID(id) : null;
  }  
	*/
  
  // ---------------------------------
  //    MULTIPLAGUAGE FIELDS
  // ---------------------------------
  public boolean isMultiLanguage(){
  	return multiLanguageFields_size() > 0;
  }
  
  private ArrayList<FStringField> multiLanguageFields_getArray(boolean create){
  	if(multiLanguageFields == null && create){
  		multiLanguageFields = new ArrayList<FStringField>();
  	}
  	return multiLanguageFields;
  }
  
  public void multiLanguageFields_add(FStringField charField){
  	multiLanguageFields_getArray(true).add(charField);
  }
  
  public int multiLanguageFields_size(){
  	ArrayList<FStringField> arr = multiLanguageFields_getArray(false);
  	return arr != null ? arr.size() : 0; 
  }

  public FStringField multiLanguageFields_getAt(int i){
  	ArrayList<FStringField> arr = multiLanguageFields_getArray(false);
  	return arr != null ? arr.get(i) : null; 
  }
  
  public LanguageValuesFocDesc getLanguageValueFocDesc(){
  	if(languageValueFocDesc == null && isMultiLanguage()){
  		languageValueFocDesc = new LanguageValuesFocDesc(this); 
  	}
  	return languageValueFocDesc;
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // REFERENCE LOCATION LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public ArrayList<ReferenceChecker> getReferenceLocationList(boolean create){
    if(referenceLocationList == null && create){
      referenceLocationList = new ArrayList<ReferenceChecker>();
    }
    return referenceLocationList;
  }
  
  public void addReferenceLocation(ReferenceChecker refLoc){
    ArrayList<ReferenceChecker> list = getReferenceLocationList(true);
    if(refLoc != null && list != null){
      list.add(refLoc);
    }
  }

  public void copyReferenceLocations(FocDesc sourceDesc){
  	if(sourceDesc  != this){
	  	Iterator<ReferenceChecker> iter = sourceDesc.referenceLocationIterator();
	  	while(iter != null && iter.hasNext()){
	  		try{
	  			ReferenceChecker refChecker = (ReferenceChecker)((ReferenceChecker)iter.next().clone());
					addReferenceLocation(refChecker);
				}catch (CloneNotSupportedException e){
					Globals.logException(e);
				}
	  	}
  	}
  }

  public Iterator referenceLocationIterator(){
    Iterator iter = null;
    ArrayList list = getReferenceLocationList(false);
    if(list != null){
      iter = list.iterator();
    }
    return iter;
  }
  
  public boolean isJoin(){
  	return false;
  }
  
  public void scanFieldsAndAddReferenceLocations(){
  	if(isDbResident() && !isJoin()){
	    for(int i=0; i<getFieldsSize(); i++){
	      FField focField = getFieldAt(i);
	      if(focField.isDBResident()){
	        focField.addReferenceLocations(this);
	      }
	    }
  	}
    
    /*
    FocFieldEnum enumer = newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_PLAIN);
    while (enumer.hasNext()) {
      FField focField = (FField) enumer.next();      
      focField.addReferenceLocations(this);
    }
    */
  }
  
  public String getFieldName_ForList(){
    String fieldName = getStorageName();
    fieldName += FField.LIST_FIELD_SUFFIX;
    return fieldName; 
  }
  
  public FListField addListField(FFieldPath descPropertyPath, int listIdInMaster, FocDescForFilter focDescForFilter){
    FListField listField = null;
    FocLinkConditionalForeignKey focLink = new FocLinkConditionalForeignKey(descPropertyPath, true);
    if(focLink != null){
      String fieldName = focLink.getSlaveDesc().getFieldName_ForList();
      listField = new FListField(fieldName, fieldName, listIdInMaster, focLink, focDescForFilter);
      addField(listField);
    }
    return listField;
  }
  
  public FListField addListField(FocDesc slaveDesc, int foreignKeyId, int listIdInMaster, FocDescForFilter focDescForFilter){
    FListField listField = null;
    FocLinkForeignKey focLink = new FocLinkForeignKey(slaveDesc, foreignKeyId, true);
    if(focLink != null){
      String fieldName = focLink.getSlaveDesc().getFieldName_ForList();
      listField = new FListField(fieldName, fieldName, listIdInMaster, focLink, focDescForFilter);
      addField(listField);
    }
    return listField;
  }
  
  public FListField addListField(FocDesc slaveDesc, int foreignKeyId, int listIdInMaster){
    return addListField(slaveDesc, foreignKeyId, listIdInMaster, null);    
  }
  
  public FocLink getN2nLink() {
    return n2nLink;
  }  
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public int getPropertyArrayLength() {
    return propertyArrayLength;
  }
  
  public void setPropertyArrayLength(int propertyArrayLength) {
    this.propertyArrayLength = propertyArrayLength;
  }

	public boolean isDbResident() {
		return dbResident;
	}

	public void setDbResident(boolean dbResident) {
		this.dbResident = dbResident;
	}
	
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public int focDesc_getTransactionType(){
  	return FOC_DESC_TRANSACTION_TYPE;
  }
  
  public String focDesc_getGuiContext(){
  	return FOC_DESC_GUI_CONTEXT;
  }

  public FocList newFocList(){
  	FocList focList = null;
    FocLink link = new FocLinkSimple(this);
    focList = new FocList(null, link, null);
    return focList;
  }

  public FocList getFocList(int mode){
    return getList(null, mode, null);
  }
  
  public FocList getFocList(){
  	return getFocList(null);
  }
  
  public FocList getFocList(String context){
  	FocList focList = null;
  	if(isListInCache()){
	  	focList = (FocList) DataStore.getInstance().getList(getStorageName(), focDesc_getTransactionType());
	  	if(focList == null){
	      focList = newFocList();
	      focList.setCheckIfRecentEnough(ConfigInfo.isRefreshCachedLists());
	      DataStore.getInstance().putList(getStorageName(), focList, focDesc_getTransactionType());
	  	}
  	}
  	return focList;
  }
  
  public FocList getList(FocList focList, int mode){
    return getList(focList, mode, null);
  }

  public FocList getList(FocList focList, int mode, FocListOrder order){
    if(focList == null){
      focList = getFocList();
    }
    
    if(focList != null){
	    if(mode == FocList.LOAD_IF_NEEDED){
	      focList.loadIfNotLoadedFromDB();      
	    }else if(mode == FocList.FORCE_RELOAD){
	      //focList.removeAll();
	      focList.reloadFromDB();
	    }

	    if(order != null){
	    	focList.setListOrder(order);
	    }else{
	    	focList.sort();
	    }
    }
    
    return focList;
  }

	public void setGuiBrowsePanelClass(Class focObjectBrowsePanelClass) {
		this.guiBrowsePanelClass = focObjectBrowsePanelClass;
	}

	public void setGuiDetailsPanelClass(Class focObjectDetailsPanelClass) {
		this.guiDetailsPanelClass = focObjectDetailsPanelClass;
	}

	public Class getGuiBrowsePanelClass() {
		return guiBrowsePanelClass;
	}

	public Class getGuiDetailsPanelClass() {
		return guiDetailsPanelClass;
	}

	public boolean isAddMasterMirror() {
		return addMasterMirror;
	}

	public void setAddMasterMirror(boolean addMasterMirror) {
		this.addMasterMirror = addMasterMirror;
	}

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // REVISION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  public boolean isRevisionSupportEnabled() {
    return getRevisionPath() != null;
  } 
  
  public boolean isParentRevisionSupportEnabled() {
    return parentRevisionSupport;
  }
  
  public FFieldPath getRevisionPath(){
    return revisionPath;
  }
  
  public void setChildRevisionSupportEnabled(FFieldPath revisionPath/*boolean revisionSupport*/) {
    this.revisionPath = revisionPath;

    FIntField creationRevisionField = (FIntField) getFieldByID(FField.CREATION_REVISION_FIELD_ID);
    FIntField deletionRevisionField = (FIntField) getFieldByID(FField.DELETION_REVISION_FIELD_ID);
    FObjectField newItemField = (FObjectField) getFieldByID(FField.NEW_ITEM_FIELD_ID);
    
    if (creationRevisionField == null && deletionRevisionField == null && newItemField == null ) {
      
      creationRevisionField = new FIntField(FField.CREATION_REVISION_FIELD_ID_NAME, FField.CREATION_REVISION_FIELD_ID_NAME, FField.CREATION_REVISION_FIELD_ID, false, 10);
      addField(creationRevisionField);
      
      deletionRevisionField = new FIntField(FField.DELETION_REVISION_FIELD_ID_NAME, FField.DELETION_REVISION_FIELD_ID_NAME, FField.DELETION_REVISION_FIELD_ID, false, 10);
      addField(deletionRevisionField);
      
      newItemField = new FObjectField(FField.NEW_ITEM_FIELD_ID_NAME, FField.NEW_ITEM_FIELD_ID_NAME, FField.NEW_ITEM_FIELD_ID, false, this, FField.NEW_ITEM_FIELD_ID_NAME_PREFIX);
      newItemField.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
      newItemField.setComboBoxCellEditor(FField.REF_FIELD_ID);
      addField(newItemField);
    }
  }
  
  public void setParentRevisionSupportEnabled() {
    this.parentRevisionSupport = true;
    if(this.parentRevisionSupport){
      FIntField revisionField = (FIntField) getFieldByID(FField.REVISION_FIELD_ID);
      if (revisionField == null ) {
        revisionField = new FIntField(FField.REVISION_FIELD_ID_NAME, FField.REVISION_FIELD_ID_NAME, FField.REVISION_FIELD_ID, false, 5);
        addField(revisionField);
      }
    }
  }
  
  /*
  private class CustomizedInheritanceListener implements FPropertyListener {
  	private int originalField   = 0;
  	private int customizedField = 0;
  	
  	public CustomizedInheritanceListener(int originalField, int customizedField){
  		this.originalField   = originalField  ;
  		this.customizedField = customizedField;
  	}
  	
		public void dispose() {
		}

		public void propertyModified(FProperty property) {
			FocObject object   = property.getFocObject();
			FProperty origProp = object.getFocProperty(originalField);
			if(origProp != null){
				origProp.setProp
			}
		}
  }
  */
  
  private static InheritedPropertyGetter standardInheritedPropertyGetter = null;
  private InheritedPropertyGetter getStandardInheritedPropertyGetter(){
  	if(standardInheritedPropertyGetter == null){
  		standardInheritedPropertyGetter = new InheritedPropertyGetter(){
  			public FProperty getInheritedProperty(FocObject object, FProperty property){
  				if(isTreeDesc()){
  					while(object != null && property != null && property.isInherited()){
  						object   = object.getFatherObject();
  						if(object != null){
  							property = object.getFocProperty(property.getFocField().getID());
  						}
  					}
  				}
  				
  				return property;
  			}
      };
  	}
  	return standardInheritedPropertyGetter;
  }
  
  public FBoolField addInheritanceField(int fieldID, int inheritanceFieldID){
    return addInheritanceField(fieldID, inheritanceFieldID, getStandardInheritedPropertyGetter());
  }
  	
  public FBoolField addInheritanceField(int fieldID, int inheritanceFieldID, InheritedPropertyGetter inheritedValueGetter){
  	FField originalField = getFieldByID(fieldID);
  	FBoolField boolField = new FBoolField(originalField.getName()+"_CUSTOM", "Cust", inheritanceFieldID, false);
  	boolField.setDBResident(originalField.isDBResident());
    addField(boolField);
    originalField.setInheritanceField(boolField, inheritedValueGetter);
    return boolField;
  }
  
  public FBoolField addNodeCollapseField(){
  	FBoolField boolField = new FBoolField("NODE_COLLAPSE", "Node Collapse", FField.FLD_NODE_COLLAPSE, false);
    addField(boolField);
    return boolField;
  }

  public boolean isWithNodeCollapseField(){
    return getFieldByID(FField.FLD_NODE_COLLAPSE) != null;
  }
  
  public FBoolField addLogicalDeleteFields(){
  	FBoolField deletionStateField = new FBoolField(FField.LOGICAL_DELETE_FIELD_NAME, "Logically Deleted", FField.FLD_LOGICAL_DELETE, false);
    addField(deletionStateField);
    
  	FDateTimeField deletionDateField = new FDateTimeField(FField.LOGICAL_DELETE_DATE_FIELD_NAME, "Logically Deleted Date Time", FField.FLD_LOGICAL_DELETE_DATE, false);
    addField(deletionDateField);
    
    FObjectField fObjectFld = new FObjectField(FField.LOGICAL_DELETE_USER_FIELD_NAME, "Deleted By User", FField.FLD_LOGICAL_DELETE_USER, FocUser.getFocDesc());
    fObjectFld.setComboBoxCellEditor(FocUserDesc.FLD_NAME);
    fObjectFld.setDisplayField(FocUserDesc.FLD_NAME);
    fObjectFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    fObjectFld.setSelectionList(FocUserDesc.getList(FocList.NONE));
    fObjectFld.setAllwaysLocked(true);
    addField(fObjectFld);
    
    return deletionStateField;
  }

  protected FObjectField setWithObjectTree(boolean isKeyField){
  	FObjectField fatherNode = new FObjectField(FField.FATHER_NODE_FIELD_NAME, FField.FATHER_NODE_FIELD_NAME, FField.FLD_FATHER_NODE_FIELD_ID, isKeyField, this, FField.FATHER_NODE_FIELDPREFIX);
    fatherNode.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    fatherNode.setComboBoxCellEditor(FField.REF_FIELD_ID);
    addField(fatherNode);
    setFObjectTreeFatherNodeID(FField.FLD_FATHER_NODE_FIELD_ID);
    return fatherNode;
  }

  public FObjectField setWithObjectTree(){
  	return setWithObjectTree(false);
  }
  
  public int getFObjectTreeFatherNodeID() {
    return fObjectTreeFatherNodeID;
  }

  public void setFObjectTreeFatherNodeID(int objectTreeFatherNodeID) {
    fObjectTreeFatherNodeID = objectTreeFatherNodeID;
  }
  
  public boolean isTreeDesc(){
    return fObjectTreeFatherNodeID > 0 || fObjectTreeFatherNodeID == FField.FLD_FATHER_NODE_FIELD_ID;
  }

  public boolean isDeprecated() {
    return deprecated;
  }

  public void setDeprecated(boolean deprecated) {
    this.deprecated = deprecated;
  }

  public FocObject newObject(int ref){
  	return newObject(ref, false);
  }
  
  public FocObject newObject(int ref, boolean shared){
  	FocObject focObject = null;
  	if(ref > 0){
	  	FocConstructor constr = new FocConstructor(this, null);
	  	focObject = constr.newItem();
	  	focObject.getReference().setInteger(ref);
	  	focObject.setShared(shared);
	  	focObject.load();
	  	focObject.adjustPropertyLocksAccordingToCreationFlag();
  	}
    return focObject;
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // COMPANY
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public boolean isByCompany(){
  	return getFieldByID(FField.FLD_COMPANY) != null;
  }
  
  public boolean isCompanyMandatory(){
  	FCompanyField compField = (FCompanyField) getFieldByID(FField.FLD_COMPANY);
  	return compField != null && compField.isMandatory();
  }

	public void addDummyProperty(FField fld){
		fld.setReflectingField(true);
		fld.setIndexOfPropertyInDummyArray(dummyPropertyCount++);
	}

	public void addDummyProperty(int fieldID){
		FField fld = getFieldByID(fieldID);
		fld.setReflectingField(true);
		fld.setIndexOfPropertyInDummyArray(dummyPropertyCount++);
	}
	
	public int getDummyPropertyCount(){
		return dummyPropertyCount;
	}
	
	//---------------------------------------------
	// LOG
	//---------------------------------------------
	public void log_AddLogFields(){
		logActive = true;
		
    FObjectField fObjectFld = new FObjectField("LOG_CREATION_USER", "Creation User", FField.FLD_CREATION_USER, FocUser.getFocDesc());
    fObjectFld.setComboBoxCellEditor(FocUserDesc.FLD_NAME);
    fObjectFld.setDisplayField(FocUserDesc.FLD_NAME);
    fObjectFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    fObjectFld.setSelectionList(FocUserDesc.getList(FocList.NONE));
    fObjectFld.setAllwaysLocked(true);
    addField(fObjectFld);

    FDateTimeField dateTimeField = new FDateTimeField("LOG_CREATION_TIME", "Creation time", FField.FLD_CREATION_TIME, false);
    dateTimeField.setAllwaysLocked(true);
    addField(dateTimeField);

    fObjectFld = new FObjectField("LOG_MODIF_USER", "Modification User", FField.FLD_MODIFICATION_USER, FocUser.getFocDesc());
    fObjectFld.setComboBoxCellEditor(FocUserDesc.FLD_NAME);
    fObjectFld.setDisplayField(FocUserDesc.FLD_NAME);
    fObjectFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    fObjectFld.setSelectionList(FocUserDesc.getList(FocList.NONE));
    fObjectFld.setAllwaysLocked(true);
    addField(fObjectFld);
    
    dateTimeField = new FDateTimeField("LOG_MODIF_TIME", "Modification time", FField.FLD_MODIFICATION_TIME, false);
    dateTimeField.setAllwaysLocked(true);
    addField(dateTimeField);
	}
	
	public boolean log_IsActive(){
		return logActive;
	}

	// ---------------------------------------------
	// ---------------------------------------------
	// Workflow
	// ---------------------------------------------
	// ---------------------------------------------

	public boolean statusHodler_IsStatusHolder() {
		return workflow_IsWorkflowSubject() && this instanceof IStatusHolderDesc;
	}
	
	public boolean workflow_IsWorkflowSubject() {
		return workflowDesc != null;
	}
	
	public boolean workflow_IsLoggable() {
		return workflowDesc != null || loggableDesc != null;
	}
	
	private int workflow_GetStatusForRigtsCheck(FocObject focObject){
		return focObject instanceof IStatusHolder ? ((IStatusHolder)focObject).getStatusHolder().getStatus() : StatusHolderDesc.STATUS_APPROVED; 
	}

	public FocList workflow_NewSiteListWithReadAccess(){
		FocList list = new FocList(new FocLinkSimple(WFSiteDesc.getInstance()));
		list.setCollectionBehaviour(true);
		if(list != null){
			FocList allSiteList = WFSiteDesc.getList(FocList.LOAD_IF_NEEDED);
			for(int i=0; i<allSiteList.size(); i++){
				WFSite site = (WFSite) allSiteList.getFocObject(i);
				RightLevel rightLevel = workflowDesc != null ? workflowDesc.getRightLevel(site) : null;
				if(rightLevel != null && rightLevel.isAllowRead()){
					list.add(site);
				}
			}
		}
		return list;
	}
	
	private boolean workflow_IsAllow_Action(FocObject focObject, int fieldForDraft, int fieldForApprove){
		boolean allow = true;
		if(workflow_IsWorkflowSubject()){
			allow = false;
			if(focObject != null){
				IWorkflow workflow = (IWorkflow) focObject;
				if(workflow != null && workflow.iWorkflow_getWorkflow() != null){
					RightLevel rightLevel = workflowDesc != null ? workflowDesc.getRightLevel(workflow.iWorkflow_getWorkflow().getArea()) : null;
					if(rightLevel != null){
						if(workflow_GetStatusForRigtsCheck(focObject) <= StatusHolderDesc.STATUS_PROPOSAL){
							allow = rightLevel.getPropertyBoolean(fieldForDraft);
						}else if(workflow_GetStatusForRigtsCheck(focObject) == StatusHolderDesc.STATUS_CLOSED){
							if(			fieldForDraft == RightLevelDesc.FLD_RESET_TO_PROPOSAL
									|| 	fieldForDraft == RightLevelDesc.FLD_RESET_TO_APPROVED) {
								allow = rightLevel.getPropertyBoolean(fieldForDraft);
							} else{
								allow = false;
							}
						}else{
							allow = rightLevel.getPropertyBoolean(fieldForApprove);
						}
					}
				}
			}else{
				RightLevel rightLevel = workflowDesc != null ? workflowDesc.getRightLevel(Globals.getApp().getCurrentSite()) : null;
				if(rightLevel != null){
					allow = rightLevel.getPropertyBoolean(fieldForDraft) || rightLevel.getPropertyBoolean(fieldForApprove);
				}				
			}
		}
		return allow;
	}

	private boolean workflow_IsAllow_Action(int fieldID){
		boolean allow = true;
		if(workflow_IsWorkflowSubject()){
			allow = false;
			RightLevel rightLevel = workflowDesc != null ? workflowDesc.getRightLevel(Globals.getApp().getCurrentSite()) : null;
			if(rightLevel != null){
				allow = rightLevel.getPropertyBoolean(fieldID);
			}
		}
		return allow;
	}

	public boolean workflow_IsAllowViewLog(){
		if(workflow_IsWorkflowSubject()) {
			return workflow_IsAllow_Action(RightLevelDesc.FLD_VIEW_LOG);
		} else if(workflow_IsLoggable()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean workflow_IsAllowInsert(){
		return workflow_IsAllow_Action(RightLevelDesc.FLD_INSERT);
	}
	
	public boolean workflow_IsAllowRead(FocObject focObject){
		return workflow_IsAllow_Action(focObject, RightLevelDesc.FLD_READ, RightLevelDesc.FLD_READ);
	}

	public boolean workflow_IsUndoSignature(FocObject focObject){
		return workflow_IsAllow_Action(focObject, RightLevelDesc.FLD_UNDO_SIGNATURE, RightLevelDesc.FLD_UNDO_SIGNATURE);
	}
	
	public boolean workflow_IsAllowModify(FocObject focObject){
		return workflow_IsAllow_Action(focObject, RightLevelDesc.FLD_MODIFY_DRAFT, RightLevelDesc.FLD_MODIFY_APPROVED);
	}

	public boolean workflow_IsAllowDelete(FocObject focObject){
		return workflow_IsAllow_Action(focObject, RightLevelDesc.FLD_DELETE_DRAFT, RightLevelDesc.FLD_DELETE_APPROVED);
	}

	public boolean workflow_IsAllowClose(FocObject focObject){
		return workflow_IsAllow_Action(focObject, RightLevelDesc.FLD_CLOSE, RightLevelDesc.FLD_CLOSE);
	}

	public boolean workflow_IsAllowApprove(FocObject focObject){
		return workflow_IsAllow_Action(focObject, RightLevelDesc.FLD_APPROVE, RightLevelDesc.FLD_APPROVE);
	}

	public boolean workflow_IsAllowResetToProposal(FocObject focObject){
		return workflow_IsAllow_Action(focObject, RightLevelDesc.FLD_RESET_TO_PROPOSAL, RightLevelDesc.FLD_RESET_TO_PROPOSAL);
	}
	
	public boolean workflow_IsAllowResetToApproved(FocObject focObject){
		return workflow_IsAllow_Action(focObject, RightLevelDesc.FLD_RESET_TO_APPROVED, RightLevelDesc.FLD_RESET_TO_APPROVED);
	}
	
	public boolean workflow_IsAllowCancel(FocObject focObject){
		return workflow_IsAllow_Action(focObject, RightLevelDesc.FLD_CANCEL, RightLevelDesc.FLD_CANCEL);
	}

	public boolean workflow_IsAllowModifyCode(FocObject focObject){
		return workflow_IsAllow_Action(focObject, RightLevelDesc.FLD_MODIFY_CODE_DRAFT, RightLevelDesc.FLD_MODIFY_CODE_APPROVED);
	}

	public boolean workflow_IsAllowSignatureStageModification(){
		return workflow_IsAllow_Action(RightLevelDesc.FLD_MODIFY_SIGNATRUE_STAGE);
	}

	public WFTransactionConfig workflow_getTransactionConfig(){
		WFTransactionConfig config = null;
		
		if(this instanceof IWorkflowDesc){
			config = WFTransactionConfigDesc.getTransactionConfig_ForTransaction((IWorkflowDesc) this);
		}
		
		return config;
	}
	
	public String workflow_getTransactionTitle(){
		String title = "";
		WFTransactionConfig assignment = workflow_getTransactionConfig();
		if(assignment != null){
			title = assignment.getTransactionTitle();
			if(title.isEmpty()){
				IWorkflowDesc iWorkflowDesc = (IWorkflowDesc) this;
				title = iWorkflowDesc.iWorkflow_getTitle();
			}
		}
		return title;
	}

	public ArrayList<WFStage> workflow_PreviousStageForThisUserTitle(){
		ArrayList<WFStage> arrayList = null;
		
		if(workflow_IsWorkflowSubject()){
			WorkflowDesc workflowDesc = ((IWorkflowDesc)this).iWorkflow_getWorkflowDesc();
			if(workflowDesc != null){
				WFTransactionConfig config = WFTransactionConfigDesc.getTransactionConfig_ForTransaction((IWorkflowDesc)this);
				if(config!= null && config.getWorkflowMap() != null){
					for(int i=0; i<config.getWorkflowMap().getSignatureList().size(); i++){
						WFSignature signature = (WFSignature) config.getWorkflowMap().getSignatureList().getFocObject(i);

						for(int t=0; t<WFSignatureDesc.FLD_TITLE_COUNT; t++){
							if(signature.getTitle(t) != null && signature.getTitle(t).equalsRef(Globals.getApp().getCurrentTitle())){
								if(arrayList == null) arrayList = new ArrayList<WFStage>();
								arrayList.add(signature.getPreviousStage());
							}
						}
					}
				}
			}
		}
		return arrayList;
	}
	
	public int iWorkflow_getCode_NumberOfDigits(){
		return 5;
	}
	
	// ---------------------------------------------
	// ---------------------------------------------
	// SYNC
	// ---------------------------------------------
	// ---------------------------------------------

	public void sync_AddField(){
		if(isDbResident() && getFieldByID(FField.FLD_SYNC_IS_NEW_OBJECT) == null){
			FBoolField syncField = new FBoolField(FField.FNAME_SYNC_IS_NEW_OBJECT, "Sync|Is New", FField.FLD_SYNC_IS_NEW_OBJECT, false);
		  addField(syncField);
		}
	}
	
	public boolean sync_AllowRemoteInsert(){
		Iterator iter = referenceLocationIterator();
		return iter == null || !iter.hasNext();
	}
	// ---------------------------------------------
	// ---------------------------------------------

	public int getMasterRefFieldID() {
		return masterRefFieldID;
	}

	public void setMasterRefFieldID(int masterRefFieldID) {
		this.masterRefFieldID = masterRefFieldID;
	}
	
	public int getSlaveFieldListID(){
	  return slaveFieldListID++;
	}
	
	public void setSlaveFieldListID(int slaveFieldID){
	  slaveFieldListID = slaveFieldID;
	}

//	public PrintingAction getPrintingAction() {
//		return printingAction;
//	}

	public PrintingAction newPrintingAction() {
		return null;
	}
	
//	public void setPrintingAction(PrintingAction printingAction) {
//		this.printingAction = printingAction;
//	}

	public IFocDescPlugIn getIFocDescPlugIn() {
		return iFocDescPlugIn;
	}

	public void setIFocDescPlugIn(IFocDescPlugIn iFocDescPlugIn) {
		this.iFocDescPlugIn = iFocDescPlugIn;
	}
	
	public FocFieldEnum getFocFieldEnum_KeyPlain(){
		if(enumer_KeyPlain == null){
			enumer_KeyPlain = newFocFieldEnum(FocFieldEnum.CAT_KEY, FocFieldEnum.LEVEL_PLAIN);
		}
		return enumer_KeyPlain;
	}
	
  public TableDefinition getFabTableDefinition() {
		return fabTableDefinition;
	}

	public void setFabTableDefinition(TableDefinition fabTableDefinition) {
		this.fabTableDefinition = fabTableDefinition;
	}
	
	public void addFieldsFromTableDefinition(TableDefinition tableDefinition, FocList fieldsList){
		if(fieldsList != null){
			if(tableDefinition.isAddLogFields()){
				log_AddLogFields();
			}
			
			Iterator<FieldDefinition> iter = fieldsList.focObjectIterator();
			while(iter != null && iter.hasNext()){
				FieldDefinition fieldDef = iter.next();
				fieldDef.addToFocDesc(this);
			}
			
			if(tableDefinition.getFatherObject() != null){				
				TableDefinition fatherDef     = (TableDefinition) tableDefinition.getFatherObject();
				FocDesc         fatherFocDesc = (FocDesc) Globals.getApp().getFocDescByName(fatherDef.getName());

				if(getFieldByName(fatherFocDesc.getStorageName()) == null){
					FObjectField objFld = new FObjectField(fatherFocDesc.getStorageName(), fatherFocDesc.getStorageName(), FField.FLD_MASTER_OBJECT, fatherFocDesc, this, getSlaveFieldListID());
					objFld.setWithList(false);
				  FListField listField = objFld.getListFieldInMaster();
				  if(listField != null){
				  	listField.setDirectlyEditable(!tableDefinition.isNotDirectlyEditable());
				  }
					addField(objFld);
				}
			}
			
			dispose_MandatoryFieldsArray();
		}
	}
	
	public void addFieldsFromTableDefinition(){
		if(getFabTableDefinition() != null){
			FocLinkForeignKey link = new FocLinkForeignKey(FieldDefinitionDesc.getInstance(), FieldDefinitionDesc.FLD_TABLE, true);
			FocList fieldsList = new FocList(getFabTableDefinition(), link, null);
			fieldsList.loadIfNotLoadedFromDB();
			fieldsList.setFatherSubject(null);
			
			addFieldsFromTableDefinition(getFabTableDefinition(), fieldsList);
			
			fieldsList.dispose();
			fieldsList = null;
		}
	}
	
	public GuiDetails getFabDefaultGuiDetails(){
		GuiDetails guiDetails = null;
		
		TableDefinition tableDeffinition = getFabTableDefinition();
		if(tableDeffinition != null){
			tableDeffinition = (TableDefinition) tableDeffinition.newObjectReloaded();
			FocList viewsFocList = tableDeffinition.getDetailsViewDefinitionList();
			for(int i=0; i<viewsFocList.size(); i++){
				GuiDetails currGuiDetails = (GuiDetails) viewsFocList.getFocObject(i);
				if(currGuiDetails.isDefaultView()){
					guiDetails = currGuiDetails;
				}
			}
		}
		return guiDetails;
	}

	private FocFieldEnum enumer_ResetObject_New(){
		return new FocFieldEnum(this, FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
	}
	
	private FocFieldEnum enumer_ResetObject_GetIfNotBusy(){
		FocFieldEnum enumer = null;
		if(!enumer_ResetObject_DuringScan){
			if(enumer_ResetObject == null){
				enumer_ResetObject = enumer_ResetObject_New();
			}
			enumer_ResetObject.reset();
			enumer = enumer_ResetObject;
			enumer_ResetObject_DuringScan = true;
		}
		return enumer;
	}

	private void enumer_ResetObject_Release(){
		enumer_ResetObject_DuringScan = false;
	}
	
  public static boolean inExcludedArray(int[] excludedProperties, int fieldID){
  	boolean inArray = false;
  	if(excludedProperties != null){
	  	for(int i=0; i<excludedProperties.length; i++){
	  		if(fieldID == excludedProperties[i]){
	  			inArray = true;
	  			break;
	  		}
	  	}
  	}
  	return inArray;
  }

	synchronized private void fieldEnumScan_resetStatus_OR_BackupRestore_OR_CopyProperties_ForFocObject(
			FocObject obj,
			FocObject sourceObject,
			int[]     excludedProperties,
			boolean 	resetStatus, 
			boolean 	backup, 
			boolean 	restore){
		//FocFieldEnum enumer = new FocFieldEnum(this, obj, FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
		boolean isNewEnumeration = false;
		FocFieldEnum enumer = enumer_ResetObject_GetIfNotBusy();
		if(enumer == null){
			enumer = enumer_ResetObject_New();
			isNewEnumeration = true;
		}
		
		while(enumer != null && enumer.hasNext()){
			FField fld = (FField) enumer.next();
			//BackupRestore
			if(backup || restore){
	      if(fld != null && !fld.isReflectingField()){
	        FProperty prop = obj.getFocProperty(fld.getID());
	        if(prop != null){
	          if (backup) {
	            prop.backup();
	          } else {
	            prop.restore();
	          }
	        } 
	      }
			}else if(resetStatus){
				if(fld.isDBResident()){
					FProperty prop = enumer.getProperty(obj);
					if(prop != null){
						prop.setModifiedFlag(false);
					}
				}
			}else if(sourceObject != null){
	      if(fld != null && fld != identifierField && fld.getID() != FField.REF_FIELD_ID && fld.getID() != FField.MASTER_REF_FIELD_ID && !inExcludedArray(excludedProperties, fld.getID())){
	        FProperty thisProp = obj.getFocProperty(fld.getID());
	        FProperty srcProp = sourceObject.getFocProperty(fld.getID());
	        if(thisProp != null && srcProp != null){
	        	thisProp.copy(srcProp);
	        }
	      }
			}
		}
		
		if(isNewEnumeration){
			enumer.dispose();
			enumer = null;
		}else{
			enumer_ResetObject_Release();
		}
	}

	public void copyPropertiesForFocObject(FocObject obj, FocObject sourceObject, int[] excludedProperties){
		fieldEnumScan_resetStatus_OR_BackupRestore_OR_CopyProperties_ForFocObject(obj, sourceObject, excludedProperties, false, false, false);
	}

	public void backupRestoreForFocObject(FocObject obj, boolean backup){
		fieldEnumScan_resetStatus_OR_BackupRestore_OR_CopyProperties_ForFocObject(obj, null, null, false, backup, !backup);
	}
	
	public void resetStatusForFocObject(FocObject obj){
		fieldEnumScan_resetStatus_OR_BackupRestore_OR_CopyProperties_ForFocObject(obj, null, null, true, false, false);
		//FocFieldEnum enumer = new FocFieldEnum(this, obj, FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
//		FocFieldEnum enumer = getEnumer_ResetObject();
//		while(enumer != null && enumer.hasNext()){
//			FField fld = (FField) enumer.next();
//			if(fld.isDBResident()){
//				FProperty prop = enumer.getProperty(obj);
//				if(prop != null){
//					prop.setModifiedFlag(false);
//				}
//			}
//		}
	}
	
//	public TableDefinition fab_GetFabTableDefinition(){
//		TableDefinition tableDefinition        = null;
//		FocList         listOfTableDefinitions = TableDefinitionDesc.getList(FocList.LOAD_IF_NEEDED);
//		for(int i=0; i<listOfTableDefinitions.size(); i++){
//			TableDefinition currTableDef = (TableDefinition) listOfTableDefinitions.getFocObject(i);
//			if(currTableDef != null && currTableDef.getDbFocDescDeclaration()){
//				
//			}
//		}
//		return tableDefinition;
//	}
	
	private Collection<Integer> vaadinFieldIDs = null;
	public Collection<Integer> vaadin_getFieldIds(){
		if(vaadinFieldIDs == null){
			vaadinFieldIDs = new ArrayList<Integer>();
			FocFieldEnum enumer = newFocFieldEnum(FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
			while(enumer != null && enumer.hasNext()){
				FField fld = enumer.nextField();
				if(fld != null && fld.getID() != FField.REF_FIELD_ID){
					vaadinFieldIDs.add(fld.getID());
				}
			}
		}
		return vaadinFieldIDs;
	}
	
  private Collection<String> vaadinFieldNames = null;
  public Collection<String> vaadin_getFieldNames(){
    if(vaadinFieldNames == null){
      vaadinFieldNames = new ArrayList<String>();
      FocFieldEnum enumer = newFocFieldEnum(FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
      while(enumer != null && enumer.hasNext()){
        FField fld = enumer.nextField();
        //if(fld != null && fld.getID() != FField.REF_FIELD_ID){
        vaadinFieldNames.add(fld.getName());
        //}
      }
    }
    return vaadinFieldNames;
  }	
  
  public ArrayList<FocObject> allFocObjectArray_get(){
  	return allFocObjectArray_get(false);
  }
  
  public int allFocObjectArray_size(){
  	ArrayList<FocObject> array = allFocObjectArray_get(false);
  	return array != null ? array.size() : -1;
  }
  
  private ArrayList<FocObject> allFocObjectArray_get(boolean createIfNeeded){
  	if(createIfNeeded && descFocObjects == null && ConfigInfo.isKeepFocObjectArrayInFocDesc()){
  		descFocObjects = new ArrayList<FocObject>();
  	}
  	return descFocObjects;
  }
  
  public void allFocObjectArray_Add(FocObject focObj){
  	ArrayList<FocObject> array = allFocObjectArray_get(true);
  	if(array != null) {
  		array.add(focObj);
  	}
  }

  public void allFocObjectArray_Remove(FocObject focObj){
  	ArrayList<FocObject> array = allFocObjectArray_get();
  	if(array != null) {
  		array.remove(focObj);
  	}
  }

  public void allFocObjectArray_AdjustPropertyArray(){
  	ArrayList<FocObject> array = allFocObjectArray_get();
  	for(int i=0; i<array.size(); i++){
  		FocObject obj = array.get(i);
  		obj.adjustFocProperties();
  	}
  }

	public FocModule getModule() {
		return module;
	}

	public void setModule(FocModule module) {
		this.module = module;
	}

	//--------------------------------------------------------
	// IFocData
  //--------------------------------------------------------	
	
	public IFocData iFocData_getFieldOrPropertyByName(FocObject focObject, String path){

		IFocData data = null;
    
  	if(path != null && path.contains(IFocData.DATA_PATH_SIGN)){
  		path = path.substring(0, path.indexOf(IFocData.DATA_PATH_SIGN));
  	}
		
    String    propertyName = path; 
    int idx = path.indexOf(".");
    if(idx > 0){
      propertyName = path.substring(0, idx);
      path         = path.substring(idx+1, path.length());
    }else{
      propertyName = path;
      path         = null;
    }

    int idxCroche = propertyName.indexOf("[");
    int itemIndex = -1;
    if(idxCroche > 0){
    	String itemIndexStr = propertyName.substring(idxCroche+1, propertyName.length()-1);
      propertyName = propertyName.substring(0, idxCroche);
      if(itemIndexStr != null && !itemIndexStr.isEmpty()){
      	itemIndex = FocMath.parseInteger(itemIndexStr);
      }
    }
    
    data = null;
    if(focObject != null){
    	data = focObject.getFocPropertyByName(propertyName);
    	if(data == null && propertyName.equals(WorkflowDesc.FNAME_SIGNATURE) && itemIndex >= 0 && workflow_IsWorkflowSubject()){
    		Workflow workflow = ((IWorkflow)focObject).iWorkflow_getWorkflow();
    		WFLog log = workflow.getSignatureAt(itemIndex);
    		data = log;
    	}
    }
    if(data == null){
    	data = getFieldByName(propertyName);
    }
    if(data instanceof FList){
      data = ((FList) data).getList();
      if(data != null && itemIndex >= 0){
      	FocList focList = (FocList)data;
      	if(itemIndex < focList.size()){
      		data = focList.getFocObject(itemIndex);
      	}
      }
    }
    
    if(data != null && path != null && !path.isEmpty()){
      data = data.iFocData_getDataByPath(path);
    }
    return data;
	}
	
  @Override
  public boolean iFocData_isValid() {
     return true;
  }

  @Override
  public boolean iFocData_validate() {
    return true;
  }

  @Override
  public void iFocData_cancel() {
  }

  @Override
  public IFocData iFocData_getDataByPath(String path) {
    return iFocData_getFieldOrPropertyByName(null, path);
  }
  //--------------------------------------------------------
  
  public static FocDesc getInstance(String tableName, Class focDescClass) {
  	return getInstance(tableName, FOC_DESC_TRANSACTION_TYPE, focDescClass);
  }
  
  /**
   * This method starts by checking if the FocDesc is in the FocDescMap
   * If not it constructs one using the Class passed as parameter 
   */
	public static FocDesc getInstance(String tableName, int type, Class focDescClass) {
		FocDesc focDesc = null;
		if(FocDescMap.getInstance() != null){
			focDesc = FocDescMap.getInstance().get(tableName, type);
	    if(focDesc == null){
	      try {
	        if (focDescClass != null) {
	          Class<FocDesc>[]  param = null;
	          Object[] args = null;
	          
	        	Constructor<FocDesc> constr = focDescClass.getConstructor(param);
	        	focDesc = constr.newInstance(args);
	        }
	      } catch (Exception e) {
	        Globals.logException(e);
	      }
	      
	      FocDescMap.getInstance().put(tableName, type, focDesc);
	      focDesc.afterCreation();
	    }
  	}
    
    return focDesc;
  }
	
  @Override
  public Object iFocData_getValue() {
    return null;
  }

	public ArrayList<Integer> getCloudStorageFields(boolean createIfNeeded) {
		if(cloudStorageFields == null && createIfNeeded){
			cloudStorageFields = new ArrayList<Integer>();
		}
		return cloudStorageFields;
	}

	public void setCloudStorageFields(ArrayList<Integer> cloudStorageFields) {
		this.cloudStorageFields = cloudStorageFields;
	}

	public FMultipleChoiceField newMultipleChoiceField_PropertyRights(String fieldName, String title, int fldID){
    FMultipleChoiceField mFld = new FMultipleChoiceField(fieldName, title, fldID, false, 1);
    mFld.addChoice(FocObject.PROPERTY_RIGHT_NONE      , "none"      );
    mFld.addChoice(FocObject.PROPERTY_RIGHT_READ      , "Read Only" );
    mFld.addChoice(FocObject.PROPERTY_RIGHT_READ_WRITE, "Read Write");
    return mFld;
	}
	
	public FocList getCustomFocListForMobile(HashMap<String, String> extraParams, String key){
		return null;
	}

	public boolean isListInCache() {
		return listInCache;
	}

	public void setListInCache(boolean listInCache) {
		this.listInCache = listInCache;
	}
	
	public FocObject findObjectByFilterExpression(String filterExpression){
		FocList list = getFocList();
		return list != null ? list.findObjectByFilterExpression(filterExpression) : null;
	}

	public boolean isAllowAdaptDataModel() {
		return allowAdaptDataModel;
	}

	public void setAllowAdaptDataModel(boolean allowAdaptDataModel) {
		this.allowAdaptDataModel = allowAdaptDataModel;
	}
	
	public boolean isLogicalDeleteEnabled() {
		return logicalDelete;
	}

	public void setLogicalDeleteEnabled(boolean enableLogicalDelete) {
		logicalDelete = enableLogicalDelete;
		if(enableLogicalDelete) addLogicalDeleteFields();
	}
	
	public int nextFldID(){
		int next = 1;
		for(int i=0; i<fields.size(); i++){
			FField fld = fields.get(i);
			if(next <= fld.getID()) next = fld.getID()+1;
		}
		return next;
	}

	public String getDbSourceKey() {
		return dbSourceKey;
	}

	public void setDbSourceKey(String dbSourceKey) {
		this.dbSourceKey = dbSourceKey;
	}
	
	public int getProvider() {
		//#PERF cahce the Provider pointer because taking 6% of a select conacts
		if(providerCached == DBManager.PROVIDER_NONE) {
			String dbSourceKey = getDbSourceKey();
			providerCached = Globals.getDBManager().getProvider(dbSourceKey);
		}		
		return providerCached;
	}

	public int getServerVersion() {
		//#PERF cahce the Provider pointer because taking 6% of a select conacts
		if(serverVersionCached == 0) {
			String dbSourceKey = getDbSourceKey();
			serverVersionCached = Globals.getDBManager().getServerVersion(dbSourceKey);
			if(serverVersionCached == 0) serverVersionCached = -1;
		}		
		return serverVersionCached <= 0 ? 0 : serverVersionCached;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FocListGroupBy getGroupBy() {
		return focListGroupBy;
	}

	public void setGroupBy(FocListGroupBy focListGroupBy) {
		this.focListGroupBy = focListGroupBy;
	}
	
	public boolean isReport() {
		return false;
	}

	public String getReportContext() {
		return reportContext;
	}

	public void setReportContext(String reportContext) {
		this.reportContext = reportContext;
  	Globals.getApp().reportConfigFocDesc_Add(reportContext, this);
	}
	
	public String getReportMenu() {
		return "MNU_REPORT_"+reportContext;
	}

	public boolean isSiteRestrictionAccess() {
		return siteRestrictionAccess;
	}

	public void setSiteRestrictionAccess(boolean siteRestrictionAccess) {
		this.siteRestrictionAccess = siteRestrictionAccess;
	}
	
	public FocDesc getWFLogDesc() {
		FocDesc logFocDesc = null;
		if(this instanceof ILoggableDesc) {
			LoggableDesc workflowDesc = ((ILoggableDesc)this).iWorkflow_getWorkflowDesc();
			if(workflowDesc != null){
				FListField listField = (FListField) getFieldByID(workflowDesc.getFieldID_LogList());
				if(listField != null) {
					logFocDesc = listField.getFocDesc();
				}
			}
		}
		return logFocDesc;
	}

	public boolean isUseOracleListAggCLOB() {
		return useOracleListAggCLOB;
	}

	public void setUseOracleListAggCLOB(boolean useOracleListAggCLOB) {
		this.useOracleListAggCLOB = useOracleListAggCLOB;
	}

	public boolean refreshCachedListFocObject(long ref) {
		boolean error = true;
		if (isListInCache()) {
			if(ref > 0) {
				FocList list = getFocList();
				if(list != null) {
					list.loadIfNotLoadedFromDB();
					FocObject focObject = list.searchByRealReferenceOnly(ref);
					if(focObject != null) {
						focObject.reloadWithSlaveLists();
						error = false;
					}
				}
			}
		}
		return error;
	}	
	
	public String getNamespacePrefix() {
	  String namespacePart = "";
	  String namespace = Globals.getApp().getDataSource().getNamespaceName(getDbSourceKey());
	  if (namespace != null) {
	  	namespacePart = namespace+".";
	  }
	  return namespacePart;
	}
}
