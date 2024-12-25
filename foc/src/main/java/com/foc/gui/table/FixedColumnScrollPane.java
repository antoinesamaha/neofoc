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

import java.awt.Color;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.JTableHeader;

import com.foc.dragNDrop.FocDropable;
import com.foc.dragNDrop.FocTransferable;
import com.foc.gui.borders.FocLineBorder;

@SuppressWarnings("serial")
public class FixedColumnScrollPane extends JScrollPane implements FocDropable {
  
  private FTable scrollTable = null;
  private FTable fixedTable  = null;
  
  public FixedColumnScrollPane(FTable scrollTable ) {
    super();
    this.scrollTable = scrollTable;
    
//    FColumnGroupHeaderConstructor headerConstr = scrollTable.getTableView().getColumnGroupHeaderConstructor();
//    headerConstr.constructHeader(scrollTable);
    
    FAbstractTableModel scrollModel = (FAbstractTableModel)scrollTable.getModel();
    FTableView tableview = scrollModel.getTableView();
    
    if(tableview.getColumnsToFreeze() > 0){
      splitTableAccordingToColmnsFreezed(scrollModel);
    }else{
      putTableInScroll();
    }
    
    
    FColumnGroupHeaderConstructor headerConstr = scrollTable.getTableView().getColumnGroupHeaderConstructor();
    headerConstr.constructHeader(scrollTable);

  }
  
  public void dispose(){
    scrollTable = null;
    fixedTable  = null; 
  }
  
  public void splitTableAccordingToColmnsFreezed(){
  	FAbstractTableModel scrollModel = (FAbstractTableModel)scrollTable.getModel();
  	if(scrollModel != null){
  		FTableView tableview = scrollModel.getTableView();
  		if(tableview.getColumnsToFreeze() > 0){
	  		splitTableAccordingToColmnsFreezed(scrollModel);
	  	}
  	}
  }
  
  private void initDrop(DropTargetListener dropTargetListener){
  	new DropTarget(this, DnDConstants.ACTION_COPY, dropTargetListener, true);
  	if(scrollTable != null){
  		scrollTable.setDropable(dropTargetListener);
  	}
  	if(fixedTable != null){
  		fixedTable.setDropable(dropTargetListener);
  	}
  }

  private void putTableInScroll() {
    setViewportView(this.scrollTable);
    
    /*setLayout(new ScrollPaneLayout.UIResource());
    setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
    setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
    setViewport(createViewport());
    setVerticalScrollBar(createVerticalScrollBar());
    setHorizontalScrollBar(createHorizontalScrollBar());
    setViewportView(this.scrollTable);
    setOpaque(true);
    updateUI();

    if (!this.getComponentOrientation().isLeftToRight()) {
      viewport.setViewPosition(new Point(Integer.MAX_VALUE, 0));
    }*/
  }

  private void splitTableAccordingToColmnsFreezed(FAbstractTableModel scrollModel){
    FTableView tableview        = scrollModel.getTableView();
    int        numFrozenColumns = tableview.getColumnsToFreeze();
    
    FAbstractTableModel fixedModel = null;
    try{
      fixedModel = (FAbstractTableModel)scrollModel.clone();
    }catch(CloneNotSupportedException e){
      e.printStackTrace();
    }
    
    this.fixedTable = new FTable(fixedModel, true, true);
    this.fixedTable.setScrollpane(this);
    
    FColumnGroupHeaderConstructor headerConstr = fixedTable.getTableView().getColumnGroupHeaderConstructor();
    if(headerConstr.getColumnGroupFatherArraySize() > 0){
      headerConstr.constructHeader(fixedTable.getScrollPane().getFixedTable());
    }
    
    fixedTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    scrollTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    setViewportView(scrollTable);
    
    //This is an artifact to show a gray line between the fixed and the scrollable panes
    FocLineBorder border = new FocLineBorder(Color.GRAY, 1);
    border.setLeft(true);
    border.setRight(false);
    border.setTop(false);
    border.setBottom(false);
    setViewportBorder(border);

    //ICI
    JViewport viewport_FixedTable = new JViewport();
    viewport_FixedTable.setView(fixedTable);
    setRowHeader(viewport_FixedTable);
    //setRowHeaderView(fixedTable);
    
    JTableHeader lockedHeader = fixedTable.getTableHeader();
    fixedTable.setFocusable(false);
    lockedHeader.setReorderingAllowed(false);
    lockedHeader.setResizingAllowed(false);
    setCorner(JScrollPane.UPPER_LEFT_CORNER, lockedHeader);
    
    fixedTable.setSelectionModel(scrollTable.getSelectionModel());
    
    fixedTable.getTableHeader().setReorderingAllowed(false);
    fixedTable.getTableHeader().setResizingAllowed(false);
    fixedTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
    scrollTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
    
    FAbstractTableModel  scrollTableModel = (FAbstractTableModel)scrollTable.getModel();
    FTableView scrollTableView = scrollTableModel.getTableView();
    if(scrollTableView != null){
      for(int i = 0; i < numFrozenColumns; i++){
        int col = scrollTableView.getVisibleColumnIndex(i);
        FTableColumn fTableColumn = scrollTableView.getColumnAt(col);
        fTableColumn.setShow(false);
      }
      scrollTableView.adjustColumnVisibility();
    }
    
    FAbstractTableModel fixedTableModel = (FAbstractTableModel)fixedTable.getModel();
    FTableView fixedTableView = fixedTableModel.getTableView();
    if(fixedTableView != null){
      for(int i = fixedTableView.getColumnCount()-1; i >= numFrozenColumns; i--){
        //int col = fixedTableView.getVisibleColumnIndex(i);
        FTableColumn fTableColumn = fixedTableView.getColumnAt(i/*col*/);
        fTableColumn.setShow(false);
        if(fTableColumn.getColumnRepresentation() != null){
        	fTableColumn.getColumnRepresentation().setShow(false);
        }
      }
      fixedTableView.adjustColumnVisibility();
    }

    fixedTable.setPreferredScrollableViewportSize(fixedTable.getPreferredSize());
  }

  public void refreshRowHeaderSize(){
  	if(getFixedTable() != null){
  		getFixedTable().setShouldRecomputePreferredScrollableViewPortSize(true);
		  getRowHeader().setPreferredSize(getFixedTable().getPreferredScrollableViewportSize());
		  
			//doLayout();
			repaint();
  	}
  }
  
  public FTable getScrollTable() {
    return scrollTable;
  }

  public FTable getFixedTable() {
    return fixedTable;
  }
  
  public void setDropable(DropTargetListener dropTargetListener){
  	initDrop(dropTargetListener);
  }

	public boolean drop(FocTransferable focTransferable, DropTargetDropEvent dtde) {
		return true;
	}

	public void fillDropInfo(FocTransferable focTransferable, DropTargetDropEvent dtde) {
	}

	public boolean shouldExecuteDrop(FocTransferable focTransferable, DropTargetDropEvent dtde) {
		return true;
	}

	/*
  private Dimension dim = new Dimension();
  
  @Override
  public Dimension getPreferredSize(){
  	dim.height = 0;
  	dim.width  = 0;
  	if(fixedTable != null){
  		dim.height = dim.height + fixedTable.getPreferredSize().height;
  		dim.width  = dim.width + fixedTable.getPreferredSize().width;
  	}
  	if(scrollTable != null){
  		dim.height = dim.height + scrollTable.getPreferredSize().height;
  		dim.width  = dim.width + scrollTable.getPreferredSize().width;
  	}
  	return dim;
  }
  */
}
