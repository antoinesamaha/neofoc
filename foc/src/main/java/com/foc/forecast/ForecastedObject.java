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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.foc.Globals;
import com.foc.business.calendar.FCalendar;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldPath;
import com.foc.gui.table.FColumnGroup;
import com.foc.gui.table.FColumnGroupHeaderConstructor;
import com.foc.gui.table.FTableColumn;
import com.foc.gui.table.FTableColumnRepresentation;
import com.foc.gui.table.FTableView;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;
import com.foc.property.FProperty;
import com.foc.util.FocMath;

import java.awt.Color;
import java.sql.Date;

public abstract class ForecastedObject extends FocObject {

	public abstract int              getForecastMode();
	public abstract boolean          isForecastModeEditable();	
	public abstract Date             getForecastFirstDate();
	public abstract Date             getForecastLastDate();
	public abstract Date             getForecastDisplayFirstDate();
	public abstract Date             getForecastDisplayLastDate();
	public abstract FocList          getForecastList_ToImplement_BringDatabaseForecasts();
	public abstract ForecastedObject getRootForecastedObject();
	public abstract int              getForecastedFirstFieldID();
	public abstract Date             getFirstValidEntryDate();
	public abstract Date             getLastValidEntryDate();

  public static final int GRANULARITY_NONE               = 0;
  public static final int GRANULARITY_DAILY              = 1;
  public static final int GRANULARITY_WEEKLY             = 2;
  public static final int GRANULARITY_MONTHLY            = 3;
  public static final int GRANULARITY_QUARTER_OF_A_MONTH = 4;
  public static final int GRANULARITY_3_MONTHS           = 5;
  public static final int GRANULARITY_6_MONTHS           = 6;
  public static final int GRANULARITY_MAX                = 7;
  
  public static final Color OFF_WHITE = new Color(255, 255, 180);
  
	private FocList focList_Loaded        = null;//List as loaded from database
	private FocList focList               = null;//Used Forecast list for display and compute...
	//Sometimes focList == focList_Loaded when we are in editing granularity 
	private boolean forecastListCompleted = false;

	public final static int FORECAST_DISTRIBUTION_UNIFORMLY  = 0;
	public final static int FORECAST_DISTRIBUTION_UP_FRONT   = 1;
	public final static int FORECAST_DISTRIBUTION_AT_THE_END = 2;
	public final static int FORECAST_DISTRIBUTION_REPEATED   = 3;
	
  public ForecastedObject(FocConstructor constr){
    super(constr);
  }

  @Override
  public FProperty getFocProperty(int fieldID){
    FProperty prop = super.getFocProperty(fieldID);
    if(prop == null && fieldID >= getForecastedFirstFieldID() && (getForecastedFirstFieldID() > FField.FLD_SHIFT_FOR_FAB_FIELDS || fieldID < FField.FLD_SHIFT_FOR_FAB_FIELDS)){
    	FocList      forecastList = getForecastList_Completed();
    	ForecastDesc forecastDesc = (ForecastDesc) forecastList.getFocDesc();
    	
    	int objectIndex  = (fieldID - getForecastedFirstFieldID()) / forecastDesc.getNumberOfForecastedValues();
    	int valueFieldID = (fieldID - getForecastedFirstFieldID()) % forecastDesc.getNumberOfForecastedValues();
    	valueFieldID     = /*forecastDesc.getOriginalFieldID(*/ForecastDesc.FLD_FIRST_VALUE+valueFieldID/*)*/;
    	
    	Forecast forecast = (Forecast) forecastList.getFocObject(objectIndex);
    	prop = forecast != null ? forecast.getFocProperty(valueFieldID) : null;
    }
    return prop;
  }
  
