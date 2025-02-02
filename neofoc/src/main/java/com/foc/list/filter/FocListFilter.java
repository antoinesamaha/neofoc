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
// MEMORY LEVEL
// DATABASE LEVEL
// COMMON LEVEL

/*
 * Created on Jul 9, 2005
 */
package com.foc.list.filter;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.foc.business.dateShifter.DateShifter;
import com.foc.business.dateShifter.IDateShifterHolder;
import com.foc.db.SQLFilter;
import com.foc.db.SQLJoin;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldPath;
import com.foc.gui.FAbstractListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.FTreeTablePanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.FTableView;
import com.foc.gui.treeTable.FTreeTableModel;
import com.foc.list.FocList;
import com.foc.list.FocListElement;
import com.foc.property.FProperty;
import com.foc.util.Utils;

/**
 * @author 01Barmaja
 */
public abstract class FocListFilter extends FocObject implements IFocListFilter, IDateShifterHolder {  
  public static final int LEVEL_MEMORY   = 1;
  public static final int LEVEL_DATABASE = 2;
  public static final int LEVEL_DATABASE_AND_MEMORY = 3;
  
  private boolean allwaysActive = false;
  private boolean active = false;
  private FAbstractListPanel selectionPanel = null;
  private ArrayList<Integer> visibleArray = null;
  private Color colorBackup = null;
  private int filterLevel = LEVEL_MEMORY;
  private boolean shouldColorRed = false;
  //private SQLJoinMap joinMap = null;
  
  private FValidationPanel validationPanel = null;
  
  public static final int VIEW_SUMMARY = -2;
  public static final int VIEW_DETAIL_FOR_DB_RESIDENT = 1;
  
  private ArrayList<FocListFilterListener> listeners = null;

  private HashMap<Integer, DateShifter> dateShifterMap = null;
  
  public FocListFilter(FocConstructor constr){
    super(constr);
    fillProperties(this);
    if(isRevisionSupportEnabled()){
      allwaysActive = true;
    }
    
    FilterDesc filterDesc = getThisFilterDesc();
    if(filterDesc != null){
	  	for(int i=0; i<filterDesc.getConditionCount(); i++){
	      FilterCondition cond = filterDesc.getConditionAt(i);
	      if(cond != null && !cond.isValueLocked(this)){
	        cond.resetToDefaultValue(this);
	      }
	    }
    }
    
    adjustDescription();
  }

  public void dispose(){    
    selectionPanel = null;
    if(visibleArray != null){
      visibleArray.clear();
      visibleArray = null;
    }
    colorBackup = null;
    
    if(validationPanel != null){
      validationPanel.dispose();
      validationPanel = null;
    }
    
    if(dateShifterMap != null) {
    	Iterator iter = dateShifterMap.values().iterator();
    	while(iter != null && iter.hasNext()) {
    		DateShifter shifter = (DateShifter) iter.next();
    		shifter.dispose();
    	}
    	dateShifterMap.clear();
    	dateShifterMap = null;
    }
    
    super.dispose();
  }
  
  @Override
  public boolean isActive() {
    return active || allwaysActive;
  }
    
  public void fillProperties(FocObject fatherObject){
    FilterDesc filterDesc = getThisFilterDesc();    
    
    if(filterDesc != null){
      filterDesc.fillProperties(fatherObject);
    }
  
  }
  
  public FilterCondition findFilterCondition(String conditionName){
  	FilterCondition foundCond = null;
  	FilterDesc filterDesc = getThisFilterDesc();
  	
  	for(int i=0; i<filterDesc.getConditionCount() && foundCond == null; i++){
      FilterCondition cond = filterDesc.getConditionAt(i);
      if(cond != null){
        if(cond.getFieldPrefix().compareTo(conditionName) == 0){
        	foundCond = cond;
        }
      }
    }
  	
  	return foundCond;
  }
  
  public void adjustDescription(){
  	FilterDesc filterDesc = getThisFilterDesc();
  	String description = "";

  	for(int i=0; i<filterDesc.getConditionCount(); i++){
      FilterCondition cond = filterDesc.getConditionAt(i);
      if(cond != null && cond.isDisplay()){
      	String descPart = cond.buildDescriptionText(this);
      	if(!Utils.isStringEmpty(descPart)){
      		if(description.length() > 0){
      			//if(ConfigInfo.isArabic()){
      				description = description + " | ";
      			//}else{
      				//description = " | " + description ;
      			//}
      		}
      		description = description + descPart;
      	}
      }
    }
  	
  	setDescription(description);
  }

