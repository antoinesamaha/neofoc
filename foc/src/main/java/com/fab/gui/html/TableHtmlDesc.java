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
package com.fab.gui.html;

import javax.swing.JEditorPane;

import com.fab.model.table.TableDefinitionDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FBlobStringField;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FObjectField;

public class TableHtmlDesc extends FocDesc {
	
	public static final int FLD_TABLE_DEFINITION   = 1;
	public static final int FLD_TITLE              = 2;
	public static final int FLD_DESCRIPTION        = 3;
	public static final int FLD_HTML               = 4;
	
	public TableHtmlDesc(){
		this(TableHtml.class, "USER_HTML_FORMS", TableDefinitionDesc.getInstance());
	}
	
	public TableHtmlDesc(Class objCls, String dbTable, TableDefinitionDesc tableDefDesc){
		super(objCls, FocDesc.DB_RESIDENT, dbTable, false);
		addReferenceField();
		setGuiBrowsePanelClass(TableHtmlGuiBrowsePanel.class);
		setGuiDetailsPanelClass(TableHtmlGuiDetailsPanel.class);
		
		FObjectField objFld = new FObjectField("TABLE_DEFINITION", "table definition", FLD_TABLE_DEFINITION, false, tableDefDesc, "TABLE_DEF_", this, TableDefinitionDesc.FLD_HTML_FORM_LIST);
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

		FBlobStringField blobFld = new FBlobStringField("HTML", "Html", FLD_HTML, false, 20, 100);
		addField(blobFld);
	}
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static TableHtmlDesc focDesc = null;
  
  public static TableHtmlDesc getInstance() {
    if(focDesc == null){
      focDesc = new TableHtmlDesc();
    }
    return focDesc;
  }

}
