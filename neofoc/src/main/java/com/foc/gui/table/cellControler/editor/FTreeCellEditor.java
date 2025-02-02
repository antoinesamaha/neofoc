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
package com.foc.gui.table.cellControler.editor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import com.foc.Globals;
import com.foc.desc.field.FField;
import com.foc.gui.DisplayManager;
import com.foc.gui.table.FTable;
import com.foc.gui.table.FTableColumn;
import com.foc.gui.treeTable.FGTreeInTable;
import com.foc.gui.treeTable.FTreeTableModel;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;
import com.foc.tree.FNode;
import com.foc.tree.FTree;

/**
 * @author 01Barmaja
 */

/**
 * An editor that can be used to edit the tree column. This extends
 * DefaultCellEditor and uses a JTextField (actually, TreeTableTextField)
 * to perform the actual editing.
 * <p>To support editing of the tree column we can not make the tree
 * editable. The reason this doesn't work is that you can not use
 * the same component for editing and renderering. The table may have
 * the need to paint cells, while a cell is being edited. If the same
 * component were used for the rendering and editing the component would
 * be moved around, and the contents would change. When editing, this
 * is undesirable, the contents of the text field must stay the same,
 * including the caret blinking, and selections persisting. For this
 * reason the editing is done via a TableCellEditor.
 * <p>Another interesting thing to be aware of is how tree positions
 * its render and editor. The render/editor is responsible for drawing the
 * icon indicating the type of node (leaf, branch...). The tree is
 * responsible for drawing any other indicators, perhaps an additional
 * +/- sign, or lines connecting the various nodes. So, the renderer
 * is positioned based on depth. On the other hand, table always makes
 * its editor fill the contents of the cell. To get the allusion
 * that the table cell editor is part of the tree, we don't want the
 * table cell editor to fill the cell bounds. We want it to be placed
 * in the same manner as tree places it editor, and have table message
 * the tree to paint any decorations the tree wants. Then, we would
 * only have to worry about the editing part. The approach taken
 * here is to determine where tree would place the editor, and to override
 * the <code>reshape</code> method in the JTextField component to
 * nudge the textfield to the location tree would place it. Since
 * JTreeTable will paint the tree behind the editor everything should
 * just work. So, that is what we are doing here. Determining of
 * the icon position will only work if the TreeCellRenderer is
 * an instance of DefaultTreeCellRenderer. If you need custom
 * TreeCellRenderers, that don't descend from DefaultTreeCellRenderer, 
 * and you want to support editing in JTreeTable, you will have
 * to do something similiar.
 */

@SuppressWarnings("serial")
public class FTreeCellEditor extends DefaultCellEditor implements CellEditorListener {
                                                       
  private FTable table = null;
  private FProperty specialNodeEditorProperty = null;
  private FProperty codeProperty = null;
  private FPropertyListener nodeEditorPropertyListener = null;
  private Border    editorBorder = BorderFactory.createLineBorder(Color.BLACK, 0);
  
  public FTreeCellEditor() {
    super(new JTextField());
    addCellEditorListener(this);
  }
  
  public FTreeCellEditor(FTable table) {
    this();
    this.table = table;
  }
  
  public void dispose() {
    table = null;
    unPlugPropertyListener(true);
  }
  
  private void unPlugPropertyListener(boolean withDispose){
    if( specialNodeEditorProperty != null ){
      specialNodeEditorProperty.removeListener(nodeEditorPropertyListener);
    }
    
    if( codeProperty != null ){
      codeProperty.removeListener(nodeEditorPropertyListener);
    }
    
    if( withDispose ){
      specialNodeEditorProperty = null;
      codeProperty = null;
      nodeEditorPropertyListener = null;  
    }
  }
  
  public void setTable(FTable table) {
    this.table = table;
  }
  
  private FTreeTableModel getTreeTableModel() {
    return (FTreeTableModel) table.getModel();
  }
  