  public void adjustDatesAccordingToShifters() {
	  if(dateShifterMap != null) {
	  	Iterator iter = dateShifterMap.values().iterator();
	  	while(iter != null && iter.hasNext()) {
	  		DateShifter shifter = (DateShifter) iter.next();
	  		if(shifter != null) {
	  			shifter.adjustDate();
	  		}
	  	}
	  }
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // MEMORY LEVEL
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  @Override
  public ArrayList<Integer> getVisibleArray(){
    if(visibleArray == null){
      visibleArray = new ArrayList<Integer>();
      FocList list = getGuiFocList();
      if(list != null){
        for(int i=0; i<list.size(); i++){
          FocObject obj = list.getFocObject(i);
          boolean include = includeObject(obj);
          if(include){
            visibleArray.add(Integer.valueOf(i));
          }
        }
      }
    }
    //Globals.logString("visible array : " + visibleArray.size());
    return visibleArray;
  }
  
  public void resetVisibleArray(){
    visibleArray = null;
    notifyListener();
  }
  
  @Override
  public boolean isObjectExist(int objectIndex){
    boolean exist = getVisibleArray().contains(objectIndex);
    return exist;
  }
  
  public int getListVisibleElementCount(){
    return getVisibleArray().size();
  }

  public FocListElement getListVisibleElementAt(int row){
  	FocList list = getGuiFocList();
    ArrayList visibleArray = (ArrayList) getVisibleArray();
    return (visibleArray != null && visibleArray.size() > row) ? list.getFocListElement(((Integer)visibleArray.get(row)).intValue()) : null;
  }

  public FocObject getListVisibleObjectAt(int row){
    FocListElement listElement = getListVisibleElementAt(row);
    return listElement.getFocObject();
  }
  
  public boolean isVisible(FocObject object){
    FocList guiList = getGuiFocList();
    int row = guiList.getRowForObject(object);
    ArrayList visibleArray = (ArrayList) getVisibleArray();
    return visibleArray.contains(row);
  }
  
  public boolean includeObject(FocObject focObject){
    boolean include = true;
    if(isActive()) {
	    if(getFilterLevel() == LEVEL_MEMORY){
	    	include = includeObject_ScanConditionsAndCheckThem(focObject, false);
	    } else if(getFilterLevel() == LEVEL_DATABASE_AND_MEMORY) {
	    	include = includeObject_ScanConditionsAndCheckThem(focObject, true);
//	    } else if(getFilterLevel() == LEVEL_DATABASE && getThisFilterDesc() != null && getThisFilterDesc().isHasOneMemoryOnlyConditionAtLeast()){
//	    	include = includeObject_ScanConditionsAndCheckThem(focObject, true);
	    }
    }
    return include;
  }
  
  private boolean includeObject_ScanConditionsAndCheckThem(FocObject focObject, boolean checkConditionLevel){
    boolean include = true;
    FilterDesc filterDesc = getThisFilterDesc();
    if(filterDesc != null){
      for(int i=0; i<filterDesc.getConditionCount() && include; i++){
        FilterCondition cond = filterDesc.getConditionAt(i);
        if(!checkConditionLevel || cond.getLevel() != FocListFilter.LEVEL_DATABASE) {
        	include = cond.includeObject(this, focObject);
        }
      }
    }
    return include;  	
  }

  public void refreshDisplay(){
    FAbstractListPanel listPanel = getSelectionPanel();
    if(listPanel != null){  
      listPanel.getTableModel().fireTableDataChanged();
    	/*SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					getSelectionPanel().getTableModel().fireTableDataChanged();					
				}
    	});*/
    }
  }
  
  public void setActive_MemoryLevel(boolean active) {
    resetVisibleArray();
    refreshDisplay();
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DATABASE LEVEL
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  private void internalExec(SQLFilter filter, boolean addingJoins /*first time only*/){ 
    FilterDesc filterDesc = getThisFilterDesc();
    StringBuffer buffer = new StringBuffer();
    
    shouldColorRed = false;
    for(int i=0; i<filterDesc.getConditionCount(); i++){
      FilterCondition cond = filterDesc.getConditionAt(i);
      SQLJoin lastJoin = null; 
      StringBuffer condWhere = (cond.getLevel() != FocListFilter.LEVEL_MEMORY) ? cond.buildSQLWhere(this, cond.getDBFieldName()) : null;
      if(condWhere != null && condWhere.length() > 0){
      	shouldColorRed = true;
        FFieldPath condFieldPath = cond.getFieldPath();
        FocDesc cMasterDesc = getThisFilterDesc().getSubjectFocDesc();
        FocDesc cSlaveDesc = null;
        
        String currentAlias = filter.getJoinMap().getMainTableAlias();
        for (int fi = 0; fi < condFieldPath.size()-1 && cMasterDesc != null; fi++) {
          int fieldId = condFieldPath.get(fi);
          FField field = cMasterDesc.getFieldByID(fieldId);
          cSlaveDesc = field.getFocDesc();
  
          if(cSlaveDesc != null){
            lastJoin = new SQLJoin(cMasterDesc.getProvider(), cSlaveDesc.getStorageName_ForSQL(),currentAlias,field.getNameInSourceTable(),field.getNameInTargetTable());
            lastJoin = filter.getJoinMap().addJoin(lastJoin);
            currentAlias = lastJoin.getNewAlias();                           
          }
          
          cMasterDesc = cSlaveDesc;
        }
        if(!addingJoins){
          String tableAlias = (lastJoin != null) ? lastJoin.getNewAlias() : SQLJoin.MAIN_TABLE_ALIAS;
          String prefixForTable = filter.hasJoinMap()?tableAlias+".":"";
          //We have to recall the build SQL where with the correct full field name (Alias)
          condWhere = cond.buildSQLWhere(this, prefixForTable+cond.getDBFieldName());
          boolean appending = buffer.length() > 0; 
          if(appending){
            buffer.append(" and ("); 
          }
          buffer.append(condWhere);
          if(appending){
            buffer.append(") ");
          }
        }
      }            
    }
    
    if(!addingJoins) filter.addAdditionalWhere(buffer);
  }
  
  /*public void setActive_DatabaseLevel(boolean active){
    FocList list = getFocList();
    
    SQLFilter filter = list.getFilter();
    if(filter != null){
      filter.setObjectTemplate(null);
      filter.setFilterFields(SQLFilter.FILTER_ON_SELECTED);
      filter.resetSelectedFields();
      
      filter.setAdditionalWhere(null);
      filter.getJoinMap().clearJoinMap();
      if(isActive()){
        internalExec(filter, true);
        internalExec(filter, false);
      }else{
        //filter.getJoinMap().clearJoinMap();      
      }
    }
    resetVisibleArray();
    reloadListFromDatabase();
    //list.fireEvent(null, FocEvent.ID_ITEM_ADD);
    refreshDisplay();
  }*/
  
  public void setActive_DatabaseLevel(boolean active){
    FocList list = getFocList();
    if(list != null){
	    SQLFilter filter = list.getFilter();
	    if(filter != null){
	      filter.setObjectTemplate(null);
	      filter.setFilterFields(SQLFilter.FILTER_ON_SELECTED);
	      filter.resetSelectedFields();
	      
	      filter.setAdditionalWhere(null);
	      filter.getJoinMap().clearJoinMap();
	      internalExec(filter, true);
	      internalExec(filter, false);
	      
	    }
	    resetVisibleArray();
	    if(active) reloadListFromDatabase();
	    //list.fireEvent(null, FocEvent.ID_ITEM_ADD);
	    refreshDisplay();
    }
  }
  
  private void resetAllNotLockedPropertiesToDefaultValue(){
  	FilterDesc filterDesc = getThisFilterDesc();

    for(int i=0; i < filterDesc.getConditionCount(); i++){
    	FilterCondition cond = filterDesc.getConditionAt(i);
    	if(cond != null){
    		if(!cond.isValueLocked(this)){
    			cond.resetToDefaultValue(this);
    		}
    	}
    }
  }
  
  public void reloadListFromDatabase(){
  	FocList list = getFocList();
    list.reloadFromDB();  	
  }
  
  private boolean isAnyConditionActive(){
  	boolean active = false;
    FilterDesc filterDesc = getThisFilterDesc();
    for(int i=0; i<filterDesc.getConditionCount(); i++){
      FilterCondition cond = filterDesc.getConditionAt(i);
      if(cond.getLevel() != FocListFilter.LEVEL_MEMORY) {
	      StringBuffer condWhere = cond.buildSQLWhere(this, cond.getDBFieldName());
	      if(condWhere != null && condWhere.length() > 0){
	      	active = true;
	      }
      }
    }
    return active;
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // COMMON LEVEL
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  private boolean firstTime = true;

  public  boolean isRevisionSupportEnabled() {
    return getThisFilterDesc().getSubjectFocDesc().isRevisionSupportEnabled();
  }
  
  private RevisionCondition getRevisionCondition() {
    FilterDesc filterDesc = getThisFilterDesc();
    RevisionCondition cond = null;
    for(int i = 0; i < filterDesc.getConditionCount() && cond == null; i++){
      if(filterDesc.getConditionAt(i).getFieldPrefix().compareTo(FField.CREATION_REVISION_FIELD_ID_NAME) == 0){
        cond = (RevisionCondition)filterDesc.getConditionAt(i);
      }
    }
    return cond;
  }
  
  private FProperty getRevisionProperty() {
    FilterDesc filterDesc = getThisFilterDesc();
    FProperty revNumber = null;
    FocList list = getFocList();
    
    //list.getForeignObjectsMap().get(arg0);
    
    revNumber =  filterDesc.getSubjectFocDesc().getRevisionPath().getPropertyFromObject(list.getFocObject(0));
    
    return revNumber;
  }
  
  public void setActive(boolean active) {
  	adjustDescription();
    if(isRevisionSupportEnabled() && firstTime ){
      FProperty revNumber =  getRevisionProperty();
      RevisionCondition cond = getRevisionCondition();
      if(revNumber != null && cond != null ){
        cond.setRev(this, revNumber.getInteger());
        cond.setOperator(this, RevisionCondition.OPERATOR_EQUALS);  
      }
      firstTime = false;
    }
    
    if(isRevisionSupportEnabled()){
      unlockPropertiesForRevisionForFocList();
    }
    
    this.active = active || allwaysActive;
    if(!isActive()){
    	resetAllNotLockedPropertiesToDefaultValue();
    }
  	
    if(filterLevel == LEVEL_MEMORY){
      setActive_MemoryLevel(this.active);
    }else{
    	setActive_DatabaseLevel(this.active);
    }
   	  
	  if(selectionPanel != null){
	  	shouldColorRed = isAnyConditionActive();
	    if(shouldColorRed){
	      if(colorBackup == null){ 
	        colorBackup = selectionPanel.getFilterButton().getBackground();
	      }
	      selectionPanel.getFilterButton().setBackground(java.awt.Color.RED);
	    }else if(colorBackup != null){
	      selectionPanel.getFilterButton().setBackground(colorBackup);
	    }
	  }
    
	  if(isRevisionSupportEnabled()){
	    FProperty revNumber =  getRevisionProperty();
	    RevisionCondition cond = getRevisionCondition();
	    if( revNumber != null && cond != null ){
	      if( cond.getRev(this) < revNumber.getInteger() ){
	        lockPropertiesForRevisionForFocList();
	        lockRelatedComponents();
	        getSelectionPanel().enableModificationButtons(false);
	      }else{
	        unlockRelatedComponents();
	        getSelectionPanel().enableModificationButtons(true);
	      }  
	    }
	  }
    FAbstractListPanel listPanel = getSelectionPanel();
    if(listPanel instanceof FTreeTablePanel){
      FTreeTableModel treeTableModel = ((FTreeTableModel)listPanel.getTableModel());
      treeTableModel.refreshGui();      
    }
  }

  private void lockPropertiesForRevisionForFocList() {
    FocList list = getFocList();
    for(int j = 0; j < list.size(); j++){
      list.getFocObject(j).lockPropertiesForRevision();
    }
  }
  
  private void lockRelatedComponents() {
    FocObject obj = getParentRevisionFocObject();
    obj.disableRelatedGuiComponents();
  }
  
  private void unlockRelatedComponents() {
    FocObject obj = getParentRevisionFocObject();
    obj.enableRelatedGuiComponents();
  }
  
  private FocObject getParentRevisionFocObject() {
    FocList list = getFocList();
    Iterator it = list.getForeignObjectsMap().keySet().iterator();
    Object key = null;
    while(it.hasNext()) {
      key = it.next();
    }
    return list.getForeignObjectsMap().get(key);
  }
  
  private void unlockPropertiesForRevisionForFocList() {
    FocList list = getFocList();
    for(int j = 0; j < list.size(); j++){
      list.getFocObject(j).unlockPropertiesForRevision();
    }
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // PANEL
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  
  //Make the details panel in a separted class
  /*public FPanel newDetailsPanel(int viewID) {
    FPanel panel = new FPanel();
    int y = 0; 
    FilterDesc filterDesc = getThisFilterDesc();
    for(int i=0; i<filterDesc.getConditionCount(); i++){
      FilterCondition cond = filterDesc.getConditionAt(i);
      GuiSpace space = cond.putInPanel(this, panel, 0, y);
      y += space.getY();
    }
    
    if(validationPanel != null){
      validationPanel.dispose();
      validationPanel = null;
    }
    validationPanel = panel.showValidationPanel(true);
    validationPanel.setValidationType(FValidationPanel.VALIDATION_OK_CANCEL);
    validationPanel.setAskForConfirmationForExit(false);
    
    validationPanel.setCancelationButtonLabel("Exit");
    validationPanel.setValidationButtonLabel("Apply");
    if(!isAllwaysActive()){
      FGButton removeButton = new FGButton("Remove");
      removeButton.setName("Remove");TEMP
      removeButton.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e) {
          setActive(false);
          validationPanel.cancel();
        }
      });
      validationPanel.addButton(removeButton);
      if (isRevisionSupportEnabled()){
        removeButton.setVisible(false);
      }
    }    
    
    validationPanel.setValidationListener(new FValidationListener(){
      public boolean proceedValidation(FValidationPanel panel) {
        //Globals.logString("list visible count in foc filter : " +getListVisibleElementCount());
        return true;
      }

      public boolean proceedCancelation(FValidationPanel panel) {
        return true;
      }

      public void postValidation(FValidationPanel panel) {   
        setActive(true); 
      }

      public void postCancelation(FValidationPanel panel) {
        
      }
    });
    
    return panel;
  }*/
  
  public FPanel newDetailsPanel(int viewID) {
  	return super.newDetailsPanel(viewID);
  	//return new FocListFilterGuiDetailsPanel(this, viewID);
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST FILTERING
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public FocList getFocList(){
    return getGuiFocList();
  }

  public FocList getGuiFocList(){
    FAbstractListPanel panel = getSelectionPanel();
    //return (panel != null && (panel instanceof FListPanel))? ((FListPanel)panel).getFocList() : null;
    return panel != null ? panel.getFocList() : null;
  }
  
  public FTableView getTableView() {
    FAbstractListPanel panel = getSelectionPanel();
    return panel != null ? panel.getTableView() : null;
  }
  
  public FAbstractListPanel getSelectionPanel() {
    return selectionPanel;
  }
  
  public void setSelectionPanel(FAbstractListPanel selectionPanel) {
    this.selectionPanel = selectionPanel;
  }
  
  public void setAllwaysActive(boolean allwaysActive) {
    this.allwaysActive = allwaysActive;
    setActive(isActive());
  }

  public boolean isAllwaysActive() {
    return allwaysActive;
  }

  public int getFilterLevel() {
    return filterLevel;
  }
  
  public void setFilterLevel(int filterLevel) {
    this.filterLevel = filterLevel;
  }
  
  public boolean isFocObjectHide(FocObject focObject){
    boolean hide = true;
    if(focObject.getFatherSubject() instanceof FocList){
      FocList focList = (FocList) focObject.getFatherSubject();
      FocListElement focListElement = focList.getFocListElement(focObject);
      int focListElementPostion = focList.getFocListElementPosition(focListElement);
      if(getVisibleArray().contains(focListElementPostion)){
        hide = false;
      }
    }
    return hide;
  }

  public ArrayList<FocListFilterListener> getListeners() {
    if(listeners == null){
      listeners = new ArrayList<FocListFilterListener>();
    }
    return listeners;
  }

  @Override
  public void addListener(FocListFilterListener listener){
    getListeners().add(listener);
  }
  
  public int getListenersCount(){
    return getListeners().size();
  }
  
  @Override
  public void removeListener(FocListFilterListener listener){
    ArrayList<FocListFilterListener> focListFilterListener = getListeners();
    focListFilterListener.remove(listener);
  }

//  public void removeAllFilters(){
//	  for(int i=0; i < tree.getFocListFilter().getListenersCount(); i++){
//	    IFocListFilter focListFilter = tree.getFocListFilter();
//	    if(focListFilter != null){
//	      focListFilter.removeListener((FocListFilterListener)this);
//	    }
//	  }
//  }
  
  public void notifyListener(){
    for(int i=0; i < getListenersCount(); i++){
      FocListFilterListener focListFilterListener = listeners.get(i);
      focListFilterListener.visibleArrayReset();
    }
  }

  @Override
  public DateShifter getDateShifter(int shifter) {
  	DateShifter dateShifter = null;
  	if(dateShifterMap == null) {
  		dateShifterMap = new HashMap<Integer, DateShifter>();
  	}
  	
  	if(dateShifterMap != null) {
  		dateShifter = dateShifterMap.get(shifter);
  		if(dateShifter == null) {
  			dateShifter = new DateShifter(this, getThisFilterDesc().getDateShifterDesc(shifter));
  			dateShifterMap.put(shifter, dateShifter);
  		}
  	}
  	return dateShifter;
  };
}
