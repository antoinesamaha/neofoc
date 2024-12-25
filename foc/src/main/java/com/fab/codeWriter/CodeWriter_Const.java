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

import com.fab.model.table.FieldDefinition;
import com.foc.list.FocList;

public class CodeWriter_Const extends CodeWriter {

	public CodeWriter_Const(CodeWriterSet set) {
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
		return CLASS_NAME_SUFFIX_CONSTANT;
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

		intWriter.printCore("public interface "+intWriter.getClassName()+" {\n\n");

		intWriter.printCore("  public static final String "+getConstant_TableName()+" = \""+getTblDef().getName()+"\";\n");
		
		FocList fieldList = getTblDef().getFieldDefinitionList();
		for(int i=0; i<fieldList.size(); i++){
			FieldDefinition fieldDefinition = (FieldDefinition) fieldList.getFocObject(i);
			
			intWriter.printCore("  public static final int "+fieldDefinition.getCW_FieldConstanteName()+" = "+fieldDefinition.getID()+";\n");
		}

		intWriter.printCore("\n");
		intWriter.printCore("}\n");
		intWriter.compile();
		
		//  External
		//  --------
		if(isUseAutoGenDirectory()){
			extWriter.addImport(intWriter.getPackageName()+"."+intWriter.getClassName());
			
			extWriter.printCore("public interface "+extWriter.getClassName()+" extends "+intWriter.getClassName()+" {\n");
			extWriter.printCore("}");
			
			extWriter.compile();
		}
		
		closeFiles();
	}
	
	public String getConstant_TableName(){
		return "DB_TABLE_NAME";//+getTblDef().getName();
	}
}
