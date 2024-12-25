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

import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

public class PropertyFormulaDesc extends FocDesc {
  public static final int FLD_EXPRESSION                = 1;
  public static final int FLD_FIELD_NAME                = 2;
  
   public PropertyFormulaDesc(){
     
     super(PropertyFormula.class);
     setDbResident(FocDesc.DB_RESIDENT);
     setKeyUnique(false);
     
     FField focFld = addReferenceField();
     
     focFld = new FStringField("FIELDNAME", "Field Name", FLD_FIELD_NAME, false, 30);    
     addField(focFld);
     
     focFld = new FStringField("FORMULA", "Formula", FLD_EXPRESSION, false, 150);
     focFld.addListener(new FPropertyListener(){

      public void dispose() {
      }

      public void propertyModified(FProperty property) {
        PropertyFormula propertyFormula = (PropertyFormula) (property != null ? property.getFocObject() : null);
        if(propertyFormula != null){
          propertyFormula.updateFatherFormulaProperties();
        }
        
      }
       
     });
     addField(focFld);
     
   }
   
  

   //ooooooooooooooooooooooooooooooooooo
   // oooooooooooooooooooooooooooooooooo
   // SINGLE LIST
   // oooooooooooooooooooooooooooooooooo
   // oooooooooooooooooooooooooooooooooo
   
   public static FocList getList(int mode){
     return getInstance().getFocList(mode);
   }
   
   @Override
   public FocList newFocList(){
     FocList list = super.newFocList();
     list.setDirectlyEditable(true);
     list.setDirectImpactOnDatabase(false);
     if(list.getListOrder() == null){
       FocListOrder order = new FocListOrder(FField.REF_FIELD_ID);
       list.setListOrder(order);
     }
     return list;
   }
   
   //ooooooooooooooooooooooooooooooooooo
   // oooooooooooooooooooooooooooooooooo
   // SINGLE INSTANCE
   // oooooooooooooooooooooooooooooooooo
   // oooooooooooooooooooooooooooooooooo
   
   private static FocDesc focDesc = null;
   
   public static FocDesc getInstance() {
     if (focDesc==null){
       focDesc = new PropertyFormulaDesc();
     }
     return focDesc;
   }
}
