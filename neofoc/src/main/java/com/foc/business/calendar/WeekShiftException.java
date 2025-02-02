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
package com.foc.business.calendar;

import java.sql.Date;

import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;

public class WeekShiftException extends FocObject {

	private long startDateTime = 0;
	private long endDateTime   = 0;
	
	public WeekShiftException(FocConstructor constr){
	  super(constr);
	  newFocProperties();
	} 
	
	public void setStartDate(Date date){
		setPropertyDate(WeekShiftExceptionDesc.FLD_START_DATE, date);
	}
	
	public Date getStartDate(){
		return getPropertyDate(WeekShiftExceptionDesc.FLD_START_DATE);
	}

	public void setEndDate(Date date){
		setPropertyDate(WeekShiftExceptionDesc.FLD_END_DATE, date);
	}
	
	public Date getEndDate(){
		return getPropertyDate(WeekShiftExceptionDesc.FLD_END_DATE);
	}	
	
	public void setWeekShift(WeekShift reason){
		setPropertyObject(WeekShiftExceptionDesc.FLD_WEEK_SHIFT, reason);
	}
	
	public WeekShift getWeekShift(){
		return (WeekShift) getPropertyObject(WeekShiftExceptionDesc.FLD_WEEK_SHIFT);
	}

	public void recomputeStartEndDatesTime(){
		startDateTime = FCalendar.getTimeAtMidnight(getStartDate());
		endDateTime   = FCalendar.getTimeAtMidnight(getEndDate());
	}
	
	public long getStartDateTime() {
		return startDateTime;
	}

	public long getEndDateTime() {
		return endDateTime;
	}
}
