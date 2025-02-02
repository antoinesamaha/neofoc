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
// CONVERSION IN TIME UNITS

package com.foc.business.calendar;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import com.foc.Globals;
import com.foc.IFocEnvironment;
import com.foc.business.units.DimensionDesc;
import com.foc.business.units.Unit;
import com.foc.business.units.UnitDesc;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;
import com.foc.property.FDate;
import com.foc.property.FTime;

@SuppressWarnings("serial")
public class FCalendar extends FocObject {
	private FocList                 holidayList        = null;
	private HashMap<String, Double> openTimeConversion = null;
	
	public static final long MILLISECONDS_IN_DAY    = 86400000;
	public static final long MILLISECONDS_IN_HOUR   = MILLISECONDS_IN_DAY / 24;
	public static final long MILLISECONDS_IN_MINUTE = MILLISECONDS_IN_HOUR / 60;
	private static Date ABSTRACT_DATE = null;

	public FCalendar(FocConstructor constr){
	  super(constr);
		newFocProperties();
	} 
	
	public int getStartingDayOfWeek(){
		return getPropertyInteger(FCalendarDesc.FLD_STARTING_DAY_OF_WEEK);
	}
	
	public void setStartingDayOfWeek(int day){
		setPropertyInteger(FCalendarDesc.FLD_STARTING_DAY_OF_WEEK, day);
	}
	
	public boolean isDefaultCalendar(){
		return getPropertyBoolean(FCalendarDesc.FLD_IS_DEFAULT);
	}
	
	public void setDefaultCalendar(boolean def){
		setPropertyBoolean(FCalendarDesc.FLD_IS_DEFAULT, def);
	}
	
  public WeekShift getWeekShift(){
  	return (WeekShift) getPropertyObject(FCalendarDesc.FLD_WEEK_SHIFT);
  }

  public void setWeekShift(WeekShift weekShift){
  	setPropertyObject(FCalendarDesc.FLD_WEEK_SHIFT, weekShift);
  }

	public static FCalendar getDefaultCalendar(){
		FocList calendarList = FCalendarDesc.getList(FocList.LOAD_IF_NEEDED);
		FCalendar cal = (FCalendar) calendarList.searchByPropertyBooleanValue(FCalendarDesc.FLD_IS_DEFAULT, true);
		if(cal == null){
			Globals.showNotification("No Default Calendar", "Default calendar not defined!", IFocEnvironment.TYPE_WARNING_MESSAGE);
		}
		return cal;
	}

	public void dispose(){
		super.dispose();
		holidayList = null;
	}
	
	public static boolean isDateZero(java.util.Date date){
		return date == null || date.getTime() < Globals.DAY_TIME;
	}
	
  public static int getWeekNumber(Date date){
    int weekNumber = -1;
    
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    
    weekNumber = calendar.get(Calendar.WEEK_OF_YEAR);
    return weekNumber;
  }
  
  public static int getMonthNumber(Date date){
    int monthNumber = -1;
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    monthNumber = calendar.get(Calendar.MONTH);
    return monthNumber;
  }
  
  public static int getYear(Date date){
    int yearNumber = -1;
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    yearNumber = calendar.get(Calendar.YEAR);
    return yearNumber;
  }
  
  public static int getDayOfYear(Date date){
  	int dayOfYear = -1;
  	Calendar calendar = Calendar.getInstance();
  	calendar.setTime(date);
  	dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
  	return dayOfYear;
  }
  
  public static int getDayOfMonth(Date date){
  	int dayOfMonth = -1;
  	Calendar calendar = Calendar.getInstance();
  	calendar.setTime(date);
  	dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
  	return dayOfMonth;
  }
  
  public static int getHour(Date date){
  	int houre = -1;
  	Calendar calendar = Calendar.getInstance();
  	calendar.setTime(date);
  	houre = calendar.get(Calendar.HOUR_OF_DAY);
  	return houre;
  }
  
  public static int getMinute(Date date){
  	int minute = -1;
  	Calendar calendar = Calendar.getInstance();
  	calendar.setTime(date);
  	minute = calendar.get(Calendar.MINUTE);
  	return minute;
  }
  
  //SHIFT
  /*
	private int getFieldIDForDayOfWeek(int dayOfWeek){
		int fieldID = 0;
		if(dayOfWeek == Calendar.SUNDAY){
			fieldID = FCalendarDesc.FLD_IS_SUNDAY_WORKDAY;
		}else if(dayOfWeek == Calendar.MONDAY){
			fieldID = FCalendarDesc.FLD_IS_MONDAY_WORKDAY;
		}else if(dayOfWeek == Calendar.TUESDAY){
			fieldID = FCalendarDesc.FLD_IS_TUESDAY_WORKDAY;
		}else if(dayOfWeek == Calendar.WEDNESDAY){
			fieldID = FCalendarDesc.FLD_IS_WEDNESDAY_WORKDAY;
		}else if(dayOfWeek == Calendar.THURSDAY){
			fieldID = FCalendarDesc.FLD_IS_THURSDAY_WORKDAY;
		}else if(dayOfWeek == Calendar.FRIDAY){
			fieldID = FCalendarDesc.FLD_IS_FRIDAY_WORKDAY;
		}else if(dayOfWeek == Calendar.SATURDAY){
			fieldID = FCalendarDesc.FLD_IS_SATURDAY_WORKDAY;
		}
		return fieldID;
	}
	*/
  
	public boolean isWeekEnd(java.sql.Date d){
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		boolean isWorking = false;
	
		WeekShift weekShift = getWeekShiftForDate(d);
		DayShift  dayShift  = weekShift != null ? weekShift.getDayShift(dayOfWeek) : null;

		isWorking = dayShift != null;
		//int fieldID = getFieldIDForDayOfWeek(dayOfWeek);
		//isWorking = this.getPropertyBoolean(fieldID);
		return !isWorking;
	}

	public static Date shiftDate(Date date, int shift){
		Calendar cal = getInstanceOfJavaUtilCalandar();
		return shiftDate(cal, date, shift);
	}
	
  public static Date shiftDate(Calendar cal, Date date, int shift){
    cal.setTime((Date)date.clone());
    //int dlsBefore = cal.get(Calendar.DST_OFFSET);
    cal.add(Calendar.DATE, shift);
    //int dlsAfter = cal.get(Calendar.DST_OFFSET);
    /*if(dlsAfter != dlsBefore){
    	int debug = 3;
    }*/
    return new Date(cal.getTimeInMillis());
  }

  public Date [] getStartingEndingDatesOfWeek (int week, int year){
   Date [] dates = new Date[2];
   Calendar baseCal = Calendar.getInstance();
   Calendar calCheckDate = Calendar.getInstance();
   
   baseCal.set(year, 0, 1);
   Date d = new Date(baseCal.getTimeInMillis());
   calCheckDate.setTime(d);
   
   if(calCheckDate.get(Calendar.DAY_OF_WEEK) > getStartingDayOfWeek() ) {
     d = FCalendar.shiftDate(baseCal, d, -(calCheckDate.get(Calendar.DAY_OF_WEEK) - getStartingDayOfWeek()) );
   }else if(calCheckDate.get(Calendar.DAY_OF_WEEK) < getStartingDayOfWeek() ) {
     d = FCalendar.shiftDate(baseCal, d, getStartingDayOfWeek() - calCheckDate.get(Calendar.DAY_OF_WEEK) - 7);
   }
   
   d = FCalendar.shiftDate(baseCal, d, 7 * week);
   dates[0] = FCalendar.shiftDate(baseCal, d, -7 );
   dates[1] = FCalendar.shiftDate(baseCal, d, -1);
  
   return dates;
  }
  
