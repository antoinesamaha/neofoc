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

public class CodeWriter_ServiceImpl extends CodeWriter {

	public CodeWriter_ServiceImpl(CodeWriterSet set){
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
		return CLASS_NAME_SUFFIX_SERVICE_IMPLEMENTATION;
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
		
		//  Internal
		//  --------
		intWriter.addImport("b01.focGWT.servlet.GwfServlet");
		intWriter.addImport(getPackageName_Client()+"."+getClassName_GwfObject());
		intWriter.addImport(getPackageName_Client()+"."+getClassName_WebService());
		intWriter.addImport(getPackageName_Server()+"."+getClassName_ServiceInstance());
		intWriter.addImport(getPackageName_ServerRoot()+".session.AppSession");
		if(!getTblDef().isSingleInstance()){
			intWriter.addImport(getPackageName_Client()+"."+getClassName_GwfList());
		}
		
		intWriter.printCore("@SuppressWarnings(\"serial\")\n");
		intWriter.printCore("public abstract class "+intWriter.getClassName()+" extends GwfServlet<AppSession> implements " + getClassName_WebService() + " {\n\n");
				
		if(getTblDef().isSingleInstance()){
			intWriter.printCore("  @Override\n");
			intWriter.printCore("  public "+className_focObjClt+" get"+className_focObj+"() throws IllegalArgumentException {\n");
			intWriter.printCore("    return "+getClassName_ServiceInstance()+".getInstance().get"+className_focObj+"();\n");
			intWriter.printCore("  }\n\n");
		}else{
			intWriter.printCore("  @Override\n");
			intWriter.printCore("  public "+getClassName_GwfList()+" get"+getClassName_GwfList()+"(){\n");
			intWriter.printCore("    return "+getClassName_ServiceInstance()+".getInstance().get"+getClassName_GwfList()+"();\n");
			intWriter.printCore("  }\n\n");
		
			intWriter.printCore("  @Override\n");
			intWriter.printCore("  public "+className_focObjClt+" get"+className_focObj+"(int ref) throws IllegalArgumentException {\n");
			intWriter.printCore("    return "+getClassName_ServiceInstance()+".getInstance().get"+className_focObj+"(ref);\n");
			intWriter.printCore("  }\n\n");
					
			intWriter.printCore("  @Override\n");
			intWriter.printCore("  public boolean delete"+className_focObj+"(int ref){\n");
			intWriter.printCore("    return "+getClassName_ServiceInstance()+".getInstance().delete"+className_focObj+"(ref);\n");
			intWriter.printCore("  }\n");
		}
		
		intWriter.printCore("  @Override\n");
		intWriter.printCore("  public int submit"+className_focObj+"("+className_focObjClt+" clt){\n");
		intWriter.printCore("    return "+getClassName_ServiceInstance()+".getInstance().submit"+className_focObj+"(clt);\n");
		intWriter.printCore("  }\n");

		//End of the class
		intWriter.printCore("}\n");
		intWriter.compile();
		
		//  External
		//  --------
		
		extWriter.addImport("com.google.gwt.user.client.rpc.RemoteServiceRelativePath");
		extWriter.addImport(intWriter.getPackageName()+"."+intWriter.getClassName());

		extWriter.printCore("@RemoteServiceRelativePath(\""+ getServiceName() +"\")\n");
		extWriter.printCore("public class "+extWriter.getClassName()+" extends "+intWriter.getClassName()+" {\n");
		extWriter.printCore("  private static final long serialVersionUID = 1L;\n\n");
		
		extWriter.printCore("}");
		extWriter.compile();
		
		closeFiles();
	}
}
