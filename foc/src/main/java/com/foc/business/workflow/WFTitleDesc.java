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

import com.foc.desc.AutoPopulatable;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class WFTitleDesc extends FocDesc implements AutoPopulatable{
	public static final int FLD_NAME                        = FField.FLD_NAME;
	public static final int FLD_DESCRIPTION                 = FField.FLD_DESCRIPTION;
	public static final int FLD_IS_PROJECT_SPECIFIC         = 1;
	public static final int FLD_USER_DATAPATH_FROM_PROJ_WBS = 2;//PROJECT_MANAGER_SIGNATURE
	
	public static final String TITLE_NAME_GUEST        = "Guest";
	public static final String TITLE_DESCRIPTION_GUEST = "Guest";
	
	public static final String DB_TABLE_NAME = "WF_TITLE";
  
	public WFTitleDesc(){
		super(WFTitle.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, true);
		setGuiBrowsePanelClass(WFTitleGuiBrowsePanel.class);
		setGuiDetailsPanelClass(WFTitleGuiDetailsPanel.class);
		addReferenceField();
		
		addNameField();
		addDescriptionField();
		
		FBoolField bFld = new FBoolField("PROJECT_SPECIFIC", "Project specific", FLD_IS_PROJECT_SPECIFIC, false);
		addField(bFld);
		
		FStringField charField = new FStringField("USER_DATAPATH_FROM_PROJ_WBS", "User Datapath From Proj WBS", FLD_USER_DATAPATH_FROM_PROJ_WBS, false, 200);
		addField(charField);
  }
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
	
	public static FocList getList(int mode){
    return getInstance().getFocList(mode);
  }
  
  @Override
  public FocList newFocList(){
    FocList list = super.newFocList();
    list.setDirectlyEditable(false);
    list.setDirectImpactOnDatabase(true);
    if(list.getListOrder() == null){
      FocListOrder order = new FocListOrder(FLD_NAME);
      list.setListOrder(order);
    }
    return list;
  }
	
	public static WFTitle getTitleByName(String name){
		WFTitle title = null;
		FocList list = getList(FocList.LOAD_IF_NEEDED);
		title = (WFTitle) list.searchByPropertyStringValue(FLD_NAME, name);
		return title;
	}
	
	public WFTitle findOrAddTitle(String titleName){
		WFTitle title = WFTitleDesc.getTitleByName(titleName);
		
		if(title == null){
			FocList list = WFTitleDesc.getList(FocList.LOAD_IF_NEEDED);
			title = (WFTitle) list.newEmptyItem();
			title.setName(titleName);
			list.add(title);
		}
			
		return title;
	}
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

	public static WFTitleDesc getInstance() {
    return (WFTitleDesc) getInstance(DB_TABLE_NAME, WFTitleDesc.class);    
  }
	
	public WFTitle findWFTitleAndCreateIfNotExist(String titleName){
		return findWFTitleAndCreateIfNotExist(titleName, true);
	}
	
	public WFTitle findWFTitleAndCreateIfNotExist(String titleName, boolean create){
  	WFTitle title = null;
  	FocList titleList = WFTitleDesc.getInstance().getFocList(FocList.LOAD_IF_NEEDED);
  	title = (WFTitle) titleList.searchByPropertyStringValue(WFTitleDesc.FLD_NAME, titleName);
  	if(title == null && create){
			title = (WFTitle) WFTitleDesc.getInstance().getFocList().newEmptyItem();
			title.setName(titleName);
			title.setDescription(titleName);
			title.validate(true);
			titleList.add(title);
			titleList.validate(true);
  	}
		return title;
  }

	@Override
	public boolean populate() {
		FocList focList = WFTitleDesc.getList(FocList.LOAD_IF_NEEDED);
		if(focList != null){
			WFTitle wfTitle = (WFTitle) focList.newEmptyItem();
			wfTitle.setName(TITLE_NAME_GUEST);
			wfTitle.setDescription(TITLE_DESCRIPTION_GUEST);
			wfTitle.validate(true);
		}
		return false;
	}

	@Override
	public String getAutoPopulatableTitle() {
		return "Title";
	}
}