  public Date getFirstDayOfWeek(Date date){
    Date beginingOfWeek = null;
    
    Calendar calcheck = Calendar.getInstance();
    calcheck.setTime(date);
    
    int backShiftToGetToFirstDayOfWeek = (calcheck.get(Calendar.DAY_OF_WEEK) + 7 - getStartingDayOfWeek()) % 7; 
    beginingOfWeek = shiftDate(calcheck, date, -backShiftToGetToFirstDayOfWeek);
    /*
    if (calcheck.get(Calendar.DAY_OF_WEEK) > getStartingDayOfWeek()){
      beginingOfWeek = shiftDate(calcheck, date, -(calcheck.get(Calendar.DAY_OF_WEEK)- getStartingDayOfWeek()));
    }else{
      beginingOfWeek = new Date(date.getTime());
    }
    */
    
    return beginingOfWeek;
  }
  
  public Date getLastDayOfWeek(Date date){
  	Date     first    = getFirstDayOfWeek(date);
    Calendar calcheck = Calendar.getInstance();
    Date     last     = shiftDate(calcheck, first, 6);
    return last;
  }
  
  public Date getFirstDayOfMonth(Date date){
    Calendar calcheck = Calendar.getInstance();
    calcheck.setTime(date);
    calcheck.set(Calendar.DAY_OF_MONTH, 1);
    calcheck.set(Calendar.HOUR_OF_DAY, 10);
    return new Date(getTimeAtMidnight(calcheck.getTime()));
  }

  public Date getLastDayOfMonth(Date date){
    Calendar calcheck = Calendar.getInstance();
    calcheck.setTime(date);
    int month = calcheck.get(Calendar.MONTH);
    if(month == Calendar.DECEMBER){
    	calcheck.set(Calendar.MONTH, Calendar.JANUARY);
    	calcheck.set(Calendar.YEAR, calcheck.get(Calendar.YEAR) + 1);
    }else{
    	calcheck.set(Calendar.MONTH, month+1);
    }
    Date firstOfNextMonth = getFirstDayOfMonth(new Date(getTimeAtMidnight(calcheck.getTime())));
    firstOfNextMonth.setTime(firstOfNextMonth.getTime() - Globals.DAY_TIME);
    return firstOfNextMonth;
  }

  public Date[] getQuarterDatesOfMonth(Date date){
  	Date quarters[] = new Date[5];
  	quarters[0] = getFirstDayOfMonth(date);
  	quarters[4] = getFirstDayOfMonth(new Date(quarters[0].getTime()+ 32 * Globals.DAY_TIME));
  	
  	long deltaTime = (quarters[4].getTime() - quarters[0].getTime()) / 4;
  	quarters[1] = new Date(quarters[0].getTime()+deltaTime);
  	quarters[1].setTime(getTimeAtMidnight(quarters[1]));
  	quarters[2] = new Date(quarters[0].getTime()+2*deltaTime);
  	quarters[2].setTime(getTimeAtMidnight(quarters[2]));
  	quarters[3] = new Date(quarters[0].getTime()+3*deltaTime);
  	quarters[3].setTime(getTimeAtMidnight(quarters[3]));
  	
  	return quarters;
  }

  public int getQuarterOfMonth(Date date){
  	int quarter = 1;
  	Date[] quarters = getQuarterDatesOfMonth(date);
  	
  	quarter = 1;
  	while(quarter < 5 && date.getTime() >= quarters[quarter].getTime()){
  		quarter++;
  	}
  	
  	return quarter;
  }

	public FocList getHolidayList(){
		if(holidayList == null){
			holidayList = getPropertyList(FCalendarDesc.FLD_HOLIDAYS_LIST);
			holidayList.reloadFromDB();
			holidayList.setDirectlyEditable(true);
			holidayList.setListOrder(new FocListOrder(HolidayDesc.FLD_START_DATE));
		}
		return holidayList;
	}

	public FocList getWeekShiftExceptionList(){
		FocList list = getPropertyList(FCalendarDesc.FLD_WEEK_SHIFT_EXCEPTION_LIST);
		list.setDirectlyEditable(true);
		if(list.getListOrder() == null){
			list.setListOrder(new FocListOrder(WeekShiftExceptionDesc.FLD_START_DATE));
		}
		return list;
	}

	public WeekShift getWeekShiftForDate(long d){
		return getWeekShiftForDate(new Date(d));
	}
	
	public WeekShift getWeekShiftForDate(java.sql.Date d){
		WeekShift shift = getWeekShift();
		
		/* FocObject found = getHolidayList().searchByPropertyDateValue(HolidayDesc.FLD_START_DATE, d);
		return found != null;*/
		long    date  = FCalendar.getTimeAtMidnight(d);

    FocList holidayList = getWeekShiftExceptionList();
    
    for(int i=0; i< holidayList.size(); i++){
      WeekShiftException holiday = (WeekShiftException) holidayList.getFocObject(i);
      long startDate = holiday.getStartDateTime();
      long endDate   = holiday.getEndDateTime();
      
      if (date <= endDate && date >= startDate){
      	shift = holiday.getWeekShift();
        break;
      }else if(startDate > date){
      	break;
      }
    }
    return shift;
  }

	public DayShift getDayShiftForDate(java.sql.Date d){
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
	
		WeekShift weekShift = getWeekShiftForDate(d);
		DayShift  dayShift  = weekShift != null ? weekShift.getDayShift(dayOfWeek) : null;
		
		return dayShift;
  }

	public int getOvertimeFactorLevel_ForDate(java.sql.Date d){
		int level = WeekShiftDesc.OT_FACTOR_NORMAL;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
	
		WeekShift weekShift = getWeekShiftForDate(d);
		if(weekShift != null){
			level = weekShift.getOvertimeLevel(dayOfWeek);
		}
		
		return level;
  }
	
	public boolean isHoliday(java.sql.Date d){
		/* FocObject found = getHolidayList().searchByPropertyDateValue(HolidayDesc.FLD_START_DATE, d);
		return found != null;*/
		long    date  = FCalendar.getTimeAtMidnight(d);
    boolean found = false;

    FocList holidayList = getHolidayList();
    
    for(int i=0; i< holidayList.size()&& !found; i++){
      Holiday holiday = (Holiday) holidayList.getFocObject(i);
      long startDate = holiday.getStartDateTime();
      long endDate   = holiday.getEndDateTime();
      
      if (date <= endDate && date >= startDate){
        found = true;
      }else if(startDate > date){
      	break;
      }
    }
    return found;
  }

	public void setDateAsHoliday(java.sql.Date d1, java.sql.Date d2, String reason){
		FocList holidayList = getHolidayList();
		if(holidayList != null){
			Holiday holiday = (Holiday) holidayList.newEmptyItem();
			holiday.setStartDate(d1);
			holiday.setEndDate(d2);
			holiday.setReason(reason);
		}
	}
	public boolean isWorkingDay(Date d){
		return !isWeekEnd(d) && !isHoliday(d);
	}
	
