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

import com.foc.admin.FocUserDesc;
import com.foc.business.company.CompanyDesc;
import com.foc.business.workflow.WFSiteDesc;
import com.foc.business.workflow.WFTitleDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FDateField;
import com.foc.desc.field.FIntField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;

public class FocPageDesc extends FocDesc {
  
	public final static int FLD_TABLE_NAME        = 1;
	public final static int FLD_TABLE_REF         = 2;
	public final static int FLD_VIEW_TYPE         = 3;
	public final static int FLD_VIEW_CONTEXT      = 4;
	public final static int FLD_USER_VIEW         = 5;
	public final static int FLD_VIEW_STORAGE_NAME = 6;
	public final static int FLD_FOC_OBJ_CLASSNAME = 7;
	public final static int FLD_TITLE             = 8;
	public final static int FLD_COMPANY           = 9;
	public final static int FLD_SITE              = 10;
	public final static int FLD_USER              = 11;
	public final static int FLD_SERIALISATION     = 12;
	public final static int FLD_CREATION_DATE     = 13;
	public final static int FLD_CREATOR_USER      = 14;
	
	public final static int MAX_NUMBER_OF_FIELDS_RESERVED = 1000;
	
	public FocPageDesc(Class<?> focObjectClass, String storageName) {
    super(focObjectClass, FocDesc.DB_RESIDENT, storageName, false);
    
    addReferenceField();
    
    FStringField focFld = new FStringField("TABLE_NAME", "Table Name",FLD_TABLE_NAME , false, 200);
    addField(focFld);
    
    FIntField intFld = new FIntField("TABLE_REF", "Table Reference", FLD_TABLE_REF, false, 4);
    addField(intFld);

    intFld = new FIntField("VIEW_TYPE", "View Type", FLD_VIEW_TYPE, false, 4);
    addField(intFld);
    
    focFld = new FStringField("VIEW_CONTEXT", "View Context",FLD_VIEW_CONTEXT , false, 50);
    addField(focFld);
    
    focFld = new FStringField("USER_VIEW", "User View",FLD_USER_VIEW , false, 50);
    addField(focFld);
    
    focFld = new FStringField("VIEW_STORAGE_NAME", "View Storage Name",FLD_VIEW_STORAGE_NAME , false, 50);
    addField(focFld);
    
    focFld = new FStringField("FOC_OBJ_CLASS_NAME", "FocObject class Name",FLD_FOC_OBJ_CLASSNAME, false, 300);
    addField(focFld);

    focFld = new FStringField("SERIALISATION", "Serialisation", FLD_SERIALISATION, false, 4000);
    addField(focFld);

    FObjectField usefFld = new FObjectField("USER", "User", FLD_USER, FocUserDesc.getInstance());
    usefFld.setSelectionList(FocUserDesc.getList(FocList.NONE));
    usefFld.setComboBoxCellEditor(FocUserDesc.FLD_NAME);
    usefFld.setDisplayField(FocUserDesc.FLD_NAME);
    addField(usefFld);
    
    FObjectField  objFld = new FObjectField("TITLE", "Title", FLD_TITLE, WFTitleDesc.getInstance());
		objFld.setSelectionList(WFTitleDesc.getList(FocList.NONE));
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setComboBoxCellEditor(WFTitleDesc.FLD_NAME);
		objFld.setDisplayField(WFTitleDesc.FLD_NAME);
		addField(objFld);
    
		FObjectField fObjectFld = new FObjectField("COMPANY", "Company", FLD_COMPANY, CompanyDesc.getInstance());
    fObjectFld.setComboBoxCellEditor(CompanyDesc.FLD_NAME);
    fObjectFld.setDisplayField(CompanyDesc.FLD_NAME);
    fObjectFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    addField(fObjectFld);

		objFld = new FObjectField("WF_SITE", "Site", FLD_SITE, WFSiteDesc.getInstance());
	  objFld.setComboBoxCellEditor(WFSiteDesc.FLD_NAME);
	  objFld.setSelectionList(WFSiteDesc.getList(FocList.NONE));
	  objFld.setWithList(false);
		addField(objFld);
		
		FDateField dateField = new FDateField("CREATION_DATE", "Creation Date", FLD_CREATION_DATE, false);
    addField(dateField);
    
    fObjectFld = new FObjectField("CREATOR_USER", "Creator User", FLD_CREATOR_USER, FocUserDesc.getInstance());
    fObjectFld.setComboBoxCellEditor(FocUserDesc.FLD_NAME);
    fObjectFld.setDisplayField(FocUserDesc.FLD_NAME);
    fObjectFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    fObjectFld.setSelectionList(FocUserDesc.getList(FocList.NONE));
    addField(fObjectFld);
  }
}
