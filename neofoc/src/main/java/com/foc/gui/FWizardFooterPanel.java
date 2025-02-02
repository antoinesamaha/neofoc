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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.SwingConstants;

import com.foc.Globals;

public abstract class FWizardFooterPanel extends FPanel implements FIFooterPanel{

	private FGButton nextButton = null;
	private FGButton previousButton = null;
  private FGButton cancelButton = null;
  
	public static final String BUTTON_LABEL_CANCEL = "Cancel";
	public static final String BUTTON_LABEL_NEXT   = "Next";
	public static final String BUTTON_LABEL_BACK   = "Back";
	public static final String BUTTON_LABEL_FINISH = "Finish";

	public abstract boolean nextAction();
	public abstract boolean previousAction();
	
	public FWizardFooterPanel(){
		
		addWizardButtons();
	}
	
  public void dispose(){
		super.dispose();
    nextButton = null;
		previousButton = null;
    cancelButton = null;
	}

	public FGButton getNextButton() {
		return nextButton;
	}

	public FGButton getPreviousButton() {
		return previousButton;
	}
  
  public FGButton getCancelButton() {
    return cancelButton;
  }
  
  public void setPreviousButtonEnabled(boolean enabled){
  	previousButton.setEnabled(enabled);
  }

  public void setNextButtonLabelAsFinish(){
  	nextButton.setText(BUTTON_LABEL_FINISH);
  }

	@SuppressWarnings("serial")
	public void addWizardButtons(){
		nextButton = new FGButton(BUTTON_LABEL_NEXT, Globals.getIcons().getNextIcon());
		nextButton.setHorizontalTextPosition(SwingConstants.LEFT);
    add(nextButton, 1, 0);
    nextButton.addActionListener(new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				nextAction();
			}
    });
    
    previousButton = new FGButton(BUTTON_LABEL_BACK, Globals.getIcons().getBackIcon());
    previousButton.setHorizontalTextPosition(SwingConstants.RIGHT);
    add(previousButton, 0, 0);
    previousButton.addActionListener(new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				previousAction();
			}
    });
    
    cancelButton = new FGButton(BUTTON_LABEL_CANCEL);
    cancelButton.setVisible(false);
    add(cancelButton, 2, 0);
    cancelButton.addActionListener(new AbstractAction(){
      public void actionPerformed(ActionEvent e) {
        cancelAction();
      }
    });
	}
}