	//SHIFT
	/*
	public boolean isWorkTime(Time time){
		boolean isWork = false;
		
		FTime startTime = (FTime) getFocProperty(FCalendarDesc.FLD_START_TIME);
		FTime endTime   = (FTime) getFocProperty(FCalendarDesc.FLD_END_TIME);
		
		if(startTime.compareTo(time) <= 0 && endTime.compareTo(time) >= 0){
			isWork = true;
		}
		
		return isWork;
	}
	*/
		
	public boolean isWorkTime(long time){
		boolean isWork = false;
		
		DayShift dayShift = getDayShiftForDate(new Date(time));
		//WeekShift weekShift = getWeekShiftForDate(time);
		
		Time startTime = dayShift.getStartTime();
		Time endTime   = dayShift.getEndTime();
		
		long startTimeLong = getTimeSinceMidnight(startTime);
		long endTimeLong = getTimeSinceMidnight(endTime);
		
		if(startTimeLong <= time && endTimeLong >= time){
			isWork = true;
		}
		return isWork;
	}

	/*
	public boolean isAfterWorkTime(long time){
		return time >= getEndWorkTime();
	}

	public long getStartWorkTime(){
		Time startTime     = getPropertyTime(FCalendarDesc.FLD_START_TIME);
		long startTimeLong = getTimeSinceMidnight(startTime);
		return startTimeLong;
	}

	public long getEndWorkTime(){
		Time endTime     = getPropertyTime(FCalendarDesc.FLD_END_TIME);
		long endTimeLong = getTimeSinceMidnight(endTime);
		return endTimeLong;
	}
	
	public boolean isBeforeWorkTime(long time){
		return time < getStartWorkTime();
	}
	*/

	/*public Date[] getNeerestStartEndDateForDuration(Date startDate, double durationInMinutes){
		Date[] res = null;
		if(startDate != null && durationInMinutes > 0){
			Date startDateClone = (Date)startDate.clone();
			boolean allDurationDuringWorkingTime = isAllDurationDuringWorkingTime(startDate, durationInMinutes);;
			if(!allDurationDuringWorkingTime){
				
				
				Calendar cal = Calendar.getInstance();
				FTime startTimeProp = (FTime)getFocProperty(FCalendarDesc.FLD_START_TIME);
				Time startTime = startTimeProp.getTime();
				cal.setTime(new Date(startTime.getTime()));
				int startHour = cal.get(Calendar.HOUR);
				int startMinute = cal.get(Calendar.MINUTE);
				int amPm = Calendar.AM;
				if(startHour >= 12){
					startHour = startHour % 12;
					amPm = Calendar.PM;
				}

				cal.setTime(startDateClone);
				cal.roll(Calendar.DAY_OF_MONTH, true);
				
				cal.set(Calendar.HOUR, startHour);
				cal.set(Calendar.MINUTE, startMinute);
				cal.set(Calendar.AM_PM, amPm);
				
				
				startDateClone = new Date(cal.getTime().getTime());
				allDurationDuringWorkingTime = isAllDurationDuringWorkingTime(startDateClone, durationInMinutes);
			}
			
			if(allDurationDuringWorkingTime){//we have to test if it is not a day off also
				Date endDate = new Date((long)(startDateClone.getTime() + durationInMinutes * 60 * 1000));
				res = new Date[2];
				res[0] = startDate;
				res[1] = endDate;
			}
		}
		return res;
	}*/
	
	public int getNumberOfOpenDaysInAWeek(){
		return getWeekShift() != null ? getWeekShift().getNumberOfOpenDaysInAWeek() : 0;
	}
	
	public int getNumberOfNonWorkingDaysBetween(Date startDate, Date endDate){
		int res = 0;
		Date date = (Date)startDate.clone();
		while(!date.after(endDate)){
			if(!isWorkingDay(date)){
				res ++;
			}
			date.setTime(date.getTime() + FCalendar.MILLISECONDS_IN_DAY);
		}
		return res;
	}
  
  public int getNumberOfWorkingDaysBetween(Date startDate, Date endDate){
    int res = 0;
    Date date = (Date)startDate.clone();
    while(!date.after(endDate)){
      if(isWorkingDay(date)){
        res ++;
      }
      date.setTime(date.getTime() + FCalendar.MILLISECONDS_IN_DAY);
    }
    return res;
  }

  public int getNumberOfDaysBetween(Date startDate, Date endDate){
    int res = 0;
    Date date = (Date)startDate.clone();
    while(!date.after(endDate)){
      res ++;
      date.setTime(date.getTime() + FCalendar.MILLISECONDS_IN_DAY);
    }
    return res;
  }

	public double getNumberOfWorkingHoursInDay(){
		//SHIFT ATTENTION
		/*
		Time startTime = getPropertyTime(FCalendarDesc.FLD_START_TIME);
		Time endTime = getPropertyTime(FCalendarDesc.FLD_END_TIME);
		long workingMilliSeconds = endTime.getTime() - startTime.getTime();
		return workingMilliSeconds > 0 ? workingMilliSeconds / MILLISECONDS_IN_HOUR : 0;
		*/
		return 8;
	}
	
	public double getNumberOfNonWorkingHoursInDay(){
		Calendar cal = Calendar.getInstance();
		int maxHourInDay = cal.getMaximum(Calendar.HOUR_OF_DAY) + 1;
		return maxHourInDay - getNumberOfWorkingHoursInDay();
	}
	
	private double getDurationInMinutesBetweenMidNightOfFirstDateAndEndDate(Date startDate, Date endDate, double nonWorkingMinutesFactor, double nonWorkingDaysFactor){
		long totalDurationMilli = endDate.getTime() - startDate.getTime() + getTimeSinceMidnight(startDate);
		long workingDurationMilli = 0;
		
		int numberOfDays = (int)(totalDurationMilli / FCalendar.MILLISECONDS_IN_DAY);
		long numberOfMilliSecondInLastDay = (totalDurationMilli % FCalendar.MILLISECONDS_IN_DAY);
		
		int numberOfNoneWorkingDays = getNumberOfNonWorkingDaysBetween(startDate, endDate);
		
		double nonWorkingHourInDay = getNumberOfNonWorkingHoursInDay();
		long milliSecondsOutOfHoraire = (long)(((numberOfDays - numberOfNoneWorkingDays) * nonWorkingHourInDay) * FCalendar.MILLISECONDS_IN_HOUR);
		
		DayShift dayShift_Start = getDayShiftForDate(startDate);
		Time calStartTime = dayShift_Start.getStartTime();
		long calStartTimeSinceMidnight = FCalendar.getTimeSinceMidnight(calStartTime);
		
		DayShift dayShift_End = getDayShiftForDate(endDate);
		Time calEndTime = dayShift_End.getStartTime();
		long calEndTimeSinceMidNight = FCalendar.getTimeSinceMidnight(calEndTime);
		
		long nonWorkingMilliSecondsBeforStartInLastDay = numberOfMilliSecondInLastDay > calStartTimeSinceMidnight ? calStartTimeSinceMidnight : numberOfMilliSecondInLastDay;
		long nonWorkingMilliSecondsAfterFinsihInLastDay = numberOfMilliSecondInLastDay > calEndTimeSinceMidNight ? numberOfMilliSecondInLastDay - calEndTimeSinceMidNight : 0;
		
		milliSecondsOutOfHoraire += nonWorkingMilliSecondsBeforStartInLastDay + nonWorkingMilliSecondsAfterFinsihInLastDay;
		
		long milliSecondsInNonWorkingDays = numberOfNoneWorkingDays * FCalendar.MILLISECONDS_IN_DAY;
		
		workingDurationMilli = totalDurationMilli - milliSecondsOutOfHoraire - milliSecondsInNonWorkingDays;
		
		double duration = workingDurationMilli + (milliSecondsOutOfHoraire * nonWorkingMinutesFactor) + (milliSecondsInNonWorkingDays * nonWorkingDaysFactor);
		duration = duration / FCalendar.MILLISECONDS_IN_MINUTE;
		return duration;
	}
	
