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

import com.fab.model.fieldFactory.FDAbstract;
import com.fab.model.fieldFactory.FieldFactory;
import com.fab.model.table.FieldDefinition;
import com.foc.Globals;
import com.foc.list.FocList;

public class CodeWriter_GuiProxyPanel extends CodeWriter {

	public CodeWriter_GuiProxyPanel(CodeWriterSet set){
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
		return CLASS_NAME_SUFFIX_DETAIL_PANEL;
	}

	@Override
	public boolean isServerSide() {
		return false;
	}

	@Override
	public void generateCode() {
		initFiles();
		
		CodeWriter_Proxy             cw_Proxy     = getCodeWriterSet().getCodeWriter_Proxy();
		CodeWriter_GuiProxyListPanel cw_ListPanel = getCodeWriterSet().getCodeWriter_GuiProxyListPanel();
		
		CodeWriter_OneFile intWriter = getInternalFileWriter(); 
		CodeWriter_OneFile extWriter = getExternalFileWriter();
		
		//  Internal
		//  --------
		
		intWriter.addImport("b01.focGWT.client.gui.components.FwGuiDetailsPanel");
		intWriter.addImport("b01.focGWT.client.gui.components.FwButton");
		intWriter.addImport("b01.focGWT.client.gui.components.GwfDialog_Wait");
		intWriter.addImport("b01.focGWT.client.dataStore.FDataRequestList_Client");
		intWriter.addImport("b01.focGWT.client.dataStore.IDataRequestCallBack");
		intWriter.addImport("b01.focGWT.client.application.CltApp");
		intWriter.addImport("b01.focGWT.client.gui.propertyComponents.FwPropertyGuiComponent");
		
		intWriter.addImport("com.google.gwt.event.dom.client.ClickHandler");
		intWriter.addImport("com.google.gwt.event.dom.client.ClickEvent");
		intWriter.addImport(cw_Proxy.getClassName_Full(false));
		intWriter.addImport(cw_ListPanel.getClassName_Full(false));
		
		intWriter.printCore("public class "+intWriter.getClassName()+" extends FwGuiDetailsPanel {\n\n");
		
//		intWriter.printCore("  private "+cw_ListPanel.getClassName()+" listPanel = null;\n"); 
//		intWriter.printCore("  private "+cw_Proxy.getClassName()+" obj = null;\n\n");

//		intWriter.addImport("com.google.gwt.dev.util.collect.HashMap");
//		intWriter.addImport("b01.focGWT.client.gui.propertyComponents.FwPropertyGuiComponent");
//		intWriter.printCore("  HashMap<String, FwPropertyGuiComponent> guiComponentsMap = null;\n");
		
//		StringBuffer disposePart = new StringBuffer();
		
		FocList fieldList = getTblDef().getFieldDefinitionList();
		/*
		for(int i=0; i<fieldList.size(); i++){
			FieldDefinition fieldDefinition = (FieldDefinition) fieldList.getFocObject(i);
			FDAbstract fieldType = FieldFactory.getInstance().get(fieldDefinition.getSQLType());
			String     clsName   = fieldType.getClassFor_GuiComponent_Details();
			if(clsName != null){
				intWriter.addImport("b01.focGWT.client.gui.propertyComponents."+clsName);				
				intWriter.printCore("  private "+clsName+" "+getVariableName_Widget(fieldDefinition)+" = null;\n");
				disposePart.append("    "+getVariableName_Widget(fieldDefinition)+" = null;\n");
			}
		}
		intWriter.printCore("\n");
		*/
		
		intWriter.printCore("  public "+getClassName(true)+"("+cw_ListPanel.getClassName()+" listPanel, "+cw_Proxy.getClassName()+" obj){\n");
		intWriter.printCore("    super(listPanel, obj);\n");
		intWriter.printCore("  }\n\n");

		/*
		intWriter.printCore("  public FwPropertyGuiComponent newPropertyGuiComponent(String propertyName){\n");
		intWriter.printCore("    FwPropertyGuiComponent guiComponent = null;\n");
		intWriter.printCore("    if(propertyName != null){\n");
		
		boolean firstIf = true;
		for(int i=0; i<fieldList.size(); i++){
			FieldDefinition fieldDefinition = (FieldDefinition) fieldList.getFocObject(i);
			FDAbstract      fieldType       = FieldFactory.getInstance().get(fieldDefinition.getSQLType());
			if(fieldType != null){			
				String          clsName         = fieldType.getClassFor_GuiComponent_Details();
	
				if(clsName == null){
					String message = "No Gui Component Defined for :"+fieldDefinition.getName();
					if(Globals.getDisplayManager() != null){
						Globals.getDisplayManager().popupMessage(message);
					}
					Globals.logString(message);
				}else{
					String ifString = "      if";
					if(firstIf){
						firstIf = false;
					}else{
						ifString = "      }else if";
					}
	
					intWriter.printCore(ifString+"(propertyName.equals(\""+fieldDefinition.getName()+"\")){\n");
					intWriter.printCore("        guiComponent = new "+clsName+"(getProxyObject(), \""+fieldDefinition.getName()+"\");\n");
				}
			}
		}
		if(!firstIf){
			intWriter.printCore("      }\n");
		}
		intWriter.printCore("    }\n");
		intWriter.printCore("    return guiComponent;\n");
		intWriter.printCore("  }\n\n");
		*/
		/*
		intWriter.printCore("  public void dispose(){\n");
		intWriter.printCore("    super.dispose();\n");
		intWriter.printCore("    obj = null;\n");
		intWriter.printCore("    listPanel = null;\n");

		intWriter.printCore("    if(guiComponentsArray != null){\n" +
											  "      Iterator<FwPropertyGuiComponent> iter = guiComponentsMap.values().iterator();\n" +
											  "      while(iter != null && iter.hasNext()){\n" +
											  "        FwPropertyGuiComponent propGuiComp = iter.next();\n" +
											  "        if(propGuiComp != null){\n" +
											  "          propGuiComp.dispose();\n" +
											  "        }\n"+
											  "      }\n"+
											  "      guiComponentsMap.clear();"+
											  "      guiComponentsMap = null;"+
											  "    }\n");
		//intWriter.printCore(disposePart.toString());
		intWriter.printCore("  }\n\n");
		
		intWriter.printCore("  public "+cw_Proxy.getClassName()+" getProxyObject(){\n");
		intWriter.printCore("    return obj;\n");
		intWriter.printCore("  }\n");
		*/
		//Get Widgets
		for(int i=0; i<fieldList.size(); i++){
			FieldDefinition fieldDefinition = (FieldDefinition) fieldList.getFocObject(i);
			FDAbstract fieldType = FieldFactory.getInstance().get(fieldDefinition.getSQLType());
			if(fieldType != null){
				//String widgetVarName = getVariableName_Widget(fieldDefinition);
				String clsName       = fieldType.getClassFor_GuiComponent_Details();
	
				if(clsName == null){
					String message = "No Gui Component Defined for :"+fieldDefinition.getName();
					if(Globals.getDisplayManager() != null){
						Globals.getDisplayManager().popupMessage(message);
					}
					Globals.logString(message);
				}else{
					intWriter.addImport("b01.focGWT.client.gui.propertyComponents."+clsName);
					
					intWriter.printCore("  public "+clsName+" getWidget_"+fieldDefinition.getName()+"(){\n");
					intWriter.printCore("    return ("+clsName+") getPropertyGuiComponent(\""+fieldDefinition.getName()+"\", true);\n");
					intWriter.printCore("  }\n\n");
				}
			}
		}

		/*
		intWriter.printCore("  public FwButton newSaveButton(){\n");
		intWriter.printCore("    FwButton save_Button = new FwButton(\"Confirm\");\n");
		intWriter.printCore("    save_Button.addClickHandler(new ClickHandler() {\n");
		intWriter.printCore("      public void onClick(ClickEvent event) {\n");
		
		//Get Widgets
		for(int i=0; i<fieldList.size(); i++){
			FieldDefinition fieldDefinition = (FieldDefinition) fieldList.getFocObject(i);
			String widgetVarName = getVariableName_Widget(fieldDefinition);
			
			FDAbstract fieldType = FieldFactory.getInstance().get(fieldDefinition.getSQLType()); 
			String clsName       = fieldType.getClassFor_GuiComponent_Details();
			if(clsName != null){
				intWriter.printCore("        if("+widgetVarName+" != null){\n");
				intWriter.printCore("          "+widgetVarName+".copyGui2Memory();\n");
				intWriter.printCore("        }\n");
			}
		}
						
		intWriter.printCore("        \n");
		
		intWriter.printCore("        if(obj.isInsert() || obj.isUpdate()){\n");
		intWriter.printCore("          GwfDialog_Wait.getWaitDialog(\"Wait\", \"Waiting...\").popup();\n");
		intWriter.printCore("          FDataRequestList_Client reqList = new FDataRequestList_Client();\n");
		intWriter.printCore("          reqList.newDataRequest_InsertDeleteUpdate(obj.getList().getDataKey(), obj);\n");
		intWriter.printCore("          reqList.send(null, new IDataRequestCallBack() {\n");
		intWriter.printCore("            @Override\n");					
		intWriter.printCore("            public void dispose() {\n");
		intWriter.printCore("            }\n");
		intWriter.printCore("            @Override\n");					
		intWriter.printCore("            public void dataReceived(Object contextualObject, FDataRequestList_Client dataRequestList) {\n");					
		intWriter.printCore("              listPanel.refreshGui();\n");					
		intWriter.printCore("              GwfDialog_Wait.getWaitDialog().hide();\n");
		intWriter.printCore("              CltApp.getInstance().getMainSequence().back();\n");
		intWriter.printCore("            }\n");
		intWriter.printCore("          });\n");
		intWriter.printCore("        }\n");
		intWriter.printCore("      }\n");
		intWriter.printCore("    });\n");
			
		intWriter.printCore("    return save_Button;\n");
		intWriter.printCore("  }\n\n");
		
		intWriter.printCore("  public FwButton newCancelButton(){\n");
		intWriter.printCore("    FwButton cancel_Button = new FwButton(\"Cancel\");\n");
		intWriter.printCore("    cancel_Button.addClickHandler(new ClickHandler(){\n");
		intWriter.printCore("      public void onClick(ClickEvent event){\n");
		intWriter.printCore("      }\n");
		intWriter.printCore("    });\n");
		intWriter.printCore("    return cancel_Button;\n");
		intWriter.printCore("  }\n\n");
		*/
		
		intWriter.printCore("}");
		intWriter.compile();
		
		//  External
		//  --------
		
		//extWriter.addImport("b01.foc.desc.FocConstructor");
		extWriter.addImport(intWriter.getPackageName()+"."+intWriter.getClassName());
		extWriter.addImport("b01.focGWT.client.gui.components.FwButton");
		extWriter.addImport("b01.focGWT.client.gui.components.GwfPanel");
		extWriter.addImport("com.google.gwt.user.client.ui.Label");
		extWriter.addImport("com.google.gwt.user.client.ui.VerticalPanel");
		
		extWriter.printCore("public class "+extWriter.getClassName()+" extends "+intWriter.getClassName()+" {\n");
		
		extWriter.printCore("  public "+extWriter.getClassName()+"("+cw_ListPanel.getClassName()+" listPanel, "+cw_Proxy.getClassName()+" fwObj){\n");
		extWriter.printCore("    super(listPanel, fwObj);\n");
		extWriter.printCore("    fill();\n");
		extWriter.printCore("  }\n\n");
		
		extWriter.printCore("  private void fill() {\n");
		extWriter.printCore("    VerticalPanel panel_withValidation = new VerticalPanel();\n");
		extWriter.printCore("    add(panel_withValidation, 0, 0, 300, 300);\n\n");
		
		extWriter.printCore("    //Validation Panel\n");
		extWriter.printCore("    GwfPanel validationPanel = new GwfPanel();\n");
		extWriter.printCore("    validationPanel.setPixelSize(500, 50);\n");
		extWriter.printCore("    panel_withValidation.add(validationPanel);\n\n");
		
		for(int i=0; i<fieldList.size(); i++){
			FieldDefinition fieldDefinition = (FieldDefinition) fieldList.getFocObject(i);
			FDAbstract fieldType = FieldFactory.getInstance().get(fieldDefinition.getSQLType());
			if(fieldType != null){
				String     clsName   = fieldType.getClassFor_GuiComponent_Details();
				if(clsName != null){
					extWriter.printCore("    panel_withValidation.add(new Label(\""+fieldDefinition.getTitle()+"\"));\n");
					extWriter.printCore("    panel_withValidation.add(getWidget_"+fieldDefinition.getName()+"().getWidget());\n\n");
				}
			}
		}
		
		extWriter.printCore("    FwButton save_Button = newSaveButton();\n");
		extWriter.printCore("    validationPanel.add(save_Button, 5, 5, 77, 23);\n");

		extWriter.printCore("\n");
		
		extWriter.printCore("    FwButton cancel_Button = newCancelButton();\n");
		extWriter.printCore("    validationPanel.add(cancel_Button, 100, 5, 77, 23);\n");
		extWriter.printCore("  }\n");
		
		extWriter.printCore("}");
		extWriter.compile();
		
		closeFiles();
	}

	public String getVariableName_Widget(FieldDefinition fieldDefinition){
		return fieldDefinition.getCW_VariableName()+"_widget";	
	}

}
