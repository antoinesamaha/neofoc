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
package com.foc.admin;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;

public class DocRightsGroupUsersDesc extends FocDesc{

  public static final int FLD_DOC_RIGHTS_GROUPS = 1;
  public static final int FLD_USER              = 2;
  
  public static final String DB_TABLE_NAME = "DOC_RIGHTS_GROUP_USERS";
  
  public DocRightsGroupUsersDesc() {
    super(DocRightsGroupUsers.class, DB_RESIDENT, DB_TABLE_NAME, false);

    addReferenceField();

    FObjectField objFld = new FObjectField("DOC_RIGHTS_GROUPS", "Doc Rights Groups", FLD_DOC_RIGHTS_GROUPS, DocRightsGroupDesc.getInstance(), this, DocRightsGroupDesc.FLD_DOC_RIGHTS_GROUP_USERS_LIST);
    objFld.setWithList(false);
    addField(objFld);
    
    objFld = new FObjectField("USER", "User", FLD_USER, FocUser.getFocDesc());
    objFld.setDisplayField(FocUserDesc.FLD_NAME);
    addField(objFld);
  }
  
  public FocList newFocList(){
	  FocList list = super.newFocList();
	  list.setDirectlyEditable(false);
	  list.setDirectImpactOnDatabase(true);
	  return list;
	}
  
  public static DocRightsGroupUsersDesc getInstance(){
    return (DocRightsGroupUsersDesc) getInstance(DB_TABLE_NAME, DocRightsGroupUsersDesc.class);
  }
}
