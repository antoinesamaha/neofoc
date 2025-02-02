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
package com.foc.gui.table.cellControler.renderer.gantChartResourceRenderer;

import java.awt.Color;
import java.sql.Date;

public interface IGantChartResourceDrawingInfo{
	/*public int getActivityCount();
  	public double getActivityDurationAt(int i);
	  public Date getActivityStartDateAt(int i);
  	public String getActivityLabelAt(int i);
  	public ArrayList<Color> getColorArrayForActivityAt(int i);*/
  
  public int    getActivityIntervalCount();
  public Date   getActivityIntervalStartDateAt(int index);
  public Date   getActivityIntervalEndDateAt(int index);
  
  public int    getResourceTaskOccupationCountAt(int resourceEstimateQuantityIndex);
  public double getPercentageOccupationAt(int resourceEstimateQuantityIndex, int resourceTaskOccupationIndex);
  
  public Color  getColorForActivityIntervalAt(int timeInterval, int occupationIndex);
}
