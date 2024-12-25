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
package com.fab.model;

import com.fab.model.table.TableDefinition;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;

public class FocListDefinition extends FocObject {
	
	public FocListDefinition(FocConstructor constr){
		super(constr);
		newFocProperties();
	}
	
	public void setFocListId(int focListId){
		setPropertyInteger(FocListDefinitionDesc.FLD_FOCLIST_ID, focListId);
	}
	
	public int getFocListId(){
		return getPropertyInteger(FocListDefinitionDesc.FLD_FOCLIST_ID);
	}
	
	public void setFocListTitle(String title){
		setPropertyString(FocListDefinitionDesc.FLD_FOCLIST_TITLE, title);
	}
	
	public String getFocListTitle(){
		return getPropertyString(FocListDefinitionDesc.FLD_FOCLIST_TITLE);
	}
	
	public void setTableDefinition(TableDefinition tableDefinition){
		setPropertyObject(FocListDefinitionDesc.FLD_TABLE_DEFINITION, tableDefinition);
	}
	
	public TableDefinition getTableDefinition(){
		return (TableDefinition)getPropertyObject(FocListDefinitionDesc.FLD_TABLE_DEFINITION);
	}

}
