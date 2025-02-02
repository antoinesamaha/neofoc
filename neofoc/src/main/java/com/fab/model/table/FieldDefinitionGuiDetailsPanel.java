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

import java.awt.GridBagConstraints;

import javax.swing.JComponent;

import com.fab.FocApplicationBuilder;
import com.fab.model.filter.UserDefinedFilter;
import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.desc.FocFieldEnum;
import com.foc.desc.FocObject;
import com.foc.desc.field.FDescFieldStringBased;
import com.foc.desc.field.FField;
import com.foc.desc.field.FObjectField;
import com.foc.gui.FGAbstractComboBox;
import com.foc.gui.FGComboBox;
import com.foc.gui.FGFormulaEditorPanel;
import com.foc.gui.FGLabel;
import com.foc.gui.FGObjectComboBox;
import com.foc.gui.FGTextField;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.list.FocList;
import com.foc.property.FMultipleChoice;
import com.foc.property.FObject;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

@SuppressWarnings("serial")
public class FieldDefinitionGuiDetailsPanel extends FPanel {
	private FieldDefinition fieldDefinition = null;
	
	private FPanel fObjectFieldPanel = null;
	private FPanel fListFieldPanel = null;
	private FGTextField keyPrefixTextField = null;
	private FGAbstractComboBox fGFocDescComboBox = null;
	//private FGComboBox listFieldIdComboBox = null;
	private FGComboBox cellEditorFieldIdComboBox       = null;
	private FGComboBox cellEditorFieldIdComboBox2      = null;
	private FGComboBox cellEditorFieldIdComboBox3      = null;
	private FGComboBox listFldIdInMasterObjectComboBox = null;	
	private FGObjectComboBox filtersComboBox = null;
	private FGObjectComboBox multipleChoiceSetComboBox = null;
	private FGAbstractComboBox slaveFocDescComboBox = null;
	private FGComboBox foreignKeyComboBox = null;
	private FGFormulaEditorPanel formulaTxtField = null;
	private FProperty focDescNameProperty = null;
	
	private FObject filterProperty = null;
	private boolean allowEdit = false;
	
	public static final int VIEW_STAND_ALONE = FocApplicationBuilder.VIEW_STAND_ALONE;
	public static final int VIEW_EMBEDDED    = FocObject.DEFAULT_VIEW_ID;
	
