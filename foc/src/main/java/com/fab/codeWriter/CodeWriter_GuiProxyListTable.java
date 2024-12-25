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

public class CodeWriter_GuiProxyListTable extends CodeWriter {

	public CodeWriter_GuiProxyListTable(CodeWriterSet set){
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
		return CLASS_NAME_SUFFIX_PROXY_GUI_TABLE;
	}

	@Override
	public boolean isServerSide() {
		return false;
	}

	@Override
	public void generateCode() {
		initFiles();
		
		CodeWriter_ProxyList cw_List = getCodeWriterSet().getCodeWriter_ProxyList();
		
		CodeWriter_OneFile intWriter = getInternalFileWriter(); 
		CodeWriter_OneFile extWriter = getExternalFileWriter();
		
		//  Internal
		//  --------
		
		intWriter.addImport("b01.focGWT.client.gui.components.FwGuiTable_ForFwList");
		
		intWriter.printCore("@SuppressWarnings(\"serial\")\n");
		intWriter.printCore("public class "+intWriter.getClassName()+" extends FwGuiTable_ForFwList {\n\n");
		
		intWriter.printCore("  public "+intWriter.getClassName()+"(){\n");
		intWriter.printCore("    super();\n");
		intWriter.printCore("  }\n\n");

		intWriter.printCore("}");
		intWriter.compile();
		
		//  External
		//  --------
		
		//extWriter.addImport("b01.foc.desc.FocConstructor");
		extWriter.addImport(intWriter.getPackageName()+"."+intWriter.getClassName());
		
		extWriter.printCore("@SuppressWarnings(\"serial\")\n");
		extWriter.printCore("public class "+extWriter.getClassName()+" extends "+intWriter.getClassName()+" {\n");

		extWriter.printCore("  public "+extWriter.getClassName()+"("+cw_List.getClassName()+" list){\n");
		extWriter.printCore("    super();\n");

		FocList fieldList = getTblDef().getFieldDefinitionList();
		for(int i=0; i<fieldList.size(); i++){
			FieldDefinition fieldDefinition = (FieldDefinition) fieldList.getFocObject(i);
		
			if(			fieldDefinition.getSQLType() != FieldDefinition.SQL_TYPE_ID_LIST_FIELD
					&& 	fieldDefinition.getSQLType() != FieldDefinition.SQL_TYPE_ID_OBJECT_FIELD){
				extWriter.printCore("    addColumn(\""+fieldDefinition.getName()+"\", \""+fieldDefinition.getTitle()+"\");\n");
			}
			
		}
		
		extWriter.printCore("    setList(list);\n");
		extWriter.printCore("  }\n\n");
		
		extWriter.printCore("}");
		extWriter.compile();
		
		closeFiles();
	}
}
