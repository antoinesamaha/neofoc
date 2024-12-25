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

import java.sql.Time;

import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;

public class WorkShift extends FocObject {
	
	public WorkShift(FocConstructor constr){
	  super(constr);
		newFocProperties();
	} 
	
	public void dispose(){
		super.dispose();
	}

	public Time getStartTime(){
		return getPropertyTime(WorkShiftDesc.FLD_START_TIME); 
	}
	
	public void setStartTime(Time startTime){
		setPropertyTime(WorkShiftDesc.FLD_START_TIME, startTime); 
	}
	
	public void setStartTime(String startTime){
		setPropertyTime(WorkShiftDesc.FLD_START_TIME, startTime); 
	}

	public Time getEndTime(){
		return getPropertyTime(WorkShiftDesc.FLD_END_TIME); 
	}
	
	public void setEndTime(Time endTime){
		setPropertyTime(WorkShiftDesc.FLD_END_TIME, endTime); 
	}
	
	public void setEndTime(String endTime){
		setPropertyTime(WorkShiftDesc.FLD_END_TIME, endTime); 
	}
	
	public double getNbrOfHours(){
		double num   = getEndTime().getTime() - getStartTime().getTime();
		double denom = 3600000;//1000 * 60 * 60;
		return num / denom;
	}
}