	public FieldDefinitionGuiDetailsPanel(FocObject focObject, int viewID){
		super("Field definition", FPanel.FILL_HORIZONTAL);
		this.fieldDefinition = (FieldDefinition) focObject;
		allowEdit = viewID != FocApplicationBuilder.VIEW_NO_EDIT;
		
		plugListenerToSlaveFocDescProperty();
		plugListenerToFocDescNameProperty();
		
		FDescFieldStringBased descField = (FDescFieldStringBased) this.fieldDefinition.getThisFocDesc().getFieldByID(FieldDefinitionDesc.FLD_FOC_DESC);
		descField.fillWithAllDeclaredFocDesc();
		
		descField = (FDescFieldStringBased) this.fieldDefinition.getThisFocDesc().getFieldByID(FieldDefinitionDesc.FLD_SLAVE_DESC);
		descField.fillWithAllDeclaredFocDesc();
		
		int y = 0;
		
		formulaTxtField = (FGFormulaEditorPanel) this.fieldDefinition.getGuiComponent(FieldDefinitionDesc.FLD_FORMULA);
		FocDesc desc = Globals.getApp().getFocDescByName(this.fieldDefinition.getTableDefinition().getName());
		if(desc != null){
			formulaTxtField.setOriginDesc(desc);
		}
		//formulaTxtField.setColumns(40);
		//formulaTxtField.setEnabled(allowEdit);
		if(viewID == VIEW_STAND_ALONE){
			FPanel detailsPanel = new FPanel();
			int k = 0;
			
			if(fieldDefinition.isCreated()){
				fieldDefinition.plugListenersToFieldDefinition();
				fieldDefinition.setID_ToMax();
			}
			
			int x = 0;
			
			detailsPanel.add(fieldDefinition, FieldDefinitionDesc.FLD_ID, x, k++);
			detailsPanel.add(fieldDefinition, FieldDefinitionDesc.FLD_NAME, x, k++);
			detailsPanel.add(fieldDefinition, FField.FLD_FAB_OWNER, x, k++);
			detailsPanel.add(fieldDefinition, FieldDefinitionDesc.FLD_TITLE, x, k++);
			detailsPanel.add(fieldDefinition, FieldDefinitionDesc.FLD_DICTIONARY_GROUP, x, k++);
			detailsPanel.add(fieldDefinition, FieldDefinitionDesc.FLD_PREDEFINED_FIELD, x, k++);
			detailsPanel.add(fieldDefinition, FieldDefinitionDesc.FLD_SQL_TYPE, x, k++);
			detailsPanel.add(fieldDefinition, FieldDefinitionDesc.FLD_LENGTH, x, k++);
			detailsPanel.add(fieldDefinition, FieldDefinitionDesc.FLD_DECIMALS, x, k++);
			
			k=0;
			x=x+2;
			
			detailsPanel.add(fieldDefinition, FieldDefinitionDesc.FLD_DEFAULT_VALUE, x, k++);
			multipleChoiceSetComboBox = (FGObjectComboBox) detailsPanel.add(fieldDefinition, FieldDefinitionDesc.FLD_MULTIPLE_CHOICE_SET, x, k++);
			detailsPanel.add(fieldDefinition, FieldDefinitionDesc.FLD_DB_RESIDENT, x+1, k++);
			detailsPanel.add(fieldDefinition, FieldDefinitionDesc.FLD_IS_KEY, x+1, k++);
			detailsPanel.add(fieldDefinition, FieldDefinitionDesc.FLD_DISPLAY_ZERO_VALUES, x+1, k++);
			detailsPanel.add(fieldDefinition, FieldDefinitionDesc.FLD_WITH_INHERITANCE, x+1, k++);
			add(detailsPanel, 0, y++, 2, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL);
			
			FPanel extDetailsPanel = new FPanel();
			extDetailsPanel.setBorder("General");
			extDetailsPanel.add(detailsPanel, 0, 0, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);
			add(extDetailsPanel, 0, y++, 2, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL);
		}
		
		FPanel formulaPanel = new FPanel();
		formulaPanel.setBorder("Formula");
		formulaPanel.add(formulaTxtField, 0, 0);
		add(formulaPanel, 0, y++, 2, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL);
		
		FPanel fObjectFieldPanel = getFObjectFieldPanel();
		add(fObjectFieldPanel, 0, y++, 2, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL);
		
		FPanel fListFieldPanel = getFListFieldPanel();
		add(fListFieldPanel, 0, y++, 2, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL);
		
		plugListenerToSQLTypeProperty();
		
		if(viewID == VIEW_STAND_ALONE){
			FValidationPanel vPanel = showValidationPanel(true);
			vPanel.addSubject(fieldDefinition);
		}
	}
	
