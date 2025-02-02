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
package com.fab.model.table;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FObjectField;

public class FabDictionaryGroupDesc extends FocDesc {
	
	public static final int FLD_NAME             = 1;
	public static final int FLD_TABLE_DEFINITION = 2;
	
	public FabDictionaryGroupDesc(){
		super(FabDictionaryGroup.class,FocDesc.DB_RESIDENT, "FAB_DICT_GRP", false);
		setGuiBrowsePanelClass(FabDictionaryGroupGuiBrowsePanel.class);
		addReferenceField();
		
		FStringField charFld = new FStringField("NAME", "Name", FLD_NAME, false, 20);
		addField(charFld);
		charFld.setMandatory(true);
		
		FObjectField objFld = new FObjectField("TABLE_DEF", "Table", FLD_TABLE_DEFINITION, false, TableDefinitionDesc.getInstance(), "TABLE_", this, TableDefinitionDesc.FLD_DICTIONARY_GROUP_LIST);
		addField(objFld);
	}
	
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FabDictionaryGroupDesc focDesc = null;
  
  public static FabDictionaryGroupDesc getInstance() {
    if (focDesc==null){
      focDesc = new FabDictionaryGroupDesc();
    }
    return focDesc;
  }
}
