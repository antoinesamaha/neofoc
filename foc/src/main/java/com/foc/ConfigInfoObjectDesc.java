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
package com.foc;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class ConfigInfoObjectDesc extends FocDesc {
  public static final int FLD_PROPERTY            = 1;
  public static final int FLD_VALUE               = 2;
  
  public static final String DB_TABLE_NAME = "ConfigInfoObject";
  
  public ConfigInfoObjectDesc(){
    super(ConfigInfoObject.class, FocDesc.NOT_DB_RESIDENT, DB_TABLE_NAME, false);
    setGuiBrowsePanelClass(ConfigInfoObjectGuiBrowsePanel.class);  
    
    FField focFld = addReferenceField();
    focFld = new FStringField("PROPERTY", "Property", FLD_PROPERTY,  true, 30);    
    addField(focFld);
    
    focFld = new FStringField("VALUE", "Value", FLD_VALUE,  true, 30);    
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

  public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, ConfigInfoObjectDesc.class);
  }
}
