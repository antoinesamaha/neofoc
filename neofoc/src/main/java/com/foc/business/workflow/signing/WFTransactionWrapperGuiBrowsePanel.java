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
package com.foc.business.workflow.signing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.gui.FGButton;
import com.foc.gui.FGListWithFilterPanel;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class WFTransactionWrapperGuiBrowsePanel extends FGListWithFilterPanel {
	
	boolean ownerOfList = false;
	
	public WFTransactionWrapperGuiBrowsePanel(FocList focList, int viewID){
		super("Pending Signatures", FPanel.FILL_BOTH);
		FocDesc focDesc = WFTransactionWrapperDesc.getInstance();
		if(focDesc != null){
			if(focList == null){
				ownerOfList = true;
				WFTransactionWrapperList wrapperFocList = new WFTransactionWrapperList();
				wrapperFocList.fill();
				focList = wrapperFocList;
			}
			setFocList(focList);
			FTableView tableView = getTableView();

			tableView.addColumn(focDesc, WFTransactionWrapperDesc.FLD_TRANSACTION_TYPE, false);
			tableView.addColumn(focDesc, WFTransactionWrapperDesc.FLD_TRANSACTION_CODE, false);
			tableView.addColumn(focDesc, WFTransactionWrapperDesc.FLD_TRANSACTION_DESCRIPTION, false);
			tableView.addColumn(focDesc, WFTransactionWrapperDesc.FLD_TRANSACTION_AREA, false);
			tableView.addColumn(focDesc, WFTransactionWrapperDesc.FLD_TRANSACTION_CURRENT_STAGE, false);
			
			construct();
			tableView.setColumnResizingMode(FTableView.COLUMN_AUTO_RESIZE_MODE);
			
			FValidationPanel validPanel = showValidationPanel(true);
			if(validPanel != null){
				validPanel.setValidationType(FValidationPanel.VALIDATION_OK);
				//validPanel.addSubject(focList);
			}

			FGButton signButton = new FGButton("Sign in slide show...");
			getButtonsPanel().addButton(signButton);
			signButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					startSlideShow();
				}
			});

			showFilterButton(true);
			showAddButton(false);
			showRemoveButton(false);
			showEditButton(false);
		}
	}
	
	public void dispose(){
		FocList listToDispose = ownerOfList ? getFocList() : null;
		super.dispose();
		if(listToDispose != null){
			listToDispose.dispose();
			listToDispose = null;
		}
	}
	
	@Override
  protected boolean doubleClickEvent(int colID, int row){
  	boolean consumed = true;
  	if(row >= 0){
  		startSlideShow();
  	}
  	return consumed;
  }
	
	protected void startSlideShow(){
		WFTransactionWrapperList    transList    = (WFTransactionWrapperList) getFocList();
		WFTransactionSlideShowPanel panel        = new WFTransactionSlideShowPanel(transList, getSelectedRow());
		Globals.getDisplayManager().popupDialog(panel, "Transactions to be signed", true);
		transList.removeAll();
		transList.fill();
	}
}
