/*
 * Created on Oct 14, 2004
 */
package com.foc.desc.field;

import java.awt.Component;
import java.sql.Types;

import com.fab.model.table.FieldDefinition;
import com.foc.Globals;
import com.foc.IFocEnvironment;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.ReferenceCheckerAdapter;
import com.foc.desc.ReferenceCheckerFilter;
import com.foc.gui.FGObjectComboBox;
import com.foc.gui.FObjectPanel;
import com.foc.gui.table.FTableView;
import com.foc.gui.table.cellControler.AbstractCellControler;
import com.foc.gui.table.cellControler.ComboBoxCellControler;
import com.foc.gui.table.cellControler.ObjectCellControler;
import com.foc.list.FocLink;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;
import com.foc.list.filter.FilterCondition;
import com.foc.list.filter.FocDescForFilter;
import com.foc.list.filter.ObjectCondition;
import com.foc.property.FObject;
import com.foc.property.FProperty;
import com.foc.shared.dataStore.IFocData;
import com.foc.util.Utils;

/**
 * @author 01Barmaja
 */
public class FObjectField extends FField {
  public static final String NONE_CHOICE = "";
  public static final int    VIEW_NONE   = -999;
  
  private FocDesc focDesc              = null;
  private String  focDescStorageName   = null;//This is in case of XMLDesc we give the table name and later the focDesc wil be fetched.
  private boolean cascade              = false;
  private boolean listDirectlyEditable = true;
  
  private String  keyPrefix    = null;
  private String  forcedDBName = null;//In this case we stop using the keyPrefix

  private boolean withList      = true;
  private boolean allowLoadListFromFocDesc = true;
  
  private FocList selectionList = null;
  private String  selectionFilterExpression = null;  
  private String  selectionFilter_PropertyDataPath = null;
  private Object  selectionFilter_Propertyvalue = null;
  
  private int     editorType    = SELECTION_PANEL_EDITOR;
  private String  nullValueDisplayString = NONE_CHOICE;
  //For Panel editor
  private int                     detailsPanelViewID         = FocObject.DEFAULT_VIEW_ID;
  private int                     selectionPanelViewID       = VIEW_NONE;
  private FFieldPath              filterExpression_FieldPath = null;
  
  //For Combo box editor
  private int                     comboBoxDisplayField   = -900;
  private FTableView              multiColTableView      = null;
  
  private ReferenceCheckerAdapter referenceCheckerAdater = null;
  private boolean                 referenceCheckerActive = true;
  private boolean                 referenceChecker_PutToZeroWhenReferenceDeleted = false;
  private boolean                 referenceChecker_DeleteWhenReferenceDeleted    = false;
  private ReferenceCheckerFilter  referenceCheckerFilter = null;
  
	private int                     nullValueMode          = NULL_VALUE_NOT_ALLOWED;
  private int                     listFieldIdInMaster    = -1;
  private boolean                 noDecoration           = false;

  public static final int SELECTION_PANEL_EDITOR               = 0;
  public static final int COMBO_BOX_EDITOR                     = 1;
  public static final int MULIT_COL_COMBO_BOX_EDITOR           = 2;
  public static final int BROWSE_POPUP_EDITOR                  = 3;

  public static final int NULL_VALUE_NOT_CUSTOMIZED = -1;//This is used for FObject
  public static final int NULL_VALUE_NOT_ALLOWED = 0;
  public static final int NULL_VALUE_ALLOWED = 1;
  public static final int NULL_VALUE_ALLOWED_AND_SHOWN = 2;

  public FObjectField(String name, String title, int id, FocDesc focDesc) {
  	this(name, title, id, false, focDesc, name+"_", null, -1);
  	setNullValueMode(NULL_VALUE_ALLOWED_AND_SHOWN);
  }

  public FObjectField(String name, String title, int id, FocDesc focDesc, FocDesc slaveFocDesc, int listFieldIdInMaster) {
    this(name, title, id, false, focDesc, name+"_", slaveFocDesc, listFieldIdInMaster, null);
  	setNullValueMode(NULL_VALUE_ALLOWED_AND_SHOWN);
  }
  
  public FObjectField(String name, String title, int id, boolean key, FocDesc focDesc, String keyPrefix) {
    /*super(name, title, id, key, 0, 0);
    this.focDesc = focDesc;
    this.keyPrefix = keyPrefix;*/
  	this(name, title, id, key, focDesc, keyPrefix, null, -1);
  }

  public FObjectField(String name, String title, int id, boolean key, FocDesc focDesc, String keyPrefix, FocDesc slaveFocDesc, int listFieldIdInMaster, FocDescForFilter focDescForFilter) {
  	this(name, title, id, key, focDesc, keyPrefix, slaveFocDesc, listFieldIdInMaster, focDescForFilter, true);
  }
  
