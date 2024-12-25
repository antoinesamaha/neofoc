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

import java.awt.GridBagConstraints;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.db.DBManager;
import com.foc.desc.*;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldPath;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FMultipleChoiceStringField;
import com.foc.gui.FGComboBox;
import com.foc.gui.FGTextField;
import com.foc.gui.FPanel;
import com.foc.property.FMultipleChoice;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;
import com.foc.property.FString;

/**
 * @author 01Barmaja
 */
public class StringCondition extends FilterCondition {
  static protected final int FLD_CONDITION_OPERATION = 1;
  static protected final int FLD_CONDITION_TEXT = 2;

  static public final int OPERATION_NONE = 0;
  static public final int OPERATION_CONTAINS = 1;
  static public final int OPERATION_STARTS_WITH = 2; 
  static public final int OPERATION_EQUALS = 3;
  static public final int OPERATION_EMPTY = 4;
  static public final int OPERATION_NOT_EMPTY = 5;
  
  public StringCondition(FFieldPath stringFieldPath, String fieldPrefix){
    super(stringFieldPath, fieldPrefix);
  }
  
  public StringCondition(int fieldID){
  	super(fieldID);
  }

  public int getOperation(FocListFilter filter){
    FProperty prop = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OPERATION);
    return prop.getInteger();
  }
  
  public void setOperation(FocListFilter filter, int operation){
    FProperty prop = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OPERATION);
    prop.setInteger(operation);
  }
  
  public void setOperation(FocListFilter filter, String operation){
    FProperty prop = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OPERATION);
    prop.setString(operation);
  }

  public String getText(FocListFilter filter){
    FProperty prop = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_TEXT);
    return prop.getString();
  }
  
  public void setText(FocListFilter filter, String text){
    FProperty prop = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_TEXT);
    prop.setString(text);
  }

  public void setToValue(FocListFilter filter, int operator, String value){
    /*FString valueProp = (FString) filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_TEXT);
    valueProp.setString(value);
    
    FProperty oppProp = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OPERATION);
    oppProp.setInteger(operator);*/
  	setToValue(filter, operator, value, false);
  }  
  
  public void forceToValue(FocListFilter filter, int operator, String value){
  	/*setToValue(filter, operator, value);
  	
    FString valueProp = (FString) filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_TEXT);
    valueProp.setValueLocked(true);*/
  	setToValue(filter, operator, value, true);
  }
  
  private void setToValue(FocListFilter filter, int operator, String value, boolean lockConditionAlso){
  	FString valueProp = (FString) filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_TEXT);
    valueProp.setString(value);
    if(lockConditionAlso){
    	valueProp.setValueLocked(true);
    }
    
    FProperty oppProp = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OPERATION);
    oppProp.setInteger(operator);
    if(lockConditionAlso){
    	valueProp.setValueLocked(true);
    }
  }
  
  public boolean isValueLocked(FocListFilter filter){
  	boolean locked = false;
  	FString valueProp = (FString) filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_TEXT);
  	if(valueProp != null){
  		locked = valueProp.isValueLocked();
  	}
  	return locked;
  }
  
  public void resetToDefaultValue(FocListFilter filter){
  	setToValue(filter, OPERATION_CONTAINS, "");
  }
  
  
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // IMPLEMENTED
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public void fillProperties(FocObject focFatherObject){
  	new FMultipleChoice(focFatherObject, getFirstFieldID() + FLD_CONDITION_OPERATION, OPERATION_CONTAINS);
    new FString(focFatherObject, getFirstFieldID() + FLD_CONDITION_TEXT, "");
  }
  
	@Override
	public void copyCondition(FocObject tarObject, FocObject srcObject, FilterCondition srcCondition) {
	  copyProperty(tarObject, getFirstFieldID() + FLD_CONDITION_OPERATION, srcObject, srcCondition.getFirstFieldID() + FLD_CONDITION_OPERATION);
	  copyProperty(tarObject, getFirstFieldID() + FLD_CONDITION_TEXT     , srcObject, srcCondition.getFirstFieldID() + FLD_CONDITION_TEXT);
	}
  
  public boolean includeObject(FocListFilter filter, FocObject object){
    boolean include = true;
    int operation = getOperation(filter);
    if(operation != OPERATION_NONE){
      String condText = getText(filter);
      
      FString textProp = (FString) getFieldPath().getPropertyFromObject(object);
      String text = textProp != null ? textProp.getString() : "";
      
      switch(operation){
        case OPERATION_EQUALS:
          include = text.toUpperCase().compareTo(condText.toUpperCase()) == 0;
          break;
        case OPERATION_CONTAINS:
          include = text.toUpperCase().contains(condText.toUpperCase());
          break;
        case OPERATION_STARTS_WITH:
          include = text.toUpperCase().startsWith(condText.toUpperCase());
          break;
        case OPERATION_EMPTY:
          include = text.trim().compareTo("") == 0;
          break;
        case OPERATION_NOT_EMPTY:
          include = text.trim().compareTo("") != 0;
          break;
      }
    }
    return include;
  }
  
  public void forceFocObjectToConditionValueIfNeeded(FocListFilter filter, FocObject focObject) {
    if(getOperation(filter) == OPERATION_EQUALS){
      FString fString = (FString) getFieldPath().getPropertyFromObject(focObject);
      if(fString != null){
        fString.setString(getText(filter));
      }
    }
  }
  
  public StringBuffer buildSQLWhere(FocListFilter filter, String fieldName) {
    //b01.foc.Globals.logString("Condition sql build not implemented yet");
    StringBuffer buffer = null;
    String text = getText(filter) != null ? getText(filter).trim() : "";
    int operation = getOperation(filter);
    
    boolean writeCondition = false;
    if(operation == OPERATION_EMPTY || operation == OPERATION_NOT_EMPTY){
    	writeCondition = true;
    }else if(operation != OPERATION_NONE){
    	writeCondition = (text.compareTo("") != 0);
    }
    
    if (writeCondition){
    	boolean ignoreCase = true;
    	
    	fieldName = FField.adaptFieldNameToProvider(getProvider(), fieldName);
    	
    	String speachMarks_Start = FField.getSpeachMarks_Start(getProvider());
    	String speachMarks_End   = FField.getSpeachMarks_End(getProvider());
    	
    	if(ignoreCase) text = text.toUpperCase();
    	
      buffer = new StringBuffer();
      switch (operation){
        case OPERATION_EQUALS:
        	if(ignoreCase){
        		buffer.append("upper(" + fieldName + ") = " + speachMarks_Start + text + speachMarks_End);
        	}else{
        		buffer.append(fieldName + " = "+speachMarks_Start + text + speachMarks_End);
        	}
        break;
        case OPERATION_STARTS_WITH:
        	/*
        	if(ignoreCase){
        		buffer.append("LEFT (upper(" + fieldName + ") , " + text.length() +") = "+ speachMarks_Start + text + speachMarks_End);
        	}else{
        		buffer.append("LEFT (" + fieldName + " , " + text.length() +") = "+ speachMarks_Start + text + speachMarks_End);
        	}
        	*/
        	if(ignoreCase){
        		buffer.append("upper("+fieldName + ") LIKE " + speachMarks_Start + text + "%" + speachMarks_End);
        	}else{
        		buffer.append(fieldName + " LIKE " + speachMarks_Start + text + "%" + speachMarks_End);
        	}
          break;
        case OPERATION_CONTAINS :
        	if(ignoreCase){
        		buffer.append("upper("+fieldName + ") LIKE " + speachMarks_Start + "%" + text + "%" + speachMarks_End);
        	}else{
        		buffer.append(fieldName + " LIKE " + speachMarks_Start + "%" + text + "%" + speachMarks_End);
        	}
          break;
        case OPERATION_EMPTY :
        	if(getProvider() == DBManager.PROVIDER_ORACLE){
        		buffer.append("trim("+fieldName +") IS NULL ");
        	}else{
        		buffer.append("(" + fieldName + " = " + speachMarks_Start + speachMarks_End + " OR " + fieldName + " IS NULL) ");
        	}
          break;
        case OPERATION_NOT_EMPTY :
        	if(getProvider() == DBManager.PROVIDER_ORACLE){
        		buffer.append("not (trim("+fieldName +") IS NULL) ");
        	}else{
        		buffer.append("(" + fieldName + " <> " + speachMarks_Start + speachMarks_End + " AND " + fieldName + " IS NOT NULL) ");
        	}
          break;
      }
    }
    return buffer;
  }
  
  public int fillDesc(FocDesc focDesc, int firstID){
    setFirstFieldID(firstID);
    
    if(focDesc != null){
    	FPropertyListener colorListener = new ColorPropertyListener(this, firstID + FLD_CONDITION_OPERATION); 
      FMultipleChoiceField multipleChoice = new FMultipleChoiceField(getFieldPrefix()+"_OP", "Operation", firstID + FLD_CONDITION_OPERATION, false, 1);
      if(ConfigInfo.isArabic()){
	      multipleChoice.addChoice(OPERATION_NONE, "لا شرط");
	      multipleChoice.addChoice(OPERATION_STARTS_WITH, "يبداء ب");
	      multipleChoice.addChoice(OPERATION_CONTAINS, "يحتوي على");
	      multipleChoice.addChoice(OPERATION_EQUALS, "=");
	      multipleChoice.addChoice(OPERATION_EMPTY, "فارغ");
	      multipleChoice.addChoice(OPERATION_NOT_EMPTY, "غير فارغ");      	
      }else{
	      multipleChoice.addChoice(OPERATION_NONE, "None");
	      multipleChoice.addChoice(OPERATION_STARTS_WITH, "Begins with");
	      multipleChoice.addChoice(OPERATION_CONTAINS, "Contains");
	      multipleChoice.addChoice(OPERATION_EQUALS, "Equals");
	      multipleChoice.addChoice(OPERATION_EMPTY, "Empty");
	      multipleChoice.addChoice(OPERATION_NOT_EMPTY, "Not Empty");
      }
      multipleChoice.setSortItems(false);
      focDesc.addField(multipleChoice);
      multipleChoice.addListener(colorListener);
      
      FField field = getFilterDesc().getSubjectFocDesc().getFieldByPath(getFieldPath());
      if(field == null){
      	FFieldPath fieldPath = getFieldPath();
      	String path = "Field Path Not Found : ";
      	if(fieldPath != null){
	      	for(int i=0; i<fieldPath.size(); i++){
	      		int fieldID = fieldPath.get(i);
	      		path += fieldID + " | ";
	      	}
	      	Globals.logString(path);
      	}
      }
      
      FStringField charField = null;
      if(field instanceof FMultipleChoiceStringField){
      	try{
					charField = (FStringField) field.clone();
					((FMultipleChoiceStringField)charField).setChoicesSelection_FieldID( ((FMultipleChoiceStringField)field).getChoicesSelection_FieldID());
					((FMultipleChoiceStringField)charField).setAllowOutofListSelection(true);
					charField.setName(getFieldPrefix()+"_TXT");
					charField.setId(firstID + FLD_CONDITION_TEXT);
					charField.setMandatory(false);
					charField.setKey(false);
				}catch (CloneNotSupportedException e){
					Globals.logException(e);
				}
      }else{
        charField = new FStringField(getFieldPrefix()+"_TXT", "Text field", firstID + FLD_CONDITION_TEXT, false, field.getSize());
        charField.setStringConverter(field.getStringConverter());
      }
      if(charField != null){//Can't be null !! But just in case
	      focDesc.addField(charField);
	      
	      charField.addListener(colorListener);
      }
    }
    
    return firstID + FLD_CONDITION_TEXT + 1;
  }

  /* (non-Javadoc)
   * @see b01.foc.list.filter.FilterCondition#putInPanel(b01.foc.gui.FPanel, int, int)
   */
  public GuiSpace putInPanel(FocListFilter filter, FPanel panel, int x, int y) {
    GuiSpace space = new GuiSpace();
    
    FProperty operationProp = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OPERATION);
    FProperty textProp = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_TEXT);
    
    FGComboBox combo = (FGComboBox) operationProp.getGuiComponent();
    panel.add(getFieldLabel(), combo, x, y);

    FGTextField txtComp = (FGTextField) textProp.getGuiComponent();
    txtComp.setColumns(25);
    panel.add(txtComp, x+2, y, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
    
    space.setLocation(2, 2);
    return space;
  }

	@Override
	public String buildDescriptionText(FocListFilter filter) {
    String description = null;
    
    int operation = getOperation(filter);
    String text = getText(filter) != null ? getText(filter).trim() : "";
    
    if((operation == OPERATION_CONTAINS || operation == OPERATION_STARTS_WITH) && text.isEmpty()) operation = OPERATION_NONE;
    
    if(operation != OPERATION_NONE){
    	String fieldName = getFieldLabel();
	    
	    switch(operation){
	    case OPERATION_EQUALS:
	    	description = fieldName + " = " + text;
	    	break;
	    case OPERATION_CONTAINS:
	    	description = fieldName + " = *" + text+"* ";
	    	break;
	    case OPERATION_STARTS_WITH:
	    	description = fieldName + " = *" + text;
	    	break;	    	
	    case OPERATION_EMPTY:
	    	description = fieldName + " = \" \"";
	    	break;	    	
	    case OPERATION_NOT_EMPTY:
	    	description = fieldName + " <> \" \"";
	    	break;	    	
	    }
    }		
		
		return description;
	}

	@Override
	public void parseString(FocListFilter filter, String str) {
		super.parseString(filter, str);
		
		String[] parts = str.split("::");
		if(parts.length > 0) {
			String operation = parts[0];
      if(ConfigInfo.isArabic()){//The String always contains English if the application is arabic we need to convert because Multiplechoice contains arabic
      	if(operation.equals("None")) operation = "لا شرط";
      	else if(operation.equals("Begins with")) operation = "يبداء ب";
      	else if(operation.equals("Contains")) operation = "يحتوي على";
      	else if(operation.equals("Equals")) operation = "=";
      	else if(operation.equals("Empty")) operation = "فارغ";
      	else if(operation.equals("Not Empty")) operation = "غير فارغ";
      }
			setOperation(filter, operation);
			
			if(parts.length > 1) {
				String text = parts[1];
			  setText(filter, text);
			}
		}
	}
}
