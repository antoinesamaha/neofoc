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
package b01.officeLink.excel.synchronize;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FIntField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FObjectField;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

public class ExcelColumnDesc extends FocDesc {
	public static final int FLD_POSITION             = 1;
  public static final int FLD_AUTOCAD_COL          = 2;
  public static final int FLD_FIELD_MODE           = 3;
  public static final int FLD_C3_COL               = 4;
  public static final int FLD_MANDATORY            = 5;
  public static final int FLD_CONFIG               = 10;
  
  public static final int MODE_EXCEL_C3            = 0;
  public static final int MODE_C3_EXCEL            = 1;
  public static final int MODE_BOTH                = 2;
  public static final int MODE_LEVEL               = 3;
  public static final int MODE_REF                 = 4;
  
  public ExcelColumnDesc(){
    super(ExcelColumn.class, FocDesc.DB_RESIDENT, "XL_SYNC_COLUMN", true);
    setGuiBrowsePanelClass(ExcelColumnGuiBrowsePanel.class);
    
    FField focFld = addReferenceField();

    focFld = new FIntField("POSITION", "", FLD_POSITION, false, 2);    
    addField(focFld);

    focFld = new FStringField("XL_COL", "From", FLD_AUTOCAD_COL, true, 30);    
    addField(focFld);
    
    focFld = new FStringField("FOC_COL", "To", FLD_C3_COL,  true, 250);
    addField(focFld);
    
    focFld = new FBoolField("MANDATORY", "Mandatory", FLD_MANDATORY, false);
    addField(focFld);
    
    FMultipleChoiceField mFld = new FMultipleChoiceField("FIELD_MODE", "Mode", FLD_FIELD_MODE, false, 2);
    mFld.setSortItems(false);
    mFld.addChoice(MODE_EXCEL_C3, "EXCEL->BARMAJA");
    mFld.addChoice(MODE_C3_EXCEL, "BARMAJA->EXCEL");
    mFld.addChoice(MODE_BOTH, "BARMAJA<->EXCEL");
    mFld.addChoice(MODE_LEVEL, "Level");
    mFld.addChoice(MODE_REF, "Reference");
    addField(mFld);
    
    mFld.addListener(new FPropertyListener() {
			@Override
			public void propertyModified(FProperty property) {
				ExcelColumn importMap = (ExcelColumn) property.getFocObject();
				if(importMap != null){
					importMap.adjustPropertyLocks();
				}
			}
			
			@Override
			public void dispose() {
			}
		});
    
    FObjectField objFld = new FObjectField("CONFIG", "Configuration", FLD_CONFIG, true, ExcelSyncDesc.getInstance(), "CONFIG_", this, ExcelSyncDesc.FLD_FIELDS_MAP_LIST);
    addField(objFld);
  }
  
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;
  
  public static FocDesc getInstance() {
    if(focDesc == null){
      focDesc = new ExcelColumnDesc();
    }
    return focDesc;
  }
  
}