  public FObjectField(String name, String title, int id, boolean key, FocDesc focDesc, String keyPrefix, FocDesc slaveFocDesc, int listFieldIdInMaster, FocDescForFilter focDescForFilter, boolean deleteListWithMaster) {
  	super(name, title, id, key, 0, 0);
    this.focDesc = focDesc;
    this.keyPrefix = keyPrefix;
    setListFieldInMaster(listFieldIdInMaster, slaveFocDesc, focDescForFilter, deleteListWithMaster);
  }

  public FObjectField(String name, String title, int id, boolean key, FocDesc focDesc, String keyPrefix, FocDesc slaveFocDesc, int listFieldIdInMaster) {
    this(name, title, id, key, focDesc, keyPrefix, slaveFocDesc, listFieldIdInMaster, null);
  }
  
  public void dispose(){
    super.dispose();
    
    focDesc   = null;
    keyPrefix = null;
    
    selectionList = null;
    if(multiColTableView != null){
      multiColTableView.dispose();
      multiColTableView = null;
    }
    if(filterExpression_FieldPath != null){
    	filterExpression_FieldPath.dispose();
    	filterExpression_FieldPath = null;
    }
  }
  
  public int getFabType() {
    return FieldDefinition.SQL_TYPE_ID_OBJECT_FIELD;
  }
  
  public FProperty newProperty_ToImplement(FocObject masterObj, Object defaultValue){
    return new FObject(masterObj, getID(), (FocObject) defaultValue);
  }

  public FProperty newProperty_ToImplement(FocObject masterObj){
    return newProperty(masterObj, null);
  }
  
  public void copyInteralProperties(FObjectField sourceField){
    if(sourceField.selectionList != null){
    	setSelectionList(sourceField.selectionList);
    }
    setWithList(sourceField.isWithList());
    setDisplayField(sourceField.getDisplayField());
   	setNoDecoration(sourceField.isNoDecoration());
    switch(sourceField.getEditorType()){
    case SELECTION_PANEL_EDITOR:
     	setDetailsPanelViewID(sourceField.getDetailsPanelViewID());
      break;
    case COMBO_BOX_EDITOR:
      setComboBoxCellEditor(sourceField.getDisplayField());      
      break;
    case MULIT_COL_COMBO_BOX_EDITOR:
      editorType = MULIT_COL_COMBO_BOX_EDITOR;
      setMultiLineComboBoxCellEditor(getDisplayField(), sourceField.multiColTableView);
      break;
    case BROWSE_POPUP_EDITOR:
      editorType = BROWSE_POPUP_EDITOR;
      setMultiLineComboBoxCellEditor(getDisplayField(), sourceField.multiColTableView);
    	break;
    }
    
    nullValueMode = NULL_VALUE_ALLOWED_AND_SHOWN;    
  }
  
  public FocList getSelectionList() {
    if (selectionList == null && focDesc != null && isWithList() && isAllowLoadListFromFocDesc()){
    	selectionList = focDesc.getFocList();
    	if(selectionList == null){
	      FocLink link = new FocLinkSimple(focDesc);
	      selectionList = new FocList(link);
    	}
    }
    if (selectionList != null) {
      selectionList.loadIfNotLoadedFromDB();
    }
    return selectionList;
  }

  public void setSelectionList(FocList selectionList) {
    this.selectionList = selectionList;
    setWithList(selectionList != null);
  }

  public int getSqlType() {
    return Types.JAVA_OBJECT;
  }

  public String getCreationString(String name) {
    return "";
  }

  /**
   * @return
   */
  public String getKeyPrefix() {
    return keyPrefix;
  }
  
  public void setKeyPrefix(String keyPrefix) {
    this.keyPrefix = keyPrefix;
  }
  
  public String getDBName(){
  	String name = getForcedDBName();
  	if(name == null){
	    name = keyPrefix != null ? keyPrefix :"";
	    //BAntoineS - 2017-10-01 
	    if(getFocDesc() != null && !Utils.isStringEmpty(getFocDesc().getRefFieldName())) {
	    	name = name + getFocDesc().getRefFieldName();
	    }else{
		    //EAntoineS - 2017-10-01	    	
	    	name = name + FField.REF_FIELD_NAME;
	    }
	  }
    return name;
  }
  
  public boolean isMasterDetailsLink(){
  	return listFieldIdInMaster > 0 || listFieldIdInMaster == FField.FLD_PROPERTY_FORMULA_LIST || (listFieldIdInMaster >= FField.FLD_SLAVE_LIST_FIRST && listFieldIdInMaster <= FField.FLD_SLAVE_LIST_LAST);
  }

