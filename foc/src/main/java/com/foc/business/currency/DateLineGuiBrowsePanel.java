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
package com.foc.business.currency;

import com.foc.FocLangKeys;
import com.foc.business.multilanguage.MultiLanguage;
import com.foc.desc.FocObject;
import com.foc.event.FValidationListener;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.FTableColumn;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class DateLineGuiBrowsePanel extends FListPanel {
	
	public DateLineGuiBrowsePanel(FocList list){
    if(list == null){
      list = new DateLineList(true);
    }
    if (list != null) {
    	setFocList(list);
    	DateLineDesc desc = (DateLineDesc) list.getFocDesc();
      list.setDirectImpactOnDatabase(false);
      list.setDirectlyEditable(true);
      
	    FTableView tableView = getTableView();
	    
	    FTableColumn dateCol = tableView.addColumn(desc, DateLineDesc.FLD_DATE, false);
	    dateCol.setTitle(MultiLanguage.getString(FocLangKeys.CURR_DATE));
	
	    for(int i=0; i<desc.getCurrencyCount(); i++){
	      FTableColumn col = tableView.addColumn(desc, desc.getCurrencyFieldId_At(i), true);
	    }
            
	    construct();

//	    setDirectlyEditable(true);
	    dateCol.setEditable(false);
    
	    FValidationPanel savePanel = showValidationPanel(true);
	    savePanel.setValidationType(FValidationPanel.VALIDATION_SAVE_CANCEL);
	    if (savePanel != null) {
	      savePanel.setValidationListener(new FValidationListener(){
	
	        public boolean proceedValidation(FValidationPanel panel) {
	        	DateLineList dateLineList = (DateLineList) getFocList();
	        	dateLineList.copyToListInCache();
	        	DateLineList.getInstance(true).saveRatesToDB();
	          return true;
	        }
	
	        public boolean proceedCancelation(FValidationPanel panel) {
	          return true;
	        }
	
	        public void postValidation(FValidationPanel panel) {
	        	if(getFocList() != null) getFocList().dispose();
	        }
	
	        public void postCancelation(FValidationPanel panel) {
	        	if(getFocList() != null) getFocList().dispose();
	        }
	      });
	    }

	    requestFocusOnCurrentItem();
	    showEditButton(false);
	    showModificationButtons(false);
	    showAddButton(true);
	    setMainPanelSising(FPanel.FILL_VERTICAL);
    }
	}
	
	public void dispose(){
		super.dispose();
	}
	
  public FPanel newDetailsPanel(FocObject focObject, int viewID){
  	DateLine dateLine = (DateLine) focObject;
  	DateLineGuiDetailsPanel guiDet = new DateLineGuiDetailsPanel(dateLine, viewID);
    return guiDet;
  }
	
}
