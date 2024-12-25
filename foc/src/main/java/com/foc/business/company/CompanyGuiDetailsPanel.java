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
package com.foc.business.company;

import java.awt.GridBagConstraints;

import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.event.FValidationListener;
import com.foc.gui.FGTabbedPane;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class CompanyGuiDetailsPanel extends FPanel {

	private UserCompanyRightsGuiBrowsePanel userCompanyRightsBrowse = null;
	
	public CompanyGuiDetailsPanel(FocObject obj, int viewID){
		Company comp = (Company) obj;
		setFrameTitle("Company");
		
		add(obj, FField.FLD_NAME, 0, 0);
		add(obj, CompanyDesc.FLD_DESCRIPTION, 0, 1);
		add(obj, CompanyDesc.FLD_VAT_RULE, 0, 2);
		add(obj, CompanyDesc.FLD_ADR_BOOK_PARTY, 0, 3, 2, 1);

		FGTabbedPane tabPane = new FGTabbedPane();
		add(tabPane, 0, 4, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

		//USERRIGHTS
//		FocList list = comp.getUserRightsList();
		FocList list = null;
		userCompanyRightsBrowse = new UserCompanyRightsGuiBrowsePanel(list, FocObject.DEFAULT_VIEW_ID);
		tabPane.addTab("User rights", userCompanyRightsBrowse);
		
		FPanel logoPanel = new FPanel();
		logoPanel.add(comp, CompanyDesc.FLD_LOGO_IMAGE, 0, 0);
		tabPane.addTab("Logo", logoPanel);
		
		FValidationPanel vPanel = showValidationPanel(true);
		vPanel.addSubject(obj);
		vPanel.addSubject(list);
		FValidationListener validListener = new FValidationListener(){
			@Override
			public void postCancelation(FValidationPanel panel) {
			}

			@Override
			public void postValidation(FValidationPanel panel) {
			}
			
			@Override
			public boolean proceedCancelation(FValidationPanel panel) {
				return true;
			}

			@Override
			public boolean proceedValidation(FValidationPanel panel) {
				if(userCompanyRightsBrowse != null){
					userCompanyRightsBrowse.updateRealList();
				}
				return true;
			}
		};
		vPanel.setValidationListener(validListener);
	}

	@Override
	public void dispose() {
		super.dispose();
		userCompanyRightsBrowse = null;
	}
}
