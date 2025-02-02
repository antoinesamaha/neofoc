/*
 * Created on Sep 9, 2005
 */
package com.foc.list.filter;

import com.foc.db.DBManager;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldPath;
import com.foc.gui.FPanel;
import com.foc.util.Utils;

/**
 * @author 01Barmaja
 */
public abstract class FilterCondition {
  private FilterDesc filterDesc   = null;
  private FFieldPath fieldPath    = null;
  private String     fieldPrefix  = null;
  private int        firstFieldID = 0;
  private String     fieldLabel   = null;
  private boolean    display      = true;
  private int        level        = FocListFilter.LEVEL_DATABASE;
  
  public abstract int          fillDesc(FocDesc focDesc, int startID);
  public abstract void         fillProperties(FocObject focFatherObject);
	public abstract void         copyCondition(FocObject tarObject, FocObject srcObject, FilterCondition srcCondition);
  public abstract boolean      includeObject(FocListFilter filter, FocObject object);
  public abstract StringBuffer buildSQLWhere(FocListFilter filter, String fieldName);
  public abstract GuiSpace     putInPanel(FocListFilter filter, FPanel panel, int x, int y);
  public abstract boolean      isValueLocked(FocListFilter filter);
  public abstract void         resetToDefaultValue(FocListFilter filter);
  public abstract void         forceFocObjectToConditionValueIfNeeded(FocListFilter filter, FocObject focObject);
  public abstract String       buildDescriptionText(FocListFilter filter);
  
  public FilterCondition(FFieldPath filterFieldPath, String filterFieldPrefix){
    this.fieldPath = filterFieldPath;
    this.fieldPrefix = filterFieldPrefix;
  }
  
  /**
   * This constructor is very useful in 90% of the cases where we are filtering on a field which is directly in the same FocObject 
   * level. Meaning that the field path would be anyway constructed of one level. 
   * In this case we will also take the field Name as Filter field prefix, which is practical.  
   *
   * @param fieldID
   * 
   */
  public FilterCondition(int fieldID){//This is very useful in 90% of the cases 
    this.fieldPath = FFieldPath.newFieldPath(fieldID);
  }
  
  public void dispose() {
    filterDesc = null;
    fieldPath  = null;
  }
  
  public String getDbSourceKey(){
  	return filterDesc != null ? filterDesc.getDbSourceKey() : null;
  }

  public int getProvider(){
  	return filterDesc != null ? filterDesc.getProvider() : null;
  }

  public void copyProperty(FocObject tarObj, int tarFieldID, FocObject srcObj, int srcFieldID){
  	tarObj.getFocProperty(tarFieldID).copy(srcObj.getFocProperty(srcFieldID));
  }

  public void copyProperty(FocObject tarObj, FocObject srcObj, int fieldID){
  	copyProperty(tarObj, fieldID, srcObj, fieldID);
  }
  
	public void copyCondition(FocObject tarObject, FocObject srcObject) {
		copyCondition(tarObject, srcObject, this);
	}
  
  public FFieldPath getFieldPath() {
    return fieldPath;
  }
  
  public String getFieldPrefix() {
    return fieldPrefix;
  }
  
  public int getFirstFieldID() {
    return firstFieldID;
  }
  
  public void setFirstFieldID(int firstFieldID) {
    this.firstFieldID = firstFieldID;
  }
  
  public FilterDesc getFilterDesc() {
    return filterDesc;
  }
  
  public void setFilterDesc(FilterDesc filterDesc) {
    this.filterDesc = filterDesc;
    if(Utils.isStringEmpty(getFieldPrefix()) && filterDesc != null){
    	fieldPrefix = getDBFieldName();
    }
  }
  
  public String getFieldLabel(){
    String str = null;
    if(fieldLabel == null){
      FField field = getFieldPath().getFieldFromDesc(filterDesc.getSubjectFocDesc());
      str = field.getTitle();
    }else{
      str = fieldLabel;
    }
    return str;
  }

  public String getDBFieldName(){
    FField field = getFieldPath().getFieldFromDesc(filterDesc.getSubjectFocDesc());
    return field != null ? field.getName() : null;
  }

  public void setFieldLabel(String fieldLabel) {
    this.fieldLabel = fieldLabel;
  }
  
	public boolean isDisplay() {
		return display;
	}
	
	public void setDisplay(boolean display) {
		this.display = display;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public void parseString(FocListFilter filter, String str) {
		
	}
}
