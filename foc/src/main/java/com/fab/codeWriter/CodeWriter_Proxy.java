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
import com.foc.list.FocList;

public class CodeWriter_Proxy extends CodeWriter {

	public CodeWriter_Proxy(CodeWriterSet set){
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
		return CLASS_NAME_SUFFIX_PROXY;
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
		
		intWriter.addImport("b01.focGWT.client.proxy.ProxyObject");
		intWriter.addImport("java.util.ArrayList");
		//intWriter.addImport(extWriter.getPackageName()+"."+extWriter.getClassName()+CodeWriter.CLASS_NAME_SUFFIX_CONSTANT);
		
		intWriter.printCore("@SuppressWarnings(\"serial\")\n");
		intWriter.printCore("public class "+intWriter.getClassName()+" extends ProxyObject {\n\n");
		
		intWriter.addImport("b01.focGWT.client.proxy.ProxyList");
		intWriter.printCore("  public "+intWriter.getClassName()+"(ProxyList list){\n");
		intWriter.printCore("    super(list);\n");
		intWriter.printCore("  }\n\n");
		
		
//		intWriter.printCore("  public "+intWriter.getClassName()+"(){\n");
//		intWriter.printCore("  }\n\n");

		//Variable declaration
		/*
		FocList fieldList = getTblDef().getFieldDefinitionList();
		for(int i=0; i<fieldList.size(); i++){
			FieldDefinition fieldDefinition = (FieldDefinition) fieldList.getFocObject(i);
			FieldFactory.getInstance().addDeclarationInFocObjectWebClient(this, fieldDefinition);
		}

		intWriter.printCore("\n");
		*/

		//Getter Setter
		/*
		{
			//Other Getter Setter		
			for(int i=0; i<fieldList.size(); i++){
				FieldDefinition fieldDefinition = (FieldDefinition) fieldList.getFocObject(i);
				FieldFactory.getInstance().addGetterSetterInWebFocObjectClient(this, fieldDefinition);
			}
		}
		*/
		
		/*
		add_GetPropertyString();
		add_SetPropertyString();

		add_GetPropertyValue();
		add_SetPropertyValue();
		*/

		/*
		intWriter.addImport("b01.shared.json.B01JsonBuilder");
		intWriter.printCore("  @Override\n");
		intWriter.printCore("  public void toJson_Properties(B01JsonBuilder builder, ArrayList<String> propertyNamesArray){\n");
		intWriter.printCore("    super.toJson_Properties(builder, propertyNamesArray);\n");
		
		for(int i=0; i<fieldList.size(); i++){
			FieldDefinition fieldDefinition = (FieldDefinition) fieldList.getFocObject(i);
			if(fieldDefinition.getSQLType() != FieldDefinition.SQL_TYPE_ID_LIST_FIELD){
				intWriter.printCore("    if(propertyNamesArray == null || propertyNamesArray.contains(\""+fieldDefinition.getName()+"\")){\n");
				intWriter.printCore("      builder.appendKeyValue(\""+fieldDefinition.getName()+"\", getPropertyString(\""+fieldDefinition.getName()+"\"));\n");
				intWriter.printCore("    }\n");
			}
		}

		intWriter.printCore("  }\n");
		*/
		
		//End of the class
		intWriter.printCore("}\n");
		intWriter.compile();
		
		//  External
		//  --------
		
		//extWriter.addImport("b01.foc.desc.FocConstructor");
		extWriter.addImport(getClassName_Full(true));
		
		extWriter.printCore("@SuppressWarnings(\"serial\")\n");
		extWriter.printCore("public class "+extWriter.getClassName()+" extends "+intWriter.getClassName()+" {\n");
		
		extWriter.addImport("b01.focGWT.client.proxy.ProxyList");
		extWriter.printCore("  public "+extWriter.getClassName()+"(ProxyList list){\n");
		extWriter.printCore("    super(list);\n");
		extWriter.printCore("  }\n\n");
		
//		extWriter.printCore("  public "+extWriter.getClassName()+"(FocConstructor constr){\n");
//		extWriter.printCore("    super(constr);\n");
//		extWriter.printCore("  }\n");
		
		extWriter.printCore("}");
		extWriter.compile();
		
		closeFiles();
	}

