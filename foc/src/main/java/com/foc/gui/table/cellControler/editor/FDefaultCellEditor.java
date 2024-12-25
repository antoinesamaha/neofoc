/*
 * Created on 15 fevr. 2004
 */
package com.foc.gui.table.cellControler.editor;

import java.awt.Color;
import java.awt.Component;
import javax.swing.*;
import javax.swing.border.Border;

import com.foc.Globals;
import com.foc.gui.DisplayManager;
import com.foc.gui.table.FTable;

import java.awt.event.*;
import java.util.EventObject;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FDefaultCellEditor extends DefaultCellEditor implements FocusListener {
  private JTextField tf;
  private JTable     table = null;
  private int        col   = -1;
  private int        row   = -1;
  private Border     editorBorder = BorderFactory.createLineBorder(Color.BLACK);
  
  public FDefaultCellEditor(JTextField tf) {
    super(tf);
    this.tf = tf;
    tf.addFocusListener(this);
    super.setClickCountToStart(2);
  }

  public void dispose(){
    tf.removeFocusListener(this);
    tf = null;
    this.table = null;
  }
  
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    Component comp = null;
    this.col   = column;
    this.row   = row;
    this.table = table;
    if(!isSelected){
      stopCellEditing();
      return null;
    }
    FTable t = (FTable)table;
    if(t.requestToEditCell() && tf != null && value != null ){
      tf.setText(value.toString());
      comp = tf;
    }
    
    if(Globals.getDisplayManager().getLookAndFeel() == DisplayManager.LOOK_AND_FEEL_NIMBUS){
	    Border border = UIManager.getBorder("Table.cellNoFocusBorder");
	    if (border == null) {
	        border = editorBorder;
	    } else {
	        // use compound with LAF to reduce "jump" text when starting edits
	        border = BorderFactory.createCompoundBorder(editorBorder, border);
	    }
	    ((JComponent)comp).setBorder(border);
    }
    
    /*
    if(comp != null){
	    //Let ENTER behave like the TAB
	    String actionValue = (String) ((JComponent)comp).getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).get(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));
	    ((JComponent)comp).getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), actionValue);
	    //-----------------------------
    }
    */
    
    return comp;    
  }

  //ATTENTION ATTENTION
  /*
  public boolean isCellEditable(EventObject anEvent) {
    //return super.isCellEditable(anEvent);
    boolean ret = super.isCellEditable(anEvent);
    if (anEvent instanceof MouseEvent) {
      MouseEvent evt = (MouseEvent) anEvent;
      if (evt.getClickCount() == 2) {
        b01.foc.Globals.getDisplayManager().popupMessage("It works !!");
        ret = false;
      }
    }
    return ret;
  }
  */  
  
  public boolean shouldSelectCell(EventObject anEvent) {
    boolean toti = false;
    //b01.foc.Globals.logString("Should SELECT CELL");
    if(anEvent.getClass() == KeyEvent.class){
      KeyEvent ke = (KeyEvent)anEvent;
      
      if(ke.getKeyCode() != KeyEvent.VK_INSERT && ke.getKeyCode() != KeyEvent.VK_DELETE){
        toti = super.shouldSelectCell(anEvent);
        if(toti){
          tf.selectAll();
        }
      }
    }
    return toti;
  }
    
  public void focusGained(FocusEvent e) {
    //b01.foc.Globals.logString("CELL FOCUS GAINED");
    tf.selectAll();
  }

  public void focusLost(FocusEvent e) {
  	if(this.table instanceof FTable){
  		FTable fTable = (FTable) getTable();
  		fTable.requestFocusInWindow();

  		SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
		  		FTable fTable = (FTable) getTable();
		  		//fTable.requestFocusInWindow();
		   		fTable.setRowSelectionInterval(getRow(), getRow());
		  		fTable.setColumnSelectionInterval(getCol(), getCol());
				}
			});
  	}
  }

	public JTable getTable() {
		return table;
	}

	public int getCol() {
		return col;
	}

	public int getRow() {
		return row;
	}
}
