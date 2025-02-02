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
package com.foc.gui;

import java.awt.Dimension;
import java.sql.Date;
import java.text.SimpleDateFormat;

import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.event.ChangeEvent;

import com.foc.Globals;
import com.foc.desc.field.FDateTimeField;
import com.foc.desc.field.FField;
import com.foc.gui.dateChooser.JCalendar;
import com.foc.gui.dateChooser.JDateChooser;
import com.foc.property.FDate;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

@SuppressWarnings("serial")
public class FGDateChooser extends JDateChooser implements FPropertyListener {
  
  private FDate 		objectProperty 		= null;
  private Dimension preferredDimension = null;
  private boolean   stateChangedOnlyForPreferredSize = false;
  
  public FGDateChooser(){
    this("dd/MM/yyyy");
  }

  public FGDateChooser(String format){
    super(format, false);
  }

  public void dispose() {
    if(objectProperty != null){
      objectProperty.removeListener(this);
      objectProperty = null;
    }
    preferredDimension = null;
  }

  @Override
  public void setName(String name){
  	super.setName(name);
  }
  
  public void setDate(String dateStr) throws Exception{
  	SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
 		setDate(format.parse(dateStr));
  }
  
  public void propertyModified(FProperty property) {
    if (objectProperty != null) {
      setDate(objectProperty.getDate());
    }
  }
  
  public void setProperty(FProperty prop){
    if(prop != objectProperty){
      if(objectProperty != null){
        objectProperty.removeListener(this);
      }
      objectProperty = (FDate) prop;
      
      setDate(objectProperty.getDate());
      //setText(objectProperty.getString());
      objectProperty.addListener(this);

      FField fld = objectProperty.getFocField();
      if(fld != null && fld instanceof FDateTimeField){
      	FDateTimeField dFld     = (FDateTimeField) objectProperty.getFocField();
	    	JCalendar      jClendar = getJCalendar();
	    	if(jClendar != null){
	    		jClendar.setWithTime(dFld.isTimeRelevant());
	    	}
      }
    }
  }
  
  public void stateChanged(ChangeEvent e) {
    super.stateChanged(e);
    try {
      if (objectProperty != null && !stateChangedOnlyForPreferredSize) {
        objectProperty.setDate(new Date(lastSelectedDate.getTime()), true);
        JSpinner spinner = getSpinner();
        if(objectProperty.getDate().getTime() < Globals.DAY_TIME ){
          ((DefaultEditor) spinner.getEditor()).getTextField().setText("");  
        }
      }
    } catch (Exception exception) {
      Globals.logException(exception);
    }
  }
  
  public void setEnabled(boolean b) {
    super.setEnabled(b);
    JSpinner spinner = getSpinner();
    if( spinner != null ){
      spinner.setEnabled(b);
      StaticComponent.setEnabled(spinner, b, objectProperty != null ? objectProperty.isOutput() : false);
      //StaticComponent.setEnabled(((DefaultEditor) spinner.getEditor()).getTextField(), b, objectProperty != null ? objectProperty.isOutput() : false);
    }
  }
  
  public Dimension getPreferredSize(){
    if(preferredDimension == null){
    	java.util.Date date = getDate();
    	stateChangedOnlyForPreferredSize = true;
    	if(date.getTime() < Globals.DAY_TIME){
	    	setDate(Globals.getApp().getSystemDate());
    	}
    	preferredDimension = super.getPreferredSize();
    	preferredDimension.width += 10;
    	if(date.getTime() < Globals.DAY_TIME){
	    	setDate(date);
    	}
    	stateChangedOnlyForPreferredSize = false;    	
    }
    return preferredDimension;
  }
}
