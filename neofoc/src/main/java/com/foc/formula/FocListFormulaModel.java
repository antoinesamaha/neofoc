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
package com.foc.formula;

import com.foc.desc.FocObject;
import com.foc.list.FocList;
import com.foc.property.FProperty;

public class FocListFormulaModel implements IFormulaModel{
  
	private FocList list = null;
	
	public FocListFormulaModel(FocList list){
		this.list = list;
	}

  @Override
  public void dispose(){
  	list = null;
  }
	
	@Override
	public String getViewName(){
  	return "MEMORY_VIEW";
  }
  
  @Override
  public String getFilterCriteria(){
  	return "NO_NEED";
  }
  
  @Override
  public FProperty getFormulaProperty(String objectRefs){
  	FProperty property = null;
  	
  	if(objectRefs != null && !objectRefs.isEmpty()){
  		int commaIndex = objectRefs.indexOf(',');
  		if(commaIndex > 0){
  			int ref     = Integer.valueOf(objectRefs.substring(0, commaIndex));	
  			int fieldID = Integer.valueOf(objectRefs.substring(commaIndex));
  			
  			FocObject focObj = list.searchByReference(ref);
  			if(focObj != null){
  				property = focObj.getFocProperty(fieldID);
  			}
  		}
  	}
  	
  	return property;
  }
  
  @Override
  public String getObjectRefs(FProperty property){
  	String str = "";
  	if(property != null){
  		FocObject focObj = property.getFocObject();
  		if(focObj != null){
  			str = focObj.getReference().getInteger()+","+property.getFocField().getID();
  		}
  	}
  	return str;
  }
}
