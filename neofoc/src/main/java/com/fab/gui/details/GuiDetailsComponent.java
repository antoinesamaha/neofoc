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

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTextField;

import com.fab.businessObjectDeclaration.FabBusinessObjectDeclaration;
import com.fab.gui.browse.GuiBrowse;
import com.fab.model.table.FieldDefinition;
import com.fab.model.table.TableDefinition;
import com.fab.model.table.TableDefinitionDesc;
import com.fab.model.table.UserDefinedObjectGuiBrowsePanel;
import com.fab.model.table.UserDefinedObjectGuiDetailsPanel;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.gui.StaticComponent;
import com.foc.list.FocList;

public class GuiDetailsComponent extends FocObject{
	
	public GuiDetailsComponent(FocConstructor constr) {
		super(constr);
		newFocProperties();
	}

	public TableDefinition getTableDefinition(){
		FocList   listOfGuiComp            = (FocList) getFatherSubject();
		FocObject guiDetailsPanel          = listOfGuiComp != null ? (FocObject) listOfGuiComp.getFatherSubject() : null;
		FocList   listOfGuiDetailsPanels   = guiDetailsPanel != null ? (FocList) guiDetailsPanel.getFatherSubject() : null;
		return listOfGuiDetailsPanels != null ? (TableDefinition) listOfGuiDetailsPanels.getFatherSubject() : null;
	}
	
	@Override
  public boolean isPropertyLocked(int fieldId){
		boolean locked = false;
		if(fieldId == GuiDetailsComponentDesc.FLD_COMPONENT_GUI_DETAILS){
			FieldDefinition fieldDefinition = (FieldDefinition) getFieldDefinition();
			locked = fieldDefinition != null && fieldDefinition.getSQLType() != FieldDefinition.SQL_TYPE_ID_OBJECT_FIELD;
		}else if(fieldId == GuiDetailsComponentDesc.FLD_COMPONENT_GUI_BROWSE){
			FieldDefinition fieldDefinition = (FieldDefinition) getFieldDefinition();
			locked = fieldDefinition != null && fieldDefinition.getSQLType() != FieldDefinition.SQL_TYPE_ID_LIST_FIELD;
		}
  	return locked;
  }

	@Override
	public FocList getObjectPropertySelectionList(int fieldID) {
		FocList list = super.getObjectPropertySelectionList(fieldID);
		if(fieldID == GuiDetailsComponentDesc.FLD_COMPONENT_GUI_DETAILS){
			FieldDefinition fieldDefinition = (FieldDefinition) getFieldDefinition();
			if(fieldDefinition != null){
				FocDesc         focDesc         = fieldDefinition.getFocDesc();
				if(focDesc != null){
					FocList         tableDefinitionList      = TableDefinitionDesc.getList(FocList.LOAD_IF_NEEDED);
					TableDefinition tableDefinition          = (TableDefinition) tableDefinitionList.searchByPropertyStringValue(TableDefinitionDesc.FLD_NAME, focDesc.getStorageName());
					list = (tableDefinition != null) ? tableDefinition.getDetailsViewDefinitionList() : null;
				}
			}else{
				FocList 		listOfComponentsInDetailGui = (FocList) getFatherSubject();
				GuiDetails 	guiDetails 									= listOfComponentsInDetailGui != null ? (GuiDetails) listOfComponentsInDetailGui.getFatherSubject() : null;
				FocList     listOfGuiDetails            = guiDetails != null ? (FocList) guiDetails.getFatherSubject() : null;
				TableDefinition tableDefinition = listOfGuiDetails != null ? (TableDefinition) listOfGuiDetails.getFatherSubject() : null;
				list = (tableDefinition != null) ? tableDefinition.getDetailsViewDefinitionList() : null;
			}
		}else if(fieldID == GuiDetailsComponentDesc.FLD_COMPONENT_GUI_BROWSE){
			FieldDefinition fieldDefinition = (FieldDefinition) getFieldDefinition();
			if(fieldDefinition != null){
				FocDesc         slaveFocDesc    = fieldDefinition.getSlaveFocDesc();
				if(slaveFocDesc != null){
					FocList         tableDefinitionList     = (FocList) getTableDefinition().getFatherSubject();//TableDefinitionDesc.getList(FocList.LOAD_IF_NEEDED);
					TableDefinition tableDefinition         = (TableDefinition) tableDefinitionList.searchByPropertyStringValue(TableDefinitionDesc.FLD_NAME, slaveFocDesc.getStorageName());
					list = tableDefinition != null ? tableDefinition.getBrowseViewDefinitionList() : null;
					if(list != null){
						list.loadIfNotLoadedFromDB();
					}	
				}
			}
		}else if(fieldID == GuiDetailsComponentDesc.FLD_FIELD_DEFINITION){
			GuiDetails detailsViewDefinition = getDetailsView();
			if(detailsViewDefinition != null){
				TableDefinition tableDefinition = detailsViewDefinition.getTableDefinition();
        if(tableDefinition != null){
					list = tableDefinition.getFieldDefinitionList();
        }
			}
		}
		return list;
	}
	
