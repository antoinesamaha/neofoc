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

import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class FabMultipleChoiceGuiBrowsePanel extends FListPanel {
	
	public FabMultipleChoiceGuiBrowsePanel(FocList list, int viewID){
		super("Dictionary groups", FPanel.FILL_VERTICAL);
		FocDesc desc = FabMultipleChoiceDesc.getInstance();

    if(desc != null && list != null) {
    	list.loadIfNotLoadedFromDB();
    	try{
    		setFocList(list);
    	}catch(Exception e){
    		Globals.logException(e);
    	}
      FTableView tableView = getTableView();       
      tableView.addColumn(desc, FabMultipleChoiceDesc.FLD_INT_VALUE, true);
      tableView.addColumn(desc, FabMultipleChoiceDesc.FLD_DISPLAY_TEXT, true);
      construct();
      
      requestFocusOnCurrentItem();
      showEditButton(false);
      showDuplicateButton(false);
      showAddButton(true);
      showRemoveButton(true);
    }
	}
}