	private void add_SetPropertyString(){
		CodeWriter_OneFile intWriter = getInternalFileWriter();
		FocList fieldList = getTblDef().getFieldDefinitionList();
		
		//setProperty
		intWriter.printCore("  public boolean setPropertyString(String name, String value){\n");
		intWriter.printCore("    boolean treated = super.setPropertyString(name, value);\n");
		intWriter.printCore("    if(!treated){\n");
		intWriter.printCore("      treated = true;\n");

		boolean firstCondition = true;
		for(int i=0; i<fieldList.size(); i++){
			FieldDefinition fieldDefinition = (FieldDefinition) fieldList.getFocObject(i);
			if(fieldDefinition.getSQLType() != FieldDefinition.SQL_TYPE_ID_LIST_FIELD){
				if(firstCondition){
					firstCondition = false;
					intWriter.printCore("      if(name.equals(\""+fieldDefinition.getName()+"\")){\n");	
				}else{
					intWriter.printCore("      }else if(name.equals(\""+fieldDefinition.getName()+"\")){\n");
				}
				
				String varFctName = fieldDefinition.getCW_GetterSetterMethodsPartialName();
				
				String conversionFctName = FieldFactory.getInstance().getConvertString2ValueFunction(fieldDefinition);
				
				String valueWithConversion = "value";
				if(conversionFctName != null){
					valueWithConversion = conversionFctName+"(value)";
				}
				
				//This is to be filled for object fields
				/*
      	int ref = 0;
      	ProxyObject objectValue = null;
      	if(value != null && !value.isEmpty()){
      		ref = Integer.valueOf(value);
      	}
      	ProxyDesc proxyDesc  = getProxyDesc();
      	ProxyDescField objField = proxyDesc.getFieldByName("ADR_BOOK_PARTY");
      	
      	if(objField != null && objField.getSelectionList() != null){
      		objectValue = objField.getSelectionList().findObject(ref);
      	}
      	
      	setPropertyValue("ADR_BOOK_PARTY", objectValue);
      	*/
				
				intWriter.printCore  ("        set"+varFctName+"("+valueWithConversion+");\n");
			}
		}
		
		intWriter.printCore("      }else{\n");
		intWriter.printCore("        treated = false;\n");
		intWriter.printCore("      }\n");
		intWriter.printCore("    }\n");
		intWriter.printCore("    return treated;\n");
		intWriter.printCore("  }\n\n");
	}
	
	private void add_GetPropertyString(){
		CodeWriter_OneFile intWriter = getInternalFileWriter();
		FocList fieldList = getTblDef().getFieldDefinitionList();
		
		//getProperty
		intWriter.printCore("  public String getPropertyString(String name){\n");
		intWriter.printCore("    String value = super.getPropertyString(name);\n");
		intWriter.printCore("    if(value == null){\n");

		boolean firstCondition = true;
		for(int i=0; i<fieldList.size(); i++){
			FieldDefinition fieldDefinition = (FieldDefinition) fieldList.getFocObject(i);
			if(fieldDefinition.getSQLType() != FieldDefinition.SQL_TYPE_ID_LIST_FIELD){
	
				String conditionStr = "if(name.equals(\""+fieldDefinition.getName()+"\")){\n";
				if(firstCondition){
					firstCondition = false;					
					conditionStr   = "      "+conditionStr;
				}else{
					conditionStr   = "      }else "+conditionStr;
				}
				intWriter.printCore(conditionStr);
						
				String varFctName = fieldDefinition.getCW_GetterSetterMethodsPartialName();
				String conversionFctName = FieldFactory.getInstance().getConvertValue2StringFunction(fieldDefinition);
				
				if(conversionFctName != null){
					intWriter.printCore  ("        value = "+conversionFctName+"(get"+varFctName+"());\n");
				}else{
					intWriter.printCore  ("        value = get"+varFctName+"();\n");
				}
			}
		}
		
		if(!firstCondition) intWriter.printCore  ("      }\n");
		
		intWriter.printCore("    }\n");
		intWriter.printCore("    return value;\n");
		intWriter.printCore("  }\n\n");
	}
	
