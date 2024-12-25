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
package com.foc.desc.parsers.pojo;

import com.foc.desc.field.FField;
import com.foc.desc.parsers.ParsedFocDesc;
import com.foc.list.filter.IFocDescForFilter;

public class PojoFocDesc extends ParsedFocDesc implements IFocDescForFilter {

	public PojoFocDesc(Class<PojoFocObject> focObjectClass, boolean dbResident, String storageName, boolean isKeyUnique){
		super(focObjectClass, dbResident, storageName, isKeyUnique, false);
	}
	
	public void dispose(){
		super.dispose();
	}
	
	@Override
	public void afterParsing() {
		if(!hasReferenceFromAJoinTable()) {
			addReferenceField();
		}
		super.afterParsing();
  	if(hasJoinNode()) {
  		setRefFieldNotDBRsident();
  	}
	}
	
	public void setRefFieldNotDBRsident() {
		FField fld = getFieldByID(FField.REF_FIELD_ID);
		if(fld != null) fld.setDBResident(false);
	}
}
