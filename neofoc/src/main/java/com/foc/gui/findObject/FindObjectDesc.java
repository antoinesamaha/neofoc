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
package com.foc.gui.findObject;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class FindObjectDesc extends FocDesc {
  
  public static final int FLD_FIND                = 1;
  public static final int FLD_STARTS_WITH         = 2;
  public static final int FLD_CONTAINS            = 3;
  
  public static final String DB_TABLE_NAME = "FIND_OBJECT";
  
  public FindObjectDesc(){
    super(FindObject.class, FocDesc.NOT_DB_RESIDENT, DB_TABLE_NAME, false);
    setGuiDetailsPanelClass(FindObjectGuiDetailsPanel.class);
    
    FField focField = addReferenceField();
    
    focField = new FStringField("FIND", "Find", FLD_FIND, false, 30);
    addField(focField);
    
    focField = new FBoolField("STARTS_WITH", "Starts With", FLD_STARTS_WITH, false);    
    addField(focField);
    
    focField = new FBoolField("CONTAINS", "Contains", FLD_CONTAINS, false);    
    addField(focField);
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
    if (list.getListOrder() == null) {
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

  
  public static FindObjectDesc getInstance() {
    return (FindObjectDesc) getInstance(DB_TABLE_NAME, FindObjectDesc.class);    
  }
}
