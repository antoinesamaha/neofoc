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

import com.foc.desc.field.FField;

public interface FPivotConst {
  public final static int FLD_VALUE_TITLE               = 1;
  public final static int FLD_VALUE_DATAPATH            = 2;
  public final static int FLD_VALUE_VIEW                = 3;
  public final static int FLD_VALUE_COMPUTE_LEVEL       = 4;
  public final static int FLD_VALUE_AGGREGATION_FORMULA = 5;
  public final static int FLD_VALUE_FORMULA             = 6;
  
  public final static int FLD_BKDN_GROUP_BY             = 1;
  public final static int FLD_BKDN_SORT_BY              = 2;
  public final static int FLD_BKDN_TITLE_CAPTION        = 3;
  public final static int FLD_BKDN_DESCRIPTION_CAPTION  = 4;
  public final static int FLD_BKDN_SHOW_ENTIRE_TREE     = 5;
  public final static int FLD_BKDN_VIEW                 = 6;
  public final static int FLD_BKDN_START_DATE           = 7;
  public final static int FLD_BKDN_END_DATE             = 8;
  public final static int FLD_BKDN_HIDE_WHEN_ALONE      = 9;
  public final static int FLD_DATE_GROUPING             = 10;
  public final static int FLD_TITLE_WHEN_EMPTY          = 11;
  public final static int FLD_DESCRIPTION_WHEN_EMPTY    = 12;
  public final static int FLD_WRAPE_NATIVE_OBJECT       = 13;
  public final static int FLD_BKDN_CUT_OFF_DATE         = 14;
  
  public static final String FNAME_GROUPING             = "GROUPING";
  public static final int DATE_GROUPING_NONE            = 0;
	public static final int DATE_GROUPING_MONTHLY         = 1;
	public static final int DATE_GROUPING_YEARLY          = 2;
	
	public static final String DATE_GROUPING_CHOICE_NONE    = "None"   ;
	public static final String DATE_GROUPING_CHOICE_MONTHLY = "Monthly";
	public static final String DATE_GROUPING_CHOICE_YEARLY  = "Yearly" ;

  public final static int FLD_PVT_ROW_GROUP_BY          = 1;
  public final static int FLD_PVT_ROW_SORT_BY           = 2;
  public final static int FLD_PVT_ROW_TITLE             = 3;
  public final static int FLD_PVT_ROW_DESCRIPTION       = FField.FLD_DESCRIPTION;
  public final static int FLD_PVT_ROW_START_BKDN        = 4;
  public final static int FLD_PVT_ROW_END_BKDN          = 5;
  public final static int FLD_PVT_ROW_FULL_TITLE        = 6;
  public final static int FLD_PVT_ROW_OBJECT            = 100;
  
  public final static int FLD_VIEW_NAME                 = FField.FLD_NAME;
  public final static int FLD_VIEW_BKDN_LIST            = 10;
  public final static int FLD_VIEW_VALUE_LIST           = 11;
  
  public final static int FORMULA_SUM = 0;
  public final static int FORMULA_MAX = 1;
  public final static int FORMULA_MIN = 2;
  public final static int FORMULA_AVG = 3;
}
