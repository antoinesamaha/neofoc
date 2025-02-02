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

import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

public class MenuAccessRightWebDesc extends FocDesc{

  public static final int FLD_MENU_CODE         = 1;
  public static final int FLD_MENU_TITLE        = 2;
  public static final int FLD_GROUP             = 3;
  public static final int FLD_RIGHT             = 4;
  public static final int FLD_CUSTOM_TITLE      = 5;
  
  public static final int  ALLOW_FULL_ACCESS = 0;
  public static final int  ALLOW_HIDE        = 1;
  
  public static final int LEN_MENU_CODE = 30;
  public static final String FLD_NAME_GROUP_PREFIX = "FOC_GROUP_";

  public static final String DB_TABLE_NAME = "ADM_MENU_ACCESS_RIGHT";
  
  public MenuAccessRightWebDesc() {
    super(MenuAccessRightWeb.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
    
    FField fField = addReferenceField();
    
    setWithObjectTree();
    FObjectField fatherFld = (FObjectField) getFieldByID(FField.FLD_FATHER_NODE_FIELD_ID);
    fatherFld.setDBResident(false);
    
    fField = new FStringField("MENU_CODE", "Menu Code", FLD_MENU_CODE, false, LEN_MENU_CODE);
    addField(fField);

    fField = new FStringField("MENU_TITLE", "Menu Title", FLD_MENU_TITLE, false, 50);
    fField.setDBResident(false);
    addField(fField);

    fField = new FStringField("MENU_CUSTOM_TITLE", "Custom Title", FLD_CUSTOM_TITLE, false, 50);
    fField.setDBResident(true);
    addField(fField);
    
    FMultipleChoiceField fMultipleField = new FMultipleChoiceField("ALLOW", "Allow", FLD_RIGHT, false, 2);
    fMultipleField.addChoice(ALLOW_HIDE       , "Hide"       );
    fMultipleField.addChoice(ALLOW_FULL_ACCESS, "Full Access");
    //fMultipleField.setWithInheritance(true);
    fMultipleField.setDisplayZeroValues(true);
    fMultipleField.addListener(new FPropertyListener(){

      public void dispose() {
      }

      public void propertyModified(FProperty property) {
      	MenuAccessRightWeb fatherMenuRights = (MenuAccessRightWeb) property.getFocObject();
        FocList menuRightsList = (FocList) fatherMenuRights.getFatherSubject();
        boolean lock = false;
        if(fatherMenuRights.getRight() == ALLOW_HIDE){
          lock = true;
        }
        setChildrenMenuValue(menuRightsList, fatherMenuRights, lock);
      }
      
    });
    addField(fMultipleField);
    
    FObjectField objectField = new FObjectField("GROUP", "Group", FLD_GROUP, false, FocGroupDesc.getInstance(), FLD_NAME_GROUP_PREFIX, this, FocGroupDesc.FLD_MENU_ACCESS_RIGHTS_WEB_LIST);
    objectField.setDisplayField(FocGroupDesc.FLD_NAME);
    objectField.setComboBoxCellEditor(FocGroupDesc.FLD_NAME);
    objectField.setSelectionList(FocGroup.getList(FocList.NONE));
    objectField.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    addField(objectField);

    /*
    objectField = new FObjectField("FATHER_MENU", "Father Menu", FLD_FATHER_MENU_RIGHT, false, this, "FATHER_MENU_");
    objectField.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    objectField.setDisplayField(FLD_MENU_CODE);
    objectField.setComboBoxCellEditor(FLD_MENU_CODE);
    objectField.setWithList(false);
    objectField.setDBResident(false);
    addField(objectField);
    setFObjectTreeFatherNodeID(FLD_FATHER_MENU_RIGHT);
    */
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
    if(list.getListOrder() == null){
      list.setListOrder(new FocListOrder(FLD_MENU_CODE, FLD_MENU_TITLE));
    }
    return list;
  }

  private static FocList globalListOfMenus = null;
  public static FocList getGlobalMenuRightsList(int mode){
  	boolean firstTime = globalListOfMenus == null;
  	globalListOfMenus = getInstance().getList(globalListOfMenus, FocList.NONE);
  	if(firstTime){
  		globalListOfMenus.getFilter().putAdditionalWhere("GLOBAL", FLD_NAME_GROUP_PREFIX+"REF=0");
  	}
  	if(mode == FocList.LOAD_IF_NEEDED){
  		globalListOfMenus.loadIfNotLoadedFromDB();
  	}else if(mode == FocList.FORCE_RELOAD){
  		globalListOfMenus.reloadFromDB();
  	}
  	globalListOfMenus.setDirectlyEditable(true);
  	globalListOfMenus.setDirectImpactOnDatabase(false);
    if(globalListOfMenus.getListOrder() == null){
    	globalListOfMenus.setListOrder(new FocListOrder(FLD_MENU_CODE, FLD_MENU_TITLE));
    }
    return globalListOfMenus;
  }
  
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static FocDesc getInstance() {
  	return getInstance(DB_TABLE_NAME, MenuAccessRightWebDesc.class);
  }
  
  private void setChildrenMenuValue(FocList menuRightsList, MenuAccessRightWeb fatherMenuRights, boolean lock){
    for(int i=0; i < menuRightsList.size(); i++){
    	MenuAccessRightWeb menuRights = (MenuAccessRightWeb) menuRightsList.getFocObject(i);
      if(menuRights.getFatherMenu() != null && menuRights.getFatherMenu().compareTo(fatherMenuRights) == 0){
        menuRights.setRight(fatherMenuRights.getRight());
        menuRights.getFocProperty(FLD_RIGHT).setValueLocked(lock);
      }
    }
  }
}
