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
package com.fab.model.filter;

import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.gui.FGCurrentItemPanel;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class FilterDefinitionGuiBrowsePanel extends FListPanel {

	public FilterDefinitionGuiBrowsePanel(FocList focList, int viewID){
		super("Filter defintion", FPanel.FILL_BOTH);
		FocDesc desc = FilterDefinitionDesc.getInstance();

    if (desc != null) {
      if(focList == null){
      	focList = FilterDefinitionDesc.getList(FocList.FORCE_RELOAD);
      }
      if (focList != null) {
      	try{
      		setFocList(focList);
      	}catch(Exception e){
      		Globals.logException(e);
      	}
        FTableView tableView = getTableView();
        tableView.setDetailPanelViewIDForNewItem(FilterDefinition.VIEW_ID_FOR_NEW_ITEM);
 
        tableView.addColumn(desc, FilterDefinitionDesc.FLD_TITLE, 20, true);
        tableView.addColumn(desc, FilterDefinitionDesc.FLD_BASE_FOC_DESC, 20, true);
        construct();
        
        FGCurrentItemPanel currPanel = new FGCurrentItemPanel(this, viewID); 
        add(currPanel, 2, 1);
        
        requestFocusOnCurrentItem();
        showEditButton(false);
        showDuplicateButton(false);
        
        FValidationPanel validPanel = showValidationPanel(true);
        if(validPanel != null){
        	validPanel.addSubject(focList);
        }
      }
    }
	}
}