  public FListField setListFieldInMaster(FocDesc masterDesc, int listFieldIdInMaster, FocDesc slaveFocDesc, FocDescForFilter focDescForFilter){
  	return setListFieldInMaster(masterDesc, listFieldIdInMaster, slaveFocDesc, focDescForFilter, true);
  }
  
  public FListField setListFieldInMaster(FocDesc masterDesc, int listFieldIdInMaster, FocDesc slaveFocDesc, FocDescForFilter focDescForFilter, boolean deleteListWithMaster){
  	FListField listField = null;
  	this.listFieldIdInMaster = listFieldIdInMaster;
  	if(isMasterDetailsLink()){
      if(masterDesc != null){
        if(focDescForFilter != null){
        	listField = masterDesc.addListField(slaveFocDesc, getID(), listFieldIdInMaster, focDescForFilter);
        }else{
        	listField = masterDesc.addListField(slaveFocDesc, getID(), listFieldIdInMaster);
        }
        listField.setDeleteListWhenMasterDeleted(deleteListWithMaster);
        if(deleteListWithMaster){
        	slaveFocDesc.setMasterRefFieldID(getID());//USed to create the index in the slave list of the master field.
        }
      }
  	}
  	return listField;
  }

  protected FListField setListFieldInMaster(int listFieldIdInMaster, FocDesc slaveFocDesc, FocDescForFilter focDescForFilter){
  	return setListFieldInMaster(getFocDesc(), listFieldIdInMaster, slaveFocDesc, focDescForFilter, true);
  }
  
  protected FListField setListFieldInMaster(int listFieldIdInMaster, FocDesc slaveFocDesc, FocDescForFilter focDescForFilter, boolean deleteListWithMaster){
  	return setListFieldInMaster(getFocDesc(), listFieldIdInMaster, slaveFocDesc, focDescForFilter, deleteListWithMaster);
  }

  public FListField getListFieldInMaster(){
  	FocDesc masterDesc = getFocDesc();
  	FListField listField = masterDesc != null ? (FListField) masterDesc.getFieldByID(listFieldIdInMaster) : null;
  	return listField;
  }

  /*
  public Object clone() throws CloneNotSupportedException {
    return null;
  }
  */
  
  public Object clone() throws CloneNotSupportedException {
    FField zClone = (FField)super.clone();
    ((FObjectField)zClone).setListFieldInMaster(-1, null, null);
    return zClone;
  }

  public void setNullValueDisplayString(String string){
  	this.nullValueDisplayString = string;
  }
  
  public String getNullValueDisplayString(){
  	return this.nullValueDisplayString;
  }
  
  public Component getGuiComponent_Panel(FProperty prop){
    FObjectPanel objPanel = new FObjectPanel();
    objPanel.setViewID(detailsPanelViewID);
    if (prop != null) objPanel.setProperty(prop);
    return objPanel;  	
  }

  public Component getGuiComponent_ComboBox(FProperty prop){
    return new FGObjectComboBox(prop, comboBoxDisplayField);
  }

  public Component getGuiComponent_MultiColumnComboBox(FProperty prop){
    return new FGObjectComboBox(prop, comboBoxDisplayField, multiColTableView);
  }

  public Component getGuiComponent_BrowsePopupComboBox(FProperty prop){
    return new FGObjectComboBox(prop, comboBoxDisplayField, multiColTableView, true);
  }

  public Component getGuiComponent(FProperty prop) {
    Component comp = null;

    switch (editorType) {
    case SELECTION_PANEL_EDITOR: {
    	comp = getGuiComponent_Panel(prop);
    }
      break;
    case COMBO_BOX_EDITOR: {
    	comp = getGuiComponent_ComboBox(prop);
    }
      break;
    case MULIT_COL_COMBO_BOX_EDITOR: {
    	comp = getGuiComponent_MultiColumnComboBox(prop);
    	break;
    }
    case BROWSE_POPUP_EDITOR: {
    	comp = getGuiComponent_BrowsePopupComboBox(prop);
    	break;
    }
    }

    return comp;
  }

