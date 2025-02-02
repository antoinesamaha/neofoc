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
package com.foc.admin;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FField;
import com.foc.gui.FPanel;
import com.foc.gui.FTreeTablePanel;
import com.foc.gui.table.FTableColumn;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class MenuRightsGuiTreePanel extends FPanel{
  
  public MenuRightsGuiTreePanel(FocList list, int viewID, boolean readOnly){
    super("Menu Rights", FPanel.FILL_BOTH);
    
    FocDesc desc = MenuRightsDesc.getInstance();
    if (desc != null) {
      if(list == null){
        list = MenuRightsDesc.getList(FocList.FORCE_RELOAD);
      }
      if (list != null) {
        MenuRightsObjectTree menuRightsTree = new MenuRightsObjectTree(list, viewID);
        
        FTreeTablePanel selectionPanel = new FTreeTablePanel(menuRightsTree);
        FTableView tableView = selectionPanel.getTableView();

        FTableColumn col = tableView.getColumnById(FField.TREE_FIELD_ID);
        col.setEditable(false);
        tableView.addColumn(list.getFocDesc(), MenuRightsDesc.FLD_MENU_TITLE, false);
        tableView.addColumn(list.getFocDesc(), MenuRightsDesc.FLD_CUSTOM_TITLE, !readOnly);
        tableView.addColumn(list.getFocDesc(), MenuRightsDesc.FLD_RIGHT, 5, !readOnly);
        
        selectionPanel.construct();

        //tableView.setColumnResizingMode(FTableView.COLUMN_AUTO_RESIZE_MODE);
        
        selectionPanel.showEditButton(false);
        selectionPanel.showAddButton(false);
        selectionPanel.showRemoveButton(false);
        
        add(selectionPanel,0,0);
      }
    }
  }
}
