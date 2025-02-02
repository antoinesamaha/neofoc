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
package com.foc.gui.dateInterval;

import java.sql.Date;
import java.util.ArrayList;

import com.foc.Globals;
import com.foc.business.calendar.FCalendar;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;

public class StartEndDate extends FocObject{
	
	private boolean useSuggestedDates = false;
	private String  firstLabel        = null;
	private String  lastLabel         = null;
  
  public StartEndDate(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
  
  public StartEndDate(){
  	super(new FocConstructor(StartEndDateDesc.getInstance(), null));
  	newFocProperties();
  }
  
  public void dispose(){
  	super.dispose();
  }
  
  public Date getFirstDate(){
    return getPropertyDate(StartEndDateDesc.FLD_FDATE);
  }

  public void setFirstDate(Date date){
    setPropertyDate(StartEndDateDesc.FLD_FDATE, date);
  }

  public Date getLastDate(){
    return getPropertyDate(StartEndDateDesc.FLD_LDATE);
  }

  public void setLastDate(Date date){
    setPropertyDate(StartEndDateDesc.FLD_LDATE, date);
  }

  public Date getSuggestedFirstDate(){
    return getPropertyDate(StartEndDateDesc.FLD_SUGGESTED_FIRST_DATE);
  }

  public void setSuggestedFirstDate(Date date){
    setPropertyDate(StartEndDateDesc.FLD_SUGGESTED_FIRST_DATE, date);
  }

  public Date getSuggestedLastDate(){
    return getPropertyDate(StartEndDateDesc.FLD_SUGGESTED_LAST_DATE);
  }

  public void setSuggestedLastDate(Date date){
    setPropertyDate(StartEndDateDesc.FLD_SUGGESTED_LAST_DATE, date);
  }

  public ArrayList<Date> newDatesArray(){
  	ArrayList<Date> arr = new ArrayList<Date>();
  	Date first = getFirstDate();
  	Date last  = getLastDate();
  	Date d     = (Date) first.clone();
  	while(!d.after(last)){
  		arr.add(d);
  		d = FCalendar.shiftDate(FCalendar.getInstanceOfJavaUtilCalandar(), d, 1);
  	}
  	return arr;
  }

	public boolean isUseSuggestedDates() {
		return useSuggestedDates;
	}

	public void setUseSuggestedDates(boolean useSuggestedDates) {
		this.useSuggestedDates = useSuggestedDates;
	}

	public String getFirstLabel() {
		return firstLabel;
	}

	public void setFirstLabel(String firstLabel) {
		this.firstLabel = firstLabel;
	}

	public String getLastLabel() {
		return lastLabel;
	}

	public void setLastLabel(String lastLabel) {
		this.lastLabel = lastLabel;
	}
	
	public boolean popupSelectionDialog(Date defaultDate1, Date defaultDate2, Date minDate, Date maxDate, String dialogTitle){
		boolean error = true;
		int  view         = StartEndDateGuiDetailsPanel.VIEW_DATE_INTERVAL;
		setFirstDate(defaultDate1);
		setLastDate(defaultDate2);
		
		StartEndDateGuiDetailsPanel datesPanel = new StartEndDateGuiDetailsPanel(this, view);
		datesPanel.setWithValidation();
		Globals.getDisplayManager().popupDialog(datesPanel, dialogTitle, true);
		if(!datesPanel.isCancel()){
			if(getFirstDate().after(getLastDate())){
				Globals.getDisplayManager().popupMessage("Dates not valid First Date Should be LEss Than Last Date");
			}else if(getFirstDate().after(getLastDate()) || getLastDate().before(getFirstDate())){
				Globals.getDisplayManager().popupMessage("Dates interval not valid");
			}else{
				error = false;
			}
		}
		return error;
	}
}
