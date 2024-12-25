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
package com.foc.business.multilanguage;

import java.util.HashMap;

import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;

public class LanguageValuesFocDesc extends FocDesc {

	public static final int FLD_MAIN_OBJECT = 1;
	public static final int FLD_LANGUAGE    = 2;
	
	public static final int FLD_FIELD_START = 100;
	
	private FocDesc                   originalTableFocDesc        = null;
	private HashMap<Integer, Integer> mapOriginal2LangValueFields = null;
	
	public LanguageValuesFocDesc(FocDesc originalTableFocDesc){
		super(LanguageValuesFocObject.class, true, originalTableFocDesc.getStorageName()+"_MULTILANG", true);
		this.originalTableFocDesc = originalTableFocDesc;

		addReferenceField();
		
		FObjectField objField = new FObjectField("MAIN_OBJECT", "Main Object", FLD_MAIN_OBJECT, true, originalTableFocDesc, "MAIN_OBJ_", this, FField.FLD_LANGUAGE_VALUE_LIST);
		objField.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objField.setWithList(false);
		addField(objField);		

		objField = new FObjectField("LANGUAGE", "Language", FLD_LANGUAGE, false, LanguageDesc.getInstance(), "LANGUAGE_");
		objField.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objField.setSelectionList(LanguageDesc.getList(FocList.NONE));
    addField(objField);

    mapOriginal2LangValueFields = new HashMap<Integer, Integer>();
    
    for(int i=0; i<originalTableFocDesc.multiLanguageFields_size(); i++){
    	FStringField originalField  = originalTableFocDesc.multiLanguageFields_getAt(i);
			try{
				FStringField langValueField = (FStringField) originalField.cloneWithoutListeners();
	    	langValueField.setId(FLD_FIELD_START+i);
	    	mapOriginal2LangValueFields.put(originalField.getID(), FLD_FIELD_START+i);
	    	addField(langValueField);
			}catch (CloneNotSupportedException e){
				Globals.logException(e);
			}
    }
	}
	
	public void dispose(){
		super.dispose();
		originalTableFocDesc = null;
		
		if(mapOriginal2LangValueFields != null){
			mapOriginal2LangValueFields.clear();
			mapOriginal2LangValueFields = null;
		}
	}

	public FocDesc getOriginalTableFocDesc() {
		return originalTableFocDesc;
	}
}
