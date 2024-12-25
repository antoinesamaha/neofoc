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
package com.foc.gui.table.cellControler.renderer;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import com.foc.gui.tree.FGTree;
import com.foc.gui.treeTable.FGTreeInTable;
import com.foc.gui.treeTable.FTreeTableModel;

@SuppressWarnings("serial")
public class CopyOfFTreeCellRenderer implements TableCellRenderer {
	
	/** Last table/tree row asked to renderer. */
	protected int visibleRow;
  private FTreeTableModel treeTableModel = null;
	
	public CopyOfFTreeCellRenderer(FTreeTableModel treeTableModel){
    setTreeTableModel(treeTableModel);
	}
  
  public void dispose(){
    treeTableModel = null;
  }
    
  private FGTree getFGTree(){
    return (treeTableModel != null) ? treeTableModel.getGuiTree(): null; 
  }
	
  public void setTreeTableModel(FTreeTableModel treeTableModel){
    this.treeTableModel = treeTableModel;
  }
  
	/**
	 * updateUI is overridden to set the colors of the Tree's renderer to match
	 * that of the table.
	 */
	public void updateUI() {
    FGTree gTree = getFGTree(); 
    if(gTree != null){
      gTree.updateUI();
  		// Make the tree's cell renderer use the table's cell selection
  		// colors.
  		TreeCellRenderer tcr = gTree.getCellRenderer();
  		if (tcr instanceof DefaultTreeCellRenderer) {
  			DefaultTreeCellRenderer dtcr = ((DefaultTreeCellRenderer) tcr);
  			// For 1.1 uncomment this, 1.2 has a bug that will cause an
  			// exception to be thrown if the border selection color is
  			// null.
  			// dtcr.setBorderSelectionColor(null);
  			dtcr.setTextSelectionColor(UIManager
  					.getColor("Table.selectionForeground"));
  			dtcr.setBackgroundSelectionColor(UIManager
  					.getColor("Table.selectionBackground"));
  		}
    }
	}

	/**
	 * Sets the row height of the tree, and forwards the row height to the
	 * table.
	 */
	public void setRowHeight(int rowHeight) {
    FGTree gTree = getFGTree(); 
    if(gTree != null){
  		if (rowHeight > 0) {
        gTree.setRowHeight(rowHeight);
  			if (gTree.getRowHeight() != rowHeight) {
  				setRowHeight(gTree.getRowHeight());
  			}
  		}
    }
	}

	/**
	 * This is overridden to set the height to match that of the JTable.
	 */
	public void setBounds(int x, int y, int w, int h) {
    FGTree gTree = getFGTree(); 
    if(gTree != null){
      gTree.setBounds(x, 0, w, /*FGTreeInTable.this.getHeight()*/gTree.getRowHeight());
    }
		//super.setBounds(x, 0, w, 18);
	}

	/**
	 * Sublcassed to translate the graphics such that the last visible row will
	 * be drawn at 0,0.
	 */
/*	public void paint(Graphics g) {
    FGTree gTree = getFGTree();
    if(gTree != null){
      b01.foc.Globals.logString(" GTREE GET ROW HEIGHT = "+gTree.getRowHeight());
      g.translate(0, -visibleRow * gTree.getRowHeight());
      //gTree.paint(g, visibleRow);
    }else{
      b01.foc.Globals.logString(" GTREE GET ROW HEIGHT = NULL");
    }
	}
*/
	/**
	 * TreeCellRenderer method. Overridden to update the visible row.
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    FGTreeInTable gTree = (FGTreeInTable) getFGTree(); 
    if(gTree != null){
  		//FDefaultCellRenderer.setCellColorStatic(this, table, value, isSelected, hasFocus, row, column);
  		if (isSelected)
  			gTree.setBackground(table.getSelectionBackground());
  		else
        gTree.setBackground(table.getBackground());
  		
      gTree.setVisibleRow(row);
    }
		return gTree;
	}
  
}
