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
import java.sql.Date;
import java.text.SimpleDateFormat;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.business.dateShifter.DateShifter;
import com.foc.business.dateShifter.DateShifterDesc;
import com.foc.db.DBManager;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FDateField;
import com.foc.desc.field.FDateTimeField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldPath;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.gui.FGComboBox;
import com.foc.gui.FGLabel;
import com.foc.gui.FPanel;
import com.foc.property.FDateTime;
import com.foc.property.FInt;
import com.foc.property.FMultipleChoice;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;
import com.foc.property.FTime;

/**
 * @author 01Barmaja
 */
public class DateTimeCondition extends FilterCondition {
  private static final int FLD_FIRST_DATE = 1;
  private static final int FLD_LAST_DATE = 2;
  private static final int FLD_OPERATOR = 3;

  public static final int OPERATOR_INDIFERENT= 0;
  public static final int OPERATOR_EQUALS = 1;
  public static final int OPERATOR_GREATER_THAN = 2;
  public static final int OPERATOR_BETWEEN = 3;
  public static final int OPERATOR_LESS_THAN = 4;
  
  private DateShifterDesc firstDateShifterDesc = null;
  private DateShifterDesc lastDateShifterDesc  = null;
  
  public DateTimeCondition(FFieldPath dateFieldPath, String fieldPrefix){
    super(dateFieldPath, fieldPrefix);
  }
  
  public DateTimeCondition(int fieldID){
  	super(fieldID);
  }

  public java.sql.Date getFirstDateTime(FocListFilter filter){
    FDateTime prop = (FDateTime) filter.getFocProperty(getFirstFieldID() + FLD_FIRST_DATE);
    return prop.getDate();//this method was on Fdate
  }

