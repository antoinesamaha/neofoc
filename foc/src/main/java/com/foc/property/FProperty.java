/*
 * Created on 13 fevr. 2004
 */
package com.foc.property;

import java.awt.Color;
import java.awt.Component;
import java.text.Format;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JComponent;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.admin.FocGroup;
import com.foc.db.DBManager;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FField;
import com.foc.desc.field.InheritedPropertyGetter;
import com.foc.formula.Formula;
import com.foc.formula.PropertyFormulaContext;
import com.foc.gui.StaticComponent;
import com.foc.property.validators.FPropertyValidator;
import com.foc.shared.dataStore.IFocData;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.converter.Converter;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FProperty implements Cloneable, Property, IFocData, Item.PropertySetChangeEvent, Property.ValueChangeNotifier {

  private static final char FLG_VALUE_LOCKED                    =   1;
  private static final char FLG_DESACTIVATE_LISTENERS           =   2;
  private static final char FLG_LAST_MODIFIED_BY_SET_SQL_STRING =   4;
  private static final char FLG_OUTPUT                          =   8;
  private static final char FLG_MODIFIED                        =  16;
  private static final char FLG_OBJECT_VALUE_LOCALY_CONSTRUCTED =  32;//FOR FObject only
  private static final char FLG_IS_IN_DISPOSE_METHOD            =  64;//FOR FObject only
  private static final char FLG_MANUALY_EDITED                  = 128;//FOR FObject only
  private static final char FLG_BACKUP_VALUE_LOCALY_CONSTRUCTED = 256;//FOR FObject only
  private static final char FLG_VALUE_IS_NULL                   = 512;//FOR FObject only
  //private static final char FLG_HIDDEN_MINUS_SIGN               = 256;
  
  //private static final char FLG_VALUE_LOCKED = 8;
  
  private ArrayList<FPropertyListener> listeners = null;
  private FocObject focObject = null;
  //private int fieldID = 0;
  private FField    focField  = null;
  
  private char flags = 0;
  
  //private boolean desactivateListeners = false;
  //private boolean lastModifiedBySetSQLString = false;
  //private boolean valueLocked = false;
  private Color background = null;
  
	//NO VALIDATOR
  //private FPropertyValidator propertyValidator = null;
  
  //private PropertyFormulaContainer propertyFormulaContainer = null;
  private PropertyFormulaContext propertyFormulaContext = null;
  
  protected void initStateVariables(){
    focField  = null;
    listeners = null;
    focObject = null;
    //valueLocked = false;
    //desactivateListeners = false;
    //lastModifiedBySetSQLString = false;
  }
  
  public void attachToObject(FocObject focObj){
    this.focObject = focObj;
    if (focObj != null) focObj.putFocProperty(this);
  }
  
  public void init(FocObject focObj, int fieldID, boolean forPropertyArray){
    initStateVariables();
    
    if (focObj != null) {
      FocDesc focDesc = focObj.getThisFocDesc();
      if (focDesc != null) {
        if(forPropertyArray){
          //focField = focDesc.getFieldArrayByID(fieldID);          
        }else{
          focField = focDesc.getFieldByID(fieldID);
        }
      }
    }
    attachToObject(focObj);
  }
  
  public FProperty(FocObject focObj, int fieldID) {
    init(focObj, fieldID, false);
  }

  public FProperty(FocObject focObj, int fieldID, boolean forPropertyArray) {
    init(focObj, fieldID, forPropertyArray);
  }
  
  public void dispose(){
  	if(getFocField() != null && getFocField().getName() != null && getFocField().getName().equals("EXTERNAL_CODE") && getFocObject() != null && getFocObject().code_getCode() != null && getFocObject().code_getCode().equals("Q14-001")){
  		int debug = 3;
  		debug++;
  	}

  	removeFormula();
  	
    if(listeners != null){
      for(int i=0; listeners != null && i<listeners.size(); i++){
        FPropertyListener propList = listeners.get(i);
        if(propList instanceof FObject){
        	//Because FObject is a listener to the FReference of the object Value inside of it and if we dispose the ObjectValue we don't want to dispose the FObject property.
        	((FObject) propList).unplugListenerToReferencePropertyOfObjectValue();
        }else{
        	propList.dispose();
        }
      }
      if(listeners != null) listeners.clear();
      listeners = null;
    }
    
    focObject = null;
    focField  = null;
    
    background        = null;
  	//NO VALIDATOR
    //propertyValidator = null;

    
    /* NOT disposed here
    if(propertyValidator != null){
      propertyValidator.dispose();
      propertyValidator= null;      
    }
    */
  }
  
  public boolean isDummy(){
  	return false;
  }
  
  public boolean isWithFormula(){
    return propertyFormulaContext != null && propertyFormulaContext.getFormula() != null; 
  }
  
  public PropertyFormulaContext getPropertyFormulaContext(){
    return propertyFormulaContext;
  }
  
  public void setPropertyFormulaContext(PropertyFormulaContext propertyFormulaContext){
  	if(isDummy()){
  		FProperty property = getFocObject().newFocPropertiesForField(getFocField(), true);
  		if(property != null){
  			property.setPropertyFormulaContext(propertyFormulaContext);
  		}
  	}else{
	  	removeFormula();
	    this.propertyFormulaContext = propertyFormulaContext;
	    this.propertyFormulaContext.setOwner(true);
	    this.propertyFormulaContext.setPropertyLockBackUp(isValueLocked());
	    setValueLocked(true);
	    this.propertyFormulaContext.plugListeners();
  	}
  }

  public void removeFormula(){
    if(propertyFormulaContext != null){
      setValueLocked(propertyFormulaContext.getPropertyLockBackUp());
      propertyFormulaContext.unplugListeners();//NEWNEWNEW 2009-02-16
      propertyFormulaContext.dispose();
      propertyFormulaContext = null;
    }
  }
  
  public Formula getFormula(){
    return propertyFormulaContext != null ? propertyFormulaContext.getFormula() : null;
  }
  
  public FProperty getFirstAncestorCustomizedProperty(){
    FProperty firstAncestorCustomizedProperty = null;
    FocObject focObj = getFocObject();
    if( focObj != null ){
      FField field = getFocField();
      if( field != null ){
        firstAncestorCustomizedProperty = focObj.getFirstAncestorCustomizedProperty(field.getID());  
      }
    }
    return firstAncestorCustomizedProperty;
  }
  
  protected Object clone() throws CloneNotSupportedException {
    FProperty zClone = (FProperty)super.clone();
    zClone.initStateVariables();
    return zClone;
  }

  public FProperty clone(FocObject object, FField field){
    FProperty zClone = null;
    try{
      zClone = (FProperty) clone();
      zClone.setFocField(field);
      zClone.attachToObject(object);
    }catch(Exception e){
      Globals.logException(e);
    }
    return zClone;
  }    
  
  public int hashCode() {
    return getString() != null ? getString().hashCode() : 0;
  }

  public int compareTo(FProperty prop) {
  	int comp = (prop != null) ? 0 : 1;
  	if(comp == 0){
	  	String s1 = this.getString();
	  	String s2 = prop.getString();
	  	if(s1 == null && s2 == null){
	  		comp = 0;
	  	}else if(s1 != null && s2 == null){
	  		comp = 1;
	  	}else if(s1 == null && s2 != null){
	  		comp = -1;
	  	}else{
	  		comp = s1.compareTo(s2);
	  	}
	  	/*
	  	try{
		  	int i1 = Integer.valueOf(s1);
		  	int i2 = Integer.valueOf(s2);
		  	comp = i1 - i2;
	  	}catch(Exception e){
	  		comp = s1.compareTo(s2);
	  	}
	  	*/
  	}
  	
    return comp;
  }

  public boolean equals(Object obj) {
    boolean equals = false;
    if(obj != null){
    	//&& obj.getClass() == FProperty.class){
    	try{
    		equals = compareTo((FProperty) obj) == 0;
    	}catch(Exception e){
    		Globals.logString("Exception in equals : "+e.getMessage());
    	}
    }
    
    /*
    if (obj != null && obj.getClass() == FProperty.class) {
      equals = compareTo((FProperty) obj) == 0;
    }
    */
    return equals;
  }

  public FField getFocField() {
    return focField;
    /*
    FField focField = null;
    if (focObject != null) {
      FocDesc focDesc = focObject.getThisFocDesc();
      if (focDesc != null) {
        focField = focDesc.getFieldByID(fieldID);
      }
    }
    return focField;
    */
  }

  public void setFocField(FField field) {
    focField = field;
  }  
  
  public void backup() {
//    Globals.logString("Backup not implemented");
  }

  public void restore() {
//    Globals.logString("Backup not implemented");
  }

  public String getModificationLogString() {
  	return "";
  }
  
  public boolean containsListener(FPropertyListener propListener) {
  	boolean contains = false;
    if(propListener != null && listeners != null){
      contains = listeners.contains(propListener);
    }
    return contains;
  }

  public void addListener(FPropertyListener propListener) {
    if(propListener != null){
      if(listeners == null){
        listeners = new ArrayList<FPropertyListener>();
      }
      listeners.add(propListener);
    }
  }
  
  public void removeListener(FPropertyListener propListener) {
    if(listeners != null && propListener != null){
      listeners.remove(propListener);
    }
    if(listeners != null && listeners.size() == 0){
      listeners.clear();
      listeners = null;
    }
  }

  /*public boolean validateProperty(){
    boolean ok = false;
    if(propertyValidator != null){
      boolean desactivateListenersBackup = isDesactivateListeners();
      setDesactivateListeners(true);
      ok = propertyValidator.validateProperty(this);
      FField field = getFocField();
      if(field != null){
      	FPropertyValidator propertyValidatorFromField = field.getPropertyValidator();
      	if(propertyValidatorFromField != null){
      		ok = propertyValidatorFromField.validateProperty(this);
      	}
      }
      setDesactivateListeners(desactivateListenersBackup) ;
    }
    return ok;
  }*/
  
  public boolean validateProperty(){
    boolean ok = true;
    boolean desactivateListenersBackup = isDesactivateListeners();
    setDesactivateListeners(true);
    
    //NO VALIDATOR
    /*
    if(propertyValidator != null){
    	ok = propertyValidator.validateProperty(this);
    }
    */
    FField field = getFocField();
    if(field != null){
    	FPropertyValidator propertyValidatorFromField = field.getPropertyValidator();
    	if(propertyValidatorFromField != null){
    		ok = propertyValidatorFromField.validateProperty(this);
    	}
    }
    setDesactivateListeners(desactivateListenersBackup) ;
    return ok;
  }
  
  public void notifyListeners() {
  	notifyListeners(false);
  }
  
  public void notifyListeners(boolean userEditingEvent) {
  	if(userEditingEvent) setManualyEdited(true);
  	
    if(!isDesactivateListeners()){
      if(!isLastModifiedBySetSQLString()){
      	validateProperty();
      }
      if(focObject != null){
        focObject.beforePropertyModified(this);  
      }
      if (listeners != null) {
        for(int i=0; i<listeners.size(); i++){
          FPropertyListener porpListener = (FPropertyListener) listeners.get(i);
          if (porpListener != null) {
            porpListener.propertyModified(this);
          }
        }
      }
      if(focObject != null){
        focObject.afterPropertyModified(this);  
      }
      notifyFieldListeners();
      fireValueChange();
    }
    if(focObject != null && getFocField() != null && getFocField().isDBResident()){
      focObject.setModified(true);
      if(!isLastModifiedBySetSQLString()){
      	setModifiedFlag(true);
      }
    }
    setLastModifiedBySetSQLString(false);
  	if(userEditingEvent) setManualyEdited(false);
  }
  
  private void notifyFieldListeners(){
    FField field = getFocField();
    if(field != null){
      field.notifyPropertyListeners(this);
    }
  }

  public boolean hasListeners(){
  	boolean has = listeners != null && listeners.size() > 0;
  	if(!has){
	    FField field = getFocField();
	    if(field != null){
	      has = field.hasListeners();
	    }
  	}
  	return has; 
  }
  
  public void setString(String str, boolean userEditingEvent) {
  }

  public void setString(String str) {
  }

  public String getString() {
    return "";
  }

  protected void setSqlStringInternal(String str) {
    setString(str);
  }
  
  public void setSqlString(String str) {
    setDesactivateListeners(true);
    if (str == null){
    	if (!isAllowNullProperties()) {
  	    if (getProvider() == DBManager.PROVIDER_ORACLE){
  	    	str = "";
  	    }
    	}
    }
    setSqlStringInternal(str);
    setModifiedFlag(false);//2017-05-31
    setDesactivateListeners(false);
    setLastModifiedBySetSQLString(true);
  }

  public String getSqlString() {
    return getString();
  }

  public void setInteger(int iVal) {
  }

  public void setLong(long lVal){
    
  }
  
  public int getInteger() {
    return 0;
  }
  
  public long getLong() {
    return 0;
  }

  public void setDouble(double dVal) {
  }

  public double getDouble() {
    return 0.0;
  }

  public double getDoubleForDisplay(){
  	return getDouble();
  }
  
  public void setObject(Object obj) {
  }

  public Object getObject() {
    return null;
  }

  public int getDisplaySize() {
    int size = 0;
    FField field = getFocField();
    if (field != null) {
      size = field.getSize();
    }
    return size;
  }

  /**
   * @return
   */
  public FocObject getFocObject() {
    return focObject;
  }

  /**
   * @param abstract1
   */
  public void setFocObject(FocObject abstract1) {
    focObject = abstract1;
  }

  public Object getTableDisplayObject(){
  	Format format = null;
  	if(getFocField() != null){
  		format = (getFocObject() != null) ? getFocObject().getFormatForFieldID(getFocField().getID()) : null;
	  	if(format == null){
	  		format = getFocField().getFormat();
	  	}
  	}
    return getTableDisplayObject(format);
  }

  protected boolean hasNoRight(){
  	FField fld = getFocField();
  	return (fld != null && fld.isNoRights()) || getAccessRight() == FocObject.PROPERTY_RIGHT_NONE;
  }
  
  public Object getTableDisplayObject(Format format) {
  	Object obj = getObject();
  	if(hasNoRight()){ 
  		obj = FField.NO_RIGHTS_STRING;
  	}
    return obj;
  }
  
  public Object getXMLValue(){
    return getTableDisplayObject(null);
  }

  public void setTableDisplayObject(Object obj) {
    setTableDisplayObject(obj, getFocField().getFormat());
  }
  
  public void setTableDisplayObject(Object obj, Format format) {
    setObject(obj);
  }
  
  public void copy(FProperty sourceProp){
  	if(sourceProp != null){
  		this.setObject(sourceProp.getObject());
  	}
  }
  
  public boolean isObjectProperty(){
    boolean isObjProp = false;
    FField field = this.getFocField();
    if(field != null){
      isObjProp = field.isObjectContainer();
    }
    return isObjProp;
  }
  
  /**
   * @return Returns the lastModifiedBySetSQLString.
   */
  public boolean isLastModifiedBySetSQLString() {
    return (flags & FLG_LAST_MODIFIED_BY_SET_SQL_STRING) != 0;
  }
  
  public void setLastModifiedBySetSQLString(boolean lastModifiedBySetSQLString) {
  	if(getFocField() != null && getFocField().getName() != null && getFocField().getName().equals("EXTERNAL_CODE") && getFocObject() != null && getFocObject().code_getCode() != null && getFocObject().code_getCode().equals("Q14-001")){
  		int debug = 3;
  		debug++;
  	}
    if(lastModifiedBySetSQLString){
      flags = (char)(flags | FLG_LAST_MODIFIED_BY_SET_SQL_STRING);
    }else{
      flags = (char)(flags & ~FLG_LAST_MODIFIED_BY_SET_SQL_STRING);
    }
  }

  public boolean isObjectValueLocalyConstructed() {
    return (flags & FLG_OBJECT_VALUE_LOCALY_CONSTRUCTED) != 0;
  }
  
  public void setObjectValueLocalyConstructed(boolean lastModifiedBySetSQLString) {
    if(lastModifiedBySetSQLString){
      flags = (char)(flags | FLG_OBJECT_VALUE_LOCALY_CONSTRUCTED);
    }else{
      flags = (char)(flags & ~FLG_OBJECT_VALUE_LOCALY_CONSTRUCTED);
    }
  }
  
  public boolean isBackupValueLocalyConstructed() {
    return (flags & FLG_BACKUP_VALUE_LOCALY_CONSTRUCTED) != 0;
  }
  
  public void setBackupValueLocalyConstructed(boolean localyConstructed) {
    if(localyConstructed){
      flags = (char)(flags | FLG_BACKUP_VALUE_LOCALY_CONSTRUCTED);
    }else{
      flags = (char)(flags & ~FLG_BACKUP_VALUE_LOCALY_CONSTRUCTED);
    }
  }
  
  public boolean isValueNull() {
    return (flags & FLG_VALUE_IS_NULL) != 0 && isAllowNullProperties();
  }
  
  public void setValueNull_AndResetIntrinsicValue(boolean notifyListeners) {
  	if(notifyListeners) {
  		setValueNull_WithListener(true);
  	} else {
  		setValueNull(true);
  	}
  }
  
  public void setValueNull(boolean isnull) {
    if(isnull){
      flags = (char)(flags | FLG_VALUE_IS_NULL);
    }else{
      flags = (char)(flags & ~FLG_VALUE_IS_NULL);
    }
  }
  
  public void setValueNull_WithListener(boolean isnull) {
  	boolean notifyListeners = isValueNull() != isnull;
  	setValueNull(isnull);
  	if(notifyListeners) notifyListeners();
  }
  
  public boolean isOutput() {
    return ((flags & FLG_OUTPUT) != 0) || (getFocField() != null && getFocField().isOutput());
  }
  
  public void setOutput(boolean output) {
    if(output){
      flags = (char)(flags | FLG_OUTPUT);
    }else{
      flags = (char)(flags & ~FLG_OUTPUT);
    }
  }
  
  public boolean isModifiedFlag() {
    return (flags & FLG_MODIFIED) != 0;
  }
  
  public void setModifiedFlag(boolean output) {
    if(output){
      flags = (char)(flags | FLG_MODIFIED);
    }else{
      flags = (char)(flags & ~FLG_MODIFIED);
    }
  }

  public boolean isInDisposeMethod() {
    return (flags & FLG_IS_IN_DISPOSE_METHOD) != 0;
  }
  
  public void setDisposeMethod(boolean output) {
    if(output){
      flags = (char)(flags | FLG_IS_IN_DISPOSE_METHOD);
    }else{
      flags = (char)(flags & ~FLG_IS_IN_DISPOSE_METHOD);
    }
  }

  public boolean isManualyEdited() {
    return (flags & FLG_MANUALY_EDITED) != 0;
  }
  
  public void setManualyEdited(boolean output) {
    if(output){
      flags = (char)(flags | FLG_MANUALY_EDITED);
    }else{
      flags = (char)(flags & ~FLG_MANUALY_EDITED);
    }
  } 

  /*
  public boolean isHiddenMinusSign() {
    return (flags & FLG_HIDDEN_MINUS_SIGN) != 0;
  }
  
  public void setHiddenMinusSign(boolean output) {
    if(output){
      flags = (char)(flags | FLG_HIDDEN_MINUS_SIGN);
    }else{
      flags = (char)(flags & ~FLG_HIDDEN_MINUS_SIGN);
    }
  }
  */

  protected void adaptGuiComponentEnableAttribute(Component comp){
    if(comp != null && this.isValueLocked()){
      comp.setEnabled(false);
    }
  }

  public Component getGuiComponent(){
    FField field = this.getFocField();
    
    Component comp = field != null ? field.getGuiComponent(this) : null;
    if(field != null){
      FocGroup focGroup = Globals.getApp().getGroup();
      boolean groupAllowNamingModif = focGroup != null && focGroup.allowNamingModif();
      if(!groupAllowNamingModif){
        if(field.isEditableIfEmpty() && !isEmpty()){
          setValueLocked(true);
        }
      }
    }
    
    adaptGuiComponentEnableAttribute(comp);
    
    FocObject focObj = getFocObject();
    if(focObj != null){
    	FocDesc focDesc = focObj.getThisFocDesc();    	
    	if(focDesc != null && field != null){
        String name = focDesc.getFieldGuiName(field);
        comp.setName(name);
        if(ConfigInfo.isUnitDevMode()){
          JComponent jcomp = (JComponent)comp;
          StaticComponent.setComponentToolTipText(jcomp, name);
        }
    	}
    }
   
    return comp;
  }
  
  /**
   * @return Returns the valueLocked.
   */
  public boolean isValueLocked() {
  	boolean locked = (flags & FLG_VALUE_LOCKED) != 0;
  	if(!locked){
  		locked = isInherited();
  		locked = isWithFormula();
  	}
    return locked;
  }
  
  /**
   * @param valueLocked The valueLocked to set.
   */
  public void setValueLocked(boolean valueLocked) {
    if(valueLocked){
      flags = (char)(flags | FLG_VALUE_LOCKED);
    }else{
    	if(getFocField() == null || !getFocField().isAllwaysLocked()){
    		flags = (char)(flags & ~FLG_VALUE_LOCKED);
    	}
    }
  }

  /**
   * @return Returns the background.
   */
  public Color getBackground() {
    return background;
  }

  /**
   * @param backgrnd The background to set.
   */
  public void setBackground(Color backgrnd) {
  	if(isDummy()){
  		if(getFocObject() != null && getFocField() != null){
  			FProperty prop = getFocObject().newFocPropertiesForField(getFocField(), true);
  			if(prop != null){
  				prop.setBackground(backgrnd);
  			}
  		}
  	}else{
	    boolean doNotify = false;
	    if(backgrnd != null && this.background != null){
	      doNotify = backgrnd.getBlue() != this.background.getBlue() || backgrnd.getRed() != this.background.getRed() || backgrnd.getGreen() != this.background.getGreen();
	    }else{
	      doNotify = backgrnd != this.background;
	    }
	    this.background = backgrnd;
	    if(doNotify){
	      notifyListeners();
	    }
  	}
  }

  public FPropertyValidator getPropertyValidator() {
  	//NO VALIDATOR
    //return propertyValidator;
  	return null;
  }
  
  public void setPropertyValidator(FPropertyValidator propertyValidator) {
  	//NO VALIDATOR
    //this.propertyValidator = propertyValidator;
  }
  
  public FProperty getFocProperty(int fldId){
    return null;
  }  
  
  public boolean isDesactivateListeners() {
    return (flags & FLG_DESACTIVATE_LISTENERS) != 0;
  }
  
  public void setDesactivateListeners(boolean desactivateListeners) {
    if(desactivateListeners){
      flags = (char)(flags | FLG_DESACTIVATE_LISTENERS);
    }else{
      flags = (char)(flags & ~FLG_DESACTIVATE_LISTENERS);
    }
  }
  
  public boolean isEmpty(){
    return false;
  }

  public void setEmptyValue(){
  }
  
  private FBoolField getIsCustomizedField(){
  	FField field = getFocField();
  	return field != null ? field.getInheritanceField() : null;
  }
  
  public boolean isInherited() {
  	boolean isInherited = false;
  	FBoolField field = getIsCustomizedField();
  	if(field != null){
  		int id = field.getID();
  		isInherited = !getFocObject().getPropertyBoolean(id);
  	}
  	return isInherited;
  }

  public void setInherited(boolean inherited) {
  	FBoolField field = getIsCustomizedField();
  	if(field != null){
	  	int id = field.getID();
	  	getFocObject().setPropertyBoolean(id, !inherited);
  	}
  }
  
  public FProperty getInhenritanceSourceProperty(){
  	FProperty prop = this;
  	if(getFocField() != null && getFocField().isWithInheritance()){
  		InheritedPropertyGetter inhPropGetter = getFocField().getInheritedPropertyGetter();
  		prop = inhPropGetter.getInheritedProperty(getFocObject(), this);
  	}
  	return prop;
  }

  public boolean isModified() {
    return true;
  }

  public void setModified(boolean modified) {
  }

  public int getAccessRight(){
  	int access = FocObject.PROPERTY_RIGHT_READ_WRITE; 
  	if(			getFocObject() != null 
  			&&  getFocField() != null){
  		access = getFocObject().getPropertyAccessRight(getFocField().getID());
  	}
  	return access;
  }
  
	public Object vaadin_TableDisplayObject(Format format, String captionProperty){
		String str = "";
		try{
			str = (String) getTableDisplayObject();
		}catch(ClassCastException e){
			str = getString();
		}
		return str;
	}
	
  //-------------------------------
  // VAADIN Property implementation
  //-------------------------------
  
	@Override
	public Object getValue() {
		return getObject();
	}

	@Override
	public void setValue(Object newValue) throws ReadOnlyException, Converter.ConversionException {
		setObject(newValue);
	}

	@Override
	public Class<?> getType() {
		return getFocField().vaadin_getClass();
	}

	@Override
	public boolean isReadOnly() {//This is mainy useful for us in the Grid because the CustomFieldGroup relies on this method to allow diting of a property
		boolean readonly = isValueLocked();
		if(!readonly && getFocField() != null){
			readonly = getFocField().isAllwaysLocked();
			if(!readonly && getFocObject() != null){
				readonly = getFocObject().isPropertyLocked(getFocField().getID());
			}
		}
		return readonly;
	}

	@Override
	public void setReadOnly(boolean newStatus) {
	}

	@Override
	public String toString() {
		return getString();
	}

  @Override
  public boolean iFocData_isValid() {
    boolean valid = validateProperty();
    if(!valid && Globals.getApp() != null){
      Globals.getApp().getFocLogger(true).addWarning("Property Not Valid "+getFocField() != null ? getFocField().getName() : "");
    }
    return valid;
  }

  @Override
  public boolean iFocData_validate() {
    return false;
  }

  @Override
  public void iFocData_cancel() {
    restore();
  }

  @Override
  public IFocData iFocData_getDataByPath(String path) {
    return null;
  }

  @Override
  public Object iFocData_getValue() {
    return getObject();
  }

  //------------------------------------------
  // Item.PropertySetChangeEvent
  //------------------------------------------
  
	@Override
	public Item getItem() {
		return getFocObject();
	}

  //------------------------------------------
  // Property.ValueChangeNotifier
  //------------------------------------------

  /**
   * List of listeners who are interested in the value changes of the Property
   */
  private LinkedList<ValueChangeListener> valueChangeListeners = null;
	
	@Override
	public void addValueChangeListener(ValueChangeListener listener) {
	  if (valueChangeListeners == null) {
	  	valueChangeListeners = new LinkedList<ValueChangeListener>();
	  }
	  valueChangeListeners.add(listener);
	}

	@Override
	@Deprecated
	public void addListener(ValueChangeListener listener) {
		addValueChangeListener(listener);
	}

	@Override
	public void removeValueChangeListener(ValueChangeListener listener) {
    if (valueChangeListeners != null) {
      valueChangeListeners.remove(listener);
    }
	}

	@Override
	@Deprecated
	public void removeListener(ValueChangeListener listener) {
		removeValueChangeListener(listener);
	}
	
  /**
   * Sends a value change event to all registered listeners.
   */
  public void fireValueChange() {
    if (valueChangeListeners != null) {
      final Object[] l = valueChangeListeners.toArray();
      final Property.ValueChangeEvent event = new ValueChangeEvent(this);
      for (int i = 0; i < l.length; i++) {
        ((Property.ValueChangeListener) l[i]).valueChange(event);
      }
    }
  }
  
  public String getDBSourceKey(){
  	String key = null;
  	FField fld = getFocField();
  	if(fld != null){
  		key = fld.getDBSourceKey();
  	}
  	return key;
  }
  
  public int getProvider(){
  	//#PERF Use the getProvider of the FocDesc which will be optimised by caching
  	int provider = DBManager.PROVIDER_NONE;
  	FField fld = getFocField();
  	if(fld != null){
  		provider = fld.getProvider();
  	}  	
  	return provider;
  }
  
  private static class ValueChangeEvent extends java.util.EventObject implements Property.ValueChangeEvent {

		/**
		* Constructs a new value change event for this object.
		* 
		* @param source
		*            source object of the event.
		*/
		protected ValueChangeEvent(Property source) {
		  super(source);
		}

		/**
		* Gets the Property whose value has changed.
		* 
		* @return source Property of the event.
		*/
		@Override
		public Property getProperty() {
		  return (Property) getSource();
		}
  }
  //------------------------------------------
  
  public boolean isAllowNullProperties() {
  	return ConfigInfo.isAllowNullProperties();
  }
  
  public String getNullSQLValue() {
  	return "NULL";
  }
}