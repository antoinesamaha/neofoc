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
package com.fab.model.table;

import com.fab.FabModule;
import com.fab.model.project.FabProjectDesc;
import com.fab.model.table.underlyingCustomisation.UndCustTableDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FDescFieldStringBased;
import com.foc.desc.field.FField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

public class TableDefinitionDesc extends FocDesc {
	public static final int FLD_NAME                  = 1;
	public static final int FLD_WITH_REF              = 2;
	public static final int FLD_DB_RESIDENT           = 3;
	public static final int FLD_KEY_UNIQUE            = 4;
	public static final int FLD_SINGLE_INSTANCE       = 5;
	public static final int FLD_BROWSE_VIEW_LIST      = 6;
	public static final int FLD_DETAILS_VIEW_LIST     = 7;
	public static final int FLD_FOCLIST_LIST          = 8;
	public static final int FLD_FILTER_FIELD_DEF_LIST = 9;
	public static final int FLD_FIELD_DEFINITION_LIST = 10;
	public static final int FLD_DICTIONARY_GROUP_LIST = 11;
	public static final int FLD_EXISTING_TABLE        = 12;
	public static final int FLD_ADD_LOG_FIELDS        = 13;
	public static final int FLD_SERVER_SIDE_PACKAGE   = 14;
	public static final int FLD_PROJECT               = 15;
	public static final int FLD_WEB_STRUCTURE         = 16;
	public static final int FLD_WEB_CLIENT_PROJECT    = 17;
	public static final int FLD_WEB_CLIENT_PACKAGE    = 18;
	public static final int FLD_CLASS_NAME            = 19;
	public static final int FLD_HTML_FORM_LIST        = 20;
	public static final int FLD_HAS_WORKFLOW          = 21;
	public static final int FLD_TITLE                 = 22;
	public static final int FLD_SHOW_IN_MENU          = 23;
	public static final int FLD_NOT_DIRECTLY_EDITABLE = 24;
	
	public static final int FLD_MAX                   = 1000;
	
	public TableDefinitionDesc(Class focObjectClass, boolean dbResident, String storageName, boolean isKeyUnique) {
		super(focObjectClass, dbResident, storageName, isKeyUnique);
		addFieldsToDesc();
	}
	
	public TableDefinitionDesc(){
		this(TableDefinition.class, FocDesc.DB_RESIDENT, FabModule.getTableDefinitionTableStorageName(), false);
	}
	
