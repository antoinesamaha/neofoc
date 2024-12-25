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
package com.fab.model.menu;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FField;
import com.foc.gui.FPanel;
import com.foc.gui.FTreeTablePanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.FTableColumn;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;
import com.foc.tree.objectTree.FObjectTreeDropTargetListener;

@SuppressWarnings("serial")
public class MenuDefinitionGuiTreePanel extends FTreeTablePanel {
	
	public MenuDefinitionGuiTreePanel(FocList list, int viewID){
		super(new MenuDefinitionTree(list, viewID));
    FocDesc desc = MenuDefinitionDesc.getInstance();
    if(desc != null){
      if(list == null){
        list = MenuDefinitionDesc.getList(FocList.FORCE_RELOAD);
      }
      if(list != null){
      	FTableView tableView = getTableView();
      	FTableColumn col = tableView.getColumnById(FField.TREE_FIELD_ID);
      	col.setEditable(true);

      	//col = new FTableColumn(desc, FField.FLD_ORDER, "Order", 20, true);
      	//tableView.addColumn(col); 
      	
      	col = new FTableColumn(desc, MenuDefinitionDesc.FLD_TABLE_DEFINITION, "Table", 20, true);
      	tableView.addColumn(col); 
      	
      	col = new FTableColumn(desc, MenuDefinitionDesc.FLD_USER_BROWSE_VIEW_DEFINITION, "Browse view", 20, true);
      	tableView.addColumn(col);
      	
      	col = new FTableColumn(desc, MenuDefinitionDesc.FLD_USER_DETAILS_VIEW_DEFINITION, "Details view", 20, true);
      	tableView.addColumn(col);
      	
      	construct();
        setDropable(new FObjectTreeDropTargetListener(this));
      	showEditButton(false);
        FValidationPanel validPanel = showValidationPanel(true);
        validPanel.addSubject(list);
        validPanel.setValidationType(FValidationPanel.VALIDATION_SAVE_CANCEL);
        
        setFrameTitle("User Menus");
        setMainPanelSising(FPanel.MAIN_PANEL_FILL_BOTH);   
      }
    }
  }
}
