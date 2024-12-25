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
package com.fab.model.filter;

import com.fab.FilterFocDescDeclaration;
import com.foc.Globals;
import com.foc.list.filter.FilterDesc;
import com.foc.list.filter.FocDescForFilter;

public class FocDescForUserDefinedFilter extends FocDescForFilter {
	
	public FocDescForUserDefinedFilter(Class focObjectClass, boolean dbResident, String storageName, boolean isKeyUnique){
		super(focObjectClass, dbResident, storageName, isKeyUnique);
	}

	@Override
	public FilterDesc getFilterDesc() {
		if(this.filterDesc == null){
			FilterFocDescDeclaration filterFocDescDeclaration = (FilterFocDescDeclaration)Globals.getApp().getIFocDescDeclarationByName(getStorageName());
			if(filterFocDescDeclaration != null){
				this.filterDesc = filterFocDescDeclaration.getFilterDesc_CreateIfNeeded();
			}
		}
		return this.filterDesc;
	}

}
