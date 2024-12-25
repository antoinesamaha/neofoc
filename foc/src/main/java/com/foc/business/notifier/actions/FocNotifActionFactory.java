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
package com.foc.business.notifier.actions;

import java.util.HashMap;

import com.foc.Globals;
import com.foc.business.notifier.FNotifTrigger;

@SuppressWarnings("serial")
public class FocNotifActionFactory extends HashMap<Integer, IFocNotifAction>{
  
  public FocNotifActionFactory(){
    put(FNotifTrigger.ACTION_SEND_EMAIL, new FocNotifAction_SendEmail());
    put(FNotifTrigger.ACTION_EXECUTE_REPORT, new FocNotifAction_SendReportByEmail());
  }
  
  public static FocNotifActionFactory getInstance(boolean createIfNeeded) {
    FocNotifActionFactory focNEF = null;
    if (Globals.getApp() != null) {
      focNEF = Globals.getApp().getNotificationActionFactory(createIfNeeded);
    }
    return focNEF;
  }
  
  public static FocNotifActionFactory getInstance(){
    return getInstance(true);
  }
}
