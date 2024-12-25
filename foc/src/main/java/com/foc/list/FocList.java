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
// DISPLAY FILTER
// ARRAY
// LIST
// ACCESS
// DATABASE
// ELEMENT HASH

/*
 * Created on Oct 14, 2004
 */
package com.foc.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import com.foc.Globals;
import com.foc.access.AccessSubject;
import com.foc.admin.UserSession;
import com.foc.api.IFocList;
import com.foc.api.IFocObject;
import com.foc.business.company.Company;
import com.foc.business.workflow.WFSite;
import com.foc.business.workflow.implementation.IWorkflowDesc;
import com.foc.business.workflow.implementation.WorkflowDesc;
import com.foc.db.ListPagination;
import com.foc.db.SQLFilter;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocFieldEnum;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldPath;
import com.foc.desc.parsers.ParsedFocDesc;
import com.foc.desc.parsers.join.ParsedJoin;
import com.foc.event.FocEvent;
import com.foc.event.FocListener;
import com.foc.formula.FocSimpleFormulaContext;
import com.foc.formula.Formula;
import com.foc.gui.FAbstractListPanel;
import com.foc.gui.table.FTable;
import com.foc.property.FDate;
import com.foc.property.FInt;
import com.foc.property.FList;
import com.foc.property.FObject;
import com.foc.property.FProperty;
import com.foc.property.FReference;
import com.foc.shared.dataStore.IFocData;
import com.foc.shared.json.B01JsonBuilder;
import com.foc.util.FocMath;
import com.foc.util.IFocIterator;
import com.foc.util.Utils;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * @author 01Barmaja - Antoine SAMAHA
 */
@SuppressWarnings("serial")
public class FocList extends AccessSubject implements IFocList, Container {
	public static final String FILTER_KEY_FOR_INCREMENTAL_UPDATE = "_INCREMENTAL_UPDATE";
	
  public static final int NONE           = 0;
  public static final int LOAD_IF_NEEDED = 1;
  public static final int FORCE_RELOAD   = 2;
  
  private ConcurrentHashMap<FocObject, FocListElement> elements      = null;
  private ConcurrentHashMap<Long, FocListElement>      elementsByRef = null;
  
  private SQLFilter      filter     = null;
  private FocListGroupBy sqlGroupBy = null;
  private ArrayList<FocListener> listeners = null;
   
  private FAbstractListPanel selectionPanel             = null;
  private FAbstractListPanel attachedListPanel          = null;//Gets the attached list panel 
  private int                viewIDForSelectionBrowse   =    0;
  private long               nextTempReference          =    1;
  private int                nextOrder                  =    0;
  private FocListOrder       listOrder                  = null;//Priority 1
  private Comparator         orderComparator            = null;//Priority 2 - This one for external unser defined orders
  private FocListListener    focListListener            = null; 
  private FObject            selectionProperty          = null;
  private FocList            listRequestingTheSelection = null;

  private char              flags                                    = 0;
  private static final char FLG_WITH_DEPRICATION                     = 1;
  private static final char FLG_SLEEP_LISTENERS                      = 2;
  private static final char FLG_LOADED                               = 4;
  private static final char FLG_DIRECT_IMPACT_ON_DATABASE            = 8;
  private static final char FLG_DIRECTLY_EDITABLE                    = 16;
  private static final char FLG_WAIT_FOR_VALIDATION_TO_ADD           = 32;
  private static final char FLG_KEEP_NEW_LINE_FOCUS_UNTIL_VALIDATION = 64;
  private static final char FLG_COLLECTION_BEHAVIOUR                 = 128;
  private static final char FLG_DISABLE_RESORT_AFTER_ADD             = 256;
  private static final char FLG_IS_LOADING                           = 512;
  private static final char FLG_DISABLE_SORT                         = 1024;
  private static final char FLG_AUTO_REFRESH                         = 2048;
  
  // Image of the real list.
  // It is set to null when modified
  // and constructed automatically when needed.
  private ArrayList<FocListElement> arrayList = null;
  private ReferencesCollection      referencesCollection_Vaadin = null;//ForVaadin implementation

  private FocObject masterObject = null;
  private HashMap<Integer, FocObject> foreignObjectsMap = null;//THE KEYS ARE THE FIELD IDS IN THE SLAVE DESC
  private FocLink   focLink        = null;
  private FList     fatherProperty = null;// When the FocList is in a property this
                                      // is used to notify the proprty of
                                      // modifications

  private Object validityCheckObject = null;
  
  private long    lastLoadTime         = 0;
  private boolean checkIfRecentEnough  = false;
  public static final long EXPIRY_TIME = 1 * 60 * 1000; 
  
  // oooooooooooooooooooooooooooooooooo
  // MAIN
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public synchronized void init(FocObject masterObject, FocLink focLink, SQLFilter filter) {
    this.focLink = focLink;
    this.masterObject = masterObject;
    this.filter = filter;

    if(this.filter == null){
      this.filter = new SQLFilter(null, 0);
    }

    this.focLink.adaptSQLFilter(this, this.filter);

    setWaitForValidationToAddObject(true);
    setKeepNewLineFocusUntilValidation(true);
    
    addLogicalDeleteFilter();
    if(getListOrder() == null && getFocDesc() != null && getFocDesc().hasOrderField()){
    	FocListOrder order = new FocListOrder(FField.FLD_ORDER);
    	setListOrder(order);
    }
  }

  private synchronized void initForForeignKeyLink(HashMap<Integer, FocObject> foreignObjectsMap, FocLinkForeignKey focLinkForeignKey, SQLFilter filter){
    setForeignObjectsMap(foreignObjectsMap);
    if(this.foreignObjectsMap.size() == 1){
      Iterator iter = this.foreignObjectsMap.keySet().iterator();
      if(iter != null && iter.hasNext()){
        int key = (Integer)iter.next();
        FocObject uniqueForeignObject = foreignObjectsMap.get(key);
        uniqueForeignObject.setTransactionalWithChildren(focLinkForeignKey.isTransactionalWithChildren());
        setFatherSubject(uniqueForeignObject);
      }
    }
    init(null,focLinkForeignKey,filter);
  }

  public FocList(FocDesc focDesc) {
  	this(new FocLinkSimple(focDesc));
  }
  
  /**
   * This constructor is mainly for simple lists used to store some objects We
   * do not need any link or filter, neether a master object
   */
  public FocList(FocLink focLink) {
    super(Globals.getDefaultAccessControl());
    init(null, focLink, null);
  }

  public FocList(FocObject masterObject, FocLink focLink, SQLFilter filter) {
    super(Globals.getDefaultAccessControl());
    if(FocLinkForeignKey.class.isInstance(focLink)){
      HashMap<Integer, FocObject> foreignMap = new HashMap<Integer, FocObject>();
      FocLinkForeignKey focLinkFK = (FocLinkForeignKey) focLink;
      int key = focLinkFK.getUniqueForeignKeyFieldID();
      if(key != -1){
        foreignMap.put(key, masterObject);
        initForForeignKeyLink(foreignMap, focLinkFK, filter);
      }
    }else{
      init(masterObject, focLink, filter);
    }
  }
  
  public FocList(HashMap<Integer, FocObject> foreignObjectsMap, FocLinkForeignKey focLinkForeignKey, SQLFilter filter){
    super(Globals.getDefaultAccessControl());
    initForForeignKeyLink(foreignObjectsMap, focLinkForeignKey, filter);
  }
  
  public FocList(FocLink focLink, SQLFilter filter) {
    super(Globals.getDefaultAccessControl());
    init(null, focLink, filter);
  }
  
  public void dispose(){
    //elementHash_Dispose();
    
    if(filter != null){
      filter.dispose();
      filter = null;
    }
    
    if(sqlGroupBy != null){
    	sqlGroupBy.dispose();
    	sqlGroupBy = null;
    }
    
    if(listeners != null){
      for(int i=0; i<listeners.size(); i++){
        FocListener listener = (FocListener)listeners.get(i);
        if(listener != null){
          listener.dispose();
          listener = null;
        }
      }
      listeners.clear();
      listeners = null;
    }

    if(selectionPanel != null){
      //selectionPanel.dispose();
      selectionPanel = null;
    }

    if(attachedListPanel != null){
      //attachedListPanel.dispose();
      attachedListPanel = null;
    }

    if(listOrder != null){
      //listOrder.dispose();
      listOrder = null;
    }
    orderComparator = null;

    if(focListListener != null){
      focListListener.dispose();
      focListListener = null;
    }
    
    selectionProperty = null;
    listRequestingTheSelection = null;

    if(arrayList != null){
      arrayList.clear();
      arrayList = null;
    }
    
    masterObject = null;
    foreignObjectsMap = null; //We dont dispose the FocObjects in the foreignObjectsMap
    
    focLink = null;
    fatherProperty = null;
    validityCheckObject = null;
    elementHash_Dispose();
    
    //Put the subjects in a temporary array then travers this array and dispose the subjects
    //cuase if we dispose them with the first method(the newSubjectIterator) we get a concurent modification exception.
    /*Iterator iter = newSubjectIterator();
    while(iter != null && iter.hasNext()){
      AccessSubject subject = (AccessSubject) iter.next();
      if (subject != null && subject instanceof FocObject) {
      	subject.dispose();
      }
    }*/
    ArrayList<AccessSubject> temporaryArrayList = new ArrayList<AccessSubject>();
    Iterator iter = newSubjectIterator();
    while(iter != null && iter.hasNext()){
      AccessSubject subject = (AccessSubject) iter.next();
      if (subject != null && subject instanceof FocObject) {
      	temporaryArrayList.add(subject);
      }
    }
    for(int i = 0; i < temporaryArrayList.size(); i++){
    	AccessSubject subject = temporaryArrayList.get(i);
    	if(subject != null){
    		subject.dispose();
    	}
    }
    super.dispose();
  }

	public boolean includeObject_ByListFilter(FocObject obj){
		return true;
	}
	
	protected void addLogicalDeleteFilter() {
		if(getFocDesc() != null && getFilter() != null) {
			if(!getFocDesc().isJoin()) {
				if(getFocDesc().isLogicalDeleteEnabled()) {
					String name = FField.adaptFieldNameToProvider(getFocDesc().getProvider(), FField.LOGICAL_DELETE_FIELD_NAME);
					getFilter().putAdditionalWhere("FOC_LOGICAL_DELETE_FILTER", name + " = 0 OR " + name + " IS NULL");
				}
			} else {
				String fullJoinWhere = getFullJoinWhereClause();
      	if(!Utils.isStringEmpty(fullJoinWhere)) getFilter().putAdditionalWhere("FOC_LOGICAL_DELETE_FILTER", fullJoinWhere);
			}
		}
	}
	
