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
/*
 * Created on Oct 14, 2004
 */
package com.foc.desc.field;

import java.sql.Types;
import java.text.Format;
import java.util.ArrayList;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.db.DBManager;
import com.foc.desc.*;
import com.foc.formula.FieldFormulaContext;
import com.foc.formula.Formula;
import com.foc.gui.table.cellControler.*;
import com.foc.list.FocLink;
import com.foc.list.FocList;
import com.foc.list.filter.FilterCondition;
import com.foc.property.*;
import com.foc.property.validators.FPropertyValidator;
import com.foc.shared.dataStore.IFocData;

import java.awt.*;

/**
 * @author 01Barmaja
 */
public abstract class FField implements Cloneable, IFocData {
	private   FocDesc focDescParent = null;
	
  protected String name = "";
  protected String title = "";
  private   String toolTipText = null;
  private   String explanation = null;
  protected int id = 0;
  protected boolean key = false;
  protected int size = 0;
  protected int decimals = 0;
  //BAnoineS - AUTOINCREMENT
  private boolean autoIncrement = false;
  //EAnoineS - AUTOINCREMENT
  private boolean isDBResident = true;
  private boolean lockValueAfterCreation = false;
  private boolean allwaysLocked = false;
  private boolean editableIfEmpty = false;
  private int     isMandatory = 0;
  private boolean includeInDBRequests = true;
  private int     indexOfPropertyInArray      = -1;
  private int     indexOfPropertyInDummyArray = -1;
  private FBoolField inheritanceField = null;
  private InheritedPropertyGetter inheritedPropertyGetter = null;
  private String formulaString = null;
  private FieldFormulaContext formulaContext = null;
  private FPropertyValidator validator = null;
  private boolean reflectingField  = false;
  private boolean showInDictionary = true ;
  private String  dictionaryGroup  = null ;
  private boolean output = false;
  private boolean fabField = false;
  private String  defaultStringValue = null;
  private boolean noRights = false;
  private IPropertyStringConverter stringConverter = null;
  
  private FField  joinOriginalField = null;
  
  private ArrayList<FPropertyListener>              listeners  = null;
  private ArrayList<FPropertyModificationControler> modificationControlers = null;

  public static final int MANDATORY_NO  = 0;
  public static final int MANDATORY_YES = 1;
  public static final int MANDATORY_YES_BUT_CAN_FILL_LATER = 2;
  
  private boolean jsonDetailedInclude = true;
  private boolean jsonListInclude     = true;
  private boolean jsonEmbeddedInclude = true;
  
  abstract public int getSqlType();
	abstract public String getCreationString(String name);
  abstract public Component getGuiComponent(FProperty prop);
  abstract public AbstractCellControler getTableCellEditor_ToImplement(FProperty prop);
  abstract public boolean isObjectContainer();
  abstract public FocDesc getFocDesc();
  abstract public void addReferenceLocations(FocDesc pointerDesc);
  abstract public FProperty newProperty_ToImplement(FocObject masterObj, Object defaultValue);
  abstract public FProperty newProperty_ToImplement(FocObject masterObj);
  abstract protected FilterCondition getFilterCondition(FFieldPath fieldPath, String conditionPrefix);
  
  final public static String NO_RIGHTS_STRING = "-NR-";
  
