/*
 * Created on 24-Apr-2005
 */
package com.foc.gui.table.cellControler;

import javax.swing.table.*;

import com.foc.gui.table.FTable;
import com.foc.gui.table.cellControler.editor.FDateCellEditor;
import com.foc.gui.table.cellControler.renderer.FDateCellRenderer;
import com.foc.gui.table.cellControler.renderer.FDefaultCellRenderer;

/**
 * @author 01Barmaja
 */
public class DateCellControler extends AbstractCellControler{
  
  private FDateCellEditor editor = null;//To allow beater all selection when get focus by mouse 
  private FDefaultCellRenderer renderer = null;//To allow editable not editable appearence
  
  public DateCellControler(){
    editor = new FDateCellEditor();
    renderer = new FDateCellRenderer();//FDateRendere
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
    //We don't need to react to this. 
    //By default when it is a text field the cell is edited when key is typed
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
