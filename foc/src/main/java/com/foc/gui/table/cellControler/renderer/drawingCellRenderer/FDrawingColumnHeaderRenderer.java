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

import java.awt.Component;
import javax.swing.JTable;

public class FDrawingColumnHeaderRenderer extends FDrawingCellRenderer {
  
  private JTable table = null;
  private FDrawingHeaderPanel panel = null;
  
  public FDrawingColumnHeaderRenderer(FDrawingScale drawingScale) {
    super(drawingScale);
    panel = new FDrawingHeaderPanel(drawingScale);
  }
  
  public void dispose() {
    super.dispose();
    table = null;
    if( panel != null ){
      panel.dispose();
      panel = null;
    }
  }
  
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    this.table = table;
    panel.set(table);
    return panel;
  }
  
}
