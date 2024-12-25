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
package com.foc.desc.field;

import java.util.HashMap;
import java.util.Iterator;

import com.fab.FabStatic;
import com.fab.model.table.FieldDefinition;
import com.fab.parameterSheet.ParameterSheetSelector;
import com.fab.parameterSheet.ParameterSheetSelectorDesc;
import com.foc.Globals;
import com.foc.IFocDescDeclaration;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.list.FocList;
import com.foc.property.FDescPropertyStringBased;
import com.foc.property.FProperty;

public class FDescFieldStringBased extends FMultipleChoiceStringField {
	private HashMap<String, FocDesc> focDescMap = null;
	private ITableNameFilter tableNameFilter = null;
	
	public FDescFieldStringBased(String name, String title, int id, boolean key) {
		super(name, title, id, key, 50);
	}
	
	public void dispose(){
		super.dispose();
		if(this.focDescMap != null){
			this.focDescMap.clear();
			this.focDescMap = null;
		}
	}
	
  @Override
  public int getFabType() {
    return FieldDefinition.SQL_TYPE_ID_MULTIPLE_CHOICE_FOC_DESC;
  }

  public FProperty newProperty(FocObject masterObj, Object defaultValue){
  	return new FDescPropertyStringBased(masterObj, getID(), (String) (defaultValue == null ? "" : defaultValue));
  }
  
  private HashMap<String, FocDesc> getFocDescMap(){
  	if(this.focDescMap == null){
  		this.focDescMap = new HashMap<String, FocDesc>();
  	}
  	return this.focDescMap;
  }
  
  public void putChoice(String focDescName){
  	addChoice(focDescName);
  }
  
  public FocDesc getFocDesc(String focDescName){
  	FocDesc focDesc = getFocDescMap().get(focDescName);
  	if(focDesc == null){
  		focDesc = Globals.getApp().getFocDescByName(focDescName);
  		if(focDesc != null){
  			getFocDescMap().put(focDescName, focDesc);
  		}
  	}
  	return focDesc;
  }
  
  public void fillWithAllDeclaredFocDesc(){
  	FabStatic.addStringBasedField(this);
  	re_fillWithAllDeclaredFocDesc();
	}

  public void re_fillWithAllDeclaredFocDesc(){
  	removeAllChoices();
		Iterator<IFocDescDeclaration> iter = Globals.getApp().getFocDescDeclarationIterator();
		while(iter != null && iter.hasNext()){
			IFocDescDeclaration declaration = iter.next();
			if(declaration != null){
				FocDesc focDesc = declaration.getFocDescription();
				if(focDesc != null && (tableNameFilter == null || tableNameFilter.includeFocDesc(focDesc))){
					String focDescName = focDesc.getStorageName();
					addChoice(focDescName);
				}
			}
		}
	}
  
  public void fillWithParamSetFocDesc(){
  	ParameterSheetSelectorDesc.addStringBasedField(this);
  	re_fillWithParamSetFocDesc();
  }
  
  public void re_fillWithParamSetFocDesc(){
  	removeAllChoices();
  	addChoice("");
  	FocList paramSetSelectorList = ParameterSheetSelectorDesc.getList(FocList.LOAD_IF_NEEDED);
  	Iterator iter = paramSetSelectorList.focObjectIterator();
  	while(iter != null && iter.hasNext()){
  		ParameterSheetSelector selector = (ParameterSheetSelector) iter.next();
  		if(selector != null){
				addChoice(selector.getTableName());
			}
		}
  }

	public ITableNameFilter getTableNameFilter() {
		return tableNameFilter;
	}

	public void setTableNameFilter(ITableNameFilter tableNameFilter) {
		this.tableNameFilter = tableNameFilter;
	}

  public interface ITableNameFilter {
  	public boolean includeFocDesc(FocDesc focDesc);
  	public void dispose();
  }
}
