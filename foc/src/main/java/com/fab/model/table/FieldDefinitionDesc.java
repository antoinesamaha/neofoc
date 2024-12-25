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
import com.foc.db.DBIndex;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FDescFieldStringBased;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFormulaExpressionField;
import com.foc.desc.field.FIntField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FNumField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;
import com.foc.property.FMultipleChoice;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

public class FieldDefinitionDesc extends FocDesc {
	
	public static final int FLD_TABLE                            =  1;
	public static final int FLD_ID                               =  2;
	public static final int FLD_NAME                             =  3;
	public static final int FLD_DB_RESIDENT                      =  4;
	public static final int FLD_TITLE                            =  5;
	public static final int FLD_SQL_TYPE                         =  6;
	public static final int FLD_IS_KEY                           =  7;
	public static final int FLD_LENGTH                           =  8;
	public static final int FLD_DECIMALS                         =  9;
	public static final int FLD_FORMULA                          = 10;
	//B For FObjectField
	public static final int FLD_FOC_DESC                         = 11;
	public static final int FLD_KEY_PREFIX                       = 12;
	public static final int FLD_OBJ_FLD_IS_MULTI_COLUMN_COMBO    = 13;
	public static final int FLD_COMBO_BOX_CELL_EDITOR_FIELD_ID   = 14;
	public static final int FLD_COMBO_BOX_CELL_EDITOR_FIELD_ID_2 = 15;
	public static final int FLD_COMBO_BOX_CELL_EDITOR_FIELD_ID_3 = 16;
	//public static final int FLD_LIST_FIELD_ID                  = 14;
	public static final int FLD_FILTER_REF                       = 17;
	public static final int FLD_FILTER_LIST                      = 18;
	public static final int FLD_OBJ_GUI_EDITOR_TYPE              = 19;
	public static final int FLD_LIST_FIELD_ID_IN_MASTER          = 20;
	public static final int FLD_PREDEFINED_FIELD                 = 21;	
	public static final int FLD_MANDATORY                        = 22;
	public static final int FLD_FORCED_DB_NAME                   = 23;
	//E For FObjectField
	
	//B For FListField
	public static final int FLD_SLAVE_DESC                       = 30;
	public static final int FLD_UNIQUE_FOREIGN_KEY               = 31;
	//E For FListField
	public static final int FLD_DICTIONARY_GROUP                 = 40;
	public static final int FLD_WITH_INHERITANCE                 = 41;
	public static final int FLD_DISPLAY_ZERO_VALUES              = 42;
	public static final int FLD_DEFAULT_VALUE                    = 43;
	public static final int FLD_MULTIPLE_CHOICE_SET              = 44;
	
	public static final int FLD_SHIFT_FOR_UNDERLYING_FIELDS      = 1000;
		
  public static final int OBJ_GUI_TYPE_COMBO_BOX_EDITOR                     = 0;
  public static final int OBJ_GUI_TYPE_SELECTION_PANEL_EDITOR               = 1;
  public static final int OBJ_GUI_TYPE_MULIT_COL_COMBO_BOX_EDITOR           = 2;
  public static final int OBJ_GUI_TYPE_BROWSE_POPUP_EDITOR                  = 3;
	
	public static final int PREDEFINED_NONE              =   0;
	public static final int PREDEFINED_COMPANY           =   1;
	public static final int PREDEFINED_CODE              =   2;
	public static final int PREDEFINED_USER              =   3;
	public static final int PREDEFINED_ADRESS_BOOK_PARTY =   4;
	public static final int PREDEFINED_CONTACT           =   5;
	
	public static final int PREDEFINED_SHIFT          = 100;
  
	public FieldDefinitionDesc(){
		this(FieldDefinition.class, TableDefinitionDesc.getInstance(), TableDefinitionDesc.getList(FocList.NONE), FabModule.getFieldDefinitionTableStorageName());
	}
	
