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
package com.foc.gui.table.cellControler.renderer.gantChartRenderer;

import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;

import com.foc.business.calendar.DayShift;
import com.foc.business.calendar.FCalendar;
import com.foc.desc.field.FMultipleChoiceField;

public class BasicGanttScale {
  
  protected int    NBR_PIXELS_PER_DAY    = 0;
  protected double NBR_PIXELS_PER_MINUTE = 0;
  protected double NIGHT_MINUTES_DIVIDER = 0;
  
  private   Date      startDate   = null;
  private   Date      endDate     = null;
  private   Date      currentDate = null;
  protected FCalendar calandar    = null;
  private   int       rowHeight   = 0;
  
  private static final int PIXEL_WIDTH_HOUR  = 180;
  private static final int PIXEL_WIDTH_DAY   = 75;
  private static final int PIXEL_WIDTH_WEEK  = 10;
  private static final int PIXEL_WIDTH_MONTH = 5;
  
  public static final int VIEW_WEEKLY  = 0;
  public static final int VIEW_DAILY   = 1;
  public static final int VIEW_HOURLY  = 2;
  public static final int VIEW_MONTHLY = 3;
  
  private int ganttColumnsView = 0;
  
  public BasicGanttScale(FCalendar calendar, int ganttColumnsView){
  	setGanttColumnsView(ganttColumnsView);
    this.calandar = calendar;
    setStartDate(new Date(0));
    setEndDate(new Date(0));
	}
  
  public void dispose(){
    startDate = null;
    endDate = null;
    calandar = null;
  }
  
  public FCalendar getCalandar(){
    return this.calandar;
  }
  
  public int getTotalNumberOFPixelsForColumn(){
    int totalPixels = getPixelsForDate(endDate) - getPixelsForDate(startDate);
    return totalPixels; 
  }

  public int getPixelsForMinutes(double duration){
    return (int)(duration * NBR_PIXELS_PER_MINUTE); 
  }
  
  public int getPixelsForDate(Date date){
    long dateTimeMilli = date != null ? date.getTime() : 0;
    long startDateMilli = startDate != null ? startDate.getTime() : 0;
    long sinceStartMilli = dateTimeMilli - startDateMilli;
    double sinceStartMinutes_Night = 0;
    double sinceStartMinutes_Day = (int)((sinceStartMilli) / (60 * 1000));
    
    long sinceStartMilli_Night = 0;
    long sinceStartMilli_Day = 0;
    FCalendar fCal = getCalandar();
    if(fCal != null){
      int numberOfDays = (int)(sinceStartMilli / FCalendar.MILLISECONDS_IN_DAY);
      int numberOfMilliSecond = (int)(sinceStartMilli % FCalendar.MILLISECONDS_IN_DAY);
      
      double nonWorkingHourInDay = fCal.getNumberOfNonWorkingHoursInDay();
      
      sinceStartMilli_Night = (long)((numberOfDays * nonWorkingHourInDay) * FCalendar.MILLISECONDS_IN_HOUR);
      
      DayShift dayShift = fCal.getDayShiftForDate(date);
      Time calendarStartTime = dayShift != null ? dayShift.getStartTime() : new Time(0);//fCal.getPropertyTime(FCalendarDesc.FLD_START_TIME);
      Time calendarEndTime   = dayShift != null ? dayShift.getEndTime() : new Time(0);
      long calendarStartSinceMidnight = FCalendar.getTimeSinceMidnight(calendarStartTime);
      long calendarEndSinceMidnight   = FCalendar.getTimeSinceMidnight(calendarEndTime);
      
      long nonWorkingBeforStartMilli  = numberOfMilliSecond > calendarStartSinceMidnight ? calendarStartSinceMidnight : numberOfMilliSecond;
      long nonWorkingAfterFinsihMilli = numberOfMilliSecond > calendarEndSinceMidnight   ? numberOfMilliSecond - calendarEndSinceMidnight : 0;
      
      sinceStartMilli_Night = sinceStartMilli_Night + nonWorkingBeforStartMilli + nonWorkingAfterFinsihMilli;
      sinceStartMilli_Day   = sinceStartMilli       - sinceStartMilli_Night     ;
      
      sinceStartMinutes_Night = (int)((sinceStartMilli_Night) / (60 * 1000));
      sinceStartMinutes_Day   = (int)((sinceStartMilli_Day  ) / (60 * 1000));
    }
    
    return (int)(getPixelsForMinutes(sinceStartMinutes_Day) + (getPixelsForMinutes(sinceStartMinutes_Night)/NIGHT_MINUTES_DIVIDER)); 
  }

