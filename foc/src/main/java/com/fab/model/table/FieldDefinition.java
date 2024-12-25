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

import java.util.HashMap;
import java.util.Iterator;

import com.fab.FabModule;
import com.fab.IFabExtender;
import com.fab.codeWriter.CodeWriterConstants;
import com.fab.model.filter.FilterDefinition;
import com.fab.model.filter.FocListForFobjectFieldSelectionList;
import com.fab.model.filter.UserDefinedFilter;
import com.foc.admin.FocUserDesc;
import com.foc.business.adrBook.AdrBookPartyDesc;
import com.foc.business.adrBook.ContactDesc;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FBlobStringField;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FCompanyField;
import com.foc.desc.field.FDateField;
import com.foc.desc.field.FDateTimeField;
import com.foc.desc.field.FEMailField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FImageField;
import com.foc.desc.field.FIntField;
import com.foc.desc.field.FListField;
import com.foc.desc.field.FLongField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FNumField;
import com.foc.desc.field.FObjectField;
import com.foc.desc.field.FPasswordField;
import com.foc.desc.field.FTimeField;
import com.foc.gui.table.FTableView;
import com.foc.list.FocLinkForeignKey;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;
import com.foc.list.filter.FocListFilterBindedToList;
import com.foc.property.FProperty;
import com.foc.shared.dataStore.IDataStoreConst;
import com.foc.util.ASCII;

@SuppressWarnings("serial")
public class FieldDefinition extends FocObject {
	
	public static final int SQL_TYPE_ID_CHAR_FIELD                   = IDataStoreConst.FIELD_TYPE_TEXT;
	public static final int SQL_TYPE_ID_EMAIL_FIELD                  = IDataStoreConst.FIELD_TYPE_EMAIL_FIELD;
	public static final int SQL_TYPE_ID_INT                          = IDataStoreConst.FIELD_TYPE_INT;
	public static final int SQL_TYPE_ID_DOUBLE                       = IDataStoreConst.FIELD_TYPE_DOUBLE;
	public static final int SQL_TYPE_ID_LONG                         = IDataStoreConst.FIELD_TYPE_LONG;
	public static final int SQL_TYPE_ID_DATE                         = IDataStoreConst.FIELD_TYPE_DATE;
	public static final int SQL_TYPE_ID_TIME                         = IDataStoreConst.FIELD_TYPE_TIME;
	public static final int SQL_TYPE_ID_DATE_TIME                    = IDataStoreConst.FIELD_TYPE_DATE_TIME;
	public static final int SQL_TYPE_ID_OBJECT_FIELD                 = IDataStoreConst.FIELD_TYPE_OBJECT;
	public static final int SQL_TYPE_ID_LIST_FIELD                   = IDataStoreConst.FIELD_TYPE_LIST;
	public static final int SQL_TYPE_ID_BOOLEAN                      = IDataStoreConst.FIELD_TYPE_BOOLEAN;
	public static final int SQL_TYPE_ID_MULTIPLE_CHOICE              = IDataStoreConst.FIELD_TYPE_MULTIPLE_CHOICE;
	public static final int SQL_TYPE_ID_MULTIPLE_CHOICE_FOC_DESC     = IDataStoreConst.FIELD_TYPE_MULTIPLE_CHOICE_FOC_DESC;
	public static final int SQL_TYPE_ID_MULTIPLE_CHOICE_STRING_BASED = IDataStoreConst.FIELD_TYPE_MULTIPLE_CHOICE_STRING_BASED;
	public static final int SQL_TYPE_ID_XML_VIEW_SELECTOR            = IDataStoreConst.FIELD_TYPE_XML_VIEW_SELECTOR;
	public static final int SQL_TYPE_ID_IMAGE                        = IDataStoreConst.FIELD_TYPE_IMAGE;
	public static final int SQL_TYPE_ID_PASSWORD                     = IDataStoreConst.FIELD_TYPE_PASSWORD;
	public static final int SQL_TYPE_ID_BLOB_STRING                  = IDataStoreConst.FIELD_TYPE_BLOB_STRING;
	public static final int SQL_TYPE_ID_BLOB_FILE                    = IDataStoreConst.FIELD_TYPE_BLOB_FILE;
	public static final int SQL_TYPE_ID_STRUCT                       = IDataStoreConst.FIELD_TYPE_STRUCT;
	
