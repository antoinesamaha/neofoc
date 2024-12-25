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
package com.foc.gui.treeTable;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.foc.Globals;
import com.foc.gui.DisplayManager;
import com.foc.gui.tree.FGTree;
import com.foc.gui.tree.FTreeModel;
import com.foc.tree.FNode;
import com.foc.tree.FTree;

@SuppressWarnings("serial")
public class FGTreeInTable extends FGTree {
	private int wBackup                         = 0;
	private int widthOfTheVerticalLevelColoring = 19;
	
	/** Last table/tree row asked to renderer. */
	private int visibleRow;
	public FGTreeInTable(TreeModel treeModel){
		super(treeModel);
		
		if(Globals.getDisplayManager() != null){
			if(Globals.getDisplayManager().getLookAndFeel() == DisplayManager.LOOK_AND_FEEL_NIMBUS){
				widthOfTheVerticalLevelColoring = 16;
			}
		}
  }
  
  public void dispose(){
    super.dispose();
  }
  
  public void setVisibleRow(int visibleRow){
    this.visibleRow = visibleRow;
  }
	
	/**
	 * updateUI is overridden to set the colors of the Tree's renderer to match
	 * that of the table.
	 */
	public void updateUI() {
    //System.out.println("XX Update UI");
		super.updateUI();
		// Make the tree's cell renderer use the table's cell selection
		// colors.
		TreeCellRenderer tcr = getCellRenderer();
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
			//dtcr.setBorderSelectionColor(Color.RED);
			//dtcr.setBackgroundSelectionColor(Color.RED);
		}
	}

	/**
	 * Sets the row height of the tree, and forwards the row height to the
	 * table.
	 */
	public void setRowHeight(int rowHeight) {
		if (rowHeight > 0) {
			//if(getRowHeight() != rowHeight){
			//	Globals.logString("Passing from "+getRowHeight()+" To "+rowHeight+" "+this.toString());
			//}
			super.setRowHeight(rowHeight);
      //BAntoineS - GUI_TREE_SHIFT
			/*
			if (FGTreeInTable.this != null
					&& FGTreeInTable.this.getRowHeight() != rowHeight) {
				FGTreeInTable.this.setRowHeight(getRowHeight());
			}
			*/
      //EAntoineS - GUI_TREE_SHIFT
		}
	}

	/**
	 * This is overridden to set the height to match that of the JTable.
	 */
	public void setBounds(int x, int y, int w, int h) {
	  //System.out.println("XX SetBounds");
    wBackup = w;
		super.setBounds(x, 0, w, this.getRowHeight());
		//super.setBounds(x, 0, w, 18);
	}

	/**
	 * Sublcassed to translate the graphics such that the last visible row will
	 * be drawn at 0,0.
	 */
  
	public void paint(Graphics g) {
    //Globals.logString("REPAINT OF THE TREE IN TABLE : "+this.getRowHeight()+" "+this.toString());

    TreePath treePath = getPathForRow(visibleRow);
    FNode node = treePath != null ? (FNode) treePath.getLastPathComponent() : null;
    if(node != null){
	    FTree fTree = ((FTreeModel)this.getModel()).getTree();
	    Color color = fTree.getColorForNode(node, visibleRow, this);
	
	    if( color != null ){
	      g.setColor(color);
	      g.fillRect(0, 0, wBackup, this.getRowHeight());  
	    }
	
	    int fullDepth = node.getNodeDepth();
	    int level = 0;
	    if(!fTree.isRootVisible()) level = 1;
	    while ( node != null /*&& !(node instanceof FRootNode)*/ ){
	      //Color marginColor = fTree.getColorForLevel(level);
	      Color marginColor = fTree.getColorForNode(node, 0, null);
	      
	      //if(marginColor != null){
	        g.setColor(marginColor != null ? marginColor : Color.WHITE);
	        g.fillRect((fullDepth-level) * widthOfTheVerticalLevelColoring, 0, widthOfTheVerticalLevelColoring, this.getRowHeight());    
	      //}
	      
	      level++;
	      node = node.getFatherNode();
	    }
    }    
    g.translate(0, -visibleRow * getRowHeight());
		super.paint(g);
	}

	/**
	 * TreeCellRenderer method. Overridden to update the visible row.
	 */
  /*
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		//FDefaultCellRenderer.setCellColorStatic(this, table, value, isSelected, hasFocus, row, column);
		if (isSelected)
			setBackground(table.getSelectionBackground());
		else
			setBackground(table.getBackground());
		
		visibleRow = row;
		return this;
	}*/
}
