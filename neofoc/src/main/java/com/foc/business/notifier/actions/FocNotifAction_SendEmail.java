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

import com.foc.business.notifier.FNotifTrigger;
import com.foc.business.notifier.FocNotificationEmail;
import com.foc.business.notifier.FocNotificationEmailDesc;
import com.foc.business.notifier.FocNotificationEmailTemplate;
import com.foc.business.notifier.FocNotificationEvent;
import com.foc.desc.FocConstructor;

public class FocNotifAction_SendEmail extends FocNotifAction_Abstract {

	@Override
	public String execute(FNotifTrigger notifier, FocNotificationEvent event) {
    FocNotificationEmailTemplate template = (FocNotificationEmailTemplate) notifier.getTemplate();
    if(template != null) {
	    FocNotificationEmail email = new FocNotificationEmail(new FocConstructor(FocNotificationEmailDesc.getInstance(), null), template, event != null ?event.getEventFocData() : null);
	    email.send();
	    email.setCreated(true);
	    email.validate(true);
    }		
    return null;
	}

}
