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
package com.foc.db.migration;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class MigDirectoryDesc extends FocDesc {

  public static final int FLD_DIR_PATH = 2;
  
  public static final String DB_TABLE_NAME = "MIG_DIRECTORY";
    
  public MigDirectoryDesc() {
    super(MigDirectory.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, true);
    setGuiBrowsePanelClass(MigDirectoryGuiBrowsePanel.class);
    setGuiDetailsPanelClass(MigDirectoryGuiDetailsPanel.class);
        
    addReferenceField();
    
    addNameField();
    
    FStringField cFld = new FStringField("DIR_PATH", "Directory full path", FLD_DIR_PATH, false, 250);
    cFld.setMandatory(true);
    addField(cFld);
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
    list.setDirectlyEditable(false);
    list.setDirectImpactOnDatabase(true);
    if(list.getListOrder() == null){
      list.setListOrder(new FocListOrder(FField.FLD_NAME));
    }
    
    return list;
  }
  
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, MigDirectoryDesc.class);    
  }
}
