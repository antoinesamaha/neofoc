/*
 * Created on 24-Apr-2005
 */
package com.foc.gui.table.cellControler;

import javax.swing.table.*;

import com.foc.gui.table.FTable;
import com.foc.gui.table.cellControler.renderer.gantChartActivityRenderer.FGanttChartActivityCellRenderer;
import com.foc.gui.table.cellControler.renderer.gantChartActivityRenderer.FGanttChartActivityColumnHeaderRenderer;
import com.foc.gui.table.cellControler.renderer.gantChartRenderer.BasicGanttScale;

/**
 * @author 01Barmaja
 */
public class GanttChartActivityCellControler extends AbstractCellControler{
  
  private FGanttChartActivityCellRenderer renderer = null;//To allow editable not editable appearence
  private FGanttChartActivityColumnHeaderRenderer columnHeaderRenderer = null;
  
  public GanttChartActivityCellControler(BasicGanttScale gantScale){
  	renderer = new FGanttChartActivityCellRenderer(gantScale);
  	columnHeaderRenderer = new FGanttChartActivityColumnHeaderRenderer(gantScale);
  }
  	
  /* (non-Javadoc)
   * @see b01.foc.gui.table.cellControler.AbstractCellControler#dispose()
   */
  public void dispose() {
    if(renderer != null){
      renderer.dispose();
      renderer = null;
    }
    if(columnHeaderRenderer != null){
    	columnHeaderRenderer.dispose();
    	columnHeaderRenderer = null;
    }
  }
  
  /* (non-Javadoc)
   * @see b01.foc.gui.table.editor.CellEditorInterface#getEditor()
   */
  public TableCellEditor getEditor() {
    return null;
  }
  
  /* (non-Javadoc)
   * @see b01.foc.gui.table.editor.CellEditorInterface#getRenderer()
   */
  public TableCellRenderer getRenderer() {
    return renderer;
  }
  
  @Override
	public TableCellRenderer getColumnHeaderRenderer() {
		return columnHeaderRenderer;
	}

  public int getRendererSupplementSize(){
    return 0;
  }
  
  public void editRequested(FTable table, int row, int col){
  }
}
