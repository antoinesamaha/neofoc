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
import com.foc.gui.table.view.ViewConfig;
import com.foc.gui.table.view.ViewFocList;
import com.foc.list.FocList;
import com.foc.property.FObject;

@SuppressWarnings("serial")
public class GrpViewRights extends FocObject{

	private ViewFocList viewFocList = null;
	
  public GrpViewRights(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
  
  @Override
  public FocList getObjectPropertySelectionList(int fieldID){
  	FocList list = super.getObjectPropertySelectionList(fieldID);
  	if(list == null && fieldID == GrpViewRightsDesc.FLD_VIEW_CONFIG){
  		adaptViewSelectionList();
  		list = viewFocList;
  	}
  	return list;
  }

  public void copy(GrpViewRights source){
    setGroup(source.getGroup());
    setViewKey(source.getViewKey());
    setViewContext(source.getViewContext());
    setRight(source.getRight());
    setViewConfig(source.getViewConfig());
  }
  
  public FocGroup getGroup(){
    return (FocGroup) getPropertyObject(GrpViewRightsDesc.FLD_GROUP);
  }
  
  public void setGroup(FocGroup group){
    setPropertyObject(GrpViewRightsDesc.FLD_GROUP, group);
  }
   
  public String getViewKey(){
    return getPropertyString(GrpViewRightsDesc.FLD_VIEW_KEY);
  }

  public void setViewKey(String key){
    setPropertyString(GrpViewRightsDesc.FLD_VIEW_KEY, key);
  }

  public String getViewContext(){
    return getPropertyString(GrpViewRightsDesc.FLD_VIEW_CONTEXT);
  }

  public void setViewContext(String title){
    setPropertyString(GrpViewRightsDesc.FLD_VIEW_CONTEXT, title);
  }
  
  public int getRight(){
    return getPropertyMultiChoice(GrpViewRightsDesc.FLD_VIEW_RIGHT);
  }

  public void setRight(int right){
    setPropertyMultiChoice(GrpViewRightsDesc.FLD_VIEW_RIGHT, right);
  }
  
  public ViewConfig getViewConfig(){
    return (ViewConfig) getPropertyObject(GrpViewRightsDesc.FLD_VIEW_CONFIG);
  }

  public long getViewConfigRef(){
    return ((FObject)getFocProperty(GrpViewRightsDesc.FLD_VIEW_CONFIG)).getLocalReferenceInt();
  }

  public void setViewConfig(ViewConfig viewConfig){
    setPropertyObject(GrpViewRightsDesc.FLD_VIEW_CONFIG, viewConfig);
  }
  
  public void adaptViewSelectionList(){
  	if(viewFocList == null){
  		viewFocList = new ViewFocList(getViewKey());
  	}
  }

  public void adjustPropertyLock(){
		FObject objProp = (FObject) getFocProperty(GrpViewRightsDesc.FLD_VIEW_CONFIG);
		objProp.setValueLocked(getRight() != GrpViewRightsDesc.ALLOW_NOTHING);
  }
}
