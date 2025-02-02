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
import com.fab.codeWriter.CodeWriterSet;
import com.fab.codeWriter.CodeWriter_OneFile;
import com.fab.model.table.FieldDefinition;
import com.fab.model.table.TableDefinition;
import com.foc.util.Utils;

public class FDObject extends FDAbstract {

	@Override
	public String getJavaType(FieldDefinition fldDef) {
		return "FocObject";
	}

	@Override
	public String getGWTJavaType(FieldDefinition fldDef, boolean fullPath) {
		TableDefinition targetTableDef = fldDef.getTableDefinition_ForTargetObject();
		CodeWriterSet cwSet_Target = new CodeWriterSet(targetTableDef);
		if(fullPath){
			return cwSet_Target.getCodeWriter_Proxy().getClassName_Full(false);
		}else{
			return cwSet_Target.getCodeWriter_Proxy().getClassName(false);
		}
	}
	
	@Override
	public String getGWTJavaType_ObjectNotNative(FieldDefinition fldDef, boolean fullPath) {
		return getGWTJavaType(fldDef, fullPath);
	}
	
	@Override
	public String getFocPropertyMethodPartialName(FieldDefinition fldDef) {
		return "Object";
	}
	
	@Override
	public String getDefaultValue(FieldDefinition fldDef) {
		return "null";
	}
	
	@Override
	public void addFieldDeclarationInFocDesc(CodeWriter codeWriter, FieldDefinition fldDef) {
		CodeWriter_OneFile intWriter = codeWriter.getInternalFileWriter();
		//CodeWriter_OneFile extWriter = codeWriter.getExternalFileWriter();
		
		String varName = getFieldVariableName(fldDef);
		intWriter.addImport("b01.foc.desc.field.FObjectField");
		
		TableDefinition master_TableDefinition = fldDef.getTableDefinition_ForTargetObject();
		
		if(master_TableDefinition != null){//This writes the Import
			CodeWriterSet cwSet = new CodeWriterSet(master_TableDefinition);
			String focDescClassName = cwSet.getCodeWriter_FocDesc().getClassName_Full(false);
			//intWrt.addImport(master_TableDefinition.getCW_PackageName_Server()+"."+fldDef.getTableDefinition_ForTargetObject().getCW_ClassName_FocDesc());
			//intWriter.addImport(extWriter.getPackageName()+"."+fldDef.getTableDefinition_ForTargetObject().getCW_ClassName_FocDesc());
			intWriter.addImport(focDescClassName);
		}
		
		FieldDefinition master_ListFieldDefinition = null; 
		int master_ListFieldID = fldDef.getListFieldInMasterId();
		if(master_ListFieldID > 0 && master_TableDefinition != null){
			master_ListFieldDefinition = master_TableDefinition.getFieldDefinitionById(master_ListFieldID);
		}
		if(master_ListFieldDefinition != null){
			intWriter.printCore("    FObjectField "+varName+" = new FObjectField(\""+fldDef.getName()+"\", \""+fldDef.getTitle()+"\", "+CodeWriter.FLD_CONSTANT_PREFIX+fldDef.getName()+ ", " + master_TableDefinition.getCW_ClassName_FocDesc() +".getInstance()" +", this, "+master_TableDefinition.getCW_ClassName_FocDesc()+"."+master_ListFieldDefinition.getCW_FieldConstanteName()+");\n");
		}else{
			if(master_TableDefinition == null){
				String masterDescClassName = fldDef.getFocDescName()+"Desc";
				intWriter.printCore("    FObjectField "+varName+" = new FObjectField(\""+fldDef.getName()+"\", \""+fldDef.getTitle()+"\", "+fldDef.getCW_FieldConstanteName()+", "+ masterDescClassName +".getInstance()" +");\n");
			}else{
				intWriter.printCore("    FObjectField "+varName+" = new FObjectField(\""+fldDef.getName()+"\", \""+fldDef.getTitle()+"\", "+fldDef.getCW_FieldConstanteName()+", "+ master_TableDefinition.getCW_ClassName_FocDesc() +".getInstance()" +");\n");
			}
		}
		if(!Utils.isStringEmpty(fldDef.getForcedDBName())){
			intWriter.printCore("    "+varName+".setForcedDBName(\""+fldDef.getForcedDBName()+"\");\n");
		}
		intWriter.printCore("    "+varName+".setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);\n");
		addFieldDeclarationInFocDesc_Common(codeWriter, fldDef, varName);
	}

