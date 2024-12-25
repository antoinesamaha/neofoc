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
package com.fab.gui.xmlView;

import com.foc.admin.FocUserDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class UserXMLViewDesc extends FocDesc {
  
  public static final int FLD_STORAGE_NAME  = 1;
  public static final int FLD_USER          = 2;
  public static final int FLD_CONTEXT       = 3;
  public static final int FLD_TYPE          = 4;
  public static final int FLD_VIEW          = 5;
  public static final int FLD_PRINTING_VIEW = 6;
  
  private static int USER_VIEW_KEY_LENGTH = 60;

  public static final String DB_TABLE_NAME = "XML_VIEW_BY_USER";
  
  public UserXMLViewDesc() {
    super(UserXMLView.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, true);
    addReferenceField();

    FObjectField usefFld = new FObjectField("USER", "User", FLD_USER, true, FocUserDesc.getInstance(), "USER_", this, FocUserDesc.FLD_XML_VIEW_SELECCTION_LIST);
    usefFld.setSelectionList(FocUserDesc.getList(FocList.NONE));
    usefFld.setComboBoxCellEditor(FocUserDesc.FLD_NAME);
    usefFld.setDisplayField(FocUserDesc.FLD_NAME);
    addField(usefFld);

    FStringField cFld = new FStringField("STORAGE_NAME", "Storage Name", FLD_STORAGE_NAME, true, 50);
    addField(cFld);
    
    FStringField xmlContextFld = new FStringField("CONTEXT", "Context", FLD_CONTEXT, true, 30);
    addField(xmlContextFld);
    
    FMultipleChoiceField typeFld = new FMultipleChoiceField("TYPE", "Type", FLD_TYPE, true, 1);
    typeFld.addChoice(0, "TYPE_FORM");
    typeFld.addChoice(1, "TYPE_LIST");
    typeFld.addChoice(2, "TYPE_TREE");
    addField(typeFld);
    
    FStringField userViewFld = new FStringField("VIEW", "View", FLD_VIEW, false, USER_VIEW_KEY_LENGTH);
    addField(userViewFld);
    
    FStringField printingViewFld = new FStringField("PRINTING_VIEW", "Printing View", FLD_PRINTING_VIEW, false, USER_VIEW_KEY_LENGTH);
    addField(printingViewFld);
  }
  
  public static FocList getList(int mode){
  	return getInstance().getFocList(mode);
  }
  
  public FocList newFocList(){
    FocList list = super.newFocList();
    list.setDirectlyEditable(false);
    list.setDirectImpactOnDatabase(true);
    if(list.getListOrder() == null){
      FocListOrder order = new FocListOrder(FLD_USER);
      list.setListOrder(order);
    }
    return list;
  }
  
  public static FocDesc getInstance() {
  	return getInstance(DB_TABLE_NAME, UserXMLViewDesc.class);
  }
}
