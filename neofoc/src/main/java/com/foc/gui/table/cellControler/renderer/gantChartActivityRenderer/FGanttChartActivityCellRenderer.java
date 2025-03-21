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
package com.foc.gui.table.cellControler.renderer.gantChartActivityRenderer;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.foc.gui.table.FTable;
import com.foc.gui.table.cellControler.renderer.gantChartRenderer.BasicGanttScale;

public class FGanttChartActivityCellRenderer implements TableCellRenderer {
  
	public static final int COLUMN_WIDTH = 1000;
	private FGanttActivityRowPanel gPanel = null;
	private BasicGanttScale gantScale = null;
	
	public FGanttChartActivityCellRenderer(BasicGanttScale gantScale){
		gPanel = new FGanttActivityRowPanel(gantScale);
		this.gantScale = gantScale;
	}
	  
  public void dispose(){
  	if(this.gPanel != null){
  		this.gPanel.dispose();
  		this.gPanel = null;
  	}
  	
  	this.gantScale = null;
  }
  
  public BasicGanttScale getGantScale(){
  	return this.gantScale;
  }

	/**
	 * TreeCellRenderer method. Overridden to update the visible row.
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		
		FTable fTable = (FTable)table;
    IGanttChartObjectInfo drawingInfo = (IGanttChartObjectInfo)fTable.getTableModel().getRowFocObject(row);
		gPanel.setTable(table);
    gPanel.setDrawingInfo(drawingInfo);
		return gPanel;
	}

}
