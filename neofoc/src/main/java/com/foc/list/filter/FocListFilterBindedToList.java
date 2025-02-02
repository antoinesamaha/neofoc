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

import com.foc.desc.FocConstructor;
import com.foc.list.FocList;
import com.foc.list.FocListWithFilter;

/**
 * @author 01Barmaja
 */
public class FocListFilterBindedToList extends FocListFilter {  
	private static final String FILTER_TABLE_NAME_SUFFIX = "_FILTER";
  private FocListWithFilter focList = null;
  
  public FocListFilterBindedToList(FocConstructor constr){
    super(constr);
    setFilterLevel(FocListFilter.LEVEL_DATABASE);
  }

	public void dispose(){
  	super.dispose();
  	focList = null;
  }

  public FocList getGuiFocList(){ // It was getFocList()changed to getGuiFocList because of getSelectionPanel = null   
  	return focList;
  }

  public void setGuiFocList(FocListWithFilter focList){ // It was getFocList()changed to getGuiFocList because of getSelectionPanel = null   
  	this.focList = focList;
  }

  @Override
  public void setActive(FocListWithFilter focList, boolean active) {
  	setGuiFocList(focList);
  	setActive(active);
  }
  
  @Override
	public void reloadListFromDatabase() {
		focList.reloadFromDB_Super();
	}
	
 /* public void refreshDisplay(){
  }*/
  
	public static String getFilterTableName(String baseTableName){
		return baseTableName == null ? null : baseTableName + FocListFilterBindedToList.FILTER_TABLE_NAME_SUFFIX;
	}

	@Override
	public FilterDesc getThisFilterDesc() {
		IFocDescForFilter focDesc = (IFocDescForFilter) getThisFocDesc();
		return focDesc.getFilterDesc();
	}
}
