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

import com.foc.desc.FocDesc;
import com.foc.desc.field.FField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class WeekShiftDesc extends FocDesc {
	public static final int FLD_NAME            = FField.FLD_NAME;
	
	public static final int FLD_MONDAY_SHIFT    = 1;
	public static final int FLD_TUSEDAY_SHIFT   = 2;
	public static final int FLD_WEDNESDAY_SHIFT = 3;
	public static final int FLD_THURSDAY_SHIFT  = 4;
	public static final int FLD_FRIDAY_SHIFT    = 5;
	public static final int FLD_SATURDAY_SHIFT  = 6;
	public static final int FLD_SUNDAY_SHIFT    = 7;
	
	public static final int FLD_MONDAY_OVERTIME_FACTOR    = 10;
	public static final int FLD_TUSEDAY_OVERTIME_FACTOR   = 11;
	public static final int FLD_WEDNESDAY_OVERTIME_FACTOR = 12;
	public static final int FLD_THURSDAY_OVERTIME_FACTOR  = 13;
	public static final int FLD_FRIDAY_OVERTIME_FACTOR    = 14;
	public static final int FLD_SATURDAY_OVERTIME_FACTOR  = 15;
	public static final int FLD_SUNDAY_OVERTIME_FACTOR    = 16;
	
	public static final int  OT_FACTOR_NORMAL    = 0;
	public static final int  OT_FACTOR_EXTRA     = 1;
	public static final int  OT_FACTOR_TYPE_NONE = 2;
	
	public static final String DB_TABLE_NAME = "WEEK_SHIFT";
		
	public WeekShiftDesc(){
		super(WeekShift.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
		setGuiBrowsePanelClass(WeekShiftGuiBrowsePanel.class);	
		setGuiDetailsPanelClass(WeekShiftGuiDetailsPanel.class);
		
    addReferenceField();
    FField fld = addNameField();
    fld.setMandatory(true);
    fld.setLockValueAfterCreation(true);
    
    String holidayStr = "--- holiday ---";
    
    FObjectField oFld = new FObjectField("MONDAY_SHIFT", "Monday", FLD_MONDAY_SHIFT, false, DayShiftDesc.getInstance(), "MONDAY_SHIFT_");
    oFld.setNullValueDisplayString(holidayStr);
    addDayShiftField(oFld);
    
    oFld = new FObjectField("TUESDAY_SHIFT", "Tuesday", FLD_TUSEDAY_SHIFT, false, DayShiftDesc.getInstance(), "TUESDAY_SHIFT_");
    oFld.setNullValueDisplayString(holidayStr);
    addDayShiftField(oFld);
    
    oFld = new FObjectField("WEDNESDAY_SHIFT", "Wednesday", FLD_WEDNESDAY_SHIFT, false, DayShiftDesc.getInstance(), "WEDNESDAY_SHIFT_");
    oFld.setNullValueDisplayString(holidayStr);
    addDayShiftField(oFld);    

    oFld = new FObjectField("THURSDAY_SHIFT", "Thursday", FLD_THURSDAY_SHIFT, false, DayShiftDesc.getInstance(), "THURSDAY_SHIFT_");
    oFld.setNullValueDisplayString(holidayStr);
    addDayShiftField(oFld);    

    oFld = new FObjectField("FRIDAY_SHIFT", "Friday", FLD_FRIDAY_SHIFT, false, DayShiftDesc.getInstance(), "FRIDAY_SHIFT_");
    oFld.setNullValueDisplayString(holidayStr);
    addDayShiftField(oFld);    

    oFld = new FObjectField("SATURDAY_SHIFT", "Saturday", FLD_SATURDAY_SHIFT, false, DayShiftDesc.getInstance(), "SATURDAY_SHIFT_");
    oFld.setNullValueDisplayString(holidayStr);
    addDayShiftField(oFld);    

    oFld = new FObjectField("SUNDAY_SHIFT", "Sunday", FLD_SUNDAY_SHIFT, false, DayShiftDesc.getInstance(), "SUNDAY_SHIFT_");
    oFld.setNullValueDisplayString(holidayStr);
    addDayShiftField(oFld);
    
    addOverTimeFactorTypeField("MONDAY_OT_FACTOR", "OT Factor", FLD_MONDAY_OVERTIME_FACTOR);
    addOverTimeFactorTypeField("TUSEDAY_OT_FACTOR", "OT Factor", FLD_TUSEDAY_OVERTIME_FACTOR);
    addOverTimeFactorTypeField("WEDNESDAY_OT_FACTOR", "OT Factor", FLD_WEDNESDAY_OVERTIME_FACTOR);
    addOverTimeFactorTypeField("THURDAY_OT_FACTOR", "OT Factor", FLD_THURSDAY_OVERTIME_FACTOR);
    addOverTimeFactorTypeField("FRIDAY_OT_FACTOR", "OT Factor", FLD_FRIDAY_OVERTIME_FACTOR);
    addOverTimeFactorTypeField("SATURDAY_OT_FACTOR", "OT Factor", FLD_SATURDAY_OVERTIME_FACTOR);
    addOverTimeFactorTypeField("SUNDAY_OT_FACTOR", "OT Factor", FLD_SUNDAY_OVERTIME_FACTOR);
	}
	
	private void addOverTimeFactorTypeField(String name, String title, int id){
		FMultipleChoiceField mFld = new FMultipleChoiceField(name, title, id, false, 2);
		mFld.addChoice(OT_FACTOR_NORMAL   , "Normal");
		mFld.addChoice(OT_FACTOR_EXTRA    , "Extra");
		mFld.addChoice(OT_FACTOR_TYPE_NONE, "-none-");
		addField(mFld);
	}
	
	private void addDayShiftField(FObjectField oFld){
		oFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    oFld.setSelectionList(DayShiftDesc.getList(FocList.NONE));
    oFld.setDisplayField(DayShiftDesc.FLD_NAME);
    oFld.setComboBoxCellEditor(DayShiftDesc.FLD_NAME);
    //oFld.setDetailsPanelViewID(DayShiftGuiDetailsPanel.VIEW_SELECTION);
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
    FocList list = super.newFocList();
    list.setDirectlyEditable(false);
    list.setDirectImpactOnDatabase(true);
    if(list.getListOrder() == null){
      FocListOrder order = new FocListOrder(FLD_NAME);
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
    return getInstance(DB_TABLE_NAME, WeekShiftDesc.class);    
  }
}