	@Override
	public void addDeclarationInFocObjectWebClient(CodeWriter codeWriter, FieldDefinition fldDef) {
		CodeWriter_OneFile intWrt = codeWriter.getInternalFileWriter();

		String varName = fldDef.getCW_VariableName();
		TableDefinition targetTableDef = fldDef.getTableDefinition_ForTargetObject();
		
		CodeWriterSet cwSet_Target = new CodeWriterSet(targetTableDef);
		intWrt.addImport(cwSet_Target.getCodeWriter_Proxy().getClassName_Full(false));
		intWrt.printCore("  private "+cwSet_Target.getCodeWriter_Proxy().getClassName(false)+" "+varName+" = null;\n");
		//intWrt.printCore("  private int "+varName+"Ref = 0;\n");
	}
	
	@Override
	public void addGetterSetterInWebFocObjectClient(CodeWriter codeWriter, FieldDefinition fldDef) {
		CodeWriter_OneFile intWrt = codeWriter.getInternalFileWriter();

		String varFctName = fldDef.getCW_GetterSetterMethodsPartialName();
		String varName    = fldDef.getCW_VariableName();
		TableDefinition targetTableDef = fldDef.getTableDefinition_ForTargetObject();
		
		CodeWriterSet cwSet_Target = new CodeWriterSet(targetTableDef);
		intWrt.addImport(cwSet_Target.getCodeWriter_Proxy().getClassName_Full(false));
		
		intWrt.printCore("  public "+cwSet_Target.getCodeWriter_Proxy().getClassName(false)+" get"+varFctName+"(){\n");
		intWrt.printCore("    return "+varName+";\n");
		intWrt.printCore("  }\n\n");
		
		intWrt.printCore("  public void set"+varFctName+"("+cwSet_Target.getCodeWriter_Proxy().getClassName(false)+" "+varName+"){\n");
		intWrt.printCore("    this." + varName + " = "+varName+";\n");
		intWrt.printCore("  }\n\n");
		
		/*
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
		String methodPartialName = getGetterSetterMethodsPartialName(fldDef);
		String srcFocObjXpr = "focObj.get"+methodPartialName+"()";
		intWrt.printCore(indentation+"clt.set"+methodPartialName+"Ref("+srcFocObjXpr+" != null ? "+srcFocObjXpr+".getReference().getInteger():0);\n");
	}
	
	@Override
	public void addCopyFromCltObjectToFocObject(CodeWriter_OneFile intWrt, FieldDefinition fldDef, String indentation){
		String methodPartialName = getGetterSetterMethodsPartialName(fldDef);
		intWrt.printCore(indentation+"focObj.set"+methodPartialName+"Ref(clt.get"+methodPartialName+"Ref());\n");
	}

	@Override
	public void addGetterSetterInFocObject(CodeWriter codeWriter, FieldDefinition fldDef) {
		super.addGetterSetterInFocObject(codeWriter, fldDef);
		
		CodeWriter_OneFile intWrt = codeWriter.getInternalFileWriter();
		
		String varName = getGetterSetterMethodsPartialName(fldDef);
		
		intWrt.addImport("b01.foc.property.FObject");
		
		intWrt.printCore("  public int get"+varName+"Ref(){\n");
		intWrt.printCore("    FObject objProp = (FObject) getFocProperty("+fldDef.getCW_FieldConstanteName()+");\n");
		intWrt.printCore("    return objProp.getLocalReferenceInt();\n");
		intWrt.printCore("  }\n\n");
		
		intWrt.printCore("  public void set"+varName+"Ref(int objRef){\n");
		intWrt.printCore("    FObject objProp = (FObject) getFocProperty("+fldDef.getCW_FieldConstanteName()+");\n");
		intWrt.printCore("    objProp.setLocalReferenceInt_WithoutNotification(objRef);\n");
		intWrt.printCore("    objProp.reactToLocalReferenceModification_AndNotifyListeners();\n");
		intWrt.printCore("  }\n\n");
	}	

	/*
	@Override
	public String getConvertString2ValueFunction(){
		return "convertString2Int";
	}
	*/
	
	@Override
	public String getConvertValue2StringFunction(){
		return "convertObject2String";
	}

	@Override
	public String getClassFor_GuiComponent_Details(){
		return "FwObjectPicker";
	}
}