	public static final String SQL_TYPE_NAME_CHAR_FIELD      = "TEXT";
	public static final String SQL_TYPE_NAME_EMAIL_FIELD     = "EMAIL";
	public static final String SQL_TYPE_NAME_INT             = "INT";
	public static final String SQL_TYPE_NAME_DOUBLE          = "DOUBLE";
	public static final String SQL_TYPE_NAME_LONG            = "LONG";
	public static final String SQL_TYPE_NAME_DATE            = "DATE";
	public static final String SQL_TYPE_NAME_DATE_TIME       = "DATE TIME";
	public static final String SQL_TYPE_NAME_TIME            = "TIME";
	public static final String SQL_TYPE_NAME_OBJECT_FIELD    = "OBJECT";
	public static final String SQL_TYPE_NAME_LIST_FIELD      = "LIST";
	public static final String SQL_TYPE_NAME_BOOLEAN         = "BOOLEAN";
	public static final String SQL_TYPE_NAME_MULTIPLE_CHOICE = "MULTIPLE CHOICE";
	public static final String SQL_TYPE_NAME_MULTIPLE_CHOICE_FOC_DESC = "MULTIPLE CHOICE FOC DESC";
	public static final String SQL_TYPE_NAME_IMAGE           = "IMAGE";
	public static final String SQL_TYPE_NAME_PASSWORD        = "PASSWORD";
	public static final String SQL_TYPE_NAME_BLOB_STRING     = "BLOB STRING";
	public static final String SQL_TYPE_NAME_XML_VIEW        = "XML VIEW";
	
	public static final String NO_LIST_FIELD_ID_LABEL    = "NONE";
	
	public static final String FIELD_TITLE_NOT_SET_YET   = "";
	public static final int    FIELD_LENGHT_NOT_SET_YET  = 0;
	
	private static HashMap<String, FocListForFobjectFieldSelectionList> filterToFocListMap = null;
	private static final String SEPARATOR_BETWEN_FOC_DESC_NAME_AND_REF = "_";
	
	private boolean listenersPlugged = false;
	
	public FieldDefinition(FocConstructor constr){
		super(constr);
		newFocProperties();
		setID(FField.NO_FIELD_ID);
		setTitle(FieldDefinition.FIELD_TITLE_NOT_SET_YET);
		setLength(FieldDefinition.FIELD_LENGHT_NOT_SET_YET);
		setFieldDBResident(true);
	}

	public TableDefinition getTableDefinition(){
		return (TableDefinition)getPropertyObject(FieldDefinitionDesc.FLD_TABLE);
	}

	@Override
	public FocList getObjectPropertySelectionList(int fieldID) {
		FocList list = super.getObjectPropertySelectionList(fieldID);
		if(fieldID == FieldDefinitionDesc.FLD_DICTIONARY_GROUP){
			list = getTableDefinition().getPropertyList(TableDefinitionDesc.FLD_DICTIONARY_GROUP_LIST);
		}
		return list;
	}

	public void setTable(TableDefinition table){
		setPropertyObject(FieldDefinitionDesc.FLD_TABLE, table);
	}
	
	public int getID(){
		int shiftForAlreadyExisting = 0;
		if(getTableDefinition().isAlreadyExisting()){
			shiftForAlreadyExisting = FField.FLD_SHIFT_FOR_FAB_FIELDS;
		}
		return shiftForAlreadyExisting+getPropertyInteger(FieldDefinitionDesc.FLD_ID);
	}
	
	public void setID(int ID){
		setPropertyInteger(FieldDefinitionDesc.FLD_ID, ID);
	}
	
	public void setID_ToMax(){
		int maxID = 1;
		FocList list = (FocList) getFatherSubject();
		for(int i=0; i<list.size(); i++){
			FieldDefinition fieldDef = (FieldDefinition) list.getFocObject(i);
			if(fieldDef.getID() >= maxID){
				maxID = fieldDef.getID() + 1;
			}
		}
		setID(maxID);		
	}
	
	public String getName(){
		return getPropertyString(FieldDefinitionDesc.FLD_NAME);
	}
	
	public void setName(String name){
		setPropertyString(FieldDefinitionDesc.FLD_NAME,name);
	}
	
	public String getForcedDBName(){
		return getPropertyString(FieldDefinitionDesc.FLD_FORCED_DB_NAME);
	}
	
	public void setForcedDBName(String name){
		setPropertyString(FieldDefinitionDesc.FLD_FORCED_DB_NAME,name);
	}

	public String getDictionaryGroup(){
		FabDictionaryGroup group = (FabDictionaryGroup) getPropertyObject(FieldDefinitionDesc.FLD_DICTIONARY_GROUP);
		return group != null ? group.getName() : null ;
	}

	public boolean isWithInheritance(){
		return getPropertyBoolean(FieldDefinitionDesc.FLD_WITH_INHERITANCE);
	}