  final public static int REF_FIELD_ID 									= 0;
  final public static int SELECTION_FIELD_ID 						= -2;
  final public static int MASTER_REF_FIELD_ID 					= -3;
  final public static int SLAVE_REF_FIELD_ID 						= -4;
  final public static int RIGHT_LEVEL_USER_FIELD_ID 		= -5;
  final public static int RIGHT_LEVEL_FIELD_ID 					= -6;
  final public static int RIGHT_LEVEL_DATETIME_FIELD_ID = -7;
  final public static int STATUS_FIELD_ID 							= -8;
  final public static int LOCK_USER_FIELD_ID 						= -9;
  final public static int LINE_NUMBER_FIELD_ID          = -10;
  final public static int TREE_FIELD_ID                 = -11;
  final public static int MASTER_MIRROR_ID              = -12;
  final public static int CREATION_REVISION_FIELD_ID    = -13;
  final public static int DELETION_REVISION_FIELD_ID    = -14;
  final public static int NEW_ITEM_FIELD_ID    			    = -15;
  final public static int REVISION_FIELD_ID             = -16;
  final public static int FLD_ID_GANTT_CHART            = -17;
  final public static int NO_LIST_FIELD_ID              = -18;
  final public static int FLD_NAME                      = -19;
  final public static int FLD_ID_DRAWING                = -20;
  final public static int FLD_ORDER                     = -21;
  final public static int FLD_PROPERTY_FORMULA_LIST     = -22;
  final public static int FLD_FATHER_NODE_FIELD_ID      = -23;
  final public static int FLD_DEPRECATED_FIELD          = -24;
  final public static int FLD_LANGUAGE_VALUE_LIST       = -25;
  final public static int FLD_DESCRIPTION               = -26;
  final public static int FLD_COMPANY                   = -27;
  final public static int FLD_CREATION_USER             = -28;
  final public static int FLD_CREATION_TIME             = -29;
  final public static int FLD_MODIFICATION_USER         = -30;
  final public static int FLD_MODIFICATION_TIME         = -31;
  final public static int FLD_SYNC_IS_NEW_OBJECT        = -32;
  final public static int FLD_CODE                      = -33;
  final public static int FLD_PROPOSAL_CODE             = -34;
  final public static int FLD_NODE_COLLAPSE             = -35;
  final public static int FLD_FAB_OWNER                 = -36;
  final public static int FLD_EXTERNAL_CODE             = -37;
  final public static int FLD_MASTER_OBJECT             = -38;
  final public static int FLD_DATE                      = -39;
  final public static int FLD_DEPARTMENT                = -40;
  final public static int FLD_IS_SYSTEM_OBJECT          = -41;
  final public static int FLD_NOT_COMPLETED_YET         = -42;
  //Review Fields
  final public static int FLD_REVIEWSTATUS		          = -43;
  final public static int FLD_REVIEWCOMMENT             = -44;
  
  final public static int FLD_LOGICAL_DELETE        	  = -45;
  final public static int FLD_LOGICAL_DELETE_DATE    		= -46;
  final public static int FLD_LOGICAL_DELETE_USER     	= -47;
  
  final public static int NO_FIELD_ID                   = -99;
  final public static int FLD_SLAVE_LIST_FIRST          = -200;
  final public static int FLD_SLAVE_LIST_LAST           = -100;
  final public static int FLD_SHIFT_FOR_FAB_FIELDS      = 5000000;
  
