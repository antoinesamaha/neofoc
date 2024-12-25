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
package com.foc.db.migration;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FDescFieldStringBased;
import com.foc.desc.field.FField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class MigrationSourceDesc extends FocDesc {

  public static final int FLD_DESCRIPTION       = 1;
  public static final int FLD_SOURCE_TYPE       = 2;
  public static final int FLD_DESTINATION_TABLE = 3;
  
  public static final int FLD_DIRECTORY         = 4;
  public static final int FLD_FILE_NAME         = 5;
  
  public static final int FLD_DATABASE          = 6;
  public static final int FLD_TABLE_NAME        = 7;
  
  public static final int FLD_FIELD_LIST        = 20;
  
  public static final int SOURCE_TYPE_COMMA_SEPARATED_FILE = 0;  
  public static final int SOURCE_TYPE_DATABASE_TABLE       = 1;
  
  public static final String DB_TABLE_NAME = "MIG_SOURCE";
  
  public MigrationSourceDesc() {
    super(MigrationSource.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
    setGuiBrowsePanelClass(MigrationSourceGuiBrowsePanel.class);
    setGuiDetailsPanelClass(MigrationSourceGuiDetailsPanel.class);
        
    addReferenceField();
   
    addNameField();
    
    FDescFieldStringBased destinationFld = new FDescFieldStringBased("DESTINATION_TABLE", "Destination table", FLD_DESTINATION_TABLE, false);
    destinationFld.setMandatory(true);
    destinationFld.setLockValueAfterCreation(true);
    addField(destinationFld);
    
    FStringField cFld = new FStringField("DESCRIP", "Description", FLD_DESCRIPTION, false, 40);
    cFld.setMandatory(true);
    addField(cFld);

    FMultipleChoiceField mFld = new FMultipleChoiceField("SOURCE_TYPE", "Source Type", FLD_SOURCE_TYPE, false, 2);
    mFld.addChoice(SOURCE_TYPE_DATABASE_TABLE      , "Database Table");
    mFld.addChoice(SOURCE_TYPE_COMMA_SEPARATED_FILE, "Comma Separated File");
    addField(mFld);

    FObjectField dirFld = new FObjectField("DIRECTORY", "Directory", FLD_DIRECTORY, false, MigDirectoryDesc.getInstance(), "MIG_DIRECTORY_");
    dirFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);    
    dirFld.setSelectionList(MigDirectoryDesc.getList(FocList.NONE));
    dirFld.setDetailsPanelViewID(MigDirectoryGuiDetailsPanel.VIEW_SELECTION);
    dirFld.setDisplayField(FField.FLD_NAME);
    addField(dirFld);
    
    cFld = new FStringField("FILE_NAME", "File name", FLD_FILE_NAME, false, 50);
    addField(cFld);    

    dirFld = new FObjectField("DATABASE", "Database", FLD_DATABASE, false, MigDataBaseDesc.getInstance(), "MIG_DATABASE_");
    dirFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);    
    dirFld.setSelectionList(MigDataBaseDesc.getList(FocList.NONE));
    dirFld.setDetailsPanelViewID(MigDataBaseGuiDetailsPanel.VIEW_SELECTION);
    dirFld.setDisplayField(FField.FLD_NAME);
    addField(dirFld);
    
    cFld = new FStringField("TABLE_NAME", "Table/View", FLD_TABLE_NAME, false, 50);
    addField(cFld);
  }
  
	public void afterConstruction(){
		FDescFieldStringBased descFeild = (FDescFieldStringBased) getFieldByID(FLD_DESTINATION_TABLE);
		descFeild.fillWithAllDeclaredFocDesc();
	}
  
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static FocList getList(int mode){
    return getInstance().getFocList(mode);
  }
  
  @Override
  public FocList newFocList(){
    FocList list = super.newFocList();
    list.setDirectlyEditable(false);
    list.setDirectImpactOnDatabase(true);
    if(list.getListOrder() == null){
      list.setListOrder(new FocListOrder(MigrationSourceDesc.FLD_DESCRIPTION));
    }
    
    return list;
  }
  
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, MigrationSourceDesc.class);    
  }
}