  public FocList getForecastList(){
  	if(focList == null){
  		focList_Loaded = getForecastList_ToImplement_BringDatabaseForecasts();
  		//BDebug
  		if(focList_Loaded == null){
  			focList_Loaded = getForecastList_ToImplement_BringDatabaseForecasts();
  		}
  		//EDebug
  		if(focList_Loaded != null){
		  	if(!isForecastModeEditable()){
		  		focList = new FocList(new FocLinkSimple(focList_Loaded.getFocDesc()), null);
		  		focList.setDbResident(false);
		  		focList.setDirectlyEditable(true);
		  	}else{
		  		focList = focList_Loaded;
		  	}
  		}
	  	if(focList != null){
	  		focList.setListOrder(new FocListOrder(ForecastDesc.FLD_START_DATE));
	  	}
	  	
	  	if(focList != null){
		  	int granularity = getForecastMode();
		  	for(int i=0; i<focList.size(); i++){
		  		Forecast forecast = (Forecast) focList.getFocObject(i);
		  		forecast.setForecastGranularity(granularity);
		  	}
	  	}
  	}
  	return focList;
  }

  public Date getForecastFirstDateAtMidnight(){
  	return new Date(FCalendar.getTimeAtMidnight(getForecastFirstDate()));
  }

  public Date getForecastLastDateAtMidnight(){
  	return new Date(FCalendar.getTimeAtMidnight(getForecastLastDate()));
  }

  public Date getForecastDisplayFirstDateAtMidnight(){
  	return new Date(FCalendar.getTimeAtMidnight(getForecastDisplayFirstDate()));
  }

  public Date getForecastDisplayLastDateAtMidnight(){
  	return new Date(FCalendar.getTimeAtMidnight(getForecastDisplayLastDate()));
  }
  
  public void outputDebug(){
	  FocList focList = getForecastList();
	  SimpleDateFormat dateForemat = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
	  for(int i=0; i<focList.size(); i++){
	  	Forecast fcst = (Forecast) focList.getFocObject(i);
	  	Globals.logString("Label ="+fcst.getLabel()+" start="+dateForemat.format(fcst.getStartDate())+" end="+dateForemat.format(fcst.getEndDate()));
	  }
  }
  
  public int getForecastValueFieldIDForOriginalFieldAndPeriod(int periodIndex, int originalFieldID){
  	FocList      forecastList                = getForecastList_Completed();
  	ForecastDesc forecastDesc                = (ForecastDesc) forecastList.getFocDesc();
  	int          fieldIDInForecast           = forecastDesc.getFieldIDFromOriginal(originalFieldID);
  	int          incrementInForecastedObject = fieldIDInForecast - forecastDesc.getFirstValueFieldID();
  	int          fldID                       = getForecastedFirstFieldID()+(periodIndex * getForecastedValuesCount()) + incrementInForecastedObject;  	
  	return fldID;
  }
  
  public FCalendar getCalendar(){
  	return FCalendar.getDefaultCalendar();
  }
  
  public FocList getForecastList_Completed(){
  	FocList forecastList = getForecastList();
  	if(!isForecastListCompleted()){
	  	ForecastedObject root = getRootForecastedObject();
	
	  	boolean needToCopyTheLoadedToTheWorkList = focList != focList_Loaded && focList.size() == 0;
	  	
	  	removeForecastsOutOfInterval();
	  	
	  	if(root != this){
	  		completeList_ByCopy(root);
	  	}else{
	  		completeList();
	  	}
	  	
	  	if(needToCopyTheLoadedToTheWorkList){
	  		for(int i=0; i<focList_Loaded.size(); i++){
	  			Forecast srcForecast = (Forecast) focList_Loaded.getFocObject(i);
	  			Forecast tarForecast = findForecast(srcForecast.getStartDate());
	  			if(tarForecast != null){
	  		  	ForecastDesc forecastDesc = (ForecastDesc) forecastList.getFocDesc();
	  		  	
	  		  	if(forecastDesc != null){
	  		  		for(int f=0; f<forecastDesc.getNumberOfForecastedValues(); f++){
	  		  			int theForecast_FieldID  = ForecastDesc.FLD_FIRST_VALUE + f;
	
	  		  			double d1    = tarForecast.getPropertyDouble(theForecast_FieldID);
	  		  			double delta = srcForecast.getPropertyDouble(theForecast_FieldID);
	  		  			tarForecast.setPropertyDouble(theForecast_FieldID, d1 + delta);
	  		  		}
	  		  	}
	  			}
	  		}
	  	}
	  	setForecastListCompleted(true);
  	}
  	
  	return forecastList;
  }
  
