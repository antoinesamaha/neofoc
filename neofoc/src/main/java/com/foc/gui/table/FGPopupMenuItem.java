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
package com.foc.gui.table;

import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JMenuItem;

import com.foc.desc.FocObject;
import com.foc.gui.FAbstractListPanel;
import com.foc.gui.treeTable.FTreeTableModel;
import com.foc.property.FProperty;
import com.foc.tree.FNode;

@SuppressWarnings("serial")
public abstract class FGPopupMenuItem extends JMenuItem implements ActionListener{
	
	private FAbstractListPanel listPanel = null;
	
	public FGPopupMenuItem(String text, Icon icon, FAbstractListPanel listPanel) {
		super(text, icon);
		setName(text);
		this.listPanel = listPanel;
		addActionListener(this);
	}

	public FGPopupMenuItem(String title, FAbstractListPanel listPanel){
		this(title, null, listPanel);
	}
	
	public void dispose(){
		removeActionListener(this);
		listPanel = null;
	}
	
	public int getSelectedRow(){
		return (listPanel != null && listPanel.getTable() != null) ? listPanel.getTable().getSelectedRow() : -1;
	}

	public int getSelectedColumn(){
		return (listPanel != null && listPanel.getTable() != null) ? listPanel.getTable().getSelectedColumn() : -1;
	}

	public int getSelectedColumnVisibleIndex(){
    FTableView tableView = listPanel != null ? listPanel.getTableView() : null;
    return (tableView != null && getSelectedColumn() >= 0) ? tableView.getVisibleColumnIndex(getSelectedColumn()) : -1;
	}
	
	public FAbstractTableModel getTableModel(){
    FTable table = listPanel != null ? listPanel.getTable() : null;
    return table != null ? (FAbstractTableModel) table.getModel() : null;
	}
	
	public FocObject getRowFocObject(){
		int row = getSelectedRow();
    FAbstractTableModel tableModel = getTableModel();
    return (tableModel != null && row >=0) ? tableModel.getRowFocObject(row) : null;
	}
	
	public FProperty getSelectedProperty(){
		FAbstractTableModel model = getTableModel();
		int row = getSelectedRow();
		int col = getSelectedColumnVisibleIndex();
		return (model != null && row >=0 && col >= 0)? model.getFProperty(row, col) : null;
	}
	
	public FNode getRowNode(){
		FNode node = null;
		
		int row = getSelectedRow();
    FAbstractTableModel tableModel = getTableModel();
    
    if(tableModel instanceof FTreeTableModel){
    	node = ((FTreeTableModel) tableModel).getNodeForRow(row);	
    }
		return node;
	}

	public FAbstractListPanel getListPanel() {
		return listPanel;
	}
	
	public void setListPanel(FAbstractListPanel listPanel) {
		this.listPanel = listPanel;
	}

}
