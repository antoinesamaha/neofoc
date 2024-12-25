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
package com.foc.business.workflow;

import com.foc.business.workflow.map.WFStageDesc;
import com.foc.business.workflow.map.WFTransactionConfigDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FMultipleChoiceStringField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;

public class WFFieldLockStageDesc extends FocDesc {

	public static final int FLD_WF_TRANSACTION_CONFIG = 1;
	public static final int FLD_FIELD_NAME            = 2;
	public static final int FLD_WF_LOCK_STAGE         = 3;
	
	public static final String DB_TABLE_NAME = "WF_FIELD_LOCK_STAGE";
	
	public WFFieldLockStageDesc(){
		super(WFFieldLockStage.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
		
		addReferenceField();
		
		FObjectField objFld = new FObjectField("WF_TRANSACTION_CONFIG", "Transaction Config", FLD_WF_TRANSACTION_CONFIG, WFTransactionConfigDesc.getInstance(), this, WFTransactionConfigDesc.FLD_WF_FIELD_LOCK_STAGE_LIST);
		addField(objFld);
		
		FMultipleChoiceStringField charField = new FMultipleChoiceStringField("FIELD_NAME", "Field Name", FLD_FIELD_NAME, false, 250);
		charField.setAllowOutofListSelection(true);
		addField(charField);
		
		objFld = new FObjectField("WF_LOCK_STAGE", "WF Lock Stage", FLD_WF_LOCK_STAGE, WFStageDesc.getInstance());
		addField(objFld);
	}
	
	public static FocList getList(int mode){
    return getInstance().getFocList(mode);
  }
  
  @Override
  public FocList newFocList(){
    FocList list = super.newFocList();
    list.setDirectlyEditable(true);
    list.setDirectImpactOnDatabase(false);
    return list;
  }
	
	public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, WFFieldLockStageDesc.class);    
  }
}
