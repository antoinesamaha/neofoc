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
package com.foc.fUnit;

public class NotUsed {
  /*
  // #######################################
  // #######################################
  // TEXT FIELD
  // #######################################
  // #######################################
  
  public void textField_SetText(Container rootContainer, String table, String field, String value, int maxAttempts) {
    FGTextField textField = (FGTextField) getChildNamed(rootContainer, table, field, maxAttempts);
    if (textField != null) {
      textField = (FGTextField) preventAlterningIfDisabled(textField);
      
      focAssertTrue("Focused", textField.requestFocusInWindow());
      
      textField.setText(value);
      textField.postActionEvent();
    }
  }
  
  public void textField_SetText(String table, String field, String value, int maxAttempts) {
    textField_SetText(null, table, field, value, maxAttempts);
  }
  
  public void textField_SetText(Container rootContainer, String table, String field, String value) {
    textField_SetText(rootContainer, table, field, value, DEFAULT_NUMBER_OF_ATTEMPTS);
  }
  
  public void textField_SetText(String table, String field, String value) {
    textField_SetText(null, table, field, value, DEFAULT_NUMBER_OF_ATTEMPTS);
  }
  
  public void textField_SetText(int fieldId, String value) {
    focAssertNotNull("Default FocDesc not NULL ?", getDefaultFocDesc());
    textField_SetText(getDefaultFocDesc().getStorageName(), getDefaultFocDesc().getFieldByID(fieldId).getName(), value, DEFAULT_NUMBER_OF_ATTEMPTS);
  }

  // #######################################
  // #######################################
  // COMBO BOX
  // #######################################
  // #######################################
  
  public void comboBox_SetSelection(Container rootContainer, String table, String field, String value, int maxAttempts) {
    FGComboBox combo = (FGComboBox) getChildNamed(rootContainer, table, field, maxAttempts);
    // focAssertNotNull("ComboBox found ? ", combo);
    focAssertTrue("Focused", combo.requestFocusInWindow());
    combo.setSelectedItem(value);
  }
  
  public void comboBox_SetSelection(int fieldId, String value) {
    focAssertNotNull("Default FocDesc not NULL ?", getDefaultFocDesc());
    comboBox_SetSelection(null, getDefaultFocDesc().getStorageName(), getDefaultFocDesc().getFieldByID(fieldId).getName(), value, DEFAULT_NUMBER_OF_ATTEMPTS);
  }
  
  public void Click_TabbedPane(String paneTitle) {
    Component comp = getChildNamed(paneTitle, DEFAULT_NUMBER_OF_ATTEMPTS, true);
    
    ArrayList<Component> tabbedArray = new ArrayList<Component>();
    if (comp != null) {
      Component root = getDefaultRootContainer();
      
      Component parent = comp;
      while(parent != root && parent != null){
        tabbedArray.add(parent);
        parent = parent.getParent();
      }
      


//      if (tabbedArray.size() == 1) {
//        tp = tabbedArray.get(0);
//      } else {
//        for (int i = (tabbedArray.size() - 1); i > 0; i--) {
//          tp = tabbedArray.get(i);
//          focAssertTrue("Focused", tp.requestFocusInWindow());
//          tp.setSelectedComponent(tabbedArray.get(i - 1));
//          if ((i - 1) == 0) {
//            tp = tabbedArray.get(i - 1);
//          }
//        }
//      }
//      sleep(1);
//      focAssertTrue("Focused", tp.requestFocusInWindow());
//      tp.setSelectedComponent(comp);

    }
  }
  
  */
  
}
