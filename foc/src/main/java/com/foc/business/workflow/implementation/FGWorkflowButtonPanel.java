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
package com.foc.business.workflow.implementation;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

import com.foc.Globals;
import com.foc.admin.FocGroup;
import com.foc.business.status.IStatusHolder;
import com.foc.business.status.StatusHolderGuiDetailsPanel;
import com.foc.desc.FocObject;
import com.foc.gui.FGButton;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;

@SuppressWarnings("serial")
public class FGWorkflowButtonPanel extends FPanel{
	
	private FocObject focObject = null;
	
	public FGWorkflowButtonPanel(FocObject focObject){
		this(focObject, "Signatures");
	}
	
	public FGWorkflowButtonPanel(FocObject focObject, String label){
		super("", FILL_BOTH);
		this.focObject = focObject;
		
		setInsets(0, 0, 0, 0);
		if(!focObject.isCreated()){
			FGButton button = new FGButton(Globals.getIcons().getWorkflowLogIcon());
			add(button, 0, 0, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH);
			
			button.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					Globals.getDisplayManager().changePanel(newSignaturePanel());
				}
			});
		}
	}
	
	public void dispose(){
		super.dispose();
		focObject = null;
	}

	protected FPanel newSignaturePanel(){
		FPanel    globalPanel = new FPanel(); 
		IWorkflow iWorkflow   = (IWorkflow)focObject;
		Workflow  workflow    = iWorkflow.iWorkflow_getWorkflow();

		FocGroup group = Globals.getApp().getGroup();
		
		FPanel header = new FPanel();
		header.addLabel("Area", 0, 0);
		JComponent jComp = (JComponent) header.addField(focObject, workflow.getWorkflowDesc().getFieldID_Site_1(), 1, 0);
		if(jComp != null) jComp.setEnabled(group.allowAreaManualModif());
		globalPanel.add(header, 0, 0, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);

		if(focObject instanceof IStatusHolder){
			StatusHolderGuiDetailsPanel guiDetailsPanel = new StatusHolderGuiDetailsPanel(focObject, FocObject.DEFAULT_VIEW_ID, true);
			globalPanel.add(guiDetailsPanel, 0, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		}

		workflow.getLogList().reloadFromDB();
		WFLogGuiBrowsePanel browse = new WFLogGuiBrowsePanel(workflow.getLogList(), FocObject.DEFAULT_VIEW_ID);
		globalPanel.add(browse, 1, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

		globalPanel.add(focObject, workflow.getWorkflowDesc().getFieldID_AllSignatures(), 0, 1, 2, 1);
		
		FValidationPanel vPanel = globalPanel.showValidationPanel(true);
		vPanel.setValidationType(FValidationPanel.VALIDATION_OK);
		
		FPanel currentPanel = Globals.getDisplayManager().getCurrentPanel();
		globalPanel.setMainPanelSising(currentPanel.getMainPanelSising());
		globalPanel.setFrameTitle(currentPanel.getFrameTitle());
		
		return globalPanel;
	}
}
