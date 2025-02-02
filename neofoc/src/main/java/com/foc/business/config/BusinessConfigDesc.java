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
package com.foc.business.config;

import com.foc.Globals;
import com.foc.admin.FocGroupDesc;
import com.foc.admin.FocVersion;
import com.foc.business.BusinessModule;
import com.foc.business.notifier.FocNotificationEmailTemplateDesc;
import com.foc.business.workflow.WFTitleDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FCompanyField;
import com.foc.desc.field.FIntField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;

public class BusinessConfigDesc extends FocDesc {
  public static final int FLD_ADR_BOOK_PARTY_PREFIX                             =  1;
  public static final int FLD_ADR_BOOK_PARTY_NBR_DIGITS                         =  2;
  public static final int FLD_ADR_BOOK_PARTY_SEPERATOR                          =  3;
  public static final int FLD_ADR_BOOK_PARTY_RESET_NUMBERING_WHEN_PREFIX_CHANGE =  4;
  public static final int FLD_ALLOW_MODIF_OF_UND_DESC_EVEN_IF_FILLED_IN_UND     =  5;
  
  public static final int FLD_PROJECT_ID_PREFIX                                 =  6;
  public static final int FLD_PROJECT_ID_NBR_DIGITS                             =  7;
  
  public static final int FLD_ADR_BOOK_TO_PARTY_ONE_2_ONE                       =  8;//"Create an Address book by party without prompt"
  public static final int FLD_CONTACT_IN_PARTY_MANDATORY                        =  9;//"Is Contact mandatory in address book party"
  public static final int FLD_GENERAL_EMAIL_TEMPLATE                            =  10;
  
  //Guest account creation
  public static final int FLD_EmailTmplUserCreation                             =  11;
  public static final int FLD_EmailTmplPwdChange                                =  12;
  public static final int FLD_GuestUserGroup                                    =  13;
  public static final int FLD_GuestUserTitle                                    =  14;
  
  public static final String DB_TABLE_NAME = "CONFIG_BUSINESS";
  
  public BusinessConfigDesc(){
    super(BusinessConfig.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);

    addReferenceField();
    
    /*
    FObjectField objField = new FObjectField("COST_BREAKDOWN", "Cost Breakdown Dimension", FLD_COST_BREAKDOWN_DIMENSION, false, ClasseDesc.getInstance(), "COST_BREAKDOWN_DIM_");
    objField.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    objField.setComboBoxCellEditor(ClasseDesc.FLD_CODE);
    //objField.setSelectionList(ClasseDesc.getRootList(FocList.NONE));
    addField(objField);
    */
    
    FObjectField oFld = new FObjectField("GENERAL_EMAIL_TEMPLATE", "General Email Template", FLD_GENERAL_EMAIL_TEMPLATE, FocNotificationEmailTemplateDesc.getInstance());
    addField(oFld);

    oFld = new FObjectField("EmailTmplUserCreation", "User Creation Email Template", FLD_EmailTmplUserCreation, FocNotificationEmailTemplateDesc.getInstance());
    addField(oFld);

    oFld = new FObjectField("EmailTmplPwdChange", "Password Change Email Template", FLD_EmailTmplPwdChange, FocNotificationEmailTemplateDesc.getInstance());
    addField(oFld);

    oFld = new FObjectField("GuestUserGroup", "FocUser Group", FLD_GuestUserGroup, FocGroupDesc.getInstance());
    FocList focList = FocGroupDesc.getInstance().getFocList(FocList.NONE);
    oFld.setSelectionList(focList);
    addField(oFld);
    
		FObjectField objFld = new FObjectField("GuestUserTitle", "Guest Title", FLD_GuestUserTitle, WFTitleDesc.getInstance());
		objFld.setSelectionList(WFTitleDesc.getList(FocList.NONE));
		addField(objFld);
    
    FCompanyField companyField = new FCompanyField(false, true);
    addField(companyField);
    
    FStringField cFld = new FStringField("PARTY_PREFIX", "Party code prefix", FLD_ADR_BOOK_PARTY_PREFIX, false, 6);
    addField(cFld);
    
    FIntField iFld = new FIntField("PARTY_NBR_DIGITS", "Party code number of digits", FLD_ADR_BOOK_PARTY_NBR_DIGITS, false, 2);
    addField(iFld);

    cFld = new FStringField("PARTY_CODE_SEPARATOR", "Party code seperator", FLD_ADR_BOOK_PARTY_SEPERATOR, false, 1);
    addField(cFld);

    FBoolField bFld = new FBoolField("PARTY_RESET_NBR", "Party code reset numbering when change prefix", FLD_ADR_BOOK_PARTY_RESET_NUMBERING_WHEN_PREFIX_CHANGE, false);
    addField(bFld);
    
    bFld = new FBoolField("ALLOW_MODIF_UND_DESC_WHEN_FILL", "Allow Modification of underlying description even when filled in underlying", FLD_ALLOW_MODIF_OF_UND_DESC_EVEN_IF_FILLED_IN_UND, false);
    addField(bFld);
    
    bFld = new FBoolField("ADR_BOOK_TO_PARTY_ONE_2_ONE", "Create an Address book by party without prompt", FLD_ADR_BOOK_TO_PARTY_ONE_2_ONE, false);
    addField(bFld);
    
    bFld = new FBoolField("CONTACT_IN_PARTY_MANDATORY", "Is contact mandatory in address book party", FLD_CONTACT_IN_PARTY_MANDATORY, false);
    addField(bFld);
    
    cFld = new FStringField("PROJECT_ID_PREFIX", "Project Id code prefix", FLD_PROJECT_ID_PREFIX, false, 6);
    addField(cFld);
    
    iFld = new FIntField("PROJECT_ID_NBR_DIGITS", "Project Id code number of digits", FLD_PROJECT_ID_NBR_DIGITS, false, 2);
    addField(iFld);
  }
  
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public static FocList getList(int mode){
    return getInstance().getFocList(mode);
  }
  
  @Override
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
    return getInstance(DB_TABLE_NAME, BusinessConfigDesc.class);    
  }
}
