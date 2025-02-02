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
package com.foc.gui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import com.foc.gui.FAbstractListPanel;
import com.foc.gui.table.FAbstractTableModel;
import com.foc.gui.table.FTable;
import com.foc.gui.table.FTableView;
import com.foc.property.FProperty;

@SuppressWarnings("serial")
public class PasteAction extends AbstractAction {
  protected FAbstractListPanel abstractListPanel = null;
  
  public PasteAction( FAbstractListPanel abstractListPanel ){
    this.abstractListPanel = abstractListPanel;
  }
  
  public void dispose(){
    abstractListPanel = null;
  }
  
  public void actionPerformed(ActionEvent e) {
    FTable table = abstractListPanel.getTable();
    
    int row = table.getSelectedRow();
    int col = table.getSelectedColumn();
    FAbstractTableModel tableModel = table.getTableModel();
    FTableView tableView = tableModel.getTableView();
    col = tableView.getVisibleColumnIndex(col);
    
    FProperty property = tableModel.getFProperty(row, col);
    if( property != null && shouldPaste(property) ){
      CopyPasteContent copyPasteContent = abstractListPanel.getCopyPasteContent();
      if(!property.isValueLocked()){ //TEMP
        if( copyPasteContent.getTableDisplayObject() != null ){
          tableModel.setValueAt(copyPasteContent.getTableDisplayObject(), row, col);  
        }
      }
    }
  }
  
  public boolean shouldPaste(FProperty property){
    return true;
  }
  
}
