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
package com.foc.admin;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class GrpWebModuleRightsDesc extends FocDesc{

  public static final int FLD_MODULE_NAME  = 1;
  public static final int FLD_ACCESS_RIGHT = 2;
  public static final int FLD_GROUP        = 3;
  public static final int FLD_IS_ADMIN     = 4;
  public static final int FLD_MODULE_TITLE = 5;
  
  public static final int ACCESS_NONE                   = 0;
  public static final int ACCESS_FULL                   = 1;
  public static final int ACCESS_FULL_WITH_CONFIGURTION = 2;

  public static final String DB_TABLE_NAME = "GROUP_WEB_MODULE";
  
  public GrpWebModuleRightsDesc() {
    super(GrpWebModuleRights.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
    
    FField fField = addReferenceField();
    
    fField = new FStringField("MODULE_NAME", "Module Name", FLD_MODULE_NAME, false, 50);
    addField(fField);

    fField = new FStringField("MODULE_TITLE", "Module Title", FLD_MODULE_TITLE, false, 100);
    fField.setDBResident(false);
    addField(fField);

    FMultipleChoiceField bFld = new FMultipleChoiceField("ACCESS", "Access", FLD_ACCESS_RIGHT, false, 3);
    bFld.addChoice(ACCESS_NONE, "-");
    bFld.addChoice(ACCESS_FULL, "Granted");
    bFld.addChoice(ACCESS_FULL_WITH_CONFIGURTION, "Granted + Config");
    addField(bFld);
    
    FObjectField objectField = new FObjectField("GROUP", "Group", FLD_GROUP, FocGroupDesc.getInstance(), this, FocGroupDesc.FLD_WEB_MODULE_RIGHTS_LIST);
    objectField.setDisplayField(FocGroupDesc.FLD_NAME);
    objectField.setComboBoxCellEditor(FocGroupDesc.FLD_NAME);
    objectField.setSelectionList(FocGroup.getList(FocList.NONE));
    addField(objectField);
    
    FBoolField blFld = new FBoolField("IS_ADMIN", "Admin", FLD_IS_ADMIN, false);
    blFld.setDBResident(false);
    addField(blFld);
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
    FocList list = newFocList();
    list.setDirectlyEditable(true);
    list.setDirectImpactOnDatabase(false);
    list.setListOrder(new FocListOrder(FLD_MODULE_NAME));
    return list;
  }
  
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static FocDesc getInstance() {
  	return getInstance(DB_TABLE_NAME, GrpWebModuleRightsDesc.class);

  }
}
