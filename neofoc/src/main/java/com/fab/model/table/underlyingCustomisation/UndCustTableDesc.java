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
package com.fab.model.table.underlyingCustomisation;

import com.fab.model.table.TableDefinitionDesc;
import com.foc.desc.FocDesc;
import com.foc.list.FocList;

public class UndCustTableDesc extends TableDefinitionDesc {
	
	public static final String DB_TABLE_NAME = "UNDERLYING_CUST_TABLE";
	
	public UndCustTableDesc(){
		super(UndCustTable.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
	}
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
	
//	private static FocList list = null;
//	public static FocList getList(int mode){
//	  list = getInstance().getList(list, mode);
//    list.setDirectlyEditable(false);
//    list.setDirectImpactOnDatabase(true);
//		return list;		
//	}

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
	
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static UndCustTableDesc getInstance() {
    return (UndCustTableDesc) getInstance(DB_TABLE_NAME, UndCustTableDesc.class);    
  }
}