  public void extendList(){
  	
  }
  
  public void removeForecastsOutOfInterval(){
    Date startDate = getForecastDisplayFirstDateAtMidnight();
    Date endDate   = getForecastDisplayLastDateAtMidnight();
  	
    if(focList != null){
	    for(int i=focList.size(); i>=0; i--){
	    	Forecast forecast = (Forecast) focList.getFocObject(i);
	    	if(forecast != null){
	    		if(forecast.getStartDate().getTime() > endDate.getTime() || forecast.getEndDate().getTime() < startDate.getTime()){
	    			focList.remove(forecast);
	    		}
	    	}
	    }
    }
  }
  
  public void completeList_ByCopy(ForecastedObject root){
  	Calendar cal = Calendar.getInstance();
  
  	ArrayList<Forecast> arrayToInsert = new ArrayList<Forecast>();
  	for(int i=0; i<root.getForecastList_Completed().size(); i++){
  		Forecast srcForecast = (Forecast) root.getForecastList_Completed().getFocObject(i);
  		Forecast tarForecast = (Forecast) findForecast((srcForecast.getStartDate().getTime()+srcForecast.getEndDate().getTime())/2);
  		
  		if(tarForecast == null){
  			arrayToInsert.add(srcForecast);		
  		}else{
  			tarForecast.setStartDate(srcForecast.getStartDate());
  			tarForecast.setEndDate(srcForecast.getEndDate());
  			tarForecast.setLabel(srcForecast.getLabel());
  		}
  	}
  	
  	for(int i=0; i<arrayToInsert.size(); i++){
  		Forecast srcForecast = arrayToInsert.get(i);
  		if(srcForecast != null){
  			createForecast(cal, srcForecast.getStartDate(), srcForecast.getEndDate(), false);		
  		}
  	}
  }
  
  private void completeList(){
    Date startDate = getForecastDisplayFirstDateAtMidnight();
    Date endDate   = getForecastDisplayLastDateAtMidnight();
  	
  	completeList(startDate, endDate);
  }

  /*
  private void extendList(Date endDate){
  	Date startDate = getForecastDisplayFirstDateAtMidnight();
  	if(getForecastPeriodCount() > 0){
  		Forecast lastForecast = getForecastPeriodAt(getForecastPeriodCount()-1);
  		startDate = lastForecast.getEndDate();
  	}
  	completeList(startDate, endDate);
  }
  */
  
