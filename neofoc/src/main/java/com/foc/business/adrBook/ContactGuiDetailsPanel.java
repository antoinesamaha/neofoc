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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

import com.foc.Globals;
import com.foc.desc.FocObject;
import com.foc.gui.FGButton;
import com.foc.gui.FGOptionPane;
import com.foc.gui.FGTextField;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class ContactGuiDetailsPanel extends FPanel {
	
	public static final int VIEW_SELECTION = 1;
	
	private Contact contact = null;
		
  public ContactGuiDetailsPanel(FocObject focObject, int view){
    super("Contact", FPanel.FILL_NONE);   
    contact = (Contact) focObject;
    int y = 0;
    
    if(view == VIEW_SELECTION){
	    FGTextField txtFld = (FGTextField) addField(contact, ContactDesc.FLD_FULL_NAME, 0, y++);
	    txtFld.setEnabled(false);
	    txtFld.setColumns(25);
    }else{
    	add(contact, ContactDesc.FLD_TITLE, 0, y++);
	    add(contact, ContactDesc.FLD_FIRST_NAME, 0, y++);
	    add(contact, ContactDesc.FLD_FAMILY_NAME, 0, y++);
	    add(contact, ContactDesc.FLD_POSITION_STR, 0, y++);
	    add(contact, ContactDesc.FLD_COMPANY_NAME, 0, y);
	    FGButton creatCompanyButton = new FGButton("Create...");
	    add(creatCompanyButton, 2, y);
	    y++;
	    if(contact.getPropertyObject(ContactDesc.FLD_POSITION) != null){
		    add(contact, ContactDesc.FLD_POSITION, 0, y++);	    	
	    }
	    
	    JComponent objField = (JComponent) add(contact, ContactDesc.FLD_ADR_BOOK_PARTY, 0, y++);
	    add(contact, ContactDesc.FLD_PHONE_1, 0, y++);
	    add(contact, ContactDesc.FLD_MOBILE, 0, y++);
	    add(contact, ContactDesc.FLD_EMAIL, 0, y++);
	    add(contact, ContactDesc.FLD_EMAIL_2, 0, y++);
	    //add(contact, ContactDesc.FLD_RELEVANT_USER, 0, y++);
	    
	    if(contact.getFatherSubject() != null && contact.getFatherSubject().getFatherSubject() != null){
	      objField.setEnabled(false);    	
	    }
	    
	    add(contact, ContactDesc.FLD_INTRODUCTION, 0, y++);
	    
	    Component comp = contact.getGuiComponent(ContactDesc.FLD_DESCRIPTION);
	    add(comp, 0, y++, 2, 1);
	
	    FValidationPanel savePanel = showValidationPanel(true);
	    if(savePanel != null){
	      savePanel.addSubject(contact);
	    }
	    
	    creatCompanyButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Contact contact = getContact();
					if(contact != null && !contact.getCompanyName().isEmpty() && contact.getAdrBookParty() == null){
						String originalCompanyName = contact.getCompanyName();
						
						FocList adrBookPartyList = AdrBookPartyDesc.getList(FocList.LOAD_IF_NEEDED);
						AdrBookParty party = (AdrBookParty) adrBookPartyList.searchByPropertyStringValue(AdrBookPartyDesc.FLD_NAME, contact.getCompanyName(), true);
						if(party == null){
							party = (AdrBookParty) adrBookPartyList.newEmptyItem();
							party.setName(contact.getCompanyName());
						}
						AdrBookPartyGuiDetailsPanel detailsPanel = new AdrBookPartyGuiDetailsPanel(party, AdrBookPartyGuiDetailsPanel.VIEW_DEFAULT);
						Globals.getDisplayManager().popupDialog(detailsPanel, "Creation of party : "+contact.getCompanyName(), true);
						if(!party.isCreated() && !party.isDeleted()){
							contact.setAdrBookParty(party);
							FocList contactList = ContactDesc.getList(FocList.LOAD_IF_NEEDED);
							for(int i=0; i<contactList.size(); i++){
								Contact currContact = (Contact) contactList.getFocObject(i);
								if(currContact != null && currContact.getAdrBookParty() == null && !currContact.getCompanyName().isEmpty()){
									if(			!currContact.equalsRef(contact) 
											&& 	(		currContact.getCompanyName().equals(originalCompanyName)
													||  currContact.getCompanyName().equals(contact.getName()))
													){
										String message = "Found also company name match for "+currContact.getFullName()+" do want to include this contact for this company?";
										if(!FGOptionPane.popupOptionPane_YesNo("Confirmation", message)){
											currContact.setAdrBookParty(party);
										}
									}
								}
							}
						}
					}
				}
			});
    }
  }
  
  public void dispose(){
  	super.dispose();
  	contact = null;
  }
  
  public Contact getContact(){
  	return contact;
  }

}
