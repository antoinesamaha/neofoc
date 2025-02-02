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
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;
import com.foc.shared.xmlView.IXMLViewConst;

public class GroupXMLViewDesc extends FocDesc implements IXMLViewConst{

  public static final int FLD_GROUP        = 1;  
  public static final int FLD_STORAGE_NAME = 2;
  public static final int FLD_TYPE         = 3;
  public static final int FLD_CONTEXT      = 4;
  
  public static final int FLD_VIEW_RIGHT   = 5;
  public static final int FLD_VIEW         = 6;
  
  public static final int ALLOW_CREATION    = 0;
  public static final int ALLOW_SELECTION   = 1;
  public static final int ALLOW_NOTHING     = 2;
  
  public static final String DB_TABLE_NAME = "GROUP_XML_VIEW_RIGHTS";
  
  public GroupXMLViewDesc() {
    super(GroupXMLView.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
    
    addReferenceField();
    
    FStringField cFld = new FStringField("STORAGE_NAME", "Storage Name", FLD_STORAGE_NAME, true, 40);
    addField(cFld);
    
    FMultipleChoiceField typeFld = new FMultipleChoiceField("TYPE", "Type", FLD_TYPE, true, 1);
    typeFld.addChoice(TYPE_FORM , "Form");
    typeFld.addChoice(TYPE_TABLE, "Table");
    typeFld.addChoice(TYPE_TREE , "Tree");
    typeFld.addChoice(TYPE_PIVOT, "Pivot");
    addField(typeFld);
    
    FStringField userViewFld = new FStringField("VIEW", "View", FLD_VIEW, true, 30);
    addField(userViewFld);
    
    FStringField xmlContextFld = new FStringField("CONTEXT", "Context", FLD_CONTEXT, true, 30);
    addField(xmlContextFld);
    
    FMultipleChoiceField fMultipleField = getViewRightField("ALLOW", "Allow", FLD_VIEW_RIGHT, false, 2);
    addField(fMultipleField);
    
//    fMultipleField.addListener(new FPropertyListener(){
//			@Override
//			public void dispose() {
//			}
//
//			@Override
//			public void propertyModified(FProperty property) {
//			  GroupXMLView groupXMLView = (GroupXMLView) property.getFocObject();
//				if(groupXMLView != null){
//				  groupXMLView.adjustPropertyLock();
//				}
//			}
//    });
    
    FObjectField objectField = new FObjectField("GROUP", "Group", FLD_GROUP, false, FocGroupDesc.getInstance(), "GROUP_", this, FocGroupDesc.FLD_XML_VIEW_RIGHTS_LIST);
    objectField.setDisplayField(FocGroupDesc.FLD_NAME);
    objectField.setComboBoxCellEditor(FocGroupDesc.FLD_NAME);
    objectField.setSelectionList(FocGroup.getList(FocList.NONE));
    objectField.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    addField(objectField);
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
    return list;
  }
  
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static FocDesc getInstance() {
  	return getInstance(DB_TABLE_NAME, GroupXMLViewDesc.class); 
  }
  
  public static FMultipleChoiceField getViewRightField(String name, String title, int id, boolean key, int size){
  	FMultipleChoiceField fMultipleField = new FMultipleChoiceField(name, title, id, key, size);
    fMultipleField.addChoice(ALLOW_CREATION , "Creation");
    fMultipleField.addChoice(ALLOW_SELECTION, "Selection");
    fMultipleField.addChoice(ALLOW_NOTHING  , "Nothing");
    return fMultipleField;
  }
}
