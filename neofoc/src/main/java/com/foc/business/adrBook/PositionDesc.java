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
package com.foc.business.adrBook;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.list.FocList;

public class PositionDesc extends FocDesc{

  public static final int FLD_NAME        = 1;
  
  public static final String DB_TABLE_NAME = "ADR_BK_POSITION";
  
  public PositionDesc() {
    super(Position.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, true);
    setGuiBrowsePanelClass(PositionGuiBrowsePanel.class);
    setGuiDetailsPanelClass(PositionGuiDetailsPanel.class);
    FField fField = addReferenceField();
    
    fField = new FStringField("NAME", "Name", FLD_NAME, true, 30);
    fField.setMandatory(true);
    fField.setLockValueAfterCreation(true);
    addField(fField);
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
    return list;
  }
  
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

//  private static FocDesc focDesc = null;
  public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, PositionDesc.class);
  }
}
