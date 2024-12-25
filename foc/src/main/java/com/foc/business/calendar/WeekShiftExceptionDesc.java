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

import java.sql.Date;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FDateField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

public class WeekShiftExceptionDesc extends FocDesc{

	public static final int FLD_START_DATE           = 1;
  public static final int FLD_END_DATE             = 2;
	public static final int FLD_WEEK_SHIFT           = 3;
	public static final int FLD_FCALENDAR            = 4;
	
	public static final String DB_TABLE_NAME = "CAL_WEEK_SHIFT_EXCEPTION";
		
	public WeekShiftExceptionDesc(){
		super(WeekShiftException.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
		setGuiBrowsePanelClass(WeekShiftExceptionGuiBrowsePanel.class);	
		//setGuiDetailsPanelClass(HolidayGuiDetailsPanel.class);
		
    FField focFld = addReferenceField();

    FPropertyListener datesListener = new FPropertyListener(){
			@Override
			public void dispose() {
			}

			@Override
			public void propertyModified(FProperty property) {
				WeekShiftException h = (WeekShiftException) property.getFocObject();
				if(h != null) h.recomputeStartEndDatesTime();
			}
    };

    focFld = new FDateField("START_DATE", "Start Date", FLD_START_DATE,  false);    
    //focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    focFld.addListener(getDatePropertyListener());
    focFld.addListener(datesListener);
    addField(focFld);
    
    focFld = new FDateField("END_DATE", "End Date", FLD_END_DATE,  false);    
    //focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    focFld.addListener(datesListener);
    addField(focFld);

    FObjectField objFld = new FObjectField("WEEK_SHIFT", "Week Shift", FLD_WEEK_SHIFT, WeekShiftDesc.getInstance());
    objFld.setSelectionList(WeekShiftDesc.getList(FocList.NONE));
    objFld.setComboBoxCellEditor(WeekShiftDesc.FLD_NAME);
    objFld.setMandatory(true);
    addField(objFld);

    objFld = new FObjectField("CALENDAR", "FCalendar", FLD_FCALENDAR, FCalendarDesc.getInstance(), this, FCalendarDesc.FLD_WEEK_SHIFT_EXCEPTION_LIST);
    objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    objFld.setSelectionList(FCalendarDesc.getList(FocList.NONE));
    objFld.setMandatory(true);
    addField(objFld);
	}

  public FPropertyListener getDatePropertyListener(){
    return new FPropertyListener(){
      public void dispose() {
      }

      public void propertyModified(FProperty property) {
        if(property != null && !property.isLastModifiedBySetSQLString()){
          WeekShiftException holidayDate = (WeekShiftException) property.getFocObject();
          Date startDate = holidayDate.getPropertyDate(WeekShiftExceptionDesc.FLD_START_DATE);
          holidayDate.setPropertyDate(WeekShiftExceptionDesc.FLD_END_DATE, startDate);
        }
      }
    };
  }

	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
	
	public static FocList getList(int mode){
    return getInstance().getFocList(mode);
  }
  
  @Override
  public FocList newFocList(){
    FocList list = super.newFocList();
    list.setDirectlyEditable(true);
    list.setDirectImpactOnDatabase(false);
    if(list.getListOrder() == null){
      FocListOrder order = new FocListOrder(FLD_START_DATE);
      list.setListOrder(order);
    }
    return list;
  }
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

	public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, WeekShiftExceptionDesc.class);    
  }
}
