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

import java.util.Calendar;

import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;

@SuppressWarnings("serial")
public class WeekShift extends FocObject {

	public WeekShift(FocConstructor constr){
	  super(constr);
		newFocProperties();
	} 
	
	public void dispose(){
		super.dispose();
	}
	
	private int getFieldIDForDayOfWeek(int dayOfWeek){
		int fieldID = 0;
		if(dayOfWeek == Calendar.SUNDAY){
			fieldID = WeekShiftDesc.FLD_SUNDAY_SHIFT;
		}else if(dayOfWeek == Calendar.MONDAY){
			fieldID = WeekShiftDesc.FLD_MONDAY_SHIFT;
		}else if(dayOfWeek == Calendar.TUESDAY){
			fieldID = WeekShiftDesc.FLD_TUSEDAY_SHIFT;
		}else if(dayOfWeek == Calendar.WEDNESDAY){
			fieldID = WeekShiftDesc.FLD_WEDNESDAY_SHIFT;
		}else if(dayOfWeek == Calendar.THURSDAY){
			fieldID = WeekShiftDesc.FLD_THURSDAY_SHIFT;
		}else if(dayOfWeek == Calendar.FRIDAY){
			fieldID = WeekShiftDesc.FLD_FRIDAY_SHIFT;
		}else if(dayOfWeek == Calendar.SATURDAY){
			fieldID = WeekShiftDesc.FLD_SATURDAY_SHIFT;
		}
		return fieldID;
	}
	
	private int getFieldIDForDayOfWeek_OverTimeLevel(int dayOfWeek){
		int fieldID = 0;
		if(dayOfWeek == Calendar.SUNDAY){
			fieldID = WeekShiftDesc.FLD_SUNDAY_OVERTIME_FACTOR;
		}else if(dayOfWeek == Calendar.MONDAY){
			fieldID = WeekShiftDesc.FLD_MONDAY_OVERTIME_FACTOR;
		}else if(dayOfWeek == Calendar.TUESDAY){
			fieldID = WeekShiftDesc.FLD_TUSEDAY_OVERTIME_FACTOR;
		}else if(dayOfWeek == Calendar.WEDNESDAY){
			fieldID = WeekShiftDesc.FLD_WEDNESDAY_OVERTIME_FACTOR;
		}else if(dayOfWeek == Calendar.THURSDAY){
			fieldID = WeekShiftDesc.FLD_THURSDAY_OVERTIME_FACTOR;
		}else if(dayOfWeek == Calendar.FRIDAY){
			fieldID = WeekShiftDesc.FLD_FRIDAY_OVERTIME_FACTOR;
		}else if(dayOfWeek == Calendar.SATURDAY){
			fieldID = WeekShiftDesc.FLD_SATURDAY_OVERTIME_FACTOR;
		}
		return fieldID;
	}
	
	public DayShift getDayShift(int dayOfWeek){
		int fldID = getFieldIDForDayOfWeek(dayOfWeek);
		return (DayShift ) getPropertyObject(fldID);
	}
	
	public void setDayShift(int dayOfWeek, DayShift dayShift){
		int fldID = getFieldIDForDayOfWeek(dayOfWeek);
		setPropertyObject(fldID, dayShift);
	}
	
	public int getOvertimeLevel(int dayOfWeek){
		int fldID = getFieldIDForDayOfWeek_OverTimeLevel(dayOfWeek);
		return getPropertyInteger(fldID);
	}
	
	public void setOvertimeLevel(int dayOfWeek, int level){
		int fldID = getFieldIDForDayOfWeek_OverTimeLevel(dayOfWeek);
		setPropertyInteger(fldID, level);
	}
	
	public int getNumberOfOpenDaysInAWeek(){
		int count = 0;
		
		if(getDayShift(Calendar.MONDAY) != null) count++;
		if(getDayShift(Calendar.TUESDAY) != null) count++;
		if(getDayShift(Calendar.WEDNESDAY) != null) count++;
		if(getDayShift(Calendar.THURSDAY) != null) count++;
		if(getDayShift(Calendar.FRIDAY) != null) count++;
		if(getDayShift(Calendar.SATURDAY) != null) count++;
		if(getDayShift(Calendar.SUNDAY) != null) count++;
		
		return count;
	}
}