	private void addFieldsToDesc(){
		setGuiBrowsePanelClass(TableDefinitionGuiBrowsePanel.class);
		setGuiDetailsPanelClass(TableDefinitionGuiDetailsPanel.class);
		
		FField fld = addReferenceField();
		
		setWithObjectTree();
		
		FStringField charFld = new FStringField("NAME", "Name", FLD_NAME, false, 50);
		charFld.setCapital(true);
		charFld.setMandatory(true);
		charFld.setLockValueAfterCreation(true);
		addField(charFld);
		charFld.addListener(new FPropertyListener() {
			@Override
			public void propertyModified(FProperty property) {
				if(property != null && property.isManualyEdited()){
					TableDefinition tableDef = (TableDefinition) property.getFocObject();
					tableDef.adjustClassNameFromTableName();
				}
			}
			
			@Override
			public void dispose() {
			}
		});
		
		FStringField cFld = new FStringField("TITLE", "Title", FLD_TITLE, false, 50);
		addField(cFld);
		
		charFld = new FStringField("CLASS_NAME", "Class Name", FLD_CLASS_NAME, false, 50);
		charFld.setLockValueAfterCreation(true);
		addField(charFld);
		
		fld = new FDescFieldStringBased("EXISTING_TABLE", "Existing table", FLD_EXISTING_TABLE, false);
		fld.setLockValueAfterCreation(true);
		fld.addListener(new FPropertyListener(){
			public void dispose(){}

			public void propertyModified(FProperty property) {
				if(property != null && !property.isLastModifiedBySetSQLString()){
					TableDefinition tableDef = (TableDefinition) property.getFocObject();
					if(tableDef != null){
						boolean lockName = !tableDef.isCreated();
						if(tableDef.isAlreadyExisting()){
							FocDesc desc = tableDef.getExistingTableDesc();
							if(desc != null && !desc.getStorageName().isEmpty()){
								tableDef.setName(desc.getStorageName());
								lockName = true;
							}
						}
						
						FProperty nameProp = tableDef.getFocProperty(FLD_NAME);
						if(nameProp != null){
							nameProp.setValueLocked(lockName);
						}
					}
				}
			}
		});
		addField(fld);
		
		fld = new FBoolField("DB_RESIDENT","DB Res.",FLD_DB_RESIDENT,false);
		addField(fld);
		
		fld = new FBoolField("SINGLE_INSTANCE", "Single Row", FLD_SINGLE_INSTANCE, false);
		addField(fld);
		
		fld = new FBoolField("WITH_REF","With ref",FLD_WITH_REF,false);
		addField(fld);
		
		fld = new FBoolField("KEY_UNIQUE","Key unique",FLD_KEY_UNIQUE,false);
		addField(fld);

		fld = new FBoolField("WEB_STRUCTURE","Web structure",FLD_WEB_STRUCTURE,false);
		addField(fld);
		
		FBoolField bFld = new FBoolField("HAS_WORFLOW", "Has worflow", FLD_HAS_WORKFLOW, false);
		addField(bFld);
		
		bFld = new FBoolField("SHOW_IN_MENU", "Show in menu", FLD_SHOW_IN_MENU, false);
		addField(bFld);

		bFld = new FBoolField("NOT_DIRECTLY_EDITABLE", "Not Directly Editable", FLD_NOT_DIRECTLY_EDITABLE, false);
		addField(bFld);
		
		FObjectField objFld = new FObjectField("PROJECT", "Project", FLD_PROJECT, FabProjectDesc.getInstance());
		objFld.setSelectionList(FabProjectDesc.getList(FocList.NONE));
		objFld.setComboBoxCellEditor(FabProjectDesc.FLD_NAME);
		addField(objFld);
		
		cFld = new FStringField("SOURCE_CODE_PACKAGE", "Package", FLD_SERVER_SIDE_PACKAGE, false, 250);
		addField(cFld);
		
		objFld = new FObjectField("WEB_CLT_PROJECT", "Web Client Project", FLD_WEB_CLIENT_PROJECT, FabProjectDesc.getInstance());
		objFld.setSelectionList(FabProjectDesc.getList(FocList.NONE));
		objFld.setComboBoxCellEditor(FabProjectDesc.FLD_NAME);
		addField(objFld);
		
		cFld = new FStringField("WEB_CLT_SRC_CODE_PACKAGE", "Web Client Package", FLD_WEB_CLIENT_PACKAGE, false, 250);
		addField(cFld);

		addFabOwnerField();
		
		if(!(this instanceof UndCustTableDesc)){
			fld = new FBoolField("ADD_LOG_FIELDS","Add log fields",FLD_ADD_LOG_FIELDS,false);
			addField(fld);
		}
	}
	
	public void afterConstruction(){
		FDescFieldStringBased descField = (FDescFieldStringBased) getFieldByID(FLD_EXISTING_TABLE);
		descField.fillWithAllDeclaredFocDesc();
	}
	
	public FieldDefinitionDesc getFieldDefinitionFocDesc(){
		return FieldDefinitionDesc.getInstance();
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
		list.setDirectlyEditable(false);
		list.setDirectImpactOnDatabase(true);
//		if(list.getListOrder() == null){
//			FocListOrder order = new FocListOrder(FLD_NAME);
//			list.setListOrder(order);
//		}
		return list;		
	}
	
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static TableDefinitionDesc getInstance() {
  	return (TableDefinitionDesc) getInstance(FabModule.getTableDefinitionTableStorageName(), TableDefinitionDesc.class); 
  }

}
