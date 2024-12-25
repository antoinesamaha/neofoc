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
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class GrpViewRightsGuiBrowsePanel extends FPanel{
  
  public GrpViewRightsGuiBrowsePanel(FocList list, int viewID, boolean readOnly){
    super("Views Rights", FPanel.FILL_BOTH);
    setFill(FPanel.FILL_HORIZONTAL);
    setMainPanelSising(FPanel.MAIN_PANEL_FILL_HORIZONTAL);
    
    FocDesc desc = GrpViewRightsDesc.getInstance();
    if (desc != null) {
      if (list != null) {
        FListPanel selectionPanel = new FListPanel(list);
        FTableView tableView = selectionPanel.getTableView();

        tableView.addColumn(list.getFocDesc(), GrpViewRightsDesc.FLD_VIEW_KEY, false);
        tableView.addColumn(list.getFocDesc(), GrpViewRightsDesc.FLD_VIEW_CONTEXT, false);
        tableView.addColumn(list.getFocDesc(), GrpViewRightsDesc.FLD_VIEW_RIGHT, !readOnly);
        tableView.addColumn(list.getFocDesc(), GrpViewRightsDesc.FLD_VIEW_CONFIG, !readOnly);
        
        selectionPanel.construct();
        tableView.setColumnResizingMode(FTableView.COLUMN_AUTO_RESIZE_MODE);
        
        selectionPanel.showEditButton(false);
        selectionPanel.showAddButton(false);
        selectionPanel.showRemoveButton(false);
        
        add(selectionPanel,0,0);
      }
    }
  }
}
