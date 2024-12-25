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

import com.fab.codeWriter.CodeWriter;
import com.fab.model.table.FieldDefinition;

public interface IFieldType {
	public void addFieldDeclarationInFocDesc(CodeWriter codeWriter, FieldDefinition fldDef);
	public void addGetterSetterInFocObject(CodeWriter codeWriter, FieldDefinition fldDef);
	public void addFieldDeclarationInPojo(CodeWriter codeWriter, FieldDefinition fldDef);
	public void addGetterSetterInPojo(CodeWriter codeWriter, FieldDefinition fldDef);
	
	/*
	public void addDeclarationInFocObjectWebClient(CodeWriter codeWriter, FieldDefinition fldDef);
	public void addGetterSetterInWebFocObjectClient(CodeWriter codeWriter, FieldDefinition fldDef);
	*/
	
	public String getFocPropertyMethodPartialName(FieldDefinition fldDef);
	//public String getFocFieldType(FieldDefinition fldDef);
	
	public String getJavaType(FieldDefinition fldDef);
	public String getGWTJavaType(FieldDefinition fldDef, boolean fullPath);
	public String getGWTJavaType_ObjectNotNative(FieldDefinition fldDef, boolean fullPath);
	public String getDefaultValue(FieldDefinition fldDef);
}
