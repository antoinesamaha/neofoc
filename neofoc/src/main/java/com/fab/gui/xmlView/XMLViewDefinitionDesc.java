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

import com.foc.desc.FocDesc;
import com.foc.desc.field.FBlobStringField;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.list.FocList;
import com.foc.shared.xmlView.XMLViewKey;

public class XMLViewDefinitionDesc extends FocDesc {
  
  public static final int FLD_STORAGE_NAME    =  1;
  public static final int FLD_CONTEXT         =  2;
  public static final int FLD_TYPE            =  3;
  public static final int FLD_VIEW            =  4;
  public static final int FLD_XML             =  5;
  public static final int FLD_JAVA_CLASS_NAME =  6;

  public static final String DB_TABLE_NAME = "XML_VIEW_DEFINITION";
  
  public XMLViewDefinitionDesc() {
    super(XMLViewDefinition.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, true);
    addReferenceField();

    FStringField cFld = new FStringField("STORAGE_NAME", "Storage Name", FLD_STORAGE_NAME, true, 40);
    cFld.setMandatory(true);
    addField(cFld);
    
    /*
    FObjectField usefFld = new FObjectField("USER", "User", FLD_USER, FocUserDesc.getInstance());
    usefFld.setSelectionList(FocUser.getList(FocList.NONE));
    usefFld.setComboBoxCellEditor(FocUserDesc.FLD_NAME);
    addField(usefFld);
    */
    
    FMultipleChoiceField typeFld = new FMultipleChoiceField("TYPE", "Type", FLD_TYPE, true, 1);
    typeFld.addChoice(0, "TYPE_FORM");
    typeFld.addChoice(1, "TYPE_TABLE");
    typeFld.addChoice(2, "TYPE_TREE");
    typeFld.addChoice(3, "TYPE_PIVOT");
    addField(typeFld);
    
    FStringField xmlContextFld = new FStringField("CONTEXT", "Context", FLD_CONTEXT, true, 30);
    xmlContextFld.setMandatory(true);
    addField(xmlContextFld);
    
    FStringField userViewFld = new FStringField("VIEW", "View", FLD_VIEW, true, XMLViewKey.LEN_VIEW);
    userViewFld.setMandatory(true);
    addField(userViewFld);
        
    FStringField javaClassName = new FStringField("FLD_JAVA_CLASS_NAME", "Java Class name", FLD_JAVA_CLASS_NAME, false, 250);
    addField(javaClassName);
    
    FBlobStringField blobField = new FBlobStringField("XML", "XML", FLD_XML, false, 10, 100);
    addField(blobField);
  }
 
  public static FocList getList(int mode){
  	return getInstance().getFocList(mode);
  }
  
  public FocList newFocList(){
    FocList list = super.newFocList();
    list.setDirectlyEditable(false);
    list.setDirectImpactOnDatabase(true);
//    if(list.getListOrder() == null){
//      FocListOrder order = new FocListOrder(FLD_CONTEXT);
//      list.setListOrder(order);
//    }
    return list;    
  }
  
  public static FocDesc getInstance() {
  	return getInstance(DB_TABLE_NAME, XMLViewDefinitionDesc.class);
  }
}
