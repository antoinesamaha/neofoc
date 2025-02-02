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
package com.foc.desc;

import com.foc.Globals;
import com.foc.desc.parsers.pojo.PojoFileScanner;
import com.foc.desc.parsers.xml.XMLDescFileScanner;
import com.foc.menu.FMenuList;
import com.foc.util.Utils;

/**
 * @author 01Barmaja
 */
public abstract class FocModule {
  private boolean declared = false;
  
  public abstract void declareFocObjectsOnce();
  
  public void declareFocObjects(){
    if(!declared){
      declared = true;
      declareFocObjectsOnce();
    }
  }
  
  public void dispose(){
  	
  }

  public void scanModelPackage(String packageName){
  	if(!Utils.isStringEmpty(packageName)){
	  	XMLDescFileScanner scanner = new XMLDescFileScanner(this, packageName);
			scanner.scanDirectory();
			scanner.dispose();
			
//			Globals.logString("Starting Pojo Scan : "+packageName);
			PojoFileScanner pojoFileScanner = new PojoFileScanner(this, packageName);
			pojoFileScanner.scanDirectory();
			pojoFileScanner.dispose();		
//			Globals.logString("Ending Pojo Scan");
  	}
  }
  
  public boolean isDeclared(){
  	return declared;
  }
  
  public void afterConstruction(){
  }
  
  public void addConfigurationMenu(FMenuList menuList){
  }
  
  public void addApplicationMenu(FMenuList menuList){
  }
  
  public void afterAdaptDataModel() {
  }

  public void afterApplicationEntry() {
  }

  public void afterApplicationLogin() {
  }

  public void beforeAdaptDataModel() {
  }
  
  public void declareSP(){
  	
  }
  
	public void declare() {
		Globals.getApp().declareModule(this);
	}
	
	public void declareFocDescClass(Class classObject){
		Globals.getApp().declaredObjectList_DeclareDescription(this, classObject);
	}
	
	public void declareFocDescClass_ForExistingInstance(Class classObject){
		Globals.getApp().declaredObjectList_DeclareObjectForExistingInstance(this, classObject);
	}
	
	public String getName(){
		String name = this.getClass().getName();
		int last = name.lastIndexOf(".");
		if(last+1 < name.length()){
			name = name.substring(last+1);
		}
		return name;  
	}
}