  final public static String REVISION_FIELD_ID_NAME          = "R_ID";
  final public static String CREATION_REVISION_FIELD_ID_NAME = "C_R";
  final public static String DELETION_REVISION_FIELD_ID_NAME = "D_R";
  final public static String NEW_ITEM_FIELD_ID_NAME          = "N_I";
  final public static String NEW_ITEM_FIELD_ID_NAME_PREFIX   = "REV_";
  final public static String ORDER_FIELD_NAME                = "ORDER_FLD";
  final public static String REF_FIELD_NAME                  = "REF";
  final public static String SELECTION_FIELD_NAME            = "SELECTION";
  final public static String MASTER_REF_FIELD_NAME           = "M_REF";
  final public static String SLAVE_REF_FIELD_NAME            = "S_REF";
  final public static String RIGHT_LEVEL_USER_FIELD_NAME     = "USER_REF";
  final public static String RIGHT_LEVEL_FIELD_NAME          = "RGHT_LVL";
  final public static String RIGHT_LEVEL_DATETIME_FIELD_NAME = "RGHT_LVL_DATE";
  final public static String LOCK_USER_FIELD_NAME            = "LOCK_USER_REF";
  final public static String CONCURRENCY_LOCK_USER_FIELD_PREFIX = "LK_USER_";
  final public static String LINE_NUMBER_FIELD_LBL              = "LINE_NBR";
  final public static String LIST_FIELD_SUFFIX                  = "_LIST";
  final public static String FATHER_NODE_FIELD_NAME             = "FATHER_NODE";
  final public static String FATHER_NODE_FIELDPREFIX            = "FATHERNODE_";
  final public static String FNAME_SYNC_IS_NEW_OBJECT           = "SYNC_IS_NEW";
  final public static String FNAME_NODE_COLLAPSE                = "NODE_COLLAPSE";
  final public static String LOGICAL_DELETE_FIELD_NAME       		= "LogicalDelete";
  final public static String LOGICAL_DELETE_DATE_FIELD_NAME  		= "LogicalDeleteDate";
  final public static String LOGICAL_DELETE_USER_FIELD_NAME  		= "LogicalDeleteUser";
  public static final String FNAME_CODE                         = "CODE";
  public static final String FNAME_NAME                         = "NAME";
  public static final String FNAME_DESCRIPTION                  = "DESCRIP";
  public static final String FNAME_COMPANY                      = "COMPANY";
	public static final String FNAME_DATE                         = "DATE";
	public static final String FNAME_DEPARTMENT                   = "DEPARTMENT";
	public static final String FNAME_IS_SYSTEM_OBJECT             = "IS_SYSTEM";
	public static final String FNAME_EXTERNAL_CODE                = "EXTERNAL_CODE";
	public static final String FNAME_SITE                         = "SITE";
	public static final String FNAME_NOT_COMPLETED_YET            = "NOT_COMPLETED";
	
  final public static String FLD_NAME_PARAM_SET_COST_QUANTITY   = "FACTOR";
  
  public void init(String name, String title, int id, boolean key, int size, int decimals) {
//    this.name = name.toUpperCase();
  	this.name = name;
    if (Globals.getDBManager()!= null){
      if((getProvider()== DBManager.PROVIDER_ORACLE) && (name == "MODE")){
          this.name = "MODE_O";
      }
    }
    this.title = title;
    this.id = id;
    this.key = key;
    this.size = size;
    this.decimals = decimals;
  }

  public FField(String name, String title, int id, boolean key, int size, int decimals) {
    init(name, title, id, key, size, decimals);
  }
  
  public void dispose(){
    name = null;
    title = null;
    formulaString = null;
    
    if(listeners != null){
      for(int i=0; i<listeners.size(); i++){
        FPropertyListener propList = (FPropertyListener) listeners.get(i);
        propList.dispose();        
      }
      listeners.clear();
      listeners = null;
    }
    
    if(modificationControlers!= null){
      for(int i=0; i<modificationControlers.size(); i++){
        FPropertyModificationControler propList = (FPropertyModificationControler) modificationControlers.get(i);
        propList.dispose();        
      }
      modificationControlers.clear();
      modificationControlers = null;
    }
    
    if(formulaContext != null){
    	formulaContext.dispose();
    	formulaContext = null;
    }
    if(validator != null){
    	validator.dispose();
    	validator = null;
    }
  }
    
