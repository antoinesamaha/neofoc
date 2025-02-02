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
package com.foc.business.notifier.manipulators;

import com.foc.Globals;
import com.foc.business.notifier.FocNotificationEmail;
import com.foc.business.notifier.FocNotificationEmailDesc;
import com.foc.business.notifier.FocNotificationEmailTemplate;
import com.foc.business.notifier.FocNotificationEvent;
import com.foc.business.notifier.FNotifTrigger;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;

public abstract class AbstractFocNotificationTableAddManipulator implements IFocNotificationEventManipulator {

  @Override
  public void treatEvent(FNotifTrigger notifier, FocNotificationEvent event) {
    sendEMail(notifier, event);
  }
  
  private void sendEMail(FNotifTrigger notifier, FocNotificationEvent event){
    FocNotificationEmailTemplate template = (FocNotificationEmailTemplate) notifier.getTemplate();
    FocNotificationEmail email = new FocNotificationEmail(new FocConstructor(FocNotificationEmailDesc.getInstance(), null), template, event.getEventFocData());
    Globals.popup(email, false);
  }

  protected boolean isSameDBTableAsFocObject(FNotifTrigger notifier, FocNotificationEvent event){
    boolean sameTable = false;
//    FocNotificationEmailTemplate template = (FocNotificationEmailTemplate) notifier.getTemplate();
  
    if (notifier != null && event != null && event.getEventFocData() != null && event.getEventFocData().iFocData_getDataByPath("TABLE_NAME") != null && event.getEventFocData().iFocData_getDataByPath("TABLE_NAME").iFocData_getValue() != null  /*&& template != null*/) {
      String eventTableName = (String) event.getEventFocData().iFocData_getDataByPath("TABLE_NAME").iFocData_getValue();
  
      FocDesc existingTableDesc = notifier.getTableDesc();
      if (existingTableDesc != null) {
        String notifierTableName = existingTableDesc.getStorageName();
        if (notifierTableName != null && !notifierTableName.isEmpty()) {
  
          if (eventTableName.equals(notifierTableName)) {
            sameTable = true;
          }
        }
      }
    }
    return sameTable;
  }

}
