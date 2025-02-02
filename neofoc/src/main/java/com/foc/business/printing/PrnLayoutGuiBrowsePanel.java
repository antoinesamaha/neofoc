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
package com.foc.business.printing;

import com.foc.desc.FocDesc;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class PrnLayoutGuiBrowsePanel extends FListPanel {

	boolean listofAll = false;
	public static final int VIEW_PRINTING = 1;
	
	public PrnLayoutGuiBrowsePanel(FocList focList, int viewID){
		super("Layout list", FPanel.FILL_NONE);
		FocDesc focDesc = PrnLayoutDesc.getInstance();
		if(focDesc != null){
			if(focList == null){
				focList   = PrnLayoutDesc.getList(FocList.FORCE_RELOAD);
				listofAll = true;
			}
			if(focList != null){
				setFocList(focList);
				FTableView tableView = getTableView();

				if(listofAll){
					tableView.addColumn(focDesc, PrnLayoutDesc.FLD_CONTEXT, false);
				}
				tableView.addColumn(focDesc, PrnLayoutDesc.FLD_FILE_NAME, false);				
				tableView.addColumn(focDesc, PrnLayoutDesc.FLD_DESCRIPTION, false);
				
				construct();
				tableView.setColumnResizingMode(FTableView.COLUMN_WIDTH_FACTOR_MODE);

				if(listofAll || viewID == VIEW_PRINTING){
					FValidationPanel vPanel = showValidationPanel(true);
					vPanel.addSubject(focList);
				}
				
				showAddButton(false);
				showRemoveButton(false);
				showEditButton(false);
			}
		}
	}
}
