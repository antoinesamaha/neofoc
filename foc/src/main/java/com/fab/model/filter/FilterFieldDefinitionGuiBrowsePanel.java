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
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class FilterFieldDefinitionGuiBrowsePanel extends FListPanel {
	
	public FilterFieldDefinitionGuiBrowsePanel(FocList focList, int viewID){
		super("Filter field defintion", FPanel.FILL_BOTH);
		FocDesc desc = FilterFieldDefinitionDesc.getInstance();

    if (desc != null) {
      if (focList != null) {
      	try{
      		setFocList(focList);
      	}catch(Exception e){
      		Globals.logException(e);
      	}
        FTableView tableView = getTableView();       

        tableView.addColumn(desc, FilterFieldDefinitionDesc.FLD_CONDITION_PROPERTY_PATH, false);
        //tableView.addColumn(desc, FilterFieldDefinitionDesc.FLD_CONDITION_PROPERTY_PATH_2222, false);
        construct();
        
        requestFocusOnCurrentItem();
        showEditButton(true);
        showDuplicateButton(false);
      }
    }
	}
}
