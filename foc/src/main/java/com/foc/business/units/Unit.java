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
package com.foc.business.units;

import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;

public class Unit extends FocObject {
	public Unit(FocConstructor constr){
    super(constr);
 	  newFocProperties();
  }
	
	public void dispose(){
		super.dispose();
	}
	
	public void setName(String name){
		setPropertyString(UnitDesc.FLD_NAME, name);
	}
	
	public String getName(){
		return getPropertyString(UnitDesc.FLD_NAME);
	}
	
	public void setSymbol(String symbol){
		setPropertyString(UnitDesc.FLD_SYMBOL, symbol);
	}
	
	public String getSymbole(){
		return getPropertyString(UnitDesc.FLD_SYMBOL);
	}
  
  public double getFactor(){
    return getPropertyDouble(UnitDesc.FLD_FACTOR);
  }
  
  public void setFactor(double factor){
    setPropertyDouble(UnitDesc.FLD_FACTOR, factor);
  }

  public boolean isSecond(){
  	return getName().equals(UnitDesc.TIME_UNIT_SECOND);
  }
  
  public boolean isMinute(){
  	return getName().equals(UnitDesc.TIME_UNIT_MINUTE);
  }
  
  public boolean isHour(){
  	return getName().equals(UnitDesc.TIME_UNIT_HOUR);
  }

  public boolean isDay(){
  	return getName().equals(UnitDesc.TIME_UNIT_DAY);
  }
  
  public boolean isWeek(){
  	return getName().equals(UnitDesc.TIME_UNIT_WEEK);
  }

  public boolean isMonth(){
  	return getName().equals(UnitDesc.TIME_UNIT_MONTH);
  }
  
  public boolean isTimeRelevant(){
  	return isHour() || isMinute();
  }
  
  public Dimension getDimension(){
  	return (Dimension) getPropertyObject(UnitDesc.FLD_DIMENSION);
  }
}
