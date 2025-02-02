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
package com.foc.gui.table.view;

import java.awt.Point;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import com.foc.dragNDrop.FocDefaultDropTargetListener;
import com.foc.dragNDrop.FocTransferable;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.table.FColumnGroupTableHeader;
import com.foc.gui.table.FTableColumn;
import com.foc.gui.table.FTableView;
import com.foc.gui.table.view.ColumnsConfig;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class ColumnsConfigGuiBrowsePanel extends FListPanel{
	
	public static final int VIEW_MAIN         = 1;
	public static final int VIEW_MULTI_SELECT = 2;
	
	private FTableView underlyingTableView = null;
	
	public ColumnsConfigGuiBrowsePanel(FocList list, int view, boolean editable, FTableView tableView){
		setFill(FPanel.FILL_BOTH);
		setWithScroll(false);
		list.rebuildArrayList();
		setFocList(list);
		
		setUnderlyingTableView(tableView); 
		
		FTableView tv = getTableView();
		if(view == VIEW_MULTI_SELECT){
			tv.addSelectionColumn();
		}else{
			tv.addColumn(ColumnsConfigDesc.getInstance(), ColumnsConfigDesc.FLD_COLUMN_ORDER, false);
			tv.addColumn(ColumnsConfigDesc.getInstance(), ColumnsConfigDesc.FLD_SHOW, editable);
		}
		tv.addColumn(ColumnsConfigDesc.getInstance(), ColumnsConfigDesc.FLD_COLUMN_TITLE, editable);
		tv.addColumn(ColumnsConfigDesc.getInstance(), ColumnsConfigDesc.FLD_BACKGROUND, editable);
		tv.addColumn(ColumnsConfigDesc.getInstance(), ColumnsConfigDesc.FLD_FOREGROUND, editable);
		tv.addColumn(ColumnsConfigDesc.getInstance(), ColumnsConfigDesc.FLD_FONT_SIZE, editable);
		tv.addColumn(ColumnsConfigDesc.getInstance(), ColumnsConfigDesc.FLD_FONT_STYLE, editable);
		tv.addColumn(ColumnsConfigDesc.getInstance(), ColumnsConfigDesc.FLD_DECIMALS, editable);
		tv.addColumn(ColumnsConfigDesc.getInstance(), ColumnsConfigDesc.FLD_GROUPING_USED, editable);
		tv.addColumn(ColumnsConfigDesc.getInstance(), ColumnsConfigDesc.FLD_COLUMN_DEFAULT_TITLE, false);
		tv.addColumn(ColumnsConfigDesc.getInstance(), ColumnsConfigDesc.FLD_COLUMN_EXPLANATION, false);
		
		construct();
		
		//addFilterExpressionPanel();
		
		tv.setColumnResizingMode(FTableView.COLUMN_AUTO_RESIZE_MODE);
		
	  FocDefaultDropTargetListener defaultDropTargetListener = new FocDefaultDropTargetListener(){
	  	public void drop(FocTransferable focTransferable, DropTargetDropEvent dtde){
	  		ColumnsConfig srcCC  = (ColumnsConfig) focTransferable.getSourceFocObject();
	  		Point point = getTable().getCellCoordinatesForMouseCurrentPosition();	  		
	  		int           tarRow = point.y;
	  		ColumnsConfig tarCC  = (ColumnsConfig) getTableModel().getFocList().getFocObject(tarRow);
	  		if(tarCC != null && srcCC != null){
	  			FTableColumn tarCol = getUnderlyingTableView().getColumnById(tarCC.getColumnID()); 
	  			FTableColumn srcCol = getUnderlyingTableView().getColumnById(srcCC.getColumnID());
	  			if(tarCol != null && srcCol != null){
	  				FColumnGroupTableHeader tableHeader = (FColumnGroupTableHeader) getUnderlyingTableView().getTable().getScrollPane().getScrollTable().getTableHeader();
	  				ArrayList tarArray = tableHeader.getGroupsForColumn(tarCol);
	  				ArrayList srcArray = tableHeader.getGroupsForColumn(srcCol);
	  				if(tarArray != null && srcArray != null){

	  					//Actual Drag And Drop
	  		  		super.drop(focTransferable, dtde);
	  		  		FocList list = getFocList();
	  		  		for(int i=0; i<list.size(); i++){
	  		  			ColumnsConfig colCfg = (ColumnsConfig) list.getFocObject(i);
	  		  			colCfg.setColumnOrder(i+1);
	  		  		}
	  					
	  				}
	  			}
	  		}
	  	}
	  };
	  if(editable){
	  	setDropable(defaultDropTargetListener);
	  }
		
		showModificationButtons(false);
		showEditButton(false);
		if(editable){
			showSelecAllUnselectAllPanel();
			addSelectAllActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					FocList list = getFocList();
					for(int i=0; i<list.size(); i++){
						ColumnsConfig config = (ColumnsConfig) list.getFocObject(i);
						config.setShow(true);
					}
				}
			});
			addUnselectAllActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					FocList list = getFocList();
					for(int i=0; i<list.size(); i++){
						ColumnsConfig config = (ColumnsConfig) list.getFocObject(i);
						config.setShow(false);
					}
				}
			});
		}
		
		if(view == VIEW_MAIN){
			showAddButton(true);
			showRemoveButton(true);
		}
		
		list.sort();
	}
	
	public FTableView getUnderlyingTableView() {
		return underlyingTableView;
	}

	public void setUnderlyingTableView(FTableView underlyingTableView) {
		this.underlyingTableView = underlyingTableView;
	}
}
