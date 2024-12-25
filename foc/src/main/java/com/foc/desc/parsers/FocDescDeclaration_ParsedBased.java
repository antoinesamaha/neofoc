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
package com.foc.desc.parsers;

import com.foc.IFocDescDeclaration;
import com.foc.desc.FocModule;

public abstract class FocDescDeclaration_ParsedBased implements IFocDescDeclaration {

	private FocModule            module      = null;
	private String               name        = null;
	private String               storageName = null;
	
	public FocDescDeclaration_ParsedBased(FocModule module, String name, String storageName){
		this.name        = name;
		this.module      = module;
		this.storageName = storageName;
	}
	
	@Override
	public FocModule getFocModule() {
		return module;
	}

	public String getName() {
		return name;
	}

	public String getStorageName() {
		return storageName;
	}
	
	public void setStorageName(String storageName) {
		this.storageName = storageName;
	}

	@Override
	public int getPriority() {
		int priority = IFocDescDeclaration.PRIORITY_FIRST;
		ParsedFocDesc focDesc = (ParsedFocDesc) getFocDescription();
		if(focDesc != null) {
			if(focDesc.getParsedFilter() != null) {
				priority = IFocDescDeclaration.PRIORITY_THIRD;
			} else if(focDesc.hasJoinNode()) {
				priority = IFocDescDeclaration.PRIORITY_SECOND;
			}
		}
		return priority;
	}
}
