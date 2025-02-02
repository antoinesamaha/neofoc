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

import com.fab.model.table.FieldDefinitionDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FIntField;
import com.foc.desc.field.FObjectField;

public class GuiBrowseColumnDesc extends FocDesc {
	
	public static final int FLD_BROWSE_VIEW      = 1;
	public static final int FLD_FIELD_DEFINITION = 2;
	public static final int FLD_VIEW_ID          = 3;
	public static final int FLD_EDITABLE         = 4;
	
	public GuiBrowseColumnDesc(){
		this(GuiBrowseColumn.class, "BROWSE_COLUMN", GuiBrowseDesc.getInstance(), FieldDefinitionDesc.getInstance());
	}
	
	public GuiBrowseColumnDesc(Class objCls, String dbName, GuiBrowseDesc browseFocDesc, FieldDefinitionDesc fieldDefinitionDesc){
		super(objCls, FocDesc.DB_RESIDENT, dbName, false);
		FField fld = addReferenceField();
		
		FObjectField objFld = new FObjectField("BROWSE_VIEW", "Browse view", FLD_BROWSE_VIEW, false, browseFocDesc, "BROWSE_VIEW_", this, GuiBrowseDesc.FLD_BROWSE_COLUMN_LIST);
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setDisplayField(GuiBrowseDesc.FLD_LABEL);
		objFld.setComboBoxCellEditor(GuiBrowseDesc.FLD_LABEL);
		objFld.setWithList(false);
		addField(objFld);
		
		objFld = new FObjectField("FIELD_DEFINITION", "Field definition", FLD_FIELD_DEFINITION, false, fieldDefinitionDesc, "FIELD_DEF_");
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setDisplayField(FieldDefinitionDesc.FLD_NAME);
		objFld.setComboBoxCellEditor(FieldDefinitionDesc.FLD_NAME);
		objFld.setWithList(false);
		addField(objFld);
		
		fld = new FIntField("VIEW_ID", "View id", FLD_VIEW_ID, false, 2);
		addField(fld);
		
		FBoolField bFld = new FBoolField("EDITABLE", "Editable", FLD_EDITABLE, false);
		addField(bFld);
	}
	
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;
  
  public static FocDesc getInstance() {
    if (focDesc==null){
      focDesc = new GuiBrowseColumnDesc();;
    }
    return focDesc;
  }

}
