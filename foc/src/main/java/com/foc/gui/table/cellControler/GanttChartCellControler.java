/*
 * Created on 24-Apr-2005
 */
package com.foc.gui.table.cellControler;

import javax.swing.table.*;

import com.foc.gui.table.FTable;
import com.foc.gui.table.cellControler.renderer.gantChartRenderer.BasicGanttScale;
import com.foc.gui.table.cellControler.renderer.gantChartResourceRenderer.FGanttChartResourceCellRenderer;
import com.foc.gui.table.cellControler.renderer.gantChartResourceRenderer.FGanttChartResourceColumnHeaderRenderer;

/**
 * @author 01Barmaja
 */
public class GanttChartCellControler extends AbstractCellControler{
  
  private FGanttChartResourceCellRenderer renderer = null;//To allow editable not editable appearence
  private FGanttChartResourceColumnHeaderRenderer columnHeaderRenderer = null;
  
  public GanttChartCellControler(BasicGanttScale gantScale){
  	renderer = new FGanttChartResourceCellRenderer(gantScale);
  	columnHeaderRenderer = new FGanttChartResourceColumnHeaderRenderer(gantScale);
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
