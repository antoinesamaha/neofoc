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
package com.foc.serializer;

import java.util.ArrayList;

import com.foc.Globals;
import com.foc.annotations.model.FocEntity;
import com.foc.desc.FocModule;
import com.foc.desc.parsers.pojo.PojoFocObject;
import com.foc.loader.FocFileLoader;
import com.foc.util.FocAnnotationUtil;
import com.foc.util.Utils;

public class FSerializerFileScanner {
	
	private String    xmlDirectory = null;
	private String    javaPackage  = null;
	
	public FSerializerFileScanner(String javaPackage) {
		this.javaPackage  = javaPackage;
		this.xmlDirectory = "/"+javaPackage.replace(".", "/")+"/";
	}
	
	public FSerializerFileScanner(FocModule module, String xmlDirectory, String javaPackage) {
		this.javaPackage  = javaPackage;
		this.xmlDirectory = xmlDirectory;
	}
	
	public void dispose(){
		javaPackage  = null;
		xmlDirectory = null;
	}

	private String getRootXmlDirectoryPath(){
		return xmlDirectory;
	}
	
	private String getJavaPackage() {
		return javaPackage;
	}
	
	public void scanDirectory(){
		FocFileLoader fileLoader = new FocFileLoader();
		ArrayList<String> xmlFileNames = fileLoader.findFiles(getRootXmlDirectoryPath(), ".class");
		fileLoader.dispose();
		
		if(xmlFileNames != null){
			for(int i=0; i<xmlFileNames.size(); i++){
				String xmlFileName = xmlFileNames.get(i);
				if(xmlFileName != null){
					Globals.logString("HTNL Java class: " + xmlFileName);
					int indexOfDOT = xmlFileName.lastIndexOf(".");
					if(indexOfDOT > 0){
						String simpleClassName = xmlFileName.substring(0, xmlFileName.indexOf("."));
						String fullClassName = getJavaPackage() + "." + simpleClassName;

						String type = "HTML";
						int typeIndex = simpleClassName.indexOf("_"+type+"_");
						if(typeIndex <= 0) {
							type = "JSON";
							typeIndex = simpleClassName.indexOf("_"+type+"_");
						}
						
						if(typeIndex > 0) {
							String storageName = simpleClassName.substring(0, typeIndex);
							String versionText = simpleClassName.substring(typeIndex+type.length()+2);
							
							if(			!Utils.isStringEmpty(storageName)
									&&  !Utils.isStringEmpty(type)
									&& 	!Utils.isStringEmpty(versionText) 
									&& 	Utils.isInteger(versionText)) {
								
								Class<FSerializer> serializerClass = null;
								try{
									serializerClass = (Class<FSerializer>) Class.forName(fullClassName);
								}catch (Exception e){
									serializerClass = null;
								}								
								
								if(serializerClass != null) {
									int version = Integer.valueOf(versionText);
									FSerializerDictionary dictionary = FSerializerDictionary.getInstance();
									dictionary.put(storageName, type, version, serializerClass);
								}
							}
						}
					}
				}
			}
		}
	}
}
