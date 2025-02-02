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
package com.foc.gui;

public abstract class FWizardPanel extends FPanel {

	private FWizardFooterPanel footerPanel = null;
  
	public abstract boolean nextAction();
	public abstract boolean previousAction();
  public boolean cancelAction(){ return false; }
	
	public void dispose(){
		super.dispose();
		footerPanel = null;
	}

	public FGButton getNextButton() {
		return footerPanel.getNextButton();
	}

	public FGButton getPreviousButton() {
		return footerPanel.getPreviousButton();
	}
  
  public FGButton getCancelButton() {
    return footerPanel.getCancelButton();
  }
  
	@SuppressWarnings("serial")
	public void addWizardButtons(){
		footerPanel = new FWizardFooterPanel(){
			@Override
			public boolean nextAction() {
				return FWizardPanel.this.nextAction();
			}

			@Override
			public boolean previousAction() {
				return FWizardPanel.this.previousAction();
			}

			public void cancelAction() {
				FWizardPanel.this.cancelAction();
			}
    };
    FGButton previousButton = footerPanel.getPreviousButton();
		if(previousButton != null){
		  previousButton.setEnabled(false);
		}
    setFooterPanel(footerPanel);
	}
}
