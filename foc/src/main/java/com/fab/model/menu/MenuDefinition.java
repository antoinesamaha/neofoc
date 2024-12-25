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
import java.util.ArrayList;
import javax.swing.AbstractAction;

import com.fab.gui.browse.GuiBrowse;
import com.fab.gui.details.GuiDetails;
import com.fab.model.FabMain;
import com.fab.model.table.TableDefinition;
import com.fab.model.table.UserDefinedObjectGuiBrowsePanel;
import com.fab.model.table.UserDefinedObjectGuiDetailsPanel;
import com.foc.Globals;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.gui.FPanel;
import com.foc.list.FocList;
import com.foc.menu.FMenu;
import com.foc.menu.FMenuItem;
import com.foc.menu.FMenuList;
import com.foc.tree.FNode;
import com.foc.tree.TreeScanner;

public class MenuDefinition extends FocObject {
	
	public static final int VIEW_DEFAULT = 1;
	public static final String USER_DEFINED_MENU_LIST_TITLE = "User menus";
	private AbstractAction abstractAction = null;
	
	public MenuDefinition(FocConstructor constr){
		super(constr);
		newFocProperties();
	}
	
	public void dispose(){
		super.dispose();
		this.abstractAction = null;
	}
	
	@Override
  public boolean isPropertyLocked(int fieldId){
		boolean locked = false;
		if(getTableDefinition() != null){
			if(fieldId == MenuDefinitionDesc.FLD_USER_BROWSE_VIEW_DEFINITION){
				locked = getTableDefinition().isSingleInstance();
			}else if(fieldId == MenuDefinitionDesc.FLD_USER_DETAILS_VIEW_DEFINITION){
				locked = !getTableDefinition().isSingleInstance();
			}
		}
  	return locked;
  }
	
	@Override
	public FocList getObjectPropertySelectionList(int fieldID) {
		FocList list = super.getObjectPropertySelectionList(fieldID);
		if(fieldID == MenuDefinitionDesc.FLD_USER_DETAILS_VIEW_DEFINITION){
			list = (getTableDefinition() != null) ? getTableDefinition().getDetailsViewDefinitionList() : null;
		}else if(fieldID == MenuDefinitionDesc.FLD_USER_BROWSE_VIEW_DEFINITION){
			list = (getTableDefinition() != null) ? getTableDefinition().getBrowseViewDefinitionList() : null;
		}
		return list;
	}
	
	public String getName(){
		return getPropertyString(MenuDefinitionDesc.FLD_NAME);
	}
	
	public void setName(String name){
		setPropertyString(MenuDefinitionDesc.FLD_NAME, name);
	}
	
	public MenuDefinition getFatherMenu(){
		return (MenuDefinition) getFatherObject();
	}
	
	public void setFatherMenu(MenuDefinition menuDefinition){
		setFatherObject(menuDefinition);
	}
	
	public void setTableDefinition(TableDefinition tableDefinition){
		setPropertyObject(MenuDefinitionDesc.FLD_TABLE_DEFINITION, tableDefinition);
	}
	
	public TableDefinition getTableDefinition(){
		return (TableDefinition) getPropertyObject(MenuDefinitionDesc.FLD_TABLE_DEFINITION);
	}
	
	public void setUserBrowseViewDefinition(GuiBrowse definition){
		setPropertyObject(MenuDefinitionDesc.FLD_USER_BROWSE_VIEW_DEFINITION, definition);
	}
	
	public GuiBrowse getBrowseViewDefinition(){
		return (GuiBrowse)getPropertyObject(MenuDefinitionDesc.FLD_USER_BROWSE_VIEW_DEFINITION);
	}
	
	public void setDetailsViewDefinition(GuiDetails details){
		setPropertyObject(MenuDefinitionDesc.FLD_USER_DETAILS_VIEW_DEFINITION, details);
	}
	
	public GuiDetails getDetailsViewDefinition(){
		return (GuiDetails) getPropertyObject(MenuDefinitionDesc.FLD_USER_DETAILS_VIEW_DEFINITION);
	}
	
	@SuppressWarnings("serial")
	public AbstractAction getAbstractAction(){
		if(abstractAction == null){
			
			abstractAction = new UserMenuAction(
					getTableDefinition(), 
					getBrowseViewDefinition() != null ? getBrowseViewDefinition().getReference().getInteger() : 0,
					getDetailsViewDefinition() != null ? getDetailsViewDefinition().getReference().getInteger() : 0);
			
			/*
			abstractAction = new AbstractAction(){
				public void actionPerformed(ActionEvent e) {
					TableDefinition tableDefinition = MenuDefinition.this.getTableDefinition();
					if(tableDefinition != null){
						FocList list = FabMain.getFocList(tableDefinition.getName());
						if(list != null){
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
									panel = new UserDefinedObjectGuiDetailsPanel(focObjectToDisplay, MenuDefinition.this.getDetailsViewDefinition().getReference().getInteger());
								}
							}else{
								panel = new UserDefinedObjectGuiBrowsePanel(list, MenuDefinition.this.getBrowseViewDefinition().getReference().getInteger());
							}
							if(panel != null){
								Globals.getDisplayManager().newInternalFrame(panel);
							}else{
								Globals.getDisplayManager().popupMessage("No panel to display, Selecte a view definition for this menu.");
							}
						}
					}
				}
			};
			*/
		}
			
		return abstractAction;
	}
	
	public static void fillUserDefinedMenuList(FMenuList rootMenuList){
		FocList menuDefinitionList = MenuDefinitionDesc.getList(FocList.LOAD_IF_NEEDED);
		if(menuDefinitionList.size() > 0){
			MenuDefinitionTree menuDefinitionTree = new MenuDefinitionTree(menuDefinitionList, MenuDefinition.DEFAULT_VIEW_ID);
			
			menuDefinitionTree.scan(new MenuDefinitionTreeScaner(rootMenuList));
		}
	}
	
	private static class MenuDefinitionTreeScaner implements TreeScanner<FNode>{
		private ArrayList<FMenu> nodesArray = null;
		
		public MenuDefinitionTreeScaner(FMenuList rootMenuList){
			nodesArray = new ArrayList<FMenu>();
			nodesArray.add(rootMenuList);
		}
		
		private void addMenuToArray(FMenu menu){
			nodesArray.add(menu);
		}
		
		private FMenu getCurrentFatherMenu(){
			return nodesArray.get(nodesArray.size() - 1);
		}
		
		public void afterChildren(FNode node){
			if(!node.isRoot()){
				nodesArray.remove(nodesArray.size() - 1);
			}
		}

		public boolean beforChildren(FNode node) {
			if(!node.isRoot()){
				FMenu menu = null;
				if(node.isLeaf()){
					MenuDefinition menuDefinition = (MenuDefinition)node.getObject();
					menu = new FMenuItem(node.getTitle(), ' ', menuDefinition != null ? menuDefinition.getAbstractAction() : null);
				}else{
					menu = new FMenuList(node.getTitle(), ' ');
				}
				menu.setCode("CUST_"+node.getTitle());
				menu.setMnemonic(((FMenuList)getCurrentFatherMenu()).getMnemonicForMenu(menu));
				((FMenuList)getCurrentFatherMenu()).addMenu(menu);
				addMenuToArray(menu);
			}
			return true;
		}
	}
}
