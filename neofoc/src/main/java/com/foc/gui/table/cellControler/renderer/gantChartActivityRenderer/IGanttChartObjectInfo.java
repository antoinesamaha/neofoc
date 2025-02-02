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

import java.sql.Date;

public interface IGanttChartObjectInfo {
	public static final int GANTT_NODE_TYPE_ACTIVITY       = 0;
	public static final int GANTT_NODE_TYPE_SUPER_ACTIVITY = 1;
	public static final int GANTT_NODE_TYPE_SUB_ACTIVITY   = 2;
	
	public Date       getMinimumStartDate();
  public Date       getMinimumEndDate();
  public Date       getMaximumStartDate();
  public Date       getMaximumEndDate();
	public Date       getForecastStartDate();
  public Date       getForecastEndDate();
	public Date       getActualStartDate();
  public Date       getActualEndDate();  
  public int        getGanttNodeType();
  public boolean    isInCriticalPath();
  public double     getPercentCompletion();
  public GanttStyle getGanttStyle();
  public String     getTextOnBarLeftSide();
  public String     getTextOnBarRightSide();
}
