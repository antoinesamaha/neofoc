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
package com.foc.desc.dataModelTree;

import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.event.FValidationListener;
import com.foc.gui.FPanel;
import com.foc.gui.FTreeTablePanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class DataModelNodeGuiTreePanel extends FPanel {
  
  private FTreeTablePanel   selectionPanel = null;
  private DataModelNodeList modelList      = null;
  private DataModelNode     selectedNode   = null;
  
  public static final int VIEW_STANDARD              = FocObject.DEFAULT_VIEW_ID;
  public static final int VIEW_FOR_REPORT_GENERATION = 1;
  
  private boolean forReport = false;
  
  public DataModelNodeGuiTreePanel(FocList list, int viewID){
    super("Data model tree", FPanel.FILL_BOTH);
    forReport = viewID == VIEW_FOR_REPORT_GENERATION;
    modelList = (DataModelNodeList) list;
    FocDesc desc = DataModelNodeDesc.getInstance();
    if (desc != null && list != null) {
      modelList.setDirectImpactOnDatabase(false);
      
      DataModelNodeTree tree = new DataModelNodeTree(modelList);
         
      selectionPanel = new FTreeTablePanel(tree);
      FTableView tableView = selectionPanel.getTableView();

      boolean editable = false;

      if(forReport){
      	tableView.addColumn(desc, DataModelNodeDesc.FLD_SELECT, true);
      }
      
      tableView.addColumn(desc, DataModelNodeDesc.FLD_PATH_SECTION, false);
      tableView.addColumn(desc, DataModelNodeDesc.FLD_DESCRIPTION, false);
      tableView.addColumn(desc, DataModelNodeDesc.FLD_VALUE, false);
      
      selectionPanel.construct();

      selectionPanel.showAddButton(editable);
      selectionPanel.showRemoveButton(editable);
      
      selectionPanel.showEditButton(false);
      selectionPanel.requestFocusOnCurrentItem();

      if(!forReport){
	      FValidationPanel vPanel = showValidationPanel(true);
	      vPanel.setValidationListener(new FValidationListener(){
					public void postCancelation(FValidationPanel panel) {
					}
	
					public void postValidation(FValidationPanel panel) {
					}
					
					public boolean proceedCancelation(FValidationPanel panel) {
						return true;
					}
	
					public boolean proceedValidation(FValidationPanel panel) {
						selectedNode = (DataModelNode) selectionPanel.getSelectedObject();
						return true;
					}
	      });
	      vPanel.setSelectionType(FValidationPanel.SELECTION_ENABLED);
      }
    }
    add(selectionPanel, 0, 0);
  }
  
  public DataModelNode getSelectedNode(){
  	return selectedNode;
  }
  
  public String getSelectedFullPath(){
  	DataModelNode node = getSelectedNode();
  	return node != null ? node.getFullPath() : "";
  }
}
