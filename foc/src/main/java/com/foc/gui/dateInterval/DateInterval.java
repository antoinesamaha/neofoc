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
import java.util.Calendar;

import com.foc.Globals;
import com.foc.business.calendar.FCalendar;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;

public class DateInterval extends FocObject{
  
	public static final int LENGTH_NONE       = -1;
	public static final int LENGTH_MONTH      =  0;
	public static final int LENGTH_SEMI_MONTH =  1;
	
  private DateIntervalGuiDetailsPanel dateInternalPanel = null;
  
  public DateInterval(FocConstructor constr) {
    super(constr);
    newFocProperties();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(Globals.getApp().getSystemDate());
    setPropertyDouble(DateIntervalDesc.FLD_YEAR, calendar.get(Calendar.YEAR));
  }
  
  public DateInterval(){
  	super(new FocConstructor(DateIntervalDesc.getInstance(), null));
  	newFocProperties();
  }
  
  public void dispose(){
  	super.dispose();
  }
  
  public int getWeek(){
    return (int)getPropertyDouble(DateIntervalDesc.FLD_WEEK);
  }
  
  public int getYear(){
    return (int)getPropertyDouble(DateIntervalDesc.FLD_YEAR);
  }

  public void setYear(int year){
    setPropertyDouble(DateIntervalDesc.FLD_YEAR, year);
  }

  public int getMonth(){
    return getPropertyMultiChoice(DateIntervalDesc.FLD_MONTH);
  }

  public void setMonth(int month){
    setPropertyMultiChoice(DateIntervalDesc.FLD_MONTH, month);
  }

  public int getMonthPart(){
    return getPropertyMultiChoice(DateIntervalDesc.FLD_MONTH_PART);
  }

  public void setMonthPart(int month){
    setPropertyMultiChoice(DateIntervalDesc.FLD_MONTH_PART, month);
  }

  public Date getFirstDate(){
  	if(dateInternalPanel != null){
  		dateInternalPanel.setDatesAccordingToSelection();
  	}
    Date firstDate = getPropertyDate(DateIntervalDesc.FLD_FDATE);
    return firstDate; 
  }

  public Date getLastDate(){
  	if(dateInternalPanel != null){
  		dateInternalPanel.setDatesAccordingToSelection();
  	}
    Date lastDate = getPropertyDate(DateIntervalDesc.FLD_LDATE);
    return lastDate; 
  }

  public void setFirstDate(Date date){
  	if(dateInternalPanel != null){
  		dateInternalPanel.setDatesAccordingToSelection();
  	}
    setPropertyDate(DateIntervalDesc.FLD_FDATE, date);
  }

  public void setLastDate(Date date){
  	if(dateInternalPanel != null){
  		dateInternalPanel.setDatesAccordingToSelection();
  	}
    setPropertyDate(DateIntervalDesc.FLD_LDATE, date);
  }
  
  //temporary function
  public void computeDates(){
    int week = getWeek();
    int year = getYear();
    FCalendar fcal = FCalendar.getDefaultCalendar();
    Date []date = fcal.getStartingEndingDatesOfWeek(week, year);
    setPropertyDate(DateIntervalDesc.FLD_FDATE, date[0]);
    setPropertyDate(DateIntervalDesc.FLD_LDATE, date[1]);    
  }

  //temporary function
  public void setDates() {
    int month = getMonth();
    int year = getYear();

    Calendar Calnow = Calendar.getInstance();

    //First Date
    if(getMonthPart() == DateIntervalDesc.MONTH_PART_FULL || getMonthPart() == DateIntervalDesc.MONTH_PART_FIRST){
    	Calnow.set(year, month, 1);
    }else{
    	Calnow.set(year, month, 15);
    }
    Date firstDate = new Date(Calnow.getTimeInMillis());
    firstDate = new Date(FCalendar.getTimeAtMidnight(firstDate));

    //Last Date
    Date lastDate = null;
    if(getMonthPart() == DateIntervalDesc.MONTH_PART_FULL || getMonthPart() == DateIntervalDesc.MONTH_PART_SECOND){
      Calnow.set(year, (month) + 1, 1);    
      lastDate = new Date(Calnow.getTimeInMillis());
      lastDate = FCalendar.shiftDate(Calnow, lastDate, -1);
    }else{
    	Calnow.set(year, month, 14);
    	lastDate = new Date(Calnow.getTimeInMillis());
    }
    
    lastDate = new Date(FCalendar.getTimeAtMidnight(lastDate));
    
    setPropertyDate(DateIntervalDesc.FLD_FDATE, firstDate);
    setPropertyDate(DateIntervalDesc.FLD_LDATE, lastDate);    
  }
  
  public DateIntervalGuiDetailsPanel getDateInternalPanel() {
    return dateInternalPanel;
  }

