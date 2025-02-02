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

import com.foc.Globals;
import com.foc.db.DBManager;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;

public class FocNotificationEmailDesc extends FocDesc implements FocNotificationEmailConst {

  public static final String DB_TABLE_NAME = "NOTIF_EMAIL";
  
  public FocNotificationEmailDesc() {
    super(FocNotificationEmail.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
    
    FField focFld = addReferenceField();
    
    focFld = new FStringField("SENDER", "Sender", FLD_SENDER, false, 200);
    addField(focFld);
    
    focFld = new FStringField("RECIPIENTS", "Recipients", FLD_RECIPIENTS, false, 1000);
    addField(focFld);
    
    focFld = new FStringField("BCC", "Bcc", FLD_BCC, false, 1000);
    addField(focFld);
    
    focFld = new FStringField("CC", "cc", FLD_CC, false, 1000);
    addField(focFld);
    
    focFld = new FStringField("SUBJECT", "Subject", FLD_SUBJECT, false, 1000);
    addField(focFld);
    
		if(getProvider() == DBManager.PROVIDER_ORACLE) {
		  focFld = new FStringField("TEXTOLD", "TextOld", FLD_TEXT_OLD, false, 4000);
		  addField(focFld);
		}
    
    focFld = new FStringField("TEXT", "Text", FLD_TEXT, false, 5000);
    addField(focFld);
    
    FObjectField objFld = new FObjectField("TEMPLATE", "Template", FLD_TEMPLATE_NAME, FocNotificationEmailTemplateDesc.getInstance());
    objFld.setComboBoxCellEditor(FocNotificationEmailTemplateDesc.FLD_TEMPLATE_NAME);
    objFld.setDisplayField(FocNotificationEmailTemplateDesc.FLD_TEMPLATE_NAME);    
    objFld.setSelectionList(FocNotificationEmailTemplateDesc.getList(FocList.NONE));
    addField(objFld);
  }
  
  public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, FocNotificationEmailDesc.class);
  }

}
