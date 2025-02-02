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
package com.foc.gui.table.view;

import com.foc.gui.FListPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class ViewConfigGuiBrowsePanel extends FListPanel{
	
	public final static int VIEW_NO_EDIT = 1;
	
	public ViewConfigGuiBrowsePanel(FocList list, int viewID){
		super("Table Views", FILL_BOTH);

    //setWithScroll(false);
		setFocList(list);
		
		FTableView tableView = getTableView();
		tableView.addColumn(list.getFocDesc(), ViewConfigDesc.FLD_CODE, false);
		tableView.setDetailPanelViewIDForNewItem(ViewConfigGuiDetailsPanel.VIEW_CREATION);
		construct();
		
		tableView.setColumnResizingMode(FTableView.COLUMN_AUTO_RESIZE_MODE);

		showModificationButtons(viewID != VIEW_NO_EDIT);
		showEditButton(false);
		showDuplicateButton(true);
	}
}
