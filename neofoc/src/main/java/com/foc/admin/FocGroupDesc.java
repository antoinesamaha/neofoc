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

import com.foc.Globals;
import com.foc.business.workflow.WFTitleDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldPath;
import com.foc.desc.field.FListField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FObjectField;
import com.foc.link.FocLinkInRightsDesc;
import com.foc.link.FocLinkOutRightsDesc;
import com.foc.list.FocLink;
import com.foc.list.FocLinkOne2One;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

/**
 * @author 01Barmaja
 */
public class FocGroupDesc extends FocDesc{

  public static final int FLD_NAME                       = 1;
  public static final int FLD_DESCRIPTION                = 2;
  public static final int FLD_APP_GROUP                  = 3;
  public static final int FLD_ALLOW_NAMING_MODIF         = 4;
  public static final int FLD_CASH_DESKS_ACCESS          = 5;
  public static final int FLD_RIGHTS_LEVEL               = 6;
  public static final int FLD_ALLOW_CURRENCY_RATES_MODIF = 7;
  public static final int FLD_ALLOW_REPORT_ACCESS        = 8;
  public static final int FLD_ALLOW_DATABASE_BACKUP      = 9;
  public static final int FLD_ALLOW_DATABASE_RESTORE     = 10;
//  public static final int FLD_ALLOW_STATUS_MANUAL_MODIF  = 11;
  public static final int FLD_ALLOW_AREA_MODIF           = 12;
  public static final int FLD_ALLOW_IMPORT               = 13;
  public static final int FLD_ALLOW_EXPORT               = 14;
  public static final int FLD_GUEST_APPLICABLE           = 15;
  public static final int FLD_STARTUP_MENU               = 16;
  public static final int FLD_ALLOW_REPORT_CREATION      = 17;
  public static final int FLD_DEFAULT_TITLE              = 18;
  public static final int FLD_DASHBOARD_CONTEXT          = 19;
  public static final int FLD_ALLOW_INSERT_IN_COMBOBOX   = 20;
  public static final int FLD_ALLOW_DELETE_ALL_UNUSED_USERS = 21;
  
  public static final int FLD_MENU_RIGHTS_LIST            = 50;
  public static final int FLD_VIEWS_RIGHTS_LIST           = 51;
  public static final int FLD_WEB_MODULE_RIGHTS_LIST      = 52;
  public static final int FLD_MENU_ACCESS_RIGHTS_WEB_LIST = 53;
  public static final int FLD_XML_VIEW_RIGHTS_LIST        = 54;
  public static final int FLD_LINK_OUT_RIGHTS             = 55;
  public static final int FLD_LINK_IN_RIGHTS              = 56;
  public static final int FLD_MOBILE_MODULE_RIGHTS_LIST   = 57;
  public static final int FLD_VIEWS_RIGHT                 = 58;
  public static final int FLD_ACCESS_DB_LOG               = 59;
  
  public static int FLD_START_APP_GROUPS = 100;
  private int numberOfAppGroupListFieldID = 0; 
  
  public static final int CASH_ACCESS_NONE = 0;
  public static final int CASH_ACCESS_VIEWER = 1; 
  public static final int CASH_ACCESS_COMPLETE = 2;
  public static final int CASH_ACCESS_OWNER = 3;  
  
  public static final String DB_TABLE_NAME = "FGROUP";
  
  public static final String FNAME_GUEST_APPLICABLE = "GUEST_APPLICABLE";
  
  private static FocLinkOne2One link_AppGroup = null;
  
  public FocLinkOne2One getLink_AppGroup(){
    if(link_AppGroup == null){
    	if(FocGroup.getApplicationGroup() != null){
    		link_AppGroup = new FocLinkOne2One(this, FocGroup.getApplicationGroup());
    	}
    }
    return link_AppGroup;
  }
  