  public static FField newField(int sqlType, String name, int id, int size, int decimals, boolean autoIncrement) {
    FField field = null;
    
    switch (sqlType) {
    case Types.NUMERIC:
      if(decimals > 0){
        field = new FNumField(name, name, id, false, size, decimals);
      }else{
        field = new FIntField(name, name, id, false, size);
      }
      break;
    case Types.SMALLINT:
    case Types.INTEGER:    
    case Types.BIGINT:
    case Types.TINYINT:
      field = new FIntField(name, name, id, false, size);
      break;
    case Types.DOUBLE:
    case Types.REAL:
    case Types.FLOAT:      
      field = new FNumField(name, name, id, false, size, decimals);
      break;
    case Types.VARCHAR:
    case Types.NVARCHAR://This is for MSSQL
    case Types.LONGVARCHAR: 
    case Types.NCHAR://This is for MSSQL
    case Types.CLOB:
    case Types.NCLOB:
    case Types.LONGNVARCHAR://NTEXT
      field = new FStringField(name, name, id, false, size);
      break;
    case Types.CHAR:
      field = new FStringField(name, name, id, false, size);
      break;
    case Types.BLOB:
    case Types.LONGVARBINARY:  
    case Types.VARBINARY:
    case Types.BINARY://This is for Postgres BYTEA
      field = new FBlobStringField(name, name, id, false, 0, 0);
      break;
    case Types.DATE:
      field = new FDateField(name, name, id, false);
      break;
    case Types.TIMESTAMP:
    	field = new FDateTimeField(name, name, id, false);
    	break;
    case Types.TIME:
      field = new FTimeField(name, name, id, false);
      break;
    case Types.BIT:
      field = new FBoolField(name, name, id, false);
      break;
    case Types.JAVA_OBJECT:
      // field = FObjectField(name, name, id, false, size, decimals);
      break;
    case Types.STRUCT:
    	field = new FSDOGeometryPointField(name, name, id, false);
    	break;
    case Types.ARRAY:
      // field = FIntField(name, name, id, false, size, decimals);
      break;
      default:
      	com.foc.Globals.logString("SQL type : "+sqlType);
    }
    
    if(field == null){
      com.foc.Globals.logString("Could not find type : "+sqlType);
    }else{
    	field.setAutoIncrement(autoIncrement);
    }
    
    return field;
  }
  
	public FocDesc getFocDescParent() {
		return focDescParent;
	}
	
	public void setFocDescParent(FocDesc focDescParent) {
		this.focDescParent = focDescParent;
	}
    
  public String getDBSourceKey(){
  	String key = null;
		FocDesc focDescHolder = getFocDescParent();
		if(focDescHolder != null){
			key = focDescHolder.getDbSourceKey();
		}
  	return key;
  }
  
  public int getProvider(){
  	int provider = DBManager.PROVIDER_NONE;
		FocDesc focDescHolder = getFocDescParent();
		if(focDescHolder != null){
			provider = focDescHolder.getProvider();
		}
  	return provider;
  }
	
  public void setTitle(String title){
  	this.title = title;
  }
  
  public boolean isReflectingField() {
		return reflectingField;
	}

  public void setReflectingField(boolean reflectingField) {
		this.reflectingField = reflectingField;
		if(reflectingField){
			setDBResident(false);
		}
	}
  
  /*
  private void setPropertyToNullIfAllowed(FProperty property){
  	if (property != null 
				&& ConfigInfo.isAllowNullProperties()
				&& (property instanceof FInt 
						|| property instanceof FLong 
						|| property instanceof FDouble 
						|| property instanceof FString  
						|| property instanceof FBoolean  		
						|| property instanceof FDate 
						|| property instanceof FTime
						|| property instanceof FDateTime  						
						|| property instanceof FMultipleChoiceString)) {
			property.setValueNull(true);
		}
  }
  */
  
	public FProperty newProperty(FocObject masterObj, Object defaultValue) {
		FProperty prop = null;
		if (!isReflectingField()) {
			prop = newProperty_ToImplement(masterObj, defaultValue);
			//setPropertyToNullIfAllowed(prop);
		}
		return prop;
	}
  
  public FProperty newProperty(FocObject masterObj){
  	FProperty prop = null;
  	if(!isReflectingField()){
  		prop = newProperty_EvenIfReflecting(masterObj);
  		//setPropertyToNullIfAllowed(prop);
  	}
  	return prop;
  }

  public FProperty newProperty_EvenIfReflecting(FocObject masterObj){
  	FProperty prop = newProperty_ToImplement(masterObj);
  	if(prop != null && getDefaultStringValue() != null) prop.setString(getDefaultStringValue());
  	//setPropertyToNullIfAllowed(prop);
  	return prop;
  }

  public AbstractCellControler getTableCellEditor(FProperty prop){
  	/*
  	AbstractCellControler controler =	null;
  	if(isWithInheritance()){
  		controler = 
  	}else{
  		controler =	getTableCellEditor_ToImplement(prop);
  	}
  	return controler;
  	*/
  	return getTableCellEditor_ToImplement(prop);
  }
  
  public String createLinkCondition(String firstTableName){
    return "";
  }
  
