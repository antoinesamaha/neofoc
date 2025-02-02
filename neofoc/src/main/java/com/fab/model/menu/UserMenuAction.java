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
package com.fab.model.menu;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.fab.model.FabMain;
import com.fab.model.table.TableDefinition;
import com.fab.model.table.UserDefinedObjectGuiBrowsePanel;
import com.fab.model.table.UserDefinedObjectGuiDetailsPanel;
import com.foc.Globals;
import com.foc.desc.FocObject;
import com.foc.gui.FPanel;
import com.foc.list.FocList;

public class UserMenuAction extends AbstractAction {

	private TableDefinition tableDefinition = null;
	private int             detailedViewRef = 0; 
	private int             browseViewRef   = 0;
	
	public UserMenuAction(TableDefinition tableDefinition, int browseViewRef, int detailedViewRef){
		this.tableDefinition = tableDefinition;
		this.detailedViewRef = detailedViewRef;
		this.browseViewRef   = browseViewRef;
	}
	
	public void dispose(){
		tableDefinition = null;
	}

	public void actionPerformed(ActionEvent e) {
		if(tableDefinition != null){
			FocList list = FabMain.getFocList(tableDefinition.getName());
			if(list != null){
			//FocDesc desc = Globals.getApp().getFocDescByName(tableDefinition.getName());
			//if(desc != null){
				//FocLinkSimple linkSimple = new FocLinkSimple(desc);
				//FocList list = new FocList(linkSimple);
				
				//FocList list = FieldDefinition.getFocList(0, desc);
				
				list.loadIfNotLoadedFromDB();
				list.setOrderByKeyFields();
				FPanel panel = null;
				if(tableDefinition.isSingleInstance()){
					FocObject focObjectToDisplay = null;
					if(list.size() > 1){
						Globals.getDisplayManager().popupMessage("Waring mulitple row found in DB for a static instance");
						focObjectToDisplay = list.getFocObject(0);
					}else if(list.size() == 1){
						focObjectToDisplay = list.getFocObject(0);
					}else if(list.size() == 0){
						focObjectToDisplay = list.newEmptyItem();
						focObjectToDisplay.forceControler(true);
						focObjectToDisplay.validate(false);
					}
					if(focObjectToDisplay != null){
						//panel = new UserDefinedObjectGuiDetailsPanel(focObjectToDisplay, MenuDefinition.this.getDetailsViewDefinition().getViewId());
						panel = new UserDefinedObjectGuiDetailsPanel(focObjectToDisplay, detailedViewRef);
					}
				}else{
					//panel = new UserDefinedObjectGuiBrowsePanel(list, MenuDefinition.this.getBrowseViewDefinition().getViewId());
					panel = new UserDefinedObjectGuiBrowsePanel(list, browseViewRef);
				}
				if(panel != null){
					Globals.getDisplayManager().newInternalFrame(panel);
				}else{
					Globals.getDisplayManager().popupMessage("No panel to display, Selecte a view definition for this menu.");
				}
			}
		}
	}
}

	
	

