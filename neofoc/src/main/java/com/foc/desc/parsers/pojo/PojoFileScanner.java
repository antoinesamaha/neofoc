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
package com.foc.desc.parsers.pojo;

import java.util.ArrayList;

import com.foc.Globals;
import com.foc.annotations.model.FocEntity;
import com.foc.desc.FocModule;
import com.foc.loader.FocFileLoader;
import com.foc.util.FocAnnotationUtil;
import com.foc.util.Utils;

public class PojoFileScanner {

	private String    xmlDirectory = null;
	private String    javaPackage  = null;
	private FocModule module       = null;
	
	public PojoFileScanner(FocModule module, String javaPackage) {
		this.javaPackage  = javaPackage;
		this.xmlDirectory = "/"+javaPackage.replace(".", "/")+"/";
		this.module       = module;
	}
	
	public PojoFileScanner(FocModule module, String xmlDirectory, String javaPackage) {
		this.javaPackage  = javaPackage;
		this.xmlDirectory = xmlDirectory;
		this.module       = module;
	}
	
	public void dispose(){
		javaPackage  = null;
		xmlDirectory = null;
		module       = null;
	}

	private FocModule getFocModule(){
		return module;
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
//					Globals.logString("Pojo File : " + xmlFileName);
					int indexOfDOT = xmlFileName.lastIndexOf(".");
					if(indexOfDOT > 0){
						String simpleClassName = xmlFileName.substring(0, xmlFileName.indexOf("."));
						String fullClassName = getJavaPackage() + "." + simpleClassName;

						Class<PojoFocObject> focObjClass = null;
						try{
							focObjClass = (Class<PojoFocObject>) Class.forName(fullClassName);
						}catch (Exception e){
							focObjClass = null;
						}
						if(focObjClass != null){
							FocEntity entity = (FocEntity) FocAnnotationUtil.findAnnotation(focObjClass, FocEntity.class);
							if(entity != null){
								//Getting names from annotation
								String name = entity.name();
								String storageName = entity.storageName();
								if(Utils.isStringEmpty(name)){
									name = focObjClass.getSimpleName();
								}
								if(Utils.isStringEmpty(storageName)){
									storageName = name;
								}

								//Searching for FocDesc class
								String focDescClassName = focObjClass.getSimpleName()+"Desc"; 
								focDescClassName = getJavaPackage() + "." + focDescClassName;
								Class<PojoFocDesc> focDescClass = null;
								try{
									focDescClass = (Class<PojoFocDesc>) Class.forName(focDescClassName);
								}catch (Exception e1){
									focDescClass = PojoFocDesc.class;
								}
								
								FocDescDeclaration_PojoBased declaration = new FocDescDeclaration_PojoBased(module, storageName, storageName, focDescClass, focObjClass);
								Globals.getApp().declaredObjectList_DeclareObject(declaration);
							}
						}
					}
				}
			}
		}
	}

	public void scanDirectory_DELETEME(){
		/*
	  Reflections reflections = new Reflections(getJavaPackage());

		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(FocEntity.class);
		Iterator iter = annotated.iterator();
		while(iter != null && iter.hasNext()){
			Class focObjClass = (Class) iter.next();
			
			FocEntity entity = (FocEntity) FocAnnotationUtil.findAnnotation(focObjClass, FocEntity.class);
			if(entity != null){			
			}			
		}
		FocFileLoader fileLoader = new FocFileLoader();
		ArrayList<String> xmlFileNames = fileLoader.findFiles(getRootXmlDirectoryPath(), ".xml");
		fileLoader.dispose();
		
		if(xmlFileNames != null){
			for(int i=0; i<xmlFileNames.size(); i++){
				String xmlFileName = xmlFileNames.get(i);
				if(xmlFileName != null){
					int indexOfDOT = xmlFileName.lastIndexOf(".");
					if(indexOfDOT > 0){
						String storageName = xmlFileName.substring(0, xmlFileName.indexOf("."));

						Class defaultFocObjectClass = PojoFocObject.class;
						if(xmlFileName.endsWith("Filter.xml")){
							defaultFocObjectClass = XMLFocObjectFilterBindedToList.class;
						}
						
						Class<PojoFocDesc> focDescClass = getFocDesc_Class(xmlFileName, false);
						Class focObjClass  = getFocObject_Class(xmlFileName, false, defaultFocObjectClass);
						
						String fullXMLFileNameWithPath = getRootXmlDirectoryPath();
						if(fullXMLFileNameWithPath.endsWith("/")){
							fullXMLFileNameWithPath += xmlFileName; 
						}else{
							fullXMLFileNameWithPath += "/"+xmlFileName;
						}
						
						FocDescDeclaration_PojoBased declaration = new FocDescDeclaration_PojoBased(module, storageName, storageName, fullXMLFileNameWithPath, focDescClass, focObjClass);
						Globals.getApp().declaredObjectList_DeclareObject(declaration);
					}
				}
			}
		}
		 */
	}
}
