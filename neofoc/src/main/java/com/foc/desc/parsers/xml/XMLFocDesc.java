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
package com.foc.desc.parsers.xml;

import com.foc.desc.FocModule;
import com.foc.desc.parsers.ParsedFocDesc;
import com.foc.list.filter.IFocDescForFilter;

public class XMLFocDesc extends ParsedFocDesc implements IFocDescForFilter {

	private XMLFocDescParser focDescParser = null;
	
	public XMLFocDesc(FocModule module, String storageName, String xmlFullFileName, Class<XMLFocObject> focObjectClass){
		this(focObjectClass, DB_RESIDENT, storageName, false, false);
		setModule(module);
	}
	
	public XMLFocDesc(Class focObjectClass, boolean dbResident, String storageName, boolean isKeyUnique, boolean withWorkfllow) {
		super(focObjectClass, dbResident, storageName, isKeyUnique, withWorkfllow);
	}
	
	public void dispose(){
		super.dispose();
	}

	public XMLFocDescParser getFocDescParser() {
		return focDescParser;
	}

	public void setFocDescParser(XMLFocDescParser focDescParser) {
		this.focDescParser = focDescParser;
	}
}
