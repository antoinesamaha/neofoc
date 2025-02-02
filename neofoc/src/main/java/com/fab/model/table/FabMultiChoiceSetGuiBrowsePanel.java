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

import com.foc.desc.FocDesc;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class FabMultiChoiceSetGuiBrowsePanel extends FListPanel {
	
	public FabMultiChoiceSetGuiBrowsePanel(FocList list, int viewID){
		super("Multiple Choice Set", FPanel.FILL_VERTICAL);

		FocDesc desc = FabMultiChoiceSetDesc.getInstance();

		if(list == null){
			list = FabMultiChoiceSetDesc.getList(FocList.LOAD_IF_NEEDED);
		}
		
    if(desc != null && list != null) {
   		setFocList(list);

   		FTableView tableView = getTableView();       
      tableView.addColumn(desc, FabMultiChoiceSetDesc.FLD_NAME, false);
      construct();
      
      requestFocusOnCurrentItem();
      showEditButton(true);
      showDuplicateButton(false);
      showAddButton(true);
      showRemoveButton(true);
    }
    
    FValidationPanel vPanel = showValidationPanel(true);
    vPanel.addSubject(list);
	}
}
