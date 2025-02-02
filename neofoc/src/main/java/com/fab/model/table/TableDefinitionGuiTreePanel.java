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
package com.fab.model.table;

import java.awt.event.ActionEvent;

import com.foc.ConfigInfo;
import com.foc.desc.FocDesc;
import com.foc.gui.FTreeTablePanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.FGPopupMenuItem;
import com.foc.gui.table.FTableView;
import com.foc.tree.objectTree.FObjectTreeDropTargetListener;

@SuppressWarnings("serial")
public class TableDefinitionGuiTreePanel extends FTreeTablePanel {
  
  public static final int VIEW_EMBEDDED_IN_SPLIT = 2;
	
  public TableDefinitionGuiTreePanel(TableDefinitionTree tree, int viewID){
  	init(tree, viewID);
  }
  
  public void addColumns(){
    FocDesc desc = getFocList().getFocDesc();

    if(desc != null){
      FTableView tableView = getTableView();
      tableView.addColumn(desc, TableDefinitionDesc.FLD_CLASS_NAME, false);
      tableView.addColumn(desc, TableDefinitionDesc.FLD_PROJECT, false);
      tableView.addColumn(desc, TableDefinitionDesc.FLD_WEB_CLIENT_PACKAGE, false);
      tableView.addColumn(desc, TableDefinitionDesc.FLD_WEB_CLIENT_PROJECT, false);
    }  	
  }
  
  public void init(TableDefinitionTree tree, int viewID){
    setMainPanelSising(FILL_VERTICAL);
    setFrameTitle("Departments Hierarchy");

    FocDesc desc = tree.getFocList().getFocDesc();

    if(desc != null){
      setTree(tree);
      expandAll();
      
      addColumns();
      
      construct();
      if(viewID != VIEW_EMBEDDED_IN_SPLIT){
      	getTable().setDropable(new FObjectTreeDropTargetListener(this));
      }
      
      //getTable().setDropable(new Obje);
      getTableView().setColumnResizingMode(FTableView.COLUMN_AUTO_RESIZE_MODE);
      showAddButton(viewID != VIEW_EMBEDDED_IN_SPLIT);
      showRemoveButton(viewID != VIEW_EMBEDDED_IN_SPLIT);
      showEditButton(true);
      requestFocusOnCurrentItem();

      FValidationPanel valid = showValidationPanel(true);
      valid.addSubject(tree.getFocList());
      
    	if(ConfigInfo.isForDevelopment()){
    		FGPopupMenuItem generateServerCodeMenu = new FGPopupMenuItem("Generate server code", this){
					@Override
					public void actionPerformed(ActionEvent e) {
						TableDefinition tableDef = (TableDefinition) getSelectedObject();
						if(tableDef != null){
							tableDef.writeCode_ServerCode();
						}
					}
    		};
    		getTable().getPopupMenu().add(generateServerCodeMenu);
    		
    		FGPopupMenuItem generateClientCodeMenu = new FGPopupMenuItem("Generate client code", this){
					@Override
					public void actionPerformed(ActionEvent e) {
						TableDefinition tableDef = (TableDefinition) getSelectedObject();
						if(tableDef != null){
							if(tableDef.isWebStructure()){
								tableDef.writeCode_ClientCode();
							}
						}
					}
    		};
    		getTable().getPopupMenu().add(generateClientCodeMenu);

    		FGPopupMenuItem generateFocDesc = new FGPopupMenuItem("Generate all code", this){
					@Override
					public void actionPerformed(ActionEvent e) {
						TableDefinition tableDef = (TableDefinition) getSelectedObject();
						if(tableDef != null){
							tableDef.writeCode_ServerCode();

							/*
							codeWriter = new CodeWriter_FocList(tableDef);
							codeWriter.generateCode();
							codeWriter.dispose();
							codeWriter = null;
							*/

							if(tableDef.isWebStructure()){
								tableDef.writeCode_ClientCode();
							}
						}
					}
    		};
    		getTable().getPopupMenu().add(generateFocDesc);
    	}
    }
  }
}