  private void completeList(Date startDate, Date endDate){
  	int      mode = getForecastMode();
    Calendar cal  = Calendar.getInstance();
        
  	if(mode == GRANULARITY_DAILY){
      while(FCalendar.getTimeAtMidnight(startDate) != FCalendar.getTimeAtMidnight(endDate)){
     		createForecast(cal, startDate, new Date(startDate.getTime()+Globals.DAY_TIME));
        startDate = FCalendar.shiftDate(cal, startDate, 1);
      }
 
    }else if(mode == GRANULARITY_WEEKLY){
      FCalendar fCal = FCalendar.getDefaultCalendar();
      if(fCal != null){
        startDate = fCal.getFirstDayOfWeek(startDate);
        if(FCalendar.getTimeAtMidnight(endDate) != FCalendar.getTimeAtMidnight(fCal.getFirstDayOfWeek(endDate))){
          endDate = FCalendar.shiftDate(Calendar.getInstance(), endDate, 7);
          endDate = fCal.getFirstDayOfWeek(endDate);
        }
        while(FCalendar.getTimeAtMidnight(startDate) < FCalendar.getTimeAtMidnight(endDate)){
          Date shiftedEndDate = FCalendar.shiftDate(cal, startDate, 7);
          createForecast(cal, startDate, new Date(shiftedEndDate.getTime()-1000));
          startDate = shiftedEndDate;
        }
      }
	      
    }else if(mode == GRANULARITY_MONTHLY || mode == GRANULARITY_QUARTER_OF_A_MONTH){
      int currentMonth = -1;
      while(FCalendar.getTimeAtMidnight(startDate) != FCalendar.getTimeAtMidnight(endDate)){
        int monthNumber = FCalendar.getMonthNumber(startDate);
        
        if(monthNumber != currentMonth){
          
          Calendar tempCal = Calendar.getInstance();
          tempCal.setTime(startDate);
          
          if(tempCal.get(Calendar.DATE) != 1){
            tempCal.set(Calendar.DATE, 1);
          }
          
          Date budgetStartDate = new Date(tempCal.getTimeInMillis());
          int month = tempCal.get(Calendar.MONTH);
          
          if((month+1) > 11){
            tempCal.set(Calendar.MONTH, 0 );
            tempCal.set(Calendar.YEAR, tempCal.get(Calendar.YEAR)+1);
          }else{
            tempCal.set(Calendar.MONTH, month+1);
          }
          
          //tempCal.set(Calendar.MONTH, (month+1) > 11 ? 0 : month+1);
      
		      //tempCal.add(Calendar.DATE, -1);
		      Date budgetEndDate = new Date(tempCal.getTimeInMillis()-1);
		      
		      if(mode == GRANULARITY_MONTHLY){
		      	createForecast(cal, budgetStartDate, budgetEndDate);
		      }else if(mode == GRANULARITY_QUARTER_OF_A_MONTH){
		      	FCalendar cals     = getCalendar();
		      	Date[]    quarters = cals.getQuarterDatesOfMonth(new Date(startDate.getTime() + 2*Globals.DAY_TIME));
		      	
		      	createForecast(cal, quarters[0], new Date(quarters[1].getTime()-1));
		      	createForecast(cal, quarters[1], new Date(quarters[2].getTime()-1));
		      	createForecast(cal, quarters[2], new Date(quarters[3].getTime()-1));
		      	createForecast(cal, quarters[3], new Date(quarters[4].getTime()-1));
		      }
		      currentMonth = monthNumber;
		      //startDate = budgetEndDate;
		    }
		    startDate = FCalendar.shiftDate(cal, startDate, 1);
		  }
      
    }else if(mode == GRANULARITY_3_MONTHS || mode == GRANULARITY_6_MONTHS){
      int monthsToRol = mode == GRANULARITY_3_MONTHS ? 3 : 6;
      while(FCalendar.getTimeAtMidnight(startDate) < FCalendar.getTimeAtMidnight(endDate)){
        int monthNumber = FCalendar.getMonthNumber(startDate);
        
        Calendar tempCal = Calendar.getInstance();
        tempCal.setTime(startDate);
        
        if(tempCal.get(Calendar.DATE) != 1){
          tempCal.set(Calendar.DATE, 1);
        }
        
        Date budgetStartDate = new Date(tempCal.getTimeInMillis());
        for(int i=0; i<monthsToRol; i++){
        	FCalendar.rollTheCalendar_Month(tempCal);
        }
	      Date budgetEndDate = new Date(tempCal.getTimeInMillis()-1);
	      
	      createForecast(cal, budgetStartDate, budgetEndDate);
	      //startDate = budgetEndDate;

		    startDate = new Date(tempCal.getTime().getTime());
		  }
      
    }
  	
	  //Globals.logString("After complete");
	  //outputDebug();
  }

  //Dichotomy
  private Forecast findForecast(long startDateTime){
  	Forecast forecast = null;
  	if(focList != null && focList.size() > 0){
  		int min = 0;
  		int max = focList.size()-1;
  		
  		while(forecast == null && min <= max){
  			int      idx          = (max + min) / 2;
  			Forecast budgetPeriod = (Forecast)focList.getFocObject(idx);

        if(startDateTime >= budgetPeriod.getStartDate().getTime()){
        	if(startDateTime < budgetPeriod.getEndDate().getTime()){
        		forecast = budgetPeriod;
        	}
        	min = idx+1;
        }else{
        	max = idx-1;
        }
  		}
  	}
  	return forecast;
  }

