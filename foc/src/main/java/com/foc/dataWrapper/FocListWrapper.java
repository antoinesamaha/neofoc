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
import java.util.List;

import com.foc.admin.UserSession;
import com.foc.business.workflow.implementation.IWorkflowDesc;
import com.foc.business.workflow.implementation.WorkflowDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class FocListWrapper extends FocDataWrapper {
  
  //When the Wrapper is used for a ComboBox (FVObjectComboBox) 
  //it should always show the already selected value from previous sessions
  //For example the job closed should always show in the combo of an invoice when we edit that invoice
  
	public FocListWrapper(FocList list){
		this(list, true);
	}
	
  public FocListWrapper(FocList list, boolean withListener){
    super(list, withListener);
    
    if(getFocList() != null){
	    getFocList().loadIfNotLoadedFromDB();
	    
	    byCompany = getFocList().getFocDesc().isByCompany();
	    if(byCompany){
	      company = UserSession.getInstanceForThread().getCompany();
	    }
	
	    siteFieldID_1 = FField.NO_FIELD_ID;
	    siteFieldID_2 = FField.NO_FIELD_ID;
	    
	    if(getFocList().getFocDesc() == null || getFocList().getFocDesc().isSiteRestrictionAccess()) {
		    if(getFocList().getFocDesc() instanceof IWorkflowDesc){
		      WorkflowDesc workflowDesc = ((IWorkflowDesc) getFocList().getFocDesc()).iWorkflow_getWorkflowDesc();
		      if(workflowDesc != null){
		        siteFieldID_1 = workflowDesc.getFieldID_Site_1();
		        siteFieldID_2 = workflowDesc.getFieldID_Site_2();
		      }
		    } 
		    
		    if(siteFieldID_1 == FField.NO_FIELD_ID){
		    	FField siteField = getFocList().getFocDesc().getFieldByName(FField.FNAME_SITE);
		    	if(siteField != null) {
		    		siteFieldID_1 = siteField.getID();
		    	}
		    }
	    }
    }
  }

  public FocList getFocList(){
    return (FocList)getFocData();
  }
  
  public void setFocList(FocList list){
    setFocData(list);
  }
  
  public FocList getFocListFiltered(){
  	return getFocList();
  }

  //-----------------
  //Container.Indexed
  //-----------------
  
//	@Override
	public Object nextItemId(Object itemId) {
		ArrayList<FocObject> array = getVisibleListElements(true);
		int idx = indexOfId(itemId);
		
		return (idx+1 < array.size()) ? array.get(idx+1).getReferenceInt() : null;
	}

//	@Override
	public Object prevItemId(Object itemId) {
		ArrayList<FocObject> array = getVisibleListElements(true);
		int idx = indexOfId(itemId);
		
		return (idx-1 > 0) ? array.get(idx-1).getReferenceInt() : null;
	}

//	@Override
	public Object firstItemId() {
		Long first = null;
		ArrayList<FocObject> array = getVisibleListElements(true);
		if(array != null && array.size() > 0){
			first = array.get(0).getReferenceInt();
		}
		return first;
	}

//	@Override
	public Object lastItemId() {
		Long last = null;
		ArrayList<FocObject> array = getVisibleListElements(true);
		if(array != null && array.size() > 0){
			last = array.get(array.size() - 1).getReferenceInt();
		}
		return last;
	}

//	@Override
	public boolean isFirstId(Object itemId) {
		boolean first = false;
		ArrayList<FocObject> array = getVisibleListElements(true);
		if(array != null && array.size() > 0){
			first = array.get(0).getReferenceInt() == ((Long)itemId).intValue();
		}
		return first;
	}

//	@Override
	public boolean isLastId(Object itemId) {
		boolean last = false;
		ArrayList<FocObject> array = getVisibleListElements(true);
		if(array != null && array.size() > 0){
			last = array.get(array.size()-1).getReferenceInt() == ((Long)itemId).intValue();
		}
		return last;
	}

//	@Override
	public Object addItemAfter(Object previousItemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

//	@Override
//	public Item addItemAfter(Object previousItemId, Object newItemId) throws UnsupportedOperationException {
//		throw new UnsupportedOperationException();
//	}

//	@Override
	public int indexOfId(Object itemId) {
		int index = -1;
		
		if(itemId instanceof Integer){
			ArrayList<FocObject> array = getVisibleListElements(true);
			for(int i=0; i<array.size(); i++){
				FocObject focObj = array.get(i);
				if(focObj != null && (Long)itemId == focObj.getReferenceInt()){
					index = i;
				}
			}
		}
		return index;
	}

//	@Override
	public Object getIdByIndex(int index) {
		ArrayList<FocObject> array = getVisibleListElements(true);
	  FocObject focObj = array.get(index);
	  return focObj != null ? focObj.getReferenceInt() : null;
	}

//	@Override
	public List<?> getItemIds(int startIndex, int numberOfItems) {
		ArrayList<Long> idsArray = new ArrayList<Long>();
		
		ArrayList<FocObject> array = getVisibleListElements(true);
		if(array != null){
			for(int i=startIndex; i<startIndex+numberOfItems && i<array.size(); i++){
				idsArray.add(array.get(i).getReferenceInt());
			}
		}
		
		return idsArray;
	}

//	@Override
	public Object addItemAt(int index) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

//	@Override
//	public Item addItemAt(int index, Object newItemId) throws UnsupportedOperationException {
//		throw new UnsupportedOperationException();
//	}
	
  /*
  private FocList newFocListFiltered = null;
	public FocList getFocListFiltered(){
		if(newFocListFiltered == null){
			ArrayList<FocObject> focObjects = getVisibleListElements(true);
	    if(focObjects != null && getFocList() != null){
	    	newFocListFiltered = new FocList(new FocLinkSimple(getFocList().getFocDesc()));
		    newFocListFiltered.setDbResident(false);
		    newFocListFiltered.setDirectlyEditable(false);
		    newFocListFiltered.setDirectImpactOnDatabase(true);
		    for(int i=0; i<focObjects.size(); i++){
	    		newFocListFiltered.add(focObjects.get(i));
		    }
	    }
		}
	  return newFocListFiltered;
	}
//	*/
}
