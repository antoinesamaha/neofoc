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

import com.fab.model.table.FieldDefinitionGuiBrowsePanel;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class UndCustFieldGuiBrowsePanel extends FieldDefinitionGuiBrowsePanel {
	
	public UndCustFieldGuiBrowsePanel(FocList list, int viewID){
		super(list, viewID);
	}
	
	public UndCustFieldGuiBrowsePanel(FocList list, int viewID, boolean withDictionaryGroups){
		super(list, viewID, withDictionaryGroups);
	}
	
	@Override
	protected void addAdditionalColumns(boolean allowEdit){
		UndCustFieldDesc focDesc = (UndCustFieldDesc) UndCustFieldDesc.getInstance();
		getTableView().addColumn(focDesc, UndCustFieldDesc.FLD_NOT_PHYSICAL_DIFFERENCE, allowEdit);
	}
}
