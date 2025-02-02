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

public class CodeWriter_FocList extends CodeWriter {

	public CodeWriter_FocList(CodeWriterSet set){
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
		return CLASS_NAME_SUFFIX_FOC_LIST;
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
		
		//  Internal
		//  --------
		
		intWriter.addImport("b01.foc.desc.FocConstructor");
		intWriter.addImport("b01.foc.desc.FocObject");
		intWriter.addImport(extWriter.getPackageName()+"."+extWriter.getClassName()+CodeWriter.CLASS_NAME_SUFFIX_CONSTANT);
		
		intWriter.printCore("public class "+intWriter.getClassName()+" extends FocList implements "+extWriter.getClassName()+CodeWriter.CLASS_NAME_SUFFIX_CONSTANT+ " {\n\n");
		intWriter.printCore("  public "+intWriter.getClassName()+"(FocConstructor constr){\n");
		
		intWriter.printCore("    super(constr);\n");
		intWriter.printCore("    newFocProperties();\n");
		intWriter.printCore("  }\n\n");

		FocList fieldList = getTblDef().getFieldDefinitionList();
		for(int i=0; i<fieldList.size(); i++){
			FieldDefinition fieldDefinition = (FieldDefinition) fieldList.getFocObject(i);
			FieldFactory.getInstance().addGetterSetterInFocObject(this, fieldDefinition);
		}
		
		intWriter.printCore("}\n");
		intWriter.compile();
		
		//  External
		//  --------
		
		extWriter.addImport("b01.foc.desc.FocConstructor");
		extWriter.addImport(intWriter.getPackageName()+"."+intWriter.getClassName());
		
		extWriter.printCore("public class "+extWriter.getClassName()+" extends "+intWriter.getClassName()+" {\n");
		
		extWriter.printCore("  public "+extWriter.getClassName()+"(FocConstructor constr){\n");
		extWriter.printCore("    super(constr);\n");
		extWriter.printCore("  }\n");
		
		extWriter.printCore("}");
		extWriter.compile();
		
		closeFiles();
	}
}
