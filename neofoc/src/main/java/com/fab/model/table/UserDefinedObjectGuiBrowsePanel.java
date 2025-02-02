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

import com.fab.gui.browse.GuiBrowse;
import com.fab.gui.details.GuiDetails;
import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class UserDefinedObjectGuiBrowsePanel extends FListPanel {
	
	public UserDefinedObjectGuiBrowsePanel(FocList list, int viewId){
		super("Browse panel", FPanel.FILL_NONE);
		if(list != null){
			try{
    		setFocList(list);
    	}catch(Exception e){
    		Globals.logException(e);
    	}
    	FocDesc userDifinedfocDesc = list.getFocDesc();
    	GuiBrowse browseViewDefinition = TableDefinition.getBrowseViewDefinitionForFocDescAndViewId(userDifinedfocDesc, viewId);
    	if(browseViewDefinition != null){
    		setMainPanelSising(browseViewDefinition.getFillMode());
    		setFill(browseViewDefinition.getFillMode());
	    	list.setDirectImpactOnDatabase(false);
	    	list.setDirectlyEditable(true);
	    	FTableView tableView = getTableView();
	    	
	    	GuiDetails detailsViewDefinition = browseViewDefinition.getDetailsViewDefinition();
	    	if(detailsViewDefinition != null){
	    		tableView.setDetailPanelViewID(detailsViewDefinition.getReference().getInteger());
	    	}
	    	
	    	GuiDetails detailsViewForInsert = browseViewDefinition.getDetailsViewForInsert();
	    	if(detailsViewForInsert != null){
	    		boolean editAfterInsert = detailsViewForInsert != detailsViewDefinition; 
	    		tableView.setDetailPanelViewIDForNewItem(detailsViewForInsert.getReference().getInteger(), editAfterInsert);
	    		list.setDirectlyEditable(false);
	    		list.setDirectImpactOnDatabase(true);
	    	}
	    	
	    	browseViewDefinition.addColumns(tableView, userDifinedfocDesc, true);
	    	/*
	    	FocList browseColumnList = browseViewDefinition.getBrowseColumnList();
	    	for(int i = 0; i < browseColumnList.size(); i++){
	    		GuiBrowseColumn browseColumn = (GuiBrowseColumn)browseColumnList.getFocObject(i);
	    		if(browseColumn != null){
	    			FieldDefinition fieldDefinition = browseColumn.getFieldDefinition();
	    			tableView.addColumn(userDifinedfocDesc, fieldDefinition.getID(), browseColumn.isEditable());
	    		}
	    	}
	    	*/
	    	construct();
	    	
	    	int colResizeMode = FTableView.COLUMN_WIDTH_FACTOR_MODE;
	    	if(browseViewDefinition.isColumnAutoResize()){
	    		colResizeMode = FTableView.COLUMN_AUTO_RESIZE_MODE;
	    	}
	    	tableView.setColumnResizingMode(colResizeMode);
	    	
	    	if(browseViewDefinition.isShowValidationPanel()){
		    	FValidationPanel savePanel = showValidationPanel(true);
		      if (savePanel != null) {
		        savePanel.addSubject(list);
		      }
	    	}
	      
	      requestFocusOnCurrentItem();
	      showEditButton(browseViewDefinition.isShowEditButton() && browseViewDefinition.getDetailsViewDefinition() != null);
	      showDuplicateButton(false);
	      setFrameTitle(browseViewDefinition.getTitle());
	      setMainPanelSising(browseViewDefinition.getFillMode());
	      setFill(browseViewDefinition.getFillMode());
			}
		}
	}
}
