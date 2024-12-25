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
import com.fab.model.table.FieldDefinition;
import com.foc.util.Utils;

public abstract class FDAbstract implements IFieldType {

	public String getVariableName(FieldDefinition fldDef){
		return fldDef.getCW_VariableName();
	}
	
	public String getGetterSetterMethodsPartialName(FieldDefinition fldDef){
		return fldDef.getCW_GetterSetterMethodsPartialName();
	}

	protected String getFieldVariableName(FieldDefinition fldDef){
		return fldDef.getCW_FieldVariableName();
	}

	protected void addFieldDeclarationInFocDesc_Common(CodeWriter codeWriter, FieldDefinition fldDef, String varName){
		CodeWriter_OneFile intWrt = codeWriter.getInternalFileWriter();
		if(!fldDef.isDbResident()){
			intWrt.printCore("    "+varName+".setDBResident(false);\n");	
		}
		intWrt.printCore("    addField("+varName+");\n\n");
	}

	@Override
	public void addGetterSetterInFocObject(CodeWriter codeWriter, FieldDefinition fldDef) {
		CodeWriter_OneFile intWrt = codeWriter.getInternalFileWriter();
		
		String varName = getGetterSetterMethodsPartialName(fldDef);
		
		intWrt.printCore("  public "+getJavaType(fldDef)+" get"+varName+"(){\n");
		intWrt.printCore("    return ("+getJavaType(fldDef)+") getProperty"+getFocPropertyMethodPartialName(fldDef)+"("+fldDef.getCW_FieldConstanteName()+");\n");
		intWrt.printCore("  }\n\n");
		
		intWrt.printCore("  public void set"+varName+"("+getJavaType(fldDef)+" obj){\n");
		intWrt.printCore("    setProperty"+getFocPropertyMethodPartialName(fldDef)+"("+fldDef.getCW_FieldConstanteName()+", obj);\n");
		intWrt.printCore("  }\n\n");
	}
	
	@Override
	public void addFieldDeclarationInPojo(CodeWriter codeWriter, FieldDefinition fldDef) {
		CodeWriter_OneFile intWrt = codeWriter.getInternalFileWriter();
		
		String fName = "FNAME_"+fldDef.getName();
		
		String attributes = "";
		if(fldDef.getLength() > 0){
			if(attributes.length() > 0) attributes = ", "+attributes;
			attributes = "size="+fldDef.getLength();
		}
		if(fldDef.isMandatory()){
			if(attributes.length() > 0) attributes = ", "+attributes;
			attributes = "mandatory="+fldDef.isMandatory();
		}
		if(fldDef.getDecimals() > 0){
			if(attributes.length() > 0) attributes = ", "+attributes;
			attributes = "decimal="+fldDef.getDecimals();
		}		
    if(!Utils.isStringEmpty(fldDef.getFocDescName())){
			if(attributes.length() > 0) attributes = ", "+attributes;
			attributes = "table="+fldDef.getFocDescName();
		}		
    String typeName = getFocPropertyMethodPartialName(fldDef);
    if(!typeName.startsWith("Foc")) typeName = "Foc" + typeName;  
		intWrt.printCore("  @"+typeName+"("+attributes+")\n");
		intWrt.printCore("  public static final String "+fName+" = \""+fldDef.getName()+"\";\n");    
	}
	
	@Override
	public void addGetterSetterInPojo(CodeWriter codeWriter, FieldDefinition fldDef){
		CodeWriter_OneFile intWrt = codeWriter.getInternalFileWriter();
		
		String fName = "FNAME_"+fldDef.getName();
		
		String varName = fldDef.getName();//getGetterSetterMethodsPartialName(fldDef);
		
		intWrt.printCore("  public "+getJavaType(fldDef)+" get"+varName+"(){\n");
		intWrt.printCore("    return ("+getJavaType(fldDef)+") getProperty"+getFocPropertyMethodPartialName(fldDef)+"("+fName+");\n");
		intWrt.printCore("  }\n\n");
		
		intWrt.printCore("  public void set"+varName+"("+getJavaType(fldDef)+" value){\n");
		intWrt.printCore("    setProperty"+getFocPropertyMethodPartialName(fldDef)+"("+fName+", value);\n");
		intWrt.printCore("  }\n\n");

//		addGetterSetterInFocObject(codeWriter, fldDef);
	}
	
	public void addDeclarationInFocObjectWebClient(CodeWriter codeWriter, FieldDefinition fldDef) {
		CodeWriter_OneFile intWrt = codeWriter.getInternalFileWriter();

		String varName = getVariableName(fldDef);

		intWrt.printCore("  private "+getJavaType(fldDef)+" "+varName+" = "+getDefaultValue(fldDef)+";\n");
	}

	public void addGetterSetterInWebFocObjectClient(CodeWriter codeWriter, FieldDefinition fldDef) {
		CodeWriter_OneFile intWrt = codeWriter.getInternalFileWriter();

		String varFctName = getGetterSetterMethodsPartialName(fldDef);
		String varName    = getVariableName(fldDef);
		
		intWrt.printCore("  public "+getJavaType(fldDef)+" get"+varFctName+"(){\n");
		intWrt.printCore("    return "+varName+";\n");
		intWrt.printCore("  }\n\n");
		
		intWrt.printCore("  public void set"+varFctName+"("+getJavaType(fldDef)+" "+varName+"){\n");
		intWrt.printCore("    this." + varName + " = "+varName+";\n");
		intWrt.printCore("  }\n\n");
	}
	
	public void addCopyFromFocObjectToCltObject(CodeWriter_OneFile intWrt, FieldDefinition fldDef, String indentation){
		String          methodPartialName = getGetterSetterMethodsPartialName(fldDef);
		intWrt.printCore(indentation+"clt.set"+methodPartialName+"(focObj.get"+methodPartialName+"());\n");
	}
	
	public void addCopyFromCltObjectToFocObject(CodeWriter_OneFile intWrt, FieldDefinition fldDef, String indentation){
		String          methodPartialName = getGetterSetterMethodsPartialName(fldDef);
		intWrt.printCore(indentation+"focObj.set"+methodPartialName+"(clt.get"+methodPartialName+"());\n");
	}
	
	public String getConvertString2ValueFunction(){
		return null;
	}
	
	public String getConvertValue2StringFunction(){
		return null;
	}
	
	public String getClassFor_GuiComponent_Details(){
		return null;
	}
	
}
