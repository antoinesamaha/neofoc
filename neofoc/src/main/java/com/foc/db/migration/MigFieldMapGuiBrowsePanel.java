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
package com.foc.db.migration;

import com.foc.desc.FocDesc;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class MigFieldMapGuiBrowsePanel extends FListPanel {
  public MigFieldMapGuiBrowsePanel(FocList list, int viewID){
    super("Field mapping", FPanel.FILL_BOTH);
    FocDesc desc = MigFieldMapDesc.getInstance();

    if(desc != null && list != null){
      setFocList(list);
      FTableView tableView = getTableView();   

      tableView.addColumn(desc, MigFieldMapDesc.FLD_DB_FIELD_NAME, false);      
      tableView.addColumn(desc, MigFieldMapDesc.FLD_DB_FIELD_TITLE, false);
      tableView.addColumn(desc, MigFieldMapDesc.FLD_DB_FIELD_EXPLANATION, false);
      tableView.addColumn(desc, MigFieldMapDesc.FLD_COLUMN_TITLE, true);
      tableView.addColumn(desc, MigFieldMapDesc.FLD_MANDATORY, true);
      tableView.addColumn(desc, MigFieldMapDesc.FLD_KEY_FIELD, true);
      tableView.addColumn(desc, MigFieldMapDesc.FLD_DB_FOREIGN_FIELD, true);
      
      construct();
      
      tableView.setColumnResizingMode(FTableView.COLUMN_AUTO_RESIZE_MODE);
      
      showModificationButtons(false);
      showEditButton(false);
      showDuplicateButton(false);
      requestFocusOnCurrentItem();      
    }
  }
}