  public String getNameInSourceTable(){
    return "";
  }
  
  public String getNameInTargetTable(){
    return "";
  }
  
  public FilterCondition getFilterCondition(FFieldPath fieldPath, FocDesc fieldPathBaseFocDesc){
  	FilterCondition condition = null;
		if(fieldPath != null && fieldPathBaseFocDesc != null){
			String conditionPrefix = fieldPath.getFieldCompleteName(fieldPathBaseFocDesc);
			condition = getFilterCondition(fieldPath, conditionPrefix);
		}
		return condition;
	}

  private static FIntField lineNumberField = null;
  
  public static FIntField getLineNumberField(){
    if(lineNumberField == null){
      lineNumberField = new FIntField(FField.LINE_NUMBER_FIELD_LBL, "Line number", FField.LINE_NUMBER_FIELD_ID, false, 5);
    }
    return lineNumberField;
  }
  
  public Object clone() throws CloneNotSupportedException {
    FField zClone = (FField)super.clone();
    zClone.key = false;
    return zClone;
  }

  public Object cloneExact() throws CloneNotSupportedException {
    FField zClone = (FField)super.clone();    
    return zClone;
  }

  public Object cloneWithoutListeners() throws CloneNotSupportedException {
    FField zClone = (FField) clone();
    zClone.listeners = null;
    return zClone;
  }

  public int compareSQLDeclaration(FField field){
    return 0;
  }
  
  public void addListener(FPropertyListener propListener) {
    if(propListener != null){
      if (listeners == null){
        listeners = new ArrayList<FPropertyListener>();
      }
      listeners.add(propListener);
    }
  }
  
  public void removeListener(FPropertyListener propListener) {
    if (listeners != null && propListener != null) {
      listeners.remove(propListener);
    }
  }
  
  public void notifyPropertyListeners(FProperty property) {
    if (listeners != null) {
      for(int i=0; i<listeners.size(); i++){
        FPropertyListener porpListener = (FPropertyListener) listeners.get(i);
        if (porpListener != null) {
          porpListener.propertyModified(property);
        }
      }
    }
  }
  
  //-----------------------------------------------
  // Property Modification Controler
  //-----------------------------------------------
  public void addFPropertyModificationControlerListener(FPropertyModificationControler propertyModificationControler) {
    if(propertyModificationControler != null){
      if (modificationControlers == null){
      	modificationControlers = new ArrayList<FPropertyModificationControler>();
      }
      modificationControlers.add(propertyModificationControler);
    }
  }

  public void removeFPropertyModificationControlerListener(FPropertyModificationControler propListener) {
    if (modificationControlers != null && propListener != null) {
    	modificationControlers.remove(propListener);
    }
  }
  
  public boolean isPropertyModificationAllowed(FProperty property, Object newValue, boolean popupMessage) {
  	boolean allowed = true; 
    if (modificationControlers != null) {
      for(int i=0; i<modificationControlers.size() && allowed; i++){
      	FPropertyModificationControler modificationControler = (FPropertyModificationControler) modificationControlers.get(i);
        if (modificationControler != null) {
        	allowed = modificationControler.isModificationAllowed(property, newValue);
        }
      }
    }
    return allowed;
  }
  
  public boolean hasPropertyModificationControllers(){
  	return modificationControlers != null && modificationControlers.size() > 0;
  }
  //-----------------------------------------------

  public boolean hasListeners(){
  	return listeners != null && listeners.size() > 0;
  }
    
  public void setPropertyValidator(FPropertyValidator validator){
  	this.validator = validator;
  }
  
  public FPropertyValidator getPropertyValidator(){
  	return this.validator;
  }
  
  public int getID() {
    return id;
  }

  public String getKeyPrefix() {
    return "";
  }

  public String getName() {
    return name;
  }
  
  public String getDBName(){
  	return getName();
  }

  public boolean getKey() {
    return key;
  }

  public void setKey(boolean key) {
    this.key = key;
  }

  public int getSize() {
    getDecimals();//In case we are Oracle, the first call to getDecimal 
                  //might adapt (modify) the size value
    return size;
  }

