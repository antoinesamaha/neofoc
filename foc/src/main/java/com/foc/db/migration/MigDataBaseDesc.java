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
import com.foc.desc.field.FPasswordField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class MigDataBaseDesc extends FocDesc {

  public static final int FLD_DESCRIPTION = 2;

  public static final int FLD_JDBC_DRIVER = 3;  
  public static final int FLD_URL         = 4;
  public static final int FLD_USER_NAME   = 5;
  public static final int FLD_PASSWORD    = 6;
  
  public static final String DB_TABLE_NAME = "MIG_DB_CREDENTIALS";
    
  public MigDataBaseDesc() {
    super(MigDataBase.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, true);
    setGuiBrowsePanelClass(MigDataBaseGuiBrowsePanel.class);
    setGuiDetailsPanelClass(MigDataBaseGuiDetailsPanel.class);
        
    addReferenceField();
    
    addNameField();
    
    FStringField cFld = new FStringField("DESCRIP", "Description", FLD_DESCRIPTION, false, 40);
    cFld.setMandatory(true);
    addField(cFld);
    
    cFld = new FStringField("URL", "DB url", FLD_URL, false, 100);
    cFld.setMandatory(true);
    addField(cFld);
    
    cFld = new FStringField("USER_NAME", "DB User name", FLD_USER_NAME, false, 30);
    cFld.setMandatory(true);
    addField(cFld);    
    
    cFld = new FPasswordField("PASSWORD", "DB Password", FLD_PASSWORD, false, 30);
    cFld.setMandatory(true);
    addField(cFld);
    
    cFld = new FStringField("DRIVER", "JDBC Driver", FLD_JDBC_DRIVER, false, 60);
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
    return getInstance(DB_TABLE_NAME, MigDataBaseDesc.class);    
  }
}
