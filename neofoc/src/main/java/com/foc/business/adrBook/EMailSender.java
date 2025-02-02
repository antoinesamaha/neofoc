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
package com.foc.business.adrBook;

import com.foc.Globals;
import com.foc.IEMailSender;
import com.foc.list.FocList;
import com.foc.list.FocListElement;
import com.foc.util.EMail;

/**
 * @author Antoine
 * 
 * This is used for the DOS old version. It was used to open Outlook by calling the executable...
 *
 */
public class EMailSender implements IEMailSender {
	
	@Override
	public boolean sendEMail_SelectContactAndPopupEMail(String contactFilterExpression, String attachment){
		boolean error = false;
		FocList list = ContactDesc.getList(FocList.LOAD_IF_NEEDED);
		ContactGuiBrowsePanel browse = new ContactGuiBrowsePanel(list, ContactGuiBrowsePanel.VIEW_MULTIPLE_SELECTION);
		Globals.getDisplayManager().popupDialog(browse, "Select Contacts", true);
		
		String toEmail = "";
		
		for(int i=0; i<list.size(); i++){
			FocListElement elem = list.getFocListElement(i);
			if(elem.isSelected()){
				elem.setSelected(false);
				Contact contact = (Contact)elem.getFocObject();
				if(!contact.getEMail().isEmpty()){
					if(toEmail.isEmpty()){
						toEmail += contact.getEMail();
					}else{
						toEmail += ";"+contact.getEMail();
					}
				}
				if(!contact.getEMail2().isEmpty()){
					if(toEmail.isEmpty()){
						toEmail += contact.getEMail2();
					}else{
						toEmail += ";"+contact.getEMail2();
					}
				}
			}
		}
		EMail.sendMail(toEmail, attachment);
		return error;
	}

}
