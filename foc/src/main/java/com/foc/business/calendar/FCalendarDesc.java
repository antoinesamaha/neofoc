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

import java.util.Calendar;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FObjectField;
import com.foc.desc.field.FStringField;
import com.foc.list.FocList;

public class FCalendarDesc extends FocDesc {
	public static final int FLD_NAME                       = FField.FLD_NAME;
	public static final int FLD_IS_DEFAULT                 = 2;
	public static final int FLD_STARTING_DAY_OF_WEEK       = 3;
	//public static final int FLD_IS_MONDAY_WORKDAY          = 4;
	//public static final int FLD_IS_TUESDAY_WORKDAY         = 5;
	//public static final int FLD_IS_WEDNESDAY_WORKDAY       = 6;
	//public static final int FLD_IS_THURSDAY_WORKDAY        = 7;
	//public static final int FLD_IS_FRIDAY_WORKDAY          = 8;
	//public static final int FLD_IS_SATURDAY_WORKDAY        = 9;
	//public static final int FLD_IS_SUNDAY_WORKDAY          = 10;
	//public static final int FLD_START_TIME                 = 11;
	//public static final int FLD_END_TIME                   = 12;
	//public static final int FLD_OVER_TIME_ADDITIONAL_HOURS = 13;
	public static final int FLD_WEEK_SHIFT                 = 14;
	public static final int FLD_HOLIDAYS_LIST              = 20;
	public static final int FLD_WEEK_SHIFT_EXCEPTION_LIST  = 21;
	
  public static final String DAY_LABEL_SUNDAY    = "Sunday"    ;
  public static final String DAY_LABEL_MONDAY    = "Monday"    ;
  public static final String DAY_LABEL_TUESDAY   = "Tuesday"   ;
  public static final String DAY_LABEL_WEDNESDAY = "Wednesday" ;
  public static final String DAY_LABEL_THURSDAY  = "Thursday"  ;
  public static final String DAY_LABEL_FRIDAY    = "Friday"    ;
  public static final String DAY_LABEL_SATURDAY  = "Saturday"  ;
  
  public static final String DB_TABLE_NAME = "CALENDAR";
		
	public FCalendarDesc(){
		super(FCalendar.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
		setGuiBrowsePanelClass(FCalendarGuiBrowsePanel.class);	
		setGuiDetailsPanelClass(FCalendarGuiDetailsPanel.class);
		
    FField focFld = addReferenceField();
    
    focFld = addNameField();
    focFld.setSize(30);
    focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    
    addExternalCodeField();
    
    focFld = new FBoolField ("ISDEFAULT", "isDefault", FLD_IS_DEFAULT, false );    
    //focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    addField(focFld);
    
    FMultipleChoiceField multiFld = new FMultipleChoiceField("STARTING_DAY_OF_WEEK", "Starting Day Of Week", FLD_STARTING_DAY_OF_WEEK, false, 5);
    multiFld.setSortItems(false);
    multiFld.addChoice(Calendar.SUNDAY   , DAY_LABEL_SUNDAY   );
    multiFld.addChoice(Calendar.MONDAY   , DAY_LABEL_MONDAY   );
    multiFld.addChoice(Calendar.TUESDAY  , DAY_LABEL_TUESDAY  );
    multiFld.addChoice(Calendar.WEDNESDAY, DAY_LABEL_WEDNESDAY);
    multiFld.addChoice(Calendar.THURSDAY , DAY_LABEL_THURSDAY );
    multiFld.addChoice(Calendar.FRIDAY   , DAY_LABEL_FRIDAY   );
    multiFld.addChoice(Calendar.SATURDAY , DAY_LABEL_SATURDAY );
    addField(multiFld);
    
    /*
    focFld = new FBoolField ("ISMONDAY", "Monday", FLD_IS_MONDAY_WORKDAY, false );    
    focFld.setMandatory(true);
    addField(focFld);
    
    focFld = new FBoolField ("ISTUESDAY", "Tuesday", FLD_IS_TUESDAY_WORKDAY, false );    
    focFld.setMandatory(true);
    addField(focFld);
    
    focFld = new FBoolField ("ISWEDNESDAY", "Wednesday", FLD_IS_WEDNESDAY_WORKDAY, false );    
    focFld.setMandatory(true);
    addField(focFld);
    
    focFld = new FBoolField ("ISTHURDAY", "Thursday", FLD_IS_THURSDAY_WORKDAY, false );    
    focFld.setMandatory(true);
    addField(focFld);
    
    focFld = new FBoolField ("ISFRIDAY", "Friday", FLD_IS_FRIDAY_WORKDAY, false );    
    focFld.setMandatory(true);
    addField(focFld);
    
    focFld = new FBoolField ("ISSATURDAY", "Saturday", FLD_IS_SATURDAY_WORKDAY, false );
    focFld.setMandatory(true);
    addField(focFld);
    
    focFld = new FBoolField ("ISSUNDAY", "Sunday", FLD_IS_SUNDAY_WORKDAY, false );
    focFld.setMandatory(true);
    addField(focFld);
    
    FTimeField timeFld = new FTimeField("START_TIME", "Start time", FLD_START_TIME, false);
    addField(timeFld);

    timeFld = new FTimeField("END_TIME", "End time", FLD_END_TIME, false);
    addField(timeFld);
    
    FNumField nFld = new FNumField("OVER_TIME_ADD_HRS", "Overtime Additional Hours", FLD_OVER_TIME_ADDITIONAL_HOURS, false, 2, 0);
    addField(nFld);
    */
    
    FObjectField oFld = new FObjectField("WEEK_SHIFT", "Default week shift", FLD_WEEK_SHIFT, false, WeekShiftDesc.getInstance(), "WEEK_SHIFT_");
    oFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    oFld.setSelectionList(WeekShiftDesc.getList(FocList.NONE));
    oFld.setDisplayField(WeekShiftDesc.FLD_NAME);
    oFld.setComboBoxCellEditor(WeekShiftDesc.FLD_NAME);
    //oFld.setDetailsPanelViewID(WeekShiftGuiDetailsPanel.VIEW_SELECTION);
    addField(oFld);
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
    FocList list = new FCalendarList();
    return list;
  }
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

	public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, FCalendarDesc.class);    
  }
}