	public double getNumberOfWorkingMinutesBetween(Date startDate, Date endDate){
		double number1 = getDurationInMinutesBetweenMidNightOfFirstDateAndEndDate(startDate, startDate, 0, 0);
		double number2 = getDurationInMinutesBetweenMidNightOfFirstDateAndEndDate(startDate, endDate, 0, 0);
		return number2 - number1;
	}
	
	public static Calendar getInstanceOfJavaUtilCalandar(){
		return Calendar.getInstance();
	}
	
	private Date rolDateToStartOfNextDay(Date date){
		Calendar cal = Calendar.getInstance();
		
		DayShift dayShift = getDayShiftForDate(new Date(date.getTime()+Globals.DAY_TIME));
		
		if(dayShift != null){ 
			Time time = dayShift.getStartTime();
			cal.setTime(time);
		}
		int houre = cal.get(Calendar.HOUR);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		int amPm = cal.get(Calendar.AM_PM);
		
		if(houre >= 12){
			houre = houre % 12;
			amPm = Calendar.PM;
		}
		
		cal.setTime(date);
		int oldDay = cal.get(Calendar.DAY_OF_MONTH);
		int oldMonth = cal.get(Calendar.MONTH);
		cal.roll(Calendar.DAY_OF_MONTH, true);
		if(cal.get(Calendar.DAY_OF_MONTH) < oldDay){
			cal.roll(Calendar.MONTH, true);
		}
		if(cal.get(Calendar.MONTH) < oldMonth){
			cal.roll(Calendar.YEAR, true);
		}

		cal.set(Calendar.HOUR, houre);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		cal.set(Calendar.AM_PM, amPm);
		
		return new Date(cal.getTimeInMillis());
	}
	
	public Date getNeerestWorkingDayForDate(Date date){
		Date nearestWorking = (Date) date.clone();
		while(!isWorkingDay(nearestWorking)){
			nearestWorking.setTime(nearestWorking.getTime() + Globals.DAY_TIME);
		}
		return nearestWorking;
	}
	
	public Date getNeerestWorkingTimeForDate(Date date, boolean includeThisDate, boolean reverse){
		Date nextWorkingDate = null;
		if(date != null){
			nextWorkingDate =(Date)date.clone();
			long     timeSinceMidnight = getTimeSinceMidnight(nextWorkingDate);
			DayShift dayShift          = getDayShiftForDate(nextWorkingDate);
			long     startTime         = dayShift != null ? dayShift.getStartTime().getTime() : 0;
			if(startTime > timeSinceMidnight){//isBeforeStartWorkingTime
				nextWorkingDate.setTime(getTimeAtMidnight(nextWorkingDate) + startTime);
			}
		}
		if(nextWorkingDate != null){
			boolean searchInNextDays = true;
			if(includeThisDate){
				long     timeSinceMidNight = getTimeSinceMidnight(nextWorkingDate);
				DayShift dayShift          = getDayShiftForDate(nextWorkingDate);
				long     endTime           = dayShift != null ? dayShift.getEndTime().getTime() : 0;
				searchInNextDays = timeSinceMidNight > endTime || !isWorkingDay(nextWorkingDate);
			}
			if(searchInNextDays){
				nextWorkingDate = reverse ? rolDateToEndOfPreviousDay(nextWorkingDate) : rolDateToStartOfNextDay(nextWorkingDate);
				while(!isWorkingDay(nextWorkingDate)){
					nextWorkingDate = reverse ? rolDateToEndOfPreviousDay(nextWorkingDate) : rolDateToStartOfNextDay(nextWorkingDate);
				}
			}
		}
		return nextWorkingDate;
	}
	
	public Date getNeerestWorkingTimeForDate(Date date, boolean includeThisDate){
		return getNeerestWorkingTimeForDate(date, includeThisDate, false);
	}
	
	private Date rolDateToEndOfPreviousDay(Date date){
		Calendar cal = FCalendar.getInstanceOfJavaUtilCalandar();
		
		DayShift dayShift = getDayShiftForDate(new Date(date.getTime() - Globals.DAY_TIME));
		if(dayShift != null){
			Time time = dayShift.getEndTime();
			cal.setTime(time);
		}
		int houre = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		
		cal.setTime(date);
		int oldDay = cal.get(Calendar.DAY_OF_MONTH);
		int oldMonth = cal.get(Calendar.MONTH);
		cal.roll(Calendar.DAY_OF_MONTH, false);
		if(cal.get(Calendar.DAY_OF_MONTH) > oldDay){
			cal.roll(Calendar.MONTH, false);
		}
		if(cal.get(Calendar.MONTH) > oldMonth){
			cal.roll(Calendar.YEAR, false);
		}

		cal.set(Calendar.HOUR_OF_DAY, houre);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		
		return new Date(cal.getTimeInMillis());
	}
	
	public Date getNeerestPreviousWorkingTimeForDate(Date date, boolean includeThisDate){
		Date previousWorkingDate = null;
		if(date != null){
			previousWorkingDate =(Date)date.clone();

			boolean searchInPreviousDays = true;
			if(includeThisDate){
				long timeSinceMidNight = getTimeSinceMidnight(previousWorkingDate);
				searchInPreviousDays = !isWorkTime(timeSinceMidNight) || !isWorkingDay(previousWorkingDate);
			}
			if(searchInPreviousDays){
				previousWorkingDate = rolDateToEndOfPreviousDay(previousWorkingDate);
				while(!isWorkingDay(previousWorkingDate)){
					previousWorkingDate = rolDateToEndOfPreviousDay(previousWorkingDate);
				}
			}
		}
		return previousWorkingDate;
	}

	public Date rolWorkingDays_START_OF_DAY(Date date, int nbrOfWorkingDaysToRol){
		Date finalDate = null;
		if(date != null && nbrOfWorkingDaysToRol >= 0){
			finalDate = (Date)date.clone();
			for(int i = 0; i < nbrOfWorkingDaysToRol; i++){
				finalDate = getNeerestWorkingTimeForDate(finalDate, false);
			}
		}
		return finalDate;
	}
	