  public void setDateInternalPanel(DateIntervalGuiDetailsPanel dateInternalPanel) {
    this.dateInternalPanel = dateInternalPanel;
  }

	public static DateInterval newDateInterval(int payFrequency, Date datePrefered){
		DateInterval dateInterval = new DateInterval();
		dateInterval.setDates();
		
		switch(payFrequency){
		case LENGTH_NONE:
  		{
    		Date     date1 = Globals.getApp().getSystemDate();
    		Date     date2 = Globals.getApp().getSystemDate();
    		if(datePrefered != null){
    			date1 = datePrefered;
    			date1.setTime(date1.getTime()+Globals.DAY_TIME);
    		}
    		dateInterval.setFirstDate(new java.sql.Date(date1.getTime()));
    		dateInterval.setLastDate(new java.sql.Date(date2.getTime()));
  		}
			break;
		case LENGTH_MONTH:
  		{
  			Date     date = Globals.getApp().getSystemDate();
  			Calendar cal  = Calendar.getInstance();
  			cal.setTime(date);
    		if(datePrefered != null){
    			date = datePrefered;
    			cal.setTime(date);
    			FCalendar.rollTheCalendar_Month(cal);
    		}
  			dateInterval.setMonth(cal.get(Calendar.MONTH));
  			dateInterval.setYear(cal.get(Calendar.YEAR));
				if(datePrefered != null){
	  			Calendar firstDateCal = Calendar.getInstance();
	  			firstDateCal.setTime(datePrefered);
    			FCalendar.rollTheCalendar_Day(firstDateCal);
    			Date preferredPlusOne = new Date(firstDateCal.getTimeInMillis());
					dateInterval.setFirstDate(preferredPlusOne);
				}
  		}
			break;
		case LENGTH_SEMI_MONTH:
  		{
  			Date     date = Globals.getApp().getSystemDate();
  			Calendar cal  = Calendar.getInstance();
  			cal.setTime(date);
    		if(datePrefered != null){
    			date = datePrefered;
    			cal.setTime(date);
    			//FCalendar.rollTheCalendar_Month(cal);
    			if(cal.get(Calendar.DAY_OF_MONTH) <= 22){
    				//This means that the last payslip was of the first period
    				dateInterval.setMonthPart(DateIntervalDesc.MONTH_PART_SECOND);
    				//
    			}else{
    				FCalendar.rollTheCalendar_Month(cal);
    				dateInterval.setMonthPart(DateIntervalDesc.MONTH_PART_FIRST);
    			}
    		}else{
    			dateInterval.setMonthPart(DateIntervalDesc.MONTH_PART_FIRST);
    		}
  			dateInterval.setMonth(cal.get(Calendar.MONTH));
  			dateInterval.setYear(cal.get(Calendar.YEAR));
  			
  			if(datePrefered != null){
  				cal.setTime(datePrefered);
  				FCalendar.rollTheCalendar_Day(cal);
  				dateInterval.setFirstDate(new java.sql.Date(cal.getTime().getTime()));
  			}
  			}
			break;
		}
		return dateInterval;
	}
  
 /* public JasperPrint fillReport() {
    JasperPrint jrPrint = null;
    JRFocListDataSource dataSource = null;
    try{
      
      FGTabbedPane tabbedPane = stockReportGuiPanel.getTabbedPane();
      int selectedTabIndex = tabbedPane.getSelectedIndex();
      switch (selectedTabIndex) {
      case 0:
        stockReportGuiPanel.setStockreport(stockReportGuiPanel.getMonthlyReportPanel().getStockreport());
        break;
      case 1:
        stockReportGuiPanel.setStockreport(stockReportGuiPanel.getWeeklyReportPanel().getStockreport());
        break;
      case 2:
        stockReportGuiPanel.setStockreport(stockReportGuiPanel.getDateReportPanel().getStockreport());
        break;
      default:
        break;
      }
      
      FocListWithFilter focListWithFilter = new FocListWithFilter(StockMovementFilterDesc.getInstance(), new FocLinkSimple(StockMovementCopyDesc.getInstance()));
      StockMovementFilter filter = (StockMovementFilter) focListWithFilter.getFocListFilter();
      filter.setFirstDate(getFirstDate());
      filter.setLastDate(getLastDate());
      
      StockMovementFilterFocList stockMovementFilterFocList = new StockMovementFilterFocList(focListWithFilter);
      stockMovementFilterFocList.refillFicticiousFocListFromFilterList();
      FocList reportList = stockMovementFilterFocList.getFocList();
      
      dataSource = new JRFocListDataSource(reportList);
      jrPrint = JasperFillManager.fillReport(Globals.getInputStream(reportFile), null, dataSource);
    }catch(Exception e){
      Globals.logException(e);
    }
    return jrPrint;
  }*/
}
