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
package com.fab.model.table;

import java.awt.GridBagConstraints;

import com.foc.ConfigInfo;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.gui.FGTextField;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

@SuppressWarnings("serial")
public class TableDefinitionGuiDetailsPanel_ForNewItem extends FPanel {
	
	public static final int VIEW_IN_TABBED = 1;
	
	private FPanel            webDetailsPanel = null;
	private FPropertyListener webListener     = null;
	private TableDefinition   tableDefinition = null;
	
	public TableDefinitionGuiDetailsPanel_ForNewItem(FocObject focObj, int viewID){
		super("New Table definition", FILL_NONE);
		tableDefinition = (TableDefinition) focObj;
		int y = 0;
		
		FPanel upPanel = new FPanel();
		
		if(ConfigInfo.isForDevelopment()){
			upPanel.add(tableDefinition, TableDefinitionDesc.FLD_PROJECT, 0, y++);
			FGTextField txtFld = (FGTextField) upPanel.add(tableDefinition, TableDefinitionDesc.FLD_SERVER_SIDE_PACKAGE, 0, y++);
			txtFld.setColumns(50);
		}
		
		upPanel.add(tableDefinition, FField.FLD_FAB_OWNER, 0, y++);
		upPanel.add(tableDefinition, TableDefinitionDesc.FLD_NAME, 0, y++);
		upPanel.add(tableDefinition, TableDefinitionDesc.FLD_TITLE, 0, y++);
		tableDefinition.adjustClassNameFromTableName_IfEmpty();
		upPanel.add(tableDefinition, TableDefinitionDesc.FLD_CLASS_NAME, 0, y++);
		upPanel.add(tableDefinition, TableDefinitionDesc.FLD_EXISTING_TABLE, 0, y++);
		upPanel.add(tableDefinition, TableDefinitionDesc.FLD_DB_RESIDENT, 0, y++);
		upPanel.add(tableDefinition, TableDefinitionDesc.FLD_KEY_UNIQUE, 0, y++);
		upPanel.add(tableDefinition, TableDefinitionDesc.FLD_SINGLE_INSTANCE, 0, y++);
		upPanel.add(tableDefinition, TableDefinitionDesc.FLD_WEB_STRUCTURE, 0, y++);
		upPanel.add(tableDefinition, TableDefinitionDesc.FLD_HAS_WORKFLOW, 0, y++);
		upPanel.add(tableDefinition, TableDefinitionDesc.FLD_SHOW_IN_MENU, 0, y++);
		
		webDetailsPanel = new FPanel();
		webDetailsPanel.add(tableDefinition, TableDefinitionDesc.FLD_WEB_CLIENT_PROJECT, 0, 0);
		FGTextField txtFld = (FGTextField) webDetailsPanel.add(tableDefinition, TableDefinitionDesc.FLD_WEB_CLIENT_PACKAGE, 0, 1);
		txtFld.setColumns(50);
		
		add(upPanel, 0, 0, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL);
		add(webDetailsPanel, 0, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL);
		
		webListener = new FPropertyListener() {
			@Override
			public void propertyModified(FProperty property) {
				TableDefinition tableDef = (TableDefinition) (property != null ? property.getFocObject() : null);
				if(tableDef != null){
					webDetailsPanel.setVisible(tableDef.isWebStructure());
					if(tableDef.getProject_WebClient() == null){
						tableDef.setProject_WebClient(tableDef.getProject());
					}
					if(tableDef.getPackageName_WebClient().isEmpty()){
						tableDef.setPackageName_WebClient(tableDef.getPackageName_ServerSide());
					}
				}
			}
			
			@Override
			public void dispose() {
			}
		};
		tableDefinition.getFocProperty(TableDefinitionDesc.FLD_WEB_STRUCTURE).addListener(webListener);
		
		if(viewID != VIEW_IN_TABBED){
			FValidationPanel panel = showValidationPanel(true);
			if(panel != null){
				panel.addSubject(tableDefinition);
			}
		}
	}
	
	public void dispose(){
		super.dispose();
		if(tableDefinition != null && webListener != null){
			tableDefinition.getFocProperty(TableDefinitionDesc.FLD_WEB_STRUCTURE).removeListener(webListener);
		}
		tableDefinition = null;
		if(webListener != null){
			webListener.dispose();
			webListener = null;
		}
	}
}