  public FocGroupDesc() {
    super(FocGroup.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, true);
    FField focFld = null;

    addReferenceField();

    focFld = new FStringField("NAME", "Name", FLD_NAME, true, FStringField.NAME_LEN);
    focFld.setMandatory(true);
    addField(focFld);
    focFld.setLockValueAfterCreation(true);

    focFld = new FStringField("DESCRIP", "Description", FLD_DESCRIPTION, false, FStringField.DESC_LEN);
    addField(focFld);
    
    FObjectField objFld = new FObjectField("DEFAULT_TITLE", "Default title", FLD_DEFAULT_TITLE, WFTitleDesc.getInstance());
    objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    addField(objFld);
    
    focFld = new FStringField("STARTUP_MENU", "Startup Menu", FLD_STARTUP_MENU, false, 150);
    addField(focFld);

    focFld = new FStringField("DASHBOARD_CONTEXT", "Dashboard Context", FLD_DASHBOARD_CONTEXT, false, 150);
    addField(focFld);

    focFld = new FBoolField("NAME_MODF", "Allow Naming Modification", FLD_ALLOW_NAMING_MODIF, false);
    addField(focFld);

    focFld = new FBoolField("AREA_MANUAL_MODF", "Allow Area Manual Modification", FLD_ALLOW_AREA_MODIF, false);
    addField(focFld);

    focFld = new FBoolField("ALLOW_INSERT_IN_COMBOBOX", "Allow Insert In ComboBox", FLD_ALLOW_INSERT_IN_COMBOBOX, false);
    addField(focFld);

    focFld = new FBoolField("ALLOW_IMPORT", "Allow Import", FLD_ALLOW_IMPORT, false);
    addField(focFld);
    
    focFld = new FBoolField("ALLOW_EXPORT", "Allow Export", FLD_ALLOW_EXPORT, false);
    addField(focFld);
    
    focFld = new FBoolField(FNAME_GUEST_APPLICABLE, "Guest Applicable", FLD_GUEST_APPLICABLE, false);
    addField(focFld);
    
    if(getLink_AppGroup() != null){
	    focFld = new FListField("APP_GRP", "Application group", FLD_APP_GROUP, getLink_AppGroup());
	    addField(focFld);
    }
    
    if(Globals.getApp().isCurrencyModuleIncluded()){
      focFld = new FBoolField("FX_RTE_MODIF", "Allow Currency rates modification", FLD_ALLOW_CURRENCY_RATES_MODIF, false);
      addField(focFld);
    }

    if(Globals.getApp().isWithReporting()){
      focFld = new FBoolField("REPORT_ACCESS", "Allow reports access", FLD_ALLOW_REPORT_ACCESS, false);
      addField(focFld);
    }
    
    focFld = new FBoolField("ALLOW_REPORT_CREATION", "Allow reports creation", FLD_ALLOW_REPORT_CREATION, false);
    addField(focFld);
    
    focFld = new FBoolField("DB_BACKUP", "Allow DB backup", FLD_ALLOW_DATABASE_BACKUP, false);
    addField(focFld);

    focFld = new FBoolField("DB_RESTORE", "Allow DB restore", FLD_ALLOW_DATABASE_RESTORE, false);
    addField(focFld);

    if(Globals.getApp().isCashDeskModuleIncluded()){
      FMultipleChoiceField focMultiFld = new FMultipleChoiceField("CASH_ACCESS", "Cash desk access", FLD_CASH_DESKS_ACCESS, false, 1);
      focMultiFld.addChoice(CASH_ACCESS_NONE, "None");
      focMultiFld.addChoice(CASH_ACCESS_VIEWER, "Viewer");
      focMultiFld.addChoice(CASH_ACCESS_COMPLETE, "Complete");
      addField(focMultiFld);
    }
    
    if(Globals.getApp().isWithRightsByLevel()){
      FMultipleChoiceField multiFocFld = new FMultipleChoiceField("RGHT_LEVEL", "Rights level", FLD_RIGHTS_LEVEL, false, 2);
      for(int i=1; i<=Globals.getApp().getRightsByLevel().getNbOfLevels(); i++){
        multiFocFld.addChoice(i, String.valueOf(i));
      }
      addField(multiFocFld);
    }
    
    focFld = new FObjectField("LINK_OUT_RIGHTS", "Link Out Rights", FLD_LINK_OUT_RIGHTS, FocLinkOutRightsDesc.getInstance());
    addField(focFld);
    
    focFld = new FObjectField("LINK_IN_RIGHTS", "Link In Rights", FLD_LINK_IN_RIGHTS, FocLinkInRightsDesc.getInstance());
    addField(focFld);
    
    FMultipleChoiceField multipleChoiceField = GroupXMLViewDesc.getViewRightField("VIEWS_RIGHT", "Views Right", FLD_VIEWS_RIGHT, false, 2);
    addField(multipleChoiceField);
    
    focFld = new FBoolField("DELETE_UNUSED_USERS", "Allow DB restore", FLD_ALLOW_DELETE_ALL_UNUSED_USERS, false);
    addField(focFld);
    
    focFld = new FBoolField("ACCESS_DB_LOG", "Access DB Log", FLD_ACCESS_DB_LOG, false);
    addField(focFld);
  }
  
  public int getAndIncrementNumberOfAppGroupListFieldID(){
    int number = FLD_START_APP_GROUPS + numberOfAppGroupListFieldID;
    numberOfAppGroupListFieldID++;
    return number;
  }
  
  public int getNumberOfAppGroupListFieldID(){
    return numberOfAppGroupListFieldID;
  }

  public FocList newFocList(){
    FocList list = null;
    FocLink link = new FocLinkSimple(this);
    list = new FocList(link);
    
    list.setDirectImpactOnDatabase(true);
    list.setDirectlyEditable(false);

    FocListOrder listOrder = new FocListOrder();
    listOrder.addField(FFieldPath.newFieldPath(FocGroupDesc.FLD_NAME));
    list.setListOrder(listOrder);    
    return list;
  }

  public FocGroup findGroupByName(String name){
  	FocGroup group = null;
  	FocList list = getFocList();
  	if(list != null){
  		group = (FocGroup) list.searchByPropertyStringValue(FLD_NAME, name);
  	}
  	return group;
  }
  
  public static FocGroupDesc getInstance(){
    return (FocGroupDesc) getInstance(DB_TABLE_NAME, FocGroupDesc.class);
  }
  
  public FocGroup findGroup_ApplicableForGuest(){
  	FocGroup singleOneFound = null;
  	
		FocList groupList = FocGroupDesc.getInstance().getFocList(FocList.LOAD_IF_NEEDED);
  	for(int i=0; i<groupList.size(); i++) {
  		FocGroup group = (FocGroup) groupList.getFocObject(i);
  		if(group.isGuestApplicable()) {
  			if (singleOneFound == null) {
  				singleOneFound = group;
  			} else {
  				singleOneFound = null;
  				break;
  			}
  		}
  	}
  	
  	return singleOneFound;
  }
  
  public FocGroup findGroupAndCreateIfNotExist(String name){
		FocList groupList = FocGroupDesc.getInstance().getFocList(FocList.LOAD_IF_NEEDED);
		FocGroup group = (FocGroup) groupList.searchByPropertyStringValue(FocGroupDesc.FLD_NAME, name);
		if(group == null){
			group = (FocGroup) groupList.newEmptyItem();
			group.scanAndAddWebModulesToGroup();
			group.setName(name);
			group.validate(true);
		}
		return group;
	}
}
