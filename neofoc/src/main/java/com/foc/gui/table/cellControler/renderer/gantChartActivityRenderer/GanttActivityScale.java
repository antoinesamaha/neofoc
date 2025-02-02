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
import java.util.Calendar;

import com.foc.business.calendar.FCalendar;
import com.foc.desc.FocObject;
import com.foc.gui.table.cellControler.renderer.gantChartRenderer.BasicGanttScale;
import com.foc.tree.FNode;
import com.foc.tree.FTree;
import com.foc.tree.TreeScanner;

public class GanttActivityScale extends BasicGanttScale {
  
	public static final int MODE_NONE    = 0;
  public static final int MODE_FORWARD = 1;
  public static final int MODE_REVERSE = 2;
  public static final int MODE_BOTH    = 3;
  private int schedulingMode = MODE_BOTH;
  
  public GanttActivityScale(FCalendar calendar, int ganttScaleView){
    super(calendar, ganttScaleView);
	}
  
  public void calculateStartAndEndDates(FTree tree){
    
    setStartDate(new Date(0));
    setEndDate(new Date(0));
    
    tree.scan(new TreeScanner<FNode>(){

      public void afterChildren(FNode node) {
        FocObject focObj = (FocObject)node.getObject();
        if(focObj != null){
          IGanttChartObjectInfo gantChartObjectInfo = (IGanttChartObjectInfo)focObj;
          if(getSchedulingMode() == MODE_FORWARD || getSchedulingMode() == MODE_BOTH || getSchedulingMode() == MODE_NONE){
          	Date minStartDate = gantChartObjectInfo.getMinimumStartDate(); 
            if(!FCalendar.isDateZero(minStartDate) && (minStartDate.compareTo(getStartDate()) < 0 || FCalendar.isDateZero(getStartDate()))){
              setStartDate(minStartDate);  
            }
            
            Date minEndDate = gantChartObjectInfo.getMinimumEndDate();
            if(!FCalendar.isDateZero(minEndDate) && minEndDate.compareTo(getEndDate()) > 0) {
              setEndDate(minEndDate);  
            }
            
            minEndDate = gantChartObjectInfo.getForecastEndDate();
            if(!FCalendar.isDateZero(minEndDate) && minEndDate.compareTo(getEndDate()) > 0) {
              setEndDate(minEndDate);  
            }
          }
          
          if(getSchedulingMode() == MODE_REVERSE || getSchedulingMode() == MODE_BOTH){
          	Date maxStartDate = gantChartObjectInfo.getMaximumStartDate();
            if(!FCalendar.isDateZero(maxStartDate) && (maxStartDate.compareTo(getStartDate()) < 0 || FCalendar.isDateZero(getStartDate()))){
              setStartDate(gantChartObjectInfo.getMaximumStartDate());  
            }
            
            Date maxEndDate = gantChartObjectInfo.getMaximumEndDate();            
            if(!FCalendar.isDateZero(maxEndDate) && maxEndDate.compareTo(getEndDate()) > 0){
              setEndDate(maxEndDate);  
            }
          }
        }
      }

      public boolean beforChildren(FNode node) {
        return true;
      }
    });
  
    if(FCalendar.isDateZero(getStartDate())){
      setStartDate(new Date(0));
    }

    //Extending the interval
    {
    	Date sDate = getStartDate();
    	Calendar cal = FCalendar.getInstanceOfJavaUtilCalandar();
    	cal.setTime(sDate);
    	if(getGanttColumnsView() == VIEW_DAILY || getGanttColumnsView() == VIEW_HOURLY){
    		FCalendar.rollTheCalendar_DayDown(cal);
    		FCalendar.rollTheCalendar_DayDown(cal);
    		FCalendar.rollTheCalendar_DayDown(cal);
    		FCalendar.rollTheCalendar_DayDown(cal);
    		FCalendar.rollTheCalendar_DayDown(cal);
    	}else{
      	FCalendar.rollTheCalendar_MonthDown(cal);
    	}
    	sDate = new Date(cal.getTime().getTime());
    	setStartDate(sDate);
    	
    	Date eDate = getEndDate();
    	cal.setTime(eDate);
    	if(getGanttColumnsView() == VIEW_DAILY || getGanttColumnsView() == VIEW_HOURLY){
    		FCalendar.rollTheCalendar_Day(cal);
    		FCalendar.rollTheCalendar_Day(cal);
    		FCalendar.rollTheCalendar_Day(cal);
    		FCalendar.rollTheCalendar_Day(cal);
    		FCalendar.rollTheCalendar_Day(cal);
    	}else{
    		FCalendar.rollTheCalendar_Month(cal);
    	}
    	//FCalendar.rollTheCalendar_Month(cal);
    	//FCalendar.rollTheCalendar_Month(cal);
    	eDate = new Date(cal.getTime().getTime());
	    setEndDate(eDate);
    }
    
    /*
    Calendar cal = FCalendar.getInstanceOfJavaUtilCalandar(); 
    cal.setTime(getStartDate());
    cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
    cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
    cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
    setStartDate(new Date(cal.getTimeInMillis()));
    
    cal.setTime(getEndDate());
    cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
    cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
    cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
    setEndDate(new Date(cal.getTimeInMillis()));
    */
  }

  public int getSchedulingMode() {
    return schedulingMode;
  }

  public void setSchedulingMode(int schedulingMode) {
    this.schedulingMode = schedulingMode;
  }
}

