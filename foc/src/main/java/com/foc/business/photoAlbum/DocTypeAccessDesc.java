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
package com.foc.business.photoAlbum;

import com.foc.admin.DocRightsGroupDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;

public class DocTypeAccessDesc extends FocDesc{

	public static final int FLD_DOCUMENT_TYPE    = 1;
  public static final int FLD_ACCESS_RIGHT     = 2;
  public static final int FLD_DOC_RIGHTS_GROUP = 3;
  
  public static final int READ_ONLY    = 0;
  public static final int READ_WRITE   = 1;
  
	public static final String DB_TABLE_NAME = "DOC_TYPE_ACCESS";
	
	public DocTypeAccessDesc() {
		super(DocTypeAccess.class, DB_RESIDENT, DB_TABLE_NAME, false);
		
		addReferenceField();

    FObjectField objFld = new FObjectField("DOCUMENT_TYPE", "Document Type", FLD_DOCUMENT_TYPE, DocumentTypeDesc.getInstance(), this, DocumentTypeDesc.FLD_DOCUMENT_TYPE_ACCESS_LIST);
    objFld.setWithList(false);
    addField(objFld);
    
    objFld = new FObjectField("DOC_RIGHTS_GROUP", "Doc Rights Group", FLD_DOC_RIGHTS_GROUP, DocRightsGroupDesc.getInstance());
    addField(objFld);
    
    FMultipleChoiceField fMultipleField = new FMultipleChoiceField("ACCESS_RIGHT", "Access Right", FLD_ACCESS_RIGHT, false, 2);
    fMultipleField.addChoice(READ_ONLY , "Read Only");
    fMultipleField.addChoice(READ_WRITE, "Read Write");
    addField(fMultipleField);
	}
	
	public FocList newFocList(){
	  FocList list = super.newFocList();
	  list.setDirectlyEditable(false);
	  list.setDirectImpactOnDatabase(true);
	  return list;
	}
  
  public static DocTypeAccessDesc getInstance(){
    return (DocTypeAccessDesc) getInstance(DB_TABLE_NAME, DocTypeAccessDesc.class);
  }

}
