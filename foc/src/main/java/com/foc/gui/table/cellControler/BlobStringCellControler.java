/*
 * Created on 24-Apr-2005
 */
package com.foc.gui.table.cellControler;

import javax.swing.table.*;

import com.foc.gui.FGTextArea;
import com.foc.gui.table.FTable;
import com.foc.gui.table.cellControler.editor.FBlobStringCellEditor;
import com.foc.gui.table.cellControler.renderer.FBlobStringCellRenderer;

/**
 * @author 01Barmaja
 */
public class BlobStringCellControler extends AbstractCellControler{
  
  private FBlobStringCellEditor   editor   = null;//To allow beater all selection when get focus by mouse 
  private FBlobStringCellRenderer renderer = null;//To allow editable not editable appearence
  
  public BlobStringCellControler(FGTextArea textAreaEditor, FGTextArea textAreaRenderer){
    editor   = new FBlobStringCellEditor(textAreaEditor);
    renderer = new FBlobStringCellRenderer(textAreaRenderer);
  }
  
  public void setRowCol(int row, int col){
  	if(editor != null){
  		editor.getTextArea().setRows(row);
  		editor.getTextArea().setColumns(col);
  	}
  	if(renderer != null){
  		renderer.getTextArea().setRows(row);
  		renderer.getTextArea().setColumns(col);
  	}
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
