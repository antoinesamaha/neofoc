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
package b01.officeLink;

import com.foc.business.adrBook.ContactDesc;
import com.foc.business.adrBook.ContactGuiBrowsePanel;
import com.foc.db.SQLFilter;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class OfficeLinkDesc extends FocDesc{

  public static final int FLD_FILE_INPUT_PATH    = 1;
  public static final int FLD_FILE_OUTPUT_PATH   = 2;
  public static final int FLD_DESCRIPTION        = 3;
  public static final int FLD_DESC_STORAGE_NAME  = 4;
  
  public static final String DB_TABLE_NAME = "OFFICE_LINK";
  
  public OfficeLinkDesc() {
    super(OfficeLink.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
    setGuiBrowsePanelClass(OfficeLinkGuiBrowsePanel.class);
    setGuiDetailsPanelClass(OfficeLinkGuiDetailsPanel.class);
    
    FField fFld = addReferenceField();
    
    fFld = new FStringField("INPUT_PATH", "Template Files", FLD_FILE_INPUT_PATH, false, 100);
    fFld.setMandatory(true);
    addField(fFld);
    
    fFld = new FStringField("OUTPUT_PATH", "Destination", FLD_FILE_OUTPUT_PATH, false, 100);
    addField(fFld);

    fFld = new FStringField("DESCRIPTION", "Description", FLD_DESCRIPTION, false, 30);
    fFld.setLockValueAfterCreation(true);
    fFld.setMandatory(true);
    addField(fFld);
    
    fFld = new FStringField("STORAGE_NAME", "Storage Name", FLD_DESC_STORAGE_NAME, false, 40);
    addField(fFld);
  }
  
  private static FocList list = null;
  public static FocList getList(String storageName){
    list = getInstance().getList(list, FocList.NONE);
    list.setDirectlyEditable(false);
    list.setDirectImpactOnDatabase(false);
    if(list.getListOrder() == null){
      FocListOrder order = new FocListOrder(FLD_DESC_STORAGE_NAME);
      list.setListOrder(order);
    }
    
    SQLFilter listFiler = list.getFilter();
    FocObject templateObject = listFiler.getObjectTemplate();
    if(templateObject == null){
      FocConstructor constr = new FocConstructor(getInstance(), null);
      templateObject = constr.newItem();
      listFiler.setObjectTemplate(templateObject);
    }
    
    templateObject.setPropertyString(FLD_DESC_STORAGE_NAME, storageName);
    listFiler.setFilterFields(SQLFilter.FILTER_ON_SELECTED);
    listFiler.addSelectedField(FLD_DESC_STORAGE_NAME);
    list.reloadFromDB();
    return list;    
  }
  
  public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, OfficeLinkDesc.class);    
  }
}
