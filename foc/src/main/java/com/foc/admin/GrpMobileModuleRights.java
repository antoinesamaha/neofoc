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
package com.foc.admin;

import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.shared.json.B01JsonBuilder;

@SuppressWarnings("serial")
public class GrpMobileModuleRights extends FocObject {

  private Object object;

	public GrpMobileModuleRights(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
  
  public FocGroup getGroup(){
    return (FocGroup) getPropertyObject(GrpMobileModuleRightsDesc.FLD_GROUP);
  }
  
  public void setGroup(FocGroup group){
    setPropertyObject(GrpMobileModuleRightsDesc.FLD_GROUP, group);
  }
   
  public String getModuleName(){
    return getPropertyString(GrpMobileModuleRightsDesc.FLD_MODULE_NAME);
  }

  public void setModuleName(String key){
    setPropertyString(GrpMobileModuleRightsDesc.FLD_MODULE_NAME, key);
  }

  public String getModuleTitle(){
    return getPropertyString(GrpMobileModuleRightsDesc.FLD_MODULE_TITLE);
  }

  public void setModuleTitle(String key){
    setPropertyString(GrpMobileModuleRightsDesc.FLD_MODULE_TITLE, key);
  }
  
  public boolean getCreate() {
  	return getPropertyBoolean(GrpMobileModuleRightsDesc.FLD_CREATE);
  }

  public boolean getRead() {
  	return getPropertyBoolean(GrpMobileModuleRightsDesc.FLD_READ);
  }
  
  public boolean getUpdate() {
  	return getPropertyBoolean(GrpMobileModuleRightsDesc.FLD_UPDATE);
  }
  
  public boolean getDelete() {
  	return getPropertyBoolean(GrpMobileModuleRightsDesc.FLD_DELETE);
  }
  
  public boolean hasAccessRight(String moduleName){
		boolean access = false;
		if(			getModuleName() != null 
				&& 	getModuleName().equals(moduleName) 
				//&&  getRight() != GrpMobileModuleRightsDesc.ACCESS_NONE
				&&  (getCreate() || getRead() || getUpdate() || getDelete())
				){
			access = true;
		}
		return access;
	}
  
  @Override
  public void toJson(B01JsonBuilder builder) {
  	builder.beginObject();
  	builder.appendKey("MODULE_NAME");
  	builder.appendValue(getModuleName());
  	if(getCreate()) {
  		builder.appendKeyValue("Create", getCreate());
  	}
  	if(getRead()) {
  		builder.appendKeyValue("Read", getRead());
  	}
  	if(getUpdate()) {
  		builder.appendKeyValue("Update", getUpdate());
  	}
  	if(getDelete()) {
  		builder.appendKeyValue("Delete", getDelete());
  	}
  	
  	/*
  	builder.appendKey("ACCESS");
  	if (getRight() == GrpMobileModuleRightsDesc.ACCESS_NONE) {
  		builder.appendValue("none");
  	} else if (getRight() == GrpMobileModuleRightsDesc.ACCESS_FULL) {
  		builder.appendValue("full");
  	} else if (getRight() == GrpMobileModuleRightsDesc.ACCESS_READ_ONLY) {
  		builder.appendValue("read_only");
  	}
  	*/

  	builder.endObject();
  }
}
