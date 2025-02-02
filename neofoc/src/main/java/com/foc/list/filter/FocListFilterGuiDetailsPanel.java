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
package com.foc.list.filter;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;

import com.fab.model.filter.UserDefinedFilter;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.gui.FGButton;
import com.foc.gui.FGButtonAction;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;

@SuppressWarnings("serial")
public class FocListFilterGuiDetailsPanel extends FPanel {
	private FocListFilter    focListFilter   = null;
	private FValidationPanel validationPanel = null;
	private FocListFilterValidationListener filterValidationPanel = null; 
  
  private static final int    MAX_COLUMN_OCCUPATION = 4;

	protected int x = 0;
	protected int y = 0;

	public FocListFilterGuiDetailsPanel(FocObject focObject, int viewID){
    if(focObject != null){
    	focListFilter = (FocListFilter)focObject;
    	
    	if(focObject.isDbResident() && focObject.getThisFocDesc().isDbResident()){
    		add(focObject, FField.FLD_NAME, x, y++);
    	}

    	addFieldsBeforeFilterFields(focObject);
    	
	    FilterDesc filterDesc = focListFilter.getThisFilterDesc();
	    for(int i=0; i<filterDesc.getConditionCount(); i++){
	      FilterCondition cond = filterDesc.getConditionAt(i);
	      if(cond.isDisplay()){
		      GuiSpace space = cond.putInPanel(focListFilter, this, x * MAX_COLUMN_OCCUPATION, y);
		      
		      x++;
		      if(x == filterDesc.getNbrOfGuiColumns()){
		      	x = 0;
		      	y += space.getY();
		      }
	      }
	    }
	    
      if (viewID != FocListFilter.VIEW_SUMMARY){
  	    if(validationPanel != null){
  	      validationPanel.dispose();
  	      validationPanel = null;
  	    }
  	    validationPanel = showValidationPanel(true);
  	    if(viewID == UserDefinedFilter.VIEW_FOR_FILTER_CREATION){
  	    	validationPanel.addSubject(focListFilter);
  	    }else{
  	    	filterValidationPanel = new FocListFilterValidationListener(null, focListFilter, validationPanel);
  	    	/*
  		    validationPanel.setValidationType(FValidationPanel.VALIDATION_OK_CANCEL);
  		    validationPanel.setAskForConfirmationForExit(false);
  		    
  		    validationPanel.setCancelationButtonLabel(BUTTON_LABEL_CANCEL);
  		    validationPanel.setValidationButtonLabel(BUTTON_LABEL_APPLY);
  		    if(!focListFilter.isAllwaysActive()){
  		      FGButton removeButton = new FGButton(BUTTON_LABEL_RESET);
  		      removeButton.setName("Remove");
  		      removeButton.addActionListener(new ActionListener(){
  		        public void actionPerformed(ActionEvent e) {
  		          focListFilter.setActive(false);
  		          validationPanel.cancel();
  		        }
  		      });
  		      validationPanel.addButton(removeButton);
  		      if (focListFilter.isRevisionSupportEnabled()){
  		        removeButton.setVisible(false);
  		      }
  		    }    
  		    
  		    validationPanel.setValidationListener(new FValidationListener(){
  		      public boolean proceedValidation(FValidationPanel panel) {
  		        //Globals.logString("list visible count in foc filter : " +getListVisibleElementCount());
  		        return true;
  		      }
  		
  		      public boolean proceedCancelation(FValidationPanel panel) {
  		        return true;
  		      }
  		
  		      public void postValidation(FValidationPanel panel) {   
  		        focListFilter.setActive(true); 
  		      }
  		
  		      public void postCancelation(FValidationPanel panel) {
  		        
  		      }
  		    });
  		    */
  	    }
      }else{
        setFill(FPanel.FILL_HORIZONTAL);
        setMainPanelSising(GridBagConstraints.NORTHEAST);
        FGButton button = new FGButton(FocListFilterValidationListener.BUTTON_LABEL_APPLY);
        FGButtonAction buttonAction = new FGButtonAction(button) {
          public void focActionPerformed(ActionEvent e) {
            focListFilter.setActive(true);
          }
        };
        button.setAction(buttonAction);
        add(button, filterDesc.getNbrOfGuiColumns() * MAX_COLUMN_OCCUPATION, 0);        
        button.setText(FocListFilterValidationListener.BUTTON_LABEL_APPLY);//A Swing Bug made the title marked above disapear if we do not reconfirm the text
      }
    }
	}
	
	public void dispose(){
		super.dispose();
		if(filterValidationPanel != null){
			filterValidationPanel.dispose();
			filterValidationPanel = null;
		}
		focListFilter   = null;
		validationPanel = null;
	}
	
	public void addFieldsBeforeFilterFields(FocObject focObject){
		
	}
}
