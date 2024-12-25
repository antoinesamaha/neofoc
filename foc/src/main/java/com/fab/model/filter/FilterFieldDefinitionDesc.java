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
package com.fab.model.filter;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FAttributeLocationField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldPath;
import com.foc.desc.field.FFormulaExpressionField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;

public class FilterFieldDefinitionDesc extends FocDesc {
	public static final int FLD_FILTER_DEFINITION = 1;
	public static final int FLD_CONDITION_PROPERTY_PATH = 2;	
	public static final int FLD_CONDITION_PROPERTY_PATH_2222 = 3;
	
	public static final String DB_TABLE_NAME = "FILTER_FIELD_DEFINITION";
	
	public FilterFieldDefinitionDesc(){
		super(FilterFieldDefinition.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
		setGuiBrowsePanelClass(FilterFieldDefinitionGuiBrowsePanel.class);
		setGuiDetailsPanelClass(FilterFieldDefinitionGuiDetailsPanel.class);
		FField fld = addReferenceField();
		
		FObjectField objFld = new FObjectField("FILTER_DEFINITION", "Filter definition", FLD_FILTER_DEFINITION, false, FilterDefinitionDesc.getInstance(), "FILTER_DEFINITION_", this, FilterDefinitionDesc.FLD_FILTER_FIELD_DEFINITION_LIST);
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setDisplayField(FilterDefinitionDesc.FLD_TITLE);
		objFld.setComboBoxCellEditor(FilterDefinitionDesc.FLD_TITLE);
		addField(objFld);
		
		fld = new FAttributeLocationField("CONDITION_PROPERTY_PATH", "Condition property path", FLD_CONDITION_PROPERTY_PATH_2222, false, FFieldPath.newFieldPath(FilterFieldDefinitionDesc.FLD_FILTER_DEFINITION, FilterDefinitionDesc.FLD_BASE_FOC_DESC));
		addField(fld);
		
		fld = new FFormulaExpressionField("CONDITION_PROP_EXPRESSION", "Condition property path", FLD_CONDITION_PROPERTY_PATH, null);
		addField(fld);
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
    return list;
  }
	
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

	public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, FilterFieldDefinitionDesc.class);
  }

}