  private Forecast findForecast(Date startDate){
  	return findForecast(startDate.getTime());
  }

  protected Forecast createForecast(Calendar cal, Date startDate, Date endDate){
  	return createForecast(cal, startDate, endDate, true);  	
  }
  
  protected Forecast createForecast(Calendar cal, Date startDate, Date endDate, boolean searchBeforeCreation){
  	FocList  focList      = getForecastList();//This has to be before the find so that we load the list
  	//The find is done on the date of the middle of the period, so that if there are any changes in border dates this does not make us lose the period
  	Forecast budgetPeriod = searchBeforeCreation ? findForecast((startDate.getTime()+endDate.getTime())/2) : null;
  	
  	if(budgetPeriod == null && focList != null){
	  	budgetPeriod = (Forecast) focList.newEmptyItem();
	  	budgetPeriod.setCreated(true);
	    budgetPeriod.setStartDate(startDate);
	    //Date endDateAtEndMidnight = FCalendar.shiftDate(cal, endDate, 1);
	    //long temp = endDateAtEndMidnight.getTime();
	    //endDateAtEndMidnight.setTime(temp-1);
	    //budgetPeriod.setEndDate(endDateAtEndMidnight);
	    budgetPeriod.setEndDate(endDate);
	    focList.add(budgetPeriod);
  	}
  	if(budgetPeriod != null){
  		Date dateForFormat = startDate;
  		if(!useStartDate()){
  			dateForFormat = endDate;
  		}
  		budgetPeriod.setLabel(getDateFormatAccordingToGranularity(dateForFormat, getForecastMode()));
  		budgetPeriod.setForecastGranularity(getForecastMode());
  		
  		budgetPeriod.setStartDate(startDate);
  		budgetPeriod.setEndDate(endDate);
  	}
    return budgetPeriod;
  }

  private String getDateFormatAccordingToGranularity(Date date, int mode){
    String format = "";
    if(mode == GRANULARITY_MONTHLY || mode == GRANULARITY_3_MONTHS || mode == GRANULARITY_6_MONTHS){
      SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-yyyy");
      format = dateFormat.format(date);
    }else if(mode == GRANULARITY_QUARTER_OF_A_MONTH){
      SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy");
      int quarter = getCalendar().getQuarterOfMonth(date);
      format = quarter + "-" + dateFormat.format(date);
    }else if(mode == GRANULARITY_WEEKLY){
      //SimpleDateFormat dateFormat = new SimpleDateFormat("w (dd/MM)-yyyy");
    	SimpleDateFormat dateFormat = new SimpleDateFormat("'W'w|dd MMM - MMM yy");
      format = dateFormat.format(date);
    }else{
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM yyyy");
      format = dateFormat.format(date);
    }
    
    return format;
  }
  
  public boolean useStartDate(){
  	return true;
  }
  
  public void getHeaderGroupForForecast(FTableView tableView){
  	getHeaderGroupForForecast(tableView, true);
  }
  
