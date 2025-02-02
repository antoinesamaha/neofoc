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
package com.foc.gui.table.cellControler;

import javax.swing.table.*;

import com.foc.gui.table.FTable;
import com.foc.gui.table.cellControler.renderer.drawingCellRenderer.FDrawingCellRenderer;
import com.foc.gui.table.cellControler.renderer.drawingCellRenderer.FDrawingColumnHeaderRenderer;
import com.foc.gui.table.cellControler.renderer.drawingCellRenderer.FDrawingScale;

/**
 * @author 01Barmaja
 */
public class DrawingCellControler extends AbstractCellControler {
  
  private FDrawingCellRenderer renderer = null;//To allow editable not editable appearence
  private FDrawingColumnHeaderRenderer columnHeaderRenderer = null;
  
  public DrawingCellControler(FDrawingScale drawingScale){
  	renderer = new FDrawingCellRenderer(drawingScale);
  	columnHeaderRenderer = new FDrawingColumnHeaderRenderer(drawingScale);
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
