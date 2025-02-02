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
import com.fab.model.table.FieldDefinitionDesc;
import com.fab.model.table.TableDefinition;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.list.FocList;

public class CodeWriter_ProxyDesc extends CodeWriter {

	public CodeWriter_ProxyDesc(CodeWriterSet set){
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
		return CLASS_NAME_SUFFIX_PROXY_DESC;
	}

	@Override
	public boolean isServerSide() {
		return false;
	}

	@Override
	public void generateCode() {
		initFiles();
		
		CodeWriter_OneFile intWriter = getInternalFileWriter(); 
		CodeWriter_OneFile extWriter = getExternalFileWriter();
		
		//  Internal
		//  --------
		
		intWriter.addImport("b01.focGWT.client.proxy.ProxyDesc");
		intWriter.addImport("b01.focGWT.client.proxy.ProxyDescField");
		intWriter.addImport("b01.shared.dataStore.IDataStoreConst");
		//intWriter.addImport(extWriter.getPackageName()+"."+extWriter.getClassName()+CodeWriter.CLASS_NAME_SUFFIX_CONSTANT);
		
		intWriter.printCore("public class "+intWriter.getClassName()+" extends ProxyDesc {\n\n");
		
		intWriter.printCore("  public "+getClassName(true)+"(){\n");
		intWriter.printCore("	   addProxyFields();\n");
		intWriter.printCore("  }\n\n");
		
		intWriter.printCore("  private void addProxyFields(){\n");
		intWriter.printCore("    ProxyDescField field = null;\n");
		
		FocList fieldList = getTblDef().getFieldDefinitionList();
		for(int i=0; i<fieldList.size(); i++){
			FieldDefinition      fieldDefinition = (FieldDefinition) fieldList.getFocObject(i);
			FMultipleChoiceField mcFld           = (FMultipleChoiceField) FieldDefinitionDesc.getInstance().getFieldByID(FieldDefinitionDesc.FLD_SQL_TYPE) ; 
			String typeConstant = (String) mcFld.getChoiceItemForKey(fieldDefinition.getSQLType()).getTitle();
			typeConstant = typeConstant.toLowerCase();
			String firstCaracterInCapital = String.valueOf(typeConstant.charAt(0));
			firstCaracterInCapital = firstCaracterInCapital.toUpperCase();
			typeConstant = firstCaracterInCapital + typeConstant.substring(1);
			
			intWriter.addImport("b01.focGWT.client.proxy.fields."+typeConstant+"ProxyField");
			intWriter.printCore("    field = new "+typeConstant+"ProxyField(this, \""+fieldDefinition.getName()+"\", \""+fieldDefinition.getTitle()+"\");\n");
			if(fieldDefinition.getSQLType() == FieldDefinition.SQL_TYPE_ID_OBJECT_FIELD){
				TableDefinition targetTableDef = TableDefinition.getTableDefinitionForFocDesc(fieldDefinition.getFocDesc());
				CodeWriterSet cws = new CodeWriterSet(targetTableDef);
				CodeWriter_ProxyList cwProxyList_Target = cws.getCodeWriter_ProxyList();
				intWriter.addImport(cwProxyList_Target.getClassName_Full(false));
				intWriter.printCore("    field.setSelectionList("+cwProxyList_Target.getClassName()+".getList());\n");
			}
			intWriter.printCore("    addField(field);\n\n");

			/*
			typeConstant = "IDataStoreConst.FIELD_TYPE_"+typeConstant;
			typeConstant = typeConstant.replace(' ', '_');
			
			intWriter.printCore("    field = new ProxyDescField(\""+fieldDefinition.getName()+"\", \""+fieldDefinition.getTitle()+"\", "+typeConstant+");\n");
			if(fieldDefinition.getSQLType() == FieldDefinition.SQL_TYPE_ID_OBJECT_FIELD){
				TableDefinition targetTableDef = TableDefinition.getTableDefinitionForFocDesc(fieldDefinition.getFocDesc());
				CodeWriterSet cws = new CodeWriterSet(targetTableDef);
				CodeWriter_ProxyList cwProxyList_Target = cws.getCodeWriter_ProxyList();
				intWriter.addImport(cwProxyList_Target.getClassName_Full(false));
				intWriter.printCore("    field.setSelectionList("+cwProxyList_Target.getClassName()+".getList());\n");
			}
			intWriter.printCore("    addField(field);\n\n");
			*/
		}

		intWriter.printCore("  }\n\n");

		intWriter.addImport(getClassName_Full(false));
		intWriter.printCore("  public static "+getClassName()+" instance = null;\n");
		intWriter.printCore("  public static "+getClassName()+" getInstance(){\n");
		intWriter.printCore("    if(instance == null){\n");
		intWriter.printCore("      instance = new "+getClassName()+"();\n");
		intWriter.printCore("    }\n");
		intWriter.printCore("    return instance;\n");
		intWriter.printCore("  }\n\n");
		
		//End of the class
		intWriter.printCore("}\n");
		intWriter.compile();
		
		//  External
		//  --------
		
		//extWriter.addImport("b01.foc.desc.FocConstructor");
		extWriter.addImport(getClassName_Full(true));
		
		extWriter.printCore("@SuppressWarnings(\"serial\")\n");
		extWriter.printCore("public class "+extWriter.getClassName()+" extends "+intWriter.getClassName()+" {\n");
		
//		extWriter.printCore("  public "+extWriter.getClassName()+"(FocConstructor constr){\n");
//		extWriter.printCore("    super(constr);\n");
//		extWriter.printCore("  }\n");
		
		extWriter.printCore("}");
		extWriter.compile();
		
		closeFiles();
	}
}
