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
package com.fab.model.project;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class FabProjectDesc extends FocDesc {
	
	public static final int FLD_NAME           = FField.FLD_NAME;
	public static final int FLD_PROJECT_PATH   = 2;
	public static final int FLD_COMMON_PACKAGE = 3;
	public static final int FLD_WORKSPACE      = 4;
	
	public static final String DB_TABLE_NAME = "FAB_PROJECT";
	
	public FabProjectDesc(){
		super(FabProject.class,FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
		setGuiBrowsePanelClass(FabProjectGuiBrowsePanel.class);
		addReferenceField();
		
		FField nameFld = addNameField();
		nameFld.setSize(50);
		nameFld.setMandatory(true);

		FStringField charFld = new FStringField("PROJECT_PATH", "Project source root", FLD_PROJECT_PATH, false, 250);
		addField(charFld);
		charFld.setMandatory(true);

		charFld = new FStringField("COMMON_PACKAGE", "Project common package", FLD_COMMON_PACKAGE, false, 250);
		addField(charFld);

		FObjectField objFld = new FObjectField("WORKSPACE", "Workspace", FLD_WORKSPACE, FabWorkspaceDesc.getInstance());
		objFld.setSelectionList(FabWorkspaceDesc.getList(FocList.NONE));
		objFld.setComboBoxCellEditor(FabWorkspaceDesc.FLD_NAME);
		addField(objFld);
	}
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
	
	public static FocList getList(int mode){
    return getInstance().getFocList(mode);
  }
  
  public FocList newFocList(){
    FocList list = super.newFocList();
    list.setDirectlyEditable(true);
    list.setDirectImpactOnDatabase(false);
    if(list.getListOrder() == null){
      FocListOrder order = new FocListOrder(FLD_NAME);
      list.setListOrder(order);
    }
    return list;
  }

	public static FabProject getFabProjectByName(String projName){
		FocList    list = getList(FocList.LOAD_IF_NEEDED);
		FabProject prj  = (FabProject) list.searchByPropertyStringValue(FabProjectDesc.FLD_NAME, projName);
		return prj;
	}
	
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static FabProjectDesc getInstance() {
    return (FabProjectDesc) getInstance(DB_TABLE_NAME, FabProjectDesc.class);
  }
}
