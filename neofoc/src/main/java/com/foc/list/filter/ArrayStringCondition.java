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

import com.foc.desc.*;
import com.foc.desc.field.FFieldPath;
import com.foc.property.FPropertyArray;

/**
 * @author 01Barmaja
 */
public class ArrayStringCondition extends StringCondition{

  public ArrayStringCondition(FFieldPath stringFieldPath, String fieldPrefix){
    super(stringFieldPath, fieldPrefix);
  }
  
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // IMPLEMENTED
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public boolean includeObject(FocListFilter filter, FocObject object){
    boolean include = true;
    int operation = getOperation(filter);
    if(operation != StringCondition.OPERATION_NONE){
      String condText = getText(filter);
      
      FPropertyArray propArray = (FPropertyArray) getFieldPath().getPropertyFromObject(object);
      String text = propArray != null ? propArray.getString() : "";
      
      switch(operation){
        case StringCondition.OPERATION_EQUALS:
          include = text.toUpperCase().compareTo(condText.toUpperCase()) == 0;
          break;
        case StringCondition.OPERATION_CONTAINS:
          include = text.toUpperCase().contains(condText.toUpperCase());
          break;
        case StringCondition.OPERATION_STARTS_WITH:
          include = text.toUpperCase().startsWith(condText.toUpperCase());
          break;
        case StringCondition.OPERATION_EMPTY:
          include = text.trim().compareTo("") == 0;
          break;
      }
    }
    return include;
  }  
}
