/*
 * Created on 15 fevr. 2004
 */
package com.foc.gui.table.cellControler.editor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import com.foc.desc.field.FField;
import com.foc.gui.table.FTable;
import com.foc.gui.table.FTableColumn;
import com.foc.gui.treeTable.FGTreeInTable;
import com.foc.gui.treeTable.FTreeTableModel;

/**
 * @author 01Barmaja
 */
public class CopyOfFTreeCellEditor extends ZAbstractCellEditor implements TableCellEditor {
	private FTable table = null;	
	
  public CopyOfFTreeCellEditor(FTable table) {
    this.table = table;
	}

	public void dispose() {
		table = null;
	}

	public void setTable(FTable table){
		this.table = table;
	}

  private FTreeTableModel getTreeTableModel(){
    return (FTreeTableModel) table.getModel();
  }
  
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int c) {
		return getTreeTableModel().getGuiTree();
	}
  
  /**
	 * Overridden to return false, and if the event is a mouse event it is
	 * forwarded to the tree.
	 * <p>
	 * The behavior for this is debatable, and should really be offered as a
	 * property. By returning false, all keyboard actions are implemented in terms
	 * of the table. By returning true, the tree would get a chance to do
	 * something with the keyboard events. For the most part this is ok. But for
	 * certain keys, such as left/right, the tree will expand/collapse where as
	 * the table focus should really move to a different column. Page up/down
	 * should also be implemented in terms of the table. By returning false this
	 * also has the added benefit that clicking outside of the bounds of the tree
	 * node, but still in the tree column will select the row, whereas if this
	 * returned true that wouldn't be the case.
	 * <p>
	 * By returning false we are also enforcing the policy that the tree will
	 * never be editable (at least by a key sequence).
	 */
	public boolean isCellEditable(EventObject e) {
    boolean editable = false;
		if (e instanceof MouseEvent) {
      FGTreeInTable gTree = getTreeTableModel().getGuiTree();
			//for (int counter = model.getColumnCount() - 1; counter >= 0; counter--) {
			//	if (model.getColumnClass(counter) == TreeTableModel.class) {
			FTableColumn fCol = ((FTreeTableModel)table.getModel()).getTableView().getColumnById(FField.TREE_FIELD_ID);
			if(fCol != null){
        editable = fCol.getEditable();
				MouseEvent me = (MouseEvent) e;
				MouseEvent newME = new MouseEvent(gTree, me.getID(), me.getWhen(), me.getModifiers(), me.getX() - table.getCellRect(0, fCol.getOrderInView(), true).x, me.getY(), me.getClickCount(), me.isPopupTrigger());
				gTree.dispatchEvent(newME);
			}
			//	break;
			//	}
			//}
		}
		return false;
	}
}
