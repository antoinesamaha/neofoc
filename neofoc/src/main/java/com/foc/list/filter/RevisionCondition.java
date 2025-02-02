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
 * Created on May 23, 2007
 */
package com.foc.list.filter;

import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import com.foc.desc.*;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldPath;
import com.foc.desc.field.FIntField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.gui.FGComboBox;
import com.foc.gui.FGLabel;
import com.foc.gui.FGNumField;
import com.foc.gui.FPanel;
import com.foc.property.FInt;
import com.foc.property.FMultipleChoice;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

/**
 * @author 01Barmaja
 */
public class RevisionCondition extends FilterCondition{
  public static final int FLD_REV = 1;
  private static final int FLD_OPERATOR = 3;

  public static final int OPERATOR_EQUALS = 0;
  public static final int OPERATOR_INSDEFFERNT = 1;
  
  public RevisionCondition(FFieldPath revisionFieldPath, String fieldPrefix){
    super(revisionFieldPath, fieldPrefix);
  }
  
  public int getRev(FocListFilter filter){
    FInt prop = (FInt) filter.getFocProperty(getFirstFieldID() + FLD_REV);
    return prop.getInteger();
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
  
  public void setRev(FocListFilter filter , int r){
    FInt rev = (FInt)filter.getFocProperty(getFirstFieldID() + FLD_REV);
    if (rev != null){
      rev.setInteger(r);
    }
  }
  
  public void setToValue(FocListFilter filter, int operation, int revision){
  	setToValue(filter, operation, revision, false);
  }
  
  public void forceToValue(FocListFilter filter, int operation, int revision){
  	setToValue(filter, operation, revision, true);
  }
  
  private void setToValue(FocListFilter filter, int operation, int revision, boolean lockConditionAlso){
  	FInt intProp = (FInt) filter.getFocProperty(getFirstFieldID() + FLD_OPERATOR);
  	intProp.setInteger(operation);
  	if(lockConditionAlso){
  		intProp.setValueLocked(true);
  	}
  	intProp = (FInt) filter.getFocProperty(getFirstFieldID() + FLD_REV);
  	intProp.setInteger(revision);
  	if(lockConditionAlso){
  		intProp.setValueLocked(true);
  	}
  }
  
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // IMPLEMENTED
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public void fillProperties(FocObject focFatherObject){
    new FMultipleChoice(focFatherObject, getFirstFieldID() + FLD_OPERATOR, OPERATOR_EQUALS);
    new FInt(focFatherObject, getFirstFieldID() + FLD_REV, 0);
  }
  
	@Override
	public void copyCondition(FocObject tarObject, FocObject srcObject, FilterCondition srcCondition) {
	  copyProperty(tarObject, getFirstFieldID() + FLD_OPERATOR, srcObject, srcCondition.getFirstFieldID() + FLD_OPERATOR);
	  copyProperty(tarObject, getFirstFieldID() + FLD_REV     , srcObject, srcCondition.getFirstFieldID() + FLD_REV);
	}
	
  public boolean includeObject(FocListFilter filter, FocObject object){
    boolean include = true;
    int op = getOperator(filter);

    if(op == OPERATOR_EQUALS){
    	FInt intProp = (FInt) getFieldPath().getPropertyFromObject(object);
      int revision = intProp != null ? intProp.getInteger() : 0;
      int rev = getRev(filter);
      include = revision == rev;
    }else if(op == OPERATOR_INSDEFFERNT){
    	include = true;
    }
    
    return include;
  }
  
  @Override
  public void forceFocObjectToConditionValueIfNeeded(FocListFilter filter, FocObject focObject) {
    if(getOperator(filter) == OPERATOR_EQUALS){
      FInt intProp = (FInt) getFieldPath().getPropertyFromObject(focObject);
      if(intProp != null){
        intProp.setInteger(getRev(filter));
      }
    }
  }
  
  public StringBuffer buildSQLWhere(FocListFilter filter, String fieldName) {
    StringBuffer buffer = null;
    int rev  = getRev(filter);
    
    int op = getOperator(filter);
    buffer = new StringBuffer();
    
    if (op == OPERATOR_EQUALS){
      String D_R = FField.DELETION_REVISION_FIELD_ID_NAME;
      buffer.append(fieldName + " <= " + rev + " AND ( " + D_R + " = " + 0 + " OR " + D_R + " > " + rev + " ) ");
    }
    return buffer;
  }
  
  public void setToOperationWithLock(FocListFilter filter, int operation, boolean lock){
  	FProperty prop = filter.getFocProperty(getFirstFieldID() + FLD_OPERATOR);
    prop.setInteger(operation);
    prop.setValueLocked(lock);
  }
  
  public int fillDesc(FocDesc focDesc, int firstID){
    setFirstFieldID(firstID);
    
    if(focDesc != null){
    	FPropertyListener colorListener = new ColorPropertyListener(this, firstID + FLD_OPERATOR);
    	
      FMultipleChoiceField multipleChoice = new FMultipleChoiceField(getFieldPrefix()+"_OP", "Operation", firstID + FLD_OPERATOR, false, 1);
      multipleChoice.addChoice(OPERATOR_EQUALS, " = ");
      multipleChoice.addChoice(OPERATOR_INSDEFFERNT, "NONE");
      multipleChoice.setSortItems(false);
      focDesc.addField(multipleChoice);
      multipleChoice.addListener(colorListener);
      
      FIntField revField = new FIntField(getFieldPrefix()+"_CREV", "Revision", firstID + FLD_REV, false, 5);
      focDesc.addField(revField);
      revField.addListener(colorListener);
    }
    return firstID + FLD_OPERATOR + 1;
  }

  FGComboBox combo = null;
  FGNumField revComp = null;
  FGLabel flecheComp = null;
  
  /* (non-Javadoc)
   * @see b01.foc.list.filter.FilterCondition#putInPanel(b01.foc.gui.FPanel, int, int)
   */
  public GuiSpace putInPanel(FocListFilter filter, FPanel panel, int x, int y) {
    GuiSpace space = new GuiSpace();
    
    FProperty operatorProp = filter.getFocProperty(getFirstFieldID() + FLD_OPERATOR);
    FProperty revProp = filter.getFocProperty(getFirstFieldID() + FLD_REV);
   
    FPanel revisionPanel = new FPanel();
    
    combo = (FGComboBox) operatorProp.getGuiComponent();
    panel.add(getFieldLabel(), combo, x, y);
    revComp = (FGNumField) revProp.getGuiComponent();
    revisionPanel.add(revComp, 0, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
    
    flecheComp = new FGLabel("->");
    revisionPanel.add(flecheComp, 1, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
    
    panel.add(revisionPanel, x+2, y, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
    
    space.setLocation(2, 2);
    ItemListener itemListener = new ItemListener(){
      public void itemStateChanged(ItemEvent e) {
        if(e == null || e.getStateChange() == ItemEvent.SELECTED){
          revComp.setVisible(true);
          flecheComp.setVisible(true);
          revComp.setVisible(false);
          flecheComp.setVisible(false);
          
          switch(combo.getSelectedIndex()){
          case OPERATOR_EQUALS:
            revComp.setVisible(true);
            break;
          }
        }
      }
    };
    
    combo.addItemListener(itemListener);
    itemListener.itemStateChanged(null);
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
  	setToValue(filter, OPERATOR_EQUALS, 1);
  }
  
  @Override
  public String buildDescriptionText(FocListFilter filter) {
  	String description = null;
  	return description;
  }
}
