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
package com.fab.gui.xmlView;

import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.shared.xmlView.XMLViewKey;

@SuppressWarnings("serial")
public class XMLViewDefinition extends FocObject{

  public XMLViewDefinition(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
  
  public void setStorageName(String storageName){
    setPropertyString(XMLViewDefinitionDesc.FLD_STORAGE_NAME, storageName);
  }
  
  public String getStorageName(){
    return getPropertyString(XMLViewDefinitionDesc.FLD_STORAGE_NAME);
  }
  
  public void setType(int type){
    setPropertyInteger(XMLViewDefinitionDesc.FLD_TYPE, type);
  }
  
  public int getType(){
    return getPropertyInteger(XMLViewDefinitionDesc.FLD_TYPE);
  }
  
  public void setContext(String xmlContexr){
    setPropertyString(XMLViewDefinitionDesc.FLD_CONTEXT, xmlContexr);
  }
  
  public String getContext(){
    return getPropertyString(XMLViewDefinitionDesc.FLD_CONTEXT);
  }
  
  public void setView(String userView){
    setPropertyString(XMLViewDefinitionDesc.FLD_VIEW, userView);
  }
  
  public String getView(){
    return getPropertyString(XMLViewDefinitionDesc.FLD_VIEW);
  }
  
  public void setJavaClassName(String javaClassName){
    setPropertyString(XMLViewDefinitionDesc.FLD_JAVA_CLASS_NAME, javaClassName);
  }
  
  public String getJavaClassName(){
    return getPropertyString(XMLViewDefinitionDesc.FLD_JAVA_CLASS_NAME);
  }
  
  public String getXML(){
  	return getPropertyString(XMLViewDefinitionDesc.FLD_XML);
  }
  
  public void setXML(String xml){
    setPropertyString(XMLViewDefinitionDesc.FLD_XML, xml);
  }
  
  public XMLViewKey newXMLViewKey(){
		XMLViewKey key = new XMLViewKey(getStorageName(), getType(), getContext(), getView());
		return key;
  }
}
