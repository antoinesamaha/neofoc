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
package com.fab.parameterSheet;

import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.event.FValidationListener;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class ParameterSheetSelectorGuiBrowsePanel extends FListPanel {
	
	public ParameterSheetSelectorGuiBrowsePanel(FocList list, int viewID){
    super("Paramete set selector",FPanel.FILL_BOTH);
    FocDesc desc = ParameterSheetSelectorDesc.getInstance();
    if(desc != null){
    	if(list == null){
    		list = ParameterSheetSelectorDesc.getList(FocList.LOAD_IF_NEEDED);
    	}
      if (list != null) {
        try{
          setFocList(list);
        }catch(Exception e){
          Globals.logException(e);
        }
        list.setDirectlyEditable(true);
        FTableView tableView = getTableView();  
        
        //tableView.addColumn(desc, ParameterSheetSelectorDesc.FLD_PARAM_SET_ID, 10, true);
        tableView.addColumn(desc, ParameterSheetSelectorDesc.FLD_PARAM_SET_NAME, 30, true);
        tableView.addColumn(desc, ParameterSheetSelectorDesc.FLD_TABLE_NAME, 30, true);

        construct();
        
        requestFocusOnCurrentItem();
        showEditButton(false);
        showDuplicateButton(false);
        
        FValidationPanel validPanel = showValidationPanel(true);
        validPanel.addSubject(list);
        
        validPanel.setValidationListener(new FValidationListener(){

					@Override
					public void postCancelation(FValidationPanel panel) {
					}

					@Override
					public void postValidation(FValidationPanel panel) {
						ParameterSheetSelectorDesc.refreshAllParamSetFieldChoices();
					}

					@Override
					public boolean proceedCancelation(FValidationPanel panel) {
						return true;
					}

					@Override
					public boolean proceedValidation(FValidationPanel panel) {
						return true;
					}
        	
        });
      }
    }
	}
}
