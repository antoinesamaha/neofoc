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
//IMPLEMENTED

/*
 * Created on Sep 9, 2005
 */
package com.foc.list.filter;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Time;
import java.text.SimpleDateFormat;

import com.foc.ConfigInfo;
import com.foc.db.DBManager;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldPath;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FTimeField;
import com.foc.gui.FGComboBox;
import com.foc.gui.FGLabel;
import com.foc.gui.FPanel;
import com.foc.property.FInt;
import com.foc.property.FMultipleChoice;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;
import com.foc.property.FTime;

/**
 * @author 01Barmaja
 */
public class TimeCondition extends FilterCondition {
  private static final int FLD_FIRST_TIME = 1;
  private static final int FLD_LAST_TIME = 2;
  private static final int FLD_OPERATOR = 3;

  //Make the constansts public and add operator_equals
  /*private static final int OPERATOR_BETWEEN = 0;
  private static final int OPERATOR_GREATER_THAN = 1;
  private static final int OPERATOR_LESS_THAN = 2;*/
  public static final int OPERATOR_INDIFERENT= 0;
  public static final int OPERATOR_EQUALS = 1;
  public static final int OPERATOR_GREATER_THAN = 2;
  public static final int OPERATOR_BETWEEN = 3;
  public static final int OPERATOR_LESS_THAN = 4;
  
  public TimeCondition(FFieldPath timeFieldPath, String fieldPrefix){
    super(timeFieldPath, fieldPrefix);
  }
  
  public TimeCondition(int fieldID){
  	super(fieldID);
  }

  public java.sql.Time getFirstTime(FocListFilter filter){
    FTime prop = (FTime) filter.getFocProperty(getFirstFieldID() + FLD_FIRST_TIME);
    return prop.getTime();
  }

  public java.sql.Time getLastTime(FocListFilter filter){
    FTime prop = (FTime) filter.getFocProperty(getFirstFieldID() + FLD_LAST_TIME);
    return prop.getTime();
  }

  public int getOperator(FocListFilter filter){
    FMultipleChoice prop = (FMultipleChoice) filter.getFocProperty(getFirstFieldID() + FLD_OPERATOR);
    return prop.getInteger();
  }
  
  public void setOperator(FocListFilter filter , int op){
    FInt operator = (FInt)filter.getFocProperty(getFirstFieldID() + FLD_OPERATOR);
    if (operator != null){
      operator.setInteger(op);
    }
  }
  
  public void setFirstTime(FocListFilter filter ,Time d){
    FTime timeProp = (FTime)filter.getFocProperty(getFirstFieldID() + FLD_FIRST_TIME);
    if (timeProp != null){
      timeProp.setTime(d);
    }
  }
  
  public void setLastTime(FocListFilter filter ,Time d){
    FTime timeProp = (FTime)filter.getFocProperty(getFirstFieldID() + FLD_LAST_TIME);
    if (timeProp != null){
      timeProp.setTime(d);
    }
  }
  
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // IMPLEMENTED
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public void fillProperties(FocObject focFatherObject){
  	new FMultipleChoice(focFatherObject, getFirstFieldID() + FLD_OPERATOR, OPERATOR_INDIFERENT);
    new FTime(focFatherObject, getFirstFieldID() + FLD_FIRST_TIME, FTime.getZeroTime_Copy());
    new FTime(focFatherObject, getFirstFieldID() + FLD_LAST_TIME, FTime.getZeroTime_Copy());
  }
  
	@Override
	public void copyCondition(FocObject tarObject, FocObject srcObject, FilterCondition srcCondition) {
	  copyProperty(tarObject, getFirstFieldID() + FLD_OPERATOR  , srcObject, srcCondition.getFirstFieldID() + FLD_OPERATOR);
	  copyProperty(tarObject, getFirstFieldID() + FLD_FIRST_TIME, srcObject, srcCondition.getFirstFieldID() + FLD_FIRST_TIME);
	  copyProperty(tarObject, getFirstFieldID() + FLD_LAST_TIME , srcObject, srcCondition.getFirstFieldID() + FLD_LAST_TIME);
	}

