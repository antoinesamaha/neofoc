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
package com.foc.gui.table.popupMenu;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import com.foc.Globals;
import com.foc.desc.FocObject;
import com.foc.desc.field.FFieldPath;
import com.foc.gui.FAbstractListPanel;
import com.foc.gui.FGOptionPane;
import com.foc.gui.table.FGPopupMenuItem;
import com.foc.gui.table.FTableColumn;
import com.foc.gui.table.FTableModel;
import com.foc.gui.table.FTableView;
import com.foc.property.FProperty;

@SuppressWarnings("serial")
public class PopupMenuCopyToAllRows extends FGPopupMenuItem {

	private ArrayList<Integer> forbiddenArray = null;
	private ArrayList<Integer> acceptedArray  = null;
	
	public PopupMenuCopyToAllRows(FAbstractListPanel listPanel) {
		super("Copy to all rows", listPanel);
	}
	
	public void addForbiddenColumn(int columnID){
		if(forbiddenArray == null) forbiddenArray = new ArrayList<Integer>();
		forbiddenArray.add(columnID);
	}

	public void addAcceptedColumn(int columnID){
		if(acceptedArray == null) acceptedArray = new ArrayList<Integer>();
		acceptedArray.add(columnID);
	}

	public void actionPerformed(ActionEvent e) {
		FTableModel  tableModel = (FTableModel)getListPanel().getTableModel();
		FTableView   tableView  = tableModel.getTableView();
		
		int          srcRow     = getSelectedRow();
		FocObject    srcObj     = tableModel.getRowFocObject(srcRow);
		
		int          col        = getSelectedColumnVisibleIndex();
		FTableColumn tCol       = tableView.getColumnAt(col);		

		if(tCol != null && srcObj != null){
			boolean acceptedOrNotForbidden = (acceptedArray == null || acceptedArray.get(tCol.getID()) != null) && (forbiddenArray == null || forbiddenArray.get(tCol.getID()) == null);
			if(!acceptedOrNotForbidden){
				Globals.getDisplayManager().popupMessage("Copy does not apply to this column");
			}else if(!FGOptionPane.popupOptionPane_YesNo("Copy Confirmation", "Are you sure you want to copy this cell value to all lines?")){
				FFieldPath path    = tCol.getFieldPath();
				int        fieldID = path.get(0);
				FProperty  srcProp = srcObj.getFocProperty(fieldID);
				
				for(int i=0; i<tableModel.getRowCount(); i++){
					FocObject tarObj  = tableModel.getRowFocObject(i);
					FProperty tarProp = tarObj.getFocProperty(fieldID);
					
					if(tarProp != null){
						tarProp.setObject(srcProp.getObject());
					}
				}
			}
		}
	}
}
