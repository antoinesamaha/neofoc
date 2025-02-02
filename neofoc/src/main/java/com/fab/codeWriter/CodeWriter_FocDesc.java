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
import com.foc.desc.field.FField;
import com.foc.list.FocList;

public class CodeWriter_FocDesc extends CodeWriter {

	public CodeWriter_FocDesc(CodeWriterSet set){
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
		return CLASS_NAME_SUFFIX_FOC_DESC;
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

		CodeWriter_Const cw_Const = getCodeWriterSet().getCodeWriter_Const();
		
		//-----------------------------------
		intWriter.addImport("b01.foc.desc.FocDesc");
		if(isUseAutoGenDirectory()) intWriter.addImport(extWriter.getPackageName()+".*");
		intWriter.addImport(extWriter.getPackageName()+"."+getClassName_FocObject());
		intWriter.addImport(extWriter.getPackageName()+"."+getClassName_FocObject()+CLASS_NAME_SUFFIX_CONSTANT);
		intWriter.addImport(extWriter.getPackageName()+"."+getClassName_FocObject()+CLASS_NAME_SUFFIX_FOC_DESC);
		
		intWriter.printCore("public class "+intWriter.getClassName()+" extends FocDesc implements "+getClassName_FocObject()+CLASS_NAME_SUFFIX_CONSTANT+"{\n");
		
		intWriter.printCore("  public "+intWriter.getClassName()+"(){\n");
		intWriter.printCore("    super("+getClassName_FocObject()+".class, DB_RESIDENT, "+cw_Const.getConstant_TableName()+", "+getTblDef().isKeyUnique()+");\n");
		intWriter.printCore("    setAllowAdaptDataModel(false);\n");//This is Done because when generated from DB we do not want to modify it later (IFS) 
		if(getTblDef().isWithReference()){
			intWriter.addImport("b01.foc.desc.field.FField");
			intWriter.printCore("    FField refFld = addReferenceField();\n");
			if(getTblDef().getRefFieldName() != null && !getTblDef().getRefFieldName().equals(FField.REF_FIELD_NAME)){
				intWriter.printCore("    refFld.setName(\""+getTblDef().getRefFieldName()+"\");\n");
			}
			intWriter.printCore("\n");
		}
		
		FocList fieldList = getTblDef().getFieldDefinitionList();
		for(int i=0; i<fieldList.size(); i++){
			FieldDefinition fieldDefinition = (FieldDefinition) fieldList.getFocObject(i);
			FieldFactory.getInstance().addFieldDeclarationInFocDesc(this, fieldDefinition);
		}

		intWriter.printCore("  }\n\n");

		intWriter.addImport("b01.foc.list.FocList");
		intWriter.printCore("  @Override\n");
		intWriter.printCore("  public FocList newFocList(){\n");
		intWriter.printCore("    FocList list = super.newFocList();\n");
		intWriter.printCore("    list.setDirectlyEditable(false);\n");
		intWriter.printCore("    list.setDirectImpactOnDatabase(true);\n");
		intWriter.printCore("    return list;\n");
		intWriter.printCore("  }\n\n");
		
		intWriter.printCore("  public static FocDesc getInstance() {\n");
		intWriter.printCore("    return getInstance(DB_TABLE_NAME, "+extWriter.getClassName()+".class);\n");
		intWriter.printCore("  }\n\n");
		
		intWriter.printCore("}\n");
		intWriter.compile();
		
		//-----------------------------------
		if(isUseAutoGenDirectory()){
			extWriter.addImport(intWriter.getPackageName()+"."+intWriter.getClassName());
	
			extWriter.printCore("public class "+extWriter.getClassName()+" extends "+intWriter.getClassName()+" {\n");
			
			extWriter.printCore("}\n");
			extWriter.compile();
		}
		
		closeFiles();
	}
}
