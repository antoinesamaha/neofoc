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
package com.foc.admin;

import java.util.HashMap;

import com.foc.Globals;
import com.foc.SrvConst_ServerSide;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;
import com.foc.property.FBoolean;

public class GrpMobileModuleRightsDesc extends FocDesc{

  public static final int FLD_MODULE_NAME  = 1;
  public static final int FLD_ACCESS_RIGHT = 2;
  public static final int FLD_GROUP        = 3;
  public static final int FLD_MODULE_TITLE = 5;
  
  public static final int FLD_CREATE       = 6;
  public static final int FLD_READ         = 7;
  public static final int FLD_UPDATE       = 8;
  public static final int FLD_DELETE       = 9;
  
  public final static String MAN_POWER    = "Man Power";
	public final static String ADDRESS_BOOK = "Address Book";
	public final static String TIME_SHEET   = "Time Sheet";
  
  public static final String DB_TABLE_NAME = "GROUP_MOBILE_MODULE";
  
  public GrpMobileModuleRightsDesc() {
    super(GrpMobileModuleRights.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
    
    FField fField = addReferenceField();
    
    fField = new FStringField("MODULE_NAME", "Module Name", FLD_MODULE_NAME, false, 50);
    addField(fField);

    fField = new FStringField("MODULE_TITLE", "Module Title", FLD_MODULE_TITLE, false, 100);
    fField.setDBResident(false);
    addField(fField);
    
    FObjectField objectField = new FObjectField("GROUP", "Group", FLD_GROUP, FocGroupDesc.getInstance(), this, FocGroupDesc.FLD_MOBILE_MODULE_RIGHTS_LIST);
    objectField.setDisplayField(FocGroupDesc.FLD_NAME);
    objectField.setComboBoxCellEditor(FocGroupDesc.FLD_NAME);
    objectField.setSelectionList(FocGroup.getList(FocList.NONE));
    addField(objectField);
    
    FBoolField boolFld = new FBoolField("Create", "Create", FLD_CREATE, false);
    addField(boolFld);
    
    boolFld = new FBoolField("Read", "Read", FLD_READ, false);
    addField(boolFld);
    
    boolFld = new FBoolField("Update", "Update", FLD_UPDATE, false);
    addField(boolFld);
    
    boolFld = new FBoolField("Delete", "Delete", FLD_DELETE, false);
    addField(boolFld);
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
    list.setDirectlyEditable(true);
    list.setDirectImpactOnDatabase(false);
    list.setListOrder(new FocListOrder(FLD_MODULE_NAME));
    return list;
  }
  
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static FocDesc getInstance() {
  	return getInstance(DB_TABLE_NAME, GrpMobileModuleRightsDesc.class);

  }
  
  @Override
  public FocList getCustomFocListForMobile(HashMap<String, String> extraParams, String key) {
  	FocList focList = super.getCustomFocListForMobile(extraParams, key);
  	if(key != null && key.equals(SrvConst_ServerSide.LIST_KEY_USER_MENU_RIGHTS)){
	  	if(focList == null){
	  		focList = new FocList(new FocLinkSimple(GrpMobileModuleRightsDesc.getInstance()));
	  		focList.setCollectionBehaviour(true);
	  	}
	  	FocUser currentUser = Globals.getApp().getUser_ForThisSession();
	  	if(currentUser != null && currentUser.getAppGroup() != null){
	  		FocGroup focGroup = currentUser.getGroup();
	  		FocList mobileModuleRightsList = focGroup.getMobileModuleRightsList();
	  		if(mobileModuleRightsList != null){
	  			for(int i=0; i<mobileModuleRightsList.size(); i++){
	  				GrpMobileModuleRights grpMobileModuleRights = (GrpMobileModuleRights) mobileModuleRightsList.getFocObject(i);
	  				if(grpMobileModuleRights != null && grpMobileModuleRights.getRead()){
	  					focList.add(grpMobileModuleRights);
	  				}
	  			}
	  		}
	  	}
  	}
  	return focList;
  }
}
