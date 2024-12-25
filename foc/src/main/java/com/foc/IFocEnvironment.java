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
package com.foc;

import java.util.Iterator;

import com.fab.gui.xmlView.IXMLViewDictionary;
import com.foc.admin.UserSession;
import com.foc.desc.FocObject;
import com.foc.list.FocList;
import com.foc.shared.IFocMobileModule;
import com.foc.shared.IFocWebModuleShared;
import com.foc.shared.dataStore.IFocData;
import com.foc.shared.dataStore.IFocDataDictionary;

public interface IFocEnvironment {
	public static final int TYPE_NOT_DEFINED       = -1;//Notification.TYPE_ERROR_MESSAGE;
  public static final int TYPE_ERROR_MESSAGE     = 0;//Notification.TYPE_ERROR_MESSAGE;
  public static final int TYPE_HUMANIZED_MESSAGE = 1;//Notification.TYPE_HUMANIZED_MESSAGE;
  public static final int TYPE_TRAY_NOTIFICATION = 2;//Notification.TYPE_TRAY_NOTIFICATION;
  public static final int TYPE_WARNING_MESSAGE   = 3;//Notification.TYPE_WARNING_MESSAGE;
  public static final int TYPE_TRAY_MESSAGE      = 4;//Notification.TYPE_TRAY_MESSAGE;
  
  public String      getThemeName();
  public void        setNotificationsEnabled(boolean enabled);
  public void        showNotification(String notificationMessage, String description, int notificationType);
//  public void        showNotification(String notificationMessage, String description, int notificationType, int delay);
  public void        showNotification(String notificationMessage, String description, int notificationType, int delay, String styleName);
  public void        popup(FocObject focObject, boolean dialog);
  public void        popup(IFocData focData, boolean dialog, String storageName, int type, String context, String view);
  public UserSession getUserSession();
  public Application getFocApplication();
  public void        setFocApplication(Application app);
  public Object      popupOptionDialog(OptionDialog dialog);
  public IXMLViewDictionary getXMLViewDictionary();
  public FocList     getMobileFocList_FromSession(String listSessionId);
  public void        setMobileFocList_ForSession(FocList focList, String listSessionId);
  public String      getSessionID();
  public void        applyUserTheme();
  public Iterator<IFocWebModuleShared> newWebModuleIterator();
  public Iterator<IFocMobileModule> newMobileModuleIterator();
  public IFocDataDictionary getFocDataDictionary();
  //public int  popupOptionDialog(String message, String[] options);
}
