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

import java.awt.GridBagConstraints;

import com.fab.gui.details.GuiDetails;
import com.fab.model.table.FieldDefinition;
import com.fab.model.table.TableDefinition;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.gui.FPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

public class GuiBrowse extends FocObject {
	
	public static final int VIEW_TYPE_ID_GRID = 1;
	public static final int VIEW_TYPE_ID_TABBED_PANEL = 2;
	
	public static final String VIEW_TYPE_LABEL_GRID = "GRID";
	public static final String VIEW_TYPE_LABEL_TABBED_PANEL = "TABBED PANEL";

	public GuiBrowse(FocConstructor constr) {
		super(constr);
		newFocProperties();
	}
	
	@Override
	public FocList getObjectPropertySelectionList(int fieldID) {
		FocList list = super.getObjectPropertySelectionList(fieldID);
		if(			fieldID == GuiBrowseDesc.FLD_DETAILS_VIEW_WHEN_EDIT
				|| 	fieldID == GuiBrowseDesc.FLD_DETAILS_VIEW_WHEN_INSERT){
			list = getTableDefinition() != null ? getTableDefinition().getDetailsViewDefinitionList() : null;
		}
		return list;
	}
	
	public void setLabel(String label){
		setPropertyString(GuiBrowseDesc.FLD_LABEL, label);
	}
	
	public String getLabel(){
		return getPropertyString(GuiBrowseDesc.FLD_LABEL);
	}

	public void setTitle(String title){
		setPropertyString(GuiBrowseDesc.FLD_TITLE, title);
	}
	
	public String getTitle(){
		return getPropertyString(GuiBrowseDesc.FLD_TITLE);
	}

	public void setDetailsViewDefinition(GuiDetails definition){
		setPropertyObject(GuiBrowseDesc.FLD_DETAILS_VIEW_WHEN_EDIT, definition);
	}

	public GuiDetails getDetailsViewDefinition(){
		return (GuiDetails)getPropertyObject(GuiBrowseDesc.FLD_DETAILS_VIEW_WHEN_EDIT);
	}

	public void setDetailsViewForInsert(GuiDetails definition){
		setPropertyObject(GuiBrowseDesc.FLD_DETAILS_VIEW_WHEN_INSERT, definition);
	}

	public GuiDetails getDetailsViewForInsert(){
		return (GuiDetails)getPropertyObject(GuiBrowseDesc.FLD_DETAILS_VIEW_WHEN_INSERT);
	}

	public void setTableViewDefinition(TableDefinition tableDefinition){
		setPropertyObject(GuiBrowseDesc.FLD_TABLE_DEFINITION, tableDefinition);
	}
	
	public TableDefinition getTableDefinition(){
		return (TableDefinition)getPropertyObject(GuiBrowseDesc.FLD_TABLE_DEFINITION);
	}
	
	public FocList getBrowseColumnList(){
		FocList list = getPropertyList(GuiBrowseDesc.FLD_BROWSE_COLUMN_LIST);
		return list;
	}
	
	public void setShowEditButton(boolean show){
		setPropertyBoolean(GuiBrowseDesc.FLD_SHOW_EDIT_BUTTON, show);
	}
	
	public boolean isShowEditButton(){
		return getPropertyBoolean(GuiBrowseDesc.FLD_SHOW_EDIT_BUTTON);
	}
	
	/*public void setViewId(int viewId){
		setPropertyInteger(GuiBrowseDesc.FLD_VIEW_ID, viewId);
	}
	
	public int getViewId(){
		return getPropertyInteger(GuiBrowseDesc.FLD_VIEW_ID);
	}*/
	
	public void setShowValidationPanel(boolean show){
		setPropertyBoolean(GuiBrowseDesc.FLD_SHOW_VALIDATION_PANEL, show);
	}
	
	public boolean isShowValidationPanel(){
		return getPropertyBoolean(GuiBrowseDesc.FLD_SHOW_VALIDATION_PANEL);
	}

	public void setColumnAutoResize(boolean show){
		setPropertyBoolean(GuiBrowseDesc.FLD_COLUMN_AUTO_RESIZE, show);
	}
	
	public boolean isColumnAutoResize(){
		return getPropertyBoolean(GuiBrowseDesc.FLD_COLUMN_AUTO_RESIZE);
	}

	public void setViewType(int viewTypeId){
		setPropertyInteger(GuiBrowseDesc.FLD_BROWSE_VIEW_TYPE, viewTypeId);
	}
	
	public int getViewType(){
		return getPropertyInteger(GuiBrowseDesc.FLD_BROWSE_VIEW_TYPE);
	}
	
	public int getFillMode(){
		int mode = GridBagConstraints.NONE;
		int dbMode = getPropertyMultiChoice(GuiBrowseDesc.FLD_BROWSE_FILL);
		switch(dbMode){
		case GuiBrowseDesc.FILL_BOTH:
			mode = FPanel.FILL_BOTH;
			break;
		case GuiBrowseDesc.FILL_NONE:
			mode = FPanel.FILL_NONE;
			break;
		case GuiBrowseDesc.FILL_VERTICAL:
			mode = FPanel.FILL_VERTICAL;
			break;
		case GuiBrowseDesc.FILL_HORIZONTAL:
			mode = FPanel.FILL_HORIZONTAL;
			break;
		}
		return mode;
	}

	public void addColumns(FTableView tableView, FocDesc focDesc, boolean editable){
		FocList browseColumnList = getBrowseColumnList();
		for(int i = 0; i < browseColumnList.size(); i++){
			GuiBrowseColumn browseColumn = (GuiBrowseColumn)browseColumnList.getFocObject(i);
			if(browseColumn != null){
				FieldDefinition fieldDefinition = browseColumn.getFieldDefinition();
				tableView.addColumn(focDesc, fieldDefinition.getID(), browseColumn.isEditable() && editable);
			}
		}
	}
}
