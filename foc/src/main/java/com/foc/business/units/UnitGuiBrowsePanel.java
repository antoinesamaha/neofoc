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
package com.foc.business.units;

import com.foc.desc.FocDesc;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class UnitGuiBrowsePanel extends FPanel {

	public UnitGuiBrowsePanel(FocList list, int viewID){
		super("Unit", FPanel.FILL_VERTICAL);
		FocDesc desc = UnitDesc.getInstance();

    FListPanel selectionPanel = null;
    if (desc != null) {
    	if(list == null){
    		list = UnitDesc.getList(FocList.LOAD_IF_NEEDED);
    	}else{
    		list.loadIfNotLoadedFromDB();
    	}
      selectionPanel = new FListPanel(list);
      FTableView tableView = selectionPanel.getTableView();   
      
      /*FocConstructor constr = new FocConstructor(CompanyFilter.getFocDesc(), null);
      PersonFilter filter = (PersonFilter) constr.newItem();
      tableView.setFilter(filter);*/
      
      tableView.addColumn(desc, UnitDesc.FLD_NAME, true);
      tableView.addColumn(desc, UnitDesc.FLD_SYMBOL, 10, true);
      tableView.addColumn(desc, UnitDesc.FLD_FACTOR, true);
      
      selectionPanel.construct();
      
      selectionPanel.showEditButton(false);
      selectionPanel.showDuplicateButton(false);
      //selectionPanel.showFilterButton(true);
      //filter.setActive(true);
      selectionPanel.requestFocusOnCurrentItem();      
      //tableView.getTable().setSelectedObject(list.getFocObject(0));
    }

    add(selectionPanel,0,0);
    
    //FGCurrentItemPanel currentItemPanel = new FGCurrentItemPanel(selectionPanel, 0);
    //add(currentItemPanel,0,1);
       
	}  
}