  public int getDecimals() {
    return decimals;
  }

  public void setStorageInfo(String name, int size) {
    this.name = name;
    this.size = size;
  }

  public void setStorageInfo(String name, int size, int decimals) {
    setStorageInfo(name, size);
    this.decimals = decimals;
  }

  /**
   * @param i
   */
  public void setId(int i) {
    id = i;
  }

  /**
   * @param string
   */
  public void setName(String string) {
    name = string;
  }

  public FocLink getLink() {
    return null;
  }

  /**
   * @return Returns the title.
   */
  public String getTitle() {
    return title;
  }

  public String getTitleForGuiDetailsPanel() {
  	String titlePlus = title;
  	if(isMandatory() && Globals.getApp().isWithGui()){//Because in server mode the * comes with web component when we setRequired() them
  		titlePlus += "*"; 
  	}
    return titlePlus;
  }

  /**
   * @return Returns the isDBResident.
   */
  public boolean isDBResident() {
    return isDBResident;
  }

  /**
   * @param isDBResident
   *          The isDBResident to set.
   */
  public void setDBResident(boolean isDBResident) {
    this.isDBResident = isDBResident;
  }
    
  public FocList getSelectionList() {
    return null;
  }
  
  /**
   * @return Returns the lockValueAfterCreation.
   */
  public boolean isLockValueAfterCreation() {
    return lockValueAfterCreation;
  }
  
  /**
   * @param lockValueAfterCreation The lockValueAfterCreation to set.
   */
  public void setLockValueAfterCreation(boolean lockValueAfterCreation) {
    this.lockValueAfterCreation = lockValueAfterCreation;
  }
  
  public boolean isAllwaysLocked(){
  	return this.allwaysLocked;
  }
  
  public void setAllwaysLocked(boolean allwaysLocked){
  	this.allwaysLocked = allwaysLocked;
  }
  
  public int getFieldDisplaySize(){
    return size;
  }  
  
  public boolean isMandatory() {
    return isMandatory != MANDATORY_NO;
  }
    
  public void setMandatory(boolean isMandatory) {
  	this.isMandatory = isMandatory ? MANDATORY_YES : MANDATORY_NO;
  }

  public int getMandatoryMode() {
  	return this.isMandatory;
  }

  public void setMandatoryButCanFillLater(boolean isMandatory) {
  	this.isMandatory = isMandatory ? MANDATORY_YES_BUT_CAN_FILL_LATER : MANDATORY_NO;
  }

  public void setMandatory(int isMandatory) {
    this.isMandatory = isMandatory;
  }
  
  public int getIndexOfPropertyInArray() {
    return indexOfPropertyInArray;
  }
  
  public void setIndexOfPropertyInArray(int indexOfPropertyInArray) {
    this.indexOfPropertyInArray = indexOfPropertyInArray;
  }

  public int getIndexOfPropertyInDummyArray() {
    return indexOfPropertyInDummyArray;
  }
  
  public void setIndexOfPropertyInDummyArray(int indexOfPropertyInArray) {
    this.indexOfPropertyInDummyArray = indexOfPropertyInArray;
  }

  public void setFormulaString(String formulaString){
  	this.formulaString = formulaString;
  }
  
  public String getFormulaString(){
  	return this.formulaString;
  }
  
  public void createFormula(FocDesc focDesc){
  	String formulaString = getFormulaString();
  	if(formulaString != null && !formulaString.equals("")){
	  	Formula formula = null;
	  	try{
	  		formula = new Formula(formulaString);
	  		FieldFormulaContext context = new FieldFormulaContext(formula, FFieldPath.newFieldPath(getID()), focDesc);
	  		setFormulaContext(context);
	  		
	  	}catch(Exception e){
	  		Globals.logString("Formula Syntax Error, Formula rejected!");
	  		Globals.logException(e);
	  	}
  	}
  }
  
  /*private void setFormula(Formula formula){
  	this.formula = formula;
  }*/
  
  private void setFormulaContext(FieldFormulaContext formulaContext){
  	this.formulaContext = formulaContext;
  }
  
