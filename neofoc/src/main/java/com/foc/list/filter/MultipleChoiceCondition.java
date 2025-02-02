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
import java.util.Iterator;

import com.foc.ConfigInfo;
import com.foc.desc.*;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldPath;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FMultipleChoiceItem;
import com.foc.gui.FPanel;
import com.foc.property.FMultipleChoice;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

/**
 * @author 01Barmaja
 */
public class MultipleChoiceCondition extends FilterCondition{
  static protected final int FLD_CONDITION_OPERATION = 1;
  static protected final int FLD_CONDITION_VALUE = 2;

  static public final int OPERATION_NONE = 0;
  static public final int OPERATION_EQUALS = 1;
  static public final int OPERATION_DIFFERENT_FROM = 3;
  
  public MultipleChoiceCondition(FFieldPath stringFieldPath, String fieldPrefix){
    super(stringFieldPath, fieldPrefix);
  }
  
  public int getOperation(FocListFilter filter){
    FProperty prop = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OPERATION);
    return prop.getInteger();
  }

  public void setOperation(FocListFilter filter, int operation){
    FProperty prop = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OPERATION);
    prop.setInteger(operation);
  }

  public int getValue(FocListFilter filter){
    FProperty prop = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_VALUE);
    return prop.getInteger();
  }

  private void setToValueWithLock(FocListFilter filter, int operation, int choice, boolean lock){
    FProperty prop = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OPERATION);
    prop.setInteger(operation);
    prop.setValueLocked(lock);
    
    prop = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_VALUE);
    prop.setInteger(choice);
    prop.setValueLocked(lock);
  }

  public void setToValue(FocListFilter filter, int operation, int choice){
  	setToValueWithLock(filter, operation, choice, false);
  }

  public void forceToValue(FocListFilter filter, int operation, int choice){
  	setToValueWithLock(filter, operation, choice, true);
  }
  
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // IMPLEMENTED
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public void fillProperties(FocObject focFatherObject){
    new FMultipleChoice(focFatherObject, getFirstFieldID() + FLD_CONDITION_OPERATION, OPERATION_NONE);
    new FMultipleChoice(focFatherObject, getFirstFieldID() + FLD_CONDITION_VALUE, 0);
  }
  
	@Override
	public void copyCondition(FocObject tarObject, FocObject srcObject, FilterCondition srcCondition) {
	  copyProperty(tarObject, getFirstFieldID() + FLD_CONDITION_OPERATION, srcObject, srcCondition.getFirstFieldID() + FLD_CONDITION_OPERATION);
	  copyProperty(tarObject, getFirstFieldID() + FLD_CONDITION_VALUE    , srcObject, srcCondition.getFirstFieldID() + FLD_CONDITION_VALUE);
	}
  
  public boolean includeObject(FocListFilter filter, FocObject object){
    boolean include = true;
    int operation = getOperation(filter);
    if(operation != OPERATION_NONE){
      int condValue = getValue(filter);
      
      FMultipleChoice itemMultiProp = (FMultipleChoice) getFieldPath().getPropertyFromObject(object);
      int itemValue = itemMultiProp != null ? itemMultiProp.getInteger() : 0;
      
      switch(operation){
        case OPERATION_EQUALS:
          include = itemValue == condValue;
          break;
        case OPERATION_DIFFERENT_FROM:
          include = itemValue != condValue;
          break;
      }
    }
    return include;
  }
  
  @Override
  public void forceFocObjectToConditionValueIfNeeded(FocListFilter filter, FocObject focObject) {
    if(getOperation(filter) == OPERATION_EQUALS){
      FMultipleChoice fMultipleChoice = (FMultipleChoice) getFieldPath().getPropertyFromObject(focObject);
      if(fMultipleChoice != null){
        fMultipleChoice.setInteger(getValue(filter));
      }
    }
  }

  public StringBuffer buildSQLWhere(FocListFilter filter, String fieldName) {
    StringBuffer buffer = null;
    
    fieldName = FField.adaptFieldNameToProvider(getProvider(), fieldName);
    
    int operation = getOperation(filter);
    if(operation != OPERATION_NONE){
      buffer = new StringBuffer();
      int condChoice = getValue(filter);
      
      switch(operation){
      case OPERATION_EQUALS:
        buffer.append(fieldName+"="+condChoice);
      break;
      case OPERATION_DIFFERENT_FROM:
        buffer.append(fieldName+"<>"+condChoice);
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
	      multipleChoice.addChoice(OPERATION_EQUALS, "يساوي");
	      multipleChoice.addChoice(OPERATION_DIFFERENT_FROM, "لايساوي");
      }else{
	      multipleChoice.addChoice(OPERATION_NONE, "None");
	      multipleChoice.addChoice(OPERATION_EQUALS, "Equals");
	      multipleChoice.addChoice(OPERATION_DIFFERENT_FROM, "Different from");
      }
      multipleChoice.setSortItems(false);
      focDesc.addField(multipleChoice);
      multipleChoice.addListener(colorListener);

      FMultipleChoiceField originalfield = (FMultipleChoiceField) getFilterDesc().getSubjectFocDesc().getFieldByPath(getFieldPath());
//      //BDebug
//      if(originalfield == null){
//      	originalfield = (FMultipleChoiceField) getFilterDesc().getSubjectFocDesc().getFieldByPath(getFieldPath());
//      }
//      //EDebug      
      multipleChoice = new FMultipleChoiceField(getFieldPrefix()+"_VAL", "Choice", firstID + FLD_CONDITION_VALUE, false, originalfield.getSize());
      Iterator iter = originalfield.getChoiceIterator();
      while(iter != null && iter.hasNext()){
        FMultipleChoiceItem choiceItem = (FMultipleChoiceItem) iter.next();
        if(choiceItem != null){
          multipleChoice.addChoice(choiceItem.getId(), choiceItem.getTitle());
        }
      }
      focDesc.addField(multipleChoice);
      multipleChoice.addListener(colorListener);
    }
    
    return firstID + FLD_CONDITION_VALUE + 1;
  }

  /* (non-Javadoc)
   * @see b01.foc.list.filter.FilterCondition#putInPanel(b01.foc.gui.FPanel, int, int)
   */
  public GuiSpace putInPanel(FocListFilter filter, FPanel panel, int x, int y) {
    GuiSpace space = new GuiSpace();
    
    FProperty operationProp = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OPERATION);
    FProperty textProp = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_VALUE);
    
    Component comp = operationProp.getGuiComponent();
    panel.add(getFieldLabel(), comp, x, y);
    
    comp = textProp.getGuiComponent();
    panel.add(comp, x+2, y, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
    
    space.setLocation(2, 2);
    return space;
  }
  
	public boolean isValueLocked(FocListFilter filter){
  	boolean locked = false;
  	FProperty operationProp = filter.getFocProperty(getFirstFieldID() + FLD_CONDITION_OPERATION);
  	if(operationProp != null){
  		locked = operationProp.isValueLocked();
  	}
  	return locked;
  }
	
	public void resetToDefaultValue(FocListFilter filter){
		setToValue(filter, OPERATION_NONE, 1);
	}
	
  public String buildDescriptionText(FocListFilter filter) {
  	String description = null;
  	
    int operation = getOperation(filter);
    if(operation != OPERATION_NONE){
    	String fieldName  = getFieldLabel();
      int value = getValue(filter);
      String valueStr = null;
      
      if(getFieldPath() != null && getFilterDesc() != null){
	      FMultipleChoiceField mFld = (FMultipleChoiceField) getFieldPath().getFieldFromDesc(getFilterDesc().getSubjectFocDesc());
	      FMultipleChoiceItem choiceItem = mFld != null ? mFld.getChoiceItemForKey(value) : null;
	      valueStr = choiceItem != null ? choiceItem.getTitle() : "";
      }
      
      if(valueStr != null){
	      switch(operation){
	      case OPERATION_EQUALS:
	      	description = fieldName + " = " + valueStr;
	      	break;
	      case OPERATION_DIFFERENT_FROM:
	      	description = fieldName + " <> " + valueStr;
	      	break;
	      }
      }
    }
  	
  	return description;
  }
}
