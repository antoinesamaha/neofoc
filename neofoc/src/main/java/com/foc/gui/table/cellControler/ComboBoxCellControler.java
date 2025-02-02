/*
 * Created on 24-Apr-2005
 */
package com.foc.gui.table.cellControler;

import java.util.Iterator;
import javax.swing.table.*;

import com.foc.gui.*;
import com.foc.gui.table.*;
import com.foc.gui.table.cellControler.editor.FDefaultComboBoxCellEditor;
import com.foc.gui.table.cellControler.renderer.FComboBoxRenderer;

/**
 * @author 01Barmaja
 */
public class ComboBoxCellControler extends AbstractCellControler{
  private FDefaultComboBoxCellEditor editor   = null;//To allow beater all selection when get focus by mouse 
  private FComboBoxRenderer          renderer = null;//To allow editable not editable appearence

  private void init(FGAbstractComboBox combo){
    editor = new FDefaultComboBoxCellEditor(combo);
    editor.setClickCountToStart(DisplayManager.NBR_OF_CLICKS);
    renderer = new FComboBoxRenderer();
  }
  
  public ComboBoxCellControler(Iterator choices, boolean sort){
    FGComboBox combo = new FGComboBox(choices, sort);
    init(combo);
  }

  public ComboBoxCellControler(int fieldID){
    FGObjectComboBox combo = new FGObjectComboBox(fieldID, null, false, true);
    init(combo);
  }

  public ComboBoxCellControler(int fieldID, FTableView tableView){
    FGObjectComboBox combo = new FGObjectComboBox(fieldID, tableView, false, true);
    init(combo);
  }

  public ComboBoxCellControler(int fieldID, FTableView tableView, boolean browsePopup){
    FGObjectComboBox combo = new FGObjectComboBox(fieldID, tableView, browsePopup, true);
    init(combo);
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
    return 2;
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