	private FPanel getFObjectFieldPanel(){
		if(fObjectFieldPanel == null){
			this.fObjectFieldPanel = new FPanel();
			fObjectFieldPanel.setBorder("Object Field");
			
			this.fObjectFieldPanel.add(new FGLabel("Key Prefix"), 0, 0, GridBagConstraints.WEST);
			keyPrefixTextField = (FGTextField) this.fieldDefinition.getGuiComponent(FieldDefinitionDesc.FLD_KEY_PREFIX);
			keyPrefixTextField.setEnabled(allowEdit);
			this.fObjectFieldPanel.add(keyPrefixTextField, 1, 0, GridBagConstraints.WEST);
			
			this.fObjectFieldPanel.add(new FGLabel("Table"), 0, 1, GridBagConstraints.WEST);
			this.fGFocDescComboBox = (FGAbstractComboBox) fieldDefinition.getGuiComponent(FieldDefinitionDesc.FLD_FOC_DESC);
			focDescNameProperty = this.fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_FOC_DESC);
			focDescNameProperty.addListener(getFocDescComboBoxListener());
			String fieldName = fieldDefinition.getFocDescName();
			this.fGFocDescComboBox.setSelectedItem(fieldName);
			this.fGFocDescComboBox.setEnabled(allowEdit);
			this.fObjectFieldPanel.add(fGFocDescComboBox, 1, 1, GridBagConstraints.WEST);

			JComponent checkBox = (JComponent)this.fieldDefinition.getGuiComponent(FieldDefinitionDesc.FLD_OBJ_GUI_EDITOR_TYPE);
			checkBox.setEnabled(allowEdit);
			this.fObjectFieldPanel.add(checkBox, 1, 2, GridBagConstraints.WEST);

			//this.fObjectFieldPanel.add(new FGLabel("Multi column table"), 0, 2, GridBagConstraints.WEST);
			checkBox = (JComponent)this.fieldDefinition.getGuiComponent(FieldDefinitionDesc.FLD_OBJ_FLD_IS_MULTI_COLUMN_COMBO);
			checkBox.setEnabled(allowEdit);
			this.fObjectFieldPanel.add(checkBox, 1, 2, GridBagConstraints.WEST);

			FPanel editorFieldsPanel = new FPanel();
			this.fObjectFieldPanel.add(editorFieldsPanel, 1, 3, 2, 1);
			
			editorFieldsPanel.add(new FGLabel("Cell editor field"), 0, 0, GridBagConstraints.WEST);
			cellEditorFieldIdComboBox = (FGComboBox)this.fieldDefinition.getGuiComponent(FieldDefinitionDesc.FLD_COMBO_BOX_CELL_EDITOR_FIELD_ID);
			cellEditorFieldIdComboBox.setEnabled(allowEdit);
			editorFieldsPanel.add(cellEditorFieldIdComboBox, 1, 0, GridBagConstraints.WEST);

			editorFieldsPanel.add(new FGLabel("Cell editor field 2"), 2, 0, GridBagConstraints.WEST);
			cellEditorFieldIdComboBox2 = (FGComboBox)this.fieldDefinition.getGuiComponent(FieldDefinitionDesc.FLD_COMBO_BOX_CELL_EDITOR_FIELD_ID_2);
			cellEditorFieldIdComboBox2.setEnabled(allowEdit);
			editorFieldsPanel.add(cellEditorFieldIdComboBox2, 3, 0, GridBagConstraints.WEST);

			editorFieldsPanel.add(new FGLabel("Cell editor field 3"), 4, 0, GridBagConstraints.WEST);
			cellEditorFieldIdComboBox3 = (FGComboBox)this.fieldDefinition.getGuiComponent(FieldDefinitionDesc.FLD_COMBO_BOX_CELL_EDITOR_FIELD_ID_3);
			cellEditorFieldIdComboBox3.setEnabled(allowEdit);
			editorFieldsPanel.add(cellEditorFieldIdComboBox3, 5, 0, GridBagConstraints.WEST);

			this.fObjectFieldPanel.add(new FGLabel("List field in master"), 0, 6, GridBagConstraints.WEST);
			listFldIdInMasterObjectComboBox = (FGComboBox)this.fieldDefinition.getGuiComponent(FieldDefinitionDesc.FLD_LIST_FIELD_ID_IN_MASTER);
			listFldIdInMasterObjectComboBox.setEnabled(allowEdit);
			this.fObjectFieldPanel.add(listFldIdInMasterObjectComboBox, 1, 6, GridBagConstraints.WEST);
			
			this.fObjectFieldPanel.add(new FGLabel("Filter"), 0, 7, GridBagConstraints.WEST);
			//this.fObjectFieldPanel.add(getFiltersComboBox(), 1, 4, GridBagConstraints.WEST);
			addFilterComboBoxToFObjectFieldPanel(getFiltersComboBox(), this.fObjectFieldPanel);
		}
		