	public FieldDefinitionDesc(Class focObjectClass, FocDesc tableFocDesc, FocList tableSelectionList, String storageName){
		super(focObjectClass, FocDesc.DB_RESIDENT, storageName, false);
		setGuiDetailsPanelClass(FieldDefinitionGuiDetailsPanel.class);
		setGuiBrowsePanelClass(FieldDefinitionGuiBrowsePanel.class);
		FField fld = addReferenceField();
		
		addFabOwnerField();
		
		FObjectField objFld = new FObjectField("TABLE","Table",FLD_TABLE,false,tableFocDesc,"TABLE_", this, TableDefinitionDesc.FLD_FIELD_DEFINITION_LIST);
		objFld.setLockValueAfterCreation(true);
		objFld.setMandatory(true);
		objFld.setComboBoxCellEditor(TableDefinitionDesc.FLD_NAME);
		objFld.setSelectionList(tableSelectionList);
		addField(objFld);
		
		fld = new FNumField("ID","Id",FLD_ID,false,3,0);
		addField(fld);
		
		FStringField charFld = new FStringField("NAME","Name",FLD_NAME,false,50);
		addField(charFld);
		charFld.setMandatory(true);
//		charFld.setCapital(true);
		
		fld = new FBoolField("DB_RESIDENT", "DB resident", FLD_DB_RESIDENT, false);
		addField(fld);

		fld = new FBoolField("MANDATORY", "Mandatory", FLD_MANDATORY, false);
		addField(fld);
		
		fld = new FStringField("TITLE","Title",FLD_TITLE,false,50);
		addField(fld);
		
		FMultipleChoiceField mFld = new FMultipleChoiceField("PREDEFINED_FIELD", "Predefined Field", FLD_PREDEFINED_FIELD, false, 3);
		mFld.addChoice(PREDEFINED_NONE             , "- none -");
		mFld.addChoice(PREDEFINED_COMPANY          , "Company" );
		mFld.addChoice(PREDEFINED_CODE             , "Code"    );
		mFld.addChoice(PREDEFINED_USER             , "User"    );
		mFld.addChoice(PREDEFINED_ADRESS_BOOK_PARTY, "Adress book party");
		mFld.addChoice(PREDEFINED_CONTACT          , "Contact" );
		if(FabModule.getInstance().getFabExtender() != null){
			FabModule.getInstance().getFabExtender().predefinedFields_addChoices(mFld);
		}
		addField(mFld);
		mFld.addListener(new FPropertyListener() {
			@Override
			public void propertyModified(FProperty property) {
				FieldDefinition fieldDefinition = (FieldDefinition) property.getFocObject();
				if(fieldDefinition != null && !property.isLastModifiedBySetSQLString()){
					int predefType = fieldDefinition.getPredefinedType();
					if(predefType != PREDEFINED_NONE){
						FMultipleChoice prop = (FMultipleChoice) fieldDefinition.getFocProperty(FLD_PREDEFINED_FIELD);
						String title  = prop.getString();
						String dbName = title.replace(' ', '_');
						dbName = dbName.toUpperCase();
						fieldDefinition.setName(dbName);
						fieldDefinition.setTitle(title);
					}
					fieldDefinition.init_AccordingToPredefined();
				}
			}
			
			@Override
			public void dispose() {
			}
		});
		
		FMultipleChoiceField multiChoice = newFabTypeField("SQL_TYPE", FLD_SQL_TYPE); 
		addField(multiChoice);
		
		fld = new FBoolField("IS_KEY","Is key",FLD_IS_KEY,false);
		addField(fld);
		
		fld = new FNumField("LENGTH","Length",FLD_LENGTH,false,5,0);
		addField(fld);
		
		fld = new FNumField("DCIMALS", "Decimals", FLD_DECIMALS, false, 3, 0);
		addField(fld);
		
		fld = new FDescFieldStringBased("FOCDESC_NAME", "FocDesc Name", FLD_FOC_DESC, false);
		//fld.addListener(getFocDescNameListener());
		addField(fld);
		
		FStringField keyPrefixfld = new FStringField("KEY_PREFIX", "Key Prefix", FLD_KEY_PREFIX, false, 20);
		keyPrefixfld.setCapital(true);
		addField(keyPrefixfld);

		keyPrefixfld = new FStringField("FORCED_DB_NAME", "Forced DB Name", FLD_FORCED_DB_NAME, false, 50);
		addField(keyPrefixfld);
		
		FBoolField bFld = new FBoolField("MULTI_COL_COMBO", "IS Multiple Column Combo", FLD_OBJ_FLD_IS_MULTI_COLUMN_COMBO, false);
		addField(bFld);

		mFld = new FMultipleChoiceField("OBJECT_EDITOR_TYPE", "Object Editor Gui Type", FLD_OBJ_GUI_EDITOR_TYPE, false, 2);
	  mFld.addChoice(OBJ_GUI_TYPE_SELECTION_PANEL_EDITOR    , "Panel editor");
	  mFld.addChoice(OBJ_GUI_TYPE_COMBO_BOX_EDITOR          , "Combo box editor");
	  mFld.addChoice(OBJ_GUI_TYPE_MULIT_COL_COMBO_BOX_EDITOR, "Combo box editor with multiple columns");
	  mFld.addChoice(OBJ_GUI_TYPE_BROWSE_POPUP_EDITOR       , "Browse Popup");
		addField(mFld);

		fld = new FMultipleChoiceField("CELL_EDITOR_FIELD_ID", "Cell editor field", FLD_COMBO_BOX_CELL_EDITOR_FIELD_ID, false, 2);
		addField(fld);

		fld = new FMultipleChoiceField("CELL_EDITOR_FIELD_ID_2", "Cell editor field 2", FLD_COMBO_BOX_CELL_EDITOR_FIELD_ID_2, false, 2);
		addField(fld);

		fld = new FMultipleChoiceField("CELL_EDITOR_FIELD_ID_3", "Cell editor field 3", FLD_COMBO_BOX_CELL_EDITOR_FIELD_ID_3, false, 2);
		addField(fld);

		fld = new FMultipleChoiceField("LIST_FIELD_ID_IN_MASTER", "List field in master", FLD_LIST_FIELD_ID_IN_MASTER, false, 2);
		addField(fld);

		/*fld = new FMultipleChoiceField("LIST_FIELD_ID", "List field in master", FLD_LIST_FIELD_ID, false, 50);
		addField(fld);*/
		
		fld = new FFormulaExpressionField("FORMULA", "Formula", FLD_FORMULA, null);
		//((FCharField)fld).setCapital(true);
		addField(fld);
		
		fld = new FIntField("FILTER_REF", "Filter ref", FLD_FILTER_REF, false, 3);
		addField(fld);
		
		fld = new FDescFieldStringBased("SLAVE_FOC_DESC", "Slave table", FLD_SLAVE_DESC, false);
		addField(fld);
		
		fld = new FMultipleChoiceField("UNIQUE_FOREING_KEY", "Unique foreign key", FLD_UNIQUE_FOREIGN_KEY, false, 30);
		addField(fld);
		
		objFld = new FObjectField("DICT_GROUP", "Dictionary Grp", FLD_DICTIONARY_GROUP, false, FabDictionaryGroupDesc.getInstance(), "DICT_GRP_");
		objFld.setComboBoxCellEditor(FabDictionaryGroupDesc.FLD_NAME);
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setNullValueDisplayString("");
		addField(objFld);

		fld = new FBoolField("WITH_INHERITANCE", "Inherit", FLD_WITH_INHERITANCE, false);
		addField(fld);
		
		fld = new FBoolField("DISPLAY_ZERO", "Disp Zero", FLD_DISPLAY_ZERO_VALUES, false);
		addField(fld);
		
		fld = new FStringField("DEFAULT_VALUE", "Default Value", FLD_DEFAULT_VALUE, false, 20);
		addField(fld);
		
		objFld = new FObjectField("MULTIPLE_CHOICE_SET", "Multiple Choices", FLD_MULTIPLE_CHOICE_SET, FabMultiChoiceSetDesc.getInstance());
		objFld.setComboBoxCellEditor(FabMultiChoiceSetDesc.FLD_NAME);
		objFld.setDisplayField(FabMultiChoiceSetDesc.FLD_NAME);
		objFld.setNullValueDisplayString("");
		objFld.setSelectionList(FabMultiChoiceSetDesc.getList(FocList.NONE));
		addField(objFld);

		//In Oracle tthis gives error because the column already indexed.
		//It is indexed because there is a master sllave relation
//		DBIndex index = new DBIndex("TABLE_MASTER", this, false);
//		index.addField(FLD_TABLE);
//		indexAdd(index);
	}
	
