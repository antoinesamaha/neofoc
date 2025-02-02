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
package com.foc.dataWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

//import me.everpro.event.EverproItemSetChangeEvent;

import com.foc.Globals;
import com.foc.admin.FocUser;
import com.foc.admin.UserSession;
import com.foc.business.adrBook.AdrBookParty;
import com.foc.business.company.Company;
import com.foc.business.workflow.WFSite;
import com.foc.business.workflow.implementation.IAdrBookParty;
import com.foc.business.workflow.implementation.IWorkflow;
import com.foc.business.workflow.implementation.IWorkflowDesc;
import com.foc.business.workflow.implementation.Workflow;
import com.foc.business.workflow.map.WFTransactionConfig;
import com.foc.business.workflow.map.WFTransactionConfigDesc;
import com.foc.dataDictionary.FocDataDictionary;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.desc.field.FMultipleChoiceItem;
import com.foc.event.FocEvent;
import com.foc.event.FocListener;
import com.foc.formula.FocSimpleFormulaContext;
import com.foc.formula.Formula;
import com.foc.list.FocList;
import com.foc.list.FocListElement;
import com.foc.list.FocListListener;
import com.foc.list.FocListOrderFocObject;
import com.foc.property.FBoolean;
import com.foc.property.FMultipleChoice;
import com.foc.property.FObject;
import com.foc.property.FProperty;
import com.foc.shared.dataStore.IFocData;
import com.foc.util.Utils;

@SuppressWarnings("serial")
public abstract class FocDataWrapper implements IFocData {
  //abstract method
  public abstract FocList getFocList();

//  protected ITableTree tableTree = null;
  protected IFocData focData = null;
  protected ArrayList<FocObject> visibleListElements = null;
//  protected ArrayList<Filter> filterArrayList = null;
  protected FocListOrderFocObject listOrder = null;
  protected FocObject                 initialValue         = null;
  protected FocListElement            initialElement       = null;
//  private   ArrayList<PropertySetChangeListener> propertySetChangeListenerArray = null;
  private   String                    filterExpression_ForXMLLayout = null;  
//  private   HashMap<String, Filter>              filtersMap                     = null;
  
  private ITableTreeDelegate iTableTreeDelegate = null;
	protected boolean byCompany     = false;
  protected Company company       = null;
  protected int     siteFieldID_1 = FField.NO_FIELD_ID;
  protected int     siteFieldID_2 = FField.NO_FIELD_ID;
  
  protected FocListener     focListener     = null;
  private   FocListListener focListPropertyListener_ToRefreshGui = null;

  private ReferencesCollection referencesCollection     = null;
  private boolean              refreshGuiDisabled       = false;
  private int                  forbidAncestorSiteAccess = -1;
  private boolean 						 filterByAddressBookForGuestUser = true;
  
  public FocDataWrapper(IFocData data){
  	this(data, true);
  }
  