		return this.fObjectFieldPanel;
	}
	
	private void addFilterComboBoxToFObjectFieldPanel(FGObjectComboBox filtersComboBox, FPanel fObjectFieldPanel){
		fObjectFieldPanel.add(filtersComboBox, 1, 7, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
	}
	
	private FPanel getFListFieldPanel(){
		fListFieldPanel = new FPanel();
		fListFieldPanel.setBorder("List Field");
		
		fListFieldPanel.add(new FGLabel("Slave table"), 0, 0, GridBagConstraints.WEST);
		slaveFocDescComboBox = (FGAbstractComboBox) this.fieldDefinition.getGuiComponent(FieldDefinitionDesc.FLD_SLAVE_DESC);
		slaveFocDescComboBox.setEnabled(allowEdit);
		fListFieldPanel.add(slaveFocDescComboBox, 1, 0, GridBagConstraints.WEST);
		
		fListFieldPanel.add(new FGLabel("Foreign key in slave"), 0, 1, GridBagConstraints.WEST);
		foreignKeyComboBox = (FGComboBox) this.fieldDefinition.getGuiComponent(FieldDefinitionDesc.FLD_UNIQUE_FOREIGN_KEY);
		foreignKeyComboBox.setEnabled(allowEdit);
		fListFieldPanel.add(foreignKeyComboBox, 1, 1, GridBagConstraints.WEST);
		
		return fListFieldPanel;
	}

	private FPropertyListener SQLTypePropertyListener = null;
	private FPropertyListener getSQLTypePropertyListener(){
		if(SQLTypePropertyListener == null){
			SQLTypePropertyListener = new FPropertyListener(){
				public void propertyModified(FProperty property) {
					FieldDefinition fieldDefinition = (FieldDefinition) property.getFocObject();
					int sqlType = fieldDefinition.getSQLType();
					
					//FMultipleChoice multipleChoice = (FMultipleChoice)property;
					//int sqlType = multipleChoice.getInteger();
					if(fGFocDescComboBox != null){
						fGFocDescComboBox.setEnabled(sqlType == FieldDefinition.SQL_TYPE_ID_OBJECT_FIELD && allowEdit);
					}
					if(filtersComboBox != null){
						filtersComboBox.setEnabled(sqlType == FieldDefinition.SQL_TYPE_ID_OBJECT_FIELD && allowEdit);
					}
					if(cellEditorFieldIdComboBox != null){
						cellEditorFieldIdComboBox.setEnabled(sqlType == FieldDefinition.SQL_TYPE_ID_OBJECT_FIELD && allowEdit);
					}
					if(cellEditorFieldIdComboBox2 != null){
						cellEditorFieldIdComboBox2.setEnabled(sqlType == FieldDefinition.SQL_TYPE_ID_OBJECT_FIELD && allowEdit);
					}
					if(cellEditorFieldIdComboBox3 != null){
						cellEditorFieldIdComboBox3.setEnabled(sqlType == FieldDefinition.SQL_TYPE_ID_OBJECT_FIELD && allowEdit);
					}
					if(listFldIdInMasterObjectComboBox != null){
						listFldIdInMasterObjectComboBox.setEnabled(sqlType == FieldDefinition.SQL_TYPE_ID_OBJECT_FIELD && allowEdit);
					}					

					if(keyPrefixTextField != null){
						keyPrefixTextField.setEnabled(sqlType == FieldDefinition.SQL_TYPE_ID_OBJECT_FIELD && allowEdit);
					}
					
					if(slaveFocDescComboBox != null){
						slaveFocDescComboBox.setEnabled(sqlType == FieldDefinition.SQL_TYPE_ID_LIST_FIELD && allowEdit);
					}
					
					if(foreignKeyComboBox != null){
						foreignKeyComboBox.setEnabled(sqlType == FieldDefinition.SQL_TYPE_ID_LIST_FIELD && allowEdit);
					}
					
					if(formulaTxtField != null){
						formulaTxtField.setEnabled(sqlType != FieldDefinition.SQL_TYPE_ID_LIST_FIELD && allowEdit);
					}
					
					if(multipleChoiceSetComboBox != null){
						multipleChoiceSetComboBox.setEnabled(sqlType == FieldDefinition.SQL_TYPE_ID_MULTIPLE_CHOICE && allowEdit);
					}
					
					if(fieldDefinition != null){
						fieldDefinition.adjustPropertiesEnability();
					}
				}
				
				public void dispose() {
				}
			};
		}
		return SQLTypePropertyListener;
	}
	
	private void plugListenerToSQLTypeProperty(){
		if(fieldDefinition != null){
			FProperty sqlTypeProperty = fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_SQL_TYPE);
			if(sqlTypeProperty != null){
				sqlTypeProperty.addListener(getSQLTypePropertyListener());
				getSQLTypePropertyListener().propertyModified(fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_SQL_TYPE));
			}
			
			FProperty multiColComboProperty = fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_OBJ_FLD_IS_MULTI_COLUMN_COMBO);
			if(multiColComboProperty != null){
				multiColComboProperty.addListener(getSQLTypePropertyListener());
				getSQLTypePropertyListener().propertyModified(fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_OBJ_FLD_IS_MULTI_COLUMN_COMBO));
			}
			
			multiColComboProperty = fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_OBJ_GUI_EDITOR_TYPE);
			if(multiColComboProperty != null){
				multiColComboProperty.addListener(getSQLTypePropertyListener());
				getSQLTypePropertyListener().propertyModified(fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_OBJ_FLD_IS_MULTI_COLUMN_COMBO));
			}
		}
	}
	
	private FPropertyListener focDescComboBoxListener = null;
	private FPropertyListener getFocDescComboBoxListener(){
		if(focDescComboBoxListener == null){
			focDescComboBoxListener = new FPropertyListener(){
				public void propertyModified(FProperty property) {
					FPanel fObjectFieldPanel = getFObjectFieldPanel();
					if(fObjectFieldPanel != null){
						fObjectFieldPanel.setVisible(false);
						if(filtersComboBox != null){
							filtersComboBox.setVisible(false);
							fObjectFieldPanel.remove(filtersComboBox);
						}
						filtersComboBox = reconstructFiltersComboBox();
						addFilterComboBoxToFObjectFieldPanel(filtersComboBox, fObjectFieldPanel);
						filtersComboBox.setVisible(true);
						fObjectFieldPanel.setVisible(true);
					}
				}

				public void dispose() {
				}
			};
		}
		return focDescComboBoxListener;
	}
	
	/**
	 * @return
	 */
	private FGObjectComboBox getFiltersComboBox(){
		if(filtersComboBox == null){
			reconstructFiltersComboBox();
		}
		return filtersComboBox;
	}
	
	private FGObjectComboBox reconstructFiltersComboBox(){
		removeFilterPropertyListenerAndDisposeFilterProperty();
		FObjectField objFld = new FObjectField("FILTER_LIST", "Filter list", FieldDefinitionDesc.FLD_FILTER_LIST, false, fieldDefinition.getFilterFocDesc(), "FILTER_LIST_");
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setDisplayField(FField.FLD_NAME);
		objFld.setComboBoxCellEditor(FField.FLD_NAME);
		filterProperty = (FObject)objFld.newProperty(null);
		filterProperty.setFocField(objFld);
		filtersComboBox = (FGObjectComboBox)objFld.getGuiComponent(filterProperty);
		filtersComboBox.setEnabled(allowEdit);
		filterProperty.addListener(newFilterPropertyListener());
		adjustFilterComboBoxSelectedItemAccordinglyToFilterRef();
		return filtersComboBox;
	}
	
	private FPropertyListener filterPropertyListener = null;
	private FPropertyListener newFilterPropertyListener(){
		filterPropertyListener = new FPropertyListener(){
			public void propertyModified(FProperty property) {
				FObject filterProp = (FObject)property;
				UserDefinedFilter userDefinedFilter = (UserDefinedFilter)filterProp.getObject();
				if(userDefinedFilter != null){
					int ref = userDefinedFilter.getReference().getInteger();
					fieldDefinition.setFilterRef(ref);
				}
			}
			
			public void dispose() {
			}

			
		};
		return filterPropertyListener;
	}
	
	private void adjustFilterComboBoxSelectedItemAccordinglyToFilterRef(){
		if(fieldDefinition.getSQLType() == FieldDefinition.SQL_TYPE_ID_OBJECT_FIELD){
			UserDefinedFilter filter = fieldDefinition.getUserDefinedFilter();
			if(filter != null){
				filterProperty.setObject(filter);
			}
		}
	}
	
	private FPropertyListener slaveFocDescListener = null;
	private FPropertyListener getSlaveFocDescListener(){
		if(slaveFocDescListener == null){
			slaveFocDescListener = new FPropertyListener(){

				public void propertyModified(FProperty property) {
					if(property != null){
						FieldDefinition fieldDefinition = (FieldDefinition) property.getFocObject();
						if(fieldDefinition != null && fieldDefinition.getSQLType() == FieldDefinition.SQL_TYPE_ID_LIST_FIELD){
							FocDesc slaveFocDesc = fieldDefinition.getSlaveFocDesc();
							if(slaveFocDesc != null){
								FMultipleChoice foreignKeyProp = (FMultipleChoice) fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_UNIQUE_FOREIGN_KEY);
								if(foreignKeyProp != null){
									fillMultipleChoicePropWithFocDescFields(foreignKeyProp, slaveFocDesc);
								}
							}
						}
						if(foreignKeyComboBox != null){
							foreignKeyComboBox.refillChoices();
						}
					}
					
				}
				
				public void dispose() {
				}
			};
		}
		return slaveFocDescListener;
	}
	
	private void plugListenerToSlaveFocDescProperty(){
		if(fieldDefinition != null){
			FProperty slaveFocDescProperty = fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_SLAVE_DESC);
			if(slaveFocDescProperty != null){
				slaveFocDescProperty.addListener(getSlaveFocDescListener());
				getSlaveFocDescListener().propertyModified(fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_SLAVE_DESC));
			}
		}
	}
	
	private FPropertyListener focDescNamePropertyListener = null;
	private FPropertyListener getFocDescNamePropertyListener(){
		if(focDescNamePropertyListener == null){
			focDescNamePropertyListener = new FPropertyListener(){
				public void propertyModified(FProperty property) {
					if(property != null){
						FieldDefinition fieldDefinition = (FieldDefinition) property.getFocObject();
						if(fieldDefinition != null && fieldDefinition.getSQLType() == FieldDefinition.SQL_TYPE_ID_OBJECT_FIELD){
							FocDesc focDesc = fieldDefinition.getFocDesc();
							if(focDesc != null){
								FMultipleChoice celEditorFieldIdProp = (FMultipleChoice) fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_COMBO_BOX_CELL_EDITOR_FIELD_ID);
								if(celEditorFieldIdProp != null){
									fillMultipleChoicePropWithFocDescFields(celEditorFieldIdProp, focDesc);
								}
								celEditorFieldIdProp = (FMultipleChoice) fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_COMBO_BOX_CELL_EDITOR_FIELD_ID_2);
								if(celEditorFieldIdProp != null){
									fillMultipleChoicePropWithFocDescFields(celEditorFieldIdProp, focDesc);
								}
								celEditorFieldIdProp = (FMultipleChoice) fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_COMBO_BOX_CELL_EDITOR_FIELD_ID_3);
								if(celEditorFieldIdProp != null){
									fillMultipleChoicePropWithFocDescFields(celEditorFieldIdProp, focDesc);
								}
								celEditorFieldIdProp = (FMultipleChoice) fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_LIST_FIELD_ID_IN_MASTER);
								if(celEditorFieldIdProp != null){
									fillMultipleChoicePropWithFocDescFields(celEditorFieldIdProp, focDesc, FocFieldEnum.CAT_LIST, true);
								}
							}
						}
						if(cellEditorFieldIdComboBox != null){
							cellEditorFieldIdComboBox.refillChoices();
						}
						if(cellEditorFieldIdComboBox2 != null){
							cellEditorFieldIdComboBox2.refillChoices();
						}
						if(cellEditorFieldIdComboBox3 != null){
							cellEditorFieldIdComboBox3.refillChoices();
						}
						if(listFldIdInMasterObjectComboBox != null){
							listFldIdInMasterObjectComboBox.refillChoices();
						}
					}
				}
				
				public void dispose() {
				}
			};
		}
		return focDescNamePropertyListener;
	}
	
	private void plugListenerToFocDescNameProperty(){
		if(fieldDefinition != null){
			FProperty focDescNameProp = fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_FOC_DESC);
			if(focDescNameProp != null){
				focDescNameProp.addListener(getFocDescNamePropertyListener());
				getFocDescNamePropertyListener().propertyModified(fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_FOC_DESC));
			}
		}
	}

	private void fillMultipleChoicePropWithFocDescFields(FMultipleChoice multipleChoiceProp, FocDesc focDesc){
		fillMultipleChoicePropWithFocDescFields(multipleChoiceProp, focDesc, FocFieldEnum.CAT_ALL);
	}
	
	private void fillMultipleChoicePropWithFocDescFields(FMultipleChoice multipleChoiceProp, FocDesc focDesc, int category){
		fillMultipleChoicePropWithFocDescFields(multipleChoiceProp, focDesc, category, false);
	}
	
	private void fillMultipleChoicePropWithFocDescFields(FMultipleChoice multipleChoiceProp, FocDesc focDesc, int category, boolean allowNullValue){
		if(multipleChoiceProp != null && focDesc != null){
			multipleChoiceProp.resetLocalSourceList();
			
			if(allowNullValue){
				multipleChoiceProp.addLocalChoice(FField.NO_FIELD_ID, "- no field -");
			}

			FocFieldEnum enumeration = focDesc.newFocFieldEnum(category, FocFieldEnum.LEVEL_PLAIN);
			while(enumeration != null && enumeration.hasNext()){
				FField field = enumeration.nextField();
				if(field != null){
					boolean addField = field.getID() > 0;
					if(addField){
						FField fieldFromThisFocDescWithSameId = focDesc.getFieldByID(field.getID());//we make like this cause if we have (FInLineFieldObject or FTypedFieldObject) the enumeration returns the fields of this fields so we have to chek if the returned field are field from this desc or from the desc of the (FInLineFieldObject or FTypedFieldObject) fields before adding them to the multiple choice
						addField = field == fieldFromThisFocDescWithSameId;
					}
					if(addField){
						multipleChoiceProp.addLocalChoice(field.getID(), field.getTitle());
					}
				}
			}
		}
	}
	
	private void removeFilterPropertyListenerAndDisposeFilterProperty(){
		if(filterProperty != null){
			if(filterPropertyListener != null){
				filterProperty.removeListener(filterPropertyListener);
			}
			filterProperty.dispose();
			filterProperty = null;
		}
		filterProperty = null;
	}
	
	public void dispose(){
		super.dispose();
		if(fGFocDescComboBox != null){
			fGFocDescComboBox.dispose();
			fGFocDescComboBox = null;
		}
		
		/*if(filterProperty != null){
			if(filterPropertyListener != null){
				filterProperty.removeListener(filterPropertyListener);
			}
			filterProperty.dispose();
			filterProperty = null;
		}
		filterProperty = null;*/
		removeFilterPropertyListenerAndDisposeFilterProperty();
		
		cellEditorFieldIdComboBox = null;
		cellEditorFieldIdComboBox2 = null;
		cellEditorFieldIdComboBox3 = null;
		listFldIdInMasterObjectComboBox = null;
		
		if(this.fObjectFieldPanel != null){
			this.fObjectFieldPanel.dispose();
			this.fObjectFieldPanel = null;
		}
		
		if(this.fListFieldPanel != null){
			this.fListFieldPanel.dispose();
			this.fListFieldPanel = null;
		}
		
		if(slaveFocDescComboBox != null){
			slaveFocDescComboBox.dispose();
			slaveFocDescComboBox = null;
		}
		
		if(foreignKeyComboBox != null){
			foreignKeyComboBox.dispose();
			foreignKeyComboBox = null;
		}
		
		if(SQLTypePropertyListener != null && fieldDefinition != null){
			FProperty sqlTypeProp = fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_SQL_TYPE);
			if(sqlTypeProp != null){
				sqlTypeProp.removeListener(SQLTypePropertyListener);
			}
			SQLTypePropertyListener.dispose();
			SQLTypePropertyListener = null;
		}
		
		if(slaveFocDescListener != null && fieldDefinition != null){
			FProperty slaveFocDescProp = fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_SLAVE_DESC);
			if(slaveFocDescProp != null){
				slaveFocDescProp.removeListener(slaveFocDescListener);
			}
			slaveFocDescListener.dispose();
			slaveFocDescListener = null;
		}
		
		if(focDescNamePropertyListener != null && fieldDefinition != null){
			FProperty focDescProp = fieldDefinition.getFocProperty(FieldDefinitionDesc.FLD_FOC_DESC);
			if(focDescProp != null){
				focDescProp.removeListener(focDescNamePropertyListener);
			}
			focDescNamePropertyListener.dispose();
			focDescNamePropertyListener = null;
		}
		
		if(keyPrefixTextField != null){
			keyPrefixTextField.dispose();
			keyPrefixTextField = null;
		}
		if(focDescNameProperty != null){
			if(focDescComboBoxListener != null){
				focDescNameProperty.removeListener(focDescComboBoxListener);
				focDescComboBoxListener = null;
			}
			focDescNameProperty = null;
		}
		this.fieldDefinition = null;
		if(filtersComboBox != null){
			filtersComboBox.dispose();
			filtersComboBox = null;
		}
	}
}
