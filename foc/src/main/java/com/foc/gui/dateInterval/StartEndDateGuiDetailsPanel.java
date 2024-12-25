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
package com.foc.gui.dateInterval;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.foc.business.calendar.FCalendar;
import com.foc.desc.FocObject;
import com.foc.event.FValidationListener;
import com.foc.gui.FGButton;
import com.foc.gui.FGTextField;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;

@SuppressWarnings("serial")
public class StartEndDateGuiDetailsPanel extends FPanel{

  private StartEndDate startEndDate = null;
  private boolean      cancel       = false;
  
  public static final int VIEW_DATE_INTERVAL = FocObject.DEFAULT_VIEW_ID;
  public static final int VIEW_SINGLE_DATE   = 2;
  
  public StartEndDateGuiDetailsPanel(FocObject object, int viewID){
    super();
    startEndDate = (StartEndDate) object;
    
    if(startEndDate.isUseSuggestedDates()){
    	if(FCalendar.isDateZero(startEndDate.getFirstDate())){
    		startEndDate.setFirstDate(startEndDate.getSuggestedFirstDate());
    	}
    	if(FCalendar.isDateZero(startEndDate.getLastDate())){
    		startEndDate.setLastDate(startEndDate.getSuggestedLastDate());
    	}
    }
    
    if(startEndDate.getFirstLabel() != null){
    	addLabel(startEndDate.getFirstLabel(), 0, 0);
    	addField(startEndDate, StartEndDateDesc.FLD_FDATE, 1, 0);
    }else{
    	add(startEndDate, StartEndDateDesc.FLD_FDATE, 0, 0);
    }
    if(startEndDate.isUseSuggestedDates()){
    	FGTextField txtField = new FGTextField();
    	txtField.setText(startEndDate.getSuggestedFirstDate().toString());
    	add("Suggested Start", txtField, 0, 1);
    	txtField.setEnabled(false);
    }
    if(viewID != VIEW_SINGLE_DATE){
      if(startEndDate.getLastLabel() != null){
      	addLabel(startEndDate.getLastLabel(), 2, 0);
      	addField(startEndDate, StartEndDateDesc.FLD_LDATE, 3, 0);
      }else{
      	add(startEndDate, StartEndDateDesc.FLD_LDATE, 2, 0);
      }
      if(startEndDate.isUseSuggestedDates()){
      	FGTextField txtField = new FGTextField();
      	txtField.setText(startEndDate.getSuggestedLastDate().toString());
      	add("Suggested End", txtField, 2, 1);
      	txtField.setEnabled(false);
      }
    }
    if(startEndDate.isUseSuggestedDates()){
    	FGButton button = new FGButton("Pick Suggested Dates");
    	add(button, 1, 2, 2, 1);
    	button.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					if(startEndDate != null){
						startEndDate.setFirstDate(startEndDate.getSuggestedFirstDate());
						startEndDate.setLastDate(startEndDate.getSuggestedLastDate());
					}
				}
    	});
    }
  }

  public void dispose(){
    super.dispose();
    startEndDate = null;
  }
  
  public void setWithValidation(){
  	FValidationPanel vPanel = showValidationPanel(true);
  	vPanel.setValidationButtonLabel("Select");
  	vPanel.setValidationListener(new FValidationListener(){
			@Override
			public void postCancelation(FValidationPanel panel) {
				
			}

			@Override
			public void postValidation(FValidationPanel panel) {
			}

			@Override
			public boolean proceedCancelation(FValidationPanel panel) {
				cancel = true;
				return true;
			}

			@Override
			public boolean proceedValidation(FValidationPanel panel) {
				cancel = false;
				return true;
			}
  	});
  }
  
  public boolean isCancel(){
  	return cancel;
  }
}
