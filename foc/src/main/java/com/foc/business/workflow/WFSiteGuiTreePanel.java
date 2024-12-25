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
package com.foc.business.workflow;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FField;
import com.foc.gui.FTreeTablePanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.FTableColumn;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;
import com.foc.tree.objectTree.FObjectTreeDropTargetListener;

@SuppressWarnings("serial")
public class WFSiteGuiTreePanel extends FTreeTablePanel {
  
  public WFSiteGuiTreePanel(FocList list, int viewID){
    setMainPanelSising(FILL_VERTICAL);
    setFrameTitle("Site Hierarchy");

    FocDesc desc = WFSiteDesc.getInstance();

    if(desc != null){
    	WFSiteTree tree = WFSiteTree.newInstance();
      
      setTree(tree);
      expandAll();
      
      FTableView tableView = getTableView();
      
      FTableColumn col = null;
     	tableView.addColumn(desc, WFSiteDesc.FLD_DESCRIPTION, false);
      
      construct();

      getTable().setDropable(new FObjectTreeDropTargetListener(this));
      
      //getTable().setDropable(new Obje);
      //tableView.setColumnResizingMode(FTableView.COLUMN_AUTO_RESIZE_MODE);
      showAddButton(true);
      showRemoveButton(true);
      showEditButton(true);
      requestFocusOnCurrentItem();

      col = tableView.getColumnById(FField.TREE_FIELD_ID);
      col.setEditable(false);
      FValidationPanel valid = showValidationPanel(true);
      valid.addSubject(tree.getFocList());
    }
  }
}