  @SuppressWarnings("serial")
  /*static class TreeTableTextField extends JTextField {
    public int offset;
    
    public void setBounds(int x, int y, int w, int h) {
      int newX = Math.max(x, offset);
      super.setBounds(newX, y, w - (newX - x), h);
    }
  }*/
  /*
  static class TreeTableTextField extends JPanel {
    public int offset;
    private GridBagConstraints constr = null;
    
    public TreeTableTextField(){
      setLayout(new GridBagLayout());
      constr = new GridBagConstraints();
      constr.gridwidth = 1;
      constr.gridheight = 1;
      constr.insets.bottom = 0;
      constr.insets.top = 0;
      constr.insets.left = 0;
      constr.insets.right = 0;
      constr.weightx = 0.99;
      constr.weighty = 0.99;
      constr.fill = GridBagConstraints.BOTH;
      constr.anchor = GridBagConstraints.WEST;
      constr.gridx = 0;
      constr.gridy = 0;
    }
    
    public void setBounds(int x, int y, int w, int h) {
      int newX = Math.max(x, offset);
      super.setBounds(newX, y, w - (newX - x), h);
    }
    
    public void addComponent(Component codeComp, Component comp){
    	constr.gridx = 0;
    	constr.weightx = 0.99;
    	if(codeComp != null){
    		constr.weightx = 0.25;
    		add(codeComp, constr);
    		constr.gridx++;
    		constr.weightx = 0.75;
    	}
      add(comp, constr);
    }
  }
  */
    