	private void add_SetPropertyValue(){
		CodeWriter_OneFile intWriter = getInternalFileWriter();
		FocList fieldList = getTblDef().getFieldDefinitionList();
		
		//setProperty
		intWriter.printCore("  public boolean setPropertyValue(String name, Object value){\n");
		intWriter.printCore("    boolean treated = super.setPropertyValue(name, value);\n");
		intWriter.printCore("    if(!treated){\n");
		intWriter.printCore("      treated = true;\n");

		boolean firstCondition = true;
		for(int i=0; i<fieldList.size(); i++){
			FieldDefinition fieldDefinition = (FieldDefinition) fieldList.getFocObject(i);
			FDAbstract      fdField         = FieldFactory.getInstance().get(fieldDefinition.getSQLType());
			if(fdField != null && fieldDefinition.getSQLType() != FieldDefinition.SQL_TYPE_ID_LIST_FIELD){
				if(firstCondition){
					firstCondition = false;
					intWriter.printCore("      if(name.equals(\""+fieldDefinition.getName()+"\")){\n");	
				}else{
					intWriter.printCore("      }else if(name.equals(\""+fieldDefinition.getName()+"\")){\n");
				}
				
				String varFctName    = fieldDefinition.getCW_GetterSetterMethodsPartialName();
				String javaType      = fdField.getGWTJavaType_ObjectNotNative(fieldDefinition, false);
				String javaType_Full = fdField.getGWTJavaType_ObjectNotNative(fieldDefinition, true);
				
				if(!javaType_Full.equals(javaType)){
					intWriter.addImport(javaType_Full);
				}
				
				String valueWithConversion = "value";
				intWriter.printCore  ("        set"+varFctName+"(("+javaType+")"+valueWithConversion+");\n");
			}
		}
		
		intWriter.printCore("      }else{\n");
		intWriter.printCore("        treated = false;\n");
		intWriter.printCore("      }\n");
		intWriter.printCore("    }\n");
		intWriter.printCore("    return treated;\n");
		intWriter.printCore("  }\n\n");
	}
	
	private void add_GetPropertyValue(){
		CodeWriter_OneFile intWriter = getInternalFileWriter();
		FocList fieldList = getTblDef().getFieldDefinitionList();
		
		//getProperty
		intWriter.printCore("  public Object getPropertyValue(String name){\n");
		intWriter.printCore("    Object value = super.getPropertyValue(name);\n");
		intWriter.printCore("    if(value == null){\n");

		boolean firstCondition = true;
		for(int i=0; i<fieldList.size(); i++){
			FieldDefinition fieldDefinition = (FieldDefinition) fieldList.getFocObject(i);
			if(fieldDefinition.getSQLType() != FieldDefinition.SQL_TYPE_ID_LIST_FIELD){
	
				String conditionStr = "if(name.equals(\""+fieldDefinition.getName()+"\")){\n";
				if(firstCondition){
					firstCondition = false;					
					conditionStr   = "      "+conditionStr;
				}else{
					conditionStr   = "      }else "+conditionStr;
				}
				intWriter.printCore(conditionStr);
				
				String varFctName = fieldDefinition.getCW_GetterSetterMethodsPartialName();
				intWriter.printCore  ("        value = get"+varFctName+"();\n");
			}
		}
		
		if(!firstCondition) intWriter.printCore  ("      }\n");
		
		intWriter.printCore("    }\n");
		intWriter.printCore("    return value;\n");
		intWriter.printCore("  }\n\n");
	}
	
}
