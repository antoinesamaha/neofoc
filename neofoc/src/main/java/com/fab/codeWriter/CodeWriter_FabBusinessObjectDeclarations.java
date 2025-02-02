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
package com.fab.codeWriter;

import com.fab.model.fieldFactory.FieldFactory;
import com.fab.model.table.FieldDefinition;
import com.foc.list.FocList;

public class CodeWriter_FabBusinessObjectDeclarations extends CodeWriter {

	public CodeWriter_FabBusinessObjectDeclarations(CodeWriterSet set){
		super(set);
	}

	@Override
	public boolean hasInternalFile() {
		return true;
	}

	@Override
	public boolean hasExternalFile() {
		return true;
	}

	@Override
	public String getFileSuffix() {
		return CLASS_NAME_SUFFIX_FAB_DECLARATION;
	}

	@Override
	public boolean isServerSide() {
		return true;
	}
	
	@Override
	public void generateCode() {
		initFiles();

		CodeWriter_OneFile intWriter = getInternalFileWriter();
		CodeWriter_OneFile extWriter = getExternalFileWriter();

		//-----------------------------------
		intWriter.addImport("b01.foc.desc.FocDesc");
		intWriter.addImport(extWriter.getPackageName()+"."+getClassName_FocObject());
		intWriter.addImport(extWriter.getPackageName()+"."+getClassName_FocObject()+CLASS_NAME_SUFFIX_CONSTANT);
		
		intWriter.printCore("public class "+intWriter.getClassName()+" {\n");
		
		intWriter.printCore("  public "+intWriter.getClassName()+"(){\n");
		intWriter.printCore("    super("+getClassName_FocObject()+".class, DB_RESIDENT, \""+getTblDef().getName()+"\", "+getTblDef().isKeyUnique()+");\n");
		if(getTblDef().isWithReference()){
			intWriter.printCore("    addReferenceField();\n");
		}
		
		FocList fieldList = getTblDef().getFieldDefinitionList();
		for(int i=0; i<fieldList.size(); i++){
			FieldDefinition fieldDefinition = (FieldDefinition) fieldList.getFocObject(i);
			FieldFactory.getInstance().addFieldDeclarationInFocDesc(this, fieldDefinition);
		}

		intWriter.printCore("  }\n");
		
		intWriter.printCore("}\n");
		intWriter.compile();
		
		//-----------------------------------
		extWriter.addImport(intWriter.getPackageName()+"."+intWriter.getClassName());

		extWriter.printCore("public class "+extWriter.getClassName()+" extends "+intWriter.getClassName()+" {\n");
		
		extWriter.printCore("}\n");
		extWriter.compile();
		
		closeFiles();
	}
}
