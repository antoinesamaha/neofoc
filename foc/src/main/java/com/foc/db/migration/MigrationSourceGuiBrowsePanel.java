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

import java.awt.event.ActionEvent;

import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FField;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.FPopupMenu;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.FGPopupMenuItem;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class MigrationSourceGuiBrowsePanel extends FListPanel {
  public MigrationSourceGuiBrowsePanel(FocList list, int viewID){
    super("Migration Sources", FPanel.FILL_VERTICAL);
    FocDesc desc = MigrationSourceDesc.getInstance();

    if (desc != null) {
      if(list == null){
        list = MigrationSourceDesc.getList(FocList.LOAD_IF_NEEDED);
      }else{
        list.loadIfNotLoadedFromDB();
      }
      setFocList(list);
      FTableView tableView = getTableView();   

      tableView.addColumn(desc, FField.FLD_NAME, false);
      tableView.addColumn(desc, MigrationSourceDesc.FLD_DESCRIPTION, false);      
      tableView.addColumn(desc, MigrationSourceDesc.FLD_SOURCE_TYPE, 20, false);
      
      construct();
      
      FPopupMenu popMenu = getTable().getPopupMenu();
      FGPopupMenuItem menuItem = new FGPopupMenuItem("Import", this){
				@Override
				public void actionPerformed(ActionEvent e) {
					MigrationSource migrationSource = (MigrationSource) getSelectedObject();
					if(migrationSource != null){
						migrationSource.doImport();
					}else{
						Globals.getDisplayManager().popupMessage("Please select a Migration Source");
					}
				}
      };
      popMenu.add(menuItem);
      
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
