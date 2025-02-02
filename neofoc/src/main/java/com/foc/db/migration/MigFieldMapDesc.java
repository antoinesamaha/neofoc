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

import com.fab.model.table.FieldDefinitionDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FIntField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FMultipleChoiceStringField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

public class MigFieldMapDesc extends FocDesc {

	public static final int FLD_DB_FIELD_ID                     = 1;
  public static final int FLD_DB_FIELD_NAME                   = 2;
  public static final int FLD_DB_FIELD_TITLE                  = 3;
  public static final int FLD_DB_FIELD_EXPLANATION            = 4;
  public static final int FLD_DB_FIELD_TYPE                   = 5;
  public static final int FLD_DB_FOREIGN_FIELD                = 6;
  public static final int FLD_COLUMN_TITLE                    = 7;
  public static final int FLD_MANDATORY                       = 8;
  public static final int FLD_KEY_FIELD                       = 9;

  public static final int FLD_MIG_SOURCE                      = 10;
  
  public static final String DB_TABLE_NAME = "MIG_FIELD_MAP";

  public MigFieldMapDesc() {
    super(MigFieldMap.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
    setGuiBrowsePanelClass(MigFieldMapGuiBrowsePanel.class);
    setGuiDetailsPanelClass(MigFieldMapGuiDetailsPanel.class);
        
    addReferenceField();
    
    FIntField fieldID = new FIntField("DB_FIELD_ID", "Field ID", FLD_DB_FIELD_ID, false, 10);
    fieldID.setDBResident(false);
    addField(fieldID);
    
    FMultipleChoiceField mfld = FieldDefinitionDesc.newFabTypeField("DB_FIELD_TYPE", FLD_DB_FIELD_TYPE);
    addField(mfld);
    
    FMultipleChoiceStringField mFld = new FMultipleChoiceStringField ("COLUMN_TITLE", "Title", FLD_COLUMN_TITLE, false, 50);
    addField(mFld);

    FStringField cFld = new FStringField("DB_FIELD_TITLE", "Title", FLD_DB_FIELD_TITLE, false, 50);
    addField(cFld);

    cFld = new FStringField("DB_FIELD_EXPLANATION", "Explanation", FLD_DB_FIELD_EXPLANATION, false, 100);
    addField(cFld);

    cFld = new FStringField("DB_FIELD_NAME", "Name", FLD_DB_FIELD_NAME, false, 50);
    addField(cFld);
    cFld.addListener(new FPropertyListener() {
			@Override
			public void propertyModified(FProperty property) {
				MigFieldMap map = (MigFieldMap) property.getFocObject();
				if(map != null){
					map.adjustEditableFields();
				}
			}
			
			@Override
			public void dispose() {
			}
		});
    
    
    FObjectField objFld = new FObjectField("MIG_SRC", "Mig Source", FLD_MIG_SOURCE, false, MigrationSourceDesc.getInstance(), "MIG_SRC_", this, MigrationSourceDesc.FLD_FIELD_LIST);
    addField(objFld);
    
    FBoolField skipEmpty = new FBoolField("MANDATORY", "Mandatory", FLD_MANDATORY, false);
    addField(skipEmpty);

    skipEmpty = new FBoolField("IS_KEY", "Key", FLD_KEY_FIELD, false);
    addField(skipEmpty);
    
    mFld = new FMultipleChoiceStringField("DB_FOREIGN_FIELD", "Destination Formula", FLD_DB_FOREIGN_FIELD, false, 70);
    addField(mFld);
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
    return getInstance(DB_TABLE_NAME, MigFieldMapDesc.class);    
  }
}
