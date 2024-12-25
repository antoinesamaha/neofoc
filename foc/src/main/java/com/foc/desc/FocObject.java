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
// MAIN
// COMPARE
// CONCURRENT ACCESS
// ACCESS
// LISTENERS
// REFERENCE
// DATABASE
// LIST
// XML
// NEXT NUMBER

/*
 * Created on Oct 14, 2004
 */
package com.foc.desc;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.sql.Date;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.fab.gui.details.GuiDetails;
import com.fab.model.table.FieldDefinition;
import com.fab.model.table.UserDefinedObjectGuiDetailsPanel;
import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.IFocEnvironment;
import com.foc.access.AccessControl;
import com.foc.access.AccessSubject;
import com.foc.admin.FocGroup;
import com.foc.admin.FocUser;
import com.foc.admin.UserSession;
import com.foc.api.IFocList;
import com.foc.api.IFocObject;
import com.foc.business.adrBook.Contact;
import com.foc.business.calendar.FCalendar;
import com.foc.business.company.Company;
import com.foc.business.company.UserCompanyRights;
import com.foc.business.company.UserCompanyRightsDesc;
import com.foc.business.department.Department;
import com.foc.business.status.IStatusHolder;
import com.foc.business.status.IStatusHolderDesc;
import com.foc.business.status.StatusHolder;
import com.foc.business.status.StatusHolderDesc;
import com.foc.business.workflow.WFFieldLockStage;
import com.foc.business.workflow.WFSite;
import com.foc.business.workflow.WFTitle;
import com.foc.business.workflow.implementation.IAdrBookParty;
import com.foc.business.workflow.implementation.IWorkflow;
import com.foc.business.workflow.implementation.IWorkflowDesc;
import com.foc.business.workflow.implementation.WFLogDesc;
import com.foc.business.workflow.implementation.Workflow;
import com.foc.business.workflow.implementation.WorkflowDesc;
import com.foc.business.workflow.map.WFMap;
import com.foc.business.workflow.map.WFSignature;
import com.foc.business.workflow.map.WFSignatureDesc;
import com.foc.business.workflow.map.WFStage;
import com.foc.business.workflow.map.WFTransactionConfig;
import com.foc.business.workflow.map.WFTransactionConfigDesc;
import com.foc.business.workflow.signing.WFSignatureNeededResult;
import com.foc.db.DBManager;
import com.foc.db.SQLFilter;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FDateField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FInLineObjectField;
import com.foc.desc.field.FListField;
import com.foc.desc.field.FNumField;
import com.foc.desc.field.FObjectField;
import com.foc.desc.field.FObjectField121;
import com.foc.desc.field.FStringField;
import com.foc.event.FocEvent;
import com.foc.event.FocListener;
import com.foc.formula.FocSimpleFormulaContext;
import com.foc.formula.Formula;
import com.foc.gui.FGLabel;
import com.foc.gui.FGOptionPane;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.FTable;
import com.foc.gui.table.IFocCellPainter;
import com.foc.link.FocLinkOutRights;
import com.foc.list.FocList;
import com.foc.list.FocListElement;
import com.foc.plugin.IFocObjectPlugIn;
import com.foc.property.FBoolean;
import com.foc.property.FCloudImageProperty;
import com.foc.property.FCloudStorageProperty;
import com.foc.property.FColorProperty;
import com.foc.property.FCurrRate;
import com.foc.property.FDate;
import com.foc.property.FDescPropertyStringBased;
import com.foc.property.FDouble;
import com.foc.property.FDummyProperty_Boolean;
import com.foc.property.FDummyProperty_Double;
import com.foc.property.FDummyProperty_Object;
import com.foc.property.FDummyProperty_String;
import com.foc.property.FImageProperty;
import com.foc.property.FInLineObject;
import com.foc.property.FInt;
import com.foc.property.FList;
import com.foc.property.FLong;
import com.foc.property.FMaster;
import com.foc.property.FMultipleChoice;
import com.foc.property.FObject;
import com.foc.property.FProperty;
import com.foc.property.FReference;
import com.foc.property.FString;
import com.foc.property.FTime;
import com.foc.property.IFDescProperty;
import com.foc.property.PropertyFocObjectLocator;
import com.foc.shared.dataStore.IFocData;
import com.foc.shared.json.B01JsonBuilder;
import com.foc.shared.json.JSONObjectWriter;
import com.foc.util.IFocIterator;
import com.foc.util.Utils;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public abstract class FocObject extends AccessSubject implements FocListener, IFocObject, Item, Item.PropertySetChangeNotifier {

	private FocDesc          				thisFocDesc  									= null;
  private FocObject        				masterObject 									= null;
  private ArrayList<FocListener> 	listeners 										= null;
  
  private FProperty        				propertiesArray[] 						= null;
  private Object           				dummyPropertiesValuesArray[] 	= null;
  private FReference       				referenceProperty 						= null;
  private IFocObjectPlugIn        iFocObjectPlugIn        			= null;
  
  private char                    flags                         = 0;
  
  private static final int FLG_LOADED_FROM_DB           = 1;
  private static final int FLG_IS_TEMP_REFERENCE        = 2;
  private static final int FLG_DURING_LOAD              = 4;
  private static final int FLG_LOCKED_BY_CONCURRENCE    = 8;
  private static final int FLG_DELETABLE                = 16;
  private static final int FLG_CONTENT_VALID_MESSAGE_ON = 32;
  private static final int FLG_SHARED                   = 64;
  private static final int FLG_FRESH_COLOR              = 128;
  /*
  private boolean loadedFromDB          = true;
  private boolean isTempReference       = false;
  private boolean duringLoad            = false;
  private boolean lockedByConcurrence   = false;
  private boolean deletable             = true;
  private boolean contentValidMessageOn = false;
  private boolean shared                = false;
  */
  
  protected FPanel detailsPanel = null;
  protected static FPanel browsePanel = null;  
  
  private ArrayList<Component> relatedGuiComponents = null ;
  
	private LinkedList<Item.PropertySetChangeListener> propertySetChangeListeners = null;

  public final static int SUMMARY_VIEW_ID =  -1;
  public final static int DEFAULT_VIEW_ID =   0;
  public final static int NO_VIEW_ID      = -99;
  
  public static final int LOCK_STATUS_NOT_LOCKED             = 1;
  public static final int LOCK_STATUS_LOCKED_BY_CURRENT_USER = 2;
  public static final int LOCK_STATUS_LOCKED_BY_OTHER_USER   = 3;
  
  public static final int PROPERTY_RIGHT_NONE       = 0;
  public static final int PROPERTY_RIGHT_READ       = 1;
  public static final int PROPERTY_RIGHT_READ_WRITE = 2;  
  
  private boolean showMessageWhenCodeChange = true;
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // MAIN
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private void initPropertiesArray(){
    propertiesArray = new FProperty[thisFocDesc.getPropertyArrayLength()];    
  }

	protected void constructFocObject(FocConstructor constr) {
		constructFocObject_1(constr);
		constructFocObject_2();
	}
		
	protected void constructFocObject_1(FocConstructor constr) {
    try{
    	setLoadedFromDB(true);
    	setTempReference(false);
    	setDuringLoad(false);
    	setLockedByConcurrence(false);
    	setDeletable(true);
    	setContentValidMessageOn(false);
    	setShared(false);
    	
    	thisFocDesc = constr.getFocDesc();
    	if(thisFocDesc != null) thisFocDesc.allFocObjectArray_Add(this);
      initPropertiesArray();
  
      if(constr != null){
        initIdentifierProperty(constr.getIdentifierValue());      
        masterObject = constr.getMasterObject();
      }
    } catch (Exception e){
      Globals.logException(e);
    }
	}
    
  protected void constructFocObject_2() {
  	try{
      
      if(thisFocDesc != null && thisFocDesc.getRightsByLevelMode() != FocDesc.RIGHTS_BY_LEVEL_MODE_NONE){
        FObject user = new FObject(this, FField.RIGHT_LEVEL_USER_FIELD_ID, null);
        FInt level = new FInt(this, FField.RIGHT_LEVEL_FIELD_ID, 0);
        FString dateTime = new FString(this, FField.RIGHT_LEVEL_DATETIME_FIELD_ID, "");
        user.setValueLocked(true);
        level.setValueLocked(true);
        dateTime.setValueLocked(true);
      }

      if(thisFocDesc != null && thisFocDesc.isRevisionSupportEnabled()){
        new FInt(this, FField.CREATION_REVISION_FIELD_ID, 0);
        new FInt(this, FField.DELETION_REVISION_FIELD_ID, 0);
        new FObject(this, FField.NEW_ITEM_FIELD_ID, null);
      }
      
      if(thisFocDesc != null && thisFocDesc.isParentRevisionSupportEnabled()){
        new FInt(this, FField.REVISION_FIELD_ID, 1);
      }
      
      if(thisFocDesc != null && thisFocDesc.hasOrderField()){
        new FInt(this, FField.FLD_ORDER, 0);
      }
      
      if(thisFocDesc != null){
        FField field = thisFocDesc.getFieldByID(FField.FLD_PROPERTY_FORMULA_LIST);
        if( field != null ){
          field.newProperty(this);  
        }
      }

      if(thisFocDesc != null){
        FField field = thisFocDesc.getFieldByID(FField.FLD_SYNC_IS_NEW_OBJECT);
        if(field != null){
          field.newProperty(this);  
        }
      }

      FStringField codeField = (FStringField)thisFocDesc.getFieldByID(FField.FLD_CODE);
      if(codeField != null && !codeField.isReflectingField()){
      	codeField.newProperty(this);
      }
      
      FDateField dateField = (FDateField)thisFocDesc.getFieldByID(FField.FLD_DATE);
      if(dateField != null && !dateField.isReflectingField()){
      	FDate date = (FDate) dateField.newProperty(this);
      	if(date != null){
      		date.setDate(Globals.getApp().getSystemDate());
      	}
      }      

      codeField = (FStringField)thisFocDesc.getFieldByID(FField.FLD_PROPOSAL_CODE);
      if(codeField != null && !codeField.isReflectingField()){
      	codeField.newProperty(this);
      }

      FStringField nameField = (FStringField)thisFocDesc.getFieldByID(FField.FLD_NAME);
      if(nameField != null){
      	nameField.newProperty(this);
      }

      FStringField descField = (FStringField)thisFocDesc.getFieldByID(FField.FLD_DESCRIPTION);
      if(descField != null){
      	descField.newProperty(this);
      }
      
      FField depField = (FField)thisFocDesc.getFieldByID(FField.FLD_DEPARTMENT);
      if(depField != null && !depField.isReflectingField()){
      	depField.newProperty(this);
      }
      
      FField logField = (FField)thisFocDesc.getFieldByID(FField.FLD_CREATION_TIME);
      if(logField != null){
      	logField.newProperty(this);
      }

      logField = (FField)thisFocDesc.getFieldByID(FField.FLD_CREATION_USER);
      if(logField != null){
      	logField.newProperty(this);
      }

      logField = (FField)thisFocDesc.getFieldByID(FField.FLD_MODIFICATION_USER);
      if(logField != null){
      	logField.newProperty(this);
      }

      logField = (FField)thisFocDesc.getFieldByID(FField.FLD_MODIFICATION_TIME);
      if(logField != null){
      	logField.newProperty(this);
      }
      
      FObjectField fatherNodeField = (FObjectField) thisFocDesc.getFieldByID(FField.FLD_FATHER_NODE_FIELD_ID);
      if(fatherNodeField != null){
      	fatherNodeField.newProperty(this);
      }
      
      FBoolField deprecatedField = (FBoolField)thisFocDesc.getFieldByID(FField.FLD_DEPRECATED_FIELD);
      if(deprecatedField != null){
        deprecatedField.newProperty(this);
      }

      FField fabOwnerField = thisFocDesc.getFieldByID(FField.FLD_FAB_OWNER);
      if(fabOwnerField != null){
      	fabOwnerField.newProperty(this);
      }

      FField extCodeField = thisFocDesc.getFieldByID(FField.FLD_EXTERNAL_CODE);
      if(extCodeField != null){
      	extCodeField.newProperty(this);
      }

      if(thisFocDesc != null && thisFocDesc.isConcurrenceLockEnabled()){
        FObject user = new FObject(this, FField.LOCK_USER_FIELD_ID, null);
        user.setValueLocked(true);
      }

      if(thisFocDesc != null && thisFocDesc.isAddMasterMirror()){
      	new FMaster(this);
      }
      
      if(thisFocDesc != null && thisFocDesc.getIFocDescPlugIn() != null){
      	IFocObjectPlugIn plugIn = thisFocDesc.getIFocDescPlugIn().newFocObjectPlugIn(this);
      	setIFocObjectPlugIn(plugIn);
      }
  
      FBoolField nodeCollapseField = (FBoolField)thisFocDesc.getFieldByID(FField.FLD_NODE_COLLAPSE);
      if(nodeCollapseField != null){
      	nodeCollapseField.newProperty(this);
      }
  
      FField fField = (FField) thisFocDesc.getFieldByID(FField.FLD_MASTER_OBJECT);
      if(fField != null){
      	fField.newProperty(this);
      }
      
      FField isSystemReport = (FField) thisFocDesc.getFieldByID(FField.FLD_IS_SYSTEM_OBJECT);
      if(isSystemReport != null){
      	isSystemReport.newProperty(this);
      }

      FBoolField notCompletedField = (FBoolField)thisFocDesc.getFieldByID(FField.FLD_NOT_COMPLETED_YET);
      if(notCompletedField != null){
      	notCompletedField.newProperty(this);
      }
      
      FField logicalDeleteField = thisFocDesc.getFieldByID(FField.FLD_LOGICAL_DELETE);
      if(logicalDeleteField != null) logicalDeleteField.newProperty(this);
      
      FField logicalDeleteDateField = thisFocDesc.getFieldByID(FField.FLD_LOGICAL_DELETE_DATE);
      if(logicalDeleteDateField != null) logicalDeleteDateField.newProperty(this);
      
      FField logicalDeleteUserField = thisFocDesc.getFieldByID(FField.FLD_LOGICAL_DELETE_USER);
      if(logicalDeleteUserField != null) logicalDeleteUserField.newProperty(this);
      
      //Review Fields
      FField reviewStatusField = thisFocDesc.getFieldByID(FField.FLD_REVIEWSTATUS);
      if(reviewStatusField != null){
      	reviewStatusField.newProperty(this);
      }
      
      FField reviewCommentField = thisFocDesc.getFieldByID(FField.FLD_REVIEWCOMMENT);
      if(reviewCommentField != null){
      	reviewCommentField.newProperty(this);
      }  
      //End Review Fields

    } catch (Exception e){
      Globals.logException(e);
    }
  }
     
  public FocObject(FocConstructor constr) {
    super(Globals.getDefaultAccessControl());
    constructFocObject(constr);
  }
  
  public FocObject(FocDesc desc){
  	super(Globals.getDefaultAccessControl());
  	FocConstructor constr = new FocConstructor(desc, null);  	
    constructFocObject(constr);
  }

  public FocObject(AccessControl accessControl) {
    super(accessControl);
  }

  public synchronized void dispose(){
  	if(thisFocDesc != null) thisFocDesc.allFocObjectArray_Remove(this);
    super.dispose();
    
    masterObject = null;
    
    if(listeners != null){
      listeners.clear();
      listeners = null;
    }
        
    if(propertiesArray != null){
      /*FocFieldEnum iter = new FocFieldEnum(getThisFocDesc(), this, FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
      while(iter != null && iter.hasNext()){
        iter.next();
        FProperty prop = (FProperty) iter.getProperty();
        if(prop != null){
          prop.dispose();
        }
      }

      iter = new FocFieldEnum(getThisFocDesc(), this, FocFieldEnum.CAT_LIST, FocFieldEnum.LEVEL_PLAIN);
      while(iter != null && iter.hasNext()){
        iter.next();        
        FProperty prop = (FProperty) iter.getProperty();
        if(prop != null){
          prop.dispose();
        }
      }*/
      
      for( int i = 0; i < propertiesArray.length; i++ ){
        FProperty prop = propertiesArray[i];
        if(prop != null){
          prop.dispose();
        }
      }
      
      propertiesArray = null;
    }
  }
  
  public int propertiesArray_Size(){
  	return propertiesArray != null ? propertiesArray.length : 0;
  }

  public FProperty propertiesArray_Get(int i){
  	return propertiesArray != null ? propertiesArray[i] : null;
  }

  public Format getFormatForFieldID(int fieldID){
  	return null;
  }
  
  public String getName(){
  	return getPropertyString(FField.FLD_NAME);
  }

  public void setName(String name){
  	setPropertyString(FField.FLD_NAME, name);
  }

  public String getDisplayTitle() {
  	return getJSONName();
  }
  
  public String getJSONName() {
  	String  str     = "";
  	FocDesc focDesc = getThisFocDesc();
  	if(focDesc.hasCodeField()) {
  		str = code_getCode();
  	}else if(focDesc.getFieldByID(FField.FLD_NAME) != null) {
  		str = getName();
  	}else if(focDesc.getFieldByID(FField.FLD_DESCRIPTION) != null) {
  		str = getDescription();
  	}else if(focDesc.getKeyFieldsSize() > 0){
  		for(int i=0; i<focDesc.getKeyFieldsSize(); i++) {
  			FField fld = focDesc.getKeyFieldAt(i);
  			FProperty prop = getFocProperty(fld.getID());
  			str += prop.getString();
  		}
  	}else {
  		str = getName();
  	}
  	return str;
  }
  
  public String getDescription(){
  	return getPropertyString(FField.FLD_DESCRIPTION);
  }

  public void setDescription(String desc){
  	setPropertyString(FField.FLD_DESCRIPTION, desc);
  }

  public Department getDepartment(){
  	return (Department) getPropertyObject(FField.FLD_DEPARTMENT);
  }

  public void setDepartment(Department department){
  	setPropertyObject(FField.FLD_DEPARTMENT, department);
  }
  
  public boolean isByCompany(){
  	return getThisFocDesc() != null ? getThisFocDesc().isByCompany() : false ;
  }

  public boolean isCompanyMandatory(){
  	return getThisFocDesc() != null ? getThisFocDesc().isCompanyMandatory() : false ;
  }

  public Company getCompany(){
  	return (Company) getPropertyObject(FField.FLD_COMPANY);
  }
  
  public long getCompanyRef(){
  	FObject objProp = (FObject) getFocProperty(FField.FLD_COMPANY);
  	return objProp != null ? objProp.getLocalReferenceInt() : 0;
  }

  public void setCompany(Company company){
  	setPropertyObject(FField.FLD_COMPANY, company);
  }
  
  public boolean isSystemObject(){
  	return getPropertyBoolean(FField.FLD_IS_SYSTEM_OBJECT);
  }
  
  public void setSystemObject(boolean isSystem){
  	setPropertyBoolean(FField.FLD_IS_SYSTEM_OBJECT, isSystem);
  }
  
  public void setCompanyToDefault(){
		setCompany(Globals.getApp().getCurrentCompany());
	}
  
  public void setCompanyIfCreated(){
    if(isCreated() && isCompanyMandatory() && Globals.getApp() != null && Globals.getApp().getUser_ForThisSession() != null && getCompany() == null){
      setCompany(Globals.getApp().getCurrentCompany());
    }  
  }
  
  public boolean isForCompany(Company sessionCompany){
  	boolean ret = false;
  	if(getCompany() != null && sessionCompany != null){
  		ret = getCompany().getReferenceInt() == sessionCompany.getReferenceInt();
  	}
  	return ret;
  }
  
  public boolean isForCurrentCompany(){
  	boolean ret = isForCompany(Globals.getApp().getCurrentCompany());
  	return ret;
 	}
  
  public int getPropertyAccessRight(int fieldID){
  	return PROPERTY_RIGHT_READ_WRITE;
  }
  
  public boolean isPropertyLocked(int fieldId){
  	return false;
  }
  
  public void setPropertyValueLocked(int fieldId, boolean lock){
  	FProperty prop = getFocProperty(fieldId);
  	if(prop != null) prop.setValueLocked(lock);
  }
  
  public ArrayList<String> getMultipleChoiceStringBased_ArrayOfValues(int fieldID){
  	return null;
  }
  
  public String getSelectionFilterExpressionFor_ObjectProperty(int fieldID){
    return null;
  }
  
  public String getSelectionFilter_PropertyDataPath_ForObjectProperty(int fieldID){
  	return null;
  }
  
  public Object getSelectionFilter_PropertyValue_ForObjectProperty(int fieldID){
  	return null;
  }
  
  public FocList getObjectPropertySelectionList(int fieldID){
  	return null;
  }
  
  public FocList getListPropertyInitialLoadedList(int fieldID){
  	return null;
  }
  
  public FProperty getSameProperty(FocObject initialObject, FProperty initialProperty){
    FProperty property = null;
    
    int originLocation = 0;
    FocFieldEnum iter = new FocFieldEnum(initialObject.getThisFocDesc(), initialObject, FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
    while(iter != null && iter.hasNext()){
      iter.next();
      FProperty prop = (FProperty) iter.getProperty();
      if(prop == initialProperty){
        break;
      }
      originLocation++;
    }
    
    int counter = 0;
    iter = new FocFieldEnum(getThisFocDesc(), this, FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
    while(iter != null && iter.hasNext() && property == null){
      iter.next();
      FProperty prop = (FProperty) iter.getProperty();
      if( counter == originLocation ){
        property = prop;
      }
      counter++;
    }
    return property;
  }
  
  public void setCreationRevisionData(){
    if(thisFocDesc != null  && thisFocDesc.isRevisionSupportEnabled()){
      FProperty revNumber =  thisFocDesc.getRevisionPath().getPropertyFromObject(this);
      if(revNumber != null ){
        FInt creationRevision = new FInt(this, FField.CREATION_REVISION_FIELD_ID, 0);
        creationRevision.setInteger(revNumber.getInteger());  
      }
    }
  }
  
  public void setDeletionRevisionData(){
    if(thisFocDesc != null  && thisFocDesc.isRevisionSupportEnabled()){
      FProperty revNumber =  thisFocDesc.getRevisionPath().getPropertyFromObject(this);
      if(revNumber != null ){
        FInt deleteRevision = new FInt(this, FField.DELETION_REVISION_FIELD_ID, 0);
        deleteRevision.setInteger(revNumber.getInteger());
      }
    }
  }
  
  public void setModificationRevisionData(FocObject focObj) {
    if(thisFocDesc != null  && thisFocDesc.isRevisionSupportEnabled()){
      FProperty revNumber =  thisFocDesc.getRevisionPath().getPropertyFromObject(this);
      if(revNumber != null ){
        setPropertyObject(FField.NEW_ITEM_FIELD_ID, focObj);
        setPropertyInteger(FField.CREATION_REVISION_FIELD_ID, revNumber.getInteger());
      }
    }
  }
  
  
  public void incrementRevision(){
    setPropertyInteger(FField.REVISION_FIELD_ID, getPropertyInteger(FField.REVISION_FIELD_ID)+1);
    save();
  }
    
  public void addRelatedGuiComponent(Component comp){
    if(relatedGuiComponents == null){
      relatedGuiComponents = new ArrayList<Component>();  
    }
    relatedGuiComponents.add(comp);
  }
  
  public void removeRelatedGuiComponent(Component comp){
    if(relatedGuiComponents == null && relatedGuiComponents.contains(comp)){
      relatedGuiComponents.remove(comp);
    }
  }
  
  public void disableRelatedGuiComponents() {
    for( int i = 0; i < relatedGuiComponents.size(); i++){
      relatedGuiComponents.get(i).setEnabled(false);
    }
  }
  
  public void enableRelatedGuiComponents() {
    for( int i = 0; i < relatedGuiComponents.size(); i++){
      relatedGuiComponents.get(i).setEnabled(true);
    }
  }
  
  public void unlockPropertiesForRevision(){
    FProperty []props = propertiesArray;
    for(int i = 0; i < props.length; i++){
      if(!props[i].getFocField().isLockValueAfterCreation() || Globals.getApp().getGroup().allowNamingModif()){
        props[i].setValueLocked(false);
      }
    }
  }
  
  public void lockPropertiesForRevision(){
    FProperty []props = propertiesArray;
    for(int i = 0; i < props.length; i++){
      props[i].setValueLocked(true);  
    }
  }

  /*public void newFocProperties(){ 
	  FocFieldEnum enumer = new FocFieldEnum(getThisFocDesc(), FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
	  while(enumer != null && enumer.hasNext()){
	    FField field = (FField) enumer.next();
	    FField[] fieldArray = enumer.getFieldPath().getFieldArrayFromDesc(getThisFocDesc());
	    field = fieldArray[0];
	    if(field != null && field.getID() > 0){
	      FProperty property = field.newProperty(this);
	      property.setValueLocked(field.isAllwaysLocked() || field.isWithFormula());
	    }
    }
	  
	  enumer = new FocFieldEnum(getThisFocDesc(), FocFieldEnum.CAT_LIST, FocFieldEnum.LEVEL_PLAIN);
	  while(enumer != null && enumer.hasNext()){
	    FField field = (FField) enumer.next();
	    if(field != null && field.getID() > 0){
	      field.newProperty(this);
	    }
	  }
	  
	  forceControler(true);
  }*/
  
  public FProperty newFocPropertiesForField(FField field, boolean evenIfReflecting){
		FProperty property = null;
		if(evenIfReflecting){
			property = field.newProperty_EvenIfReflecting(this);
		}else{
			property = field.newProperty(this);
		}
    if(property != null){
    	property.setValueLocked(field.isAllwaysLocked() || field.isWithFormula());
    }
    return property;
  }
  
  public void newFocProperties(){
  	newFocProperties(false);
  }
  
  public void adjustFocProperties(){
  	newFocProperties(true);
  }
  
  private void newFocProperties(boolean checkIfExists){
  	FocDesc thisFocDesc = getThisFocDesc();
	  if(!checkIfExists && thisFocDesc.getDummyPropertyCount() > 0){
	  	dummyPropertiesValuesArray = new Object[thisFocDesc.getDummyPropertyCount()];
	  }
  	
  	int fieldsSize = thisFocDesc.getFieldsSize();
  	for(int i = 0; i < fieldsSize; i++){
  		FField field = thisFocDesc.getFieldAt(i);
  		if(field != null && (field.getID() > 0 || field.getID() == FField.FLD_COMPANY || (field.getID() >= FField.FLD_SLAVE_LIST_FIRST && field.getID() <= FField.FLD_SLAVE_LIST_LAST))){
  			boolean doCreate = true;
  			if(checkIfExists){
  			  doCreate = getFocProperty(field.getID()) == null;
  			}
  			
  			if(doCreate){ 
  				newFocPropertiesForField(field, false);
	      
		      if(dummyPropertiesValuesArray != null && field.getIndexOfPropertyInDummyArray() >= 0){
		      	if(field instanceof FNumField){
		      		dummyPropertiesValuesArray[field.getIndexOfPropertyInDummyArray()] = Double.valueOf(0);	
		      	}else if(field instanceof FBoolField){
		      		dummyPropertiesValuesArray[field.getIndexOfPropertyInDummyArray()] = Boolean.valueOf(false);
		      	}else if(field instanceof FStringField){
		      		dummyPropertiesValuesArray[field.getIndexOfPropertyInDummyArray()] = "";
		      	}else if(field instanceof FObjectField){
		      		dummyPropertiesValuesArray[field.getIndexOfPropertyInDummyArray()] = null;
		      	}
		      }
  			}
  		}
  	}
  	if(!checkIfExists){
  		boolean forceControlerBackup = isForceControler();
		  forceControler(true);
		  computePropertiesWithFormula_IfNeeded();
		  forceControler(forceControlerBackup);
  	}
  }
  
  public void computePropertiesWithFormula_IfNeeded(){
    FocDesc thisFocDesc = getThisFocDesc();
    int fieldsSize = thisFocDesc.getFieldsSize();
    for(int i = 0; i < fieldsSize; i++){
      FField field = thisFocDesc.getFieldAt(i);
      if(field != null && field.isWithFormula()){
        FProperty property = getFocProperty(field.getID());
        field.computePropertyUsingFormulaIfNeeded(property);
      }
    }
  }
  
  public void computePropertiesWithFormula(){
    FocDesc thisFocDesc = getThisFocDesc();
    int fieldsSize = thisFocDesc.getFieldsSize();
    for(int i = 0; i < fieldsSize; i++){
      FField field = thisFocDesc.getFieldAt(i);
      if(field != null && field.isWithFormula()){
        field.computePropertyUsingFormula(this);
      }
    }
  }
  
  //Cloud Storage
  public InputStream getPropertyCloudStorage(int fieldID){
  	FProperty prop = getFocProperty(fieldID);
  	return (InputStream) (prop != null ? prop.getObject() : null);
  }

  public InputStream getPropertyCloudStorage(String fieldName){
  	FProperty prop = getFocPropertyByName(fieldName);
  	return (InputStream) (prop != null ? prop.getObject() : null);
  }

  public void setPropertyCloudStorage(String fieldNAME, InputStream inputStream, String directoryName, boolean createDirectoryIfNeeded, String fileName){
  	FField fld = getThisFocDesc() != null ? getThisFocDesc().getFieldByName(fieldNAME) : null;
  	if(fld != null){
  		setPropertyCloudStorage(fld.getID(), inputStream, directoryName, createDirectoryIfNeeded, fileName);
  	}
  }
  
  public void setPropertyCloudStorage(int fieldID, InputStream inputStream, String directoryName, boolean createDirectoryIfNeeded, String fileName){
  	FCloudStorageProperty prop = (FCloudStorageProperty) getFocProperty(fieldID);
  	if(prop != null){
  		if(Utils.isStringEmpty(directoryName)){
  			directoryName = Globals.getApp().getCloudStorageDirectory();
  		}
  		prop.setDirectory(directoryName, createDirectoryIfNeeded);
  		prop.generateKey();
  		prop.setObject(inputStream);
  	}
  }
  
  public InputStream getPropertyCloudImage(int fieldID){
  	FProperty prop = getFocProperty(fieldID);
  	return (InputStream) (prop != null ? prop.getObject() : null);
  }
  
  public void setPropertyCloudImage(int fieldID, InputStream inputStream, String directoryName, boolean createDirectoryIfNeeded){
  	FCloudImageProperty prop = (FCloudImageProperty) getFocProperty(fieldID);
  	if(prop != null){
  		if(Utils.isStringEmpty(directoryName)){
  			directoryName = Globals.getApp().getCloudStorageDirectory();
  		}
  		prop.setDirectory(directoryName, createDirectoryIfNeeded);
  		prop.generateKey();
  		prop.setObject(inputStream);
  	}
  }

  public String getPropertyString(int fieldID){
  	FProperty prop = getFocProperty(fieldID);  	
  	return prop != null ? prop.getString() : "";
  }

  public void setPropertyString(int fieldID, String str){
  	FProperty prop = getFocProperty(fieldID);  	
  	if(prop != null){
  		prop.setString(str);
  	}
  }
  
  public String getPropertyString(String fieldName){
  	FField field = getThisFocDesc().getFieldByName(fieldName);
  	return field != null ? getPropertyString(field.getID()) : "";
  }

  public void setPropertyString(String fieldName, String value){
  	FField field = getThisFocDesc().getFieldByName(fieldName);
  	if(field != null){
  		setPropertyString(field.getID(), value);
  	}
  }

  public java.sql.Date getPropertyDate(int fieldID){
  	FDate prop = (FDate) getFocProperty(fieldID);  	
  	return prop != null ? prop.getDate() : null;
  }

  public java.sql.Date getPropertyDate(String fieldName){
  	FField field = getThisFocDesc().getFieldByName(fieldName);
  	return field != null ? getPropertyDate(field.getID()) : null;
  }

  public void setPropertyDate(int fieldID, java.sql.Date date){
  	setPropertyDate(fieldID, date, false);
  }

  public void setPropertyDate(String fieldName, java.sql.Date date){
  	FField field = getThisFocDesc().getFieldByName(fieldName);
  	if(field != null){
  		setPropertyDate(field.getID(), date, false);
  	}
  }
  
	public void setPropertyNull_WithListener(String fieldName) {
		FProperty prop = getFocPropertyByName(fieldName);
		if (prop != null) {
			prop.setValueNull_AndResetIntrinsicValue(true);
		}
	}

  public void setPropertyDate_WithoutListeners(int fieldID, java.sql.Date date){
  	setPropertyDate(fieldID, date, true);
  }

  private void setPropertyDate(int fieldID, java.sql.Date date, boolean withoutListeners){
  	if(date != null){
	  	FDate prop = (FDate)getFocProperty(fieldID);  	
	  	if(prop != null){
	  		boolean oldValue = false;
	  		if(withoutListeners){
	  			oldValue = prop.isDesactivateListeners();
	  			prop.setDesactivateListeners(true);
	  		}
	  		prop.setDate(date);
	  		if(withoutListeners) prop.setDesactivateListeners(oldValue);
	  	}
  	}
  }

  public java.sql.Time getPropertyTime(int fieldID){
  	FTime prop = (FTime) getFocProperty(fieldID);  	
  	return prop != null ? prop.getTime() : null;
  }

  public java.sql.Time getPropertyTime(String fieldName){
  	FField field = getThisFocDesc().getFieldByName(fieldName);
  	return field != null ? getPropertyTime(field.getID()) : null;
  }

  public void setPropertyTime(int fieldID, java.sql.Time time){
  	FTime prop = (FTime)getFocProperty(fieldID);  	
  	if(prop != null){
  		prop.setTime(time);
  	}
  }
  
  public void setPropertyTime(int fieldID, String time){
  	FTime prop = (FTime)getFocProperty(fieldID);  	
  	if(prop != null){
  		prop.setString(time);
  	}
  }
  
  public void setPropertyTime(String fieldName, java.sql.Time time){
  	FField field = getThisFocDesc().getFieldByName(fieldName);
  	if(field != null){
  		setPropertyTime(field.getID(), time);
  	}
  }

  public void setPropertyTime(String fieldName, String time){
  	FField field = getThisFocDesc().getFieldByName(fieldName);
  	if(field != null){
  		setPropertyTime(field.getID(), time);
  	}
  }
  
  public int getPropertyInteger(int fieldID){
  	FProperty prop = getFocProperty(fieldID);  	
  	return prop != null ? prop.getInteger() : 0;
  }

  public int getPropertyInteger(String fieldName){
  	FField field = getThisFocDesc().getFieldByName(fieldName);  	
  	return field != null ? getPropertyInteger(field.getID()) : 0;
  }
  
  public void setPropertyInteger(String fieldName, int val){
  	FField field = getThisFocDesc().getFieldByName(fieldName);  	
  	if(field != null){
  		setPropertyInteger(field.getID(), val);
  	}
  }
    
  public void setPropertyInteger(int fieldID, int val){
  	setPropertyInteger(fieldID, val, false);
  }
  
  public void setPropertyInteger(int fieldID, int val, boolean withoutListeners){
  	FProperty prop = getFocProperty(fieldID);  	
  	if(prop != null){
  		boolean oldValue = false;
  		if(withoutListeners){
  			oldValue = prop.isDesactivateListeners();
  			prop.setDesactivateListeners(true);
  		}
  		prop.setInteger(val);
  		if(withoutListeners) prop.setDesactivateListeners(oldValue);
  	}
  }
  
  public long getPropertyLong(int fieldID){
  	FProperty prop = getFocProperty(fieldID);  	
  	return prop != null ? prop.getLong() : 0;
  }

  public long getPropertyLong(String fieldName){
  	FField field = getThisFocDesc().getFieldByName(fieldName);  	
  	return field != null ? getPropertyLong(field.getID()) : 0;
  }
  
  public long getPropertyLong(String aliasName, String fieldName){
  	FField field = getThisFocDesc().getFieldByName(aliasName+"-"+fieldName);  	
  	return field != null ? getPropertyLong(field.getID()) : 0;
  }
  
  public void setPropertyLong(String fieldName, long val){
  	FField field = getThisFocDesc().getFieldByName(fieldName);  	
  	if(field != null){
  		setPropertyLong(field.getID(), val);
  	}
  }
  
  public void setPropertyLong(int fieldID, long val){
  	setPropertyLong(fieldID, val, false);
  }
  
  public void setPropertyLong(int fieldID, long val, boolean withoutListeners){
  	FProperty prop = getFocProperty(fieldID);  	
  	if(prop != null){
  		boolean oldValue = false;
  		if(withoutListeners){
  			oldValue = prop.isDesactivateListeners();
  			prop.setDesactivateListeners(true);
  		}
  		prop.setLong(val);
  		if(withoutListeners) prop.setDesactivateListeners(oldValue);
  	}
  }
  
  public double getPropertyCurrencyRate_DisplayDouble(int fieldID){
  	FCurrRate prop = (FCurrRate) getFocProperty(fieldID);  	
  	return prop != null ? prop.getDouble() : 0;
  }

  public double getPropertyCurrencyRate_RateFactor(int fieldID){
  	FCurrRate prop = (FCurrRate) getFocProperty(fieldID);  	
  	double rate = prop != null ? prop.getRateFactor() : 0;
  	return rate;
  }

  public boolean getPropertyCurrencyRate_IsReverse(int fieldID){
  	FCurrRate prop = (FCurrRate) getFocProperty(fieldID);  	
  	return prop != null ? prop.isReverse() : false;
  }

  public void setPropertyCurrencyRate(int fieldID, boolean reverse, double displayDouble){
  	FCurrRate currRate = (FCurrRate) getFocProperty(fieldID);
  	if(currRate != null){
  		currRate.setReverse(reverse);
  		currRate.setDouble(displayDouble);//We set what should be displayed positive and everything
  	}
  }

  public void setPropertyCurrencyRate_DisplayDouble(int fieldID, double displayDouble){
  	FCurrRate prop = (FCurrRate) getFocProperty(fieldID);  	
  	if(prop != null){
  		prop.setDouble(displayDouble);
  	}
  }

  public void setPropertyCurrencyRate_RateFactor(int fieldID, double rateFactor){
  	FCurrRate prop = (FCurrRate) getFocProperty(fieldID);  	
  	if(prop != null){
  		double dblValue = rateFactor;
  		if(prop.isReverse()){
  			dblValue = 1 / rateFactor;
  		}
  		prop.setDouble(dblValue);
  	}
  }

  public double getPropertyDouble(int fieldID){
  	FProperty prop = getFocProperty(fieldID);  	
  	return prop != null ? prop.getDouble() : 0;
  }

  public double getPropertyDouble(String fieldName){
  	FField field = getThisFocDesc().getFieldByName(fieldName);
  	return field != null ? getPropertyDouble(field.getID()) : 0;
  }

  public void setPropertyDouble(int fieldID, double val){
    setPropertyDoubleGeneral(fieldID, val, false);
  }
  
  public void setPropertyDouble(String fieldName, double val){
    setPropertyDoubleGeneral(fieldName, val, false);
  }
  
  public void setPropertyDoubleWithoutListener(int fieldID, double val){
    setPropertyDoubleGeneral(fieldID, val, true);
  }

  private void setPropertyDoubleGeneral(int fieldID, double val, boolean withoutListener){
    FProperty prop = getFocProperty(fieldID);   
    if(prop != null){
    	boolean backup = prop.isDesactivateListeners();
      if(withoutListener) prop.setDesactivateListeners(true);
      prop.setDouble(val);
      if(withoutListener) prop.setDesactivateListeners(backup);
    }
  }

  private void setPropertyDoubleGeneral(String fieldName, double val, boolean withoutListener){
  	FField fld = getThisFocDesc().getFieldByName(fieldName);
  	if(fld != null){
  		setPropertyDoubleGeneral(fld.getID(), val, withoutListener);
  	}
  }

  public FocObject getPropertyObject_WithoutCreateIfNeeded(int fieldID){
  	FObject prop = (FObject)getFocProperty(fieldID);  	
  	return prop != null ? (FocObject) prop.getObject() : null;
  }

  public FocObject propertyObject_NewIfNull(int fieldID){
  	FocObject obj = null;
  	FObject objProperty = (FObject) getFocProperty(fieldID);
  	if(objProperty != null){
  		obj = objProperty.newObjectCreatedLocally_IfNull();
  	}
  	return obj;
  }
  
  public FocObject propertyObject_NewIfNull(String fieldName){
  	FocObject obj = null;
  	FObject objProperty = (FObject) getFocPropertyByName(fieldName);
  	if(objProperty != null){
  		obj = objProperty.newObjectCreatedLocally_IfNull();
  	}
  	return obj;
  }
  
  public FocObject getPropertyObject(int fieldID){
  	FObject prop = (FObject)getFocProperty(fieldID);  	
  	return prop != null ? (FocObject) prop.getObject_CreateIfNeeded() : null;
  }

  public FocObject getPropertyObject(String fieldName){
  	FField field = getThisFocDesc().getFieldByName(fieldName);
  	return field != null ? getPropertyObject(field.getID()) : null;
  }

  public void setPropertyObject(String fieldName, FocObject obj){
  	FProperty prop = getFocPropertyByName(fieldName);
  	if(prop != null){
  		prop.setObject(obj);
  	}
  }

  public void setPropertyObject(int fieldID, FocObject obj){
  	FProperty prop = getFocProperty(fieldID);  	
  	if(prop != null){
  		prop.setObject(obj);
  	}
  }

  public long getPropertyObjectLocalReference(String fieldName){
		FObject prop = (FObject) getFocPropertyByName(fieldName);
		return prop != null ? prop.getLocalReferenceInt() : 0;
  }
  
  public void setPropertyObjectLocalReference(String fieldName, long reference){
  	FObject prop = (FObject) getFocPropertyByName(fieldName);
  	if(prop != null){
  		prop.setLocalReferenceInt(reference);
  	}
  }
  
  public BufferedImage getPropertyImage(int fieldID){
  	FImageProperty prop = (FImageProperty) getFocProperty(fieldID);  	
  	return prop != null ? (BufferedImage) prop.getImageValue() : null;
  }

  public void setPropertyImage(int fldID, BufferedImage image){
  	FImageProperty prop = (FImageProperty) getFocProperty(fldID);
  	if(prop != null){
  		prop.setImageValue(image);
  	}
  }
  
  public void setPropertyImage(String fieldName, BufferedImage image){
  	FImageProperty prop = (FImageProperty) getFocPropertyByName(fieldName);
  	if(prop != null){
  		prop.setImageValue(image);
  	}
  }

  public FocList getPropertyList(String fieldName){
  	FList prop = (FList)getFocPropertyByName(fieldName);  	
  	return prop != null ? (FocList) prop.getList() : null;
  }
  
  public FocList getPropertyList(int fieldID){
  	FList prop = (FList)getFocProperty(fieldID);  	
  	return prop != null ? (FocList) prop.getList() : null;
  }

  public FocList getPropertyList(int fieldID, int mode){
  	FocList list = null;
  	FList prop = (FList)getFocProperty(fieldID);
  	if(prop != null){
  		list = prop.getListWithoutLoad();  		
	  	if(mode == FocList.FORCE_RELOAD){
	  		list.reloadFromDB();
	  	}else if(mode == FocList.LOAD_IF_NEEDED){
	  		list.loadIfNotLoadedFromDB();
	  	}
  	}
  	return list;
  }
    
  public boolean getPropertyBoolean(int fieldID){
  	int i = getPropertyInteger(fieldID);
  	return i != 0;
  }

  public boolean getPropertyBoolean(String fieldName){
  	FField field = getThisFocDesc().getFieldByName(fieldName);
  	return field != null ? getPropertyBoolean(field.getID()) : null;
  }
  
  public void setPropertyBoolean(int fieldID, boolean b){
  	setPropertyInteger(fieldID, b ? 1 : 0);
  }

  public void setPropertyBoolean(String fieldName, boolean b){
    FField field = getThisFocDesc().getFieldByName(fieldName);
    if(field != null){
    	setPropertyInteger(field.getID(), b ? 1 : 0);
    }
  }
  
  public void setPropertyBooleanWithoutListeners(int fieldID, boolean b){
  	FProperty prop = getFocProperty(fieldID);
  	boolean backup = false;
  	if(prop != null){
	  	backup = prop.isDesactivateListeners();
	  	prop.setDesactivateListeners(true);
  	}
  	setPropertyBoolean(fieldID, b);
  	if(prop != null) prop.setDesactivateListeners(backup);
  }

  public int getPropertyMultiChoice(int fieldID){
  	return getPropertyInteger(fieldID);
  }
  
  public void setPropertyMultiChoice(String fieldName, int value){
  	FField field = getThisFocDesc().getFieldByName(fieldName);
  	if(field != null){
  		setPropertyMultiChoice(field.getID(), value);
  	}
  }

  public void setPropertyMultiChoice(int fieldID, int choice){
  	setPropertyInteger(fieldID, choice);
  }
  
  public void setPropertyMultiChoice(int fieldID, String choice){
    FMultipleChoice mc = (FMultipleChoice) getFocProperty(fieldID);
    mc.setString(choice);
  }

  public String getPropertyMultiChoiceTitle(int fieldID){
  	FMultipleChoice mc = (FMultipleChoice) getFocProperty(fieldID);
  	return mc != null ? mc.getString() : null;
  }
  
  public String getPropertyMultiChoiceTitle(String fieldName){
  	FMultipleChoice mc = (FMultipleChoice) getFocPropertyByName(fieldName);
  	return mc != null ? mc.getString() : null;
  }
  
  public Color getPropertyColor(int fieldID){
  	FColorProperty prop = (FColorProperty) getFocProperty(fieldID);  	
  	return prop != null ? prop.getColor() : null;
  }
  
  public void setPropertyColor(int fieldID, Color color){
  	FColorProperty prop = (FColorProperty) getFocProperty(fieldID);  	
  	if(prop != null){
  		prop.setColor(color);
  	}
  }

  public FocDesc getPropertyDesc(String fieldName){
  	FocDesc ret = null;
  	FField fld = getThisFocDesc() != null ? getThisFocDesc().getFieldByName(fieldName) : null;
  	if(fld != null) {
  		ret = getPropertyDesc(fld.getID());
  	}
  	return ret;
  }

  public void setPropertyDesc(String fieldName, FocDesc focDesc){
  	FField fld = getThisFocDesc() != null ? getThisFocDesc().getFieldByName(fieldName) : null;
  	if(fld != null) {
  		setPropertyDesc(fld.getID(), focDesc);
  	}
  }

  public FocDesc getPropertyDesc(int fieldID){
  	IFDescProperty prop = (IFDescProperty) getFocProperty(fieldID);  	
  	return prop != null ? prop.getSelectedFocDesc() : null;
  }

  public void setPropertyDesc(int fieldID, FocDesc focDesc){
  	FDescPropertyStringBased prop = (FDescPropertyStringBased) getFocProperty(fieldID);  	
  	if(prop != null){
  		prop.setString(focDesc.getStorageName());
  	}
  }
  
  public void setPropertyDesc(int fieldID, String focDesc){
  	FDescPropertyStringBased prop = (FDescPropertyStringBased) getFocProperty(fieldID);  	
  	if(prop != null){
  		prop.setString(focDesc);
  	}
  }

  public void putFocPropertyWithSpecifiedIndex(FProperty prop, int index) {
    if (propertiesArray == null) {
      initPropertiesArray();
    }
    if (propertiesArray != null) {
      if(propertiesArray.length <= index){
        Globals.logString("++++++++++++++++++++ index = " + index + " lenght = " + propertiesArray.length + " Title " + prop.getFocField().getTitle());
      }
      propertiesArray[index] = prop;
    }
  }

  public void putFocProperty(FProperty prop) {
    if(prop != null){
      FField field = prop.getFocField();
      if (field != null) {        
        putFocPropertyWithSpecifiedIndex(prop, field.getIndexOfPropertyInArray());
      }
    }
  }
  
  public String getPropertyMultipleChoiceStringBased(int fieldID){
  	return getPropertyString(fieldID);
  }
  
  public void setPropertyMultipleChoiceStringBase(int fieldID, String choice){
  	setPropertyString(fieldID, choice);
  }
  
  public int getMandatoryFieldCount(){
  	int count = 0;
  	
  	FocDesc focDesc = getThisFocDesc();
  	count = focDesc != null ? focDesc.mandatoryFieldCount() : 0;
  	
  	IFocObjectPlugIn plugIn = getIFocObjectPlugIn();
  	if(plugIn != null){
  		count = plugIn.getMandatoryFieldCount(count);
  	}
  	
  	return count;
  }

  public FField getMandatoryFieldAt(int i){
  	FField fld = null;
  	
  	FocDesc focDesc = getThisFocDesc();
  	fld = focDesc != null ? focDesc.mandatoryFieldAt(i) : null;
  	
  	IFocObjectPlugIn plugIn = getIFocObjectPlugIn();
  	if(plugIn != null){
  		fld = plugIn.getMandatoryFieldAt(i, fld);
  	}
  	
  	return fld;
  }
  
  public boolean isNotCompletedYet(){
  	return getPropertyBoolean(FField.FLD_NOT_COMPLETED_YET);
  }
  
  public boolean isContentValid(boolean displayMessage){
    boolean valid = !isContentValidMessageOn();
    if(valid){
    	
      for(int i=0; i<getMandatoryFieldCount() && valid; i++){
        FField field = getMandatoryFieldAt(i);
        if(field == null){
        	field = getMandatoryFieldAt(i);
        }
        if(!field.isReflectingField()){
	        FProperty prop = getFocProperty(field.getID());
	        
	        if(prop == null){
	        	Globals.logString("!!! NULL PROPERTY FOR FIELD:"+field.getName()+" "+field.getID());
	        }
	        
	        valid = isPropertyDataValid(field, prop);
	        
	        if(!valid && displayMessage){ 
	        	String fieldStr = (field.getTitle() != null && !field.getTitle().isEmpty()) ? field.getTitle() + " ("+field.getName()+")" : field.getName();
	        	String tableStr = getThisFocDesc() != null ? getThisFocDesc().getStorageName() : "";
	          StringBuffer message = new StringBuffer("The field \""+fieldStr+"\" in "+tableStr+" cannot remain empty!\n");
	          popupContentValidMessage(message);	        	
	        }
        }
      }
      
    }
    return valid;
  }
  
  public boolean isPropertyDataValid(FField field, FProperty prop){
    boolean valid = 		field != null
    								&& 	prop != null 
    								&& (!prop.isEmpty() || (isNotCompletedYet() && field.getMandatoryMode() == FField.MANDATORY_YES_BUT_CAN_FILL_LATER));
    return valid;
  }
  
  public boolean isContentValidMessageOn() {
  	return (flags & FLG_CONTENT_VALID_MESSAGE_ON) != 0;  	
  }

  public void setContentValidMessageOn(boolean contentValidMessageOn) {
    if(contentValidMessageOn){
      flags = (char)(flags | FLG_CONTENT_VALID_MESSAGE_ON);
    }else{
      flags = (char)(flags & ~FLG_CONTENT_VALID_MESSAGE_ON);
    }
  }

  public void popupContentValidMessage(String message){
    setContentValidMessageOn(true);
    Globals.showNotification("Content Not Valid",message, IFocEnvironment.TYPE_ERROR_MESSAGE);
    if(Globals.getDisplayManager() != null){
    	Globals.getDisplayManager().popupMessage(message);
    }
    setContentValidMessageOn(false);    
  }

  public void popupContentValidMessage(StringBuffer message){
    popupContentValidMessage(message.toString());
  }
  
  public StringBuffer checkDeletionWithMessage(){
  	StringBuffer message = null;
    
    if(!isDeletable()){
    	message = new StringBuffer("This item cannot be deleted.\nIt might be a system object.\nFor further assistance please call 01Barmaja.");
    }else if(focObject_IsLocked()){
    	message = new StringBuffer("This item is Locked and cannot be deleted.");
    }else if(!workflow_IsAllowDeletion()){
    	workflow_IsAllowDeletion();
    	if (ConfigInfo.isArabic()) {
    		message = new StringBuffer("ليس لديك صلحية حذف هذا البيان");
			} else {				
				message = new StringBuffer("You don't have deletion rights on this transaction.");
			}
    }else{
    	StringBuffer messageInternal = new StringBuffer();
      int refNbr = referenceCheck_GetNumber(messageInternal);
      if(refNbr > 0){
      	message = messageInternal;
        referenceCheck_CompleteTheMessage(refNbr, message);
      }
    }
  	
  	return message;
  }
  
  public boolean isDeletable() {
  	return (flags & FLG_DELETABLE) != 0;
  }
  
  public void setDeletable(boolean deletable) {
    if(deletable){
      flags = (char)(flags | FLG_DELETABLE);
    }else{
      flags = (char)(flags & ~FLG_DELETABLE);
    }
  }
  
  public FField getFieldByName(String fieldName){
  	return getThisFocDesc().getFieldByName(fieldName);
  }
  
  public FProperty getFocProperty(int fieldID) {
  	return getFocProperty_FocObject(fieldID);
  }
  
  public FProperty getFocProperty_FocObject(int fieldID) {
    FProperty property = null;
    FField    field    = null;
    
    //BAntoineS - AUTOINCREMENT
    if (fieldID == FField.MASTER_REF_FIELD_ID) {
      if (masterObject != null) {
      	if(masterObject.needsAssignReference()){
      		
      	}
        //masterObject.assignReferenceIfNeeded();
        property = masterObject.getIdentifierProperty();
      }
    }
    //EAntoineS - AUTOINCREMENT
    if(property == null){
      FocDesc thisFocDesc = getThisFocDesc();

      if (!isLoadedFromDB() && !isCreated() && fieldID != FField.REF_FIELD_ID && hasRealReference()) {
        load();
      }
      
      //field = field == null ? thisFocDesc.getFieldArrayByID(fieldID) : field;      
      field = thisFocDesc.getFieldByID(fieldID);      

      if(field != null){
      	if(/*property == null && */field.getIndexOfPropertyInDummyArray() >= 0){
      		Object valueObj = getObjectInDummyArrayAt(field.getIndexOfPropertyInDummyArray());
      		if(field instanceof FNumField){
      			property = getDummyProperty_Double();
      			property.setFocObject(null);
      			getDummyProperty_Double().setDouble_WithoutListeners((Double)valueObj);
      		}else if(field instanceof FBoolField){
      			property = getDummyProperty_Boolean();
      			property.setFocObject(null);
      			getDummyProperty_Boolean().setBoolean_WithoutListeners((Boolean)valueObj);
      		}else if(field instanceof FStringField){
      			property = getDummyProperty_String();
      			property.setFocObject(null);
      			getDummyProperty_String().setString_WithoutListeners((String)valueObj);
      		}else if(field instanceof FObjectField){
      			property = getDummyProperty_Object();
      			property.setFocObject(null);
      			getDummyProperty_Object().setObject_WithoutListeners((FocObject)valueObj);
      		}
      		property.setFocField(field);
      		property.setFocObject(this);
      	}else if(propertiesArray != null){
	       	property = (FProperty) propertiesArray[field.getIndexOfPropertyInArray()];
	      }
      	
      }
    }

    //If Property not found we check if it is for ArrayField
//    if(property == null){
//      if(thisFocDesc != null){
//        FFieldArray fieldArray = (FFieldArray)thisFocDesc.getFieldArrayByID(fieldID);
//        if(fieldArray != null){
//          FField curField = fieldArray.getCurrentField();
//          if(curField != null && curField.getID() != fieldID){
//            property = (FProperty) propertiesArray[curField.getIndexOfPropertyInArray()];
//          }
//        }
//      }
//    }
    
    return property;
  }

  public FProperty getFocPropertyByName(String fieldName){
  	FProperty prop = null;
  	FocDesc focDesc = getThisFocDesc();
  	if(focDesc != null){
  		int fieldID = focDesc.getFieldIDByName(fieldName);
  		if(fieldID != FField.NO_FIELD_ID){
	  		prop = getFocProperty(fieldID);
  		}
  	}
  	return prop;
  }
  
  public FProperty getFocPropertyForPath(String name){
    FProperty property = null;
    
    FocDesc focDesc = getThisFocDesc();
    
    if(name != null){
	    if(name.contains(".")){
	      PropertyFocObjectLocator propertyFocObjectLocator = new PropertyFocObjectLocator();
	      propertyFocObjectLocator.parsePath(name, focDesc, this, null);
	      property = propertyFocObjectLocator.getLocatedProperty();
	      propertyFocObjectLocator.dispose();
	    }else{
	      property = getFocPropertyByName(name);
	    }
    }
    return property;
  }
  
  public void setPropertiesValues(String[] fieldNames, Object[] values){
  	if(fieldNames != null && values != null && values.length == fieldNames.length){
	  	for(int i=0; i<fieldNames.length; i++){
	  		FProperty prop = getFocPropertyByName(fieldNames[i]);
	  		if(prop != null){
	  			prop.setObject(values[i]);
	  		}
	  	}
  	}
  }
  
  public void lockAllproperties(){
    FocFieldEnum iter = new FocFieldEnum(getThisFocDesc(), this, FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
    while(iter != null && iter.hasNext()){
      iter.next();
      FProperty prop = (FProperty) iter.getProperty();
      if(prop != null){
        prop.setValueLocked(true);
      }
    }
  }
  
  public void unlockAllProperties(){
    FocFieldEnum iter = new FocFieldEnum(getThisFocDesc(), this, FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
    while(iter != null && iter.hasNext()){
      iter.next();
      FProperty prop = (FProperty) iter.getProperty();
      if(prop != null){
        prop.setValueLocked(false);
      }
    }
  }
  
  public String toString() {
    String str = "";
    FocFieldEnum enumer = newFocFieldEnum(FocFieldEnum.CAT_KEY, FocFieldEnum.LEVEL_PLAIN);
    while (enumer != null && enumer.hasNext()) {
      enumer.next();
      FProperty prop = enumer.getProperty();
      if (prop != null) {
        str = str + prop.getString();
      }
    }
    return str;
  }

  public String toString_Super() {
    return super.toString();
  }

  public FPanel newUserLevelPanel(){
    FPanel panel = null;
    FInt levelProp = (FInt)getFocProperty(FField.RIGHT_LEVEL_FIELD_ID);
    FString dateTime = (FString) getFocProperty(FField.RIGHT_LEVEL_DATETIME_FIELD_ID);
    
    if(levelProp.getInteger() > 0){
      panel = new FPanel();
      FObject userProp = (FObject)getFocProperty(FField.RIGHT_LEVEL_USER_FIELD_ID);
      FocUser objUser = (FocUser) userProp.getObject_CreateIfNeeded();
      
      FGLabel label = new FGLabel(levelProp.getInteger() + ":" + objUser.getName() + " " + dateTime.getString());
      
      panel.add(label, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE);
    }
    return panel;
  }
  
  public int getRightsLevel(){
    FInt levelProp = (FInt)getFocProperty(FField.RIGHT_LEVEL_FIELD_ID);
    return levelProp != null ? levelProp.getInteger() : 0;
  }

  public FocUser getRightsLevelLastUser(){
    FObject userProp = (FObject)getFocProperty(FField.RIGHT_LEVEL_USER_FIELD_ID);
    return userProp != null ? (FocUser) userProp.getObject_CreateIfNeeded() : null;
  }

  @Deprecated
  public FPanel popup() {
    FPanel pan = newDetailsPanel(FocObject.DEFAULT_VIEW_ID);
    Globals.getDisplayManager().changePanel(pan);
    return pan;
  }
  
  public FocObject getThis() {
    return this;
  }

  public FocDesc getThisFocDesc() {
  	return thisFocDesc;
  }

  @SuppressWarnings("unchecked")
	public FPanel newDetailsPanel(int viewID){
  	Globals.setMouseComputing(true);
    FPanel panel = null;
    try {
    	Class cls = getThisFocDesc().getGuiDetailsPanelClass();
    	if( cls != null ){
        Class[]     argsDeclare = {FocObject.class, int.class};
        Object[]    args        = {this, viewID };
        Constructor constr      = cls.getConstructor(argsDeclare);
        
        Globals.setMouseComputing(true);
        panel = (FPanel) constr.newInstance(args);
        Globals.setMouseComputing(false);
      }
    } catch (Exception e) {
      Globals.logException(e);
    }
  	Globals.setMouseComputing(false);
    return panel;  	
  }
  
  public FocFieldEnum newFocFieldEnum(int category, int level) {
    return new FocFieldEnum(this.getThisFocDesc(), this, category, level);
  }

  public FocObject getMasterObject() {
    return masterObject;
  }

  public void setMasterObject(FocObject object) {
    masterObject = object;
  }

  public void copyPropertiesFrom(FocObject sourceObj){
  	copyPropertiesFrom(sourceObj, null);
  }

  /*
  private boolean inExcludedArray(int[] excludedProperties, int fieldID){
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
  */
  
  public void copyPropertiesFrom(FocObject sourceObj, int[] excludedProperties){
    copyPropertiesFrom(sourceObj, false, excludedProperties);
  }
  
  public void copyPropertiesFrom(FocObject sourceObj, boolean excludeLists, int[] excludedProperties){
    FocDesc focDesc         = this.getThisFocDesc();
    FField  identifierField = focDesc.getIdentifierField();
    
    focDesc.copyPropertiesForFocObject(this, sourceObj, excludedProperties);
    /*
    FocFieldEnum iter = focDesc.newFocFieldEnum(FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
    while(iter != null && iter.hasNext()){
      FField field = (FField) iter.next();
      if(field != null && field != identifierField && field.getID() != FField.REF_FIELD_ID && field.getID() != FField.MASTER_REF_FIELD_ID && !inExcludedArray(excludedProperties, field.getID())){
        FProperty thisProp = this.getFocProperty(field.getID());
        FProperty srcProp = sourceObj.getFocProperty(field.getID());
        if(thisProp != null && srcProp != null){
        	thisProp.copy(srcProp);
        }
      }
    }
    */
    //Globals.logString(dupObj.getDebugInfo());
    
    if(!excludeLists){
	    FocFieldEnum iter = focDesc.newFocFieldEnum(FocFieldEnum.CAT_LIST, FocFieldEnum.LEVEL_PLAIN);
	    while(iter != null && iter.hasNext()){
	      FField field = (FField) iter.next();
	      if(field != null && field != identifierField && !FocDesc.inExcludedArray(excludedProperties, field.getID())){
	        FProperty thisProp = this.getFocProperty(field.getID());
	        FProperty srcProp = sourceObj.getFocProperty(field.getID());
	        thisProp.copy(srcProp);
	
	        FList dupListProp = (FList) thisProp;
	        FocList list = (FocList) dupListProp.getList();
	        list.setLoaded(true);
	      }
	    }
    }
  }
  
  /*public FocObject duplicate(FocObject initialTargetObj, FocObject newMasterObject, boolean callDuplicateModification){
    FocObject dupObj = null;
    try{
      FocDesc focDesc = this.getThisFocDesc();
      
      if(initialTargetObj == null){
        FocConstructor constr = new FocConstructor(focDesc, null, newMasterObject);
        dupObj = constr.newItem();
        //dupObj = focDesc.newClassInstance(constr);
      }else{
        dupObj = initialTargetObj;
        dupObj.setMasterObject(newMasterObject);
      }
      
      //Globals.logString(dupObj.getDebugInfo());
      if(dupObj != null && focDesc != null){
        dupObj.setCreated(true);
        dupObj.copyPropertiesFrom(this);
        
        //B-DUP
        if(callDuplicateModification){
          dupObj.duplicationModification(this);
        }
        dupObj.validate(false);
        //E-DUP
      }
    }catch (Exception e){
      Globals.logException(e);
    }
    return dupObj;
  }*/
  
  public FocObject duplicate(FocObject initialTargetObj, FocObject newMasterObject, boolean callDuplicateModification){
    return duplicate(initialTargetObj, newMasterObject, callDuplicateModification, true);
  }
  
  public FocObject duplicateNoValidate(FocObject initialTargetObj, FocObject newMasterObject, boolean callDuplicateModification){
    return duplicate(initialTargetObj, newMasterObject, callDuplicateModification, false);
  }
  
  public FocObject duplicate(FocObject initialTargetObj, FocObject newMasterObject, boolean callDuplicateModification, boolean validate){
  	return duplicate(initialTargetObj, newMasterObject, callDuplicateModification, validate, duplication_getExcludedPropertiesArray());
  }
  
  public FocObject duplicate(FocObject initialTargetObj, FocObject newMasterObject, boolean callDuplicateModification, boolean validate, int[] excludedProperties){
  	return duplicate(initialTargetObj, newMasterObject, callDuplicateModification, validate, false, excludedProperties);
  }
  
  public FocObject duplicate(FocObject initialTargetObj, FocObject newMasterObject, boolean callDuplicateModification, boolean validate, boolean excludeLists, int[] excludedProperties){
    FocObject dupObj = null;
    try{
      FocDesc focDesc = this.getThisFocDesc();
      
      if(initialTargetObj == null){
      	if(getFatherSubject() instanceof FocList && (newMasterObject == null || getFatherSubject().getFatherSubject() == newMasterObject)){
      		dupObj = ((FocList) getFatherSubject()).newEmptyItem(); 
      	}else{
          FocConstructor constr = new FocConstructor(focDesc, null, newMasterObject);
          dupObj = constr.newItem();
      	}
        
        //dupObj = focDesc.newClassInstance(constr);
      }else{
        dupObj = initialTargetObj;
        dupObj.setMasterObject(newMasterObject);
      }
      
      //Globals.logString(dupObj.getDebugInfo());
      if(dupObj != null && focDesc != null){
      	if(getThisFocDesc().hasOrderField()){
      		if(excludedProperties == null){
      			int[] temp = {FField.FLD_ORDER};  
      			excludedProperties = temp;
      		}else{
      			int[] temp = new int[excludedProperties.length+1];
      			for(int i=0; i<excludedProperties.length; i++){
      				temp[i] = excludedProperties[i];
      			}
      			temp[excludedProperties.length] = FField.FLD_ORDER;
      			excludedProperties = temp;
      		}
      	}
      	duplication_CopyPropertiesAndSetToCreated(dupObj, excludeLists, excludedProperties);
      	
        //B-DUP
        if(callDuplicateModification){
          dupObj.duplicationModification(this);
        }
        if(validate){
        	dupObj.validate(false);
        }
        //E-DUP
      }
    }catch (Exception e){
      Globals.logException(e);
    }
    return dupObj;
  }
  
  protected void duplication_CopyPropertiesAndSetToCreated(FocObject dupObj, int[] excludedProperties){
  	duplication_CopyPropertiesAndSetToCreated(dupObj, false, excludedProperties);
  }
  
  protected void duplication_CopyPropertiesAndSetToCreated(FocObject dupObj, boolean excludeLists, int[] excludedProperties){
  	if(dupObj != null){
	    dupObj.setCreated(true);
	    dupObj.copyPropertiesFrom(this, excludeLists, excludedProperties);
  	}
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.foc.access.AccessSubject#generalFocActionPerformed(b01.foc.event.FocEvent)
   */
  public void focActionPerformed(FocEvent evt) {
    if (evt != null && evt.getSourceType() == FocEvent.TYPE_ORDER) {
      switch (evt.getID()) {
      case FocEvent.ID_RESTORE:
        // this.restore();
        break;
      }
    }
  }
  
  public StringBuffer getDebugInfo(){
    StringBuffer str = new StringBuffer();
    FocDesc focDesc = getThisFocDesc();
    if(focDesc != null){
      str.append(focDesc.getStorageName());
      str.append("\n");
      FocFieldEnum iter = new FocFieldEnum(focDesc , this, FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
      while(iter != null && iter.hasNext()){
        FField field = (FField) iter.next();
        FProperty prop = iter.getProperty();
        if(field != null && prop != null){
          str.append(field.getName());
          str.append(";");        
          str.append(field.getTitle());        
          str.append(";");        
          str.append(prop.getString());
          str.append(";");        
          str.append(prop);
          str.append("\n");          
        }
      }
      iter = new FocFieldEnum(focDesc , this, FocFieldEnum.CAT_LIST, FocFieldEnum.LEVEL_PLAIN);
      while(iter != null && iter.hasNext()){
        FField field = (FField) iter.next();
        FProperty prop = iter.getProperty();
        if(field != null && prop != null){
          FList listProp = (FList) prop;
          
          str.append(field.getName());
          str.append(";");        
          str.append(field.getTitle());        
          str.append(";");        
          str.append(listProp.getList().size());
          str.append("\n");          
        }
      }
    }
    
    return str;
  }
  
  public Component getGuiComponent(int fieldID){
    FProperty prop = getFocProperty(fieldID);
    return prop != null ? prop.getGuiComponent() : null;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // COMPARE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public int compareUniqueKey(FocObject focObj2){
    int compare = -1;
    FocFieldEnum enumer = getThisFocDesc().getFocFieldEnum_KeyPlain();
    enumer.reset();
    if(enumer.hasNext()){
      compare = 0;
      while (enumer.hasNext() && compare == 0) {
        FField fld = (FField) enumer.next();
        if(fld != null){
	        FProperty thisProp = getFocProperty(fld.getID());
	        FProperty otherProp = focObj2.getFocProperty(fld.getID());
	        if (thisProp != null && otherProp != null) {
	          compare = thisProp.compareTo(otherProp);
	        }
        }
      }
    }
    return compare;
  }
  
  public int compareTo(Object obj) {
    FocObject focObj2 = (FocObject) obj;
    FocDesc focDesc = getThisFocDesc();
    FocDesc focDesc2 = null;
    int compare = 0;

    if (obj == null) compare = 1;
    
    if (compare == 0) {
      focDesc2 = focObj2.getThisFocDesc();
      if (focDesc2 == null) compare = 0;
      
      else if (!focDesc.equals(focDesc2)) {
      	if(focDesc2.getStorageName() != null && focDesc.getStorageName() != null){
      		compare = focDesc2.getStorageName().compareTo(focDesc.getStorageName());
      	}else{
      		compare = 1;
      	}
      }
    }

    boolean compDone = false;
    if (compare == 0) {
      FProperty idProp = this.getIdentifierProperty();
      FProperty idProp2 = focObj2.getIdentifierProperty();
      FField field = focDesc.getIdentifierField();
      if(idProp != null && idProp2 != null && field != null){
        if(field.getID() != FField.REF_FIELD_ID || idProp.getInteger() == 0 || idProp2.getInteger() == 0){

        }else{
          compare = idProp.compareTo(idProp2);
          compDone = true;
        }
      }
    }

    if (compare == 0 && !compDone) {
      compare = compareUniqueKey(focObj2);
      /*
      FocFieldEnum enumer = newFocFieldEnum(FocFieldEnum.CAT_KEY, FocFieldEnum.LEVEL_PLAIN);
      while (enumer.hasNext() && compare == 0) {
        FField fld = (FField) enumer.next();
        FProperty thisProp = enumer.getProperty();
        FProperty otherProp = (fld != null) ? focObj2.getFocProperty(fld.getID()) : null;
        if (thisProp != null && otherProp != null) {
          compare = thisProp.compareTo(otherProp);
        }
      }
      */
    }

    return compare;
  }

	public FocObject clone(){
		FocConstructor constr = new FocConstructor(getThisFocDesc(), null);
		FocObject newObj = constr.newItem();
		newObj.copyPropertiesFrom(this);
		return newObj;
	}
  
	public boolean equalsRef(FocObject obj){
		return obj != null && getReference().getInteger() == obj.getReference().getInteger();
	}
	
  //B-HASHCODE  
  /*
  public boolean equals(Object obj) {
    return (compareTo((FocObject) obj) == 0);
  }
  */
  //E-HASHCODE  
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // CONCURRENT ACCESS
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public boolean updateLockFields(){
  	int fieldsArray[] = new int[1];
	  fieldsArray[0] = FField.LOCK_USER_FIELD_ID;
  	return Globals.getApp().getDataSource().focObject_Save(this, fieldsArray);
    //return dbUpdate(true);
  }
  
  private void lock(){
    FObject lockUserProp = (FObject) getFocProperty(FField.LOCK_USER_FIELD_ID);
    lockUserProp.setObject(Globals.getApp().getUser());
    updateLockFields();    
    Globals.getDBManager().addLockedObject(this);
  }

  public void unlock(){
    FObject lockUserProp = (FObject) getFocProperty(FField.LOCK_USER_FIELD_ID);
    if(lockUserProp != null){
      lockUserProp.setObject(null);    
    }
    updateLockFields();
    Globals.getDBManager().removeLockedObject(this);
  }

  public boolean isLocked(boolean popupMessage){
  	return isLocked(popupMessage, null);
  }
  
  public boolean isLocked(boolean popupMessage, String objectDescription){    
    boolean isLocked = false;
    FocDesc desc = getThisFocDesc();
    if(desc.isConcurrenceLockEnabled()){
      FObject lockUserProp = (FObject) getFocProperty(FField.LOCK_USER_FIELD_ID);
      lockUserProp.setObject(null);
      load();
      FocUser lockUser = (FocUser) lockUserProp.getObject_CreateIfNeeded();
      isLocked = lockUser != null && lockUser.getReference().getInteger() > 0;
      
      if(isLocked && popupMessage){
      	if(lockUser.getReference().getInteger() == Globals.getApp().getUser().getReference().getInteger()){
      		String opts[] = {"Unlock", "Keep Locked"};
      		int dialogRet = JOptionPane.NO_OPTION;
          dialogRet = JOptionPane.showOptionDialog(Globals.getDisplayManager().getMainFrame(), 
          		"Object locked by yourself '"+lockUser.getName()+"' do you wish to:\n  1- UNLOCK the object and get full access\n  2- KEEP LOCKED and get read only access\n\nNB: If you select UNLOCK, make sure you don't have the object opened in another window.\n    otherwise you will create access conflict.",
          		objectDescription != null ? "Locked object : "+objectDescription : "Locked object", 
          		JOptionPane.YES_NO_OPTION,
          		JOptionPane.WARNING_MESSAGE,
          		null,
          		opts, "Keep Locked");
          isLocked = dialogRet == JOptionPane.NO_OPTION;                   
      	}else{
          Globals.getDisplayManager().popupMessage("Object locked by user "+lockUser.getName()+"\nYour access will be in read only mode.");          
      	}
      }
      
      if(isLocked){
        setLockedByConcurrence(true);
      }else{
        setLockedByConcurrence(false);
        lock();
      }
    } 
    return isLocked;
  }
  
  public boolean isLockedByConcurrence() {
  	return (flags & FLG_LOCKED_BY_CONCURRENCE) != 0;
  }
  
  public void setLockedByConcurrence(boolean lockedByConcurrence) {
    if(lockedByConcurrence){
      flags = (char)(flags | FLG_LOCKED_BY_CONCURRENCE);
    }else{
      flags = (char)(flags & ~FLG_LOCKED_BY_CONCURRENCE);
    }
  }
  
  public int getLockStatus(){
    int res = LOCK_STATUS_NOT_LOCKED;
    FObject lockUserProp = (FObject) getFocProperty(FField.LOCK_USER_FIELD_ID);
    lockUserProp.setObject(null);
    load();
    FocUser lockUser = (FocUser) lockUserProp.getObject_CreateIfNeeded();
    if(lockUser != null){
      if(Globals.getApp().getUser().getName().equals(lockUser.getName())){
        res = LOCK_STATUS_LOCKED_BY_CURRENT_USER;
      }else{
        res = LOCK_STATUS_LOCKED_BY_OTHER_USER;
      }
    }
    return res;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // ACCESS
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public void setFatherSubject(AccessSubject fatherSubject) {
    super.setFatherSubject(fatherSubject);
    /*
    if(getThisFocDesc().isTreeDesc() && fatherSubject instanceof FocList){
      FObject prop = (FObject) getFocProperty(getThisFocDesc().getFObjectTreeFatherNodeID());
      if(prop != null){
        prop.setLocalSourceList((FocList) fatherSubject);
      }
    }
    */
  }

  public void adjustPropertyLocksAccordingToCreationFlag(){
	  boolean lockNamesValues = true;
	  if(isCreated()){
	    lockNamesValues = false;
	  }else{
	    FocGroup focGroup = Globals.getApp().getGroup();
	    boolean groupAllowNamingModif = focGroup != null && focGroup.allowNamingModif();         
	    lockNamesValues = !groupAllowNamingModif;
	  }
	  
	  if(lockNamesValues){
	    Iterator iter = this.getThisFocDesc().newFocFieldEnum(FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
	    while(iter != null && iter.hasNext()){
	      FField field = (FField) iter.next();
	      if(field != null){
          if(field instanceof FObjectField121){
          	FocObject object121 = (FocObject) getPropertyObject(field.getID());
          	if(object121 != null) object121.adjustPropertyLocksAccordingToCreationFlag();
          }
		      if(field.isLockValueAfterCreation() && Globals.getApp().getGroup() != null && !Globals.getApp().getGroup().allowNamingModif()){
		        FProperty thisProp = this.getFocProperty(field.getID());
		        if(thisProp != null){
		          thisProp.setValueLocked(lockNamesValues);
		        }
		      }
	      }
	    }
	  }
  }
  
  /* (non-Javadoc)
   * @see com.foc.access.AccessSubject#statusModification(int)
   */
  protected void statusModification(int statusModified) {
    if(statusModified == STATUS_CREATED){
    	adjustPropertyLocksAccordingToCreationFlag();
//    	if(isDbResident() && getThisFocDesc() != null && getThisFocDesc().isDbResident() && !(getThisFocDesc() instanceof FocJoinDesc)){
	    	if(isCreated() && workflow_IsWorkflowSubject()){
	    	  IWorkflow workflow = (IWorkflow) this;
	    	  UserSession userSession = UserSession.getInstanceForThread();
	    	  if(workflow.iWorkflow_getWorkflow() != null && userSession != null){ 
	    	    workflow.iWorkflow_getWorkflow().setArea(userSession.getSite());
	    	  }
	    	}

//    	}
    }
  }

  public void code_resetIfApplicableAndCreated(){
		if(isCreated()){
		  FField codeField = getThisFocDesc() != null ? getThisFocDesc().getFieldByID(FField.FLD_CODE) : null;
		  if(codeField != null && codeField.isDBResident()){
		  	code_resetIfCreated();
		  }
		}
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.foc.access.AccessSubject#childFocActionPerformed(b01.foc.event.FocEvent)
   */
  public void childStatusModification(AccessSubject child) {
    if(child.isModified()){
      FocDesc desc = getThisFocDesc();
      for(int i=0; i<desc.getFieldsSize(); i++){
        FField field = desc.getFieldAt(i);
        if(FInLineObjectField.class.isInstance(field)){
          FInLineObject obj = (FInLineObject) getFocProperty(field.getID());
          FocObject inLineFocObj = (FocObject) obj.getObject();
          if(inLineFocObj == child){
            setModified(true);
          }
        }
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.foc.access.AccessSubject#childStatusUndo(b01.foc.access.AccessSubject)
   */
  public void childStatusUndo(AccessSubject childSubject) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.foc.access.AccessSubject#childValidated(b01.foc.access.AccessSubject)
   */
  public void childValidated(AccessSubject childSubject, char initialStatusFlags) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.foc.access.AccessSubject#executeFocAction(b01.foc.event.FocEvent)
   */
  
  public boolean commitStatusToDatabase(){
  	boolean error = false;
    if(isDbResident()){
      if(this.isDeleted()){
        this.delete();
      }else if (this.isCreated() || this.isModified()) {
        if(getThisFocDesc().getRightsByLevelMode() != FocDesc.RIGHTS_BY_LEVEL_MODE_NONE){
          FObject userProp = (FObject)getFocProperty(FField.RIGHT_LEVEL_USER_FIELD_ID);
          userProp.setObject(Globals.getApp().getUser());
          FInt rightsLevelProp = (FInt)getFocProperty(FField.RIGHT_LEVEL_FIELD_ID);
          rightsLevelProp.setInteger(Globals.getApp().getUser().getRightsLevel());
          FString dateTimeProp = (FString)getFocProperty(FField.RIGHT_LEVEL_DATETIME_FIELD_ID);
          
          Date timeStampAsTime = Globals.getDBManager().getCurrentTimeStamp_AsTime();
          SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
          String timeStamp = dateFormat.format(timeStampAsTime);

          dateTimeProp.setString(timeStamp);
        }
        
        FocFieldEnum fEnum = newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_PLAIN);
        while(fEnum != null && fEnum.hasNext()){
        	fEnum.nextField();
        	FProperty prop = fEnum.getProperty();
          if( prop != null && prop instanceof FObject){
            FocObject obj = (FocObject)prop.getObject();
            if( obj != null && obj.isCreated() && !obj.isEmpty()){
            	error = error || !obj.validate(true);//commitStatusToDatabaseWithPropagation(); VALIDATE_VALIDATE_VALIDATE
            	if(error){
            		int debug = 3;
            		debug++;
            	}
            }
          }
        }
             
        this.save();
      }
    }
    return error;
  }
  
  public boolean commitStatusToDatabaseWithPropagation() {
  	boolean error = false;
  	if(!isCreated() || !isEmpty()){
  		//Start saving the requests
//			if(workflow_IsLoggable()){
//				Loggable loggable = ((ILoggable) this).iWorkflow_getWorkflow();
//				if(loggable != null) {
//					ThreadLocal 
//				}
//			}
  		
	  	error = super.commitStatusToDatabaseWithPropagation();
	  	fireEvent(FocEvent.ID_SAVE_AFTER_PROPAGATION);
	  	
  		//Save them in the SQL field in the Database
  	}
  	return error;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.foc.access.AccessSubject#undoStatus()
   */
  public void undoStatus() {
    if (this.isModified()) {
      this.restore();
    }else if(this.isCreated() && hasRealReference()){
      //In this case we have maybe saved info related to this object
      //even though we did not validate the object itself
      //Example: We are creating an item, we entered into sub screens and saved some slaves
      //         then we decided not to save the main item.
      this.delete();
    }
    this.resetStatus();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.foc.access.AccessSubject#doBackup()
   */  
  public void doBackup(){
    this.backup();
  }  
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LISTENERS
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public void addListener(FocListener listener) {
    if (listeners == null) {
      listeners = new ArrayList<FocListener>();
    }
    listeners.add(listener);
  }

  public void removeListener(FocListener listener) {
    if (listeners != null) {
      listeners.remove(listener);
    }
  }

  public void fireEvent(int id) {
    if (listeners != null) {
      FocEvent focEvt = new FocEvent(this, FocEvent.composeId(FocEvent.TYPE_OBJECT, id), "");
      for (int i = 0; i < listeners.size(); i++) {
        FocListener listener = (FocListener) listeners.get(i);
        if (listener != null) {
          listener.focActionPerformed(focEvt);
        }
      }
    }
  }  

  public void scanPropertiesAndNotifyListeners(FocFieldEnum enumer){
    while (enumer.hasNext()) {
      FField fld = (FField) enumer.next();
      if(fld.getID() != FField.MASTER_REF_FIELD_ID){
        FProperty prop = enumer.getProperty(this);
        if(prop != null){
          prop.notifyListeners();
        }
      }
    }
  }
  
  public void scanPropertiesAndNotifyListeners(){
    FocFieldEnum enumer = newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_DB);
    scanPropertiesAndNotifyListeners(enumer);
  }
  
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // ORDER
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public FInt getOrderProperty() {
    FInt order = null;
    FocDesc focDesc = getThisFocDesc();
    if (focDesc.hasOrderField()) {
      order = (FInt) this.getFocProperty(FField.FLD_ORDER);
    }
    return order;
  }

  public int getOrder(){
  	int oInt = -1;
    FInt order = getOrderProperty();
    if(order != null){      
    	oInt = order.getInteger();
    }
    return oInt;
  }

  public void setOrder(int orderVal){
    FInt order = getOrderProperty();
    if (order != null) {      
      order.setInteger(orderVal);
    }
  }

  public int getOrderAutomatic(){
  	int orderVal = 0;
    FInt order = getOrderProperty();
    if (order != null) {      
    	orderVal = order.getInteger();
    }
    return orderVal;
  }

  public boolean isDuringLoad() {
    return (flags & FLG_DURING_LOAD) != 0;
  }

  public void setDuringLoad(boolean isDuringLoad) {
    if(isDuringLoad){
      flags = (char)(flags | FLG_DURING_LOAD);
    }else{
      flags = (char)(flags & ~FLG_DURING_LOAD);
    }
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // REFERENCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public boolean isTempReference() {
    return (flags & FLG_IS_TEMP_REFERENCE) != 0;
  }

  public void setTempReference(boolean isTempReference) {
    if(isTempReference){
      flags = (char)(flags | FLG_IS_TEMP_REFERENCE);
    }else{
      flags = (char)(flags & ~FLG_IS_TEMP_REFERENCE);
    }
  }

  public long getReferenceInt(){
  	FReference ref = getReference();
  	return ref != null ? ref.getLong() : 0;
  }
  
  public FReference getReference() {
  	if(referenceProperty == null){
	    FocDesc focDesc = getThisFocDesc();
	    referenceProperty = (FReference) this.getFocProperty(FField.REF_FIELD_ID);
	    if(referenceProperty == null && focDesc.getWithReference()){
	    	referenceProperty = new FReference(this);
	    }
  	}
    return referenceProperty;
  }
  
  public void setReference_WithFocListRefHashMapAdjustment(long ref){
		//BAntoine - We need to remove from the FocList realReference MAP the temp ref and put the real one.
		FocList focList = null;
		Object fatherSubject = getFatherSubject();
		if(fatherSubject != null && fatherSubject instanceof FocList){
			focList = (FocList) fatherSubject;
			focList.elementHash_RemoveFromReferencesMap(this);
		}
		//EAntoine
		
		setReference(ref);
		
		//BAntoine - We need to remove from the FocList realReference MAP the temp ref and put the real one.
		if(focList != null){
			focList.elementHash_AddToReferencesMap(this);
		}
	  //EAntoine
  }
  
  public void setReference(long ref){
    FReference refProp = getReference();

    if (refProp != null) {
    	AccessSubject accSubj = getFatherSubject();
    	if(accSubj != null && accSubj instanceof FocList){
    		((FocList)accSubj).fireItemReferenceModification(this, ref);
    	}
    	
      refProp.setLong(ref);
    }
    
    setTempReference(false);
  }

  public boolean referenceNeeded() {
    FocDesc focDesc = this.getThisFocDesc();
    return focDesc != null && focDesc.getWithReference() && getReference().getLong() == 0;
  }

  public boolean setTemporaryReferenceIfNeeded(long ref) {
    boolean needed = referenceNeeded(); 
    if (needed) {
      setReference(ref);
      setTempReference(true);
    }
    return needed;
  }

  //BAntoineS - AUTOINCREMENT
  public boolean needsAssignReference(){
    FocDesc focDesc = this.getThisFocDesc();    
    return (referenceNeeded() || isTempReference()) && focDesc.isPersistent();     
  }
  //EAntoineS - AUTOINCREMENT
  
  public boolean hasRealReference(){
    boolean hasRealRef = false;
    FocDesc focDesc = this.getThisFocDesc();
    if(focDesc != null){
      hasRealRef = focDesc.getWithReference() && getReference().getInteger() > 0 && !isTempReference();
    }
    return hasRealRef;
  }
  
  public FProperty getIdentifierProperty() {
    FProperty property = null;
    FocDesc focDesc = this.getThisFocDesc();
    if (focDesc != null) {
      FField refField = focDesc.getIdentifierField();
      if (refField != null) {
        property = this.getFocProperty(refField.getID());
      }
    }
    if(property == null){
      //Globals.logException(new Exception("FocObject has no identifier property!"));
    }
    return property;
  }
  
  public FProperty initIdentifierProperty(Object identifierObj) {
    FocDesc focDesc = getThisFocDesc();
    FReference ref = (FReference) this.getFocProperty(FField.REF_FIELD_ID);
    if (focDesc != null && ref == null && focDesc.getWithReference()) {
      FocRef focRef = identifierObj != null ? (FocRef) identifierObj : null;
      //FocRef focRef = new FocRef(intVal.intValue());
      ref = new FReference(this, FField.REF_FIELD_ID, focRef != null ? (FocRef)focRef.clone() : null);
    }
    return ref; 
  }
  
  public void loadReferenceFromDatabaseAccordingToKey(){
  	Globals.getApp().getDataSource().focObject_GetReference_ForUniqueKey(this);
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DATABASE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public int referenceCheck_GetNumber(StringBuffer message){
  	return referenceCheck_GetNumber(message, false);
  }
  
  public int referenceCheck_GetNumber(StringBuffer message, boolean popupMessage){
  	return referenceCheck_GetNumber(message, popupMessage, null, null, null);
  }
  
  public int referenceCheck_GetNumber(StringBuffer message, boolean popupMessage, ReferenceChecker referenceCjeckerToIgnore, ArrayList<ReferenceCheckerToPutToZero> arrayPutToZero, ArrayList<ReferenceCheckerDelete> arrayDelete){
  	int nbOfReferences = Globals.getApp().getDataSource().focObject_GetNumberOfReferences(this, message, referenceCjeckerToIgnore, arrayPutToZero, arrayDelete);
  	/*
    int nbOfReferences = 0;
    FocDesc focDesc = getThisFocDesc();
    
    if(hasRealReference()){
	    Iterator iter = focDesc.referenceLocationIterator();
	    while(iter != null && iter.hasNext()){
	      ReferenceChecker refCheck = (ReferenceChecker)iter.next();
	      if(refCheck != null && (referenceCjeckerToIgnore == null || refCheck != referenceCjeckerToIgnore)){
	      	if(refCheck.isPutToZeroWhenReferenceDeleted()){
	      		if(arrayPutToZero != null){
		      		ReferenceCheckerToPutToZero toPutToZero = new ReferenceCheckerToPutToZero(refCheck, this);
		      		arrayPutToZero.add(toPutToZero);
	      		}
	      	}else{
	      		nbOfReferences += refCheck.getNumberOfReferences(this, message);
	      	}
	      }
	    }
    }
  	*/
    if(popupMessage && nbOfReferences > 0 && message != null){
      referenceCheck_PopupDialog(nbOfReferences, message);
    }
    
    return nbOfReferences;
  }

  public String referenceCheck_CompleteTheMessage(int nbOfReferences, StringBuffer message){
  	StringBuffer newBuffer = new StringBuffer("Object ");
  	FocDesc focDesc = getThisFocDesc();
  	newBuffer.append(focDesc.getStorageName());
  	newBuffer.append(":");
  	newBuffer.append(getIdentifierProperty().getString());
  	newBuffer.append(" is referenced ");
  	newBuffer.append(nbOfReferences);
  	newBuffer.append(" times.\nPlease call 01Barmaja for further assistance on deleting this object.\nReferenced in:");
  	newBuffer.append(message);
  	return newBuffer.toString();
  }
  
  public void referenceCheck_PopupDialog(int nbOfReferences, StringBuffer message){
    if(nbOfReferences > 0){
    	String completeMessage = referenceCheck_CompleteTheMessage(nbOfReferences, message);
    	if(Globals.getDisplayManager() != null){
	      JOptionPane.showConfirmDialog(Globals.getDisplayManager().getMainFrame(),
	      		completeMessage,
	          "01Barmaja",
	          JOptionPane.DEFAULT_OPTION,
	          JOptionPane.INFORMATION_MESSAGE,
	          null);
    	}else{
    	  Globals.showNotification(this.getThisFocDesc().getStorageName(), "Can not delete " + message + ".", IFocEnvironment.TYPE_ERROR_MESSAGE);
    		Globals.logString(completeMessage);
    	}
    }
  }
  
  public void referenceCheck_RedirectToNewFocObject(FocObject focObjectToRedirectTo){
  	Globals.getApp().getDataSource().focObject_Redirect(this, focObjectToRedirectTo);
  	/*
  	Iterator iter = getThisFocDesc().referenceLocationIterator();
    while(iter != null && iter.hasNext()){
      ReferenceChecker refCheck = (ReferenceChecker)iter.next();
      if(refCheck != null){
      	refCheck.redirectReferencesToNewFocObject(this, focObjectToRedirectTo);
      }
    }
    */
  }
  
  public boolean workflow_IsWorkflowSubject(){
  	return getThisFocDesc() != null && getThisFocDesc().workflow_IsWorkflowSubject();
  }
  
  public boolean workflow_IsLoggable(){
  	return getThisFocDesc() != null && getThisFocDesc().workflow_IsLoggable();
  }
  
  public WFSite workflow_GetSite(){
  	WFSite site = null;
  	if(this instanceof IWorkflow && this instanceof IStatusHolder){
  		IWorkflow     workflow     = (IWorkflow) this;
  		if(workflow != null && workflow.iWorkflow_getWorkflow() != null){
  			site = workflow.iWorkflow_getWorkflow().getArea();
  		}
  	}
  	return site;
  }

    
  //IFocDataSource
  /*
  public boolean canDelete(ReferenceChecker referenceCjeckerToIgnore){
  	boolean can = false;
  	if(Globals.getDBManager() != null && isDeletable() && sync_AllowObjectDBModification()){
	    FocDesc focDesc = getThisFocDesc();
	    if(focDesc != null){
		    if(!focDesc.getWithReference() || hasRealReference()){
		      StringBuffer message = new StringBuffer();
		      can = referenceCheck_GetNumber(message, true, referenceCjeckerToIgnore) == 0;
		    }else{
		    	can = true;
		    }
		    if(can){
		    	FocFieldEnum fieldsIterator = focDesc.newFocFieldEnum(FocFieldEnum.CAT_LIST, FocFieldEnum.LEVEL_PLAIN);
		      while(fieldsIterator.hasNext() && can) {
		        FListField focField = (FListField) fieldsIterator.next();
		  
		        if(focField.isDBResident() && focField.isDeleteListWhenMasterDeleted()){
		          FFieldPath fieldPath       = fieldsIterator.getFieldPath();
		          FList      list            = (FList) fieldPath.getPropertyFromObject(this);
		          FocList    focListToDelete = list.getList();
		          can = focListToDelete.canDelete();
		        }
		      }
		    }
	    }
  	}
    return can;
  }
  */
  
  public boolean dbDelete(ReferenceChecker refCheck) {
    boolean successfull = !Globals.getApp().getDataSource().focObject_Delete(this, refCheck);
    return successfull;
    
    //IFocDataSource
    /*
    if(canDelete(refCheck)){
    	if(!getThisFocDesc().getWithReference() || !hasRealReference()){
    		successfull = true;
    	}else{
	      SQLFilter filter    = new SQLFilter(FocObject.this, SQLFilter.FILTER_ON_IDENTIFIER);
	      SQLDelete sqlDelete = new SQLDelete(getThisFocDesc(), filter);
	      try{
	      	sqlDelete.execute();
	      	successfull = true;            	
	      }catch(Exception e){
	      	Globals.logException(e);
	      	successfull = false;
	      }
    	}
    }

    if(successfull){
      setDeletionExecuted(true);
    }

		return successfull;
		*/
  }
  
  protected boolean dbSelect() {
    boolean error = true;
    
    if(this.getThisFocDesc().isPersistent()){
    	//error = Globals.getApp().getDataSource().focObject_Load(this);
    	Globals.getApp().getDataSource().focObject_Load(this);    	
    	
    	//IFocDataSource
      /*
    	setLoadedFromDB(true);
      DBManager dbManager = Globals.getDBManager();
      if (dbManager != null) {
        SQLFilter filter = new SQLFilter(this, SQLFilter.FILTER_ON_IDENTIFIER);
        filter.setFilterByCompany(!isShared());

        FocDesc focDesc = getThisFocDesc();
        if (focDesc != null) {
          SQLSelect sqlSelect = new SQLSelect(this, focDesc, filter);
          if(sqlSelect.execute()){
            setLoadedFromDB(false);
            error = false;
          }
        }
      }
      
      if(Globals.getApp().getRightsByLevel() != null){
        Globals.getApp().getRightsByLevel().lockValuesIfNecessary(this);
      }
      setModified(false);
      */
    }
    return error;
  }

  public void save() {
  	save(null);
  }

  public boolean isShowMessageWhenCodeChange() {
    return showMessageWhenCodeChange;
  }
  
  public void setShowMessageWhenCodeChange(boolean show) {
    showMessageWhenCodeChange = show;
  }
  
  public boolean code_NotifyWhenCodeTaken() {
  	return true;
  }

  public void code_ChangingCodeBecauseTaken(String previousCode, String newCode) {
  }

  public void save(int queryFields[]) {
  	Globals.setMouseComputing(true);
    if (isCreated()) {
    	if(code_checkIfTaken()){
    		String originalCode = code_getCode();
    		code_resetCode();
    		String newCode = code_getCode();
    		code_ChangingCodeBecauseTaken(originalCode, newCode);
    		if(!originalCode.equals(newCode) && code_NotifyWhenCodeTaken() && isShowMessageWhenCodeChange()){
    			String message = "A new code has been assigned : " + newCode + " because the previous one was taken";
    			String title = "Code already taken";
    			if (ConfigInfo.isArabic()) {
        		message = "إن الرقم المعطى لهذا البيان قد أخذ, فأعطي هذا البيان رقماً جديداً : " + newCode;
        		title = "رقم تسجيل البيان تبدل";
    			} 
    			Globals.showNotification(title, message, IFocEnvironment.TYPE_HUMANIZED_MESSAGE);
    		}
    	}
    	if(this instanceof IStatusHolder){
    		StatusHolder statusHolder = ((IStatusHolder) this).getStatusHolder();
    		if(statusHolder != null){
    			statusHolder.fillCreationInfo();
    		}
    	}
    }
    
    Globals.getApp().getDataSource().focObject_Save(this, queryFields);
    
    //B-DUP
    setLoadedFromDB(true);
    //E-DUP
    fireEvent(FocEvent.ID_SAVE);
    Globals.setMouseComputing(false);
  }
  
  public boolean delete() {
  	return delete(null);
  }
  
  public boolean delete(ReferenceChecker referenceCjeckerToIgnore) {
  	boolean succcess = true;
  	
  	if(getThisFocDesc().isJoin()) {
  		//In this case we should not try to delete
  	} else {
	    succcess = !Globals.getApp().getDataSource().focObject_Delete(this, referenceCjeckerToIgnore);
	    
	    ArrayList<Integer> cloudStorageFields = getThisFocDesc().getCloudStorageFields(false);
	    
	    if(cloudStorageFields != null){
	    	for(int i=0; i<cloudStorageFields.size(); i++){
	    		FCloudStorageProperty csProp = (FCloudStorageProperty) getFocProperty(cloudStorageFields.get(i));
	    		
	    		if(csProp != null){
	    			csProp.deleteObject();
	    		}
	    	}
	    }
  	}
    return succcess;
  }

  public boolean load() {
    boolean error = true;
    if(!isCreated() && !isDuringLoad()){
    	Globals.setMouseComputing(true);
      setDuringLoad(true);
      fireEvent(FocEvent.ID_BEFORE_LOAD);
      error = dbSelect();
      fireEvent(FocEvent.ID_LOAD);
      setDuringLoad(false);
    	Globals.setMouseComputing(false);
    }
    return error;
  }
  
  public void reloadWithSlaveLists() {
		load();
		FocFieldEnum enumer = newFocFieldEnum(FocFieldEnum.CAT_LIST, FocFieldEnum.LEVEL_PLAIN);
		if(enumer != null) {
			while(enumer.hasNext()) {
				FField fld = enumer.nextField();
				FProperty prop = enumer.getProperty();
				if (prop != null && prop instanceof FList) {
					FocList list = ((FList) prop).getList();
					list.reloadFromDB();
				}
			}
		}
  }

  private void backupRestore(boolean backup) {
    //FocDesc focDesc = getThisFocDesc();
    if (backup || !isCreated()) {
    	getThisFocDesc().backupRestoreForFocObject(this, backup);
    	/*
      Iterator iter = focDesc.newFocFieldEnum(FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
      while(iter != null && iter.hasNext()){
        FField focField = (FField) iter.next();
        if(focField != null && !focField.isReflectingField()){
          FProperty prop = this.getFocProperty(focField.getID());
          if(prop != null){
            if (backup) {
              prop.backup();
            } else {
              prop.restore();
            }
          } 
        }
      }
      */
    }
  }

  public void restore() {
    backupRestore(false);
    fireEvent(FocEvent.ID_RESTORE);
  }

  public void backup() {
    backupRestore(true);
    fireEvent(FocEvent.ID_BACKUP);
  }

  public boolean isLoadedFromDB() {
  	return (flags & FLG_LOADED_FROM_DB) != 0;  	
  }

  public void setLoadedFromDB(boolean b) {
    if(b){
      flags = (char)(flags | FLG_LOADED_FROM_DB);
    }else{
      flags = (char)(flags & ~FLG_LOADED_FROM_DB);
    }
  	
    //loadedFromDB = b;
    /*
    if(b){
      this.backup();
    }
    */
  }
  
  public void duplicationModification(FocObject source){
  	FDate dateProperty = (FDate) getFocProperty(FField.FLD_DATE);
    if(dateProperty != null){
    	dateProperty.setDate(Globals.getApp().getSystemDate());
    }
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // XML
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public void writeXMLFile(OutputStream outStream, FocLinkOutRights rights){
  	writeXMLFile(outStream, false, rights);
  }
  
  public void writeXMLFile(OutputStream outStream, boolean includeReference, FocLinkOutRights rights){
  	try{
  		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
  		DocumentBuilder builder = factory.newDocumentBuilder();
  		Document doc = builder.newDocument();
  		writeXML(doc, null, includeReference, true, rights);
  		Transformer t = TransformerFactory.newInstance().newTransformer();
  		//      t.setOutputProperty(OutputKeys.INDENT, "yes");
  		t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
  		t.setOutputProperty("encoding", "UTF-8");

  		t.transform(new DOMSource(doc), new StreamResult(outStream));
  	}catch(Exception e){
  		Globals.logException(e);
  	}
  }
  

  public Element writeXML_MainNode(Document doc, Element root, String nodeName, boolean includeReference){
    Element mainNode = doc.createElement(nodeName);
    if(includeReference){
    	mainNode.setAttribute("ref", getReference().toString());
    }
    if(root == null){
      doc.appendChild(mainNode);
    }else{
      root.appendChild(mainNode);        
    }
    return mainNode;
  }
  
  public Element writeXML_MainNode(Document doc, Element root, boolean includeReference){
    return writeXML_MainNode(doc, root, getThisFocDesc().getStorageName(), includeReference);
  }

  public boolean writeXML_DoPrintListItem(FList listProp, FocObject object){
    return true;
  }
  
  public void writeXML_ForProperty(Document doc, Element father, FField field, FProperty property){
    Element elmt = doc.createElement(field.getName());
    father.appendChild(elmt);
    
    if(property != null){
      String value = property.getXMLValue() != null ? (String)property.getXMLValue() : "";
      //At MCC the received XML using the FocLink does not need to convert the special characters
//      String newValue = value.replace("&" , "&amp;");
//      value = newValue;
//      value = value.replace("<" , "&lt;");
//      value = value.replace(">" , "&gt;");
//      value = value.replace("\"", "&quot;");
//      value = value.replace("'" , "&#39;");
      Text txt = doc.createTextNode(value);
      elmt.appendChild(txt);
    }
  }

  public void writeXML_ForList(Document doc, Element father, FListField listField, FList listProp, boolean includeReference, boolean includeLists, FocLinkOutRights rights){
    /*FocList focList = listProp.getList();
    if(focList != null && focList.size() > 0){
      Element elmt = doc.createElement(listField.getName());
      father.appendChild(elmt);
      
      for(int i=0; i<focList.size(); i++){
        FocObject focObj = (FocObject) focList.getFocObject(i);
        if(focObj != null && writeXML_DoPrintListItem(listProp, focObj)){
          focObj.writeXML(doc, elmt, includeReference, includeLists);
        }
      }            
    }*/
  	writeXML_ForList(doc, father, listField.getName(), listProp, includeReference, includeLists, rights);
  }
  
  public void writeXML_ForList(Document doc, Element father, String nodeTitle, FList listProp, boolean includeReference, boolean includeLists, FocLinkOutRights rights){
    FocList focList = listProp.getList();
    if(focList != null && focList.size() > 0){
      Element elmt = doc.createElement(nodeTitle);
      father.appendChild(elmt);
      
      for(int i=0; i<focList.size(); i++){
        FocObject focObj = (FocObject) focList.getFocObject(i);
        if(focObj != null && writeXML_DoPrintListItem(listProp, focObj)){
          focObj.writeXML(doc, elmt, includeReference, includeLists, rights);
        }
      }            
    }
  }

  public void writeXML(Document doc, Element root, boolean includeReference, boolean includeLists, FocLinkOutRights rights){
  	FocDesc desc = getThisFocDesc();
  	if(desc != null){
  		Element mainNode = writeXML_MainNode(doc, root, includeReference);

  		FocList focLinkOutRightsDetailsList = null;

  		if(rights != null){
  			focLinkOutRightsDetailsList = rights.getDetailsList();
  		}

  		FocFieldEnum enumer = this.newFocFieldEnum(FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
  		while(enumer != null && enumer.hasNext()){

  			boolean shouldWritePropertyToXML = false;

  			FField field = (FField) enumer.next();        
  			FProperty prop = (FProperty) enumer.getProperty();

  			if(field.getID() != FField.REF_FIELD_ID 
  					&& field.getID() != FField.MASTER_REF_FIELD_ID
  					&& field.getID() != FField.SLAVE_REF_FIELD_ID
  					){

  				if(focLinkOutRightsDetailsList != null){
  					shouldWritePropertyToXML = writeAllowedOnField(field, desc, rights);
  				}
  				else{
  					shouldWritePropertyToXML = true;
  				}
  			}

  			if(shouldWritePropertyToXML){
  				writeXML_ForProperty(doc, mainNode, field, prop);
  			}
  		}

  		if(includeLists){
  			enumer = this.newFocFieldEnum(FocFieldEnum.CAT_LIST, FocFieldEnum.LEVEL_PLAIN);
  			while(enumer != null && enumer.hasNext()){

  				boolean shouldWritePropertyToXML = false;

  				FListField field = (FListField) enumer.next();        
  				FList listProp = (FList) enumer.getProperty();

  				if(focLinkOutRightsDetailsList != null){
  					shouldWritePropertyToXML = writeAllowedOnField(field, desc, rights);
  				}
  				else{
  					shouldWritePropertyToXML = true;
  				}

  				if(shouldWritePropertyToXML){
  					writeXML_ForList(doc, mainNode, field, listProp, includeReference, includeLists, rights);
  				}
  			}
  		}
  	}
  }

  //Looking in the details list to check if we have the rights on a certain field to write to XML
  private boolean writeAllowedOnField(FField field, FocDesc desc, FocLinkOutRights rights){
		return rights.hasRightsForFieldInTableDesc(field, desc);
  }
  
  public void disabelValidationPanelButtonsWithPropagation(){
    disabelValidationPanelButtons();
    FocObject masterObject = (FocObject)getMasterObject();
    if (masterObject != null){
      masterObject.disabelValidationPanelButtonsWithPropagation();
    }else{
      if (browsePanel != null){
        ((FValidationPanel)browsePanel.getValidationPanel()).disabelButtons();
      }
    }
  }
  
  public void disabelValidationPanelButtons(){
    if (detailsPanel != null){
      FValidationPanel validPanel = (FValidationPanel) detailsPanel.getValidationPanel();
      if (validPanel != null){
        validPanel.disabelButtons();
      }
    }
  }
  
  public void enabelValidationPanelButtonsWithPropagation(){
    enabelValidationPanelButtons();
    FocObject masterObject = (FocObject)getMasterObject();
    if (masterObject != null){
      masterObject.enabelValidationPanelButtonsWithPropagation();
    }if (browsePanel != null){
      ((FValidationPanel)browsePanel.getValidationPanel()).enabelButtons();
    }
  }
  
  public void enabelValidationPanelButtons(){
    if (detailsPanel != null){
      FValidationPanel validPanel = (FValidationPanel)detailsPanel.getValidationPanel();
      if (validPanel != null){
        validPanel.enabelButtons();
      }
    }
  }
    
  public void setDesactivatePropertyListeners(boolean mode){
    FocFieldEnum iter = new FocFieldEnum(this.getThisFocDesc(), this, FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
    while(iter != null && iter.hasNext()){
      iter.next();
      FProperty prop = (FProperty) iter.getProperty();
      if(prop != null){
      	prop.setDesactivateListeners(mode);
      }
    }
  }

  public FProperty getFirstCustomizedProperty(int fieldID){
    FProperty prop = getFocProperty(fieldID);
  	if(prop != null && prop.isInherited()){
  		prop = getFirstAncestorCustomizedProperty(fieldID);
  	}
  	return prop ;
  }
  
  public FProperty getFirstAncestorCustomizedProperty(int fieldID){
    FocObject fatherObject = getPropertyObject(getThisFocDesc().getFObjectTreeFatherNodeID());
    FProperty firstAncestorCustomizedProperty = null;
    
    if(getFocProperty(fieldID).getFocField().isWithInheritance()){
      while(fatherObject != null && firstAncestorCustomizedProperty == null){
        
        if(!fatherObject.getFocProperty(fieldID).isInherited()){
          firstAncestorCustomizedProperty = fatherObject.getFocProperty(fieldID);
        }
        
        if(fatherObject == fatherObject.getPropertyObject(getThisFocDesc().getFObjectTreeFatherNodeID())){
          break;
        }
        fatherObject = fatherObject.getPropertyObject(getThisFocDesc().getFObjectTreeFatherNodeID());
      }  
    }
    
    return firstAncestorCustomizedProperty;
  }

  public Object getFirstAncestorTableDisplayObject(int fieldID, Format format){
    Object objDisplay = null;
    FProperty firstAncestorCustomized = getFirstAncestorCustomizedProperty(fieldID);
    if(firstAncestorCustomized != null){
      objDisplay = firstAncestorCustomized.getTableDisplayObject(format);
    }
    return objDisplay;
  }
  
  public FocList getPropertyFormulaList(){
    FocList list = getPropertyList(FField.FLD_PROPERTY_FORMULA_LIST);
    if( list != null ){
      list.setDirectlyEditable(true);  
    }
    return list;  
  }

  public void beforePropertyModified(FProperty property) {
  }

  public void afterPropertyModified(FProperty property) {
  	fireItemPropertySetChange(property);
  }
  
  public FocObject getFatherObject(){
    FocObject fatherObject = null;
    int fatherNodeID = getThisFocDesc().getFObjectTreeFatherNodeID();
    if(fatherNodeID != FField.NO_FIELD_ID){
      fatherObject = getPropertyObject(fatherNodeID);  
    }
    return fatherObject;
  }

  public void setFatherObject(FocObject fatherNode){
    int fatherNodeID = getThisFocDesc().getFObjectTreeFatherNodeID();
    if(fatherNodeID != FField.NO_FIELD_ID){
      setPropertyObject(fatherNodeID, fatherNode);  
    }
  }
  
  public boolean isDeprecated(){
    return getPropertyBoolean(FField.FLD_DEPRECATED_FIELD);
  }
  
  public void setDeprecated(boolean deprecated){
    setPropertyBoolean(FField.FLD_DEPRECATED_FIELD, deprecated);
  }
  
	public boolean isLogicalDeleted() {
		return getPropertyBoolean(FField.FLD_LOGICAL_DELETE);
	}
	
	public void setLogicalDeleted(boolean deleted) {
		boolean alreadyDeleted = isLogicalDeleted();
		setPropertyBoolean(FField.FLD_LOGICAL_DELETE, deleted);
		if(!alreadyDeleted && deleted) {
			Date now = new Date(System.currentTimeMillis());
			setLogicalDeletedDate(now);
			if(Globals.getApp() != null) setLogicalDeletedUser(Globals.getApp().getUser_ForThisSession());
		}
	}
	
	public Date getLogicalDeleteDate() {
		return getPropertyDate(FField.FLD_LOGICAL_DELETE_DATE);
	}
	
	public void setLogicalDeletedDate(Date value) {
		setPropertyDate(FField.FLD_LOGICAL_DELETE_DATE, value);
	}
	
	public FocUser getLogicalDeleteUser() {
		return (FocUser) getPropertyObject(FField.FLD_LOGICAL_DELETE_USER);
	}
	
	public void setLogicalDeletedUser(FocUser user) {
		setPropertyObject(FField.FLD_LOGICAL_DELETE_USER, user);
	}
  
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // CODE SEQUENCIAL
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public String code_getExternalCode(){
  	return getPropertyString(FField.FLD_EXTERNAL_CODE);
  }
  
	public String accVoucherWriter_getTransactionExternalCode(){
		return code_getExternalCode();
	}

  public void code_setExternalCode(String code){
  	setPropertyString(FField.FLD_EXTERNAL_CODE, code);
  }
  
  //Review Fields
  public int getReviewStatus(){
  	return getPropertyInteger(FField.FLD_REVIEWSTATUS);
  }
  
  public String getReviewStatusText(){
  	return getPropertyString(FField.FLD_REVIEWSTATUS);
  }

  public void setReviewStatus(int status){
  	setPropertyInteger(FField.FLD_REVIEWSTATUS, status);
  }
  
  public String getReviewComment(){
  	return getPropertyString(FField.FLD_REVIEWCOMMENT);
  }

  public void setReviewComment(String comment){
  	setPropertyString(FField.FLD_REVIEWCOMMENT, comment);
  }
  //End Review Fields
  
  public String code_getCode(){
  	return getPropertyString(FField.FLD_CODE);
  }
  
  public void code_setCode(String code){
  	setPropertyString(FField.FLD_CODE, code);
  }
  
  public Date getDate(){
  	return getPropertyDate(FField.FLD_DATE);
  }
  
  public void setDate(Date date){
  	setPropertyDate(FField.FLD_DATE, date);
  }

  public String code_getProposalCode(){
  	return getPropertyString(FField.FLD_PROPOSAL_CODE);
  }
  
  public void code_setProposalCode(String proposalCode){
  	setPropertyString(FField.FLD_PROPOSAL_CODE, proposalCode);
  }

	public void code_resetCode(){
		if(getThisFocDesc() != null && getThisFocDesc().getFieldByID(FField.FLD_CODE) != null){
			code_setCode(FField.FLD_CODE, code_getPrefix(), code_getNumberOfDigits());
		}
	}

	public void code_resetIfCreated(){
		if(isCreated()){
			code_resetCode();
		}
	}

	public void code_setCodeIfEmpty(){
		if(code_getCode().isEmpty()){
			code_resetCode();
		}
	}

	public void code_resetCodeIfPrefixHasChanged(){
  	if(this instanceof IWorkflow){
  		code_resetCodeIfPrefixHasChanged_WithoutWorkflowCheck();
  	}
	}

	public boolean code_prefixHasChanged(){
		boolean changed = false;
		if(getThisFocDesc() != null && getThisFocDesc().getFieldByID(FField.FLD_CODE) != null){
			String str = code_getPrefix();
			boolean samePrefix = code_getCode().startsWith(str);
			if(samePrefix){
				String numericalPart = code_getCode().substring(str.length());
				try{
					Integer.valueOf(numericalPart);
				}catch(Exception e){
					samePrefix = false;
				}
			}
			changed = !samePrefix;
		}
		return changed;
	}
	
	public void code_resetCodeIfPrefixHasChanged_WithoutWorkflowCheck(){
		if(code_prefixHasChanged()){
			code_resetCode();
		}
	}
	
  public int code_getNumberOfDigits(){
  	int nbrOfDigits = 6;
  	if(this instanceof IWorkflow && this instanceof IStatusHolder){
  		int plugInDigits = -1;
	  	IFocObjectPlugIn plugIn = getIFocObjectPlugIn();
	  	if(plugIn != null){
	  		plugInDigits = plugIn.getTransactionCodeNumberOfDigits();
	  	}
	  	
	  	if(plugInDigits > 0){
	  		nbrOfDigits = plugInDigits;
	  	}else{
	  		IWorkflow     workflow     = (IWorkflow) this;
	  		if(workflow != null){
	  			WFTransactionConfig assignment = WFTransactionConfigDesc.getTransactionConfig_ForTransaction(workflow);
	  			if(assignment != null){
	  				nbrOfDigits = assignment.codePrefix_getNbrOfDigits();
	  			}
	  		}
	  	}
  	}
  	return nbrOfDigits;
  }
  
  public String code_getPrefix_ForStatus(int status){
  	String codePrefix = "";

		IWorkflow     workflow     = (IWorkflow    ) this;
		IStatusHolder statusHolder = (IStatusHolder) this;
		if(workflow != null && statusHolder != null){
			WFTransactionConfig assignment 	= WFTransactionConfigDesc.getTransactionConfig_ForTransaction(workflow);
			if(status <= StatusHolderDesc.STATUS_PROPOSAL || status == StatusHolderDesc.STATUS_SYSTEM){
				codePrefix = assignment.codePrefix_getTransactionPrefixForProposal();
			}else{
				codePrefix = assignment.codePrefix_getTransactionPrefix();
			}
		}
		
		return codePrefix;
  }
  
  public String code_getPrefix(){
  	String codePrefix = "";
  	  	
  	if(this instanceof IWorkflow && this instanceof IStatusHolder){
  		IWorkflow     workflow     = (IWorkflow) this;
  		IStatusHolder statusHolder = (IStatusHolder) this;
  		if(workflow != null && statusHolder != null && workflow.iWorkflow_getWorkflow() != null){
  			WFSite area = workflow.iWorkflow_getWorkflow().getArea();
  			//if(area == null) area = workflow.iWorkflow_getComputedSite();
  			String areaPrefix        = area != null ? area.getTransactionCodePrefix() : "";
  			String transactionPrefix = "";
  			
  			WFTransactionConfig assignment 	= WFTransactionConfigDesc.getTransactionConfig_ForTransaction(workflow);
  			if(assignment != null){
	  			if(statusHolder.getStatusHolder().getStatus() <= StatusHolderDesc.STATUS_PROPOSAL || statusHolder.getStatusHolder().getStatus() == StatusHolderDesc.STATUS_SYSTEM){
	  				transactionPrefix = assignment.codePrefix_getTransactionPrefixForProposal();
	  			}else{
	  				transactionPrefix = assignment.codePrefix_getTransactionPrefix();
	  			}
  			}
  			
  			codePrefix = null;
  			
  	  	IFocObjectPlugIn plugIn = getIFocObjectPlugIn();
  	  	if(plugIn != null){
  	  		codePrefix = plugIn.getTransactionCodePrefix(areaPrefix, transactionPrefix);
  	  	}
  	  	
  	  	if(codePrefix == null){
  	  		if(assignment != null && !assignment.codePrefix_isUseSitePrefix()) areaPrefix = "";
  	  		codePrefix = areaPrefix + transactionPrefix;
  	  	}

	  		codePrefix = adjustCodePrefix(codePrefix);
	  		if(workflow.iWorkflow_getWorkflow().isSimulation()) codePrefix = "."+codePrefix; 
  		}
  	}
  	return codePrefix;
  }
  
  public String adjustCodePrefix(String codePrefix){
		if(codePrefix != null && (codePrefix.contains("{YY}") || codePrefix.contains("{MM}") || codePrefix.contains("{MMM}"))){  	
	  	Calendar cal = FCalendar.getInstanceOfJavaUtilCalandar();
			if(cal != null){
				Date date = getDate();
				if(date == null) date = Globals.getApp().getSystemDate(); 
				cal.setTime(date);

				if(codePrefix.contains("{YY}")){
					int year = cal.get(Calendar.YEAR);
					String yearStrg = year+"";
					yearStrg = yearStrg.substring(2, yearStrg.length());
					codePrefix = codePrefix.replace("{YY}", yearStrg);
				}
				
				if(codePrefix.contains("{MM}")){
					int month = cal.get(Calendar.MONTH);
					month += 1;
					String monthStrg = month+"";
					if(month < 10) monthStrg = "0"+monthStrg; 
					codePrefix = codePrefix.replace("{MM}", monthStrg);
				}
				
				if(codePrefix.contains("{MMM}")){
					SimpleDateFormat sdf = new SimpleDateFormat("MMM");
					String monthStrg = sdf.format(date);
					codePrefix = codePrefix.replace("{MMM}", monthStrg);
				}
				
				if(codePrefix.contains("{DD}")){
					int day = cal.get(Calendar.DAY_OF_MONTH);
					String dayStrg = day+"";
					if(day < 10) dayStrg = "0"+dayStrg; 
					codePrefix = codePrefix.replace("{DD}", dayStrg);
				}
			}
		}
  	return codePrefix;
  }
  
  public boolean code_isResetDigitsWhenPrefixChanges(){
  	return true;
  }

  public String code_getSeperator(){
  	return null;
  }
  
  public String code_refreshCodeIfAutomatic(int fieldID, String prefix, int nbrOfCodeDigits){
  	String code = getPropertyString(fieldID);
  	boolean doRefresh = code == null || code.isEmpty();
  	if(!doRefresh){
  		doRefresh = code.startsWith(prefix);
  		if(doRefresh){
  			String sub = code.substring(prefix.length(), code.length());
  			boolean numeric = true;
  			try{
  				Integer.valueOf(sub);
  			}catch(Exception e){
  				numeric = false;
  			}
  			doRefresh = numeric;
  		}
  	}
  	if(doRefresh){
  		code = code_getNextCode(fieldID, prefix, code_getSeperator(), nbrOfCodeDigits, code_isResetDigitsWhenPrefixChanges());
  		setPropertyString(fieldID, code);
  	}
  	return code;
  }
  
  public String code_refreshCodeIfAutomatic(int fieldID, String prefix){
  	return code_refreshCodeIfAutomatic(fieldID, prefix, 5);
  }
  
  public String code_setCodeIfNeeded(int fieldID, String prefix){
  	return code_setCodeIfNeeded(fieldID, prefix, 5);
  }

  public String code_setCodeIfNeeded(int fieldID, String prefix, int nbrOfDigits){
  	String code = getPropertyString(fieldID);
  	if(code == null || code.isEmpty()){
  		code_setCode(fieldID, prefix, nbrOfDigits);
  	}
  	return code;
  }

  public boolean code_hasCode(){
  	return getThisFocDesc().getFieldByID(FField.FLD_CODE) != null;
  }
  
  public boolean code_hasProposalCode(){
  	return getThisFocDesc().getFieldByID(FField.FLD_PROPOSAL_CODE) != null;
  }
  
  public void code_copyProposalCode(boolean promptForChange){
  	boolean doit = code_hasProposalCode();
  	if(doit && !code_getProposalCode().isEmpty() && promptForChange && !code_getProposalCode().equals(code_getCode())){
  		doit = false;
  		if(Globals.getDisplayManager() == null){
  			doit = true;
  		}else if(!FGOptionPane.popupOptionPane_YesNo("Change Proposal Code", "Do you want to change the 'Proposal Code' from "+code_getProposalCode()+" to "+code_getCode()+" ?")){
  			doit = true;
  		}
  	}
		if(doit) code_setProposalCode(code_getCode());
  }

  public void code_setCode(int fieldID, String prefix, int nbrOfDigits){
		if(workflow_getTransactionConfig() == null || !workflow_getTransactionConfig().isLeaveCodeEmpty()){
			String code = code_getNextCode(fieldID, prefix, code_getSeperator(), nbrOfDigits, code_isResetDigitsWhenPrefixChanges());
			setPropertyString(fieldID, code);
	
			if(this instanceof IStatusHolder){
				IStatusHolder statusHolder = (IStatusHolder) this;
				if(statusHolder != null && statusHolder.getStatusHolder() != null){
					if(statusHolder.getStatusHolder().getStatus() == StatusHolderDesc.STATUS_PROPOSAL){
						code_copyProposalCode(true);
					}
				}
			}
		}
  }

  
  //FIXME
  public boolean code_checkIfTaken(){
  	boolean exist = false;
    FocDesc desc  = getThisFocDesc();
    
    FField fld = desc.getFieldByID(FField.FLD_CODE);
    if(fld != null && fld.isDBResident()){
//    	StringBuffer buff = new StringBuffer(fld.getName()+" = \""+code_getCode()+"\"");
    	int filterType = SQLFilter.FILTER_ON_KEY;
    	WFTransactionConfig transConfig = workflow_getTransactionConfig();
    	if(transConfig != null && !transConfig.isCodeBySITE()) filterType = SQLFilter.FILTER_ON_KEY_EXCLUDE_SITE;
    	
    	StringBuffer buff = new StringBuffer();
    	SQLFilter filter = new SQLFilter(this, filterType);
    	filter.setOwnerOfTemplate(false);
    	filter.addWhereToRequest_WithoutWhere(buff, getThisFocDesc());
    	filter.dispose();
	    
	    ArrayList valuesArray = Globals.getApp().getDataSource().command_Select(desc, FField.FLD_CODE, false, buff);
	    exist = valuesArray.size() > 0;
    }
    return exist;
  }
  
//  public boolean code_checkIfTaken(){
//
//  	boolean exist = false;
//    FocDesc desc  = getThisFocDesc();
//    
//    FField fld = desc.getFieldByID(FField.FLD_CODE);
//    if(fld != null && fld.isDBResident()){
//	    StringBuffer buff = new StringBuffer(fld.getName()+" = \""+code_getCode()+"\"");
//	    
//	    ArrayList valuesArray = Globals.getApp().getDataSource().command_Select(desc, FField.FLD_CODE, false, buff);
//	    exist = valuesArray.size() > 0;
//    }
//    return exist;
//  
//  }

  public int code_getNextCodeIndex(int fieldID, String prefix, String separator, int nbrOfCodeDigits, boolean resetWhenPrefixChange){
  	int code = 1;
  	FProperty prop = getFocProperty(fieldID);
  	if(prop != null){
  		//In case the field is reflecting, this will give us the focObject with the property stored int the database
  		//This is for the case of WBSPointer and WBS
  		FocObject focObject = prop.getFocObject();
  		if(focObject != null){
  			code = focObject.code_getNextCodeIndex_Internal(fieldID, prefix, separator, nbrOfCodeDigits, resetWhenPrefixChange);
  		}
  	}
  	return code;
  }
  
  protected int code_getNextCodeIndex_Internal(int fieldID, String prefix, String separator, int nbrOfCodeDigits, boolean resetWhenPrefixChange){
    int     next = 1;
    FocDesc desc = getThisFocDesc();
    
    String prefixWithSeperator = prefix;
    if(separator != null) prefixWithSeperator += separator; 
    
    StringBuffer buff = null;
    if(resetWhenPrefixChange){
    	if(desc.getProvider() == DBManager.PROVIDER_MSSQL){
    		buff = new StringBuffer(desc.getFieldByID(fieldID).getName()+" like \'"+prefixWithSeperator+"%\'");
    	}else if(desc.getProvider() == DBManager.PROVIDER_ORACLE
    			  || desc.getProvider() == DBManager.PROVIDER_H2
    			  || desc.getProvider() == DBManager.PROVIDER_POSTGRES
    			  ){
      	buff = new StringBuffer("\""+desc.getFieldByID(fieldID).getName()+"\" like \'"+prefixWithSeperator+"%\'");
    	}else{
    		buff = new StringBuffer(desc.getFieldByID(fieldID).getName()+" like \""+prefixWithSeperator+"%\"");
    	}
    }
    if(desc.isByCompany()){
      Company company = getCompany();
      if(company != null){
      	String expression = "";
      	if(			desc.getProvider() == DBManager.PROVIDER_ORACLE
      			|| 	desc.getProvider() == DBManager.PROVIDER_POSTGRES){
      		expression = "\""+desc.getFieldByID(FField.FLD_COMPANY).getDBName()+"\"="+company.getReferenceInt();
      	}else{
      		expression = desc.getFieldByID(FField.FLD_COMPANY).getDBName()+"="+company.getReferenceInt();
      	}
      	
	      if(buff == null){
	       	buff = new StringBuffer(expression);
	      }else{
	      	buff.append(" and ");
	      	buff.append(expression);
	      }
      }
    }
    
    WFTransactionConfig transactionConfig = workflow_getTransactionConfig();
		if(transactionConfig != null && transactionConfig.isCodeBySITE()){
	  	if(this instanceof IWorkflow){
	  		IWorkflow workflow = (IWorkflow) this;
	  		if(workflow != null && workflow.iWorkflow_getWorkflow() != null){
	  			WFSite area = workflow.iWorkflow_getWorkflow().getArea();
	  			if(area == null) area = Globals.getApp().getCurrentSite();
	  			if(area != null && getThisFocDesc() != null && getThisFocDesc() instanceof IWorkflowDesc){
	  				WorkflowDesc workflowDesc = ((IWorkflowDesc)getThisFocDesc()).iWorkflow_getWorkflowDesc();
	  				if(workflowDesc != null){
	  					FField fld = getThisFocDesc().getFieldByID(workflowDesc.getFieldID_Site_1());
	  					if(fld != null && !Utils.isStringEmpty(fld.getDBName())){
			  	    	String expression = "";
			  	    	if(    desc.getProvider() == DBManager.PROVIDER_ORACLE
			  	    			|| desc.getProvider() == DBManager.PROVIDER_POSTGRES){
			  	    		expression = "\""+fld.getDBName()+"\"="+area.getReferenceInt();
			  	    	}else{
			  	    		expression = fld.getDBName()+"="+area.getReferenceInt();
			  	    	}
			  	    	
			  	      if(buff == null){
			  	       	buff = new StringBuffer(expression);
			  	      }else{
			  	      	buff.append(" and ");
			  	      	buff.append(expression);
			  	      }
			  	      
	  					}
	  				}
	  			}
	  		}
	  	}
		}
    
    ArrayList valuesArray = Globals.getApp().getDataSource().command_Select(desc, fieldID, false, buff);
    for(int i=0; i<valuesArray.size(); i++){
      String  str  = (String) valuesArray.get(i);

      //These lines were added to go arround the case where there is no character separating the prefix from the code
      //example PJ130001 the 13 is part of the prefix. In this case and without these lines we will get
      //PJ13130002 as next code
      if(str.startsWith(prefixWithSeperator)){
      	str = str.replace(prefixWithSeperator, "");
      }

      int number = 0;
      if(str != null && !str.isEmpty()){
	      try{
	      	number = Integer.valueOf(str);
	      }catch(Exception e){
	      	number=0;
	      }
      }
      
      if(next <= number){
        next = number + 1;
      }  
    }
        
    return next;
  }

  public String code_getNextCode(int fieldID, String prefix, String separator, int nbrOfCodeDigits, boolean resetWhenPrefixChange){
    int next = code_getNextCodeIndex(fieldID, prefix, separator, nbrOfCodeDigits, resetWhenPrefixChange);
    if(getThisFocDesc() != null && getThisFocDesc().getFieldByID(FField.FLD_PROPOSAL_CODE) != null){
    	if(this instanceof IStatusHolder){
    		StatusHolder statusHolder = ((IStatusHolder) this).getStatusHolder();
    		if(statusHolder != null && statusHolder.getStatus() == StatusHolderDesc.STATUS_PROPOSAL){
        	int next_BasedOnProposalField = code_getNextCodeIndex(FField.FLD_PROPOSAL_CODE, prefix, separator, nbrOfCodeDigits, resetWhenPrefixChange);
        	if(next_BasedOnProposalField > next) next = next_BasedOnProposalField;    			
    		}
    	}
    }
    
    String prefixWithSeperator = prefix;
    if(separator != null) prefixWithSeperator += separator; 
    
    String nextStr = String.valueOf(next);
    StringBuffer nextCode = new StringBuffer(prefixWithSeperator);
    for(int i=nextStr.length(); i<nbrOfCodeDigits; i++) nextCode.append('0');
    nextCode.append(nextStr);
    
    return nextCode.toString();
  }

  public boolean status_hasStatus(){
  	boolean hasStatus = false;
  	if(this instanceof IStatusHolder){
			IStatusHolder statusHolder = (IStatusHolder) this;
			if(statusHolder != null && statusHolder.getStatusHolder() != null){
				hasStatus = true;
			}
  	}
  	return hasStatus;
  }
  
  public FocObject newObjectReloaded(){
  	return newObjectReloaded(false);
  }
  
  public FocObject newObjectReloaded(boolean shared){
  	return getThisFocDesc().newObject(getReference().getInteger(), shared);
  }

  public IFocCellPainter getCellPainter(FTable table, boolean isSelected, boolean hasFocus, int row, int column, FProperty prop, Rectangle cellRectangle){
  	return null;  	
  }
  
  @Override
  public void setCreated(boolean created) {
  	super.setCreated(created);
  	setCompanyIfCreated();
  }

	public boolean isShared() {
		return (flags & FLG_SHARED) != 0;
	}

	public void setShared(boolean shared) {
    if(shared){
      flags = (char)(flags | FLG_SHARED);
    }else{
      flags = (char)(flags & ~FLG_SHARED);
    }
	}
	
	public boolean isFreshColor() {
		return (flags & FLG_FRESH_COLOR) != 0;
	}

	public void setFreshColor(boolean shared) {
    if(shared){
      flags = (char)(flags | FLG_FRESH_COLOR);
    }else{
      flags = (char)(flags & ~FLG_FRESH_COLOR);
    }
	}
	
	private static FDummyProperty_String dummyProperty_String = null;
	public static FDummyProperty_String getDummyProperty_String(){
		if(dummyProperty_String == null){ 
			dummyProperty_String = new FDummyProperty_String();
		}
		return dummyProperty_String;
	}

	private static FDummyProperty_Double dummyProperty_Double = null;
	public static FDummyProperty_Double getDummyProperty_Double(){
		if(dummyProperty_Double == null){ 
			dummyProperty_Double = new FDummyProperty_Double();
		}
		return dummyProperty_Double;
	}
	
	private static FDummyProperty_Boolean dummyProperty_Boolean = null;
	public static FDummyProperty_Boolean getDummyProperty_Boolean(){
		if(dummyProperty_Boolean == null){ 
			dummyProperty_Boolean = new FDummyProperty_Boolean();
		}
		return dummyProperty_Boolean;
	}

	private static FDummyProperty_Object dummyProperty_Object = null;
	public static FDummyProperty_Object getDummyProperty_Object(){
		if(dummyProperty_Object == null){ 
			dummyProperty_Object = new FDummyProperty_Object();
		}
		return dummyProperty_Object;
	}

	private void setObjectInDummyArrayAt(int at, Object obj){
		dummyPropertiesValuesArray[at] = obj;
	}
	
	private Object getObjectInDummyArrayAt(int at){
		return dummyPropertiesValuesArray[at];
	}
	
	public void dummyPropertyModified(FProperty property){
		int index = property.getFocField().getIndexOfPropertyInDummyArray();
		setObjectInDummyArrayAt(index, property.getObject());
	}
	
	public boolean isFieldMandatory(int fieldID){
		FocDesc desc  = getThisFocDesc();
		FField  field = desc != null ? desc.getFieldByID(fieldID) : null;
		return field != null ? field.isMandatory() : false;	
	}
	
	//--------------------------
	// LOG
	//--------------------------
	
	public Date getLogCreationTime(){
		return getPropertyDate(FField.FLD_CREATION_TIME);
	}

	public void setLogCreationTime(Date date){
		setPropertyDate(FField.FLD_CREATION_TIME, date);
	}

	public FocUser getLogCreationUser(){
		return (FocUser) getPropertyObject(FField.FLD_CREATION_USER);
	}

	public void setLogCreationUser(FocUser user){
		setPropertyObject(FField.FLD_CREATION_USER, user);
	}

	public Date getLogModificationTime(){
		return getPropertyDate(FField.FLD_MODIFICATION_TIME);
	}

	public void setLogModificationTime(Date date){
		setPropertyDate(FField.FLD_MODIFICATION_TIME, date);
	}

	public FocUser getLogModificationUser(){
		return (FocUser) getPropertyObject(FField.FLD_MODIFICATION_USER);
	}

	public void setLogModificationUser(FocUser user){
		setPropertyObject(FField.FLD_MODIFICATION_USER, user);
	}

	@Override
	public void resetStatus(){
		//The condition is because when the object is Created and empty it is not saved => Ref < 0 and we should not reset the status
		if(!isCreated() || hasRealReference()){
		//-------------
			boolean wasModified = isModified() ;//|| isCreated();
			super.resetStatus();
			if(wasModified){
				getThisFocDesc().resetStatusForFocObject(this);
			}
		}
	}

	public boolean isNodeCollapsed(){
		return getPropertyBoolean(FField.FLD_NODE_COLLAPSE);
	}

	public void setNodeCollapsed(boolean nodeCollapse){
		setPropertyBoolean(FField.FLD_NODE_COLLAPSE, nodeCollapse);
	}

	public int getFabOwner(){
		return getPropertyMultiChoice(FField.FLD_FAB_OWNER);
	}

	public void setFabOwner(int owner){
		setPropertyMultiChoice(FField.FLD_FAB_OWNER, owner);
	}
	
	public String getPrintingFileName(){
		String prefix = "";
		if(getThisFocDesc().getFieldByID(FField.FLD_CODE) != null && !code_getCode().isEmpty()){
			prefix = code_getCode();
		}else{
			prefix = "everpro_document";
		}
		return prefix;
	}

	//--------------------------
	// IFocObject implementation
	//--------------------------
	@Override
  public String iFocObject_getPropertyString(String fieldName){
  	FField fld = getFieldByName(fieldName);
  	return getPropertyString(fld.getID());
  }
  
	@Override
  public void iFocObject_setPropertyString(String fieldName, String value){
  	FField fld = getFieldByName(fieldName);
  	setPropertyString(fld.getID(), value);
  }

	@Override
  public double iFocObject_getPropertyDouble(String fieldName){
  	FField fld = getFieldByName(fieldName);
  	return getPropertyDouble(fld.getID());
  }
  
	@Override
  public void iFocObject_setPropertyDouble(String fieldName, double value){
		FField fld = getFieldByName(fieldName);
		setPropertyDouble(fld.getID(), value);
  }

	@Override
  public int iFocObject_getPropertyInteger(String fieldName){
  	FField fld = getFieldByName(fieldName);
  	return getPropertyInteger(fld.getID());
  }
  
	@Override
  public void iFocObject_setPropertyInteger(String fieldName, int value){
		FField fld = getFieldByName(fieldName);
		setPropertyInteger(fld.getID(), value);
  }

	@Override
  public IFocObject iFocObject_getPropertyFocObject(String fieldName){
		FField fld = getFieldByName(fieldName);
		return getPropertyObject(fld.getID());
  }
  
	@Override
  public void iFocObject_setPropertyFocObject(String fieldName, IFocObject value){
		FField fld = getFieldByName(fieldName);
		setPropertyObject(fld.getID(), (FocObject) value);
  }

	@Override
  public Date iFocObject_getPropertyDate(String fieldName){
		FField fld = getFieldByName(fieldName);
		return getPropertyDate(fld.getID());
	}
	
	@Override
  public void iFocObject_setPropertyDate(String fieldName, Date value){
		FField fld = getFieldByName(fieldName);
		setPropertyDate(fld.getID(), value);
	}

	@Override
  public IFocList iFocObject_getPropertyList(String fieldName){
		FField fld = getFieldByName(fieldName);
		return getPropertyList(fld.getID());
  }

	@Override
	public IFocObject iFocObject_getFatherObject(){
		return getFatherObject();
	}
	
	@Override
	public boolean iFocObject_validate(){
		return validate(true);
	}
	
	@Override
	public void iFocObject_cancel(){
		cancel();
	}
	
	@Override
	public void iFocObject_dispose(){
		dispose();
	}
	//--------------------------
	
	protected int[] duplication_getExcludedPropertiesArray(){
		return null;
	}
	
	public void setObjectPropertyEqualToSingleValue(int fieldID){
		FObject oProp = (FObject) getFocProperty(fieldID); 
  	if(oProp.getPropertySourceList() != null && oProp.getPropertySourceList().size() == 1){
  		oProp.setObject(oProp.getPropertySourceList().getFocObject(0));
  	}
	}
	
	public boolean isEditAfterInsert(){
		return false;
	}
	
	// ---------------------------------------------
	// ---------------------------------------------
	// SYNC
	// ---------------------------------------------
	// ---------------------------------------------

	public boolean sync_IsNew(){
		return getPropertyBoolean(FField.FLD_SYNC_IS_NEW_OBJECT);
	}

	public void sync_SetNew(boolean isNew){
		setPropertyBoolean(FField.FLD_SYNC_IS_NEW_OBJECT, isNew);
	}

	public boolean sync_AllowObjectDBModification(){
		return Globals.getDBManager() != null && (!Globals.getDBManager().sync_isRemote() || sync_IsNew());
	}
	
	// ---------------------------------------------
	// ---------------------------------------------
	
	public static boolean equal(FocObject o1, FocObject o2){
		return (o1 == null && o2 == null) || (o1 != null && o2 != null && o1.equalsRef(o2));
	}
	
	public static int compareByRef(FocObject o1, FocObject o2){
		int compare = 0;
		if(o1 == null && o2 != null){
			compare = -1;
		}else if(o1 != null && o2 == null){
			compare = 1;
		}else if(o1 != null && o2 != null){
			long ref1 = o1.getReferenceInt();
			long ref2 = o2.getReferenceInt();
			long diff = ref1 - ref2;
			
			compare = diff == 0 ? 0 : (diff < 0 ? -1 : 1);
		}

		return compare;
	}

	public IFocObjectPlugIn getIFocObjectPlugIn() {
		return iFocObjectPlugIn;
	}

	public void setIFocObjectPlugIn(IFocObjectPlugIn iFocDescPlugIn) {
		this.iFocObjectPlugIn = iFocDescPlugIn;
	}
	
	public FPanel newFabGuiDetailsPanel_DefaultView(){
		FPanel guiPanel = null; 
		GuiDetails guiDetails = getThisFocDesc().getFabDefaultGuiDetails();
		if(guiDetails != null){
			guiPanel = new UserDefinedObjectGuiDetailsPanel(this, guiDetails.getReference().getInteger());
		}
		return guiPanel;
	}

	// --------------------------------------------------------------------------
	// --------------------------------------------------------------------------
	// Workflow
	// --------------------------------------------------------------------------
	// --------------------------------------------------------------------------
	/*
  public boolean workflowAllowsActionByThisUser(int action){
  	boolean allow = true;
  	if(this instanceof IWorkflow && this instanceof IStatusHolder){
  		IWorkflow workflow = (IWorkflow) this;
  		if(workflow != null && workflow.iWorkflow_getWorkflow() != null){
  			WFSite area = workflow.iWorkflow_getWorkflow().getArea();
  			if(area != null){
    			IWorkflowDesc iWorkflowDesc = (IWorkflowDesc) getThisFocDesc();
    			allow = area.canThisUser_DoAction_OnThisTransaction(iWorkflowDesc.iWorkflow_getDBTitle(), action);
  			}
  		}
  	}
  	return allow;
  }
  */

	public boolean workflow_IsAllowUndoSignature(){
		return getThisFocDesc() != null ? getThisFocDesc().workflow_IsUndoSignature(this) : false;
	}
	
	public boolean workflow_IsAllowModification(){
		return workflow_IsAllowModification(null);
	}

	public FocObject getFirstFatherFocObject(){
		FocObject father = null;
		AccessSubject accessSubject = getFatherSubject();
		while(accessSubject != null && father == null){
			accessSubject = accessSubject.getFatherSubject();
			if(accessSubject instanceof FocObject){
				father = (FocObject) accessSubject; 
			}
		}
		return father;
	}
	
	public FocObject workflow_GetFirstFatherLoggable(){
		FocObject fatherWithWorkflow = null;
		FocObject father = this;
		while(father != null && fatherWithWorkflow == null){
			if(father.workflow_IsLoggable()){
				fatherWithWorkflow = father;
			}
			father = father.getFirstFatherFocObject();
		}
		return fatherWithWorkflow;
	}
	
	public FocObject workflow_GetFirstFatherWorkflowSubject(){
		FocObject fatherWithWorkflow = null;
		FocObject father = this;
		while(father != null && fatherWithWorkflow == null){
			if(father.workflow_IsWorkflowSubject()){
				fatherWithWorkflow = father;
			}
			father = father.getFirstFatherFocObject();
		}
		return fatherWithWorkflow;
	}
	
	public boolean workflow_IsAllowModification_AccordingToFieldStageLock(String dataPath){
		boolean allowModif = true;
		//If the FocObject is Workflow Subject with a MAP configuration then only the currently signing user can edit 
		if(workflow_IsWorkflowSubject() && workflow_getTransactionConfig() != null && workflow_getTransactionConfig().getApprovalMethod() == WFTransactionConfigDesc.APPROVAL_METHOD_BY_WORKFLOW && workflow_getTransactionConfig().getWorkflowMap() != null){
			IWorkflow workflow = (IWorkflow) this;
			
			//This weird condition is For the WBS and The Project (When we have in the project WBS.TARGET_DATE as editable up to a certain stage without this condition it gets locked.
			//---------------------------------------------------
			if(this.getThisFocDesc().getStorageName().equals(workflow.iWorkflow_getWorkflow().getFocDesc().getStorageName())){
				WFStage stageLock = null;
				boolean neverLockWhateverTheStage = false;
				if(dataPath != null){
					FocList listOfFieldWithSpecialLockStage = workflow_getTransactionConfig().getFieldLockStageList();
					for(int i=0; i<listOfFieldWithSpecialLockStage.size(); i++){
						WFFieldLockStage fls = (WFFieldLockStage) listOfFieldWithSpecialLockStage.getFocObject(i);
						if(fls.getFieldName().equals(dataPath)){
							stageLock = fls.getWFStage();
							if(stageLock == null) neverLockWhateverTheStage = true;
						}
					}
				}
				
				if(stageLock != null){
					allowModif = workflow_CompareStages(stageLock) < 0;
//					allowModif = workflow_NeedsSignatureOfThisUser();
				}else{
					if(neverLockWhateverTheStage){
						allowModif = true;
					}else{
						WFMap map = workflow_getTransactionConfig().getWorkflowMap();
						
						WFStage currentStage = workflow.iWorkflow_getWorkflow().getCurrentStage();
						if(currentStage == null) {
							allowModif =    workflow_NeedsSignatureOfThisUser_WithoutAllowSignatureCheck()/*Only the user waiting to sign can edit*/
									         || (map.getTitleInitialEdit() != null && map.getTitleInitialEdit().equals(Globals.getApp().getCurrentTitle()));
						} else {
							WFStage lockStartStage = map.getStageOfLockBegin();
							allowModif = 		lockStartStage != null 
													&& 	workflow_CompareStages(lockStartStage) < 0
													&&  workflow_NeedsSignatureOfThisUser_WithoutAllowSignatureCheck();
						}
						
//						allowModif =	 workflow.iWorkflow_getWorkflow().getCurrentStage() == null 
//												&& (    workflow_NeedsSignatureOfThisUser_WithoutAllowSignatureCheck()/*Only the user waiting to sign can edit*/
//												     || (map.getTitleInitialEdit() != null && map.getTitleInitialEdit().equals(Globals.getApp().getCurrentTitle()))
//												   );
					}
//						allowModif = workflow.iWorkflow_getWorkflow().getCurrentStage() == null || workflow_NeedsSignatureOfThisUser();//Only the user waiting to sign can edit
				}
			}
		}
		return allowModif;
	}
	
	public boolean workflow_IsAllowModification(String dataPath){
		boolean allowModif = true;
		if(dataPath != null && !dataPath.equals("WF_COMMENT") && !dataPath.equals("WF_CANCEL_REASON")){
			if(allowModif) allowModif = getThisFocDesc() != null ? getThisFocDesc().workflow_IsAllowModify(this) : false;
			if(allowModif){
				allowModif = workflow_IsAllowModification_AccordingToFieldStageLock(dataPath);
				if(allowModif){
					FocObject fatherWithWorkflow = workflow_GetFirstFatherWorkflowSubject();
					if(fatherWithWorkflow != null){
						allowModif = fatherWithWorkflow.workflow_IsAllowModification_AccordingToFieldStageLock(dataPath);
					}
				}
			}
		}
		return allowModif;
	}

	public boolean workflow_IsAllowModification_Code(){
	  return getThisFocDesc() != null ? getThisFocDesc().workflow_IsAllowModifyCode(this) : false;
	}

	public boolean workflow_IsAllowViewLog(){
		return getThisFocDesc() != null ? getThisFocDesc().workflow_IsAllowViewLog() : false;
	}

	/*public boolean workflow_IsAllowDeletion(){
		return getThisFocDesc() != null ? getThisFocDesc().workflow_IsAllowDelete(this) : false;
	}*/
	
	public boolean workflow_IsAllowDeletion(){
		boolean isAllowDeletion = getThisFocDesc() != null ? getThisFocDesc().workflow_IsAllowDelete(this) : false;
		isAllowDeletion = isAllowDeletion && !focObject_IsLocked();
		return isAllowDeletion;
	}
	
	public boolean workflow_IsAllowClose(){
		boolean isAllowClose = getThisFocDesc() != null ? getThisFocDesc().workflow_IsAllowClose(this) : false;
		isAllowClose = isAllowClose && !focObject_IsLocked();
		return isAllowClose;
	}

	public boolean workflow_IsAllowApprove(){
		boolean isAllowApprove = getThisFocDesc() != null ? getThisFocDesc().workflow_IsAllowApprove(this) : false;
		isAllowApprove = isAllowApprove && !focObject_IsLocked();
		return isAllowApprove;
	}
	
	public boolean workflow_IsAllowResetToProposal(){
		boolean isAllowProposal = getThisFocDesc() != null ? getThisFocDesc().workflow_IsAllowResetToProposal(this) : false;
		isAllowProposal = isAllowProposal && !focObject_IsLocked();
		return isAllowProposal;
	}
	
	public boolean workflow_IsAllowResetToApproved(){
		boolean isAllowApprove = getThisFocDesc() != null ? getThisFocDesc().workflow_IsAllowResetToApproved(this) : false;
		isAllowApprove = isAllowApprove && !focObject_IsLocked();
		return isAllowApprove;
	}

	public WFTransactionConfig workflow_getTransactionConfig(){
		WFTransactionConfig assignment = null; 
  	
  	if(this instanceof IWorkflow && this instanceof IStatusHolder){
  		IWorkflow     workflow     = (IWorkflow) this;
  		if(workflow != null){
  			assignment = WFTransactionConfigDesc.getTransactionConfig_ForTransaction(workflow);
  		}
  	}
  	return assignment;
	}

	public String workflow_GetSignButtonCaption(boolean onBehalf){
		String caption = "";
		
		WFSignatureNeededResult result = workflow_NeedsSignatureOfThisUser_AsTitleIndex(null);
		if(result != null){
			WFSignature currentSignature = result.getSignature();
			if(currentSignature != null) caption = currentSignature.getSignCaption();
		}
	  
	  if(Utils.isStringEmpty(caption)) {
			if(onBehalf) caption = ConfigInfo.isArabic() ? "موافقة بالنيابة" : "Sign PP";
			else caption = ConfigInfo.isArabic() ? " موافقة " : "Sign" ;
	  }

		return caption;
	}
	
	public String workflow_GetRejectButtonCaption(boolean onBehalf){
		String caption = "";
		
		WFSignatureNeededResult result = workflow_NeedsSignatureOfThisUser_AsTitleIndex(null);
		if(result != null){
			WFSignature currentSignature = result.getSignature();
			if(currentSignature != null) caption = currentSignature.getRejectCaption();
		}
	  
	  if(Utils.isStringEmpty(caption)) {
	  	caption = ConfigInfo.isArabic() ? " رفض " : "Reject" ;
	  }

		return caption;
	}
	
	public boolean workflow_IsRejectButtonVisible(){
		boolean visible = false;
		
		WFSignatureNeededResult result = workflow_NeedsSignatureOfThisUser_AsTitleIndex(null);
		if(result != null){
			WFSignature currentSignature = result.getSignature();
			if(currentSignature.getPreviousStage() == null) {
				visible = false;
			} else {
				visible = !currentSignature.isRejectHidden();
			}
		}
		return visible;
	}
	
	public boolean workflow_IsLastSignatureDoneByThisUser(boolean forceReloadOfLogList){
		boolean signProject = false;
		
		if(workflow_IsWorkflowSubject() && this instanceof IWorkflow){
			Workflow workflow = ((IWorkflow)this).iWorkflow_getWorkflow();
			if(workflow != null){
				signProject = workflow.isLastSignatureDoneByThisUser(forceReloadOfLogList);
			}
		}
		return signProject;
	}	
	
	public FocObject workflow_getProjectWBS(){
		return null;
	}

	public boolean workflow_NeedsSignatureOfThisUser_WithoutAllowSignatureCheck(){
		return workflow_NeedsSignatureOfThisUser_AsTitleIndex(null, false).getTitleIndex() >= 0;
	}

	public boolean workflow_NeedsSignatureOfThisUser(){
		return workflow_NeedsSignatureOfThisUser(null);
	}
	
	public boolean workflow_NeedsSignatureOfThisUser(WFMap map){
		return workflow_NeedsSignatureOfThisUser_AsTitleIndex(map).getTitleIndex() >= 0;
	}
	
	public WFSignatureNeededResult workflow_NeedsSignatureOfThisUser_AsTitleIndex(WFMap map){
		return workflow_NeedsSignatureOfThisUser_AsTitleIndex(map, true);
	}
	
	public WFSignatureNeededResult workflow_NeedsSignatureOfThisUser_AsTitleIndex(WFMap map, boolean checkIfAllowSignature){
		WFSignatureNeededResult signatureNeededResult = new WFSignatureNeededResult(-1, false);
		
		if(!iStatusHolder_isTransactionCanceled() && workflow_IsWorkflowSubject() && this instanceof IWorkflow){
			Workflow workflow = ((IWorkflow)this).iWorkflow_getWorkflow();
			if(workflow != null && workflow.getIWorkflowDesc() != null){
				WFSite     currentSite       = workflow.getArea();
				WFStage    currentStage      = workflow.getCurrentStage();
				Department currentDepartment = getDepartment();

				//A- Get the Title needed for the signature
				//1- Get the Transaction Signature Map
				//2- Get the WFSignature line that has the current transaction stage as previousStage
				//3- read the titles on that WFSignature
				
				//B- Get the Titles for the this user.getTitlesList();
				
				//A-
				FocUser user = Globals.getApp().getUser_ForThisSession();
				if(map == null){
					map = WFTransactionConfigDesc.getMap_ForTransaction(workflow.getIWorkflowDesc().iWorkflow_getDBTitle());
				}
				if(map != null && user != null /*&& user.getTitlesList() != null*/){//USERREFACTOR
					WFSignature currentSignature = map.findSignature_PreviousStage(this, currentStage);
					
					if(currentSignature != null){
						signatureNeededResult.setSignature(currentSignature);
						for(int t=0; t<WFSignatureDesc.FLD_TITLE_COUNT && signatureNeededResult.getTitleIndex()<0; t++){
							WFTitle title = currentSignature.getTitle(t);
							if(title != null){
								boolean projSpecific = false;
								if(title.isProjectSpecific()){
									FocObject wbsFocObject = workflow_getProjectWBS();
									if(wbsFocObject != null){
										projSpecific = true;
									//PROJECT_MANAGER_SIGNATURE
										IFocData userProperty = wbsFocObject.iFocData_getDataByPath(title.getUserDataPathFromProjWBS());
										if(userProperty != null){
											Object userFocObject = userProperty.iFocData_getValue();
											
											signatureNeededResult.setTitleIndex(-1);
											
											if(userFocObject instanceof Contact){
												if(FocObject.equal(user.getContact(), (FocObject) userFocObject)){
													signatureNeededResult.setTitleIndex(t);
												}
											}else if(userFocObject instanceof FocUser){
												if(FocObject.equal(user, (FocUser) userFocObject)){
													signatureNeededResult.setTitleIndex(t);
												}
											}
										}
									}
								}
								
								if(!projSpecific){
									boolean hasTitle               = user.hasTitle(currentSite, title, currentDepartment);
									boolean hasTitle_ByReplacement = user.hasTitle_InActingAs(currentSite, title, currentDepartment);
									if(hasTitle || hasTitle_ByReplacement){
										if(!checkIfAllowSignature || ((IWorkflow)this).iWorkflow_allowSignature(currentSignature)){
											signatureNeededResult.setTitleIndex(t);
											signatureNeededResult.setOnBehalfOf(hasTitle_ByReplacement);
										}else{
											signatureNeededResult.setSignature(null);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return signatureNeededResult;
	}

	public void workflow_SignIfAllowed(String comment) {
		if(workflow_IsWorkflowSubject() && this instanceof IWorkflow){
			Workflow workflow = ((IWorkflow)this).iWorkflow_getWorkflow();
			
			WFSignatureNeededResult result = workflow_NeedsSignatureOfThisUser_AsTitleIndex(null);
			if(result != null){
				workflow.sign(result.getSignature(), result.getTitleIndex(), result.isOnBehalfOf(), comment);
			}else{
				workflow.sign(comment);
			}
		}
	}
	
	/**
	 * 
	 * @return  0 if the transaction stage is equal to stage
	 *          1 if the transaction stage is greater than stage
	 *         -1 if the transaction stage is less then stage
	 */
	public int workflow_CompareStages(WFStage stage){
		int compare = -1;
		
		if(workflow_IsWorkflowSubject() && this instanceof IWorkflow){
			Workflow workflow = ((IWorkflow)this).iWorkflow_getWorkflow();
			if(workflow != null){
				WFStage currentStage = workflow.getCurrentStage();
				
				if(stage == null && currentStage == null){
					compare = 0;
				}else if(stage == null && currentStage != null){
					compare = 1;
				}else if(stage != null && currentStage == null){					
					compare = -1;
				}else{
					WFTransactionConfig config = workflow_getTransactionConfig();
					if(config.getWorkflowMap() != null){
						for(int i=0; i<config.getWorkflowMap().getSignatureList().size(); i++){
							WFSignature signature = (WFSignature) config.getWorkflowMap().getSignatureList().getFocObject(i);
							
							boolean isTargetStage  = signature.getTargetStage().equalsRef(stage);
							boolean isCurrentStage = signature.getTargetStage().equalsRef(currentStage);
							if(isTargetStage || isCurrentStage){
								if(isCurrentStage && isTargetStage){
									compare = 0;
								}else if(isCurrentStage){
									compare = -1;
								}else if(isTargetStage){
									compare = 1;
								}
								break;
							}
						}
					}
				}
			}
		}
		return compare;
	}
	
	public String workflow_getTransactionTitle(){
		String title = "";
		WFTransactionConfig assignment = workflow_getTransactionConfig();
		if(assignment != null){
			IStatusHolder statusHolder = (IStatusHolder) this;
			if(statusHolder.getStatusHolder().getStatus() == StatusHolderDesc.STATUS_PROPOSAL){
				title = assignment.getTransactionTitle_Proposal();
			}else{
				title = assignment.getTransactionTitle();
			}
			if(title.isEmpty()){
				IWorkflowDesc iWorkflowDesc = (IWorkflowDesc) getThisFocDesc();
				title = iWorkflowDesc.iWorkflow_getTitle();
			}
		}
		return title;
	}

	public boolean workflow_IsPromptForApproveUponValidation(){
  	boolean prompt = true;
  	
  	if(this instanceof IWorkflow && this instanceof IStatusHolder){
  		IWorkflow     workflow     = (IWorkflow) this;
  		IStatusHolder statusHolder = (IStatusHolder) this;
  		if(workflow != null && statusHolder != null){
  			WFTransactionConfig assignment 	= WFTransactionConfigDesc.getTransactionConfig_ForTransaction(workflow);
  			prompt = assignment.isPromtForApproveUponValidation();
  		}
  	}
  	return prompt;
	}

	public boolean workflow_IsAllowCancel(){
		return !focObject_IsLocked() && getThisFocDesc() != null ? getThisFocDesc().workflow_IsAllowCancel(this) : false;
	}
	
	@Override
  public boolean validate_FromTheValidationPanel(boolean checkValidity) {
		boolean isCreated = isCreated();
		boolean b         = validate(checkValidity);
		if(b && !isCreated){
			if(workflow_IsWorkflowSubject() && workflow_IsAllowApprove() && workflow_IsPromptForApproveUponValidation()){
	    	if(this instanceof IStatusHolder){
	    		StatusHolder statusHolder = ((IStatusHolder) this).getStatusHolder();
	    		if(statusHolder != null && statusHolder.getStatus() == StatusHolderDesc.STATUS_PROPOSAL){
	    			String[] opts = {"Approve", "Keep it "+StatusHolderDesc.PRINTED_LABEL_FOR_PROPOSAL};
	    			
	    			int ret = FGOptionPane.popupOptionPane_Options("Do you want to Approve this transaction", "This transaction is still a "+StatusHolderDesc.PRINTED_LABEL_FOR_PROPOSAL+".\nDo you want to Approve it?", opts);
	    			if(ret == 0){
	    				if(!FGOptionPane.popupOptionPane_YesNo("Confirmation", "Are you sure you want to approve this transaction")){
			    			statusHolder.setStatusToValidated();
			    			validate(true);
	    				}
	    			}else if(ret == 1){
	    			}
	    		}
	    	}
			}
		}
  	return b;
  }
	
	public boolean isFieldCreationField(String fieldName) {
		return fieldName != null && fieldName.equals(StatusHolderDesc.FNAME_CREATION_USER);
	}
	
	/*
	public boolean isFieldCreationField(FField fld) {
		boolean isCreationUser = false;
		FocDesc focDesc = getThisFocDesc();
		if(focDesc != null && focDesc instanceof IStatusHolderDesc) {
			IStatusHolderDesc statusDesc = (IStatusHolderDesc) focDesc;
			isCreationUser = fld.getID() == statusDesc.getFLD_CREATION_USER();
		}
		return isCreationUser;
	}
	*/
	
	public boolean isFieldWorkflowField(FField fld) {
		boolean isWorkflow = false;
		FocDesc focDesc = getThisFocDesc();
		if(focDesc != null) {
			if(focDesc instanceof IStatusHolderDesc) {
				IStatusHolderDesc statusDesc = (IStatusHolderDesc) focDesc;
				isWorkflow = fld.getID() == statusDesc.getFLD_CLOSURE_DATE();
				isWorkflow = isWorkflow || fld.getID() == statusDesc.getFLD_CREATION_DATE();
//				isWorkflow = isWorkflow || fld.getID() == statusDesc.getFLD_CREATION_USER();
				isWorkflow = isWorkflow || fld.getID() == statusDesc.getFLD_STATUS();
				isWorkflow = isWorkflow || fld.getID() == statusDesc.getFLD_VALIDATION_DATE();
			}
			if(!isWorkflow && focDesc instanceof IWorkflowDesc) {
				IWorkflowDesc iWorkflowDesc = (IWorkflowDesc) focDesc;
				WorkflowDesc   workflowDesc = iWorkflowDesc.iWorkflow_getWorkflowDesc();

				if(workflowDesc != null) {
					isWorkflow = isWorkflow || fld.getID() == workflowDesc.getFieldID_AllSignatures();
					isWorkflow = isWorkflow || fld.getID() == workflowDesc.getFieldID_Canceled();
					isWorkflow = isWorkflow || fld.getID() == workflowDesc.getFieldID_CancelReason();
					isWorkflow = isWorkflow || fld.getID() == workflowDesc.getFieldID_Comment();
					isWorkflow = isWorkflow || fld.getID() == workflowDesc.getFieldID_CurrentStage();
					isWorkflow = isWorkflow || fld.getID() == workflowDesc.getFieldID_FunctionalStage();
					isWorkflow = isWorkflow || fld.getID() == workflowDesc.getFieldID_Hide(0);
					isWorkflow = isWorkflow || fld.getID() == workflowDesc.getFieldID_Hide(1);
					isWorkflow = isWorkflow || fld.getID() == workflowDesc.getFieldID_Hide(2);
					isWorkflow = isWorkflow || fld.getID() == workflowDesc.getFieldID_LastComment();
					isWorkflow = isWorkflow || fld.getID() == workflowDesc.getFieldID_LastModificationDate();
					isWorkflow = isWorkflow || fld.getID() == workflowDesc.getFieldID_LastModificationUser();
					isWorkflow = isWorkflow || fld.getID() == workflowDesc.getFieldID_LogList();
					isWorkflow = isWorkflow || fld.getID() == workflowDesc.getFieldID_Simulation();
					isWorkflow = isWorkflow || fld.getID() == workflowDesc.getFieldID_Site_1();
					isWorkflow = isWorkflow || fld.getID() == workflowDesc.getFieldID_Site_2();
					isWorkflow = isWorkflow || fld.getID() == workflowDesc.getFieldID_Title(0);
					isWorkflow = isWorkflow || fld.getID() == workflowDesc.getFieldID_Title(1);
					isWorkflow = isWorkflow || fld.getID() == workflowDesc.getFieldID_Title(2);
				}
			}
		}
		return isWorkflow;
	}
	
	public String buildJsonKey() {
		return getThisFocDesc() != null ? getThisFocDesc().getStorageName()+"|"+getReferenceInt() : null; 
	}

	public void appendKeyValueForFieldName_FullObject(B01JsonBuilder builder, String joinAlias, String fieldName) {
		boolean fullObject = builder.isPrintForeignKeyFullObject();
		builder.setPrintForeignKeyFullObject(true);
		appendKeyValueForFieldName(builder, joinAlias, fieldName);
		builder.setPrintForeignKeyFullObject(fullObject);
	}

	public void appendKeyValueForFieldName(B01JsonBuilder builder, String fieldName) {
		appendKeyValueForFieldName(builder, null, fieldName);
	}

	public void appendKeyValueForFieldName(B01JsonBuilder builder, String joinAlias, String fieldName) {
		FProperty prop = null;
		if(Utils.isStringEmpty(joinAlias)) {
			prop = getFocPropertyByName(fieldName);
		} else {
			prop = getFocPropertyByName(joinAlias+"-"+fieldName);
		}
		if(prop != null) {
			appendKeyValue_ForProperty(builder, fieldName, prop);
		}
	}
	
	public void appendKeyValue_ForProperty(B01JsonBuilder builder, String fieldName, FProperty prop) {
		if (prop != null) {
			if (prop instanceof FObject) {
				FObject objProp = (FObject) prop;
				if (builder.isPrintForeignKeyFullObject() && !isFieldCreationField(fieldName)) {
					FocObject valueObj = objProp.getObject_CreateIfNeeded();
					if (valueObj != null && !builder.containsMasterObject(valueObj.buildJsonKey())) {
						builder.appendKey(fieldName);
						B01JsonBuilder newBuilder = new B01JsonBuilder(builder);
						valueObj.toJson_Embedded(newBuilder);
						String objStr = newBuilder.toString();
						builder.append(objStr);
						newBuilder.dispose();
					}
				} else {
					if (builder.isPrintObjectNamesNotRefs()) {
						String value = String.valueOf(objProp.getLocalReferenceInt());
						FocObject valueFocObject = (FocObject) objProp.getObject();
						if (valueFocObject != null) {
							value = valueFocObject.getJSONName() + "[" + value + "]";
						}
						builder.appendKeyValue(fieldName, value);
					} else {
						long value = objProp.getLocalReferenceInt();
						builder.appendKeyValue(fieldName, value);
					}
				}
			} else if (prop instanceof FInt) {
				if (prop.isValueNull()) {
					builder.appendKey(fieldName);
					builder.appendNullValue();
				} else {
					builder.appendKeyValue(fieldName, prop.getInteger());
				}
			} else if (prop instanceof FLong) {
				if (prop.isValueNull()) {
					builder.appendKey(fieldName);
					builder.appendNullValue();
				} else {
					builder.appendKeyValue(fieldName, prop.getLong());
				}				
			} else if (prop instanceof FDouble) {
				if (prop.isValueNull()) {
					builder.appendKey(fieldName);
					builder.appendNullValue();
				} else {
					builder.appendKeyValue(fieldName, prop.getDouble());
				}
			} else if (prop instanceof FBoolean) {
				if (prop.isValueNull()) {
					builder.appendKey(fieldName);
					builder.appendNullValue();
				} else {
					builder.appendKeyValue(fieldName, ((FBoolean) prop).getBoolean());
				}

			} else if (prop instanceof FDate) {
				if (prop.isValueNull()) {
					builder.appendKey(fieldName);
					builder.appendNullValue();
				} else {
					String valStr = prop.getString();
					builder.appendKeyValue(fieldName, valStr);
				}
			} else if (prop instanceof FTime) {
				if (prop.isValueNull()) {
					builder.appendKey(fieldName);
					builder.appendNullValue();
				} else {
					String valStr = prop.getString();
					builder.appendKeyValue(fieldName, valStr);
				}				
			} else if (prop instanceof FList) {
				FocList list = ((FList) prop).getList();
				builder.appendKey(fieldName);
				list.toJson(builder);
			} else if (prop instanceof FString) {
				if (prop.isValueNull()) {
					builder.appendKey(fieldName);
					builder.appendNullValue();
				} else {
					String valStr = prop.getString();
					builder.appendKeyValue(fieldName, valStr);
				}
			} else {
				String valStr = prop.getString();
				builder.appendKeyValue(fieldName, valStr);
			}
		}
	}
	
	public void toJsonInternal(B01JsonBuilder builder){
		if(builder != null){
			builder.beginObject();
			if(builder.isPrintCRUD()) {
				String statusValue = null;
				if(isDeleted()) {
					statusValue = "D";
				} else if(isCreated()) {
					statusValue = "C";
				} else if(isModified()) {
					statusValue = "U";
				}
				if(statusValue != null) {
					builder.appendKeyValue("CRUD", statusValue);
				}
			}
			FReference fRef = getReference();
			if(fRef != null && builder.isPrintRootRef() && !isCreated()){//If created the REF<0 means nothing
				builder.appendKeyValue(fRef.getFocField().getName(), fRef.getInteger());
			}
			FocFieldEnum fieldEnum = new FocFieldEnum(getThisFocDesc(), this, FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
			while(fieldEnum != null && fieldEnum.hasNext()){
				FField fld = fieldEnum.nextField();
				if(			fld.getID() != FField.REF_FIELD_ID 
						&& (!builder.isHideWorkflowFields() || !isFieldWorkflowField(fld))
						&& (!builder.isHideCreationUser() || !isFieldCreationField(fld.getName()))
						&& (fld.getID() != FField.FLD_ORDER || builder.isPrintOrderField())
						&& (fld.getID() != FField.FLD_DEPRECATED_FIELD || builder.isPrintDepricatedField())
						&& builder.includeField(getThisFocDesc().getStorageName(), fld.getName())
						){
					FProperty prop = fieldEnum.getProperty();
					if(prop != null && (!builder.isModifiedOnly() || prop.isModifiedFlag())){
						appendKeyValue_ForProperty(builder, fld.getName(), prop);
					}
				}
			}
			fieldEnum.dispose();
			fieldEnum = null;
				
			if(builder.isScanSubList()) {
				builder.pushMasterObject(this.buildJsonKey());
				
				fieldEnum = new FocFieldEnum(getThisFocDesc(), this, FocFieldEnum.CAT_LIST, FocFieldEnum.LEVEL_PLAIN);
				while(fieldEnum != null && fieldEnum.hasNext()){
					FField fld = fieldEnum.nextField();
					if(		 fld.getID() != FField.REF_FIELD_ID
							&& builder.includeField(getThisFocDesc().getStorageName(), fld.getName())						
							){
						FList prop = (FList) fieldEnum.getProperty();
						FocList list = prop != null ? prop.getListWithoutLoad() : null;
						if(list != null && list.getFocDesc() != null && !(list.getFocDesc() instanceof WFLogDesc)) {
							if(!builder.isModifiedOnly()) list.loadIfNotLoadedFromDB();
							
							boolean isCRUD    = builder.isPrintCRUD();
							boolean isRootRef = builder.isPrintRootRef();
							boolean listStarted = false;
							
							Iterator iter = list.newSubjectIterator(); 
							while(iter != null && iter.hasNext()) {
								AccessSubject subject = (AccessSubject) iter.next();
								if(subject != null && subject instanceof FocObject) {
									FocObject focObj = (FocObject) subject;
									
	//									if(			focObj != null 
	//											&& (!builder.isModifiedOnly() 
	//													|| (    focObj.isModified() 
	//															||  focObj.isDeleted()
	//															|| (focObj.isCreated() && !focObj.isEmpty()) 
	//													)))
									
									boolean doWrite = focObj != null;
									if(doWrite && builder.isModifiedOnly()) {
										doWrite =     focObj.isModified() 
															||  focObj.isDeleted()
															|| (focObj.isCreated() && !focObj.isEmpty());
									} else if(doWrite && !builder.isModifiedOnly()) {
										doWrite = !(focObj.isCreated() && focObj.isEmpty());	
									}
									
									if(doWrite) {
										if(!listStarted) {
											builder.appendKey(fld.getName());
											builder.beginList();
											listStarted = true;
										}
										if(builder.isModifiedOnly()) {
											builder.setPrintCRUD(true);
											builder.setPrintRootRef(true);
										}
										focObj.toJson_InList(builder);
									}
								}
							}
							
							builder.setPrintCRUD(isCRUD);
							builder.setPrintRootRef(isRootRef);
							if(listStarted) {
								builder.endList();
							}
							
	//							ToJsonListIterator listIterator = new ToJsonListIterator(builder, fld.getName());
	//							list.iterate(listIterator);
	//							if(listIterator.isListStarted()) {
	//								builder.endList();
	//							}
	//							listIterator.dispose();
						}
					}
				}
				fieldEnum.dispose();
				fieldEnum = null;
			}
			
			builder.endObject();
		}
	}
	
	public void toJson_Detailed(B01JsonBuilder builder){
		toJson(builder);
	}

	public void toJson_InList(B01JsonBuilder builder){
		toJson_Detailed(builder);
	}
		
	public void toJson_Embedded(B01JsonBuilder builder){
		toJson_Detailed(builder);
	}
	
	public void toJson(B01JsonBuilder builder){
		if(builder != null){
			JSONObjectWriter writer = getThisFocDesc() != null ? builder.getJsonObjectWriter(getThisFocDesc().getStorageName()) : null;
			if(writer != null) {
				writer.writeJson(builder, this);
			} else {
				toJsonInternal(builder);
			}
		}
	}

	private class ToJsonListIterator implements IFocIterator {

		private String  fieldName = null;
		private boolean listStarted= false;
		private B01JsonBuilder builder = null;
		
		public ToJsonListIterator(B01JsonBuilder builder, String fieldName) {
			this.builder = builder;
			this.fieldName = fieldName;
		}
		
		public void dispose() {
			builder = null;
		}
		
		@Override
		public boolean treatElement(Object element) {
			FocObject focObj = ((FocListElement)element).getFocObject();
			if(focObj != null && (!builder.isModifiedOnly() || (focObj.isModified() || focObj.isCreated() || focObj.isDeleted()))) {
				if(!listStarted) {
					builder.appendKey(fieldName);
					builder.beginList();
					listStarted = true;
				}
				
				focObj.toJson(builder);
			}
			
			return false;
		}

		public boolean isListStarted() {
			return listStarted;
		}
		
	}
	
  /**
   * Gets the Property corresponding to the given Property ID stored in the
   * Item. If the Item does not contain the Property, <code>null</code> is
   * returned.
   * 
   * @param id
   *            identifier of the Property to get
   * @return the Property with the given ID or <code>null</code>
   */
  public Property getItemProperty(Object id){
//		int idInt = ((Integer)id).intValue();
//		return getFocProperty(idInt);
  	Object obj = iFocData_getDataByPath((String) id);
  	return (Property) ((obj instanceof Property) ? obj : null);
    //return getFocPropertyForPath((String) id);
  }

  /**
   * Gets the collection of IDs of all Properties stored in the Item.
   * 
   * @return unmodifiable collection containing IDs of the Properties stored
   *         the Item
   */
  public Collection<String> getItemPropertyIds(){
  	//return getThisFocDesc().vaadin_getFieldIds();
    return getThisFocDesc().vaadin_getFieldNames();
  }

  /**
   * Tries to add a new Property into the Item.
   * 
   * <p>
   * This functionality is optional.
   * </p>
   * 
   * @param id
   *            ID of the new Property
   * @param property
   *            the Property to be added and associated with the id
   * @return <code>true</code> if the operation succeeded, <code>false</code>
   *         if not
   * @throws UnsupportedOperationException
   *             if the operation is not supported.
   */
  public boolean addItemProperty(Object id, Property property) throws UnsupportedOperationException{
  	return false;
  }

  /**
   * Removes the Property identified by ID from the Item.
   * 
   * <p>
   * This functionality is optional.
   * </p>
   * 
   * @param id
   *         ID of the Property to be removed
   * @return <code>true</code> if the operation succeeded
   * @throws UnsupportedOperationException
   *             if the operation is not supported. <code>false</code> if not
   */
  public boolean removeItemProperty(Object id) throws UnsupportedOperationException{
  	return false;
  }

	/**
	 * Registers a new property set change listener for this Item.
	 * 
	 * @param listener
	 *          the new Listener to be registered.
	 */
	@Override
	public void addPropertySetChangeListener(Item.PropertySetChangeListener listener) {
		if(propertySetChangeListeners == null){
			propertySetChangeListeners = new LinkedList<PropertySetChangeListener>();
		}
		propertySetChangeListeners.add(listener);
	}

	/**
	 * @deprecated As of 7.0, replaced by
	 *             {@link #addPropertySetChangeListener(com.vaadin.data.Item.PropertySetChangeListener)}
	 **/
	@Override
	@Deprecated
	public void addListener(Item.PropertySetChangeListener listener) {
		addPropertySetChangeListener(listener);
	}

	/**
	 * Removes a previously registered property set change listener.
	 * 
	 * @param listener
	 *          the Listener to be removed.
	 */
	@Override
	public void removePropertySetChangeListener(Item.PropertySetChangeListener listener) {
		if(propertySetChangeListeners != null){
			propertySetChangeListeners.remove(listener);
		}
	}

	/**
	 * @deprecated As of 7.0, replaced by
	 *             {@link #removePropertySetChangeListener(com.vaadin.data.Item.PropertySetChangeListener)}
	 **/
	@Override
	@Deprecated
	public void removeListener(Item.PropertySetChangeListener listener) {
		removePropertySetChangeListener(listener);
	}

	/**
	 * Sends a Property set change event to all interested listeners.
	 */
	public void fireItemPropertySetChange(FProperty property) {
		if(propertySetChangeListeners != null){
			final Object[] l = propertySetChangeListeners.toArray();
			for(int i = 0; i < l.length; i++){
				((Item.PropertySetChangeListener) l[i]).itemPropertySetChange(property);
			}
		}
	}
  
  public IFocData iFocData_getDataByPath(String path){
    return getThisFocDesc().iFocData_getFieldOrPropertyByName(this, path);
  }
  
	public boolean focObject_IsLocked() {
		boolean locked = false;
		if (isSystemObject()) {
			locked = true;
		} else {			
			if (Globals.getApp() != null && Globals.getApp().getUser_ForThisSession() != null) {
				FocUser user = Globals.getApp().getUser_ForThisSession();
				if (user != null && user.getCompanyRightsList() != null && user.getCompanyRightsList().size() > 0) {
					for (int i = 0; i < user.getCompanyRightsList().size(); i++) {
						UserCompanyRights companyRight = (UserCompanyRights) user.getCompanyRightsList().getFocObject(i);
						if (companyRight != null && companyRight.getCompany() != null) {
							// Object related to Company
							if (getCompany() != null && getCompany().equals(companyRight.getCompany())) {
								if (companyRight.getAccessRight() == UserCompanyRightsDesc.ACCESS_RIGHT_NONE || companyRight.getAccessRight() == UserCompanyRightsDesc.ACCESS_RIGHT_READ_ONLY) {
									locked = true;
								}
							} else if (getCompany() == null) {
								if (companyRight.getAccessRight() == UserCompanyRightsDesc.ACCESS_RIGHT_READ_WRITE) {
									locked = false;
									break;
								}else{
									locked = true;
								}
							}
						}
					}
				}
			}
		}
		return locked;
	}

  public boolean isEditable_AtomicNoDotLookup(String dataPath){
  	boolean editable = true;
      editable = workflow_IsAllowModification(dataPath) && !focObject_IsLocked();
      if(editable){
      	
        FField fld = getThisFocDesc().getFieldByName(dataPath);
        editable = fld != null ? !fld.isAllwaysLocked() : true;
        if(editable){
          if(fld != null 
          		&& fld.isLockValueAfterCreation() 
          		&& !isCreated() 
          		&& Globals.getApp().getUser_ForThisSession() != null 
          		&& Globals.getApp().getUser_ForThisSession().getGroup() != null 
          		&& !Globals.getApp().getUser_ForThisSession().getGroup().allowNamingModif()){
            editable = false;
          }
          if(editable){
            if(dataPath.equals(StatusHolderDesc.FNAME_STATUS) && getThisFocDesc() != null && getThisFocDesc().workflow_IsWorkflowSubject()){
              editable = false;
            }else if(dataPath.equals(FField.FNAME_CODE) && getThisFocDesc() != null && getThisFocDesc().workflow_IsWorkflowSubject()){
              editable = workflow_IsAllowModification_Code();
            }else{
//	                FocUser user = FocWebApplication.getFocUser();
//	                if(user != null && user.getGroup() != null && fld != null){
//	                  editable = !fld.isLockValueAfterCreation() || !FocWebApplication.getFocUser().getGroup().allowNamingModif();
//	                }
            }
          }
        }
      }
    return editable;
  }
  
  public boolean iStatusHolder_isClosed(){
  	boolean closed = false;
  	if(this instanceof IStatusHolder){
  		closed = ((IStatusHolder)this).getStatusHolder().getStatus() == StatusHolderDesc.STATUS_CLOSED;
  	}
  	return closed;
  }

  public boolean iStatusHolder_isTransactionCanceled(){
  	int status = StatusHolderDesc.STATUS_NONE;
  	if(this instanceof IStatusHolder){
  		IStatusHolder iStatusHolder = (IStatusHolder) this;
  		StatusHolder statusHolder = iStatusHolder.getStatusHolder();
  		if(statusHolder != null){
  			status = statusHolder.getStatus();
  		}
  	}
  	return status == StatusHolderDesc.STATUS_CANCELED;
  }

  public Object computeFormula(String expression){
  	Object result = null;
  	if(expression != null && !expression.isEmpty()){
  		expression = expression.replace("'", "\"");
  		
			Formula formula = new Formula(expression);
			FocSimpleFormulaContext formulaContext = new FocSimpleFormulaContext(formula);
			result = formulaContext.compute(this);
  	}		
		return result;
  }
  
  public boolean hasAdrBookParty() {
  	return this instanceof IAdrBookParty;
  }
  
  public boolean isEmpty(){
  	boolean empty = true;
  	
  	FocFieldEnum enumer = this.newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_PLAIN);
  	while(empty && enumer != null && enumer.hasNext()){
  		enumer.nextField();
  		FProperty prop = enumer.getProperty();
  		if(prop != null){
	  		if(!(prop instanceof FReference)){
	  			if(prop instanceof FBoolean){
	  			}else if(prop.getFocField() != null && 
	  					(prop.getFocField().getID() == FField.FLD_ORDER
	  					)){
	  				//Consider as empty
//	  			}else if(prop instanceof FList){
//	  				FocList focList = ((FList)prop).getList();
//	  				empty = focList == null || focList.size() == 0;
//	  				if(!empty){
//	  					empty = true;
//	  					for(int i=0; i<focList.size() && empty; i++){
//	  						FocObject listItem = focList.getFocObject(i);
//	  						empty = listItem.isEmpty();
//	  					}
//	  				}
	  			}else{
	  				boolean checkThisProperty = true;
	  				if(prop instanceof FObject){
	  					FObjectField objField = (FObjectField) ((FObject)prop).getFocField();
	  					checkThisProperty = !objField.isCascade();
	  					if(checkThisProperty){
	  						FocObject valueObj = (FocObject) prop.getObject();
	  						if(valueObj != null && valueObj.isCreated() && valueObj.isEmpty()){
	  							checkThisProperty = false;
	  						}
	  					}
	  				}
	  				if(checkThisProperty){
	  					empty = prop.isEmpty();
	  				}
	  			}
	  		}
	  	}
  	}
 
  	enumer = this.newFocFieldEnum(FocFieldEnum.CAT_LIST, FocFieldEnum.LEVEL_PLAIN);
  	while(empty && enumer != null && enumer.hasNext()){
  		enumer.nextField();
  		FProperty prop = enumer.getProperty();
  		if(prop != null && prop instanceof FList){
				FocList focList = ((FList)prop).getList();
				if(focList.isDbResident()){
					empty = focList == null || focList.size() == 0;
					if(!empty){
						empty = true;
						for(int i=0; i<focList.size() && empty; i++){
							FocObject listItem = focList.getFocObject(i);
							empty = listItem.isEmpty();
						}
					}
				}
  		}
  	}
   	
  	return empty;
  }
  
  @Override
  public boolean validate(boolean checkValidity, boolean callFromValidationPanel){
  	boolean doNotValidate = isCreated() && isEmpty();
  	return doNotValidate ? true : super.validate(checkValidity, callFromValidationPanel);
  }
  
  //---------------------------------------- JSON Parsers ----------------------------------------//
  
  public boolean isNullAndAllowed(String val) {
		if ((Utils.isStringEmpty(val) || val.equalsIgnoreCase("null")) && ConfigInfo.isAllowNullProperties()) {
			return true;
		}
		return false;
	}
  
  public void jsonParseSlaveList_MultipleSelection(JSONObject jsonObject, String listFieldName, String fieldNameInSlave) throws Exception {
		FocList slaveList = getPropertyList(listFieldName);
		if(slaveList != null && jsonObject.has(listFieldName)) {
			FocDesc      slaveDesc   = slaveList.getFocDesc();
			FObjectField objectField = slaveDesc != null ? (FObjectField) slaveDesc.getFieldByName(fieldNameInSlave) : null;
			
			Object jsonArray = jsonObject.get(listFieldName);
			if(jsonArray != null && objectField != null) {
				FocList lookupList = objectField.getSelectionList();
				if(lookupList != null) {
					jsonParseSlaveList_MultipleSelection(lookupList, slaveList, jsonArray, fieldNameInSlave);
				}
			}
		}
  }
  
	public void jsonParseSlaveList_MultipleSelection(FocList lookupList, FocList slaveList, Object jsonObject, String fieldNameInSlave) throws Exception {
		if(slaveList != null && jsonObject != null) {
			//Prepare ToDelete Map 
			HashMap<Long, FocObject> toDelete = new HashMap<Long, FocObject>();
			for(int i=0; i<slaveList.size(); i++) {
				FocObject slaveObj = (FocObject) slaveList.getFocObject(i);
				toDelete.put(slaveObj.getReferenceInt(), slaveObj);
			}
			
			if(jsonObject instanceof String && ((String)jsonObject).equalsIgnoreCase("null")) {
				
			} else if(jsonObject instanceof JSONArray) {
				JSONArray jsonArray = (JSONArray) jsonObject;
				
				for(int i=0; i<jsonArray.length(); i++) {
					int  refTypeInt = (int)  jsonArray.get(i);
					long refType    = (long) refTypeInt;
					
					if (refType > 0) {
						FocObject type = (FocObject) lookupList.searchByRealReferenceOnly(refType);
						if (type != null) {
							FocObject slaveObj = slaveList.searchByPropertyObjectReference(fieldNameInSlave, refType);
						
							if(slaveObj == null) {
								slaveObj = slaveList.newEmptyItem();
								slaveObj.setPropertyObject(fieldNameInSlave, type);
								slaveObj.setCreated(true);
								slaveObj.validate(false);
							} else {
								toDelete.remove(slaveObj.getReferenceInt());
							}
						}
					}
				}
			}
			
			Iterator<FocObject> iter = toDelete.values().iterator();
			while(iter != null && iter.hasNext()) {
				FocObject slaveObj = iter.next();
				slaveObj.setDeleted(true);
				slaveList.remove(slaveObj);
				slaveObj.validate(false);
			}
		}
	}
  
	public void jsonParseString(JSONObject jsonObj, String fieldName) {
		try{
			String value = jsonObj.getString(fieldName);
			if (isNullAndAllowed(value)) {
				setPropertyNull_WithListener(fieldName);
			} else {
				setPropertyString(fieldName, value);
			}
		}catch (JSONException e){
			Globals.logException(e);
		}
	}
	
	public void jsonParseDATE(JSONObject jsonObj) {
		if(jsonObj.has("Date")){
			try{
				String dateString = jsonObj.getString("Date");
				if (isNullAndAllowed(dateString)) {
					setPropertyNull_WithListener(FField.FNAME_DATE);
				} else {
					SimpleDateFormat simpleFormat= new SimpleDateFormat("dd/MM/yyyy");
					java.util.Date jsonDate = simpleFormat.parse(dateString);
					setDate(new java.sql.Date(jsonDate.getTime()));
				}
			}catch (JSONException e){
				Globals.logException(e);
			}catch (ParseException e){
				Globals.logException(e);
			}
		}
	}
	
	public void jsonParseDate(JSONObject jsonObj, String fieldName) {
		if (jsonObj.has(fieldName)) {
			try {
				String dateString = jsonObj.getString(fieldName);
				if (isNullAndAllowed(dateString)) {
					setPropertyNull_WithListener(fieldName);
				} else {
					SimpleDateFormat simpleFormat = new SimpleDateFormat("dd/MM/yyyy");
					java.util.Date jsonDate = simpleFormat.parse(dateString);
					setPropertyDate(fieldName, new java.sql.Date(jsonDate.getTime()));
				}
			} catch (JSONException e) {
				Globals.logException(e);
			} catch (ParseException e) {
				Globals.logException(e);
			}
		}
	}
	
	public void jsonParseDateTime(JSONObject jsonObj, String fieldName) {
		if (jsonObj.has(fieldName)) {
			try {
				String dateString = jsonObj.getString(fieldName);
				if (isNullAndAllowed(dateString)) {
					setPropertyNull_WithListener(fieldName);
				} else {
					SimpleDateFormat simpleFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					java.util.Date jsonDate = simpleFormat.parse(dateString);
					setPropertyDate(fieldName, new java.sql.Date(jsonDate.getTime()));
				}
			} catch (JSONException e) {
				Globals.logException(e);
			} catch (ParseException e) {
				Globals.logException(e);
			}
		}
	}

	public void jsonParseTime(JSONObject jsonObj, String fieldName) {
		if (jsonObj.has(fieldName)) {
			try {
				String dateString = jsonObj.getString(fieldName);
				if (isNullAndAllowed(dateString)) {
					setPropertyNull_WithListener(fieldName);
				} else {
					setPropertyTime(fieldName, dateString);
				}
			} catch (JSONException e) {
				Globals.logException(e);
			}
		}
	}

	public void jsonParseBoolean(JSONObject jsonObj, String fieldName) {
		if (jsonObj.has(fieldName)) {
			try {
				String booleanString = jsonObj.getString(fieldName);
				if (isNullAndAllowed(booleanString)) {
					setPropertyNull_WithListener(fieldName);
				} else {
					setPropertyBoolean(fieldName, jsonObj.getBoolean(fieldName));
				}
			} catch (JSONException e) {
				Globals.logException(e);
			}
		}
	}
  
	public void jsonParseInt(JSONObject jsonObj, String fieldName) {
		if (jsonObj.has(fieldName)) {
			try {
				setPropertyInteger(fieldName, jsonObj.getInt(fieldName));
			} catch (Exception e) {
				try {
					String strValue = jsonObj.getString(fieldName);
					if (isNullAndAllowed(strValue)) {
						setPropertyNull_WithListener(fieldName);
					} else {
						strValue = Utils.convertIndianNumberstoArabic(strValue);
						int intValue = Utils.parseInteger(strValue, 0);
						setPropertyInteger(fieldName, intValue);
					}
				} catch (Exception e2) {
					Globals.logException(e2);
				}
			}
		}
	}
	
	public void jsonParseLong(JSONObject jsonObj, String fieldName) {
		if (jsonObj.has(fieldName)) {
			try {
				setPropertyLong(fieldName, jsonObj.getLong(fieldName));
			} catch (Exception e) {
				try {
					String strValue = jsonObj.getString(fieldName);
					if (isNullAndAllowed(strValue)) {
						setPropertyNull_WithListener(fieldName);
					} else {
						strValue = Utils.convertIndianNumberstoArabic(strValue);
						long intValue = Utils.parseLong(strValue, 0);
						setPropertyLong(fieldName, intValue);
					}
				} catch (Exception e2) {
					Globals.logException(e2);
				}
			}
		}
	}
	
	public void jsonParseDouble(JSONObject jsonObj, String fieldName) {
		if (jsonObj.has(fieldName)) {
			try {
				String strValue = jsonObj.getString(fieldName);
				if (isNullAndAllowed(strValue)) {
					setPropertyNull_WithListener(fieldName);
				} else {
					strValue = Utils.convertIndianNumberstoArabic(strValue);
					double doubleValue = Utils.parseDouble(strValue, 0.00);
					setPropertyDouble(fieldName, doubleValue);
				}
			} catch (JSONException e) {
				Globals.logException(e);
			}
		}
	}
  
	public void jsonParseForeignKey(JSONObject jsonObj, String fieldName) {
		if (jsonObj.has(fieldName)) {
			try {
				FObject fObj = (FObject) getFocPropertyByName(fieldName);
				if (jsonObj.isNull(fieldName) && ConfigInfo.isAllowNullProperties()) {
					setPropertyObject(fieldName, null);
				} else {
					FocList list = fObj != null ? fObj.getPropertySourceList() : null;
					if (list != null && !jsonObj.isNull(fieldName)) {
						list.loadIfNotLoadedFromDB();
						FocObject foundObj = null;
						if (jsonObj.get(fieldName) instanceof JSONObject) {
							JSONObject jsonForeignObj = (JSONObject) jsonObj.get(fieldName);
							foundObj = list.searchByRealReferenceOnly(jsonForeignObj.getInt(FField.REF_FIELD_NAME));
						} else {
							foundObj = list.searchByRealReferenceOnly(jsonObj.getInt(fieldName));
						}
						setPropertyObject(fieldName, foundObj);
					}
				}
			} catch (JSONException e) {
				Globals.logException(e);
			}
		}
	}
	
	public void jsonParseMandatory(JSONObject jsonObj, String fieldName) {
		jsonParseInternal(jsonObj, fieldName, true);
	}
	
	public void jsonParse(JSONObject jsonObj, String fieldName) {
		jsonParseInternal(jsonObj, fieldName, false);
	}
	
	private void jsonParseInternal(JSONObject jsonObj, String fieldName, boolean mandatory) {
		if(jsonObj.has(fieldName)){
			FProperty property = getFocPropertyByName(fieldName);
			if(property != null){
				FField fld = property.getFocField();
				
				if (fld != null) {
					switch (fld.getFabType()) {
					case FieldDefinition.SQL_TYPE_ID_OBJECT_FIELD:
						jsonParseForeignKey(jsonObj, fieldName);
						break;
					case FieldDefinition.SQL_TYPE_ID_DATE:
						jsonParseDate(jsonObj, fieldName);
						break;
					case FieldDefinition.SQL_TYPE_ID_DATE_TIME:
						jsonParseDateTime(jsonObj, fieldName);
						break;						
					case FieldDefinition.SQL_TYPE_ID_TIME:						
						jsonParseTime(jsonObj, fieldName);
						break;						
					case FieldDefinition.SQL_TYPE_ID_BOOLEAN:
						jsonParseBoolean(jsonObj, fieldName);
						break;										
					case FieldDefinition.SQL_TYPE_ID_INT:
					case FieldDefinition.SQL_TYPE_ID_MULTIPLE_CHOICE:
						jsonParseInt(jsonObj, fieldName);
						break;					
					case FieldDefinition.SQL_TYPE_ID_LONG:
						jsonParseLong(jsonObj, fieldName);
						break;
					case FieldDefinition.SQL_TYPE_ID_DOUBLE:
						jsonParseDouble(jsonObj, fieldName);
						break;
					case FieldDefinition.SQL_TYPE_ID_CHAR_FIELD:
					case FieldDefinition.SQL_TYPE_ID_MULTIPLE_CHOICE_STRING_BASED:			
					case FieldDefinition.SQL_TYPE_ID_EMAIL_FIELD:
					case FieldDefinition.SQL_TYPE_ID_PASSWORD:
						jsonParseString(jsonObj, fieldName);
						break;					
					}
				}
			}
		} else if (mandatory) {
			FProperty property = getFocPropertyByName(fieldName);
			if(property != null){
				FField fld = property.getFocField();
				
				if (fld != null) {
					
					if (property.isAllowNullProperties()) {

						switch (fld.getFabType()) {
						case FieldDefinition.SQL_TYPE_ID_OBJECT_FIELD:
							((FObject)property).setObject(null);
							break;
						case FieldDefinition.SQL_TYPE_ID_DATE:
						case FieldDefinition.SQL_TYPE_ID_BOOLEAN:
						case FieldDefinition.SQL_TYPE_ID_INT:
						case FieldDefinition.SQL_TYPE_ID_LONG:
						case FieldDefinition.SQL_TYPE_ID_DOUBLE:
						case FieldDefinition.SQL_TYPE_ID_CHAR_FIELD:
							property.setValueNull_AndResetIntrinsicValue(true);	
							break;
						case FieldDefinition.SQL_TYPE_ID_MULTIPLE_CHOICE_STRING_BASED:
						case FieldDefinition.SQL_TYPE_ID_EMAIL_FIELD:
							property.setString("");
							break;					
						}

					} else {
					
						switch (fld.getFabType()) {
						case FieldDefinition.SQL_TYPE_ID_OBJECT_FIELD:
							((FObject)property).setObject(null);
							break;
						case FieldDefinition.SQL_TYPE_ID_DATE:
							FDate dateProp = ((FDate)property);  
							dateProp.setDate(new Date(dateProp.getZeroReference()));
							break;
						case FieldDefinition.SQL_TYPE_ID_BOOLEAN:
							//jsonParseBoolean(jsonObj, fieldName);
							break;										
						case FieldDefinition.SQL_TYPE_ID_INT:
						case FieldDefinition.SQL_TYPE_ID_MULTIPLE_CHOICE:
							property.setInteger(0);
							break;					
						case FieldDefinition.SQL_TYPE_ID_LONG:
							property.setLong(0);
							break;
						case FieldDefinition.SQL_TYPE_ID_DOUBLE:
							property.setDouble(0);
							break;
						case FieldDefinition.SQL_TYPE_ID_CHAR_FIELD:
						case FieldDefinition.SQL_TYPE_ID_MULTIPLE_CHOICE_STRING_BASED:
						case FieldDefinition.SQL_TYPE_ID_EMAIL_FIELD:
							property.setString("");
							break;					
						}
					}
				}
			}
		}
	}
}