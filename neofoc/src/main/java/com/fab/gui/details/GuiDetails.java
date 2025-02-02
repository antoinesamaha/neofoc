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

import com.fab.model.table.FieldDefinition;
import com.fab.model.table.FieldDefinitionDesc;
import com.fab.model.table.TableDefinition;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class GuiDetails extends FocObject {
	
	public static final int VIEW_MODE_ID_NORMAL = 1;
	public static final int VIEW_MODE_ID_TABBED_PANEL = 2;
	
	public static final String VIEW_MODE_LABEL_NORMAL = "NORMAL";
	public static final String VIEW_MODE_LABEL_TABBED_PANEL = "TABBED PANEL";

	private boolean firstCallToGetDetailsComponentList = true;
	
	public GuiDetails(FocConstructor constr) {
		super(constr);
		newFocProperties();
		setViewMode(GuiDetails.VIEW_MODE_ID_NORMAL);
	}
	
	public void setDescription(String description){
		setPropertyString(GuiDetailsDesc.FLD_DESCRIPTION, description);
	}
	
	public String getDescription(){
		return getPropertyString(GuiDetailsDesc.FLD_DESCRIPTION);
	}

	public void setTitle(String title){
		setPropertyString(GuiDetailsDesc.FLD_TITLE, title);
	}
	
	public String getTitle(){
		return getPropertyString(GuiDetailsDesc.FLD_TITLE);
	}

	public void setTableDefinition(TableDefinition tableDefinition){
		setPropertyObject(GuiDetailsDesc.FLD_TABLE_DEFINITION, tableDefinition);
	}
	
	public TableDefinition getTableDefinition(){
		return (TableDefinition)getPropertyObject(GuiDetailsDesc.FLD_TABLE_DEFINITION);
	}
	
	public FocList getGuiDetailsFieldList(){
		FocList guiDetailsList = getPropertyList(GuiDetailsDesc.FLD_DETAILS_FIELD_LIST);
		if(firstCallToGetDetailsComponentList){
			firstCallToGetDetailsComponentList = false;
			FocListOrder order = new FocListOrder(GuiDetailsComponentDesc.FLD_Y, GuiDetailsComponentDesc.FLD_X);
			guiDetailsList.setListOrder(order);
		}
		return getPropertyList(GuiDetailsDesc.FLD_DETAILS_FIELD_LIST);
	}
	
	/*public void setViewId(int viewId){
		setPropertyInteger(GuiDetailsDesc.FLD_VIEW_ID, viewId);
	}
	
	public int getViewId(){
		return getPropertyInteger(GuiDetailsDesc.FLD_VIEW_ID);
	}*/
	
	public void setAddSubjectToValidationPanel(boolean add){
		setPropertyBoolean(GuiDetailsDesc.FLD_ADD_SUBJECT_TO_VALIDATION_PANEL, add);
	}
	
	public boolean isAddSubjectToValidationPanel(){
		return getPropertyBoolean(GuiDetailsDesc.FLD_ADD_SUBJECT_TO_VALIDATION_PANEL);
	}
	
	public void setShowValidationPanel(boolean show){
		setPropertyBoolean(GuiDetailsDesc.FLD_SHOW_VALIDATION_PANEL, show);
	}
	
	public boolean isShowValidationPanel(){
		return getPropertyBoolean(GuiDetailsDesc.FLD_SHOW_VALIDATION_PANEL);
	}
	
	public void setViewMode(int viewMode){
		setPropertyInteger(GuiDetailsDesc.FLD_VIEW_MODE, viewMode);
	}
	
	public int getViewMode(){
		return getPropertyInteger(GuiDetailsDesc.FLD_VIEW_MODE);
	}
	
	public void setDefaultView(boolean defaultView){
		setPropertyBoolean(GuiDetailsDesc.FLD_IS_DEFAULT_VIEW, defaultView);
	}
	
	public boolean isDefaultView(){
		return getPropertyBoolean(GuiDetailsDesc.FLD_IS_DEFAULT_VIEW);
	}
	
	public void setSummaryView(boolean summaryVeiw){
		setPropertyBoolean(GuiDetailsDesc.FLD_IS_SUMMARY_VIEW, summaryVeiw);
	}
	
	public boolean isSummaryView(){
		return getPropertyBoolean(GuiDetailsDesc.FLD_IS_SUMMARY_VIEW);
	}
	
	public GuiDetailsComponent addGuiDetailsFieldForFField(FField fField){
    GuiDetailsComponent guiDetailsField = null;
		FocList guiDetailsFieldList = getGuiDetailsFieldList();
		if(guiDetailsFieldList != null){
			guiDetailsField = (GuiDetailsComponent)guiDetailsFieldList.newEmptyItem();
      guiDetailsField.setDbResident(false);
      FocConstructor constr = new FocConstructor(FieldDefinitionDesc.getInstance(), null);
      FieldDefinition fieldDefinition = (FieldDefinition)constr.newItem();
      fieldDefinition.setDbResident(false);
      fieldDefinition.setCreated(true);
			fieldDefinition.setID(fField.getID());
      guiDetailsField.setFieldDefinition(fieldDefinition);
			guiDetailsFieldList.add(guiDetailsField);
		}
    return guiDetailsField;
	}
}