  public AbstractCellControler getTableCellEditor_ToImplement(FProperty prop) {
    AbstractCellControler cellEditor = null;
    switch (editorType) {
    case SELECTION_PANEL_EDITOR:
      cellEditor = new ObjectCellControler();
      break;
    case COMBO_BOX_EDITOR:
    	cellEditor = new ComboBoxCellControler(/*getSelectionList(), */this.comboBoxDisplayField);
    	break;
    case MULIT_COL_COMBO_BOX_EDITOR:
    	cellEditor = new ComboBoxCellControler(this.comboBoxDisplayField, this.multiColTableView, true);
      break;
    case BROWSE_POPUP_EDITOR:
    	cellEditor = new ObjectCellControler();
      //cellEditor = new ComboBoxCellControler(this.comboBoxDisplayField, this.multiColTableView, true);
      break;
    }
    return cellEditor;
  }

  public boolean isObjectContainer() {
    return true;
  }

  /**
   * @return
   */
  public FocDesc getFocDesc() {
    return focDesc;
  }

  public void setFocDesc(FocDesc focDesc) {
    this.focDesc = focDesc;
  }

  public void addReferenceLocations(FocDesc pointerDesc) {
  	if(isReferenceCheckerActive() && !isMasterDetailsLink() && getID() != FField.FLD_FATHER_NODE_FIELD_ID){
	    FocDesc targetDesc = getFocDesc();
	
	    referenceCheckerAdater = new ReferenceCheckerAdapter(pointerDesc, getID());
	    referenceCheckerAdater.setReferenceCheckerFilter(getReferenceCheckerFilter());
	    if(targetDesc != null){
	      targetDesc.addReferenceLocation(referenceCheckerAdater);
	    }
  	}
  }
  
  public void setDisplayField(int displayField) {
    this.comboBoxDisplayField = displayField;
  }

  public int getDisplayField() {
    return comboBoxDisplayField;
  }

  public void setComboBoxCellEditor(int displayField) {
    editorType = COMBO_BOX_EDITOR;
    this.comboBoxDisplayField = displayField;
  }

  public void setMultiLineComboBoxCellEditor(int displayField, FTableView tableView) {
    editorType = MULIT_COL_COMBO_BOX_EDITOR;
    this.comboBoxDisplayField = displayField;
    multiColTableView = tableView;
  }

  public void setBrowsePopupComboBoxCellEditor(int displayField, FTableView tableView) {
    editorType = BROWSE_POPUP_EDITOR;
    this.comboBoxDisplayField = displayField;
    multiColTableView = tableView;
  }

  public int getDetailsPanelViewID() {
    return detailsPanelViewID;
  }

  public void setDetailsPanelViewID(int detailsPanelViewID) {
    this.detailsPanelViewID = detailsPanelViewID;
  }

  public int getFieldDisplaySize() {
    FField field = getFocDesc().getFieldByID(comboBoxDisplayField);
    return field != null ? field.getFieldDisplaySize() : 0;
  }

  public boolean isWithList() {
    return withList;
  }

  public void setWithList(boolean withList) {
    this.withList = withList;
  }

  public int getNullValueMode() {
    return nullValueMode;
  }

  public void setNullValueMode(int nullValueMode) {
    this.nullValueMode = nullValueMode;
  }
  
  public int getEditorType() {
    return editorType;
  }
  
  public FTableView getMultiColTableView(){
  	return multiColTableView;
  }
  
  protected FilterCondition getFilterCondition(FFieldPath fieldPath, String conditionPrefix){
		ObjectCondition condition = null;
		if(fieldPath != null && conditionPrefix != null){
			condition = new ObjectCondition(fieldPath, conditionPrefix);
		}
		return condition;
	}
  
  public String getNameInSourceTable(){
    return getDBName();//getKeyPrefix()+FField.REF_FIELD_NAME;
  }
  public String getNameInTargetTable(){
    return getFocDesc() != null ? getFocDesc().getRefFieldName() : FField.REF_FIELD_NAME;
  }
  
  private boolean displayNullValues = true;
  public boolean isDisplayNullValues() {
    return displayNullValues;
  }
  
  public void setDisplayNullValues(boolean displayNullValues) {
    this.displayNullValues = displayNullValues;
  }

	public ReferenceCheckerAdapter getReferenceCheckerAdater() {
		return referenceCheckerAdater;
	}

	public boolean isReferenceCheckerActive() {
		return referenceCheckerActive;
	}

	public void setReferenceCheckerActive(boolean referenceCheckerActive) {
		this.referenceCheckerActive = referenceCheckerActive;
	}

	public boolean isNoDecoration() {
		return noDecoration;
	}

	public void setNoDecoration(boolean noDecoration) {
		this.noDecoration = noDecoration;
	}
	
	public ReferenceCheckerFilter getReferenceCheckerFilter() {
		return referenceCheckerFilter;
	}

	public void setReferenceCheckerFilter(ReferenceCheckerFilter referenceCheckerFilter) {
		this.referenceCheckerFilter = referenceCheckerFilter;
	}