	public boolean isDisplayZeroValues(){
		return getPropertyBoolean(FieldDefinitionDesc.FLD_DISPLAY_ZERO_VALUES);
	}
	
	public String getDefaultStringValue(){
		return getPropertyString(FieldDefinitionDesc.FLD_DEFAULT_VALUE);
	}
	
	public boolean isFieldDBResident(){
		return getPropertyBoolean(FieldDefinitionDesc.FLD_DB_RESIDENT);
	}
	
	public void setFieldDBResident(boolean dbResident){
		setPropertyBoolean(FieldDefinitionDesc.FLD_DB_RESIDENT, dbResident);
	}

	public boolean isMandatory(){
		return getPropertyBoolean(FieldDefinitionDesc.FLD_MANDATORY);
	}
	
	public void setMandatory(boolean dbResident){
		setPropertyBoolean(FieldDefinitionDesc.FLD_MANDATORY, dbResident);
	}

	public String getTitle(){
		return getPropertyString(FieldDefinitionDesc.FLD_TITLE);
	}
	
	public void setTitle(String title){
		setPropertyString(FieldDefinitionDesc.FLD_TITLE, title);
	}
	
	public int getSQLType(){
		return getPropertyInteger(FieldDefinitionDesc.FLD_SQL_TYPE);
	}
	
	public void setSQLType(int SQLType){
		setPropertyInteger(FieldDefinitionDesc.FLD_SQL_TYPE, SQLType);
	}

	public int getPredefinedType(){
		return getPropertyInteger(FieldDefinitionDesc.FLD_PREDEFINED_FIELD);
	}
	
	public void setPredefinedType(int SQLType){
		setPropertyInteger(FieldDefinitionDesc.FLD_PREDEFINED_FIELD, SQLType);
	}

	public boolean isKey(){
		return getPropertyBoolean(FieldDefinitionDesc.FLD_IS_KEY);
	}
	
	public void setKey(boolean key){
		setPropertyBoolean(FieldDefinitionDesc.FLD_IS_KEY, key);
	}
	
	public int getLength(){
		return getPropertyInteger(FieldDefinitionDesc.FLD_LENGTH);
	}
	
	public void setLength(int length){
		setPropertyInteger(FieldDefinitionDesc.FLD_LENGTH, length);
	}
	
	public int getDecimals(){
		return getPropertyInteger(FieldDefinitionDesc.FLD_DECIMALS);
	}
	
	public void setDecimals(int decimals){
		setPropertyInteger(FieldDefinitionDesc.FLD_DECIMALS, decimals);
	}
	
	public String getFocDescName(){
		return getPropertyString(FieldDefinitionDesc.FLD_FOC_DESC);
	}
	
	public void setFocDescName(String focDescName){
		setPropertyString(FieldDefinitionDesc.FLD_FOC_DESC, focDescName);
	}
	
	public TableDefinition getTableDefinition_ForTargetObject(){
		TableDefinition tableDef = TableDefinition.getTableDefinitionForFocDesc(getFocDescName());
		return tableDef;
	}
	
	public String getKeyPrefix(){
		return getPropertyString(FieldDefinitionDesc.FLD_KEY_PREFIX);
	}
	
	public void setKeyPrefix(String prefix){
		setPropertyString(FieldDefinitionDesc.FLD_KEY_PREFIX, prefix);
	}

	public FabMultiChoiceSet getMultiChoiceSet(){
		return (FabMultiChoiceSet) getPropertyObject(FieldDefinitionDesc.FLD_MULTIPLE_CHOICE_SET);
	}

	public int getObjectGuiEditorType(){
		return getPropertyMultiChoice(FieldDefinitionDesc.FLD_OBJ_GUI_EDITOR_TYPE);
	}
	
	public void setObjectGuiEditorType(int type){
		setPropertyMultiChoice(FieldDefinitionDesc.FLD_OBJ_GUI_EDITOR_TYPE, type);
	}

	public boolean isMultiColumnComboBox(){
		return getObjectGuiEditorType() == FieldDefinitionDesc.OBJ_GUI_TYPE_MULIT_COL_COMBO_BOX_EDITOR || getPropertyBoolean(FieldDefinitionDesc.FLD_OBJ_FLD_IS_MULTI_COLUMN_COMBO);
	}
	
	public void setMultiColumComboBox(boolean id){
		setObjectGuiEditorType(FieldDefinitionDesc.OBJ_GUI_TYPE_MULIT_COL_COMBO_BOX_EDITOR);
		setPropertyBoolean(FieldDefinitionDesc.FLD_OBJ_FLD_IS_MULTI_COLUMN_COMBO, id);
	}
	
