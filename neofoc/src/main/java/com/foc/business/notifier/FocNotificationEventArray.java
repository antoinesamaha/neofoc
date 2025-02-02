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

import java.util.ArrayList;

public class FocNotificationEventArray {

	private ArrayList<FocNotificationEvent> arrayOfEvents = null;
	
	public FocNotificationEventArray(){
		arrayOfEvents = new ArrayList<FocNotificationEvent>();
	}
	
	public void dispose(){
		if(arrayOfEvents != null){
			for(int i=0; i<arrayOfEvents.size(); i++){
				arrayOfEvents.get(i).dispose();
			}
			arrayOfEvents.clear();
			arrayOfEvents = null;
		}
	}
	
	public int size(){
		return arrayOfEvents != null ? arrayOfEvents.size() : 0;
	}
	
	public FocNotificationEvent get(int i){
		FocNotificationEvent event = null;
		if(arrayOfEvents != null){
			event = arrayOfEvents.get(i);
		}
		return event;
	}
	
	public void add(FocNotificationEvent event){
		if(arrayOfEvents == null) arrayOfEvents = new ArrayList<FocNotificationEvent>();
		arrayOfEvents.add(event);
	}
	
	public void fireEvents(){
		if(arrayOfEvents != null){
			for(int i=0; i<arrayOfEvents.size(); i++){
				FocNotificationEvent event = arrayOfEvents.get(i);
				FocNotificationManager.getInstance().fireEvent(event);
			}
		}
	}
}
