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
package com.foc.desc.parsers.xml;

import java.io.InputStream;
import java.lang.reflect.Constructor;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.foc.Globals;
import com.foc.IFocDescDeclaration;
import com.foc.desc.FocDesc;
import com.foc.desc.FocModule;
import com.foc.desc.parsers.FocDescDeclaration_ParsedBased;
import com.foc.util.Utils;
import com.vaadin.server.ClassResource;

public class FocDescDeclaration_XMLBased extends FocDescDeclaration_ParsedBased {

	private Class<XMLFocDesc>   descClass   = null;
	private Class<XMLFocObject> objClass    = null;
	private String              xmlFileName = null;
	private XMLFocDesc          focDesc     = null;
	
	public FocDescDeclaration_XMLBased(FocModule module, String name, String storageName, String xmlFileName, Class<XMLFocDesc> descClass, Class<XMLFocObject> objClass){
		super(module, name, storageName);
		this.xmlFileName = xmlFileName;
		this.descClass   = descClass;
		this.objClass    = objClass;
	}
	
	@Override
	public FocDesc getFocDescription() {
		if(focDesc == null){
			focDesc = parse();
			if(focDesc != null) focDesc.afterParsing();
		}
    return focDesc;
	}
	
	public XMLFocDesc newFocDesc(String forcedStorageName){
		if(!Utils.isStringEmpty(forcedStorageName)){
			setStorageName(forcedStorageName);
		}
	  try {
	    if (descClass != null) {
	      Class[] clss = new Class[4];
	      Object[] args = new Object[4];
	      {
	      	clss[0] = FocModule.class;
	      	args[0] = getFocModule();
	      	
	      	clss[1] = String.class;
	      	args[1] = getStorageName();
	      	
	      	clss[2] = String.class;
	      	args[2] = xmlFileName;
	      	
	      	clss[3] = Class.class;
	      	args[3] = objClass;
	      }
	      Constructor<XMLFocDesc> methodGetFocDesc = null;
	      try{
	      	methodGetFocDesc = descClass.getConstructor(clss);
	      }catch(NoSuchMethodException e){
	      	Globals.logException(e);
	      }
	      if(methodGetFocDesc != null){
	      	focDesc = (XMLFocDesc) methodGetFocDesc.newInstance(args);
	      	focDesc.setName(getName());
	      	
					Globals.getApp().putIFocDescDeclaration(getName(), this);
	      }
	    }
	  } catch (Exception e) {
	  	Globals.logString("Exception while getting FocDesc for class : "+descClass.getName());
	    Globals.logException(e);
	  }
	  return focDesc;
	}

	private XMLFocDesc parse(){
		XMLFocDesc xmlFocDesc = null;
		try{
      ClassResource resource = null;
      InputStream inputStream = null;
      resource = new ClassResource(xmlFileName);
      inputStream = resource.getStream().getStream();

      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxParser = factory.newSAXParser();
      
      XMLFocDescParser focDescParser = new XMLFocDescParser(this);
      
      saxParser.parse(inputStream, focDescParser);
      xmlFocDesc = focDescParser.getXmlFocDesc();
    } catch (Exception e) {
      Globals.logString("Could not load file : " + xmlFileName);
      Globals.logException(e);
    }
		return xmlFocDesc;
	}
}
