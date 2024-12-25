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
package com.foc.wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;

public class WrapperFocList extends FocList{
  
  private Map<Integer, Wrapper> wrapperListFocObjectMap = null;
  private WrapperDesc           wrapperDesc             = null;
  private FocList               sourceFocList           = null;
  
  public WrapperFocList(WrapperDesc wrapperDesc, FocList focList){
  	super(null, new FocLinkSimple(wrapperDesc), null);
  	this.wrapperDesc = wrapperDesc;
    setDirectlyEditable(true);
    setDirectImpactOnDatabase(false);
    wrapperListFocObjectMap = new HashMap<Integer, Wrapper>();
    //buildFocList_children(focList);
    buildWrapperFocList(focList);
  }
  
  public void dispose(){
  	super.dispose();
  	dispose_Map();
  	wrapperListFocObjectMap = null;
    wrapperDesc   = null;
    sourceFocList = null;
  }
  
  private void dispose_Map(){
    if(wrapperListFocObjectMap != null){
      wrapperListFocObjectMap.clear();
    }
  }
  
  private WrapperDesc getWrapperDesc(){
  	return this.wrapperDesc;
  }
  
  private WrapperLevel getWrapperLevelHavingSameFocDescOfList(FocList list){
  	WrapperLevel level = null;
  	if(list != null){
  		FocDesc listFocDesc = list.getFocDesc();
  		WrapperDesc wrapperDesc = getWrapperDesc();
  		if(wrapperDesc != null){
	  		WrapperLevel rootLevel = wrapperDesc.getRootWrapperLevel();
	  		level = getWrapperLevelHavingFocDesc(rootLevel, listFocDesc);
  		}
  	}
  	return level;
  }
  
  private WrapperLevel getWrapperLevelHavingFocDesc(WrapperLevel fatherWrapperLevel, FocDesc focDesc){
  	WrapperLevel level = fatherWrapperLevel;
  	boolean found = level!= null && level.getFocDesc() == focDesc;
  	if(!found){
  		ArrayList<WrapperLevel> childrenList = level.getChildrenLevelList();
  		if(childrenList != null){
  			for(int i = 0; i < childrenList.size() && !found; i++){
  				WrapperLevel childLevel = childrenList.get(i);
  				if(childLevel != null){
  					level = getWrapperLevelHavingFocDesc(childLevel, focDesc);
  					found = level != null;
  				}
  			}
  		}
  	}
  	return found ? level : null;
  }
  
  public void rebuildWrapperFocList(){
  	sourceFocList.reloadFromDB();
  	dispose_Map();
  	removeAll();
  	buildWrapperFocList(sourceFocList);
  }

  protected void buildWrapperFocList(FocList focList){
  	sourceFocList = focList;
  	WrapperLevel level = getWrapperLevelHavingSameFocDescOfList(focList);
  	if(level != null){
  		buildFocList_children(focList, level);
  		completeWrapperFocListWithMissingFatherWrappers(level);
  	}
  }
  
  private void buildFocList_children(FocList focList, WrapperLevel levelToStartBuildingFrom){
  	if(levelToStartBuildingFrom != null){
  		levelToStartBuildingFrom.addLevelToFocList(this, null, focList);
  	}
  }
  
  private void completeWrapperFocListWithMissingFatherWrappers(WrapperLevel levelToStartBuildingFrom){
		ArrayList<Wrapper> arrayOfwrapperHavingNullFather = new ArrayList<Wrapper>();
		for(int i = 0; i < size(); i++){
			Wrapper wrapper = (Wrapper) getFocObject(i);
			if(wrapper != null && wrapper.getPropertyObject(FField.FLD_FATHER_NODE_FIELD_ID) == null){
				arrayOfwrapperHavingNullFather.add(wrapper);
			}
		}
		for(int i = 0; i < arrayOfwrapperHavingNullFather.size(); i++){
			Wrapper wrapper = arrayOfwrapperHavingNullFather.get(i);
			if(wrapper != null){
				createFathersForWrapper(wrapper);
			}
		}
  }
  
