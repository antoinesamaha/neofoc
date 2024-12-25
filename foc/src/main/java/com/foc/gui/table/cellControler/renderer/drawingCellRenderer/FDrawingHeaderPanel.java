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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.JTable;

import com.foc.Globals;

@SuppressWarnings("serial")
public class FDrawingHeaderPanel extends JPanel {
	private FDrawingScale drawingScale = null;
	private JTable table = null;
  
	public FDrawingHeaderPanel(FDrawingScale drawingScale) {
    this.drawingScale = drawingScale;
		setBackground(Globals.getDisplayManager().getColumnTitleBackground());
	}
  
	public void dispose(){
		this.drawingScale = null;
    table = null;
	}
	
	private FDrawingScale getDrawingScale(){
		return this.drawingScale;
	}
	
	public void set(JTable table){
		this.table = table;
		int headerHeight = table.getTableHeader().getHeight();
		Rectangle oldBound = getBounds();
		setBounds(oldBound.x, oldBound.y, oldBound.width, headerHeight);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
  
  public Dimension getPreferredSize(){
    Dimension dimension = super.getPreferredSize();
    dimension.width = drawingScale.getTotalNumberOFPixelsForColumn();
    return dimension;
  }
}
