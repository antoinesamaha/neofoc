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
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FDateField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

public class HolidayDesc extends FocDesc{

	public static final int FLD_START_DATE           = 1;
  public static final int FLD_END_DATE             = 2;
	public static final int FLD_REASON               = 3;
	public static final int FLD_FCALENDAR            = 4;
	
	public static final String DB_TABLE_NAME = "HOLIDAY";
		
	public HolidayDesc(){
		super(Holiday.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
		setGuiBrowsePanelClass(HolidayGuiBrowsePanel.class);	
		//setGuiDetailsPanelClass(HolidayGuiDetailsPanel.class);
		
    FField focFld = addReferenceField();

    FPropertyListener datesListener = new FPropertyListener(){
			@Override
			public void dispose() {
			}

			@Override
			public void propertyModified(FProperty property) {
				Holiday h = (Holiday) property.getFocObject();
				if(h != null) h.recomputeStartEndDatesTime();
			}
    };

    focFld = new FDateField("HOLIDAY_DATE", "Start Date", FLD_START_DATE,  false);    
    //focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    focFld.addListener(getDatePropertyListener());
    focFld.addListener(datesListener);
    addField(focFld);
    
    focFld = new FDateField("HOLIDAY_END_DATE", "End Date", FLD_END_DATE,  false);    
    //focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    focFld.addListener(datesListener);
    addField(focFld);
    
    focFld = new FStringField("REASON", "Holidays", FLD_REASON,  false, 30);    
    //focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    addField(focFld);
    
    FObjectField objFld = new FObjectField("FCALENDAR", "FCalendar", FLD_FCALENDAR, false, FCalendarDesc.getInstance(), "FCALENDAR_", this, FCalendarDesc.FLD_HOLIDAYS_LIST);
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
          Holiday holidayDate = (Holiday) property.getFocObject();
          Date startDate = holidayDate.getPropertyDate(HolidayDesc.FLD_START_DATE);
          holidayDate.setPropertyDate(HolidayDesc.FLD_END_DATE, startDate);
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
    return getInstance(DB_TABLE_NAME, HolidayDesc.class);    
  }
}
