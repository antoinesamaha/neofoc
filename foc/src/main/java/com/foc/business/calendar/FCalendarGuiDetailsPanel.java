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

import java.awt.GridBagConstraints;

import com.foc.desc.FocObject;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class FCalendarGuiDetailsPanel extends FPanel {

	private FCalendar calendar = null;

  public FCalendarGuiDetailsPanel(FocObject c, int view){
  	super("Calendar", FPanel.FILL_VERTICAL);  	
  	this.calendar = (FCalendar) c;

  	/*
  	FPanel workTimesPanel = new FPanel("Work time", FPanel.FILL_NONE);
  	workTimesPanel.setBorder("Work time");
  	workTimesPanel.add(calendar, FCalendarDesc.FLD_START_TIME, 0, 0);
  	workTimesPanel.add(calendar, FCalendarDesc.FLD_END_TIME, 0, 1);
  	workTimesPanel.add(new FGLabel("OT nbr Hrs"), 0, 2);
  	
  	FPanel workingDaysPanel = new FPanel("Working days", FPanel.FILL_NONE);
  	workingDaysPanel.setBorder("Working days");
  	workingDaysPanel.addField(calendar, FCalendarDesc.FLD_IS_MONDAY_WORKDAY, 0, 1, 1, 1, GridBagConstraints.NONE);
  	workingDaysPanel.addField(calendar, FCalendarDesc.FLD_IS_TUESDAY_WORKDAY, 0, 2, 1, 1, GridBagConstraints.NONE);
  	workingDaysPanel.addField(calendar, FCalendarDesc.FLD_IS_WEDNESDAY_WORKDAY, 0, 3, 1, 1, GridBagConstraints.NONE);
  	workingDaysPanel.addField(calendar, FCalendarDesc.FLD_IS_THURSDAY_WORKDAY, 0, 4, 1, 1, GridBagConstraints.NONE);
  	workingDaysPanel.addField(calendar, FCalendarDesc.FLD_IS_FRIDAY_WORKDAY, 0, 5, 1, 1, GridBagConstraints.NONE);
  	workingDaysPanel.addField(calendar, FCalendarDesc.FLD_IS_SATURDAY_WORKDAY, 0, 6, 1, 1, GridBagConstraints.NONE);
  	workingDaysPanel.addField(calendar, FCalendarDesc.FLD_IS_SUNDAY_WORKDAY, 0, 7, 1, 1, GridBagConstraints.NONE);
  	*/
  	
  	FocList list = calendar.getHolidayList();
  	FPanel holidaysListPanel = new HolidayGuiBrowsePanel(list, FocObject.DEFAULT_VIEW_ID);

  	list = calendar.getWeekShiftExceptionList();
  	FPanel shiftExceptionListPanel = new WeekShiftExceptionGuiBrowsePanel(list, FocObject.DEFAULT_VIEW_ID);

  	add(calendar, FCalendarDesc.FLD_NAME, 0, 0);
  	add(calendar, FCalendarDesc.FLD_STARTING_DAY_OF_WEEK, 0, 1);
  	add(calendar, FCalendarDesc.FLD_WEEK_SHIFT, 2, 1);
  	//add(workingDaysPanel, 2, 1, 2, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);  	  	
  	add(holidaysListPanel, 0, 2, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL);
  	add(shiftExceptionListPanel, 2, 2, 4, 1, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL);
  	//add(workTimesPanel, 2, 2, 2, 1);

    FValidationPanel savePanel = showValidationPanel(true);
    if (savePanel != null) {
      savePanel.addSubject(calendar);
    }
  }
  
  public void dispose(){
  	super.dispose();
  	calendar = null;
  }
}