  public FocDataWrapper(IFocData data, boolean withListeners){
    setFocData(data);

    if(withListeners){
	    focListener = new FocListener() {
	
	      @Override
	      public void focActionPerformed(FocEvent evt) {
	      	if(!isRefreshGuiDisabled()){
		        if(evt.getID()==FocEvent.ID_ITEM_ADD || evt.getID() == FocEvent.ID_ITEM_REMOVE) {
	        		FocObject focObj = (FocObject) evt.getEventSubject();
	        		long      ref    = focObj != null ? focObj.getReferenceInt() : 0;
//	        		int       everproEventID = evt.getID()==FocEvent.ID_ITEM_ADD ? EverproItemSetChangeEvent.ADD_EVENT : EverproItemSetChangeEvent.DELETE_EVENT;
//		        	EverproItemSetChangeEvent event = new EverproItemSetChangeEvent((Container)FocDataWrapper.this, everproEventID, (Object)ref);
//		        	refreshGuiForContainerChanges(event);
		        }else if(evt.getID() == FocEvent.ID_BEFORE_REFERENCE_SET){
		        	if(getTableTreeDelegate() != null && evt.getEventSubject() instanceof FocObject){
		          	getTableTreeDelegate().listListenerCall_BeforeObjectReferenceSet((FocObject) evt.getEventSubject());
//		        		if(focListener_ForTableTReeDelegate != null){
//		        			focListener_ForTableTReeDelegate.focActionPerformed(evt);
//		        		}
		        	}
		        }
//DANGER
//2014-03-03		        
//This is called when the Set of properties supported by the container has changed.		        
//		        firePropertySetChangeEvent();
	      	}
	      }
	
	      @Override
	      public void dispose() {
	        
	      }
	      
	    };
	    
	    FocList list = getFocList();
	    if(list != null){
	    	list.addFocListener(focListener);
	    }
	    
	    //TODO: Idealy here we should be listeneing only to visible items and to shown columns
	    //------------------------------------------------------------------------------
  	  //DANGER
	    /*
	    focListPropertyListener_ToRefreshGui = new FocListListener(getFocList());
	    focListPropertyListener_ToRefreshGui.addListener(new FocListener(){
				@Override
				public void focActionPerformed(FocEvent evt) {
					//DANGER
					//IF WE PUT THIS FIRE firePropertySetChangeEvent A FULL REFRESH WILL BE CALLED EVERY TIME WE CLICK A TAB GOING OUT OF A CELL. BECAUSE OF THIS, THE NEXT FOCUSED ITEM IS NOT THE EXPECTED ONE!
					//AND ACTUALLY THIS EVENT SHOULD ONLY BE FIRED WHEN THE SET OF PROPERTIES SUPPORTED HAS CHANGED!
					//firePropertySetChangeEvent();
				}
	
				@Override
				public void dispose() {
				}
	    });
	    FocFieldEnum enumer = getFocList().getFocDesc().newFocFieldEnum(FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
	    while(enumer != null && enumer.hasNext()){
	    	FField fld = enumer.nextField();
	    	focListPropertyListener_ToRefreshGui.addProperty(FFieldPath.newFieldPath(fld.getID()));
	    }
	    focListPropertyListener_ToRefreshGui.startListening();
	    */
	    //-------------------------------------------------
    }
  }

  public void dispose(){
    if(focListener != null){
    	if(getFocList() != null){
    		getFocList().removeFocListener(focListener);
    	}
    }
    focListener = null;
    
//    removeAllContainerFilters();
    resetVisibleListElements();
    
    focData = null;
    if(referencesCollection != null){
    	referencesCollection.dispose();
    	referencesCollection = null;
    }
    if(focListPropertyListener_ToRefreshGui != null){
    	focListPropertyListener_ToRefreshGui.dispose();
    	focListPropertyListener_ToRefreshGui = null;
    }
//    if(propertySetChangeListenerArray != null){
//    	propertySetChangeListenerArray.clear();
//    	propertySetChangeListenerArray = null;
//    }
//    tableTree = null;
    iTableTreeDelegate = null;
//    if(filtersMap != null){
//    	filtersMap.clear();
//    	filtersMap = null;
//    }
  }
  
  public void removeSiteFilter(){
    siteFieldID_1 = FField.NO_FIELD_ID;
    siteFieldID_2 = FField.NO_FIELD_ID;
  }
  
  public void refreshGuiForContainerChanges(){
	  resetVisibleListElements();
	  //if(getTableTree() != null) getTableTree().applyFocListAsContainer();
//	  notifyItemSetChange();
  }

  public IFocData getFocData(){
    return focData;
  }
  
  public void setFocData(IFocData focData){
    this.focData = focData;
  }
    
  public void resetVisibleListElements(){
    if(visibleListElements != null){
      visibleListElements.clear();
      visibleListElements = null;
    }
  }
  
  public boolean isFilterByAddressBookForGuestUser() {
  	return filterByAddressBookForGuestUser;
  }
  