	public Date rolWorkingDays_REVERSE(Date endDate, int nbrOfWorkingDaysToRol){
		Date startDate = null;
		if(endDate != null && nbrOfWorkingDaysToRol >= 0){
			startDate = (Date) endDate.clone();
			
			Calendar cal = getInstanceOfJavaUtilCalandar();
			cal.setTime(startDate);
			
			while(nbrOfWorkingDaysToRol > 0){
				rollTheCalendar_DayDown(cal);
				startDate = new Date(cal.getTime().getTime());
				if(isWorkingDay(startDate)){
					nbrOfWorkingDaysToRol--;
				}
			}
		}
		return startDate;
	}

	public Date[] getNeerestStartEndDateForDuration(Date startDate, double durationInMinutes, boolean allowBreakingThroughtDays){
		return getNeerestStartEndDateForDuration(startDate, durationInMinutes, UnitDesc.getUnit(DimensionDesc.DIMENSION_TIME, UnitDesc.TIME_UNIT_MINUTE), allowBreakingThroughtDays);
	}
	
	public Date[] getNeerestStartEndDateForDuration(Date startDate, double duration, Unit durationUnit, boolean allowBreakingThroughtDays){
		return getNeerestStartEndDateForDuration(startDate, duration, durationUnit, allowBreakingThroughtDays, false);
	}
	
	public Date[] getNeerestStartEndDateForDuration(Date startDate, double duration, Unit durationUnit, boolean allowBreakingThroughtDays, boolean reverse){
		Date[] res = null;
		int one = reverse ? -1 : 1;
		if(startDate != null && duration >= 0){
			res = new Date[2];
			Date realStartingDate = getNeerestWorkingTimeForDate(startDate, true, reverse);// getNeerestWorkingDayForDate(startDate);
			res[0] = realStartingDate;
			Date endDate = new Date(realStartingDate.getTime());
			res[1] = endDate;
			
			if(durationUnit.isHour()){
				duration = duration * 60;
			}else if(durationUnit.isSecond()){
				duration = duration / 60;
			}
			if(durationUnit.isHour() || durationUnit.isMinute() || durationUnit.isSecond()){//20150923 - Introduction of the Seconds support
				double durationPassed = 0;
				long d1 = getTimeAtMidnight(realStartingDate);
				long t1 = getTimeSinceMidnight(realStartingDate);

				//long d2 = getTimeAtMidnight(date2)+Globals.DAY_TIME;
				if(d1 > Globals.DAY_TIME){
					DayShift dayShift = getDayShiftForDate(realStartingDate);
					long w1 = dayShift != null ? dayShift.getStartTime().getTime() : 0;
					long w2 = dayShift != null ? dayShift.getEndTime().getTime() : 0;
					
					long inactiveTimeIn24Hours = Globals.DAY_TIME - (w2-w1);
					long activeTimeIn24Hours   = w2-w1;
					
					//Duration is in minuts here
					while(durationPassed < duration){
						double increment = 0; 
						if(durationPassed == 0){
							if(reverse){
								d1 += Math.min(t1, w2);
								increment = Math.min(t1, w2);
							}else{
								d1 += Math.max(t1, w1);
								increment = w2 - Math.max(t1, w1);
								if(increment == 0){
									d1        = getTimeAtMidnight(realStartingDate) + 1 * Globals.DAY_TIME + w1;
									increment = activeTimeIn24Hours;
								}
							}
						}else{
							d1 = d1 + one * inactiveTimeIn24Hours;
							increment = activeTimeIn24Hours;
						}
						increment = increment / (60*1000);
						
						endDate.setTime(d1);
						while(!isWorkingDay(endDate)){
							d1 = d1 + one * Globals.DAY_TIME;
							endDate.setTime(d1);
						}

						if(increment > duration - durationPassed){
							increment = duration - durationPassed;
						}

						d1 = d1 + one * (long)(increment * 60*1000);
						durationPassed += increment;
						
						//endDate.setTime(endDate.getTime()+(ksour ? (long)(increment * 60000) : Globals.DAY_TIME));
					}
				}
				endDate.setTime(d1);
			}else if(durationUnit.isDay() || durationUnit.isWeek()){
				if(durationUnit.isWeek()) duration = duration * getNumberOfOpenDaysInAWeek();
				
				//move until first working day
				while(!isWorkingDay(endDate)){
					endDate.setTime(endDate.getTime() + one * FCalendar.MILLISECONDS_IN_DAY);
				}
				
				if(duration > 0){
					duration = duration-1;
					endDate.setTime(endDate.getTime() + one * (FCalendar.MILLISECONDS_IN_DAY - 1));
					
					while(duration > 0){
						endDate.setTime(endDate.getTime() + one * FCalendar.MILLISECONDS_IN_DAY);					
						if(isWorkingDay(endDate)){
							duration--;
						}
					}
				}
			}else if(durationUnit.isMonth()){
				Calendar cal = FCalendar.getInstanceOfJavaUtilCalandar();
				cal.setTime(endDate);
				while(duration >= 1){
					if(reverse){
						rollTheCalendar_MonthDown(cal);
					}else{
						rollTheCalendar_Month(cal);
					}
					
					duration--;
				}

				long t1 = cal.getTimeInMillis();
				
				if(duration > 0){
					if(reverse){
						rollTheCalendar_MonthDown(cal);
					}else{
						rollTheCalendar_Month(cal);
					}

					long t2 = cal.getTimeInMillis();
					long nbrOfDaysInThisMonth = one * (t2 - t1) / Globals.DAY_TIME;//This is the nbtr of days in this month 30/31/28/29
					
					duration = duration * nbrOfDaysInThisMonth;//Now I have days
					t1 = t1 + one * (long) duration * Globals.DAY_TIME;
				}

				endDate.setTime(t1);
			}else{
				Globals.showNotification("Unit Support", "Unit "+durationUnit.getName()+" Not supported by FCalendar.getNeerestStartEndDateForDuration()", IFocEnvironment.TYPE_ERROR_MESSAGE);
			}
		}
		return res;
	}

	public Date getNeerestEndOpenDateForDurationInDays(Date startDate, double duration){
		Date res = null;
		int one = 1;
		if(startDate != null && duration >= 0){
			Date realStartingDate = startDate;
			Date endDate = new Date(realStartingDate.getTime());
			res = endDate;
			
			//move until first working day
			while(!isWorkingDay(endDate)){
				endDate.setTime(endDate.getTime() + FCalendar.MILLISECONDS_IN_DAY);
			}
			
			if(duration > 0){
				duration = duration-1;
				endDate.setTime(endDate.getTime() + (FCalendar.MILLISECONDS_IN_DAY - 1));
				
				while(duration > 0){
					endDate.setTime(endDate.getTime() + FCalendar.MILLISECONDS_IN_DAY);					
					if(isWorkingDay(endDate)){
						duration--;
					}
				}
			}
		}
		return res;
	}
	
