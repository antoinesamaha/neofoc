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
import com.fab.model.table.TableDefinitionGuiDetailsPanel;
import com.foc.desc.FocObject;
import com.foc.gui.FGTabbedPane;
import com.foc.gui.FPanel;

@SuppressWarnings("serial")
public class UndCustTableGuiDetailsPanel extends TableDefinitionGuiDetailsPanel {
	
	public UndCustTableGuiDetailsPanel(FocObject tableDefinition, int viewID){
		super(tableDefinition, viewID, false);
	}
	
	@Override
	public FieldDefinitionGuiBrowsePanel newFieldBrowsePanel(int viewID, boolean withBrowseDetailsDictionary){
		return new UndCustFieldGuiBrowsePanel(getTableDefinition().getFieldDefinitionList(), viewID, withBrowseDetailsDictionary);
	}
	
	@Override
	public void postValidationAction(){
		
	}
}
