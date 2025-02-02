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

import com.fab.model.table.FieldDefinition;
import com.foc.shared.xmlView.XMLViewKey;

public class FXMLViewSelectorField extends FMultipleChoiceStringField {
	
	private XMLViewKey xmlViewKey = null; 
	
	public FXMLViewSelectorField(String name, String title, int id) {
		super(name, title, id, false, XMLViewKey.LEN_VIEW);
	}
	
	public void dispose(){
		super.dispose();
	}
	
  @Override
  public int getFabType() {
    return FieldDefinition.SQL_TYPE_ID_XML_VIEW_SELECTOR;
  }

	
	public XMLViewKey getXmlViewKey() {
		return xmlViewKey;
	}

	public void setXmlViewKey(XMLViewKey xmlViewKey) {
		this.xmlViewKey = xmlViewKey;
	}

}