  private void createFathersForWrapper(Wrapper wrapper){
		HashMap<WrapperLevel, FocObject> ancestorsFocObjectsMap = new HashMap<WrapperLevel, FocObject>();
    //ArrayList<FocObject> ancestorsFocObjectsList = new ArrayList<FocObject>();
    ArrayList<WrapperLevel> ancestorsWrapperLevelArray = new ArrayList<WrapperLevel>();
		WrapperLevel currentLevel = wrapper.getLevel();
		if(currentLevel != null){
			FocObject currentFocObject = wrapper.getPropertyObject(currentLevel.getObjectFieldID());
			while(currentLevel != null && currentFocObject != null){
				ancestorsWrapperLevelArray.add(currentLevel);
				ancestorsFocObjectsMap.put(currentLevel, currentFocObject);
        //ancestorsFocObjectsList.add(currentFocObject);
        
				int foreingFieldID = currentLevel.getForeignKeyField();
	  		currentFocObject  = currentFocObject.getPropertyObject(foreingFieldID);
	  		currentLevel = currentLevel.getFatherLevel();
			}
			
			Wrapper fatherWrapper = null;
			for(int i = ancestorsWrapperLevelArray.size() -1; i > 0; i--){//> and not >= because the first level is the one of the wrapper : it's already in the list and we do not want to add it another time
				WrapperLevel ancestorWrapperLevel = ancestorsWrapperLevelArray.get(i);
				if(ancestorWrapperLevel != null){
					FocObject ancestorFocObject = ancestorsFocObjectsMap.get(ancestorWrapperLevel);
            //ancestorsFocObjectsList.get(i);
					fatherWrapper = ancestorWrapperLevel.getWrapper_createIfNeeded(this, fatherWrapper, ancestorFocObject);
					if(i == 1){//the wraper of this level is the father of the wraper that we are building fathers for.
						wrapper.setPropertyObject(FField.FLD_FATHER_NODE_FIELD_ID, fatherWrapper);
					}
				}
			}
		}
  }
  
 /* private Wrapper addWrapperFocObjectToList(FocObject focObj){
    int id = WrapperDesc.FLD_FIRST_OBJECT + counterID;
    Wrapper wrapper = (Wrapper) wrapperListFocObjectMap.get(focObj.getPropertyInteger(FField.REF_FIELD_ID));
    if(wrapper == null){
      wrapper = (Wrapper) wrapperList.newEmptyItem();
      wrapperListFocObjectMap.put(focObj.getPropertyInteger(FField.REF_FIELD_ID), wrapper);
      
      WrapperLevel wrapperLevel = wrapperDescription.getWrapperLevelArrayList().get(0);
     
      FProperty prop = focObj.getFocProperty(wrapperLevel.getDisplayField());
      FProperty wrapperProp = wrapper.getFocProperty(WrapperDesc.FLD_NODE_NAME);
      wrapperProp.setObject(prop.getObject());
      
      wrapper.setPropertyObject(id, focObj);
      
      FocObject focObjFatherNode = focObj.getPropertyObject(focObj.getThisFocDesc().getFObjectTreeFatherNodeID());
      if(focObjFatherNode != null){
        Wrapper wrapperResourceFather = addWrapperFocObjectToList(focObjFatherNode);
        wrapper.setPropertyObject(WrapperDesc.FATHER_NODE_ID, wrapperResourceFather);
      }
      wrapperLevel = wrapperDescription.getWrapperLevelArrayList().get(j);
      FocList wrapperLevelList = wrapper.getChildfocList(focObj, wrapperLevel);
      
      if (wrapperLevelList != null){
        for(int i=0; i< wrapperLevelList.size(); i++){
          FocObject childObj = wrapperLevelList.getFocObject(i);
          Wrapper wrapperChild = (Wrapper) wrapperList.newEmptyItem();
          
          prop = childObj.getFocProperty(wrapperLevel.getDisplayField());
          wrapperProp = wrapperChild.getFocProperty(WrapperDesc.FLD_NODE_NAME);
          wrapperProp.setObject(prop.getObject());
          
          wrapperChild.setPropertyObject(id, childObj);
          wrapperChild.setPropertyObject(WrapperDesc.FATHER_NODE_ID, wrapper);
        }  
      }
    }
    return wrapper;
  }*/
}
