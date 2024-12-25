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

import com.fab.model.table.FieldDefinition;
import com.fab.model.table.TableDefinition;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.list.FocList;

public class GuiBrowseColumn extends FocObject {
	
	public GuiBrowseColumn(FocConstructor constr) {
		super(constr);
		newFocProperties();
	}
	
	@Override
	public FocList getObjectPropertySelectionList(int fieldID) {
		FocList list = super.getObjectPropertySelectionList(fieldID);
		if(fieldID == GuiBrowseColumnDesc.FLD_FIELD_DEFINITION){
			GuiBrowse browseViewDefinition = getBrowseViewDefinition();
			if(browseViewDefinition != null){
				TableDefinition tableDefinition = browseViewDefinition.getTableDefinition();
				if(tableDefinition != null){
					list = tableDefinition.getFieldDefinitionList();
				}
			}
		}
		return list;
	}
	
	public void setBrowseViewDefinition(GuiBrowse definition){
		setPropertyObject(GuiBrowseColumnDesc.FLD_BROWSE_VIEW, definition);
	}
	
	public GuiBrowse getBrowseViewDefinition(){
		return (GuiBrowse)getPropertyObject(GuiBrowseColumnDesc.FLD_BROWSE_VIEW);
	}
	
	public void setFieldDefinition(FieldDefinition fieldDefinition){
		setPropertyObject(GuiBrowseColumnDesc.FLD_FIELD_DEFINITION, fieldDefinition);
	}
	
	public FieldDefinition getFieldDefinition(){
		return (FieldDefinition)getPropertyObject(GuiBrowseColumnDesc.FLD_FIELD_DEFINITION);
	}
	
	public void setViewId(int viewId){
		setPropertyInteger(GuiBrowseColumnDesc.FLD_VIEW_ID, viewId);
	}
	
	public int getViewId(){
		return getPropertyInteger(GuiBrowseColumnDesc.FLD_VIEW_ID);
	}

	public boolean isEditable(){
		return getPropertyBoolean(GuiBrowseColumnDesc.FLD_EDITABLE);
	}
}
