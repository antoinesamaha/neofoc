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

import java.awt.GridBagConstraints;

import com.foc.Globals;
import com.foc.business.workflow.rights.UserTransactionRight;
import com.foc.business.workflow.rights.UserTransactionRightGuiBrowsePanel;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.event.FValidationListener;
import com.foc.gui.FGTabbedPane;
import com.foc.gui.FGTextField;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class WFSiteGuiDetailsPanel extends FPanel{

	public static final int VIEW_STANDARD  = FocObject.DEFAULT_VIEW_ID;
	public static final int VIEW_SELECTION = 2;
	
	private WFSite area = null; 
		
	public WFSiteGuiDetailsPanel(FocObject focObj, int view){
		super("Site", FILL_BOTH);
		area = (WFSite) focObj;
		
		if(view == VIEW_SELECTION){
			FGTextField tf = (FGTextField) add(area, WFSiteDesc.FLD_NAME, 0, 0);
			tf.setEditable(false);
			
			tf = (FGTextField) add(area, WFSiteDesc.FLD_DESCRIPTION, 0, 1);
			tf.setEditable(false);
		}else{
	    if(area.isCreated() && area.getCompany() != null){
	    	area.setAddress(area.getCompany().getLeagalAddress());
	    }

			setMainPanelSising(FILL_BOTH);
			
			FPanel headerPanel = new FPanel();
			headerPanel.add(area, WFSiteDesc.FLD_NAME, 0, 0);
			FGTextField txtFld = (FGTextField) headerPanel.add(area, WFSiteDesc.FLD_DESCRIPTION, 0, 1);
			txtFld.setColumns(50);
			
			headerPanel.add(area, WFSiteDesc.FLD_TRANSACTION_PREFIX, 0, 2);
			headerPanel.add(area, FField.FLD_COMPANY, 0, 3);
			headerPanel.add(area, WFSiteDesc.FLD_ADDRESS, 2, 0, 1, 6);
			add(headerPanel, 0, 0, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH);

			FPanel tabbContainer = new FPanel();
			add(tabbContainer, 0, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH);

			FGTabbedPane tabb = new FGTabbedPane();
			tabbContainer.add(tabb);
	
			{
				FocList list = area.getOperatorList();
				WFOperatorGuiBrowsePanel browse = new WFOperatorGuiBrowsePanel(list, FocObject.DEFAULT_VIEW_ID);
				tabb.add("User Titles", browse);
			}
			
			{
				FocList list = area.getUserTransactionRightsList();
				FPanel userTransactionRightsPanel = new UserTransactionRightGuiBrowsePanel(list, FocObject.DEFAULT_VIEW_ID);
				tabb.add("User Transaction Rights", userTransactionRightsPanel);
				//add(bottom, 0, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH);
			}
	
			FPanel userDetailsPanel = newUserDetailsPanel();
			if(userDetailsPanel != null){
				String title = "Details";
				if(userDetailsPanel.getFrameTitle() != null && !userDetailsPanel.getFrameTitle().isEmpty()){
					title = userDetailsPanel.getFrameTitle();
				}
				tabb.add(title, userDetailsPanel);
			}
			
			/*
			list = area.getSuperUserList();
			WFSuperUserGuiBrowsePanel superUserBrowse = new WFSuperUserGuiBrowsePanel(list, FocObject.DEFAULT_VIEW_ID);
			superUserBrowse.setBorder("Exception rights for Titles or Users");
			bottom.add(superUserBrowse, 1, 1);
			*/
			
			FValidationPanel validPanel = showValidationPanel(true);
			validPanel.addSubject(area);
			
			validPanel.setValidationListener(new FValidationListener() {
				
				@Override
				public boolean proceedValidation(FValidationPanel panel) {
					boolean proceed = true;
					WFSite area = getArea();
					if(area != null){
						FocList superUserList = area.getUserTransactionRightsList();
						for(int i=0; i<superUserList.size() && proceed; i++){
							UserTransactionRight superUser = (UserTransactionRight) superUserList.getFocObject(i);
							if(superUser != null){
								if(superUser.getUser() == null && superUser.getTitle() == null){
									proceed = false;
									Globals.getDisplayManager().popupMessage("Some Exception rights don't have any User nor Title");
								}
							}
						}
					}
					if(proceed){
						proceed = userProceedValidation();
					}
					return proceed;
				}
				
				@Override
				public boolean proceedCancelation(FValidationPanel panel) {
					return true;
				}
				
				@Override
				public void postValidation(FValidationPanel panel) {
				}
				
				@Override
				public void postCancelation(FValidationPanel panel) {
				}
			});
		}
	}
	
	public void dispose(){
		super.dispose();
		area = null;
	}
	
	public WFSite getArea(){
		return area;
	}
	
	public FPanel newUserDetailsPanel(){
		return null;
	}
	
	public boolean userProceedValidation(){
		return true;
	}
}