  public void getHeaderGroupForForecast(FTableView tableView, boolean writeEndTitles){
  	FColumnGroupHeaderConstructor columnGroupHeaderConstructor = tableView.getColumnGroupHeaderConstructor();

  	//Only the root needs to be clearly completed, Others will follow in the getForecastList_Comppleted()
  	FocList forecastList = getForecastList_Completed();
  	ForecastDesc forecastDesc = (ForecastDesc) forecastList.getFocDesc();
  	
  	FColumnGroup unForecastedGroup = new FColumnGroup("Non Projected");
  	columnGroupHeaderConstructor.addChildGroup(unForecastedGroup);
  	for(int f=0; f<forecastDesc.getNumberOfForecastedValues(); f++){
  		FField field = forecastDesc.getFieldByID(ForecastDesc.FLD_FIRST_VALUE+f);
  		int colID = forecastDesc.getOriginalUnforecastedFieldID(field.getID());
  		if(colID != FField.NO_FIELD_ID){
  			unForecastedGroup.addFTableColumn(tableView.addColumn(getThisFocDesc(), colID, false));
  		}
  	}
  	
  	FTableColumnRepresentation arrayColRep[] = new FTableColumnRepresentation[forecastDesc.getNumberOfForecastedValues()];
  	
  	FColumnGroup highTitleGroup    = null;
  	String       previousHighTitle = null;
    for(int i = 0; forecastList != null && i < forecastList.size(); i++){
    	Forecast     forecast     = (Forecast) forecastList.getFocObject(i);
    	
    	String fullLabel = forecast.getLabel();
    	ForecastTitle titleBuilder = new ForecastTitle(fullLabel);
    	//String lowTitle  = titleBuilder.getLowTitle();
    	//String highTitle = titleBuilder.getHighTitle();
    	
    	if(previousHighTitle == null || !previousHighTitle.equals(titleBuilder.getHighTitle())){
    		highTitleGroup = new FColumnGroup(titleBuilder.getHighTitle());
    		columnGroupHeaderConstructor.addChildGroup(highTitleGroup);
    	}
    	
    	FColumnGroup periodColumnGroup = highTitleGroup; 
    	if(writeEndTitles){
	    	periodColumnGroup = new FColumnGroup(titleBuilder.getLowTitle());
	    	highTitleGroup.addGroup(periodColumnGroup);
    	}

    	int numberOFForecastedValues = forecastDesc.FLD_NEXT_VALUE - ForecastDesc.FLD_FIRST_VALUE; 
    	for(int f=0; f<forecastDesc.getNumberOfForecastedValues(); f++){
    		FField field = forecastDesc.getFieldByID(ForecastDesc.FLD_FIRST_VALUE+f);
    		int colID = getForecastedFirstFieldID()+(i*numberOFForecastedValues) + f;

    		FTableColumnRepresentation tableColRepresentation = null; 
    		//Representation fields
    		if(i == 0){
    			int origFieldID = forecastDesc.getOriginalFieldID(ForecastDesc.FLD_FIRST_VALUE+f);
    			FField fld = getThisFocDesc().getFieldByID(origFieldID);
    			tableColRepresentation = new FTableColumnRepresentation(colID, fld.getTitle());
    			tableColRepresentation.setExplanation("Forecasted - "+fld.getTitle());
    			tableView.addColumnRepresentation(tableColRepresentation);
    			arrayColRep[f] = tableColRepresentation;
    		}else{
    			tableColRepresentation = arrayColRep[f];
    		}
    		
    		FTableColumn column = tableView.addColumn(getThisFocDesc(), colID, FFieldPath.newFieldPath(colID), (writeEndTitles ? field.getTitle() : titleBuilder.getLowTitle()), field.getSize(), true);
    		column.setCellEditor(field.getTableCellEditor(null));
    		column.setColumnRepresentation(tableColRepresentation);
    	  //FTableColumn column = tableView.addColumn(field, getForecastedFirstFieldID()+(i*numberOFForecastedValues) + f, field.getTitle(), field.getSize(), true);
    		periodColumnGroup.addFTableColumn(column);
    	}
    	
    	previousHighTitle = titleBuilder.getHighTitle();
    }
  }

  public int getForecastedValuesCount(){
  	FocList      forecastList = getForecastList();
  	ForecastDesc forecastDesc = (ForecastDesc) forecastList.getFocDesc();
  	return forecastDesc.getNumberOfForecastedValues();
  }

  public int getForecastPeriodCount(){
  	FocList forecastList = getForecastList_Completed();
  	return forecastList.size();
  }

  public Forecast getForecastPeriodAt(int periodIndex){
  	FocList forecastList = getForecastList_Completed();
  	return (Forecast) forecastList.getFocObject(periodIndex);
  }

