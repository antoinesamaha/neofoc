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

import java.util.HashMap;

import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FDateTimeField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FMultipleChoiceField;

public class ForecastDesc extends FocDesc {
  
	public static final int FLD_LABEL                = 1;
  public static final int FLD_START_DATE           = 2;
  public static final int FLD_END_DATE             = 3;
  public static final int FLD_FORECASTED_OBJECT    = 4;
  public static final int FLD_FORECAST_GRANULARITY = 5;
  
  public static final int FLD_FIRST_VALUE    = 100;
  public              int FLD_NEXT_VALUE     = FLD_FIRST_VALUE;
  
  private HashMap<Integer, ForecastRelatedFields> original2Forecast = null;
  private HashMap<Integer, ForecastRelatedFields> forecast2Original = null;
  
  public ForecastDesc(Class focObjectClass, boolean dbResident, String storageName, boolean isKeyUnique) {
    super(focObjectClass, dbResident, storageName, isKeyUnique);
    
    FField focFld = addReferenceField();
    
    addForecastGranularityField(this, "FORECAST_GRANULARITY", FLD_FORECAST_GRANULARITY);
    
    focFld = new FStringField("LABEL", "Label", FLD_LABEL, false, 25);
    addField(focFld);
    
    focFld = new FDateTimeField("START_DATE", "Start Date", FLD_START_DATE, false);
    addField(focFld);

    focFld = new FDateTimeField("END_DATE", "End Date", FLD_END_DATE, false);
    addField(focFld);
    
    original2Forecast = new HashMap<Integer, ForecastRelatedFields>();
    forecast2Original = new HashMap<Integer, ForecastRelatedFields>();
  }
  
  public void dispose(){
  	super.dispose();
  	original2Forecast = null;
  	forecast2Original = null;
  }
  
  public int getFirstValueFieldID(){
  	return FLD_FIRST_VALUE;
  }
  
  public int getNumberOfForecastedValues(){
  	return FLD_NEXT_VALUE - FLD_FIRST_VALUE;
  }
  
  protected FField addForecastedField(FField originalField, FField unforecastedField, FField forecastedOutOfRangeField){
		FField                target        = null;  	
  	ForecastRelatedFields relatedFields = new ForecastRelatedFields(originalField.getID(), forecastedOutOfRangeField != null ? forecastedOutOfRangeField.getID() : FField.NO_FIELD_ID, unforecastedField != null ? unforecastedField.getID() : FField.NO_FIELD_ID, FLD_NEXT_VALUE);
  	original2Forecast.put(originalField.getID(), relatedFields);
  	forecast2Original.put(FLD_NEXT_VALUE       , relatedFields);
  	
  	if(originalField != null){
			try{
				target = (FField) originalField.clone();
				target.setExplanation("Forecasted - "+target.getTitle());
	  		target.setId(FLD_NEXT_VALUE);
	  		addField(target);
			}catch(CloneNotSupportedException e){
				Globals.logException(e);
			}
  	}
  	FLD_NEXT_VALUE++;
  	
  	if(originalField.getIndexOfPropertyInDummyArray() >= 0){
  		addDummyProperty(target.getID());
  	}
  	
  	return target;	
  }
  
  protected FField addForecastedField(FField originalField, FField unforecastedField){
  	return addForecastedField(originalField, unforecastedField, null);
  }

  public int getFieldIDFromOriginal(int original){
  	ForecastRelatedFields related = original2Forecast.get(original);
  	return related != null ? related.theForecast : FField.NO_FIELD_ID;
  }
  
  public int getOriginalFieldID(int fieldID){
  	ForecastRelatedFields related = forecast2Original.get(fieldID);
  	return related.forecasted;
  }

  public int getOriginalUnforecastedFieldID(int fieldID){
  	ForecastRelatedFields related = forecast2Original.get(fieldID);
  	return related.unforecasted;
  }

  public class ForecastRelatedFields{
  	public int forecasted                  = 0;
  	public int unforecasted                = 0;
  	public int forecastedOutOfDisplayRange = 0;
  	public int theForecast                 = 0;
  	
  	public ForecastRelatedFields(int forecasted, int unforecasted, int theForecast){
  		this.theForecast                 = theForecast;
  		this.unforecasted                = unforecasted;
  		this.forecasted                  = forecasted;
  		this.forecastedOutOfDisplayRange = 0; 
  	}

  	public ForecastRelatedFields(int forecasted, int unforecasted, int forecastedOutOfDisplayRange, int theForecast){
  		this.theForecast                 = theForecast;
  		this.unforecasted                = unforecasted;
  		this.forecastedOutOfDisplayRange = forecastedOutOfDisplayRange;   		
  		this.forecasted                  = forecasted;
  	}
  }
  
  public static FMultipleChoiceField addForecastGranularityField(FocDesc focDesc, String fieldName, int fldID){
  	FMultipleChoiceField multipleChoice = new FMultipleChoiceField(fieldName, "Forecast Granularity", fldID, false, 30);
	  multipleChoice.addChoice(ForecastedObject.GRANULARITY_NONE, "-- NONE --");
	  multipleChoice.addChoice(ForecastedObject.GRANULARITY_DAILY, "Daily");
	  multipleChoice.addChoice(ForecastedObject.GRANULARITY_WEEKLY, "Weekly");
	  multipleChoice.addChoice(ForecastedObject.GRANULARITY_MONTHLY, "Monthly");
	  multipleChoice.addChoice(ForecastedObject.GRANULARITY_QUARTER_OF_A_MONTH, "1/4 Month");
	  multipleChoice.addChoice(ForecastedObject.GRANULARITY_3_MONTHS, "3 Months");
	  multipleChoice.addChoice(ForecastedObject.GRANULARITY_6_MONTHS, "6 Months");
	  multipleChoice.setSortItems(false);
	  focDesc.addField(multipleChoice);
	  return multipleChoice;
  }
}
