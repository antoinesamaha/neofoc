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
package com.fab;

import java.util.Iterator;

import com.fab.model.filter.FilterDefinition;
import com.fab.model.filter.FilterFieldDefinition;
import com.fab.model.filter.FilterFieldDefinitionDesc;
import com.fab.model.filter.FocDescForUserDefinedFilter;
import com.fab.model.filter.UserDefinedFilter;
import com.foc.Globals;
import com.foc.IFocDescDeclaration;
import com.foc.desc.FocDesc;
import com.foc.desc.FocModule;
import com.foc.list.FocLinkForeignKey;
import com.foc.list.FocList;
import com.foc.list.filter.FilterDesc;

public class FilterFocDescDeclaration implements IFocDescDeclaration {
	
	private FilterDefinition filterDefinition = null;
	private FocDesc focDesc = null;
	private FilterDesc filterDesc = null;
	private boolean constructingFocDesc = false;
	private FocModule module = null;
	
	private FilterFocDescDeclaration(FocModule module, FilterDefinition fitlerDefinition){
		this.filterDefinition = fitlerDefinition;
		constructingFocDesc = false;
		this.module = module;
	}
	
	public void dispose(){
		this.filterDefinition = null;
		focDesc = null;
		filterDesc = null;
		module = null;
	}
	
	private FilterDefinition getFilterDefinition(){
		return this.filterDefinition;
	}
	
	public String getTableName(){
		String name = null;
		FilterDefinition definition = getFilterDefinition();
		if(definition != null){
			name = definition.getFilterTableName();
		}
		return name;
	}
	
	@SuppressWarnings("unchecked")
	public FilterDesc getFilterDesc_CreateIfNeeded(){
		if(filterDesc == null){
			FilterDefinition filterDefinition = getFilterDefinition();
			if(filterDefinition != null){
				filterDesc = new FilterDesc(filterDefinition.getBaseFocDesc());
				//dont use the the fucntion filterDefinition.getFilterFieldList() because when this filterDefinition was created 
				//the function FilterFieldDefinitionDesc.getInstance() was not called yet(that means the FObjectField "filterDefinition" 
				//in FilterFieldDefinitionDesc was not created and it has not create the listField in the masterDesc "FilterDefinitionDesc"; see Application.prepareDBForLogin() and fabModule.declareFocObjects())
				//so this object(filterDefinition) was created with out the property FList for this reson we cant use getPropertyList
				//to get the list of filterFieldDefinition
				FocLinkForeignKey link = new FocLinkForeignKey(FilterFieldDefinitionDesc.getInstance(), FilterFieldDefinitionDesc.FLD_FILTER_DEFINITION, true);
				FocList fieldDefinitionList = new FocList(filterDefinition, link, null);
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
		}
		return filterDesc;
	}
	 
	public FocDesc getFocDescription() {
		if(!constructingFocDesc){
			constructingFocDesc = true;
			if(focDesc == null){
				focDesc = new FocDescForUserDefinedFilter(UserDefinedFilter.class, FocDesc.DB_RESIDENT, getTableName(), false);
				constructingFocDesc = false;
			}
		}
		return focDesc;
	}
	
	public static FilterFocDescDeclaration getFilterFocDescDeclaration(FilterDefinition filterDefinition){
		FilterFocDescDeclaration declaration = null;
		if(filterDefinition != null){
			String name = filterDefinition.getFilterTableName();
			declaration = (FilterFocDescDeclaration)Globals.getApp().getIFocDescDeclarationByName(name);
			if(declaration == null){
				declaration = new FilterFocDescDeclaration(FabModule.getInstance(), filterDefinition);
				Globals.getApp().putIFocDescDeclaration(name, declaration);
			}
		}
		return declaration;
	}

	public int getPriority() {
		return IFocDescDeclaration.PRIORITY_THIRD;
	}

	@Override
	public FocModule getFocModule() {
		return module;
	}

	@Override
	public String getName() {
		return getTableName();
	}
}