	public void setDetailsView(GuiDetails definition){
		setPropertyObject(GuiDetailsComponentDesc.FLD_GUI_DETAILS, definition);
	}
	
	public GuiDetails getDetailsView(){
		return (GuiDetails)getPropertyObject(GuiDetailsComponentDesc.FLD_GUI_DETAILS);
	}
	
	public void setFieldDefinition(FieldDefinition fieldDefinition){
		setPropertyObject(GuiDetailsComponentDesc.FLD_FIELD_DEFINITION, fieldDefinition);
	}
	
	public FieldDefinition getFieldDefinition(){
		return (FieldDefinition)getPropertyObject(GuiDetailsComponentDesc.FLD_FIELD_DEFINITION);
	}
	
	public void setX(int x){
		setPropertyInteger(GuiDetailsComponentDesc.FLD_X, x);
	}
	
	public int getX(){
		return getPropertyInteger(GuiDetailsComponentDesc.FLD_X);
	}
	
	public void setY(int y){
		setPropertyInteger(GuiDetailsComponentDesc.FLD_Y, y);
	}
	
	public int getY(){
		return getPropertyInteger(GuiDetailsComponentDesc.FLD_Y);
	}

	public int getGridWidth(){
		return getPropertyInteger(GuiDetailsComponentDesc.FLD_GRID_WIDTH);
	}

	public int getGridHeight(){
		return getPropertyInteger(GuiDetailsComponentDesc.FLD_GRID_HEIGHT);
	}

	public void setColumns(int cols){
		setPropertyInteger(GuiDetailsComponentDesc.FLD_COLUMNS, cols);
	}
	
	public int getColumns(){
		return getPropertyInteger(GuiDetailsComponentDesc.FLD_COLUMNS);
	}
	
	public GuiDetails getComponentGuiDetails(){
		return (GuiDetails) getPropertyObject(GuiDetailsComponentDesc.FLD_COMPONENT_GUI_DETAILS);
	}
	
	public GuiBrowse getComponentGuiBrowse(){
		return (GuiBrowse) getPropertyObject(GuiDetailsComponentDesc.FLD_COMPONENT_GUI_BROWSE);
	}
		
	public Component getComponentForFocObject(FocObject focObject){
		Component comp = null;
		FieldDefinition fieldDefinition = getFieldDefinition();
		if(fieldDefinition != null){
			if(fieldDefinition.getSQLType() == FieldDefinition.SQL_TYPE_ID_LIST_FIELD){
				FocList list = focObject.getPropertyList(fieldDefinition.getID());
				GuiBrowse componentGuiBrowse = getComponentGuiBrowse();
				if(componentGuiBrowse != null){
					comp = new UserDefinedObjectGuiBrowsePanel(list, componentGuiBrowse.getReference().getInteger());
				}
			}else if(fieldDefinition.getSQLType() == FieldDefinition.SQL_TYPE_ID_OBJECT_FIELD && getComponentGuiDetails() != null){
				FocObject focObj = focObject.getPropertyObject(fieldDefinition.getID());
				comp = new UserDefinedObjectGuiDetailsPanel(focObj, getComponentGuiDetails().getReference().getInteger());
				((UserDefinedObjectGuiDetailsPanel)comp).createBorder();
			}else{
				FField field = focObject.getThisFocDesc().getFieldByID(fieldDefinition.getID());
				comp = focObject.getGuiComponent(field.getID());
				if(fieldDefinition.getID() == FField.FLD_CODE){
					focObject.code_setCodeIfNeeded(fieldDefinition.getID(), fieldDefinition.getDefaultStringValue());
					//focObject.code_setCodeIfNeeded(fieldID, prefix, nbrOfDigits)
					//focObject.code_resetIfCreated();
				}
				String ttt = ((JComponent)comp).getToolTipText();
				if(ttt != null && !ttt.isEmpty()){
					ttt = ttt + " ("+field.getName()+")";
				}else{
					ttt = field.getName();
				}
				StaticComponent.setComponentToolTipText((JComponent)comp, ttt);
			}
		}else{
			GuiDetails componentGuiDetails = getComponentGuiDetails();
			if(componentGuiDetails != null){
				comp = new UserDefinedObjectGuiDetailsPanel(focObject, componentGuiDetails.getReference().getInteger());
				((UserDefinedObjectGuiDetailsPanel)comp).createBorder();
			}
		}
		
		if(getColumns() > 0){
			if(comp instanceof JTextField){
				((JTextField)comp).setColumns(getColumns());
			}
		}		
		return comp;
	}
}