  public Forecast getForecastPeriodForDate(java.util.Date date){
  	int p = getForecastPeriodIndexForDate(date);
  	Forecast foundForecast = (p >= 0) ? getForecastPeriodAt(p) : null;
  	
    //Dichotomy
  	/*
  	getForecastList_Completed();
  	Forecast foundForecast = null;
  	
  	int inf = 0;
  	int sup = getForecastPeriodCount()-1;
  	
  	while(foundForecast == null && inf <= sup){
    	int      idx      = (sup + inf) / 2;
  		Forecast forecast = getForecastPeriodAt(idx);
  		
  		if(date.getTime() > forecast.getEndDate().getTime()){
  			inf = idx+1;
  		}else if(date.getTime() < forecast.getStartDate().getTime()){
  			sup = idx-1;
  		}else{
  			foundForecast = forecast;
  		}
  	}
  	*/
  	
  	return foundForecast;
  }
  
  //Dichotomy
  public int getForecastPeriodIndexForDate(java.util.Date date){
  	getForecastList_Completed();
  	int foundForecastIndex = -1;
  	
  	int inf = 0;
  	int sup = getForecastPeriodCount()-1;
  	
  	while(foundForecastIndex == -1 && inf <= sup){
    	int      idx      = (sup + inf) / 2;
  		Forecast forecast = getForecastPeriodAt(idx);
  		
  		if(date.getTime() > forecast.getEndDate().getTime()){
  			inf = idx+1;
  		}else if(date.getTime() < forecast.getStartDate().getTime()){
  			sup = idx-1;
  		}else{
  			foundForecastIndex = idx;
  		}
  	}
  	
  	return foundForecastIndex;
  }  
  
  public void computeUnforecastedQuantities(){
  	FocList      forecastList = getForecastList_Completed();
  	ForecastDesc forecastDesc = (ForecastDesc) forecastList.getFocDesc();
  	
  	if(forecastDesc != null){
  		for(int f=0; f<forecastDesc.getNumberOfForecastedValues(); f++){
  			int theForecast_FieldID  = ForecastDesc.FLD_FIRST_VALUE + f;
  			int originalForecasted   = forecastDesc.getOriginalFieldID(theForecast_FieldID);
  			int originalUnforecasted = forecastDesc.getOriginalUnforecastedFieldID(theForecast_FieldID);
  			
  			double sum = 0;
  	  	for(int i=0; forecastList != null && i<forecastList.size(); i++){
  	  		Forecast forecast = (Forecast) forecastList.getFocObject(i);
  	  		if(forecast != null){
  	  			sum += forecast.getPropertyDouble(theForecast_FieldID);
  	  			
  	  			FProperty prop  = forecast.getFocProperty(theForecast_FieldID);
  	  			Color     color = forecast.getColorAccordingToRatio();
  	  			prop.setBackground(color);
  	  			
  	  			/*
  	  			if(ratio < 0){
  	  				prop.setBackground(null);
  	  			}else if(ratio == 0){
	  					prop.setBackground(Color.DARK_GRAY);
	  				}else{
	  					prop.setBackground(getColorForRatio(ratio));
	  				}
	  				*/
  	  		}
  	  	}
  	  	
  	  	if(originalUnforecasted != FField.NO_FIELD_ID){
  	  		setPropertyDouble(originalUnforecasted, getPropertyDouble(originalForecasted) - sum);
  	  	}
  		}
  	}
  }

  public void adjustForecastAccordingToSchedule(int originalFieldID, double quantity, int forecastDistribution){
  	adjustForecastAccordingToSchedule(originalFieldID, quantity, forecastDistribution, "");
  }
  
