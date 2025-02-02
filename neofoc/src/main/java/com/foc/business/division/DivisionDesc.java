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
package com.foc.business.division;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FCompanyField;
import com.foc.desc.field.FField;
import com.foc.list.FocList;

public class DivisionDesc extends FocDesc{

	public static final int FLD_NAME           = FField.FLD_NAME;
  public static final int FLD_DESCRIPTION    = FField.FLD_DESCRIPTION;
  public static final int FLD_END_DIVISION = 1;
  
  public static final String DB_NAME = "DIVISION";
  
  public static final String FNAME_END_DIVISION = "END_DIVISION";
  
  public DivisionDesc() {
    super(Division.class, FocDesc.DB_RESIDENT, DB_NAME, true);

    addReferenceField();
    setWithObjectTree();
    
    addNameField();
    addDescriptionField();
    
    FCompanyField compField = new FCompanyField(true, true);
    addField(compField);
    
    FBoolField bFld = new FBoolField(FNAME_END_DIVISION, "End Division", FLD_END_DIVISION, false);
    addField(bFld);
  }
  
  public static FocList getList(int mode) {
  	return getInstance().getFocList(mode);
  }
  
  public FocList newFocList(){
  	return new DivisionList(false);
  }

  public static DivisionDesc getInstance() {
  	return (DivisionDesc) getInstance(DB_NAME, DivisionDesc.class);
  }
}
