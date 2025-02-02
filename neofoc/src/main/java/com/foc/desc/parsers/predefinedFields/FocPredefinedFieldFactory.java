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
package com.foc.desc.parsers.predefinedFields;

import java.util.HashMap;

public class FocPredefinedFieldFactory {
	
	private HashMap<String, IFocPredefinedFieldType> map = null;
	
	private FocPredefinedFieldFactory(){
		map = new HashMap<String, IFocPredefinedFieldType>();
		
		put(new FTypeCODE());
		put(new FTypeNAME());
		put(new FTypeEXTERNAL_CODE());
		put(new FTypeDESCRIPTION());
		put(new FTypeCOMPANY());
		put(new FTypeSITE());
		put(new FTypeDATE());
		put(new FTypeORDER());
		put(new FTypeNOT_COMPLETED_YET());
		put(new FTypeDEPRECATED());
		put(new FTypeIS_SYSTEM());
	}

	public void put(IFocPredefinedFieldType type) {
		map.put(type.getTypeName(), type);
	}
	
	public IFocPredefinedFieldType get(String typeName) {
		return map != null ? map.get(typeName) : null;
	}

	private static FocPredefinedFieldFactory instance = null;
	public static FocPredefinedFieldFactory getInstance(){
		if(instance == null){
			instance = new FocPredefinedFieldFactory();
		}
		return instance;
	};
	
}