	public int getComboBoxCellEditorFieldId(){
		return getPropertyInteger(FieldDefinitionDesc.FLD_COMBO_BOX_CELL_EDITOR_FIELD_ID);
	}
	
	public void setComboBoxCellEditorFieldId(int id){
		setPropertyInteger(FieldDefinitionDesc.FLD_COMBO_BOX_CELL_EDITOR_FIELD_ID, id);
	}

	public int getComboBoxCellEditorFieldId2(){
		return getPropertyInteger(FieldDefinitionDesc.FLD_COMBO_BOX_CELL_EDITOR_FIELD_ID_2);
	}
	
	public void setComboBoxCellEditorFieldId2(int id){
		setPropertyInteger(FieldDefinitionDesc.FLD_COMBO_BOX_CELL_EDITOR_FIELD_ID_2, id);
	}

	public int getComboBoxCellEditorFieldId3(){
		return getPropertyInteger(FieldDefinitionDesc.FLD_COMBO_BOX_CELL_EDITOR_FIELD_ID_3);
	}
	
	public void setComboBoxCellEditorFieldId3(int id){
		setPropertyInteger(FieldDefinitionDesc.FLD_COMBO_BOX_CELL_EDITOR_FIELD_ID_3, id);
	}

	public int getListFieldInMasterId(){
		return getPropertyInteger(FieldDefinitionDesc.FLD_LIST_FIELD_ID_IN_MASTER);
	}

	public void setListFieldInMasterId(int id){
		setPropertyInteger(FieldDefinitionDesc.FLD_LIST_FIELD_ID_IN_MASTER, id);
	}

	public String getFormulaString(){
		return getPropertyString(FieldDefinitionDesc.FLD_FORMULA);
	}
	
	public void setFormula(String formula){
		setPropertyString(FieldDefinitionDesc.FLD_FORMULA, formula);
	}
	
	public boolean isWithFormula(){
		String formula = getFormulaString();
		return formula != null && !formula.equals("");
	}
	
	public void setFilterRef(int filterRef){
		setPropertyInteger(FieldDefinitionDesc.FLD_FILTER_REF, filterRef);
	}
	
	public int getFilterRef(){
		return getPropertyInteger(FieldDefinitionDesc.FLD_FILTER_REF);
	}
	
	public FocDesc getSlaveFocDesc(){
		return getPropertyDesc(FieldDefinitionDesc.FLD_SLAVE_DESC);
	}

	public void setSlaveFocDesc(FocDesc focDesc){
		setPropertyDesc(FieldDefinitionDesc.FLD_SLAVE_DESC, focDesc);
	}

	public void setSlaveFocDesc(String focDesc){
		setPropertyDesc(FieldDefinitionDesc.FLD_SLAVE_DESC, focDesc);
	}

	public int getUniqueForeignKey(){
		return getPropertyMultiChoice(FieldDefinitionDesc.FLD_UNIQUE_FOREIGN_KEY);
	}
	
	/*public FocDesc getFocDesc(){
		FocDesc focDesc = null;
		String focDescName = getFocDescName();
		if(focDescName != null && focDescName.length() > 0){
			focDesc = Globals.getApp().getFocDescByName(focDescName);
		}
		return focDesc;
	}*/
	
	public FocDesc getFocDesc(){
		return getPropertyDesc(FieldDefinitionDesc.FLD_FOC_DESC);
	}
	
