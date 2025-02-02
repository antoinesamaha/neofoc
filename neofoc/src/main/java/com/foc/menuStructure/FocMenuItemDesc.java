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
package com.foc.menuStructure;

import com.foc.desc.field.FBoolField;
import com.foc.menuStructure.autoGen.AutoGen_FocMenuItemDesc;

public class FocMenuItemDesc extends AutoGen_FocMenuItemDesc {
	
	 public static final int FLD_HAS_ACCESS              =  20;
	 
	
	public FocMenuItemDesc(){
		super();
		setWithObjectTree();
		setDbResident(NOT_DB_RESIDENT);
		
		FBoolField bField = new FBoolField("HAS_ACCESS", "Has access", FLD_HAS_ACCESS, false);
		bField.setDefaultStringValue("true");
		addField(bField);
	}
	
}
