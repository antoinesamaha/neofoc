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

import com.foc.Globals;
import com.foc.business.units.Unit;

public class TimeConverter {
	
  private static boolean errorMessageOnce = false;
	
	public static double convert(Unit unit1, Unit unit2, double d){
		double ret = 0;
		if(unit2.getName().equals(unit1.getName())){
			ret = d;
		}else if(unit1.isMonth() && unit2.isDay()){
			ret = d * 30;
		}else if(unit1.isDay() && unit2.isMonth()){
			ret = d / 30; 
		}else{
			if(!errorMessageOnce){
				Globals.getDisplayManager().popupMessage("Conversion not supported!");
				errorMessageOnce = true;
			}
		}
		return ret;
	}
}
