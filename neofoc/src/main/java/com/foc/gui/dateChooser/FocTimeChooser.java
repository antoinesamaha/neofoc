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
package com.foc.gui.dateChooser;

import java.util.Date;
import java.util.Calendar;

import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

import com.foc.Globals;

public class FocTimeChooser extends JSpinner {

	private JDayChooser dayChooser = null;
	
	public FocTimeChooser(){
	  super(new SpinnerDateModel(Globals.getApp().getSystemDate(), null, null, Calendar.HOUR_OF_DAY));
	  JSpinner.DateEditor de = new JSpinner.DateEditor(this, "HH:mm");
	  setEditor(de);
	}
	
	public void dispose(){
		dayChooser = null;
	}

	public void setDayChooser(JDayChooser dayChooser) {
		this.dayChooser = dayChooser;
	}
	
	public void setDate(Date date){
		getModel().setValue(date);
	}
}
