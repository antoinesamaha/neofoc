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

import java.util.Iterator;

import com.foc.Globals;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.list.FocList;
import com.foc.list.filter.FilterDesc;
import com.foc.list.filter.FocListFilterBindedToList;

public class FilterDefinition extends FocObject {
	
	public static final int VIEW_ID_DEFAULT = 1;
	public static final int VIEW_ID_FOR_NEW_ITEM = 2;
	
	private FilterDesc filterDesc = null;

	public FilterDefinition(FocConstructor constr) {
		super(constr);
		newFocProperties();
	}
	
	public void dispose(){
		super.dispose();
		this.filterDesc = null;
	}
	
	public void setTitle(String title){
		setPropertyString(FilterDefinitionDesc.FLD_TITLE, title);
	}
	
	public String getTitle(){
		return getPropertyString(FilterDefinitionDesc.FLD_TITLE);
	}
	
	public String getBaseFocDescName(){
		return getPropertyMultipleChoiceStringBased(FilterDefinitionDesc.FLD_BASE_FOC_DESC);
	}
	
	public FocDesc getBaseFocDesc(){
		return getPropertyDesc(FilterDefinitionDesc.FLD_BASE_FOC_DESC);
	}
	
	public String getFilterTableName(){
		String name = getBaseFocDescName();
		if(name != null){
			name = FocListFilterBindedToList.getFilterTableName(name);
		}
		return name;
	}
	
	public FocList getFieldDefinitionList(){
		return getPropertyList(FilterDefinitionDesc.FLD_FILTER_FIELD_DEFINITION_LIST);
	}
	
	@SuppressWarnings("unchecked")
	public FilterDesc getFilterDesc(){
		if(filterDesc == null){
			filterDesc = new FilterDesc(getBaseFocDesc());
			/*FocLinkForeignKey link = new FocLinkForeignKey(FilterFieldDefinitionDesc.getInstance(), FilterFieldDefinitionDesc.FLD_FILTER_DEFINITION, true);
			FocList fieldDefinitionList = new FocList(this, link, null);*/
			FocList fieldDefinitionList = getFieldDefinitionList();
			fieldDefinitionList.loadIfNotLoadedFromDB();
			if(fieldDefinitionList != null){
				Iterator<FilterFieldDefinition> iter = fieldDefinitionList.focObjectIterator();
				while(iter != null && iter.hasNext()){
					FilterFieldDefinition definition = iter.next();
					if(definition != null){
						definition.addConditionToFilterDesc(filterDesc);
					}
				}
			}
		}
		return filterDesc;
	}
	
	/*public static void fillFDescFieldChoices(FDescFieldStringBased descField){
		if(descField != null){
			Iterator<IFocDescDeclaration> iter = Globals.getApp().getFocDescDeclarationIterator();
			while(iter != null && iter.hasNext()){
				IFocDescDeclaration declaration = iter.next();
				if(declaration != null){
					FocDesc focDesc = declaration.getFocDesctiption();
					if(focDesc != null){
						String focDescName = focDesc.getStorageName();
						descField.putChoice(focDescName);
					}
				}
			}
		}
	}*/
	
	public static FocDesc getFilterFocDesc(String baseDescStorageName){
		String filterTableName = FocListFilterBindedToList.getFilterTableName(baseDescStorageName);
		FocDesc filterFocDesc = Globals.getApp().getFocDescByName(filterTableName);
		return filterFocDesc;
	}

}
