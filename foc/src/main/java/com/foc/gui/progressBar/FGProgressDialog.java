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
package com.foc.gui.progressBar;

import java.awt.Dimension;
import java.awt.GridBagConstraints;

import javax.swing.JProgressBar;

import com.foc.Globals;
import com.foc.event.FValidationListener;
import com.foc.gui.FGLabel;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;

public class FGProgressDialog {
	
	private FGLabel          label               = null;
	private JProgressBar     progressBar         = null;
	private FValidationPanel validationPanel     = null;
	private int              maxCount            = 0;
	private IProgressClass   process             = null;
	private boolean          interruptionAllowed = true;
	
	public FGProgressDialog(IProgressClass process, int maxCount){
		this.maxCount = maxCount;
		this.process  = process;
	}
	
	public void dispose(){
		progressBar     = null;
		process         = null;
		validationPanel = null;
		label           = null;
	}
	
	public void finish(){
		if(process != null && label != null){
			validationPanel.setValidationType(FValidationPanel.VALIDATION_OK);
			label.setText(process.getSuccessMessage());
		}
	}
	
	private JProgressBar getProgressBar(){
		if(progressBar == null){
			progressBar = new JProgressBar(0, maxCount);
		}
		return progressBar;
	}
	
	private FGLabel getLabel(){
		label = new FGLabel(process.getRuntimeMessage());
		return label;
	}
	
	public void setValue(int value){
		getProgressBar().setValue(value);
	}
	
	public void popupDialog(){
		Thread thread = new Thread(new Runnable(){
			public void run() {
				popupDialog_NoThread();
			}
		});
		thread.start();
		
		try{
			Thread.sleep(10000);
		}catch (InterruptedException e){
			Globals.logException(e);
		}
	}

	public void popupDialog_NoThread(){
		FPanel panel = new FPanel();
		
		panel.add(getProgressBar(), 0, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
		panel.add(getLabel(), 0, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
		
		validationPanel = panel.showValidationPanel(true);
		
		if(isInterruptionAllowed()){
			validationPanel.setValidationType(FValidationPanel.VALIDATION_OK_CANCEL);
		}else{
			validationPanel.setValidationType(FValidationPanel.VALIDATION_OK);
		}
		validationPanel.setValidationListener(new FValidationListener() {
			public void postCancelation(FValidationPanel panel) {
			}
			public void postValidation(FValidationPanel panel) {
			}
			public boolean proceedCancelation(FValidationPanel panel) {
				if(process != null && isInterruptionAllowed()){
					process.setRequestInterruption();
					label.setText(process.getInterruptedMessage());
					while(!process.isSuccessful() && !process.isInterrupted()) {
						try{
							Thread.sleep(500);
						}catch(Exception e){
							Globals.logException(e);
						}
					}
				}
				if(validationPanel != null) validationPanel.setValidationType(FValidationPanel.VALIDATION_OK);
				return false;
			}
			public boolean proceedValidation(FValidationPanel panel) {
				return process != null ? process.isSuccessful() : true;
			}
		});
		panel.setPreferredSize(new Dimension(400, 90));
		Globals.getDisplayManager().popupDialog(panel, "Progress status", true, 300, 300);
	}

	public boolean isInterruptionAllowed() {
		return interruptionAllowed;
	}

	public void setInterruptionAllowed(boolean interruptionAllowed) {
		this.interruptionAllowed = interruptionAllowed;
	}
}
