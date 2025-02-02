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
package com.fab.gui.browse;

import com.fab.gui.details.GuiDetailsDesc;
import com.fab.model.table.TableDefinitionDesc;
import com.foc.db.DBManager;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FObjectField;
import com.foc.desc.field.FStringField;

public class GuiBrowseDesc extends FocDesc {
	
	public static final int FLD_LABEL                    = 1;
	public static final int FLD_TABLE_DEFINITION         = 2;
	public static final int FLD_DETAILS_VIEW_WHEN_EDIT   = 3;
	public static final int FLD_DETAILS_VIEW_WHEN_INSERT = 4;
	public static final int FLD_BROWSE_COLUMN_LIST       = 5;
	public static final int FLD_SHOW_EDIT_BUTTON         = 6;
	public static final int FLD_TITLE                    = 7;
	public static final int FLD_SHOW_VALIDATION_PANEL    = 8;
	public static final int FLD_BROWSE_VIEW_TYPE         = 9;
	public static final int FLD_BROWSE_FILL              = 10;
	public static final int FLD_COLUMN_AUTO_RESIZE       = 11;	
	
	public static final int FILL_NONE       = 0;
	public static final int FILL_BOTH       = 1;
	public static final int FILL_VERTICAL   = 2;
	public static final int FILL_HORIZONTAL = 3;

	public GuiBrowseDesc(){
		this(GuiBrowse.class, "USER_BROWSE_VIEW_DEFINITION", TableDefinitionDesc.getInstance(), GuiDetailsDesc.getInstance());
	}
	
	public GuiBrowseDesc(Class objCls, String dbTable, TableDefinitionDesc tableDefinitionDesc, GuiDetailsDesc guiDetailsDesc){
		super(objCls, FocDesc.DB_RESIDENT, dbTable, false);
		
		setGuiBrowsePanelClass(GuiBrowseGuiBrowsePanel.class);
		setGuiDetailsPanelClass(GuiBrowseGuiDetailsPanel.class);
		
		addReferenceField();

		FField fld = new FStringField("LABEL", "Label", FLD_LABEL, false, 50);
		addField(fld);

		fld = new FStringField("TITLE", "Title", FLD_TITLE, false, 50);
		addField(fld);

		FObjectField objFld = new FObjectField("TABLE_DEFINITION", "table definition", FLD_TABLE_DEFINITION, false, tableDefinitionDesc, "TABLE_DEF_", this, TableDefinitionDesc.FLD_BROWSE_VIEW_LIST);
		objFld.setDisplayField(TableDefinitionDesc.FLD_NAME);
		objFld.setComboBoxCellEditor(TableDefinitionDesc.FLD_NAME);
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		//objFld.setSelectionList(TableDefinitionDesc.getList(FocList.NONE));
		objFld.setWithList(false);
		addField(objFld);
		
		objFld = new FObjectField("USER_DETAILS_DEFINITION", "Details View|When 'Edit'", FLD_DETAILS_VIEW_WHEN_EDIT, false, GuiDetailsDesc.getInstance(), "USER_DETAILS_VIEW_DEFINITION_");
		if(getProvider() == DBManager.PROVIDER_ORACLE){
			objFld.setKeyPrefix("USER_DETAILS_VIEW_DEF_");
		}		
		objFld.setDisplayField(GuiDetailsDesc.FLD_DESCRIPTION);
		objFld.setComboBoxCellEditor(GuiDetailsDesc.FLD_DESCRIPTION);
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		addField(objFld);

		objFld = new FObjectField("DETAILS_VIEW_WHEN_NEW", "Details View|When 'Insert'", FLD_DETAILS_VIEW_WHEN_INSERT, false, GuiDetailsDesc.getInstance(), "DETAILS_VIEW_INSERT_");
		objFld.setDisplayField(GuiDetailsDesc.FLD_DESCRIPTION);
		objFld.setComboBoxCellEditor(GuiDetailsDesc.FLD_DESCRIPTION);
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		addField(objFld);

		fld = new FBoolField("SHOW_EDIT_BUTTON", "Show Edit|Button", FLD_SHOW_EDIT_BUTTON, false);
		addField(fld);
		
		/*fld = new FIntField("VIEW_ID", "View id", FLD_VIEW_ID, false, 2);
		addField(fld);*/
		
		fld = new FBoolField("SHOW_VALIDATION_PANEL", "Show Validation|Buttons", FLD_SHOW_VALIDATION_PANEL, false);
		addField(fld);

		fld = new FBoolField("COL_RESIZE_AUTOMATIC", "Column Resize|Automatic", FLD_COLUMN_AUTO_RESIZE, false);
		addField(fld);

		FMultipleChoiceField multipleChoice = new FMultipleChoiceField("VIEW_TYPE", "View Layout", FLD_BROWSE_VIEW_TYPE, false, 1);
		multipleChoice.addChoice(GuiBrowse.VIEW_TYPE_ID_GRID, GuiBrowse.VIEW_TYPE_LABEL_GRID);
		multipleChoice.addChoice(GuiBrowse.VIEW_TYPE_ID_TABBED_PANEL, GuiBrowse.VIEW_TYPE_LABEL_TABBED_PANEL);
		addField(multipleChoice);
		
		FMultipleChoiceField fillModeChoice = new FMultipleChoiceField("FILL_MODE", "Fill", FLD_BROWSE_FILL, false, 1);
		fillModeChoice.addChoice(FILL_BOTH, "Both");
		fillModeChoice.addChoice(FILL_VERTICAL, "Vertical");
		fillModeChoice.addChoice(FILL_HORIZONTAL, "Horizontal");
		fillModeChoice.addChoice(FILL_NONE, "None");
		addField(fillModeChoice);
	}
	
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static GuiBrowseDesc focDesc = null;
  
  public static GuiBrowseDesc getInstance() {
    if(focDesc == null){
      focDesc = new GuiBrowseDesc();
    }
    return focDesc;
  }

}
