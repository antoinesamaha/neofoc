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
import com.foc.event.FValidationListener;
import com.foc.gui.FGButton;
import com.foc.gui.FGOptionPane;
import com.foc.gui.FGReplaceablePanel;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.performance.PerfManager;

@SuppressWarnings("serial")
public class WFTransactionSlideShowPanel extends FPanel{
	
	private WFTransactionWrapperList transactionList  = null;
	private int                      row              = -1;
	private FGReplaceablePanel       replaceablePanel = null;
	private WFTransactionWrapper     currentWrapper   = null;
	
	public WFTransactionSlideShowPanel(WFTransactionWrapperList transactionList, int startRow){
		setMainPanelSising(FPanel.MAIN_PANEL_FILL_BOTH);
		setWithScroll(false);
		this.transactionList = transactionList;
		this.row = startRow;
		if(row < 0){
			row = 0;
		}
		
		currentWrapper = transactionList.getVisibleObjectAt(row);
		FPanel panel = currentWrapper.newDetailsPanel(true);
		replaceablePanel = new FGReplaceablePanel(panel);
		add(replaceablePanel, 0, 0);
				
		FGButton exitSlidesButton = new FGButton("Exit Slide Show");
		exitSlidesButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Globals.getDisplayManager().goBack();
			}
		});
		
		FGButton rejectAllSignaturesButton = new FGButton("Cancel All Previous Signatures");
		rejectAllSignaturesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!FGOptionPane.popupOptionPane_YesNo("Alert!", "Are you sure you want to cancel all previous signatures!\nThis will take the transaction back to the beginning of the workflow.")){
					getCurrentWrapper().undoAllSignatures();
				}
			}
		});
			
		FValidationPanel vPanel = showValidationPanel(true);
		vPanel.addButton(rejectAllSignaturesButton);
		vPanel.setValidationButtonLabel("Sign");
		vPanel.setCancelationButtonLabel("Skip For The Moment");
		vPanel.addButton(exitSlidesButton);
		
		vPanel.setValidationListener(new FValidationListener(){
			@Override
			public void postCancelation(FValidationPanel panel) {
			}

			@Override
			public void postValidation(FValidationPanel panel) {
			}

			@Override
			public boolean proceedCancelation(FValidationPanel panel) {
				return gotoNext() == null;
			}

			@Override
			public boolean proceedValidation(FValidationPanel panel) {
				getCurrentWrapper().sign();
				return gotoNext() == null;
			}
		});
	}
	
	@Override
	public void dispose(){
		super.dispose();
		transactionList  = null;
		replaceablePanel = null;
		currentWrapper   = null;
	}
	
//	private ArrayList<Integer> getVisibleArray(){
//		return transactionList.getFocListFilter().getVisibleArray();
//	}
	
//	private WFTransactionWrapper getVisibleObjectAt(int tableRow){
//		WFTransactionWrapper wrapper = null;
//		if(getVisibleArray() != null && transactionList != null && tableRow < getVisibleArray().size() && tableRow >= 0){
//			int listRow = getVisibleArray().get(tableRow);
//			wrapper = (WFTransactionWrapper) transactionList.getFocObject(listRow);
//		}
//		return wrapper;
//	}
	
	public WFTransactionWrapper gotoNext(){
		row++;
		currentWrapper = transactionList.getVisibleObjectAt(row);
		if(currentWrapper != null){
			PerfManager.start("MySignature_"+row);
			FPanel detailsPanel = currentWrapper.newDetailsPanel(true);
			replaceablePanel.replacePanel(detailsPanel);
			PerfManager.end();
		}
		return currentWrapper;
	}
	
	private WFTransactionWrapper getCurrentWrapper(){
		return currentWrapper;
	}
}
