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
package com.foc.desc.dataModelTree;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;

public class DataModelNodeDesc extends FocDesc {
  public static final int FLD_PATH_SECTION = 1;
  public static final int FLD_DESCRIPTION  = 3;
  public static final int FLD_VALUE        = 4;
  public static final int FLD_SELECT       = 5;
  
  public DataModelNodeDesc() {
    super(DataModelNode.class, FocDesc.NOT_DB_RESIDENT, "DM_NODE", false);
    FField focFld = addReferenceField();
    
    addNameField();
    
    focFld = new FStringField("PATH_SECTION", "Path section", FLD_PATH_SECTION, false, 50);
    addField(focFld);
    
    focFld = new FStringField("DESCRIPTION", "Description", FLD_DESCRIPTION, false, 50);
    addField(focFld);

    focFld = new FStringField("VALUE", "Value", FLD_VALUE, false, 30);
    addField(focFld);

    focFld = new FBoolField("SELECT", "Select", FLD_SELECT, false);
    addField(focFld);
    
    setWithObjectTree();
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  private static DataModelNodeDesc focDesc = null;
  public static DataModelNodeDesc getInstance() {
    if (focDesc==null){
      focDesc = new DataModelNodeDesc();
    }
    return focDesc;
  }
}