	public void afterConstruction(){
		FDescFieldStringBased descFeild = (FDescFieldStringBased) getFieldByID(FLD_FOC_DESC);
		descFeild.fillWithAllDeclaredFocDesc();
		
		descFeild = (FDescFieldStringBased) getFieldByID(FLD_SLAVE_DESC);
		descFeild.fillWithAllDeclaredFocDesc();
	}
	
	public static FMultipleChoiceField newFabTypeField(String fldName, int fldID){
		FMultipleChoiceField multiChoice = new FMultipleChoiceField(fldName, "SQL Type", fldID, false, 30);
		multiChoice.setLockValueAfterCreation(true);
		
		multiChoice.addChoice(FieldDefinition.SQL_TYPE_ID_CHAR_FIELD, FieldDefinition.SQL_TYPE_NAME_CHAR_FIELD);
		multiChoice.addChoice(FieldDefinition.SQL_TYPE_ID_EMAIL_FIELD, FieldDefinition.SQL_TYPE_NAME_EMAIL_FIELD);
		multiChoice.addChoice(FieldDefinition.SQL_TYPE_ID_PASSWORD, FieldDefinition.SQL_TYPE_NAME_PASSWORD);
		multiChoice.addChoice(FieldDefinition.SQL_TYPE_ID_INT, FieldDefinition.SQL_TYPE_NAME_INT);
		multiChoice.addChoice(FieldDefinition.SQL_TYPE_ID_DOUBLE, FieldDefinition.SQL_TYPE_NAME_DOUBLE);
		multiChoice.addChoice(FieldDefinition.SQL_TYPE_ID_LONG, FieldDefinition.SQL_TYPE_NAME_LONG);
		multiChoice.addChoice(FieldDefinition.SQL_TYPE_ID_BOOLEAN, FieldDefinition.SQL_TYPE_NAME_BOOLEAN);
		multiChoice.addChoice(FieldDefinition.SQL_TYPE_ID_DATE, FieldDefinition.SQL_TYPE_NAME_DATE);
		multiChoice.addChoice(FieldDefinition.SQL_TYPE_ID_DATE_TIME, FieldDefinition.SQL_TYPE_NAME_DATE_TIME);
		multiChoice.addChoice(FieldDefinition.SQL_TYPE_ID_TIME, FieldDefinition.SQL_TYPE_NAME_TIME);
		multiChoice.addChoice(FieldDefinition.SQL_TYPE_ID_OBJECT_FIELD, FieldDefinition.SQL_TYPE_NAME_OBJECT_FIELD);
		multiChoice.addChoice(FieldDefinition.SQL_TYPE_ID_LIST_FIELD, FieldDefinition.SQL_TYPE_NAME_LIST_FIELD);
		multiChoice.addChoice(FieldDefinition.SQL_TYPE_ID_MULTIPLE_CHOICE, FieldDefinition.SQL_TYPE_NAME_MULTIPLE_CHOICE);
		multiChoice.addChoice(FieldDefinition.SQL_TYPE_ID_MULTIPLE_CHOICE_FOC_DESC, FieldDefinition.SQL_TYPE_NAME_MULTIPLE_CHOICE_FOC_DESC);
		multiChoice.addChoice(FieldDefinition.SQL_TYPE_ID_IMAGE, FieldDefinition.SQL_TYPE_NAME_IMAGE);
		multiChoice.addChoice(FieldDefinition.SQL_TYPE_ID_BLOB_STRING, FieldDefinition.SQL_TYPE_NAME_BLOB_STRING);
		//multiChoice.addListener(getSqlTypeListener());
		//multiChoice.setMandatory(true);
		return multiChoice;
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
    list.setDirectlyEditable(true);
    list.setDirectImpactOnDatabase(false);
    if(list.getListOrder() == null){
      FocListOrder order = new FocListOrder(FLD_NAME);
      list.setListOrder(order);
    }
    return list;
  }
	
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FieldDefinitionDesc focDesc = null;
  
