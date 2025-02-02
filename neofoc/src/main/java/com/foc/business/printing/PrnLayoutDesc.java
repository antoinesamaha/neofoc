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
package com.foc.business.printing;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class PrnLayoutDesc extends FocDesc {
	public static final int FLD_CONTEXT     = 1;
	public static final int FLD_FILE_NAME   = 2;
	public static final int FLD_NAME        = FField.FLD_NAME;
	public static final int FLD_DESCRIPTION = FField.FLD_DESCRIPTION;
	
	public static final String DB_TABLE_NAME = "PRN_LAYOUT";
	
	public static final int LEN_LAYOUT_FILE_NAME = 50;
  
	public PrnLayoutDesc(){
		super(PrnLayout.class, FocDesc.NOT_DB_RESIDENT, DB_TABLE_NAME, false);
		setGuiBrowsePanelClass(PrnLayoutGuiBrowsePanel.class);
		setGuiDetailsPanelClass(PrnLayoutGuiDetailsPanel.class);
		addReferenceField();
		
		addNameField();
		addDescriptionField();
		
		FObjectField objFld = new FObjectField("REPORT", "Report", FLD_CONTEXT, PrnContextDesc.getInstance(), this, PrnContextDesc.FLD_LAYOUT_LIST);
		objFld.setSelectionList(PrnContextDesc.getList(FocList.NONE));
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setComboBoxCellEditor(PrnContextDesc.FLD_NAME);
		objFld.setDisplayField(PrnContextDesc.FLD_NAME);
		objFld.setMandatory(true);
		addField(objFld);
		
		FStringField cFld = new FStringField("LAYOUT_FILE", "File", FLD_FILE_NAME, false, LEN_LAYOUT_FILE_NAME);
		addField(cFld);
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
      FocListOrder order = new FocListOrder(FLD_FILE_NAME);
      list.setListOrder(order);
    }
    return list;
  }
  
  public PrnLayout findPrnLayout(String name){
  	PrnLayout layout = null;
  	FocList list = PrnContextDesc.getInstance().getFocList();
  	for(int i=0; i<list.size() && layout == null; i++){
  		PrnContext context = (PrnContext) list.getFocObject(i);
  		layout = context.getLayoutByName(name);
  	}
  	return layout;
  }

	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

	public static PrnLayoutDesc getInstance() {
    return (PrnLayoutDesc) getInstance(DB_TABLE_NAME, PrnLayoutDesc.class);    
  }
}