  public boolean includeObject(FocListFilter filter, FocObject object){
    boolean include = true;
    
    FTime timeProp = (FTime) getFieldPath().getPropertyFromObject(object);
    Time time = timeProp.getTime(); 
    Time firstTime = getFirstTime(filter);
    Time lastTime = getLastTime(filter);
    
    int op = getOperator(filter);
    
    if(op != OPERATOR_INDIFERENT){
      if(!FTime.isEmpty(firstTime) && (op == OPERATOR_BETWEEN || op == OPERATOR_GREATER_THAN)){
        include = time.after(firstTime) || time.equals(firstTime);
      }
  
      if(include && !FTime.isEmpty(lastTime) && (op == OPERATOR_BETWEEN || op == OPERATOR_LESS_THAN)){
        include = time.before(lastTime) || time.equals(lastTime);
      }

      if(include && !FTime.isEmpty(firstTime) && (op == OPERATOR_EQUALS)){
        include = time.equals(firstTime);
      }
    }
    
    return include;
  }
  
  @Override
  public void forceFocObjectToConditionValueIfNeeded(FocListFilter filter, FocObject focObject) {
    if(getOperator(filter) == OPERATOR_EQUALS){
      FTime fTimeProp = (FTime) getFieldPath().getPropertyFromObject(focObject);
      if(fTimeProp != null){
        fTimeProp.setTime(getFirstTime(filter));
      }
    }
  }

  public static StringBuffer buildSQLWhere(int provider, String fieldName, int op, Time firstTime, Time lastTime) {
    StringBuffer buffer = null;
    
    buffer = new StringBuffer();
    
    String firstDateFormat = FTime.convertTimeToSQLString(provider, firstTime);
    String lastDateFormat = FTime.convertTimeToSQLString(provider, lastTime);
    
    if (op != OPERATOR_INDIFERENT){
	    fieldName = FField.adaptFieldNameToProvider(provider, fieldName);

	    if(provider == DBManager.PROVIDER_ORACLE) {
	      if (op == OPERATOR_GREATER_THAN){ 
	        buffer.append(fieldName + ">= TO_DATE('" + firstDateFormat + "', 'dd-MM-yyyy HH24:MI:SS')");
	      }else if (op == OPERATOR_LESS_THAN) {
	      	 buffer.append(fieldName + "<= TO_DATE('" + lastDateFormat +"', 'dd-MM-yyyy HH24:MI:SS')");
	      }else if (op == OPERATOR_BETWEEN){
	      	buffer.append(fieldName + ">= TO_DATE('" + firstDateFormat + "', 'dd-MM-yyyy HH24:MI:SS') AND "+fieldName + "<= TO_DATE('" + lastDateFormat + "', 'dd-MM-yyyy HH24:MI:SS')");
	      }else if (op == OPERATOR_EQUALS){
	      	buffer.append(fieldName + " =  TO_DATE('" + firstDateFormat + "', 'dd-MM-yyyy HH24:MI:SS')");
	      }
	    } else {
	      if (op == OPERATOR_GREATER_THAN){//CAST(N'2016-06-08' AS Date) 
	        buffer.append(fieldName + ">= " + firstDateFormat);
	      }else if (op == OPERATOR_LESS_THAN) {
	        buffer.append(fieldName + "<= " + lastDateFormat);
	      }else if (op == OPERATOR_BETWEEN){
	        buffer.append(fieldName + " BETWEEN "+ firstDateFormat +" AND " + lastDateFormat);
	      }else if (op == OPERATOR_EQUALS){
	        buffer.append(fieldName + " = " + firstDateFormat);
	      }	    	
	    }
    }
    return buffer;
  }
  
  public StringBuffer buildSQLWhere(FocListFilter filter, String fieldName) {
    //b01.foc.Globals.logString("Condition sql build not implemented yet");
  	return buildSQLWhere(getProvider(), fieldName, getOperator(filter), getFirstTime(filter), getLastTime(filter));
  }
  
