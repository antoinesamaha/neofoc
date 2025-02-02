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
package com.foc.desc.parsers.fields;

import java.lang.reflect.Field;

import com.foc.annotations.model.fields.FocMultipleChoiceString;
import com.foc.desc.field.FField;
import com.foc.desc.field.FMultipleChoiceStringField;

public class FTypeMultipleChoiceString extends FocFieldTypAbstract<FocMultipleChoiceString> {

	@Override
	public String getTypeName() {
		return TYPE_MULTIPLE_CHOICE_STRING;
	}

	@Override
	public FField newFField(Class focObjClass, Field f, FocMultipleChoiceString a) {
//  	fld = new FMultipleChoiceStringField(getName(att), getTitle(att), xmlFocDesc.nextFldID(), isKey(att), getSize(att));
//  	String sameCol = getString(att, ATT_SAME_COLUMN);
//  	if(sameCol == null || sameCol.trim().toLowerCase().equals("true")){
//  		((FMultipleChoiceStringField)fld).setChoicesAreFromSameColumn(xmlFocDesc);
//  	}
//  	xmlFocDesc.addField(fld);
		
		FMultipleChoiceStringField focField = null;
		focField = new FMultipleChoiceStringField(getDBFieldName(f), getFieldTitle(f), FField.NO_FIELD_ID, false, a.size());
		focField.setDBResident(a.dbResident());
		focField.setUseSameColumnValues(a.useColumnValues());
		focField.setAllowOutofListSelection(a.allowNewValues());
		focField.setSortItems(a.sortItems());
		if(a.choices() != null){
			for(String title: a.choices()){
				focField.addChoice(title);
			}
		}
		return focField;
	}

}
