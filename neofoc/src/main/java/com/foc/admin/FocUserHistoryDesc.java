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
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FIntField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;

public class FocUserHistoryDesc extends FocDesc implements FocUserHistoryConst {

  public static final String DB_TABLE_NAME = "FUSER_HISTORY";
  
  public FocUserHistoryDesc(){
    super(FocUserHistory.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
    
    addReferenceField();
    
    FObjectField oFld = new FObjectField(FocUserDesc.DB_TABLE_NAME, "User", FLD_USER, FocUserDesc.getInstance(), this, FocUserDesc.FLD_HISTORY_LIST);
    oFld.setSelectionList(FocUserDesc.getList(FocList.NONE));
    addField(oFld);
       
    FField ffld = new FStringField("HISTORY", "History", FLD_HISTORY, false, 2000);
    addField(ffld);
    
    ffld = new FStringField("RECENT_TRANSACTIONS", "Recent Transactions Visited", FLD_RECENT_TRANSACTIONS, false, 2000);
    addField(ffld);
    
    FIntField ifld = new FIntField("FULLSCREEN", "Fullscreen", FLD_FULLSCREEN, false, 4);
    addField(ifld);
  }
  
  @Override
  public FocList newFocList() {
    FocUserHistoryList list = new FocUserHistoryList();
    list.setDirectImpactOnDatabase(true);
    list.setDirectlyEditable(false);
    return list;
  }
  
  @Override
  public FocList getFocList(){
  	FocList list = super.getFocList();
  	if(list != null) list.loadIfNotLoadedFromDB();
    return list;
  }
  
  public static FocDesc getInstance(){
    return getInstance(DB_TABLE_NAME, FocUserHistoryDesc.class);
  }
}
