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

import com.foc.ConfigInfo;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldPath;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.gui.FPanel;
import com.foc.property.FBoolean;
import com.foc.property.FInt;
import com.foc.property.FMultipleChoice;
import com.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
public class BooleanCondition extends FilterCondition{
  private static final int FLD_VALUE = 1;

  public static final int VALUE_INDIFFERENT = 0;
  public static final int VALUE_TRUE = 1;
  public static final int VALUE_FALSE = 2; 
  
  public BooleanCondition(FFieldPath boolFieldPath, String fieldPrefix){
    super(boolFieldPath, fieldPrefix);
  }
  
  public int getValue(FocListFilter filter){
    FInt prop = (FInt) filter.getFocProperty(getFirstFieldID() + FLD_VALUE);
    return prop.getInteger();
  }

  public void setValue(FocListFilter filter, int iValue){
    /*FInt prop = (FInt) filter.getFocProperty(getFirstFieldID() + FLD_VALUE);
    prop.setInteger(iValue);*/
  	setValue(filter, iValue, false);
  }

  public FProperty getValueProperty(FocListFilter filter){
    return (FInt) filter.getFocProperty(getFirstFieldID() + FLD_VALUE);
  }

  public void forceToValue(FocListFilter filter, int value){
    /*FInt valueProp = (FInt) filter.getFocProperty(getFirstFieldID() + FLD_VALUE);
    valueProp.setInteger(value);
    valueProp.setValueLocked(true);*/
  	setValue(filter, value, true);
  }
  
  private void setValue(FocListFilter filter, int iValue, boolean lockConditionAlso){
  	FInt valueProp = (FInt) filter.getFocProperty(getFirstFieldID() + FLD_VALUE);
    valueProp.setInteger(iValue);
    if(lockConditionAlso){
    	valueProp.setValueLocked(true);
    }
  }

  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // IMPLEMENTED
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public void fillProperties(FocObject focFatherObject){
    new FMultipleChoice(focFatherObject, getFirstFieldID() + FLD_VALUE, VALUE_INDIFFERENT);
  }
  
	@Override
	public void copyCondition(FocObject tarObject, FocObject srcObject, FilterCondition srcCondition) {
	  copyProperty(tarObject, getFirstFieldID() + FLD_VALUE, srcObject, srcCondition.getFirstFieldID() + FLD_VALUE);
	}

  public boolean includeObject(FocListFilter filter, FocObject object){
    boolean include = true;
    if(getValue(filter) != VALUE_INDIFFERENT){
      include = false;
      
      FBoolean boolProp = (FBoolean) getFieldPath().getPropertyFromObject(object);
      if(boolProp != null){
        boolean boolVal = boolProp != null ? boolProp.getBoolean() : false;
        if(getValue(filter) == VALUE_TRUE){
          include = boolVal;
        }else{
          include = !boolVal;
        }
      }
    }
    return include;
  }
  
  @Override
  public void forceFocObjectToConditionValueIfNeeded(FocListFilter filter, FocObject focObject) {
    if(getValue(filter) != VALUE_INDIFFERENT){
      FBoolean boolProp = (FBoolean) getFieldPath().getPropertyFromObject(focObject);
      if(boolProp != null){
        if( getValue(filter) == VALUE_TRUE ){
          boolProp.setBoolean(true);
        }else{
          boolProp.setBoolean(false);
        }
      }
    }
  }
  
  public StringBuffer buildSQLWhere(FocListFilter filter, String fieldName) {
    StringBuffer buffer = null;
    int valueCondition = getValue(filter);
    if(valueCondition != VALUE_INDIFFERENT){
      buffer = new StringBuffer();
      
      fieldName = FField.adaptFieldNameToProvider(getProvider(), fieldName);
      
      if(valueCondition == VALUE_TRUE){
        buffer.append(fieldName+"=1");
      }else{
      	if(ConfigInfo.isAdaptConstraints()) {
      		buffer.append(fieldName+" IS NULL OR "+fieldName+"=0");
      	} else {
      		buffer.append(fieldName+"=0");
      	}
      }
    }
    return buffer;
  }
  
  public int fillDesc(FocDesc focDesc, int firstID){
    setFirstFieldID(firstID);
    
    if(focDesc != null){
      FMultipleChoiceField multipleChoice = new FMultipleChoiceField(getFieldPrefix()+"_VAL", "Value", firstID + FLD_VALUE, false, 1);
      if(ConfigInfo.isArabic()){
	      multipleChoice.addChoice(VALUE_INDIFFERENT, "لا شرط");
	      multipleChoice.addChoice(VALUE_TRUE, "نعم");
	      multipleChoice.addChoice(VALUE_FALSE, "لا");
      }else{      
	      multipleChoice.addChoice(VALUE_INDIFFERENT, "All");
	      multipleChoice.addChoice(VALUE_TRUE, "True");
	      multipleChoice.addChoice(VALUE_FALSE, "False");
      }
      multipleChoice.setSortItems(false);
      focDesc.addField(multipleChoice);
      multipleChoice.addListener(new ColorPropertyListener(this, firstID + FLD_VALUE));
    }
    
    return firstID + FLD_VALUE + 1;
  }

  /* (non-Javadoc)
   * @see b01.foc.list.filter.FilterCondition#putInPanel(b01.foc.gui.FPanel, int, int)
   */
  public GuiSpace putInPanel(FocListFilter filter, FPanel panel, int x, int y) {
    GuiSpace space = new GuiSpace();
    
    FProperty valueProp = filter.getFocProperty(getFirstFieldID() + FLD_VALUE);
    
    Component active = (Component) valueProp.getGuiComponent();
    panel.add(getFieldLabel(), active, x, y);
    
    space.setLocation(2, 2);
    return space;
  }
  
  public boolean isValueLocked(FocListFilter filter){
  	boolean locked = false;
  	FProperty valueProp = filter.getFocProperty(getFirstFieldID() + FLD_VALUE);
  	if(valueProp != null){
  		locked = valueProp.isValueLocked();
  	}
  	return locked;
  }
  
  public void resetToDefaultValue(FocListFilter filter){
  	setValue(filter, VALUE_INDIFFERENT);
  }
  
	@Override
	public String buildDescriptionText(FocListFilter filter) {
		String description = null;
		String trueTxt = " = TRUE";
		String falseTxt = " = FALSE";
		if (ConfigInfo.isArabic()) {
			trueTxt = " = نعم";
			falseTxt = " = لا";
		}
		int valueCondition = getValue(filter);
		if (valueCondition != VALUE_INDIFFERENT) {
			String fieldName = getFieldLabel();
			if (valueCondition == VALUE_TRUE) {
				description = (fieldName + trueTxt);
			} else {
				description = (fieldName + falseTxt);
			}
		}
		return description;
	}
}
