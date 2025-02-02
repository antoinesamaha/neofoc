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
package com.foc.menuStructure.autoGen;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FStringField;
import com.foc.list.FocList;
import com.foc.menuStructure.FocMenuItem;
import com.foc.menuStructure.FocMenuItemConst;
import com.foc.menuStructure.FocMenuItemDesc;

public class AutoGen_FocMenuItemDesc extends FocDesc implements FocMenuItemConst{
  public AutoGen_FocMenuItemDesc(){
    super(FocMenuItem.class, DB_RESIDENT, DB_TABLE_MENU_ITEM, false);
    addReferenceField();
    FStringField fldCODE = new FStringField(FNAME_CODE, "Code", FLD_CODE, false, 50);
    addField(fldCODE);

    FStringField fldTITLE = new FStringField("TITLE", "Title", FLD_TITLE, false, 150);
    addField(fldTITLE);
    
    FStringField fldHELP = new FStringField("HELP", "Help", FLD_HELP, false, 250);
    addField(fldHELP);
    
    FBoolField fldGUEST = new FBoolField("GUEST", "Guest", FLD_GUEST, false);
    addField(fldGUEST);
    
    FStringField fldEXTRA = new FStringField("EXTRA_ACTION_0", "Extra Action 0", FLD_EXTRA_ACTION_0, false, 50);
    addField(fldEXTRA);
  }

  public static FocList getList(int mode){
    return getInstance().getFocList(mode);
  }
    
  public FocList newFocList(){
    FocList list = super.newFocList();
    list.setDirectlyEditable(false);
    list.setDirectImpactOnDatabase(true);
    return list;
  }

  public static FocMenuItemDesc getInstance() {
    return (FocMenuItemDesc) getInstance(DB_TABLE_MENU_ITEM, FocMenuItemDesc.class);    
  }

}