  private FieldFormulaContext getFormulaContext(){
  	return this.formulaContext;
  }
  
  public boolean isWithFormula(){
  	return getFormulaString() != null;
  }
  
  public void computePropertyUsingFormulaIfNeeded(FProperty property){
  	FieldFormulaContext formulaContext = getFormulaContext();
  	if(formulaContext != null && formulaContext.computeUponConstruction()){
  		formulaContext.compute();
  	}
  }

  public void computePropertyUsingFormula(FocObject focObject){
  	if(isWithFormula()){
	  	FProperty           prop           = focObject.getFocProperty(getID());
	  	FieldFormulaContext formulaContext = getFormulaContext();
	  	if(prop != null && formulaContext != null){
	  		formulaContext.setCurrentFocObject(focObject);
	  		Object obj = formulaContext.compute();
	  		if(obj instanceof Double){
	  			prop.setDouble(((Double) obj).doubleValue());
	  		}else if(obj instanceof Integer){	  			
	  			prop.setInteger(((Integer) obj).intValue());	  			
	  		}else if(obj instanceof String){
	  			prop.setString((String)obj);
	  		}
	  	}
  	}
  }
  
  public boolean isWithInheritance() {
    return inheritanceField != null;
  }

  public FBoolField getInheritanceField(){
    return this.inheritanceField;
  }

  public void setInheritanceField(FBoolField inheritanceField, InheritedPropertyGetter inheritedValueGetter) {
    this.inheritanceField        = inheritanceField;
    this.inheritedPropertyGetter = inheritedValueGetter;
  }
  
  public boolean isIncludeInDBRequests() {
    return includeInDBRequests;
  }
  
  public void setIncludeInDBRequests(boolean includeInDBRequests) {
    this.includeInDBRequests = includeInDBRequests;
	}
  
