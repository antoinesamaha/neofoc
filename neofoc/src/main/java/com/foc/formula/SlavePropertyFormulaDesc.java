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
package com.foc.formula;

import java.util.ArrayList;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FField;
import com.foc.desc.field.FObjectField;

public class SlavePropertyFormulaDesc extends PropertyFormulaDesc {
  
  public static final int FLD_FIRST_OBJECT  = 1000;
  private int fieldCount = 0;
  private static ArrayList<IPropertyFormulaDesc> descInterface = null;
  
  public SlavePropertyFormulaDesc(){
    super();
    setFocObjectClass(SlavePropertyFormula.class);
    setStorageName("FORMULA_BY_PROP");
    
    if( descInterface != null ){
      for(int i = 0; i < descInterface.size(); i++){
        FocDesc desc = descInterface.get(i).getForeignDesc();
        newObjectField(desc);
      }  
    }
  }
  
  private int newObjectField(FocDesc desc){
    int currentFieldID = FLD_FIRST_OBJECT + fieldCount;
    
    FObjectField  objField = new FObjectField(desc.getStorageName(), desc.getStorageName(), currentFieldID , false, desc, desc.getStorageName()+"_", this, FField.FLD_PROPERTY_FORMULA_LIST );
    objField.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    objField.setWithList(false);
    addField(objField);
    
    fieldCount++;
    return currentFieldID; 
  }
  
  public int getForeignObjectFieldCount() {
    return fieldCount;
  }
  
  public static void addForeignObject(IPropertyFormulaDesc fieldFormulasInterface){
    if (descInterface == null){
      descInterface = new ArrayList<IPropertyFormulaDesc>();
    }
    descInterface.add(fieldFormulasInterface);
  }
  
  private static FocDesc focDesc = null;
  public static FocDesc getInstance() {
    if (focDesc==null){
      focDesc = new SlavePropertyFormulaDesc();
    }
    return focDesc;
  }
}