	public void adjustPropertiesEnability(){
		int SQLTYPE = getSQLType();
		boolean lockLength = SQLTYPE == FieldDefinition.SQL_TYPE_ID_BOOLEAN || SQLTYPE == FieldDefinition.SQL_TYPE_ID_DATE || SQLTYPE == FieldDefinition.SQL_TYPE_ID_TIME || SQLTYPE == FieldDefinition.SQL_TYPE_ID_DATE_TIME ||SQLTYPE == FieldDefinition.SQL_TYPE_ID_OBJECT_FIELD ||SQLTYPE == FieldDefinition.SQL_TYPE_ID_LIST_FIELD;
		boolean lockDecimals = SQLTYPE != FieldDefinition.SQL_TYPE_ID_DOUBLE && SQLTYPE != FieldDefinition.SQL_TYPE_ID_IMAGE;
		boolean lockFormula = SQLTYPE == FieldDefinition.SQL_TYPE_ID_OBJECT_FIELD || SQLTYPE == FieldDefinition.SQL_TYPE_ID_LIST_FIELD || SQLTYPE == FieldDefinition.SQL_TYPE_ID_IMAGE;
		boolean lockPropertiesRelatedToObjectField = SQLTYPE != FieldDefinition.SQL_TYPE_ID_OBJECT_FIELD;
		boolean lockPropertiesRelatedToListField = SQLTYPE != FieldDefinition.SQL_TYPE_ID_LIST_FIELD;
		
		FProperty prop = getFocProperty(FieldDefinitionDesc.FLD_LENGTH);
		prop.setValueLocked(lockLength);
		
		prop = getFocProperty(FieldDefinitionDesc.FLD_DECIMALS);
		prop.setValueLocked(lockDecimals);
		
		prop = getFocProperty(FieldDefinitionDesc.FLD_KEY_PREFIX);
		prop.setValueLocked(lockPropertiesRelatedToObjectField);
		
		prop = getFocProperty(FieldDefinitionDesc.FLD_OBJ_FLD_IS_MULTI_COLUMN_COMBO);
		prop.setValueLocked(lockPropertiesRelatedToObjectField);

		prop = getFocProperty(FieldDefinitionDesc.FLD_COMBO_BOX_CELL_EDITOR_FIELD_ID);
		prop.setValueLocked(lockPropertiesRelatedToObjectField);

		prop = getFocProperty(FieldDefinitionDesc.FLD_COMBO_BOX_CELL_EDITOR_FIELD_ID_2);
		prop.setValueLocked(lockPropertiesRelatedToObjectField || !isMultiColumnComboBox());

		prop = getFocProperty(FieldDefinitionDesc.FLD_COMBO_BOX_CELL_EDITOR_FIELD_ID_3);
		prop.setValueLocked(lockPropertiesRelatedToObjectField || !isMultiColumnComboBox());

		prop = getFocProperty(FieldDefinitionDesc.FLD_LIST_FIELD_ID_IN_MASTER);
		prop.setValueLocked(lockPropertiesRelatedToObjectField || !isMultiColumnComboBox());
		
		prop = getFocProperty(FieldDefinitionDesc.FLD_SLAVE_DESC);
		prop.setValueLocked(lockPropertiesRelatedToListField);
		
		prop = getFocProperty(FieldDefinitionDesc.FLD_UNIQUE_FOREIGN_KEY);
		prop.setValueLocked(lockPropertiesRelatedToListField);
		
		prop.getFocProperty(FieldDefinitionDesc.FLD_FORMULA);
		prop.setValueLocked(lockFormula);
		
		/*prop = getFocProperty(FieldDefinitionDesc.FLD_LIST_FIELD_ID);
		prop.setValueLocked(lockPropertiesRelatedToObjectField);*/
	}
	
	public FocDesc getFilterFocDesc(){
		FocDesc filterFocDesc = FilterDefinition.getFilterFocDesc(getFocDescName());;
		return filterFocDesc;
	}
	
	public UserDefinedFilter getUserDefinedFilter(){
		UserDefinedFilter filter = null;
		if(getSQLType() == SQL_TYPE_ID_OBJECT_FIELD){
			FocDesc filterFocDesc = getFilterFocDesc();
			if(filterFocDesc != null){
				FocList filtersList = filterFocDesc.getFocList(FocList.LOAD_IF_NEEDED);
				if(filtersList != null){
					filter = (UserDefinedFilter)filtersList.searchByReference(getFilterRef());
				}
			}
		}
		return filter;
	}
	
	public FField addToFocDesc(FocDesc focDesc){
		FField field = null;
		if(focDesc != null){
			field  = focDesc.getFieldByID(getID());
			boolean create = field == null;
			if(!create && getSQLType() != field.getFabType()){
				focDesc.removeField(field);
				create = true;
			}
			if(!create){
				field.setName(getName());
				field.setTitle(getTitle());
				field.setKey(isKey());
			}
			
			int predefined = getPredefinedType();
			if(predefined != FieldDefinitionDesc.PREDEFINED_NONE){
				field = adjustOrAddFieldToFocDesc_Predefined(focDesc, field, predefined);
			}else{
				int sqlType = getSQLType();
				field = adjustOrAddFieldToFocDesc_SQLType(focDesc, field, sqlType);
			}
		}
		return field;
	}
	
