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
package com.foc.ecomerce;

import com.foc.admin.FocGroupDesc;
import com.foc.business.notifier.FocNotificationEmailTemplateDesc;
import com.foc.business.workflow.WFSiteDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FCompanyField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;

public class EComConfigurationDesc extends FocDesc {
  
  public static final int FLD_EMAIL_SUBJECT                       = 1;
  public static final int FLD_EMAIL_CONTENT                       = 2;
  public static final int FLD_TEMPLATE_AFTER_ACTIVATION           = 3;
  public static final int FLD_TEMPLATE_BEFORE_ACTIVATION          = 4;
	public static final int FLD_GUEST_USER_GROUP                    = 5;
  public static final int FLD_GUEST_USER_SITE                     = 6;
  public static final int FLD_GUEST_USER_TITLE                    = 7;//TODO: This ID is not used today
  public static final int FLD_TEMPLATE_USER_CREATION              = 8;
  public static final int FLD_TEMPLATE_PASSWORD_RESET             = 9;
	
	public static final String DB_TABLE_NAME = "ECOM_CONFIG";
	
  public EComConfigurationDesc(){
    super(EComConfiguration.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);

    addReferenceField();
   
    FCompanyField compField = new FCompanyField(false, true);
    addField(compField);

    FObjectField oFld = new FObjectField("EMAIL_TEMPLATE_BEFORE_ACTIVATION", "Template Before Activation", FLD_TEMPLATE_BEFORE_ACTIVATION, FocNotificationEmailTemplateDesc.getInstance());
    FocList focList = FocNotificationEmailTemplateDesc.getInstance().getFocList(FocList.NONE);
    oFld.setSelectionList(focList);
    addField(oFld);
    
    oFld = new FObjectField("EMAIL_TEMPLATE_AFTER_ACTIVATION", "Template After Activation", FLD_TEMPLATE_AFTER_ACTIVATION, FocNotificationEmailTemplateDesc.getInstance());
    focList = FocNotificationEmailTemplateDesc.getInstance().getFocList(FocList.NONE);
    oFld.setSelectionList(focList);
    addField(oFld);
    
    oFld = new FObjectField("GUEST_USER_GROUP", "FocUser Group", FLD_GUEST_USER_GROUP, FocGroupDesc.getInstance());
    focList = FocGroupDesc.getInstance().getFocList(FocList.NONE);
    oFld.setSelectionList(focList);
    addField(oFld);
        
    FObjectField objectField = WFSiteDesc.newSiteField("GUEST_USER_SITE", FLD_GUEST_USER_SITE, false);
    addField(objectField);
    
    oFld = new FObjectField("EMAIL_TEMPLATE_USER_CREATION", "Template User Creation", FLD_TEMPLATE_USER_CREATION, FocNotificationEmailTemplateDesc.getInstance());
    oFld.setSelectionList(FocNotificationEmailTemplateDesc.getInstance().getFocList(FocList.NONE));
    addField(oFld);
    
    oFld = new FObjectField("EMAIL_TEMPLATE_PASSWORD_RESET", "Template Password Reset", FLD_TEMPLATE_PASSWORD_RESET, FocNotificationEmailTemplateDesc.getInstance());
    oFld.setSelectionList(FocNotificationEmailTemplateDesc.getInstance().getFocList(FocList.NONE));
    addField(oFld);
  }
  
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public static FocList getList(int mode){
  	return getInstance().getFocList(mode);
  }
  
  public FocList newFocList(){
    FocList list = super.newFocList();
    list.setDirectlyEditable(false);
    list.setDirectImpactOnDatabase(true);
    return list;    
  }
    
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static FocDesc getInstance() {
  	return getInstance(DB_TABLE_NAME, EComConfigurationDesc.class);
  }
}
