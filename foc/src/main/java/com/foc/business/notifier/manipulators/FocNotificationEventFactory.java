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

import java.util.HashMap;

import com.foc.Globals;
import com.foc.business.notifier.FocNotificationConst;


@SuppressWarnings("serial")
public class FocNotificationEventFactory extends HashMap<Integer, IFocNotificationEventManipulator>{
  
  public FocNotificationEventFactory(){
    put(FocNotificationConst.EVT_CREATE_USER_FROM_CONTACT, new FocNotificationUserFromContactManipulator());
    put(FocNotificationConst.EVT_TABLE_ADD, new FocNotificationTableAddManipulator());
    put(FocNotificationConst.EVT_TABLE_DELETE, new FocNotificationTableDeleteManipulator());
    put(FocNotificationConst.EVT_TABLE_UPDATE, new FocNotificationTableUpdateManipulator());
  }
  
  public static FocNotificationEventFactory getInstance(boolean createIfNeeded) {

    FocNotificationEventFactory focNEF = null;
    if (Globals.getApp() != null) {
      focNEF = Globals.getApp().getNotificationEventFactory(createIfNeeded);
    }
    return focNEF;
  }
  
  public static FocNotificationEventFactory getInstance(){
    return getInstance(true);
  }
}
