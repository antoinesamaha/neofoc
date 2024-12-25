/*
 * Created on 24-Apr-2005
 */
package com.foc.gui.table.cellControler;

import javax.swing.table.*;

import com.foc.gui.FPanel;
import com.foc.gui.table.FTable;
import com.foc.gui.table.cellControler.editor.AttributeLocationCellEditor;
import com.foc.gui.table.cellControler.editor.FTableObjectCellEditor;
import com.foc.gui.table.cellControler.renderer.FComboBoxRenderer;

/**
 * @author 01Barmaja
 */
public class AttributeLocationCellControler extends AbstractCellControler{
  private AttributeLocationCellEditor editor = null; 
  private FComboBoxRenderer renderer = null;
  
  public AttributeLocationCellControler(){
    editor = new AttributeLocationCellEditor(this);
    renderer = new FComboBoxRenderer();
  }
  
  /* (non-Javadoc)
   * @see com.foc.gui.table.editor.CellEditorInterface#getEditor()
   */
  public TableCellEditor getEditor() {
    return editor;
  }
  
  /* (non-Javadoc)
   * @see com.foc.gui.table.editor.CellEditorInterface#getRenderer()
   */
  public TableCellRenderer getRenderer() {
    return renderer;
  }
  
  @Override
	public TableCellRenderer getColumnHeaderRenderer() {
		return null;
	}  
  
  public int getRendererSupplementSize(){
    return 2;
  }
  
  public void editRequested(FTable table, int row, int col){
    if(table != null && row >= 0 && col >= 0){
      
      editor.startCellEditing(table, row, col);
      /*
      if (table.editCellAt(row, col)){
        table.getEditorComponent().requestFocusInWindow();
      }
      */
      
    }
  }
  
  /* (non-Javadoc)
   * @see b01.foc.gui.table.cellControler.AbstractCellControler#dispose()
   */
  public void dispose() {
    if(editor != null){
      editor.dispose();
      editor = null;
    }
    if(renderer != null){
      renderer.dispose();
      renderer = null;
    }
  }

}