  public void setToOperationWithLock(FocListFilter filter, int operation, boolean lock){
  	FProperty prop = filter.getFocProperty(getFirstFieldID() + FLD_OPERATOR);
    prop.setInteger(operation);
    prop.setValueLocked(lock);
  }
  
  public void setToValue(FocListFilter filter, int operation, Time firstTime, Time lastTime){
  	setToValue(filter, operation, firstTime, lastTime, false);
  }
  
  public void forceToValue(FocListFilter filter, int operation, Time firstTime, Time lastTime){
  	setToValue(filter, operation, firstTime, lastTime, true);
  }
  
  private void setToValue(FocListFilter filter, int operation, Time firstTime, Time lastTime, boolean lockConditionAlso){
  	FProperty operatorprop = filter.getFocProperty(getFirstFieldID() + FLD_OPERATOR);
  	operatorprop.setInteger(operation);
  	if(lockConditionAlso){
  		operatorprop.setValueLocked(true);
  	}
  	
  	FTime timeCond = null;
  	
  	if(firstTime != null){
	  	timeCond = (FTime) filter.getFocProperty(getFirstFieldID() + FLD_FIRST_TIME);
	  	timeCond.setTime(firstTime);
	  	if(lockConditionAlso){
	  		timeCond.setValueLocked(true);
	  	}
  	}

  	if(lastTime != null){
	  	timeCond = (FTime) filter.getFocProperty(getFirstFieldID() + FLD_LAST_TIME);
	  	timeCond.setTime(lastTime);
	  	if(lockConditionAlso){
	  		timeCond.setValueLocked(true);
	  	}
  	}
  }

  public int fillDesc(FocDesc focDesc, int firstID){
    setFirstFieldID(firstID);
    
    if(focDesc != null){
    	FPropertyListener colorListener = new ColorPropertyListener(this, firstID + FLD_OPERATOR);
      FMultipleChoiceField multipleChoice = new FMultipleChoiceField(getFieldPrefix()+"_OP", "Operation", firstID + FLD_OPERATOR, false, 1);
      if(ConfigInfo.isArabic()){
	      multipleChoice.addChoice(OPERATOR_INDIFERENT, "لا شرط");
	      multipleChoice.addChoice(OPERATOR_BETWEEN, "بين");
	      multipleChoice.addChoice(OPERATOR_GREATER_THAN, " >= ");
	      multipleChoice.addChoice(OPERATOR_LESS_THAN, " <= ");
	      multipleChoice.addChoice(OPERATOR_EQUALS, " = ");
      }else{
        multipleChoice.addChoice(OPERATOR_INDIFERENT, "None ");
        multipleChoice.addChoice(OPERATOR_BETWEEN, "Between");
        multipleChoice.addChoice(OPERATOR_GREATER_THAN, " >= ");
        multipleChoice.addChoice(OPERATOR_LESS_THAN, " <= ");
        multipleChoice.addChoice(OPERATOR_EQUALS, " = ");
      }
      multipleChoice.setSortItems(false);
      focDesc.addField(multipleChoice);
      multipleChoice.addListener(colorListener);

      int firstTimeFLD = firstID + FLD_FIRST_TIME;
      FTimeField dateField = new FTimeField(getFieldPrefix()+"_FTIME", "First time", firstTimeFLD, false);
      focDesc.addField(dateField);
      dateField.addListener(colorListener);
      
      int lastTimeFLD = firstID + FLD_LAST_TIME;
      dateField = new FTimeField(getFieldPrefix()+"_LTIME", "Last time", lastTimeFLD, false);
      focDesc.addField(dateField);
      dateField.addListener(colorListener);
    }
    
    return firstID + FLD_OPERATOR + 1;
  }

  FGComboBox combo = null;
  Component firstTimeComp = null;
  Component lastTimeComp = null;
  FGLabel flecheComp = null;
  
