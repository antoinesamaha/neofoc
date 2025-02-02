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
package com.fab.model.fieldFactory;

import java.util.HashMap;

import com.fab.codeWriter.CodeWriter;
import com.fab.model.table.FieldDefinition;
import com.foc.Globals;
import com.foc.IFocEnvironment;

@SuppressWarnings("serial")
public class FieldFactory extends HashMap<Integer, FDAbstract> {
	
//	private HashMap<String, FDAbstract> mapByTypeName = null;
	
	public FieldFactory(){
//		mapByTypeName = new HashMap<String, FDAbstract>();
		
		put(FieldDefinition.SQL_TYPE_ID_INT, new FDInteger());
		put(FieldDefinition.SQL_TYPE_ID_CHAR_FIELD, new FDChar());
		put(FieldDefinition.SQL_TYPE_ID_EMAIL_FIELD, new FDChar());//TODO FDEMail
		put(FieldDefinition.SQL_TYPE_ID_PASSWORD, new FDPassword());
		put(FieldDefinition.SQL_TYPE_ID_DOUBLE, new FDDouble());
		put(FieldDefinition.SQL_TYPE_ID_DATE, new FDDate());
		put(FieldDefinition.SQL_TYPE_ID_DATE_TIME, new FDDate());
		put(FieldDefinition.SQL_TYPE_ID_OBJECT_FIELD, new FDObject());
		put(FieldDefinition.SQL_TYPE_ID_MULTIPLE_CHOICE, new FDMultipleChoice());
		put(FieldDefinition.SQL_TYPE_ID_BOOLEAN, new FDBoolean());
		put(FieldDefinition.SQL_TYPE_ID_TIME, new FDTime());
		put(FieldDefinition.SQL_TYPE_ID_IMAGE, new FDImage());
		put(FieldDefinition.SQL_TYPE_ID_LIST_FIELD, new FDList());
		put(FieldDefinition.SQL_TYPE_ID_BLOB_STRING, new FDBlobString());
	}

	@Override
	public FDAbstract put(Integer key, FDAbstract field) {
		FDAbstract fld = super.put(key, field);
//		if(mapByTypeName != null) mapByTypeName.put(field.getFieldTypeName(), field);
		return fld;
	}
	
//	public IFieldType getFieldType(String typeName){
//		return mapByTypeName.get(typeName);
//	}
	
	public void addFieldDeclarationInPojo(CodeWriter codeWriter, FieldDefinition fieldDefinition){
		FDAbstract fieldType = get(fieldDefinition.getSQLType());
		if(fieldType != null){
			fieldType.addFieldDeclarationInPojo(codeWriter, fieldDefinition);
		}else{
			String message = fieldDefinition.getTableDefinition() != null ? fieldDefinition.getTableDefinition().getName() : "";
			message += " - "+fieldDefinition.getName() + " Type: " + fieldDefinition.getSQLType();
			Globals.showNotification("Error", "Could Not find implementation for Field Definition Type : "+message, IFocEnvironment.TYPE_ERROR_MESSAGE);
			fieldType = get(fieldDefinition.getSQLType());
		}
	}	
	
	public void addFieldGetterSetterInPojo(CodeWriter codeWriter, FieldDefinition fieldDefinition){
		FDAbstract fieldType = get(fieldDefinition.getSQLType());
		if(fieldType != null){
			fieldType.addGetterSetterInPojo(codeWriter, fieldDefinition);
		}else{
			String message = fieldDefinition.getTableDefinition() != null ? fieldDefinition.getTableDefinition().getName() : "";
			message += " - "+fieldDefinition.getName() + " Type: " + fieldDefinition.getSQLType();
			Globals.showNotification("Error", "Could Not find implementation for Field Definition Type : "+message, IFocEnvironment.TYPE_ERROR_MESSAGE);
			fieldType = get(fieldDefinition.getSQLType());
		}
	}
	
	public void addFieldDeclarationInFocDesc(CodeWriter codeWriter, FieldDefinition fieldDefinition){
		FDAbstract fieldType = get(fieldDefinition.getSQLType());
		if(fieldType != null){
			fieldType.addFieldDeclarationInFocDesc(codeWriter, fieldDefinition);
		}else{
			String message = fieldDefinition.getTableDefinition() != null ? fieldDefinition.getTableDefinition().getName() : "";
			message += " - "+fieldDefinition.getName() + " Type: " + fieldDefinition.getSQLType();
			Globals.showNotification("Error", "Could Not find implementation for Field Definition Type : "+message, IFocEnvironment.TYPE_ERROR_MESSAGE);
			fieldType = get(fieldDefinition.getSQLType());
		}
	}

	public void addGetterSetterInFocObject(CodeWriter codeWriter, FieldDefinition fieldDefinition){
		FDAbstract fieldType = get(fieldDefinition.getSQLType());
		if(fieldType != null){
			fieldType.addGetterSetterInFocObject(codeWriter, fieldDefinition);
		}else{
			Globals.getDisplayManager().popupMessage("Could Not find implementation for Getter Setter in Field Definition Type : "+fieldDefinition.getName());
		}
	}

	public void addDeclarationInFocObjectWebClient(CodeWriter codeWriter, FieldDefinition fieldDefinition){
		FDAbstract fieldType = get(fieldDefinition.getSQLType());
		if(fieldType != null){
			fieldType.addDeclarationInFocObjectWebClient(codeWriter, fieldDefinition);
		}else{
			Globals.getDisplayManager().popupMessage("Could Not find implementation for Getter Setter in Field Definition Type : "+fieldDefinition.getName());
		}
	}

	public void addGetterSetterInWebFocObjectClient(CodeWriter codeWriter, FieldDefinition fieldDefinition){
		FDAbstract fieldType = get(fieldDefinition.getSQLType());
		if(fieldType != null){
			fieldType.addGetterSetterInWebFocObjectClient(codeWriter, fieldDefinition);
		}else{
			Globals.getDisplayManager().popupMessage("Could Not find implementation for Getter Setter in Field Definition Type : "+fieldDefinition.getName());
		}
	}
	
	public String getConvertString2ValueFunction(FieldDefinition fieldDefinition){
		String fctName = null;
		FDAbstract fieldType = get(fieldDefinition.getSQLType());
		if(fieldType != null){
			fctName = fieldType.getConvertString2ValueFunction();
		}else{
			Globals.getDisplayManager().popupMessage("Could Not find implementation for convertString2ValueFunction Type : "+fieldDefinition.getName());
		}
		return fctName;
	}

	public String getConvertValue2StringFunction(FieldDefinition fieldDefinition){
		String fctName = null;
		FDAbstract fieldType = get(fieldDefinition.getSQLType());
		if(fieldType != null){
			fctName = fieldType.getConvertValue2StringFunction();
		}else{
			Globals.getDisplayManager().popupMessage("Could Not find implementation for convertValue2StringFunction Type : "+fieldDefinition.getName());
		}
		return fctName;
	}

	/*
	public void getGetterMethodNameField(CodeWriter codeWriter, FieldDefinition fieldDefinition){
		FDAbstract fieldType = get(fieldDefinition.getSQLType());
		if(fieldType != null){
			fieldType.getGetterMethodNameField(codeWriter, fieldDefinition);
		}else{
			Globals.getDisplayManager().popupMessage("Could Not find implementation for Getter Setter in Field Definition Type : "+fieldDefinition.getName());
		}
	}
	*/
	
	private static FieldFactory fieldFactory = null;
	public static FieldFactory getInstance(){
		if(fieldFactory == null){
			fieldFactory = new FieldFactory();
		}
		return fieldFactory;
	}
	
}
