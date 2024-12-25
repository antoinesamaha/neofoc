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

public class CodeWriter_GuiProxyListPanel extends CodeWriter {

	public CodeWriter_GuiProxyListPanel(CodeWriterSet set){
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
		return CLASS_NAME_SUFFIX_PROXY_LIST_PANEL;
	}

	@Override
	public boolean isServerSide() {
		return false;
	}

	@Override
	public void generateCode() {
		initFiles();

		CodeWriter_Proxy             cw_Proxy           = getCodeWriterSet().getCodeWriter_Proxy();
		CodeWriter_ProxyList         cw_List            = getCodeWriterSet().getCodeWriter_ProxyList();
		CodeWriter_GuiProxyPanel     cw_GuiDetailsPanel = getCodeWriterSet().getCodeWriter_GuiProxyPanel();
		
		CodeWriter_OneFile intWriter = getInternalFileWriter(); 
		CodeWriter_OneFile extWriter = getExternalFileWriter();
		
		//  Internal
		//  --------
		
		intWriter.addImport(cw_List.getClassName_Full(false));
		intWriter.addImport(cw_Proxy.getClassName_Full(false));
		intWriter.addImport(cw_GuiDetailsPanel.getClassName_Full(false));
		intWriter.addImport(getClassName_Full(false));

		intWriter.addImport("b01.focGWT.client.proxy.ProxyObject");
		intWriter.addImport("b01.focGWT.client.proxy.ProxyList");
		intWriter.addImport("b01.focGWT.client.gui.components.GwfPanel");
		intWriter.addImport("b01.focGWT.client.gui.components.FwGuiListPanel");
		intWriter.addImport("b01.focGWT.client.gui.components.FwGuiTable_ForFwList");
		
		intWriter.addImport(cw_Proxy.getClassName_Full(false));
		
		intWriter.printCore("public class "+getClassName(true)+" extends FwGuiListPanel {\n\n");
		
		intWriter.printCore("  public " +getClassName(true)+ "("+cw_List.getClassName()+" list){\n");
		intWriter.printCore("    super(list);\n");
		intWriter.printCore("  }\n\n");
		
		intWriter.printCore("  @Override\n");
		intWriter.printCore("  public GwfPanel newDetailsPanel(ProxyObject object){\n");
		intWriter.printCore("    return new "+cw_GuiDetailsPanel.getClassName()+"(("+getClassName()+")"+getClassName(true)+".this, ("+cw_Proxy.getClassName()+")object);\n");
		intWriter.printCore("  }\n\n");

		intWriter.printCore("  @Override\n");
		intWriter.printCore("  public FwGuiTable_ForFwList newFwGuiTable(ProxyList list) {\n");
		intWriter.printCore("    FwGuiTable_ForFwList table = new FwGuiTable_ForFwList();\n");
		
		FocList fieldList = getTblDef().getFieldDefinitionList();
		for(int i=0; i<fieldList.size(); i++){
			FieldDefinition fieldDefinition = (FieldDefinition) fieldList.getFocObject(i);
		
			if(			fieldDefinition.getSQLType() != FieldDefinition.SQL_TYPE_ID_LIST_FIELD
					&& 	fieldDefinition.getSQLType() != FieldDefinition.SQL_TYPE_ID_OBJECT_FIELD){
				intWriter.printCore("    table.addColumn(\""+fieldDefinition.getName()+"\", \""+fieldDefinition.getTitle()+"\");\n");
			}
		}
		
		intWriter.printCore("    table.setList(list);\n");
		intWriter.printCore("    return table;\n");
		intWriter.printCore("  }\n");
		
		
		/*
		intWriter.printCore("    Button btnNewButton = new Button(\"Add\");\n");
		intWriter.printCore("    btnNewButton.addClickHandler(new ClickHandler() {\n");
		intWriter.printCore("      public void onClick(ClickEvent event) {\n");

		intWriter.printCore("        "+cw_Proxy.getClassName()+" obj = getList().addObject(0);\n");
		intWriter.printCore("        obj.setInsert();\n");
		intWriter.printCore("        getWebSequence().next(new "+cw_GuiDetailsPanel.getClassName()+"(("+getClassName()+")"+getClassName(true)+".this, obj));\n");
		
		intWriter.printCore("      }\n");
	  intWriter.printCore("    });\n");
	  intWriter.printCore("    add(btnNewButton, 10, 20, 78, 24);\n\n");

		intWriter.printCore("    Button btnDelButton = new Button(\"Del\");\n");
		intWriter.printCore("    btnDelButton.addClickHandler(new ClickHandler() {\n");
		intWriter.printCore("      public void onClick(ClickEvent event) {\n");
		intWriter.printCore("      }\n");
	  intWriter.printCore("    });\n");
	  intWriter.printCore("    add(btnDelButton, 110, 20, 78, 24);\n\n");

		intWriter.printCore("    Button btnEditButton = new Button(\"Edit\");\n");
		intWriter.printCore("    btnEditButton.addClickHandler(new ClickHandler() {\n");
		intWriter.printCore("      public void onClick(ClickEvent event) {\n");
		intWriter.printCore("      }\n");
	  intWriter.printCore("    });\n");
	  intWriter.printCore("    add(btnEditButton, 210, 20, 78, 24);\n\n");
	  
	  intWriter.printCore("    guiTable = new "+cw_GuiTable.getClassName()+"(list);\n");
	  intWriter.printCore("    add(guiTable, 10, 40, 1100, 1100);\n\n");
	  
		intWriter.printCore("  }\n\n");
		
		intWriter.printCore("  public "+cw_List.getClassName()+" getList() {\n");
		intWriter.printCore("    return list;\n");
		intWriter.printCore("  }\n\n");

		intWriter.printCore("  public void setList("+cw_List.getClassName()+" list) {\n");
		intWriter.printCore("    this.list = list;\n");
		intWriter.printCore("  }\n\n");

		intWriter.printCore("  public void refreshGui(){\n");
		intWriter.printCore("    if(guiTable != null){\n");
		intWriter.printCore("      guiTable.refreshGui();\n");
		intWriter.printCore("    }\n");
		intWriter.printCore("  }\n\n");
		*/
		
		intWriter.printCore("}");
		intWriter.compile();
		
		//  External
		//  --------
		
		//extWriter.addImport("b01.foc.desc.FocConstructor");
		extWriter.addImport(getClassName_Full(true));
		
		extWriter.printCore("public class "+getClassName(false)+" extends "+getClassName(true)+" {\n\n");
		
		extWriter.printCore("  public " + getClassName(false)+"("+cw_List.getClassName()+" list){\n");
		extWriter.printCore("    super(list);\n");
		extWriter.printCore("  }\n\n");
		
		extWriter.printCore("}");
		extWriter.compile();
		
		closeFiles();
	}
}
