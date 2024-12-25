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
package com.fab.gui.details;

import com.fab.gui.browse.GuiBrowseDesc;
import com.fab.model.table.FieldDefinitionDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FField;
import com.foc.desc.field.FIntField;
import com.foc.desc.field.FObjectField;

public class GuiDetailsComponentDesc extends FocDesc {
	
	public static final int FLD_GUI_DETAILS           = 1;
	public static final int FLD_FIELD_DEFINITION      = 2;
	public static final int FLD_X                     = 3;
	public static final int FLD_Y                     = 4;
	//public static final int FLD_VIEW_ID = 5;
	public static final int FLD_COLUMNS               = 5;
	public static final int FLD_COMPONENT_GUI_DETAILS = 6;
	public static final int FLD_COMPONENT_GUI_BROWSE  = 7;
	public static final int FLD_GRID_WIDTH            = 8;
	public static final int FLD_GRID_HEIGHT           = 9;
	
	public GuiDetailsComponentDesc(){
		super(GuiDetailsComponent.class, FocDesc.DB_RESIDENT, "DETAILS_FEILD_DEFINITION", false);
		FField fld = addReferenceField();
		
		FObjectField objFld = new FObjectField("DETAILS_VIEW", "Details view", FLD_GUI_DETAILS, false, GuiDetailsDesc.getInstance(), "DETAILS_VIEW_", this, GuiDetailsDesc.FLD_DETAILS_FIELD_LIST);
		objFld.setDisplayField(GuiDetailsDesc.FLD_DESCRIPTION);
		objFld.setComboBoxCellEditor(GuiDetailsDesc.FLD_DESCRIPTION);
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setWithList(false);
		addField(objFld);
		
		objFld = new FObjectField("FIELD_DEFINITION", "Field definition", FLD_FIELD_DEFINITION, false, FieldDefinitionDesc.getInstance(), "FIELD_DEF_");
		objFld.setDisplayField(FieldDefinitionDesc.FLD_NAME);
		objFld.setComboBoxCellEditor(FieldDefinitionDesc.FLD_NAME);
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setNullValueDisplayString("THIS");
    objFld.setWithList(false);
		addField(objFld);
		
		fld = new FIntField("X", "X",FLD_X, false, 2);
		addField(fld);
		
		fld = new FIntField("Y", "Y", FLD_Y, false, 2);
		addField(fld);

		fld = new FIntField("GRID_WIDTH", "Grid|Width", FLD_GRID_WIDTH, false, 2);
		addField(fld);
		
		fld = new FIntField("GRID_HEIGHT", "Grid|Height", FLD_GRID_HEIGHT, false, 2);
		addField(fld);

		fld = new FIntField("COLUMNS", "Columns", FLD_COLUMNS, false, 2);
		addField(fld);
		
		/*fld = new FIntField("VIEW_ID", "View id", FLD_VIEW_ID, false, 2);
		addField(fld);*/
		
		objFld = new FObjectField("GUI_DETAILS", "Gui details", FLD_COMPONENT_GUI_DETAILS, false, GuiDetailsDesc.getInstance(), "GUI_DETAILS_");
		objFld.setDisplayField(GuiDetailsDesc.FLD_DESCRIPTION);
		objFld.setComboBoxCellEditor(GuiDetailsDesc.FLD_DESCRIPTION);
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setWithList(false);
		addField(objFld);
		
		objFld = new FObjectField("GUI_BROWSE", "Gui browse", FLD_COMPONENT_GUI_BROWSE, false, GuiBrowseDesc.getInstance(), "GUI_BROWSE_");
		objFld.setDisplayField(GuiBrowseDesc.FLD_LABEL);
		objFld.setComboBoxCellEditor(GuiBrowseDesc.FLD_LABEL);
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setWithList(false);
		addField(objFld);
	}
	
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;
  
  public static FocDesc getInstance() {
    if (focDesc==null){
      focDesc = new GuiDetailsComponentDesc();;
    }
    return focDesc;
  }
}
