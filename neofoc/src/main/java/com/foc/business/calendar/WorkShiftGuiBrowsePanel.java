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
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class WorkShiftGuiBrowsePanel extends FListPanel {
	
	public WorkShiftGuiBrowsePanel(FocList list, int viewID){
		super("Work Shifts", FPanel.FILL_VERTICAL);
		FocDesc desc = WorkShiftDesc.getInstance();

    if (desc != null && list != null) {
    	list.loadIfNotLoadedFromDB();

    	setFocList(list);
      FTableView tableView = getTableView();   
      
      tableView.addColumn(desc, WorkShiftDesc.FLD_START_TIME, true);
      tableView.addColumn(desc, WorkShiftDesc.FLD_END_TIME, true);
      
      construct();
      
      tableView.setColumnResizingMode(FTableView.COLUMN_AUTO_RESIZE_MODE);
      
      showEditButton(false);
      showDuplicateButton(false);
      requestFocusOnCurrentItem();      
    }
	}  
}
