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

import com.foc.desc.FocDesc;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class WFOperatorGuiBrowsePanel extends FListPanel {

	boolean listofAll = false;
	
	public WFOperatorGuiBrowsePanel(FocList focList, int viewID){
		super("Operator List", FPanel.FILL_BOTH);
		FocDesc focDesc = WFOperatorDesc.getInstance();
		if(focDesc != null){
			if(focList == null){
				focList   = WFOperatorDesc.getList(FocList.FORCE_RELOAD);
				listofAll = true;
			}
			if(focList != null){
				setFocList(focList);
				FTableView tableView = getTableView();

				if(listofAll){
					tableView.addColumn(focDesc, WFOperatorDesc.FLD_AREA, false);
				}
				tableView.addColumn(focDesc, WFOperatorDesc.FLD_USER, !listofAll);				
				tableView.addColumn(focDesc, WFOperatorDesc.FLD_TITLE, !listofAll);
				
				construct();
				tableView.setColumnResizingMode(FTableView.COLUMN_WIDTH_FACTOR_MODE);

				if(listofAll){
					FValidationPanel vPanel = showValidationPanel(true);
					vPanel.addSubject(focList);
				}
				
				showAddButton(true);
				showRemoveButton(true);
				showEditButton(false);
			}
		}
	}
}
