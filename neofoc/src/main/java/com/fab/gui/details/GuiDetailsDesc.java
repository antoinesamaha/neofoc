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

import com.fab.model.table.TableDefinitionDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FObjectField;

public class GuiDetailsDesc extends FocDesc {
	
	public static final int FLD_DESCRIPTION                     = 1;
	public static final int FLD_TABLE_DEFINITION                = 2;
	public static final int FLD_DETAILS_FIELD_LIST              = 3;
	public static final int FLD_TITLE                           = 4;
	public static final int FLD_ADD_SUBJECT_TO_VALIDATION_PANEL = 5;
	public static final int FLD_SHOW_VALIDATION_PANEL           = 6;
	public static final int FLD_VIEW_MODE                       = 7;
	public static final int FLD_IS_DEFAULT_VIEW                 = 8;
	public static final int FLD_IS_SUMMARY_VIEW                 = 9;
	
	public GuiDetailsDesc(){
		this(GuiDetails.class, "USER_DETAILS_VIEW_DEFINTION", TableDefinitionDesc.getInstance());
	}
	
	public GuiDetailsDesc(Class objCls, String dbTable, TableDefinitionDesc tableDefDesc){
		super(objCls, FocDesc.DB_RESIDENT, dbTable, false);
		addReferenceField();
		setGuiBrowsePanelClass(GuiDetailsGuiBrowsePanel.class);
		setGuiDetailsPanelClass(GuiDetailsGuiDetailsPanel.class);
		
		FObjectField objFld = new FObjectField("TABLE_DEFINITION", "table definition", FLD_TABLE_DEFINITION, false, tableDefDesc, "TABLE_DEF_", this, TableDefinitionDesc.FLD_DETAILS_VIEW_LIST);
		objFld.setDisplayField(TableDefinitionDesc.FLD_NAME);
		objFld.setComboBoxCellEditor(TableDefinitionDesc.FLD_NAME);
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		//objFld.setSelectionList(TableDefinitionDesc.getList(FocList.NONE));
		objFld.setWithList(false);
		addField(objFld);
		
		FField fld = new FStringField("DESCRIPTION", "Description", FLD_DESCRIPTION, false, 50);
		addField(fld);

		fld = new FStringField("TITLE", "Title", FLD_TITLE, false, 50);
		addField(fld);

		/*fld = new FIntField("VIEW_ID", "View id", FLD_VIEW_ID, false, 2);
		addField(fld);*/
		
		fld = new FBoolField("ADD_SUBJECT_TO_VALID_PANEL", "Validate subject on save", FLD_ADD_SUBJECT_TO_VALIDATION_PANEL, false);
		addField(fld);
		
		fld = new FBoolField("SHOW_VALIDATION_PANEL", "Show validation panel", FLD_SHOW_VALIDATION_PANEL, false);
		addField(fld);
		
		FMultipleChoiceField multiField = new FMultipleChoiceField("VIEW_MODE", "View mode", FLD_VIEW_MODE, false, 1);
		multiField.addChoice(GuiDetails.VIEW_MODE_ID_NORMAL, GuiDetails.VIEW_MODE_LABEL_NORMAL);
		multiField.addChoice(GuiDetails.VIEW_MODE_ID_TABBED_PANEL, GuiDetails.VIEW_MODE_LABEL_TABBED_PANEL);
		addField(multiField);
		
		fld = new FBoolField("IS_DEFAULT_VIEW", "Is default view", FLD_IS_DEFAULT_VIEW, false);
		addField(fld);
		
		fld = new FBoolField("IS_SUMMARY_VIEW", "Is summary view", FLD_IS_SUMMARY_VIEW, false);
		addField(fld);
	}
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static GuiDetailsDesc focDesc = null;
  
  public static GuiDetailsDesc getInstance() {
    if (focDesc==null){
      focDesc = new GuiDetailsDesc();
    }
    return focDesc;
  }

}
