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
package com.foc.list;

import com.foc.db.SQLFilter;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.list.filter.FilterCondition;
import com.foc.list.filter.FilterDesc;
import com.foc.list.filter.FocListFilter;
import com.foc.list.filter.FocListFilterBindedToList;
import com.foc.list.filter.IFocListFilter;
import com.foc.shared.dataStore.IFocData;

public class FocListWithFilter extends FocList {
	private FocListFilterBindedToList filter = null;

  private void init(FocDesc filterFocDesc){
    if(filterFocDesc != null){
      FocConstructor constr = new FocConstructor(filterFocDesc, null);
      setFocListFilter((FocListFilterBindedToList)constr.newItem());
      addLogicalDeleteFilter();
    }
  }
  
	public FocListWithFilter(FocDesc filterFocDesc, FocLink focLink) {
		super(focLink);
    init(filterFocDesc);
	}
	
	public FocListWithFilter(FocDesc filterFocDesc, FocObject masterObject, FocLink focLink, SQLFilter filter) {
    super(masterObject, focLink, filter);
    init(filterFocDesc);    
  }

  public void dispose(){
		super.dispose();
		disposeFilter();
	}
	
	private void disposeFilter(){
		if(filter != null){
			filter.dispose();
			filter = null;
		}
	}
	
	public void setFocListFilter(FocListFilterBindedToList filter){
		disposeFilter();
		this.filter = filter;
		if(this.filter != null){
			this.filter.setGuiFocList(this);
		}
	}
	
	public void reloadFromDB_Super(){
		super.reloadFromDB();
	}

	@Override
	public boolean includeObject_ByListFilter(FocObject obj){
		return this.filter != null && obj != null ? this.filter.includeObject(obj) : false;
	}
	
  public void reloadFromDB() {
  	if(filter != null){
  		if(filter.getFilterLevel() == FocListFilterBindedToList.LEVEL_MEMORY){
  			reloadFromDB_Super();
  		}
//      int filterLevel = filter.getFilterLevel();
//      filter.setFilterLevel(FocListFilterBindedToList.LEVEL_DATABASE);
  		filter.setActive(this, true);
//      filter.setFilterLevel(filterLevel);
  	}else{
  		reloadFromDB_Super();
  	}
  }
  
  public FocListFilterBindedToList loadFilterByReference(int filterRef){
  	if(filter != null){
	  	filter.setReference(filterRef);
	  	filter.load();
  	}
  	return filter;
  }
  
  public IFocListFilter getFocListFilter(){
  	return filter;
  }

  @Override
  protected void fillForeignObjectsProperties(FocObject newFocObj){
  	super.fillForeignObjectsProperties(newFocObj);
    IFocListFilter focListFilter = getFocListFilter();
    if(focListFilter != null){
	    FilterDesc filterDesc = focListFilter.getThisFilterDesc();
	    if(filterDesc != null && focListFilter.isActive()){
	      for(int i=0; i<filterDesc.getConditionCount(); i++){
	        FilterCondition cond = filterDesc.getConditionAt(i);
	        cond.forceFocObjectToConditionValueIfNeeded((FocListFilter) focListFilter, newFocObj);
	      }
	    }
    }  	
  }
  
  @Override
  public FocObject newEmptyItem() {
    FocObject newFocObj = super.newEmptyItem();
    return newFocObj;
  }
  
	@Override
  public IFocData iFocData_getDataByPath(String path){
		IFocData result = null; 
		if(path != null && path.startsWith("FILTER")){
			String remainingPath = path.substring(6);
			if(remainingPath.length() > 0 && remainingPath.startsWith(".")){
				result = filter;
				result = result.iFocData_getDataByPath(remainingPath); 
			}else if(remainingPath.length()==0){
				result = filter;
			}else{
				result = super.iFocData_getDataByPath(path);
			}
		}else{
			result = super.iFocData_getDataByPath(path);
		}
    return result;
  }
}
