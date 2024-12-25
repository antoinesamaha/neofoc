/*
 * Created on 15 fevr. 2004
 */
package com.foc.property;

import java.awt.Component;
import java.text.Format;
import java.util.Iterator;

import com.foc.Globals;
import com.foc.access.AccessSubject;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocFieldEnum;
import com.foc.desc.FocObject;
import com.foc.desc.FocRef;
import com.foc.desc.field.FField;
import com.foc.desc.field.FObjectField;
import com.foc.gui.FAbstractListPanel;
import com.foc.gui.FObjectPanel;
import com.foc.gui.InternalFrame;
import com.foc.list.FocList;
import com.foc.shared.dataStore.IFocData;
import com.vaadin.data.util.converter.Converter;

/*
 * //*** SEE TOP EXPLANATION
 * The backup of FObject never worked; At least not after the SQL select. Because we backup the focObjValue and not 
 * the local REF. At that point, focObjValue is null and only REF is assigned a value
 * 
 * The restore of FObject never worked; because we were restoring the focObjValue leaving the REF as is. 
 * But REF is the true value saved in DB...
 * 
 * Till here it is true that backups do not work, restores set the focObjValue = null, but the cal to a getObject_CreateIfNeeded()
 * would simply re-fetch the focObject from the list since the REF is still there. 
 * REF is still good because the backup did not set it to zero....
 * 
 * 2018-09-28: Antoine corrected partially the bug:
 * 
 * Antoine made the restore adjust the REF to the restored Object; meaning that after restoring 
 * and setting the focObjValue to null, we now recompute the SHOULD BE REF, the one related to the focObjValue 
 * this REF is almost always = 0 since the backup doesn't work and was not fixed a that point.
 * We will now remove this line introduced by Antoine
 * 
 * The TRUE FULL Fix to come:
 * 1- Make the backup and restore both deal with the REF first. if the focObjValue backed up corresponds 
 * to the restored REF then we use it otherwize we stick to the REF and set the focObject to null waiting for 
 * the first getObject_CreateIfNeeded to be called. 
 *  
 */

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FObject extends FProperty implements FPropertyListener{
  private   FReference localReference               = null ;
  private   FocObject  focObjValue                  = null ;
  private   FocObject  backupObject                 = null ;
  private   long       backupRef                    = 0;
  /*
  private   FocList    backupLocalSourceList        = null ;
  private   FocList    localSourceList              = null ;
  */
  
  private InternalFrame     internalFrame                = null;
  
  private void init(FocObject focObj) {
    localReference = new FReference(null);
    localReference.addListener(this);
    
    this.setObject(focObj);
  }
  
  /*
  public FObject(FocObject fatherObj, int fieldID, FocObject focObj, int displayField) {
    super(fatherObj, fieldID);
    init(focObj, displayField, null);    
  }

  public FObject(FocObject fatherObj, int fieldID, FocObject focObj, int displayField, FocList localSourceList) {
    super(fatherObj, fieldID);
    init(focObj, displayField, localSourceList);    
  }
  */

  public FObject(FocObject fatherObj, int fieldID, FocObject focObj) {
    super(fatherObj, fieldID);
    init(focObj);
  }

  public void dispose(){
  	if(!isInDisposeMethod()){
	  	setDisposeMethod(true);
	    unplugListenerToReferencePropertyOfObjectValue();
	
	    super.dispose();
	
	    if(localReference != null){
	      localReference.removeListener(this);
	      localReference.dispose();
	      localReference = null;
	    }
	
	    disposeLocallyConstructedBackup(backupObject);
	    disposeLocallyConstructedObject(focObjValue);
	    setObject_Encapsulation(null);
	    
	    internalFrame         = null;
	    setDisposeMethod(false);
	    
	    backupObject = null;
	    focObjValue = null;
  	}
  }
  
  private void disposeLocallyConstructedObject(FocObject locallyConstructedObject){
    if(isObjectValueLocalyConstructed() 
    		&& locallyConstructedObject != null
    		&& locallyConstructedObject != backupObject){
      locallyConstructedObject.dispose();
      locallyConstructedObject = null;
    }
    setObjectValueLocalyConstructed(false);
  }
  
  private void disposeLocallyConstructedBackup(FocObject locallyConstructedBackup){
    if(isBackupValueLocalyConstructed() && locallyConstructedBackup != null){
      locallyConstructedBackup.dispose();
      locallyConstructedBackup = null;
    }
    setBackupValueLocalyConstructed(false);
  }
  
  public void propertyModified(FProperty property) {
//		if(property.getFocObject() != null 
//				&& property.getFocObject().getThisFocDesc() != null 
//				&& property.getFocObject().getThisFocDesc().getStorageName() != null
//				&& property.getFocObject().getThisFocDesc().getStorageName().equals("WBS")
//				&& getFocObject() != null
//				&& getFocObject().getThisFocDesc() != null
//				&& getFocObject().getThisFocDesc().getStorageName().equals("PRODUCTION_ORDER")){
//			int i=0;
//			i++;
//		}
		
  	if(property.getFocObject() == null || property.getFocField() == null){
  		//This means we are listening to the local Reference property
  		reactToLocalReferenceModification_AndNotifyListeners();
  	}else{
  		//This means we are listening to the valueObject reference property
  		boolean valueModified = copyReferenceFromObject();
      if(valueModified){
        getFocObject().setModified(true);
        setModifiedFlag(true);//2017-10-18
      }
  	}
  }

  @Override
  public boolean isLastModifiedBySetSQLString() {
  	boolean b = super.isLastModifiedBySetSQLString();
  	if(localReference != null){
  		b = localReference.isLastModifiedBySetSQLString();
  	}
  	return b;
  }
  
  @Override  
  public void setLastModifiedBySetSQLString(boolean lastModifiedBySetSQLString) {
  	if(localReference != null){
  		localReference.setLastModifiedBySetSQLString(lastModifiedBySetSQLString);
  	}
  }
    
  public String getNullValueDisplayString(){
  	String       str = null;
		FObjectField fld = (FObjectField) getFocField();
		if(fld != null){
			str = fld.getNullValueDisplayString();
		}
  	return str;
  }
  
  public boolean isEmpty(){
    return focObjValue == null && localReference.getLong() <= 0;
  }
  
  public void setEmptyValue(){
  	setObject(null);
  }
  
  public boolean isWithList(){
    return getPropertySourceList() != null;
  }
  
  private FPropertyListener getValueObjectReferenceListener(){
  	/*
    if(valueObjectReferenceListener == null){
      valueObjectReferenceListener = new FPropertyListener(){
        public void propertyModified(FProperty property) {
          boolean valueModified = copyReferenceFromObject();
          if(valueModified){
            getFocObject().setModified(true);
          }
        }

        public void dispose() {
        }
      };
    }
    return valueObjectReferenceListener;
    */
  	return this;
  }
  
  private void plugListenerToReferencePropertyOfObjectValue(){
    if(focObjValue != null && (focObjValue.isTempReference() || focObjValue.referenceNeeded())){
      FReference refProp = focObjValue.getReference();
      if(refProp != null){
        refProp.addListener(getValueObjectReferenceListener());
      }
    }
  }

  public void unplugListenerToReferencePropertyOfObjectValue(){
    if(focObjValue != null && (focObjValue.isTempReference() || focObjValue.referenceNeeded())){
      FReference refProp = focObjValue.getReference();
      if(refProp != null){
        refProp.removeListener(getValueObjectReferenceListener());
      }
    }
  }
  
  public FocDesc getFocDesc(){
    FocDesc desc = null;//focDesc;
    if(desc == null){
      FField fld = getFocField();
      if(fld != null){
      	desc = fld.getFocDesc();
      }
    }
    return desc;
  }

  /*
  public void setFocDesc(FocDesc focDesc){
    this.focDesc = focDesc;
  }
  */

  public String getLocalReferenceToString(){
    return localReference != null ? localReference.toString() : "";
  }

  public long getLocalReferenceInt(){
    return localReference != null ? localReference.getLong() : 0;
  }

  public void setLocalReferenceInt_WithoutNotification(long ref){
    if(localReference != null){
    	boolean listenersActive = localReference.isDesactivateListeners(); 
    	localReference.setDesactivateListeners(true);
    	localReference.setLong(ref);
    	localReference.setDesactivateListeners(listenersActive);
    }
  }
  
  public void setLocalReferenceInt(long ref){
    if(localReference != null){
//    	boolean listenersActive = localReference.isDesactivateListeners(); 
//    	localReference.setDesactivateListeners(true);
    	localReference.setLong(ref);
//    	localReference.setDesactivateListeners(listenersActive);
    }
  }

  private void copyReferenceIntoObject(){
    FReference ref = focObjValue != null ? focObjValue.getReference() : null;
    if(ref != null){
      long objRefInt   = ref.getLong();
      long localRefInt = localReference.getLong(); 
      if(localRefInt != objRefInt){
        ref.setReferenceWithoutNotification(localReference.getReferenceClone());
        focObjValue.setLoadedFromDB(false);
        //focObjValue.setCreated(false);//Recently added 29/09/2010
      }
    }
  }

  private boolean copyReferenceFromObject(){
    boolean valueModified = false;
    FocRef valueObjRef = null;
    
    if(focObjValue != null){
      FReference ref = focObjValue.getReference();
      if(ref != null){
        valueObjRef = ref.getReferenceClone();
      }
    }
        
    if(localReference != null){
      if((valueObjRef == null && localReference.getLong() != 0) || (valueObjRef != null && localReference.getLong() != valueObjRef.getLong())){
        valueModified = true;
      }
      localReference.setReferenceWithoutNotification(valueObjRef);
    }
    return valueModified;
  }
  
  private void getSimilarObjectFromList(){
    if(isWithList()){
      FocList focList = this.getPropertySourceList();
      //Attention
      if(!focList.isCollectionBehaviour()){
        focList.loadIfNotLoadedFromDB();
      }
      //Attention
      FocObject focObjFromList = focList.searchByReference(localReference.getLong());
      
      if(focObjFromList == null){
        if(focObjValue != null){
        	if(focList != focObjValue.getFatherSubject()){
	          focObjFromList = focList.searchByUniqueKey(focObjValue);
        	}else{
        		focObjFromList = focObjValue;
        	}
        }
      }
      
      if(focObjFromList != null && focObjFromList != focObjValue){
        setObject(focObjFromList);
      }
    }
  }
  
  /*private void getSimilarObjectFromList(){
    if(focObjValue == null || focObjValue.isDbResident()){
      if(isWithList()){
        FocList focList = this.getPropertySourceList();
        //Attention
        focList.loadIfNotLoadedFromDB();
        //Attention
        FocObject focObjFromList = focList.searchByReference(localReference.getInteger());
        if(focObjFromList != null && focObjFromList != focObjValue){
          setObject(focObjFromList);
        }
      }
    }
  }*/

  public FocObject newObjectCreatedLocally_IfNull(){
  	FocObject obj = getObject_CreateIfNeeded();
  	if(obj == null){
  		newObject();
  		obj = getObject_CreateIfNeeded();
  	}
  	return obj;
  }
  
  protected void newObject(){
  	if(getFocDesc() != null){
	    FocConstructor constr = new FocConstructor(getFocDesc(), null, getFocObject());
	    unplugListenerToReferencePropertyOfObjectValue();
	    disposeLocallyConstructedObject(focObjValue);	    
	    setObject_Encapsulation(constr.newItem());
	    setObjectValueLocalyConstructed(true);
	    localReference.setFocObject(focObjValue);
	    copyReferenceIntoObject();
	    FReference objRef = focObjValue.getReference();
	    if(objRef != null && objRef.getLong() == 0){
	      focObjValue.setCreated(true);
	      if(focObjValue.getMasterObject() != null){
	        focObjValue.getMasterObject().setModified(true);
	        setModifiedFlag(true);
	        //focObjValue.getMasterObject().getMasterObject().setModified(true);
	      }
	      //getFocObject().setModified(true);
	    }
	    plugListenerToReferencePropertyOfObjectValue();
  	}
  }    

  //This function was created in the specific case of stock accounts
  public FocObject newObject_External(){
  	getObject_CreateIfNeeded();
  	if(focObjValue == null){
  		newObject();
  	}
  	return focObjValue;
  }
  
  protected FocObject getAnyObjectBecauseCannotRemainEmpty(){
    FocList focList = this.getPropertySourceList();
    FocObject obj = focList.getAnyItem();
  	return obj;
  }
  
  protected FocObject getFocObjValue(boolean createIfNeeded){
  	if(createIfNeeded && focObjValue == null){
      //I realy create only if we are not in ALLOW_NULL_VALUE or if the localReference != 0  
      boolean allowNullVal = getNullValueMode() == FObjectField.NULL_VALUE_ALLOWED || getNullValueMode() == FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN;
      if(!allowNullVal || (localReference != null && localReference.getLong() != 0)){
        getSimilarObjectFromList();
        if(focObjValue == null && isWithList() && !allowNullVal){
        	//BDebug
        	getSimilarObjectFromList();
        	//EDebug
          FocObject obj = getAnyObjectBecauseCannotRemainEmpty();
          if(obj != null){
            setObject(obj);
          }
        }
        if(focObjValue == null /*&& !allowNullVal*/){//Antoine 2015-05-20 Added the && !allowNullVal because when the DERIVED_PO had a value that was deleted we still got the code!!
          newObject();
        }
      }
    }
    return focObjValue;
  }

  public void reactToLocalReferenceModification_AndNotifyListeners(){
    reactToLocalReferenceModification();
    notifyListeners();
  }
  
  private void reactToLocalReferenceModification(){
    if(focObjValue != null){
      focObjValue.getReference();
      /*if(fRef != null && fRef.getInteger() != localReference.getInteger()){*/
        if(isWithList()){
          getSimilarObjectFromList();
        }else{
          copyReferenceIntoObject();
        }
      //}
    }
  }
  
  public FocObject getObject_CreateIfNeeded(){
    return getFocObjValue(true);
  }
  
  public FocObject getFocObjValue(){
    return focObjValue;
  }
  
  public int getDisplayField(){
    FObjectField objFld = (FObjectField) getFocField();
    return objFld != null ? objFld.getDisplayField() : FField.NO_FIELD_ID;
  }
  
  public int hashCode() {
    return focObjValue != null ? focObjValue.hashCode() : 0;
  }

  public int compareTo(FProperty prop) {
    int compare = 1;
    if(prop != null && localReference != null){
      try{
      	FObject   objProp             = (FObject) prop;
      	FocObject otherFocObjectValue = (FocObject) objProp.getObject();
      	long thisRef  = focObjValue != null && focObjValue.getReference() != null ? focObjValue.getReference().getLong() : localReference.getLong(); 
      	long otherRef = otherFocObjectValue != null && otherFocObjectValue.getReference() != null ? otherFocObjectValue.getReference().getLong() : objProp.getLocalReferenceInt();
      	long diff = thisRef - otherRef;
      	compare = diff == 0 ? 0 : (diff > 0 ? 1 : -1) ;
      		
      	/*
        String thisTableDisp = (String) this.getTableDisplayObject(null);
        String otherTableDisp = (String) prop.getTableDisplayObject(null);
        compare = (thisTableDisp != null && otherTableDisp != null) ? thisTableDisp.compareTo(otherTableDisp) : 1;
        */
      }catch(Exception e){
        this.getTableDisplayObject(null);
        Globals.logException(e);
      }
    }
    return compare;
  }

  public void popupSelectionPanel(FocList propertySourceList) {
    FObjectField objectField = (FObjectField) this.getFocField();

    if (objectField != null) {      

      if (propertySourceList != null) {
        boolean createInternalFrame = true;
        
        if(internalFrame != null){
          createInternalFrame = !Globals.getDisplayManager().restoreInternalFrame(internalFrame);
        }
        if(createInternalFrame){
          propertySourceList.setSelectionProperty(this);
          
          FAbstractListPanel selPanel = null;
          if(objectField.getSelectionPanelViewID() != FObjectField.VIEW_NONE){
          	selPanel = propertySourceList.getSelectionPanel(false, objectField.getSelectionPanelViewID());	
          }else{
          	selPanel = propertySourceList.getSelectionPanel(false);
          }
          
          if(objectField.getFilterExpression_FieldPath() != null){
          	FProperty prop = objectField.getFilterExpression_FieldPath().getPropertyFromObject(getFocObject());
          	if(prop != null){
	          	String expression = prop.getString();
	          	if(expression != null){
		          	selPanel.getFilterExpressionPanel().getExpressionField().setText(expression);
		          	selPanel.getFilterExpressionPanel().executeFind();
	          	}
          	}
          }
          
          /*          
          FValidationPanel validPanel = selPanel.getValidationPanel();
          if(validPanel != null){
            validPanel.addSubject(propertySourceList);
            validPanel.setValidationType(FValidationPanel.VALIDATION_OK_CANCEL);
          }
          */
          //internalFrame = selPanel.popup(getObject_CreateIfNeeded());
          if(objectField.isNoDecoration()) selPanel.setLightWeight(true);
          selPanel.popup(getObject_CreateIfNeeded(), true);
        }
        /*
        InternalListListener spl = getInternalListListener();
        selPanel.getFocList().addFocListener(spl);
        */
      }
    }
  }
  
  public void popupSelectionPanel() {
    popupSelectionPanel(this.getPropertySourceList());
  }

  public String getString() {
    String str = "";
    boolean valueFound = false;

    getObject_CreateIfNeeded();
    
    if (focObjValue != null) {
      FProperty displayProperty = focObjValue.getFocProperty(getDisplayField());
      if (displayProperty != null) {
        str = displayProperty.getString();
        valueFound = true;
      }
    }
    if (!valueFound) {
      FocDesc focDesc = focObjValue != null ? focObjValue.getThisFocDesc() : null;
      if(focDesc != null){
        Iterator fieldsEnum = focDesc.newFocFieldEnum(FocFieldEnum.CAT_KEY, FocFieldEnum.LEVEL_PLAIN);
        while (fieldsEnum.hasNext()) {
          FField keyField = (FField) fieldsEnum.next();
          FProperty objProp = focObjValue.getFocProperty(keyField.getID());
          str += objProp != null ? objProp.getString() : "";
        }
      }
    }
    return str;
  }

  public void setString(String str) {
  	FObjectField objFld = (FObjectField) getFocField();
  	if(objFld.getDisplayField() != FField.NO_FIELD_ID && objFld.isWithList()){
  		FocList list = objFld.getSelectionList();
  		if(list != null){
  			FocObject obj = list.searchByPropertyStringValue(objFld.getDisplayField(), str);
  			if(obj != null){
  				setObject(obj);
  			}
  		}
  	}
  }

  public void setInteger(int iVal) {

  }

  public int getInteger() {
    return 0;
  }

  public void setDouble(double dVal) {
  }

  public double getDouble() {
    return 0;
  }

  public void setObjectToNullWithoutLocalReferenceModification() {
    unplugListenerToReferencePropertyOfObjectValue();
    disposeLocallyConstructedObject(focObjValue);
    setObject_Encapsulation(null);
    localReference.setFocObject(null);
  }
  
  public void setObject_WithoutListeners(Object obj) {
  	disposeLocallyConstructedObject(focObjValue);
  	setObject_Encapsulation((FocObject) obj);
  	long ref = focObjValue != null ? focObjValue.getReference().getLong() : 0;
  	setLocalReferenceInt_WithoutNotification(ref);
  }
  
  public String getSelectionFilterExpression() {
    String       filterExpression = null;
    FocObject    focObject = getFocObject();
    FObjectField objField = (FObjectField) getFocField();
    if(objField != null){
      filterExpression = focObject != null ? focObject.getSelectionFilterExpressionFor_ObjectProperty(objField.getID()) : null;
      if(filterExpression == null){
        filterExpression = objField.getSelectionFilterExpression();
      }
    }
    return filterExpression;
  }
  
  public String getSelectionFilter_PropertyDataPath() {
    String       dataPath = null;
    FocObject    focObject = getFocObject();
    FObjectField objField = (FObjectField) getFocField();
    if(objField != null){
    	dataPath = focObject != null ? focObject.getSelectionFilter_PropertyDataPath_ForObjectProperty(objField.getID()) : null;
    	if(dataPath == null){
    		dataPath = objField.getSelectionFilter_PropertyDataPath();
    	}
    }
    return dataPath;
  }
  
  public Object getSelectionFilter_PropertyValue() {
    Object       value = null;
    FocObject    focObject = getFocObject();
    FObjectField objField = (FObjectField) getFocField();
    if(objField != null){
    	value = focObject != null ? focObject.getSelectionFilter_PropertyValue_ForObjectProperty(objField.getID()) : null;
    	if(value == null){
    		value = objField.getSelectionFilter_PropertyValue();
    	}
    }
    return value;
  }
  
  private void setObject_Internal(Object obj, boolean userEditingEvent) {
  	/*
  	obj : is the new object to be set
  	focObjValue : previous object already in property
  	localReference : is a Reference property that contains the ref of the object even if this one is null because not loaded

		focObjValue and localReference should be kept synchronized as long as we do not load uselessly focObjValue,
		this is ensured by:
		1- Listener to the Reference property of the focObjValue, so when it changes, we change the localReference
		2- If the object focObjValue itself changes, we copy its reference to localReference and redirect the listener (1) to the new reference property of the focObjValue 
  	
    setObject cases:
    
    A- General case : focObjValue != null and has a reference != 0 and obj also.
    unplug the listener to the previous object reference property
    set the focObjValue value
    set the localReference
    plug the listener to the new objeect reference
    
    notify listeners.

  	  
  	*/
  	FocObject objectBackedUpLocally = isObjectValueLocalyConstructed() ? focObjValue : null;
    if(focObjValue != obj || (focObjValue == null && obj == null && localReference != null && localReference.getLong() != 0)){
      //The second part of the condition is used for setting the property Object value to null if
      //it has a reference even if it is not loaded into memory yet.(in database but not in memory)
      FocObject newFocObj = (FocObject) obj;
    	boolean objectModifiedButReferenceNotModified = false;
  		if(focObjValue == null){
  			objectModifiedButReferenceNotModified = newFocObj != null && (getFocDesc() == null || !getFocDesc().getWithReference() || newFocObj.getReference() == null || newFocObj.getReference().getLong() == 0);
  		}else if(focObjValue.isCreated()){
  			objectModifiedButReferenceNotModified = newFocObj == null || newFocObj.getReference().getLong() == 0;
  		}
    	//}
    	unplugListenerToReferencePropertyOfObjectValue();
    	setObject_Encapsulation((FocObject) obj);
      localReference.setFocObject(focObjValue);
      plugListenerToReferencePropertyOfObjectValue();
      
      boolean refModified = copyReferenceFromObject();
      //Here maybe I need to listen to the Object reference modification
      getSimilarObjectFromList();//Only if needed
      if(refModified || objectModifiedButReferenceNotModified){
      	notifyListeners(userEditingEvent);
      }
      //setInherited(false);
      
      disposeLocallyConstructedObject(objectBackedUpLocally);
    }
  }

  protected void setObject_Encapsulation(FocObject obj){
  	focObjValue = obj;
  }
  
  public void setObject(Object obj) {
  	setObject_Internal(obj, false);
  }
  
  public void setObject_UserEditingEvent(Object obj) {
  	setObject_Internal(obj, true);
  }
  
  public Object getObject() {
    return (Object) focObjValue;
  }

  public void backup() {
  	if(backupObject != focObjValue) {
  		disposeLocallyConstructedBackup(backupObject);
  	}
  	//if(focObjValue == null) getFocObjValue(true); 
    backupObject = focObjValue;
    //We only fill the backupRef if the backupObject was null
    backupRef = 0;
    if(backupObject == null) {
  		if(localReference != null && localReference.getLong() != 0) {
  			backupRef = localReference.getLong();
  		} 
  	}
    //-------------------------------------------------------
    setBackupValueLocalyConstructed(isObjectValueLocalyConstructed());
    //backupLocalSourceList = localSourceList;
  }

  public void restore() {
    unplugListenerToReferencePropertyOfObjectValue();
    if(backupObject != focObjValue) disposeLocallyConstructedObject(focObjValue);
    setObject_Encapsulation(backupObject);
  	setObjectValueLocalyConstructed(isBackupValueLocalyConstructed());
    if(localReference != null){
    	localReference.setFocObject(focObjValue);
    }
    //*** SEE TOP EXPLANATION
    //copyReferenceFromObject();
    plugListenerToReferencePropertyOfObjectValue();
    //localSourceList = backupLocalSourceList;
  }

  public String getModificationLogString() {
  	StringBuffer str = new StringBuffer(); 
  	if(backupObject != null) {
  		str.append(backupObject.getDisplayTitle());
  		str.append(" [");
  		str.append(backupObject.getReferenceInt());
  		str.append("]");
  	} else if (backupRef > 0) {
  		FocObject localbakupObject = null; 
      FocList focSourceList = getPropertySourceList();
      if(focSourceList != null){
      	localbakupObject = focSourceList.searchByReference(backupRef);
      }
      if (localbakupObject != null) {
      	str.append(localbakupObject.getDisplayTitle());
      } else {
      	str.append("...");
      }
  		str.append(" [");
  		str.append(backupRef);
  		str.append("]");  		
  	}
  	str.append(" -> ");
  	FocObject value = getObject_CreateIfNeeded();
  	if(value != null) {
  		str.append(value.getDisplayTitle());
  		str.append(" [");
  		str.append(value.getReferenceInt());
  		str.append("]");
  	}
  	return str.toString();
  }
  
  /**
   * @return
   */
  public FocList getPropertySourceList(){
    FocList propertySourceList = null;//this.localSourceList;
    if(propertySourceList == null){
      FObjectField objectField = (FObjectField) getFocField();
      if (objectField != null) {
      	if(objectField.getID() == FField.FLD_FATHER_NODE_FIELD_ID){
      		AccessSubject accessSubjectFather = getFocObject() != null ? getFocObject().getFatherSubject() : null;
      		if(accessSubjectFather != null && accessSubjectFather instanceof FocList){
      			propertySourceList = (FocList) accessSubjectFather;
      			//this.localSourceList = propertySourceList; 
      		}
      	}else{
         	if(getFocObject() != null && getFocField() != null){
        		propertySourceList = getFocObject().getObjectPropertySelectionList(getFocField().getID());
        	}
      		if(propertySourceList == null){
      			propertySourceList = objectField.getSelectionList();
      		}
      	}
      }
    }
    return propertySourceList;
  }

  public Object getTableDisplayObject(Format format) {
    //Object displaObj = this;
  	Object displaObj = null;
  	
    if(focObjValue == null){
    	getObject_CreateIfNeeded();
    }
    
    if(focObjValue == null){
      if(getNullValueMode() == FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN){
        displaObj = getNullValueDisplayString();
      }
      FObjectField objField = (FObjectField)getFocField();
      if(objField != null && !objField.isDisplayNullValues()){
        displaObj = "";
      }
    }else{
      FProperty displayProperty = focObjValue.getFocProperty(getDisplayField());
      if (displayProperty != null) {
        displaObj = displayProperty.getTableDisplayObject(null);
      }
    }
    
    return displaObj;
  }
  
  public void setTableDisplayObject(Object obj, Format format) {
    FocList list = this.getPropertySourceList();

    String strObj = (String)obj;
    
    if(strObj != null){
      //if(strObj.compareTo(FGObjectComboBox.NONE_CHOICE) == 0){
    	if(strObj.compareTo(getNullValueDisplayString()) == 0){
        setObject_UserEditingEvent(null);
      }else if (list != null) {
        for (int i = 0; i < list.size(); i++) {
          FocObject focObj = (FocObject) list.getFocObject(i);
          if (focObj != null) {
            FProperty displayProperty = focObj.getFocProperty(getDisplayField());
            
            if (displayProperty != null && displayProperty.getString().compareTo((String) obj) == 0) {
              setObject_UserEditingEvent(focObj);
              break;
            }
          }
        }
      }
    }
  }

  /*
  public void setLocalSourceList(FocList localSourceList) {
    this.localSourceList = localSourceList;
  }

  public FocList getLocalSourceList() {
    return this.localSourceList;
  }
  */

  public boolean isObjectProperty(){
    return true;
  }
  
  public void copy(FProperty sourceProp){
    FObject sourceObjProp = (FObject)sourceProp;
    if(sourceObjProp != null){
    	long sourceReference = sourceObjProp.getLocalReferenceInt();
    	 
    	FocList objList = null; 
    			
    	boolean doASearchToCopyFromListToList = sourceReference != 0;
    	if(doASearchToCopyFromListToList){
    		objList = getPropertySourceList();
    		doASearchToCopyFromListToList = (objList != null && objList != sourceObjProp.getPropertySourceList());
    	}
	    
	    if(doASearchToCopyFromListToList){
	      if(objList != null){
	        FocObject tarObj = objList.searchByUniqueKey((FocObject)sourceObjProp.getObject_CreateIfNeeded());
	        if(tarObj != null){
	          setObject(tarObj);
	        }
	      }
	    }else{
	    	setLocalReferenceInt_WithoutNotification(sourceObjProp.getLocalReferenceInt());
	    	if(!sourceObjProp.isObjectValueLocalyConstructed()){
	    		FocObject srcObject = (FocObject) sourceObjProp.getObject();
	    		if(srcObject != null){
	    			this.setObject(srcObject);
	    		}
	    	}
	    }
    }
  }
  
  public FProperty getFocProperty(int fldId){
    FProperty prop = null;
    
    if(fldId == FField.REF_FIELD_ID){
      prop = localReference;
      /*
      FocObject focObj = (FocObject) getObject();
      FReference fRef = focObj != null ? focObj.getReference() : null;
      if(fRef != null && prop.getInteger() != fRef.getInteger() && fRef.getInteger() != 0){
        prop.copy(fRef);
      }
      */
    }else{
      FocObject obj = getObject_CreateIfNeeded();
      if(obj != null){
        prop = obj.getFocProperty(fldId);
      }
    }

    return prop;
  }
  /*
  public static class InternalListListener implements FocListener {
    public void focActionPerformed(FocEvent event) {
      if (event.getID() == FocEvent.ID_ITEM_SELECT) {
        FocList list = (FocList) event.getSource();
        FocObject selectedObj = list.getSelectedObject();
        //setObject(selectedObj);
      }
    }
  }

  private static InternalListListener internalListListener = null;
  
  public static InternalListListener getInternalListListener(){
    if(internalListListener == null){
      internalListListener = new InternalListListener(); 
    }
    return internalListListener;
  }
  */
  
  public int getNullValueMode() {
    int b = FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN;
    FObjectField objFld = (FObjectField) getFocField();
    if(objFld != null){
      b = objFld.getNullValueMode();
    }
    return b;
  }
  
  @Override
  protected void adaptGuiComponentEnableAttribute(Component comp){
  	if(comp instanceof FObjectPanel){
      FObjectPanel objPanel = (FObjectPanel) comp;
     	objPanel.setSelectButtonVisible(!isValueLocked());
  	}else{
  		super.adaptGuiComponentEnableAttribute(comp);
  	}
  }
  
  public Component getGuiComponent_Panel(){
    FObjectField field    = (FObjectField)this.getFocField();
    FObjectPanel objPanel = (FObjectPanel) (field != null ? field.getGuiComponent_Panel(this) : null);
    adaptGuiComponentEnableAttribute(objPanel);
    return objPanel;
  }

  public Component getGuiComponent_ComboBox(){
    FObjectField field = (FObjectField)this.getFocField();
    Component comp = field != null ? field.getGuiComponent_ComboBox(this) : null;
    adaptGuiComponentEnableAttribute(comp);
    return comp;
  }

  public Component getGuiComponent_MultiColumnComboBox(FProperty prop){
    FObjectField field = (FObjectField)this.getFocField();
    Component comp = field != null ? field.getGuiComponent_MultiColumnComboBox(this) : null;
    adaptGuiComponentEnableAttribute(comp);
    return comp;
  }

  public FocObject getBackup_Object(){
  	return backupObject;
  }
  
	public Object vaadin_TableDisplayObject(Format format, String captionProperty){
		String str = "";
		if(captionProperty != null){
			FocObject focObj = getObject_CreateIfNeeded();
			if(focObj != null){
				FProperty prop = focObj.getFocPropertyForPath(captionProperty);
				if(prop != null){
					str = (String) prop.vaadin_TableDisplayObject(format, null);
				}
			}
		}else{
			str = (String) super.vaadin_TableDisplayObject(format, captionProperty);
		}
		return str;
	}

  //-------------------------------
  // VAADIN Property implementation
  //-------------------------------

  @Override
  public Object getValue() {
    return localReference != null ? localReference.getLong() : 0;
  }  
  
  @Override
  public void setValue(Object newValue) throws ReadOnlyException, Converter.ConversionException {
    if(newValue instanceof Long || newValue instanceof Integer){
    	if(localReference != null){
    		long newValueInt = 0;
    		if(newValue instanceof Long){
    			newValueInt = ((Long)newValue).longValue();
    		}else{
    			newValueInt = ((Integer)newValue).intValue();
    		}
    		if(newValueInt != localReference.getLong()){
    			localReference.setLong(newValueInt);
    			reactToLocalReferenceModification_AndNotifyListeners();
    		}
    	}else{
    		Globals.logString("Local reference is null in FObject");
    	}
    }else{
      setObject(newValue);
    }
    //setObject(newValue);
  }

  //-------------------------------
  
  @Override
  public IFocData iFocData_getDataByPath(String path) {
    IFocData focData = null;
    
    FocObject value = getObject_CreateIfNeeded();
    if(value != null){
      focData = value.iFocData_getDataByPath(path);
    }else{
      focData = getFocDesc() != null ? getFocDesc().iFocData_getDataByPath(path) : null;
    }
    
    return focData;
  }

  @Override
  protected void setSqlStringInternal(String str) {
    // TODO Auto-generated method stub
    super.setSqlStringInternal(str);
  }
  
  @Override
  public void setModifiedFlag(boolean output) {
  	if(    getFocField() != null 
  			&& getFocField().getName() != null
  			&& getFocField().getName().equals("Address")
  			&& getFocField().getFocDescParent() != null
  			&& getFocField().getFocDescParent().getStorageName() != null
  			&& getFocField().getFocDescParent().getStorageName().equals("MahdarIncidenType")
  			){
  		int debug = 3;
  		debug++;
  	}
  	super.setModifiedFlag(output);
  }
}