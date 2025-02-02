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
package com.foc.business.workflow;

import com.foc.business.workflow.map.WFMapDesc;
import com.foc.business.workflow.map.WFStageDesc;
import com.foc.business.workflow.map.WFTransactionConfigDesc;
import com.foc.business.workflow.rights.RightLevelDesc;
import com.foc.business.workflow.signing.WFTransactionWrapperGuiBrowsePanel;
import com.foc.desc.FocObject;
import com.foc.gui.FPanel;
import com.foc.menu.FAbstractMenuAction;
import com.foc.menu.FMenuAction;
import com.foc.menu.FMenuItem;
import com.foc.menu.FMenuList;
import com.foc.menu.FMenuSeparator;

public class WorkflowMenu {

  public static FMenuItem addRightsLevelsMenuItem(FMenuList list){
    FMenuItem menuItem = new FMenuItem("Rights Levels", 'R', new FMenuAction(RightLevelDesc.getInstance(), true));
    list.addMenu(menuItem);
    menuItem.setCode("WF_RIGHTS_LEVELS");
    return menuItem;
  }

  public static FMenuItem addSiteMenuItem(FMenuList list){
    FMenuItem menuItem = new FMenuItem("Site", 'S', new FMenuAction(WFSiteDesc.getInstance(), true));
    list.addMenu(menuItem);
    menuItem.setCode("WF_AREA");
    return menuItem;
  }

  public static FMenuItem addTitleMenuItem(FMenuList list){
    FMenuItem menuItem = new FMenuItem("Title", 'T', new FMenuAction(WFTitleDesc.getInstance(), true));
    list.addMenu(menuItem);
    menuItem.setCode("WF_TITLES");
    return menuItem;
  }

  @SuppressWarnings("serial")
	public static FMenuItem addOperatorMenuItem(FMenuList list){
    FMenuItem menuItem = new FMenuItem("All operators view", 'O', new FAbstractMenuAction(WFOperatorDesc.getInstance(), true){
			@Override
			public FPanel generatePanel() {
				WFOperatorGuiBrowsePanel browse = new WFOperatorGuiBrowsePanel(null, FocObject.DEFAULT_VIEW_ID);
				return browse;
			}
    });
    list.addMenu(menuItem);
    menuItem.setCode("WF_ALL_OPERATORS");
    return menuItem;
  }

  public static FMenuItem addStageMenuItem(FMenuList list){
    FMenuItem menuItem = new FMenuItem("Stage", 'S', new FMenuAction(WFStageDesc.getInstance(), true));
    list.addMenu(menuItem);
    menuItem.setCode("WF_STAGES");
    return menuItem;
  }

  public static FMenuItem addMapMenuItem(FMenuList list){
    FMenuItem menuItem = new FMenuItem("Signature Sequence Map", 'M', new FMenuAction(WFMapDesc.getInstance(), true));
    list.addMenu(menuItem);
    menuItem.setCode("WF_SIGNATURE_MAP");
    return menuItem;
  }
  
  public static FMenuItem addWFAssignementMenuItem(FMenuList list){
    FMenuItem menuItem = new FMenuItem("Transaction configuration", 'T', new FMenuAction(WFTransactionConfigDesc.getInstance(), true));
    list.addMenu(menuItem);
    menuItem.setCode("WF_ASSIGN_TRANS_2_MAP");
    return menuItem;
  }
  
  @SuppressWarnings("serial")
	public static FMenuItem addSignaturesToDoMenuItem(FMenuList list){
    FMenuItem menuItem = new FMenuItem("My pending signatures", 'y', new FAbstractMenuAction(WFTransactionConfigDesc.getInstance(), true){
			@Override
			public FPanel generatePanel() {
				WFTransactionWrapperGuiBrowsePanel guiBrowsePanel = new WFTransactionWrapperGuiBrowsePanel(null, FocObject.DEFAULT_VIEW_ID);
				return guiBrowsePanel;
			}
    });
    list.addMenu(menuItem);
    menuItem.setCode("WF_MY_PENDING_SIGNATURE");
    return menuItem;
  }  
  
  public static void addGeneralMenus(FMenuList list){
  	addRightsLevelsMenuItem(list);
  	addSiteMenuItem(list);
  	addTitleMenuItem(list);
  	addOperatorMenuItem(list);
  	addStageMenuItem(list);
  	list.addMenu(new FMenuSeparator());
  	addMapMenuItem(list);
  	addWFAssignementMenuItem(list);
  	list.addMenu(new FMenuSeparator());
  	addSignaturesToDoMenuItem(list);
  }
}
