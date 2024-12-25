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
package com.foc.forecast;

import java.awt.Color;
import java.sql.Date;

import com.foc.Globals;
import com.foc.business.calendar.FCalendar;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;

public class Forecast extends FocObject {
  
  public Forecast(FocConstructor constr){
    super(constr);
    newFocProperties();
  }
  
  public void dispose(){
    super.dispose();
  }
  
  public void copyValues(Forecast src){
  	for(int i=0; i<src.getValueCount(); i++){
  		setValueAt(i, getValueAt(i) + src.getValueAt(i));
  	}
  }
  
  public String getLabel(){
  	String label = getPropertyString(ForecastDesc.FLD_LABEL);
  	return label;
  }
  
  public void setLabel(String label){
  	setPropertyString(ForecastDesc.FLD_LABEL, label);
  }  
  
  public int getForecastGranularity(){
  	return getPropertyInteger(ForecastDesc.FLD_FORECAST_GRANULARITY);
  }
  
  public void setForecastGranularity(int granularity){
  	setPropertyInteger(ForecastDesc.FLD_FORECAST_GRANULARITY, granularity);
  }
  
  public Date getStartDate(){
    return getPropertyDate(ForecastDesc.FLD_START_DATE);
  }

  public void setStartDate(Date date){
    setPropertyDate(ForecastDesc.FLD_START_DATE, date);
  }
  
  public Date getEndDate(){
    return getPropertyDate(ForecastDesc.FLD_END_DATE);
  }
  
  public void setEndDate(Date date){
    setPropertyDate(ForecastDesc.FLD_END_DATE, date);
  }
  
  public ForecastedObject getForecastedObject(){
    return (ForecastedObject) getPropertyObject(ForecastDesc.FLD_FORECASTED_OBJECT);
  }
  
  public void setForecastedObject(FocObject forecasted){
    setPropertyObject(ForecastDesc.FLD_FORECASTED_OBJECT, forecasted);
  }

  public ForecastDesc getForecastDesc(){
  	return (ForecastDesc) getThisFocDesc();
  }
  
  public double getValue(int originalFieldID){
  	ForecastDesc focDesc = getForecastDesc();
  	int id = focDesc.getFieldIDFromOriginal(originalFieldID);
  	return getPropertyDouble(id);
  }

  public void setValue(int originalFieldID, double value){
  	ForecastDesc focDesc = getForecastDesc();
  	int id = focDesc.getFieldIDFromOriginal(originalFieldID);
  	setPropertyDouble(id, value);
  }
  
  public int getValueCount(){
  	ForecastDesc focDesc = getForecastDesc();
  	return (focDesc.FLD_NEXT_VALUE - ForecastDesc.FLD_FIRST_VALUE);
  }
  
  public double getValueAt(int at){
  	return getPropertyDouble(ForecastDesc.FLD_FIRST_VALUE+at);
  }

  public void setValueAt(int at, double val){
  	setPropertyDouble(ForecastDesc.FLD_FIRST_VALUE+at, val);
  }

  protected boolean shouldBeInDataBase(){
    boolean allZeros = true;
    for(int i=0; i<getValueCount() && allZeros; i++){
    	allZeros = allZeros && getValueAt(i) == 0;
    }
    return !allZeros;
  }
  
  public boolean commitStatusToDatabase(){
  	boolean error = false;
    if(!shouldBeInDataBase()){
    	if(isCreated()){
    		resetStatus();
    	}else{
    		resetStatus();
    		setDeleted(true);
    	}
    }
    error = super.commitStatusToDatabase();
    return error;
  }
  
  public double getForecastValidityRatio(){
  	//double ratio = -1;
  	double ratio = 0;
  	ForecastedObject forecasted = getForecastedObject();
  	if(forecasted != null){
	  	Date minEstimatedDate = forecasted.getFirstValidEntryDate();
			Date maxEstimatedDate = forecasted.getLastValidEntryDate();
			long minEstimateTime  = minEstimatedDate == null ? 0 : minEstimatedDate.getTime();
			long maxEstimateTime  = maxEstimatedDate == null ? 0 : maxEstimatedDate.getTime();
			if(    minEstimateTime < Globals.DAY_TIME 
					|| maxEstimateTime < Globals.DAY_TIME
					|| (getStartDate().getTime() > minEstimateTime && getStartDate().getTime() < maxEstimateTime)
					|| (getEndDate().getTime() > minEstimateTime && getEndDate().getTime() < maxEstimateTime)){
				
				long minTime = Math.max(minEstimateTime, getStartDate().getTime());
				minTime = Math.min(minTime, getEndDate().getTime());
				long maxTime = Math.min(maxEstimateTime, getEndDate().getTime());
				maxTime = Math.max(maxTime, getStartDate().getTime());
						
				double denom = getEndDate().getTime() - getStartDate().getTime();
				if(denom != 0){
					ratio = (double)(maxTime - minTime) / denom;
				}else{
					ratio = 1;
				}
				
				//ratio = 1;
			}else if(getStartDate().getTime() <= minEstimateTime && getEndDate().getTime() >= maxEstimateTime){
				//This means that the start end date of activity are completely included in the period
				ratio = 1;
			} 
			/*
			if(minEstimateTime > Globals.DAY_TIME && maxEstimateTime > Globals.DAY_TIME){
				long minTime = Math.max(minEstimateTime, getStartDate().getTime());
				minTime = Math.min(minTime, getEndDate().getTime());
				long maxTime = Math.min(maxEstimateTime, getEndDate().getTime());
				maxTime = Math.max(maxTime, getStartDate().getTime());
						
				double denom = getEndDate().getTime() - getStartDate().getTime();
				if(denom != 0){
					ratio = (double)(maxTime - minTime) / denom;
				}
			}
			*/
  	}
		return ratio;
  }
  
  public boolean isOutOfDisplayRange(){
  	boolean          out        = false;
  	ForecastedObject forecasted = getForecastedObject();
  	
  	Date firstLimit = forecasted.getForecastFirstDate();  	
  	Date lastLimit  = forecasted.getForecastLastDate();
  	
  	if(!FCalendar.isDateZero(firstLimit)){
  		out = getEndDate().before(firstLimit);
  	}

  	if(!out && !FCalendar.isDateZero(lastLimit)){
  		out = getStartDate().before(lastLimit);
  	}
  	
  	return out;
  }
  
  public Color getColorAccordingToRatio(){
  	Color  color = null; 
		double ratio = getForecastValidityRatio();
		if(ratio == 0){
			//color = Color.black;
			color = Color.LIGHT_GRAY;
		}else{
			color = Color.WHITE;
		}
	  return color;
  }
}
