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
package com.foc.gui.table.cellControler.renderer.drawingCellRenderer;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import javax.swing.JTable;

@SuppressWarnings("serial")
public class FDrawingRowPanel extends JPanel {
	private IFDrawingObjectInfo drawingInfo = null;
  private FDrawingScale drawingScale = null;
  private JTable table = null;
  
	public FDrawingRowPanel(FDrawingScale drawingScale){
		this.drawingScale = drawingScale;
  }
	
	public void dispose(){
		drawingInfo = null;
    drawingScale = null;
    table = null;
	}
  
  @Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
    
    int rowHeight = this.table.getRowHeight();
    drawingInfo.draw(g, drawingScale, rowHeight);
	}
  
  public Dimension getPreferredSize(){
    Dimension dimension = super.getPreferredSize();
    dimension.width = drawingScale.getTotalNumberOFPixelsForColumn();
    return dimension;
  }

  public void setDrawingInfo(IFDrawingObjectInfo drawingInfo) {
    this.drawingInfo = drawingInfo;
  }

  public void setTable(JTable table) {
    this.table = table;
  }
}
