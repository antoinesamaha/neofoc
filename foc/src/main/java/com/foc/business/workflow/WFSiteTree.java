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
package com.foc.business.workflow;

import com.foc.Globals;
import com.foc.business.company.Company;
import com.foc.list.FocList;
import com.foc.tree.objectTree.FObjectTree;

public class WFSiteTree extends FObjectTree {
	
	public static final String ROOT_TITLE = "Site Hierarchy";
	public static WFSiteTree wFAreaTree = null;
	
	private WFSiteTree(){
    super();
    Company comp = Globals.getApp().getCurrentCompany();
    if(comp != null){
//	  	FocList list = comp.getSiteList();
//    	FocListWrapper list = comp.newFocListWrapperForCurrentCompany();
    	FocList list = WFSiteDesc.getInstance().getFocList(FocList.LOAD_IF_NEEDED);
    	
	  	list.loadIfNotLoadedFromDB();
	    setDisplayFieldId(WFSiteDesc.FLD_NAME);
	    growTreeFromFocList(list);
	    getRoot().setTitle(ROOT_TITLE);
    }
  }
	
	public static WFSiteTree newInstance(){
		WFSiteTree wFAreaTree = new WFSiteTree();
		return wFAreaTree;
	}
}