	public void adjustForecastAccordingToSchedule(int originalFieldID, double quantity, int forecastDistribution, String precision){
  	FocList      forecastList = getForecastList_Completed();
  	ForecastDesc forecastDesc = (ForecastDesc) forecastList.getFocDesc();
  	
  	if(forecastDesc != null){
  		//int theForecast_FieldID = forecastDesc.getFieldIDFromOriginal(originalFieldID);
  		double totalWeight    = 0;
  		int    lastRatioIndex = -1;
	  	for(int i=0; forecastList != null && i<forecastList.size(); i++){
	  		Forecast forecast = (Forecast) forecastList.getFocObject(i);
	  		if(forecast != null){
	  			double ratio = forecast.getForecastValidityRatio();
	  			if(ratio > 0){
	  				totalWeight += ratio;
	  				lastRatioIndex = i;
	  			}
	  		}
	  	}
	  	
	  	if(forecastDistribution == FORECAST_DISTRIBUTION_UNIFORMLY || forecastDistribution == FORECAST_DISTRIBUTION_REPEATED){
	  		double remainingQuantity = quantity;
		  	for(int i=0; forecastList != null && i<forecastList.size(); i++){
		  		Forecast forecast = (Forecast) forecastList.getFocObject(i);
		  		if(forecast != null){
		  			double ratio = forecast.getForecastValidityRatio();
		  			if(ratio > 0){
		  				double quantityToSet = 0;
		  				if(forecastDistribution == FORECAST_DISTRIBUTION_UNIFORMLY){
			  				if(i == lastRatioIndex){
			  					quantityToSet = remainingQuantity;
			  				}else{
				  				quantityToSet = quantity * ratio / totalWeight;
				  				quantityToSet = FocMath.round(quantityToSet, precision);
				  				remainingQuantity -= quantityToSet;			  				
			  				}
		  				}else{
		  					quantityToSet = quantity;
		  				}
		  				forecast.setValue(originalFieldID, quantityToSet);		  				
		  			}else{
		  				forecast.setValue(originalFieldID, 0);
		  			}
		  		}
		  	}
	  	}else{
	  		int firstIndex = -1;
	  		int lastIndex  = -1;
		  	for(int i=0; forecastList != null && i<forecastList.size(); i++){
		  		Forecast forecast = (Forecast) forecastList.getFocObject(i);
		  		if(forecast != null){
		  			double ratio = forecast.getForecastValidityRatio();
		  			if(ratio > 0){
		  				if(firstIndex < 0){
		  					firstIndex = i;
		  				}
		  			}
		  			if(ratio <= 0 && firstIndex >= 0 && lastIndex < 0){
	  					lastIndex = i;
	  				}
			  		forecast.setValue(originalFieldID, 0);
		  		}
		  	}
		  	if(forecastDistribution == FORECAST_DISTRIBUTION_UP_FRONT && firstIndex >= 0){
		  		Forecast forecast = (Forecast) forecastList.getFocObject(firstIndex);
		  		forecast.setValue(originalFieldID, quantity);
		  	}else if(forecastDistribution == FORECAST_DISTRIBUTION_AT_THE_END && lastIndex > 0){
		  		Forecast forecast = (Forecast) forecastList.getFocObject(lastIndex-1);
		  		forecast.setValue(originalFieldID, quantity);
		  	}
	  	}
  	}
	}

  public double computeTotalForecastedForOriginalField(int originalFld){
  	FocList      forecastList = getForecastList_Completed();
  	ForecastDesc forecastDesc = (ForecastDesc) forecastList.getFocDesc();
		double       sum          = 0;
  	
  	if(forecastDesc != null){
  		int theForecast_FieldID = forecastDesc.getFieldIDFromOriginal(originalFld);
  		
	  	for(int i=0; forecastList != null && i<forecastList.size(); i++){
	  		Forecast forecast = (Forecast) forecastList.getFocObject(i);
	  		if(forecast != null){
	  			sum += forecast.getPropertyDouble(theForecast_FieldID);
	  		}
	  	}
  	}
  	return sum;
  }
	public boolean isForecastListCompleted() {
		return forecastListCompleted;
	}
	public void setForecastListCompleted(boolean forecastListCompleted) {
		this.forecastListCompleted = forecastListCompleted;
	}
}
