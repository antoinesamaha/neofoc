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
package b01.officeLink.excel.synchronize;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.desc.dataModelTree.DataModelNode;
import com.foc.desc.dataModelTree.DataModelNodeGuiTreePanel;
import com.foc.desc.dataModelTree.DataModelNodeList;
import com.foc.gui.FButtonsPanel;
import com.foc.gui.FGButton;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class ExcelColumnGuiBrowsePanel extends FListPanel {
	
  public ExcelColumnGuiBrowsePanel(FocList list, int viewID){
    super("Import Map", FPanel.FILL_BOTH);
    FocDesc desc = ExcelColumnDesc.getInstance();

    if (desc != null && list != null) {
      list.loadIfNotLoadedFromDB();
      try{
      	setFocList(list);
      }catch(Exception e){
      	Globals.logException(e);
      }
      FTableView tableView = getTableView();   
      
      tableView.addColumn(desc, ExcelColumnDesc.FLD_POSITION, false);
      tableView.addColumn(desc, ExcelColumnDesc.FLD_AUTOCAD_COL, false);
      tableView.addColumn(desc, ExcelColumnDesc.FLD_FIELD_MODE, true);
      tableView.addColumn(desc, ExcelColumnDesc.FLD_MANDATORY, true);
      tableView.addColumn(desc, ExcelColumnDesc.FLD_C3_COL, 40, true);
      
      construct();

      tableView.setColumnResizingMode(FTableView.COLUMN_AUTO_RESIZE_MODE);
      tableView.setEditAfterInsertion(true);
      
      showAddButton(false);
      showRemoveButton(false);
      showEditButton(false);
      showDuplicateButton(false);
      requestFocusOnCurrentItem();      
    }

    FButtonsPanel buttonsPanel = getButtonsPanel();
    FGButton fieldBrowser = new FGButton("Field browser");
    fieldBrowser.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				FocList mapFocList = getFocList();
				ExcelSync configObj = mapFocList != null ? (ExcelSync) mapFocList.getFatherSubject() : null;
				FocList configFocList = configObj != null ? (FocList) configObj.getFatherSubject() : null;
				ExcelSyncGuiBrowsePanel browsePanel = configFocList != null ? (ExcelSyncGuiBrowsePanel) configFocList.getAttachedListPanel() : null;
				DataModelNodeList modelNodeList = browsePanel != null ? browsePanel.getDataModelNodeList() : null;
				
				if(modelNodeList != null){
					DataModelNodeGuiTreePanel panel = new DataModelNodeGuiTreePanel(modelNodeList, 0);
					Globals.getDisplayManager().popupDialog(panel, "Select a field", true);
					
					DataModelNode node = panel.getSelectedNode();
					if(node != null){
						String path = node.getFullPath();
						ExcelColumn mapObj = (ExcelColumn) getSelectedObject();
						mapObj.setC3Header(path);
					}
				}
			}
    });
    buttonsPanel.add(fieldBrowser);
  }
}