	/*
 	public Date[] getNeerestStartEndDateForDuration(Date startDate, double duration, Unit durationUnit, boolean allowBreakingThroughtDays){
		Date[] res = null;
		if(startDate != null && duration >= 0){
			res = new Date[2];
			Date realStartingDate = getNeerestWorkingTimeForDate(startDate, true);// getNeerestWorkingDayForDate(startDate);
			res[0] = realStartingDate;
			Date endDate = new Date(realStartingDate.getTime());
			res[1] = endDate;
			
			if(durationUnit.isHour()){
				duration = duration * 60;
			}
			if(durationUnit.isHour() || durationUnit.isMinute()){
				double durationPassed = 0;
				long d1 = getTimeAtMidnight(realStartingDate);
				long t1 = getTimeSinceMidnight(realStartingDate);

				//long d2 = getTimeAtMidnight(date2)+Globals.DAY_TIME;
				if(d1 > Globals.DAY_TIME){
					long w1 = getPropertyTime(FCalendarDesc.FLD_START_TIME).getTime();
					long w2 = getPropertyTime(FCalendarDesc.FLD_END_TIME).getTime();
					
					while(durationPassed < duration){
						if(durationPassed == 0){
							d1 += Math.max(t1, w1);
						}else{
							d1 += Globals.DAY_TIME - (w2-w1);
						}
						endDate.setTime(d1);
						while(!isWorkingDay(endDate)){
							d1 += Globals.DAY_TIME;
							endDate.setTime(d1);
						}
						double  increment = (w2-w1) / (60*1000);
						if(durationPassed == 0){
							increment = ((double)(w2 - Math.max(t1, w1))) / (60*1000);
						}
						if(increment > duration - durationPassed){
							increment = duration - durationPassed;
						}

						d1 += increment * 60*1000;
						durationPassed += increment;	
						//endDate.setTime(endDate.getTime()+(ksour ? (long)(increment * 60000) : Globals.DAY_TIME));
					}
				}
				endDate.setTime(d1);
			}else if(durationUnit.isDay() || durationUnit.isWeek()){
				if(durationUnit.isWeek()) duration = duration * getNumberOfOpenDaysInAWeek();
				
				//move until first working day
				while(!isWorkingDay(endDate)){
					endDate.setTime(endDate.getTime() + FCalendar.MILLISECONDS_IN_DAY);
				}
				
				duration = duration-1;
				endDate.setTime(endDate.getTime() + FCalendar.MILLISECONDS_IN_DAY - 1);
				
				while(duration > 0){
					endDate.setTime(endDate.getTime() + FCalendar.MILLISECONDS_IN_DAY);					
					if(isWorkingDay(endDate)){
						duration--;
					}
				}
			}else if(durationUnit.isMonth()){
				Calendar cal = FCalendar.getInstanceOfJavaUtilCalandar();
				cal.setTime(endDate);
				while(duration >= 1){
					rollTheCalendar_Month(cal);
					
					duration--;
				}

				long t1 = cal.getTimeInMillis();
				
				if(duration > 0){
					rollTheCalendar_Month(cal);
					long t2 = cal.getTimeInMillis();
					long nbrOfDaysInThisMonth = (t2 - t1) / Globals.DAY_TIME;//This is the nbtr of days in this month 30/31/28/29
					
					duration = duration * nbrOfDaysInThisMonth;//Now I have days
					t1 = t1 + (long) duration * Globals.DAY_TIME;
				}

				endDate.setTime(t1);
			}else{
				Globals.getDisplayManager().popupMessage("Unit "+durationUnit.getName()+" Not supported by FCalendar.getNeerestStartEndDateForDuration()");
			}
		}
		return res;
	}
	 */

	public static void rollTheCalendar_Year(Calendar cal){
		cal.roll(Calendar.YEAR, 1);	
	}

	public static void rollTheCalendar_Month(Calendar cal){
		if(cal.get(Calendar.MONTH) == Calendar.DECEMBER){
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			cal.roll(Calendar.YEAR, 1);
		}else{
			cal.roll(Calendar.MONTH, 1);	
		}
	}

	public static void rollTheCalendar_MonthDown(Calendar cal){
		if(cal.get(Calendar.MONTH) == Calendar.JANUARY){
			cal.set(Calendar.MONTH, Calendar.DECEMBER);
			cal.roll(Calendar.YEAR, -1);
		}else{
			cal.roll(Calendar.MONTH, -1);	
		}
	}

	public static void rollTheCalendar_Day(Calendar cal){
		cal.roll(Calendar.DAY_OF_MONTH, 1);
		if(cal.get(Calendar.DAY_OF_MONTH) == 1){
			rollTheCalendar_Month(cal);
		}
	}

	public static void rollTheCalendar_DayDown(Calendar cal){
		/*if(cal.get(Calendar.DAY_OF_MONTH) == 1){
			rollTheCalendar_MonthDown(cal);
		}
		cal.roll(Calendar.DAY_OF_MONTH, -1);*/
		rollTheCalendar_DayDown(cal, 1);
	}
	
	public static void rollTheCalendar_DayDown(Calendar cal, int numberOfDays){
		if(cal.get(Calendar.DAY_OF_MONTH) == 1){
			rollTheCalendar_MonthDown(cal);
		}
		cal.roll(Calendar.DAY_OF_MONTH, -numberOfDays);
	}

	/**
	 * 
	 * @param date1 
	 * 				first date
	 * @param date2
	 * 				second date
	 * @return ArrayList of Long each couple of elements are a start and an end of a working period
	 * 
	 */
	public ArrayList<Long> getArrayOfWorkingTimesBetweenDates(Date date1, Date date2){
		ArrayList<Long> array = new ArrayList<Long>();

		Date tempDate = new Date(0);
		
		//SHIFT
		//CORRECT
		
		long d1 = getTimeAtMidnight(date1);
		long d2 = getTimeAtMidnight(date2)+Globals.DAY_TIME;
		if(d1 > Globals.DAY_TIME){
			//long w1 = getPropertyTime(FCalendarDesc.FLD_START_TIME).getTime();
			//long w2 = getPropertyTime(FCalendarDesc.FLD_END_TIME).getTime();
	
			long d = d1;
			while(d < d2){
				Date date = new Date(d);
				DayShift shift = getDayShiftForDate(date);
				if(shift != null){
					tempDate.setTime(d+shift.getStartTime().getTime());
					if(isWorkingDay(tempDate)){
						array.add(d+shift.getStartTime().getTime());
						array.add(d+shift.getEndTime().getTime());
				 	}
				}
				d = d + Globals.DAY_TIME;
			}
		}
		
		return array;
	}
	
	public double getDurationForStartEndDates(Date startDate, Date endDate, Unit durationUnit){
		return getDurationForStartEndDates(startDate, endDate, durationUnit, true); 
	}

	//We should use the method:
	//public double getDurationForStartEndDates(Date startDate, Date endDate, Unit durationUnit, boolean openTimeOnly);
	//But we did not want to modify it and we do not need the fractions of days
	public double getDurationForStartEndDates_OpenDays(Date startDate, Date endDate){
		Date d = (Date) startDate.clone();
		
		double duration = 0;
		
		while(getTimeAtMidnight(d) <= getTimeAtMidnight(endDate)){
			if(isWorkingDay(d)){
				duration++;
			}
			
			Calendar cal = getInstanceOfJavaUtilCalandar();
			cal.setTime(d);
			rollTheCalendar_Day(cal);
			d = new Date(cal.getTimeInMillis()); 
		}
		return duration;
	}
	
