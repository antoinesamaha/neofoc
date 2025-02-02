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
package com.foc.gui.table.view;

import java.awt.Color;
import java.awt.Font;

import com.foc.admin.FocUserDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FColorField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FIntField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FObjectField;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

public class ColumnsConfigDesc extends FocDesc{

	public static final int FLD_VIEW_CONFIG          =  1;
  public static final int FLD_COLUMN_ID            =  2;
  public static final int FLD_SHOW                 =  3;
  public static final int FLD_COLUMN_TITLE         =  4;
  public static final int FLD_BACKGROUND           =  5;
  public static final int FLD_FOREGROUND           =  6;
  public static final int FLD_FONT_SIZE            =  7;
  public static final int FLD_FONT_STYLE           =  8;
  public static final int FLD_DECIMALS             =  9;
  public static final int FLD_GROUPING_USED        = 10;
  
  public static final int FLD_COLUMN_DEFAULT_TITLE = 100;
  public static final int FLD_COLUMN_EXPLANATION   = 101;
  public static final int FLD_COLUMN_ORDER         = 102;
  
  public static final int LEN_TITLE                = 40;
  
	public ColumnsConfigDesc(){
    super(ColumnsConfig.class, DB_RESIDENT, "VIEW_COLUMNS", true);
    FField focFld = addReferenceField();

    //addOrderField();
    
    focFld = new FObjectField("VIEW_CONFIG", "View Config", FLD_VIEW_CONFIG, true, ViewConfigDesc.getInstance(), "VIEW_", this, ViewConfigDesc.FLD_COLUMN_ID_LIST);
    addField(focFld);
    
    focFld = new FIntField("COL_ID", "Column id", FLD_COLUMN_ID, true, 9);
    addField(focFld);

    focFld = new FBoolField("SHOW_COL", "Show", FLD_SHOW, false);
    addField(focFld);

    focFld = new FStringField("COLUMN_DEFAULT_TITLE", "Default Title", FLD_COLUMN_DEFAULT_TITLE, false, LEN_TITLE);
    focFld.setDBResident(false);
    addField(focFld);

    focFld = new FStringField("COLUMN_TITLE", "Title", FLD_COLUMN_TITLE, false, LEN_TITLE);
    addField(focFld);

    focFld = new FStringField("COLUMN_EXPLANATION", "Explanation", FLD_COLUMN_EXPLANATION, false, 100);
    focFld.setDBResident(false);
    addField(focFld);

    focFld = new FIntField("COL_ORDER", "Order", FLD_COLUMN_ORDER, false, 9);
    focFld.setDBResident(true);
    addField(focFld);
    
    focFld = new FColorField("BACKGROUND", "Bkg", FLD_BACKGROUND, false, Color.WHITE);
    addField(focFld);
    
    focFld = new FColorField("FOREGROUND", "Frg", FLD_FOREGROUND, false, Color.WHITE);
    addField(focFld);
    
    FPropertyListener pListener = new FPropertyListener(){
			@Override
			public void dispose() {
			}

			@Override
			public void propertyModified(FProperty property) {
				ColumnsConfig config = (ColumnsConfig) property.getFocObject();
				if(config != null) config.resetFont();
			}
    };
    
    focFld = FocUserDesc.addFontSizeField(this, "FONT_SIZE", FLD_FONT_SIZE);
    focFld.addListener(pListener);
    
    FMultipleChoiceField mFld = new FMultipleChoiceField("FONT_STYLE", "Font style", FLD_FONT_STYLE, false, 2);
    mFld.addChoice(Font.BOLD  , "Bold");
    mFld.addChoice(Font.ITALIC, "Italic");
    mFld.addChoice(Font.PLAIN , "");
    mFld.addListener(pListener);
    addField(mFld);
    
    mFld = new FMultipleChoiceField("NBR_DECIMALS", "Dec", FLD_DECIMALS, false, 2);
    mFld.addChoice(-1, "-");
    mFld.addChoice(0 , "0");
    mFld.addChoice(1 , "1");
    mFld.addChoice(2 , "2");
    mFld.addChoice(3 , "3");
    mFld.addChoice(4 , "4");
    mFld.addChoice(5 , "5");
    mFld.addChoice(6 , "6");
    mFld.addChoice(7 , "7");
    mFld.addChoice(8 , "8");
    mFld.addChoice(9 , "9");
    mFld.addChoice(10, "10");
    addField(mFld);
    
    FBoolField bFld = new FBoolField("GROUPING_USED", "#,###", FLD_GROUPING_USED, false);
    addField(bFld);
  }

  private static ColumnsConfigDesc focDesc = null;
  public static FocDesc getInstance() {
    if(focDesc == null){
      focDesc = new ColumnsConfigDesc();
    }
    return focDesc;
  }
}
