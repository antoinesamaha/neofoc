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
package com.foc.gui.table.view;

import com.foc.admin.FocUser;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.gui.FPanel;
import com.foc.list.FocList;
import com.foc.property.FObject;

public class UserView extends FocObject{
	private ViewFocList viewFocList = null;
	
	public UserView(FocConstructor constr){
    super(constr);
    newFocProperties();
  }
  
  public void dispose(){
  	super.dispose();
  	viewFocList = null;
  }

  public static void disposeColumnsConfigFocList(FocList list){
    list.dispose();
    list = null;
  }
  
  @Override
  public FocList getObjectPropertySelectionList(int fieldID){
  	FocList list = super.getObjectPropertySelectionList(fieldID);
  	if(fieldID == UserViewDesc.FLD_VIEW){
  		list = viewFocList;
  	}
  	return list;
  }

  @Override
  public FPanel newDetailsPanel(int viewID){
    return null;
  }
  
  public FocUser getUser(){
    FObject user = (FObject)getFocProperty(UserViewDesc.FLD_USER);
    return (FocUser)user.getObject_CreateIfNeeded();
  }
  
  public void setUser(FocUser user){
    FObject objProp = (FObject) getFocProperty(UserViewDesc.FLD_USER);
    objProp.setObject(user);
  }
  
  public String getViewKey(){
  	return getPropertyString(UserViewDesc.FLD_VIEW_KEY);
  }
  
  public void setViewKey(String viewKey){
  	setPropertyString(UserViewDesc.FLD_VIEW_KEY, viewKey);
  }

  public String getViewContext(){
  	return getPropertyString(UserViewDesc.FLD_VIEW_CONTEXT);
  }
  
  public void setViewContext(String viewContext){
  	setPropertyString(UserViewDesc.FLD_VIEW_CONTEXT, viewContext);
  }

  public ViewConfig getViewConfig(){
  	return (ViewConfig) getPropertyObject(UserViewDesc.FLD_VIEW);
  }
  
  public void setViewConfig(ViewConfig viewConfig){
  	setPropertyObject(UserViewDesc.FLD_VIEW, viewConfig);
  }
  
  public void setViewConfigRef(long ref){
  	((FObject)getFocProperty(UserViewDesc.FLD_VIEW)).setLocalReferenceInt_WithoutNotification(ref);
  }
  
  public ViewFocList getViewFocList() {
		return viewFocList;
	}

	public void setViewFocList(ViewFocList viewFocList) {
		this.viewFocList = viewFocList;
	}
}
