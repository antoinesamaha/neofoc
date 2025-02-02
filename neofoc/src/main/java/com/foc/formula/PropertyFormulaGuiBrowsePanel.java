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
package com.foc.formula;

import com.foc.desc.FocDesc;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class PropertyFormulaGuiBrowsePanel extends FPanel {
  public PropertyFormulaGuiBrowsePanel(FocList list, int viewID){
    super("Property Formulas", FPanel.FILL_NONE);
    FocDesc desc = PropertyFormulaDesc.getInstance();

    FListPanel selectionPanel = null;
    if (desc != null) {
      if(list == null){
        list = PropertyFormulaDesc.getList(FocList.LOAD_IF_NEEDED);
      }else{
        list.loadIfNotLoadedFromDB();
      }
      selectionPanel = new FListPanel(list);
      FTableView tableView = selectionPanel.getTableView();   
      
      tableView.addColumn(desc, PropertyFormulaDesc.FLD_FIELD_NAME, true);
      tableView.addColumn(desc, PropertyFormulaDesc.FLD_EXPRESSION, true);

      
      selectionPanel.construct();
      selectionPanel.showEditButton(false);
      selectionPanel.showDuplicateButton(false);
      selectionPanel.requestFocusOnCurrentItem();      
    }

    add(selectionPanel, 0, 0);
    
    FValidationPanel savePanel = showValidationPanel(true);
    if (savePanel != null) {
      savePanel.addSubject(list);
    }
  }
}
