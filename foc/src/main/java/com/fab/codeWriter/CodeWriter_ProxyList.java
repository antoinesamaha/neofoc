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

public class CodeWriter_ProxyList extends CodeWriter {

	public CodeWriter_ProxyList(CodeWriterSet set){
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
		return CLASS_NAME_SUFFIX_PROXY_LIST;
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
		
		CodeWriter cwProxy     = getCodeWriterSet().getCodeWriter_Proxy();
		CodeWriter cwProxyDesc = getCodeWriterSet().getCodeWriter_ProxyDesc();
		
		intWriter.addImport("b01.focGWT.client.proxy.ProxyList");
		intWriter.addImport(cwProxy.getClassName_Full(false));
		intWriter.addImport(getClassName_Full(false));
		intWriter.addImport("b01.focGWT.client.dataStore.DataStore_Client");
		intWriter.addImport("b01.shared.dataStore.DataKey");
		
		intWriter.printCore("@SuppressWarnings(\"serial\")\n");
		intWriter.printCore("public class "+getClassName(true)+" extends ProxyList<"+cwProxyDesc.getClassName()+", "+cwProxy.getClassName()+"> {\n\n");

		intWriter.addImport(cwProxyDesc.getClassName_Full(false));
		intWriter.printCore("  public "+getClassName(true)+"(){\n");
		intWriter.printCore("    super("+cwProxyDesc.getClassName()+".getInstance());\n");
		intWriter.printCore("  }\n\n");

		intWriter.printCore("  @Override\n");
		intWriter.printCore("  public "+cwProxy.getClassName()+" newObject(){\n");
		intWriter.printCore("    return new "+cwProxy.getClassName()+"(this);\n");
		intWriter.printCore("  }\n\n");
		
		intWriter.printCore("  public static "+getClassName()+" getList(){\n");
		intWriter.printCore("    String dataKey = DataKey.getKey_ForTable(\""+getTblDef().getName()+"\");\n");
		intWriter.printCore("    "+getClassName()+" proxyList = ("+getClassName()+") DataStore_Client.getInstance().getData(dataKey);\n");
		intWriter.printCore("    if(proxyList == null){\n");
		intWriter.printCore("      proxyList = new "+getClassName()+"();\n");
		intWriter.printCore("      proxyList.setDataKey(dataKey);\n");
		intWriter.printCore("      DataStore_Client.getInstance().putData(dataKey, proxyList);\n");
		intWriter.printCore("    }\n");
		intWriter.printCore("    return proxyList;\n");
		intWriter.printCore("  }\n\n");
		
		intWriter.printCore("}");
		intWriter.compile();
		
		//  External
		//  --------
		
		//extWriter.addImport("b01.foc.desc.FocConstructor");
		extWriter.addImport(getClassName_Full(true));
		
		extWriter.printCore("@SuppressWarnings(\"serial\")\n");
		extWriter.printCore("public class "+getClassName(false)+" extends "+getClassName(true)+" {\n");
		
		extWriter.printCore("}");
		extWriter.compile();
		
		closeFiles();
	}
}
