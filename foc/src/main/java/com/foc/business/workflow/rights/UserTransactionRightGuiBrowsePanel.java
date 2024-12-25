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
package com.foc.business.workflow.rights;

import com.foc.desc.FocDesc;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class UserTransactionRightGuiBrowsePanel extends FListPanel {
	
	//private FocListListener listListener = null;
	
	public UserTransactionRightGuiBrowsePanel(FocList focList, int viewID){
		super("User transaction rights", FPanel.FILL_BOTH);
		FocDesc focDesc = UserTransactionRightDesc.getInstance();
		if(focDesc != null && focList != null){
			setFocList(focList);
			FTableView tableView = getTableView();

			tableView.addColumn(focDesc, UserTransactionRightDesc.FLD_TRANSACTION  , true);
			tableView.addColumn(focDesc, UserTransactionRightDesc.FLD_TITLE        , true);
			tableView.addColumn(focDesc, UserTransactionRightDesc.FLD_USER         , true);
			tableView.addColumn(focDesc, UserTransactionRightDesc.FLD_RIGHTS_LEVEL , true);
			//tableView.addColumn(focDesc, WFSuperUserDesc.FLD_AREA, true);
			
			construct();
			tableView.setColumnResizingMode(FTableView.COLUMN_AUTO_RESIZE_MODE);
						
			showAddButton(true);
			showRemoveButton(true);
			showEditButton(false);
			
			/*
			listListener = new FocListListener(focList);
			listListener.addProperty(FFieldPath.newFieldPath(WFSuperUserDesc.FLD_TITLE));
			listListener.addProperty(FFieldPath.newFieldPath(WFSuperUserDesc.FLD_USER));
			listListener.addListener(new FocListener() {
				@Override
				public void focActionPerformed(FocEvent evt) {
					evt.getProp
					FocList superUser title getFocList();
				}
				
				@Override
				public void dispose() {
				}
			});
			listListener.startListening();
			*/
		}
	}
	
	@Override
	public void dispose(){
		super.dispose();
		/*
		if(listListener != null){
			listListener.dispose();
			listListener = null;
		}
		*/
	}
}