	public void init_AccordingToPredefined(){
		int predefined = getPredefinedType();
		switch(predefined){
		case FieldDefinitionDesc.PREDEFINED_CODE:
			setSQLType(SQL_TYPE_ID_CHAR_FIELD);
			break;
		case FieldDefinitionDesc.PREDEFINED_COMPANY:
			setSQLType(SQL_TYPE_ID_OBJECT_FIELD);
			break;
		case FieldDefinitionDesc.PREDEFINED_CONTACT:
			setSQLType(SQL_TYPE_ID_OBJECT_FIELD);
			break;
		case FieldDefinitionDesc.PREDEFINED_ADRESS_BOOK_PARTY:
			setSQLType(SQL_TYPE_ID_OBJECT_FIELD);
			break;
		case FieldDefinitionDesc.PREDEFINED_USER:
			setSQLType(SQL_TYPE_ID_OBJECT_FIELD);
			break;
		default:
			IFabExtender extender = FabModule.getInstance().getFabExtender();
			extender.predefinedFields_fillDefaultValues(predefined, this);
			break;
		}
	}
	
	protected FField adjustOrAddFieldToFocDesc_Predefined(FocDesc focDesc, FField field, int predefinedType){
		boolean create = field == null;
		switch(predefinedType){
		case FieldDefinitionDesc.PREDEFINED_CODE:
			if(create){
				field = focDesc.addCodeField(true);
				setID(FField.FLD_CODE);
				validate(true);
			}
			break;
		case FieldDefinitionDesc.PREDEFINED_COMPANY:
			if(create){
				FCompanyField company = new FCompanyField(true, true);
				focDesc.addField(company);
				setID(FField.FLD_COMPANY);
				validate(true);
				field = company;
			}
			break;
		case FieldDefinitionDesc.PREDEFINED_USER:
			if(create){
		    FObjectField fObjectFld = new FObjectField(getName(), getTitle(), getID(), FocUserDesc.getInstance());
		    fObjectFld.setComboBoxCellEditor(FocUserDesc.FLD_NAME);
		    fObjectFld.setDisplayField(FocUserDesc.FLD_NAME);
		    fObjectFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		    fObjectFld.setMandatory(false);
		    fObjectFld.setSelectionList(FocUserDesc.getList(FocList.NONE));    
		    focDesc.addField(fObjectFld);
		    field = fObjectFld;
			}
			break;
		case FieldDefinitionDesc.PREDEFINED_ADRESS_BOOK_PARTY:
			if(create){
		    FObjectField oFld = new FObjectField(getName(), getTitle(), getID(), AdrBookPartyDesc.getInstance());
		    oFld.setSelectionList(AdrBookPartyDesc.getList(FocList.NONE));
		    oFld.setDisplayField(AdrBookPartyDesc.FLD_CODE_NAME);
		    oFld.setComboBoxCellEditor(AdrBookPartyDesc.FLD_CODE_NAME);
		    focDesc.addField(oFld);
		    field = oFld;
			}
			break;
		case FieldDefinitionDesc.PREDEFINED_CONTACT:
			if(create){
		    FObjectField oFld = ContactDesc.newContactField(getName(), getTitle(), getID());
		    focDesc.addField(oFld);
		    field = oFld;
			}
			break;
		default:
			field = FabModule.getInstance().getFabExtender().predefinedFields_addField(focDesc, field, predefinedType, this);
		}
		return field;
	}
		
