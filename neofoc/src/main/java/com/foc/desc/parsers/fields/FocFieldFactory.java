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

import java.util.HashMap;

@SuppressWarnings("serial")
public class FocFieldFactory {
	
	private HashMap<String, IFocFieldType> map = null;
	
	private FocFieldFactory(){
		map = new HashMap<String, IFocFieldType>();
		
		put(new FTypeBoolean());
		put(new FTypeDate());
		put(new FTypeDateTime());
		put(new FTypeDouble());
		put(new FTypeString());
		put(new FTypePassword());
		put(new FTypeTime());
		put(new FTypeInteger());
		put(new FTypeLong());
		put(new FTypeReference());
		put(new FTypeMultipleChoice());
		put(new FTypeMultipleChoiceString());
		put(new FTypeBlob());
		put(new FTypeBlobMedium());
		put(new FTypeImage());
		put(new FTypeFile());
		put(new FTypeObject());
		put(new FTypeTableName());
		put(new FTypeCloudImage());
		put(new FTypeSDOGeometryPoint());
/*		
		put(FieldDefinition.SQL_TYPE_ID_INT, new FDInteger());
		put(FieldDefinition.SQL_TYPE_ID_CHAR_FIELD, new FDChar());
		put(FieldDefinition.SQL_TYPE_ID_EMAIL_FIELD, new FDChar());//TODO FDEMail
		put(FieldDefinition.SQL_TYPE_ID_PASSWORD, new FDPassword());
		put(FieldDefinition.SQL_TYPE_ID_DOUBLE, new FDoubleType());
		put(FieldDefinition.SQL_TYPE_ID_DATE, new FDDate());
		put(FieldDefinition.SQL_TYPE_ID_OBJECT_FIELD, new FDObject());
		put(FieldDefinition.SQL_TYPE_ID_MULTIPLE_CHOICE, new FDMultipleChoice());
		put(FieldDefinition.SQL_TYPE_ID_BOOLEAN, new FBooleanType());
		put(FieldDefinition.SQL_TYPE_ID_TIME, new FDTime());
		put(FieldDefinition.SQL_TYPE_ID_IMAGE, new FDImage());
		put(FieldDefinition.SQL_TYPE_ID_LIST_FIELD, new FDList());
		put(FieldDefinition.SQL_TYPE_ID_BLOB_STRING, new FDBlobString());
*/
	}

	public void put(IFocFieldType type) {
		map.put(type.getTypeName(), type);
	}
	
	public IFocFieldType get(String typeName) {
		return map != null ? map.get(typeName) : null;
	}

	private static FocFieldFactory instance = null;
	public static FocFieldFactory getInstance(){
		if(instance == null){
			instance = new FocFieldFactory();
		}
		return instance;
	};
	
}
