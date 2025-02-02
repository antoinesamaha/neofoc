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

import java.util.HashMap;

import com.foc.Globals;
import com.foc.business.notifier.actions.IFocNotifAction;
import com.foc.desc.FocDesc;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;

public class FocNotificationManager {

  private FocList eventNotifierList         = null;
  private FocList internalEventNotifierList = null;
  private HashMap<Long, FocNotificationEventArray> mapOfThreadsSuspendingTheirEvents = null; 

  public FocNotificationManager() {
    setEventNotifierList(FNotifTrigger.getFocDesc().getFocList(FocList.LOAD_IF_NEEDED));
    
    mapOfThreadsSuspendingTheirEvents = new HashMap<Long, FocNotificationEventArray>();
    
//    Thread thread = new Thread(new ScheduledThread());
//    thread.start();
  }

  public void dispose() {
    if(internalEventNotifierList != null){
      internalEventNotifierList.dispose();
      internalEventNotifierList = null;
    }
    eventNotifierList = null;
  }

  public void threadCumulatingEvents_AddThread(){
  	long id = Thread.currentThread().getId();
  	if(mapOfThreadsSuspendingTheirEvents != null && !mapOfThreadsSuspendingTheirEvents.containsKey(id)){
  		mapOfThreadsSuspendingTheirEvents.put(id, null);
  	}
  }

  public void threadCumulatingEvents_FireEventsCumulated(){
  	long id = Thread.currentThread().getId();
  	if(mapOfThreadsSuspendingTheirEvents != null && mapOfThreadsSuspendingTheirEvents.containsKey(id)){
  		FocNotificationEventArray eventArray = mapOfThreadsSuspendingTheirEvents.get(id);
  		if(eventArray != null){
  			mapOfThreadsSuspendingTheirEvents.remove(id);
  			for(int i=0; i<eventArray.size(); i++){
  				FocNotificationEvent event = eventArray.get(i);
  				fireEvent(event);
  			}
  		}
  	}
  }
  
  private boolean threadCumulatingEvents_AddEvent(FocNotificationEvent eventFired){
  	boolean isSuspending = false;
  	long id = Thread.currentThread().getId();
  	if(mapOfThreadsSuspendingTheirEvents != null && mapOfThreadsSuspendingTheirEvents.containsKey(id)){
  		FocNotificationEventArray eventArray = mapOfThreadsSuspendingTheirEvents.get(id);
  		if(eventArray == null){
  			eventArray = new FocNotificationEventArray();
  			mapOfThreadsSuspendingTheirEvents.put(id, eventArray);
  		}
  		eventArray.add(eventFired);
  		isSuspending = true;
  	}
  	return isSuspending;
  }
  
  public void fireEvent(FocNotificationEvent eventFired) {
    if (getEventNotifierList() != null) {
    	if(!threadCumulatingEvents_AddEvent(eventFired)){
	      FocList eventNotifierList = getInternalEventNotifierList(false);
	      if(eventNotifierList != null){
	        scanListOfEventsAndTreat(eventNotifierList, eventFired);
	      }
	      
	      eventNotifierList = getEventNotifierList();
	      scanListOfEventsAndTreat(eventNotifierList, eventFired);
    	}
    }
  }
  
  private void scanListOfEventsAndTreat(FocList eventNotifierList, FocNotificationEvent eventFired){
    if(eventNotifierList != null){
      for (int i = 0; i < eventNotifierList.size(); i++) {
        FNotifTrigger notifier = (FNotifTrigger) eventNotifierList.getFocObject(i);
        if(notifier != null) {
        	notifier.executeIfSameEvent(eventFired);
        }
      }
    }
  }
  
  private FocList getInternalEventNotifierList(boolean createIfNeeded){
    if(internalEventNotifierList == null && createIfNeeded){
      internalEventNotifierList = new FocList(new FocLinkSimple(FNotifTrigger.getFocDesc()));
      internalEventNotifierList.setDbResident(false);
      internalEventNotifierList.setCollectionBehaviour(true);
    }
    return internalEventNotifierList;
  }

  public FNotifTrigger addInternalEventNotifier(int eventID, FocDesc focDesc, String transactionName, IFocNotifAction eventAction){
    FocList list = getInternalEventNotifierList(true);
    FNotifTrigger notifier = (FNotifTrigger) list.newEmptyDisconnectedItem();
    notifier.setEvent(eventID);
    notifier.setTableDesc(focDesc);
    notifier.setActionObject(eventAction);
    list.add(notifier);
    return notifier;
  }
  
  public void removeInternalEventNotifier(FNotifTrigger notifier){
  	FocList list = getInternalEventNotifierList(false);
  	if(notifier != null && list != null){
  		list.remove(notifier);
  		notifier.dispose();
  	}
  }
  
  public FocList getEventNotifierList() {
    return eventNotifierList;
  }

  public void setEventNotifierList(FocList list) {
    eventNotifierList = list;
  }

  public static FocNotificationManager getInstance() {
    FocNotificationManager notificationManager = null;
    if (Globals.getApp() != null) {
      notificationManager = Globals.getApp().getNotificationManager();
    }
    return notificationManager;
  }
}
