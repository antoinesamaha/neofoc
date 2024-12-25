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
package com.fab.model.menu;

import com.fab.gui.browse.GuiBrowseDesc;
import com.fab.gui.details.GuiDetailsDesc;
import com.fab.model.table.TableDefinitionDesc;
import com.foc.Globals;
import com.foc.db.DBManager;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class MenuDefinitionDesc extends FocDesc{
	
	public static final int FLD_NAME 													= 1;
	public static final int FLD_TABLE_DEFINITION 							= 3;
	public static final int FLD_USER_BROWSE_VIEW_DEFINITION 	= 4;
	public static final int FLD_USER_DETAILS_VIEW_DEFINITION 	= 5;
	
	public static final String DB_TABLE_NAME = "MENU_DEFINITION";
	
	public MenuDefinitionDesc(){
		super(MenuDefinition.class,FocDesc.DB_RESIDENT, DB_TABLE_NAME,false);
		FField fld = addReferenceField();
		
		fld = new FStringField("NAME","Name",FLD_NAME,false,30);
		addField(fld);

		setWithObjectTree();
		addOrderField();
		
		FObjectField objFld = new FObjectField("TABLE_DEFINITION", "Table definition", FLD_TABLE_DEFINITION, false, TableDefinitionDesc.getInstance(), "TBLE_DEF_");
		objFld.setDisplayField(TableDefinitionDesc.FLD_NAME);
		objFld.setComboBoxCellEditor(TableDefinitionDesc.FLD_NAME);
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setSelectionList(TableDefinitionDesc.getList(FocList.NONE));
		addField(objFld);
		
		objFld = new FObjectField("USER_BROWSE_DEFINITION", "User browse view definition", FLD_USER_BROWSE_VIEW_DEFINITION, false, GuiBrowseDesc.getInstance(), "USER_BROWSE_VIEW_DEFINITION_");
		if(getProvider() == DBManager.PROVIDER_ORACLE || getProvider() == DBManager.PROVIDER_H2){
			objFld.setKeyPrefix("USER_BROWSE_VIEW_DEF_");
		}
		objFld.setDisplayField(GuiBrowseDesc.FLD_LABEL);
		objFld.setComboBoxCellEditor(GuiBrowseDesc.FLD_LABEL);
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setWithList(false);
		addField(objFld);
		
		objFld = new FObjectField("USER_DETAILS_DEFINITION", "User details view definition", FLD_USER_DETAILS_VIEW_DEFINITION, false, GuiDetailsDesc.getInstance(), "USER_DETAILS_VIEW_DEFINITION_");
		if(getProvider() == DBManager.PROVIDER_ORACLE || getProvider() == DBManager.PROVIDER_H2){
			objFld.setKeyPrefix("USER_DETAILS_VIEW_DEF_");
		}		
		objFld.setDisplayField(GuiDetailsDesc.FLD_DESCRIPTION);
		objFld.setComboBoxCellEditor(GuiDetailsDesc.FLD_DESCRIPTION);
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setWithList(false);
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
	
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

	public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, MenuDefinitionDesc.class);
  }

}
