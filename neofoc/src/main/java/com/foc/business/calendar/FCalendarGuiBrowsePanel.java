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
package com.foc.business.calendar;

import com.foc.desc.FocDesc;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.FTableColumn;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class FCalendarGuiBrowsePanel extends FPanel {
	
	public FCalendarGuiBrowsePanel(FocList list, int viewID){
		super("Calendar", FPanel.FILL_NONE);
		FocDesc desc = FCalendarDesc.getInstance();

    FListPanel selectionPanel = null;
    if (desc != null) {
    	if(list == null){
    		list = FCalendarDesc.getList(FocList.LOAD_IF_NEEDED);
    	}else{
    		list.loadIfNotLoadedFromDB();
    	}
      selectionPanel = new FListPanel(list);
      FTableView tableView = selectionPanel.getTableView();   
      
      selectionPanel.setDirectlyEditable(false);
      
      tableView.addColumn(desc, FCalendarDesc.FLD_NAME, false);
      FTableColumn col = tableView.addColumn(desc, FCalendarDesc.FLD_IS_DEFAULT, true);
      col.setSize(15);
      
      selectionPanel.construct();
      
      selectionPanel.showEditButton(true);
      selectionPanel.showDuplicateButton(false);
      selectionPanel.requestFocusOnCurrentItem();      
    }

    add(selectionPanel, 0, 0);
       
  	FValidationPanel savePanel = showValidationPanel(true);
    if (savePanel != null) {
      savePanel.addSubject(list);	
    }
	}  
}