	private FField adjustOrAddFieldToFocDesc_SQLType(FocDesc focDesc, FField field, int sqlType){
		boolean create = field == null; 
		if(sqlType == SQL_TYPE_ID_CHAR_FIELD){
			if(create){
				field = new FStringField(getName(),getTitle(),getID(),isKey(),getLength());
			}else{
				field.setSize(getLength());
			}
		}else if(sqlType == SQL_TYPE_ID_EMAIL_FIELD){
			if(create){
				field = new FEMailField(getName(),getTitle(),getID(),isKey());
			}
		}else	if(sqlType == SQL_TYPE_ID_PASSWORD){
			if(create){
				field = new FPasswordField(getName(),getTitle(),getID(),isKey(),getLength());
			}else{
				field.setSize(getLength());
			}
		}else if(sqlType == SQL_TYPE_ID_INT){
			if(create){
				field = new FIntField(getName(),getTitle(),getID(),isKey(),getLength());
			}else{
				field.setSize(getLength());
			}
			
		}else if(sqlType == SQL_TYPE_ID_DOUBLE){
			if(create){
				field = new FNumField(getName(),getTitle(),getID(),isKey(),getLength(),getDecimals());
			}else{
				field.setSize(getLength());
				field.setDecimals(getDecimals());
			}
			
		}else if(sqlType == SQL_TYPE_ID_LONG){
			if(create){
				field = new FLongField(getName(),getTitle(),getID(),isKey(),getLength());
			}else{
				field.setSize(getLength());
				field.setDecimals(getDecimals());
			}
			
		}else if(sqlType == SQL_TYPE_ID_BOOLEAN){
			if(create){
				field = new FBoolField(getName(),getTitle(),getID(),isKey());
			}
			
		}else if(sqlType == SQL_TYPE_ID_DATE){
			if(create){
				field = new FDateField(getName(),getTitle(),getID(),isKey());
			}

		}else if(sqlType == SQL_TYPE_ID_MULTIPLE_CHOICE){
			if(create){
				FMultipleChoiceField mField = new FMultipleChoiceField(getName(), getTitle(), getID(), isKey(), getLength());
				FabMultiChoiceSet multiChoiceSet = getMultiChoiceSet();
				if(multiChoiceSet != null){
					FocList list = multiChoiceSet.getMultipleChoiceList();
					for(int i=0; i<list.size(); i++){
						FabMultipleChoice mChoice = (FabMultipleChoice) list.getFocObject(i);
						if(mChoice != null){
							mField.addChoice(mChoice.getIntValue(), mChoice.getDisplayText());
						}
					}
				}
				
				field = mField;
			}

		}else if(sqlType == SQL_TYPE_ID_TIME){
			if(create){
				field = new FTimeField(getName(),getTitle(),getID(),isKey());
			}
			
		}else if(sqlType == SQL_TYPE_ID_DATE_TIME){
			if(create){
				field = new FDateTimeField(getName(),getTitle(),getID(),isKey());
			}			

		}else if(sqlType == SQL_TYPE_ID_IMAGE){
			if(create){
				field = new FImageField(getName(), getTitle(), getID(), getLength(), getDecimals());
			}
			
		}else if(sqlType == SQL_TYPE_ID_BLOB_STRING){
			if(create){
				field = new FBlobStringField(getName(), getTitle(), getID(), isKey(), getLength(), getDecimals());
			}
			
		}else if(sqlType == SQL_TYPE_ID_OBJECT_FIELD){
			FocDesc masterDesc = getFocDesc();
			//BDebug
			if(masterDesc == null){
				masterDesc = getFocDesc();
			}
			//EDebug
			if(create){
				field = new FObjectField(getName(), getTitle(), getID(), isKey(), masterDesc, getKeyPrefix());
			}else{
				((FObjectField)field).setFocDesc(masterDesc);
				((FObjectField)field).setKeyPrefix(getKeyPrefix());
			}
			((FObjectField)field).setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);

			((FObjectField)field).setDisplayField(getComboBoxCellEditorFieldId());
			if(isMultiColumnComboBox() && getComboBoxCellEditorFieldId2() > 0){
				FTableView tableView = new FTableView();
				tableView.addColumn(masterDesc, getComboBoxCellEditorFieldId(), false);
				tableView.addColumn(masterDesc, getComboBoxCellEditorFieldId2(), false);
				if(getComboBoxCellEditorFieldId3() > 0) tableView.addColumn(masterDesc, getComboBoxCellEditorFieldId3(), false);
				((FObjectField)field).setMultiLineComboBoxCellEditor(getComboBoxCellEditorFieldId(), tableView);
			}else if(getObjectGuiEditorType() == FieldDefinitionDesc.OBJ_GUI_TYPE_SELECTION_PANEL_EDITOR){
				((FObjectField)field).setDetailsPanelViewID(7);
			}else{
				((FObjectField)field).setComboBoxCellEditor(getComboBoxCellEditorFieldId());
			}				
			
			//FocLinkSimple focLink = new FocLinkSimple(masterDesc);
			//FocListForFobjectFieldSelectionList focList = new FocListForFobjectFieldSelectionList(getFilterRef(), focLink);
			FocListForFobjectFieldSelectionList focList = getFocList(getFilterRef(), masterDesc);
			((FObjectField)field).setSelectionList(focList);
			
		}else if(sqlType == SQL_TYPE_ID_LIST_FIELD){
			if(create){
				FocLinkForeignKey linkForeignKey = new FocLinkForeignKey(getSlaveFocDesc(), getUniqueForeignKey(), true);
				field = new FListField(getName(), getTitle(), getID(), linkForeignKey);
			}else{
				FocLinkForeignKey linkForeignKey = (FocLinkForeignKey) ((FListField)field).getLink();
				linkForeignKey.setSlaveDesc(getSlaveFocDesc());
				linkForeignKey.setUniqueForeignKey(getUniqueForeignKey());
				linkForeignKey.setTransactionalWithChildren(true);
			}
		}
		if(field != null){
			field.setFabField(true);
			field.setDBResident(isFieldDBResident());
			field.setMandatory(isMandatory());
			if(create){
				focDesc.addField(field);
			}
			String formula = getFormulaString();
			if(formula != null && formula.length() > 0){
				field.setFormulaString(formula);
				field.setToolTipText(formula);
			}
			field.setDictionaryGroup(getDictionaryGroup());
			String defValue = getDefaultStringValue();
			if(defValue != null && !defValue.trim().equals("")){
				field.setDefaultStringValue(defValue);
			}
			if(field instanceof FNumField){
				((FNumField)field).setDisplayZeroValues(isDisplayZeroValues());
			}
			if(field instanceof FIntField){
				((FIntField)field).setDisplayZeroValues(isDisplayZeroValues());
			}
			
			if(isWithInheritance()){
				/*FField custFld = */focDesc.addInheritanceField(getID(), 10000+getID());//ATTENTION BIZARE D'utilizer le 10000 en dure co ca
				//custFld.setFabField(true);
			}
		}
		return field;
	}
	
	//GET ONLY ONE FOCLIST FOR EACH FILTER

	public static void refreshAllListForFocDesc(FocDesc focDesc){
		HashMap<String, FocListForFobjectFieldSelectionList> map = getFilterToFocListMap();
		if(map != null){
			Iterator<FocListForFobjectFieldSelectionList> iter = map.values().iterator();
			while(iter != null && iter.hasNext()){
				FocList list = iter.next();
				if(list.getFocDesc().getStorageName().equals(focDesc.getStorageName())){ 
					list.setLoaded(false);
				}
			}
		}
	}
	
	private static HashMap<String, FocListForFobjectFieldSelectionList> getFilterToFocListMap(){
		if(filterToFocListMap == null){
			filterToFocListMap = new HashMap<String, FocListForFobjectFieldSelectionList>();
		}
		return filterToFocListMap;
	}
	
	public static FocListForFobjectFieldSelectionList getFocList(int filterRef, FocDesc focDesc){
		FocListForFobjectFieldSelectionList focList = null;
		if(focDesc != null){
			String filterFocDescNameString = FocListFilterBindedToList.getFilterTableName(focDesc.getStorageName());
			String key = filterFocDescNameString + SEPARATOR_BETWEN_FOC_DESC_NAME_AND_REF + filterRef;

			focList = getFilterToFocListMap().get(key);
			if(focList == null){
				FocLinkSimple focLink = new FocLinkSimple(focDesc);
				focList = new FocListForFobjectFieldSelectionList(filterRef, focLink);
				getFilterToFocListMap().put(key, focList);
			}
		}
		return focList;
	}
	
	private void plugUnplugListenersToFieldDefinition(boolean plug){
		FProperty prop = getFocProperty(FieldDefinitionDesc.FLD_NAME);
		if(prop != null){
			if(plug){
				prop.addListener(FieldDefinitionDesc.getNamePropertyListener());
			}else{
				prop.removeListener(FieldDefinitionDesc.getNamePropertyListener());
			}
		}
		prop = getFocProperty(FieldDefinitionDesc.FLD_SQL_TYPE);
		if(prop != null){
			if(plug){
				prop.addListener(FieldDefinitionDesc.getSQLTypePropertyListener());
			}else{
				prop.removeListener(FieldDefinitionDesc.getSQLTypePropertyListener());
			}
		}
	}
	
	public void plugListenersToFieldDefinition(){
		if(!listenersPlugged){
			plugUnplugListenersToFieldDefinition(true);
			listenersPlugged = true;
		}
	}
	
	public void unplugListenersFromFocObject(){
		plugUnplugListenersToFieldDefinition(false);
		listenersPlugged = false;
	}
	
	//-----------------------------------------------------------
	// CODE WRITER
	//-----------------------------------------------------------
	
	public String getCW_VariableName(){
		return ASCII.convertJavaNaming_ToVariableNaming(getName());
	}
	
	public String getCW_GetterSetterMethodsPartialName(){
		return ASCII.convertJavaNaming_ToVariableGetterSetterNaming(getName());
	}

	public String getCW_FieldVariableName(){
		return ASCII.convertJavaNaming_ToVariableNaming(getName()) + "Fld";
		//return "fld"+getName();
	}
	
	public String getCW_FieldConstanteName(){
		return CodeWriterConstants.FLD_CONSTANT_PREFIX+ASCII.convertJavaClassNameToA_ConstantNameWith_(getName());
	}
	
	public TableDefinition getCW_SlaveTableDefinition(){
		return TableDefinition.getTableDefinitionForFocDesc(getSlaveFocDesc());
	}
}
