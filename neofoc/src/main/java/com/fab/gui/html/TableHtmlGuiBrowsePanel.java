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
package com.fab.gui.html;

import com.fab.FocApplicationBuilder;
import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.gui.FGCurrentItemPanel;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class TableHtmlGuiBrowsePanel extends FListPanel {
	
	public TableHtmlGuiBrowsePanel(FocList list, int viewId){
		super("Table forms", FPanel.FILL_BOTH);
		boolean allowEdit = viewId != FocApplicationBuilder.VIEW_NO_EDIT;
		FocDesc desc = TableHtmlDesc.getInstance();

    if (desc != null) {
      if (list != null) {
      	try{
      		setFocList(list);
      	}catch(Exception e){
      		Globals.logException(e);
      	}
        FTableView tableView = getTableView();       
        
        tableView.addLineNumberColumn();
        tableView.addColumn(desc, TableHtmlDesc.FLD_DESCRIPTION, allowEdit);
        tableView.addColumn(desc, TableHtmlDesc.FLD_TITLE      , allowEdit);
        
        construct();
        
        FGCurrentItemPanel currentItemPanel = new FGCurrentItemPanel(this, viewId);
        add(currentItemPanel, 1, 3);
        
        requestFocusOnCurrentItem();
        showEditButton(false);
        showDuplicateButton(false);
        showAddButton(allowEdit);
        showRemoveButton(allowEdit);
      }
    }
	
	}

}
