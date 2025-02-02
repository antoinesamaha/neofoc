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
package b01.officeLink.excel.synchronize;

import com.foc.db.SQLFilter;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FDescFieldStringBased;
import com.foc.desc.field.FField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class ExcelSyncDesc extends FocDesc {
	public static final int FLD_FILE            =  1;
	public static final int FLD_DESC            =  2;
  public static final int FLD_FIELDS_MAP_LIST = 10;
  
  public static final String FNAME_DESC = "STORAGE_NAME";
  
  public static final String DB_TABLE_NAME = "XL_SYNC";
  
  public ExcelSyncDesc(){
    super(ExcelSync.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, true);
    setGuiBrowsePanelClass(ExcelSyncGuiBrowsePanel.class);  
    setGuiDetailsPanelClass(ExcelSyncGuiDetailsPanel.class);
    
    addReferenceField();
    addNameField();

    FDescFieldStringBased paramSetField = new FDescFieldStringBased(FNAME_DESC, "Storage Name", FLD_DESC, false);
    addField(paramSetField);
    
    FField focFld = new FStringField("FILE_NAME", "File", FLD_FILE,  true, 254);
    focFld.setLockValueAfterCreation(true);
    addField(focFld);
  }
  
	@Override
	protected void afterConstruction() {
		super.afterConstruction();
		FDescFieldStringBased descFld = (FDescFieldStringBased)getFieldByID(FLD_DESC);
		if(descFld != null){
			descFld.fillWithAllDeclaredFocDesc();
		}
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
    FocList focList = super.newFocList();
    focList.setDirectlyEditable(false);
    focList.setDirectImpactOnDatabase(true);
    FocListOrder order = new FocListOrder(FField.REF_FIELD_ID);
    focList.setListOrder(order);
    return focList;
  }

  private static FocList list = null;
  public static FocList newList(String storageName){
    list = getInstance().getFocList(FocList.NONE);
    list.setDirectlyEditable(false);
    list.setDirectImpactOnDatabase(true);
    SQLFilter filter = list.getFilter();
    filter.putAdditionalWhere("STORAGE_NAME", FNAME_DESC + " = '" + storageName +"'");
    if(list.getListOrder() == null){
      FocListOrder order = new FocListOrder(FField.FLD_NAME);
      list.setListOrder(order);
    }
    return list;    
  }
  
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static ExcelSyncDesc getInstance() {
    return (ExcelSyncDesc) getInstance(DB_TABLE_NAME, ExcelSyncDesc.class);
  }
  
}