	protected String getFullJoinWhereClause() {
		String fullJoinWhere = "";
		if(getFocDesc().isJoin() && getFocDesc() instanceof ParsedFocDesc) {			
			Iterator<ParsedJoin> newItr = ((ParsedFocDesc)getFocDesc()).newJoinIterator();
			while(newItr != null && newItr.hasNext()) {
				ParsedJoin join = newItr.next();
				if(join != null && Utils.isStringEmpty(join.getOtherAlias()) && join.getWhere() != null && !Utils.isStringEmpty(join.getWhere())) { 
	    		if(!Utils.isStringEmpty(fullJoinWhere)) fullJoinWhere += " AND ";
	    		fullJoinWhere += join.getWhere();
				}
			}
		}
		return fullJoinWhere;
	}

  public void putSiteReadRightConditionIfRequired(){
	  //Add Site Acces Condition
	  if(getFocDesc().workflow_IsWorkflowSubject() && getFilter().getAdditionalWhere("SITE_READ_ACCESS") == null && !Globals.getApp().isWebServer()){
	  	
	  	FocList list = getFocDesc().workflow_NewSiteListWithReadAccess();
	  	if(list.size() > 0){
	  		FField areaFld1 = getFocDesc().getFieldByID(((IWorkflowDesc)getFocDesc()).iWorkflow_getWorkflowDesc().getFieldID_Site_1());
	  		FField areaFld2 = getFocDesc().getFieldByID(((IWorkflowDesc)getFocDesc()).iWorkflow_getWorkflowDesc().getFieldID_Site_2());
	
	  		StringBuffer cond1 = null;
	  		if(areaFld1 != null && !areaFld1.isReflectingField()){
		  		cond1 = new StringBuffer(areaFld1.getDBName());
		  		cond1.append(" in (");
		  		for(int i=0; i<list.size(); i++){
		  			FocObject site = (FocObject) list.getFocObject(i);
		  			if(i > 0) cond1.append(",");
		  			cond1.append(site.getReference().getLong());
		  		}
		  		cond1.append(")");
	  		}

	  		StringBuffer cond2 = null;
	  		if(areaFld2 != null && !areaFld2.isReflectingField()){
		  		cond2 = new StringBuffer(areaFld2.getDBName());
		  		cond2.append(" in (");
		  		for(int i=0; i<list.size(); i++){
		  			FocObject site = (FocObject) list.getFocObject(i);
		  			if(i > 0) cond2.append(",");
		  			cond2.append(site.getReference().getLong());
		  		}
		  		cond2.append(")");
	  		}

	  		String cond = null;
	  		if(cond1 != null && cond2 != null){
	  			cond = cond1+" OR "+ cond2;
	  		}else if(cond1 != null){
	  			cond = cond1.toString();
	  		}else if(cond2 != null){
	  			cond = cond2.toString();
	  		}
	  		
	  		getFilter().putAdditionalWhere("SITE_READ_ACCESS", cond);
	  	}else{
	  		getFilter().putAdditionalWhere("SITE_READ_ACCESS", "1>2");
	  	}
	  }
  }
  
  public void setListOrder(){
  	if(getListOrder() == null){
  		FocDesc focDesc = getFocDesc();
  		if(focDesc.getKeyFieldsSize() > 0){
  			FocListOrder order = new FocListOrder();
	  		for(int i=0; i<focDesc.getKeyFieldsSize(); i++){
	  			FField fld = focDesc.getKeyFieldAt(i);
	  			order.addField(FFieldPath.newFieldPath(fld.getID()));
	  		}
	  		setListOrder(order);
  		}
  	}
  }
  
  public boolean isSortedByOrder(){
  	boolean 			isSortedByorder = false;
  	FocListOrder 	order 					= getListOrder();
  	if(order != null && order.getFieldsNumber() > 0){
  		FFieldPath path = order.getFieldAt(0);
  		if(path != null && path.size() == 1){
  			int fld = path.get(0);
  			isSortedByorder = fld == FField.FLD_ORDER;
  		}
  	}
  	return isSortedByorder;
  }
  
  private void setForeignObjectsMap(HashMap<Integer, FocObject> foreignObjects){
    this.foreignObjectsMap = foreignObjects;
  }
  
  //Should be private and have only a public funtion that returns an iterator on the keys
  public HashMap<Integer, FocObject> getForeignObjectsMap(){
    return this.foreignObjectsMap;
  }
  
  public FocObject getSingleTableDisplayObject(){
    return (focLink != null) ? focLink.getSingleTableDisplayObject(this) : null;
  }
  
  /**
   * @return returns the FocDesc object on the items of that list
   */
  public FocDesc getFocDesc() {
    return (focLink != null) ? focLink.getSlaveDesc() : null;
  }
  
  protected void setFocLink(FocLink focLink){
  	this.focLink = focLink;
  }
  
  public FocLink getFocLink(){
  	return this.focLink;
  }

  public boolean isContentValid(boolean displayMessage){
    /* boolean valid = true;
    Iterator iter = focObjectIterator();
    while(iter != null && iter.hasNext() && valid){
      FocObject obj = (FocObject) iter.next();
      if(!obj.isDeleted()){
        valid = obj.isContentValidWithPropagation();
      }
    }*/

    final class LocalIterObject{
      public boolean valid = true;
    }
    LocalIterObject userObj = new LocalIterObject();
    
    iterate(new FocListIterator(userObj){
      @Override
      public boolean treatElement(FocListElement element, FocObject focObj) {
        LocalIterObject userObj = (LocalIterObject) getObject(0);
        if(!focObj.isDeleted() && focObj.isDbResident()){
          userObj.valid = focObj.isContentValidWithPropagation();
        }
        return !userObj.valid;
      }
      
    });
    return userObj.valid;
  }

  public void synchronize(FocList srcFocList) {
    if(srcFocList != this){
      // Scan this and remove the items absent from source
      Iterator iterator = focObjectIterator();
      if (iterator != null) {
        while (iterator.hasNext()) {
          FocObject tarObj = (FocObject) iterator.next();
          if (!srcFocList.containsKey(tarObj)) {
            //removeCurrentObjectFromIterator(iterator);
            elementHash_removeCurrentObjectFromIterator(iterator,tarObj);
          }
        }
      }
  
      // Scan the source and push items absent from this
      iterator = null;
      iterator = srcFocList.focObjectIterator();
      if (iterator != null) {
        while (iterator.hasNext()) {
          FocObject srcObj = (FocObject) iterator.next();
          if (!this.containsKey(srcObj)) {
            this.add(srcObj);
          }
        }
      }
    }
  }

  public void copy(FocList sourceList){
    this.setCreated(true);  
    focLink.copy(this, sourceList);
  }
  
  public void addFocListener(FocListener focListener) {
    if(focListener != null){
      if(listeners == null){
        listeners = new ArrayList<FocListener>(1);
      }
      if(listeners != null){
        listeners.add(focListener);
      }
    }
  }

  public void removeFocListener(FocListener focListener) {
    if(focListener != null){
      if(listeners != null){
        listeners.remove(focListener);
      }
    }
  }
  
  public void fireEvent(FocObject subject, int id) {
  	if(!isSleepListeners()){
	    FocEvent event = new FocEvent(this, FocEvent.composeId(FocEvent.TYPE_LIST, id), null);
	    event.setEventSubject(subject);
	    if (listeners != null) {
	      for (int i = 0; i < listeners.size(); i++) {
	        FocListener listener = (FocListener) listeners.get(i);
	        if (listener != null) {
	          listener.focActionPerformed(event);
	        }
	      }
	    }
	    if (this.fatherProperty != null) {
	      fatherProperty.notifyListeners();
	    }
  	}
  }
  
  public FAbstractListPanel getSelectionPanel(boolean withSeletionCheckBox) {
  	return getSelectionPanel(withSeletionCheckBox, viewIDForSelectionBrowse);
  }
  
  public FAbstractListPanel getSelectionPanel(boolean withSeletionCheckBox, int viewIDForBrowser_Local) {
    if(selectionPanel != null){
      selectionPanel.dispose();
      selectionPanel = null;      
    }
    FocDesc focDesc = this.getFocDesc();
    if (focDesc != null) {
      selectionPanel = (FAbstractListPanel) focDesc.callNewBrowsePanel(this, viewIDForBrowser_Local);
      if(selectionPanel != null){
        selectionPanel.requestFocusOnCurrentItem();
      }
    }
    attachedListPanel = selectionPanel; 
    return selectionPanel;
  }  
  
  public FocObject getSelectedObject() {
    FocObject selectedObj = null;
    if (attachedListPanel != null) {
      selectedObj = attachedListPanel.getSelectedObject();
    }
    return selectedObj;
  }

  /**
   * @return SQLFilter 
   */
  public SQLFilter getFilter() {
    return filter;
  }
  
  /**
   * @return FocObject considered as Master Object of that list 
   */
  public FocObject getMasterObject() {
    return masterObject;
  }
  
  public FocObject getTheUniqueForeignObject(){
    FocObject uniqueForeignObject = null;
    HashMap<Integer, FocObject> foreignObjectsMap = getForeignObjectsMap();
    if(foreignObjectsMap != null && foreignObjectsMap.size() == 1){
      Iterator<FocObject> iter = foreignObjectsMap.values().iterator();
      if(iter != null && iter.hasNext()){
        uniqueForeignObject = iter.next();
      }
    }
    return uniqueForeignObject;
  }
  
  /*public FocObject getForeignObject(){
    return foreignObject;
  }*/

  public void setSelectionProperty(FObject selectionProperty) {
    this.selectionProperty = selectionProperty;
    if(selectionPanel != null){
      selectionPanel.requestFocusOnCurrentItem();
    }
  }

  public void validateSelectedObject() {
    if (selectionProperty != null) {
      FocObject focObj = getSelectedObject();
      if(focObj != null){
        selectionProperty.setObject(focObj);
        if(listRequestingTheSelection != null && listRequestingTheSelection.selectionPanel != null){
          listRequestingTheSelection.selectionPanel.requestFocusOnCurrentItem();          
        }
      }
    }
  }
  
