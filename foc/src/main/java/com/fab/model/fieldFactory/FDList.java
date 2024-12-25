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

public class FDList extends FDAbstract {

	@Override
	public String getJavaType(FieldDefinition fldDef) {
		return "FocList";
	}

	@Override
	public String getGWTJavaType(FieldDefinition fldDef, boolean fullPath) {
		return fullPath ? "b01.focGWT.navigation.ProxyList" : "ProxyList";
	}
	
	@Override
	public String getGWTJavaType_ObjectNotNative(FieldDefinition fldDef, boolean fullPath) {
		return getGWTJavaType(fldDef, fullPath);
	}
	
	@Override
	public String getFocPropertyMethodPartialName(FieldDefinition fldDef) {
		return "List";
	}
	
	@Override
	public String getDefaultValue(FieldDefinition fldDef) {
		return "null";
	}
	
	@Override
	public void addFieldDeclarationInFocDesc(CodeWriter codeWriter, FieldDefinition fldDef) {
		//No declaration in FocDesc it is the FObjectField that pushes things here
		/*
		CodeWriter_OneFile intWrt = codeWriter.getInternalFileWriter();
		
		String varName = getFieldVariableName(fldDef);
		intWrt.addImport("b01.foc.desc.field.FListField");
		intWrt.addImport("b01.foc.list.FocLinkForeignKey");
		
		//intWrt.addImport(fldDef.getTableDefinition_ForTargetObject().getCW_PackageName_Server()+"."+fldDef.getTableDefinition_ForTargetObject().getCW_ClassName_FocDesc());
		TableDefinition slaveTableDef = fldDef.getCW_SlaveTableDefinition();
		int fieldIDInSlavePointingToMaster = fldDef.getUniqueForeignKey();
		FieldDefinition fieldInSlavePointingToMaster = slaveTableDef.getFieldDefinitionById(fieldIDInSlavePointingToMaster);
		
		String focLinkVarName = varName+"_Link";
		
		intWrt.addImport(slaveTableDef.getCW_PackageName_Server()+"."+slaveTableDef.getCW_ClassName_FocDesc());
		
		intWrt.printCore("    FocLinkForeignKey "+focLinkVarName+" = new FocLinkForeignKey("+slaveTableDef.getCW_ClassName_FocDesc()+".getInstance(), "+slaveTableDef.getCW_ClassName_FocDesc()+"."+ fieldInSlavePointingToMaster.getCW_FieldConstanteName()+", true);\n");
		intWrt.printCore("    FListField "+varName+" = new FListField(\""+fldDef.getName()+"\", \""+fldDef.getTitle()+"\", "+fldDef.getCW_FieldConstanteName()+", "+focLinkVarName+ ", null);\n");    	
		addFieldDeclarationInFocDesc_Common(codeWriter, fldDef, varName);
		*/
	}

	@Override
	public void addDeclarationInFocObjectWebClient(CodeWriter codeWriter, FieldDefinition fldDef) {
		/*
		CodeWriter_OneFile intWrt = codeWriter.getInternalFileWriter();

		String varName = fldDef.getCW_VariableName();
		TableDefinition targetTableDef = fldDef.getTableDefinition_ForTargetObject();
		
		intWrt.addImport(targetTableDef.getCW_PackageName_Client()+"."+targetTableDef.getCW_ClassName_GwfObject());
		intWrt.printCore("  private "+targetTableDef.getCW_ClassName_GwfObject()+" "+varName+" = null;\n");
		intWrt.printCore("  private int "+varName+"Ref = 0;\n");
		*/
	}
	
	@Override
	public void addGetterSetterInWebFocObjectClient(CodeWriter codeWriter, FieldDefinition fldDef) {
		/*
		CodeWriter_OneFile intWrt = codeWriter.getInternalFileWriter();

		String varFctName = fldDef.getCW_GetterSetterMethodsPartialName();
		String varName    = fldDef.getCW_VariableName();
		TableDefinition targetTableDef = fldDef.getTableDefinition_ForTargetObject();
		
		intWrt.printCore("  public "+targetTableDef.getCW_ClassName_GwfObject()+" get"+varFctName+"(){\n");
		intWrt.printCore("    return "+varName+";\n");
		intWrt.printCore("  }\n\n");
		
		intWrt.printCore("  public void set"+varFctName+"("+targetTableDef.getCW_ClassName_GwfObject()+" "+varName+"){\n");
		intWrt.printCore("    this." + varName + " = "+varName+";\n");
		intWrt.printCore("  }\n\n");
		
		intWrt.printCore("  public int get"+varFctName+"Ref(){\n");
		intWrt.printCore("    return "+varName+"Ref;\n");
		intWrt.printCore("  }\n\n");
		
		intWrt.printCore("  public void set"+varFctName+"Ref(int "+varName+"Ref){\n");
		intWrt.printCore("    this." + varName + "Ref = "+varName+"Ref;\n");
		intWrt.printCore("  }\n\n");
		*/
	}

	@Override
	public void addCopyFromFocObjectToCltObject(CodeWriter_OneFile intWrt, FieldDefinition fldDef, String indentation){
//		String methodPartialName = getGetterSetterMethodsPartialName(fldDef);
//		intWrt.printCore(indentation+"clt.set"+methodPartialName+"Ref(focObj.get"+methodPartialName+"().getReference().getInteger());\n");
	}
	
	@Override
	public void addCopyFromCltObjectToFocObject(CodeWriter_OneFile intWrt, FieldDefinition fldDef, String indentation){
//		String methodPartialName = getGetterSetterMethodsPartialName(fldDef);
//		intWrt.printCore(indentation+"focObj.set"+methodPartialName+"Ref(clt.get"+methodPartialName+"Ref());\n");
	}
	
	@Override
	public void addGetterSetterInFocObject(CodeWriter codeWriter, FieldDefinition fldDef) {
		//super.addGetterSetterInFocObject(codeWriter, fldDef);
		
		CodeWriter_OneFile intWrt = codeWriter.getInternalFileWriter();
		
		String varName = getGetterSetterMethodsPartialName(fldDef);
		
		intWrt.addImport("b01.foc.list.FocList");
		
		intWrt.printCore("  public FocList get"+varName+"(){\n");
		intWrt.printCore("    FocList focList = (FocList) getPropertyList("+fldDef.getCW_FieldConstanteName()+");\n");
		intWrt.printCore("    return focList;\n");
		intWrt.printCore("  }\n\n");
	}
}