  public java.sql.Date getLastDateTime(FocListFilter filter){
  	FDateTime prop = (FDateTime) filter.getFocProperty(getFirstFieldID() + FLD_LAST_DATE);
    return prop.getDate();
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
  
  public void setFirstDateTime(FocListFilter filter ,Date d){
    FDateTime date = (FDateTime)filter.getFocProperty(getFirstFieldID() + FLD_FIRST_DATE);
    if (date != null){
      date.setDate(d);
    }
  }
  
  public void setLastDateTime(FocListFilter filter ,Date d){
    FDateTime dateTime = (FDateTime)filter.getFocProperty(getFirstFieldID() + FLD_LAST_DATE);
    if (dateTime != null){
    	dateTime.setDate(d);
    }
  }
  
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // IMPLEMENTED
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public void fillProperties(FocObject focFatherObject){
  	new FMultipleChoice(focFatherObject, getFirstFieldID() + FLD_OPERATOR, OPERATOR_GREATER_THAN);
    new FDateTime(focFatherObject, getFirstFieldID() + FLD_FIRST_DATE, new Date(0));
    new FDateTime(focFatherObject, getFirstFieldID() + FLD_LAST_DATE, new Date(0));
  }
  
	@Override
	public void copyCondition(FocObject tarObject, FocObject srcObject, FilterCondition srcCondition) {
	  copyProperty(tarObject, getFirstFieldID() + FLD_OPERATOR  , srcObject, srcCondition.getFirstFieldID() + FLD_OPERATOR);
	  copyProperty(tarObject, getFirstFieldID() + FLD_FIRST_DATE, srcObject, srcCondition.getFirstFieldID() + FLD_FIRST_DATE);
	  copyProperty(tarObject, getFirstFieldID() + FLD_LAST_DATE , srcObject, srcCondition.getFirstFieldID() + FLD_LAST_DATE);
	}

  public boolean includeObject(FocListFilter filter, FocObject object){
    boolean include = true;
    
    FDateTime dateTimeProp = (FDateTime) getFieldPath().getPropertyFromObject(object);
    Date date = dateTimeProp.getDate(); 
    Date firstDate = getFirstDateTime(filter);
    Date lastDate = getLastDateTime(filter);
    
    int op = getOperator(filter);
    
    if(op != OPERATOR_INDIFERENT){
      if(!FDateTime.isEmpty(firstDate) && (op == OPERATOR_BETWEEN || op == OPERATOR_GREATER_THAN)){
        include = date.after(firstDate) || date.equals(firstDate);
      }
  
      if(include && !FDateTime.isEmpty(lastDate) && (op == OPERATOR_BETWEEN || op == OPERATOR_LESS_THAN)){
        include = date.before(lastDate) || date.equals(lastDate);
      }

      if(include && !FDateTime.isEmpty(firstDate) && (op == OPERATOR_EQUALS)){
        include = date.equals(firstDate);
      }
    }
    
    return include;
  }
  
  @Override
  public void forceFocObjectToConditionValueIfNeeded(FocListFilter filter, FocObject focObject) {
    if(getOperator(filter) == OPERATOR_EQUALS){
      FDateTime fDateTime = (FDateTime) getFieldPath().getPropertyFromObject(focObject);
      if(fDateTime != null){
      	fDateTime.setDate(getFirstDateTime(filter));
      }
    }
  }

  public static int adjustTheOperation(int op, Date firstDate, Date lastDate) {
    if (op != OPERATOR_INDIFERENT){
    	if(op == OPERATOR_BETWEEN && firstDate.getTime() < Globals.DAY_TIME) op = OPERATOR_LESS_THAN;
    	if(op == OPERATOR_BETWEEN && lastDate.getTime() < Globals.DAY_TIME) op = OPERATOR_GREATER_THAN;
	    if(op == OPERATOR_GREATER_THAN && firstDate.getTime() < Globals.DAY_TIME) op = OPERATOR_INDIFERENT;
	    if(op == OPERATOR_LESS_THAN && lastDate.getTime() < Globals.DAY_TIME) op = OPERATOR_INDIFERENT;
    }
    return op;
  }
  
  public static StringBuffer buildSQLWhere(int provider, String fieldName, int op, Date firstDate, Date lastDate) {
    StringBuffer buffer = null;
    
    buffer = new StringBuffer();
    
    String firstDateFormat = FTime.convertTimeToSQLString(provider, firstDate);
    String lastDateFormat = FTime.convertTimeToSQLString(provider, lastDate);
    
    if (op != OPERATOR_INDIFERENT){
	    fieldName = FField.adaptFieldNameToProvider(provider, fieldName);

	    if(provider == DBManager.PROVIDER_ORACLE) {
	      if (op == OPERATOR_GREATER_THAN){ 
	        buffer.append("TRUNC(" +fieldName+",'MI')"+ ">= TO_DATE('" + firstDateFormat + "', 'dd-MM-yyyy HH24:MI:SS')");
	      }else if (op == OPERATOR_LESS_THAN) {
	      	 buffer.append("TRUNC(" +fieldName+",'MI')"+ "<= TO_DATE('" + lastDateFormat +"', 'dd-MM-yyyy HH24:MI:SS')");
	      }else if (op == OPERATOR_BETWEEN){
	      	buffer.append("TRUNC(" +fieldName+",'MI')" + ">= TO_DATE('" + firstDateFormat + "', 'dd-MM-yyyy HH24:MI:SS') AND "+"TRUNC(" +fieldName+",'MI')" + "<= TO_DATE('" + lastDateFormat + "', 'dd-MM-yyyy HH24:MI:SS')");
	      }else if (op == OPERATOR_EQUALS){
	      	buffer.append("TRUNC(" +fieldName+",'MI')" + " =  TO_DATE('" + firstDateFormat + "', 'dd-MM-yyyy HH24:MI:SS')");
	      }
	    } else {
	      if (op == OPERATOR_GREATER_THAN){//CAST(N'2016-06-08' AS Date) 
	        buffer.append(fieldName + ">= '" + firstDateFormat+"'");
	      }else if (op == OPERATOR_LESS_THAN) {
	        buffer.append(fieldName + "<= '" + lastDateFormat+"'");
	      }else if (op == OPERATOR_BETWEEN){
	        buffer.append(fieldName + " BETWEEN '"+ firstDateFormat +"' AND '" + lastDateFormat+"'");
	      }else if (op == OPERATOR_EQUALS){
	        buffer.append(fieldName + " = '" + firstDateFormat+"'");
	      }	    	
	    }
    }
    return buffer;
  }
  
  /*public static StringBuffer buildSQLWhere(int provider, String fieldName, int op, Date firstDate, Date lastDate) {
    StringBuffer buffer = null;    
    buffer = new StringBuffer();

    String firstDateFormat = "";
    String lastDateFormat = "";
    if (provider == DBManager.PROVIDER_ORACLE){
    	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
      firstDateFormat = dateFormat.format(firstDate);
      lastDateFormat = dateFormat.format(lastDate);
    }else{
      firstDateFormat = String.valueOf(firstDate);
      lastDateFormat = String.valueOf(lastDate);
    }
    
    op = adjustTheOperation(op, firstDate, lastDate); 
    
    if (op != OPERATOR_INDIFERENT){
	    fieldName = FField.adaptFieldNameToProvider(provider, fieldName);
	    
	    if(provider == DBManager.PROVIDER_MSSQL){
	      if (op == OPERATOR_GREATER_THAN){//CAST(N'2016-06-08' AS Date) 
	        buffer.append(fieldName + ">= CAST(N'" + firstDateFormat + "' AS Date)");
	      }else if (op == OPERATOR_LESS_THAN) {
	        buffer.append(fieldName + "<= CAST(N'" + lastDateFormat + "' AS Date)");
	      }else if (op == OPERATOR_BETWEEN){
	        buffer.append(fieldName + " BETWEEN CAST(N'" + firstDateFormat + "' AS Date) AND CAST(N'" + lastDateFormat + "' AS Date)" );
	      }else if (op == OPERATOR_EQUALS){
	        buffer.append(fieldName + " = CAST(N'" + firstDateFormat + "' AS Date)");
	      }
	    }else if(provider == DBManager.PROVIDER_ORACLE){
//	    	fieldName = "\""+fieldName+"\"";
	      if (op == OPERATOR_GREATER_THAN){//CAST(N'2016-06-08' AS Date) 
	        buffer.append("TRUNC(" + fieldName + ") >= TO_DATE('" + firstDateFormat + "', 'dd-MM-yyyy')");
	      }else if (op == OPERATOR_LESS_THAN) {
	        buffer.append("TRUNC(" + fieldName + ") <= TO_DATE('" + lastDateFormat + "', 'dd-MM-yyyy')");
	      }else if (op == OPERATOR_BETWEEN){
	        buffer.append("TRUNC(" + fieldName + ") >= TO_DATE('" + firstDateFormat + "', 'dd-MM-yyyy') AND "+fieldName + "<= TO_DATE('" + lastDateFormat + "', 'dd-MM-yyyy')" );
	      }else if (op == OPERATOR_EQUALS){
	        buffer.append("TRUNC(" + fieldName + ") = TO_DATE('" + firstDateFormat + "', 'dd-MM-yyyy')");
	      }
	    }else if(provider == DBManager.PROVIDER_H2){
	      if (op == OPERATOR_GREATER_THAN){
	        buffer.append(fieldName + ">= '" + firstDateFormat + "'");
	      }else if (op == OPERATOR_LESS_THAN) {
	        buffer.append(fieldName + "<= '" + lastDateFormat + "'");
	      }else if (op == OPERATOR_BETWEEN){
	        buffer.append(fieldName + " BETWEEN '" + firstDateFormat + "' AND '" + lastDateFormat + "'" );
	      }else if (op == OPERATOR_EQUALS){
	        buffer.append(fieldName + " = '" + firstDateFormat + "'");
	      }
	    }else{
	      if (op == OPERATOR_GREATER_THAN){
	        buffer.append(fieldName + ">= \"" + firstDateFormat + "\"");
	      }else if (op == OPERATOR_LESS_THAN) {
	        buffer.append(fieldName + "<= \"" + lastDateFormat + "\"");
	      }else if (op == OPERATOR_BETWEEN){
	        buffer.append(fieldName + " BETWEEN \"" + firstDateFormat + " \" AND \"" + lastDateFormat + "\"" );
	      }else if (op == OPERATOR_EQUALS){
	        buffer.append(fieldName + " = \"" + firstDateFormat + "\"");
	      }
	    }
    }
    return buffer;
  }*/
  
  public StringBuffer buildSQLWhere(FocListFilter filter, String fieldName) {
    //b01.foc.Globals.logString("Condition sql build not implemented yet");
  	return buildSQLWhere(getProvider(), fieldName, getOperator(filter), getFirstDateTime(filter), getLastDateTime(filter));
  }
  
  public void setToOperationWithLock(FocListFilter filter, int operation, boolean lock){
  	FProperty prop = filter.getFocProperty(getFirstFieldID() + FLD_OPERATOR);
    prop.setInteger(operation);
    prop.setValueLocked(lock);
  }
  
  public void setToValue(FocListFilter filter, int operation, Date firstDate, Date lastDate){
  	setToValue(filter, operation, firstDate, lastDate, false);
  }
  
  public void forceToValue(FocListFilter filter, int operation, Date firstDate, Date lastDate){
  	setToValue(filter, operation, firstDate, lastDate, true);
  }
  
  private void setToValue(FocListFilter filter, int operation, Date firstDate, Date lastDate, boolean lockConditionAlso){
  	FProperty operatorprop = filter.getFocProperty(getFirstFieldID() + FLD_OPERATOR);
  	operatorprop.setInteger(operation);
  	if(lockConditionAlso){
  		operatorprop.setValueLocked(true);
  	}
  	
  	FDateTime dateCond = null;
  	
  	if(firstDate != null){
	  	dateCond = (FDateTime) filter.getFocProperty(getFirstFieldID() + FLD_FIRST_DATE);
	  	dateCond.setDate(firstDate);
	  	if(lockConditionAlso){
	  		dateCond.setValueLocked(true);
	  	}
  	}

  	if(lastDate != null){
	  	dateCond = (FDateTime) filter.getFocProperty(getFirstFieldID() + FLD_LAST_DATE);
	  	dateCond.setDate(lastDate);
	  	if(lockConditionAlso){
	  		dateCond.setValueLocked(true);
	  	}
  	}
  }

  public int fillDesc(FocDesc focDesc, int firstID){
  	int nextIdx = firstID;
  	
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

      int firstDateFLD = firstID + FLD_FIRST_DATE;
      FDateTimeField dateField = new FDateTimeField(getFieldPrefix()+"_FDATE", "First date", firstDateFLD, false);
      focDesc.addField(dateField);
      dateField.addListener(colorListener);
      
      int lastDateFLD = firstID + FLD_LAST_DATE;
      dateField = new FDateTimeField(getFieldPrefix()+"_LDATE", "Last date", lastDateFLD, false);
      focDesc.addField(dateField);
      dateField.addListener(colorListener);

      IFocDescForFilter focDescForFilter = (IFocDescForFilter) focDesc;
      FilterDesc filterDesc = focDescForFilter != null ? focDescForFilter.getFilterDesc() : null;
      
      firstDateShifterDesc = new DateShifterDesc(focDesc, firstID + FLD_OPERATOR + 1, getFieldPrefix()+"_F_", null, firstDateFLD, DateShifterDesc.ADJUST_LOW);
      nextIdx = firstDateShifterDesc.addFields();
      if(filterDesc != null) {
      	filterDesc.putDateShifterDesc(firstDateShifterDesc.getFieldsShift(), firstDateShifterDesc);
      }
      	
      lastDateShifterDesc = new DateShifterDesc(focDesc, nextIdx, getFieldPrefix()+"_L_", null, lastDateFLD, DateShifterDesc.ADJUST_UP);
      nextIdx = lastDateShifterDesc.addFields();
      if(filterDesc != null) {
      	filterDesc.putDateShifterDesc(lastDateShifterDesc.getFieldsShift(), lastDateShifterDesc);
      }
    }
    
    return nextIdx;//firstID + FLD_OPERATOR + 1;
  }

