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
package com.foc.business.printing;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FBlobMediumField;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.list.FocList;

public class PrnLayoutDefinitionDesc extends FocDesc {
	public static final int FLD_NAME             = FField.FLD_NAME;
	public static final int FLD_DESCRIPTION      = FField.FLD_DESCRIPTION;
	public static final int FLD_PRN_LAYOUT       = 1;	
	public static final int FLD_PRN_CONTEXT      = 2;
	public static final int FLD_PRN_FILE_NAME    = 3;
	public static final int FLD_PRN_JASPER_FILE  = 4;
	
	public static final String DB_TABLE_NAME = "PRN_REPORT_DEFINITION";
  
	public PrnLayoutDefinitionDesc(){
		super(PrnLayoutDefinition.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, true);
		addReferenceField();
		
		addNameField();
		addDescriptionField();
		
//		FMultipleChoiceFieldStringBased mFld = new FMultipleChoiceFieldStringBased("CONTEXT", "Context", FLD_PRN_CONTEXT, false, PrnContextDesc.LEN_CONTEXT_DB_NAME);
//		FocList contextList = ReportFactory.getInstance().getContextList();
//		for(int i=0; i<contextList.size(); i++){
//			PrnContext context = (PrnContext) contextList.getFocObject(i);
//			mFld.addChoice(context.getDBName());
//		}
//    addField(mFld);
    
		FStringField mFld = new FStringField("CONTEXT", "Context", FLD_PRN_CONTEXT, false, PrnContextDesc.LEN_CONTEXT_DB_NAME);
	  addField(mFld);

    FStringField cFld = new FStringField("LAYOUT_FILE", "File", FLD_PRN_FILE_NAME, false, PrnLayoutDesc.LEN_LAYOUT_FILE_NAME);
		addField(cFld);
		
    FBlobMediumField blobField = new FBlobMediumField("JASPER_FILE", "Jasper File", FLD_PRN_JASPER_FILE, false);
    addField(blobField);
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
    return list;
  }
  
  @Override
  public void afterLogin() {
  	super.afterLogin();
  	
  }
  
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

	public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, PrnLayoutDefinitionDesc.class);    
  }
}