  /* (non-Javadoc)
   * @see b01.foc.list.filter.FilterCondition#putInPanel(b01.foc.gui.FPanel, int, int)
   */
  public GuiSpace putInPanel(FocListFilter filter, FPanel panel, int x, int y) {
    GuiSpace space = new GuiSpace();
    
    FProperty operatorProp = filter.getFocProperty(getFirstFieldID() + FLD_OPERATOR);
    FProperty firstDateProp = filter.getFocProperty(getFirstFieldID() + FLD_FIRST_TIME);
    FProperty lastDateProp = filter.getFocProperty(getFirstFieldID() + FLD_LAST_TIME);
   
    FPanel datesPanel = new FPanel();
    
    combo = (FGComboBox) operatorProp.getGuiComponent();
    panel.add(getFieldLabel(), combo, x, y);
    
    firstTimeComp = firstDateProp.getGuiComponent();
    datesPanel.add(firstTimeComp, 0, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

    flecheComp = new FGLabel("->");
    datesPanel.add(flecheComp, 1, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
    
    lastTimeComp = lastDateProp.getGuiComponent();
    datesPanel.add(lastTimeComp, 2, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
    
    panel.add(datesPanel, x+2, y, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
    
    space.setLocation(2, 2);
    
    ItemListener itemListener = new ItemListener(){
      public void itemStateChanged(ItemEvent e) {
        if(e == null || e.getStateChange() == ItemEvent.SELECTED){
          firstTimeComp.setVisible(true);
          lastTimeComp.setVisible(true);
          flecheComp.setVisible(true);
          firstTimeComp.setVisible(false);
          lastTimeComp.setVisible(false);
          flecheComp.setVisible(false);
          
          switch(combo.getSelectedIndex()){
          case OPERATOR_BETWEEN:
            firstTimeComp.setVisible(true);
            lastTimeComp.setVisible(true);
            flecheComp.setVisible(true);
            break;
          case OPERATOR_GREATER_THAN:           
            firstTimeComp.setVisible(true);
            
            break;
          case OPERATOR_LESS_THAN:            
            lastTimeComp.setVisible(true);            
            break;
          case OPERATOR_EQUALS:
            firstTimeComp.setVisible(true);
            break;
          }
        }
      }
    };
    
    combo.addItemListener(itemListener);
    itemListener.itemStateChanged(null);
    //lastDateComp.setVisible(true);
    return space;
  }
  
  public boolean isValueLocked(FocListFilter filter){
  	boolean locked = false;
  	FProperty operatorProp = filter.getFocProperty(getFirstFieldID() + FLD_OPERATOR);
  	if(operatorProp != null){
  		locked = operatorProp.isValueLocked();
  	}
  	return locked;
  } 
  
  public void resetToDefaultValue(FocListFilter filter){
  	Time date = FTime.getZeroTime_Copy();
  	setToValue(filter, OPERATOR_INDIFERENT, date, date);
  }
  
  @Override
  public String buildDescriptionText(FocListFilter filter) {
  	String description = null;
  	
  	int op = getOperator(filter);
  	Time firstTime = getFirstTime(filter);
  	Time lastTime = getLastTime(filter);
  	
//  	op = adjustTheOperation(op, firstDate, lastDate);
  	if (op != OPERATOR_INDIFERENT){
  		String fieldName = getFieldLabel();
  		
  		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
      String firstDateFormat = dateFormat.format(firstTime);
      String lastDateFormat = dateFormat.format(lastTime);
  		
      switch(op){
      case OPERATOR_EQUALS:
      	description = fieldName + " = " + firstDateFormat; 
      	break;
      case OPERATOR_BETWEEN:
      	description = firstDateFormat + " < " + fieldName + " < " + lastDateFormat;
      	break;
      case OPERATOR_GREATER_THAN:
      	description = fieldName + " > " + firstDateFormat;
      	break;
      case OPERATOR_LESS_THAN:
      	description = fieldName + " < " + lastDateFormat;
      	break;
      }
  	}  	
  	
  	return description;
  }
}
