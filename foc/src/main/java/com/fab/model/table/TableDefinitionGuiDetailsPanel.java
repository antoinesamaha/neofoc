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

import com.fab.FabStatic;
import com.fab.FocApplicationBuilder;
import com.fab.gui.browse.GuiBrowseGuiBrowsePanel;
import com.fab.gui.details.GuiDetailsGuiBrowsePanel;
import com.fab.gui.html.TableHtmlGuiBrowsePanel;
import com.foc.ConfigInfo;
import com.foc.desc.FocObject;
import com.foc.event.FValidationListener;
import com.foc.gui.FGTabbedPane;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;

@SuppressWarnings("serial")
public class TableDefinitionGuiDetailsPanel extends FPanel {
	private TableDefinition tableDefinition = null;
	
	public TableDefinitionGuiDetailsPanel(FocObject tableDefinition, int viewID){
		this(tableDefinition, viewID, true);
	}
	
	public TableDefinitionGuiDetailsPanel(FocObject tableDefinition, int viewID, boolean withBrowseDetailsDictionary){
		setWithScroll(false);
		FGTabbedPane tabbedPan = new FGTabbedPane();
		this.tableDefinition = (TableDefinition)tableDefinition;

		if(viewID != FocApplicationBuilder.VIEW_NO_EDIT){
			FPanel generalPanel = newDetailsPanel_General();
			tabbedPan.add("Definition", generalPanel);
		}
		
		FPanel fieldPanel = newFieldBrowsePanel(viewID, withBrowseDetailsDictionary);
		tabbedPan.add(fieldPanel.getFrameTitle(), fieldPanel);

		if(withBrowseDetailsDictionary){
			if(this.tableDefinition.getBrowseViewDefinitionList() != null){
				FPanel browseViewDefintionBorwsePanel = new GuiBrowseGuiBrowsePanel(this.tableDefinition.getBrowseViewDefinitionList(), viewID);
				tabbedPan.add(browseViewDefintionBorwsePanel.getFrameTitle(), browseViewDefintionBorwsePanel);
			}
			
			if(this.tableDefinition.getDetailsViewDefinitionList() != null){
				FPanel detaildViewDefinitionBrowsePanel = new GuiDetailsGuiBrowsePanel(this.tableDefinition.getDetailsViewDefinitionList(), viewID);
				tabbedPan.add(detaildViewDefinitionBrowsePanel.getFrameTitle(), detaildViewDefinitionBrowsePanel);
			}

			if(this.tableDefinition.getDictionaryGroupList() != null){
				FPanel dictionaryGroupsBrowsePanel = new FabDictionaryGroupGuiBrowsePanel(this.tableDefinition.getDictionaryGroupList(), FocObject.DEFAULT_VIEW_ID);
				tabbedPan.add(dictionaryGroupsBrowsePanel.getFrameTitle(), dictionaryGroupsBrowsePanel);
			}
			
			if(this.tableDefinition.getHtmlFormList() != null){
				FPanel htmlFormsBrowsePanel = new TableHtmlGuiBrowsePanel(this.tableDefinition.getHtmlFormList(), FocObject.DEFAULT_VIEW_ID);
				tabbedPan.add(htmlFormsBrowsePanel.getFrameTitle(), htmlFormsBrowsePanel);
			}
		}
		
		add(tabbedPan, 0, 0);
		
		setFrameTitle(this.tableDefinition.getName());
		setMainPanelSising(FPanel.FILL_BOTH);
		
		if(viewID != FocApplicationBuilder.VIEW_NO_EDIT){
			FValidationPanel validPanel = showValidationPanel(true);
			if(validPanel != null){
				validPanel.addSubject(this.tableDefinition);
				validPanel.setValidationType(FValidationPanel.VALIDATION_SAVE_CANCEL);
				validPanel.setValidationListener(new FValidationListener(){
	
					public void postCancelation(FValidationPanel panel) {
					}
	
					public void postValidation(FValidationPanel panel) {
						postValidationAction();
					}
	
					public boolean proceedCancelation(FValidationPanel panel) {
						return true;
					}
	
					public boolean proceedValidation(FValidationPanel panel) {
						return true;
					}
					
				});
			}
		}
	}
	
	public void dispose(){
		super.dispose();
		tableDefinition = null;
	}

	//To be overriden
	public FieldDefinitionGuiBrowsePanel newFieldBrowsePanel(int viewID, boolean withBrowseDetailsDictionary){
		return new FieldDefinitionGuiBrowsePanel(getTableDefinition().getFieldDefinitionList(), viewID, withBrowseDetailsDictionary);
	}
	
	public FPanel newDetailsPanel_General(){
		FPanel generalPanel = new TableDefinitionGuiDetailsPanel_ForNewItem(this.tableDefinition, TableDefinitionGuiDetailsPanel_ForNewItem.VIEW_IN_TABBED);
		return generalPanel;
	}
	
	public void postValidationAction(){
		TableDefinition tableDef = TableDefinitionGuiDetailsPanel.this.tableDefinition;
		if(tableDef != null){
			tableDef.adjustIFocDescDeclaration();
			if(			tableDef.getDbFocDescDeclaration() != null 
					&& 	tableDef.getDbFocDescDeclaration().getFocDescription() != null
					&&  !ConfigInfo.isForDevelopment()){
				tableDef.getDbFocDescDeclaration().getFocDescription().adaptTableAlone();
			}
		}
		FabStatic.refreshAllTableFieldChoices();
	}

	public TableDefinition getTableDefinition() {
		return tableDefinition;
	}
}