  public void cancellingSelection(){
    if (selectionProperty != null) {
      selectionProperty.setObject(selectionProperty.getObject());
      if(listRequestingTheSelection != null && listRequestingTheSelection.selectionPanel != null){
        listRequestingTheSelection.selectionPanel.requestFocusOnCurrentItem();          
      }
    }    
  }
  
  /**
   * @return Returns the selectionProperty.
   */
  public FObject getSelectionProperty() {
    return selectionProperty;
  }

  /**
   * @param fatherProperty
   *          The fatherProperty to set.
   */
  public void setFatherProperty(FList fatherProperty) {
    this.fatherProperty = fatherProperty;
  }

  /**
   * @param masterObject
   *          The masterObject to set.
   */
  public void setMasterObject(FocObject masterObject) {
    this.masterObject = masterObject;
  }
   
  public synchronized void backupAllObjects(){
    Iterator iter = this.focObjectIterator();
    while(iter != null && iter.hasNext()){
      FocObject obj = (FocObject) iter.next();
      obj.backup();
    }
  }
  
  /**
   * @return Returns the listRequestingTheSelection.
   */
  public FocList getListRequestingTheSelection() {
    return listRequestingTheSelection;
  }
  
  /**
   * @param listRequestingTheSelection The listRequestingTheSelection to set.
   */
  public void setListRequestingTheSelection(FocList listRequestingTheSelection) {
    this.listRequestingTheSelection = listRequestingTheSelection;
  }
  /**
   * @return Returns the attachedListPanel.
   */
  public FAbstractListPanel getAttachedListPanel() {
    return attachedListPanel;
  }
  /**
   * @param attachedListPanel The attachedListPanel to set.
   */
  public void setAttachedListPanel(FAbstractListPanel attachedListPanel) {
    this.attachedListPanel = attachedListPanel;
  }

