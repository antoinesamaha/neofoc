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
import com.foc.desc.field.FIntField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;

public class FabMultipleChoiceDesc extends FocDesc {
	
	public static final int FLD_INT_VALUE           = 1;
	public static final int FLD_DISPLAY_TEXT        = 2;
	public static final int FLD_MULTIPLE_CHOICE_SET = 3;
	
	public FabMultipleChoiceDesc(){
		super(FabMultipleChoice.class,FocDesc.DB_RESIDENT, "FAB_MULTIPLE_CHOICE", true);
		setGuiBrowsePanelClass(FabMultipleChoiceGuiBrowsePanel.class);
		addReferenceField();

		addOrderField();
		
		FIntField iFld = new FIntField("INT_VALUE", "Choice|Integer|Value", FLD_INT_VALUE, true, 5);
		addField(iFld);
	
		FStringField charFld = new FStringField("DISPLAY_TEXT", "Dislpay|Text", FLD_DISPLAY_TEXT, false, 70);
		addField(charFld);
		
		FObjectField oFld = new FObjectField("MULTIPLE_CHOICE_SET", "Multiple Choice Set", FLD_MULTIPLE_CHOICE_SET, true, FabMultiChoiceSetDesc.getInstance(), "MULTI_CHOICE_SET_", this, FabMultiChoiceSetDesc.FLD_CHOICE_LIST);
		oFld.setComboBoxCellEditor(FabMultiChoiceSetDesc.FLD_NAME);
		oFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		oFld.setSelectionList(FabMultiChoiceSetDesc.getList(FocList.NONE));
		addField(oFld);
	}
	
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FabMultipleChoiceDesc focDesc = null;
  
  public static FabMultipleChoiceDesc getInstance() {
    if(focDesc == null){
      focDesc = new FabMultipleChoiceDesc();
    }
    return focDesc;
  }
}
