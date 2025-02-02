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
package com.foc.business.notifier;

import com.foc.access.FocDataMap;
import com.foc.desc.FocObject;
import com.foc.plugin.IFocObjectPlugIn;
import com.foc.shared.dataStore.IFocData;

public class FocNotificationEvent {

  private int      eventKey = 0;
  private IFocData focData  = null;
  
  public FocNotificationEvent(int eventKey, IFocData focData){
    this.eventKey = eventKey;
    this.focData  = focData;
  }
  
  public void dispose(){
  	focData = null;
  }
  
  public int getEventKey(){
    return eventKey;
  }
  
  public void setEventKey(int eventKey){
    this.eventKey = eventKey;
  }
  
  public IFocData getEventFocData(){
    return focData;
  }
  
  public void setEventFocData(IFocData focData){
    this.focData = focData;
  }
  
  public IFocObjectPlugIn getIFocObjectPlugIn(){
  	IFocObjectPlugIn iFocObjectPlugIn = null;
  	IFocData iFocData = getEventFocData();
  	if(iFocData != null){
  		if(iFocData instanceof FocDataMap){
  			FocDataMap focDataMap = (FocDataMap) iFocData;
  			iFocData = focDataMap.getMainFocData();
  		}
	  	if(iFocData instanceof FocObject){
	  		FocObject focObject = (FocObject) iFocData;
	  		iFocObjectPlugIn = focObject.getIFocObjectPlugIn();
	  	}
  	}
  	return iFocObjectPlugIn;
  }

  public FocObject getEventFocObject(){
  	FocObject focObject = null;
  	IFocData iFocData = getEventFocData();
  	if(iFocData != null){
  		if(iFocData instanceof FocDataMap){
  			FocDataMap focDataMap = (FocDataMap) iFocData;
  			iFocData = focDataMap.getMainFocData();
  		}
	  	if(iFocData instanceof FocObject){
	  		focObject = (FocObject) iFocData;
	  	}
  	}
  	return focObject;
  }

}
