/*
 * Created on 24-Apr-2005
 */
package com.foc.gui.table.cellControler;

import javax.swing.JComponent;
import javax.swing.table.*;

import com.foc.gui.DisplayManager;
import com.foc.gui.table.*;
import com.foc.gui.table.cellControler.editor.InheritanceCellEditor;
import com.foc.gui.table.cellControler.renderer.InheritanceCellRenderer;

/**
 * @author 01Barmaja
 */
public class InheritanceCellControler extends AbstractCellControler{
  private InheritanceCellEditor   editor   = null;//To allow beater all selection when get focus by mouse 
  private InheritanceCellRenderer renderer = null;//To allow editable not editable appearence

  private void InheritanceCellControler(JComponent checkBox){
    //editor = new InheritanceCellEditor(checkBox);
    editor.setClickCountToStart(DisplayManager.NBR_OF_CLICKS);
    renderer = new InheritanceCellRenderer();
  }
  
  /* (non-Javadoc)
   * @see b01.foc.gui.table.editor.CellEditorInterface#getEditor()
   */
  public TableCellEditor getEditor() {
    return editor;
  }
  
  /* (non-Javadoc)
   * @see b01.foc.gui.table.editor.CellEditorInterface#getRenderer()
   */
  public TableCellRenderer getRenderer() {
    return renderer;
  }
  
	@Override
	public TableCellRenderer getColumnHeaderRenderer() {
		return null;
	}
  
  public int getRendererSupplementSize(){
    return 0;
  }
  
  public void editRequested(FTable table, int row, int col){
    if(table != null && row >= 0 && col >= 0){
      if (table.editCellAt(row, col)){
        table.getEditorComponent().requestFocusInWindow();
      }
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
