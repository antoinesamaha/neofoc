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
package com.foc.list.filter;

import com.fab.model.filter.UserDefinedFilter;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FField;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class FocListFilterGuiBrowsePanel extends FListPanel {
		
	private FocListFilter                   filter                = null;
	private FocListFilterValidationListener filterValidationPanel = null;
	
	public FocListFilterGuiBrowsePanel(FocList focList, int viewID){
		this(null, focList, viewID);
	}
	
	public FocListFilterGuiBrowsePanel(FocListFilter filter, FocList focList, int viewID){
		super("Filters", FPanel.FILL_BOTH);
		if(focList != null){
			this.filter           = filter;
			FocDesc filterFocDesc = focList.getFocDesc();
			if(filterFocDesc != null){
				setFocList(focList);
				FTableView tableView = getTableView();
				tableView.setDetailPanelViewID(filter == null ? viewID : UserDefinedFilter.VIEW_FOR_FILTER_CREATION);
				tableView.addColumn(filterFocDesc, FField.FLD_NAME, false);
				
				construct();
				
				if(filter != null){
					FValidationPanel validPanel = showValidationPanel(true);
					if(validPanel != null){
						filterValidationPanel = new FocListFilterValidationListener(this, getFilter(), validPanel);
					}
				}
				setFrameTitle("User defined filter");
				setMainPanelSising(FPanel.FILL_BOTH);
				showAddButton(true);
				showRemoveButton(true);
				showEditButton(true);
			}
		}
	}
	
	public void dispose(){
		super.dispose();
		filter = null;
		if(filterValidationPanel != null){
			filterValidationPanel.dispose();
			filterValidationPanel = null;
		}
	}

	public FocListFilter getFilter() {
		return filter;
	}
}
