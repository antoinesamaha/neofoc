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
package com.foc.list.filter;

import com.foc.desc.FocDesc;
import com.foc.desc.FocFieldEnum;
import com.foc.desc.field.FStringField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public abstract class FocDescForFilter extends FocDesc implements IFocDescForFilter {
	
	public abstract FilterDesc getFilterDesc();

	protected FilterDesc filterDesc = null; 

	public FocDescForFilter(Class focObjectClass, boolean dbResident, String storageName, boolean isKeyUnique){
		this(focObjectClass, dbResident, storageName, isKeyUnique, false);
	}
	
	public FocDescForFilter(Class focObjectClass, boolean dbResident, String storageName, boolean isKeyUnique, boolean addSyncFields){
		super(focObjectClass, dbResident, storageName, isKeyUnique);
		setGuiBrowsePanelClass(FocListFilterGuiBrowsePanel.class);
		setGuiDetailsPanelClass(FocListFilterGuiDetailsPanel.class);
	
		addReferenceField();
		addNameField();

    if(getFilterDesc() != null){
      getFilterDesc().fillDesc(this, 1);
    }
	}

	public FocList newFilterFocList(int mode){
		FocListOrder order = null;
		
		FocFieldEnum enumer = new FocFieldEnum(this, FocFieldEnum.CAT_KEY, FocFieldEnum.LEVEL_PLAIN);
		while(enumer != null && enumer.hasNext()){
			enumer.next();				
			if(order == null) order = new FocListOrder();
			order.addField(enumer.getFieldPath());
		}
		
		FocList list = getList(null, mode, order);

		list.setDirectImpactOnDatabase(true);
		list.setDirectlyEditable(false);
		return list;
	}
}
