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

import java.util.Calendar;

import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FDateField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FNumField;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

public class DateIntervalDesc extends FocDesc {

  public static final int FLD_FDATE      = 1;
  public static final int FLD_LDATE      = 2;
  public static final int FLD_WEEK       = 3;
  public static final int FLD_MONTH      = 4;
  public static final int FLD_YEAR       = 5;
  public static final int FLD_MONTH_PART = 6;
  
  public static final int MONTH_PART_FULL   = 0;
  public static final int MONTH_PART_FIRST  = 1;
  public static final int MONTH_PART_SECOND = 2;
  
  public DateIntervalDesc() {
    super(DateInterval.class, FocDesc.NOT_DB_RESIDENT, "DATE_INTERVAL", false);
    setGuiDetailsPanelClass(WeeklyIntervalGuiDetailsPanel.class);
    
    FField fField = addReferenceField();
    
    fField = new FDateField("START_DATE", "Start Date", FLD_FDATE, false);
    addField(fField);
    
    fField = new FDateField("END_DATE", "End Date", FLD_LDATE, false);
    addField(fField);
    
    fField = new FNumField("WEEK", "Week", FLD_WEEK, false, 5, 0);    
    fField.addListener(dateListener);
    addField(fField);
    
    FMultipleChoiceField multiFld = newMonthField("MONTH", "Month", FLD_MONTH, false, 5, false);
    addField(multiFld);
    multiFld.addListener(dateListener);
    
//    FMultipleChoiceField multiFld = new FMultipleChoiceField("MONTH", "Month", FLD_MONTH, false, 5);
//    multiFld.setSortItems(false);
//    multiFld.addChoice(Calendar.JANUARY, "January");
//    multiFld.addChoice(Calendar.FEBRUARY, "February");
//    multiFld.addChoice(Calendar.MARCH, "March");
//    multiFld.addChoice(Calendar.APRIL, "April");
//    multiFld.addChoice(Calendar.MAY, "May");
//    multiFld.addChoice(Calendar.JUNE, "June");
//    multiFld.addChoice(Calendar.JULY, "July");
//    multiFld.addChoice(Calendar.AUGUST, "August");
//    multiFld.addChoice(Calendar.SEPTEMBER, "September");
//    multiFld.addChoice(Calendar.OCTOBER, "October");
//    multiFld.addChoice(Calendar.NOVEMBER, "November");
//    multiFld.addChoice(Calendar.DECEMBER, "December");
//    multiFld.addListener(dateListener);
//    addField(multiFld);

    FMultipleChoiceField yearFld = newYearField("YEAR", "Year", FLD_YEAR, false, 5);
    addField(yearFld);
    yearFld.addListener(dateListener);
//    FMultipleChoiceField yearFld = new FMultipleChoiceField("YEAR", "Year", FLD_YEAR, false, 5);
//    yearFld.addListener(dateListener);
//    yearFld.setSortItems(false);
//    Calendar calendar = Calendar.getInstance();
//    calendar.setTime(Globals.getApp().getSystemDate());
//    for(int i = 0; i <= 16; i++){
//      yearFld.addChoice((calendar.get(Calendar.YEAR)-15)+i, ""+((calendar.get(Calendar.YEAR)-15)+i));
//    }
//    addField(yearFld);
    
    FMultipleChoiceField monthPartFld = new FMultipleChoiceField("MONTH_PART", "Month part", FLD_MONTH_PART, false, 2);
    monthPartFld.addChoice(MONTH_PART_FULL , "Full");
    monthPartFld.addChoice(MONTH_PART_FIRST, "First");
    monthPartFld.addChoice(MONTH_PART_SECOND , "Second");
    monthPartFld.addListener(dateListener);
    addField(monthPartFld);
    yearFld.addListener(dateListener);
  }

  public FPropertyListener dateListener = new FPropertyListener(){
    public void dispose() {       
    }
    
    public void propertyModified(FProperty property) {
      DateInterval stockReport = (DateInterval) (property != null ? property.getFocObject() : null);
      if(stockReport != null){
        if(stockReport.getPropertyInteger(FLD_WEEK) != 0 && stockReport.getPropertyInteger(FLD_YEAR) != 0){
          stockReport.computeDates();
        }else if (stockReport.getPropertyInteger(FLD_MONTH) != -1 && stockReport.getPropertyInteger(FLD_YEAR) != 0){
          stockReport.setDates();
        }
      }
    }
  };
  
  public static FMultipleChoiceField newMonthField(String name, String title, int id, boolean key, int size, boolean allowNoSelection){
  	FMultipleChoiceField multiFld = new FMultipleChoiceField(name, title, id, key, size);
    multiFld.setSortItems(false);
    if(allowNoSelection){
    	multiFld.addChoice(-1, "All");
    }
    multiFld.addChoice(Calendar.JANUARY, "January");
    multiFld.addChoice(Calendar.FEBRUARY, "February");
    multiFld.addChoice(Calendar.MARCH, "March");
    multiFld.addChoice(Calendar.APRIL, "April");
    multiFld.addChoice(Calendar.MAY, "May");
    multiFld.addChoice(Calendar.JUNE, "June");
    multiFld.addChoice(Calendar.JULY, "July");
    multiFld.addChoice(Calendar.AUGUST, "August");
    multiFld.addChoice(Calendar.SEPTEMBER, "September");
    multiFld.addChoice(Calendar.OCTOBER, "October");
    multiFld.addChoice(Calendar.NOVEMBER, "November");
    multiFld.addChoice(Calendar.DECEMBER, "December");
    return multiFld;
  }

  public static FMultipleChoiceField newYearField(String name, String title, int id, boolean key, int size){
  	FMultipleChoiceField yearFld = new FMultipleChoiceField(name, title, id, key, size);
    yearFld.setSortItems(false);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(Globals.getApp().getSystemDate());
    for(int i = 0; i <= 16; i++){
      yearFld.addChoice((calendar.get(Calendar.YEAR)-15)+i, ""+((calendar.get(Calendar.YEAR)-15)+i));
    }
    return yearFld;
  }
  
  private static FocDesc focDesc = null;
   
  public static FocDesc getInstance() {
    if (focDesc==null){
      focDesc = new DateIntervalDesc();
    }
    return focDesc;
  }
}