  public static FieldDefinitionDesc getInstance() {
    if(focDesc == null){
      focDesc = new FieldDefinitionDesc();
    }
    return focDesc;
  }
  
	private static FPropertyListener namePropertyListener = null;
	public static FPropertyListener getNamePropertyListener(){
		if(namePropertyListener == null){
			namePropertyListener = new FPropertyListener(){
				public void propertyModified(FProperty property) {
					FieldDefinition fieldDefinition = (FieldDefinition) property.getFocObject();
					//if(fieldDefinition != null && fieldDefinition.getTitle().equals(FieldDefinition.FIELD_TITLE_NOT_SET_YET)){
						String name = fieldDefinition.getName();
						if(name != null && name.length() > 0){
							name  = name.replace("_", " ");
							StringBuffer title = new StringBuffer(name.toLowerCase());
							title.setCharAt(0, Character.toUpperCase(title.charAt(0)));
							fieldDefinition.setTitle(String.valueOf(title));
						}
					//}
				}

				public void dispose() {
				}
			};
		}
		return namePropertyListener;
	}
	
	private static FPropertyListener sqlTypePropertyListener = null;
	public static FPropertyListener getSQLTypePropertyListener(){
		if(sqlTypePropertyListener == null){
			sqlTypePropertyListener = new FPropertyListener(){
				public void propertyModified(FProperty property) {
					FieldDefinition fieldDefinition = (FieldDefinition) property.getFocObject();
					int SQLType = fieldDefinition.getSQLType();
					if(		SQLType == FieldDefinition.SQL_TYPE_ID_CHAR_FIELD
						|| 	SQLType == FieldDefinition.SQL_TYPE_ID_PASSWORD){
						fieldDefinition.setLength(20);
						fieldDefinition.setDecimals(0);
					}else if(SQLType == FieldDefinition.SQL_TYPE_ID_INT){
						fieldDefinition.setLength(3);
						fieldDefinition.setDecimals(0);
					}else if(SQLType == FieldDefinition.SQL_TYPE_ID_DOUBLE){
						fieldDefinition.setLength(5);
						fieldDefinition.setDecimals(2);
					}else if(SQLType == FieldDefinition.SQL_TYPE_ID_LONG){
						fieldDefinition.setLength(3);
						fieldDefinition.setDecimals(0);
					}else if(SQLType == FieldDefinition.SQL_TYPE_ID_BOOLEAN){
						fieldDefinition.setLength(0);
						fieldDefinition.setDecimals(0);
					}else if(SQLType == FieldDefinition.SQL_TYPE_ID_DATE
							  || SQLType == FieldDefinition.SQL_TYPE_ID_DATE_TIME){
						fieldDefinition.setLength(0);
						fieldDefinition.setDecimals(0);
					}else if(SQLType == FieldDefinition.SQL_TYPE_ID_MULTIPLE_CHOICE){
						fieldDefinition.setLength(2);
						fieldDefinition.setDecimals(0);
					}else if(SQLType == FieldDefinition.SQL_TYPE_ID_TIME){
						fieldDefinition.setLength(0);
						fieldDefinition.setDecimals(0);
					}else if(SQLType == FieldDefinition.SQL_TYPE_ID_OBJECT_FIELD){
						fieldDefinition.setLength(0);
						fieldDefinition.setDecimals(0);
					}else if(SQLType == FieldDefinition.SQL_TYPE_ID_IMAGE){
						fieldDefinition.setLength(100);
						fieldDefinition.setDecimals(100);
					}
				}
				public void dispose() {
				}
			};
		}
		return sqlTypePropertyListener;
	}
}