  public void setFilterByAddressBookForGuestUser(boolean value) {
  	filterByAddressBookForGuestUser = value;
  }

  public void setFilterByExpression_FocXMLLayout(String expression){
  	filterExpression_ForXMLLayout = expression;
  	addFilterByExpression(expression);
  }
  
  public void addFilterByExpression(String expression){
  	addFilterByExpression(null, expression);
  }
  
  public void addFilterByExpression(IFocData focData, String expression){
  	if(expression != null && !expression.isEmpty()){
  		ListFilterUsingAFormulaExpression filter = new ListFilterUsingAFormulaExpression(focData, expression);
//  		addContainerFilter();
  		if(expression != null && !expression.isEmpty()){
//  			getFiltersMap(true).put(expression, filter);
//  			putFilter(expression, filter);
  		}
  	}
  }
  
  public void addFilterByPropertyValue(String propertyDataPath, Object value){
    if(propertyDataPath != null && !propertyDataPath.isEmpty()){
      ListFilterUsingPropertyValue filter = new ListFilterUsingPropertyValue(propertyDataPath, value);
    }
  }
  
  public void setSortingComparator(FocListOrderFocObject comparator){
   	if(listOrder != null) listOrder.dispose();
    listOrder = comparator;
  }
  
  public void setSortingExpression(String sortingExpression){
    if(getFocList() != null){
    	FocListOrderFocObject newListOrder = FocListOrderFocObject.newFocListOrder_ForExpression(getFocList().getFocDesc(), sortingExpression, true);
    	setSortingComparator(newListOrder);
//    	if(listOrder != null) listOrder.dispose();
//    	listOrder = FocListOrderFocObject.newFocListOrder_ForExpression(getFocList().getFocDesc(), sortingExpression, true);
    }
  }
  
  UserSession userSession = null;
  public UserSession getUserSession() {
  	if(userSession == null) {
  		userSession = UserSession.getInstanceForThread();
  	}
  	return userSession;
  }
  
  public boolean isForbidAncestorsSitesFromAccess() {
  	if(forbidAncestorSiteAccess == -1) {
  		if(getFocList() != null && getFocList().getFocDesc() != null && getFocList().getFocDesc() instanceof IWorkflowDesc) {
  			WFTransactionConfig config = WFTransactionConfigDesc.getTransactionConfig_ForTransaction((IWorkflowDesc)getFocList().getFocDesc());
  			forbidAncestorSiteAccess = config != null && config.isForbidAncestorSiteAccess() ? 1 : 0;
  		}
  	}
  	return forbidAncestorSiteAccess == 1;
  }
  
