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
package com.fab.gui.html;

import com.fab.model.table.TableDefinition;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;

public class TableHtml extends FocObject {

	public TableHtml(FocConstructor constr) {
		super(constr);
		newFocProperties();
	}
	
	public void setDescription(String description){
		setPropertyString(TableHtmlDesc.FLD_DESCRIPTION, description);
	}
	
	public String getDescription(){
		return getPropertyString(TableHtmlDesc.FLD_DESCRIPTION);
	}

	public void setTitle(String title){
		setPropertyString(TableHtmlDesc.FLD_TITLE, title);
	}
	
	public String getTitle(){
		return getPropertyString(TableHtmlDesc.FLD_TITLE);
	}

	public void setTableDefinition(TableDefinition tableDefinition){
		setPropertyObject(TableHtmlDesc.FLD_TABLE_DEFINITION, tableDefinition);
	}
	
	public TableDefinition getTableDefinition(){
		return (TableDefinition)getPropertyObject(TableHtmlDesc.FLD_TABLE_DEFINITION);
	}
	
	public void setHTML(String html){
		setPropertyString(TableHtmlDesc.FLD_HTML, html);
	}
	
	public String getHTML(){
		return getPropertyString(TableHtmlDesc.FLD_HTML);
	}

}
