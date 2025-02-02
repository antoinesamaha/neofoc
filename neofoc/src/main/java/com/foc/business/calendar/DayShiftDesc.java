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
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class DayShiftDesc extends FocDesc {
	public static final int FLD_NAME                 = FField.FLD_NAME;
	public static final int FLD_SHIFT_LIST           = 20;
	
	public static final String DB_TABLE_NAME = "DAY_SHIFT";
		
	public DayShiftDesc(){
		super(DayShift.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
		setGuiBrowsePanelClass(DayShiftGuiBrowsePanel.class);	
		setGuiDetailsPanelClass(DayShiftGuiDetailsPanel.class);
		
    addReferenceField();

    FField fld = addNameField();
    fld.setMandatory(true);
    fld.setLockValueAfterCreation(true);
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
    return getInstance(DB_TABLE_NAME, DayShiftDesc.class);
  }
}