  /**
	 * @return the referenceChecker_PutToZeroWhenReferenceDeleted
	 */
	public boolean isReferenceChecker_PutToZeroWhenReferenceDeleted() {
		return referenceChecker_PutToZeroWhenReferenceDeleted;
	}

	/**
	 * @param referenceChecker_PutToZeroWhenReferenceDeleted the referenceChecker_PutToZeroWhenReferenceDeleted to set
	 */
	public void setReferenceChecker_PutToZeroWhenReferenceDeleted(boolean referenceChecker_PutToZeroWhenReferenceDeleted) {
		this.referenceChecker_PutToZeroWhenReferenceDeleted = referenceChecker_PutToZeroWhenReferenceDeleted;
	}

	public boolean isReferenceChecker_DeleteWhenReferenceDeleted() {
		return referenceChecker_DeleteWhenReferenceDeleted;
	}

	public void setReferenceChecker_DeleteWhenReferenceDeleted(boolean referenceChecker_DeleteWhenReferenceDeleted) {
		this.referenceChecker_DeleteWhenReferenceDeleted = referenceChecker_DeleteWhenReferenceDeleted;
	}

	public FFieldPath getFilterExpression_FieldPath() {
		return filterExpression_FieldPath;
	}

	public void setFilterExpression_FieldPath(FFieldPath filterExpression_FieldPath) {
		this.filterExpression_FieldPath = filterExpression_FieldPath;
	}

	public int getSelectionPanelViewID() {
		return selectionPanelViewID;
	}

	public void setSelectionPanelViewID(int selectionPanelViewID) {
		this.selectionPanelViewID = selectionPanelViewID;
	}

  @Override
  public IFocData iFocData_getDataByPath(String path) {
    IFocData focData = null;

    if(getFocDesc() != null){
      focData = getFocDesc().iFocData_getDataByPath(path);
    }
    
    return focData;
  }

  public String getSelectionFilterExpression() {
    return selectionFilterExpression;
  }

  public void setSelectionFilterExpression(String selectionFilterExpression) {
    this.selectionFilterExpression = selectionFilterExpression;
  }
  
	public Class vaadin_getClass(){
		return Integer.class;
	}

	public boolean isAllowLoadListFromFocDesc() {
		return allowLoadListFromFocDesc;
	}

	public void setAllowLoadListFromFocDesc(boolean allowLoadListFromFocDesc) {
		this.allowLoadListFromFocDesc = allowLoadListFromFocDesc;
	}

	public String getSelectionFilter_PropertyDataPath() {
		return selectionFilter_PropertyDataPath;
	}

	public void setSelectionFilter_PropertyDataPath(String selectionFilter_PropertyDataPath) {
		this.selectionFilter_PropertyDataPath = selectionFilter_PropertyDataPath;
	}

	public Object getSelectionFilter_PropertyValue() {
		return selectionFilter_Propertyvalue;
	}

	public void setSelectionFilter_Propertyvalue(Object selectionFilter_Propertyvalue) {
		this.selectionFilter_Propertyvalue = selectionFilter_Propertyvalue;
	}

	public String getForcedDBName() {
		return forcedDBName;
	}

	public void setForcedDBName(String forcedDBName) {
		this.forcedDBName = forcedDBName;
	}

	public String getFocDescStorageName() {
		return focDescStorageName;
	}
	
	public void setFocDescStorageName(String focDescStorageName, boolean cascade, boolean listDirectlyEditable) {
		this.focDescStorageName   = focDescStorageName;
		this.cascade              = cascade;
		this.listDirectlyEditable = listDirectlyEditable;
	}
	
	public void getFocDescFromStorageNameIfNeeded(FocDesc slaveFocDesc){
		String storageName = getFocDescStorageName();
		if(!Utils.isStringEmpty(storageName) && getFocDesc() == null){
			FocDesc focDesc = Globals.getApp().getFocDescByName(storageName);
			if(focDesc == null){
				Globals.showNotification("Table FocDesc Not Found", "TableName:"+storageName, IFocEnvironment.TYPE_ERROR_MESSAGE);
				focDesc = Globals.getApp().getFocDescByName(storageName);
			}else{
				setFocDesc(focDesc);
				if(focDesc != null && !focDesc.isListInCache()){
					setWithList(false);
				}
				if(focDesc != null && isCascade()){
					FListField listField = setListFieldInMaster(focDesc, focDesc.nextFldID(), slaveFocDesc, null);
					listField.setDirectlyEditable(listDirectlyEditable);
				}
			}
		}
	}
	
	public boolean isCascade() {
		return cascade;
	}
}