  protected boolean includeFocObject(FocObject focObj){
    boolean include = focObj != null ? true : false;
//    FocUser user = FocWebApplication.getFocWebSession_Static().getUserSession().getUser();
  	UserSession userSession = getUserSession();
    FocUser user = userSession != null ? userSession.getUser() : null;
    if(user != null && user.isGuest()){
    	if(focObj.hasAdrBookParty() && isFilterByAddressBookForGuestUser()){
        include = false;
        IAdrBookParty iAdrBookParty = (IAdrBookParty) focObj;
        AdrBookParty adrBookParty = iAdrBookParty != null ? iAdrBookParty.iWorkflow_getAdrBookParty() : null;
        if(adrBookParty != null){
          if(user.getContact() != null && user.getContact().getAdrBookParty() != null){
            include = adrBookParty.equalsRef(user.getContact().getAdrBookParty());
          }
        }
      }
      /*FocDesc focDesc = focObj.getThisFocDesc();
      if(focDesc.workflow_IsWorkflowSubject()){
        include = false;          
        Workflow workflow = ((IWorkflow)focObj).iWorkflow_getWorkflow();
        AdrBookParty adrBookParty = workflow.getAdrBookParty();
        if(adrBookParty != null){
          if(user.getContact() != null && user.getContact().getAdrBookParty() != null){
            include = adrBookParty.equalsRef(user.getContact().getAdrBookParty());
          }
        }
      }*/
    }else{
      if(include && byCompany){
        include = false;      
        if(focObj.getCompany() == null || focObj.getCompany().equalsRef(company)){
          
          if(siteFieldID_1 != FField.NO_FIELD_ID && userSession != null){
            WFSite site = (WFSite) focObj.getPropertyObject(siteFieldID_1);
            if(site != null){
              include = isForbidAncestorsSitesFromAccess() ? site.equalsRef(userSession.getSite()) : site.hasAncestorOrEqualTo(userSession.getSite());
            }
          }
          
          if(siteFieldID_2 != FField.NO_FIELD_ID && userSession != null){
            WFSite site = (WFSite) focObj.getPropertyObject(siteFieldID_2);
            if(site != null && site.hasAncestorOrEqualTo(userSession.getSite())){
              include = isForbidAncestorsSitesFromAccess() ? site.equalsRef(userSession.getSite()) : site.hasAncestorOrEqualTo(userSession.getSite());
            }
          }
          
          if(siteFieldID_1 == FField.NO_FIELD_ID && siteFieldID_2 == FField.NO_FIELD_ID){
            include = true;
          }
        }
      }
    }

    if(include && userSession != null && !userSession.isSimulation()){
    	if(focObj.workflow_IsWorkflowSubject()){
    		Workflow workflow = ((IWorkflow)focObj).iWorkflow_getWorkflow();
    		if(workflow != null && workflow.isSimulation()){
    			include = false;
    		}
    	}
    }
    
    if(include){
      //The initialValue should be visible in the selections no matter what.
      if(getInitialValue() == null || !focObj.equalsRef(initialValue)){
//        ArrayList<Filter> filters = getFilterArrayList(false);
//        if(filters != null && filters.size() > 0){
//          for(int i=0; i<filters.size() && include; i++){
//            Filter fltr = filters.get(i);
//            if(focObj.getReference() != null){
//            	include = include && fltr.passesFilter(focObj.getReference().getLong(), focObj);
//            }
//          }
//        }
      }
    }
    
    //This allows to take into account Memory level filters
    if(include) {
    	FocList list = getFocList();
    	include = list.includeObject_ByListFilter(focObj);
    }
    
    return include;
  }
  
  public FocObject searchByPropertyValue(String propertyName, String value){
    FocObject focObject = null;
    for(int i=0; i<getVisibleListElements(true).size() && focObject == null; i++){
      FocObject listElement = getVisibleListElements(false).get(i);
      if(listElement != null){
        FProperty prop = listElement.getFocPropertyForPath(propertyName);
        if(prop != null && Utils.isEqual_String(prop.getString(), value)){
          focObject = listElement;
        }
      }
    }
    return focObject;
  }
  
  protected ArrayList<FocObject> getVisibleListElements(boolean create){
    if(visibleListElements == null && create){
      visibleListElements = new ArrayList<FocObject>();
      FocList list = getFocList();
      if(list != null){
	      for(int i=0; i<list.size(); i++){
	        FocListElement elem   = list.getFocListElement(i);
	        FocObject      focObj = elem.getFocObject();
	        if(includeFocObject(focObj)){
	          visibleListElements.add(focObj);
	        }
	      }
      }      
      if(getInitialValue() != null){
        long initialValueId = getInitialValue().getReference().getLong();
        if(list == null || !list.containsId(initialValueId)){
          visibleListElements.add(getInitialValue());
        }
      }
      if(listOrder != null){
        Collections.sort(visibleListElements, listOrder);
      }
    }
    return visibleListElements;
  }
  
  public boolean containsReference(long ref){
    boolean contains = false;
    ArrayList<FocObject> arrayList = getVisibleListElements(true);
    for(int i=0; i<arrayList.size() && !contains; i++){
      FocObject obj = arrayList.get(i);
      contains = (obj != null && obj.getReference() != null && obj.getReference().getLong() == ref);
    }
    return contains;
  }
  