  /**
   * Overridden to determine an offset that tree would place the
   * editor at. The offset is determined from the
   * <code>getRowBounds</code> JTree method, and additionally
   * from the icon DefaultTreeCellRenderer will use.
   * <p>The offset is then set on the TreeTableTextField component
   * created in the constructor, and returned.
   */
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int c) {
    
    super.getTableCellEditorComponent(table, value, isSelected, r, c);
    JTree tree = getTreeTableModel().getGuiTree();
    JTree t = getTreeTableModel().getGuiTree();
       
    boolean rv = t.isRootVisible();
    int offsetRow = rv ? r : r - 1;
    Rectangle bounds = t.getRowBounds(offsetRow);
    int offset = bounds != null ? bounds.x : 0;
    TreeCellRenderer tcr = t.getCellRenderer();
    if (tcr instanceof DefaultTreeCellRenderer) {
      Object node = t.getPathForRow(offsetRow).getLastPathComponent();
      Icon icon;
      if (t.getModel().isLeaf(node))
        icon = ((DefaultTreeCellRenderer) tcr).getLeafIcon();
      else if (tree.isExpanded(offsetRow))
        icon = ((DefaultTreeCellRenderer) tcr).getOpenIcon();
      else
        icon = ((DefaultTreeCellRenderer) tcr).getClosedIcon();
      if (icon != null) {
        offset += ((DefaultTreeCellRenderer) tcr).getIconTextGap() + icon.getIconWidth();
      }
    }
    
    TreeTableTextField treeTableTextField = null;
    
    if(isNodeEditable(r)){
      
      FTree fTree = getTreeTableModel().getTree();
      FNode node = getTreeTableModel().getNodeForRow(r);
      specialNodeEditorProperty = fTree.getTreeSpecialProperty(node);

      if(specialNodeEditorProperty != null){
        treeTableTextField = new TreeTableTextField();
        treeTableTextField.offset = offset;
       	//BAntoineS - CODE EDITING
        /*Desactivate CODE EDITING
        FocObject focObj = (FocObject) node.getObject();
    		codeProperty = focObj.getFocProperty(fTree.getDisplayCodeFieldID());
    		JComponent codeComp = (JComponent) (codeProperty != null ? codeProperty.getGuiComponent() : null);
    		*/
        JComponent codeComp = null;
        JComponent titleComp = (JComponent) specialNodeEditorProperty.getGuiComponent();
        
        if(codeComp != null && titleComp != null){
          if(codeComp.getClass() != titleComp.getClass()){
            Dimension d = (Dimension) titleComp.getPreferredSize().clone();
            d.width /= 2;
            codeComp.setMinimumSize(d);  
            titleComp.setMaximumSize((Dimension) titleComp.getPreferredSize().clone());  
          }
        }

        if(Globals.getDisplayManager().getLookAndFeel() == DisplayManager.LOOK_AND_FEEL_NIMBUS){
        	if(codeComp != null){
        		codeComp.setBorder(null);
        	}
        	if(titleComp != null){
        		titleComp.setBorder(null);
        	}        	
        }
        
        treeTableTextField.addComponent(codeComp, titleComp);
      	titleComp.requestFocusInWindow();
        //EAntoineS
        
        //treeTableTextField.addComponent(specialNodeEditorProperty.getGuiComponent());
        /*the DefaultEditor handles cell editing through a listener called delegate, and since we r not adding
         * delegate to our gui components(foc), we had to manage cell editing ourselves by adding a property listener
         * to inform the table that we finished editing. we also need to update the delegate value with the new
         * edited value so that when calling fireEditingStopped() method we dont set back the old value.
         */
        
        if( codeProperty != null ){
          codeProperty.addListener(getNodeEditorPropertyListener());
        }
        
        FPropertyListener nodeEditorPropertyListener = getNodeEditorPropertyListener();
        specialNodeEditorProperty.addListener(nodeEditorPropertyListener);
      }
    }
    
    if(Globals.getDisplayManager().getLookAndFeel() == DisplayManager.LOOK_AND_FEEL_NIMBUS){
    	/*
    	Border border = UIManager.getBorder("Tree.cellNoFocusBorder");
      if (border == null) {
          border = editorBorder;
      } else {
          // use compound with LAF to reduce "jump" text when starting edits
          border = BorderFactory.createCompoundBorder(editorBorder, border);
      }
      treeTableTextField.setBorder(border);
      */
    	if(treeTableTextField != null){
    		treeTableTextField.setBorder(null);
    	}
    }
    
    return treeTableTextField;
  }
  
  private FPropertyListener getNodeEditorPropertyListener(){
    if( nodeEditorPropertyListener == null ){
      nodeEditorPropertyListener = new FPropertyListener(){
        public void dispose() {
        }

        public void propertyModified(FProperty property) {
          delegate.setValue(property.getString());
          stopCellEditing();         
        }
      };
    }
    return nodeEditorPropertyListener;
  }
  
  private boolean isNodeEditable(int row) {
    FTableColumn fCol = ((FTreeTableModel) table.getModel()).getTableView().getColumnById(FField.TREE_FIELD_ID);
    //int row = this.table.getCellCoordinatesForMouseCurrentPosition().y;
    boolean rootNode = (getTreeTableModel().getNodeForRow(row) == getTreeTableModel().getTree().getRoot());
    FTree fTree = getTreeTableModel().getTree();
    boolean nodeLocked = fTree.isNodeLocked(getTreeTableModel().getNodeForRow(row));
    
    
    return !rootNode && !nodeLocked && fCol.getEditable();
  }
  
  /**
   * This is overridden to forward the event to the tree. This will
   * return true if the click count greater or equal to 3, or the event is null.
   */
  
  public boolean isCellEditable(EventObject e) {
    if (e instanceof MouseEvent) {
      FGTreeInTable gTree = getTreeTableModel().getGuiTree();
      MouseEvent me = (MouseEvent) e;
      FTableColumn fCol = ((FTreeTableModel) table.getModel()).getTableView().getColumnById(FField.TREE_FIELD_ID);
      if (fCol != null) {
        // If the modifiers are not 0 (or the left mouse button),
        // tree may try and toggle the selection, and table
        // will then try and toggle, resulting in the
        // selection remaining the same. To avoid this, we
        // only dispatch when the modifiers are 0 (or the left mouse
        // button).
        if (me.getModifiers() == 0 || me.getModifiers() == InputEvent.BUTTON1_MASK) {
        	//B-BugOfCollapseThatDoesNotWork
        	//This is due to the fact that the scrollTable does not contain the column
        	//Thus the rectangle.x < 0.
        	//We are working it arround by getting the exact table in which this column is.
        	FTable       tempTable = table.getScrollPane().getScrollTable();
        	FTableColumn tempFCol  = fCol;
        	if(!tempTable.getTableView().getColumnById(fCol.getID()).isVisible()){
        		tempTable = table.getScrollPane().getFixedTable();
        		tempFCol  = tempTable.getTableView().getColumnById(fCol.getID());
        	}
        	//E-BugOfCollapseThatDoesNotWork
          MouseEvent newME = new MouseEvent(gTree, me.getID(), me.getWhen(), me.getModifiers(), me.getX() - tempTable.getCellRect(0, tempFCol.getOrderInView(), true).x, me.getY(), me.getClickCount(), me.isPopupTrigger());
          gTree.dispatchEvent(newME);
        }
      }
      if (me.getClickCount() >= 2) {
        return true;
      }
      return false;
    }
    if (e == null) {
      return true;
    }
    return false;
  }

  public void editingCanceled(ChangeEvent e) {
    unPlugPropertyListener(false);
  }

  public void editingStopped(ChangeEvent e) {
    unPlugPropertyListener(false);
    SwingUtilities.invokeLater(new Runnable(){
			public void run() {
		    if(table != null){
		    	table.autoResizeColumns();
		    	((FTreeTableModel)table.getModel()).refreshGui();
		    	com.foc.Globals.getDisplayManager().violentRefresh();
		    }
			}
    });
  }
}
