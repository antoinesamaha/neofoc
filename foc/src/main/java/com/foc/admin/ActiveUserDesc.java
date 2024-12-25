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
// EXTERNAL PROPERTIES
// INSTANCE
//    MAIN
//    PANEL
// LIST
// DESCRIPTION

/*
 * Created on 20-May-2005
 */
package com.foc.admin;

import com.foc.business.company.CompanyDesc;
import com.foc.business.workflow.WFSiteDesc;
import com.foc.business.workflow.WFTitleDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FDateTimeField;
import com.foc.desc.field.FMultipleChoiceStringField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;

/**
 * @author 01Barmaja
 */
public class ActiveUserDesc extends FocDesc{
	
	public final static int FLD_USER             =  1;
	public final static int FLD_COMPANY          =  2;
	public final static int FLD_LAST_HEART_BEAT  =  3;
	public final static int FLD_SITE             =  4;
	public final static int FLD_TITLE            =  5;
	public final static int FLD_ORIGIN           =  6;

  public static final String DB_TABLE_NAME = "ACTIVE_USER";
  
	public static final String ORIGIN_DESKTOP = "Desktop";
	public static final String ORIGIN_MOBILE  = "Mobile";
	
  public ActiveUserDesc() {
    super(ActiveUser.class, FocDesc.NOT_DB_RESIDENT, DB_TABLE_NAME, true);

    addReferenceField();
    
    FObjectField fObjectFld = new FObjectField("USER", "User", FLD_USER, FocUserDesc.getInstance());
    addField(fObjectFld);
    
    fObjectFld = new FObjectField("COMPANY", "Company", FLD_COMPANY, CompanyDesc.getInstance());
    addField(fObjectFld);
    
    FDateTimeField dtf = new FDateTimeField("LAST_HEART_BEAT", "Last Heart Beat", FLD_LAST_HEART_BEAT, false);
    addField(dtf);
    
  	FObjectField objFld = new FObjectField("SITE", "Site", FLD_SITE, WFSiteDesc.getInstance());
  	objFld.setWithList(false);
  	addField(objFld);

  	objFld = new FObjectField("TITLE", "Title", FLD_TITLE, WFTitleDesc.getInstance());
  	objFld.setWithList(false);
  	addField(objFld);
  	
		FMultipleChoiceStringField originField = new FMultipleChoiceStringField("Origin", "Origin", FLD_ORIGIN, false, 100);
    originField.addChoice(ORIGIN_DESKTOP);
    originField.addChoice(ORIGIN_MOBILE);
    addField(originField);
  }
  
  public FocList newFocList(){
    return super.newFocList();
  }
  
  public static ActiveUserDesc getInstance(){
    return (ActiveUserDesc) getInstance(DB_TABLE_NAME, ActiveUserDesc.class);
  }
}
