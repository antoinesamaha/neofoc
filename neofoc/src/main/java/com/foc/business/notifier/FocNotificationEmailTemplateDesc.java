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

import com.foc.business.printing.PrnLayoutDesc;
import com.foc.desc.AutoPopulatable;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class FocNotificationEmailTemplateDesc extends FocDesc implements FocNotificationEmailConst, AutoPopulatable {

  public static final String DB_TABLE_NAME                         = "NOTIF_EMAIL_TEMPLATE";
  
  //Standard Templates
  public static final String USER_CREATION_TEMPLATE                = "User Creation";
  public static final String SUPPLIER_NOTFICATION_NEW_RFQ_TEMPLATE = "Supplier Notification New RFQ";
  public static final String RFQ_SUPPLIER_SUBMIT_TEMPLATE          = "RFQ Supplier Submit";
  public static final String RFQ_SUPPLIER_REJECT_TEMPLATE          = "RFQ Supplier Reject";
  public static final String RFQ_SUPPLIER_REMINDER_TEMPLATE        = "RFQ Supplier Reminder";
  public static final String DEFAULT_PDF_PRINTOUT_SENDING          = "PDF Printout Sending";
  
  public FocNotificationEmailTemplateDesc() {
    super(FocNotificationEmailTemplate.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
    
    FField focFld = addReferenceField();
    
    focFld = new FStringField("NAME", "Name", FLD_TEMPLATE_NAME, false, 200);
    addField(focFld);
    
    focFld = new FStringField("SUBJECT", "Subject", FLD_SUBJECT, false, 1000);
    addField(focFld);
    
    focFld = new FStringField("TEXT", "Text", FLD_TEXT, false, 4000);
    addField(focFld);
    
    focFld = new FStringField("RECIPIENTS", "Recipients", FLD_RECIPIENTS, false, 1000);
    addField(focFld);
    
    focFld = new FStringField("BCC", "Bcc", FLD_BCC, false, 1000);
    addField(focFld);
    
    focFld = new FStringField("CC", "cc", FLD_CC, false, 1000);
    addField(focFld);
    
    focFld = new FStringField("LAYOUT_FILE", "File", FLD_PRN_FILE_NAME, false, PrnLayoutDesc.LEN_LAYOUT_FILE_NAME);
		addField(focFld);

    focFld = new FBoolField("FORMAT", "Html", FLD_HTML, false);
		addField(focFld);
		
    addIsSystemObjectField();
  }
  
  @Override
  public FocList newFocList(){
  	FocList list = super.newFocList();
    list.setDirectlyEditable(false);
    list.setDirectImpactOnDatabase(true);
    if(list.getListOrder() == null){
      FocListOrder order = new FocListOrder(FLD_TEMPLATE_NAME);
      list.setListOrder(order);
    }
    return list;
  }
  
  public static FocList getList(int mode){
    return getInstance().getFocList(mode);
  }
  
  public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, FocNotificationEmailTemplateDesc.class);
  }

  //---------------------------------------------------------
  // AUTOPOPULATE
  //---------------------------------------------------------
  
	private FocNotificationEmailTemplate addEmailTemplate_IfNotExist(FocList list, String name){
		FocNotificationEmailTemplate template = (FocNotificationEmailTemplate) list.searchByPropertyStringValue(FLD_TEMPLATE_NAME, name);
		if(template == null){
			template = (FocNotificationEmailTemplate) list.newEmptyItem();
			template.setName(name);
		}
		return template;
	}
  
	@Override
	public boolean populate() {
		FocList list = FocNotificationEmailTemplateDesc.getInstance().getFocList(FocList.LOAD_IF_NEEDED);
		
		FocNotificationEmailTemplate template = addEmailTemplate_IfNotExist(list, SUPPLIER_NOTFICATION_NEW_RFQ_TEMPLATE);
		template.setSystemObject(true);
		template.setRecipients("$F{SUPPLIER_EMAILLIST}");
		template.setSubject("EVERPRO new RFQ [$F{RFQ.CODE}] from $P{CURRENT_COMPANY.NAME}");
		StringBuffer buffer = new StringBuffer("Dear $F{SUPPLIER_CONTACT.FULL_NAME},\n");
		buffer.append("You have been invited by: '$P{CURRENT_COMPANY.NAME}' ");
		buffer.append("to bid on a new RFQ ($F{RFQ.CODE}).\n");
		buffer.append("\n");
		buffer.append("To reply online please follow this link:\n");
		buffer.append("$P{URL}\n\n");
		buffer.append("You must have already received in a previous email your username and password. If not, please contact '$P{CURRENT_COMPANY.NAME}'");
		buffer.append("\n");
		buffer.append("Regards,\n");
		buffer.append("\n");
		buffer.append("DO NOT REPLY TO THIS EMAIL PLEASE\n");
		template.setText(buffer.toString());
		template.validate(true);
		
		template = addEmailTemplate_IfNotExist(list, USER_CREATION_TEMPLATE);
		template.setSystemObject(true);
		template.setRecipients("$F{NAME}");
		template.setSubject("Congratulations you have a user account at EVERPRO");
		buffer = new StringBuffer("Dear $F{CONTACT.FULL_NAME},\n");
		buffer.append("    Please find here under the user credential to login to Everpro:\n");
		buffer.append("    EVerpro URL: $P{URL}\n");
		buffer.append("    \n");
		buffer.append("    Username: $F{NAME}\n");
		buffer.append("    Password: $F{READABLE_PASSWORD}\n");
		buffer.append("    \n");
		buffer.append("    Please change your username and password upon login.");
		buffer.append("    ");
		buffer.append("    Regards,");
		template.setText(buffer.toString());
		template.validate(true);
		
		template = addEmailTemplate_IfNotExist(list, RFQ_SUPPLIER_SUBMIT_TEMPLATE);
		template.setSystemObject(true);
		template.setRecipients("$P{PROCUREMENT_CONFIG.OUR_PROCUREMENT_EMAIL}");
		template.setSubject("RFQ Supplier Submit");
		buffer = new StringBuffer("Dear $F{COMPANY.NAME},\n");
		buffer.append("    We have reviewed your quotation and we decided to $P{MULTIPLE_CHOICE_LABEL(SBMISSION_STATE)} quotation $F{RFQ.CODE}.\n");
		buffer.append("    For more info please contact $F{SUPPLIER_CONTACT.FULL_NAME}\n");
		buffer.append("    Email: $F{SUPPLIER_CONTACT.ADR_BK_PARTY.EMAIL}\n");
		buffer.append("    Phone: $F{SUPPLIER_CONTACT.ADR_BK_PARTY.PHONE1}\n");
		buffer.append("    Fax: $F{SUPPLIER_CONTACT.ADR_BK_PARTY.FAX}\n");
		buffer.append("    \n");
		buffer.append("    ");
		buffer.append("    Regards,");
		template.setText(buffer.toString());
		template.validate(true);
		
		template = addEmailTemplate_IfNotExist(list, RFQ_SUPPLIER_REJECT_TEMPLATE);
		template.setSystemObject(true);
		template.setRecipients("$P{PROCUREMENT_CONFIG.OUR_PROCUREMENT_EMAIL}");
		template.setSubject("RFQ Supplier Reject");
		buffer = new StringBuffer("Dear $F{COMPANY.NAME},\n");
		buffer.append("    We have reviewed your quotation and we decided to $P{MULTIPLE_CHOICE_LABEL(SBMISSION_STATE)} quotation $F{RFQ.CODE} .\n");
		buffer.append("    For more info please contact $F{SUPPLIER_CONTACT.FULL_NAME}\n");
		buffer.append("    Email: $F{SUPPLIER_CONTACT.ADR_BK_PARTY.EMAIL}\n");
		buffer.append("    Phone: $F{SUPPLIER_CONTACT.ADR_BK_PARTY.PHONE1}\n");
		buffer.append("    Fax: $F{SUPPLIER_CONTACT.ADR_BK_PARTY.FAX}\n");
		buffer.append("    \n");
		buffer.append("    ");
		buffer.append("    Regards,");
		template.setText(buffer.toString());
		template.validate(true);
		
		template = addEmailTemplate_IfNotExist(list, RFQ_SUPPLIER_REMINDER_TEMPLATE);
		template.setSystemObject(true);
		template.setRecipients("");
		template.setSubject("RFQ Supplier Reminder");
		template.setPrintFileName("RFQ Reply / Simple");
		buffer = new StringBuffer("Dear $F{SUPPLIER_CONTACT.FULL_NAME},\n");
		buffer.append("This email to remind you .\n");
		buffer.append("\n");
		buffer.append("\n");
		buffer.append("$P{CURRENT_COMPANY.NAME}");
		buffer.append("egards,");
		template.setText(buffer.toString());
		template.validate(true);
		
		template = addEmailTemplate_IfNotExist(list, DEFAULT_PDF_PRINTOUT_SENDING);
		template.setSystemObject(true);
		template.setRecipients("");
		template.setSubject("Email sent by $P{CURRENT_COMPANY.NAME} using EVERPRO");
		buffer = new StringBuffer("Hello,\n");
		buffer.append("\n");
		buffer.append("Kindly find the attached document sent to you on behalf of $P{CURRENT_COMPANY.NAME} using EVERPRO software.\n");
		buffer.append("\n");
		buffer.append("\n");
		buffer.append("Regards,");
		buffer.append("\n");
		buffer.append("\n");
		buffer.append("DO NOT REPLY TO THIS EMAIL PLEASE\n");
		
		template.setText(buffer.toString());
		template.validate(true);
		
		list.validate(true);
		
		return false;
	}

	@Override
	public String getAutoPopulatableTitle() {
		return "e-Mail Templates";
	}  
	
	public static FocNotificationEmailTemplate getFocNotificationEmailTemplateByName(String name){
		FocNotificationEmailTemplate focNotificationEmailTemplate = null;
		if(FocNotificationEmailTemplateDesc.getInstance() != null){
			FocList focNotificationEmailTemplateList = FocNotificationEmailTemplateDesc.getInstance().getFocList();
			focNotificationEmailTemplate = (FocNotificationEmailTemplate) focNotificationEmailTemplateList.searchByPropertyStringValue(FocNotificationEmailTemplateDesc.FLD_TEMPLATE_NAME, name);
		}
		return focNotificationEmailTemplate;
	}
	
}