  public Date getEndDate(){
    return endDate;
  }

  public void setEndDate(Date date){
  	boolean treated = false;
  	if(!FCalendar.isDateZero(date)){
  		switch(getGanttColumnsView()){
  			case VIEW_WEEKLY:
  			case VIEW_MONTHLY:
  			{
  				Calendar calendar = FCalendar.getInstanceOfJavaUtilCalandar();
  	  		calendar.setTime(date);
  	      calendar.set(Calendar.DAY_OF_MONTH, 1);
  	      calendar.set(Calendar.HOUR_OF_DAY, 0);
  	      calendar.set(Calendar.MINUTE, 0);
  	      calendar.set(Calendar.SECOND, 0);
  	      FCalendar.rollTheCalendar_Month(calendar);
  	  		endDate = new Date(calendar.getTimeInMillis());
  	  		treated = true;
  			}
  			break;
  		}
  	}
  	if(!treated){
  		endDate = date;
  	}
  }
  
  public Date getStartDate(){
    return startDate;
  }
  
  public void setStartDate(Date date){
  	boolean treated = false;
  	if(!FCalendar.isDateZero(date)){
  		switch(getGanttColumnsView()){
  			case VIEW_WEEKLY:
  			case VIEW_MONTHLY:
  			{
  	  		Calendar calendar = FCalendar.getInstanceOfJavaUtilCalandar();
  	  		calendar.setTime(date);
  	      calendar.set(Calendar.DAY_OF_MONTH, 1);
  	      calendar.set(Calendar.HOUR_OF_DAY, 0);
  	      calendar.set(Calendar.MINUTE, 0);
  	      calendar.set(Calendar.SECOND, 0);
  	      calendar.set(Calendar.MINUTE, 0);
  	      calendar.set(Calendar.MILLISECOND, 0);
  	  		startDate = new Date(calendar.getTimeInMillis());
  	  		treated = true;
  			}
  			break;
  			case VIEW_DAILY:
  			case VIEW_HOURLY:
  			{
  	  		Calendar calendar = FCalendar.getInstanceOfJavaUtilCalandar();
  	  		calendar.setTime(date);
  	      calendar.set(Calendar.HOUR_OF_DAY, 0);
  	      calendar.set(Calendar.MINUTE, 0);
  	      calendar.set(Calendar.SECOND, 0);
  	      calendar.set(Calendar.MINUTE, 0);
  	      calendar.set(Calendar.MILLISECOND, 0);
  	  		startDate = new Date(calendar.getTimeInMillis());
  	  		treated = true;
  			}
  			break;
  		}
  	}
  	if(!treated){
  		startDate = date;
  	}
  }

  public Date getCurrentDate(){
    return currentDate;
  }

  public void setCurrentDate(Date date){
    currentDate = date;
  }
  
  public int getGanttColumnsView(){
    return ganttColumnsView;
  }

  public void setGanttColumnsView(int ganttColumnsView){
    this.ganttColumnsView = ganttColumnsView;
    if( ganttColumnsView == VIEW_HOURLY ){
      NBR_PIXELS_PER_DAY = PIXEL_WIDTH_HOUR;
    }else if( ganttColumnsView == VIEW_DAILY ){
      NBR_PIXELS_PER_DAY = PIXEL_WIDTH_DAY;
    }else if( ganttColumnsView == VIEW_WEEKLY ){
      NBR_PIXELS_PER_DAY = PIXEL_WIDTH_WEEK;
    }else if( ganttColumnsView == VIEW_MONTHLY ){
      NBR_PIXELS_PER_DAY = PIXEL_WIDTH_MONTH;
    }
    NBR_PIXELS_PER_MINUTE = ((double)NBR_PIXELS_PER_DAY) / (24*60);
    NIGHT_MINUTES_DIVIDER = 1000;
  }
  
  public static void adjustGanttScaleField(FMultipleChoiceField field){
  	field.addChoice(VIEW_HOURLY , "Hourly" );
  	field.addChoice(VIEW_DAILY  , "Daily"  );
  	field.addChoice(VIEW_WEEKLY , "Weekly" );
  	field.addChoice(VIEW_MONTHLY, "Monthly");
  }

	public int getRowHeight() {
		return rowHeight;
	}

	public void setRowHeight(int rowHeight) {
		this.rowHeight = rowHeight;
	}
}
