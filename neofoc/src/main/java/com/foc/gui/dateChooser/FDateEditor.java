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

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;

import com.foc.Globals;

//We have our own Date Editor to implement our own default formatter that replaces 1/1/1970 with "" allows  

@SuppressWarnings("serial")
public class FDateEditor extends JSpinner.DateEditor{

	/**
	 * 
	 */
	
	public FDateEditor(JSpinner spinner){
		super(spinner);
		afterInit(spinner);
	}

	public FDateEditor(JSpinner spinner, String dateFormatPattern){
		super(spinner, dateFormatPattern);
		afterInit(spinner);
	}
	
	private void afterInit(JSpinner spinner){
    SpinnerDateModel model = (SpinnerDateModel)spinner.getModel();
    DateFormatter formatter = new FDateEditorFormatter(model, getFormat());
    JFormattedTextField ftf = getTextField();
    DefaultFormatterFactory factory = (DefaultFormatterFactory) ftf.getFormatterFactory();
    factory.setDefaultFormatter(formatter);
	}
	
	public class FDateEditorFormatter extends DateFormatter{
		
		private final SpinnerDateModel model;

		FDateEditorFormatter(SpinnerDateModel model, DateFormat format) {
		    super(format);
		    this.model = model;
		}

		public void setMinimum(Comparable min) {
		    model.setStart(min);
		}

		public Comparable getMinimum() {
		    return  model.getStart();
		}

		public void setMaximum(Comparable max) {
		    model.setEnd(max);
		}

		public Comparable getMaximum() {
		    return model.getEnd();
		}

		@Override
		public Object stringToValue(String text) throws ParseException {
			Date date = null;
			if(text.isEmpty()){
				date = new Date(0);
			}else{
				date = (Date) super.stringToValue(text);
			}
			return date;
		}

		@Override
		public String valueToString(Object value) throws ParseException {
			String str = super.valueToString(value);
			if(value != null && value instanceof Date){
				Date date = (Date) value;
				if(date.getTime() < Globals.DAY_TIME){
					str = "";
				}
			}

			return str;
		}		
		
	}
}