	public boolean isAutoIncrement() {
		return autoIncrement;
	}
	
	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}
	
	public boolean isShowInDictionary() {
		return showInDictionary;
	}
	
	public void setShowInDictionary(boolean showInDictionary) {
		this.showInDictionary = showInDictionary;
	}
	public String getDictionaryGroup() {
		return dictionaryGroup;
	}
	
	public void setDictionaryGroup(String dictionaryGroup) {
		this.dictionaryGroup = dictionaryGroup;
	}
	
	public boolean isOutput() {
		return output;
	}
	
	public void setOutput(boolean output) {
		this.output = output;
	}
	
  public boolean isEditableIfEmpty() {
    return editableIfEmpty;
  }
  
  public void setEditableIfEmpty(boolean editableIfEmpty) {
    this.editableIfEmpty = editableIfEmpty;
  }
  
	public boolean isFabField() {
		return fabField;
	}
	
	public void setFabField(boolean fabField) {
		this.fabField = fabField;
	}
	
	public String getToolTipText() {
		return toolTipText;
	}
	
	public void setToolTipText(String toolTipText) {
		this.toolTipText = toolTipText;
	}

	public String getExplanation() {
		return explanation;
	}
	
	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public String getDefaultStringValue() {
		return defaultStringValue;
	}
	
	public void setDefaultStringValue(String defaultStringValue) {
		this.defaultStringValue = defaultStringValue;
	}
	
	public InheritedPropertyGetter getInheritedPropertyGetter() {
		return inheritedPropertyGetter;
	}
	
	public void setSize(int size){
		this.size = size;
	}
	
	public void setDecimals(int decimals){
		this.decimals = decimals;
	}
	
	public boolean isNoRights() {
		return noRights;
	}
	
	public void setNoRights(boolean noRights) {
		this.noRights = noRights;
	}
	
	public Class vaadin_getClass(){
		return String.class;
	}
	
	public int getFabType() {
	  Globals.logString("!!!! NO FAB TYPE FOR THIS FIELD");
	  return -1;
	}
	
	public Format getFormat(){
	  return null;
	}
	
	public static String adaptFieldNameToProvider(int provider, String fieldName){
		String fieldNameString = fieldName;
		if(provider == DBManager.PROVIDER_MSSQL){
  		int dotIndex = fieldNameString.indexOf('.');
  		if(dotIndex > 0){//If there is a dot we put the [ after the .
  			String initial = fieldNameString;
  			fieldNameString = initial.substring(0, dotIndex+1) + "[" + initial.substring(dotIndex+1) + "]"; //This is to cover for fields with names like keywords (TRANSACTION for example)
  		}else{
  			fieldNameString = "["+ fieldNameString +"]"; //This is to cover for fields with names like keywords (TRANSACTION for example)
  		}
  	}else if(  provider == DBManager.PROVIDER_ORACLE
  			    || provider == DBManager.PROVIDER_H2
  			    || provider == DBManager.PROVIDER_POSTGRES){
			int dotIndex = fieldNameString.indexOf('.');
			if(dotIndex > 0){//If there is a dot we put the " after the .
				String initial = fieldNameString;
				fieldNameString = initial.substring(0, dotIndex+1) + "\"" + initial.substring(dotIndex+1) + "\""; //This is to cover for fields with names like keywords (TRANSACTION for example)
			}else{
				fieldNameString = "\""+ fieldNameString +"\""; //This is to cover for fields with names like keywords (TRANSACTION for example)
			}	        		
		}
		return fieldNameString;
	}
	
	public static String getSpeachMarks_Start(int dbProvider) {
		String speachMarks_Start = "\"";
		if(dbProvider == DBManager.PROVIDER_MSSQL){
			speachMarks_Start = "N\'";
		}else if(			dbProvider == DBManager.PROVIDER_ORACLE
				      ||  dbProvider == DBManager.PROVIDER_H2
							||  dbProvider == DBManager.PROVIDER_POSTGRES){
	  	speachMarks_Start = "'";
		}
		return speachMarks_Start;
	}
	
	public static String getSpeachMarks_End(int dbProvider) {
		String speachMarks_End   = "\"";
		if(dbProvider == DBManager.PROVIDER_MSSQL){
			speachMarks_End = "\'";
		}else if(			dbProvider == DBManager.PROVIDER_ORACLE
	      ||  dbProvider == DBManager.PROVIDER_H2
				||  dbProvider == DBManager.PROVIDER_POSTGRES){
	  	speachMarks_End   = "'";
		}
		return speachMarks_End;
	}
	
  //--------------------------------------------------------
  // IFocData
  //--------------------------------------------------------  
  
  @Override
  public boolean iFocData_isValid() {
     return true;
  }

  @Override
  public boolean iFocData_validate() {
    return false;
  }

  @Override
  public void iFocData_cancel() {
  }

  @Override
  public IFocData iFocData_getDataByPath(String path) {
    return null;
  }
  
  @Override
  public Object iFocData_getValue() {
    return null;
  }
  
  //--------------------------------------------------------	
	public FField getJoinOriginalField() {
		return joinOriginalField;
	}
	
	public void setJoinOriginalField(FField joinOriginalField) {
		this.joinOriginalField = joinOriginalField;
	}
	
	public IPropertyStringConverter getStringConverter() {
		return stringConverter;
	}
	
	public void setStringConverter(IPropertyStringConverter stringConverter) {
		this.stringConverter = stringConverter;
	}
	
	public boolean isAllowNullProperties() {
		return ConfigInfo.isAllowNullProperties();
	}
	
	public boolean isJsonDetailedInclude() {
		return jsonDetailedInclude;
	}
	
	public void setJsonDetailedInclude(boolean jsonDetailedInclude) {
		this.jsonDetailedInclude = jsonDetailedInclude;
	}
	
	public boolean isJsonListInclude() {
		return jsonListInclude;
	}
	
	public void setJsonListInclude(boolean jsonListInclude) {
		this.jsonListInclude = jsonListInclude;
	}
	
	public boolean isJsonEmbeddedInclude() {
		return jsonEmbeddedInclude;
	}
	
	public void setJsonEmbeddedInclude(boolean jsonEmbeddedInclude) {
		this.jsonEmbeddedInclude = jsonEmbeddedInclude;
	}
}
