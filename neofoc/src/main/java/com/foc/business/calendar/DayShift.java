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
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class DayShift extends FocObject {

	public DayShift(FocConstructor constr){
	  super(constr);
		newFocProperties();
	} 
	
	public void dispose(){
		super.dispose();
	}

	public Time getStartTime(){
		Time 		time = null;
		FocList list = getShiftList();
		if(list.size() > 0){
			WorkShift workShift = (WorkShift) list.getFocObject(0);
			if(workShift != null){
				time = workShift.getStartTime();
			}
		}
		return time;
	}
	
	public Time getEndTime(){
		Time 		time = null;
		FocList list = getShiftList();
		if(list.size() > 0){
			WorkShift workShift = (WorkShift) list.getFocObject(list.size() - 1);
			if(workShift != null){
				time = workShift.getEndTime();
			}
		}
		return time;		
	}
	
	public FocList getShiftList(){
		FocList list = getPropertyList(DayShiftDesc.FLD_SHIFT_LIST);
		if(list.getListOrder() == null){
			list.setListOrder(new FocListOrder(WorkShiftDesc.FLD_START_TIME));
		}
		list.setDirectImpactOnDatabase(false);
		list.setDirectlyEditable(true);
		return list;
	}
	
	public double getNbrOfWorkingHours(){
		double hrs = 0;
		FocList focList = getShiftList();
		for(int i=0; i<focList.size(); i++){
			WorkShift workShift = (WorkShift) focList.getFocObject(i);
			hrs += workShift.getNbrOfHours();
		}
		return hrs;
	}
}