  protected FocObject getContainerProperty_GetFocObject(Object itemId){
  	FocObject obj = null;
    
    if(itemId != null){
      long ref = ((Long)itemId).longValue();
      
      if(initialValue != null && initialValue.getReference() != null && initialValue.getReference().getLong() == ref){
        obj = initialValue;
      }
//      if(obj == null) obj = getFocList().searchByReference(ref);
      if(obj == null) {
      	obj = getFocList() != null ? getFocList().searchByReference(ref) : null;
      }
    }
    return obj;
  }
  
//  @Override
  public Class<?> getType(Object propertyId) {
  	if(propertyId != null && !propertyId.toString().isEmpty() && propertyId.toString().contains(IFocData.DATA_PATH_SIGN)){
  		propertyId = propertyId.toString().substring(0, propertyId.toString().indexOf(IFocData.DATA_PATH_SIGN));
  	}
    return getFocList() != null ? getFocList().getType(propertyId) : null;
  }

//  @Override
  public int size() {
    return getVisibleListElements(true).size();
  }
  
  public FocObject getAt(int at){
  	FocObject focObj = null;
  	ArrayList<FocObject> array = getVisibleListElements(true);
  	if(at >= 0 && at < array.size()){
  		focObj = array.get(at);
  	}
  	return focObj;
  }

//  @Override
  public boolean containsId(Object itemId) {
    return containsReference((long)itemId);
  }

//  @Override
  public Object addItem() throws UnsupportedOperationException {
    Object obj = getFocList() != null ? getFocList().addItem() : null;
    resetVisibleListElements();
    return obj;
  }

//  @Override
  public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue) throws UnsupportedOperationException {
    return getFocList() != null ? getFocList().addContainerProperty(propertyId, type, defaultValue) : false;
  }

//  @Override
  public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
    return getFocList() != null ? getFocList().removeContainerProperty(propertyId) : false;
  }

