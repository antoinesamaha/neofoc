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
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FObjectField;

public class FPivotValueDesc extends FocDesc implements FPivotConst {
  
  public static final String DB_TABLE_NAME = "PIVOT_VALUE";
  
	public FPivotValueDesc() {
		super(FPivotValue.class, NOT_DB_RESIDENT, DB_TABLE_NAME, true);
	
		addOrderField();

		FStringField cfld = new FStringField("COLUMN_TITLE", "Title", FLD_VALUE_TITLE, false, 50);
		addField(cfld);
		
		cfld = new FStringField("DATAPATH", "Datapath", FLD_VALUE_DATAPATH, false, 50);
		addField(cfld);
		
		cfld = new FStringField("COMPUTE_LEVEL", "Compute Level", FLD_VALUE_COMPUTE_LEVEL, false, 50);
		addField(cfld);

		cfld = new FStringField("FORMULA", "Formula", FLD_VALUE_FORMULA, false, 250);
		addField(cfld);

		FMultipleChoiceField mfld = new FMultipleChoiceField("AGGREGATION_FORMULA", "Formula", FLD_VALUE_AGGREGATION_FORMULA, false, 50);
		mfld.addChoice(FORMULA_SUM, "SUM");//DO NOT CHANGE VALUES THEY ARE COMPARED TO WHAT IS IN XML
		mfld.addChoice(FORMULA_MAX, "MAX");//DO NOT CHANGE VALUES THEY ARE COMPARED TO WHAT IS IN XML
		mfld.addChoice(FORMULA_MIN, "MIN");//DO NOT CHANGE VALUES THEY ARE COMPARED TO WHAT IS IN XML
		mfld.addChoice(FORMULA_AVG, "AVG");//DO NOT CHANGE VALUES THEY ARE COMPARED TO WHAT IS IN XML
		addField(mfld);
		
		FObjectField objFld = new FObjectField("PIVOT_VIEW", "Pivot View", FLD_VALUE_VIEW, FPivotViewDesc.getInstance(), this, FLD_VIEW_VALUE_LIST);
    addField(objFld);
	}
	
	 public static FocDesc getInstance(){
	    return getInstance(DB_TABLE_NAME, FPivotValueDesc.class);
	  }
}
