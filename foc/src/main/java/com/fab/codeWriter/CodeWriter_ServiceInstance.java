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

public class CodeWriter_ServiceInstance extends CodeWriter {

	public CodeWriter_ServiceInstance(CodeWriterSet set){
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
		return CLASS_NAME_SUFFIX_SERVICE_INSTANCE;
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
		
		String className_focObjClt     = getClassName_GwfObject(); 
		String className_focObj        = getClassName_FocObject();
		
		String mthName_newCltForFocObj = "new"+className_focObjClt+"For"+className_focObj;

		FocList fieldList = getTblDef().getFieldDefinitionList();
		
		//  Internal
		//  --------
		//intWriter.addImport("java.util.List");
		//intWriter.addImport("java.util.ArrayList");
		intWriter.addImport("b01.foc.list.FocList");
		//intWriter.addImport("b01.focGWT.servlet.GwfServlet");
		intWriter.addImport(getPackageName_Server()+"."+getClassName_ServiceInstance());
		intWriter.addImport(getPackageName_Client()+"."+getClassName_GwfObject());
		intWriter.addImport(getPackageName_Client()+"."+getClassName_GwfList());
		//intWriter.addImport(getPackageName_Client()+"."+getClassName_WebService());
		intWriter.addImport(getPackageName_Server()+"."+getClassName_FocObject());
		intWriter.addImport(getPackageName_Server()+"."+getClassName_FocDesc());
		
		//intWriter.printCore("public    abstract FocList getFocList(int mode);\n");
		//intWriter.printCore("protected abstract List    getCltList_Internal(int mode);\n\n");
		
		//extends RemoteServiceServlet implements HotelService {
		
		//intWriter.printCore("@SuppressWarnings(\"serial\")");
		intWriter.printCore("public abstract class "+intWriter.getClassName()+" {\n\n");

		intWriter.printCore("  //-------------------------------------------------------------\n");
		intWriter.printCore("  //STATIC\n");
		intWriter.printCore("  //-------------------------------------------------------------\n");
		intWriter.printCore("  private static "+extWriter.getClassName()+" instance = null;\n");
		intWriter.printCore("  public static "+extWriter.getClassName()+" getInstance(){\n");
		intWriter.printCore("    if(instance == null){\n");
		intWriter.printCore("      instance = new "+extWriter.getClassName()+"();\n");
		intWriter.printCore("    }\n");
		intWriter.printCore("    return instance;\n");
		intWriter.printCore("  }\n");
		intWriter.printCore("  //-------------------------------------------------------------\n");
		
		intWriter.printCore("  protected abstract FocList   new"+getClassName_FocObject()+"FocList();\n");
		intWriter.printCore("  protected abstract "+getClassName_GwfList()+" new"+getClassName_FocObject()+"List();\n\n");

		intWriter.printCore("  private FocList   focList = null;\n");
		intWriter.printCore("  private "+getClassName_GwfList()+" cltList = null;\n\n");

		intWriter.printCore("  public FocList getFocList(boolean loadIfNeeded){\n");
		intWriter.printCore("    if(focList == null){\n");
		intWriter.printCore("      focList = new"+getClassName_FocObject()+"FocList();\n");
		intWriter.printCore("    }\n");
		intWriter.printCore("    if(loadIfNeeded) focList.loadIfNotLoadedFromDB();\n");
		intWriter.printCore("    return focList;\n");
		intWriter.printCore("  }\n");
		
		intWriter.printCore("  public static "+className_focObjClt+" "+mthName_newCltForFocObj+"("+className_focObj+" focObj){\n");
		intWriter.printCore("    "+className_focObjClt+" clt = null;\n");
		intWriter.printCore("    if(focObj!=null){\n");
		intWriter.printCore("      clt = new "+className_focObjClt+"();\n");
		if(getTblDef().isWithReference()) intWriter.printCore("      clt.setReference(focObj.getReference().getInteger());\n");
		for(int i=0; i<fieldList.size(); i++){
			FieldDefinition fieldDefinition   = (FieldDefinition) fieldList.getFocObject(i);
			FDAbstract      fdAbstract        = FieldFactory.getInstance().get(fieldDefinition.getSQLType());
			
			fdAbstract.addCopyFromFocObjectToCltObject(intWriter, fieldDefinition, "      ");
		}
		intWriter.printCore("    }\n");
		intWriter.printCore("    return clt;\n");
		intWriter.printCore("  }\n\n");

		intWriter.printCore("  protected void copy("+className_focObj+" focObj, "+className_focObjClt+" clt){\n");
		for(int i=0; i<fieldList.size(); i++){
			FieldDefinition fieldDefinition   = (FieldDefinition) fieldList.getFocObject(i);
			FDAbstract      fdAbstract        = FieldFactory.getInstance().get(fieldDefinition.getSQLType());
			fdAbstract.addCopyFromCltObjectToFocObject(intWriter, fieldDefinition, "    ");
		}
		intWriter.printCore("  }\n\n");
		
		if(getTblDef().isSingleInstance()){
			intWriter.printCore("  public "+className_focObjClt+" get"+className_focObj+"() throws IllegalArgumentException {\n");
			intWriter.printCore("    "+className_focObjClt+" objClt = "+mthName_newCltForFocObj+"("+className_focObj+".getInstance());\n");
			intWriter.printCore("    return objClt;\n");
			intWriter.printCore("  }\n\n");
		}else{
			intWriter.printCore("  public "+getClassName_GwfList()+" get"+getClassName_GwfList()+"(){\n");
			intWriter.printCore("    cltList = new"+getClassName_GwfList()+"();\n");
			intWriter.printCore("    FocList focList = getFocList(true);\n");
			intWriter.printCore("    for(int i=0; i<focList.size(); i++){\n");
			intWriter.printCore("      "+className_focObj+" focObj = ("+className_focObj+") focList.getFocObject(i);\n");
			intWriter.printCore("      "+className_focObjClt+" objClt = "+mthName_newCltForFocObj+"(focObj);\n");
			intWriter.printCore("      cltList.add(objClt);\n");
			intWriter.printCore("    }\n\n");
			intWriter.printCore("    return cltList;\n");
			intWriter.printCore("  }\n\n");

			intWriter.printCore("  public "+className_focObjClt+" get"+className_focObj+"(int ref) throws IllegalArgumentException {\n");
			intWriter.printCore("    FocList focList = getFocList(true);\n");
			intWriter.printCore("    "+className_focObj+" focObj = focList != null ? ("+className_focObj+") focList.searchByRealReferenceOnly(ref) : null;\n");
			intWriter.printCore("    "+className_focObjClt+" objClt = "+mthName_newCltForFocObj+"(focObj);\n");
			intWriter.printCore("    return objClt;\n");
			intWriter.printCore("  }\n\n");
		
			intWriter.printCore("  public boolean delete"+className_focObj+"(int ref){\n");
			intWriter.printCore("    //FocList focList = getFocList(true);\n");
			intWriter.printCore("    //"+className_focObj+" focObj = ("+className_focObj+") focList.newEmptyItem();\n");
			intWriter.printCore("    //copy(focObj, clt);\n");
			intWriter.printCore("    //focObj.validate(true);\n");
			intWriter.printCore("    //focList.validate(true);\n");
			intWriter.printCore("    return false;\n");
			intWriter.printCore("  }\n");
		}

		intWriter.printCore("  public int submit"+className_focObj+"(FocList focList, "+className_focObjClt+" clt){\n");
		intWriter.printCore("    "+className_focObj+" focObj = null;\n");
		intWriter.printCore("    if(focList != null && clt != null){\n");
		intWriter.printCore("      if(clt.isInsert()){\n");
		intWriter.printCore("        focObj = ("+className_focObj+") focList.newEmptyItem();\n");
		intWriter.printCore("      }else{\n");
		intWriter.printCore("        focObj = ("+className_focObj+") focList.searchByReference(clt.getReference());\n");
		intWriter.printCore("      }\n");
		intWriter.printCore("      if(focObj != null){\n");
		intWriter.printCore("        copy(focObj, clt);\n");
		intWriter.printCore("        focObj.validate(true);\n");
		intWriter.printCore("        focList.validate(true);\n");
		intWriter.printCore("      }\n");
		intWriter.printCore("    }\n");
		intWriter.printCore("    return focObj != null && focObj.hasRealReference() ? focObj.getReference().getInteger() : 0;\n");
		intWriter.printCore("  }\n");

		intWriter.printCore("  public int submit"+className_focObj+"("+className_focObjClt+" clt){\n");
		intWriter.printCore("    FocList focList = getFocList(true);\n");
		intWriter.printCore("    return submit"+className_focObj+"(focList, clt);\n");
		intWriter.printCore("  }\n");
		
		//End of the class
		intWriter.printCore("}\n");
		intWriter.compile();
		
		//  External
		//  --------
		
		extWriter.addImport("b01.foc.list.FocList");
		extWriter.addImport(getPackageName_Client()+"."+getClassName_GwfList());
		extWriter.addImport(intWriter.getPackageName()+"."+intWriter.getClassName());

		extWriter.printCore("public class "+extWriter.getClassName()+" extends "+intWriter.getClassName()+" {\n");
		extWriter.printCore("  private static final long serialVersionUID = 1L;\n\n");
		
		extWriter.printCore("  @Override\n");
		extWriter.printCore("  protected FocList new"+getClassName_FocObject()+"FocList() {\n");
		extWriter.printCore("  	 return "+getClassName_FocDesc()+".getInstance().getList(null, (FocList.NONE));\n");
		extWriter.printCore("  }\n\n");

		extWriter.printCore("  @Override\n");
		extWriter.printCore("  protected "+getClassName_GwfList()+" new"+getClassName_GwfList()+"() {\n");
		extWriter.printCore("  	 return new "+getClassName_GwfList()+"();\n");
		extWriter.printCore("  }\n\n");
		
//		extWriter.printCore("  public "+extWriter.getClassName()+"(FocConstructor constr){\n");
//		extWriter.printCore("    super(constr);\n");
//		extWriter.printCore("  }\n");
		
		extWriter.printCore("}");
		extWriter.compile();
		
		closeFiles();
	}
}