	public double getDurationForStartEndDates(Date startDate, Date endDate, Unit durationUnit, boolean openTimeOnly){
		double duration = 0;
		if(durationUnit != null && startDate != null && endDate != null){
			if(durationUnit.isDay() || durationUnit.isWeek()){
				Date d = (Date) startDate.clone();
				duration = 0;
				boolean wasWorkingDay = isWorkingDay(d);
				while(d.getTime() + FCalendar.MILLISECONDS_IN_DAY <= endDate.getTime()){
					d.setTime(d.getTime() + FCalendar.MILLISECONDS_IN_DAY);
					wasWorkingDay = isWorkingDay(d);
					if(!openTimeOnly || wasWorkingDay){
						duration++;
					}
				}
				
				if(d.getTime() < endDate.getTime() && (!openTimeOnly || isWorkingDay(d))){
					long endDateSinceMidnight = getTimeSinceMidnight(endDate);
					//SHIFT
					//CORRECT
					long endWorkTime          = openTimeOnly ? getDayShiftForDate(endDate).getEndTime().getTime() : Globals.DAY_TIME * 24;
					long dSinceMidnight       = getTimeSinceMidnight(d);
					long startWorkTime        = openTimeOnly ? getDayShiftForDate(d).getEndTime().getTime() : 0;
					
					long lessThanADay = (Math.min(endDateSinceMidnight, endWorkTime) + getTimeAtMidnight(endDate)) - (Math.max(dSinceMidnight, startWorkTime) + getTimeAtMidnight(d));
					if(lessThanADay > 0){
						double nbrOfHoursInADay = openTimeOnly ? getNumberOfWorkingHoursInDay() : 24;
						duration += lessThanADay / (nbrOfHoursInADay * 60 * 60 * 1000);
					}
				}
				
				if(durationUnit.isWeek()) duration = duration / getNumberOfOpenDaysInAWeek();
			}else if(durationUnit.isHour() || durationUnit.isMinute()){
				ArrayList<Long> arr = null;
				if(openTimeOnly){
					arr = getArrayOfWorkingTimesBetweenDates(startDate, endDate);
				}else{
					arr = new ArrayList<Long>();
					arr.add(startDate.getTime());
					arr.add(endDate.getTime());
				}
				
				for(int i=0; i<arr.size(); i=i+2){
					long t1 = arr.get(i);
					long t2 = arr.get(i+1);
					if(i==0 && i==arr.size()-2){
						duration = duration + Math.min(t2, endDate.getTime()) - Math.max(t1, startDate.getTime());
					}else if(i==0){
						duration = duration + t2 - Math.max(t1, startDate.getTime()); 
					}else if(i==arr.size()-2){
						duration = duration + Math.min(t2, endDate.getTime()) - t1;
					}else{
						duration = duration + (t2-t1);
					}
				}
				//At that stage the duration is in milliseconds
				duration = duration / (60 * 1000);
				if(durationUnit.isHour()){
					duration = duration / 60;
				}
			}else if(durationUnit.isMonth()){
				Calendar cal = FCalendar.getInstanceOfJavaUtilCalandar();
				cal.setTime(startDate);
				int sYear  = cal.get(Calendar.YEAR); 
				int sMonth = cal.get(Calendar.MONTH);
				int sDay   = cal.get(Calendar.DAY_OF_MONTH);
				cal.setTime(endDate);
				int eYear  = cal.get(Calendar.YEAR); 
				int eMonth = cal.get(Calendar.MONTH);
				int eDay   = cal.get(Calendar.DAY_OF_MONTH);
				
				duration = (eYear - sYear) * 12 + eMonth - sMonth + (double)(eDay - sDay) / 30;
			}else{
				Globals.showNotification("Unit Support!", "Unit "+durationUnit.getName()+" Not supported by FCalendar.getDurationForStartEndDates()", IFocEnvironment.TYPE_ERROR_MESSAGE);
			}
		}
		return duration;
	}
	
	public static long getTimeAtMidnight(java.util.Date date){
		long l = 0;
    if(date != null){
      //long timeShift = Globals.getApp().getTimeZoneShift();
      TimeZone tz = TimeZone.getDefault();
      long theTime = date.getTime();
      long timeShift = -tz.getOffset(theTime);
      
      long timeSinceAbsoluteOrigine = theTime - timeShift;//AbsoluteOrigin is as 1/1/1970 at midnight
      l = timeSinceAbsoluteOrigine - (timeSinceAbsoluteOrigine % Globals.DAY_TIME);
      l = l + timeShift;
      
      /*long dayTime = (timeSinceAbsoluteOrigine / Globals.DAY_TIME);
      l = dayTime * Globals.DAY_TIME;
      l = l + timeShift; */
      
      /*Calendar cal1 = Calendar.getInstance();
      Calendar cal2 = Calendar.getInstance();
    	
	    cal1.setTime(date);
	    cal2.setTime(new Date(0));
	    cal2.set(Calendar.DAY_OF_MONTH, cal1.get(Calendar.DAY_OF_MONTH));
	    cal2.set(Calendar.MONTH, cal1.get(Calendar.MONTH));
	    cal2.set(Calendar.YEAR, cal1.get(Calendar.YEAR));    
	    cal2.set(Calendar.HOUR, 0);    
	    cal2.set(Calendar.MINUTE, 0);
	    cal2.set(Calendar.SECOND, 0);
	    cal2.set(Calendar.AM_PM, Calendar.AM);
	    cal2.set(Calendar.MILLISECOND, 0);
	    l = cal2.getTimeInMillis();*/
      
    }
    return l; 
	}
	
  public static long getTimeSinceMidnight(java.util.Date date) {
    Calendar cal1 = Calendar.getInstance();
    cal1.setTime(date);

    return cal1.getTimeInMillis() - getTimeAtMidnight(date);
  }

  public static long getDateForTimeSinceMidnight(java.util.Date timeSinceMidnight){
  	Calendar cal = Calendar.getInstance();
  	cal.setTime(timeSinceMidnight);
  	return cal.getTimeInMillis() + getTimeAtMidnight(new Date(0));
  }

  public static long compareTimesRegardlessOfDates(java.util.Date date1, java.util.Date date2) {
  	return getTimeSinceMidnight(date1) - getTimeSinceMidnight(date2);
  }

  public static long compareDatesRegardlessOfTime(java.util.Date date1, java.util.Date date2) {
  	return getTimeAtMidnight(date1) - getTimeAtMidnight(date2);
  }

  public static java.util.Date getExactFullDateFromDateAndTime(java.util.Date date, Time time){
    long combinedExactTime = getTimeAtMidnight(date) + getTimeSinceMidnight(time);
    return new Date(combinedExactTime);
	}
  
  public static java.util.Date getDateAndTimeFromProperties(FDate dateProperty, FTime timeProperty){
    Date date = dateProperty.getDate();
    Time time = timeProperty.getTime();
    
    long combinedExactTime = FCalendar.getTimeAtMidnight(date) + FCalendar.getTimeSinceMidnight(time);
    return new Date(combinedExactTime);
  }

  public static java.sql.Date getYear_1_JAN(){
  	Calendar cal = FCalendar.getInstanceOfJavaUtilCalandar();
  	cal.setTime(Globals.getApp().getSystemDate());
  	
  	cal.set(Calendar.MONTH, Calendar.JANUARY);
  	cal.set(Calendar.DAY_OF_MONTH, 1);
  	
  	Date date = new Date(cal.getTimeInMillis());
    return date;
  }
  
