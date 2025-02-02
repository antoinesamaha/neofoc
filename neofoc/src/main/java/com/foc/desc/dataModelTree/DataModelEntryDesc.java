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
import com.foc.desc.field.FDescFieldStringBased;
import com.foc.desc.field.FIntField;

public class DataModelEntryDesc extends FocDesc {
  public static final int FLD_FOC_DESC  = 1;
  public static final int FLD_MAX_LEVEL = 2;
  
  public DataModelEntryDesc() {
    super(DataModelEntry.class, FocDesc.NOT_DB_RESIDENT, "DM_ENTRY", false);
    
    FDescFieldStringBased descFld = new FDescFieldStringBased("OBJECT", "Initial object", FLD_FOC_DESC, false);
    descFld.fillWithAllDeclaredFocDesc();
    addField(descFld);
    
    FIntField focFld = new FIntField("MAX_LEVEL", "maximum level", FLD_MAX_LEVEL, false, 50);
    addField(focFld);
  }
  
	protected void afterConstruction(){
		FDescFieldStringBased descFld = (FDescFieldStringBased)getFieldByID(FLD_FOC_DESC);
		if(descFld != null){
			descFld.fillWithAllDeclaredFocDesc();
		}
	}
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  private static DataModelEntryDesc focDesc = null;
  public static DataModelEntryDesc getInstance() {
    if (focDesc == null){
      focDesc = new DataModelEntryDesc();
    }
    return focDesc;
  }
}
