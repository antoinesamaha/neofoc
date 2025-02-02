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
package com.foc.business.adrBook;

import com.foc.desc.FocDesc;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class AdrBookPartyGuiSelectionPanel extends FListPanel {
  
	public static final int VIEW_MULTIPLE_SELECTION = 1;
	
  public AdrBookPartyGuiSelectionPanel(FocList list, int viewID){
    super("Select a company", FPanel.FILL_VERTICAL);
    FocDesc desc = AdrBookPartyDesc.getInstance();
    if(desc != null){
      setFocList(list);
    }
    
    FTableView tableView = getTableView();
    
    if(viewID == VIEW_MULTIPLE_SELECTION){
    	tableView.addSelectionColumn();
    }
    
    tableView.addColumn(desc, AdrBookPartyDesc.FLD_CODE, false);
    tableView.addColumn(desc, AdrBookPartyDesc.FLD_NAME, false);
    tableView.addColumn(desc, AdrBookPartyDesc.FLD_EXTERNAL_CODE, false);
    construct();
    
    addFilterExpressionPanel();
    
    FValidationPanel selectionPanel = showValidationPanel(true);
    selectionPanel.addSubject(list);
    if(viewID == VIEW_MULTIPLE_SELECTION){
    	selectionPanel.setValidationButtonLabel("Ok");
    }

    requestFocusOnCurrentItem();
    showEditButton(false);
    showAddButton(false);
    showRemoveButton(false);
  }
}