  public static java.sql.Date getLastYear_31_DEC(){
  	Calendar cal = FCalendar.getInstanceOfJavaUtilCalandar();
  	cal.setTime(Globals.getApp().getSystemDate());
  	
  	cal.set(Calendar.MONTH, Calendar.DECEMBER);
  	cal.set(Calendar.DAY_OF_MONTH, 31);
  	cal.set(Calendar.YEAR, cal.get(Calendar.YEAR)-1);
  	
  	Date date = new Date(cal.getTimeInMillis());
    return date;
  }
  
  public static java.sql.Date getLastYear_1_JAN(){
  	Calendar cal = FCalendar.getInstanceOfJavaUtilCalandar();
  	cal.setTime(Globals.getApp().getSystemDate());
  	
  	cal.set(Calendar.MONTH, Calendar.JANUARY);
  	cal.set(Calendar.DAY_OF_MONTH, 1);
  	cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
  	
  	Date date = new Date(cal.getTimeInMillis());
    return date;
  }
  
  public static java.sql.Date getBeforeLastYear_31_DEC(){
  	Calendar cal = FCalendar.getInstanceOfJavaUtilCalandar();
  	cal.setTime(Globals.getApp().getSystemDate());
  	
  	cal.set(Calendar.MONTH, Calendar.DECEMBER);
  	cal.set(Calendar.DAY_OF_MONTH, 31);
  	cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 2);
  	
  	Date date = new Date(cal.getTimeInMillis());
    return date;
  }
  
  public static long compareDatesIgnoreYear(java.util.Date date1, java.util.Date date2) {
  	int year = -1;
  	Calendar cal = FCalendar.getInstanceOfJavaUtilCalandar();
  	cal.setTime(date2);
  	year = cal.get(Calendar.YEAR);
  	
  	cal = FCalendar.getInstanceOfJavaUtilCalandar();
  	cal.setTime(date1);
  	cal.set(Calendar.YEAR, year);
  	date1 = new Date(cal.getTimeInMillis());
  	
  	return compareDatesRegardlessOfTime(date1, date2);
  }
  
  //====================================================
  //====================================================
  // CONVERSION IN TIME UNITS
  //====================================================
  //====================================================

  /*
  private ArrayList<> timeUnitsArray = null;
  
  public ArrayList<> getTimeUnitsArray(){
  	if(timeUnitsArray != null){
  		timeUnitsArray = new ArrayList<E>;
  		
  	}
  }
  */

  public Date getAbstractDate(){
  	if(ABSTRACT_DATE == null){
  		Calendar cal = getInstanceOfJavaUtilCalandar();
  		cal.set(Calendar.DAY_OF_MONTH, 1);
  		cal.set(Calendar.MONTH, Calendar.APRIL);
  		cal.set(Calendar.YEAR, 2000);
  		cal.set(Calendar.HOUR, 0);
  		cal.set(Calendar.MILLISECOND, 0);
  		cal.set(Calendar.SECOND, 0);
  		cal.set(Calendar.MINUTE, 0);
  		ABSTRACT_DATE = new Date(cal.getTimeInMillis()); 
  	}
  	return ABSTRACT_DATE;
  }
  
  public double convertDurations_OpenTimeOnly(double duration, Unit unit1, Unit unit2){
  	double convFactor = duration;
  	if(unit1 != null && unit2 != null && unit1.getReference().getInteger() != unit2.getReference().getInteger()){
	  	if(openTimeConversion == null){
	  		double nbrOpenDaysPerWeek = (double) getNumberOfOpenDaysInAWeek();
	  		double nbrHoursInADay     = getNumberOfWorkingHoursInDay();
	  		
	  		openTimeConversion = new HashMap<String, Double>();
	  		openTimeConversion.put(UnitDesc.TIME_UNIT_SECOND, 1/(nbrHoursInADay * 60 * 60));
	  		openTimeConversion.put(UnitDesc.TIME_UNIT_MINUTE, 1/(nbrHoursInADay * 60));
	  		openTimeConversion.put(UnitDesc.TIME_UNIT_HOUR, 1/nbrHoursInADay);
	  		openTimeConversion.put(UnitDesc.TIME_UNIT_DAY , 1.0);
	  		openTimeConversion.put(UnitDesc.TIME_UNIT_WEEK, nbrOpenDaysPerWeek);
	  		openTimeConversion.put(UnitDesc.TIME_UNIT_MONTH, 4 * nbrOpenDaysPerWeek);
	  		openTimeConversion.put(UnitDesc.TIME_UNIT_YEAR, 12 * 4 * nbrOpenDaysPerWeek);
	  	}
	  	
	  	Double d1 = openTimeConversion.get(unit1.getName());
	  	Double d2 = openTimeConversion.get(unit2.getName());
	
	  	if(d1 == null || d2 == null){
	  		Globals.showNotification("Unit Support!", "Cannot convert open time from unit "+unit1+" to "+unit2+"\nPlease contact 01Barmaja for assistance.", IFocEnvironment.TYPE_ERROR_MESSAGE);
	  	}else{
	  		convFactor = d2 != 0 ? d1 / d2 : 0;
	  	}
  	}  	
  	return convFactor; 
  	
  	/*
  	Date[] dates = getNeerestStartEndDateForDuration(getAbstractDate(), duration, unit1, true);
  	return getDurationForStartEndDates(dates[0], dates[1], unit2);
  	*/
  }
  
  public double convertDurations(double duration, Unit unit1, Unit unit2){
  	double newDur = duration;
  	
  	//unit1.get
  	
  	if((unit1.isTimeRelevant() && unit2.isTimeRelevant()) || (!unit1.isTimeRelevant() && !unit2.isTimeRelevant())){
  		newDur = duration * unit1.getFactor() / unit2.getFactor();
  	}else{
  		Globals.logString("ATTENTION TIME CONVERSION NOT SUPPORTED FROM TimeRelevant to Non TimeRelevant");
  		newDur = duration * unit1.getFactor() / unit2.getFactor();
  	}
  	return newDur;
  }
  
  public double getOverTimeAdditionalHours(Date date){
  	//Should be configurable
  	Globals.logString("Over Time Hours Should Be Configurable! Using 4 dy default!");
  	return 4;//getPropertyDouble(FCalendarDesc.FLD_OVER_TIME_ADDITIONAL_HOURS);
  }
  
  public static String debug_CompareDates(Date d1){
  	SimpleDateFormat dsf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
  	return dsf.format(d1);
  }
  
  public static int computeNumberOfDays(Date date1, Date date2){
  	long time = compareDatesRegardlessOfTime(date2, date1);
  	return (int)(time / Globals.DAY_TIME);
  }
  
  public static Date newDate(int year, int month, int dayOfMonth){
  	return newDate(year, month, dayOfMonth, 0, 0, 0);
  }
  
  public static Date newDate(int year, int monthNumberStarting_1, int dayOfMonth, int hour, int min, int sec){
		Calendar cal = getInstanceOfJavaUtilCalandar();
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		cal.set(Calendar.MONTH, monthNumberStarting_1-1);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.HOUR, hour);
		cal.set(Calendar.SECOND, sec);
		cal.set(Calendar.MINUTE, min);
		return new Date(cal.getTimeInMillis()); 
  }
}
