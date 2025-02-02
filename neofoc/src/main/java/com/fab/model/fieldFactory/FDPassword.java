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
import com.fab.codeWriter.CodeWriter_OneFile;
import com.fab.model.table.FieldDefinition;

public class FDPassword extends FDAbstract {

	@Override
	public String getJavaType(FieldDefinition fldDef) {
		return "String";
	}

	@Override
	public String getGWTJavaType(FieldDefinition fldDef, boolean fullPath) {
		return getJavaType(fldDef);
	}
	
	@Override
	public String getGWTJavaType_ObjectNotNative(FieldDefinition fldDef, boolean fullPath) {
		return getGWTJavaType(fldDef, fullPath);
	}
	
	@Override
	public String getFocPropertyMethodPartialName(FieldDefinition fldDef){
		return "String";
	}
	
	@Override
	public String getDefaultValue(FieldDefinition fldDef) {
		return "null";
	}

	@Override
	public void addFieldDeclarationInFocDesc(CodeWriter codeWriter, FieldDefinition fldDef) {
		CodeWriter_OneFile intWrt = codeWriter.getInternalFileWriter();
		
		String varName = getFieldVariableName(fldDef);
		intWrt.addImport("b01.foc.desc.field.FPasswordField");
		intWrt.printCore("    FPasswordField "+varName+" = new FPasswordField(\""+fldDef.getName()+"\", \""+fldDef.getTitle()+"\", "+fldDef.getCW_FieldConstanteName()+", "+fldDef.isKey()+", "+fldDef.getLength()+");\n");
		addFieldDeclarationInFocDesc_Common(codeWriter, fldDef, varName);
	}
}
