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

import com.foc.desc.FocObject;
import com.foc.desc.field.FFieldPath;
import com.foc.gui.table.view.ViewKeyFactory;
import com.foc.list.DisplayList;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class GrpViewRightsDisplayList extends DisplayList{

  private FocGroup group = null;
  
  public GrpViewRightsDisplayList(FocList realList, FocGroup group) {
    super(realList);
    this.group = group;
    setDoNotRemoveRealItems(false);
    construct();
  }
  
  @Override
  public void dispose(){
  	super.dispose();
    group = null;
  }

  @Override
  public void completeTheDisplayList(FocList realList, FocList displayList) {
    displayList.setDbResident(false);
    //displayList.setLoaded(true);
    FocListOrder focListOrder = new FocListOrder(GrpViewRightsDesc.FLD_VIEW_KEY, GrpViewRightsDesc.FLD_VIEW_CONTEXT);
    focListOrder.addField(FFieldPath.newFieldPath(GrpViewRightsDesc.FLD_VIEW_KEY));
    focListOrder.addField(FFieldPath.newFieldPath(GrpViewRightsDesc.FLD_VIEW_KEY));
    displayList.setListOrder(focListOrder);
    
    ViewKeyFactory factory = ViewKeyFactory.getInstance();
    for(int i=0; i<factory.size(); i++){
    	String key     = factory.getViewKey(i);
    	String context = factory.getViewContext(i);
    
    	GrpViewRights existedMenuRights = (GrpViewRights) findViewInList(displayList, key, context);
      if(existedMenuRights == null){
        existedMenuRights = (GrpViewRights) displayList.newEmptyItem();
        
        existedMenuRights.setViewKey(key);
        existedMenuRights.setViewContext(context);
        existedMenuRights.setGroup(group);
        existedMenuRights.setRight(GrpViewRightsDesc.ALLOW_CREATION);
        displayList.add(existedMenuRights);
      }
      existedMenuRights.adaptViewSelectionList();
      existedMenuRights.adjustPropertyLock();
    }
  }
  
  @Override
  public void copyFromObjectToObject(FocObject target, FocObject source) {
    GrpViewRights tar = (GrpViewRights) target;
    GrpViewRights src = (GrpViewRights) source;
    
    tar.copy(src);
  }

  private GrpViewRights findViewInList(FocList focList, String viewKey, String viewContext) {
  	GrpViewRights foundObj = null;
    if(viewKey != null && viewContext != null){
    	for(int i=0; i<focList.size() && foundObj == null; i++){
    		GrpViewRights currObj = (GrpViewRights) focList.getFocObject(i);
        if(currObj != null){
        	if(currObj.getViewKey().equals(viewKey) && currObj.getViewContext().equals(viewContext)){
        		foundObj = currObj;
        	}
        }
    	}
    }
    return foundObj;
  }
  
  @Override
  public FocObject findObjectInList(FocList focList, FocObject object) {
  	GrpViewRights menuRights  = (GrpViewRights) object;
    String        viewKey     = menuRights.getViewKey();
    String        viewContext = menuRights.getViewContext();
    FocObject     foundObj    = findViewInList(focList, viewKey, viewContext);
    return foundObj;
  }

  @Override
  public boolean isDisplayItemToBeSaved(FocObject object) {
    return true;
  }
}