  public void removeAttachedListPanel(FAbstractListPanel attachedListPanel) {
    if(attachedListPanel == this.attachedListPanel){
      this.attachedListPanel = null;
    }
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // ARRAY
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  private void array_Add(FocListElement focObj) {
    if(focObj != null && arrayList != null){
      arrayList.add(focObj);
    }
  }
  
  private void array_Remove(FocListElement focObj) {
    if(focObj != null && arrayList != null){
      arrayList.remove(focObj);
    }
  }
  
  public void switchElementsAtPositions(int pos1, int pos2){
  	if(pos1 != pos2 && pos1 >= 0 && pos2 >= 0 && pos1<size() && pos2<size()){
	  	FocListElement temp = arrayList.get(pos1);
	  	arrayList.set(pos1, arrayList.get(pos2));
	  	arrayList.set(pos2, temp);
  	}
  }
  
  private synchronized ArrayList getArrayList() {
    // if the array list is null we construct it
    // by scaning the elements and putting them in an arrayList.
    // The array List is set to null when a major modification of the real list
    // is made.
    if (arrayList == null) {
      arrayList = new ArrayList<FocListElement>();
      Iterator iterator = listElementIterator();
      while (iterator != null && iterator.hasNext()) {
        FocListElement focObj = (FocListElement) iterator.next();
        if (focObj != null && !focObj.isHide()) {
          array_Add(focObj);
        }
      }
      sort();
    }
    return arrayList;
  }

  public synchronized void rebuildArrayList() {
  	arrayList = null;
  	getArrayList();
  }
  
  @SuppressWarnings("unchecked")
	public synchronized void sort(){
  	if(!isSortDisabled()){
  		if(arrayList == null){
  			rebuildArrayList();
  		}
		  if(arrayList != null){
		    if(listOrder != null){
		      Collections.sort(arrayList, listOrder);
		    }else if(orderComparator != null){
		    	Collections.sort(arrayList, orderComparator);
		    }else{
		      Collections.sort(arrayList);
		    }
		  }
  	}
  }
  
  private ReferencesCollection getReferencesCollection_Vaadin(){
    if(referencesCollection_Vaadin == null){
      referencesCollection_Vaadin = new ReferencesCollection(this);
    }
    return referencesCollection_Vaadin; 
  }
  
  public FocListElement getFocListElement(FocObject focObject) {
    return elementHash_GetFocListElement(focObject);
  }

  public FocListElement getFocListElement(int i) {
    FocListElement retValue = null;
    ArrayList arrayList = getArrayList();
    if (arrayList != null && i<arrayList.size() && i>=0) {
      retValue = (FocListElement) arrayList.get(i);
    }
    return retValue;
  }

  public FocObject getFocObject(int i) {
    FocObject retValue = null;
    FocListElement le = getFocListElement(i);
    if (le != null) {
      retValue = le.getFocObject();
    }
    return retValue;
  }

  public int size() {
    int retValue = 0;
    ArrayList arrayList = getArrayList();
    if(arrayList != null){
      retValue = arrayList.size();
    }
    return retValue;
  }

  public int getRowForObject(FocObject obj){
    int row = -1;
    for(int i=0; i<size(); i++){
      FocObject focObj = getFocObject(i);
      if(focObj != null && focObj == obj){
        row = i;
        break;
      }
    }
    return row; 
  }
  
  public void elementMoved (int initialPosition, int finalPosition){
    if(finalPosition < size() && initialPosition < size() && finalPosition >= 0 && initialPosition >=0){
      if(initialPosition != finalPosition){
        ArrayList<FocListElement> arrayList    = getArrayList();
        FocListElement            element      = (FocListElement)arrayList.get(initialPosition);
        boolean                   hasOrder     = getFocDesc().hasOrderField();
      
        if(hasOrder){
	        if(initialPosition > finalPosition){
	          int orderBackup = arrayList.get(finalPosition).getFocObject().getOrderAutomatic();
	          for(int i = finalPosition; i < initialPosition; i++){
	          	arrayList.get(i).getFocObject().setOrder(arrayList.get(i+1).getFocObject().getOrderAutomatic());
	          }
	          element.getFocObject().setOrder(orderBackup);
	        }else{
	          int orderBackup = arrayList.get(finalPosition).getFocObject().getOrderAutomatic();
	        	for(int i = finalPosition; i > initialPosition; i--){
	          	arrayList.get(i).getFocObject().setOrder(arrayList.get(i-1).getFocObject().getOrderAutomatic());
	          }
	        	element.getFocObject().setOrder(orderBackup);
	        }
        }
        
        if(initialPosition > finalPosition){
          for(int i = initialPosition; i > finalPosition; i--){
            arrayList.set(i, arrayList.get(i-1));
          }
        }else{
          for(int i = initialPosition; i < finalPosition; i++){
          	arrayList.set(i, arrayList.get(i+1));
          }
        }
        arrayList.set(finalPosition, element);

        fireEvent(null, FocEvent.ID_ITEM_MODIFY);
      }
    }
  }

  /**
   * @return FocListOrder the list order
   */
  public FocListOrder getListOrder() {
    return listOrder;
  }

  /**
   * @param order
   */
  public void setListOrder(FocListOrder order) {
  	boolean sortNow = listOrder != order;
    listOrder = order;
    if(sortNow){
    	sort();
    }
    /*
    if(order != null){
      ListOrderListener orderListener = new ListOrderListener(this);
      focListListener = orderListener.newFocListListener();
    }
    */
  }

  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public boolean isEmpty(){
    return elementHash_isEmpty();
  }

  
  public Iterator focObjectIterator() {
    return elementHash_GetFocObjectIterator();
  }

  public Iterator listElementIterator() {
    return elementHash_GetListElementIterator();
  }
  
  public synchronized boolean iterate(IFocIterator iterator){
    Iterator iter = listElementIterator();
    boolean stop = false;
    while(!stop && iter != null && iter.hasNext()){
      FocListElement element = (FocListElement) iter.next();
      stop = iterator.treatElement(element);
    }
    return stop; 
  }

  /**
   * @param arg0
   * @return true if contains the key
   */
  public synchronized boolean containsKey(Object arg0) {//Attention
    return (elements != null) ? elements.containsKey(arg0) : false;
  }
  
  public synchronized boolean containsObject(FocObject focObj) {
    boolean exist = false;
    if (elements != null && focObj != null) {
      exist = elements.containsKey(focObj);
    }
    return exist;
  }
  
  
  public synchronized FocObject searchByPointer(FocObject obj) {
    FocObject foundObj = null;
    FocListElement foundElmt = elements != null ? (FocListElement) elements.get(obj) : null;
    foundObj = foundElmt != null ? foundElmt.getFocObject() : null;
    /*
    Iterator iter = elements.keySet().iterator();
    while(iter!=null && iter.hasNext()){
      FocObject currObj = (FocObject)iter.next();
    }
    */
    return foundObj;
  }
  
  public synchronized FocObject searchByUniqueKey(FocObject obj){
    FocObject foundObj = null;
    Iterator iter = elements != null ? elements.keySet().iterator() : null;
    while(iter!=null && iter.hasNext()){
      FocObject currObj = (FocObject)iter.next();
      if(currObj != null){
        if(obj != null && obj.compareUniqueKey(currObj) == 0){
          foundObj = currObj;
          break;
        }
      }
    }
    return foundObj;
  }

  /*public FocObject searchByReference(int ref){
    FocListElement elm = elementHash_GetFocListElement(ref);    
    return elm != null ? elm.getFocObject() : null; 
  }*/

  public synchronized FocListElement searchElementByReference(long ref){
    FocListElement elm = elementHash_GetFocListElement(ref);
    if (elm == null){//Attention la recherche peut etre peut transmis dans elementHash_gtFocListElement(ref)
      Iterator iter = elementHash_GetListElementIterator();
      while (iter.hasNext() && elm == null){
        FocListElement currElem = (FocListElement) iter.next();
        if (currElem.getFocObject().getReference().getLong() == ref){
          elm = currElem;
        }
      }
    }
    return elm;
  }
  
  public FocObject searchByReference(long ref){
  	return searchByReference(ref, true);
  }
  
  public synchronized FocObject searchByReference(long ref, boolean reloadIfCheckIfRecentEnough){
    FocListElement elm = elementHash_GetFocListElement(ref);
    //Globals.logString("dans search by reference : "+ (elm == null ? elm==null : elm.getFocObject().getReference().getInteger()));
    //Globals.logString("elements.size : " + elements.size() + "elementsByRef.size : " + elementsByRef.size()); 
    FocObject object = elm != null ? elm.getFocObject() : null;
    if(object == null){//Attention la recherche peut etre peut transmis dans elementHash_gtFocListElement(ref)
      Iterator iter = elements != null ? elements.keySet().iterator() : null;
      FocObject searchObject = null;
      while(iter != null && iter.hasNext()){
        searchObject = (FocObject)iter.next();
        if(searchObject.getReference().getLong() == ref){
          object = searchObject;
          break;
        }
      }
    }
    
    if (reloadIfCheckIfRecentEnough && object == null && isCheckIfRecentEnough()) {
    	reloadFromDB();
    	object = searchByReference(ref, false);
    }
    
    //Globals.logString("dans search by reference apres while : "+ (object == null));
    return object;
  }
  
  public FocObject searchByRealReferenceOnly(long ref){
    FocListElement elm = elementHash_GetFocListElement(ref);
    return elm!= null ? elm.getFocObject() : null;
  }
  
  /*public FocObject searchByProperyStringValue(int id, String strValue){
    FocObject foundObj = null;
    if(strValue != null){
      Iterator iter = elements.keySet().iterator();
      while(iter!=null && iter.hasNext()){
        FocObject currObj = (FocObject)iter.next();
        if(currObj != null){
          FProperty prop = currObj.getFocProperty(id);
          
          if(prop != null && prop.getString() != null && prop.getString().compareTo(strValue) == 0){
            foundObj = currObj;
            break;
          }
        }
      }
    }
    return foundObj;
  }*/
  
  public FocObject searchByPropertyStringValue(String fieldName, String strValue){
  	FField fld = getFocDesc().getFieldByName(fieldName);
  	return fld != null ? searchByPropertyStringValue(fld.getID(), strValue, true) : null;
  }

  public FocObject searchByPropertyStringValue(int id, String strValue){
  	return searchByPropertyStringValue(id, strValue, true);
  }

  public FocObject searchByPropertyStringValue(String fieldName, String strValue, boolean ignoreCase){
  	FField fld = getFocDesc().getFieldByName(fieldName);
  	return fld != null ? searchByPropertyStringValue(fld.getID(), strValue, ignoreCase) : null;
  }
  
  public FocObject searchByPropertyStringValue(int id, String strValue, boolean ignoreCase, boolean trim){
  	return searchByPropertyStringValue(id, strValue, ignoreCase, trim, false);
  }
  
  public FocObject searchByPropertyStringValue(int id, String strValue, boolean ignoreCase, boolean trim, boolean sqlString){
    FocObject foundObj = null;
//    Globals.logString("SEARCHING FOR : "+strValue);
    if(strValue != null){
      Iterator iter = elements != null ? elements.keySet().iterator() : null;
      while(iter!=null && iter.hasNext()){
        FocObject currObj = (FocObject)iter.next();
        if(currObj != null){
          FProperty prop = currObj.getFocProperty(id);
          
          String propString = null;
          if(prop != null){
          	if(sqlString){
          		propString = prop.getSqlString();
          		if(propString.length() > 2);
          		propString = propString.substring(1,propString.length()-1);
          	}else{
          		propString = prop.getString();
          	}
          	if(trim && propString != null){
          		propString = propString.trim();
          	}
//          	Globals.logString("    current material code : "+propString);
          }
          
          if(ignoreCase){
          	if(propString != null && propString.toUpperCase().compareTo(strValue.toUpperCase()) == 0){
	            foundObj = currObj;
	            break;
	          }
          }else{
	          if(propString != null && propString.compareTo(strValue) == 0){
	            foundObj = currObj;
	            break;
	          }
          }
        }
      }
    }
    return foundObj;
  }

  public FocObject searchByPropertyStringValue(int id, String strValue, boolean ignoreCase){
    return searchByPropertyStringValue(id, strValue, ignoreCase, false);
  }
  
  public FocObject searchByPropertyBooleanValue(String fieldName, boolean boolValue){
  	FField fld = getFocDesc() != null ? getFocDesc().getFieldByName(fieldName) : null;
    FocObject foundObj = fld != null ? searchByPropertyBooleanValue(fld.getID(), boolValue) : null;
    return foundObj;
  }
  
  public FocObject searchByPropertyBooleanValue(int id, boolean boolValue){
    FocObject foundObj = null;
    Iterator iter = elements != null ? elements.keySet().iterator() : null;
    while(iter!=null && iter.hasNext()){
      FocObject currObj = (FocObject)iter.next();
      if(currObj != null){
        boolean currBool = currObj.getPropertyBoolean(id);
        if(currBool == boolValue){
          foundObj = currObj;
          break;
        }
      }
    }
    return foundObj;
  }
  
  public FocObject searchByPropertyDateValue(int id, java.sql.Date date){
    FocObject foundObj = null;
    if(date != null){
      Iterator iter = elements != null ? elements.keySet().iterator() : null;
      while(iter!=null && iter.hasNext()){
        FocObject currObj = (FocObject)iter.next();
        if(currObj != null){
          FDate prop = (FDate) currObj.getFocProperty(id);
          if(prop != null && prop.getDate().compareTo(date) == 0){
            foundObj = currObj;
            break;
          }
        }
      }
    }
    return foundObj;
  }
  
  public FocObject searchByPropertyIntValue(String fieldName, int value){
  	FField fld = getFocDesc().getFieldByName(fieldName);
  	return fld != null ? searchByPropertyIntValue(fld.getID(), value) : null;
  }

  public FocObject searchByPropertyIntValue(int id, int value){
    FocObject foundObj = null;
    Iterator iter = elements != null ? elements.keySet().iterator() : null;
    while(iter!=null && iter.hasNext()){
      FocObject currObj = (FocObject)iter.next();
      if(currObj != null){
        FInt prop = (FInt) currObj.getFocProperty(id);
        if(prop != null && prop.getInteger() == value){
          foundObj = currObj;
          break;
        }
      }
    }
    return foundObj;
  }
  
  public FocObject searchByPropertyObjectValue(String fieldName, FocObject propObjValue){
  	FField fld = getFocDesc().getFieldByName(fieldName);
  	return fld != null ? searchByPropertyObjectValue(fld.getID(), propObjValue) : null;
  }
  
  public FocObject searchByPropertyObjectValue(int id, FocObject propObjValue){
    FocObject foundObj = null;
    if(propObjValue != null){
      Iterator iter = elements != null ? elements.keySet().iterator() : null;
      while(iter!=null && iter.hasNext()){
        FocObject currObj = (FocObject)iter.next();
        if(currObj != null){
          FProperty prop = currObj.getFocProperty(id);
          if(FObject.class.isInstance(prop)){
            FObject objProp = (FObject) prop;
            objProp.getObject_CreateIfNeeded();
          }
          //B-HASHCODE
          FocObject obj0 = (FocObject)prop.getObject();
          if(prop != null && obj0 != null && obj0.compareTo(propObjValue) == 0){
            //E-HASHCODE            
            foundObj = currObj;
            break;
          }
        }
      }
    }
    return foundObj;
  }
  
  public FocObject searchByPropertyObjectReference(String fieldName, long ref){
  	FocDesc focDesc = getFocDesc();
  	FField field = focDesc != null ? focDesc.getFieldByName(fieldName) : null;
  	return field != null ? searchByPropertyObjectReference(field.getID(), ref) : null;
  }
  
  public FocObject searchByPropertyObjectReference(int id, long ref){
    FocObject foundObj = null;
    Iterator iter = elements != null ? elements.keySet().iterator() : null;
    while(iter!=null && iter.hasNext() && foundObj == null){
      FocObject currObj = (FocObject)iter.next();
      if(currObj != null){
        FProperty prop = currObj.getFocProperty(id);
        if(FObject.class.isInstance(prop)){
          FObject objProp = (FObject) prop;
          if(objProp.getLocalReferenceInt() == ref){
          	foundObj = currObj;
          }
        } else if(FReference.class.isInstance(prop)){
          FReference objProp = (FReference) prop;
          if(objProp.getLong() == ref){
          	foundObj = currObj;
          }
        }
      }
    }
    return foundObj;
  }
  
  public FocObject searchByPropertiesValue(int id1, Object value1, int id2, Object value2){
    FocObject foundObj = null;
    Iterator iter = elements != null ? elements.keySet().iterator() : null;
    while(iter!=null && iter.hasNext()){
      FocObject currObj = (FocObject)iter.next();
      if(currObj != null){
        FProperty prop = currObj.getFocProperty(id1);
        if(prop != null && prop.getObject().equals( value1)){
        	prop = currObj.getFocProperty(id2);
          if(prop != null && prop.getObject().equals( value2)){
	          foundObj = currObj;
	          break;
          }
        }
      }
    }
    return foundObj;
  }
  
  public void fireItemReferenceModification(FocObject focObj, long newRef){
		fireEvent(focObj, FocEvent.ID_BEFORE_REFERENCE_SET);
  }
  
  public void add(FocObject focObj) {
  	add(focObj, true);
  }
  
  public synchronized void add(FocObject focObj, boolean withFatherSubject) {
    //System.out.println("LIST - Adding");
    if (focObj != null) {
      
      //2012 I added the ++
      if(focObj.setTemporaryReferenceIfNeeded(-nextTempReference)){
        nextTempReference ++;
      }              
      //focObj.setTemporaryReferenceIfNeeded(nextTempReference++);
      
      if(!isCollectionBehaviour()) focObj.setMasterObject(masterObject);
      
      if(!containsObject(focObj)){
      	if(isWaitForValidationToAddObject()) {
      		removeDisposeDependentObject(focObj);
      	}
      	
        FInt fInt = focObj.getOrderProperty();
        if(fInt != null && fInt.getInteger() == 0){
          focObj.setOrder(getNextOrder());  
        }
        
        FocListElement focListElmt = new FocListElement(focObj, false);
        //elements.put(focObj, focListElmt);
        elementHash_Put(focListElmt);
        array_Add(focListElmt);
        if(isAutoRefresh()){
        	focObj.setFreshColor(true);
        }
        if(!isCollectionBehaviour()){
          if(isDirectImpactOnDatabase()){
            focObj.forceControler(true);
          }
          if(withFatherSubject) focObj.setFatherSubject(this);
        }
        if(!isDisableReSortAfterAdd()){
          arrayList = null;
        }
        fireEvent(focObj, FocEvent.ID_ITEM_ADD);
      }

      // Keep next temp reference max
      FReference fInt = focObj.getReference();
//TEMPREF 2012      
//      if (fInt != null && fInt.getInteger() >= nextTempReference) {
//        nextTempReference = fInt.getInteger() + 1;
//      }
      
      // ---------------------------
    }
    /*if(!isDisableReSortAfterAdd()){
      arrayList = null;
    }*/
  }
    
  private void resetNextOrder(){
  	nextOrder = 0;
  }
  
  private int getNextOrder(){
    if(getFocDesc().hasOrderField()){
    	if(nextOrder == 0){ 
	    	Iterator iter = listElementIterator();
	    	while(iter != null && iter.hasNext()){
	    		FocListElement elmt = (FocListElement) iter.next();
	    		nextOrder = Math.max(nextOrder, elmt.getFocObject().getOrderProperty().getInteger());
	    	}
    	}

    	nextOrder = nextOrder+1;
    }
    return nextOrder;
  }

  public synchronized void remove(FocObject focObj) {//Attention il faut enlever de elementByRefAussi
    
    if(elements != null && focObj != null){
      //focObj.setFatherSubject(null);
      //Object debugObj = elements.remove(focObj);
      
      FocListElement elementRemoved = elementHash_remove(focObj);
      if(elementRemoved == null){
        //Globals.logString("null debug obj");
      }else{
	      //arrayList = null;
	      array_Remove(elementRemoved);
	
	      fireEvent(focObj, FocEvent.ID_ITEM_REMOVE);
      	if(focObj.getFatherSubject() == null){
          elementRemoved.dispose(false);      		
      	}
      }
    }
  }
  
  public void removeCurrentObjectFromIterator(Iterator iter) {
    iter.remove();
    arrayList = null;
  }
  
  public void elementHash_RemoveFromReferencesMap(FocObject focObject) {
		if(elementsByRef != null && focObject != null && focObject.getReference() != null){
			elementsByRef.remove(focObject.getReference().getLong());
		}
  }

  public void elementHash_AddToReferencesMap(FocObject focObject) {
		if(elementsByRef != null && focObject != null && focObject.getReference() != null){
			FocListElement element = getFocListElement(focObject);
			if (element != null) {
				elementsByRef.put(focObject.getReference().getLong(), element);
			}
		}
  }
  
  public void elementHash_removeCurrentObjectFromIterator(Iterator iter,FocObject focObj) {
    FocListElement elemToRemove = elementHash_GetFocListElement(focObj);
    //TEMPREF 2012
    //if(focObj.hasRealReference()){
      elementsByRef.remove(focObj.getReference().getLong());
    //}
    try{
      iter.remove();
    }catch(Exception e){
      Globals.logException(e);      
    }
    arrayList = null;
    elemToRemove.dispose(isCollectionBehaviour());
    /*if(isCollectionBehaviour()){
      elemToRemove.disposeLeaveFocObject();
    }else{
      elemToRemove.dispose();
    }*/
  }
  
  public synchronized void removeAll() {
    
    if (elements != null && elements.size() > 0) {
      elementHash_DisposeAndClear();      
      arrayList = null;
      fireEvent(null, FocEvent.ID_ITEM_REMOVE);
      setLoaded(false);
    }
  }
    
  public FocObject newItemInternal(FProperty identifier, boolean disconnected) {

    FocDesc desc = getFocDesc();
    
    FocObject newFoc = null;
    if (desc != null) {

      FocConstructor constr = new FocConstructor(desc, identifier != null ? identifier.getObject() : null, masterObject);
      newFoc = constr.newItem();
      fillForeignObjectsProperties(newFoc);
      newFoc.setDbResident(isDbResident());      

      //TEMPREF 2012 this condition was inside the !disconnected
      if(newFoc.setTemporaryReferenceIfNeeded(-nextTempReference)){
        nextTempReference ++;
      }              
      
      if(!disconnected){
      	connectItem(newFoc);
        if(filter != null){
          filter.copy_SelectedFieldsValues_From_Template_To_Object(newFoc);
        }
      }else if(Globals.getApp().isWebServer()){
        if(filter != null){
          filter.copy_SelectedFieldsValues_From_Template_To_Object(newFoc);
        }
      }
    }
    
    return newFoc;
  }
  
  public void connectItem(FocObject newFoc){
    newFoc.setFatherSubject(this);
    if (isDirectImpactOnDatabase()) {
    	newFoc.forceControler(true);
    }
    if(!isLoading()) {
    	newFoc.setCreated(true);
    	if(isWaitForValidationToAddObject()){
    		addDisposeDependentObject(newFoc);
    	}
    }
  }
  
  protected void fillForeignObjectsProperties(FocObject newFocObj){
    if(foreignObjectsMap != null && foreignObjectsMap.size() > 0){
      Iterator<Integer> iter = foreignObjectsMap.keySet().iterator();
      while(iter != null && iter.hasNext()){
        int key = (Integer)iter.next();
        FocObject foreignObject = foreignObjectsMap.get(key);
        newFocObj.setPropertyObject(key, foreignObject);
      }
    }
  }
  
  public FocObject newItem(FProperty identifier) {
    return newItemInternal(identifier, false);
  }
  
  public FocObject newEmptyItem() {
    FocObject newItem = newItem(null); 
    return newItem;
  }

  public FocObject newEmptyDisconnectedItem() {
    return newItemInternal(null, true);
  }
  
  public FocObject getOrInsertAnItem(){
    FocObject obj = null;
    Iterator iter = focObjectIterator();
    while(iter != null && iter.hasNext()){
      obj = (FocObject) iter.next();
      break;
    }
    if(obj == null){
      obj = newItem(null);
      add(obj);
    }
    return obj;
  }

  public synchronized FocObject getAnyItem(){
    FocObject obj = null;
    Iterator iter = focObjectIterator();
    while(iter != null && iter.hasNext()){
      obj = (FocObject) iter.next();
      break;
    }
    return obj;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // ACCESS
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  protected void statusModification(int statusModified) {
  }
  
  public void childStatusModification(AccessSubject subject){
    if(subject != null){
    	if(subject.isModified()){
    	  if(!subject.isControler()){
    	    setModified(true);
    	  }
    	  try{
    	    fireEvent((FocObject) subject, FocEvent.ID_ITEM_MODIFY);
    	  }
    	  catch (Exception e) {
    	    Globals.logException(e);
        }
    	}
    	if(subject.isCreated()) {
    		if(!isWaitForValidationToAddObject()){
    			this.add((FocObject) subject);
    		}
      }else if (subject.isDeleted()){
      	boolean doRemove = false;
        if(this.isDirectImpactOnDatabase()){
          subject.commitStatusToDatabase();
          this.removeSubject(subject);
          subject.setFatherSubject(null);//2019-06-17 Without this line the deleted object in DirectImpactDatabases are never disposed
          doRemove = subject.isDeletionExecuted();
        }else{
        	doRemove = true;
        }
        if(doRemove){
        	this.remove((FocObject) subject);
        }
      }
    }
  }

  public void childStatusUndo(AccessSubject childSubject) {
    if (childSubject.isCreated()) {
      childSubject.setFatherSubject(null); //If I keep this line I get a concurrent modification exception
      childSubject.resetStatus();
      remove((FocObject) childSubject);
    } else if (childSubject.isDeleted() && !childSubject.isDeletedAfterCreation()) {
      add((FocObject) childSubject);
    }
  }

  //BDebug
  public void writeDebugInfo(){
    if(true || getFocDesc().getStorageName().compareTo("LOCATION_TYPE") == 0){
      /*
      for(int i=0; i<size(); i++){
        FocObject obj = getFocObject(i);
        String status = "";
        if(obj.isCreated()){
          status = "Created";
        }
        if(obj.isModified()){
          status += "Modified";
        }
        if(obj.isDeleted()){
          status += "Deleted";
        }
        Globals.logString("Array["+i+"] = "+obj.getReference().getString()+" status : "+status);
      }
      */
      /*
      Iterator iter = focObjectIterator();
      while(iter != null && iter.hasNext()){
        FocObject obj = (FocObject) iter.next();
        String status = "";
        if(obj.isCreated()){
          status = "Created";
        }
        if(obj.isModified()){
          status += "Modified";
        }
        if(obj.isDeleted()){
          status += "Deleted";
        }
        Globals.logString("Obj[] = "+obj.getReference().getString()+" status : "+status);
      }
      */
      Iterator iter = newSubjectIterator();
      while(iter != null && iter.hasNext()){
        FocObject obj = (FocObject) iter.next();
        String status = "";
        if(obj.isCreated()){
          status = "Created";
        }
        if(obj.isModified()){
          status += "Modified";
        }
        if(obj.isDeleted()){
          status += "Deleted";
        }
        Globals.logString("Sub[] = "+obj.getReference().getString()+" status : "+status+" " + obj);
      }
    }
  }
  //EDebug

  private int getRevisionVersion(FocObject obj) {
    FProperty prop = getFocDesc().getRevisionPath().getPropertyFromObject(obj);
    return prop != null ? prop.getInteger() : 0; 
  }
  
  public boolean commitStatusToDatabaseWithPropagation() {
  	boolean error = false;
    Globals.setMouseComputing(true);
    //writeDebugInfo();
    
    //SCAN THE FOC OBJECTS AND DO YOUR LOGIC OF INSERT DELETE uPDATE 
    //this.
    if (getFocDesc().isRevisionSupportEnabled()){
      ArrayList<FocObject> focObjects = new ArrayList<FocObject>();
      
      Iterator iter = this.newSubjectIterator();
      while(iter != null && iter.hasNext()){
        FocObject focObj = (FocObject)iter.next();
        if(getRevisionVersion(focObj) != focObj.getPropertyInteger(FField.CREATION_REVISION_FIELD_ID)){
          /*if( focObj.isCreated()){
            focObj.resetStatus();
            focObj.setCreationRevisionData();  
          }else*/ if( focObj.isDeleted()){
            focObj.resetStatus();
            focObj.setDeletionRevisionData(); 
          }else if(focObj.isModified()){
            focObjects.add(focObj);  
          }
        }
      }
    
      for(int i = 0; i < focObjects.size(); i++){
        FocObject focObj = focObjects.get(i);
        FocObject newFocObj = this.newEmptyItem();
        newFocObj.copyPropertiesFrom(focObj);
        focObj.restore();
        focObj.setDeletionRevisionData();
        newFocObj.setModificationRevisionData(focObj);
        newFocObj.save();
        focObj.save();
      }
    }
    
    adjustOrderField();
    
    error = error || super.commitStatusToDatabaseWithPropagation();
    Globals.setMouseComputing(false);
    return error;
  }
  
  public boolean commitStatusToDatabase() {
  	boolean error = false;
    if (isModified() && !isDirectImpactOnDatabase()) {
      this.saveDB();
    }
    return error;
  }
  
  public void childValidated(AccessSubject subject, char initialStatusFlags) {
    if (subject != null) {
      if (AccessSubject.isCreated(initialStatusFlags)) {
      	if(isWaitForValidationToAddObject()) {
      		this.add((FocObject) subject);
      	} 
      }

      if (this.isDirectImpactOnDatabase()) {
        subject.commitStatusToDatabaseWithPropagation();
      }
    }
  }

  public void undoStatus() {
  }

  public void doBackup(){
  }  
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DATABASE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo  

  public void reloadFromDB() {
  	reloadFromDB(0);//0 because not incremental
  }
  
  public synchronized void reloadFromDB(long refToUpdateIncementally) {
    //this.fireEvent(FocEvent.ID_BEFORE_LOAD);
  	if(isDbResident()){
  		lastLoadTime = System.currentTimeMillis();
    	Globals.setMouseComputing(true);
    	//putSiteReadRightConditionIfRequired();
	    boolean backup = false;
	    if(fatherProperty != null && fatherProperty.getFocObject() != null){
	      backup = fatherProperty.getFocObject().isModified();
	    }
      boolean loaded = focLink.loadDB(this, refToUpdateIncementally);
      setLoaded(loaded);
      resetNextOrder();
	    //fireEvent(null, FocEvent.ID_ITEM_MODIFY);//attention attention
	    if(fatherProperty != null && fatherProperty.getFocObject() != null){
	      fatherProperty.getFocObject().setModified(backup);
	    }
	    Globals.setMouseComputing(false);
  	}
    //this.fireEvent(FocEvent.ID_LOAD);    
  }
  
  public synchronized void deleteFromDB() {
    boolean loaded = focLink.deleteDB(this);
    setLoaded(loaded);
  }
  
  private boolean isCheckIfRecentEnough() {
  	return checkIfRecentEnough;
  }

	public void setCheckIfRecentEnough(boolean checkIfRecentEnough) {
		this.checkIfRecentEnough = checkIfRecentEnough;
	}
	
  public boolean isRecentEnough() {
  	boolean recentEnough = true;
  	if (isCheckIfRecentEnough()) {
  		recentEnough = (System.currentTimeMillis() - lastLoadTime) < EXPIRY_TIME;
  	}
  	return recentEnough;
  }
  
  public boolean needsToBeLoaded(){
  	return (!isLoaded() || !isRecentEnough()) && !this.isCreated();
  }

  public synchronized boolean loadIfNotLoadedFromDB() {
    boolean reloaded = false;
    if(needsToBeLoaded()){
      reloadFromDB();
      reloaded = true;
    }
    return reloaded ;
  }

  public synchronized void saveDB() {
    if (focLink != null) {
      focLink.saveDB(this);
      //B-DUP
      setLoaded(true);
      //E-DUP
    }
  }  
      
  public void reactToNewSelection(FTable table, FocObject selObj, boolean focusLost){
    if(!table.isEditing() && isDirectImpactOnDatabase() && isDirectlyEditable()){
    	//ATTENTION ATTENTION AntoineS a enleve ca pour les messages inutils dans devTask
      for(int i=0; i<size(); i++){
        FocObject obj = getFocObject(i);
        if((selObj != obj || focusLost) && obj.isCreated() || obj.isModified()){
          if(obj.isContentValidWithPropagation()){
            obj.validate(true);
          }
        }
      }
    }
  }
  
  public Object getValidityCheckObject() {
    return validityCheckObject;
  }
  
  public void setValidityCheckObject(Object validityCheckObject) {
    this.validityCheckObject = validityCheckObject;
  }
      
  public int getViewIDForSelectionBrowse() {
    return viewIDForSelectionBrowse;
  }
  
  public void setViewIDForSelectionBrowse(int viewIDForSelectionBrowse) {
    this.viewIDForSelectionBrowse = viewIDForSelectionBrowse;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // ELEMENTS
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo  
  
  private void elementHash_Init(){
  	if(elements == null){
	    elements = new ConcurrentHashMap<FocObject, FocListElement>();
	    elementsByRef = new ConcurrentHashMap<Long, FocListElement>();
  	}
  }
    
  private void elementHash_Dispose(){
    elementHash_DisposeAndClear();
    elements      = null;    
    elementsByRef = null;  
    arrayList     = null;
  }

  private void elementHash_DisposeAndClear(){
    if(elements != null){
      Iterator iter = listElementIterator();
      while(iter != null && iter.hasNext()){
        FocListElement elem = (FocListElement) iter.next();
        if(elem != null){
          elem.dispose(isCollectionBehaviour());
          elem = null;
        }
      }
      elements.clear();
    }
  }
  
  private FocListElement elementHash_GetFocListElement(FocObject focObj){
    return elements != null ? elements.get(focObj) : null;
  }

  private FocListElement elementHash_GetFocListElement(long ref){
  	FocListElement elem = elementsByRef != null ? elementsByRef.get(ref) : null;
    return elem;
  } 
  
  private boolean elementHash_isEmpty(){
    return elements == null || elements.size() == 0; 
  }

  private Iterator elementHash_GetFocObjectIterator(){
    Iterator iter = null;
    if(elements != null){
      iter = elements.keySet().iterator();
    }
    return iter; 
  }
  
  private Iterator elementHash_GetListElementIterator(){
    Iterator iter = null;
    if(elements != null){
      iter = elements.values().iterator();
    }
    return iter; 
  }
  
  private void elementHash_Put(FocListElement elem){
  	elementHash_Init();
    elements.put(elem.getFocObject(), elem);
    //TEMPREF 2012
    //if (elem.getFocObject().hasRealReference()){
    if(getFocDesc().getWithReference()){
    	elementsByRef.put(elem.getFocObject().getReference().getLong(), elem);
    }
    //}
  }
  
  private FocListElement elementHash_remove(FocObject focObj){
    FocListElement obj = null;
    //printDebug();    
    //Globals.logString("dans elementHash_remove : focObj.hasRealReference = " + focObj.hasRealReference() + "elementByRef.size : "+ elementsByRef.size()+" removing ref="+focObj.getReference().getInteger());
    if(/*focObj.hasRealReference() &&*/ getFocDesc().getWithReference() && elementsByRef != null){//TEMPREF 2012
      obj = elementsByRef.remove(focObj.getReference().getLong());
    }
    //Globals.logString("elementByRef.size : "+ elementsByRef.size() + (obj == null));
    if(elements != null){
    	obj = elements.remove(focObj);
    }
    return obj;
  }
  
  @SuppressWarnings("unused")
	private void printDebug(){
    Iterator iter = elementsByRef != null ? elementsByRef.keySet().iterator() : null;
    int i=0;
    Globals.logString("elements by ref:");
    while(iter.hasNext()){
      Integer inte = (Integer) iter.next();
      Globals.logString(" ref["+(i++)+"]= "+inte);
    }
  }

  public boolean isWithinDemoLimit(){
    return size() < -1;
  }
  
  public int getDEMO_MAX_SIZE() {
    return -1;
  }
  
  public int getFocListElementPosition(FocListElement focListElement){
    int position = -1;
    if(arrayList != null){
      position = arrayList.indexOf(focListElement);
    }
    return position;
  }

	public Comparator getOrderComparator() {
		return orderComparator;
	}

	public void setOrderComparator(Comparator orderComparator) {
		this.orderComparator = orderComparator;
	}
	
	public void setOrderByKeyFields(){
		if(getListOrder() == null && !getFocDesc().hasOrderField()){
			FocListOrder listOrder = new FocListOrder();
			FocFieldEnum enumer = getFocDesc().newFocFieldEnum(FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
			while(enumer != null && enumer.hasNext()){
				FField fld = enumer.nextField();
				if(fld != null && fld.getKey()){
					listOrder.addField(FFieldPath.newFieldPath(fld.getID()));	
				}
			}
			setListOrder(listOrder);
		}
	}

	public FocListGroupBy getSqlGroupBy() {
		if(sqlGroupBy == null){
			FocDesc focDesc = getFocDesc();
			if(focDesc != null && focDesc.getGroupBy() != null){
				return focDesc.getGroupBy();
			}
		}
		return sqlGroupBy;
	}

	public void setSqlGroupBy(FocListGroupBy sqlGroupBy) {
		this.sqlGroupBy = sqlGroupBy;
	}
	
  public boolean isWithDeprecation() {
    return (flags & FLG_WITH_DEPRICATION) != 0;
  }

  public void setWithDeprecation(boolean withDeprecation) {
    if(withDeprecation){
      flags = (char)(flags | FLG_WITH_DEPRICATION);
    }else{
      flags = (char)(flags & ~FLG_WITH_DEPRICATION);
    }
  }

  public boolean isSleepListeners() {
  	return (flags & FLG_SLEEP_LISTENERS) != 0;
  }
  
  public void setSleepListeners(boolean sleepListeners) {
    boolean forceListenerCall = !sleepListeners && isSleepListeners() ;
    if(sleepListeners){
      flags = (char)(flags | FLG_SLEEP_LISTENERS);
    }else{
      flags = (char)(flags & ~FLG_SLEEP_LISTENERS);
    }
    if(forceListenerCall){
      fireEvent(null, FocEvent.ID_WAIK_UP_LIST_LISTENERS);
    }
  }
  
  public boolean isDirectlyEditable() {
  	return (flags & FLG_DIRECTLY_EDITABLE) != 0;
  }
  
  public void setDirectlyEditable(boolean directlyEditable) {
    setWaitForValidationToAddObject(!directlyEditable);
    if(directlyEditable){
      flags = (char)(flags | FLG_DIRECTLY_EDITABLE);
    }else{
      flags = (char)(flags & ~FLG_DIRECTLY_EDITABLE);
    }
  }

  public void setInTableEditable(boolean inTableEditable){
  	setDirectlyEditable(inTableEditable);
  	setDirectImpactOnDatabase(!inTableEditable);
  }
  
  /**
   * @param loaded
   *          The loaded to set.
   */
  public void setLoaded(boolean loaded) {
    if(loaded){
      flags = (char)(flags | FLG_LOADED);
    }else{
      flags = (char)(flags & ~FLG_LOADED);
    }
  }
  
  public boolean isLoaded(){
  	return (flags & FLG_LOADED) != 0;
  }
  
  public void setSortDisabled(boolean loaded) {
    if(loaded){
      flags = (char)(flags | FLG_DISABLE_SORT);
    }else{
      flags = (char)(flags & ~FLG_DISABLE_SORT);
    }
  }
  
  public boolean isSortDisabled(){
  	return (flags & FLG_DISABLE_SORT) != 0;
  }

  public void setAutoRefresh(boolean loaded) {
    if(loaded){
      flags = (char)(flags | FLG_AUTO_REFRESH);
    }else{
      flags = (char)(flags & ~FLG_AUTO_REFRESH);
    }
    if(loaded){
    	for(int i=0; i<size(); i++){
    		FocObject elem = getFocObject(i);
    		elem.setFreshColor(false);
    	}
    }
  }
  
  public boolean isAutoRefresh(){
  	return (flags & FLG_AUTO_REFRESH) != 0;
  }

  public boolean isDirectImpactOnDatabase() {
  	return (flags & FLG_DIRECT_IMPACT_ON_DATABASE) != 0;
  }

  public void setDirectImpactOnDatabase(boolean directImpactOnDatabase) {
    if(directImpactOnDatabase){
      flags = (char)(flags | FLG_DIRECT_IMPACT_ON_DATABASE);
    }else{
      flags = (char)(flags & ~FLG_DIRECT_IMPACT_ON_DATABASE);
    }

    Iterator iter = focObjectIterator();
    if (iter != null && iter.hasNext()) {
      FocObject obj = (FocObject) iter.next();
      if (obj != null) {
        obj.forceControler(directImpactOnDatabase);
      }
    }
  }
 
  /**
   * @return Returns the waitForValidationToAddObject.
   */
  public boolean isWaitForValidationToAddObject() {
  	return (flags & FLG_WAIT_FOR_VALIDATION_TO_ADD) != 0;
  }

  /**
   * @param waitForValidationToAddObject
   *          The waitForValidationToAddObject to set.
   */
  public void setWaitForValidationToAddObject(boolean waitForValidationToAddObject) {
    if(waitForValidationToAddObject){
      flags = (char)(flags | FLG_WAIT_FOR_VALIDATION_TO_ADD);
    }else{
      flags = (char)(flags & ~FLG_WAIT_FOR_VALIDATION_TO_ADD);
    }
  }

  public boolean isKeepNewLineFocusUntilValidation(){
  	boolean keepNewLineFocusUntilValidation = (flags & FLG_KEEP_NEW_LINE_FOCUS_UNTIL_VALIDATION) != 0;
    return keepNewLineFocusUntilValidation || (isDirectlyEditable() && isDirectImpactOnDatabase());
  }

  public void setKeepNewLineFocusUntilValidation(boolean keepNewLineFocusUntilValidation){
    if(keepNewLineFocusUntilValidation){
      flags = (char)(flags | FLG_KEEP_NEW_LINE_FOCUS_UNTIL_VALIDATION);
    }else{
      flags = (char)(flags & ~FLG_KEEP_NEW_LINE_FOCUS_UNTIL_VALIDATION);
    }
  }

  public boolean isCollectionBehaviour() {
  	return (flags & FLG_COLLECTION_BEHAVIOUR) != 0;
  }  
  
  public void setCollectionBehaviour(boolean collectionBehaviour) {
    if(collectionBehaviour){
      flags = (char)(flags | FLG_COLLECTION_BEHAVIOUR);
    }else{
      flags = (char)(flags & ~FLG_COLLECTION_BEHAVIOUR);
    }
    setLoaded(true);
  }

  public boolean isDisableReSortAfterAdd() {
  	return (flags & FLG_DISABLE_RESORT_AFTER_ADD) != 0;
  }
  
  public void setDisableReSortAfterAdd(boolean disableReSortAfterAdd) {
    if(disableReSortAfterAdd){
      flags = (char)(flags | FLG_DISABLE_RESORT_AFTER_ADD);
    }else{
      flags = (char)(flags & ~FLG_DISABLE_RESORT_AFTER_ADD);
    }
  }
  
  public boolean isLoading() {
  	return (flags & FLG_IS_LOADING) != 0;
  }
  
  public void setLoading(boolean loading) {
    if(loading){
      flags = (char)(flags | FLG_IS_LOADING);
    }else{
      flags = (char)(flags & ~FLG_IS_LOADING);
    }
  }

	//--------------------------
	// IFocList implementation
	//--------------------------

  @Override
  public void iFocList_addFocObject(IFocObject iFocObject) {
		add((FocObject) iFocObject);
	}

  @Override
	public void iFocList_cancel() {
  	cancel();
	}

  @Override
	public void iFocList_dispose() {
  	dispose();
	}

  @Override
	public void iFocList_newAddedFocObject() {
  	FocObject obj = newEmptyItem();
  	add(obj);
	}

  @Override
	public IFocObject iFocList_newFocObject() {
		return newEmptyItem();
	}

  @SuppressWarnings("unchecked")
	@Override
	public Iterator<IFocObject> iFocList_newIterator() {
		return newSubjectIterator();
	}

  @Override
	public boolean iFocList_validate() {
		return validate(true);
	}

  private void adjustOrderField(){
		if(isSortedByOrder()){
			int maxOrder = -1; 
	  	for(int i=0; i<size(); i++){
	  		FocObject obj = getFocObject(i);
	  		int order = obj.getOrder();
	  		if(order <= maxOrder){
	  			order = maxOrder+1;
	  			obj.setOrder(order);
	  		}
	  		maxOrder = order; 
	  	}
		}
  }
  
  @Override
  public boolean validate(boolean checkValidity){
  	return isDbResident() ? super.validate(checkValidity) : true;
  }

  @Override
	public IFocObject iFocList_searchByPropertiesValues(String[] fieldNames, Object[] values) {
    FocObject foundObj = null;
    FField[]  fld      = null;
    
    Iterator iter = elements != null ? elements.keySet().iterator() : null;
    while(iter!=null && iter.hasNext()){
      FocObject currObj = (FocObject)iter.next();
      if(currObj != null){
      	boolean creatingFields = fld == null; 
      	if(creatingFields){
      		fld = new FField[fieldNames.length];	
      	}
      	
      	boolean found = true;
      	for(int i=0; i<fieldNames.length; i++){
      		FProperty prop = null;
      		if(creatingFields){
      			fld[i] = getFocDesc().getFieldByName(fieldNames[i]);
      		}
      		prop = fld[i] != null ? currObj.getFocProperty(fld[i].getID()) : null;

      		if(prop == null || !prop.getObject().equals(values[i])){
      			found = false;
      		}
      	}
      	
      	if(found){
      		foundObj = currObj;
      		break;
      	}
      }
    }
    return foundObj;
	}

	@Override
	public IFocObject iFocList_searchByPropertyValue(String fieldName, Object value) {
		String[] fieldNames = {fieldName};
		Object[] values     = {value};
		return iFocList_searchByPropertiesValues(fieldNames, values);
	}
	
	public synchronized void toJson(B01JsonBuilder builder){
		if(builder != null){
			builder.beginList();
			
			int start    = 0;
			int maxCount = 0;
			if(builder.getListStart() >= 0) {
				start = builder.getListStart();
			}
			if(builder.getListCount() > 0) {
				maxCount = builder.getListCount();
			}
			
			if(start < 0) start = 0;
			
			FField depricatedField = getFocDesc().getFieldByID(FField.FLD_DEPRECATED_FIELD);

			int count = 0;

			int readIndex = 0;
			
			for(int i=0; i<size() && (maxCount == 0 || count<maxCount); i++){
				FocObject focObj = getFocObject(i);
				if(			focObj != null 
						&& (depricatedField == null || !focObj.isDeprecated())
						&& (builder.getObjectFilter() == null || builder.getObjectFilter().includeObject(focObj))
						){
					
					if(readIndex >= start) {
						focObj.toJson_InList(builder);
						//focObj.toJson(builder);
						count++;
					}
					
					readIndex++;
				}
			}
			
			/*
			for(int i=start; i<size() && (maxCount == 0 || count<maxCount); i++){
				FocObject focObj = getFocObject(i);
				if(			focObj != null 
						&& (depricatedField == null || !focObj.isDeprecated())
						&& (builder.getObjectFilter() == null || builder.getObjectFilter().includeObject(focObj))
						){
					focObj.toJson(builder);
					count++;
				}
			}
			*/
			
			
			builder.endList();
		}
	}

	public synchronized int toJson_TotalCount(B01JsonBuilder builder){
		int count = 0;
		if(builder != null){
			FField depricatedField = getFocDesc().getFieldByID(FField.FLD_DEPRECATED_FIELD);
			
			for(int i=0; i<size(); i++){
				FocObject focObj = getFocObject(i);
				if(			focObj != null 
						&& (depricatedField == null || !focObj.isDeprecated())
						&& (builder.getObjectFilter() == null || builder.getObjectFilter().includeObject(focObj))
						){
					count++;
				}
			}
		}
		return count;
	}

	public IFocObject iFocList_searchByIntPropertiesValues(String[] fieldNames, Object[] values) {
		FocObject foundObj = null;
		FField[] fld = null;

		Iterator iter = elements != null ? elements.keySet().iterator() : null;
		while (iter != null && iter.hasNext()){
			FocObject currObj = (FocObject) iter.next();
			if(currObj != null){
				boolean creatingFields = fld == null;
				if(creatingFields){
					fld = new FField[fieldNames.length];
				}

				boolean found = true;
				for(int i = 0; i < fieldNames.length; i++){
					FProperty prop = null;
					if(creatingFields){
						fld[i] = getFocDesc().getFieldByName(fieldNames[i]);
					}
					prop = fld[i] != null ? currObj.getFocProperty(fld[i].getID()) : null;

					if(prop == null || !prop.getObject().equals(values[i])){
						found = false;
					}
				}

				if(found){
					foundObj = currObj;
					break;
				}
			}
		}
		return foundObj;
	}

	//-----------------------------------------------
	//-----------------------------------------------
	//-----------------------------------------------
	// VAADIN Container implementation
	//-----------------------------------------------
	//-----------------------------------------------
	//-----------------------------------------------
	
	@Override
	public Item getItem(Object itemId) {
		FocObject obj = null;
		if(itemId != null){
			long ref = ((Long)itemId).longValue();
			obj = searchByReference(ref);
		}
		return obj;
	}

	@Override
	public Collection<?> getContainerPropertyIds() {
		FocDesc focDesc = getFocDesc();
		return focDesc != null ? focDesc.vaadin_getFieldNames() : null;
	}

	@Override
	public Collection<?> getItemIds() {
	  Collection<Long> returnedArray = null;
    if(getFocDesc().isByCompany()){
      ArrayList<Long> refsArray = new ArrayList<Long>();      
      Company company = UserSession.getInstanceForThread().getCompany();

      int siteFieldID_1 = FField.NO_FIELD_ID;
      int siteFieldID_2 = FField.NO_FIELD_ID;
      
      if(getFocDesc() instanceof IWorkflowDesc){
        WorkflowDesc workflowDesc = ((IWorkflowDesc)getFocDesc()).iWorkflow_getWorkflowDesc();
        if(workflowDesc != null){
          siteFieldID_1 = workflowDesc.getFieldID_Site_1();
          siteFieldID_2 = workflowDesc.getFieldID_Site_2();
        }
      }
      
      for(int i=0; i<size(); i++){
        FocObject currentObj = getFocObject(i);
        if(currentObj.getCompany() == null || currentObj.getCompany().equalsRef(company)){
          boolean add = false;
          
          if(siteFieldID_1 != FField.NO_FIELD_ID){
            WFSite site = (WFSite) currentObj.getPropertyObject(siteFieldID_1);
            if(site != null && site.equalsRef(UserSession.getInstanceForThread().getSite())){
              add = true;
            }
          }
          
          if(siteFieldID_2 != FField.NO_FIELD_ID){
            WFSite site = (WFSite) currentObj.getPropertyObject(siteFieldID_2);
            if(site != null && site.equalsRef(UserSession.getInstanceForThread().getSite())){
              add = true;
            }
          }
          
          if(siteFieldID_1 == FField.NO_FIELD_ID && siteFieldID_2 == FField.NO_FIELD_ID){
            add = true;
          }
          
          if(add) refsArray.add(currentObj.getReference().getLong());
        }
      }
      returnedArray = refsArray;
    }else{
      returnedArray = getReferencesCollection_Vaadin();
    }
    
    return returnedArray;
	  
	  //FocUser user01Barmaja = Globals.getApp().getUser();
	  //return getReferencesCollection_Vaadin();
		//return elementsByRef != null ? elementsByRef.keySet() : new ArrayList<Integer>();
	}

	@Override
	public Property getContainerProperty(Object itemId, Object propertyId) {
		Property property = null;
		if(itemId != null && propertyId != null){
			long ref     = ((Long)itemId).longValue();
			//int fieldID = ((Integer)propertyId).intValue();

			FocObject obj = searchByReference(ref);
			property = obj != null ? obj.getItemProperty(propertyId) : null;
		}		
		return property;
	}

	@Override
	public Class<?> getType(Object propertyId) {
		FocDesc focDesc = getFocDesc();
		//int     fieldID = ((Integer)propertyId).intValue();
		FField  field   = focDesc != null ? focDesc.getFieldByName((String) propertyId) : null;
		
		return field != null ? field.vaadin_getClass() : null; 
	}

	@Override
	public boolean containsId(Object itemId) {
		return getItem(itemId) != null;
	}

	@Override
	public Item addItem(Object itemId) throws UnsupportedOperationException {
		return null;
	}

	@Override
	public Object addItem() throws UnsupportedOperationException {
		return newEmptyItem();
	}

	@Override
	public boolean removeItem(Object itemId) throws UnsupportedOperationException {
		boolean done = false;
		FocObject obj = (FocObject) getItem(itemId);
		if(obj != null){
      obj.setDeleted(true);		  
			//remove(obj);
			done = true;
		}
		return done;
	}

	@Override
	public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue) throws UnsupportedOperationException {
	  throw new UnsupportedOperationException();	  
	}

	@Override
	public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
	  throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAllItems() throws UnsupportedOperationException {
		removeAll();
		return true;
	}
	
	//-----------------------------------------------------------------------
	//-----------------------------------------------------------------------
	
	//-----------------------------------------------------------------------
  //-----------------------------------------------------------------------
	public class ReferencesCollection implements Collection<Long> {
	  private FocList list = null;
	  
	  public ReferencesCollection(FocList list){
	    this.list = list;
	  }
	  
	  public void dispose(){
	    list = null;
	  }
	  
    @Override
    public boolean add(Long arg0) {
      return false;
    }

    @Override
    public boolean addAll(Collection<? extends Long> arg0) {
      return false;
    }

    @Override
    public void clear() {
    }

    @Override
    public boolean contains(Object arg0) {
      return list != null ? list.searchByReference((Long)arg0) != null : false;
    }

    @Override
    public boolean containsAll(Collection<?> arg0) {
      Globals.logString("!! WARNING !! METHIOD NOT IMPLEMENTED FocList.ReferencesCollection.containsAll");
      return false;
    }

    @Override
    public boolean isEmpty() {
      return list != null ? list.isEmpty() : true;
    }

    @Override
    public Iterator<Long> iterator() {
      return new Iterator<Long>() {

        int pos  =  0;
        
//        UserSession userSession = UserSession.getInstanceForThread();
//        Company currentCompany = userSession.getCompany();
//        FocDesc focDesc = getFocDesc();
//        
//        focDesc.isByCompany()
        
        @Override
        public boolean hasNext() {
          return pos < size();
        }

        @Override
        public Long next() {
          long val = 0;
          if(pos >= 0 && pos < size()){
            FocObject obj = getFocObject(pos);
            if(obj != null && obj.getReference() != null) val = obj.getReference().getLong();
          }
          
          pos++;          
          return val;
        }

        @Override
        public void remove() {
          Globals.logString("!! WARNING !! METHIOD NOT IMPLEMENTED FocList.ReferencesCollection.Iterator.remove()");
        }
        
      };
    }

    @Override
    public boolean remove(Object arg0) {
      Globals.logString("!! WARNING !! METHIOD NOT IMPLEMENTED FocList.ReferencesCollection.remove()");
      return false;
    }

    @Override
    public boolean removeAll(Collection<?> arg0) {
      Globals.logString("!! WARNING !! METHIOD NOT IMPLEMENTED FocList.ReferencesCollection.removeAll()");
      return false;
    }

    @Override
    public boolean retainAll(Collection<?> arg0) {
      Globals.logString("!! WARNING !! METHIOD NOT IMPLEMENTED FocList.ReferencesCollection.retainAll()");
      return false;
    }

    @Override
    public int size() {
      return list != null ? list.size() : 0;
    }

    @Override
    public Object[] toArray() {
      Long[] array = new Long[size()];
      int i = 0;
      Iterator<Long> iter = iterator();
      while(iter!=null && iter.hasNext()){
        array[i++]=iter.next();
      }
      //Globals.logString("!! WARNING !! METHIOD NOT IMPLEMENTED FocList.ReferencesCollection.toArray()");
      
      return array;
    }

    @Override
    public <T> T[] toArray(T[] arg0) {
      Globals.logString("!! WARNING !! METHIOD NOT IMPLEMENTED FocList.ReferencesCollection.toArray(T[] arg0)");
      return null;
    }
	}
	
	@Override
  public IFocData iFocData_getDataByPath(String path){
    return super.iFocData_getDataByPath(path);
  }

	public FocObject findObjectByFilterExpression(String filterExpression){
		FocObject foundObject = null;
		if(filterExpression != null && !filterExpression.isEmpty()){
			boolean treated = false;
			
			//If the filter expression starts with REF= and contains a ref integer value we use the search by ref quick method 
			if(filterExpression.startsWith(FField.REF_FIELD_NAME)){
				int equalsIndex = filterExpression.indexOf('=');
				if(equalsIndex > 0){
					String refStr = filterExpression.substring(equalsIndex).trim();
					long ref = FocMath.parseLong(refStr);
					treated = ref != 0;
					if(treated){
						foundObject = searchByReference(ref);
					}
				}
			}

			//If it was not treated this means that the filter is not a simple REF= it is a complicated formula that need evaluation...			
			if(!treated){
				Formula formula = new Formula(filterExpression);
				FocSimpleFormulaContext filterFormulaContext = new FocSimpleFormulaContext(formula);
				
				loadIfNotLoadedFromDB();
				for(int i=0; i<=size() && foundObject == null; i++){
					FocObject currentFocObject = getFocObject(i);
					boolean isVisible = filterFormulaContext.computeBooleanValue(currentFocObject);
					if(isVisible){
						foundObject = currentFocObject;
					}
				}
				
				filterFormulaContext.dispose();
				filterFormulaContext = null;
			}
		}
		return foundObject;
	}

	public String getStoredProcedureName() {
		return null;
	}
	
	public Object[] getStoredProcedureParams() {
		return null;
	}

	public boolean isStoredProcedure(){
		return !Utils.isStringEmpty(getStoredProcedureName());
	}

	public int requestCount() {
		return requestCount("\""+FField.REF_FIELD_NAME+"\"");
	}
	
	public int requestCount(String fieldName) {
		int count = 0;
		FocDesc focDesc = getFocDesc();
		if (focDesc != null) {
			StringBuffer request = new StringBuffer();
			request.append("SELECT COUNT( " + fieldName + ") ");
			request.append("FROM ");
      request.append(focDesc.getNamespacePrefix());
			request.append("\"" + focDesc.getStorageName_ForSQL() + "\" ");
			
			SQLFilter filter = getFilter();
			boolean atLeastOneAdded = filter.addWhereToRequest(request, this.getFocDesc(), true, true);
			
      long startTimeDBRequest = System.currentTimeMillis();
			
			ArrayList<String> valuesArray = Globals.getApp().getDataSource().command_SelectRequest(request);
      
			long endTimeDBRequest = System.currentTimeMillis();
      Globals.logString(" - SQL DURATION = "+(endTimeDBRequest-startTimeDBRequest));

			if(valuesArray.size() == 1) {
				String countStr = valuesArray.get(0);
				count = countStr != null ? Utils.parseInteger(countStr, 0) : 0;
			}
		}
		return count;
	}
	
	public ListPagination getPagination(boolean createIfNeeded) {
		SQLFilter filter = getFilter();
		return filter != null ? filter.getPagination(createIfNeeded) : null;
	}
}
