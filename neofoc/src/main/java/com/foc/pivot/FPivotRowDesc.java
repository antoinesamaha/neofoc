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
package com.foc.pivot;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FObjectField;

public class FPivotRowDesc extends FocDesc implements FPivotConst {
  
  public static final String DB_TABLE_NAME     = "PIVOT_TABLE";
  public static final String FNAME_TITLE       = "TITLE";
  public static final String FNAME_DESCRIPTION = FField.FNAME_DESCRIPTION;
  public static final String FNAME_NATIVE      = "NATIVE";
  
  /**
   * Constructor that creates a FPivotRowDesc with a specific number of breakdown levels.
   * @param desc
   * @param numberOfBreakdownLevels The number of breakdown levels.
   */
  public FPivotRowDesc(FocDesc desc, int numberOfBreakdownLevels) {
    super(FPivotRow.class, NOT_DB_RESIDENT, DB_TABLE_NAME, false);
    
    init(desc);
  }

  /**
   * Constructor that creates a FPivotRowDesc with FLD_PVT_ROW_BKDN_SIZE number of breakdowns.
   * @param desc
   */
  public FPivotRowDesc(FocDesc desc) {
    super(FPivotRow.class, NOT_DB_RESIDENT, "FPIVOT", false);

    init(desc);
  }

  public void init(FocDesc desc) {
      addReferenceField();
      
      FStringField cfld = new FStringField(FNAME_TITLE, "Title", FLD_PVT_ROW_TITLE, false, 500);
      addField(cfld);

      cfld = new FStringField("FULL_TITLE", "Full Title", FLD_PVT_ROW_FULL_TITLE, false, 500);
      addField(cfld);

      addDescriptionField();
      
      cfld = new FStringField("GROUP_BY", "Group by", FLD_PVT_ROW_GROUP_BY, false, 500);
      addField(cfld);

      cfld = new FStringField("SORT_BY", "Sort by", FLD_PVT_ROW_SORT_BY, false, 500);
      addField(cfld);

      FObjectField objectField = new FObjectField(FNAME_NATIVE, "Object" + desc.getStorageName(), FLD_PVT_ROW_OBJECT, desc);
      objectField.setWithList(false);
      addField(objectField);

      objectField = new FObjectField("START_BKDN", "Start Breakdown", FLD_PVT_ROW_START_BKDN, FPivotBreakdownDesc.getInstance());
      addField(objectField);
      
      objectField = new FObjectField("END_BKDN", "End Breakdown", FLD_PVT_ROW_END_BKDN, FPivotBreakdownDesc.getInstance());
      addField(objectField);

      setWithObjectTree();
  }
  
  
  public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, FPivotRowDesc.class);
  }


}