  FGComboBox combo = null;
  Component firstDateComp = null;
  Component lastDateComp = null;
  FGLabel flecheComp = null;
  
  /* (non-Javadoc)
   * @see b01.foc.list.filter.FilterCondition#putInPanel(b01.foc.gui.FPanel, int, int)
   */
  public GuiSpace putInPanel(FocListFilter filter, FPanel panel, int x, int y) {
    GuiSpace space = new GuiSpace();
    
    FProperty operatorProp = filter.getFocProperty(getFirstFieldID() + FLD_OPERATOR);
    FProperty firstDateProp = filter.getFocProperty(getFirstFieldID() + FLD_FIRST_DATE);
    FProperty lastDateProp = filter.getFocProperty(getFirstFieldID() + FLD_LAST_DATE);
   
    FPanel datesPanel = new FPanel();
    
    combo = (FGComboBox) operatorProp.getGuiComponent();
    panel.add(getFieldLabel(), combo, x, y);
    
    firstDateComp = firstDateProp.getGuiComponent();
    datesPanel.add(firstDateComp, 0, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

    flecheComp = new FGLabel("->");
    datesPanel.add(flecheComp, 1, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
    
    lastDateComp = lastDateProp.getGuiComponent();
    datesPanel.add(lastDateComp, 2, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
    
    panel.add(datesPanel, x+2, y, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
    
    space.setLocation(2, 2);
    
    ItemListener itemListener = new ItemListener(){
      public void itemStateChanged(ItemEvent e) {
        if(e == null || e.getStateChange() == ItemEvent.SELECTED){
          firstDateComp.setVisible(true);
          lastDateComp.setVisible(true);
          flecheComp.setVisible(true);
          firstDateComp.setVisible(false);
          lastDateComp.setVisible(false);
          flecheComp.setVisible(false);
          
          switch(combo.getSelectedIndex()){
          case OPERATOR_BETWEEN:
            firstDateComp.setVisible(true);
            lastDateComp.setVisible(true);
            flecheComp.setVisible(true);
            break;
          case OPERATOR_GREATER_THAN:           
            firstDateComp.setVisible(true);
            
            break;
          case OPERATOR_LESS_THAN:            
            lastDateComp.setVisible(true);            
            break;
          case OPERATOR_EQUALS:
            firstDateComp.setVisible(true);
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
  	Date date = new Date(0);
  	setToValue(filter, OPERATOR_GREATER_THAN, date, date);
  }
  
  @Override
  public String buildDescriptionText(FocListFilter filter) {
  	String description = null;
  	
  	FDateTime dateProp = (FDateTime) filter.getFocProperty(getFirstFieldID() + FLD_FIRST_DATE);
  	FDateField dateField = dateProp != null ? (FDateField) dateProp.getFocField() : null;
  	String format = dateField != null ? dateField.newDateFormat() : null;
  	
  	int op = getOperator(filter);
  	Date firstDate = getFirstDateTime(filter);
  	Date lastDate = getLastDateTime(filter);
  	op = adjustTheOperation(op, firstDate, lastDate);
  	if (op != OPERATOR_INDIFERENT){
  		String fieldName = getFieldLabel();
	  	
      SimpleDateFormat dateFormat = new SimpleDateFormat(format);
      String firstDateFormat = dateFormat.format(firstDate);
      String lastDateFormat = dateFormat.format(lastDate);
  		
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
  
  public void computeDatesFromShifters(FocListFilter filter) {
  	DateShifter dateShifter = new DateShifter(filter, firstDateShifterDesc);
  	if(dateShifter != null) {
	  	dateShifter.adjustDate();
	  	dateShifter.dispose();
  	}
  	
  	dateShifter = new DateShifter(filter, lastDateShifterDesc);
  	if(dateShifter != null) {
	  	dateShifter.adjustDate();
	  	dateShifter.dispose();
  	}
  }

	public DateShifterDesc getFirstDateShifterDesc() {
		return firstDateShifterDesc;
	}

	public DateShifterDesc getLastDateShifterDesc() {
		return lastDateShifterDesc;
	}
}
