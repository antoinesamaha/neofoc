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
import com.fab.model.table.FabMultiChoiceSet;
import com.fab.model.table.FabMultipleChoice;
import com.fab.model.table.FieldDefinition;

public class FDMultipleChoice extends FDAbstract {

	@Override
	public String getJavaType(FieldDefinition fldDef) {
		return "int";
	}

	@Override
	public String getGWTJavaType(FieldDefinition fldDef, boolean fullPath) {
		return "Integer";
	}
	
	@Override
	public String getGWTJavaType_ObjectNotNative(FieldDefinition fldDef, boolean fullPath) {
		return getGWTJavaType(fldDef, fullPath);
	}
	
	@Override
	public String getFocPropertyMethodPartialName(FieldDefinition fldDef) {
		return "MultiChoice";
	}
	
	@Override
	public String getDefaultValue(FieldDefinition fldDef) {
		return "0";
	}
	
	@Override
	public void addFieldDeclarationInFocDesc(CodeWriter codeWriter, FieldDefinition fldDef) {
		CodeWriter_OneFile intWrt = codeWriter.getInternalFileWriter();
		
		String varName = getFieldVariableName(fldDef);
		intWrt.addImport("b01.foc.desc.field.FMultipleChoiceField");
		intWrt.printCore("    FMultipleChoiceField "+varName+" = new FMultipleChoiceField(\""+fldDef.getName()+"\", \""+fldDef.getTitle()+"\", "+fldDef.getCW_FieldConstanteName()+", "+fldDef.isKey()+", "+fldDef.getLength()+");\n");
		FabMultiChoiceSet mcSet = fldDef.getMultiChoiceSet();
		if(mcSet != null && mcSet.getMultipleChoiceList() != null){
			for(int i=0; i<mcSet.getMultipleChoiceList().size(); i++){
				FabMultipleChoice choice = (FabMultipleChoice) mcSet.getMultipleChoiceList().getFocObject(i);
				intWrt.printCore("    "+varName+".addChoice("+choice.getIntValue()+", \""+choice.getDisplayText()+"\");\n");
			}
		}
		addFieldDeclarationInFocDesc_Common(codeWriter, fldDef, varName);
	}
}
