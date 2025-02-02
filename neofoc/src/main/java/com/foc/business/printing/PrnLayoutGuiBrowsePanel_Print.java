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
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class PrnLayoutGuiBrowsePanel_Print extends FListPanel {

	private IPrnReportCreator reportCreator = null;
	private PrnReportLauncher repLauncher   = null;
	
	public PrnLayoutGuiBrowsePanel_Print(PrnContext context, IPrnReportCreator reportCreator){
		super("Layout list", FPanel.FILL_NONE);

		this.reportCreator = reportCreator;
		
		FocList focList = context.getLayoutList();
		FocDesc focDesc = PrnLayoutDesc.getInstance();
		if(focDesc != null && focList != null){
			setFocList(focList);
			FTableView tableView = getTableView();

			tableView.addColumn(focDesc, PrnLayoutDesc.FLD_NAME, false);
			tableView.addColumn(focDesc, PrnLayoutDesc.FLD_DESCRIPTION, false);
			
			construct();
			tableView.setColumnResizingMode(FTableView.COLUMN_WIDTH_FACTOR_MODE);

			reportCreator.getLauncher().setLayoutBrowsePanel(this);
			getTotalsPanel().add(reportCreator.getLauncher().newPanel());
		
			showModificationButtons(false);

			if(getFocList().size() > 0){
				//requestFocusInWindow();
				setSelectedObject(getFocList().getFocObject(0));
			}
		}
	}
	
	public void dispose(){
		super.dispose();
		if(reportCreator != null){
			reportCreator.disposeLauncherContent();
			reportCreator = null;
		}
		if(repLauncher != null){
			repLauncher.dispose();
			repLauncher = null;
		}
	}
}
