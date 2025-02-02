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
package com.foc;

import com.foc.desc.FocDesc;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class ConfigInfoObjectGuiBrowsePanel extends FPanel{

  public ConfigInfoObjectGuiBrowsePanel(FocList list, int viewID){
    super("Config Info", FPanel.FILL_NONE);
    FocDesc desc = ConfigInfoObjectDesc.getInstance();

    FListPanel selectionPanel = null;
    if (desc != null) {
      if(list == null){
        list = ConfigInfoObjectDesc.getList(FocList.LOAD_IF_NEEDED);
      }else{
        list.loadIfNotLoadedFromDB();
      }
      selectionPanel = new FListPanel(list);
      FTableView tableView = selectionPanel.getTableView();   
      
      tableView.addColumn(desc, ConfigInfoObjectDesc.FLD_PROPERTY, false);
      tableView.addColumn(desc, ConfigInfoObjectDesc.FLD_VALUE, true);
      
      selectionPanel.construct();
      
      selectionPanel.showAddButton(false);
      selectionPanel.showRemoveButton(false);
      selectionPanel.showEditButton(false);
      selectionPanel.showDuplicateButton(false);
      selectionPanel.requestFocusOnCurrentItem();      
    }

    add(selectionPanel,0,0);
       
  }  
}
