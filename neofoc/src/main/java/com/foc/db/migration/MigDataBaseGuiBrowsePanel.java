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
import com.foc.desc.field.FField;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class MigDataBaseGuiBrowsePanel extends FListPanel {
  public MigDataBaseGuiBrowsePanel(FocList list, int viewID){
    super("DataBases", FPanel.FILL_VERTICAL);
    FocDesc desc = MigDataBaseDesc.getInstance();

    if (desc != null) {
      if(list == null){
        list = MigDataBaseDesc.getList(FocList.LOAD_IF_NEEDED);
      }else{
        list.loadIfNotLoadedFromDB();
      }
      setFocList(list);
      FTableView tableView = getTableView();   

      tableView.addColumn(desc, FField.FLD_NAME, false);
      tableView.addColumn(desc, MigDataBaseDesc.FLD_DESCRIPTION, false);
      tableView.addColumn(desc, MigDataBaseDesc.FLD_URL, false);      
      tableView.addColumn(desc, MigDataBaseDesc.FLD_JDBC_DRIVER, false);
      tableView.addColumn(desc, MigDataBaseDesc.FLD_USER_NAME, false);
      
      construct();
      
      showEditButton(true);
      showDuplicateButton(false);
      requestFocusOnCurrentItem();      
    }

    FValidationPanel savePanel = showValidationPanel(true);
    if (savePanel != null) {
      savePanel.addSubject(list);
    }
  }
}