//  @Override
  public boolean removeAllItems() throws UnsupportedOperationException {
    boolean ret = getFocList() != null ? getFocList().removeAllItems() : false;
    resetVisibleListElements();
    return ret;
  }
  
  //-----------------------------------------------------------------------
  //-----------------------------------------------------------------------
  
  //-----------------------------------------------------------------------
  //-----------------------------------------------------------------------
  public class ReferencesCollection implements Collection<Long> {
    private FocDataWrapper dataWrapper = null;
    
    public ReferencesCollection(FocDataWrapper dataWrapper){
      this.dataWrapper = dataWrapper;
    }
    
    public void dispose(){
      dataWrapper = null;
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
      return dataWrapper != null ? dataWrapper.containsReference((Long)arg0) : false;
    }

    @Override
    public boolean containsAll(Collection<?> arg0) {
      Globals.logString("!! WARNING !! METHIOD NOT IMPLEMENTED FocList.ReferencesCollection.containsAll");
      return false;
    }

    @Override
    public boolean isEmpty() {
      return dataWrapper != null ? dataWrapper.getVisibleListElements(true).size() == 0 : true;
    }

    @Override
    public Iterator<Long> iterator() {
      
      return new Iterator<Long>() {
        int pos  =  0;
        
        @Override
        public boolean hasNext() {
          return pos < size();
        }

        @Override
        public Long next() {
          long val = 0;
          if(pos >= 0 && pos < size()){
            FocObject obj = getVisibleListElements(true).get(pos);
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
      return dataWrapper != null ? dataWrapper.getVisibleListElements(true).size() : 0;
    }

    @Override
    public Object[] toArray() {
      Long[] array = new Long[size()];
      int i = 0;
      Iterator<Long> iter = iterator();
      while(iter!=null && iter.hasNext()){
        array[i++]=iter.next();
      }
      
      return array;
    }

    @Override
    public <T> T[] toArray(T[] arg0) {
      Globals.logString("!! WARNING !! METHIOD NOT IMPLEMENTED FocList.ReferencesCollection.toArray(T[] arg0)");
      return null;
    }
  }

  @Override
  public boolean iFocData_isValid() {
    return getFocList() != null ? getFocList().iFocData_isValid() : true;
  }

  @Override
  public boolean iFocData_validate() {
    return getFocList() != null ? getFocList().iFocData_validate() : null;
  }

  @Override
  public void iFocData_cancel() {
    if(getFocList() != null){
      getFocList().iFocData_cancel();
    }
  }

  @Override
  public IFocData iFocData_getDataByPath(String path) {
    return getFocList() != null ? getFocList().iFocData_getDataByPath(path) : null;
  }

  public class ListFilterUsingAFormulaExpression {

    private FocSimpleFormulaContext filterFormulaContext = null;
    private IFocData                focData              = null;
    
    public ListFilterUsingAFormulaExpression(String expression){
    	this(null, expression);
    }
    
    public ListFilterUsingAFormulaExpression(IFocData focData, String expression){
      if(expression != null && !expression.isEmpty()){
      	expression = FocDataDictionary.getInstance().resolveExpression(focData, expression, true);
      	
      	Formula formula = new Formula(expression);
        filterFormulaContext = new FocSimpleFormulaContext(formula);
      }     
    }
    
//    @Override
//    public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException {
//      boolean visible = true;
//      if(filterFormulaContext != null){
//        visible = filterFormulaContext.computeBooleanValue((FocObject) item);
//      }
//      return visible;
//    }

//    @Override
    public boolean appliesToProperty(Object propertyId) {
      return false;
    }
  }
  
  public class ListFilterUsingPropertyValue {

    private String propertyDataPath = null;
    private Object propertyValue    = null;
    
    public ListFilterUsingPropertyValue(String propertyDataPath, Object propertyValue){
    	this.propertyDataPath = propertyDataPath;
    	this.propertyValue    = propertyValue;
    }
    
    public void adjustValue(FocObject focObj){
    	IFocData focData = focObj.iFocData_getDataByPath(propertyDataPath);
    	if(focData != null && focData instanceof FProperty){
//    		((FProperty)focData).setValue(propertyValue);
    	}
    }
    
    private boolean propertyEqualsValue(FProperty prop){
    	boolean equals = false;
    	if(prop instanceof FObject && propertyValue instanceof FocObject && ((FocObject)propertyValue).getReference() != null){
    		equals = ((FObject)prop).getLocalReferenceInt() == ((FocObject)propertyValue).getReference().getLong();
    	}else if(prop instanceof FMultipleChoice){
    		if(propertyValue instanceof Integer){
    			equals = ((FMultipleChoice)prop).getInteger() == (Integer)propertyValue;
    		}else if(propertyValue instanceof FMultipleChoiceItem){
    			equals = ((FMultipleChoice)prop).getInteger() == ((FMultipleChoiceItem)propertyValue).getId();
    		}
    	}else if(prop instanceof FBoolean){
    		if(propertyValue instanceof String){
    			if(propertyValue != null){
    				String upperVal = ((String) propertyValue).toUpperCase();
	    			boolean val = upperVal.equals("TRUE") || upperVal.equals("1");  
	    			equals = prop.getValue().equals(val);
    			}
    		}else{
    			equals = prop.getValue().equals(propertyValue);
    		}
    	}else{
    		equals = prop.getValue().equals(propertyValue);
    	}
    	return equals;
    }
    
//    @Override
//    public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException {
//      boolean visible = false;
//      FocObject focObj = (FocObject) item;
//      if(propertyDataPath != null && focObj != null){
//      	IFocData focData = focObj.iFocData_getDataByPath(propertyDataPath);
//      	if(focData instanceof FProperty){
//      		FProperty prop = (FProperty) focData;
//      		if(			(prop.getValue() == null && propertyValue == null)
//      				|| 	(prop.getValue() != null && propertyValue != null && propertyEqualsValue(prop))){
//            visible = true;
//      		}
//      	}
//      }
//      return visible;
//    }

//    @Override
    public boolean appliesToProperty(Object propertyId) {
      return false;
    }
  }

  @Override
  public Object iFocData_getValue() {
    return null;
  }

  public FocObject getInitialValue() {
    return initialValue;
  }

  public void setInitialValue(FocObject initialValue) {
    this.initialValue = initialValue;
    
    if(initialValue != null){
      initialElement = new FocListElement(initialValue, false);
    }
  }
  
  public FocListElement getInitialElement() {   
    return initialElement;
  }
  
  // ------------------------------------------------------
  // Filter implementation
  // ------------------------------------------------------
  
//  public ArrayList<Filter> getFilterArrayList(boolean create){
//    if(filterArrayList == null && create){
//      filterArrayList = new ArrayList<Filter>();
//    }
//    return filterArrayList;
//  }
//
//  @Override
//  public void addContainerFilter(Filter filter) throws UnsupportedFilterException {
//    getFilterArrayList(true).add(filter);
//    resetVisibleListElements();
//  }
//
//  public void putFilter(String expression, Filter filter){
//  	getFiltersMap(true).put(expression, filter);
//  }
//
//  public Filter getFilter(String expression){
//  	return getFiltersMap(true).get(expression);
//  }
//
//  private HashMap<String, Filter> getFiltersMap(boolean create){
//  	if(filtersMap == null && create){
//  		filtersMap = new HashMap<String, Container.Filter>();
//  	}
//  	return filtersMap;
//  }
//
//  @Override
//  public void removeContainerFilter(Filter filter) {
//  	removeContainerFilter_ForDispose(filter);
//    resetVisibleListElements();
//  }
//
//  public void removeContainerFilter_ForDispose(Filter filter) {
//  	if(getFilterArrayList(false) != null){
//  		getFilterArrayList(false).remove(filter);
//  	}
//  }
//
//  @Override
//  public void removeAllContainerFilters() {
//    ArrayList<Filter> arrayFilters = getFilterArrayList(false);
//    if(arrayFilters != null){
//    	for(int i=arrayFilters.size()-1; i>=0 ; i--){
//    		arrayFilters.remove(i);
//    	}
//    }
//    resetVisibleListElements();
//  }
//
//  @Override
//  public Collection<Filter> getContainerFilters(){
//  	ArrayList<Filter> arrayFilters = getFilterArrayList(true);
//  	return arrayFilters;
//  }
  // ------------------------------------------------------

//  private ArrayList<PropertySetChangeListener> getPropertySetChangeListenerArray(boolean create){
//  	if(propertySetChangeListenerArray == null && create){
//  		propertySetChangeListenerArray = new ArrayList<Container.PropertySetChangeListener>();
//  	}
//  	return propertySetChangeListenerArray;
//  }

  public void firePropertySetChangeEvent(){
  	if(!isRefreshGuiDisabled()){
//	  	ArrayList<PropertySetChangeListener> array = getPropertySetChangeListenerArray(false);
//	  	if(array != null && array.size() > 0){
//		  	PropertySetChangeEvent event = new PropertySetChangeEvent() {
//					@Override
//					public Container getContainer() {
//						return FocDataWrapper.this;
//					}
//				};
//
//		  	for(int i=0; i<array.size(); i++){
//		  		PropertySetChangeListener listener = array.get(i);
//		  		listener.containerPropertySetChange(event);
//		  	}
//	  	}
  	}
  }
  
//	@Override
//	public void addPropertySetChangeListener(PropertySetChangeListener listener) {
//		ArrayList<PropertySetChangeListener> propertySetChangeListeners = getPropertySetChangeListenerArray(true);
//		if(propertySetChangeListeners != null){
//			propertySetChangeListeners.add(listener);
//		}
//	}
//
//	@Override
//	@Deprecated
//	public void addListener(PropertySetChangeListener listener) {
//		addPropertySetChangeListener(listener);
//	}
//

//	@Override
//	public void removePropertySetChangeListener(PropertySetChangeListener listener) {
//		ArrayList<PropertySetChangeListener> propertySetChangeListeners = getPropertySetChangeListenerArray(false);
//		if(propertySetChangeListeners != null){
//			propertySetChangeListeners.remove(listener);
//		}
//	}
//
//	@Override
//	@Deprecated
//	public void removeListener(PropertySetChangeListener listener) {
//		removePropertySetChangeListener(listener);
//	}
	
	public int getLineNumberForFocObject(FocObject focObj){
		ArrayList<FocObject> arrayList = getVisibleListElements(true);
		int idx = (arrayList != null && focObj != null) ? arrayList.indexOf(focObj) : 0;
		return idx+1;
	}

	public boolean isRefreshGuiDisabled() {
		return refreshGuiDisabled;
	}

	public boolean setRefreshGuiDisabled(boolean refreshGuiDisabled) {
		boolean initialValue = this.refreshGuiDisabled; 
		this.refreshGuiDisabled = refreshGuiDisabled;
		if(!refreshGuiDisabled){
			firePropertySetChangeEvent();
		}
		return initialValue;
	}
	
	// -------------------------------
	// Container.ItemSetChangeNotifier 
	// -------------------------------
//	private ArrayList<ItemSetChangeListener> itemSetChangeListenerArray = null;
//
//	@Override
//	public void addItemSetChangeListener(ItemSetChangeListener listener) {
//		if(itemSetChangeListenerArray == null){
//			itemSetChangeListenerArray = new ArrayList<ItemSetChangeListener>();
//		}
//		itemSetChangeListenerArray.add(listener);
//	}
//
//	@Override
//	@Deprecated
//	public void addListener(ItemSetChangeListener listener) {
//		addItemSetChangeListener(listener);
//	}
//
//	@Override
//	public void removeItemSetChangeListener(ItemSetChangeListener listener) {
//		if(itemSetChangeListenerArray != null){
//			itemSetChangeListenerArray.remove(listener);
//		}
//	}
//
//	@Override
//	@Deprecated
//	public void removeListener(ItemSetChangeListener listener) {
//		removeItemSetChangeListener(listener);
//	}
  
//	public void notifyItemSetChange(){
//		if(itemSetChangeListenerArray != null){
//			for(int i=0; i<itemSetChangeListenerArray.size(); i++){
//				ItemSetChangeListener listener = itemSetChangeListenerArray.get(i);
//				ItemSetChangeEvent event = everproEvent;
//				if(event == null){
//					event = new ItemSetChangeEvent() {
//						@Override
//						public Container getContainer() {
//							return FocDataWrapper.this;
//						}
//					};
//				}
//				listener.containerItemSetChange(event);
//			}
//		}
//	}
	// -------------------------------
	
//	public void removeFilterByName(String filterExpression){
//		if(filterExpression != null){
//			Filter filter = getFiltersMap(false) != null ? getFiltersMap(false).get(filterExpression) : null;
//			if(filter != null){
//				removeContainerFilter(filter);
//			}
//		}
//	}
	
  //20150831-Begin
//  @Override
  public Collection<?> getContainerPropertyIds() {
//    return getFocList() != null ? getFocList().getContainerPropertyIds() : null;
		return getTableTreeDelegate() != null ? getTableTreeDelegate().newVisibleColumnIds() : null;
  }
  //20150831-End

	public ITableTreeDelegate getTableTreeDelegate() {
		return iTableTreeDelegate;
	}
	
	public void setTableTreeDelegate(ITableTreeDelegate iTableTreeDelegate) {
		this.iTableTreeDelegate = iTableTreeDelegate;
	}
}
