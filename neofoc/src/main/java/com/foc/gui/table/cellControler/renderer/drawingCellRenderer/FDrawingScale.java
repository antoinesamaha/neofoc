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


public class FDrawingScale  {
  
  private int maxPixels   = 0;
  private double maxValue = 0;
  
  public FDrawingScale(int maxPixels ){
    super();
    this.maxPixels = maxPixels;
	}

  public int getTotalNumberOFPixelsForColumn() {
    return getMaxPixels();
  }

  public double getMaxValue() {
    return maxValue;
  }

  public void setMaxValue(double maxValue) {
    this.maxValue = maxValue;
  }

  public int getMaxPixels() {
    return maxPixels;
  }

  public void setMaxPixels(int maxPixels) {
    this.maxPixels = maxPixels;
  }
  
}
