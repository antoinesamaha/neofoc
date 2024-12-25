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

import com.fab.model.table.TableDefinition;
import com.foc.Globals;
import com.foc.desc.FocConstructor;
import com.foc.list.FocList;

public class UndCustTable extends TableDefinition{
	public UndCustTable(FocConstructor constr){
		super(constr);
		newFocProperties();
		setPropertyBoolean(UndCustTableDesc.FLD_DB_RESIDENT, true );
		setPropertyBoolean(UndCustTableDesc.FLD_KEY_UNIQUE , false);
		setPropertyBoolean(UndCustTableDesc.FLD_WITH_REF   , false);
	}
	
	@Override
	public boolean isAlreadyExisting() {
		return true;
	}
	
	private static UndCustTable undCustomFields = null;
	public static UndCustTable getInstance(){
		if(undCustomFields == null){
			if(!Globals.getApp().isEmptyDatabaseJustCreated()){//20151216-Without this condition we get error messages when the SaaS Empty database is entered the first time.
				FocList list = UndCustTableDesc.getList(FocList.LOAD_IF_NEEDED);
				if(list != null){
					undCustomFields = (UndCustTable) list.getAnyItem();
					if(undCustomFields == null){
						undCustomFields = (UndCustTable) list.newEmptyItem();
						undCustomFields.setName("UNDERLYING_USER_FIELDS");
						list.add(undCustomFields);
						if(list.size() == 1){
							list.validate(true);
						}
					}
				}
			}
		}
		return undCustomFields;
	}
}
