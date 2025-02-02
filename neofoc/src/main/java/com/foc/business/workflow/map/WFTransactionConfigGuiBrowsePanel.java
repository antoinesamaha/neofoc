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
package com.foc.business.workflow.map;

import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class WFTransactionConfigGuiBrowsePanel extends FListPanel {
	
	public WFTransactionConfigGuiBrowsePanel(FocList focList, int viewID){
		super("Assignement", FPanel.FILL_BOTH);
		FocDesc focDesc = WFTransactionConfigDesc.getInstance();
		if(focDesc != null){
			if(focList == null){
				focList = WFTransactionConfigDesc.getList(FocList.LOAD_IF_NEEDED);
			}
			if(focList != null){
				WFTransactionConfigDesc.completeList(focList, Globals.getApp().getCurrentCompany());
				setFocList(focList);
				FTableView tableView = getTableView();

				tableView.addColumn(focDesc, WFTransactionConfigDesc.FLD_TRANSACTION                            , false);
				tableView.addColumn(focDesc, WFTransactionConfigDesc.FLD_TRANSACTION_TITLE_PROPOSAL             , true );
				tableView.addColumn(focDesc, WFTransactionConfigDesc.FLD_TRANSACTION_TITLE                      , true );
				tableView.addColumn(focDesc, WFTransactionConfigDesc.FLD_MAP                                    , true );
				tableView.addColumn(focDesc, WFTransactionConfigDesc.FLD_DEFAULT_AREA                           , true );
				tableView.addColumn(focDesc, WFTransactionConfigDesc.FLD_APPROVAL_METHOD                        , true );
				tableView.addColumn(focDesc, WFTransactionConfigDesc.FLD_PROMPT_FOR_APPROVE_UPON_VALIDATION     , true );
				tableView.addColumn(focDesc, WFTransactionConfigDesc.FLD_CODE_PREFIX_USE_SITE_PREFIX            , true );
				tableView.addColumn(focDesc, WFTransactionConfigDesc.FLD_CODE_PREFIX_TRANSACTION_PREFIX         , true );
				tableView.addColumn(focDesc, WFTransactionConfigDesc.FLD_CODE_PREFIX_TRANSACTION_PREFIX_PROPOSAL, true );
				tableView.addColumn(focDesc, WFTransactionConfigDesc.FLD_CODE_PREFIX_NUMBER_OF_DIGITS           , true );
				construct();
				tableView.setColumnResizingMode(FTableView.COLUMN_AUTO_RESIZE_MODE);
				
				FValidationPanel validPanel = showValidationPanel(true);
				if(validPanel != null){
					validPanel.addSubject(focList);
				}
				
				showAddButton(false);
				showRemoveButton(false);
				showEditButton(false);
			}
		}
	}
}
