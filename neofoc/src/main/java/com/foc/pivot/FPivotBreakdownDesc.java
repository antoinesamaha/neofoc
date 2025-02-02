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
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FDateField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FObjectField;

public class FPivotBreakdownDesc extends FocDesc implements FPivotConst {
  
  public static final String DB_TABLE_NAME = "PIVOT_BREAKDOWN";
  
	public FPivotBreakdownDesc() {
		super(FPivotBreakdown.class, NOT_DB_RESIDENT, DB_TABLE_NAME, true);
	
		addReferenceField();

		addNameField();
		addOrderField();
		
		FStringField cfld = new FStringField("GROUP_BY", "Group by", FLD_BKDN_GROUP_BY, false, 200);
		addField(cfld);
		
		cfld = new FStringField("SORT_BY", "Sort by", FLD_BKDN_SORT_BY, false, 200);
		addField(cfld);
		
		cfld            = new FStringField("CAPTION_PROPERTY", "Caption Property", FLD_BKDN_TITLE_CAPTION, false, 200);
		addField(cfld);
		
		cfld            = new FStringField("DESCRIPTION_PROPERTY", "Description Property", FLD_BKDN_DESCRIPTION_CAPTION, false, 200);
		addField(cfld);
		
		FBoolField bfld = new FBoolField("SHOW_ENTIRE_TREE", "Show Entire Tree", FLD_BKDN_SHOW_ENTIRE_TREE, false);
		addField(bfld);
		
		FDateField dfld = new FDateField("START_DATE", "Start Date", FLD_BKDN_START_DATE, false);
		addField(dfld);
		
		dfld = new FDateField("END_DATE", "End Date", FLD_BKDN_END_DATE, false);
		addField(dfld);
		
		dfld = new FDateField("CUT_OFF_DATE", "Cut Off Date", FLD_BKDN_CUT_OFF_DATE, false);
		addField(dfld);
		
		FObjectField objFld = new FObjectField("PIVOT_VIEW", "Pivot View", FLD_BKDN_VIEW, FPivotViewDesc.getInstance(), this, FLD_VIEW_BKDN_LIST);
    addField(objFld);
    
    FBoolField bFld = new FBoolField("HIDE_WHEN_ONLY_CHILD", "Hide when only child", FLD_BKDN_HIDE_WHEN_ALONE, false);
    addField(bFld);

    FMultipleChoiceField mFld = new FMultipleChoiceField(FNAME_GROUPING, "Type", FLD_DATE_GROUPING, false, 2);
		mFld.addChoice(DATE_GROUPING_NONE   , DATE_GROUPING_CHOICE_NONE);
		mFld.addChoice(DATE_GROUPING_MONTHLY, DATE_GROUPING_CHOICE_MONTHLY);
		mFld.addChoice(DATE_GROUPING_YEARLY , DATE_GROUPING_CHOICE_YEARLY);
		addField(mFld);
		
		cfld            = new FStringField("TITLE_WHEN_EMPTY", "Title When Empty", FLD_TITLE_WHEN_EMPTY, false, 70);
		addField(cfld);

		cfld            = new FStringField("DESCRIPTION_WHEN_EMPTY", "Description When Empty", FLD_DESCRIPTION_WHEN_EMPTY, false, 70);
		addField(cfld);
		
		bfld = new FBoolField("WRAPE_NATIVE_OBJECT", "Wrape Native Object", FLD_WRAPE_NATIVE_OBJECT, false);
		addField(bfld);
	}
	
	 public static FocDesc getInstance(){
	    return getInstance(DB_TABLE_NAME, FPivotBreakdownDesc.class);
	  }
}
