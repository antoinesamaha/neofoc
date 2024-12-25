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

import com.foc.desc.FocObject;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class DayShiftGuiDetailsPanel extends FPanel {

	private DayShift dayShift = null;
	public static final int VIEW_SELECTION = 1; 

  public DayShiftGuiDetailsPanel(FocObject c, int view){
  	super("Day Shift", FPanel.FILL_VERTICAL);  	
  	this.dayShift = (DayShift) c;

  	if(view == VIEW_SELECTION){
  		addField(dayShift, DayShiftDesc.FLD_NAME, 0, 0);
  	}else{
	   	add(dayShift, DayShiftDesc.FLD_NAME, 0, 0);
	  	
	  	FocList list           = dayShift.getShiftList();
	  	FPanel  shiftListPanel = new WorkShiftGuiBrowsePanel(list, FocObject.DEFAULT_VIEW_ID);
	  	add(shiftListPanel, 0, 3, 2, 1);
	
	    FValidationPanel savePanel = showValidationPanel(true);
	    if (savePanel != null) {
	      savePanel.addSubject(dayShift);
	    }
  	}
  }
  
  public void dispose(){
  	super.dispose();
  	dayShift = null;
  }
}
